package tideengineimplementation.gui.dialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;

import java.util.GregorianCalendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import tideengine.TideStation;

import util.SwingUtil;


public class SearchPanel
  extends JPanel
{
  @SuppressWarnings("compatibility:1992585320685695242")
  public final static long serialVersionUID = 1L;

  public final static int HIGH_TIDE = 0;
  public final static int LOW_TIDE  = 1;

  private final static DecimalFormat DF2 = new DecimalFormat("00");
  
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private DatePanel fromDatePanel = new DatePanel();
  private DatePanel toDatePanel   = new DatePanel();
  private JLabel findDaysLabel = new JLabel();
  private JLabel stationNameLabel = new JLabel();

  private JPanel datePanel = new JPanel();
  private JPanel weekDayPanel = new JPanel();
  private JPanel rbWeekDayPanel = new JPanel();
  private JPanel weekDaysPanel = new JPanel();
  
  private JLabel weekDayLabel = new JLabel("...and when week day is");
  private JRadioButton anyDay = new JRadioButton("any day");
  private JRadioButton specDay = new JRadioButton("checked below");
  private ButtonGroup dayGroup = new ButtonGroup();
  
  private JCheckBox mondayCheckBox = new JCheckBox("Monday");
  private JCheckBox tuesdayCheckBox = new JCheckBox("Tuesday");
  private JCheckBox wednesdayCheckBox = new JCheckBox("Wednesday");
  private JCheckBox thursdayCheckBox = new JCheckBox("Thursday");
  private JCheckBox fridayCheckBox = new JCheckBox("Friday");
  private JCheckBox saturdayCheckBox = new JCheckBox("Saturday");
  private JCheckBox sundayCheckBox = new JCheckBox("Sunday");
  
  private JComboBox hiLoComboBox = new JComboBox();
  private JLabel hiLoLabel = new JLabel();
  private JPanel hiLoPanel = new JPanel();

  private JPanel betweenPanel = new JPanel();
  private JComboBox fromHourComboBox = new JComboBox();
  private JComboBox toHourComboBox = new JComboBox();
  private JLabel betweenLabel = new JLabel();
  private JLabel and = new JLabel(" and ");

  private JLabel from = new JLabel("From ");
  private JLabel to = new JLabel(" To ");
  
  private transient TideStation station = null;
  private JButton nowButton = new JButton();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private GridBagLayout gridBagLayout3 = new GridBagLayout();
  private GridBagLayout gridBagLayout4 = new GridBagLayout();
  private GridBagLayout gridBagLayout5 = new GridBagLayout();

  public SearchPanel(TideStation ts)
  {
    this.station = ts;
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
    this.setSize(new Dimension(588, 249));
    if (this.station != null)
    {
      findDaysLabel.setText("Find days at");
      stationNameLabel.setText(this.station.getFullName());
    }
    datePanel.setLayout(gridBagLayout2);
    weekDayPanel.setLayout(gridBagLayout3);
    rbWeekDayPanel.setLayout(gridBagLayout4);
    weekDaysPanel.setLayout(gridBagLayout5);
    
    hiLoLabel.setText("when tide is ");
    betweenLabel.setText("between");

    nowButton.setText("Now");
    nowButton.setMargin(new Insets(1, 1, 1, 1));
    nowButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          nowButton_actionPerformed(e);
        }
      });
    this.add(findDaysLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(stationNameLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    hiLoPanel.add(hiLoLabel, null);
    hiLoComboBox.removeAllItems();
    hiLoComboBox.addItem("High");
    hiLoComboBox.addItem("Low");
    hiLoPanel.add(hiLoComboBox, null);
    this.add(hiLoPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    fromHourComboBox.removeAllItems();
    toHourComboBox.removeAllItems();
    for (int i=0; i<24; i++)
    {
      String item = DF2.format(i) + ":00";
      fromHourComboBox.addItem(item);
      toHourComboBox.addItem(item);
    }
    betweenPanel.add(betweenLabel, null);
    betweenPanel.add(fromHourComboBox, null);
    betweenPanel.add(and, null);
    betweenPanel.add(toHourComboBox, null);
    this.add(betweenPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    if (this.station != null)
    {
      datePanel.add(from, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
      datePanel.add(nowButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
      datePanel.add(fromDatePanel, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
      datePanel.add(to, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
      datePanel.add(toDatePanel, new GridBagConstraints(4, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0));
      this.add(datePanel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0));
    }
    dayGroup.add(anyDay);
    dayGroup.add(specDay);
    anyDay.setSelected(true);
    anyDay.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          anyDay_actionPerformed(e);
        }
      });
    specDay.setSelected(false);
    specDay.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          specDay_actionPerformed(e);
        }
      });
    enableWeekDays(specDay.isSelected());
    
    rbWeekDayPanel.add(weekDayLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    rbWeekDayPanel.add(anyDay, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    rbWeekDayPanel.add(specDay, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));

    weekDaysPanel.add(mondayCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    weekDaysPanel.add(tuesdayCheckBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    weekDaysPanel.add(wednesdayCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    weekDaysPanel.add(thursdayCheckBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    weekDaysPanel.add(fridayCheckBox, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    weekDaysPanel.add(saturdayCheckBox, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    weekDaysPanel.add(sundayCheckBox, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));

    weekDayPanel.add(rbWeekDayPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    weekDayPanel.add(weekDaysPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(weekDayPanel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(5, 0, 0, 0), 0, 0));
  }
  
  private void enableWeekDays(boolean b)
  {
    mondayCheckBox.setEnabled(b);
    tuesdayCheckBox.setEnabled(b);
    wednesdayCheckBox.setEnabled(b);
    thursdayCheckBox.setEnabled(b);
    fridayCheckBox.setEnabled(b);
    saturdayCheckBox.setEnabled(b);
    sundayCheckBox.setEnabled(b);
  }
  
  public void setStation(TideStation ts)
  {
    this.station = ts;
    stationNameLabel.setText(this.station.getFullName());
  }
  
  public int getFromHour()
  {
    return fromHourComboBox.getSelectedIndex();
  }
  public int getToHour()
  {
    return toHourComboBox.getSelectedIndex();
  }
  
  public int getHighLow()
  {
    return hiLoComboBox.getSelectedIndex();
  }
  
  public GregorianCalendar getFromDate()
  {
    return fromDatePanel.getDate();
  }
  public GregorianCalendar getToDate()
  {
    return toDatePanel.getDate();
  }
  
  public final static int MONDAY    = 0;
  public final static int TUESDAY   = 1;
  public final static int WEDNESDAY = 2;
  public final static int THURSDAY  = 3;
  public final static int FRIDAY    = 4;
  public final static int SATURDAY  = 5;
  public final static int SUNDAY    = 6;
  
  private final static String[] DAY_NAMES = { "Monday",
                                              "Tuesday",
                                              "Wednesday",
                                              "Thursday",
                                              "Friday",
                                              "Saturday",
                                              "Sunday" };
  
  public static String[] getDayNames()
  {
    return DAY_NAMES.clone();  
  }
  
  public int[] getWeekDay()
  {
    int[] days = null;
    if (specDay.isSelected())
    {
      days = new int[7];
      days[MONDAY]    = (mondayCheckBox.isSelected()?1:0);
      days[TUESDAY]   = (tuesdayCheckBox.isSelected()?1:0);
      days[WEDNESDAY] = (wednesdayCheckBox.isSelected()?1:0);
      days[THURSDAY]  = (thursdayCheckBox.isSelected()?1:0);
      days[FRIDAY]    = (fridayCheckBox.isSelected()?1:0);
      days[SATURDAY]  = (saturdayCheckBox.isSelected()?1:0);
      days[SUNDAY]    = (sundayCheckBox.isSelected()?1:0);
    }    
    return days;
  }

  private void nowButton_actionPerformed(ActionEvent e)
  {
    fromDatePanel.setDate(GregorianCalendar.getInstance());
  }

  private void anyDay_actionPerformed(ActionEvent e)
  {
    enableWeekDays(specDay.isSelected());
  }

  private void specDay_actionPerformed(ActionEvent e)
  {
    enableWeekDays(specDay.isSelected());
  }

  @Override
  public void setEnabled(final boolean enabled)
  {
//  super.setEnabled(enabled);
    hiLoLabel.setEnabled(enabled);
    hiLoComboBox.setEnabled(enabled);
    nowButton.setEnabled(enabled);
    fromHourComboBox.setEnabled(enabled);
    toHourComboBox.setEnabled(enabled);
    betweenLabel.setEnabled(enabled);
    and.setEnabled(enabled);
    weekDayLabel.setEnabled(enabled);
    weekDayLabel.setEnabled(enabled);
    anyDay.setEnabled(enabled);
    specDay.setEnabled(enabled);
    
    enableWeekDays(enabled && specDay.isSelected());
    try
    {
      EventQueue.invokeLater(new Runnable()
                               {
                                 public void run()
                                 {
                                   repaint();             
                                   System.out.println("Boom " + (enabled?"on":"off"));
                                 }
                               });
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
