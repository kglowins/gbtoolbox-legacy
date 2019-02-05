package com.github.kglowins.gbtoolbox.gui_bricks;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import com.github.kglowins.gbtoolbox.algorithms.IndividualGBTester;


public class TestMinimizationOptsPanel extends JPanel {
	private JTextField tiltFld;
	private JTextField twistFld;
	private JTextField symFld;
	private JTextField iQuasiFld;
	
	private YesNoLabel tiltYN;
	private YesNoLabel twistYN;
	
	private JCheckBox useCb;
	private JTextField devFld;
	
	private JButton saveTiltBtn;
	private JButton saveTwistBtn;
	private JButton saveSymBtn;
	private JButton saveImpropQuasiBtn;
	
	public final void setButtonsEnabled(boolean b) {
		
		saveTiltBtn.setEnabled(b);
		saveTwistBtn.setEnabled(b);
		saveSymBtn.setEnabled(b);
		saveImpropQuasiBtn.setEnabled(b);				
	}
	
	private final void setFieldsEnabled(boolean b) {
		
		devFld.setEnabled(b);
	}
	
	
	private Clipboard clipboard;
	private IndividualGBTester analyzer;
	
	private LatticePanel latticePane;
	private JLabel bra1Lbl;
	private JLabel lblnbsp;
	private JLabel lblnbsp_1;
	private JLabel lblnbsp_2;
	private JLabel lblnbsp_3;
	private YesNoLabel symYN;
	private YesNoLabel iQuasiYN;


