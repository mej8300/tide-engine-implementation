package tideengineimplementation.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.text.DecimalFormat;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import tideengineimplementation.gui.TideInternalFrame;


public class FoundStationPanel
extends JPanel 
{
  @SuppressWarnings("compatibility:-467412741448390710")
  public final static long serialVersionUID = 1L;

  private String selectedStation = null;
  private final static DecimalFormat DF_X2 = new DecimalFormat("#0.00");
  // Table Columns
  private final static String STATIONS  = "Station";
  private final static String DISTANCES = "Distance";

  private final String[] names = {STATIONS, DISTANCES};
  // Table content
  private transient Object[][] data = new Object[0][2];
  private transient TableModel dataModel;
  private JTable table;

  private BorderLayout borderLayout1 = new BorderLayout();
  private JScrollPane scrollPane = null;

  private JPanel tablePane = new JPanel();
  private JLabel topLabel = new JLabel("Select a row, and click OK to display its curves");
  
  public FoundStationPanel()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public Object[][] getData()
  {
    return data;
  }
  
  public void setData(Object[][] o)
  {
    data = o;
    ((AbstractTableModel)dataModel).fireTableDataChanged();
  }
  
  private void jbInit() throws Exception
  {
    this.setLayout(borderLayout1);
    this.setPreferredSize(new Dimension(235, 200));
    this.add(topLabel, BorderLayout.NORTH);
    this.add(tablePane, BorderLayout.CENTER);
    initTable();
    this.setPreferredSize(new Dimension(400, 280));
  }

  private void initTable()
  {
    // Init Table
    dataModel = new AbstractTableModel()
    {
      @SuppressWarnings("compatibility:9070027359489543434")
      public final static long serialVersionUID = 1L;

      public int getColumnCount()
      { return names.length; }
      public int getRowCount()
      { return data.length; }
      public Object getValueAt(int row, int col)
      { return data[row][col]; }
      public String getColumnName(int column)
      { return names[column]; }
      public Class getColumnClass(int c)
      {
        return String.class;
      }
      public boolean isCellEditable(int row, int col)
      { 
        return false; // All editable
      }
      public void setValueAt(Object aValue, int row, int column)
      { 
        data[row][column] = aValue; 
        fireTableCellUpdated(row, column);
      }
    };
    // Create JTable
    table = new JTable(dataModel);
    scrollPane = new JScrollPane(table);
    scrollPane.setPreferredSize(new Dimension(400, 280));
    tablePane.setLayout(new BorderLayout());    
    tablePane.add(scrollPane, BorderLayout.CENTER);
    SelectionListener listener = new SelectionListener(table);
    table.getSelectionModel().addListSelectionListener(listener);
    table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
    table.getColumn(STATIONS).setPreferredWidth(300);
    
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  private void addLineInTable(String a,
                              String b)
  {
    int len = data.length;
    Object[][] newData = new Object[len + 1][names.length];
    for (int i=0; i<len; i++)
    {
      for (int j=0; j<names.length; j++)
        newData[i][j] = data[i][j];
    }
    newData[len][0] = a;
    newData[len][1] = b;
    data = newData;
    ((AbstractTableModel)dataModel).fireTableDataChanged();
  }

  public void setStationData(List<TideInternalFrame.StationDistance> stationMap, int nbRows)
  {
    int nb = 0;
    for (TideInternalFrame.StationDistance station : stationMap)
    {
      String name = station.getStationName();
      double d    = station.getDistance();
      addLineInTable(name, DF_X2.format(d) + " nm");
      if (++nb > nbRows)
        break;
    }
  }

  public String getSelectedStation()
  {
    return selectedStation;
  }

  public class SelectionListener implements ListSelectionListener
  {
    JTable table;
    
    SelectionListener(JTable table) 
    {
      this.table = table;
    }
    public void valueChanged(ListSelectionEvent e) 
    {
      int selectedRow = table.getSelectedRow();
      if (selectedRow < 0)
        selectedStation = null;
      else
        selectedStation = (String)data[selectedRow][0]; // Name
    }    
  }
}