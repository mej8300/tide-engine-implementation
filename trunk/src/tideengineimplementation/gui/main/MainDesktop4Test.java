package tideengineimplementation.gui.main;

import java.awt.Dimension;

import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import javax.swing.UIManager;

import tideengineimplementation.gui.TideInternalFrame;

public class MainDesktop4Test
     extends JFrame
{
  public MainDesktop4Test()
  {
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    this.getContentPane().setLayout( null );
    this.setSize(new Dimension(1295, 774));
    this.setTitle( "Oliv" );
    
    TideInternalFrame tides = new TideInternalFrame(null);
    tides.setIconifiable(true);
    tides.setClosable(true);
    tides.setMaximizable(true);
    tides.setResizable(true);
    this.add(tides);
    tides.setVisible(true);
    tides.setBounds(new Rectangle(70, 35, 1079, 637));
  }
  
  public static void main(String[] args)
  {
    String lnf = null;
    try { lnf = System.getProperty("swing.defaultlaf"); } catch (Exception ignore) {}
    //  System.out.println("LnF:" + lnf);
    if (lnf == null) // Let the -Dswing.defaultlaf do the job.
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new MainDesktop4Test();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height)
    {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width)
    {
      frameSize.width = screenSize.width;
    }
    frame.setLocation( ( screenSize.width - frameSize.width ) / 2, ( screenSize.height - frameSize.height ) / 2 );

    frame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });    
    //  frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    frame.setVisible(true);
    
  }
}
