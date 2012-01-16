package tideengineimplementation.utils;

import calculation.SightReductionUtil;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

import nauticalalmanac.Anomalies;
import nauticalalmanac.Context;
import nauticalalmanac.Core;
import nauticalalmanac.Jupiter;
import nauticalalmanac.Mars;
import nauticalalmanac.Moon;

import nauticalalmanac.Saturn;
import nauticalalmanac.Venus;

public class AstroComputer
{
  private static int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
  private static double deltaT = 66.4749d; // 2011.Overridden by deltaT system variable.  
  /**
   * Time are UTC
   * @param y year
   * @param m Month. Attention: Jan=1, Dec=12 !!!! Does NOT start with 0.
   * @param d day
   * @param h hour
   * @param mi minute
   * @param s second
   * @return Phase in Degrees
   */
  public static double getMoonPhase(int y, int m, int d, int h, int mi, int s)
  {
    double phase = 0f;
    year = y;
    month = m;
    day = d;
    hour = h;
    minute = mi;
    second = s;
    
    calculate();
    phase = Context.lambdaMapp - Context.lambda_sun;
    while (phase < 0d) phase += 360d;
    return phase;
  }
  
  public static void calculate()
  {
    deltaT = Double.parseDouble(System.getProperty("deltaT", Double.toString(deltaT)));
    Core.julianDate(year, month, day, hour, minute, second, deltaT);
    Anomalies.nutation();
    Anomalies.aberration();

    Core.aries();
    Core.sun();
    
    Moon.compute();
    
    Venus.compute();
    Mars.compute();
    Jupiter.compute();
    Saturn.compute();    
 // Core.polaris();
    Core.moonPhase();
 // Core.weekDay();
  }

  /**
   * The calculate() method must have been invoked before.
   * 
   * @param latitude
   * @return the time of rise and set of the body (Sun in that case).
   * 
   * @see http://aa.usno.navy.mil/data/docs/RS_OneYear.php
   * @see http://www.jgiesen.de/SunMoonHorizon/
   */
  public static double[] sunRiseAndSet(double latitude, double longitude)
  {
  //  out.println("Sun HP:" + Context.HPsun);
  //  out.println("Sun SD:" + Context.SDsun);
    double h0 = (Context.HPsun / 3600d) - (Context.SDsun / 3600d); // - (34d / 60d);
  //  out.println("Sin Sun H0:" + Math.sin(Math.toRadians(h0)));
    double cost = Math.sin(Math.toRadians(h0)) - (Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(Context.DECsun)));
    double t    = Math.acos(cost);
    double lon = longitude;
//  while (lon < -180D)
//    lon += 360D;
  //  out.println("Lon:" + lon + ", Eot:" + Context.EoT + " (" + (Context.EoT / 60D) + ")" + ", t:" + Math.toDegrees(t));
    double utRise = 12D - (Context.EoT / 60D) - (lon / 15D) - (Math.toDegrees(t) / 15D);
    double utSet  = 12D - (Context.EoT / 60D) - (lon / 15D) + (Math.toDegrees(t) / 15D);
    
    double Z = Math.acos((Math.sin(Math.toRadians(Context.DECsun)) + (0.0145 * Math.sin(Math.toRadians(latitude)))) / 
                         (0.9999 * Math.cos(Math.toRadians(latitude))));    
    Z = Math.toDegrees(Z);
    
