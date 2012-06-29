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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nauticalalmanac.Context;

import tideengine.TideStation;

import tideengineimplementation.gui.TideInternalFrame;
import tideengineimplementation.gui.ctx.TideContext;
import tideengineimplementation.gui.ctx.TideEventListener;

import tideengineimplementation.utils.AstroComputer;

import user.util.GeomUtil;

public class CommandPanel 
     extends JPanel
  implements ChartPanelParentInterface_II
{
  private String stationFilterPattern = "";
  
  private BorderLayout borderLayout1;
  private JScrollPane jScrollPane1;
  private ChartPanel chartPanel;
  private JPanel bottomPanel;
  private JButton zoomInButton;
  private JButton zoomOutButton;
  private JCheckBox mouseCheckBox;
  private JCheckBox withNameCheckBox;
  private JCheckBox withAstroCheckBox;
  
  private transient List<TideStation> stationData = null;
  
  private double sunD    = 0d;
  private double sunGHA  = 0d;
  private double moonD   = 0d;
  private double moonGHA = 0d;
  
  private transient Image moonSymbol    = new ImageIcon(TideInternalFrame.class.getResource("moon.png")).getImage();
  private transient Image sunSymbol     = new ImageIcon(TideInternalFrame.class.getResource("sun.png")).getImage();
  private transient Image venusSymbol   = new ImageIcon(TideInternalFrame.class.getResource("venus.png")).getImage();
  private transient Image marsSymbol    = new ImageIcon(TideInternalFrame.class.getResource("mars.png")).getImage();
  private transient Image jupiterSymbol = new ImageIcon(TideInternalFrame.class.getResource("jupiter.png")).getImage();
  private transient Image saturnSymbol  = new ImageIcon(TideInternalFrame.class.getResource("saturn.png")).getImage();

  public CommandPanel()
  {
    borderLayout1 = new BorderLayout();
    jScrollPane1 = new JScrollPane();
    chartPanel = new ChartPanel(this);
    bottomPanel = new JPanel();
    zoomInButton = new JButton();
    zoomOutButton = new JButton();
    mouseCheckBox = new JCheckBox("GrabScroll enabled");
    mouseCheckBox.setSelected(false);
    withNameCheckBox = new JCheckBox("With Station Names");
    withNameCheckBox.setSelected(false);
    withAstroCheckBox = new JCheckBox("With Astro Data");
    withAstroCheckBox.setSelected(false);
    
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
    TideContext.getInstance().addTideListener(new TideEventListener()
      {
        @Override
        public void filterList(String pattern)
        {
          stationFilterPattern = pattern;
          if (chartPanel.isVisible())
            repaint();
        }
      });
    setLayout(borderLayout1);
    zoomInButton.setText("Zoom In");
    zoomInButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        jButton1_actionPerformed(e);
      }

    });
    zoomOutButton.setText("Zoom Out");
    zoomOutButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        jButton2_actionPerformed(e);
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
    jScrollPane1.getViewport().add(chartPanel, null);
    add(jScrollPane1, BorderLayout.CENTER);
    bottomPanel.add(zoomInButton, null);
    bottomPanel.add(zoomOutButton, null);
    bottomPanel.add(mouseCheckBox, null);
    bottomPanel.add(withNameCheckBox, null);
    bottomPanel.add(withAstroCheckBox, null);
    add(bottomPanel, BorderLayout.SOUTH);
    double nLat  =  83D;
    double sLat  = -80D;
    double wLong = -180D;
    double eLong =  180D; // chartPanel.calculateEastG(nLat, sLat, wLong);
    chartPanel.setEastG(eLong);
    chartPanel.setWestG(wLong);
    chartPanel.setNorthL(nLat);
    chartPanel.setSouthL(sLat);
    
    chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
    chartPanel.setHorizontalGridInterval(10D);
    chartPanel.setVerticalGridInterval(10D);
    chartPanel.setWithScale(false);
    chartPanel.setGridColor(Color.lightGray);

    chartPanel.setMouseDraggedEnabled(mouseCheckBox.isSelected());
    
