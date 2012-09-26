package tideengineimplementation.gui.toast;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JWindow;

import tideengineimplementation.gui.dialog.SpecialProgressBar;

public class ToastWindow extends JWindow
{
  @SuppressWarnings("compatibility:417524017059390911")
  public final static long serialVersionUID = 1L;

  private static ToastWindow instance;
  private boolean paintCalled = false;

  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private SpecialProgressBar loadProgressBar = new SpecialProgressBar();
  private static JLabel loadingLabel = new JLabel();
  
  private final static int H = 100;
  private final static int W = 325;
  
  private ToastWindow(Frame parent)
  {
    this(parent, null);
  }
  
  private ToastWindow(Frame parent, JFrame parentFrame) 
  {
    super(parent);
    // Center the window on the screen
//  int imgWidth = image.getWidth(this);
//  int imgHeight = image.getHeight(this);
//  setSize(imgWidth, imgHeight);
    Dimension screenDim = null;
    if (parentFrame == null)
      screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    else
      screenDim = parentFrame.getSize(); // .getContentPane().getSize();
    Dimension dim = new Dimension(W, H);
    this.setSize(dim);
    int x = 0;
    int y = 0;
    if (parentFrame != null)
    {
      Point location = parentFrame.getLocation();
      x = location.x;
      y = location.y;
    }
    setLocation(x + (screenDim.width - dim.width) / 2,
                y + (screenDim.height - dim.height) / 2);
    JLayeredPane layer = new JLayeredPane();
    
    JPanel itemHolder = new JPanel();
    itemHolder.setLayout(gridBagLayout1);
    itemHolder.setOpaque(false);

//  loadingLabel.setText("Working...");
    loadingLabel.setForeground(Color.red);
    itemHolder.add(loadingLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    
//  loadProgressBar.setIndeterminate(true);
    loadProgressBar.setPreferredSize(new Dimension(250, 20));
    itemHolder.add(loadProgressBar, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

    itemHolder.setBounds(0, 0, W, H);
    layer.add(itemHolder, JLayeredPane.PALETTE_LAYER);

    this.setContentPane(layer);

    // Users shall be able to close the splash window by
    // clicking on its display area. This mouse listener
    // listens for mouse clicks and disposes the splash window.
    MouseAdapter disposeOnClick = new MouseAdapter() 
    {
      public void mouseClicked(MouseEvent evt) 
      {
        // Note: To avoid that method splash hangs, we
        // must set paintCalled to true and call notifyAll.
        // This is necessary because the mouse click may
        // occur before the contents of the window
        // has been painted.
        synchronized(ToastWindow.this) 
        {
          ToastWindow.this.paintCalled = true;
          ToastWindow.this.notifyAll();
        }
        dispose();
      }
    };
    addMouseListener(disposeOnClick);
  }

  /**
   * Updates the display area of the window.
   */
  public void update(Graphics g) 
  {
    // Note: Since the paint method is going to draw an
    // image that covers the complete area of the component we
    // do not fill the component with its background color
    // here. This avoids flickering.
    paint(g);
  }
  /**
   * Paints the image on the window.
   */
  public void paint(Graphics g) 
  {
    if (g instanceof Graphics2D)
    {
      // Transparency
      ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));    
    }
//  super.paint(g);
//  super.setBackground(Color.white);
    int w = this.getWidth();
    int h = this.getHeight();
//  g.setColor(Color.LIGHT_GRAY);
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, w, h);
    ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));    
    g.setColor(Color.RED);
    g.drawRoundRect(2, 2, w-4, h-4, 5, 5);
//  g.drawImage(image, 0, 0, this);
    
    // Notify method splash that the window
    // has been painted.
    // Note: To improve performance we do not enter
    // the synchronized block unless we have to.
    if (! paintCalled) 
    {
      paintCalled = true;
      synchronized (this) { notifyAll(); }
    }
  }
  
  public static void splash(String label) 
  {
    splash(label, null);
  }
  
  public static void splash(final String label, final JFrame parent) 
  {
    Thread splashThread = new Thread()
      {
        public void run()
        {
          showSplash(label, parent);
        }
      };
    splashThread.start();
  }
  
  private static void showSplash(String label, JFrame parent)
  {
    loadingLabel.setText(label);    
    if (instance == null) 
    {
      Frame f = new Frame();      
      // Create the splash image
      instance = new ToastWindow(f, parent);            
      // Show the window.
      instance.setVisible(true); // .show();
      System.out.println("Toast visible...");
      // Note: To make sure the user gets a chance to see the
      // splash window we wait until its paint method has been
      // called at least once by the AWT event dispatcher thread.
      // If more than one processor is available, we don't wait,
      // and maximize CPU throughput instead.
      if (! EventQueue.isDispatchThread() && Runtime.getRuntime().availableProcessors() == 1) 
      {
        synchronized (instance) 
        {
          while (!instance.paintCalled) 
          {
            try { instance.wait(); } catch (InterruptedException e) {}
          }
        }
      }
    }
  }
  /**
   * Closes the splash window.
   */
  public static void disposeSplash() 
  {
    if (instance != null) 
    {
      instance.getOwner().dispose();
      instance = null;
    }
    System.out.println("Toast disposed.");
  }
  
  /**
   * Invokes the main method of the provided class name.
   * @param args the command line arguments
   */
  public static void invokeMain(String className, String[] args) 
  {
    try 
    {
      Class.forName(className).getMethod("main", new Class[] {String[].class}).invoke(null, new Object[] {args});
    } 
    catch (Exception e) 
    {
      InternalError error = new InternalError("Failed to invoke main method");
      error.initCause(e);
      throw error;
    }
  }
}