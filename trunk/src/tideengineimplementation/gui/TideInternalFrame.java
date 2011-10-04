package tideengineimplementation.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;

import java.net.URL;

import java.sql.SQLException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import ocss.nmea.parser.GeoPos;

import tideengine.BackEndTideComputer;
import tideengine.Coefficient;
import tideengine.TideStation;
import tideengine.TideUtilities;

import tideengineimplementation.charts.CommandPanel;

import tideengineimplementation.gui.ctx.TideContext;
import tideengineimplementation.gui.ctx.TideEventListener;
import tideengineimplementation.gui.dialog.PrintDialog;
import tideengineimplementation.gui.dialog.SearchPanel;
import tideengineimplementation.gui.main.splash.SplashWindow;
import tideengineimplementation.gui.main.splash.Splasher;
import tideengineimplementation.gui.table.FilterTable;
import tideengineimplementation.gui.table.TimeZoneTable;

import tideengineimplementation.print.TideForOneMonth;

import tideengineimplementation.utils.AstroComputer;
import tideengineimplementation.utils.Utils;


public class TideInternalFrame
  extends JInternalFrame
{
  private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm (z) Z ");
  private final static DecimalFormat DF2 = new DecimalFormat("00");
  private final static DecimalFormat DF22 = new DecimalFormat("#0.00");
  private final static DecimalFormat DF3 = new DecimalFormat("##0");
  private final static SimpleDateFormat SUN_RISE_SET_SDF = new SimpleDateFormat("E dd-MMM-yyyy HH:mm (z)");

  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d-MMM");
  private final static SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("E dd-MMM-yyyy HH:mm:ss Z z");
  
  private final static SimpleDateFormat LOCAL_DATE_FORMAT = new SimpleDateFormat("E dd-MMM-yyyy HH:mm:ss Z z");
  private final static SimpleDateFormat UTC_DATE_FORMAT = new SimpleDateFormat("E dd-MMM-yyyy HH:mm:ss Z");
  static
  {
    UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
  }
  private final static SimpleDateFormat SUITABLE_DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm (Z) z");
  private final static SimpleDateFormat JUST_DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy");
  private final static String FRAME_TITLE = "Tide Computer";

  private transient TideStation ts = null;  
  private transient ArrayList<Coefficient> constSpeed = null;

  private String currentUnit = TideStation.METERS;
  private String timeZone2Use = "";
  private SearchPanel searchDialog = null;

  private JMenuBar menuBar = new JMenuBar();
  private JMenu menuFile = new JMenu();
  private JMenuItem menuFileExit = new JMenuItem();
  private JMenuItem menuFilePrint = new JMenuItem();
  private JMenuItem menuFileSearch = new JMenuItem();
  private JMenuItem menuFileGoogle = new JMenuItem();
  private JMenu menuHelp = new JMenu();
  private JMenuItem menuHelpAbout = new JMenuItem();
  private JLabel statusBar = new JLabel();
  private BorderLayout mainBorderLayout = new BorderLayout();
  private JSplitPane mainSplitPane = new JSplitPane();
  private JScrollPane stationScrollPane = new JScrollPane();
  private JTree stationTree = new JTree();
  private final static String ROOT_NAME = "invisible root";
  private JPanel rightPanel = new JPanel(new BorderLayout());
  private JSplitPane leftPanel = new JSplitPane();
  private FilterTable filterTable = new FilterTable(null);
  private Calendar now = GregorianCalendar.getInstance();
  private TidePanel graphPanel = new TidePanel()
  {
    @Override
    public void mouseMoved(MouseEvent e)
    {
      if (oneDayRadioButton.isSelected())
      {
        double timeWidth = 24D; // One day
        double widthRatio = (double)this.getWidth() / timeWidth;
        double h = e.getX() / widthRatio;
        double m = (h - (int)h) * 60;

        Calendar cal = new GregorianCalendar(now.get(Calendar.YEAR),
                                             now.get(Calendar.MONTH),
                                             now.get(Calendar.DAY_OF_MONTH),
                                             (int)h, (int)Math.round(m));
        cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
        double wh = 0;
        try { wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, cal), ts.getDisplayUnit(), currentUnit); } catch (Exception ex) {}


        this.setToolTipText("<html>" + DF2.format((int)h) + ":" + DF2.format(m) + "<br>" +
                            TideUtilities.DF22PLUS.format(wh) + " " + currentUnit + "</html>");
      }
      else
        this.setToolTipText(null);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                       RenderingHints.VALUE_TEXT_ANTIALIAS_ON);      
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                       RenderingHints.VALUE_ANTIALIAS_ON);      
      
      now.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      TIME_FORMAT.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      FULL_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      super.paintComponent(g);
