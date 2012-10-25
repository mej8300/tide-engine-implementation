package tideengineimplementation.gui.ctx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TideContext
{
  private static TideContext staticObjects = null;
  private List<TideEventListener> tideListeners = null;
  
  private List<String> recentStations = new LinkedList<String>();

  private TideContext()
  {
    tideListeners = new ArrayList<TideEventListener>(2); // 2: Initial Capacity
  }
  
  public static synchronized TideContext getInstance()
  {
    if (staticObjects == null)
      staticObjects = new TideContext();
    return staticObjects;
  }
  
  public List<TideEventListener> getListeners()
  {
    return tideListeners;
  }
    
  public void release()
  {
    staticObjects = null;
    System.gc();
  }
  
  public synchronized void addTideListener(TideEventListener l)
  {
    if (!tideListeners.contains(l))
    {
      tideListeners.add(l);
    }
  }

  public synchronized void removeTideListener(TideEventListener l)
  {
    tideListeners.remove(l);
  }

  public void fireInternalFrameClosed()
  {
    for (int i=0; i<tideListeners.size(); i++)
    {
      TideEventListener l = tideListeners.get(i);
      l.internalFrameClosed();
    }
  }
  
  public void fireSetBusy(boolean b)
  {
    for (TideEventListener tel : tideListeners)
      tel.setBusy(b);
  }
  
  public void fireSetNbStationsSelected(int n)
  {
    for (TideEventListener tel : tideListeners)
      tel.setNbStationsSelected(n);
  }

  public void fireStationSelected(String sn)
  {
    for (TideEventListener tel : tideListeners)
      tel.stationSelected(sn);
  }

  public void fireShowAllCurves(boolean b)
  {
    for (TideEventListener tel : tideListeners)
      tel.showAllCurves(b);
  }

  public void fireFilter(String pattern)
  {
    for (TideEventListener tel : tideListeners)
      tel.filterList(pattern);
  }  
  
  public void fireCoeffSelection(List<String> names)
  {
    for (TideEventListener tel : tideListeners)
      tel.setCoeffToHighlight(names);
  }

  public List<String> getRecentStations()
  {
    return recentStations;
  }
}