//  chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_GRAB_SCROLL);
    chartPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    chartPanel.setPositionToolTipEnabled(true);
  }

  private void jButton1_actionPerformed(ActionEvent e)
  {
    chartPanel.zoomIn();
  }

  private void jButton2_actionPerformed(ActionEvent e)
  {
    chartPanel.zoomOut();
  }

  private void swicthMouse(boolean b)
  {
    chartPanel.setMouseDraggedEnabled(b);
    chartPanel.setMouseDraggedType(b?ChartPanel.MOUSE_DRAG_GRAB_SCROLL:ChartPanel.MOUSE_DRAG_ZOOM);
  }
  
  GeoPoint from = null;
  GeoPoint to   = null;
  
  @Override
  public void chartPanelPaintComponent(Graphics gr)
  {
    Graphics2D g2d = null;
    if (gr instanceof Graphics2D)
      g2d = (Graphics2D)gr;
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
          gr.drawOval(pt.x-2,pt.y-2, 4, 4);
          if (withNameCheckBox.isSelected())
            gr.drawString(sd.getFullName(), pt.x + 2, pt.y);
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
      gr.fillOval(pt.x-8, pt.y-8, 16, 16);
      gr.drawImage(img, pt.x - 7, pt.y - 7, null); // Image is 13x13
    }
  }
  
  public boolean onEvent(EventObject e, int type)
  {
    return true;
  }

  public String getMessForTooltip()
  {
    return null;
  }

  public boolean replaceMessForTooltip()
  {
    return false;
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

  @Override
  public void chartPanelPaintComponentAfter(Graphics gr)
  {
    // Sun and Moon, Planets 
    if (sunD != 0 && sunGHA != 0 && moonD != 0 && moonGHA != 0)
    {
      gr.setColor(Color.LIGHT_GRAY);
      plotBody(gr, "Sun",  sunD,  sunGHA, sunSymbol);
      plotBody(gr, "Moon", moonD, moonGHA, moonSymbol);

      plotBody(gr, "Venus",   AstroComputer.getVenusDecl(),   AstroComputer.getVenusGHA(),   venusSymbol);
      plotBody(gr, "Mars",    AstroComputer.getMarsDecl(),    AstroComputer.getMarsGHA(),    marsSymbol);
      plotBody(gr, "Jupiter", AstroComputer.getJupiterDecl(), AstroComputer.getJupiterGHA(), jupiterSymbol);
      plotBody(gr, "Saturn",  AstroComputer.getSaturnDecl(),  AstroComputer.getSaturnGHA(),  saturnSymbol);
      
      // Day/Night limit
      double sunLongitude = 0;
      if (sunGHA < 180)
        sunLongitude = -sunGHA;
      if (sunGHA >= 180)
        sunLongitude = 360 - sunGHA;
      gr.setColor(Color.darkGray);
      GeoPoint sunPos = new GeoPoint(sunD, sunLongitude);
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
      Polygon night = new Polygon();
      for (Double d : sortedLng)
      {
        double lat = nightMap.get(d).doubleValue();
        Point pp = chartPanel.getPanelPoint(new GeoPoint(lat, d));
        night.addPoint(pp.x, pp.y);
      }
      if (sunD > 0) // Then night is south
      {
        night.addPoint(chartPanel.getW(), chartPanel.getH());  
        night.addPoint(0, chartPanel.getH());  
      }
      else // Night is north
      {
        night.addPoint(chartPanel.getW(), 0);  
        night.addPoint(0, 0);          
      }
      // Now fill the night polygon
      ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
       gr.fillPolygon(night);
      ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
    if (withAstroCheckBox.isSelected())
    {
      // Drawing astro data
      if (sunD != 0 && sunGHA != 0 && moonD != 0 && moonGHA != 0)
      {
        gr.setColor(Color.blue);
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
        Utilities.drawPanelTable(data, gr, new Point(10, 20), 10, 2, new int[] { Utilities.LEFT_ALIGNED, Utilities.RIGHT_ALIGNED });
        
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
}
