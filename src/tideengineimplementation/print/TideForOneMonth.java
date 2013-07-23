package tideengineimplementation.print;

import java.io.PrintStream;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import tideengine.BackEndTideComputer;
import tideengine.Coefficient;
import tideengine.TideStation;
import tideengine.TideUtilities;

import tideengineimplementation.gui.ctx.TideContext;
import tideengineimplementation.gui.dialog.PrintDialog;

import tideengineimplementation.gui.dialog.SearchPanel;

import tideengineimplementation.utils.AstroComputer;
import tideengineimplementation.utils.Utils;


/**
 * Tide Publisher
 * with Moon phases
 */
public class TideForOneMonth
{
//private final static String CONSTITUENT_FILE = BackEndTideComputer.getConstituentFileLocation(); // ".." + File.separator + "TideEngine" + File.separator + "xml.data" + File.separator + "constituents.xml";
//private final static String STATION_FILE     = BackEndTideComputer.getStationFileLocation(); // ".." + File.separator + "TideEngine" + File.separator + "xml.data" + File.separator + "stations.xml";
  
  private final static SimpleDateFormat SDF = new SimpleDateFormat("EEE dd MMM yyyy");
  public final static SimpleDateFormat TF  = new SimpleDateFormat("HH:mm z");
  
  private final static NumberFormat DF2 = new DecimalFormat("00");
  
//private final static boolean verbose = true;
  
  /**
   * Just for tests
   * @param args -month MM -year YYYY. For month, 1=Jan, 2=Feb,... 12=Dec.
   * @throws Exception
   */
  public static void main_(String[] args) throws Exception
  {
    String yearStr  = null;
    String monthStr = null;
    
    int year  = -1;
    int month = -1;
    
    if (args.length != 4)
      throw new RuntimeException("Wrong number of arguments: -year 2011 -month 2, for Feb 2011.");
    else
    {
      for (int i=0; i<args.length; i++)
      {
        if (args[i].equals("-year"))
          yearStr = args[i+1];
        else if (args[i].equals("-month"))
          monthStr = args[i+1];
      }
      try { year = Integer.parseInt(yearStr); } catch (NumberFormatException nfe) 
      {
        throw (nfe);
      }
      try { month = Integer.parseInt(monthStr); } catch (NumberFormatException nfe) 
      {
        throw (nfe);
      }
    }
//  long before = System.currentTimeMillis();
//  BackEndTideComputer.setVerbose(verbose);
//  XMLDocument constituents = BackEndXMLTideComputer.loadDOM(CONSTITUENT_FILE);
//  long after = System.currentTimeMillis();
//  if (verbose) System.out.println("DOM loading took " + Long.toString(after - before) + " ms");

    List<Coefficient> constSpeed = BackEndTideComputer.buildSiteConstSpeed();

    String location = "Oyster Point Marina";
//  String location = "Adelaide";
    System.out.println("-- " + location + " --");
    System.out.println("Date and time zone:" + "Etc/UTC");
    tideForOneMonth(System.out, "Etc/UTC", year, month, location, "meters", constSpeed);
  }
  
  public static void tideForOneMonth(PrintStream out, 
                                     String timeZone, 
                                     int year, 
                                     int month, 
                                     String location, 
                                     String unitToUse, 
                                     List<Coefficient> constSpeed) throws Exception
  {
    tideForOneMonth(out, timeZone, year, month, location, unitToUse, constSpeed, TEXT_FLAVOR, null);
  }
  
  public final static int TEXT_FLAVOR = 0;
  public final static int XML_FLAVOR  = 1;
  
