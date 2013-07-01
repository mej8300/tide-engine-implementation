package tideengineimplementation.gui;

import astro.calc.GeoPoint;
import astro.calc.GreatCircle;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
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
import java.awt.font.TextAttribute;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.net.URL;

import java.sql.SQLException;

import java.text.AttributedString;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import nmea.server.ctx.NMEAContext;
import nmea.server.ctx.NMEADataCache;

import ocss.nmea.parser.GeoPos;

import tideengine.BackEndTideComputer;
import tideengine.Coefficient;
import tideengine.Harmonic;
import tideengine.TideStation;
import tideengine.TideUtilities;

import tideengineimplementation.charts.CommandPanel;

import tideengineimplementation.gui.ctx.TideContext;
import tideengineimplementation.gui.ctx.TideEventListener;
import tideengineimplementation.gui.dialog.ClosestStationPanel;
import tideengineimplementation.gui.dialog.FoundStationPanel;
import tideengineimplementation.gui.dialog.PrintDialog;
import tideengineimplementation.gui.dialog.SearchPanel;
import tideengineimplementation.gui.dialog.SpecialProgressBar;
import tideengineimplementation.gui.main.splash.SplashWindow;
import tideengineimplementation.gui.main.splash.Splasher;
import tideengineimplementation.gui.table.CoeffTable;
import tideengineimplementation.gui.table.FilterTable;
import tideengineimplementation.gui.table.TimeZoneTable;

import tideengineimplementation.print.TideForOneMonth;

import tideengineimplementation.utils.AstroComputer;
import tideengineimplementation.utils.Utils;

import user.util.GeomUtil;

public class TideInternalFrame
     extends JInternalFrame
{
  @SuppressWarnings("compatibility:-3292072210595257705")
  public final static long serialVersionUID = 1L;

  public final static String TIDE_INTERNAL_FRAME_PROP_FILE = "internal.tide.frame.properties";
  public final static String TOP_LEFT_X_PROP               = "top.left.x";
  public final static String TOP_LEFT_Y_PROP               = "top.left.y";
  public final static String WIDTH_PROP                    = "width";
  public final static String HEIGHT_PROP                   = "height";
  
  public final static String COMPUTER_TIME_ZONE = "Computer Time Zone";
  public final static String STATION_TIME_ZONE  = "Station Time Zone";
  public final static String SEPARATOR          = "------------------";
  
  private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm (z) Z ");
  private final static SimpleDateFormat UTC_TIME_FORMAT = new SimpleDateFormat("HH:mm ('UTC')");
  static { UTC_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("etc/UTC")); }
  private final static DecimalFormat DF2  = new DecimalFormat("00");
  private final static DecimalFormat DF22 = new DecimalFormat("#0.00");
  private final static DecimalFormat DF3  = new DecimalFormat("##0");
  private final static SimpleDateFormat SUN_RISE_SET_SDF = new SimpleDateFormat("E dd-MMM-yyyy HH:mm (z)");

  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d-MMM");
  private final static SimpleDateFormat FULL_DATE_FORMAT = new SimpleDateFormat("E dd-MMM-yyyy HH:mm:ss Z z");
  
  private final static SimpleDateFormat LOCAL_DATE_FORMAT = new SimpleDateFormat("E dd-MMM-yyyy HH:mm:ss Z z");
  public  final static SimpleDateFormat UTC_DATE_FORMAT = new SimpleDateFormat("E dd-MMM-yyyy HH:mm:ss Z");
  static
  {
    UTC_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
  }
  private final static SimpleDateFormat SOLAR_DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
  private final static SimpleDateFormat SUITABLE_DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy HH:mm (Z) z");
  private final static SimpleDateFormat JUST_DATE_FORMAT = new SimpleDateFormat("EEEE dd MMMM yyyy");
  private final static SimpleDateFormat JUST_DATE_FORMAT_SMALL = new SimpleDateFormat("E dd MMM yyyy z (Z)");
  private final static String FRAME_TITLE = "Tide Computer";
  
  private final static String SELECTED_STATIONS_PREFIX = "View Selected Stations ";

  private transient TideStation ts = null;  
  private transient List<Coefficient> constSpeed = null;

  private String currentUnit = TideStation.METERS;
  private String timeZone2Use = "";
  private SearchPanel searchDialog = null;

  private JMenuBar menuBar = new JMenuBar();
  private JMenu menuFile = new JMenu();
  private JMenu menuFileRecent = new JMenu();
  private JMenuItem menuClearHistory = new JMenuItem();
  private JMenuItem menuFileExit = new JMenuItem();
  private JMenuItem menuFilePrint = new JMenuItem();
  private JMenuItem menuFileSearch = new JMenuItem();
  private JMenuItem menuFileFindClosest = new JMenuItem();
  private JMenu menuFileGoogleMenu = new JMenu();
  private JMenuItem menuFileGoogleOneStation = new JMenuItem();
  private JMenuItem menuFileGoogleSelectedStation = new JMenuItem();
  private JMenu menuHelp = new JMenu();
  private JMenuItem menuHelpAbout = new JMenuItem();
  private BorderLayout mainBorderLayout = new BorderLayout();
  private JSplitPane mainSplitPane = new JSplitPane();
  private JScrollPane stationScrollPane = new JScrollPane();
  private JTree stationTree = new JTree();
  private final static String ROOT_NAME = "invisible root"; // De ch'val
  private JPanel rightPanel = new JPanel(new BorderLayout());
  private JSplitPane leftPanel = new JSplitPane();
  private FilterTable filterTable = new FilterTable(null);
  private Calendar now = GregorianCalendar.getInstance();
  
  private transient Image sunSymbol  = new ImageIcon(this.getClass().getResource("sun.png")).getImage();
  private transient Image moonSymbol = new ImageIcon(this.getClass().getResource("moon.png")).getImage();
  
  private transient ImageIcon nowImage       = new ImageIcon(this.getClass().getResource("img/now.png"));
  private transient ImageIcon calLeftImage1  = new ImageIcon(this.getClass().getResource("img/cal-left-1.png"));
  private transient ImageIcon calRightImage1 = new ImageIcon(this.getClass().getResource("img/cal-right-1.png"));
  private transient ImageIcon calLeftImage2  = new ImageIcon(this.getClass().getResource("img/cal-left-2.png"));
  private transient ImageIcon calRightImage2 = new ImageIcon(this.getClass().getResource("img/cal-right-2.png"));
  private transient ImageIcon calLeftImage3  = new ImageIcon(this.getClass().getResource("img/cal-left-3.png"));
  private transient ImageIcon calRightImage3 = new ImageIcon(this.getClass().getResource("img/cal-right-3.png"));
  private transient ImageIcon calLeftImage4  = new ImageIcon(this.getClass().getResource("img/cal-left-4.png"));
  private transient ImageIcon calRightImage4 = new ImageIcon(this.getClass().getResource("img/cal-right-4.png"));

  private int hourOffset = 0;
  private CoeffTable ct = null;
  private transient HashMap<ColoredCoeff, String> coeffData = null;
  private boolean showAllCurves = true;
  private boolean showTideCurve = true;
  private int defaultWidth = 3; // 3 Days
  
  private int currentYear = -1;
  
  private TidePanel graphPanelOneDay = new TidePanel() // Current Day, now.
  {
    @SuppressWarnings("compatibility:6183464550290531188")
    public final static long serialVersionUID = 1L;

    private boolean mouseIsIn = false;
    private String postit = "";
    private int mouseX = 0, mouseY = 0;
    private double mouseWh = 0;

    private double low1  = Double.NaN;
    private double low2  = Double.NaN;
    private double high1 = Double.NaN;
    private double high2 = Double.NaN;
    private Calendar low1Cal = null;
    private Calendar low2Cal = null;
    private Calendar high1Cal = null;
    private Calendar high2Cal = null;
    private transient List<TimedValue> slackList = null;
    private int trend = 0;

    @Override
    public void mouseMoved(MouseEvent e)
    {
      mouseDown(e);
    }
    
    private void mouseDown(MouseEvent e)
    {
      double timeWidth = 24D; // One day
      double widthRatio = (double)this.getWidth() / timeWidth;
      double h = (e.getX() / widthRatio) + hourOffset;
      double m = (h - (int)h) * 60;
      
      Calendar cal = new GregorianCalendar(now.get(Calendar.YEAR),
                                           now.get(Calendar.MONTH),
                                           now.get(Calendar.DAY_OF_MONTH),
                                           (int)h, (int)Math.round(m));
      cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      double wh = 0;
      if (ts != null)
      {
        try { wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, cal), ts.getDisplayUnit(), currentUnit); } 
        catch (Exception ex) 
        {
          System.err.println("MouseDown:" + ex.getLocalizedMessage());
          ex.printStackTrace();
        }
      }
      String thisTime    = TIME_FORMAT.format(cal.getTime());
      String thisUTCTime = UTC_TIME_FORMAT.format(cal.getTime());
      
      if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) // Left button pressed on the graph
      {
//      postit = DF2.format((int)(h % 24)) + ":" + DF2.format(m) + "\n" + TideUtilities.DF22PLUS.format(wh) + " " + currentUnit;
        postit = thisTime + "\n" +  thisUTCTime + "\n" + TideUtilities.DF22PLUS.format(wh) + " " + currentUnit;
        mouseX = e.getX();
        mouseY = e.getY() - 35;  // 35 = 3*10 + 5. 3 lines, 10:font size, 5: rab
        mouseWh = wh;
        repaint();
      }
      else
//      this.setToolTipText("<html>" + DF2.format((int)(h % 24)) + ":" + DF2.format(m) + "<br>" + TideUtilities.DF22PLUS.format(wh) + " " + currentUnit + "</html>");
        this.setToolTipText("<html>" + 
                              thisTime + "<br>" +  
                              thisUTCTime + "<br>" + 
                              TideUtilities.DF22PLUS.format(wh) + " " + currentUnit + 
//                            "<br>x:" + e.getX() + ", y:" + e.getY() + 
                            "</html>");
      
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
      if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
      {
        mouseIsIn = true;
        repaint();
      }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
      if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
      {
        mouseIsIn = true;
        mouseDown(e);
        repaint();
      }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
      if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0)
        mouseIsIn = true;
      if (mouseIsIn)
      {
        mouseMoved(e);
      }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
      mouseIsIn = false;
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
      mouseIsIn = false;
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
  //  if (!this.isVisible()) // Leave it on, for the astro data to be updated
  //    return;
      
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                       RenderingHints.VALUE_TEXT_ANTIALIAS_ON);      
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                       RenderingHints.VALUE_ANTIALIAS_ON);      
      
      GradientPaint nightGradient = new GradientPaint(0, this.getHeight(), Color.WHITE, 0, 0, Color.BLACK); // vertical, bottom up
      GradientPaint dayGradient   = new GradientPaint(0, this.getHeight(), Color.BLUE, 0, 0, Color.WHITE); // vertical, bottom up
      
      now.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      TIME_FORMAT.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      FULL_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
      super.paintComponent(g);
//    g.setFont(new Font("courier new", Font.PLAIN, 12));
      double moonPhase = -1D;
      Calendar sunRise = null;
      Calendar sunSet = null;
      
      if (tideStationName != null)
      {
        try
        {
          if (ts != null)
          {
            if (now.get(Calendar.YEAR) != currentYear)
            {
              ts = BackEndTideComputer.findTideStation(tideStationName, now.get(Calendar.YEAR));
              currentYear = now.get(Calendar.YEAR);
            }
            // Draw the graph.
            g.setColor(Color.WHITE);
            int height = this.getHeight();
            int width  = this.getWidth();
            g.fillRect(0, 0, width, height);
            // Get boundaries
            double[] mm = null;
            mm = TideUtilities.getMinMaxWH(ts, constSpeed, now);
//          System.out.println("At " + tideStationName + " in " + now.get(Calendar.YEAR) + ", min : " + BackEndXMLTideComputer.DF22.format(mm[BackEndXMLTideComputer.MIN_POS]) + " " + ts.getUnit() + ", max : " + BackEndXMLTideComputer.DF22.format(mm[BackEndXMLTideComputer.MAX_POS]) + " " + ts.getDisplayUnit());
            mm[TideUtilities.MIN_POS] = Utils.convert(mm[TideUtilities.MIN_POS], ts.getDisplayUnit(), currentUnit);
            mm[TideUtilities.MAX_POS] = Utils.convert(mm[TideUtilities.MAX_POS], ts.getDisplayUnit(), currentUnit);
            
            float gutter = 2f; // 2 Feet
            if ("meters".equals(currentUnit))
              gutter = (float)TideUtilities.feetToMeters(gutter);
            double timeWidth = 24D; // One day
            double widthRatio = (double)width / timeWidth;
            double heightRatio = (double)height / ((2 * gutter) + mm[TideUtilities.MAX_POS] - mm[TideUtilities.MIN_POS]);
            double bottomValue = mm[TideUtilities.MIN_POS] - gutter;
            double heightRatioAlt = (double)height / 180d;
              
//          System.out.println("heightRatio:" + heightRatio + ", gutter:" + gutter + ", bottomValue:" + bottomValue + ", heightRatioAlt:" + heightRatioAlt);  
            // Horizontal grid
            g.setColor(Color.LIGHT_GRAY);
            for (int hgt=(int)Math.floor(mm[TideUtilities.MIN_POS]); hgt<=(int)Math.floor(mm[TideUtilities.MAX_POS]); hgt++)
            {
              g.drawLine(0, height - (int)((hgt - bottomValue) * heightRatio), width, height - (int)((hgt - bottomValue) * heightRatio));
              Color c = g.getColor();
              g.setColor(Color.BLACK);              
              g.drawString((hgt==0?"0":TideUtilities.DF2PLUS.format(hgt)) /* + " " + unitComboBox.getSelectedItem().toString() */, 
                           5, 
                           height - (int)((hgt - bottomValue) * heightRatio) - 2);
              g.setColor(c);
            }
            // Vertical grid
            FontMetrics fm = g.getFontMetrics();
            for (int hour=(2 + hourOffset); hour<(24 + hourOffset); hour+=2)  
            {
              int _x = (int)((hour - hourOffset) * widthRatio);
              g.drawLine(_x, 0, _x, height);
              int _h = hour;
              while (_h < 0) _h += 24;
              String s = DF2.format(_h % 24);
              Color c = g.getColor();
              g.setColor(Color.BLACK);
              g.drawString(s, _x - (fm.stringWidth(s) / 2), height - 14);
              g.setColor(c);
            }
            
            double[] rsSun  = null;
            double[] est = null;
            double moonIllum = 0d;
            // Current time
            int tx = (int)((now.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(now.get(Calendar.MINUTE) / 60D) + ((double)(now.get(Calendar.SECOND) / 3600D))) * widthRatio);
            Stroke origStroke = ((Graphics2D)g).getStroke();
            ((Graphics2D) g).setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
            g.setColor(Color.GREEN);
            g.drawLine(tx, 0, tx, height);
            ((Graphics2D) g).setStroke(origStroke);
            
            Calendar utcCal = (Calendar)now.clone();
            utcCal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            utcCal.getTime();
         // System.out.println("UTC Date:" + utcCal.getTime());
            moonPhase = AstroComputer.getMoonPhase(utcCal.get(Calendar.YEAR), 
                                                   utcCal.get(Calendar.MONTH) + 1, 
                                                   utcCal.get(Calendar.DAY_OF_MONTH), 
                                                   utcCal.get(Calendar.HOUR_OF_DAY), // 12 - (int)Math.round(AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(ts.getTimeZone()))), 
                                                   utcCal.get(Calendar.MINUTE), 
                                                   utcCal.get(Calendar.SECOND));
//          rsSun  = AstroComputer.sunRiseAndSet_wikipedia(ts.getLatitude());
            rsSun  = AstroComputer.sunRiseAndSet(ts.getLatitude(), ts.getLongitude());
            moonIllum = AstroComputer.getMoonIllum();
//          System.out.println("display.sun.moon.data = " + System.getProperty("display.sun.moon.data"));
            if ("true".equals(System.getProperty("display.sun.moon.data")))
            {
              est = AstroComputer.getSunMoon(utcCal.get(Calendar.YEAR), 
                                             utcCal.get(Calendar.MONTH) + 1, 
                                             utcCal.get(Calendar.DAY_OF_MONTH), 
                                             utcCal.get(Calendar.HOUR_OF_DAY), 
                                             utcCal.get(Calendar.MINUTE), 
                                             utcCal.get(Calendar.SECOND), 
                                             ts.getLatitude(), 
                                             ts.getLongitude());
            }            
            sunRise = new GregorianCalendar();
            sunRise.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
            sunRise.set(Calendar.YEAR, now.get(Calendar.YEAR));
            sunRise.set(Calendar.MONTH, now.get(Calendar.MONTH));
            sunRise.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
            sunRise.set(Calendar.SECOND, 0);
                          
            double r = rsSun[AstroComputer.UTC_RISE_IDX] /* + Utils.daylightOffset(sunRise) */ + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use /*ts.getTimeZone()*/), sunRise.getTime());
            int min = (int)((r - ((int)r)) * 60);
            sunRise.set(Calendar.MINUTE, min);
            sunRise.set(Calendar.HOUR_OF_DAY, (int)r);
            
            sunSet = new GregorianCalendar();
            sunSet.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
            sunSet.set(Calendar.YEAR, now.get(Calendar.YEAR));
            sunSet.set(Calendar.MONTH, now.get(Calendar.MONTH));
            sunSet.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
            sunSet.set(Calendar.SECOND, 0);
            r = rsSun[AstroComputer.UTC_SET_IDX] /* + Utils.daylightOffset(sunSet) */ + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use/*ts.getTimeZone()*/), sunSet.getTime());
            min = (int)((r - ((int)r)) * 60);
            sunSet.set(Calendar.MINUTE, min);
            sunSet.set(Calendar.HOUR_OF_DAY, (int)r);
            
            SUN_RISE_SET_SDF.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
