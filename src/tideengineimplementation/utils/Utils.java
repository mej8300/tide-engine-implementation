package tideengineimplementation.utils;


import astro.calc.GeoPoint;
import astro.calc.GreatCircle;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import tideengine.BackEndTideComputer;
import tideengine.TideStation;
import tideengine.TideUtilities;

import tideengineimplementation.gui.TideInternalFrame;
import tideengineimplementation.gui.ctx.TideContext;


public class Utils
{
  public final static long NB_S_PER_MIN  = 60L;
  public final static long NB_S_PER_HOUR = 60 * NB_S_PER_MIN;
  public final static long NB_S_PER_DAY  = 24 * NB_S_PER_HOUR;
  
  public static double convert(double value, String from, String to)
  {
    double d = value;
    if (!from.equals(to))
    {
      if (from.equals(TideStation.FEET))
        d = TideUtilities.feetToMeters(value);
      else if (from.equals(TideStation.METERS))
        d = TideUtilities.metersToFeet(value);
    }
    return d;
  }  
  
  public final static NumberFormat DF2 = new DecimalFormat("00");
  
  public static String decimalHoursToHM(double d)
  {
    String s = "";
    int h = (int)d;
    int m = (int)((d - h) * 60);
    s = DF2.format(h) + ":" + DF2.format(m);
    return s;
  }
  
  public static Calendar decimalHourToDate(Calendar cal, double decHour)
  {
    int h = (int)Math.floor(decHour);
    int m = (int)Math.floor((decHour - h) * 60);
    int s = (int)Math.round(3600d * ((decHour - h) - (m / 60d)));
    
    Calendar newCal = (Calendar)cal.clone();
    newCal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    newCal.set(Calendar.HOUR_OF_DAY, h);
    newCal.set(Calendar.MINUTE, m);
    newCal.set(Calendar.SECOND, s);
    newCal.set(Calendar.MILLISECOND, 0);
    
    return newCal;
  }
  
  public static float daylightOffset(Calendar cal)
  {
    float dstOffset  = (cal.get(Calendar.DST_OFFSET) / 3600000f);
    return dstOffset;
  }
  
  public static float fullGMTOffset(Calendar cal)
  {
    float zoneOffset  = (cal.get(Calendar.ZONE_OFFSET) / 3600000f);
    float dstOffset   = (cal.get(Calendar.DST_OFFSET) / 3600000f);
    return dstOffset + zoneOffset;
  }
  
  public static String formatTimeDiff(Calendar from, Calendar to)
  {
    long diff = (to.getTimeInMillis() - from.getTimeInMillis()) / 1000L;
    return formatTimeDiff(diff);  
  }  
  
  /**
   *
   * @param diff in seconds
   * @return
   */
  public static String formatTimeDiff(long diff)
  {
    String ret = "";
    int nbDay  = (int)(diff / NB_S_PER_DAY);
    int nbHour = (int)((diff - (nbDay * NB_S_PER_DAY)) / NB_S_PER_HOUR);
    int nbMin  = (int)((diff - (nbDay * NB_S_PER_DAY) - (nbHour * NB_S_PER_HOUR)) / NB_S_PER_MIN);
    if (nbDay > 0)
      ret += (Integer.toString(nbDay) + " day" + ((nbDay>1)?"s ":" "));
    if (nbHour > 0 || nbDay > 0)
      ret += (Integer.toString(nbHour) + " hour" + ((nbHour>1)?"s ":" "));
    if (nbMin > 0 || nbHour > 0 || nbDay > 0)
      ret += (Integer.toString(nbMin) + " minute" + ((nbMin>1)?"s ":" "));
//  System.out.println("Diff:" + diff + ", :" + ret);
    return ret;
  }
  
  public static String escapePattern(String s)
  {
    return (s.replace("(", "\\(").replace(")", "\\)").replace(".", "\\."));  
  }
  
  public static void shrinkStationList()
  {
    shrinkStationList(null);
  }
  
  public static void shrinkStationList(String mess)
  {
    boolean verbose = (mess != null);
    if (verbose)
      System.out.println("Shrinking Station List " + mess);
    int maxRecent = Integer.parseInt(System.getProperty("max.recent.stations", "5"));
    if (verbose)
      System.out.println("Max recent stations is " + maxRecent);
    while (TideContext.getInstance().getRecentStations().size() > maxRecent)
    {
      if (verbose)
        System.out.println(".Removing " + TideContext.getInstance().getRecentStations().get(0) + " from the station list");
      TideContext.getInstance().getRecentStations().remove(0);
    }
    if (verbose)
    {
      System.out.println(".List is now:");
      for (String s : TideContext.getInstance().getRecentStations())
        System.out.println("- " + s);
    }
  }
  