  public static void tideForOneMonth(PrintStream out, 
                                     String timeZone, 
                                     int year, 
                                     int month, 
                                     String location, 
                                     String unitToUse, 
                                     List<Coefficient> constSpeed, 
                                     int flavor,
                                     PrintDialog.SpecialPrm sPrm) throws Exception
  {
    int nextMonth = (month==12)?0:month;
    Calendar firstDay = new GregorianCalendar(year, month - 1, 1);
    Calendar now = firstDay;    
    
    TideStation ts = BackEndTideComputer.findTideStation(location, now.get(Calendar.YEAR)); 
    int prevYear =  now.get(Calendar.YEAR);
    boolean loop = true;    
    while (loop) 
    {      
      String mess = now.getTime().toString();
      TideContext.getInstance().fireSetStatus(mess);
      System.out.println(" -- " + mess);
      // If year changes, recompute TideStation
      if (now.get(Calendar.YEAR) != prevYear)
        ts = BackEndTideComputer.findTideStation(location, now.get(Calendar.YEAR)); 
      
      List<TimedValue> timeAL = tideForOneDay(now, timeZone, ts, constSpeed, unitToUse);
      Calendar utcCal = (Calendar)now.clone();
      utcCal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
      // System.out.println("UTC Date:" + utcCal.getTime());
      double moonPhase = AstroComputer.getMoonPhase(utcCal.get(Calendar.YEAR), 
                                                   utcCal.get(Calendar.MONTH) + 1, 
                                                   utcCal.get(Calendar.DAY_OF_MONTH), 
                                                   utcCal.get(Calendar.HOUR_OF_DAY), 
                                                   utcCal.get(Calendar.MINUTE), 
                                                   utcCal.get(Calendar.SECOND));
      double[] rsSun  = AstroComputer.sunRiseAndSet(ts.getLatitude(), ts.getLongitude());
      Calendar sunRise = new GregorianCalendar();
      sunRise.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
      sunRise.set(Calendar.YEAR, now.get(Calendar.YEAR));
      sunRise.set(Calendar.MONTH, now.get(Calendar.MONTH));
      sunRise.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
      sunRise.set(Calendar.SECOND, 0);
                    
      double r = rsSun[AstroComputer.UTC_RISE_IDX] /* + Utils.daylightOffset(sunRise) */ + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone /*ts.getTimeZone()*/), sunRise.getTime());
      int min = (int)((r - ((int)r)) * 60);
      sunRise.set(Calendar.MINUTE, min);
      sunRise.set(Calendar.HOUR_OF_DAY, (int)r);
      
