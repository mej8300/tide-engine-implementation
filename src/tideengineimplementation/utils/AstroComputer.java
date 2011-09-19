package tideengineimplementation.utils;

import nauticalalmanac.Anomalies;
import nauticalalmanac.Context;
import nauticalalmanac.Core;
// import nauticalalmanac.Jupiter;
// import nauticalalmanac.Mars;
import nauticalalmanac.Moon;
// import nauticalalmanac.Saturn;
// import nauticalalmanac.Venus;

public class AstroComputer
{
  private static int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
  private static double deltaT = 66.4749d; // 2011
  
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
    Core.julianDate(year, month, day, hour, minute, second, deltaT);
    Anomalies.nutation();
    Anomalies.aberration();

    Core.aries();
    Core.sun();
    
    Moon.compute();
    
 // Venus.compute();
 // Mars.compute();
 // Jupiter.compute();
 // Saturn.compute();    
 // Core.polaris();
    Core.moonPhase();
 // Core.weekDay();
  }

  /**
   * The calculate() method must have been invoked before.
   * 
   * @param latitude
   * @return the time of rise and set of the body (Sun in that case).
   */
  public static double[] sunRiseAndSet(double latitude)
  {
  //  out.println("Sun HP:" + Context.HPsun);
  //  out.println("Sun SD:" + Context.SDsun);
    double h0 = (Context.HPsun / 3600d) - (Context.SDsun / 3600d); // - (34d / 60d);
  //  out.println("Sin Sun H0:" + Math.sin(Math.toRadians(h0)));
    double cost = Math.sin(Math.toRadians(h0)) - (Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(Context.DECsun)));
    double t    = Math.acos(cost);
    // TASK Look into this next line...
  //  out.println("Sun GHA: " + Context.GHAsun);
  //  double lon = - Context.GHAsun; // - Math.toDegrees(t);
  //  double lon = Context.GHAsun; // - Math.toDegrees(t);
    double lon = 0;
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

  public static double[] sunRiseAndSet_wikipedia(double latitude)
  {
    double cost = Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(Context.DECsun));
    double t    = Math.acos(cost);
    double lon = 0;
    double utRise = 12D - (Context.EoT / 60D) - (lon / 15D) - (Math.toDegrees(t) / 15D);
    double utSet  = 12D - (Context.EoT / 60D) - (lon / 15D) + (Math.toDegrees(t) / 15D);
    
    double Z = Math.acos((Math.sin(Math.toRadians(Context.DECsun)) + (0.0145 * Math.sin(Math.toRadians(latitude)))) / 
                         (0.9999 * Math.cos(Math.toRadians(latitude))));    
    Z = Math.toDegrees(Z);
    
    return new double[] { utRise, utSet, Z, 360d - Z };    
  }

  public static double[] moonRiseAndSet(double latitude)
  {    
  //  out.println("Moon HP:" + (Context.HPmoon / 60) + "'");
  //  out.println("Moon SD:" + (Context.SDmoon / 60) + "'");
    double h0 = (Context.HPmoon / 3600d) - (Context.SDmoon / 3600d) - (34d / 60d);
  //  out.println("Moon H0:" + h0);
    double cost = Math.sin(Math.toRadians(h0)) - (Math.tan(Math.toRadians(latitude)) * Math.tan(Math.toRadians(Context.DECmoon)));
    double t    = Math.acos(cost);
    // TASK Look into this next line...
  //  out.println("Sun GHA: " + Context.GHAsun);
  //  double lon = - Context.GHAsun; // - Math.toDegrees(t);
  //  double lon = Context.GHAsun; // - Math.toDegrees(t);
    double lon = 0;
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
    AstroComputer.deltaT = deltaT;
  }
  
  public static void main(String[] args)
  {
    System.out.println("Moon phase:" + getMoonPhase(2011, 8, 22, 12, 00, 00));
  }
}
