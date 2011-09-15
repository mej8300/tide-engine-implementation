package tideengineimplementation.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics;

import javax.swing.JPanel;

public class SpecialProgressBar
  extends JPanel
{
  SpecialProgressBar instance = this;
  
  public SpecialProgressBar()
  {
    try
    {
      jbInit();
      Thread thread = new Thread()
      {
        @Override
        public void run()
        {
          while (true)
          {
            instance.repaint();
            try { Thread.sleep(100L); } catch (Exception ex) { ex.printStackTrace(); }
          }
        }
      };
      thread.start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    this.setLayout(null);
    this.setSize(new Dimension(350, 25));
  }


  @Override
  public void paintComponent(Graphics g)
  {
//  super.paintComponent(g);
    g.setColor(Color.BLACK); // Background
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
    g.setColor(Color.BLACK); // Frame
    g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    g.setColor(Color.GREEN); // Lines
    for (int i=0; i<50; i++)
    {
      int x = (int)Math.round(Math.random() * this.getWidth());    
//    System.out.println("X:" + x);
      g.drawLine(x, 0, x, this.getHeight());
    }
  }
}
