package tideengineimplementation.gui.main.splash;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import tideengineimplementation.gui.dialog.SpecialProgressBar;

public class SplashWindow extends JWindow
{
  private static SplashWindow instance;
  private transient Image image;
  private boolean paintCalled = false;

//private JLabel jLabel1 = new JLabel();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel copyrightLabel = new JLabel();
//private JLabel toolLabel = new JLabel();
  private SpecialProgressBar loadProgressBar = new SpecialProgressBar();
  private JLabel loadingLabel = new JLabel();
  
  private final static int H = 200;
  private final static int W = 325;
  
  /**
   * Creates a new instance.
   * @param parent the parent of the window.
   * @param image the splash image.
   */
  private SplashWindow(Frame parent, Image image)
  {
    this(parent, image, null);
  }
  private SplashWindow(Frame parent, Image image, JFrame parentFrame) 
  {
    super(parent);
    this.image = image;
    
    // Load the image
    MediaTracker mt = new MediaTracker(this);
    mt.addImage(image,0);
    try 
    {
      mt.waitForID(0);
    } 
    catch(InterruptedException ie){}
    
    // Abort on failure
    if (mt.isErrorID(0)) 
    {
      setSize(0,0);
      System.err.println("Warning: SplashWindow couldn't load splash image.");
      synchronized(this) 
      {
        paintCalled = true;
        notifyAll();
      }
      return;
    }
    
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
    
    ImageIcon img = new ImageIcon(this.getClass().getResource("paperboat.png"));    
    JLabel imgHolder = new JLabel(img);
//  imgHolder.setBorder(BorderFactory.createLineBorder(new Color(228, 24, 106), 2));
    imgHolder.setBounds(0, 10, 325, 150);
    layer.add(imgHolder, JLayeredPane.DRAG_LAYER);

    JPanel itemHolder = new JPanel();
    itemHolder.setLayout(gridBagLayout1);
    itemHolder.setOpaque(false);

    // 169 = Copyright
    copyrightLabel.setText((char)169  + " " + "OlivSoft, 2011");
    copyrightLabel.setForeground(Color.red);

    String flavor = "SQLITE";
    try
    {
      String tideFlavor = System.getProperty("tide.flavor", "xml"); 
      if (tideFlavor.equals("xml"))
        flavor = "XML";
      else if (tideFlavor.equals("sqllite"))
        flavor = "SQLITE";
      else
        flavor = "SQL";
    }
    catch (Exception ex)
    {
      System.err.println("You're an Applet, hey? " + ex.getLocalizedMessage() + " (OK).");
    }
    loadingLabel.setText("Loading " + "(" + flavor + ")");
    loadingLabel.setForeground(Color.red);
    itemHolder.add(loadingLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(140, 0, 0, 0), 0, 0));
    
//  loadProgressBar.setIndeterminate(true);
    loadProgressBar.setPreferredSize(new Dimension(250, 20));
    itemHolder.add(loadProgressBar, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    itemHolder.add(copyrightLabel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

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
        synchronized(SplashWindow.this) 
        {
          SplashWindow.this.paintCalled = true;
          SplashWindow.this.notifyAll();
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
    g.setColor(Color.LIGHT_GRAY);
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
  
  /**
   * Open's a splash window using the specified image.
   * @param image The splash image.
   */
  public static void splash(Image image) 
  {
    splash(image, null);
  }
  
  public static void splash(Image image, JFrame parent) 
  {
    if (instance == null && image != null) 
    {
      Frame f = new Frame();
      
      // Create the splash image
      instance = new SplashWindow(f, image, parent);
      
      // Show the window.
      instance.setVisible(true); // .show();
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
   * Open's a splash window using the specified image.
   * @param imageURL The url of the splash image.
   */
  public static void splash(URL imageURL, JFrame parent) 
  {
    if (imageURL != null) 
    {
      splash(Toolkit.getDefaultToolkit().createImage(imageURL), parent);
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