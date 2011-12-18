package tideengineimplementation.gui.ctx;

import java.util.ArrayList;
import java.util.EventListener;

public abstract class TideEventListener implements EventListener
{
  public void internalFrameClosed() {}
  public void beginLoad() {}
  public void stopLoad() {}
  public void stationSelected(String stationName) {}
  public void filterList(String pattern) {}
  public void setDate(long date) {}
  public void setCoeffToHighlight(ArrayList<String> names) {}
  public void showAllCurves(boolean b) {}
  public void showTideCurve(boolean b) {}
}