//          System.out.println("Sun Rise:" + SUN_RISE_SET_SDF.format(sunRise.getTime()) + ", Set:" + SUN_RISE_SET_SDF.format(sunSet.getTime()));

            // Paint background for Sun
            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setColor(Color.LIGHT_GRAY);
            
            sunRise.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
            sunSet.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
            
            SUN_RISE_SET_SDF.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
//          System.out.println("Sun Rise:" + SUN_RISE_SET_SDF.format(sunRise.getTime()) + ", Set:" + SUN_RISE_SET_SDF.format(sunSet.getTime()));

            if (sunRise.get(Calendar.DAY_OF_MONTH) != sunSet.get(Calendar.DAY_OF_MONTH)) // TASK Not always right...
            {
              sunSet.set(Calendar.YEAR, sunRise.get(Calendar.YEAR));
              sunSet.set(Calendar.MONTH, sunRise.get(Calendar.MONTH));
              sunSet.set(Calendar.DAY_OF_MONTH, sunRise.get(Calendar.DAY_OF_MONTH));
            }
            
            if (sunRise.before(sunSet))
            {
              int x1 = (int)((sunRise.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunRise.get(Calendar.MINUTE) / 60D)) * widthRatio);
              int x2 = (int)((sunSet.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunSet.get(Calendar.MINUTE) / 60D)) * widthRatio);

              Paint paint = ((Graphics2D)g).getPaint();
              if (x1 > 0)
              {
                ((Graphics2D)g).setPaint(nightGradient);
                g.fillRect(0, 0, x1, this.getHeight());
                ((Graphics2D)g).setPaint(paint);              
              }
              
              paint = ((Graphics2D)g).getPaint();
              ((Graphics2D)g).setPaint(dayGradient);
              g.fillRect(x1, 0, x2 - x1, this.getHeight());
              ((Graphics2D)g).setPaint(paint);              
              
              if (x2 < this.getWidth())
              {
                paint = ((Graphics2D)g).getPaint();
                ((Graphics2D)g).setPaint(nightGradient);
                g.fillRect(x2, 0, this.getWidth() - x2, this.getHeight());
                ((Graphics2D)g).setPaint(paint);              
              }
            }
            if (sunSet.before(sunRise))
            {
              int x1 = (int)((sunSet.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunSet.get(Calendar.MINUTE) / 60D)) * widthRatio);
              int x2 = (int)((sunRise.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunRise.get(Calendar.MINUTE) / 60D)) * widthRatio);
              Paint paint = ((Graphics2D)g).getPaint();
              ((Graphics2D)g).setPaint(nightGradient);              
              g.fillRect(x1, 0, x2 - x1, this.getHeight());
              ((Graphics2D)g).setPaint(paint);              
            }
            ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            Point previous = null;
            // Draw here            
//          double previousWH = Double.NaN;
            Calendar reference = (Calendar)now.clone();
            
            showTideCurveCB.setVisible(decomposeCheckBox.isSelected());
            
            // Decompose
            if (decomposeCheckBox.isSelected())              
            {             
              showTideCurveCB.setBounds(this.getWidth() - 130, this.getHeight() - 25, 130, 25);
              if (coeffColor == null && ts != null) // Tide Station has changed
              {
                buildCoeffColor();
              }
              if (harmonicCurves == null)
              {
                setHarmonicsReady(false);
                final TidePanel instance = this;
                final double _bottomValue = bottomValue;
                final Calendar _reference = (Calendar)now.clone();
                Thread harmonicThread = new Thread()
                  {
                    public void run()
                    {
                      long before = System.currentTimeMillis();
                      instance.setHarmonicsReady(false);
                      harmonicCurves = new Hashtable<String, List<DataPoint>>();              
                      int k = lookBusy("Computing Harmonic curves");
                      for (int j=0; j<coeffColor.length; j++)
                      {
                        // Calculate one full curve
                        List<DataPoint> lp = harmonicCurves.get(coeffColor[j].name);
                        if (lp == null) 
                        {             
                          System.out.println("Calculating curve for [" + coeffColor[j].name + "]");
                          updateBusyLook("Calculating [" + coeffColor[j].name + "]", k);
                          lp = new ArrayList<DataPoint>();
                          for (int h=0; h<24; h++) 
                          {
                            for (int m=0; m<60; m++)
                            {
                              Calendar cal = new GregorianCalendar(_reference.get(Calendar.YEAR),
                                                                   _reference.get(Calendar.MONTH),
                                                                   _reference.get(Calendar.DAY_OF_MONTH),
                                                                   h + hourOffset, m);
                              cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                              int year = cal.get(Calendar.YEAR); 
                              // Calc Jan 1st of the current year
                              Date jan1st = new GregorianCalendar(year, 0, 1).getTime();
                      //      double value = Utils.convert(TideUtilities.getHarmonicValue(cal.getTime(), jan1st, ts, constSpeed, i), ts.getDisplayUnit(), currentUnit);
                              double value = Utils.convert(TideUtilities.getHarmonicValue(cal.getTime(), jan1st, ts, constSpeed, coeffColor[j].name), ts.getDisplayUnit(), currentUnit);
                              double x = (h + (double)(m / 60D));
                              double y = (value - _bottomValue);
                              lp.add(new DataPoint(x, y));
                            }
                          }
                          harmonicCurves.put(coeffColor[j].name, lp);
                        }
                      }     
                      long after = System.currentTimeMillis();
                      coolDown(k);
                      System.out.println("1 - Harmonic computation completed in " + Long.toString(after - before) + " ms");
                      instance.setHarmonicsReady(true);
                      instance.repaint();
                    }
                  };
                harmonicThread.start();
              }
              if (isHarmonicsReady())
              {
                for (int j=0; j<coeffColor.length; j++)
                {
                  origStroke = null;
                  if (coeffToHighlight != null)
                  {
                //  System.out.println("Displaying " + coeffColor[j].name + "?");
                    if (coeffToHighlight.contains(coeffColor[j].name))
                    {
                      // Thicker
                //    System.out.println("Highlighting " + i + " (" + coeffColor[i].name + ")");
                      origStroke = ((Graphics2D)g).getStroke();
                      ((Graphics2D) g).setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                    }
                    else if (!showAllCurves)
                      continue;
                  }
                  List<DataPoint> lp = harmonicCurves.get(coeffColor[j].name);                        
                  // Draw the curve
                  g.setColor(coeffColor[j].color);
                  Point previousVal = null;
                  for (DataPoint p : lp)
                  {
                    int x = (int)(p.getX() * widthRatio);
                    int y = height - (int)(p.getY() * heightRatio);
                    if (previousVal != null)
                      g.drawLine(previousVal.x, previousVal.y, x, y);
                    previousVal = new Point(x, y);                                              
                  }
                  // Reset thickness
                  if (origStroke != null)
                    ((Graphics2D) g).setStroke(origStroke);
                }
              }
            }  
            if (showAltitudesCheckBox.isSelected())
            {
              if (sunAltitudes == null || moonAltitudes == null)
              {
                setAstroReady(false);
                final TidePanel instance = this;
                final Calendar _reference = reference;
                Thread astroThread = new Thread()
                  {
                    public void run()
                    {
                      long before = System.currentTimeMillis();
                      instance.setAstroReady(false);
                      System.out.println("Calculating Sun and Moon altitudes");
                      int k = lookBusy("Calculating Sun and Moon altitudes");
                      sunAltitudes  = new ArrayList<DataPoint>();
                      moonAltitudes = new ArrayList<DataPoint>();
                      for (int h=0; h<24; h++)
                      {
                        for (int m=0; m<60; m+=5)
                        {
                          Calendar cal = new GregorianCalendar(_reference.get(Calendar.YEAR),
                                                               _reference.get(Calendar.MONTH),
                                                               _reference.get(Calendar.DAY_OF_MONTH),
                                                               h + hourOffset, 
                                                               m);
                          cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                      /*  Date date = */ cal.getTime(); // To apply the new Time Zone... Does not happen otherwise. :o( 
                          cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                      //  date = cal.getTime(); 
                          // Sun & Moon altitudes
                          double[] values = AstroComputer.getSunMoonAltDecl(cal.get(Calendar.YEAR), 
                                                                            cal.get(Calendar.MONTH) + 1, 
                                                                            cal.get(Calendar.DAY_OF_MONTH), 
                                                                            cal.get(Calendar.HOUR_OF_DAY), 
                                                                            cal.get(Calendar.MINUTE), 
                                                                            0, 
                                                                            ts.getLatitude(), 
                                                                            ts.getLongitude());
                          double value = values[AstroComputer.HE_SUN_IDX];  // Sun                  
                          double x = (h + (double)(m / 60D));
                          double y = value;
                          try
                          {
                            synchronized (sunAltitudes) { sunAltitudes.add(new DataPoint(x, y)); }
                          }
                          catch (NullPointerException npe)
                          {
                            System.err.println("sunAltitudes is null. Wierd.");
                            npe.printStackTrace();
                          }
                          
                          value = values[AstroComputer.HE_MOON_IDX]; // Moon
                          y = value;
                          try
                          {
                            synchronized (moonAltitudes) { moonAltitudes.add(new DataPoint(x, y)); }
                          }
                          catch (NullPointerException npe)
                          {
                            System.err.println("moonAltitudes is null. Wierd.");
                            npe.printStackTrace();
                          }
                        }
                      }
                      long after = System.currentTimeMillis();
                      System.out.println("1 - Sun & Moon Calculation completed in " + Long.toString(after - before) + " ms");
                      coolDown(k);
                      instance.setAstroReady(true);
                      instance.repaint();
                    }                    
                  };
                astroThread.start();                
              }
              if (isAstroReady())
              {
                // Horizon
                g.setColor(Color.GRAY);
                double bh = ts.getBaseHeight();
                bh = Utils.convert(bh, ts.getDisplayUnit(), currentUnit);
                int y = this.getHeight() / 2;
                g.drawLine(0, y, this.getWidth(), y);       
                GradientPaint gradient = new GradientPaint(0, this.getHeight(), Color.WHITE, 0, 0, Color.BLUE); // vertical, upside down
                Paint paint = ((Graphics2D)g).getPaint();
                ((Graphics2D)g).setPaint(gradient);              
                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g.fillPolygon(new Polygon(new int[] {0, this.getWidth(), this.getWidth(), 0},
                                          new int[] {y, y, this.getHeight(), this.getHeight()},
                                          4));
                ((Graphics2D)g).setPaint(paint);              
                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                
                // Sun
                g.setColor(Color.DARK_GRAY);
                Point previousPt = null;
                for (DataPoint dp : sunAltitudes)
                {
                  Point p = new Point((int)((dp.getX()) * widthRatio),
                                      (this.getHeight() / 2) - (int)(dp.getY() * heightRatioAlt));
                  if (previousPt != null)
                    g.drawLine(previousPt.x, previousPt.y, p.x, p.y);
                  previousPt = p;
                }
                // Moon
                g.setColor(Color.BLACK);
                previousPt = null;
                for (DataPoint dp : moonAltitudes)
                {
                  Point p = new Point((int)((dp.getX()) * widthRatio),
                                      (this.getHeight() / 2) - (int)(dp.getY() * heightRatioAlt));
                  if (previousPt != null)
                    g.drawLine(previousPt.x, previousPt.y, p.x, p.y);
                  previousPt = p;
                }
              }
              else
                System.out.println("Please wait, astro data are being computed");
            }
            
            origStroke = ((Graphics2D)g).getStroke();
            Stroke mainCurveStroke = null;
            mainCurveStroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
            ((Graphics2D) g).setStroke(mainCurveStroke);

            if (showBaseHeightCheckBox.isSelected())
            {
              g.setColor(Color.BLUE);
              double bh = ts.getBaseHeight();
              if (ts.isCurrentStation())
                bh = 0;
              bh = Utils.convert(bh, ts.getDisplayUnit(), currentUnit);
              int y = height - (int)((bh - bottomValue) * heightRatio);
              g.drawLine(0, y, this.getWidth(), y);
            }
            
            if (showTideCurve)
            {
              if (!decomposeCheckBox.isSelected() || (decomposeCheckBox.isSelected() && showTideCurveCB.isSelected()))
              {
                if (mainCurve == null)
                {
                  final TidePanel instance = this;
                  final double _bottomValue = bottomValue;
                  setMainCurveReady(false);
//                mainCurve = new ArrayList<DataPoint>();                
                  Thread mainCurveThread = new Thread()
                    {
                      public void run()
                      {                        
                        long before = System.currentTimeMillis();
                        mainCurve = new ArrayList<DataPoint>();                
                        instance.setMainCurveReady(false);
                        int k = lookBusy("Calculating main curve");
                        double previousWH = Double.NaN;
                        
                        Calendar reference = (Calendar)now.clone();
                        
//                        System.out.println("1 - Calculating Main Curve, ReferenceTZ:" + 
//                                           reference.getTimeZone().getID() + " " +
//                                           FULL_DATE_FORMAT.format(reference.getTime()));

                        low1  = Double.NaN;
                        low2  = Double.NaN;
                        high1 = Double.NaN;
                        high2 = Double.NaN;
                        low1Cal = null;
                        low2Cal = null;
                        high1Cal = null;
                        high2Cal = null;
                        trend = 0;
                        
                        slackList = new ArrayList<TimedValue>();

                        double previousUTCOffset = Double.NaN;
                        
                        for (int h=0; h<24; h++)
                        {
                          for (int m=0; m<60; m++)
                          {
                            Calendar cal = new GregorianCalendar(reference.get(Calendar.YEAR),
                                                                 reference.get(Calendar.MONTH),
                                                                 reference.get(Calendar.DAY_OF_MONTH),
                                                                 h + hourOffset, m, 0);
                            cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                            double wh = 0;
                            try { wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, cal), ts.getDisplayUnit(), currentUnit); }
                            catch (Exception ex) { ex.printStackTrace(); }
//                            if ( m == 0)
//                              System.out.println("WH at " + h + ":" + m + " (" + timeZone2Use + ") =" + wh + " [" + TIME_FORMAT.format(cal.getTime()) + "]");
                            double offset = AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use), cal.getTime());
                            if (Double.isNaN(previousUTCOffset))
                              previousUTCOffset = offset;
                            if (offset != previousUTCOffset) // This is for when the time goes from DT to ST and vice versa.
                            {
                              System.out.println("TimeOffset change at " + cal.getTime() + ", offset was " + previousUTCOffset + ", now " + offset);
                              System.out.println("Previous WH:" + previousWH + ", WH:" + wh);
                              System.out.println("Trend:" + (trend==FALLING?"Falling":"Rising"));
                            }
                            if (!Double.isNaN(previousWH))
                            {
                              if (ts.isCurrentStation())
                              {
                                if ((previousWH > 0 && wh <= 0) || (previousWH < 0 && wh >= 0))
                                {
                                  slackList.add(new TimedValue("Slack", cal, 0d));
                                }
                              }
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
                                    if (previousWH > wh && offset == previousUTCOffset) // Now going down
                                    {
                                      Calendar prev = (Calendar)cal.clone();
                                      prev.add(Calendar.MINUTE, -1);                                      
                                      if (AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use), cal.getTime()) == 
                                          AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use), prev.getTime()))
                                      {
                                        if (Double.isNaN(high1))
                                        {                                          
                                          high1 = previousWH;
                                          cal.add(Calendar.MINUTE, -1);
                                          high1Cal = cal;
//                                        System.out.println("High Tide (1) detected [" + previousWH + "] at " + cal.getTime());
                                        }
                                        else
                                        {
                                          high2 = previousWH;
                                          cal.add(Calendar.MINUTE, -1);
                                          high2Cal = cal;
//                                        System.out.println("High Tide (2) detected [" + previousWH + "] at " + cal.getTime());
                                        }
                                      }
                                      trend = FALLING; // Now falling
                                    }
                                    break;
                                  case FALLING:
                                    if (previousWH < wh && offset == previousUTCOffset) // Now going up
                                    {
                                      Calendar prev = (Calendar)cal.clone();
                                      prev.add(Calendar.MINUTE, -1);                                      
                                      if (AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use), cal.getTime()) == 
                                          AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use), prev.getTime()))
                                      {
                                        if (Double.isNaN(low1))
                                        {
                                          low1 = previousWH;
                                          cal.add(Calendar.MINUTE, -1);
                                          low1Cal = cal;
//                                        System.out.println("Low Tide (1) detected [" + previousWH + "] at " + cal.getTime());
                                        }
                                        else
                                        {
                                          low2 = previousWH;
                                          cal.add(Calendar.MINUTE, -1);
                                          low2Cal = cal;
//                                        System.out.println("Low Tide (2) detected [" + previousWH + "] at " + cal.getTime());
                                        }
                                      }
                                      trend = RISING; // Now rising
                                    }
                                    break;
                                }
                              }
                              double x = (h + (double)(m / 60D));
                              double y = (wh - _bottomValue);
                              try { synchronized (mainCurve) { mainCurve.add(new DataPoint(x, y)); } }
                              catch (NullPointerException npe)
                              {
                                System.err.println("MainCurve is null, wierd. It is " + h + ":" + m);
                                npe.printStackTrace();
                              }
                            }
                            previousWH = wh;
                            previousUTCOffset = offset;
                          }
                        }
                        long after = System.currentTimeMillis();
                        System.out.println("1 - Main curve computation completed in " + Long.toString(after - before) + " ms");
                        coolDown(k);
                        instance.setMainCurveReady(true);
                        instance.repaint();
                      }
                    } ;
                  mainCurveThread.start();
                }
                // Draw the curve
                if (isMainCurveReady())
                {
                  g.setColor(Color.RED);            
                  Polygon curvePolygon = new Polygon();
                  for (DataPoint dp : mainCurve)
                  {
                    int x = (int)((dp.getX()) * widthRatio);
                    int y = height - (int)(dp.getY() * heightRatio);
                    if (previous != null)
                      g.drawLine(previous.x, previous.y, x, y);
                    previous = new Point(x, y);                      
                    curvePolygon.addPoint(x, y);
                  }
                  
                  ((Graphics2D) g).setStroke(origStroke);
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
              }
              int x = 5, y = 12;
              g.setColor(Color.BLUE);
              List<TimedValue> timeAL = new ArrayList<TimedValue>(4); 
              if (low1Cal != null)
                timeAL.add(new TimedValue("LW", low1Cal, low1));
              if (low2Cal != null)
                timeAL.add(new TimedValue("LW", low2Cal, low2));
              if (high1Cal != null)
                timeAL.add(new TimedValue("HW", high1Cal, high1));
              if (high2Cal != null)
                timeAL.add(new TimedValue("HW", high2Cal, high2));
              
              if (ts.isCurrentStation() && slackList != null && slackList.size() > 0)
              {
                for (TimedValue tv : slackList)
                  timeAL.add(tv);
              }
              
              Collections.sort(timeAL);
              // Station Name            
              int fontSize = 12;
  //          g.setFont(new Font("Arial", Font.PLAIN, fontSize));
              g.setFont(g.getFont().deriveFont(Font.PLAIN, fontSize));
              Font f = g.getFont();
              g.setFont(f.deriveFont(Font.BOLD, f.getSize()));
              Calendar solar = (Calendar)now.clone();
              solar.setTimeZone(TimeZone.getTimeZone("etc/UTC"));
              SOLAR_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("etc/UTC"));
              long time = solar.getTime().getTime();
              double offset = (ts.getLongitude() / 15d) * 3600d * 1000d; // in milliseconds
              time += offset;
              String snstr = ts.getFullName() + ", " + FULL_DATE_FORMAT.format(now.getTime()) + " (Solar " + SOLAR_DATE_FORMAT.format(new Date(time)) + ")";
              AttributedString aSnStr = new AttributedString(snstr);
              aSnStr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD, g.getFont().getSize()), 0, snstr.length());
              
              Pattern pattern = Pattern.compile(Utils.escapePattern(ts.getFullName()));
              Matcher matcher = pattern.matcher(snstr);
              if (matcher.find())
              {
                int start = matcher.start();
                int end   = matcher.end();
                aSnStr.addAttribute(TextAttribute.BACKGROUND, Color.YELLOW, start, end);                
                aSnStr.addAttribute(TextAttribute.FOREGROUND, Color.RED, start, end);                
              }              
              else // else weird...
              {
                System.out.println("[" + ts.getFullName() + "] not found in [" + snstr + "]...");
              }

              g.drawString(aSnStr.getIterator(), x, y);
              g.setFont(f);
              y += (fontSize + 2);
              
              // Station Position and base height
              g.drawString(new GeoPos(ts.getLatitude(), ts.getLongitude()).toString() + " - Base Height : " + DF22.format(Utils.convert(ts.getBaseHeight(), ts.getDisplayUnit(), currentUnit)) + " " + currentUnit, x, y);
              y += (fontSize + 2);
              // Sun rise & set
              SUN_RISE_SET_SDF.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
              long daylight = (sunSet.getTimeInMillis() - sunRise.getTimeInMillis()) / 1000L;
              if (!Double.isNaN(rsSun[AstroComputer.UTC_RISE_IDX]))
              {
                String srs = "Sun Rise :" + SUN_RISE_SET_SDF.format(sunRise.getTime()) + " Z:" + DF3.format(rsSun[AstroComputer.RISE_Z_IDX]) + ", Set:" + SUN_RISE_SET_SDF.format(sunSet.getTime()) + " Z:" + DF3.format(rsSun[AstroComputer.SET_Z_IDX]) + (daylight>0?(" - daylight:" + DF2.format(daylight / 3600) + "h " + DF2.format((daylight % 3600) / 60L) + "m"):"");
                // Isolate the hours in the string
  //            System.out.println(srs);
                AttributedString astr = new AttributedString(srs);
                pattern = Pattern.compile("\\d{2}:\\d{2}");
                matcher = pattern.matcher(srs);
                int nbMatch = 0;
                boolean found = matcher.find();
                // Highlight Rise and Set time
                while (found && nbMatch < 2)
                {
                  nbMatch++;
  //              String match = matcher.group();
                  int start = matcher.start();
                  int end   = matcher.end();
  //              System.out.println("Match: [" + match + "] : " + start + ", " + end);
                  astr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD, g.getFont().getSize()), start, end);
  //              astr.addAttribute(TextAttribute.FOREGROUND, Color.RED, start, end);
                  astr.addAttribute(TextAttribute.BACKGROUND, Color.YELLOW, start, end);
                  found = matcher.find();
                }
                // Body
                pattern = Pattern.compile("Sun");
                matcher = pattern.matcher(srs);
                nbMatch = 0;
                found = matcher.find();
                while (found && nbMatch < 1)
                {
                  nbMatch++;
                  int start = matcher.start();
                  int end   = matcher.end();
                  astr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD, g.getFont().getSize()), start, end);
                  astr.addAttribute(TextAttribute.BACKGROUND, Color.YELLOW, start, end);
                  found = matcher.find();
                }              
                g.drawString(astr.getIterator(), x, y);
                
                y += (fontSize + 2);            
              }
              double prevHeight = -Double.MAX_VALUE;
              int diffOffset = -1;
              
              // Add the sun rise and set for the next event
              timeAL.add(new TimedValue("SR", sunRise, (double)sunRise.getTimeInMillis()));
              timeAL.add(new TimedValue("SS", sunSet, (double)sunSet.getTimeInMillis()));
              Collections.sort(timeAL);
              
              TimedValue nextEvent = null;
              for (TimedValue tv : timeAL)
              {
                if (nextEvent == null && tv.getCalendar().after(now))
                {
                  nextEvent = tv;
                }
                String dataStr = "";
                if (!tv.getType().equals("SS") && !tv.getType().equals("SR"))
                {
              //  dataStr = (tv.getType().equals("SR")?"Sun Rise":"Sun Set") + " " + TIME_FORMAT.format(tv.getCalendar().getTime());
                  if (currentUnit != null && currentUnit.equals(TideStation.KNOTS)) // Current, in knots
                  {
                    if (tv.getType().equals("Slack"))
                      dataStr = "- Slack " + TIME_FORMAT.format(tv.getCalendar().getTime());
                    else
                      dataStr = (tv.getType().equals("HW")?"Max Flood":"Max Ebb ") + " " + TIME_FORMAT.format(tv.getCalendar().getTime()) + " : " + TideUtilities.DF22PLUS.format(tv.getValue()) + " " + /*ts.getDisplayUnit()*/ currentUnit;
                  }
                  else
                    dataStr = tv.getType() + " " + TIME_FORMAT.format(tv.getCalendar().getTime()) + " : " + TideUtilities.DF22PLUS.format(tv.getValue()) + " " + /*ts.getDisplayUnit()*/ currentUnit;
                  if (diffOffset == -1)
                  {
                    int len = g.getFontMetrics(g.getFont().deriveFont(Font.BOLD)).stringWidth(dataStr);
                    int tabSize = 50;
                    diffOffset = (int)(Math.floor(len / tabSize) + 1) * tabSize;
                  }
                  AttributedString as = new AttributedString(dataStr);
                  as.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD, g.getFont().getSize()), 0, dataStr.length());
                  g.drawString(as.getIterator(), x, y);
                  
                  if (prevHeight != -Double.MAX_VALUE && currentUnit != null && !currentUnit.startsWith("knots"))
                  {
                    double ampl = Math.abs(prevHeight - tv.getValue());
                    String diffStr = "\u0394 : " + TideUtilities.DF22.format(ampl) + " " + currentUnit; // \u0394 : Delta
                    g.drawString(diffStr, x + diffOffset, y - (fontSize / 2));
                  }
                  y += (fontSize + 2);    
                  if (!tv.getType().equals("SS") && !tv.getType().equals("SR"))
                    prevHeight = tv.getValue();
                }
              }
              if (nextEvent != null)
              {
                String dataStr = "- Next event:"; // + nextEvent.getType() + " " + TIME_FORMAT.format(nextEvent.getCalendar().getTime()) + " : " + TideUtilities.DF22PLUS.format(nextEvent.getValue()) + " " + /*ts.getDisplayUnit()*/ currentUnit + " (in " + Utils.formatTimeDiff(now, nextEvent.getCalendar()) + ")";
                if (nextEvent.getType().equals("SS") || nextEvent.getType().equals("SR"))
                  dataStr += (nextEvent.getType().equals("SR")?"Sun Rise":"Sun Set") + " " + TIME_FORMAT.format(nextEvent.getCalendar().getTime()) + " (in " + Utils.formatTimeDiff(now, nextEvent.getCalendar()) + ")";
                else
                {
                  if (TideStation.KNOTS.equals(currentUnit))
                    dataStr += (nextEvent.getType().equals("HW")?"Max Flood":"Max Ebb  ") + " " + TIME_FORMAT.format(nextEvent.getCalendar().getTime()) + " : " + TideUtilities.DF22PLUS.format(nextEvent.getValue()) + " " + /*ts.getDisplayUnit()*/ currentUnit + " (in " + Utils.formatTimeDiff(now, nextEvent.getCalendar()) + ")";
                  else
                    dataStr += nextEvent.getType() + " " + TIME_FORMAT.format(nextEvent.getCalendar().getTime()) + " : " + TideUtilities.DF22PLUS.format(nextEvent.getValue()) + " " + /*ts.getDisplayUnit()*/ currentUnit + " (in " + Utils.formatTimeDiff(now, nextEvent.getCalendar()) + ")";
                }
                AttributedString astr = new AttributedString(dataStr);
                pattern = Pattern.compile("Next event");
                matcher = pattern.matcher(dataStr);
                int nbMatch = 0;
                boolean found = matcher.find();
                // Highlight Rise and Set time
                while (found && nbMatch < 2)
                {
                  nbMatch++;
                  int start = matcher.start();
                  int end   = matcher.end();
                  astr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD | Font.ITALIC, g.getFont().getSize()), start, end);
                  found = matcher.find();
                }
                // Type
                pattern = Pattern.compile("HW");
                matcher = pattern.matcher(dataStr);
                nbMatch = 0;
                found = matcher.find();
                while (found && nbMatch < 1)
                {
                  nbMatch++;
                  int start = matcher.start();
                  int end   = matcher.end();
                  astr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD | Font.ITALIC, g.getFont().getSize()), start, end);
                  found = matcher.find();
                }              
                pattern = Pattern.compile("LW");
                matcher = pattern.matcher(dataStr);
                nbMatch = 0;
                found = matcher.find();
                while (found && nbMatch < 1)
                {
                  nbMatch++;
                  int start = matcher.start();
                  int end   = matcher.end();
                  astr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD | Font.ITALIC, g.getFont().getSize()), start, end);
                  found = matcher.find();
                }              
                g.drawString(astr.getIterator(), x, y);
