package tideengineimplementation.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseWheelEvent;

import java.awt.event.MouseWheelListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class DatePanel
  extends JPanel
{
  @SuppressWarnings("compatibility:2893176718828670228")
  public final static long serialVersionUID = 1L;

  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JSpinner yearSpinner = null; // new JSpinner();
  private JComboBox monthComboBox = new JComboBox();
  private JComboBox dayComboBox = new JComboBox();

  public DatePanel()
  {
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
    SpinnerModel sm = new SpinnerNumberModel(GregorianCalendar.getInstance().get(Calendar.YEAR), 1970, 2037, 1);
    yearSpinner = new JSpinner(sm);
    yearSpinner.addMouseWheelListener(new MouseWheelListener()
      {
        public void mouseWheelMoved(MouseWheelEvent e)
        {
          int notches = e.getWheelRotation();
          Integer ds = (Integer)yearSpinner.getValue();
          yearSpinner.setValue(new Integer(ds.intValue() + (notches * -1)));
        }
      });

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
    
    dayComboBox.removeAllItems();
    for (int i=0; i<31; i++)
      dayComboBox.addItem(Integer.toString(i+1));

    this.setLayout(gridBagLayout1);
    this.setSize(new Dimension(302, 59));
    this.setPreferredSize(new Dimension(150, 21));
    monthComboBox.setPreferredSize(new Dimension(45, 21));
    monthComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          monthComboBox_actionPerformed(e);
        }
      });
    dayComboBox.setPreferredSize(new Dimension(40, 21));
    this.add(yearSpinner, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(monthComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(dayComboBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
  }

  private void monthComboBox_actionPerformed(ActionEvent e)
  {
    // Get last day of the month
    GregorianCalendar date = new GregorianCalendar(((Integer)yearSpinner.getValue()).intValue(),
                                                   monthComboBox.getSelectedIndex(), 
                                                   1);
    date.add(Calendar.MONTH, 1);
    date.add(Calendar.DAY_OF_MONTH, -1); // Last day of the previous month
    int lastDay = date.get(Calendar.DAY_OF_MONTH);
    dayComboBox.removeAllItems();
    for (int i=0; i<lastDay; i++)
      dayComboBox.addItem(Integer.toString(i+1));    
  }
  
  public GregorianCalendar getDate()
  {
    return new GregorianCalendar(((Integer)yearSpinner.getValue()).intValue(),
                                 monthComboBox.getSelectedIndex(), 
                                 dayComboBox.getSelectedIndex() + 1);
  }
  
  public void setDate(Calendar cal)
  {
    yearSpinner.setValue(new Integer(cal.get(Calendar.YEAR)));
    monthComboBox.setSelectedIndex(cal.get(Calendar.MONTH));
    dayComboBox.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH) - 1);
  }
}
