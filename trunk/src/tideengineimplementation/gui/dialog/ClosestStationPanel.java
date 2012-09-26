package tideengineimplementation.gui.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ClosestStationPanel
  extends JPanel
{
  @SuppressWarnings("compatibility:7672932141139302930")
  public final static long serialVersionUID = 1L;

  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel titleLabel = new JLabel();
  private JLabel positionLabel = new JLabel();
  private JLabel nbStationLabel = new JLabel();
  private JFormattedTextField nbStationFormattedTextField = new JFormattedTextField(new DecimalFormat("###0"));

  private String posStr = "";
  
  public ClosestStationPanel(String pos)
  {
    this.posStr = pos;
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
    titleLabel.setText("Find closest Stations from");
    titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    positionLabel.setText(this.posStr);
    positionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    nbStationLabel.setText("Number of stations");
    nbStationFormattedTextField.setMinimumSize(new Dimension(50, 19));
    nbStationFormattedTextField.setPreferredSize(new Dimension(50, 19));
    nbStationFormattedTextField.setText("50");
    nbStationFormattedTextField.setHorizontalAlignment(JTextField.CENTER);
    this.add(titleLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(positionLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(nbStationLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 5), 0, 0));
    this.add(nbStationFormattedTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
  }
  
  public int getNbStation()
  {
    return Integer.parseInt(nbStationFormattedTextField.getText());
  }
}