//    g.setFont(new Font("courier new", Font.PLAIN, 12));
      Calendar from = null, to = null;
      double moonPhase = -1D;
      int prevPhase = -1;
      Calendar sunRise = null;
      Calendar sunSet = null;
      Calendar moonRise = null;
      Calendar moonSet = null;
      
      if (tideStationName != null)
      {
        try
        {
          if (ts != null)
          {
            // Draw the graph.
            g.setColor(Color.WHITE);
            int height = this.getHeight();
            int width  = this.getWidth();
            g.fillRect(0, 0, width, height);
            // Get boundaries
            double[] mm = null;
            if (oneDayRadioButton.isSelected())
              mm = TideUtilities.getMinMaxWH(ts, constSpeed, now);
            else // Lunar Month
            {
              from = (Calendar)now.clone();
              to = (Calendar)now.clone();
              from.add(Calendar.DAY_OF_MONTH, -14);
              to.add(Calendar.DAY_OF_MONTH, 14);
//            System.out.println("From " + from.getTime().toString() + " to " + to.getTime().toString());
              mm = TideUtilities.getMinMaxWH(ts, constSpeed, from, to);
            }
//          System.out.println("At " + tideStationName + " in " + now.get(Calendar.YEAR) + ", min : " + BackEndXMLTideComputer.DF22.format(mm[BackEndXMLTideComputer.MIN_POS]) + " " + ts.getUnit() + ", max : " + BackEndXMLTideComputer.DF22.format(mm[BackEndXMLTideComputer.MAX_POS]) + " " + ts.getDisplayUnit());
            mm[TideUtilities.MIN_POS] = Utils.convert(mm[TideUtilities.MIN_POS], ts.getDisplayUnit(), currentUnit);
            mm[TideUtilities.MAX_POS] = Utils.convert(mm[TideUtilities.MAX_POS], ts.getDisplayUnit(), currentUnit);
            
            float gutter = 2f; // 2 Feet
            if (currentUnit.equals("meters"))
              gutter = (float)TideUtilities.feetToMeters(gutter);
            double timeWidth = 24D; // One day
            if (oneLunarMonthRadioButton.isSelected())
              timeWidth = 24 * 28;  // On lunar Month
            double widthRatio = (double)width / timeWidth;
            double heightRatio = (double)height / ((2 * gutter) + mm[TideUtilities.MAX_POS] - mm[TideUtilities.MIN_POS]);
            double bottomValue = mm[TideUtilities.MIN_POS] - gutter;
              
            // Horizontal grid
            g.setColor(Color.LIGHT_GRAY);
            for (int hgt=(int)Math.floor(mm[TideUtilities.MIN_POS]); hgt<=(int)Math.floor(mm[TideUtilities.MAX_POS]); hgt++)
            {
              g.drawLine(0, height - (int)((hgt - bottomValue) * heightRatio), width, height - (int)((hgt - bottomValue) * heightRatio));
              g.drawString(TideUtilities.DF22.format(hgt), 5, height - (int)((hgt - bottomValue) * heightRatio) - 2);
            }
            // Vertical grid
            FontMetrics fm = g.getFontMetrics();
            for (int hour=2; (from == null && to == null) && hour<24; hour+=2)
            {
              int _x = (int)(hour * widthRatio);
              g.drawLine(_x, 0, _x, height);
              String s = DF2.format(hour);
              g.drawString(s, _x - (fm.stringWidth(s) / 2), height - 14);
            }
            
            double[] rsSun  = null;
            double[] rsMoon = null;
            double moonIllum = 0d;
            // Current time
            if (from == null && to == null)
            {
              int tx = (int)((now.get(Calendar.HOUR_OF_DAY) + (double)(now.get(Calendar.MINUTE) / 60D) + ((double)(now.get(Calendar.SECOND) / 3600D))) * widthRatio);
              g.setColor(Color.GREEN);
              g.drawLine(tx, 0, tx, height);
              
              Calendar utcCal = (Calendar)now.clone();
              utcCal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
           // System.out.println("UTC Date:" + utcCal.getTime());
              moonPhase = AstroComputer.getMoonPhase(utcCal.get(Calendar.YEAR), 
                                                     utcCal.get(Calendar.MONTH) + 1, 
                                                     utcCal.get(Calendar.DAY_OF_MONTH), 
                                                     utcCal.get(Calendar.HOUR_OF_DAY), 
                                                     utcCal.get(Calendar.MINUTE), 
                                                     utcCal.get(Calendar.SECOND));
//            rsSun  = AstroComputer.sunRiseAndSet_wikipedia(ts.getLatitude());
              rsSun  = AstroComputer.sunRiseAndSet(ts.getLatitude());
              rsMoon = AstroComputer.moonRiseAndSet(ts.getLatitude());
              moonIllum = AstroComputer.getMoonIllum();
              
              sunRise = new GregorianCalendar();
              sunRise.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
              sunRise.set(Calendar.YEAR, now.get(Calendar.YEAR));
              sunRise.set(Calendar.MONTH, now.get(Calendar.MONTH));
              sunRise.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
              sunRise.set(Calendar.SECOND, 0);
              double r = rsSun[0] + Utils.daylightOffset(sunRise);
              int min = (int)((r - ((int)r)) * 60);
              sunRise.set(Calendar.MINUTE, min);
              sunRise.set(Calendar.HOUR_OF_DAY, (int)r);
              
              sunSet = new GregorianCalendar();
              sunSet.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
              sunSet.set(Calendar.YEAR, now.get(Calendar.YEAR));
              sunSet.set(Calendar.MONTH, now.get(Calendar.MONTH));
              sunSet.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
              sunSet.set(Calendar.SECOND, 0);
              r = rsSun[1] + Utils.daylightOffset(sunSet);
              min = (int)((r - ((int)r)) * 60);
              sunSet.set(Calendar.MINUTE, min);
              sunSet.set(Calendar.HOUR_OF_DAY, (int)r);
              
              SUN_RISE_SET_SDF.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
              System.out.println("Sun Rise:" + SUN_RISE_SET_SDF.format(sunRise.getTime()) + ", Set:" + SUN_RISE_SET_SDF.format(sunSet.getTime()));

              moonRise = new GregorianCalendar();
              moonRise.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
              moonRise.set(Calendar.YEAR, now.get(Calendar.YEAR));
              moonRise.set(Calendar.MONTH, now.get(Calendar.MONTH));
              moonRise.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
              moonRise.set(Calendar.SECOND, 0);
              r = rsMoon[0] + Utils.daylightOffset(moonRise);
              min = (int)((r - ((int)r)) * 60);
              moonRise.set(Calendar.MINUTE, min);
              moonRise.set(Calendar.HOUR_OF_DAY, (int)r);
              
              moonSet = new GregorianCalendar();
              moonSet.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
              moonSet.set(Calendar.YEAR, now.get(Calendar.YEAR));
              moonSet.set(Calendar.MONTH, now.get(Calendar.MONTH));
              moonSet.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
              moonSet.set(Calendar.SECOND, 0);
              r = rsMoon[1] + Utils.daylightOffset(moonSet);
              min = (int)((r - ((int)r)) * 60);
              moonSet.set(Calendar.MINUTE, min);
              moonSet.set(Calendar.HOUR_OF_DAY, (int)r);
              
              SUN_RISE_SET_SDF.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
              System.out.println("Moon Rise:" + SUN_RISE_SET_SDF.format(moonRise.getTime()) + ", Set:" + SUN_RISE_SET_SDF.format(moonSet.getTime()));

              // Paint background for Sun
              ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
              g.setColor(Color.LIGHT_GRAY);
              
              sunRise.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
              sunSet.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
              moonRise.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
              moonSet.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
              
              SUN_RISE_SET_SDF.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
              System.out.println("Sun Rise:" + SUN_RISE_SET_SDF.format(sunRise.getTime()) + ", Set:" + SUN_RISE_SET_SDF.format(sunSet.getTime()));

              if (sunRise.get(Calendar.DAY_OF_MONTH) != sunSet.get(Calendar.DAY_OF_MONTH)) // TASK Not always right...
              {
                sunSet.set(Calendar.YEAR, sunRise.get(Calendar.YEAR));
                sunSet.set(Calendar.MONTH, sunRise.get(Calendar.MONTH));
                sunSet.set(Calendar.DAY_OF_MONTH, sunRise.get(Calendar.DAY_OF_MONTH));
              }
              
              if (sunRise.before(sunSet))
              {
                int x = (int)((sunRise.get(Calendar.HOUR_OF_DAY) + (double)(sunRise.get(Calendar.MINUTE) / 60D)) * widthRatio);
                if (x > 0)
                  g.fillRect(0, 0, x, this.getHeight());
                x = (int)((sunSet.get(Calendar.HOUR_OF_DAY) + (double)(sunSet.get(Calendar.MINUTE) / 60D)) * widthRatio);
                if (x < this.getWidth())
                  g.fillRect(x, 0, this.getWidth() - x, this.getHeight());
              }
              if (sunSet.before(sunRise))
              {
                int x1 = (int)((sunSet.get(Calendar.HOUR_OF_DAY) + (double)(sunSet.get(Calendar.MINUTE) / 60D)) * widthRatio);
                int x2 = (int)((sunRise.get(Calendar.HOUR_OF_DAY) + (double)(sunRise.get(Calendar.MINUTE) / 60D)) * widthRatio);
                  g.fillRect(x1, 0, x2 - x1, this.getHeight());
              }
              ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            Point previous = null;
            // Draw here            
            double low1  = Double.NaN;
            double low2  = Double.NaN;
            double high1 = Double.NaN;
            double high2 = Double.NaN;
            Calendar low1Cal = null;
            Calendar low2Cal = null;
            Calendar high1Cal = null;
            Calendar high2Cal = null;
            int trend = 0;
            
            double previousWH = Double.NaN;
            boolean keepLooping = true;
            Calendar reference = (Calendar)now.clone();
            
            int currDay = 0;
            if (from != null && to != null)
              reference = (Calendar)from.clone();
            
            // Decompose
            if (decomposeCheckBox.isSelected() && from == null && to == null)              
            {
//            g.setColor(Color.CYAN);
//            ((Graphics2D) g).setStroke(origStroke);
              for (int i=0; i<constSpeed.size(); i++)
              {
                g.setColor(randomColor[i]);
                Point previousVal = null;
                for (int h=0; h<24; h++)
                {
                  for (int m=0; m<60; m++)
                  {
                    Calendar cal = new GregorianCalendar(reference.get(Calendar.YEAR),
                                                         reference.get(Calendar.MONTH),
                                                         reference.get(Calendar.DAY_OF_MONTH),
                                                         h, m);
                    cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                    int year = cal.get(Calendar.YEAR); 
                    // Calc Jan 1st of the current year
                    Date jan1st = new GregorianCalendar(year, 0, 1).getTime();
                    double value = Utils.convert(TideUtilities.getHarmonicValue(cal.getTime(), jan1st, ts, constSpeed, i), ts.getDisplayUnit(), currentUnit);
                    int x = (int)(((currDay * 24) + h + (double)(m / 60D)) * widthRatio);
                    int y = height - (int)((value - bottomValue) * heightRatio);
                    if (previousVal != null)
                      g.drawLine(previousVal.x, previousVal.y, x, y);
                    previousVal = new Point(x, y);                            
                  }
                }
              }                
//            ((Graphics2D) g).setStroke(mainCurveStroke);
            }

            Stroke origStroke = ((Graphics2D)g).getStroke();
            Stroke mainCurveStroke = null;
            if (from == null && to == null)
            {
              mainCurveStroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
              ((Graphics2D) g).setStroke(mainCurveStroke);
            }
            else
            {
              mainCurveStroke = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
              ((Graphics2D) g).setStroke(mainCurveStroke);
            }
            if (showBaseHeightCheckBox.isSelected())
            {
              g.setColor(Color.BLUE);
              double bh = ts.getBaseHeight();
              bh = Utils.convert(bh, ts.getDisplayUnit(), currentUnit);
              int y = height - (int)((bh - bottomValue) * heightRatio);
              g.drawLine(0, y, this.getWidth(), y);
            }
            g.setColor(Color.RED);            
            /* The curve */
            Polygon curvePolygon = new Polygon();
            while (keepLooping)
            {
              if (from == null && to == null)
                keepLooping = false;
              else
              {
//              System.out.println("Calculating tide for " + reference.getTime().toString());
                if (!reference.before(to))
                {
                  keepLooping = false;
//                System.out.println("Exiting loop:" + reference.getTime().toString() + " after " + to.getTime().toString());
                }
              }
              for (int h=0; h<24; h++)
              {
                if (from != null && to != null)
                {
                  Calendar utcCal = new GregorianCalendar(reference.get(Calendar.YEAR), 
                                                          reference.get(Calendar.MONTH), 
                                                          reference.get(Calendar.DAY_OF_MONTH), 
                                                          h, 
                                                          0, 
                                                          0);
                  utcCal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                  moonPhase = AstroComputer.getMoonPhase(utcCal.get(Calendar.YEAR), 
                                                        utcCal.get(Calendar.MONTH) + 1, 
                                                        utcCal.get(Calendar.DAY_OF_MONTH), 
                                                        utcCal.get(Calendar.HOUR_OF_DAY), 
                                                        0, 
                                                        0);
//                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM HH:mm Z");
//                System.out.println("At " + sdf.format(utcCal.getTime()) + ", phase = " + moonPhase);
                }
                for (int m=0; m<60; m++)
                {
                  Calendar cal = new GregorianCalendar(reference.get(Calendar.YEAR),
                                                       reference.get(Calendar.MONTH),
                                                       reference.get(Calendar.DAY_OF_MONTH),
                                                       h, m);
                  cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                  double wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, cal), ts.getDisplayUnit(), currentUnit);
                  if (!Double.isNaN(previousWH))
                  {
                    if (from == null && to == null)
                    {
                      if (trend == 0)
                      {
                        if (previousWH > wh)
                          trend = TidePanel.FALLING;
                        else if (previousWH < wh)
                          trend = TidePanel.RISING;
                      }
                      else
                      {
                        switch (trend)
                        {
                          case RISING:
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
                            break;
                          case FALLING:
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
                            break;
                        }
                      }
                    }
                    int x = (int)(((currDay * 24) + h + (double)(m / 60D)) * widthRatio);
                    int y = height - (int)((wh - bottomValue) * heightRatio);
                    if (previous != null)
                      g.drawLine(previous.x, previous.y, x, y);
                    previous = new Point(x, y);                      
                    curvePolygon.addPoint(x, y);
                    if (from != null && to != null)  
                    {
                      if (h == 0 && m == 0)
                      {
                        Color c = g.getColor();
                        g.setColor(Color.DARK_GRAY);
                        g.drawLine(x, 0, x, height);
                        g.setColor(c);
                      }
                      if (h == 12 && m == 0)
                      {
                        String dStr = DATE_FORMAT.format(cal.getTime());
                        Color c = g.getColor();
                        g.setColor(Color.DARK_GRAY);
                        Font f = g.getFont();
//                      Font f2 = new Font("Arial", Font.PLAIN, 9); // f.deriveFont(Font.PLAIN, 6);
                        g.setFont(g.getFont().deriveFont(Font.PLAIN, 9));
                        int l = g.getFontMetrics(g.getFont()).stringWidth(dStr);
                        g.drawString(dStr, x - (l/2), 5 + (g.getFont().getSize() * ((currDay % 2) + 1)));
                        g.setFont(f);
                        g.setColor(c);
                      }
                      Calendar currentDate = GregorianCalendar.getInstance();
                      // If Today, draw line for current time
                      if (currentDate.get(Calendar.YEAR)         == cal.get(Calendar.YEAR) &&
                          currentDate.get(Calendar.MONTH)        == cal.get(Calendar.MONTH) &&
                          currentDate.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
                          currentDate.get(Calendar.HOUR_OF_DAY)  == cal.get(Calendar.HOUR_OF_DAY) &&
                          currentDate.get(Calendar.MINUTE)       == cal.get(Calendar.MINUTE) /* &&
                          currentDate.get(Calendar.SECOND)       == cal.get(Calendar.SECOND) */)
                      {
                        Color c = g.getColor();
                        g.setColor(Color.GREEN);
                        g.drawLine(x, 0, x, height);
                        g.setColor(c);
                      }                      
                      if (((int)Math.round(moonPhase) == 0 && prevPhase != 0 && prevPhase != 360) ||
                          ((int)Math.round(moonPhase) == 90 && prevPhase != 90) ||
                          ((int)Math.round(moonPhase) == 180 && prevPhase != 180) ||
                          ((int)Math.round(moonPhase) == 270 && prevPhase != 270) ||
                          ((int)Math.round(moonPhase) == 360 && prevPhase != 360 && prevPhase != 0))
                      {
                        // Draw line for moon phase (for now)
                        Color c = g.getColor();
                        g.setColor(Color.BLUE);
                        g.drawLine(x, 0, x, height);
                        String phaseStr = "";
                        switch ((int)Math.round(moonPhase))
                        {
                          case 0:
                          case 360:
                            phaseStr = "NM";
                            break;
                          case 90:
                            phaseStr = "FQ";
                            break;
                          case 180:
                            phaseStr = "FM";
                            break;
                          case 270:
                            phaseStr = "LQ";
                            break;
                          default:
                            break;
                        }
                        g.setColor(c);
                        drawMoon((Graphics2D)g, x, height - 15, 12, moonPhase, phaseStr);
                        prevPhase = (int)Math.round(moonPhase);
                      }
                    }
                  }
                  previousWH = wh;
                }
              }
              if (from != null && to != null)
                reference.add(Calendar.DAY_OF_YEAR, 1);
              currDay++;
    //        System.out.println("Day " + currDay + " widthRatio:" + widthRatio);
            }
            ((Graphics2D) g).setStroke(origStroke);
            if (from == null && to == null)
            {
              // Paint the lower part of the curve
              curvePolygon.addPoint(this.getWidth(), this.getHeight());
              curvePolygon.addPoint(0, this.getHeight());
              GradientPaint gradient = new GradientPaint(0, this.getHeight(), Color.WHITE, 0, 0, Color.BLUE); // vertical, upside down
              Paint paint = ((Graphics2D)g).getPaint();
              ((Graphics2D)g).setPaint(gradient);              
              ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
              g.fillPolygon(curvePolygon);
              ((Graphics2D)g).setPaint(paint);              
              ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            int x = 5, y = 12;
            g.setColor(Color.BLUE);
            if (from == null && to == null)
            {
              ArrayList<TimedValue> timeAL = new ArrayList<TimedValue>(4); 
              if (low1Cal != null)
                timeAL.add(new TimedValue("LW", low1Cal, low1));
              if (low2Cal != null)
                timeAL.add(new TimedValue("LW", low2Cal, low2));
              if (high1Cal != null)
                timeAL.add(new TimedValue("HW", high1Cal, high1));
              if (high2Cal != null)
                timeAL.add(new TimedValue("HW", high2Cal, high2));
              
              Collections.sort(timeAL);
              // Station Name            
              int fontSize = 12;
//            g.setFont(new Font("Arial", Font.PLAIN, fontSize));
              g.setFont(g.getFont().deriveFont(Font.PLAIN, fontSize));
              Font f = g.getFont();
              g.setFont(f.deriveFont(Font.BOLD, f.getSize()));
              g.drawString(ts.getFullName() + ", " + FULL_DATE_FORMAT.format(now.getTime()), x, y);
              g.setFont(f);
              y += (fontSize + 2);
              // Station Position and base height
              g.drawString(new GeoPos(ts.getLatitude(), ts.getLongitude()).toString() + " - Base Height : " + DF22.format(Utils.convert(ts.getBaseHeight(), ts.getDisplayUnit(), currentUnit)) + " " + currentUnit, x, y);
              y += (fontSize + 2);
              // Sun rise & set
              SUN_RISE_SET_SDF.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
              g.drawString("Sun Rise :" + SUN_RISE_SET_SDF.format(sunRise.getTime()) + ", Set:" + SUN_RISE_SET_SDF.format(sunSet.getTime()), x, y);
              y += (fontSize + 2);              
              g.drawString("Moon Rise:" + SUN_RISE_SET_SDF.format(moonRise.getTime()) + ", Set:" + SUN_RISE_SET_SDF.format(moonSet.getTime()), x, y);
              y += (fontSize + 2);       
              double prevHeight = -Double.MAX_VALUE;
              int diffOffset = -1;
              for (TimedValue tv : timeAL)
              {
                String dataStr = tv.getType() + " " + TIME_FORMAT.format(tv.getCalendar().getTime()) + " : " + TideUtilities.DF22PLUS.format(tv.getValue()) + " " + /*ts.getDisplayUnit()*/ currentUnit;
                if (diffOffset == -1)
                {
                  int len = g.getFontMetrics(g.getFont()).stringWidth(dataStr);
                  int tabSize = 50;
                  diffOffset = (int)((len / tabSize) + 1) * tabSize;
                }
                g.drawString(dataStr, x, y);
                if (prevHeight != -Double.MAX_VALUE && !currentUnit.startsWith("knots"))
                {
                  double ampl = Math.abs(prevHeight - tv.getValue());
                  String diffStr = "\u0394 : " + TideUtilities.DF22.format(ampl) + " " + currentUnit; // \u0394 : Delta
                  g.drawString(diffStr, x + diffOffset, y - (fontSize / 2));
                }
                y += (fontSize + 2);                
                prevHeight = tv.getValue();
              }
              // Moon Phase
              //     Percentage
              g.drawString("Moon Phase: " + DF3.format(moonPhase) + "\272 (" + Long.toString(Math.round(moonIllum)) + "%)", x, y);
              int phaseInDay = (int)Math.round(moonPhase / (360d / 28d)) + 1;
              if (phaseInDay > 28) phaseInDay = 28;
              if (phaseInDay < 1) phaseInDay = 1;
              String moonImageName = "img/phase" + DF2.format(phaseInDay) + ".gif"; // ".png"
//            System.out.println("Image Name:" + moonImageName + " monn phase:" + moonPhase);
              URL imgUrl = this.getClass().getResource(moonImageName);
  //          System.out.println("Phase Image:" + imgUrl.toString());
              Image moon = new ImageIcon(imgUrl).getImage();
              g.drawImage(moon, this.getWidth() - 50 -10, 10, null); // 50: image width (gif 50, png 30)
              y += (fontSize + 2);
              // Current Height
              y += (fontSize + 2);
              double wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, now), ts.getDisplayUnit(), currentUnit);
              g.drawString("At " + TIME_FORMAT.format(now.getTime()) + " : " + TideUtilities.DF22PLUS.format(wh) + " " + /*ts.getDisplayUnit()*/ currentUnit, x, y);
              y += (fontSize + 2);
            }
          }
        }
        catch (Exception ex)
        {
          JOptionPane.showMessageDialog(this, ex.toString(), "Oops", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    }
  };
    
  private JPanel buttonPanel = new JPanel();
  
  private String tideStationName = null;
  private JButton backOneYearButton = new JButton();
  private JButton backOneMonthButton = new JButton();
  private JButton backOneWeekButton = new JButton();
  private JButton backOneDayButton = new JButton();
  private JButton forwardOneDayButton = new JButton();
  private JButton forwardOneWeekButton = new JButton();
  private JButton forwardOneMonthButton = new JButton();
  private JButton forwardOneYearButton = new JButton();
  private JButton nowButton = new JButton();
  private JRadioButton oneLunarMonthRadioButton = new JRadioButton();
  private JRadioButton oneDayRadioButton = new JRadioButton();
  private JComboBox unitComboBox = new JComboBox();
  
  private ButtonGroup group = new ButtonGroup();
  
  CommandPanel chartCommandPanel = new CommandPanel();
  JTabbedPane tabbedPane = new JTabbedPane();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JPanel topButtonPanel = new JPanel();
  private JPanel bottomButtonPanel = new JPanel();
  private JLabel useUnitLabel = new JLabel();
  private JLabel stationTZLabel = new JLabel();
  private JLabel stationTimeZoneLabel = new JLabel();
  private JLabel useTZLabel = new JLabel();
  private JComboBox tzComboBox = new JComboBox();
  private JLabel displayLabel = new JLabel();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private GridBagLayout gridBagLayout3 = new GridBagLayout();
  private JButton findTimeZoneButton = new JButton();
  
  private Color[] randomColor = null;
  private JCheckBox decomposeCheckBox = new JCheckBox();
  private JCheckBox showBaseHeightCheckBox = new JCheckBox();

  public TideInternalFrame()
  {
    this(null);
  }

  public TideInternalFrame(final JFrame parent)
  {
    try
    {
      System.out.println("Loading...");
       
      for (TideEventListener tel : TideContext.getInstance().getListeners())
        tel.beginLoad();
      Thread splashThread = new Thread()
        {
          public void run()
          {
            SplashWindow.splash(Splasher.class.getResource("paperboat.png"), parent);
          }
        };
      splashThread.start();
      jbInit();
      System.out.println("Loaded!");
      SplashWindow.disposeSplash();
      for (TideEventListener tel : TideContext.getInstance().getListeners())
        tel.stopLoad();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    try
    {
      if (System.getProperty("tide.flavor", "xml").equals("xml"))
        BackEndTideComputer.connect(BackEndTideComputer.XML_OPTION);
      else
        BackEndTideComputer.connect(BackEndTideComputer.SQL_OPTION);
    }
    catch (Exception ex)
    {
      if (ex instanceof SQLException)
        JOptionPane.showMessageDialog(this, ex.toString(), "Database Connection", JOptionPane.ERROR_MESSAGE);
      else
        JOptionPane.showMessageDialog(this, ex.toString(), "XML Data", JOptionPane.ERROR_MESSAGE);
    }
    TideContext.getInstance().addTideListener(new TideEventListener()
      {
        @Override
        public void stationSelected(String stationName)
        {
          displayTide(stationName);
        }
        @Override
        public void setDate(long date)
        {
          now.setTime(new Date(date));
          graphPanel.repaint();          
        }
      });
    this.setJMenuBar( menuBar );
    this.getContentPane().setLayout(mainBorderLayout);
    this.setSize(new Dimension(1080, 650));
    this.setTitle( "Oliv's Tide Computer" );
    menuFile.setText( "File" );
    menuFilePrint.setText("Print");
    menuFilePrint.setEnabled(false);
    menuFilePrint.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { filePrint_ActionPerformed( ae ); } } );
    menuFileSearch.setText("Search");
    menuFileSearch.setEnabled(false);
    menuFileSearch.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileSearch_ActionPerformed( ae ); } } );
    menuFileGoogle.setText("Google Map");
    menuFileGoogle.setEnabled(false);
    menuFileGoogle.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileGoogle_ActionPerformed( ae ); } } );    
    menuFileExit.setText( "Exit" );
    menuFileExit.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileExit_ActionPerformed( ae ); } } );
    menuHelp.setText( "Help" );
    menuHelpAbout.setText( "About" );
    menuHelpAbout.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { helpAbout_ActionPerformed( ae ); } } );
    statusBar.setText( "" );
    menuFile.add( menuFilePrint );
    menuFile.add( menuFileSearch );
    menuFile.add( menuFileGoogle );
    menuFile.add(new JSeparator());
    menuFile.add( menuFileExit );
    menuBar.add( menuFile );
    menuHelp.add( menuHelpAbout );
    menuBar.add( menuHelp );
    this.getContentPane().add( statusBar, BorderLayout.SOUTH );
    stationScrollPane.getViewport().add(stationTree, null);
    stationTree.setCellRenderer(new StationTreeCellRenderer());
    mainSplitPane.setDividerLocation(300);
    leftPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
    leftPanel.add(filterTable, JSplitPane.LEFT);
    leftPanel.add(stationScrollPane, JSplitPane.RIGHT);
    leftPanel.setDividerLocation(300);

    unitComboBox.removeAllItems();
    // Default values
    unitComboBox.addItem(TideStation.METERS);
    unitComboBox.addItem(TideStation.FEET);
    unitComboBox.setPreferredSize(new Dimension(75, 21));
    unitComboBox.setMinimumSize(new Dimension(55, 21));
    unitComboBox.setSize(new Dimension(75, 21));
    unitComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          currentUnit = (String)unitComboBox.getSelectedItem();
          graphPanel.repaint();
        }
      });

    buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    buttonPanel.setLayout(gridBagLayout1);
    bottomButtonPanel.setLayout(gridBagLayout2);
    topButtonPanel.setLayout(gridBagLayout3);
    useUnitLabel.setText("Use Unit");
    stationTZLabel.setText("Station Time Zone");
    stationTimeZoneLabel.setText("Etc/UTC");