	public TestMinimizationOptsPanel(final Clipboard clipboard, final IndividualGBTester analyzer) {
		
		this.clipboard = clipboard;
		this.analyzer = analyzer;
	//	latticePane = lattice;
		setLayout(new MigLayout("insets 0", "[][][][][][][][]", "[][]"));
		
		useCb = new JCheckBox("<html><font color=#0000cc>Use distances in the boundary space computed via numerical minimization and assume the tolerance of</font>");
		useCb.setToolTipText("Distances (defined in the 5D boundary space) to the nearest characteristic boundaries will be computed.");
		add(useCb, "flowx,cell 0 0 8 1,alignx left,gapy 0 5");
		
				
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
		
		JLabel tiltLbl = new JLabel("<html><b>Tilt</b>?");
		tiltLbl.setToolTipText("Is the tested boundary a tilt boundary?");
		add(tiltLbl, "flowx,cell 0 1");
		
		tiltYN = new YesNoLabel();
		add(tiltYN, "flowx,cell 1 1");
		
		JLabel bra2Lbl = new JLabel("<html>(\u03B4<sub>L</sub> =");
		bra2Lbl.setToolTipText("Distance to the nearest pure-tilt boundary");
		add(bra2Lbl, "cell 1 1,gapx 5");
		
		tiltFld = new JTextField();
		tiltFld.setToolTipText("Distance to the nearest pure-tilt boundary");
		tiltFld.setEditable(false);
		tiltFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(tiltFld, "cell 1 1");
		tiltFld.setColumns(4);
		
		JLabel bra3Lbl = new JLabel("\u00B0");
		bra3Lbl.setToolTipText("");
		add(bra3Lbl, "cell 1 1");
		
		saveTiltBtn = new JButton();
		saveTiltBtn.setToolTipText("Save the nearest tilt boundary to the clipboard");
		saveTiltBtn.setIcon(new ImageIcon(TestMinimizationOptsPanel.class.getResource("/gui_bricks/save.png")));
		saveTiltBtn.setPreferredSize(new Dimension(24, 24));
		saveTiltBtn.setMinimumSize(new Dimension(24, 24));
		saveTiltBtn.setMaximumSize(new Dimension(24, 24));
		add(saveTiltBtn, "cell 1 1");
		
		
		saveTiltBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clipboard.saveGB(analyzer.getNearestTilt());
			}
		});
		
		JLabel twistLbl = new JLabel("<html><b>Twist</b>?");
		twistLbl.setToolTipText("Is the tested boundary a twist boundary?");
		add(twistLbl, "flowx,cell 2 1,gapx 20");
		
		twistYN = new YesNoLabel();
		add(twistYN, "flowx,cell 3 1");
		
		JLabel bra4Lbl = new JLabel("<html>(\u03B4<sub>N</sub> =");
		bra4Lbl.setToolTipText("Distance to the nearest pure-twist boundary");
		add(bra4Lbl, "cell 3 1,gapx 5");
		
		twistFld = new JTextField();
		twistFld.setToolTipText("Distance to the nearest pure-twist boundary");
		twistFld.setHorizontalAlignment(SwingConstants.RIGHT);
		twistFld.setEditable(false);
		add(twistFld, "cell 3 1");
		twistFld.setColumns(4);
		
		JLabel bra5Lbl = new JLabel("\u00B0");
		bra5Lbl.setToolTipText("");
		add(bra5Lbl, "cell 3 1");
		
		saveTwistBtn = new JButton();
		saveTwistBtn.setToolTipText("Save the nearest twist boundary to the clipboard");
		saveTwistBtn.setIcon(new ImageIcon(TestMinimizationOptsPanel.class.getResource("/gui_bricks/save.png")));
		saveTwistBtn.setPreferredSize(new Dimension(24, 24));
		saveTwistBtn.setMinimumSize(new Dimension(24, 24));
		saveTwistBtn.setMaximumSize(new Dimension(24, 24));
		add(saveTwistBtn, "cell 3 1");
		
		saveTwistBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clipboard.saveGB(analyzer.getNearestTwist());				
			}
		});
		
		JLabel symLbl = new JLabel("<html><b>Symmetric</b>?");
		symLbl.setToolTipText("Is the tested boundary a symmetric boundary?");
		add(symLbl, "flowx,cell 4 1,gapx 20");
		
		symYN = new YesNoLabel();
		add(symYN, "flowx,cell 5 1");
		
		JLabel bra6Lbl = new JLabel("<html>(\u03B4<sub>S</sub> =");
		bra6Lbl.setToolTipText("Distance to the nearest pure-symmetric boundary");
		add(bra6Lbl, "cell 5 1,gapx 5");
		
		JLabel iQuasiLbl = new JLabel("<html><b>180\u00b0-tilt</b>?");
		iQuasiLbl.setToolTipText("Is the tested boundary a 180\u00B0-tilt boundary?");
		add(iQuasiLbl, "flowx,cell 6 1,gapx 20");

		

		
		useCb.setSelected(true);
		
		devFld = new JTextField();
		devFld.setText("5");
		devFld.setHorizontalAlignment(SwingConstants.RIGHT);
		devFld.setToolTipText("Maximum acceptable distance from a pure tilt boundary");
		add(devFld, "cell 0 0 4 1,aligny top");
		devFld.setColumns(3);
		
		bra1Lbl = new JLabel("<html><font color=#0000cc>\u00B0:<sub>&nbsp;</sub></font>");
		add(bra1Lbl, "cell 0 0 4 1");
		
		symFld = new JTextField();
		symFld.setToolTipText("Distance to the nearest pure-symmetric boundary");
		symFld.setHorizontalAlignment(SwingConstants.RIGHT);
		symFld.setEditable(false);
		add(symFld, "cell 5 1");
		symFld.setColumns(4);
		
		JLabel bra7Lbl = new JLabel("\u00B0");
		bra7Lbl.setToolTipText("");
		add(bra7Lbl, "cell 5 1");
		
		saveSymBtn = new JButton();
		saveSymBtn.setToolTipText("Save the nearest symmetric boundary to the clipboard");
		saveSymBtn.setIcon(new ImageIcon(TestMinimizationOptsPanel.class.getResource("/gui_bricks/save.png")));
		saveSymBtn.setPreferredSize(new Dimension(24, 24));
		saveSymBtn.setMinimumSize(new Dimension(24, 24));
		saveSymBtn.setMaximumSize(new Dimension(24, 24));
		add(saveSymBtn, "cell 5 1");
		
		iQuasiYN = new YesNoLabel();
		add(iQuasiYN, "flowx,cell 7 1");
		
		JLabel bra10Lbl = new JLabel("<html>(\u03B4<sub>I</sub> =");
		bra10Lbl.setToolTipText("Distance to the nearest pure improperly quasi-symmetric (180\u00B0-tilt) boundary");
		add(bra10Lbl, "cell 7 1,gapx 5");
		
		iQuasiFld = new JTextField();
		iQuasiFld.setToolTipText("Distance to the nearest pure improperly quasi-symmetric (180\u00B0-tilt) boundary");
		iQuasiFld.setHorizontalAlignment(SwingConstants.RIGHT);
		iQuasiFld.setEditable(false);
		add(iQuasiFld, "cell 7 1");
		iQuasiFld.setColumns(4);
		
		JLabel label_1 = new JLabel("\u00B0");
		label_1.setToolTipText("");
		add(label_1, "cell 7 1");
		
		saveImpropQuasiBtn = new JButton();
		saveImpropQuasiBtn.setEnabled(false);
		saveImpropQuasiBtn.setToolTipText("Save the nearest 180\u00B0-tilt boundary to the clipboard");
		saveImpropQuasiBtn.setIcon(new ImageIcon(TestMinimizationOptsPanel.class.getResource("/gui_bricks/save.png")));
		saveImpropQuasiBtn.setPreferredSize(new Dimension(24, 24));
		saveImpropQuasiBtn.setMinimumSize(new Dimension(24, 24));
		saveImpropQuasiBtn.setMaximumSize(new Dimension(24, 24));
		add(saveImpropQuasiBtn, "cell 7 1");
		
		lblnbsp = new JLabel("<html>);<sub>&nbsp;</sub>");
		add(lblnbsp, "cell 1 1");
		
		lblnbsp_1 = new JLabel("<html>);<sub>&nbsp;</sub>");
		add(lblnbsp_1, "cell 3 1");
		
		lblnbsp_2 = new JLabel("<html>);<sub>&nbsp;</sub>");
		add(lblnbsp_2, "cell 5 1");
		
		lblnbsp_3 = new JLabel("<html>);<sub>&nbsp;</sub>");
		add(lblnbsp_3, "cell 7 1");
		
		saveImpropQuasiBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clipboard.saveGB(analyzer.getNearestImprop());
			}
		});
		
		saveSymBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clipboard.saveGB(analyzer.getNearestSymmetric());			
			}
		});
		
		setFieldsEnabled(true);
		setButtonsEnabled(false);
	}
	
	
	public final void reset() {
		tiltFld.setText("");
		twistFld.setText("");
		symFld.setText("");
		iQuasiFld.setText("");
		tiltYN.setEmpty();
		twistYN.setEmpty();
		symYN.setEmpty();
		iQuasiYN.setEmpty();
	}
	
	
	
	public final double getDeviation() {
		String s = devFld.getText();
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
	
	public final void setSymmetric(boolean yes) {
		if(yes) symYN.setYes(); else symYN.setNo();
	}
		
	public final void setImpropQuasi(boolean yes) {
		if(yes) iQuasiYN.setYes(); else iQuasiYN.setNo();
	}

	
	public final void setTiltDist(double dist) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double d = Math.toDegrees(dist);		
		tiltFld.setText(df.format(d));		
		tiltFld.setCaretPosition(0);			
	}
	
	public final void setTwistDist(double dist) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
			
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		double d = Math.toDegrees(dist);		
		twistFld.setText(df.format(d));		
		twistFld.setCaretPosition(0);	
	}
	
	public final void setSymmetricDist(double dist) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		 		
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double d = Math.toDegrees(dist);		
		symFld.setText(df.format(d));		
		symFld.setCaretPosition(0);	
	}	
	
	public final void setImpropQuasiDist(double dist) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double d = Math.toDegrees(dist);		
		iQuasiFld.setText(df.format(d));		
		iQuasiFld.setCaretPosition(0);		
	}
}
