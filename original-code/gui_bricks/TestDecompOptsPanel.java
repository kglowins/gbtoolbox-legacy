package gui_bricks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;


public class TestDecompOptsPanel extends JPanel {
	private JTextField twistAngleFld;
	private JTextField tiltAngleFld;
	
	private YesNoLabel tiltYN;
	private YesNoLabel twistYN;
	
	private JCheckBox useCb;
	private JTextField maxAngleFld;
	
	
	private final void setFieldsEnabled(boolean b) {
		
		maxAngleFld.setEnabled(b);

	}
	
	
	public TestDecompOptsPanel() {
		
		
		setLayout(new MigLayout("insets 0", "[][][][][][]", "[]"));
		
		useCb = new JCheckBox("<html><font color=#0000cc>Use Fortes decomposition and assume the tolerance of</font>");
		useCb.setToolTipText("The boundary will be decomposed into its tilt and twist components");
		add(useCb, "flowx,cell 0 0 2 1");
		
		useCb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox) evt.getSource();
				if(src.isSelected()) {
					setFieldsEnabled(true);
				} else {
					setFieldsEnabled(false);
				}
			}
		});
		
		maxAngleFld = new JTextField();
		maxAngleFld.setText("7");
		maxAngleFld.setToolTipText("Maximum acceptable angle of a twist or tilt component, respectively.");
		maxAngleFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(maxAngleFld, "cell 0 0 2 1,aligny top");
		maxAngleFld.setColumns(3);
		
		JLabel bracket1Lbl = new JLabel("<html><font color=#0000cc>\u00B0:<sub>&nbsp;</sub></font>");
		add(bracket1Lbl, "cell 0 0 2 1");
		
		JLabel tiltLbl = new JLabel("<html><b>Tilt</b>?");
		tiltLbl.setToolTipText("Is the tested boundary a tilt boundary?");
		add(tiltLbl, "flowx,cell 2 0,gapx 20");
		
		tiltYN = new YesNoLabel();
		add(tiltYN, "flowx,cell 3 0");
		
	
		JLabel twistLbl = new JLabel("<html><b>Twist</b>?");
		twistLbl.setToolTipText("Is the tested boundary a twist boundary?");
		add(twistLbl, "cell 4 0,gapx 20");
		
		twistYN = new YesNoLabel();
		add(twistYN, "flowx,cell 5 0");
		
		JLabel twistAngleLbl = new JLabel("(\u03BD =");
		twistAngleLbl.setToolTipText("Angle of the twist component");
		add(twistAngleLbl, "cell 3 0,gapx 5");
		
		twistAngleFld = new JTextField();
		twistAngleFld.setToolTipText("Angle of the twist component");
		twistAngleFld.setHorizontalAlignment(SwingConstants.RIGHT);
		twistAngleFld.setEditable(false);
		add(twistAngleFld, "cell 3 0");
		twistAngleFld.setColumns(4);
		
		JLabel bra2Lbl = new JLabel("\u00B0);");
		bra2Lbl.setToolTipText("The angle of the twist component");
		add(bra2Lbl, "cell 3 0");
		
		JLabel tiltAngleLbl = new JLabel("(\u03BB =");
		tiltAngleLbl.setToolTipText("Angle of the tilt component");
		add(tiltAngleLbl, "cell 5 0,gapx 5");
		
		tiltAngleFld = new JTextField();
		tiltAngleFld.setToolTipText("Angle of the tilt component");
		tiltAngleFld.setHorizontalAlignment(SwingConstants.RIGHT);
		tiltAngleFld.setEditable(false);
		add(tiltAngleFld, "cell 5 0");
		tiltAngleFld.setColumns(4);
		
		JLabel bra3Lbl = new JLabel("\u00B0);");
		bra3Lbl.setToolTipText("The angle of the tilt component");
		add(bra3Lbl, "cell 5 0");
		
		setFieldsEnabled(false);

	}
	
	
	public final void reset() {
		twistAngleFld.setText("");
		tiltAngleFld.setText("");
		
		tiltYN.setEmpty();
		twistYN.setEmpty();
	}
	
	
	public final double getMaxAngle() {
		String s = maxAngleFld.getText();
		s = s.replace(",", ".");
		return Double.parseDouble(s);
	}
	

	
	public final boolean isChecked() {
		return useCb.isSelected();
	}
	
	public final void setTilt(boolean yes) {
		if(yes) tiltYN.setYes(); else tiltYN.setNo();
	}
	
	public final void setTwist(boolean yes) {
		if(yes) twistYN.setYes(); else twistYN.setNo();
	}
	
	public final void setTiltAngle(double angle) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double w = Math.toDegrees(angle);		
		tiltAngleFld.setText(df.format(w));		
		tiltAngleFld.setCaretPosition(0);			
	}
	
	public final void setTwistAngle(double angle) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double w = Math.toDegrees(angle);		
		twistAngleFld.setText(df.format(w));		
		twistAngleFld.setCaretPosition(0);	
	}

}