      Calendar sunSet = new GregorianCalendar();
      sunSet.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
      sunSet.set(Calendar.YEAR, now.get(Calendar.YEAR));
      sunSet.set(Calendar.MONTH, now.get(Calendar.MONTH));
      sunSet.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
      sunSet.set(Calendar.SECOND, 0);
      r = rsSun[AstroComputer.UTC_SET_IDX] /* + Utils.daylightOffset(sunSet) */ + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone/*ts.getTimeZone()*/), sunSet.getTime());
      min = (int)((r - ((int)r)) * 60);
      sunSet.set(Calendar.MINUTE, min);
      sunSet.set(Calendar.HOUR_OF_DAY, (int)r);

      int phaseInDay = (int)Math.round(moonPhase / (360d / 28d)) + 1;
      if (phaseInDay > 28) phaseInDay = 28;
      if (phaseInDay < 1) phaseInDay = 1;
      if (timeZone != null)
        TF.setTimeZone(TimeZone.getTimeZone(timeZone));
            
      if (flavor == TEXT_FLAVOR)
      {
        out.println("- " + SDF.format(now.getTime()) + " - Moon Age:" + DF2.format(phaseInDay));
        for (TimedValue tv : timeAL)
          out.println(tv.getType() + ": " + TF.format(tv.getCalendar().getTime()) + " : " + TideUtilities.DF22.format(tv.getValue()) + " " + unitToUse);
      }
      else if (flavor == XML_FLAVOR)
      {
        String specialBG = "specBG='n' ";
        if (sPrm != null)
        {
//        System.out.println("Special BG required");
          for (TimedValue tv : timeAL)
          {
            double tideHour = tv.getCalendar().get(Calendar.HOUR_OF_DAY) + (tv.getCalendar().get(Calendar.MINUTE) / 60d);
            System.out.println("Tidetype:" + tv.getType() + " hour:" + tideHour + " within [" + sPrm.getFromHour() + ", " + sPrm.getToHour() + "], weekday=" + tv.getCalendar().get(Calendar.DAY_OF_WEEK));
            if (((sPrm.getTideType() == SearchPanel.HIGH_TIDE && tv.getType().equals("HW")) ||
                 (sPrm.getTideType() == SearchPanel.LOW_TIDE && tv.getType().equals("LW"))) &&
                 (tideHour >= sPrm.getFromHour() && tideHour <= sPrm.getToHour()))
            {
              boolean go = true;
              // Week days
              if (sPrm.getWeekdays() != null)
              {
                int day = tv.getCalendar().get(Calendar.DAY_OF_WEEK);
                int[] dd = sPrm.getWeekdays();
                System.out.println("See if " + day + " is in [");
                for (int d : dd)
                  System.out.print(d + " ");
                System.out.println();
                go = ((day == Calendar.MONDAY && dd[SearchPanel.MONDAY] == 1) || 
                      (day == Calendar.TUESDAY && dd[SearchPanel.TUESDAY] == 1) ||
                      (day == Calendar.WEDNESDAY && dd[SearchPanel.WEDNESDAY] == 1) ||
                      (day == Calendar.THURSDAY && dd[SearchPanel.THURSDAY] == 1) ||
                      (day == Calendar.FRIDAY && dd[SearchPanel.FRIDAY] == 1) ||
                      (day == Calendar.SATURDAY && dd[SearchPanel.SATURDAY] == 1) ||
                      (day == Calendar.SUNDAY && dd[SearchPanel.SUNDAY] == 1) );
              }
              if (go)
              {
                specialBG = "specBG='y' ";
                break;
              }
            }
          }          
        }
        out.println("<date val='" + SDF.format(now.getTime()) + "' " + specialBG + "moon-phase='" + DF2.format(phaseInDay) + "' sun-rise='" + TF.format(sunRise.getTime()) + "' sun-set='" + TF.format(sunSet.getTime()) + "'>");
        for (TimedValue tv : timeAL)
        {
          if ("Slack".equals(tv.getType()))
            out.println("  <plot type='" + tv.getType() + "' date='" + TF.format(tv.getCalendar().getTime()) + "'/>");
          else
            out.println("  <plot type='" + tv.getType() + "' date='" + TF.format(tv.getCalendar().getTime()) + "' height='" + TideUtilities.DF22.format(tv.getValue()) + "' unit='" + unitToUse + "'/>");
        }
        out.println("</date>");
      }
      
      now.add(Calendar.DAY_OF_MONTH, 1);
      if (now.get(Calendar.MONTH) == nextMonth)
        loop = false;
    }
    TideContext.getInstance().fireSetStatus("Ok!");
  }
  
  public static List<TimedValue> tideForOneDay(Calendar now, 
                                               String timeZone, 
                                               String location,                                      
                                               List<Coefficient> constSpeed, 
                                               String unitToUse) throws Exception
  {
    TideStation ts = BackEndTideComputer.findTideStation(location, now.get(Calendar.YEAR));    
    return tideForOneDay(now, timeZone, ts, constSpeed, unitToUse);
  }
  
  public static List<TimedValue> tideForOneDay(Calendar now, 
                                               String timeZone, 
                                               TideStation ts,                                      
                                               List<Coefficient> constSpeed, 
                                               String unitToUse) throws Exception
  {
    List<TimedValue> timeAL = null;
    final int RISING  =  1;
    final int FALLING = -1;

    double low1  = Double.NaN;
    double low2  = Double.NaN;
    double high1 = Double.NaN;
    double high2 = Double.NaN;
    Calendar low1Cal = null;
    Calendar low2Cal = null;
    Calendar high1Cal = null;
    Calendar high2Cal = null;
    List<TimedValue> slackList = null;
    int trend = 0;

    slackList = new ArrayList<TimedValue>();
    double previousWH = Double.NaN;
    for (int h=0; h<24; h++)
    {
      for (int m=0; m<60; m++)
      {
        Calendar cal = new GregorianCalendar(now.get(Calendar.YEAR),
                                             now.get(Calendar.MONTH),
                                             now.get(Calendar.DAY_OF_MONTH),
                                             h, m);
        if (timeZone != null)
          cal.setTimeZone(TimeZone.getTimeZone(timeZone));
        double wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, cal), ts.getDisplayUnit(), unitToUse);
        if (Double.isNaN(previousWH))
          previousWH = wh;
        else
        {
          if (trend == 0)
          {
            if (previousWH > wh)
              trend = -1;
            else if (previousWH < wh)
              trend = 1;
          }
          else
          {
            if (ts.isCurrentStation())
            {
              if ((previousWH > 0 && wh <= 0) || (previousWH < 0 && wh >= 0))
              {
                slackList.add(new TimedValue("Slack", cal, 0d));
              }
            }
            switch (trend)
            {
              case RISING:
                {
                  Calendar prev = (Calendar)cal.clone();
                  prev.add(Calendar.MINUTE, -1);                                      
                  if (AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone), cal.getTime()) == 
                      AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone), prev.getTime()))
                  {
                    if (previousWH > wh) // Now going down
                    {
                      if (Double.isNaN(high1))
                      {
                        high1 = previousWH;
                        cal.add(Calendar.MINUTE, -1);
                        high1Cal = cal;
                      }
                      else
                      {
                        high2 = previousWH;
                        cal.add(Calendar.MINUTE, -1);
                        high2Cal = cal;
                      }
                      trend = FALLING; // Now falling
                    }
                  }
                }
                break;
              case FALLING:
                {
                  Calendar prev = (Calendar)cal.clone();
                  prev.add(Calendar.MINUTE, -1);                                      
                  if (AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone), cal.getTime()) == 
                      AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone), prev.getTime()))
                  {
                    if (previousWH < wh) // Now going up
                    {
                      if (Double.isNaN(low1))
                      {
                        low1 = previousWH;
                        cal.add(Calendar.MINUTE, -1);
                        low1Cal = cal;
                      }
                      else
                      {
                        low2 = previousWH;
                        cal.add(Calendar.MINUTE, -1);
                        low2Cal = cal;
                      }
                      trend = RISING; // Now rising
                    }
                  }
                }
                break;
            }
          }
          previousWH = wh;
        }
      }
    }
    timeAL = new ArrayList<TimedValue>(4);
    if (low1Cal != null)
      timeAL.add(new TimedValue(ts.isTideStation()?"LW":"ME", low1Cal, low1));
    if (low2Cal != null)
      timeAL.add(new TimedValue(ts.isTideStation()?"LW":"ME", low2Cal, low2));
    if (high1Cal != null)
      timeAL.add(new TimedValue(ts.isTideStation()?"HW":"MF", high1Cal, high1));
    if (high2Cal != null)
      timeAL.add(new TimedValue(ts.isTideStation()?"HW":"MF", high2Cal, high2));

    if (ts.isCurrentStation() && slackList != null && slackList.size() > 0)
    {
      for (TimedValue tv : slackList)
        timeAL.add(tv);
    }
    
    Collections.sort(timeAL);
    return timeAL;
  }
  
  public static class TimedValue implements Comparable<TimedValue>
  {
    private Calendar cal;
    private double   value;
    private String   type = "";
    
    public TimedValue(String type, Calendar cal, double d)
    {
      this.type = type;
      this.cal = cal;
      this.value = d;
    }
    
    public int compareTo(TimedValue tv)
    {
      return this.cal.compareTo(tv.getCalendar());
    }

    public Calendar getCalendar()
    {
      return cal;
    }

    public double getValue()
    {
      return value;
    }

    public String getType()
    {
      return type;
    }
  }
}
