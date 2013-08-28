package tideengineimplementation.charts;


import astro.calc.GeoPoint;

import chart.components.ui.ChartPanel;
import chart.components.ui.ChartPanelParentInterface_II;
import chart.components.util.World;

import coreutilities.Utilities;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.util.Calendar;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import nmea.server.ctx.NMEAContext;
import nmea.server.ctx.NMEADataCache;

import ocss.nmea.parser.GeoPos;

import tideengine.TideStation;

import tideengineimplementation.gui.TideInternalFrame;
import tideengineimplementation.gui.TideInternalFrame.StationDistance;
import tideengineimplementation.gui.ctx.TideContext;
import tideengineimplementation.gui.ctx.TideEventListener;

import tideengineimplementation.utils.AstroComputer;
import tideengineimplementation.utils.Utils;

import user.util.GeomUtil;


public class CommandPanel 
     extends JPanel
  implements ChartPanelParentInterface_II
{
  @SuppressWarnings("compatibility:3601482022102225908")
  public final static long serialVersionUID = 1L;
  
  private final static Color DARK_RED = new Color(108, 0, 0);

  private final static int PROJECTION = ChartPanel.ANAXIMANDRE; // ChartPanel.GLOBE_VIEW;  ;0)
//private final static double NORTH_LAT  =  83D;
//private final static double SOUTH_LAT  = -80D;
  private final static double NORTH_LAT  =  90D;
  private final static double SOUTH_LAT  = -90D;
  private final static double WEST_LONG  = -180D;
  private final static double EAST_LONG  =  180D;
    
  private String stationFilterPattern = "";
  
  private BorderLayout borderLayout1;
  private JScrollPane jScrollPane1;
  private ChartPanel chartPanel;
  private JPanel bottomPanel;
  private JButton zoomInButton;
  private JButton zoomOutButton;
  private JButton resetZoomButton;  
  private JCheckBox mouseCheckBox;
  private JCheckBox showStationsCheckBox;
  private JCheckBox withNameCheckBox;
  private JCheckBox withAstroCheckBox;
  private JCheckBox withWanderingBodiesCheckBox;
  private JRadioButton sunLightRadioButton;
  private JRadioButton moonLightRadioButton;
  private ButtonGroup lightGroup = new ButtonGroup();
  
  private transient List<TideStation> stationData = null;
  
  private double sunD    = 0d;
  private double sunGHA  = 0d;
  private double moonD   = 0d;
  private double moonGHA = 0d;
  private Calendar currentDate = null;
  
  private transient Image moonSymbol    = new ImageIcon(TideInternalFrame.class.getResource("moon.png")).getImage();
  private transient Image sunSymbol     = new ImageIcon(TideInternalFrame.class.getResource("sun.png")).getImage();
  private transient Image venusSymbol   = new ImageIcon(TideInternalFrame.class.getResource("venus.png")).getImage();
  private transient Image marsSymbol    = new ImageIcon(TideInternalFrame.class.getResource("mars.png")).getImage();
  private transient Image jupiterSymbol = new ImageIcon(TideInternalFrame.class.getResource("jupiter.png")).getImage();
  private transient Image saturnSymbol  = new ImageIcon(TideInternalFrame.class.getResource("saturn.png")).getImage();
  private transient Image ariesSymbol   = new ImageIcon(TideInternalFrame.class.getResource("aries.png")).getImage();

  private transient ImageIcon zoomInImage  = new ImageIcon(this.getClass().getResource("zoomexpand.gif"));
  private transient ImageIcon zoomOutImage = new ImageIcon(this.getClass().getResource("zoomshrink.gif"));
  private transient ImageIcon refreshImage = new ImageIcon(this.getClass().getResource("refresh.png"));
  private transient ImageIcon pushPinImage = new ImageIcon(this.getClass().getResource("pushpin_25x25.gif"));
  
  private transient ImageIcon bluePushPinImage = new ImageIcon(this.getClass().getResource("bluepushpin.png"));

  private transient TideEventListener tel = null;
  private transient AstroPingThread astroPingThread = null;
  
  private String dateStr = null;

  public CommandPanel()
  {
    borderLayout1 = new BorderLayout();
    jScrollPane1 = new JScrollPane();
    chartPanel = new ChartPanel(this);
    chartPanel.setProjection(PROJECTION);
    bottomPanel = new JPanel();
    zoomInButton = new JButton();
    zoomOutButton = new JButton();
    resetZoomButton = new JButton();
    mouseCheckBox = new JCheckBox("Grab-Scroll");
    mouseCheckBox.setSelected(false);
    showStationsCheckBox = new JCheckBox("Show Stations");
    showStationsCheckBox.setSelected(true);
    withNameCheckBox = new JCheckBox("With Station Names");
    withNameCheckBox.setSelected(false);
    withAstroCheckBox = new JCheckBox("With Astro Data");
    withAstroCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          dateStr = null;
        }
      });
    
    withAstroCheckBox.setSelected(false);
    withWanderingBodiesCheckBox = new JCheckBox("Wandering Bodies");
    withWanderingBodiesCheckBox.setSelected(true);
    
    sunLightRadioButton = new JRadioButton("Sun Light");
    sunLightRadioButton.setSelected(true);
    moonLightRadioButton = new JRadioButton("Moon Light");
    moonLightRadioButton.setSelected(false);
    
    lightGroup.add(sunLightRadioButton);
    lightGroup.add(moonLightRadioButton);
    
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
        throws Exception
  {
    tel = new TideEventListener()
      {
        @Override
        public void filterList(String pattern)
        {
          stationFilterPattern = pattern;
          if (chartPanel.isVisible())
            repaint();
        }
        
        @Override
        public void timePing()
        {
          if (false && withAstroCheckBox.isSelected())
          {
            repaint();            
          }
        }
      };
    TideContext.getInstance().addTideListener(tel);
    
    setLayout(borderLayout1);
    zoomInButton.setToolTipText("Zoom In");
    zoomInButton.setIcon(zoomInImage);
    zoomInButton.setPreferredSize(new Dimension(24, 24));
    zoomInButton.setBorderPainted(false);
    zoomInButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        jButton1_actionPerformed(e);
      }

    });
    zoomOutButton.setToolTipText("Zoom Out");
    zoomOutButton.setIcon(zoomOutImage);
    zoomOutButton.setPreferredSize(new Dimension(24, 24));
    zoomOutButton.setBorderPainted(false);
    zoomOutButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        jButton2_actionPerformed(e);
      }

    });
    resetZoomButton.setToolTipText("Reset Zoom");
    resetZoomButton.setIcon(refreshImage);
    resetZoomButton.setPreferredSize(new Dimension(24, 24));
    resetZoomButton.setBorderPainted(false);
    
    resetZoomButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        jButton3_actionPerformed(e);
      }

    });
    
    showStationsCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          withNameCheckBox.setEnabled(showStationsCheckBox.isSelected());
          chartPanel.repaint();
        }
      });
    mouseCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          swicthMouse(mouseCheckBox.isSelected());
        }
      });
    withNameCheckBox.addActionListener(new ActionListener()
                                       {
                                         public void actionPerformed(ActionEvent e)
                                         {
                                           chartPanel.repaint();
                                         }
                                       });
    withAstroCheckBox.addActionListener(new ActionListener()
                                       {
                                         public void actionPerformed(ActionEvent e)
                                         {
                                           chartPanel.repaint();
                                         }
                                       });
    withWanderingBodiesCheckBox.addActionListener(new ActionListener()
                                       {
                                         public void actionPerformed(ActionEvent e)
                                         {
                                           chartPanel.repaint();
                                         }
                                       });
    sunLightRadioButton.addActionListener(new ActionListener()
                                       {
                                         public void actionPerformed(ActionEvent e)
                                         {
                                           chartPanel.repaint();
                                         }
                                       });
    moonLightRadioButton.addActionListener(new ActionListener()
                                       {
                                         public void actionPerformed(ActionEvent e)
                                         {
                                           chartPanel.repaint();
                                         }
                                       });
    
    jScrollPane1.getViewport().add(chartPanel, null);
    add(jScrollPane1, BorderLayout.CENTER);
    bottomPanel.add(showStationsCheckBox, null);
    bottomPanel.add(sunLightRadioButton, null);
    bottomPanel.add(moonLightRadioButton, null);
    bottomPanel.add(zoomInButton, null);
    bottomPanel.add(zoomOutButton, null);
    bottomPanel.add(resetZoomButton, null);
    bottomPanel.add(mouseCheckBox, null);
    bottomPanel.add(withNameCheckBox, null);
    bottomPanel.add(withAstroCheckBox, null);
    bottomPanel.add(withWanderingBodiesCheckBox, null);
    add(bottomPanel, BorderLayout.SOUTH);
    double nLat  = NORTH_LAT;
    double sLat  = SOUTH_LAT;
    double wLong = WEST_LONG;
    double eLong = EAST_LONG;
 // chartPanel.calculateEastG(nLat, sLat, wLong);
    chartPanel.setEastG(eLong);
    chartPanel.setWestG(wLong);
    chartPanel.setNorthL(nLat);
    chartPanel.setSouthL(sLat);
    
    chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
    
    chartPanel.setHorizontalGridInterval(10D);
    chartPanel.setVerticalGridInterval(10D);
    chartPanel.setWithScale(false);
    chartPanel.setGridColor(Color.lightGray);
    // GrabScroll - DDZoom
    chartPanel.setMouseDraggedEnabled(true); // mouseCheckBox.isSelected());
