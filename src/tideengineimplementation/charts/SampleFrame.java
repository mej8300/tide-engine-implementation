package tideengineimplementation.charts;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.util.ArrayList;

import java.util.List;

import javax.swing.JFrame;

import tideengine.BackEndTideComputer;
import tideengine.TideStation;

import tideengineimplementation.utils.StationPositions;


public class SampleFrame extends JFrame
{
  @SuppressWarnings("compatibility:2455624528469075514")
  public final static long serialVersionUID = 1L;
  private CommandPanel commandPanel;

  public SampleFrame()
  {
    commandPanel = new CommandPanel();
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    getContentPane().setLayout(new BorderLayout());
    setSize(new Dimension(600, 400));
    setTitle("Tide Stations");
    getContentPane().add(commandPanel, BorderLayout.CENTER);
    Thread sdThread = new Thread()
      {
        public void run()
        {
          try
          { 
            System.out.println("Reading Station Data...");
            List<TideStation> stationData = BackEndTideComputer.getStationData();
            System.out.println("Done!");
            commandPanel.setStationData(stationData);
            commandPanel.repaint();
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      };
    sdThread.start();
  }
}
