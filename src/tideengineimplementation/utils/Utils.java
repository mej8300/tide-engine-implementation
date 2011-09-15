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