//              g.drawString(dataStr, x, y);
                y += (fontSize + 2);
              }
              // Moon Phase
              //     Percentage
              g.drawString("Moon Phase: " + DF3.format(moonPhase) + "\272 (" + Long.toString(Math.round(moonIllum)) + "%)", x, y);
              int phaseInDay = (int)Math.round(moonPhase / (360d / 28d)) + 1;
              if (phaseInDay > 28) phaseInDay = 28;
              if (phaseInDay < 1) phaseInDay = 1;
              // Southern Hemisphere. Rotate Image.
              if (ts.getLatitude() < 0)
                phaseInDay = 29 - phaseInDay;
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
              
              // Bottom note
              g.setFont(g.getFont().deriveFont(Font.PLAIN, 9));
              String s = "Rise and Set times are given for an altitude of the body equal to zero.";
              //          012345678901234567890123456789012345678901234567890123456789012345678901
              //                    1         2         3         4         5         6         7  
              AttributedString astr = new AttributedString(s);
              astr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD, 10), 36, 44);
              astr.addAttribute(TextAttribute.FONT, g.getFont().deriveFont(Font.BOLD, 10), 66, 70);
              y = this.getHeight() - 5;
              g.drawString(astr.getIterator(), x, y);
  
              g.setFont(g.getFont().deriveFont(Font.PLAIN, fontSize));
              y -= (fontSize + 2);
              // Astronomical data
              if (isAstroReady() && est != null && showAltitudesCheckBox.isSelected())
              {
                double value = est[AstroComputer.SUN_ALT_IDX];  // Sun Altitude     
                int h = now.get(Calendar.HOUR_OF_DAY);
                int m = now.get(Calendar.MINUTE);
                int _x = (int)((/*(currDay * 24) +*/ h - hourOffset + (double)(m / 60D)) * widthRatio);
                int _y = (this.getHeight() / 2) - (int)((value) * heightRatioAlt);
                g.drawImage(sunSymbol, _x - 6, _y - 14, null); // Image is 13x13
                value = est[AstroComputer.MOON_ALT_IDX];  // Moon Altitude     
                _y = (this.getHeight() / 2) - (int)((value) * heightRatioAlt);
                g.drawImage(moonSymbol, _x - 6, _y - 14, null); // Image is 13x13
              }
              if (est != null && est[AstroComputer.MOON_ALT_IDX] > 0)
              {
  //              System.out.println("Moon Alt:" + GeomUtil.decToSex(est[2], GeomUtil.SWING, GeomUtil.NONE) + " (" + est[2] + ")");
  //              System.out.println("Moon Z  :" + GeomUtil.decToSex(est[3], GeomUtil.SWING, GeomUtil.NONE));
                g.drawString("Moon : Alt=" + GeomUtil.decToSex(est[AstroComputer.MOON_ALT_IDX], GeomUtil.SWING, GeomUtil.NONE) + ", Z:" + GeomUtil.decToSex(est[AstroComputer.MOON_Z_IDX], GeomUtil.SWING, GeomUtil.NONE), x, y);
                y -= (fontSize + 2);
              }
              if (est != null && est[AstroComputer.SUN_ALT_IDX] > 0)
              {
  //              System.out.println("Sun Alt:" + GeomUtil.decToSex(est[0], GeomUtil.SWING, GeomUtil.NONE) + " (" + est[0] + ")");
  //              System.out.println("Sun Z  :" + GeomUtil.decToSex(est[1], GeomUtil.SWING, GeomUtil.NONE));
                g.drawString("Sun : Alt=" + GeomUtil.decToSex(est[AstroComputer.SUN_ALT_IDX], GeomUtil.SWING, GeomUtil.NONE) + ", Z:" + GeomUtil.decToSex(est[AstroComputer.SUN_Z_IDX], GeomUtil.SWING, GeomUtil.NONE), x, y);
                y -= (fontSize + 2);
              }
              g.drawString("LHA Aries:" + GeomUtil.decToSex(est[AstroComputer.LHA_ARIES_IDX], GeomUtil.SWING, GeomUtil.NONE), x, y);
              y -= (fontSize + 2);
              
              g.drawString("Moon D: " + GeomUtil.decToSex(AstroComputer.getMoonDecl(), GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) + 
                           " GHA:" + GeomUtil.decToSex(AstroComputer.getMoonGHA(), GeomUtil.SWING, GeomUtil.NONE) + 
                           " (G: " + GeomUtil.decToSex(AstroComputer.ghaToLongitude(AstroComputer.getMoonGHA()), GeomUtil.SWING, GeomUtil.EW, GeomUtil.LEADING_SIGN) + ")", 
                           x, y);
              y -= (fontSize + 2);
              g.drawString("Sun D: " + GeomUtil.decToSex(AstroComputer.getSunDecl(), GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) + 
                           " GHA:" + GeomUtil.decToSex(AstroComputer.getSunGHA(), GeomUtil.SWING, GeomUtil.NONE) + 
                           " (G: " + GeomUtil.decToSex(AstroComputer.ghaToLongitude(AstroComputer.getSunGHA()), GeomUtil.SWING, GeomUtil.EW, GeomUtil.LEADING_SIGN) + ")", 
                           x, y);
              y -= (fontSize + 2);
              // Tell the chart panel
              chartCommandPanel.setSunD(AstroComputer.getSunDecl());
              chartCommandPanel.setMoonD(AstroComputer.getMoonDecl());
              chartCommandPanel.setSunGHA(AstroComputer.getSunGHA());
              chartCommandPanel.setMoonGHA(AstroComputer.getMoonGHA());   
              chartCommandPanel.setCurrentDate(now);
            }
            if (mouseIsIn)
            {
              g.setColor(Color.black);
              float[] dashPattern = { 2, 2, 2, 2 };
              ((Graphics2D)g).setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
              g.drawLine(mouseX, 0, mouseX, this.getHeight());
              int y = this.getHeight() - (int)((mouseWh - bottomValue) * heightRatio);
              g.fillOval(mouseX - 2, y - 2, 4, 4);
//            postit(g, postit, mouseX, y, Color.black, Color.cyan, 0.50f);
              postit(g, postit, mouseX, mouseY, Color.black, Color.cyan, 0.50f);
            }
          }
        }
        catch (Exception ex)
        {
//        JOptionPane.showMessageDialog(this, (ex!=null?ex.toString():"Null Exception..."), "Oops", JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
        }
      }
    }

    public void postit(Graphics g, String s, int x, int y, Color bgcolor, Color fgcolor, Float transp)
    {
      int bevel = 2;
      int postitOffset = 5;
      
      int startX = x;
      int startY = y;
      
      Color origin = g.getColor();
      g.setColor(Color.black);
      Font f = g.getFont();
      int nbCr = 0;
      int crOffset;
      for (crOffset = 0; (crOffset = s.indexOf("\n", crOffset) + 1) > 0;)
        nbCr++;

      String txt[] = new String[nbCr + 1];
      int i = 0;
      crOffset = 0;
      for (i = 0; i < nbCr; i++)
        txt[i] = s.substring(crOffset, (crOffset = s.indexOf("\n", crOffset) + 1) - 1);

      txt[i] = s.substring(crOffset);
      int strWidth = 0;
      for (i = 0; i < nbCr + 1; i++)
      {
        if (g.getFontMetrics(f).stringWidth(txt[i]) > strWidth)
          strWidth = g.getFontMetrics(f).stringWidth(txt[i]);
      }
      Color c = g.getColor(); // postitTextColor
      g.setColor(bgcolor);
      if (g instanceof Graphics2D)
      {
        // Transparency
        Graphics2D g2 = (Graphics2D)g;
        float alpha = (transp!=null?transp.floatValue():0.3f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      }
      // left or right, up or down...
      Point topRightExtremity      = new Point(x + postitOffset + strWidth + (2 * bevel), (y - f.getSize()) + 1);
      Point bottomRightExtremity   = new Point(x + postitOffset + strWidth + (2 * bevel), (nbCr + 1) * f.getSize());
      Point bottomLeftExtremity    = new Point(x, (nbCr + 1) * f.getSize());
      
      if (!this.getVisibleRect().contains(topRightExtremity) && !this.getVisibleRect().contains(bottomRightExtremity))   
      {
        // This display left
        startX = x - strWidth - (2 * bevel) - (2 * postitOffset);
      }
      if (!this.getVisibleRect().contains(bottomLeftExtremity))   
      {
        // This display up
    //    startY = y - ((nbCr + 1) * f.getSize());
        startY = y - ((nbCr) * f.getSize());
    //    System.out.println("Up, y [" + y + "] becomes [" + startY + "]");
      }
      
      g.fillRect(startX + postitOffset, (startY - f.getSize()) + 1, strWidth + (2 * bevel), (nbCr + 1) * f.getSize());
      if (g instanceof Graphics2D)
      {
        // Reset Transparency
        Graphics2D g2 = (Graphics2D)g;
        float alpha = 1.0f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      }
      if (fgcolor != null)
        g.setColor(fgcolor);
      else
        g.setColor(c);
      
      for(i = 0; i < nbCr + 1; i++)
        g.drawString(txt[i], startX + bevel + postitOffset, startY + (i * f.getSize()));

      g.setColor(origin);
    }
  };   
  
  private TidePanel graphPanelExtended = new TidePanel() // Extended, user-tuned (non dynamic)
  {
    private transient List<Double> zeroUTC = null;
    private transient List<LabeledValue> noonLabel = null;
    private transient List<MoonPhaseValue> moonPhaseList = null;
    private transient List<DayNight> dayNight = null;
    private transient List<DataPoint> moonDeclination = null;
    
    private transient GradientPaint nightGradient = null;
    private transient GradientPaint dayGradient   = null;

    @Override
    public void mouseMoved(MouseEvent e)
    {
      double timeWidth = 24D * defaultWidth; 
      double widthRatio = (double)this.getWidth() / timeWidth;
      double h = (e.getX() / widthRatio) + hourOffset;
      double m = (h - (int)h) * 60;
      
      // "now" is the first day, and it is in the middle of the fork
      int nbDay = (int)(h / 24) - (defaultWidth / 2);
      Calendar cal = (GregorianCalendar)now.clone();
      cal.add(Calendar.DAY_OF_YEAR, nbDay);                      
             
      String moonDeclValue = "";       
      if (moonDeclination != null)
      {
        moonDeclValue = "";
        synchronized (moonDeclination)
        {
          for (DataPoint dp : moonDeclination)
          {
            if (e.getX() == (int)Math.round(dp.getX() * widthRatio))
            {
              moonDeclValue += ("<br>Moon Decl: " + GeomUtil.decToSex(dp.getY(), GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN));
              break;
            }
          }
        }
      }
      JUST_DATE_FORMAT_SMALL.setTimeZone(TimeZone.getTimeZone(timeZone2Use));    
      String toolTipMess = "<html><center>" + 
                           JUST_DATE_FORMAT_SMALL.format(cal.getTime()) + 
                           "<br>" + DF2.format((int)(h % 24)) + ":" + DF2.format(m) +
                           moonDeclValue +
                           "</center>" + 
//                         "<br>x:" + e.getX() + ", y:" + e.getY() + 
                           "</html>";
      this.setToolTipText(toolTipMess);
//    this.setToolTipText("<html>" + DF2.format((int)(h)) + ":" + DF2.format(m) + "</html>");
    }

    @Override
    protected void paintComponent(Graphics g)
    {
      if (!this.isVisible())
        return;      
      nightGradient = new GradientPaint(0, this.getHeight(), Color.WHITE, 0, 0, Color.BLACK); // vertical, bottom up
      dayGradient   = new GradientPaint(0, this.getHeight(), Color.BLUE, 0, 0, Color.WHITE); // vertical, bottom up
      refresh(g);
    }
    
    private void refresh(final Graphics g)
    {
      synchronized (g)
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
    //  g.setFont(new Font("courier new", Font.PLAIN, 12));
        Calendar from = null, to = null;
//      double moonPhase = -1D;
//      int prevPhase = -1;
        
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
              from = (Calendar)now.clone();
              to = (Calendar)now.clone();
              from.add(Calendar.DAY_OF_MONTH, -(defaultWidth / 2)); 
              to.add(Calendar.DAY_OF_MONTH,   1 + (defaultWidth / 2));
    //        System.out.println("From " + from.getTime().toString() + " to " + to.getTime().toString());
              mm = TideUtilities.getMinMaxWH(ts, constSpeed, from, to);
    //        System.out.println("At " + tideStationName + " in " + now.get(Calendar.YEAR) + ", min : " + BackEndXMLTideComputer.DF22.format(mm[BackEndXMLTideComputer.MIN_POS]) + " " + ts.getUnit() + ", max : " + BackEndXMLTideComputer.DF22.format(mm[BackEndXMLTideComputer.MAX_POS]) + " " + ts.getDisplayUnit());
              mm[TideUtilities.MIN_POS] = Utils.convert(mm[TideUtilities.MIN_POS], ts.getDisplayUnit(), currentUnit);
              mm[TideUtilities.MAX_POS] = Utils.convert(mm[TideUtilities.MAX_POS], ts.getDisplayUnit(), currentUnit);
              
              float gutter = 2f; // 2 Feet
              if ("meters".equals(currentUnit))
                gutter = (float)TideUtilities.feetToMeters(gutter);
              double timeWidth = 24D;         // One day
              timeWidth = 24 * defaultWidth;  
              double widthRatio = (double)width / timeWidth;
              double heightRatio = (double)height / ((2 * gutter) + mm[TideUtilities.MAX_POS] - mm[TideUtilities.MIN_POS]);
              double bottomValue = mm[TideUtilities.MIN_POS] - gutter;
              double heightRatioAlt = (double)height / 180d;
                
              // Horizontal grid
              g.setColor(Color.LIGHT_GRAY);
              for (int hgt=(int)Math.floor(mm[TideUtilities.MIN_POS]); hgt<=(int)Math.floor(mm[TideUtilities.MAX_POS]); hgt++)
              {
                g.drawLine(0, height - (int)((hgt - bottomValue) * heightRatio), width, height - (int)((hgt - bottomValue) * heightRatio));
                Color c = g.getColor();
                g.setColor(Color.BLACK);              
                g.drawString((hgt==0?"0":TideUtilities.DF2PLUS.format(hgt)) /* + " " + unitComboBox.getSelectedItem().toString() */, 
                             5, 
                             height - (int)((hgt - bottomValue) * heightRatio) - 2);
                g.setColor(c);
              }
              // Vertical grid
              FontMetrics fm = g.getFontMetrics();
              for (int hour=(2 + hourOffset); (from == null && to == null) && hour<(24 + hourOffset); hour+=2)  // TASK something more dynamic here
              {
                int _x = (int)((hour - hourOffset) * widthRatio);
                g.drawLine(_x, 0, _x, height);
                int _h = hour;
                while (_h < 0) _h += 24;
                String s = DF2.format(_h % 24);
                Color c = g.getColor();
                g.setColor(Color.BLACK);
                g.drawString(s, _x - (fm.stringWidth(s) / 2), height - 14);
                g.setColor(c);
              }
              
              Point previous = null;
              // Calculate and Draw here            
              Font f;
              boolean keepLooping = true;
              Calendar reference = null; // (Calendar)now.clone();
              
              int currDay = 0;
  //          reference = (Calendar)from.clone();            
  
              showTideCurveCB.setVisible(decomposeCheckBox.isSelected());
              // Decompose
              if (decomposeCheckBox.isSelected())               
              {
                showTideCurveCB.setBounds(this.getWidth() - 130, this.getHeight() - 25, 130, 25);
                if (coeffColor == null && ts != null) // Tide Station has changed
                {
                  buildCoeffColor();
                }
                if (harmonicCurves == null)
                {
                  setHarmonicsReady(false);
                  final TidePanel instance = this;
                  final Calendar _from = from;
                  final Calendar _to = to;
                  final double _bottomValue = bottomValue;
                  Thread harmonicThread = new Thread()
                    {
                      public void run()
                      {
                        long before = System.currentTimeMillis();
                        instance.setHarmonicsReady(false);
                        harmonicCurves = new Hashtable<String, List<DataPoint>>();
                        int k = lookBusy("Calculating Harmonics");
                        for (int j=0; j<coeffColor.length; j++)
                        {
                          // Calculating one full curve
                          int currDay = 0;
                          Calendar reference = (Calendar)_from.clone();  
                          List<DataPoint> lp = harmonicCurves.get(coeffColor[j].name);
                          if (lp == null) 
                          {             
                            System.out.println("Calculating curve for [" + coeffColor[j].name + "]");
                            updateBusyLook("Calculating [" + coeffColor[j].name + "]", k);
                            lp = new ArrayList<DataPoint>();
                            boolean keepLooping = true;
                            while (keepLooping)
                            {
                        //    System.out.println("Calculating tide for " + reference.getTime().toString());
                              if (!reference.before(_to))
                              {
                                keepLooping = false;
                        //      System.out.println("Exiting loop:" + reference.getTime().toString() + " after " + to.getTime().toString());
                              }
                              if (reference.get(Calendar.YEAR) != currentYear)
                              {
                        //      System.out.println("Refetching TideStation for " + reference.get(Calendar.YEAR) + " was " + currentYear);
                                try {ts = BackEndTideComputer.findTideStation(tideStationName, reference.get(Calendar.YEAR)); }
                                catch (Exception ex) { ex.printStackTrace(); }
                                currentYear = reference.get(Calendar.YEAR);
                              }
                              for (int h=0; h<24; h++) 
                              {
                                for (int m=0; m<60; m++)
                                {
                                  Calendar cal = new GregorianCalendar(reference.get(Calendar.YEAR),
                                                                       reference.get(Calendar.MONTH),
                                                                       reference.get(Calendar.DAY_OF_MONTH),
                                                                       h + hourOffset, m);
                                  cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                                  int year = cal.get(Calendar.YEAR); 
                                  // Calc Jan 1st of the current year
                                  Date jan1st = new GregorianCalendar(year, 0, 1).getTime();
                        //              double value = Utils.convert(TideUtilities.getHarmonicValue(cal.getTime(), jan1st, ts, constSpeed, i), ts.getDisplayUnit(), currentUnit);
                                  double value = Utils.convert(TideUtilities.getHarmonicValue(cal.getTime(), jan1st, ts, constSpeed, coeffColor[j].name), ts.getDisplayUnit(), currentUnit);
                                  double x = (currDay * 24) + (h + (double)(m / 60D));
                                  double y = (value - _bottomValue);
                                  lp.add(new DataPoint(x, y));
                                }
                              }
                              reference.add(Calendar.DAY_OF_YEAR, 1);
                              currDay++;
                        //    System.out.println("Day " + currDay + " widthRatio:" + widthRatio);
                            }
                            harmonicCurves.put(coeffColor[j].name, lp);
                          }
                        }
                        long after = System.currentTimeMillis();
                        coolDown(k);
                        System.out.println("2 - Harmonic computation completed in " + Long.toString(after - before) + " ms");
                        instance.setHarmonicsReady(true);
                        instance.repaint();
                      }
                    };
                  harmonicThread.start();
                }
                if (isHarmonicsReady())
                {
                  for (int j=0; j<coeffColor.length; j++)
                  {
                    if (coeffToHighlight != null && !showAllCurves && !coeffToHighlight.contains(coeffColor[j].name))
                        continue;
                    List<DataPoint> lp = harmonicCurves.get(coeffColor[j].name);
                    // Draw now, hormonic curve
                    Stroke origStroke = null;
                    if (coeffToHighlight != null)
                    {
                      if (coeffToHighlight.contains(coeffColor[j].name))
                      {
                        // Thicker
                        origStroke = ((Graphics2D)g).getStroke();
                        ((Graphics2D) g).setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                      }
                      else if (!showAllCurves)
                        continue;
                    }
                    g.setColor(coeffColor[j].color);
                    Point previousVal = null;
                    for (DataPoint p : lp)
                    {
                      int x = (int)(p.getX() * widthRatio);
                      int y = height - (int)(p.getY() * heightRatio);
                      if (previousVal != null)
                        g.drawLine(previousVal.x, previousVal.y, x, y);
                      previousVal = new Point(x, y);                                              
                    }
                    // Reset thickness
                    if (origStroke != null)
                      ((Graphics2D) g).setStroke(origStroke);
                  } 
                }
              }
  
              if (showAltitudesCheckBox.isSelected())
              {
                if (sunAltitudes == null || moonAltitudes == null)
                {
                  final TidePanel instance = this;
                  setAstroReady(false);
                  final Calendar _from  = from, _to = to;
                  Thread astroThread = new Thread()
                    {
                      public void run()
                      {
                        long before = System.currentTimeMillis();
                        instance.setAstroReady(false);
                        System.out.println("Calculating Sun and Moon altitudes");
                        int k = lookBusy("Calculating Sun and Moon altitudes");
                        sunAltitudes  = new ArrayList<DataPoint>();
                        moonAltitudes = new ArrayList<DataPoint>();
                        moonDeclination = new ArrayList<DataPoint>();
                        int currDay = 0;
                        Calendar reference = (Calendar)_from.clone();  
                        boolean keepLooping = true;
                        while (keepLooping)
                        {
                          if (!reference.before(_to))
                          {
                            keepLooping = false;
                          }
                          for (int h=0; h<24; h++)
                          {
                            for (int m=0; m<60; m+=5) // every five minutes
                            {
                              Calendar cal = new GregorianCalendar(reference.get(Calendar.YEAR),
                                                                   reference.get(Calendar.MONTH),
                                                                   reference.get(Calendar.DAY_OF_MONTH),
                                                                   h + hourOffset, 
                                                                   m);
                              cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                          /*  Date date = */ cal.getTime(); // To apply the new Time Zone... Does not happen otherwise. :o( 
                              cal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                          //  date = cal.getTime(); 
                              // Sun & Moon altitudes
                              double[] values = AstroComputer.getSunMoonAltDecl(cal.get(Calendar.YEAR), 
                                                                                cal.get(Calendar.MONTH) + 1, 
                                                                                cal.get(Calendar.DAY_OF_MONTH), 
                                                                                cal.get(Calendar.HOUR_OF_DAY), 
                                                                                cal.get(Calendar.MINUTE), 
                                                                                0, 
                                                                                ts.getLatitude(), 
                                                                                ts.getLongitude());
                              double value = values[AstroComputer.HE_SUN_IDX];  // Sun                  
                              double x = ((currDay * 24) + h + (double)(m / 60D));
                              double y = value; // (this.getHeight() / 2) - (int)((value) * heightRatioAlt);
                              try { synchronized(sunAltitudes) { sunAltitudes.add(new DataPoint(x, y)); } }
                              catch (NullPointerException npe)
                              {
                                System.err.println("Sun Altitude is null, wierd");
                                npe.printStackTrace();                                                    
                              }
                              
                              value = values[AstroComputer.HE_MOON_IDX]; // Moon
                              y = value; // (this.getHeight() / 2) - (int)((value) * heightRatioAlt);
                              try
                              {
                                synchronized (moonAltitudes) { moonAltitudes.add(new DataPoint(x, y)); }
                              }
                              catch (NullPointerException npe)
                              {
                                System.err.println("moonAltitudes is null. Wierd.");
                                npe.printStackTrace();
                              }
                              
                              value = values[AstroComputer.DEC_MOON_IDX];
                              y = value;
                              try
                              {
                                synchronized (moonDeclination) { moonDeclination.add(new DataPoint(x, y)); }
                              }
                              catch (NullPointerException npe)
                              {
                                System.err.println("moonDeclination is null, wierd.");
                                npe.printStackTrace();
                              }
                            }
                          }
                          reference.add(Calendar.DAY_OF_YEAR, 1);
                          currDay++;
                        //                System.out.println("Day " + currDay + " widthRatio:" + widthRatio);
                        }
                        long after = System.currentTimeMillis();
                        coolDown(k);
                        System.out.println("2 - Sun & Moon computation completed in " + Long.toString(after - before) + " ms");
                        instance.setAstroReady(true);
                        instance.repaint();
                      }
                    };
                  astroThread.start();
                  
                }
                if (isAstroReady())
                {
                  // Horizon
                  g.setColor(Color.GRAY);
                  double bh = ts.getBaseHeight();
                  bh = Utils.convert(bh, ts.getDisplayUnit(), currentUnit);
                  int y = this.getHeight() / 2;
                  g.drawLine(0, y, this.getWidth(), y);       
                  GradientPaint gradient = new GradientPaint(0, this.getHeight(), Color.WHITE, 0, 0, Color.BLUE); // vertical, upside down
                  Paint paint = ((Graphics2D)g).getPaint();
                  ((Graphics2D)g).setPaint(gradient);              
                  ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                  g.fillPolygon(new Polygon(new int[] {0, this.getWidth(), this.getWidth(), 0},
                                            new int[] {y, y, this.getHeight(), this.getHeight()},
                                            4));
                  ((Graphics2D)g).setPaint(paint);              
                  ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                  
                  // Sun
                  g.setColor(Color.BLACK);
                  Point previousPt = null;
                  for (DataPoint dp : sunAltitudes)
                  {
                    Point p = new Point((int)((dp.getX()) * widthRatio),
                                        (this.getHeight() / 2) - (int)(dp.getY() * heightRatioAlt));
                    if (previousPt != null)
                      g.drawLine(previousPt.x, previousPt.y, p.x, p.y);
                    previousPt = p;
                  }
                  // Moon
                  g.setColor(Color.BLACK);
                  previousPt = null;
                  for (DataPoint dp : moonAltitudes)
                  {
                    Point p = new Point((int)((dp.getX()) * widthRatio),
                                        (this.getHeight() / 2) - (int)(dp.getY() * heightRatioAlt));
                    if (previousPt != null)
                      g.drawLine(previousPt.x, previousPt.y, p.x, p.y);
                    previousPt = p;
                  }
                  // Moon decl.
                  g.setColor(Color.BLUE);
                  previousPt = null;
                  Stroke origStroke = ((Graphics2D)g).getStroke();
                  Stroke moonDeclStroke = null;
             //   float[] dashPattern = { 1, 1, 1, 1 };
                  moonDeclStroke = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER); //, 10, dashPattern, 0);
                  ((Graphics2D) g).setStroke(moonDeclStroke);
                  for (DataPoint dp : moonDeclination)
                  {
                    Point p = new Point((int)((dp.getX()) * widthRatio),
                                        (this.getHeight() / 2) - (int)(dp.getY() * heightRatioAlt));
                    if (previousPt != null)
                      g.drawLine(previousPt.x, previousPt.y, p.x, p.y);
                    previousPt = p;
                  }
                  ((Graphics2D) g).setStroke(origStroke);
                }
              }
              
              Stroke origStroke = ((Graphics2D)g).getStroke();
              Stroke mainCurveStroke = null;
              mainCurveStroke = new BasicStroke(2f);  // , BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
              ((Graphics2D) g).setStroke(mainCurveStroke);
  
              if (showBaseHeightCheckBox.isSelected())
              {
                g.setColor(Color.BLUE);
                double bh = ts.getBaseHeight();
                bh = Utils.convert(bh, ts.getDisplayUnit(), currentUnit);
                int y = height - (int)((bh - bottomValue) * heightRatio);
                g.drawLine(0, y, this.getWidth(), y);
              }
              if (showTideCurve)
              {
                if (!decomposeCheckBox.isSelected() || (decomposeCheckBox.isSelected() && showTideCurveCB.isSelected()))
                {
                  /* The curve */
                  if (mainCurve == null)
                  {
                    final TidePanel instance = this;
                    final Calendar _from = from, _to = to;
                    final double _bottomValue = bottomValue;
                    setMainCurveReady(false);
                    mainCurve = new ArrayList<DataPoint>();
                    Thread tideComputerThread = new Thread()
                      {
                        public void run()
                        {
                          long before = System.currentTimeMillis();
                          instance.setMainCurveReady(false);
                          System.out.println("1- Calculating main Curve, " + new Date().toString());
                          int k = lookBusy("Calculating main Curve");
                          zeroUTC = new ArrayList<Double>();
                          noonLabel = new ArrayList<LabeledValue>();
                          moonPhaseList = new ArrayList<MoonPhaseValue>();
                          dayNight = new ArrayList<DayNight>();
                          
                          int currDay = 0;
                          Calendar reference = (Calendar)_from.clone();
                          boolean keepLooping = true;
                          
                          double moonPhase = 0d;
                          int prevPhase = -1;
                          
                          double prevSunRS = 0;
                          while (keepLooping)
                          {
                          //System.out.println("Calculating tide for " + reference.getTime().toString());
                            if (!reference.before(_to))
                            {
                              keepLooping = false;
                          //System.out.println("Exiting loop:" + reference.getTime().toString() + " after " + to.getTime().toString());
                            }
                            if (reference.get(Calendar.YEAR) != currentYear)
                            {
            //                System.out.println("Refetching TideStation for " + reference.get(Calendar.YEAR) + " was " + currentYear);
                              try { ts = BackEndTideComputer.findTideStation(tideStationName, reference.get(Calendar.YEAR)); }
                              catch (Exception ex) { ex.printStackTrace(); }
                              currentYear = reference.get(Calendar.YEAR);
                            }
                            for (int h=0; h<24; h++)
                            {
                              Calendar utcCal = new GregorianCalendar(reference.get(Calendar.YEAR), 
                                                                      reference.get(Calendar.MONTH), 
                                                                      reference.get(Calendar.DAY_OF_MONTH), 
                                                                      h + hourOffset, 
                                                                      0, 
                                                                      0);
                              utcCal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
                              Date d = utcCal.getTime();
                              moonPhase = AstroComputer.getMoonPhase(utcCal.get(Calendar.YEAR), 
                                                                     utcCal.get(Calendar.MONTH) + 1, 
                                                                     utcCal.get(Calendar.DAY_OF_MONTH), 
                                                                     h + hourOffset - (int)Math.round(AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(ts.getTimeZone()), d)), 
                                                                     0, 
                                                                     0);
                              if (h == 0) // Sun rise and set
                              {
                                // Night and Day
                                double[] rsSun  = null;
                                rsSun  = AstroComputer.sunRiseAndSet(ts.getLatitude(), ts.getLongitude());
                                Calendar sunRise = new GregorianCalendar();
                                sunRise.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
//                                sunRise.set(Calendar.YEAR, now.get(Calendar.YEAR));
//                                sunRise.set(Calendar.MONTH, now.get(Calendar.MONTH));
//                                sunRise.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                                sunRise.set(Calendar.YEAR, reference.get(Calendar.YEAR));
                                sunRise.set(Calendar.MONTH, reference.get(Calendar.MONTH));
                                sunRise.set(Calendar.DAY_OF_MONTH, reference.get(Calendar.DAY_OF_MONTH));
                                sunRise.set(Calendar.SECOND, 0);
                                              
//                              System.out.println("Now      :" + now.getTime());
//                              System.out.println("Reference:" + reference.getTime());
//                              System.out.println("Rise: TZ offset at " + sunRise.getTime() + " is " + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use /*ts.getTimeZone()*/), sunRise.getTime()));              
                                double r = rsSun[AstroComputer.UTC_RISE_IDX] /*+ Utils.daylightOffset(sunRise)*/ + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use /*ts.getTimeZone()*/), sunRise.getTime());
                                int min = (int)((r - ((int)r)) * 60);
                                sunRise.set(Calendar.MINUTE, min);
                                sunRise.set(Calendar.HOUR_OF_DAY, (int)r);
                                
                                Calendar sunSet = new GregorianCalendar();
                                sunSet.setTimeZone(TimeZone.getTimeZone(ts.getTimeZone()));
//                                sunSet.set(Calendar.YEAR, now.get(Calendar.YEAR));
//                                sunSet.set(Calendar.MONTH, now.get(Calendar.MONTH));
//                                sunSet.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
                                sunSet.set(Calendar.YEAR, reference.get(Calendar.YEAR));
                                sunSet.set(Calendar.MONTH, reference.get(Calendar.MONTH));
                                sunSet.set(Calendar.DAY_OF_MONTH, reference.get(Calendar.DAY_OF_MONTH));
                                sunSet.set(Calendar.SECOND, 0);

//                              System.out.println("Set : TZ offset at " + sunSet.getTime() + " is " + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use /*ts.getTimeZone()*/), sunSet.getTime()));              
                                r = rsSun[AstroComputer.UTC_SET_IDX] /*+ Utils.daylightOffset(sunSet)*/ + AstroComputer.getTimeZoneOffsetInHours(TimeZone.getTimeZone(timeZone2Use/*ts.getTimeZone()*/), sunSet.getTime());
                                min = (int)((r - ((int)r)) * 60);
                                sunSet.set(Calendar.MINUTE, min);
                                sunSet.set(Calendar.HOUR_OF_DAY, (int)r);
                                
                                sunRise.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                                sunSet.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                                if (sunRise.get(Calendar.DAY_OF_MONTH) != sunSet.get(Calendar.DAY_OF_MONTH)) // TASK Not always right...
                                {
                                  sunSet.set(Calendar.YEAR, sunRise.get(Calendar.YEAR));
                                  sunSet.set(Calendar.MONTH, sunRise.get(Calendar.MONTH));
                                  sunSet.set(Calendar.DAY_OF_MONTH, sunRise.get(Calendar.DAY_OF_MONTH));
                                }
                                if (sunRise.before(sunSet))
                                {
                                  double x = ((currDay * 24) + sunRise.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunRise.get(Calendar.MINUTE) / 60D));
                                  if (x > 0)
                                  {
                                    dayNight.add(new DayNight(prevSunRS, (x - prevSunRS), nightGradient));
                                  }
                                  x = ((currDay * 24) + sunSet.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunSet.get(Calendar.MINUTE) / 60D));
                                  dayNight.add(new DayNight(prevSunRS, (x - prevSunRS), dayGradient));
                                  prevSunRS = x;
                                }
                                if (sunSet.before(sunRise))
                                {
                                  double x1 = ((currDay * 24) + sunSet.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunSet.get(Calendar.MINUTE) / 60D));
                                  double x2 = ((currDay * 24) + sunRise.get(Calendar.HOUR_OF_DAY) - hourOffset + (double)(sunRise.get(Calendar.MINUTE) / 60D));
                                  dayNight.add(new DayNight(x1, x2 - x1, nightGradient));
                                  prevSunRS = x2;
                                }
                              }
                          //            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM HH:mm Z");
                          //            System.out.println("At " + sdf.format(utcCal.getTime()) + ", phase = " + moonPhase);
                              for (int m=0; m<60; m++)
                              {
                                Calendar cal = new GregorianCalendar(reference.get(Calendar.YEAR),
                                                                     reference.get(Calendar.MONTH),
                                                                     reference.get(Calendar.DAY_OF_MONTH),
                                                                     h + hourOffset, m);
                                cal.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                                double wh = 0;
                                try { wh = Utils.convert(TideUtilities.getWaterHeight(ts, constSpeed, cal), ts.getDisplayUnit(), currentUnit); }
                                catch (Exception ex) { ex.printStackTrace(); }
                                double x = (currDay * 24) + (h + (double)(m / 60D));
                                double y = (wh - _bottomValue);
                                try
                                {
                                  synchronized (mainCurve) { mainCurve.add(new DataPoint(x, y)); }
                                }
                                catch (NullPointerException npe)
                                {
                                  System.err.println("mainCurve is null, wierd.");
                                  npe.printStackTrace();
                                }
                                
                                if (h == 0 && m == 0) // Vertical grid at 00:00 each day
                                  zeroUTC.add(x);

                                if (h == 12 && m == 0) // Local Noon, display day label
                                {
                                  String dStr = DATE_FORMAT.format(cal.getTime());
                                  try
                                  {
                                    synchronized (noonLabel) {noonLabel.add(new LabeledValue(x, dStr)); }
                                  }
                                  catch (NullPointerException npe)
                                  {
                                    System.err.println("noonLabel is null. Wierd.");
                                    npe.printStackTrace();
                                  }
                                }
                                Calendar currentDate = GregorianCalendar.getInstance();
                                currentDate.setTimeZone(TimeZone.getTimeZone(timeZone2Use));
                                currentDate.getTime(); // Validate...
                                // If Today, draw line for current time
                                if (false)
                                {
                                  if (currentDate.get(Calendar.YEAR)         == cal.get(Calendar.YEAR) &&
                                      currentDate.get(Calendar.MONTH)        == cal.get(Calendar.MONTH) &&
                                      currentDate.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
                                      currentDate.get(Calendar.HOUR_OF_DAY)  == cal.get(Calendar.HOUR_OF_DAY) &&
                                      currentDate.get(Calendar.MINUTE)       == cal.get(Calendar.MINUTE) /* &&
                                      currentDate.get(Calendar.SECOND)       == cal.get(Calendar.SECOND) */)
                                  {
                                    Color c = g.getColor();
                                    g.setColor(Color.GREEN);
                          //        g.drawLine(x, 0, x, height);
                                    g.setColor(c);
                                  }
                                }
                          //                  System.out.println("Phase:" + (int)Math.round(moonPhase) + " at " + cal.getTime());
                                if (((int)Math.round(moonPhase) == 0 && prevPhase != 0 && prevPhase != 360) ||
                                    ((int)Math.round(moonPhase) == 90 && prevPhase != 90) ||
                                    ((int)Math.round(moonPhase) == 180 && prevPhase != 180) ||
                                    ((int)Math.round(moonPhase) == 270 && prevPhase != 270) ||
                                    ((int)Math.round(moonPhase) == 360 && prevPhase != 360 && prevPhase != 0))
                                {
                          //                    System.out.println("Phase:" + (int)Math.round(moonPhase) + " at " + cal.getTime() + ", X=" + x + " (width:" + this.getWidth() + ")");
                                  // Draw line for moon phase (for now)
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
                                  try
                                  {
                                    synchronized (moonPhaseList) { moonPhaseList.add(new MoonPhaseValue(x, moonPhase, phaseStr)); }
                                  }
                                  catch (NullPointerException npe)
                                  {
                                    System.err.println("moonPhaseList is null, wierd.");
                                    npe.printStackTrace();
                                  }
                                  prevPhase = (int)Math.round(moonPhase);
                                }
                              }
                            }
                            reference.add(Calendar.DAY_OF_YEAR, 1);
                            currDay++;
                  //        System.out.println("Day " + currDay + " widthRatio:" + widthRatio);
                          }
                          long after = System.currentTimeMillis();
                          coolDown(k);     
                          System.out.println("2 _ Main curve computation completed in " + Long.toString(after - before) + " ms");
                          instance.setMainCurveReady(true);
                          instance.repaint();
                        }
                      };
                    tideComputerThread.start();                    
                  }
                  // -- Drawing now --
                  if (isMainCurveReady())
                  {
                    // Night and days
                    // Paint background for daylight
                    ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    Color c = g.getColor();
                    g.setColor(Color.LIGHT_GRAY);
                    
                    for (DayNight dn : dayNight)
                    {
                      int x1 = (int)(dn.getX1() * widthRatio);                    
                      int x2 = (int)(dn.getX2() * widthRatio);                    
                      Paint paint = ((Graphics2D)g).getPaint();
                      ((Graphics2D)g).setPaint(dn.getGradient());              
                      g.fillRect(x1, 0, x2, this.getHeight());
                      ((Graphics2D)g).setPaint(paint);                                  
                    }
                    ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    g.setColor(c);
                    // Days
                    c = g.getColor();
                    ((Graphics2D) g).setStroke(origStroke);
                    g.setColor(Color.DARK_GRAY);
                    for (Double d : zeroUTC)
                    {
                      int x = (int)((d.doubleValue()) * widthRatio);
                      g.drawLine(x, 0, x, height);
                    }
                    g.setColor(c);
                    ((Graphics2D) g).setStroke(mainCurveStroke);
                    // Noon Labels                
                    c = g.getColor();
                    g.setColor(Color.DARK_GRAY);
                    f = g.getFont();
                    g.setFont(g.getFont().deriveFont(Font.PLAIN, 9));
                    currDay = 0;
                    for (LabeledValue lv : noonLabel)
                    {
                      String label = lv.getLabel();
                      int x = (int)(lv.getX() * widthRatio);
                      int l = g.getFontMetrics(g.getFont()).stringWidth(label);
                      g.drawString(label, x - (l/2), 5 + (g.getFont().getSize() * ((currDay++ % 2) + 1)));
                    }
                    g.setFont(f);
                    g.setColor(c);
                    
                    // Current day here?
                    
                    // Moon Phases
                    c = g.getColor();
                    g.setColor(Color.BLUE);
                    for (MoonPhaseValue mpv : moonPhaseList)
                    {
                      String label = mpv.getLabel();
                      int x = (int)(mpv.getX() * widthRatio);
                      g.drawLine(x, 0, x, height);
                      drawMoon((Graphics2D)g, x, height - 15, 12, mpv.getPhase(), label);
                    }                  
                  }
                  // Main curve
                  if (showTideCurve && isMainCurveReady())
                  {
                    if (!decomposeCheckBox.isSelected() || (decomposeCheckBox.isSelected() && showTideCurveCB.isSelected()))
                    {   
  //                  System.out.println("Plotting main curve");
                      g.setColor(Color.RED);            
                      Polygon curvePolygon = new Polygon();
                      for (DataPoint dp : mainCurve)
                      {
                        int x = (int)((dp.getX()) * widthRatio);
                        int y = height - (int)(dp.getY() * heightRatio);
                        if (previous != null)
                          g.drawLine(previous.x, previous.y, x, y);
                        previous = new Point(x, y);                      
                        curvePolygon.addPoint(x, y);
                      }
                      ((Graphics2D) g).setStroke(origStroke);
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
                  }
                }
              }
              if (showAltitudesCheckBox.isSelected() && isAstroReady())
              {
                Polygon curvePolygon = new Polygon();
                // Paint the lower part of the curve, below horizon
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
              int fontSize = 12;
              int _x = 5, _y = (3 * fontSize); // To leave the date readable.
              g.setColor(Color.BLUE);
              //          g.setFont(new Font("Arial", Font.PLAIN, fontSize));
              g.setFont(g.getFont().deriveFont(Font.PLAIN, fontSize));
              f = g.getFont();
              g.setFont(f.deriveFont(Font.BOLD, f.getSize()));
              g.drawString(ts.getFullName(), _x, _y);
              g.setFont(f);
              _y += (fontSize + 2);
              // Station Position and base height
              g.drawString(new GeoPos(ts.getLatitude(), ts.getLongitude()).toString() + " - Base Height : " + DF22.format(Utils.convert(ts.getBaseHeight(), ts.getDisplayUnit(), currentUnit)) + " " + currentUnit, _x, _y);
              _y += (fontSize + 2);
            }
          }
          catch (Exception ex)
          {
      //    JOptionPane.showMessageDialog(this, (ex!=null?ex.toString():"Null Exception..."), "Oops", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
          }
        }
      }
    }
  };
  
  private JTabbedPane tideTabPane = new JTabbedPane();
  
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
  private JButton refreshButton = new JButton();
  
  private JSlider widthSlideBar = new JSlider();
  private JComboBox unitComboBox = new JComboBox();
  
  private CommandPanel chartCommandPanel = new CommandPanel();
  private JTabbedPane tabbedPane = new JTabbedPane();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JPanel topButtonPanel = new JPanel()
  {
    @Override
    public void setEnabled(boolean enabled)
    {
      super.setEnabled(enabled);
      for (Component component : this.getComponents())
        component.setEnabled(enabled);
    }
  };
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
  
  private transient ColoredCoeff[] coeffColor = null;
  private JCheckBox decomposeCheckBox = new JCheckBox();
  private JCheckBox showBaseHeightCheckBox = new JCheckBox();
  private JCheckBox showAltitudesCheckBox = new JCheckBox();

  private transient TideEventListener tideEventListener = null;
  private List<String> coeffToHighlight = null;
  private JPanel bottomPanel = new JPanel();
  private SpecialProgressBar statusIndicator = new SpecialProgressBar(true, true);
  private BorderLayout borderLayout1 = new BorderLayout();

  public TideInternalFrame()
  {
    this(null);
  }

  public TideInternalFrame(final JFrame parent)
  {
    try
    {
//    System.out.println("Loading...");       
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
//    System.out.println("Loaded!");
      SplashWindow.disposeSplash();
      for (TideEventListener tel : TideContext.getInstance().getListeners())
        tel.stopLoad();
      // Last station ?
      try
      {
        Properties prop = new Properties();
        prop.load(new FileReader("tidestation.properties")); 
        String sn = prop.getProperty("tide.station"); // Last displayed station from previous session
        if (sn != null)
        {
//        TideContext.getInstance().getRecentStations().add(sn);
          displayTide(sn);
        }
        boolean loop = true;
        int idx = 0;
        while (loop)
        {
          String s = prop.getProperty("recent." + Integer.toString(++idx));
          if (s == null)
            loop = false;
          else
          {
            if (!TideContext.getInstance().getRecentStations().contains(s))
              TideContext.getInstance().getRecentStations().add(s);
          }
        }
        Utils.shrinkStationList(); // "from constructor");
      } 
      catch (FileNotFoundException fnfe)
      {
        ; // No Problem
      }
      catch (Exception ex) 
      { ex.printStackTrace(); }

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
//      String flavor = "sqlite"; 
//      try { flavor = System.getProperty("tide.flavor", "xml"); } 
//      catch (Exception ignore)
//      {        
//        System.err.println("Are you an Applet? " + ignore.getLocalizedMessage() + " (Ok)");
//      }
//      if (flavor.equals("xml"))
        BackEndTideComputer.connect(); // BackEndTideComputer.XML_OPTION);
//      else if (flavor.equals("serialized"))
//        BackEndTideComputer.connect(BackEndTideComputer.JAVA_SERIALIZED_OPTION);
//      else if (flavor.equals("json"))
//        BackEndTideComputer.connect(BackEndTideComputer.JSON_SERIALIZED_OPTION);
//      else if (flavor.equals("sql"))
//        BackEndTideComputer.connect(BackEndTideComputer.SQL_OPTION);
//      else // sqlite
//        BackEndTideComputer.connect(BackEndTideComputer.SQLITE_OPTION);
    }
    catch (Exception ex)
    {
      if (ex instanceof SQLException)
        JOptionPane.showMessageDialog(this, ex.toString(), "Database Connection", JOptionPane.ERROR_MESSAGE);
      else
        JOptionPane.showMessageDialog(this, ex.toString(), "XML Data", JOptionPane.ERROR_MESSAGE);
    }
    
    tideEventListener = new TideEventListener()
      {
        @Override
        public void stationSelected(String stationName)
        {
          displayTide(stationName);
          Properties prop = new Properties();
          prop.setProperty("tide.station", stationName);
          int idx = 0;
          
          if (!TideContext.getInstance().getRecentStations().contains(stationName))
            TideContext.getInstance().getRecentStations().add(stationName);

          Utils.shrinkStationList(); // "from TideEventListener.stationSelected");
          
          for (String s : TideContext.getInstance().getRecentStations())
            prop.setProperty("recent." + Integer.toString(++idx), s);
          try { prop.store(new FileOutputStream("tidestation.properties"), null); } catch (Exception ex) 
          { ex.printStackTrace(); }
        }
        @Override
        public void setDate(long date)
        {
          if (now.getTime().getTime() != date)
            resetData();
          now.setTime(new Date(date));
          if (graphPanelOneDay.isVisible())
            graphPanelOneDay.repaint();          
        }
        @Override
        public void setCoeffToHighlight(List<String> names) 
        {
          coeffToHighlight = names;
          if (graphPanelOneDay.isVisible())
            graphPanelOneDay.repaint();
          if (graphPanelExtended.isVisible())
            graphPanelExtended.repaint();
        }
        @Override
        public void showAllCurves(boolean b) 
        {
          showAllCurves = b;
          graphPanelOneDay.repaint();
          graphPanelExtended.repaint();
        }

//        public void showTideCurve(boolean b)
//        {
//          showTideCurve = b;
//          graphPanelOneDay.repaint();
//          graphPanelExtended.repaint();
//        }
        
        @Override
        public void setBusy(boolean b)
        {
//        System.out.println("-- EventListener setBusy:" + Boolean.toString(b));
          topButtonPanel.setEnabled(!b);
        }
        
        @Override
        public void setNbStationsSelected(int n) 
        {
          menuFileGoogleSelectedStation.setText(SELECTED_STATIONS_PREFIX + "(" + Integer.toString(n) + ")");
        }
      };
    TideContext.getInstance().addTideListener(tideEventListener);

    this.setJMenuBar( menuBar );
    this.getContentPane().setLayout(mainBorderLayout);
    this.setSize(new Dimension(1272, 684));
    this.setTitle("Oliv's Tide Computer");
    menuFile.setText("File");
    menuFilePrint.setText("Print");
    menuFilePrint.setEnabled(false);
    menuFilePrint.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { filePrint_ActionPerformed( ae ); } } );
    menuFileSearch.setText("Search");
    menuFileSearch.setEnabled(false);
    menuFileSearch.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileSearch_ActionPerformed( ae ); } } );
    menuFileFindClosest.setText("Find closest stations");
    menuFileFindClosest.setEnabled(false);
    menuFileFindClosest.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileFindClosest_ActionPerformed( ae ); } } );
    
    menuFileGoogleMenu.setText("Google Maps");
    
    menuFileGoogleOneStation.setText("Google Map"); // Dynamically modified
    menuFileGoogleOneStation.setEnabled(false);
    menuFileGoogleOneStation.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileGoogle_ActionPerformed( ae ); } } );    
    menuFileGoogleSelectedStation.setText(SELECTED_STATIONS_PREFIX); // Dynamically modified
    menuFileGoogleSelectedStation.setEnabled(true);
    menuFileGoogleSelectedStation.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileGoogle_ActionPerformed( ae ); } } );    
        
    menuFileExit.setText("Exit");
    menuFileExit.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { fileExit_ActionPerformed( ae ); } } );
    
    menuFileRecent.setText("Recent Stations");
    menuClearHistory.setText("Clear History");
    menuClearHistory.addActionListener(new ActionListener() 
      { 
        public void actionPerformed( ActionEvent ae ) 
        { 
          while (TideContext.getInstance().getRecentStations().size() > 0)
            TideContext.getInstance().getRecentStations().remove(0);
          if (ts != null)
            TideContext.getInstance().getRecentStations().add(ts.getFullName());

          Properties prop = new Properties();
          prop.setProperty("tide.station", (ts!=null?ts.getFullName():""));
          int idx = 0;
          for (String s : TideContext.getInstance().getRecentStations())
            prop.setProperty("recent." + Integer.toString(++idx), s);
          try { prop.store(new FileOutputStream("tidestation.properties"), null); } catch (Exception ex) 
          { ex.printStackTrace(); }
        } 
      });
    menuFileRecent.addMenuListener(new MenuListener()
      {
        public void menuSelected(MenuEvent e)
        {
          menuFileRecent.removeAll();
          menuFileRecent.add(menuClearHistory);
          menuFileRecent.add(new JSeparator());
          
          for (int i=TideContext.getInstance().getRecentStations().size(); i>0; i--)
//        for (final String s : TideContext.getInstance().getRecentStations())
          {
            final String s = TideContext.getInstance().getRecentStations().get(i-1);
            JMenuItem jmi = new JMenuItem(s);
            jmi.addActionListener(new ActionListener() 
              { 
                public void actionPerformed( ActionEvent ae ) 
                { 
                  TideContext.getInstance().fireStationSelected(s); 
                } 
              });
            menuFileRecent.add(jmi);
          }
        }

        public void menuDeselected(MenuEvent e)
        {
        }

        public void menuCanceled(MenuEvent e)
        {
        }
      });
    
    menuHelp.setText("Help");
    menuHelpAbout.setText("About");
    menuHelpAbout.addActionListener( new ActionListener() { public void actionPerformed( ActionEvent ae ) { helpAbout_ActionPerformed( ae ); } } );
    menuFile.add( menuFilePrint );
    menuFile.add( menuFileSearch );
    menuFile.add( menuFileFindClosest );
    menuFile.add(menuFileGoogleMenu);
    menuFileGoogleMenu.add( menuFileGoogleOneStation );
    menuFileGoogleMenu.add( menuFileGoogleSelectedStation );
    menuFile.add(new JSeparator());
    menuFile.add( menuFileRecent );
    menuFile.add(new JSeparator());
    menuFile.add( menuFileExit );
    menuBar.add( menuFile );
    menuHelp.add( menuHelpAbout );
    menuBar.add( menuHelp );
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
          if (unitComboBox.hasFocus())
            resetData();
          graphPanelOneDay.repaint();
          graphPanelExtended.repaint();
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
    tzComboBox.addItem(COMPUTER_TIME_ZONE); // ==> "user.timezone"
    tzComboBox.addItem(STATION_TIME_ZONE);
    tzComboBox.addItem(SEPARATOR);
    String[] tz = TimeZone.getAvailableIDs();
    for (int i=0; i<tz.length; i++)
      tzComboBox.addItem(tz[i]);
    tzComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          timeZone2Use = (String)tzComboBox.getSelectedItem();
          if (timeZone2Use.equals(SEPARATOR))
            return;
          if (timeZone2Use.equals(COMPUTER_TIME_ZONE))
          {
            timeZone2Use = System.getProperty("user.timezone");
            tzComboBox.setSelectedItem(timeZone2Use);
          }
          if (timeZone2Use.equals(STATION_TIME_ZONE))
          {
            timeZone2Use = (ts != null?ts.getTimeZone():System.getProperty("user.timezone"));
            tzComboBox.setSelectedItem(timeZone2Use);
          }
          if (tzComboBox.hasFocus())
            resetData();
          graphPanelOneDay.repaint();
          graphPanelExtended.repaint();
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
    decomposeCheckBox.setToolTipText("Show curves for each coefficient");
    decomposeCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          decomposeCheckBox_actionPerformed(e);
        }
      });
    showBaseHeightCheckBox.setText("Base Height");
    showBaseHeightCheckBox.setToolTipText("Show the tide station base height.");
    showBaseHeightCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          graphPanelOneDay.repaint();
          graphPanelExtended.repaint();
        }
      });
    showAltitudesCheckBox.setText("Alt");
    showAltitudesCheckBox.setToolTipText("<html>Show Sun and Moon altitude curves<br><b>Warning:</b><br>This is a demanding operation...</html>");
    showAltitudesCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          graphPanelOneDay.repaint();
          graphPanelExtended.repaint();
        }
      });

    bottomPanel.setLayout(borderLayout1);
    buttonPanel.add(topButtonPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    buttonPanel.add(bottomButtonPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));

    topButtonPanel.add(backOneYearButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(backOneMonthButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(backOneWeekButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(backOneDayButton, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(nowButton, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneDayButton, new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneWeekButton, new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneMonthButton, new GridBagConstraints(10, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));
    topButtonPanel.add(forwardOneYearButton, new GridBagConstraints(11, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 5, 0), 0, 0));

    topButtonPanel.add(refreshButton, new GridBagConstraints(12, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 5, 5, 0), 0, 0));

    topButtonPanel.add(showBaseHeightCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 5), 0, 0));
    topButtonPanel.add(decomposeCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 5), 0, 0));
    topButtonPanel.add(showAltitudesCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 20), 0, 0));
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
    bottomButtonPanel.add(widthSlideBar, new GridBagConstraints(8, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(5, 0, 5, 0), 0, 0));
    bottomButtonPanel.add(findTimeZoneButton,
                          new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(buttonPanel, BorderLayout.NORTH);
    
    tideTabPane.add("One Day", graphPanelOneDay);
    final JScrollPane scrollPane = new JScrollPane(graphPanelExtended);
    JPanel secondPanel = new JPanel(new BorderLayout());
    secondPanel.add(scrollPane, BorderLayout.CENTER);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    JSlider verticalSlider = new JSlider();                    
    verticalSlider.setMinimum(1);
    verticalSlider.setMaximum(50);
    verticalSlider.setValue(1);
    verticalSlider.setToolTipText("<html><center>Graph width<br>same as view port</center></html>");
    verticalSlider.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent evt)
        {
          JSlider slider = (JSlider) evt.getSource();
          if (!slider.getValueIsAdjusting())
          {
            int value = slider.getValue();
//          System.out.println("Slider now " + value);
            if (value == 1)
              slider.setToolTipText("<html><center>Graph width<br>same as view port</center></html>");
            else
              slider.setToolTipText("<html><center>Graph width<br>" + Integer.toString(value) + " times the<br>size of the view port</center></html>");
            if (graphPanelExtended.isVisible())
            {
              int w = graphPanelExtended.getWidth();
              int h = graphPanelExtended.getHeight();
              int vpw = scrollPane.getViewport().getWidth();    
              w = vpw * value;
              graphPanelExtended.setSize(w, h);  
              graphPanelExtended.setPreferredSize(new Dimension(w, h));
              graphPanelExtended.repaint();
            }
          }
        }
      });
    verticalSlider.setOrientation(JSlider.VERTICAL);
    secondPanel.add(verticalSlider, BorderLayout.EAST);
