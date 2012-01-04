package tideengineimplementation.utils;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import tideengine.BackEndTideComputer;
import tideengine.BackEndXMLTideComputer;
import tideengine.TideStation;

import tideengineimplementation.gui.table.FilterTable;

import user.util.GeomUtil;


public class StationPositions
{
  public static void main(String[] args) throws Exception
  {
    List<TideStation> stationData = BackEndTideComputer.getStationData();
    boolean display = false;
    if (display)
    {
      for (TideStation sd : stationData)
        System.out.println(sd.getFullName() + "\n" +
                           sd.getNameParts().get(sd.getNameParts().size() - 1) + ", \t" + 
                           GeomUtil.decToSex(sd.getLatitude(), GeomUtil.SHELL, GeomUtil.NS)  + " - " + 
                           GeomUtil.decToSex(sd.getLongitude(), GeomUtil.SHELL, GeomUtil.EW));
    }
    System.out.println("There are " + stationData.size() + " Stations.");
    
    // Reg Exp test (for finder)
    long before = System.currentTimeMillis();
    try
    {
      String patternStr = ".*OYSTER.*";
//    String patternStr = ".*Marina.*";
      Pattern p = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
      for (TideStation sd : stationData)
      {      
        Matcher m = p.matcher(sd.getFullName());
        if (m.matches())
          System.out.println("-> " + sd.getFullName());
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    long after = System.currentTimeMillis();
    System.out.println("Found match in " + Long.toString(after - before) + " ms.");
    
    // For the finder
    FilterTable ft = new FilterTable(stationData);
    JOptionPane.showConfirmDialog(null, ft, "Station Data", JOptionPane.PLAIN_MESSAGE);
    TideStation ts = ft.getSelectedStationData();
    if (ts != null)
      System.out.println("Selected " + ts.getFullName());
    else
      System.out.println("Nothing seledcted!");
  }
}