    return new double[] { utRise, utSet, Z, 360d - Z };    
  }

  public static double[] sunRiseAndSet_wikipedia(double latitude, double longitude)
  {
    double cost = Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(Context.DECsun));
    double t    = Math.acos(cost);
    double lon = longitude;
    double utRise = 12D - (Context.EoT / 60D) - (lon / 15D) - (Math.toDegrees(t) / 15D);
    double utSet  = 12D - (Context.EoT / 60D) - (lon / 15D) + (Math.toDegrees(t) / 15D);
    
    double Z = Math.acos((Math.sin(Math.toRadians(Context.DECsun)) + (0.0145 * Math.sin(Math.toRadians(latitude)))) / 
                         (0.9999 * Math.cos(Math.toRadians(latitude))));    
    Z = Math.toDegrees(Z);
    
    return new double[] { utRise, utSet, Z, 360d - Z };    
  }

  /**
   * @see http://aa.usno.navy.mil/data/docs/RS_OneYear.php
   * @see http://www.jgiesen.de/SunMoonHorizon/
   */
  public static double[] moonRiseAndSet(double latitude, double longitude)
  {    
  //  out.println("Moon HP:" + (Context.HPmoon / 60) + "'");
  //  out.println("Moon SD:" + (Context.SDmoon / 60) + "'");
    double h0 = (Context.HPmoon / 3600d) - (Context.SDmoon / 3600d) - (34d / 60d);
  //  out.println("Moon H0:" + h0);
    double cost = Math.sin(Math.toRadians(h0)) - (Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(Context.DECmoon)));
    double t    = Math.acos(cost);
    double lon = longitude;
    while (lon < -180D)
      lon += 360D;
  //  out.println("Moon Eot:" + Context.moonEoT + " (" + (Context.moonEoT / 60D) + ")" + ", t:" + Math.toDegrees(t));
    double utRise = 12D - (Context.moonEoT / 60D) - (lon / 15D) - (Math.toDegrees(t) / 15D);
    while (utRise < 0)
      utRise += 24;
    while (utRise > 24)
      utRise -= 24;
    double utSet  = 12D - (Context.moonEoT / 60D) - (lon / 15D) + (Math.toDegrees(t) / 15D);
    while (utSet < 0)
      utSet += 24;
    while (utSet > 24)
      utSet -= 24;

    return new double[] { utRise, utSet };    
  }

  public static double getMoonIllum()
  {
    return Context.k_moon;
  }
  
  public static void setDeltaT(double deltaT)
  {
    System.out.println("...DeltaT set to " + deltaT);
    AstroComputer.deltaT = deltaT;
  }

  public static final double getTimeZoneOffsetInHours(TimeZone tz)
  {
    double d = 0;
    if (false)
    {
      SimpleDateFormat sdf = new SimpleDateFormat("Z");
      sdf.setTimeZone(tz);
      String s = sdf.format(new Date());
      if (s.startsWith("+"))
        s = s.substring(1);
      int i = Integer.parseInt(s);
      d = (int)(i / 100);
      int m = (int)(i % 100);
      d += (m / 60d);
    }
    else
      d = (tz.getOffset(new Date().getTime()) / (3600d * 1000d));

    return d;
  }
  
  public static final double getTimeOffsetInHours(String timeOffset)
  {
//  System.out.println("Managing:" + timeOffset);
    double d = 0d;
    String[] hm = timeOffset.split(":");
    int h = Integer.parseInt(hm[0]);
    int m = Integer.parseInt(hm[1]);
    if (h > 0)
      d = h + (m / 60d);
    if (h < 0)
      d = h - (m / 60d);
    return d;
  }
  
  public final static int SUN_ALT_IDX  = 0;
  public final static int SUN_Z_IDX    = 1;  
  public final static int MOON_ALT_IDX = 2;
  public final static int MOON_Z_IDX   = 3;

  public static double[] getSunMoon(int y, int m, int d, int h, int mi, int s, double lat, double lng)
  {
    double[] values = new double[4];
    year = y;
    month = m;
    day = d;
    hour = h;
    minute = mi;
    second = s;
    
    calculate();
    SightReductionUtil sru = new SightReductionUtil();    
    sru.setL(lat);
    sru.setG(lng);
    
    // Sun
    sru.setAHG(Context.GHAsun);
    sru.setD(Context.DECsun);    
    sru.calculate();          
    values[SUN_ALT_IDX] = sru.getHe();
    values[SUN_Z_IDX]   = sru.getZ();
    // Moon
    sru.setAHG(Context.GHAmoon);
    sru.setD(Context.DECmoon);    
    sru.calculate();          
    values[MOON_ALT_IDX] = sru.getHe();
    values[MOON_Z_IDX]   = sru.getZ();
    
    return values;
  }
  
  public static double getSunAlt(int y, int m, int d, int h, int mi, int s, double lat, double lng)
  {
    double value = 0d;
    year = y;
    month = m;
    day = d;
    hour = h;
    minute = mi;
    second = s;
    
    calculate();
    SightReductionUtil sru = new SightReductionUtil();    
    sru.setL(lat);
    sru.setG(lng);
    
    // Sun
    sru.setAHG(Context.GHAsun);
    sru.setD(Context.DECsun);    
    sru.calculate();          
    value = sru.getHe();
    
    return value;
  }
  
  public static double getMoonAlt(int y, int m, int d, int h, int mi, int s, double lat, double lng)
  {
    double value = 0d;
    year = y;
    month = m;
    day = d;
    hour = h;
    minute = mi;
    second = s;
    
    calculate();
    SightReductionUtil sru = new SightReductionUtil();    
    sru.setL(lat);
    sru.setG(lng);
    
    // Moon
    sru.setAHG(Context.GHAmoon);
    sru.setD(Context.DECmoon);    
    sru.calculate();          
    value = sru.getHe();
    
    return value;
  }
  
  public static double[] getSunMoonAlt(int y, int m, int d, int h, int mi, int s, double lat, double lng)
  {
    double[] values = new double[2];
    year = y;
    month = m;
    day = d;
    hour = h;
    minute = mi;
    second = s;
    
//  System.out.println(y + "-" + month + "-" + day + " " + h + ":" + mi + ":" + s);
    
    calculate();
    SightReductionUtil sru = new SightReductionUtil();    
    sru.setL(lat);
    sru.setG(lng);
    
    // Sun
    sru.setAHG(Context.GHAsun);
    sru.setD(Context.DECsun);    
    sru.calculate();          
    values[0] = sru.getHe();
    // Moon
    sru.setAHG(Context.GHAmoon);
    sru.setD(Context.DECmoon);    
    sru.calculate();          
    values[1] = sru.getHe();
    
    return values;
  }

  /**
   * Warning: Context must have been initialized!
   * @return
   */
  public static double getSunDecl()
  {
    return Context.DECsun;
  }
  
  public static double getSunGHA()
  {
    return Context.GHAsun;
  }

  /**
   * Warning: Context must have been initialized!
   * @return
   */
  public static double getMoonDecl()
  {
    return Context.DECmoon;
  }
  
  public static double getMoonGHA()
  {
    return Context.GHAmoon;
  }
  
  public static double getVenusDecl() { return Context.DECvenus; }
  public static double getMarsDecl() { return Context.DECmars; }
  public static double getJupiterDecl() { return Context.DECjupiter; }
  public static double getSaturnDecl() { return Context.DECsaturn; }

  public static double getVenusGHA() { return Context.GHAvenus; }
  public static double getMarsGHA() { return Context.GHAmars; }
  public static double getJupiterGHA() { return Context.GHAjupiter; }
  public static double getSaturnGHA() { return Context.GHAsaturn; }
  
  public static double ghaToLongitude(double gha)
  {
    double longitude = 0;
    if (gha < 180)
      longitude = -gha;
    if (gha >= 180)
      longitude = 360 - gha;
    return longitude;
  }
  
  public static void main(String[] args)
  {
    System.out.println("Moon phase:" + getMoonPhase(2011, 8, 22, 12, 00, 00));
    System.out.println("TimeOffset:" + getTimeOffsetInHours("-09:30"));
    String[] tz = new String[] { "Pacific/Marquesas", "America/Los_Angeles", "GMT", "Europe/Paris", "Europe/Moscow", "Australia/Sydney", "Australia/Adelaide" };
    for (int i=0; i<tz.length; i++)
      System.out.println("TimeOffset for " + tz[i] + ":" +  getTimeZoneOffsetInHours(TimeZone.getTimeZone(tz[i])));
    
    System.out.println("TZ:" + TimeZone.getTimeZone(tz[0]).getDisplayName() + ", " + (TimeZone.getTimeZone(tz[0]).getOffset(new Date().getTime()) / (3600d * 1000)));
  }
}
