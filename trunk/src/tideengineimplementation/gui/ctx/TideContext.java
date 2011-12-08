package tideengineimplementation.gui.ctx;

import java.util.ArrayList;

public class TideContext
{
  private static TideContext staticObjects = null;
  private transient ArrayList<TideEventListener> tideListeners = null;
  
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
  
  public ArrayList<TideEventListener> getListeners()
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
  
  public void fireCoeffSelection(ArrayList<String> names)
  {
    for (TideEventListener tel : tideListeners)
      tel.setCoeffToHighlight(names);
  }
}
