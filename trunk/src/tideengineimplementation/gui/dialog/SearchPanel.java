package tideengineimplementation.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;

import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tideengine.TideStation;

public class SearchPanel
  extends JPanel
{
  public final static int HIGH_TIDE = 0;
  public final static int LOW_TIDE  = 1;

  private final static DecimalFormat DF2 = new DecimalFormat("00");
  
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private DatePanel fromDatePanel = new DatePanel();
  private DatePanel toDatePanel   = new DatePanel();
  private JLabel findDaysLabel = new JLabel();
  private JLabel stationNameLabel = new JLabel();

  private JPanel datePanel = new JPanel();
  
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
    findDaysLabel.setText("Find days at");
    stationNameLabel.setText(this.station.getFullName());
    datePanel.setLayout(gridBagLayout2);
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
  
  public void setStation(TideStation ts)
  {
    this.station = ts;
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

  private void nowButton_actionPerformed(ActionEvent e)
  {
    fromDatePanel.setDate(GregorianCalendar.getInstance());
  }
}