//  tideTabPane.add("Extended", graphPanelExtended);
    tideTabPane.add("Extended", secondPanel);
    widthSlideBar.setEnabled(false);
    displayLabel.setEnabled(false);
    widthSlideBar.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent evt)
        {
          JSlider slider = (JSlider) evt.getSource();
          if (slider.isVisible() && slider.isEnabled() && !slider.getValueIsAdjusting())
          {
            int value = slider.getValue();
//          System.out.println("Slider now " + value);
            slider.setToolTipText("<html>Period width:<br><center>" + Integer.toString(value) + " day" + (value>1?"s":"") + "</center></html>");
            defaultWidth = value;
            if (graphPanelExtended.isVisible())
              graphPanelExtended.repaint();
            if (slider.hasFocus())
              resetData();
          }
        }
      });

    tideTabPane.addChangeListener(new ChangeListener() 
    {
      public void stateChanged(ChangeEvent evt) 
      {
        JTabbedPane pane = (JTabbedPane)evt.getSource();
        int sel = pane.getSelectedIndex();
        widthSlideBar.setEnabled(sel == 1);
        displayLabel.setEnabled(sel == 1);
      }
    });
    rightPanel.add(tideTabPane, BorderLayout.CENTER);
//  rightPanel.add(ct, BorderLayout.EAST);

    //  leftPanel.setPreferredSize(new Dimension(300, 600)); // TEMP
    tabbedPane.add("Tide Curves", rightPanel);
    tabbedPane.add("Tide Stations", chartCommandPanel);

    mainSplitPane.add(leftPanel, JSplitPane.LEFT);
    mainSplitPane.add(tabbedPane, JSplitPane.RIGHT);

    getContentPane().add(mainSplitPane, BorderLayout.CENTER);

    bottomPanel.add(statusIndicator, BorderLayout.EAST);
    statusIndicator.setEnabled(false);
    statusIndicator.setIndeterminate(false);
    this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
