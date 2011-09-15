package tideengineimplementation.gui.table;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import tideengine.BackEndXMLTideComputer;

import tideengine.TideStation;

import tideengineimplementation.gui.ctx.TideContext;

import user.util.GeomUtil;

public class FilterTable
     extends JPanel 
{
  private transient ArrayList<TideStation> stationData;
  
  // Table Columns
  final static String STATION_NAME = "Station";
  final static String LATITUDE     = "Latitude";
  final static String LONGITUDE    = "Longitude";

  final static String[] names = {STATION_NAME,
                                 LATITUDE,
                                 LONGITUDE};
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

  public FilterTable(ArrayList<TideStation> stationData)
  {
    try
    {
      jbInit();
      if (stationData != null)
      {
        setStationData(stationData);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void setValues()
  {
    int nbl = 0;
    try
    {
      if (stationData != null)
      {
        for (TideStation sd : stationData)
        {
          addLineInTable(sd.getFullName(), 
                         GeomUtil.decToSex(sd.getLatitude(), GeomUtil.SWING, GeomUtil.NS), 
                         GeomUtil.decToSex(sd.getLongitude(), GeomUtil.SWING, GeomUtil.EW));
          nbl++;
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    this.setStatusLabel(Integer.toString(nbl) + " station(s)");
  }

  private void jbInit() throws Exception
  {
    this.setLayout(borderLayout1);
    centerPanel.setLayout(borderLayout2);
    bottomPanel.setLayout(borderLayout3);
    topPanel.setLayout(gridBagLayout1);
    filterLabel.setText("Filter:");
    statusLabel.setText("Ready");
    this.add(centerPanel, BorderLayout.CENTER);
    bottomPanel.add(statusLabel, BorderLayout.CENTER);
    this.add(bottomPanel, BorderLayout.SOUTH);
    topPanel.add(filterLabel,
                 new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
                                                                                                                           0,
                                                                                                                           0,
                                                                                                                           0),
                                        0, 0));
    topPanel.add(filterTextField,
                 new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
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
    filterTextField.setToolTipText("Enter selection criteria, like part of the name...");        
    this.add(topPanel, BorderLayout.NORTH);
    initTable();

    SelectionListener listener = new SelectionListener(table);
    table.getSelectionModel().addListSelectionListener(listener);
    table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
    
    this.filterLabel.setEnabled(this.stationData != null);
    this.filterTextField.setEnabled(this.stationData != null);
    
    table.addMouseListener(new MouseAdapter()
                           {
                             public void mouseReleased(MouseEvent e)
                             {
                               if (e.getClickCount() == 2)
                               {
                                 int sr = table.getSelectedRow();
                                 if (sr >= 0)
                                 {
                                   for (TideStation sd : stationData)
                                   {
                                     if (sd.getFullName().equals(data[sr][0]))
                                     {
                                       TideContext.getInstance().fireStationSelected(sd.getFullName());
                                       break;
                                     }
                                   }
                                 }
                               }
                               else
                               {
                                 ; // tryPopup(e);
                               }
                             }
                           });
  }
  
  private void initTable()
  {
    // Init Table
    dataModel = new AbstractTableModel()
    {
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
        return getValueAt(0, c).getClass();
      }
      public boolean isCellEditable(int row, int col)
      { 
        return false; 
      }
      public void setValueAt(Object aValue, int row, int column)
      { 
        data[row][column] = aValue; 
        fireTableCellUpdated(row, column);
      }
    };
    // Create JTable
    table = new JTable(dataModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    centerScrollPane = new JScrollPane(table);
    centerPanel.add(centerScrollPane, BorderLayout.CENTER);
//  KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new JTableFocusChangeListener(table));
  }

  private void addLineInTable(String mn,
                              String lat,
                              String lng)
  {
    int len = data.length;
    Object[][] newData = new Object[len + 1][names.length];
    for (int i=0; i<len; i++)
    {
      for (int j=0; j<names.length; j++)
        newData[i][j] = data[i][j];
    }
    newData[len][0] = mn;
    newData[len][1] = lat;
    newData[len][2] = lng;
    data = newData;
    ((AbstractTableModel)dataModel).fireTableDataChanged();
    table.repaint();
  }

  public TideStation getSelectedStationData()
  {
    TideStation ts = null;
    int sr = table.getSelectedRow();
    if (sr >= 0)
    {
      for (TideStation sd : stationData)
      {
        if (sd.getFullName().equals(data[sr][0]))
        {
          ts = sd;
          break;
        }
      }
    }
    return ts;
  }
  
  private void setSelection()
  {
    String fieldContent = filterTextField.getText();

    TideContext.getInstance().fireFilter(fieldContent);

    String patternStr = ".*" + fieldContent + ".*";
    Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);

    data = new Object[0][names.length];
    int nbl = 0;
    for (TideStation sd : stationData)
    {      
      Matcher m = p.matcher(sd.getFullName());
      if (m.matches())
      {
        // Add in table
        nbl++;
        addLineInTable(sd.getFullName(), 
                       GeomUtil.decToSex(sd.getLatitude(), GeomUtil.SWING, GeomUtil.NS), 
                       GeomUtil.decToSex(sd.getLongitude(), GeomUtil.SWING, GeomUtil.EW));
      }
    }
    this.setStatusLabel(Integer.toString(nbl) + " station(s)");
    ((AbstractTableModel)dataModel).fireTableDataChanged();
    table.repaint();
  }
  
  public void setStationData(ArrayList<TideStation> stationData)
  {
    this.stationData = stationData;
    this.filterLabel.setEnabled(this.stationData != null);
    this.filterTextField.setEnabled(this.stationData != null);
    setValues();
  }

  public ArrayList<TideStation> getStationData()
  {
    return stationData;
  }
  
  public class SelectionListener implements ListSelectionListener
  {
    JTable table;
    
    SelectionListener(JTable table) 
    {
      this.table = table;
    }
    
    public void valueChanged(ListSelectionEvent lse) 
    {
      int selectedRow = table.getSelectedRow();
      if (selectedRow < 0)
        ;
      else
        ;
    }    
  }
  
  public void setStatusLabel(String s)
  {
    this.statusLabel.setText(s);
  }
}