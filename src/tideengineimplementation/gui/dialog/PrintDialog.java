package tideengineimplementation.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.event.MouseWheelEvent;

import java.awt.event.MouseWheelListener;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JSpinner;

import javax.swing.SpinnerModel;

import javax.swing.SpinnerNumberModel;

import tideengine.TideStation;

public class PrintDialog
  extends JPanel
{
  @SuppressWarnings("compatibility:6965100563857786638")
  public final static long serialVersionUID = 1L;

  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel stationNameLabel = new JLabel();
  private JComboBox timeZoneComboBox = new JComboBox();
  private JLabel timeZoneLabel = new JLabel();

  private transient TideStation ts;
  
  private JLabel printForLabel = new JLabel();
  private JSpinner nbSpinner = null; // new JSpinner();
  private JComboBox quantityComboBox = new JComboBox();
  
  private final static String MONTH = "Month(s)";
  private final static String YEAR  = "Year(s)";
  private JLabel startLabel = new JLabel();
  private JComboBox monthComboBox = new JComboBox();
  private JSpinner yearSpinner = null;
  private JLabel unitLabel = new JLabel();
  private JComboBox unitComboBox = new JComboBox(); // new JSpinner();

  public PrintDialog(TideStation ts)
  {
    this.ts = ts;
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    this.setLayout(gridBagLayout1);
    stationNameLabel.setText("Station Name");
    timeZoneComboBox.setPreferredSize(new Dimension(150, 21));
    timeZoneComboBox.setMinimumSize(new Dimension(60, 21));
    timeZoneComboBox.setToolTipText("Time Zome");
    timeZoneLabel.setText("Use Time Zone : ");
    printForLabel.setText("Print for ");

    startLabel.setText("Start ");
    SpinnerModel sm = new SpinnerNumberModel(1, 1, 100, 1);
    nbSpinner = new JSpinner(sm);
    nbSpinner.addMouseWheelListener(new MouseWheelListener()
      {
        public void mouseWheelMoved(MouseWheelEvent e)
        {
          int notches = e.getWheelRotation();
          Integer ds = (Integer)nbSpinner.getValue();
          nbSpinner.setValue(new Integer(ds.intValue() + (notches * -1)));
        }
      });

    SpinnerModel sm2 = new SpinnerNumberModel(GregorianCalendar.getInstance().get(Calendar.YEAR), 1970, 2037, 1);
    yearSpinner = new JSpinner(sm2);
    yearSpinner.addMouseWheelListener(new MouseWheelListener()
      {
        public void mouseWheelMoved(MouseWheelEvent e)
        {
          int notches = e.getWheelRotation();
          Integer ds = (Integer)yearSpinner.getValue();
          yearSpinner.setValue(new Integer(ds.intValue() + (notches * -1)));
        }
      });

    this.add(stationNameLabel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 10, 0), 0, 0));
    this.add(timeZoneComboBox, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(timeZoneLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));

    this.add(printForLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(nbSpinner, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(quantityComboBox, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(startLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(monthComboBox, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(yearSpinner, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(unitLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(unitComboBox, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    timeZoneComboBox.removeAllItems();
    String[] tz = TimeZone.getAvailableIDs();
    for (int i=0; i<tz.length; i++)
      timeZoneComboBox.addItem(tz[i]);
    timeZoneComboBox.setSelectedItem(this.ts.getTimeZone());
    stationNameLabel.setText(ts.getFullName());
    
    quantityComboBox.removeAllItems();
    quantityComboBox.addItem(MONTH);
    quantityComboBox.addItem(YEAR);
    
    monthComboBox.removeAllItems();
    monthComboBox.addItem("Jan");
    monthComboBox.addItem("Feb");
    monthComboBox.addItem("Mar");
    monthComboBox.addItem("Apr");
    monthComboBox.addItem("May");
    monthComboBox.addItem("Jun");
    monthComboBox.addItem("Jul");
    monthComboBox.addItem("Aug");
    monthComboBox.addItem("Sep");
    monthComboBox.addItem("Oct");
    monthComboBox.addItem("Nov");
    monthComboBox.addItem("Dec");
    monthComboBox.setSelectedIndex(GregorianCalendar.getInstance().get(Calendar.MONTH));
    unitLabel.setText("Unit to use : ");
    unitComboBox.removeAllItems();
    if (ts.isTideStation())
    {
      unitComboBox.addItem(TideStation.FEET);
      unitComboBox.addItem(TideStation.METERS);
    }
    if (ts.isCurrentStation())
      unitComboBox.addItem(TideStation.KNOTS);
    unitComboBox.setSelectedItem(ts.getUnit());
  }
  
  public String getTimeZone()
  {
    return (String)timeZoneComboBox.getSelectedItem();
  }
  
  public int getNb()
  {
    return ((Integer)nbSpinner.getValue()).intValue();
  }
  
  public int getQuantity()
  {
    int q = 0;
    if (MONTH.equals(quantityComboBox.getSelectedItem()))
      q = Calendar.MONTH;
    else if (YEAR.equals(quantityComboBox.getSelectedItem()))
      q = Calendar.YEAR;
    
    return q;
  }
  
  public int getStartMonth()
  {
    return monthComboBox.getSelectedIndex();
  }
  
  public int getStartYear()
  {
    return ((Integer)yearSpinner.getValue()).intValue();
  }
  
  public String getUnitToUse()
  {
    return (String)unitComboBox.getSelectedItem();
  }
  
  public static void main(String[] args)
  {
    SimpleDateFormat sdf = new SimpleDateFormat("E yyyy-MMM-dd HH:mm:ss.SSS Z");
    Calendar cal = GregorianCalendar.getInstance();
    System.out.println("Current time zone:" + cal.getTimeZone().getID());
    System.out.println("Now:" + sdf.format(cal.getTime()));
    Calendar utcCal = (Calendar)cal.clone();
    utcCal.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    System.out.println("Current time zone:" + utcCal.getTimeZone().getID());
    System.out.println("Now:" + sdf.format(utcCal.getTime()));
    System.out.println("Year:" + utcCal.get(Calendar.YEAR)); 
    System.out.println("Month:" + utcCal.get(Calendar.MONTH));
    System.out.println("Day:" + utcCal.get(Calendar.DAY_OF_MONTH));
    System.out.println("Hour:" + utcCal.get(Calendar.HOUR_OF_DAY)); 
    System.out.println("Minute:" + utcCal.get(Calendar.MINUTE)); 
    System.out.println("Second:" + utcCal.get(Calendar.SECOND));
    
    sdf.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    System.out.println("Now:" + sdf.format(utcCal.getTime()));        
  }
}
