package tideengineimplementation.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class SpecialProgressBar
  extends JPanel
{
  private final SpecialProgressBar instance = this;
  private String label = "";
  private boolean move = true;
  private transient SpecialThread thread = null;
  
  public SpecialProgressBar()
  {
    this(true);
  }
  
  public SpecialProgressBar(boolean b)
  {
    this.move = b;
    this.setEnabled(b);
    try
    {
      jbInit();
      if (move)
        start();
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
    this.setSize(new Dimension(350, 20));
    this.setPreferredSize(new Dimension(350, 20));
  }

  public void start()
  {
    this.move = true;
    thread = new SpecialThread();
    thread.start();
  }
  
  public void stop()
  {
    System.out.println("Stop looking busy");
    this.move = false;
    if (thread != null)
    {
      synchronized (thread)
      {
        thread.stopit();
        thread.notify();
      }
    }
  }

  @Override
  public void paintComponent(Graphics g)
  {
//  super.paintComponent(g);
    if (this.isEnabled())
      g.setColor(Color.BLACK); // Background
    else
      g.setColor(Color.LIGHT_GRAY); // Background
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
    g.setColor(Color.BLACK); // Frame
    g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
    g.setColor(Color.GREEN); // Lines
    for (int i=0; this.move &&  i<50; i++)
    {
      int x = (int)Math.round(Math.random() * this.getWidth());    
//    System.out.println("X:" + x);
      g.drawLine(x, 0, x, this.getHeight());
    }
    if (this.move && label != null && label.trim().length() > 0)
    {
      Font f = g.getFont();
      int l  = g.getFontMetrics(f).stringWidth(label);
      g.drawString(label, (this.getWidth() / 2) - (l/2), (this.getHeight() / 2) + (f.getSize() / 2));      
    }
  }

  public void setLabel(String label)
  {
    this.label = label;
  }
  
  private class SpecialThread extends Thread
  {
    private boolean move = true;
    
    @Override
    public void run()
    {
      while (move)
      {
        instance.repaint();
        try { Thread.sleep(100L); } catch (Exception ex) { ex.printStackTrace(); }
      }
      System.out.println("Stop!");
      instance.repaint();
    }
    
    public void stopit()
    {
      this.move = false;
    }
  }

}
