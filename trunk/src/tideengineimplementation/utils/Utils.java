package tideengineimplementation.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import tideengine.TideStation;
import tideengine.TideUtilities;

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