  private static List<TideStation> tideStations = null;
  
  public static TideInternalFrame.StationDistance findClosestStation(GeoPoint origin, double maxDist)
  {
    List<TideInternalFrame.StationDistance> stationMap = null;
    TideInternalFrame.StationDistance closest = null;
    if (tideStations == null)
    {
      try { tideStations = BackEndTideComputer.getStationData(); } catch (Exception ex) {}
    }
    if (tideStations != null)
    {
//    System.out.println("Found " + Integer.toString(tideStations.size()) + " station(s)");
      stationMap = new ArrayList<TideInternalFrame.StationDistance>();
      // Populate
      for (TideStation tideStation : tideStations)
      {
        double dist = GreatCircle.getDistanceInNM(origin, new GeoPoint(tideStation.getLatitude(), tideStation.getLongitude()));
        if (dist <= maxDist)
          stationMap.add(new TideInternalFrame.StationDistance(tideStation.getFullName(), dist));
      }               
      // Sort
      Collections.sort(stationMap, new Comparator<TideInternalFrame.StationDistance>()
      {
        public int compare(TideInternalFrame.StationDistance o1, TideInternalFrame.StationDistance o2)
        {
          // Sort on distance
          int cmp = 0;
          if (o1.getDistance() > o2.getDistance())
            cmp = 1;
          else if (o1.getDistance() < o2.getDistance())
            cmp = -1;
          return cmp;
        }
      });               
      // Populate a table for the user to select one station.
    }
    if (stationMap.size() > 0)
      closest = stationMap.get(0);
    return closest;
  }
  
  public static void main1(String[] args)
  {
    System.out.println(formatTimeDiff(8094701L / 1000L));
  }
  
  public static void main(String[] args)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("E dd-MMM-yyyy HH:mm Z (z)");
    Calendar cal = GregorianCalendar.getInstance();
//  cal.set(Calendar.MONTH, 11);
    
    String tz = "Pacific/Marquesas";
    cal.setTimeZone(TimeZone.getTimeZone(tz));
    sdf.setTimeZone(TimeZone.getTimeZone(tz));
    System.out.println(sdf.format(cal.getTime()) + ": For " + tz + " DST:" + daylightOffset(cal) + ", full Offset:" + fullGMTOffset(cal));

    tz = "America/Los_Angeles";
    cal.setTimeZone(TimeZone.getTimeZone(tz));
    sdf.setTimeZone(TimeZone.getTimeZone(tz));
    System.out.println(sdf.format(cal.getTime()) + ": For " + tz + " DST:" + daylightOffset(cal) + ", full Offset:" + fullGMTOffset(cal));

    tz = "GMT";
    cal.setTimeZone(TimeZone.getTimeZone(tz));
    sdf.setTimeZone(TimeZone.getTimeZone(tz));
    System.out.println(sdf.format(cal.getTime()) + ": For " + tz + " DST:" + daylightOffset(cal) + ", full Offset:" + fullGMTOffset(cal));
    
    tz = "America/Los_Angeles";
    sdf.setTimeZone(TimeZone.getTimeZone(tz));
    cal.setTimeZone(TimeZone.getTimeZone(tz));
    
    cal.set(Calendar.YEAR, 2011);
    cal.set(Calendar.MONTH, 7);
    cal.set(Calendar.DAY_OF_MONTH, 30);
    cal.set(Calendar.HOUR_OF_DAY, 6);
    cal.set(Calendar.MINUTE, 29);
    cal.set(Calendar.SECOND, 0);
    System.out.println("----------------------");
    System.out.println(sdf.format(cal.getTime()) + ", full GMT offset:" + fullGMTOffset(cal) + " (Hour:" + cal.get(Calendar.HOUR_OF_DAY) + ")");
    tz = "GMT";
    cal.setTimeZone(TimeZone.getTimeZone(tz));
    sdf.setTimeZone(TimeZone.getTimeZone(tz));
    System.out.println(sdf.format(cal.getTime()) + ", full GMT offset:" + fullGMTOffset(cal) + " (Hour:" + cal.get(Calendar.HOUR_OF_DAY) + ")");
  }
  
}