//  chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_GRAB_SCROLL);
    chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    chartPanel.setPositionToolTipEnabled(true);
  }
  
  public void removeApplicationListener()
  {
    TideContext.getInstance().removeTideListener(tel);
  }
  
  private void jButton1_actionPerformed(ActionEvent e)
  {
    chartPanel.zoomIn();
  }

  private void jButton2_actionPerformed(ActionEvent e)
  {
    chartPanel.zoomOut();
  }

  private void jButton3_actionPerformed(ActionEvent e)
  {
    double nLat  = NORTH_LAT;
    double sLat  = SOUTH_LAT;
    double wLong = WEST_LONG;
    double eLong = EAST_LONG;
    chartPanel.setEastG(eLong);
    chartPanel.setWestG(wLong);
    chartPanel.setNorthL(nLat);
    chartPanel.setSouthL(sLat);

    chartPanel.setH(this.getHeight());
    chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
    
    chartPanel.repaint();
  }

  private void swicthMouse(boolean b)
  {
//  chartPanel.setMouseDraggedEnabled(b);
    chartPanel.setMouseDraggedType(b?ChartPanel.MOUSE_DRAG_GRAB_SCROLL:ChartPanel.MOUSE_DRAG_ZOOM);
  }
  
  GeoPoint from = null;
  GeoPoint to   = null;
  
  GeoPoint stationPosition = null;

  private transient Image blue = new ImageIcon(TideInternalFrame.class.getResource("img/bullet_ball_glass_blue.png")).getImage();
  private transient Image red  = new ImageIcon(TideInternalFrame.class.getResource("img/bullet_ball_glass_red.png")).getImage();
  
  @Override
  public void chartPanelPaintComponent(Graphics gr)
  {
    Graphics2D g2d = null;
    if (gr instanceof Graphics2D)
      g2d = (Graphics2D)gr;
    World.paintChart(null, chartPanel, g2d, Color.orange);
    World.drawChart(chartPanel, gr);

    gr.setColor(Color.red);
    if (stationData != null)
    {
      Pattern pattern = null;
      if (stationFilterPattern.trim().length() > 0)
      {
        String patternStr = ".*" + stationFilterPattern + ".*";
        pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);        
      }
      
      if (showStationsCheckBox.isSelected())
      {
        for (TideStation sd : stationData)
        {
          boolean go = true;
          if (pattern != null)
          {
            Matcher m = pattern.matcher(sd.getFullName());
            go = m.matches();          
          }
          if (go)
          {
            GeoPoint p =new GeoPoint(sd.getLatitude(), sd.getLongitude());
            Point pt = chartPanel.getPanelPoint(p);
            Image img = null;
            if (sd.isCurrentStation())
  //          gr.setColor(Color.blue);
              img = red;   // this matches the tree
            if (sd.isTideStation())
  //          gr.setColor(Color.red);
              img = blue;  // this matches the tree
  //        gr.drawOval(pt.x-2,pt.y-2, 4, 4);
            gr.drawImage(img, pt.x-8, pt.y-8, null);
            if (withNameCheckBox.isSelected())
              gr.drawString(sd.getFullName(), pt.x + 2, pt.y);
          }
        }
      }
    }
  }

  private void plotBody(Graphics gr, String name, double decl, double gha)
  {
    plotBody(gr, name, decl, gha, null);  
  }
  
  private void plotBody(Graphics gr, String name, double decl, double gha, Image img)
  {
    double longitude = 0;
    if (gha < 180)
      longitude = -gha;
    if (gha >= 180)
      longitude = 360 - gha;
    GeoPoint p = new GeoPoint(decl, longitude);
//  System.out.println(name + ":" + decl + " " + _gha);
    Point pt = chartPanel.getPanelPoint(p);
    if (img == null)
    {
      gr.fillOval(pt.x-2, pt.y-2, 4, 4);
      gr.drawString(name, pt.x + 2, pt.y);    
    }
    else
    {
      boolean gloss = true;
      if (gloss)
      {
        int radius = 10;
        Graphics2D g2d = (Graphics2D)gr;
        Point center = pt;
        boolean shadow = false;
        if (shadow)
        {
          Color bgColor = this.getBackground();
          Point shadowCenter = new Point((int)(center.x + (radius / 3)), (int)(center.y + (radius / 3)));
          RadialGradientPaint rgp = new RadialGradientPaint(shadowCenter, 
                                                            (int)(radius * 1.0), 
                                                            new float[] {0f, 0.9f, 1f}, 
                                                            new Color[] {Color.white, Color.gray, bgColor});
          g2d.setPaint(rgp);
          g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        Color lightColor = Color.lightGray;
        Color darkColor = Color.gray; 
        drawGlossyCircularBall(g2d, center, radius, lightColor, darkColor, 1f);
        
        if ("Aries".equals(name))
          gr.drawImage(img, pt.x - 5, pt.y - 5, null); // 10x10
        else
          gr.drawImage(img, pt.x - 7, pt.y - 7, null); // Image is 13x13
      }
      else
      {
        gr.fillOval(pt.x-8, pt.y-8, 16, 16);
        gr.drawImage(img, pt.x - 7, pt.y - 7, null); // Image is 13x13
      }
    }
  }
  
  private void drawEcliptic(Graphics gr, double ariesGHA, double eclipticObliquity)
  {
    double longitude = 0;
    if (ariesGHA < 180)
      longitude = -ariesGHA;
    if (ariesGHA >= 180)
      longitude = 360 - ariesGHA;
    
    longitude += 90d; // Extremum
    while (longitude > 360)
      longitude -= 360;
    GeoPoint p = new GeoPoint(eclipticObliquity, longitude);
    GeoPoint eclCenter = deadReckoning(p, 90 * 60, 0); // Position of the center of the Ecliptic 

    gr.setColor(Color.blue);
    for (int i=0; i<360; i++)
    {
      GeoPoint gp = deadReckoning(eclCenter, 90 * 60, i);
      double lng = gp.getG();
      if (lng < -180)
        lng += 360;
      if (lng > 180)
        lng -= 360;
      // Just plot
      Point pp = chartPanel.getPanelPoint(new GeoPoint(gp.getL(), lng));
      gr.fillOval(pp.x, pp.y, 1, 1);
    }
  }

  private static void drawGlossyCircularBall(Graphics2D g2d, Point center, int radius, Color lightColor, Color darkColor, float transparency)
  {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
    g2d.setPaint(null);

    g2d.setColor(darkColor);
    g2d.fillOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius);

    Point gradientOrigin = new Point(center.x - radius,
                                     center.y - radius);
    GradientPaint gradient = new GradientPaint(gradientOrigin.x, 
                                               gradientOrigin.y, 
                                               lightColor, 
                                               gradientOrigin.x, 
                                               gradientOrigin.y + (2 * radius / 3), 
                                               darkColor); // vertical, light on top
    g2d.setPaint(gradient);
    g2d.fillOval((int)(center.x - (radius * 0.90)), 
                 (int)(center.y - (radius * 0.95)), 
                 (int)(2 * radius * 0.9), 
                 (int)(2 * radius * 0.95));
  }
  
  private String tooltipMess = null;
  private boolean replaceTooltip = false;
                  
  public boolean onEvent(EventObject e, int type)
  {
    if (type == ChartPanel.MOUSE_MOVED)
    {
      // Closets station
      MouseEvent me = (MouseEvent)e;
      int x = me.getX();
      int y = me.getY();
      GeoPoint gp = chartPanel.getGeoPos(x, y);
      StationDistance closestStation = Utils.findClosestStation(gp, 100d);
      if (closestStation != null)
      {
        tooltipMess = closestStation.getStationName();
        replaceTooltip = true;
        System.out.println("Closest:" + closestStation.getStationName() + ", at " + closestStation.getDistance() + " nm.");
      }
      else
      {
        tooltipMess = null;
        replaceTooltip = false;
      }
    }
    return true;
  }

  public String getMessForTooltip()
  {
    return tooltipMess;
  }

  public boolean replaceMessForTooltip()
  {
    return replaceTooltip;
  }

  public void videoCompleted() {}
  public void videoFrameCompleted(Graphics g, Point p) {}

  public void zoomFactorHasChanged(double d)
  {
  }

  public void chartDDZ(double top, double bottom, double left, double right)
  {
  }

  public void setStationData(List<TideStation> stationData)
  {
    this.stationData = stationData;
  }

  public void setSunGHA(double sunGHA)
  {
    this.sunGHA = sunGHA;
  }

  public void setMoonGHA(double moonGHA)
  {
    this.moonGHA = moonGHA;
  }

  private void setFrom(GeoPoint from)
  {
    this.from = from;
  }

  public void setSunD(double sunD)
  {
    this.sunD = sunD;
  }

  public void setMoonD(double moonD)
  {
    this.moonD = moonD;
  }
  
  public void setCurrentDate(Calendar cal)
  {
    this.currentDate = cal;
  }

  @Override
  public void chartPanelPaintComponentAfter(Graphics gr)
  {
    // Sun and Moon, Planets 
    if (withWanderingBodiesCheckBox.isSelected() && sunD != 0 && sunGHA != 0 && moonD != 0 && moonGHA != 0)
    {
      gr.setColor(Color.LIGHT_GRAY);
      plotBody(gr, "Sun",  sunD,  sunGHA, sunSymbol);
      plotBody(gr, "Moon", moonD, moonGHA, moonSymbol);

      plotBody(gr, "Aries",  0d,  AstroComputer.getAriesGHA(),  ariesSymbol);

      plotBody(gr, "Venus",   AstroComputer.getVenusDecl(),   AstroComputer.getVenusGHA(),   venusSymbol);
      plotBody(gr, "Mars",    AstroComputer.getMarsDecl(),    AstroComputer.getMarsGHA(),    marsSymbol);
      plotBody(gr, "Jupiter", AstroComputer.getJupiterDecl(), AstroComputer.getJupiterGHA(), jupiterSymbol);
      plotBody(gr, "Saturn",  AstroComputer.getSaturnDecl(),  AstroComputer.getSaturnGHA(),  saturnSymbol);
      
      // Day/Night limit
      double dayCenterLongitude = 0;
      double dayCenterLatitude = 0;
      if (sunLightRadioButton.isSelected())
      {
        if (sunGHA < 180)
          dayCenterLongitude = -sunGHA;
        if (sunGHA >= 180)
          dayCenterLongitude = 360 - sunGHA;
        dayCenterLatitude = sunD;
      }
      if (moonLightRadioButton.isSelected())
      {
        if (moonGHA < 180)
          dayCenterLongitude = -moonGHA;
        if (moonGHA >= 180)
          dayCenterLongitude = 360 - moonGHA;
        dayCenterLatitude = moonD;
      }
      
      GeoPoint sunPos = new GeoPoint(dayCenterLatitude, dayCenterLongitude);
//    System.out.println("Sun Position:" + sunPos.toString());
      Map<Double, Double> nightMap = new HashMap<Double, Double>();
//    int leftLng = (int)Math.round(chartPanel.getWestG());
      for (int i=0; i<360; i++)
      {
        GeoPoint gp = deadReckoning(sunPos, 90 * 60, i);
        double lng = gp.getG();
        if (lng < -180)
          lng += 360;
        if (lng > 180)
          lng -= 360;
        nightMap.put(lng, gp.getL());
      }
      SortedSet<Double> sortedLng= new TreeSet<Double>(nightMap.keySet());
      int arraySize = sortedLng.size();
      Polygon night = new Polygon();
      for (Double d : sortedLng)
      {
        double lat = nightMap.get(d).doubleValue();
        Point pp = chartPanel.getPanelPoint(new GeoPoint(lat, d));
        night.addPoint(pp.x, pp.y);
      }
      night.addPoint(chartPanel.getW(), night.ypoints[arraySize - 1]);
      if (dayCenterLatitude > 0) // Then night is south
      {
        night.addPoint(chartPanel.getW(), chartPanel.getH());  
        night.addPoint(0, chartPanel.getH());  
      }
      else // Night is north
      {
        night.addPoint(chartPanel.getW(), 0);  
        night.addPoint(0, 0);          
      }
      night.addPoint(0, night.ypoints[0]);
      // Now fill the night polygon
      gr.setColor(Color.darkGray);
      ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
       gr.fillPolygon(night);
      ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      
      // Ecliptic
      double ghaAries = AstroComputer.getAriesGHA();
      double meanOblEcl = AstroComputer.getMeanObliquityOfEcliptic();
      drawEcliptic(gr, ghaAries, meanOblEcl);
    }
    
    GeoPos gps = null;    
    try { gps = (GeoPos)NMEAContext.getInstance().getCache().get(NMEADataCache.POSITION); } catch (Exception ex) {}
    if (gps != null)
    {
      Point gpsPt = chartPanel.getPanelPoint(gps.lat, gps.lng);
      if (true)
        gr.drawImage(pushPinImage.getImage(), gpsPt.x - 25, gpsPt.y - pushPinImage.getImage().getHeight(null), null);
      else
      {
        gr.setColor(Color.blue);
        gr.fillOval(gpsPt.x - 3, gpsPt.y - 3,  6,  6);
        gr.drawOval(gpsPt.x - 5, gpsPt.y - 5, 10, 10);
      }
    }
    
    if (this.stationPosition != null)
    {
      Point stationPt = chartPanel.getPanelPoint(stationPosition.getL(), stationPosition.getG());
      gr.drawImage(bluePushPinImage.getImage(), stationPt.x - 10, stationPt.y - bluePushPinImage.getImage().getHeight(null), null);
    }
      
    if (withAstroCheckBox.isSelected())
    {
//      if (astroPingThread == null)
//      {
//        astroPingThread = new AstroPingThread(chartPanel);
//        astroPingThread.start();
//      }
      // Drawing astro data
      if (sunD != 0 && sunGHA != 0 && moonD != 0 && moonGHA != 0)
      {
        gr.setColor(DARK_RED); // Color.blue);
        Font f = gr.getFont();
        gr.setFont(f.deriveFont(Font.BOLD));
        String[][] data = new String[][]
          {
            { "Sun GHA",     GeomUtil.decToSex(sunGHA, GeomUtil.SWING, GeomUtil.NONE) },
            { "Sun D",       GeomUtil.decToSex(sunD, GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) },
            { "Moon GHA",    GeomUtil.decToSex(moonGHA, GeomUtil.SWING, GeomUtil.NONE) },
            { "Moon D",      GeomUtil.decToSex(moonD, GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) }, 
            { "Venus GHA",   GeomUtil.decToSex(AstroComputer.getVenusGHA(), GeomUtil.SWING, GeomUtil.NONE) },
            { "Venus D",     GeomUtil.decToSex(AstroComputer.getVenusDecl(), GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) },
            { "Mars GHA",    GeomUtil.decToSex(AstroComputer.getMarsGHA(), GeomUtil.SWING, GeomUtil.NONE) },
            { "Mars D",      GeomUtil.decToSex(AstroComputer.getMarsDecl(), GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) },
            { "Jupiter GHA", GeomUtil.decToSex(AstroComputer.getJupiterGHA(), GeomUtil.SWING, GeomUtil.NONE) },
            { "Jupiter D",   GeomUtil.decToSex(AstroComputer.getJupiterDecl(), GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) },
            { "Saturn GHA",  GeomUtil.decToSex(AstroComputer.getSaturnGHA(), GeomUtil.SWING, GeomUtil.NONE) },
            { "Saturn D",    GeomUtil.decToSex(AstroComputer.getSaturnDecl(), GeomUtil.SWING, GeomUtil.NS, GeomUtil.LEADING_SIGN) }
          };
        int x = (int)chartPanel.getVisibleRect().getX();
        int y = (int)chartPanel.getVisibleRect().getY();    
        if (dateStr == null)
          dateStr = "At UTC:" + TideInternalFrame.UTC_DATE_FORMAT.format(this.currentDate.getTime());
        gr.drawString(dateStr, x + 10, y + 20);
//      Utilities.drawPanelTable(data, gr, new Point(x + 10, y + 20 + gr.getFont().getSize() + 2), 10, 2, new int[] { Utilities.LEFT_ALIGNED, Utilities.RIGHT_ALIGNED });
        gr.setColor(Color.white);        
        y += 5;
        Utilities.drawPanelTable(data, 
                                 gr, 
                                 new Point(x + 10, y + 20 + gr.getFont().getSize() + 2), 
                                 10, 
                                 2, 
                                 new int[] { Utilities.LEFT_ALIGNED, Utilities.RIGHT_ALIGNED }, 
                                 true, 
                                 Color.cyan,
                                 Color.blue,
                                 0.35f,
                                 0.9f);        
        gr.setFont(f);
      }
    }
    else
    {
      if (astroPingThread != null)
      {
        // Stop the thread
        astroPingThread.stopThat();
        astroPingThread = null;
      }
    }
  }

  /**
   * Spherical Model used here
   *
   * @param start in degrees
   * @param dist in nautical miles
   * @param bearing in degrees
   * @return
   */
  private static GeoPoint deadReckoning(GeoPoint start, double dist, double bearing)
  {
    GeoPoint reached = null;
    double radianDistance = Math.toRadians(dist / 60d);
    double finalLat = (Math.asin((Math.sin(Math.toRadians(start.getL())) * Math.cos(radianDistance)) +
                                  (Math.cos(Math.toRadians(start.getL())) * Math.sin(radianDistance) * Math.cos(Math.toRadians(bearing))))); 
    double finalLng = Math.toRadians(start.getG()) + Math.atan2(Math.sin(Math.toRadians(bearing)) * Math.sin(radianDistance) * Math.cos(Math.toRadians(start.getL())), 
                                                                Math.cos(radianDistance) - Math.sin(Math.toRadians(start.getL())) * Math.sin(finalLat));
    finalLat = Math.toDegrees(finalLat);
    finalLng = Math.toDegrees(finalLng);
    
    reached = new GeoPoint(finalLat, finalLng);
    return reached;
  }

  public void afterEvent(EventObject eventObject, int i)
  {
  }

  public void setStationPosition(GeoPoint statioonPosition)
  {
    this.stationPosition = statioonPosition;
  }
  
  private static class AstroPingThread extends Thread
  {
    private boolean go = false;
    private ChartPanel chartPanel;
    
    public AstroPingThread(ChartPanel chartPanel)
    {
      super();
      this.chartPanel = chartPanel;
    }
    
    public void run()
    {
      go = true;
      while (go)
      {
        chartPanel.repaint();
        // Wait one sec
        try { Thread.sleep(1000L); } catch (Exception ex) {}
      }
      System.out.println("Poof!");
    }
    
    public void stopThat()
    {
      go = false;
    }
  }
}