//    Thread sdThread = new Thread()
//      {
//        public void run()
//        {
//          try
//          { 
//            System.out.print("Reading Station Data...");
//            List<TideStation> stationData = BackEndTideComputer.getStationData();
//            System.out.println(" Done.");
//            chartCommandPanel.setStationData(stationData);
//            chartCommandPanel.repaint();
//            filterTable.setStationData(stationData);
//          }
//          catch (Exception ex)
//          {
//            ex.printStackTrace();
//          }
//        }
//      };
//    sdThread.start();
    
    ExecutorService es = Executors.newSingleThreadExecutor();
    es.execute(new Runnable()
      {
        public void run()
        {
          try
          { 
            System.out.print("Reading Station Data...");
            List<TideStation> stationData = BackEndTideComputer.getStationData();
            System.out.println(" Done.");
            chartCommandPanel.setStationData(stationData);
            chartCommandPanel.repaint();
            filterTable.setStationData(stationData);
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        }
      });
    es.shutdown();

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
    coeffColor = null;
    if (ts != null)
    {
      coeffColor = new ColoredCoeff[getNumberOfNonNullHarmonics(ts.getHarmonics())] ; // .size() /*constSpeed.size()*/];
      int j = 0;
      for (int i=0; i<ts.getHarmonics().size() /* constSpeed.size() */; i++)
      {
        Harmonic harmonic = ts.getHarmonics().get(i);
        if (harmonic.getAmplitude() != 0d && harmonic.getEpoch() != 0d)
        {
          coeffColor[j] = new ColoredCoeff();
          coeffColor[j].color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
          coeffColor[j].name  = harmonic.getName(); // TideUtilities.getHarmonicCoeffName(ts, constSpeed, i);
          j++;
        }
      }      
    }

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
                    if (!TideContext.getInstance().getRecentStations().contains(stn.getFullStationName()))
                      TideContext.getInstance().getRecentStations().add(stn.getFullStationName());

                    Utils.shrinkStationList(); // "from MouseListener.dblClicked");
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

    graphPanelOneDay.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    buttonPanel.setLayout(gridBagLayout1);
//  buttonPanel.setPreferredSize(new Dimension(764, 66));
//  backOneYearButton.setText("< Y");
    backOneYearButton.setIcon(calLeftImage4);
    backOneYearButton.setBorderPainted(false);
    backOneYearButton.setToolTipText("<html>Back 1 Year<br>" + "</html>");
//  backOneYearButton.setMaximumSize(new Dimension(50, 32));
//  backOneYearButton.setMinimumSize(new Dimension(50, 32));
    backOneYearButton.setPreferredSize(new Dimension(58, 32));
//  backOneYearButton.setMargin(new Insets(1, 1, 1, 1));
    backOneYearButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneYearButton_actionPerformed(e);
        }
      });
