package tideengineimplementation.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import tideengineimplementation.gui.TideInternalFrame;
import tideengineimplementation.gui.ctx.TideContext;

public class CoeffTable
  extends JPanel
{
  private transient Object[][] coeffData;

  // Table Columns
  final static String COEFF_NAME = "Name";
  final static String COEFF_DEF  = "Definition";

  final static String[] names =
  { COEFF_NAME, COEFF_DEF };
  // Table content
  private transient Object[][] data = new Object[0][names.length];

  private transient TableModel dataModel;
  private JTable table;

  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel centerPanel = new JPanel();
  private JPanel bottomPanel = new JPanel();
  private BorderLayout borderLayout2 = new BorderLayout();
  private JScrollPane centerScrollPane = null; // new JScrollPane();
  private JPanel topPanel = new JPanel();
  private JLabel filterLabel = new JLabel();
  private JTextField filterTextField = new JTextField();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel statusLabel = new JLabel();
  private BorderLayout borderLayout3 = new BorderLayout();
  private JPanel rbPanel = new JPanel();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private JRadioButton allCoeffRadioButton = new JRadioButton();
  private JRadioButton selectedCoeffRadioButton = new JRadioButton();
  private ButtonGroup bg = new ButtonGroup();
  private JCheckBox showTideCurveCheckBox = new JCheckBox();

  public CoeffTable(HashMap<TideInternalFrame.ColoredCoeff, String> coeff)
  {
    try
    {
      jbInit();
      if (coeff != null)
      {
        setCoeffData(coeff);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

//  public void paintComponent(Graphics g)
//  {
//    super.paintComponent(g);
//    this.setSize(150, this.getHeight());
//    System.out.println("Repainting CoeffTable");
//  }
  
  public void setValues()
  {
    int nbl = 0;
    try
    {
      if (coeffData != null)
      {
        for (int i=0; i<coeffData.length; i++)
        {
          addLineInTable((TideInternalFrame.ColoredCoeff)coeffData[i][0], (String)coeffData[i][1]);
          nbl++;
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    this.setStatusLabel(Integer.toString(nbl) + " coefficient(s)");
  }

  private void jbInit()
    throws Exception
  {
    this.setLayout(borderLayout1);
    this.setSize(new Dimension(210, 430));
    this.setPreferredSize(new Dimension(210, 430));
    centerPanel.setLayout(borderLayout2);
    bottomPanel.setLayout(borderLayout3);
    topPanel.setLayout(gridBagLayout1);
    filterLabel.setText("Filter:");
    statusLabel.setText("Ready");
    rbPanel.setLayout(gridBagLayout2);
    allCoeffRadioButton.setText("All");
    allCoeffRadioButton.setToolTipText("Show curves for all coefficients");
    allCoeffRadioButton.setSelected(true);
    allCoeffRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          allCoeffRadioButton_actionPerformed(e);
        }
      });
    selectedCoeffRadioButton.setText("Selected");
    selectedCoeffRadioButton.setToolTipText("Show only curves of selected coefficients");
    selectedCoeffRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          selectedCoeffRadioButton_actionPerformed(e);
        }
      });
    showTideCurveCheckBox.setText("Tide Curve");
    showTideCurveCheckBox.setToolTipText("<html>Show the tide curve<br>(sum of all)</html>");

    showTideCurveCheckBox.setSelected(true);
    showTideCurveCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          showTideCurveCheckBox_actionPerformed(e);
        }
      });
    this.add(centerPanel, BorderLayout.CENTER);
    bottomPanel.add(statusLabel, BorderLayout.CENTER);
    this.add(bottomPanel, BorderLayout.SOUTH);
    topPanel.add(filterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    topPanel.add(filterTextField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    topPanel.add(rbPanel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
    topPanel.add(allCoeffRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    topPanel.add(selectedCoeffRadioButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 5, 0, 0), 0, 0));
    topPanel.add(showTideCurveCheckBox, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    filterTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e) // [Return] in the field
        {
          setSelection();
        }
      });
    filterTextField.getDocument().addDocumentListener(new DocumentListener()
      {
        public void insertUpdate(DocumentEvent e)
        {
          setSelection();
        }

        public void removeUpdate(DocumentEvent e)
        {
          setSelection();
        }

        public void changedUpdate(DocumentEvent e)
        {
          setSelection();
        }
      });
    filterTextField.setToolTipText("Enter selection criteria, like part of the name, definition...");
    bg.add(allCoeffRadioButton);
    bg.add(selectedCoeffRadioButton);
    this.add(topPanel, BorderLayout.NORTH);
    initTable();

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // JTable.AUTO_RESIZE_OFF JTable.AUTO_RESIZE_ALL_COLUMNS
    
    SelectionListener listener = new SelectionListener(table);
    table.getSelectionModel().addListSelectionListener(listener);
    table.getColumnModel().getSelectionModel().addListSelectionListener(listener);

    this.filterLabel.setEnabled(this.coeffData != null);
    this.filterTextField.setEnabled(this.coeffData != null);
  }

  private void initTable()
  {
    // Init Table
    dataModel = new AbstractTableModel()
      {
        public int getColumnCount()
        {
          return names.length;
        }

        public int getRowCount()
        {
          return data.length;
        }

        public Object getValueAt(int row, int col)
        {
          return data[row][col];
        }

        public String getColumnName(int column)
        {
          return names[column];
        }

        public Class getColumnClass(int c)
        {
          if (getValueAt(0, c) != null)
            return getValueAt(0, c).getClass();
          else
            return null;
        }

        public boolean isCellEditable(int row, int col)
        {
          return (col == 0);
        }

        public void setValueAt(Object aValue, int row, int column)
        {
          data[row][column] = (String)aValue;
          fireTableCellUpdated(row, column);
        }
      };
    // Create JTable
    table = new JTable(dataModel)
    {
      /* For the tooltip text */
      public Component prepareRenderer_tamere(TableCellRenderer renderer, int rowIndex, int vColIndex)
      {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        if (c instanceof JComponent)
        {
          JComponent jc = (JComponent) c;
          try
          { 
            if (vColIndex == 1)
            {
              jc.setToolTipText(getValueAt(rowIndex, vColIndex).toString());
              jc.setForeground(Color.BLACK);
              jc.setBackground(Color.WHITE);
            }
            else if (vColIndex == 0)
            {
              TideInternalFrame.ColoredCoeff cc = (TideInternalFrame.ColoredCoeff)getValueAt(rowIndex, vColIndex);
              jc.setToolTipText(cc.name);
              jc.setForeground(reverseColor(cc.color));
              jc.setBackground(cc.color);
            }
          }
          catch (Exception ex)
          {
            System.err.println("ParamPanel:" + ex.getMessage());
          }
        }
        return c;
      }
    };
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    centerScrollPane = new JScrollPane(table);
    centerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    centerPanel.add(centerScrollPane, BorderLayout.CENTER);
//  KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new JTableFocusChangeListener(table));
    table.getColumn(COEFF_NAME).setMinWidth(0);
    table.getColumn(COEFF_NAME).setMaxWidth(50);
    table.getColumn(COEFF_NAME).setPreferredWidth(50);
    
    table.getColumn(COEFF_DEF).setMinWidth(100);
    table.getColumn(COEFF_DEF).setPreferredWidth(400);
//  table.getColumn(COEFF_DEF).setCellRenderer(new CustomTableCellRenderer());
  }

  private void addLineInTable(TideInternalFrame.ColoredCoeff cc, String def)
  {
    int len = data.length;
    Object[][] newData = new Object[len + 1][names.length];
    for (int i = 0; i < len; i++)
    {
      for (int j = 0; j < names.length; j++)
        newData[i][j] = data[i][j];
    }
    newData[len][0] = cc;
    newData[len][1] = def;
    data = newData;
    ((AbstractTableModel) dataModel).fireTableDataChanged();
    table.repaint();
  }

  public ArrayList<String> getSelectedCoeff()
  {
    ArrayList<String> selected = null;
    int[] sr = table.getSelectedRows();
    if (sr.length > 0)
    {
      selected = new ArrayList<String>(sr.length);
      for (int i=0; i<coeffData.length; i++)
      {
        for (int j=0; j<sr.length; j++)
        {
          if (coeffData[i][0].equals(data[sr[j]][0]))
          {
            selected.add(((TideInternalFrame.ColoredCoeff)coeffData[i][0]).toString());
            break;
          }
        }
      }
    }
    return selected;
  }

  private void setSelection()
  {
    String fieldContent = filterTextField.getText();

//  TideContext.getInstance().fireFilter(fieldContent);

    String patternStr = ".*" + fieldContent + ".*";
    Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);

    data = new String[0][names.length];
    int nbl = 0;
    for (int i=0; i<coeffData.length; i++)
    {
      Matcher m1 = p.matcher(((TideInternalFrame.ColoredCoeff)coeffData[i][0]).toString());
      Matcher m2 = p.matcher((String)coeffData[i][1]);
      if (m1.matches() || m2.matches())
      {
        // Add in table
        nbl++;
        addLineInTable((TideInternalFrame.ColoredCoeff)coeffData[i][0], (String)coeffData[i][1]);
      }
    }
    this.setStatusLabel(Integer.toString(nbl) + " zone(s)");
    ((AbstractTableModel) dataModel).fireTableDataChanged();
    table.repaint();
  }

  public void setCoeffData(HashMap<TideInternalFrame.ColoredCoeff, String> coeff)
  {
    Object[][] cd = new Object[coeff.size()][2];
    Set<TideInternalFrame.ColoredCoeff> keys = coeff.keySet();
    int i = 0;
    for (TideInternalFrame.ColoredCoeff cc : keys)
    {
      String val = coeff.get(cc);
      cd[i][0] = cc;
      cd[i][1] = (val!=null?val:"");
      i++;
    }
    setCoeffData(cd);
  }
  
  public void setCoeffData(Object[][] coeff)
  {
    this.coeffData = coeff;
    this.filterLabel.setEnabled(this.coeffData != null);
    this.filterTextField.setEnabled(this.coeffData != null);
    setValues();
  }

  public Object[][] getCoeffData()
  {
    return coeffData;
  }

  private void allCoeffRadioButton_actionPerformed(ActionEvent e)
  {
    TideContext.getInstance().fireShowAllCurves(allCoeffRadioButton.isSelected());
  }

  private void selectedCoeffRadioButton_actionPerformed(ActionEvent e)
  {
    TideContext.getInstance().fireShowAllCurves(allCoeffRadioButton.isSelected());
  }

  private void showTideCurveCheckBox_actionPerformed(ActionEvent e)
  {
    TideContext.getInstance().fireShowTideCurve(showTideCurveCheckBox.isSelected());
  }

  public class SelectionListener
    implements ListSelectionListener
  {
    JTable table;

    SelectionListener(JTable table)
    {
      this.table = table;
    }

    public void valueChanged(ListSelectionEvent lse)
    {
      TideContext.getInstance().fireCoeffSelection(getSelectedCoeff());
    }
  }

  public void setStatusLabel(String s)
  {
    this.statusLabel.setText(s);
  }
  
  private static Color reverseColor(Color c)
  {
    Color reversed = new Color(255 - c.getRed(), 
                               255 - c.getGreen(), 
                               255 - c.getBlue());    
    return reversed;
  }
  
  public class CustomTableCellRenderer
    extends JLabel
    implements TableCellRenderer
  {
    Object curValue = null;
    boolean selected = false;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      curValue = value;
      selected = isSelected;
      this.setToolTipText(value.toString());
      return this;
    }

    public void paintComponent(Graphics g)
    {
      System.out.println("Painting, " + (selected?"":"non ") + "selected:" + curValue.toString());
      if (curValue != null)
      {
        this.setBackground(selected?Color.LIGHT_GRAY:Color.WHITE);
        g.drawString((String)curValue, 1, getHeight() - 1);
      }
    }
  }
}
