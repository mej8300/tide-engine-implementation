package tideengineimplementation.charts;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.util.ArrayList;

import javax.swing.JFrame;

import tideengine.TideStation;

import tideengineimplementation.utils.StationPositions;


public class SampleFrame extends JFrame
{
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
            ArrayList<TideStation> stationData = StationPositions.getStationData();
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