//  backOneMonthButton.setText("< M");
    backOneMonthButton.setIcon(calLeftImage3);
    backOneMonthButton.setBorderPainted(false);
    backOneMonthButton.setToolTipText("<html>Back 1 Month<br>" + "</html>");
//  backOneMonthButton.setMargin(new Insets(1, 1, 1, 1));
//  backOneMonthButton.setMaximumSize(new Dimension(50, 32));
//  backOneMonthButton.setMinimumSize(new Dimension(50, 32));
    backOneMonthButton.setPreferredSize(new Dimension(52, 32));
    backOneMonthButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneMonthButton_actionPerformed(e);
        }
      });
//  backOneWeekButton.setText("< W");
    backOneWeekButton.setIcon(calLeftImage2);
    backOneWeekButton.setBorderPainted(false);
    backOneWeekButton.setToolTipText("<html>Back 1 Week<br>" + "</html>");
//  backOneWeekButton.setMargin(new Insets(1, 1, 1, 1));
//  backOneWeekButton.setMaximumSize(new Dimension(50, 32));
//  backOneWeekButton.setMinimumSize(new Dimension(50, 32));
    backOneWeekButton.setPreferredSize(new Dimension(46, 32));
    backOneWeekButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneWeekButton_actionPerformed(e);
        }
      });
