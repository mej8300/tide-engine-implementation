package tideengineimplementation.gui.ctx;

import java.util.EventListener;

public abstract class TideEventListener implements EventListener
{
  public void internalFrameClosed() {}
  public void beginLoad() {}
  public void stopLoad() {}
  public void stationSelected(String stationName) {}
  public void filterList(String pattern) {}
  public void setDate(long date) {}
}