//  stationTimeZoneLabel.setFont(new Font("Tahoma", 1, 11));
    stationTimeZoneLabel.setFont(stationTimeZoneLabel.getFont().deriveFont(Font.BOLD, 11));
    useTZLabel.setText("Use Time Zone");
    tzComboBox.setMinimumSize(new Dimension(100, 21));
    tzComboBox.removeAllItems();
    String[] tz = TimeZone.getAvailableIDs();
    for (int i=0; i<tz.length; i++)
      tzComboBox.addItem(tz[i]);
    tzComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timeZone2Use = (String)tzComboBox.getSelectedItem();
          graphPanel.repaint();
        }
      });
    
    tzComboBox.setPreferredSize(new Dimension(140, 21));
    displayLabel.setText("Display : ");
    findTimeZoneButton.setText("?");
    findTimeZoneButton.setToolTipText("Find Time Zone");
    findTimeZoneButton.setMargin(new Insets(1, 1, 1, 1));
    findTimeZoneButton.setMaximumSize(new Dimension(21, 21));
    findTimeZoneButton.setMinimumSize(new Dimension(21, 21));
    findTimeZoneButton.setPreferredSize(new Dimension(21, 21));
    findTimeZoneButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          findTimeZoneButton_actionPerformed(e);
        }
      });
    decomposeCheckBox.setText("Decompose");
    decomposeCheckBox.setToolTipText("<html><b>Warning:</b><br>This is a demanding operation...</html>");
    showBaseHeightCheckBox.setText("Show Base Height");
    group.add(oneLunarMonthRadioButton);
    group.add(oneDayRadioButton);

    buttonPanel.add(topButtonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(bottomButtonPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));

    topButtonPanel.add(backOneYearButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(backOneMonthButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(backOneWeekButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(backOneDayButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(nowButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneDayButton, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneWeekButton, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneMonthButton, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneYearButton, new GridBagConstraints(10, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));

    topButtonPanel.add(decomposeCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 20), 0, 0));
    topButtonPanel.add(showBaseHeightCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 5), 0, 0));
    bottomButtonPanel.add(useUnitLabel,
                          new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                 new Insets(5, 5, 5, 3), 0, 0));
    bottomButtonPanel.add(unitComboBox,
                          new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                                                 new Insets(5, 0, 5, 0), 0, 0));
    bottomButtonPanel.add(stationTZLabel,
                          new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                 new Insets(5, 5, 5, 0), 0, 0));
    bottomButtonPanel.add(stationTimeZoneLabel,
                          new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                 new Insets(5, 5, 5, 0), 0, 0));
    bottomButtonPanel.add(useTZLabel,
                          new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                 new Insets(5, 5, 5, 5), 0, 0));
    bottomButtonPanel.add(tzComboBox,
                          new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                 new Insets(5, 0, 5, 0), 0, 0));
    bottomButtonPanel.add(displayLabel,
                          new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                 new Insets(5, 8, 5, 5), 0, 0));
    bottomButtonPanel.add(oneDayRadioButton,
                          new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                 new Insets(5, 0, 5, 0), 0, 0));
    bottomButtonPanel.add(oneLunarMonthRadioButton,
                          new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                 new Insets(5, 0, 5, 0), 0, 0));
    bottomButtonPanel.add(findTimeZoneButton,
                          new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(buttonPanel, BorderLayout.NORTH);
    rightPanel.add(graphPanel, BorderLayout.CENTER);

    //  leftPanel.setPreferredSize(new Dimension(300, 600)); // TEMP
    tabbedPane.add("Tide Curves", rightPanel);
    tabbedPane.add("Tide Stations", chartCommandPanel);

    mainSplitPane.add(leftPanel, JSplitPane.LEFT);
    mainSplitPane.add(tabbedPane, JSplitPane.RIGHT);

    getContentPane().add(mainSplitPane, BorderLayout.CENTER);

    Thread sdThread = new Thread()
      {
        public void run()
        {
          try
          { 
            System.out.println("Reading Station Data...");
            ArrayList<TideStation> stationData = BackEndTideComputer.getStationData();
            System.out.println("Done!");
            chartCommandPanel.setStationData(stationData);
            chartCommandPanel.repaint();
            filterTable.setStationData(stationData);
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      };
    sdThread.start();

    this.addInternalFrameListener(new InternalFrameAdapter()
      {
        public void internalFrameClosed(InternalFrameEvent e)
        {
          try { BackEndTideComputer.disconnect(); } catch (Exception ex) { ex.printStackTrace(); }
          this_internalFrameClosed(e);
        }                
      });

    stationTree.setRootVisible(false); // Hide the root
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(ROOT_NAME, true);
    TreeModel treeModel = new DefaultTreeModel(root);
    stationTree.setModel(treeModel);
    ToolTipManager.sharedInstance().registerComponent(stationTree);

    // Tide Specific part
    constSpeed = BackEndTideComputer.buildSiteConstSpeed();
    // Random Colors array
    randomColor = new Color[constSpeed.size()];
    for (int i=0; i<constSpeed.size(); i++)
      randomColor[i] = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));

    // Populate the station tree
    TreeMap<String, TideUtilities.StationTreeNode> tree = BackEndTideComputer.buildStationTree();
    root.removeAllChildren();
    renderTree(tree, root);
    ((DefaultTreeModel)stationTree.getModel()).reload(root);
    
    stationTree.addMouseListener(new MouseListener()
        {
          public void mouseClicked(MouseEvent e)
          {
          }

          public void mousePressed(MouseEvent e)
          {
            // tryPopup(e);
          }

          public void mouseReleased(MouseEvent e)
          {
            if (e.getClickCount() == 2)
            {
              dblClicked(e);
            }
            else
            {
              // tryPopup(e);
            }
          }

          public void mouseEntered(MouseEvent e)
          {
          }

          public void mouseExited(MouseEvent e)
          {
          }

          private void dblClicked(MouseEvent e)
          {
            if (e.isConsumed())
            {
              return;
            }
            // Let's make sure we only invoke double click action when
            // we have a treepath. For example; This avoids opening an editor on a
            // selected node when the user double clicks on the expand/collapse icon.
            if (e.getClickCount() == 2)
            {
              if (stationTree.getPathForLocation(e.getX(), e.getY()) != null)
              {
                DefaultMutableTreeNode dtn = (DefaultMutableTreeNode) stationTree.getLastSelectedPathComponent();
                if (dtn.getChildCount() == 0) // This is a leaf
                {
                  Object o = dtn.getUserObject();
                  if (o instanceof TideUtilities.StationTreeNode)
                  {
                    TideUtilities.StationTreeNode stn = (TideUtilities.StationTreeNode)o;
                    TideContext.getInstance().fireStationSelected(stn.getFullStationName());
                  }
                }
              }
            }
            else if (e.getClickCount() > 2)
            {
              // Fix triple-click wanna-be drag events...
              e.consume();
            }
          }
        });

    graphPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    buttonPanel.setLayout(gridBagLayout1);
//  buttonPanel.setPreferredSize(new Dimension(764, 66));
    backOneYearButton.setText("< Y");
    backOneYearButton.setToolTipText("Back 1 Year");
    backOneYearButton.setMaximumSize(new Dimension(50, 21));
    backOneYearButton.setMinimumSize(new Dimension(50, 21));
    backOneYearButton.setPreferredSize(new Dimension(50, 21));
    backOneYearButton.setMargin(new Insets(1, 1, 1, 1));
    backOneYearButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneYearButton_actionPerformed(e);
        }
      });
    backOneMonthButton.setText("< M");
    backOneMonthButton.setToolTipText("Back 1 Month");
    backOneMonthButton.setMargin(new Insets(1, 1, 1, 1));
    backOneMonthButton.setMaximumSize(new Dimension(50, 21));
    backOneMonthButton.setMinimumSize(new Dimension(50, 21));
    backOneMonthButton.setPreferredSize(new Dimension(50, 21));
    backOneMonthButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneMonthButton_actionPerformed(e);
        }
      });
    backOneWeekButton.setText("< W");
    backOneWeekButton.setToolTipText("Back 1 Week");
    backOneWeekButton.setMargin(new Insets(1, 1, 1, 1));
    backOneWeekButton.setMaximumSize(new Dimension(50, 21));
    backOneWeekButton.setMinimumSize(new Dimension(50, 21));
    backOneWeekButton.setPreferredSize(new Dimension(50, 21));
    backOneWeekButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneWeekButton_actionPerformed(e);
        }
      });
    backOneDayButton.setText("< D");
    backOneDayButton.setToolTipText("Back 1 Day (Shift: 1 Hour)");
    backOneDayButton.setMargin(new Insets(1, 1, 1, 1));
    backOneDayButton.setMaximumSize(new Dimension(50, 21));
    backOneDayButton.setMinimumSize(new Dimension(50, 21));
    backOneDayButton.setPreferredSize(new Dimension(50, 21));
    backOneDayButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneDayButton_actionPerformed(e);
        }
      });
    forwardOneDayButton.setText("D >");
    forwardOneDayButton.setToolTipText("Forward 1 Day (Shift: 1 Hour)");
    forwardOneDayButton.setMargin(new Insets(1, 1, 1, 1));
    forwardOneDayButton.setMaximumSize(new Dimension(50, 21));
    forwardOneDayButton.setMinimumSize(new Dimension(50, 21));
    forwardOneDayButton.setPreferredSize(new Dimension(50, 21));
    forwardOneDayButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneDayButton_actionPerformed(e);
        }
      });
    forwardOneWeekButton.setText("W >");
    forwardOneWeekButton.setToolTipText("Forward 1 Week");
    forwardOneWeekButton.setMargin(new Insets(1, 1, 1, 1));
    forwardOneWeekButton.setMaximumSize(new Dimension(50, 21));
    forwardOneWeekButton.setMinimumSize(new Dimension(50, 21));
    forwardOneWeekButton.setPreferredSize(new Dimension(50, 21));
    forwardOneWeekButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneWeekButton_actionPerformed(e);
        }
      });
    forwardOneMonthButton.setText("M >");
    forwardOneMonthButton.setToolTipText("Forward 1 Month");
    forwardOneMonthButton.setMargin(new Insets(1, 1, 1, 1));
    forwardOneMonthButton.setMaximumSize(new Dimension(50, 21));
    forwardOneMonthButton.setMinimumSize(new Dimension(50, 21));
    forwardOneMonthButton.setPreferredSize(new Dimension(50, 21));
    forwardOneMonthButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneMonthButton_actionPerformed(e);
        }
      });
    forwardOneYearButton.setText("Y >");
    forwardOneYearButton.setToolTipText("Forward 1 Year");
    forwardOneYearButton.setMargin(new Insets(1, 1, 1, 1));
    forwardOneYearButton.setMaximumSize(new Dimension(50, 21));
    forwardOneYearButton.setMinimumSize(new Dimension(50, 21));
    forwardOneYearButton.setPreferredSize(new Dimension(50, 21));
    forwardOneYearButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneYearButton_actionPerformed(e);
        }
      });
    nowButton.setText("Now");
    nowButton.setMargin(new Insets(1, 1, 1, 1));
    nowButton.setMaximumSize(new Dimension(50, 21));
    nowButton.setMinimumSize(new Dimension(50, 21));
    nowButton.setPreferredSize(new Dimension(50, 21));
    nowButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          nowButton_actionPerformed(e);
        }
      });
    oneLunarMonthRadioButton.setText("Lunar Month");
    oneLunarMonthRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          oneLunarMonthRadioButton_actionPerformed(e);
        }
      });
    oneDayRadioButton.setText("Day");
    oneDayRadioButton.setSelected(true);
    Thread refreshThread = new Thread()
      {
        public void run()
        {
          while (true)
          {
            if (oneDayRadioButton.isSelected())
              graphPanel.repaint();
            String title = FRAME_TITLE + " - System Date:" + LOCAL_DATE_FORMAT.format(now.getTime()) + ", UTC:" + UTC_DATE_FORMAT.format(now.getTime());
            setTitle(title);
            now.add(Calendar.SECOND, 1);
            try { Thread.sleep(1000L); } catch (Exception ex) { ex.printStackTrace(); } 
          }
        }
      };
    refreshThread.start();
  }

  public static void renderTree(TreeMap<String, TideUtilities.StationTreeNode> tree, DefaultMutableTreeNode dmtn)
  {
    Set<String> keys = tree.keySet();
    for (String key : keys)
    {
      TideUtilities.StationTreeNode stn = tree.get(key);      
      DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(stn, true);
      dmtn.add(subNode); 
      if (stn.getSubTree().size() > 0)
        renderTree(stn.getSubTree(), subNode);
    }
  }

  private void displayTide(final String stationName)
  {
    Thread tideThread = new Thread()
      {
        public void run()
        {
          tideStationName = stationName;
          try 
          { 
            ts = BackEndTideComputer.findTideStation(tideStationName, now.get(Calendar.YEAR));
            if (ts != null)
            {
              menuFilePrint.setEnabled(true);
              menuFileSearch.setEnabled(true);
              menuFileGoogle.setEnabled(true);
              String stationUnit = ts.getDisplayUnit();
              unitComboBox.removeAllItems();
              if (ts.isTideStation())
              {
                unitComboBox.addItem(TideStation.METERS);
                unitComboBox.addItem(TideStation.FEET);
              }
              else
                unitComboBox.addItem(TideStation.KNOTS);
              unitComboBox.setSelectedItem(stationUnit);            
              currentUnit = stationUnit;
              timeZone2Use = ts.getTimeZone();
              
              tzComboBox.setSelectedItem(ts.getTimeZone());
              stationTimeZoneLabel.setText(ts.getTimeZone());
            }
            else
            {
              menuFilePrint.setEnabled(false);
              menuFileSearch.setEnabled(false);
            }
            graphPanel.repaint();
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      };
    tideThread.start();    
  }
  
  private void drawMoon(Graphics2D g2d, int xCenter, int yCenter, int radius, double phase, String label)
  {
    Color bg = Color.BLACK;
    Color fg = Color.YELLOW;
    
    Color origColor = g2d.getColor();
    g2d.setColor(bg);
    g2d.fillOval(xCenter - radius, yCenter - radius, 2 * radius, 2 * radius);
    
    switch ((int)Math.round(phase))
    {
      case 0:
      case 360: // All black, done already
        break;
      case 90: // First Quarter
        g2d.setColor(fg);
        g2d.fillArc(xCenter - radius, yCenter - radius, 2 * radius, 2 * radius, -90, 180);
        break;
      case 180: // Full Moon
        g2d.setColor(fg);
        g2d.fillOval(xCenter - radius, yCenter - radius, 2 * radius, 2 * radius);
        break;
      case 270: // Last Quarter
        g2d.setColor(fg);
        g2d.fillArc(xCenter - radius, yCenter - radius, 2 * radius, 2 * radius, -90, -180);
        break;
      default:
        break;
    }
    
    g2d.setColor(Color.BLUE);
    Font f = g2d.getFont();
    Font f2 = f.deriveFont(Font.BOLD, f.getSize()); // new Font("Arial", Font.BOLD, 10);
    g2d.setFont(f2);
    int l = g2d.getFontMetrics(f).stringWidth(label);
    g2d.drawString(label, xCenter - (l/2), yCenter + (f.getSize() / 2));
    g2d.setFont(f);
    g2d.setColor(origColor);
  }
  
  private void fileExit_ActionPerformed(ActionEvent e)
  {
    this_internalFrameClosed(null);
    this.dispose();
  }

  private void filePrint_ActionPerformed(ActionEvent e)
  {    
    PrintDialog printDialog = new PrintDialog(this.ts);
    int resp = JOptionPane.showConfirmDialog(this, printDialog, "Print", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      System.out.println("Print Time Zone:" + printDialog.getTimeZone()); 
      final String tz = printDialog.getTimeZone();
      final int sm = printDialog.getStartMonth();
      final int sy = printDialog.getStartYear();
      final int nb = printDialog.getNb();
      final int q = printDialog.getQuantity();
      final String utu = printDialog.getUnitToUse();
      System.out.println("Starting month:" + sm + ", year:" + sy);
      System.out.println("For " + nb + " " + (q==Calendar.MONTH?"month(s)":"year(s)"));
      
      Thread printThread = new Thread()
        {
          public void run()
          {
            GregorianCalendar start = new GregorianCalendar(sy, sm, 1);
            GregorianCalendar end = (GregorianCalendar)start.clone();
            end.add(q, nb);
            boolean loop = true;
            PrintStream out = System.out;
            String radical = "";
            try
            {
              File tempFile = File.createTempFile("tide.data.", ".xml");
              out = new PrintStream(new FileOutputStream(tempFile));
              radical = tempFile.getAbsolutePath();
              radical = radical.substring(0, radical.lastIndexOf(".xml"));
//            System.out.println("Writing data in " + tempFile.getAbsolutePath());
            }
            catch (Exception ex)
            {
              JOptionPane.showMessageDialog(null, ex.toString(), "Creating temp file", JOptionPane.ERROR_MESSAGE);
              ex.printStackTrace();
              return;
            }
            
            out.println("<tide station='" + ts.getFullName() + "' station-time-zone='" + ts.getTimeZone() + "' print-time-zone='" + tz + "'>");                        
            while (loop)
            {
              if (start.equals(end))
                loop = false;
              else
              {
                out.println("  <period month='" + (start.get(Calendar.MONTH) + 1) + "' year='" + start.get(Calendar.YEAR) + "'>");
                try
                {
                  System.out.println("Calculating tide for " + start.getTime().toString());
                  TideForOneMonth.tideForOneMonth(out,
                                                  tz,
                                                  start.get(Calendar.YEAR), 
                                                  start.get(Calendar.MONTH) + 1, // Base: 1 
                                                  ts.getFullName(), 
                                                  utu,
                                                  constSpeed,
                                                  TideForOneMonth.XML_FLAVOR);
                  start.add(Calendar.MONTH, 1);
                }
                catch (Exception ex)
                {
                  ex.printStackTrace();
                }
                out.println("  </period>");
              }
            }
            out.println("</tide>");            
            out.close();
            System.out.println("Generation completed.");
            // Ready for transformation
            try
            {
              String cmd = "cmd /k start pub" + File.separator + "publishtide.bat \"" + radical + "\"";
              if (System.getProperty("os.name").indexOf("Linux") > -1)
                cmd = "." + File.separator + "pub" + File.separator + "publishtide " + radical;
              System.out.println("Command:" + cmd);
              Process p = Runtime.getRuntime().exec(cmd);
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }            
          }
        };
      printThread.start();
    }
  }
  
  private void fileSearch_ActionPerformed(ActionEvent e)
  {    
    if (searchDialog == null)
      searchDialog = new SearchPanel(this.ts);
    else
      searchDialog.setStation(this.ts);
    int resp = JOptionPane.showConfirmDialog(this, searchDialog, "Search", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      
      final int tideType = searchDialog.getHighLow();
      final int fromHour = searchDialog.getFromHour();
      final int toHour   = searchDialog.getToHour();
      final GregorianCalendar fromDate = searchDialog.getFromDate();
      final GregorianCalendar toDate   = searchDialog.getToDate();
      Thread searchThread = new Thread()
        {
          public void run()
          {
            String result = 
            "<html><head><style type='text/css'>" +
//          "body { background : #efefef; color : #008000; font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "body { background : #efefef; color : #000000; font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "h1 { color: white; font-style: italic; font-size: 14pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular; background-color: black; padding-left: 5pt }\n" + 
            "h2 { font-size: 12pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "h3 { font-style: italic; font-weight: bold; font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular; bold:  }\n" + 
            "li { font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "p { font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "td { font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "small { font-size: 8pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "blockquote{ font-style: italic; font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }-->\n" + 
            "em { font-size: 10pt; font-style: italic; font-weight: bold; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "pre { font-size: 9pt; font-family: Courier New, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "address { font-size: 8pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
            "a:link { color : #000000} \n" + 
            "a:active { color: #000000} \n" + 
            "a:visited { color : #000000}\n" +
            "</style></head><body>\n";
            result += "<h3>\n";
            result += ("Days when tide is <b>" + ((tideType == SearchPanel.HIGH_TIDE)?"high":"low") + "</b> at " + ts.getFullName() + "<br>\n");
            result += ("between " + DF2.format(fromHour) + ":00 and " + DF2.format(toHour) + ":00<br>\n");
            result += ("(dates between " + JUST_DATE_FORMAT.format(fromDate.getTime()) + " and " + JUST_DATE_FORMAT.format(toDate.getTime()) + ")\n");
            result += "</h3>\n";
            result += "<hr>\n";
            result += "<ul>\n";
            
            Calendar currDate = fromDate;
            int nbDays = 0;
            while (!currDate.after(toDate))
            {
//            System.out.println("Calculating for " + SUITABLE_DATE_FORMAT.format(currDate.getTime()) + " ...");
              double low1  = Double.NaN;
              double low2  = Double.NaN;
              double high1 = Double.NaN;
              double high2 = Double.NaN;
              Calendar low1Cal = null;
              Calendar low2Cal = null;
              Calendar high1Cal = null;
              Calendar high2Cal = null;
              int trend = 0;
              double previousWH = Double.NaN;
              
              for (int h=0; h<24; h++)
              {
                for (int m=0; m<60; m++)
                {
                  Calendar cal = new GregorianCalendar(currDate.get(Calendar.YEAR),
                                                       currDate.get(Calendar.MONTH),
                                                       currDate.get(Calendar.DAY_OF_MONTH),
                                                       h, m);
                  cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                  double wh = 0d;
                  try { wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, cal), ts.getDisplayUnit(), currentUnit); } catch (Exception ex) { ex.printStackTrace(); } 
                  if (!Double.isNaN(previousWH))
                  {
                    if (trend == 0)
                    {
                      if (previousWH > wh)
                        trend = TidePanel.FALLING;
                      else if (previousWH < wh)
                        trend = TidePanel.RISING;
                    }
                    else
                    {
                      switch (trend)
                      {
                        case TidePanel.RISING:
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
                            trend = TidePanel.FALLING; // Now falling
                          }
                          break;
                        case TidePanel.FALLING:
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
                            trend = TidePanel.RISING; // Now rising
                          }
                          break;
                      }
                    }
                  }
                  previousWH = wh;
                }
              }
              ArrayList<TimedValue> timeAL = new ArrayList<TimedValue>(4); 
              if (low1Cal != null)
                timeAL.add(new TimedValue("LW", low1Cal, low1));
              if (low2Cal != null)
                timeAL.add(new TimedValue("LW", low2Cal, low2));
              if (high1Cal != null)
                timeAL.add(new TimedValue("HW", high1Cal, high1));
              if (high2Cal != null)
                timeAL.add(new TimedValue("HW", high2Cal, high2));
              
              Collections.sort(timeAL);
              // Suitable?
              for (TimedValue tv : timeAL)
              {
                if ((tv.getType().equals("HW") && tideType == SearchPanel.HIGH_TIDE) ||
                    (tv.getType().equals("LW") && tideType == SearchPanel.LOW_TIDE))
                {
                  double tideHour = tv.getCalendar().get(Calendar.HOUR_OF_DAY) + (tv.getCalendar().get(Calendar.MINUTE) / 60d);
//                System.out.println("Checking " + SUITABLE_DATE_FORMAT.format(tv.getCalendar().getTime()));
//                System.out.print("  Is " + tideHour + " between " + fromHour + " and " + toHour + " ?");
                  if (tideHour >= fromHour && tideHour <= toHour)
                  {
                    result += ("<li type='disc'><a href='showDate(" + Long.toString(tv.getCalendar().getTime().getTime()) + ")'>" + SUITABLE_DATE_FORMAT.format(tv.getCalendar().getTime()) + "</a></li>\n");
                    nbDays++;
//                  System.out.println(" ... yes");
                  }
//                else
//                  System.out.println(" ... no");
                }
              }              
              currDate.add(Calendar.DAY_OF_MONTH, 1);
//            System.out.println(".. Date is now " + SUITABLE_DATE_FORMAT.format(currDate.getTime()));
            }
//          System.out.println("Now displaying");
            // Display list
            result += ("</body></html>");
            // Produce clickable list here
            JPanel finalList = new JPanel();
            finalList.setPreferredSize(new Dimension(500, 300));
            JEditorPane jEditorPane = new JEditorPane();
            JScrollPane jScrollPane = new JScrollPane();
            finalList.setLayout(new BorderLayout());
            jEditorPane.setEditable(false);
            jEditorPane.setFocusable(false);
            jEditorPane.setFont(new Font("Verdana", 0, 10));
            jEditorPane.setBackground(Color.lightGray);
            jScrollPane.getViewport().add(jEditorPane, null);
            jEditorPane.addHyperlinkListener(new HyperlinkListener()
              {
                public void hyperlinkUpdate(HyperlinkEvent he)
                {
                  try
                  {
                    if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                    {
            //            System.out.println("URL Activated:" + he.getURL().toString());
                      String activatedURL = he.getURL().toString();
                      String value = activatedURL.substring(activatedURL.lastIndexOf("/") + 1);
                      if (value.startsWith("showDate("))
                      {
                        System.out.println("Value:" + value);
                        value = value.substring("showDate(".length(), value.length() - 1);
                        System.out.println("arg:" + value);
                        long date = Long.parseLong(value);
                        // Broadcast
                        System.out.println("Broadcasting Date " + date + ":" + new Date(date).toString());
                        for (TideEventListener tel : TideContext.getInstance().getListeners())
                          tel.setDate(date);
                      }
                    }
                  }
                  catch (Exception ioe)
                  {
                    ioe.printStackTrace();
                  }
                }
              });
            try
            {
              File tempFile = File.createTempFile("data", ".html");
              tempFile.deleteOnExit();
              BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
              bw.write(result);
              bw.flush();
              bw.close();              
              jEditorPane.setPage(tempFile.toURI().toURL());
              jEditorPane.repaint();
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
            finalList.add(jScrollPane, BorderLayout.CENTER);
            JLabel nbDayLabel = new JLabel();
            nbDayLabel.setText(Integer.toString(nbDays) + " day(s).");
            finalList.add(nbDayLabel, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(null, finalList, "Search completed", JOptionPane.PLAIN_MESSAGE);
          }
        };
      searchThread.start();
    }
  }
  
  private void fileGoogle_ActionPerformed(ActionEvent e)
  {
    // Display the tide for the day, in Google Map
    // Generate googletide.js
    try
    {
      BufferedWriter bw = new BufferedWriter(new FileWriter("googletide.js"));
      bw.write("var lat2plot = " + Double.toString(ts.getLatitude()) + ";\n");
      bw.write("var lng2plot = " + Double.toString(ts.getLongitude()) + ";\n");
      bw.write("var stationName = \"" + ts.getFullName() + "\";\n");
      bw.write("\n");
      ArrayList<TideForOneMonth.TimedValue> timeAL = TideForOneMonth.tideForOneDay(now, timeZone2Use, ts.getFullName(), constSpeed, currentUnit);      
      bw.write("var tidedata = new Array\n(\n");
      int nbl = 0;
      for (TideForOneMonth.TimedValue tv : timeAL)
      {
        if (nbl++ > 0)
          bw.write("  ,\n");
        bw.write("  {type:\"" + tv.getType() + "\",\n");
        bw.write("   time:\"" + TideForOneMonth.TF.format(tv.getCalendar().getTime()) + "\",\n");
        bw.write("   height:\"" + TideUtilities.DF22PLUS.format(tv.getValue()) + "\",\n");
        bw.write("   unit:\"" + currentUnit + "\"}\n");
      }
      bw.write(");\n");
      
      bw.close();
      
      coreutilities.Utilities.openInBrowser("googletide.html");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  private void helpAbout_ActionPerformed(ActionEvent e)
  {
    JOptionPane.showMessageDialog(this, new TideFrame_AboutBoxPanel1(), "About", JOptionPane.PLAIN_MESSAGE);
  }
  
  private TideStation findStation(String location, Calendar date) throws Exception
  {
    return BackEndTideComputer.findTideStation(location, date.get(Calendar.YEAR));    
  }

  private void backOneDayButton_actionPerformed(ActionEvent e)
  {
    int quantity = Calendar.DAY_OF_MONTH;
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.SHIFT_MASK) != 0)
      quantity = Calendar.HOUR_OF_DAY;
    now.add(quantity, -1);
    graphPanel.repaint();
  }
  private void backOneWeekButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.DAY_OF_MONTH, -7);
    graphPanel.repaint();
  }
  private void backOneMonthButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.MONTH, -1);
    graphPanel.repaint();
  }
  private void backOneYearButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.YEAR, -1);
    graphPanel.repaint();
  }

  private void forwardOneDayButton_actionPerformed(ActionEvent e)
  {
    int quantity = Calendar.DAY_OF_MONTH;
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.SHIFT_MASK) != 0)
      quantity = Calendar.HOUR_OF_DAY;
    now.add(quantity, 1);
    graphPanel.repaint();
  }
  private void forwardOneWeekButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.DAY_OF_MONTH, 7);
    graphPanel.repaint();
  }
  private void forwardOneMonthButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.MONTH, 1);
    graphPanel.repaint();
  }
  private void forwardOneYearButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.YEAR, 1);
    graphPanel.repaint();
  }

  private void nowButton_actionPerformed(ActionEvent e)
  {
    now = GregorianCalendar.getInstance();
    graphPanel.repaint();
  }

  private void oneLunarMonthRadioButton_actionPerformed(ActionEvent e)
  {
    if (oneLunarMonthRadioButton.isSelected())
      graphPanel.repaint();
  }

  private void this_internalFrameClosed(InternalFrameEvent e)
  {
    TideContext.getInstance().fireInternalFrameClosed();
  }

  private void findTimeZoneButton_actionPerformed(ActionEvent e)
  {
    TimeZoneTable tzt = new TimeZoneTable(TimeZone.getAvailableIDs());
    int resp = JOptionPane.showConfirmDialog(this, tzt, "Time Zone Filter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      timeZone2Use = tzt.getSelectedTimeZoneData();
      tzComboBox.setSelectedItem(tzt.getSelectedTimeZoneData());
    }
  }

  private static class TimedValue implements Comparable<TimedValue>
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

  class StationTreeCellRenderer extends DefaultTreeCellRenderer 
  {
    public StationTreeCellRenderer()
    {
      super();
    }
    public Component getTreeCellRendererComponent(JTree tree, 
                                                  Object value,
                                                  boolean sel,  
                                                  boolean expanded,
                                                  boolean leaf, 
                                                  int row,
                                                  boolean hasFocus)
    {
      super.getTreeCellRendererComponent(tree, 
                                         value, 
                                         sel, 
                                         expanded, 
                                         leaf, 
                                         row,
                                         hasFocus); 
      if (leaf && value instanceof DefaultMutableTreeNode)
      {
        Object obj = ((DefaultMutableTreeNode)value).getUserObject();
        if (obj instanceof TideUtilities.StationTreeNode)
        {
          TideUtilities.StationTreeNode stn = (TideUtilities.StationTreeNode)obj;
          if (stn.getStationType() == TideUtilities.StationTreeNode.TIDE_STATION)
          {
            setIcon(new ImageIcon(this.getClass().getResource("img/bullet_ball_glass_blue.png")));
            setToolTipText("Tides for " + stn.getFullStationName());
          }
          else
          {
            setIcon(new ImageIcon(this.getClass().getResource("img/bullet_ball_glass_red.png")));
            setToolTipText("Current for " + stn.getFullStationName());
          }
        }
    //  else
    //    System.out.println("Value is a " + value.getClass().getName());
      }
      else
      {
        if (value instanceof DefaultMutableTreeNode)
        {
          DefaultMutableTreeNode dtn = (DefaultMutableTreeNode)value;
          String ttt = value.toString();
          if (dtn.getParent() != null && dtn.getParent().getParent() != null && dtn.getParent().getParent().toString().equals(ROOT_NAME)) // Then level 1
            ttt  ="TZ of " + dtn.getParent().toString() + "/" + ttt;
          setToolTipText(ttt);
        }
      }
      return this;
    }
  }
  
    
  private class TidePanel extends JPanel implements MouseMotionListener
  {
    public final static int RISING  =  1;
    public final static int FALLING = -1;

    public TidePanel()
    {
      super();
      addMouseMotionListener(this);  
    }
    
    public void mouseDragged(MouseEvent e)
    {
    }

    public void mouseMoved(MouseEvent e)
    {
      System.out.println("Mouse Moved: x=" + e.getX() + ", y=" + e.getY());
      if (oneDayRadioButton.isSelected())
        this.setToolTipText("Mouse Moved: x=" + e.getX() + ", y=" + e.getY());
      else
        this.setToolTipText("One month");
    }
  }
}