//  backOneDayButton.setText("< D");
    backOneDayButton.setIcon(calLeftImage1);
    backOneDayButton.setBorderPainted(false);
    backOneDayButton.setToolTipText("<html>Back 1 Day<br>(Shift: 1 Hour, Ctrl: Scroll)</html>");
//  backOneDayButton.setMargin(new Insets(1, 1, 1, 1));
//  backOneDayButton.setMaximumSize(new Dimension(50, 32));
//  backOneDayButton.setMinimumSize(new Dimension(50, 32));
    backOneDayButton.setPreferredSize(new Dimension(42, 32));
    backOneDayButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          backOneDayButton_actionPerformed(e);
        }
      });
//  forwardOneDayButton.setText("D >");
    forwardOneDayButton.setIcon(calRightImage1);
    forwardOneDayButton.setBorderPainted(false);
    forwardOneDayButton.setToolTipText("<html>Forward 1 Day<br>(Shift: 1 Hour, Ctrl: Scroll)<html>");
//  forwardOneDayButton.setMargin(new Insets(1, 1, 1, 1));
//  forwardOneDayButton.setMaximumSize(new Dimension(50, 32));
//  forwardOneDayButton.setMinimumSize(new Dimension(50, 32));
    forwardOneDayButton.setPreferredSize(new Dimension(42, 32));
    forwardOneDayButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneDayButton_actionPerformed(e);
        }
      });
//  forwardOneWeekButton.setText("W >");
    forwardOneWeekButton.setIcon(calRightImage2);
    forwardOneWeekButton.setBorderPainted(false);
    forwardOneWeekButton.setToolTipText("<html>Forward 1 Week<br>" + "</html>");
//  forwardOneWeekButton.setMargin(new Insets(1, 1, 1, 1));
//  forwardOneWeekButton.setMaximumSize(new Dimension(50, 32));
//  forwardOneWeekButton.setMinimumSize(new Dimension(50, 32));
    forwardOneWeekButton.setPreferredSize(new Dimension(46, 32));
    forwardOneWeekButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneWeekButton_actionPerformed(e);
        }
      });
//  forwardOneMonthButton.setText("M >");
    forwardOneMonthButton.setIcon(calRightImage3);
    forwardOneMonthButton.setBorderPainted(false);
    forwardOneMonthButton.setToolTipText("<html>Forward 1 Month<br>" + "</html>");
//  forwardOneMonthButton.setMargin(new Insets(1, 1, 1, 1));
//  forwardOneMonthButton.setMaximumSize(new Dimension(50, 32));
//  forwardOneMonthButton.setMinimumSize(new Dimension(50, 32));
    forwardOneMonthButton.setPreferredSize(new Dimension(52, 32));
    forwardOneMonthButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneMonthButton_actionPerformed(e);
        }
      });
//  forwardOneYearButton.setText("Y >");
    forwardOneYearButton.setIcon(calRightImage4);
    forwardOneYearButton.setBorderPainted(false);
    forwardOneYearButton.setToolTipText("<html>Forward 1 Year<br>" + "</html>");
//  forwardOneYearButton.setMargin(new Insets(1, 1, 1, 1));
//  forwardOneYearButton.setMaximumSize(new Dimension(50, 32));
//  forwardOneYearButton.setMinimumSize(new Dimension(50, 32));
    forwardOneYearButton.setPreferredSize(new Dimension(58, 32));
    forwardOneYearButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          forwardOneYearButton_actionPerformed(e);
        }
      });
//  nowButton.setText("<html>Now<br>" + "</html>");
    nowButton.setIcon(nowImage);
//  nowButton.setMargin(new Insets(1, 1, 1, 1));
//  nowButton.setMaximumSize(new Dimension(32, 32));
//  nowButton.setMinimumSize(new Dimension(32, 32));
    nowButton.setPreferredSize(new Dimension(32, 32));
    nowButton.setBorderPainted(false);
    nowButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          nowButton_actionPerformed(e);
        }
      });
    updateTooltips(now);
    
    try { refreshButton.setIcon(new ImageIcon(this.getClass().getResource("img/refresh.png"))); } catch (Exception ex) { System.err.println(ex.getLocalizedMessage()); }
    refreshButton.setToolTipText("Refresh");
//  refreshButton.setMargin(new Insets(1, 1, 1, 1));
//  refreshButton.setMaximumSize(new Dimension(32, 32));
//  refreshButton.setMinimumSize(new Dimension(32, 32));
    refreshButton.setPreferredSize(new Dimension(32, 32));
    refreshButton.setBorderPainted(false);
    refreshButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          refreshButton_actionPerformed(e);
        }
      });
    
    widthSlideBar.setMinimumSize(new Dimension(200, 30));
    widthSlideBar.setValue(3);
    widthSlideBar.setMaximum(31);
    widthSlideBar.setMinimum(1);
    widthSlideBar.setSnapToTicks(true);
    widthSlideBar.setMajorTickSpacing(1);
    widthSlideBar.setPaintTicks(true);
    widthSlideBar.setPaintTrack(false);
    Thread refreshThread = new Thread()
      {
        public void run()
        {
          while (true)
          {
            if (graphPanelOneDay.isVisible())
              graphPanelOneDay.repaint();
            String title = FRAME_TITLE + " - System Date:" + LOCAL_DATE_FORMAT.format(now.getTime()) + ", UTC:" + UTC_DATE_FORMAT.format(now.getTime());
            setTitle(title);
            now.add(Calendar.SECOND, 1);
            try { Thread.sleep(1000L); } catch (Exception ex) { ex.printStackTrace(); } 
          }
        }
      };
    refreshThread.start();
//  initphase = false;
  }

  private void resetData()
  {
    topButtonPanel.setEnabled(false);
    // Invalidate data
    graphPanelOneDay.setMainCurveReady(false);
    graphPanelOneDay.setHarmonicsReady(false);
    graphPanelOneDay.setAstroReady(false);
    
    graphPanelExtended.setMainCurveReady(false);
    graphPanelExtended.setHarmonicsReady(false);
    graphPanelExtended.setAstroReady(false);

    // Who called me
    if ("true".equals(System.getProperty("who.called.me")))
    {
      Throwable t = new Throwable(); 
      StackTraceElement[] elements = t.getStackTrace(); 
      System.out.println("----------------------------------");
      for (StackTraceElement ste : elements)
        System.out.println(ste.toString());
      System.out.println("----------------------------------");
    }
    graphPanelOneDay.resetData();
    graphPanelExtended.resetData();
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

  private static int getNumberOfNonNullHarmonics(List<Harmonic> alh)
  {
    int nb = 0;
    for (Harmonic h : alh)
    {
      if (h.getAmplitude() != 0d && h.getEpoch() != 0d)
        nb++;
    }
    return nb;
  }
  
  /**
   * New Tide Station requested
   * @param stationName
   */
  private void displayTide(final String stationName)
  {
    resetData();
    coeffData = null;
    coeffColor = null;
    coeffToHighlight = null;
    if (decomposeCheckBox.isSelected())
    {
      decomposeCheckBox.setSelected(false);
      decomposeCheckBox_actionPerformed(null);
    }
    Thread tideThread = new Thread()
      {
        public void run()
        {
          tideStationName = stationName;
          try 
          { 
            ts = BackEndTideComputer.findTideStation(tideStationName, now.get(Calendar.YEAR));
            currentYear = now.get(Calendar.YEAR);
            if (ts != null)
            {
              menuFilePrint.setEnabled(true);
              menuFileSearch.setEnabled(true);
              menuFileFindClosest.setEnabled(true);
              menuFileGoogleOneStation.setEnabled(true);
              menuFileGoogleOneStation.setText(ts.getFullName());
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
              if (chartCommandPanel != null)
              {
                chartCommandPanel.setStationPosition(new GeoPoint(ts.getLatitude(), ts.getLongitude()));
                if (chartCommandPanel.isVisible())
                  chartCommandPanel.repaint();
              }
            }
            else
            {
              menuFilePrint.setEnabled(false);
              menuFileSearch.setEnabled(false);
              menuFileFindClosest.setEnabled(false);
              menuFileGoogleOneStation.setEnabled(false);
            }
            graphPanelOneDay.repaint();
            graphPanelExtended.repaint();
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
      
      PrintDialog.SpecialPrm sPrm = null;
      if (printDialog.getSpecialBackground())
        sPrm = printDialog.getSpecialBackgroundParameters();
      final PrintDialog.SpecialPrm specialBGPrm = sPrm;
      
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
            
            try
            {
              out.println("<tide station='" + ts.getFullName().replace("'", "&apos;") + "' station-time-zone='" + ts.getTimeZone() + "' print-time-zone='" + tz + "'>");
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
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
                                                  TideForOneMonth.XML_FLAVOR,
                                                  specialBGPrm);
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
          /*  Process p = */ Runtime.getRuntime().exec(cmd);
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
      final int[] weekdays = searchDialog.getWeekDay();
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
            result += ("between " + DF2.format(fromHour) + ":00 and " + DF2.format(toHour) + ":00 (local time)<br>\n");
            result += ("(dates between " + JUST_DATE_FORMAT.format(fromDate.getTime()) + " and " + JUST_DATE_FORMAT.format(toDate.getTime()));
            if (weekdays != null)
            {
              boolean first = true;
              result += ", and week day is ";
              for (int i=0; i<7; i++)
              {
                if (weekdays[i] == 1)
                {
                  if (!first) result += ", ";
                  result += SearchPanel.getDayNames()[i];
                  first = false;
                }
              }
            }
            result += ")\n";
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
              List<TimedValue> timeAL = new ArrayList<TimedValue>(4); 
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
                    boolean good = true;
                    // Week day?
                    if (weekdays != null)
                    {
                      good = false;
                      int day = tv.getCalendar().get(Calendar.DAY_OF_WEEK);
                      if ((day == Calendar.MONDAY && weekdays[SearchPanel.MONDAY] == 1) || 
                          (day == Calendar.TUESDAY && weekdays[SearchPanel.TUESDAY] == 1) ||
                          (day == Calendar.WEDNESDAY && weekdays[SearchPanel.WEDNESDAY] == 1) ||
                          (day == Calendar.THURSDAY && weekdays[SearchPanel.THURSDAY] == 1) ||
                          (day == Calendar.FRIDAY && weekdays[SearchPanel.FRIDAY] == 1) ||
                          (day == Calendar.SATURDAY && weekdays[SearchPanel.SATURDAY] == 1) ||
                          (day == Calendar.SUNDAY && weekdays[SearchPanel.SUNDAY] == 1) )
                        good = true;
                    }
                    if (good)
                    {
                      result += ("<li type='disc'><a href='showDate(" + Long.toString(tv.getCalendar().getTime().getTime()) + ")'>" + SUITABLE_DATE_FORMAT.format(tv.getCalendar().getTime()) + "</a></li>\n");
                      nbDays++;
//                    System.out.println(" ... yes");
                    }
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
              final File tempFile = File.createTempFile("data", ".html");
              tempFile.deleteOnExit();
              BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
              bw.write(result);
              bw.flush();
              bw.close();              
              jEditorPane.setPage(tempFile.toURI().toURL());
              jEditorPane.repaint();
              if (false)
              {
                int resp = JOptionPane.showConfirmDialog(jEditorPane, "Send to browser?", "Search", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);  
                if (resp == JOptionPane.YES_OPTION)
                {
                  Desktop.getDesktop().browse(tempFile.toURI());
                }
              }
              finalList.add(jScrollPane, BorderLayout.CENTER);
              JPanel panel = new JPanel();
              
              JLabel nbDayLabel = new JLabel();
              nbDayLabel.setText(Integer.toString(nbDays) + " day(s).");
              JButton toBrowser = new JButton("Send to browser");
              toBrowser.addActionListener(new ActionListener()
              {
                public void actionPerformed(ActionEvent e)
                {
                  try { Desktop.getDesktop().browse(tempFile.toURI()); } catch (Exception ex) { ex.printStackTrace(); }
                }
              });
              panel.add(nbDayLabel, null);
              panel.add(toBrowser, null);
              finalList.add(panel, BorderLayout.SOUTH);
  
              JOptionPane.showMessageDialog(null, finalList, "Search completed", JOptionPane.PLAIN_MESSAGE);
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
          }
        };
      searchThread.start();
    }
  }
  
  private void fileFindClosest_ActionPerformed(ActionEvent e)
  {
    GeoPos gps = null;
    try { gps = (GeoPos)NMEAContext.getInstance().getCache().get(NMEADataCache.POSITION); } catch (Exception ex) {}
    if (gps == null)
    {
      System.out.println("Using current tide station position");
      gps = new GeoPos(this.ts.getLatitude(), this.ts.getLongitude());
    }
    else
    {
      System.out.println("Finding station from current position " + gps.toString());
    }
    final GeoPoint origin = new GeoPoint(gps.lat, gps.lng);

    /* 
     * A dialog with the position read from the NMEA Cache (if no cache, take the position of the current station)
     * Prompt the user for a radius
     */
    final ClosestStationPanel csp = new ClosestStationPanel(origin.toString());
    
    int resp = JOptionPane.showConfirmDialog(this, csp, "Find closest stations", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      final TideInternalFrame instance = this;
      Thread findClosestStationsThread = new Thread()
        {
          public void run()
          {
            System.out.println("Working...");
            List<TideStation> tideStations = null;
            try { tideStations = BackEndTideComputer.getStationData(); } catch (Exception ex) {}
            if (tideStations != null)
            {
              System.out.println("Found " + Integer.toString(tideStations.size()) + " station(s)");
              List<StationDistance> stationMap = new ArrayList<StationDistance>();
              // Populate
              for (TideStation tideStation : tideStations)
              {
                double dist = GreatCircle.getDistanceInNM(origin, new GeoPoint(tideStation.getLatitude(), tideStation.getLongitude()));
                stationMap.add(new StationDistance(tideStation.getFullName(), dist));
              }               
              // Sort
              Collections.sort(stationMap, new Comparator<StationDistance>()
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
              // Display
              if (false)
              {
                int nbs = 0;
                for (StationDistance tideStation : stationMap)
                {
                  System.out.println(tideStation.getStationName() + " at " + tideStation.getDistance() + " nm");
                  nbs++;
                  if (nbs > csp.getNbStation()) // Limit display
                    break;
                }
              }
              // Populate a table for the user to select one station.
              FoundStationPanel fsp = new FoundStationPanel();
              fsp.setStationData(stationMap, csp.getNbStation());
              int resp = JOptionPane.showConfirmDialog(instance, fsp, "Nearest Stations", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
              if (resp == JOptionPane.OK_OPTION)
              {
                String sName = fsp.getSelectedStation();
                if (sName != null)
                {
                  TideContext.getInstance().fireStationSelected(sName);
                  if (!TideContext.getInstance().getRecentStations().contains(sName))
                    TideContext.getInstance().getRecentStations().add(sName);

                  Utils.shrinkStationList(); // "from findClosestStationsThread dialog");
                }
              }
            }
            else
              System.out.println("Not station found...");
          }
        };
      findClosestStationsThread.start();
    }
  }
  
  private void fileGoogle_ActionPerformed(ActionEvent e)
  {
    // Display the tide for the day, in Google Map
    // Generate googletide.js
    if (e.getActionCommand().startsWith(SELECTED_STATIONS_PREFIX))
    {
      List<TideStation> selected = filterTable.getSelectedStations();
      if (selected.size() == 0)
      {
        JOptionPane.showMessageDialog(this, "No station selected, try again.", "Google Tides", JOptionPane.WARNING_MESSAGE);
        return;
      }
      try
      {
        BufferedWriter bw = new BufferedWriter(new FileWriter("googletide.js"));
        bw.write("var option = 1;\n");
        bw.write("var lat2plot = " + Double.toString(selected.get(0).getLatitude()) + ";\n"); 
        bw.write("var lng2plot = " + Double.toString(selected.get(0).getLongitude()) + ";\n");
        
        bw.write("var stations = new Array\n(\n");      
        int nbl = 0;
        for (TideStation ts : selected)
        {
          if (nbl++ > 0)
            bw.write("  ,\n");
          bw.write("  {name:\"" + ts.getFullName() + "\",\n");
          bw.write("   type:\"" + (ts.isCurrentStation()?"C":"T") + "\",\n");
          bw.write("   latitude:\"" + ts.getLatitude() + "\",\n");
          bw.write("   longitude:\"" + ts.getLongitude() + "\"}\n");
          nbl++;
        }
        bw.write(");\n");
        bw.close();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    else
    {
      try
      {
        BufferedWriter bw = new BufferedWriter(new FileWriter("googletide.js"));
        bw.write("var option = 0;\n");
        bw.write("var lat2plot = " + Double.toString(ts.getLatitude()) + ";\n");
        bw.write("var lng2plot = " + Double.toString(ts.getLongitude()) + ";\n");
        bw.write("var stationName = \"" + ts.getFullName() + "\";\n");
        bw.write("\n");
        List<TideForOneMonth.TimedValue> timeAL = TideForOneMonth.tideForOneDay(now, timeZone2Use, ts.getFullName(), constSpeed, currentUnit);      
        bw.write("var tidedata = new Array\n(\n");
        int nbl = 0;
        for (TideForOneMonth.TimedValue tv : timeAL)
        {
          if (nbl++ > 0)
            bw.write("  ,\n");
          String evtType = tv.getType();
          if (ts.isCurrentStation())
            evtType = evtType.equals("HW")?"Max Flood":"Max Ebb";
          bw.write("  {type:\"" + evtType + "\",\n");
          bw.write("   time:\"" + TideForOneMonth.TF.format(tv.getCalendar().getTime()) + "\",\n");
          bw.write("   height:\"" + TideUtilities.DF22PLUS.format(tv.getValue()) + "\",\n");
          bw.write("   unit:\"" + currentUnit + "\"}\n");
        }
        bw.write(");\n");
        
        bw.close();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    try { coreutilities.Utilities.openInBrowser("googletide.html"); }
    catch (Exception ex)
    { ex.printStackTrace(); }
  }
  
  private void helpAbout_ActionPerformed(ActionEvent e)
  {
    JOptionPane.showMessageDialog(this, new TideFrame_AboutBoxPanel1(), "About", JOptionPane.PLAIN_MESSAGE);
  }
  
  private TideStation findStation(String location, Calendar date) throws Exception
  {
    resetData();
    return BackEndTideComputer.findTideStation(location, date.get(Calendar.YEAR));    
  }

  private void backOneDayButton_actionPerformed(ActionEvent e)
  {
    int quantity = Calendar.DAY_OF_MONTH;
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.SHIFT_MASK) != 0)
      quantity = Calendar.HOUR_OF_DAY;
    if ((modifiers & InputEvent.CTRL_MASK) != 0)
    {
      hourOffset -= 1;
    }
    else
      now.add(quantity, -1);
    if (backOneDayButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }
  private void backOneWeekButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.DAY_OF_MONTH, -7);
    if (backOneWeekButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }
  private void backOneMonthButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.MONTH, -1);
    if (backOneMonthButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }
  private void backOneYearButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.YEAR, -1);
    if (backOneYearButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }

  private void forwardOneDayButton_actionPerformed(ActionEvent e)
  {
    int quantity = Calendar.DAY_OF_MONTH;
    int modifiers = e.getModifiers();
    if ((modifiers & InputEvent.SHIFT_MASK) != 0)
      quantity = Calendar.HOUR_OF_DAY;
    if ((modifiers & InputEvent.CTRL_MASK) != 0)
    {
      hourOffset += 1;
    }
    else
      now.add(quantity, 1);
    if (forwardOneDayButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }
  private void forwardOneWeekButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.DAY_OF_MONTH, 7);
    if (forwardOneWeekButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }
  private void forwardOneMonthButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.MONTH, 1);
    if (forwardOneMonthButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }
  private void forwardOneYearButton_actionPerformed(ActionEvent e)
  {
    now.add(Calendar.YEAR, 1);
    if (forwardOneYearButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }

  private void nowButton_actionPerformed(ActionEvent e)
  {
    now = GregorianCalendar.getInstance();
    hourOffset = 0;
    if (nowButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
    updateTooltips(now);
  }

  private void updateTooltips(Calendar cal)
  {
    Calendar _cal = (Calendar)cal.clone();
    _cal.add(Calendar.YEAR, -1);
    backOneYearButton.setToolTipText("<html><center>Back 1 <b>Year</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "</center></html>");
    _cal = (Calendar)cal.clone();
    _cal.add(Calendar.MONTH, -1);
    backOneMonthButton.setToolTipText("<html><center>Back 1 <b>Month</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "</html></center>");
    _cal = (Calendar)cal.clone();
    _cal.add(Calendar.DAY_OF_MONTH, -7);
    backOneWeekButton.setToolTipText("<html><center>Back 1 <b>Week</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "</html></center>");
    _cal = (Calendar)cal.clone();
    _cal.add(Calendar.DAY_OF_MONTH, -1);
    backOneDayButton.setToolTipText("<html><center>Back 1 <b>Day</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "<br>(Shift: 1 Hour, Ctrl: Scroll)</html></center>");

    Calendar _now = GregorianCalendar.getInstance(TimeZone.getTimeZone(timeZone2Use));
    nowButton.setToolTipText("<html><center>Now<br>" + JUST_DATE_FORMAT.format(_now.getTime()) + "</html></center>");

    _cal = (Calendar)cal.clone();
    _cal.add(Calendar.YEAR, 1);
    forwardOneYearButton.setToolTipText("<html><center>Forward 1 <b>Year</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "</html></center>");
    _cal = (Calendar)cal.clone();
    _cal.add(Calendar.MONTH, 1);
    forwardOneMonthButton.setToolTipText("<html><center>Forward 1 <b>Month</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "</html></center>");
    _cal = (Calendar)cal.clone();
    _cal.add(Calendar.DAY_OF_MONTH, 7);
    forwardOneWeekButton.setToolTipText("<html><center>Forward 1 <b>Week</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "</html></center>");
    _cal = (Calendar)cal.clone();
    _cal.add(Calendar.DAY_OF_MONTH, 1);
    forwardOneDayButton.setToolTipText("<html><center>Forward 1 <b>Day</b><br>" + JUST_DATE_FORMAT.format(_cal.getTime()) + "<br>(Shift: 1 Hour, Ctrl: Scroll)</html></center>");
  }
  
  private void refreshButton_actionPerformed(ActionEvent e)
  {
    if (refreshButton.hasFocus())
      resetData();
    graphPanelOneDay.repaint();
    graphPanelExtended.repaint();
  }
  
  private void this_internalFrameClosed(InternalFrameEvent e)
  {
    chartCommandPanel.removeApplicationListener();        
    // Store position
    Point topLeft = this.getLocation();
    Dimension dim = this.getSize();
    Properties ifProps = new Properties();
    ifProps.put(TOP_LEFT_X_PROP, Integer.toString(topLeft.x));
    ifProps.put(TOP_LEFT_Y_PROP, Integer.toString(topLeft.y));
    ifProps.put(WIDTH_PROP, Integer.toString(dim.width));
    ifProps.put(HEIGHT_PROP, Integer.toString(dim.height));
    try
    {
      PrintWriter pw = new PrintWriter(new File(TIDE_INTERNAL_FRAME_PROP_FILE));
      ifProps.store(pw, "Internal Frame position and dimension");
      pw.close();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    TideContext.getInstance().fireInternalFrameClosed();
    TideContext.getInstance().removeTideListener(tideEventListener);
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

  private void decomposeCheckBox_actionPerformed(ActionEvent e)
  {
    if (!decomposeCheckBox.isSelected())
      showTideCurve = true;
    
    if (decomposeCheckBox.isSelected())
    {
      if (ts != null)
      {
        buildCoeffColor();
      }
      coeffData = new HashMap<ColoredCoeff, String>();
      for (int i=0; i<coeffColor.length /*constSpeed.size()*/ && coeffColor[i] != null; i++)
      {
     // String coeffName = coeffColor[i].name; // TideUtilities.getHarmonicCoeffName(ts, constSpeed, i);
        coeffData.put(coeffColor[i], TideUtilities.getHarmonicCoeffDefinition(coeffColor[i].name)); 
      }
      // Order it, based on TideUtilities.ORDERED_COEFF
      Map<ColoredCoeff, String> tempCoeffData = new LinkedHashMap<ColoredCoeff, String>(coeffData.size());
      for (String s : TideUtilities.getOrderedCoeff())
      {
        Set<ColoredCoeff> keys = coeffData.keySet();
        for (ColoredCoeff cc : keys)
        {
          if (cc.name.equals(s))
          {
            tempCoeffData.put(cc, coeffData.get(cc));
            coeffData.remove(cc);
            break;
          }
        }
      }
      if (coeffData.size() > 0)
      {
        tempCoeffData.putAll(coeffData);
      }
      
      // Create coeff table
      ct = new CoeffTable(tempCoeffData);
      rightPanel.add(ct, BorderLayout.EAST);
    }
    else
    {
      rightPanel.remove(ct);
      coeffData = null;
      ct = null;
      coeffToHighlight = null;
    }
    rightPanel.repaint();
  }

  private void buildCoeffColor()
  {
    int nnSize = getNumberOfNonNullHarmonics(ts.getHarmonics());
    coeffColor = new ColoredCoeff[nnSize]; // .size() /*constSpeed.size()*/]; // Pas terrible... Les non nulls seulement.
    int j = 0;
    for (int i=0; i<ts.getHarmonics().size() /* constSpeed.size() */; i++)
    {
      Harmonic harmonic = ts.getHarmonics().get(i);
      if (harmonic.getAmplitude() != 0d && harmonic.getEpoch() != 0d)
      {
        coeffColor[j] = new ColoredCoeff();
        coeffColor[j].color = new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
        coeffColor[j].name  = harmonic.getName(); // TideUtilities.getHarmonicCoeffName(ts, constSpeed, i);
        j++;
      }
    }
  }
    
  private int nbBusyThread = 0;
  private Map<Integer, String> busyMap = null;
  
  private synchronized int lookBusy(final String label)
  {
    if (nbBusyThread == 0)
      busyMap = new Hashtable<Integer, String>();
   
    if (false)
    {
      Thread t = new Thread()
        {
          public void run()
          {
            synchronized (statusIndicator)
            {
              statusIndicator.setString(label);
              statusIndicator.setStringPainted(true);
              statusIndicator.setEnabled(true);
              statusIndicator.setIndeterminate(true);

              statusIndicator.repaint();
            }
          }
        };
      t.run();
    }
    else
    {
      SwingWorker swingWorker = new SwingWorker()
        {
          protected Object doInBackground()
          {
            synchronized (statusIndicator)
            {
              statusIndicator.setString(label);
              statusIndicator.setStringPainted(true);
              statusIndicator.setEnabled(true);
              statusIndicator.setIndeterminate(true);
              statusIndicator.repaint();
            }
            return null;
          }
        };
      TideContext.getInstance().fireSetBusy(true);
//    System.out.println("... Locking");
      swingWorker.execute();
    }
    nbBusyThread++;
    busyMap.put(new Integer(nbBusyThread), label);
    return nbBusyThread;
  }
  
  private synchronized void coolDown(int i)
  {
    nbBusyThread--;
    busyMap.remove(new Integer(i));
    if (nbBusyThread == 0)
    {
      synchronized (statusIndicator)
      {
        statusIndicator.setString("");
        statusIndicator.setStringPainted(false);
        statusIndicator.setEnabled(false);
        statusIndicator.setIndeterminate(false);
        statusIndicator.repaint();
        // Broadcast release
        TideContext.getInstance().fireSetBusy(false);
//      System.out.println("... Releasing");
      }
    }
    else
    {
//    System.out.println(Integer.toString(nbBusyThread) + " busy thread(s) remaining...");
      synchronized (statusIndicator)
      {
        Integer firstKey = null;
        for (Integer k : busyMap.keySet())
        {
          firstKey = k;
          break;
        }
        String newLabel = "";
        try { newLabel = busyMap.get(firstKey); } catch (Exception ex) { System.err.println(ex.getLocalizedMessage()); }
//      System.out.println("  -> New Label: [" + newLabel + "]");
        statusIndicator.setString(newLabel);
        statusIndicator.repaint();
      }
    }
  }
  
  private synchronized void updateBusyLook(String label, int k)
  {
    synchronized (statusIndicator)
    {
      busyMap.put(new Integer(k), label);
      statusIndicator.setString(label);
      statusIndicator.repaint();
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

  private static class StationTreeCellRenderer extends DefaultTreeCellRenderer 
  {
    @SuppressWarnings("compatibility:-8216068910265867686")
    private final static long serialVersionUID = 1L;
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
      
  private static class TidePanel extends JPanel implements MouseMotionListener, MouseListener
  {
    @SuppressWarnings("compatibility:4053780883260119913")
    private final static long serialVersionUID = 1L;
    public final static int RISING  =  1;
    public final static int FALLING = -1;
    
    protected transient List<DataPoint> mainCurve = null;
    protected transient Hashtable<String, List<DataPoint>> harmonicCurves = null;    
    protected transient List<DataPoint> sunAltitudes  = null;
    protected transient List<DataPoint> moonAltitudes = null;    
    
    protected boolean mainCurveReady = false;
    protected boolean harmonicsReady = false;
    protected boolean astroReady     = false;

    protected JCheckBox showTideCurveCB = new JCheckBox("Show tide curve");
      
    public TidePanel()
    {
      super();
      addMouseMotionListener(this);  
      addMouseListener(this);
      init();
    }
    
    protected void init()
    {      
      this.setLayout(null); // Mandatory for the setBounds.
      showTideCurveCB.setHorizontalTextPosition(SwingConstants.LEADING);
      showTideCurveCB.setForeground(Color.BLUE);
      showTideCurveCB.setSelected(true);
      showTideCurveCB.setOpaque(false);
      showTideCurveCB.setVisible(false);
      showTideCurveCB.setBounds(10, 10, 150, 30);
      this.add(showTideCurveCB);
      showTideCurveCB.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            repaint();
          }
        });
    }
    
    public void resetData()
    {
//    Thread.currentThread().dumpStack();      
      if (mainCurve != null) synchronized (mainCurve) { mainCurve = null; } 
      if (harmonicCurves != null) synchronized (harmonicCurves) { harmonicCurves = null; }
      if (sunAltitudes != null) synchronized (sunAltitudes) { sunAltitudes = null; }
      if (moonAltitudes != null) synchronized (moonAltitudes) { moonAltitudes = null; }
      mainCurveReady = false;
      harmonicsReady = false; 
      astroReady = false; 
    }
    
    public void mouseMoved(MouseEvent e)
    {
      System.out.println("Mouse Moved: x=" + e.getX() + ", y=" + e.getY());
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mouseDragged(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    protected void setMainCurveReady(boolean mainCurveReady)
    {
      this.mainCurveReady = mainCurveReady;
    }

    protected boolean isMainCurveReady()
    {
      return mainCurveReady;
    }

    protected void setHarmonicsReady(boolean harmonicsReady)
    {
      this.harmonicsReady = harmonicsReady;
    }

    protected boolean isHarmonicsReady()
    {
      return harmonicsReady;
    }

    protected void setAstroReady(boolean astroReady)
    {
      this.astroReady = astroReady;
    }

    protected boolean isAstroReady()
    {
      return astroReady;
    }
  }  
  
  public class ColoredCoeff
  {
    public String name = "";
    public Color color = Color.WHITE;
    
    public String toString()
    { return name; }
  }
  
  public class DataPoint
  {
    private double x, y;
    
    public DataPoint(double x, double y)
    {
      this.x = x;
      this.y = y;
    }

    public double getX()
    {
      return x;
    }

    public double getY()
    {
      return y;
    }
  }

  public class LabeledValue
  {
    private double x;
    private String label;

    public LabeledValue(double x, String label)
    {
      this.x = x;
      this.label = label;
    }
    
    public double getX()
    {
      return x;
    }

    public String getLabel()
    {
      return label;
    }
  }
  
  public class MoonPhaseValue
  {
    private double x, phase;
    private String label;

    public MoonPhaseValue(double x, double phase, String label)
    {
      this.x = x;
      this.phase = phase;
      this.label = label;
    }
    
    public double getX()
    {
      return x;
    }

    public double getPhase()
    {
      return phase;
    }

    public String getLabel()
    {
      return label;
    }
  }
  
  public class DayNight
  {
    private double x1, x2;
    private GradientPaint gradient;

    public DayNight(double x1, double x2, GradientPaint gradient)
    {
      this.x1 = x1;
      this.x2 = x2;
      this.gradient = gradient;
    }
    
    public double getX1()
    {
      return x1;
    }

    public double getX2()
    {
      return x2;
    }

    public GradientPaint getGradient()
    {
      return gradient;
    }
  }
  
  public static class StationDistance
  {
    private String stationName = "";
    private double distance = 0D;
    
    public StationDistance(String name, double d)
    {
      this.stationName = name;
      this.distance = d;
    }

    public void setStationName(String stationName)
    {
      this.stationName = stationName;
    }

    public String getStationName()
    {
      return stationName;
    }

    public void setDistance(double distance)
    {
      this.distance = distance;
    }

    public double getDistance()
    {
      return distance;
    }
  }
  
  public static void main1(String[] args)
  {
    String str = "Sun Rise :Thu 01-Dec-2011 07:09 (PST) Z:117, Set:Thu 01-Dec-2011 16:47 (PST) Z:243 - daylight:09:38";
    Pattern pattern = Pattern.compile("\\d{2}:\\d{2}");
    Matcher matcher = pattern.matcher(str);
    boolean found = matcher.find();
    while (found)
    {
      String match = matcher.group();
      int start = matcher.start();
      int end   = matcher.end();
      System.out.println("Match: [" + match + "] : " + start + ", " + end);
      found = matcher.find();
    }
  }
}