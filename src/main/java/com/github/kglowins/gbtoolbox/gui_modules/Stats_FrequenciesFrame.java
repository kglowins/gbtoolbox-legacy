package com.github.kglowins.gbtoolbox.gui_modules;

import com.github.kglowins.gbtoolbox.enums.PointGroup;
import com.github.kglowins.gbtoolbox.gui_bricks.LatticeParams_CDivAPanel;
import com.github.kglowins.gbtoolbox.gui_bricks.LatticeParams_NonePanel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.math3.util.FastMath;
import org.math.plot.Plot2DPanel;
import org.math.plot.utils.Array;

import com.github.kglowins.gbtoolbox.algorithms.CSLMisorientations;
import com.github.kglowins.gbtoolbox.algorithms.IndividualGBTester;

import com.github.kglowins.gbtoolbox.utils.CSLMisor;
import com.github.kglowins.gbtoolbox.utils.ConstantsAndStatic;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.GBDatHeader;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MyMath;
import com.github.kglowins.gbtoolbox.utils.Transformations;
import com.github.kglowins.gbtoolbox.utils.UnitVector;
import net.miginfocom.swing.MigLayout;

public class Stats_FrequenciesFrame extends JFrame {

	private JPanel contentPane;

	private static final double INFTY = Double.MAX_VALUE;
	private static final int A_LOT = Integer.MAX_VALUE;

	private static final String M3M = "m-3m";
	private static final String _6MMM = "6/mmm";
	//private static final String _4MMM = "4/mmm";
	private static final String MMM = "mmm";

	private JTextField tiltTolFld;
	private JTextField twistTolFld;
	private JTextField symTolFld;
	private JTextField impropTolFld;
	private JTextField maxTwistAngFld;
	private JTextField maxTiltAngFld;
	private JTextField omega0Fld;
	private JTextField pFld;
	private JTextField sigmaMaxFld;
	private JTextField resultFld;
	
	private int nTotal;

	private JPanel latticeCards;
	private LatticeParams_NonePanel noDetails; // m3m
	private LatticeParams_CDivAPanel cDivA; // 6/mmm 4/mmm
	//private LatticeParams_ABCPanel abcPane; //mmm

	private ArrayList<GBDatHeader> gbFiles;

	private boolean procTiltDist;
	private boolean procTwistDist;
	private boolean procSymDist;
	private boolean procImpropDist;	
	private boolean procTiltAngle;
	private boolean procTwistAngle;
	
	private boolean procTiltTTC;
	private boolean procTwistTTC;
	private boolean procSymTTC;
	private boolean procImpropTTC;

	private double areaThr = INFTY;
	
	private JButton drawTiltDistBtn;
	private JButton drawTwistDistBtn;
	private JButton drawSymDistBtn;
	private JButton drawImpropDistBtn;
	private JButton drawTwistAngleBtn;
	private JButton drawTiltAngleBtn;
	private JButton sigmaHistBtn;

	private JRadioButton allSigmaRb;
	private JRadioButton onlySigmaRb;

	private JCheckBox tiltDistChB;
	private JCheckBox twistDistChB;
	private JCheckBox symDistChB;
	private JCheckBox impropDistChB;
	private JCheckBox tiltAngleChB;
	private JCheckBox twistAngleChB;
	private JCheckBox cslChB;

	private double tiltTol;
	private double twistTol;
	private double symTol;
	private double impropTol;
	private double maxTiltAngle;
	private double maxTwistAngle;
	
	private double maxTiltTtc;
	private double maxTwistTtc;
	
	private double maxSymTtc;
	private double maxImpropTtc;
	
	private int sigmaChosen;

	private double weightTotal = 0d;
	private double weightAccepted = 0d;
	private int N = 0;

	private JTextField onlySigmaFld;

	private JButton abortBtn;
	private JButton processBtn;
	private JProgressBar progressBar;

	private FrequenciesTask task;
	private IndividualGBTester tester;

	private Plot2DPanel plot = null;
	private String histFrameTitle = null; 
	
	private boolean eliminate;
	private double areaThreshold;
	private JLabel omitLbl;
	private JSeparator separator;
	private JSeparator separator_4;
	private JCheckBox tiltTtcChB;
	private JLabel lblububmax;
	private JTextField tiltTtcFld;
	private JLabel label_1;
	private JButton drawMaxTtcBtn;
	private JCheckBox twistTtcChB;
	private JLabel lblmin;
	private JTextField twistTtcFld;
	private JLabel label_3;
	private JButton drawMinTtcBtn;
	private JCheckBox symTtcChB;
	private JLabel lblslt;
	private JTextField symTtcFld;
	private JLabel label_2;
	private JButton drawSymTtcBtn;
	private JCheckBox impropTtcChB;
	private JLabel lblilt;
	private JTextField impropTtcFld;
	private JLabel label_5;
	private JButton drawImpropTtcBtn;

	public Stats_FrequenciesFrame(ArrayList<GBDatHeader> gbs, boolean elimin, double areaThr,
			boolean procTiltDist, boolean procTwistDist, boolean procSymDist, boolean procImpropDist,
			boolean procTiltAngle, boolean procTwistAngle,
			boolean procTiltTTC, boolean procTwistTTC, boolean procSymTTC, boolean procImpropTTC
			) 
	{
		setTitle("GBToolbox: Frequencies of occurrence of boundaries of particular geometries");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_FrequenciesFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	
	
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		
		this.procTiltDist = procTiltDist;
		this.procTwistDist = procTwistDist;
		this.procSymDist = procSymDist;
		this.procImpropDist = procImpropDist;	
		this.procTiltAngle = procTiltAngle;
		this.procTwistAngle = procTwistAngle;
		
		this.procTiltTTC = procTiltTTC;
		this.procTwistTTC = procTwistTTC;
		this.procSymTTC = procSymTTC;
		this.procImpropTTC = procImpropTTC;
						
		this.eliminate = elimin;
		this.areaThreshold = areaThr;
		
		gbFiles = gbs;
		
		nTotal = 0;
		for(GBDatHeader head : gbFiles) nTotal += head.getNumberOfGBs();
			
		sigmaChosen = Integer.MAX_VALUE;

		contentPane.setLayout(new MigLayout("", "[][][][][][]", "[][][][][][][][][][][][][][][][]"));
		
		omitLbl = new JLabel();
		if(elimin) omitLbl.setText("<html><font color=#0000ff>Mesh triangles with area greater than " + areaThr + " will not be taken into account</i></font>");
		else omitLbl.setText("<html><font color=#0000ff>All mesh triangles will be taken into account</i></font>");
	
		contentPane.add(omitLbl, "cell 0 0 6 1");
		
		separator = new JSeparator();
		contentPane.add(separator, "cell 0 1 6 1,growx,aligny center,gapy 5 5");

		JLabel countLbl = new JLabel("<html><b>Count boundaries which are simultaneously (assuming criteria specified in brackets)</b>:");
		contentPane.add(countLbl, "cell 0 2 6 1");

		tiltDistChB = new JCheckBox("<html><u>tilt</u> ");
		tiltDistChB.setEnabled(false);
		contentPane.add(tiltDistChB, "cell 0 3,gapx 10,gapy 5");

		JLabel dLbl1 = new JLabel("<html>(\u03B4<sub>L</sub> &lt;");
		dLbl1.setToolTipText("Maximum allowed distance from a pure tilt boundary");
		contentPane.add(dLbl1, "flowx,cell 1 3,alignx right,gapx 20,gapy 5");

		drawTiltDistBtn = new JButton();
		drawTiltDistBtn.setToolTipText("Distribution of distances to the nearest pure tilt boundaries");
		drawTiltDistBtn.setEnabled(false);
		drawTiltDistBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		contentPane.add(drawTiltDistBtn, "cell 2 3,gapx 10,gapy 5");
		drawTiltDistBtn.setMinimumSize(new Dimension(24,24));
		drawTiltDistBtn.setMaximumSize(new Dimension(24,24));
		drawTiltDistBtn.setPreferredSize(new Dimension(24,24));


		drawTiltDistBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(1);
				} catch(Exception e) {
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}
			}
		});


		twistDistChB = new JCheckBox("<html><u>twist</u>");
		twistDistChB.setEnabled(false);
		contentPane.add(twistDistChB, "cell 3 3,gapx 20,gapy 5");

		JLabel dLbl2 = new JLabel("<html>(\u03B4<sub>N</sub> &lt;");
		dLbl2.setToolTipText("Maximum allowed distance from a pure twist boundary");
		contentPane.add(dLbl2, "flowx,cell 4 3,alignx right,gapx 20,gapy 5");

		drawTwistDistBtn = new JButton();
		drawTwistDistBtn.setToolTipText("Distribution of distances to the nearest pure twist boundaries");
		drawTwistDistBtn.setEnabled(false);
		drawTwistDistBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawTwistDistBtn.setPreferredSize(new Dimension(24, 24));
		drawTwistDistBtn.setMinimumSize(new Dimension(24, 24));
		drawTwistDistBtn.setMaximumSize(new Dimension(24, 24));
		contentPane.add(drawTwistDistBtn, "cell 5 3,gapx 10,gapy 5");

		drawTwistDistBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(2);
				} catch(Exception e) {
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}
			}
		});

		symDistChB = new JCheckBox("<html><u>symmetric</u>");
		symDistChB.setEnabled(false);
		contentPane.add(symDistChB, "cell 0 4,gapx 10");

		JLabel dLbl3 = new JLabel("<html>(\u03B4<sub>S</sub> &lt;");
		dLbl3.setToolTipText("Maximum allowed distance from a pure symmetric boundary");
		contentPane.add(dLbl3, "flowx,cell 1 4,alignx right,gapx 20");

		drawSymDistBtn = new JButton();
		drawSymDistBtn.setToolTipText("Distribution of distances to the nearest pure symmetric boundaries");
		drawSymDistBtn.setEnabled(false);
		drawSymDistBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawSymDistBtn.setPreferredSize(new Dimension(24, 24));
		drawSymDistBtn.setMinimumSize(new Dimension(24, 24));
		drawSymDistBtn.setMaximumSize(new Dimension(24, 24));
		contentPane.add(drawSymDistBtn, "cell 2 4,gapx 10");

		drawSymDistBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(3);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});

		impropDistChB = new JCheckBox("<html><u>improperly quasi-symmetric</u>");
		impropDistChB.setEnabled(false);
		contentPane.add(impropDistChB, "cell 3 4,gapx 20");

		JLabel dLbl4 = new JLabel("<html>(\u03B4<sub>I</sub> &lt;");
		dLbl4.setToolTipText("Maximum allowed distance from a pure improperly quasi-symmetric boundary");
		contentPane.add(dLbl4, "flowx,cell 4 4,alignx right,gapx 20");

		drawImpropDistBtn = new JButton();
		drawImpropDistBtn.setToolTipText("Distribution of distances to the nearest pure improperly quasi-symmetric boundaries");
		drawImpropDistBtn.setEnabled(false);
		drawImpropDistBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawImpropDistBtn.setPreferredSize(new Dimension(24, 24));
		drawImpropDistBtn.setMinimumSize(new Dimension(24, 24));
		drawImpropDistBtn.setMaximumSize(new Dimension(24, 24));
		contentPane.add(drawImpropDistBtn, "cell 5 4,gapx 10");

		drawImpropDistBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(4);
				} catch(Exception e) {
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});

		JSeparator separator_3 = new JSeparator();
		contentPane.add(separator_3, "cell 0 5 6 1,growx,gapx 10,aligny center,gapy 5 5");
		
		tiltTtcChB = new JCheckBox("<html><u>tilt</u> (TTC)");
		tiltTtcChB.setEnabled(false);
		contentPane.add(tiltTtcChB, "cell 0 6,gapx 10");
		
		lblububmax = new JLabel("<html>(\u03B1<sub>L</sub> &lt;");
		lblububmax.setToolTipText("<html>Maximum allowed value for \u03B1<sub>L</sub>");
		contentPane.add(lblububmax, "flowx,cell 1 6,alignx right,gapx 20");
		
		drawMaxTtcBtn = new JButton();
		drawMaxTtcBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawMaxTtcBtn.setToolTipText("Distribution of maximum 'tilt/twist component parameters'");
		drawMaxTtcBtn.setPreferredSize(new Dimension(24, 24));
		drawMaxTtcBtn.setMinimumSize(new Dimension(24, 24));
		drawMaxTtcBtn.setMaximumSize(new Dimension(24, 24));
		drawMaxTtcBtn.setEnabled(false);
		contentPane.add(drawMaxTtcBtn, "cell 2 6,gapx 10");
		
		drawMaxTtcBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(8);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		
		twistTtcChB = new JCheckBox("<html><u>twist</u> (TTC)");
		twistTtcChB.setEnabled(false);
		contentPane.add(twistTtcChB, "cell 3 6,gapx 20");
		
		lblmin = new JLabel("<html>(\u03B1<sub>N</sub> &lt;");
		lblmin.setToolTipText("<html>Maximum allowed value for \u03B1<sub>N</sub>");
		contentPane.add(lblmin, "flowx,cell 4 6,alignx right,gapx 20");
		
		drawMinTtcBtn = new JButton();
		drawMinTtcBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawMinTtcBtn.setToolTipText("Distribution of minimum 'tilt/twist component parameters'");
		drawMinTtcBtn.setPreferredSize(new Dimension(24, 24));
		drawMinTtcBtn.setMinimumSize(new Dimension(24, 24));
		drawMinTtcBtn.setMaximumSize(new Dimension(24, 24));
		drawMinTtcBtn.setEnabled(false);
		contentPane.add(drawMinTtcBtn, "cell 5 6,gapx 10");
		
		drawMinTtcBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(7);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		
		symTtcChB = new JCheckBox("<html><u>symmetric</u> (TTC)");
		symTtcChB.setEnabled(false);
		contentPane.add(symTtcChB, "cell 0 7,gapx 10");
		
		lblslt = new JLabel("<html>(\u03B1<sub>S</sub> &lt;");
		lblslt.setToolTipText("<html>Maximum allowed value for \u03B1<sub>S</sub>");
		contentPane.add(lblslt, "flowx,cell 1 7,alignx right,aligny bottom");
		
		drawSymTtcBtn = new JButton();
		drawSymTtcBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawSymTtcBtn.setToolTipText("<html>Distribution of \u03B1<sub>S</sub>");
		drawSymTtcBtn.setPreferredSize(new Dimension(24, 24));
		drawSymTtcBtn.setMinimumSize(new Dimension(24, 24));
		drawSymTtcBtn.setMaximumSize(new Dimension(24, 24));
		drawSymTtcBtn.setEnabled(false);
		contentPane.add(drawSymTtcBtn, "cell 2 7,gapx 10");
		
		impropTtcChB = new JCheckBox("<html><u>improperly quasi-symmetric</u> (TTC)");
		impropTtcChB.setEnabled(false);
		contentPane.add(impropTtcChB, "cell 3 7,gapx 20");
		
		lblilt = new JLabel("<html>(\u03B1<sub>I</sub> &lt;");
		lblilt.setToolTipText("<html>Maximum allowed value for \u03B1<sub>I</sub>");
		contentPane.add(lblilt, "flowx,cell 4 7,alignx right,aligny bottom");
		
		drawImpropTtcBtn = new JButton();
		drawImpropTtcBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawImpropTtcBtn.setToolTipText("<html>Distribution of \u03B1<sub>I</sub>");
		drawImpropTtcBtn.setPreferredSize(new Dimension(24, 24));
		drawImpropTtcBtn.setMinimumSize(new Dimension(24, 24));
		drawImpropTtcBtn.setMaximumSize(new Dimension(24, 24));
		drawImpropTtcBtn.setEnabled(false);
		contentPane.add(drawImpropTtcBtn, "cell 5 7,gapx 10");
		
		separator_4 = new JSeparator();
		contentPane.add(separator_4, "cell 0 8 6 1,growx,gapx 10,aligny center,gapy 5 5");

		tiltAngleChB = new JCheckBox("<html><u>tilt</u> (Fortes)");
		tiltAngleChB.setEnabled(false);
		contentPane.add(tiltAngleChB, "cell 0 9,gapx 10");

		JLabel nuLbl = new JLabel("(\u03BD <");
		nuLbl.setToolTipText("Maximum allowed angle of a twist component");
		contentPane.add(nuLbl, "flowx,cell 1 9,alignx right,gapx 20");

		drawTwistAngleBtn = new JButton();
		drawTwistAngleBtn.setToolTipText("Distribution of angles of twist components");
		drawTwistAngleBtn.setEnabled(false);
		drawTwistAngleBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawTwistAngleBtn.setPreferredSize(new Dimension(24, 24));
		drawTwistAngleBtn.setMinimumSize(new Dimension(24, 24));
		drawTwistAngleBtn.setMaximumSize(new Dimension(24, 24));
		contentPane.add(drawTwistAngleBtn, "cell 2 9,gapx 10");

		drawTwistAngleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(6);
				} catch(Exception e) {
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});

		twistAngleChB = new JCheckBox("<html><u>twist</u> (Fortes)");
		twistAngleChB.setEnabled(false);
		contentPane.add(twistAngleChB, "cell 3 9,gapx 20");

		JLabel lambdaLbl = new JLabel("(\u03BB <");
		lambdaLbl.setToolTipText("Maximum allowed angle of a tilt component");
		contentPane.add(lambdaLbl, "flowx,cell 4 9,alignx right,gapx 20");

		drawTiltAngleBtn = new JButton();
		drawTiltAngleBtn.setToolTipText("Distribution of angles of tilt components");
		drawTiltAngleBtn.setEnabled(false);
		drawTiltAngleBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		drawTiltAngleBtn.setPreferredSize(new Dimension(24, 24));
		drawTiltAngleBtn.setMinimumSize(new Dimension(24, 24));
		drawTiltAngleBtn.setMaximumSize(new Dimension(24, 24));
		contentPane.add(drawTiltAngleBtn, "cell 5 9,gapx 10");

		drawTiltAngleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(5);
				} catch(Exception e) {
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}
			}
		});
		
		drawSymTtcBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(9);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});
		
		drawImpropTtcBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHistogram(10);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		});

		JSeparator separator_2 = new JSeparator();
		contentPane.add(separator_2, "cell 0 10 6 1,growx,gapx 10,aligny center,gapy 5 5");

		cslChB = new JCheckBox("<html><u>CSL</u>");
		cslChB.setToolTipText("<html>Use Brandon-like criterion; \u0394\u03C9 is an angle of deviation from a CSL misorientation<br><font color=#cc0000>Warning: verification whether boundaries are CSL boundaries may take long times.</font>");
		cslChB.setEnabled(false);
		contentPane.add(cslChB, "cell 0 11,gapx 10");

		JLabel brandonLbl = new JLabel("<html>(\u0394\u03C9 \u2264 \u03C9<sub>0</sub> / \u03A3<sup>p</sup>,");
		brandonLbl.setToolTipText("\u0394\u03C9 is an angle measuring deviation from CSL misorientation");
		contentPane.add(brandonLbl, "flowx,cell 1 11 4 1,gapx 20");

		sigmaHistBtn = new JButton();
		sigmaHistBtn.setToolTipText("Distribution of \u03A3-values for boundaries recognized as CSL boundaries\r\n(may take a long time)");
		sigmaHistBtn.setEnabled(false);
		sigmaHistBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/bars.png")));
		sigmaHistBtn.setPreferredSize(new Dimension(24, 24));
		sigmaHistBtn.setMinimumSize(new Dimension(24, 24));
		sigmaHistBtn.setMaximumSize(new Dimension(24, 24));
		contentPane.add(sigmaHistBtn, "cell 5 11,gapx 10");

		
		//TODO
		sigmaHistBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				final double omega0;
				final double p;
				final int maxSigma;
				CSLMisor[] csl = null;
				
				try {
					String s = omega0Fld.getText();
					s = s.replace(",", ".");
					omega0 = Math.toRadians(Double.parseDouble(s));
					if(omega0 <= 0d) throw new NumberFormatException();					
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"<html>\u03C9<sub>0</sub> must be a positive decimal number.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}

				try {
					String s = pFld.getText();
					s = s.replace(",", ".");
					p = Double.parseDouble(s);
					if(p < ConstantsAndStatic.MINBPOWER || p > 1.0d) throw new NumberFormatException();					
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"p must be a decimal number between " + ConstantsAndStatic.MINBPOWERST + " and 1.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}

				try {
					String s = sigmaMaxFld.getText();
					maxSigma = Integer.parseInt(s);
					if (maxSigma > ConstantsAndStatic.MAXSIGMA || maxSigma < 1) throw new NumberFormatException();
				}  catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"Maximum \u03a3 value should be a positive integer less than " + ConstantsAndStatic.MAXSIGMA + ".",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}

				switch(gbFiles.get(0).getPointGrp()) {
				case M3M:
					csl = CSLMisorientations.getForCubic(maxSigma);
					break;

				case _6MMM:
					int m;
					int n;

					try {
						m = Integer.parseInt(cDivA.getmFld().getText());
						n = Integer.parseInt(cDivA.getnFld().getText());						
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Ratio (c/a)\u00b2 must be a rational number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);	
						return;
					}
					csl = CSLMisorientations.getForHexagonal(maxSigma, m, n);
					break;

				default: 
					JOptionPane.showMessageDialog(null,
							"This feature works only for cubic and hexagonal symmetries.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}

				final Matrix3x3[] setC = Transformations.getSymmetryTransformations(gbFiles.get(0).getPointGrp());
				
				
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				histFrameTitle = "\u03A3-values";
				String xAxis = "\u03A3";
				
				
				final ArrayList<Integer> data = new ArrayList<Integer>();

				Iterator<GBDatHeader> iterator = gbFiles.iterator();

				try {

					while(iterator.hasNext()) {

						GBDatHeader head = iterator.next();			
						final BufferedReader in = new BufferedReader(new FileReader(head.getPath()));

						GBDatHeader.skipHeaderLines(in);

						String line = null;

						while ((line = in.readLine()) != null) {

							final String[] num = line.trim().split("\\s+");		
							
							double area = 0d;
							if(eliminate) area = Double.parseDouble(num[9]);
							
							if(!eliminate || (eliminate&& area <= areaThreshold)) {
						
								final double phi1L = Math.toRadians(Double.parseDouble(num[0]));
								final double PhiL = Math.toRadians(Double.parseDouble(num[1]));
								final double phi2L = Math.toRadians(Double.parseDouble(num[2]));
								
								final double phi1R = Math.toRadians(Double.parseDouble(num[3]));
								final double PhiR = Math.toRadians(Double.parseDouble(num[4]));
								final double phi2R = Math.toRadians(Double.parseDouble(num[5]));
	
								final EulerAngles eulL = new EulerAngles();
								eulL.set(phi1L, PhiL, phi2L);
								final EulerAngles eulR = new EulerAngles();
								eulR.set(phi1R, PhiR, phi2R);
	
								final Matrix3x3 ML = new Matrix3x3();
								ML.set(eulL);							
								final Matrix3x3 MR = new Matrix3x3();
								MR.set(eulR);
								
								final Matrix3x3 MT = new Matrix3x3(MR);
								MT.timesTransposed(ML);
								
								int sigmaFound = A_LOT;
								
								for(Matrix3x3 C1 : setC) { // TODO
									for(Matrix3x3 C2 : setC) {
										
										final Matrix3x3 R = new Matrix3x3(C1);
										R.times(MT);
										R.timesTransposed(C2);
		
										for(CSLMisor __csl : csl) {
							   	     			
							    			final Matrix3x3 RMcslT = new Matrix3x3(R);   	    		
							   	    		RMcslT.timesTransposed(__csl.getMatrix());
							   	    			
							   	    		final double angle = MyMath.acos(0.5d * (RMcslT.tr() - 1d));
							   	    				
							   	    		if(angle < omega0 / FastMath.pow(__csl.getSigma(), p) ) {
							   	    					
							   	    			sigmaFound = __csl.getSigma();
							   	    			break;
							   	    		}
							    		}
										if(sigmaFound < A_LOT) break;
									}
									if(sigmaFound < A_LOT) break;
								}
								
								if(sigmaFound < A_LOT) data.add(sigmaFound);	
							}
						//	System.out.println(sigmaFound);}
						}
						in.close();
					}

				} catch(IOException | NumberFormatException exc ) {
					
				}


				double[] dataArr = new double[data.size()];
				int index = -1;
				for(Integer d : data) {
					index++;
					dataArr[index] = d;
				}
				

				plot = new Plot2DPanel();

				plot.addHistogramPlot(histFrameTitle, Color.red, dataArr, 0.5d, maxSigma + 0.5d, maxSigma);
				

				plot.setAxisLabels(xAxis,"N");
				plot.addLegend("SOUTH");
					
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							JFrame frame = new JFrame("GBToolbox: " + histFrameTitle);
							frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_FrequenciesFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
							frame.setSize(600, 600);
							frame.setContentPane(plot);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

				setCursor(Cursor.getDefaultCursor());
			}							
		});				

		allSigmaRb = new JRadioButton("all \u03A3-values");
		allSigmaRb.setEnabled(false);
		allSigmaRb.setSelected(true);
		getContentPane().add(allSigmaRb, "cell 0 12,gapx 20");

		JLabel whereLbl = new JLabel("<html>where \u03C9<sub>0</sub><sup>&nbsp;</sup>=");
		getContentPane().add(whereLbl, "flowx,cell 1 12 3 1,gapx 20,aligny center");

		onlySigmaRb = new JRadioButton("Only \u03A3 =");
		onlySigmaRb.setEnabled(false);
		getContentPane().add(onlySigmaRb, "flowx,cell 0 13,gapx 20");

		ButtonGroup group = new ButtonGroup();
		group.add(allSigmaRb);
		group.add(onlySigmaRb);

		JLabel latticeLbl = new JLabel("Lattice:");
		getContentPane().add(latticeLbl, "flowx,cell 1 13 5 1,gapx 20");

		JSeparator separator_1 = new JSeparator();
		getContentPane().add(separator_1, "cell 0 14 6 1,growx,aligny center,gapy 5 5");

		tiltTolFld = new JTextField();
		tiltTolFld.setToolTipText("Maximum allowed distance from a pure tilt boundary");
		tiltTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		tiltTolFld.setText("3");
		getContentPane().add(tiltTolFld, "flowx,cell 1 3,alignx right,gapy 5");
		tiltTolFld.setColumns(3);

		JLabel bra1Lbl = new JLabel("\u00b0)");
		getContentPane().add(bra1Lbl, "cell 1 3,alignx right,gapy 5");

		latticeCards = new JPanel();
		getContentPane().add(latticeCards, "cell 1 13 5 1");

		noDetails = new LatticeParams_NonePanel(); 
		cDivA = new LatticeParams_CDivAPanel();
	//	abcPane = new LatticeParams_ABCPanel();

		latticeCards.setLayout(new CardLayout(0, 0));
		latticeCards.add(noDetails, M3M);
		latticeCards.add(cDivA, _6MMM);
		//latticeCards.add(abcPane, MMM);

		omega0Fld = new JTextField();
		omega0Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		omega0Fld.setText("15");
		getContentPane().add(omega0Fld, "cell 1 12 3 1");
		omega0Fld.setColumns(3);

		JLabel pLbl = new JLabel("<html>\u00B0&nbsp;&nbsp;&and;&nbsp;&nbsp;p =");
		getContentPane().add(pLbl, "cell 1 12 3 1");

		pFld = new JTextField();
		pFld.setHorizontalAlignment(SwingConstants.RIGHT);
		pFld.setText("0.5");
		getContentPane().add(pFld, "cell 1 12 3 1");
		pFld.setColumns(4);

		JLabel sigmaMaxLbl = new JLabel("<html>&nbsp;&nbsp;&and;&nbsp;&nbsp;\u03A3 &le;");
		getContentPane().add(sigmaMaxLbl, "cell 1 12 3 1");

		sigmaMaxFld = new JTextField();
		sigmaMaxFld.setHorizontalAlignment(SwingConstants.RIGHT);
		sigmaMaxFld.setText("30");
		getContentPane().add(sigmaMaxFld, "cell 1 12 3 1");
		sigmaMaxFld.setColumns(3);

		JLabel bra7Lbl = new JLabel(")");
		getContentPane().add(bra7Lbl, "cell 1 12 3 1");
		processBtn = new JButton("Process");

		getContentPane().add(processBtn, "flowx,cell 0 15 3 1,alignx left");

		//PROCESS
		//TODO
		processBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				
				if(!(tiltDistChB.isSelected() ||
						twistDistChB.isSelected() ||
						symDistChB.isSelected() ||
						impropDistChB.isSelected() ||
						tiltAngleChB.isSelected() ||
						twistAngleChB.isSelected() || 
						tiltTtcChB.isSelected() || 
						twistTtcChB.isSelected() ||
						symTtcChB.isSelected() || 
						impropTtcChB.isSelected() ||
						cslChB.isSelected()
						
						) ) {

					JOptionPane.showMessageDialog(null,
							"No boundary type selected",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// READ THRESHOLDS
				tiltTol = 0d;
				twistTol = 0d;
				symTol = 0d;
				impropTol = 0d;
				maxTiltAngle = 0d;
				maxTwistAngle = 0d;
				
				maxTiltTtc = 0d;
				maxTwistTtc = 0d;
				
				maxSymTtc = 0d;
				maxImpropTtc = 0d;

				try {
					if(tiltDistChB.isSelected()) {
						String s = tiltTolFld.getText().replace(",", ".");
						tiltTol = Double.parseDouble(s);		
						if(tiltTol <= 0d) throw new NumberFormatException();
					}
					if(twistDistChB.isSelected()) {
						String s = twistTolFld.getText().replace(",", ".");
						twistTol = Double.parseDouble(s);
						if(twistTol <= 0d) throw new NumberFormatException();
					}
					if(symDistChB.isSelected()) {
						String s = symTolFld.getText().replace(",", ".");
						symTol = Double.parseDouble(s);
						if(symTol <= 0d) throw new NumberFormatException();
					}
					if(impropDistChB.isSelected()) {
						String s = impropTolFld.getText().replace(",", ".");
						impropTol = Double.parseDouble(s);
						if(impropTol <= 0d) throw new NumberFormatException();
					}
					if(tiltAngleChB.isSelected()) {
						String s = maxTwistAngFld.getText().replace(",", ".");
						maxTwistAngle = Double.parseDouble(s);
						if(maxTwistAngle <= 0d) throw new NumberFormatException();
					}
					if(twistAngleChB.isSelected()) {
						String s = maxTiltAngFld.getText().replace(",", ".");
						maxTiltAngle = Double.parseDouble(s);				
						if(maxTiltAngle <= 0d) throw new NumberFormatException();
					}
					if(tiltTtcChB.isSelected()) {
						String s = tiltTtcFld.getText().replace(",", ".");
						maxTiltTtc = Double.parseDouble(s);				
						if(maxTiltTtc <= 0d) throw new NumberFormatException();
					}
					if(twistTtcChB.isSelected()) {
						String s = twistTtcFld.getText().replace(",", ".");
						maxTwistTtc = Double.parseDouble(s);				
						if(maxTwistTtc <= 0d) throw new NumberFormatException();
					}
					if(symTtcChB.isSelected()) {
						String s = symTtcFld.getText().replace(",", ".");
						maxSymTtc = Double.parseDouble(s);				
						if(maxSymTtc <= 0d) throw new NumberFormatException();
					}
					if(impropTtcChB.isSelected()) {
						String s = impropTtcFld.getText().replace(",", ".");
						maxImpropTtc = Double.parseDouble(s);				
						if(maxImpropTtc <= 0d) throw new NumberFormatException();
					}
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"Thresholds must be positive decimal numbers.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
				}

				if(cslChB.isSelected() && onlySigmaRb.isSelected()) {
					try {
						sigmaChosen = Integer.parseInt(onlySigmaFld.getText());
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"\u03a3 value must be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);	
						return;
					}
				}

				double omega0 = 0d;
				double p = 0d;
				int maxSigma = 0;
				CSLMisor[] csl = null;
				tester = new IndividualGBTester();

				if(cslChB.isSelected()) {
					try {
						String s = omega0Fld.getText();
						s = s.replace(",", ".");
						omega0 = Math.toRadians(Double.parseDouble(s));
						if(omega0 <= 0d) throw new NumberFormatException();					
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"<html>\u03C9<sub>0</sub> must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);	
						return;
					}

					try {
						String s = pFld.getText();
						s = s.replace(",", ".");
						p = Double.parseDouble(s);
						if(p < ConstantsAndStatic.MINBPOWER || p > 1.0d) throw new NumberFormatException();					
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"p must be a decimal number between " + ConstantsAndStatic.MINBPOWERST +" and 1.",
								"Error",
								JOptionPane.ERROR_MESSAGE);	
						return;
					}

					try {
						String s = sigmaMaxFld.getText();
						maxSigma = Integer.parseInt(s);
						if (maxSigma > ConstantsAndStatic.MAXSIGMA || maxSigma < 1) throw new NumberFormatException();
					}  catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Maximum \u03a3-value must be a positive integer not greater than "+ConstantsAndStatic.MAXSIGMA +".",
								"Error",
								JOptionPane.ERROR_MESSAGE);	
						return;
					}

					switch(gbFiles.get(0).getPointGrp()) {
					case M3M:
						csl = CSLMisorientations.getForCubic(maxSigma);
						break;

					case _6MMM:
						int m;
						int n;

						try {
							m = Integer.parseInt(cDivA.getmFld().getText());
							n = Integer.parseInt(cDivA.getnFld().getText());						
						} catch(NumberFormatException exc) {
							JOptionPane.showMessageDialog(null,
									"(c/a)\u00b2 ratio must be a rational number.",
									"Error",
									JOptionPane.ERROR_MESSAGE);	
							return;
						}
						csl = CSLMisorientations.getForHexagonal(maxSigma, m, n);
						break;

					default: break;
					}

					tester.setSymmetryTransformations(Transformations.getSymmetryTransformations(gbFiles.get(0).getPointGrp()));
					tester.testCSL(p, omega0, csl);
				}

				// tester is defined
				task = new FrequenciesTask();
				task.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						   if ("progress".equals(evt.getPropertyName())) {
				                 progressBar.setValue((Integer)evt.getNewValue());
				             }						
					}
				});
				task.execute();
			}
		});

		JLabel lblResult = new JLabel("<html><b>Fraction (area-weighted):</b>");
		contentPane.add(lblResult, "flowx,cell 3 15 3 1,alignx right");

		onlySigmaFld = new JTextField();
		onlySigmaFld.setHorizontalAlignment(SwingConstants.RIGHT);
		onlySigmaFld.setText("3");
		contentPane.add(onlySigmaFld, "cell 0 13");
		onlySigmaFld.setColumns(3);

		impropTolFld = new JTextField();
		impropTolFld.setToolTipText("Maximum allowed distance from a pure improperly quasi-symmetric boundary");
		impropTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		impropTolFld.setText("3");
		impropTolFld.setColumns(3);
		contentPane.add(impropTolFld, "cell 4 4,alignx right");

		JLabel bra4Lbl = new JLabel("\u00B0)");
		contentPane.add(bra4Lbl, "cell 4 4,alignx right");

		twistTolFld = new JTextField();
		twistTolFld.setToolTipText("Maximum allowed distance from a pure twist boundary");
		twistTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		twistTolFld.setText("3");
		twistTolFld.setColumns(3);
		contentPane.add(twistTolFld, "cell 4 3,alignx right,gapy 5");

		JLabel bra2Lbl = new JLabel("\u00B0)");
		contentPane.add(bra2Lbl, "cell 4 3,alignx right,gapy 5");

		symTolFld = new JTextField();
		symTolFld.setToolTipText("Maximum allowed distance from a pure symmetric boundary");
		symTolFld.setText("3");
		symTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		symTolFld.setColumns(3);
		contentPane.add(symTolFld, "cell 1 4,alignx right");

		JLabel bra3Lbl = new JLabel("\u00B0)");
		contentPane.add(bra3Lbl, "cell 1 4,alignx right");

		maxTwistAngFld = new JTextField();
		maxTwistAngFld.setToolTipText("Maximum allowed angle of a twist component");
		maxTwistAngFld.setHorizontalAlignment(SwingConstants.RIGHT);
		maxTwistAngFld.setText("5");
		maxTwistAngFld.setColumns(3);
		contentPane.add(maxTwistAngFld, "cell 1 9,alignx right");

		JLabel bra5Lbl = new JLabel("\u00B0)");
		contentPane.add(bra5Lbl, "cell 1 9,alignx right");

		maxTiltAngFld = new JTextField();
		maxTiltAngFld.setToolTipText("Maximum allowed angle of a tilt component");
		maxTiltAngFld.setText("5");
		maxTiltAngFld.setHorizontalAlignment(SwingConstants.RIGHT);
		maxTiltAngFld.setColumns(3);
		getContentPane().add(maxTiltAngFld, "cell 4 9,alignx right");

		JLabel bra6Lbl = new JLabel("\u00B0)");
		getContentPane().add(bra6Lbl, "cell 4 9,alignx right");

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		getContentPane().add(progressBar, "cell 0 15 3 1,alignx left,gapx 20 20");

		resultFld = new JTextField();
		resultFld.setEditable(false);
		resultFld.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(resultFld, "cell 3 15 3 1,alignx right,aligny center");
		resultFld.setColumns(7);
				
						JLabel percentLbl = new JLabel("%");
						getContentPane().add(percentLbl, "cell 3 15 3 1,alignx right,aligny center");
		
				JButton eraseBtn = new JButton();
				eraseBtn.setEnabled(false);
				eraseBtn.setToolTipText("Erase the result");
				eraseBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/eraser.png")));
				contentPane.add(eraseBtn, "cell 3 15 3 1,alignx right,gapx 10,aligny center");
				eraseBtn.setPreferredSize(new Dimension(24,24));
				eraseBtn.setMinimumSize(new Dimension(24,24));
				//eraseBtn.setMaximumSize(new Dimension(24,24));
				eraseBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						resultFld.setText("");
					}
				});
		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setEnabled(false);
		abortBtn.setIcon(new ImageIcon(Stats_FrequenciesFrame.class.getResource("/gui_bricks/abort.png")));
		getContentPane().add(abortBtn, "cell 0 15 3 1,alignx left");
		abortBtn.setPreferredSize(new Dimension(24, 24));
		abortBtn.setMinimumSize(new Dimension(24, 24));
		abortBtn.setMaximumSize(new Dimension(24, 24));
		
		tiltTtcFld = new JTextField();
		tiltTtcFld.setToolTipText("<html>Maximum allowed value for \u03B1<sub>L</sub>");
		tiltTtcFld.setText("3");
		tiltTtcFld.setHorizontalAlignment(SwingConstants.RIGHT);
		tiltTtcFld.setColumns(3);
		contentPane.add(tiltTtcFld, "cell 1 6,alignx right");
		
		label_1 = new JLabel("\u00B0)");
		contentPane.add(label_1, "cell 1 6,alignx right");
		
		twistTtcFld = new JTextField();
		twistTtcFld.setToolTipText("<html>Maximum allowed value for \u03B1<sub>N</sub>");
		twistTtcFld.setText("3");
		twistTtcFld.setHorizontalAlignment(SwingConstants.RIGHT);
		twistTtcFld.setColumns(3);
		contentPane.add(twistTtcFld, "cell 4 6,alignx right");
		
		label_3 = new JLabel("\u00B0)");
		contentPane.add(label_3, "cell 4 6,alignx right");
		
		symTtcFld = new JTextField();
		symTtcFld.setToolTipText("<html>Maximum allowed value for \u03B1<sub>S</sub>");
		symTtcFld.setText("3");
		symTtcFld.setHorizontalAlignment(SwingConstants.RIGHT);
		symTtcFld.setColumns(3);
		contentPane.add(symTtcFld, "cell 1 7");
		
		label_2 = new JLabel("\u00B0)");
		contentPane.add(label_2, "cell 1 7");
		
		impropTtcFld = new JTextField();
		impropTtcFld.setToolTipText("<html>Maximum allowed value for \u03B1<sub>I</sub>");
		impropTtcFld.setText("3");
		impropTtcFld.setHorizontalAlignment(SwingConstants.RIGHT);
		impropTtcFld.setColumns(3);
		contentPane.add(impropTtcFld, "cell 4 7");
		
		label_5 = new JLabel("\u00B0)");
		contentPane.add(label_5, "cell 4 7");


		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				task.cancel(true);
			}
		});
		
		pack();
		setResizable(false);
		refreshProc();
	}
	
	
	private final void showHistogram(int histType) throws IOException, NumberFormatException {

			
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		String histTitle = "";
		String histXaxis = "";

		final ArrayList<Double> data = new ArrayList<Double>();

		Iterator<GBDatHeader> iterator = gbFiles.iterator();

		while(iterator.hasNext()) {

			GBDatHeader head = iterator.next();			
			final BufferedReader in = new BufferedReader(new FileReader(head.getPath()));

			GBDatHeader.skipHeaderLines(in);
			
			int pos = 8;				

			if(head.isExperimental()) { pos++; pos++; }
			
			if(histType > 1 && head.containsTiltDist()) pos++;
			if(histType > 2 && head.containsTwistDist()) pos++;
			if(histType > 3 && head.containsSymDist())pos++;
			if(histType > 4 && head.containsImpropDist()) pos++;
			if(histType > 5 && head.containsTiltAngle()) pos++;
			if(histType > 6 && head.containsTwistAngle()) pos++;
			if(histType > 7 && head.containsMinTTC()) pos++;
			if(histType > 8 && head.containsMaxTTC()) pos++;			
			if(histType > 9 && head.containsSymTTC()) pos++;
			if(histType > 10 && head.containsImpropTTC()) pos++;

			String line = null;

			while ((line = in.readLine()) != null) {

				final String[] num = line.trim().split("\\s+");
				
				double area = INFTY;
				
				if(eliminate) area = Double.parseDouble(num[9]);
				
				if(!eliminate || (eliminate && area <= areaThr)) {
				
					final double value = Double.parseDouble(num[pos]);
					data.add(value);
				
				}
			}
			
			in.close();
		}
		
				
		switch(histType) {
		case 1:
			histTitle = "Distances to the nearest pure tilt boundaries";
			histXaxis="\u03B4 [\u00b0]";
			break;

		case 2:
			histTitle = "Distances to the nearest pure twist boundaries";
			histXaxis="\u03B4 [\u00b0]";
			break;

		case 3:
			histTitle = "Distances to the nearest pure symmetric boundaries";
			histXaxis="\u03B4 [\u00b0]";
			break;

		case 4:
			histTitle = "Distances to the nearest pure 180\u00b0-tilt boundaries";
			histXaxis="\u03B4 [\u00b0]";
			break;

		case 5:
			histTitle = "Angles of the tilt components (Fortes decomposition)";
			histXaxis="\u03Bd [\u00b0]";
			break;

		case 6:
			histTitle = "Angles of the twist components (Fortes decomposition)";
			histXaxis="\u03Bb [\u00b0]";
			break;

		case 7:
			histTitle = "Approximate distances to the nearest twist boundaries";
			histXaxis="\u03B1_N [\u00b0]";
			break;
			
		case 8:
			histTitle = "Approximate distances to the nearest tilt boundaries";
			histXaxis="\u03B1_L [\u00b0]";
			break;
			
		case 9:
			histTitle = "Approximate distances to the nearest symmetric boundaries";
			histXaxis="\u03B1_S [\u00b0]";
			break;
			
		case 10:
			histTitle = "Approximate distances to the nearest 180\u00b0-tilt boundaries";
			histXaxis="\u03b1_I [\u00b0]";
			break;
			
			
		default:
			break;
		}
		
		double[] dataArr = new double[data.size()];
		int index = -1;
		for(Double d : data) {
			index++;
			dataArr[index] = d;
		}

		plot = new Plot2DPanel();
		
		double max = Math.ceil(Array.max(dataArr));
		if( ((int)max) == 0) max = 10d; //TODO
		plot.addHistogramPlot(histTitle, Color.RED, dataArr, 0d, max, (int)max);		
		
		plot.setAxisLabels(histXaxis, "N");
		plot.addLegend("SOUTH");
		
		histFrameTitle = histTitle;
			
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFrame frame = new JFrame(ConstantsAndStatic.GBTOOLBOX + ": " + histFrameTitle);
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_FrequenciesFrame.class.getResource("/gui_bricks/gbtoolbox.png")));

					frame.setSize(600, 600);
					frame.setContentPane(plot);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		setCursor(Cursor.getDefaultCursor());
	}

	private class FrequenciesTask extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() throws Exception {

			abortBtn.setEnabled(true);
			processBtn.setEnabled(false);	

			progressBar.setValue(0);

			weightTotal = 0d;
			weightAccepted = 0d;
			N = 0;

			final boolean expData = gbFiles.get(0).isExperimental();

			GBDatHeader header = null;

			Iterator<GBDatHeader> iterator = gbFiles.iterator();
			int nProcessed = 0;

			while(iterator.hasNext() && !isCancelled()) {

				header = iterator.next();

				final BufferedReader in = new BufferedReader(new FileReader(header.getPath()));

				GBDatHeader.skipHeaderLines(in);

				String line = null;

				while ((line = in.readLine()) != null && !isCancelled()) {

					final String[] num = line.trim().split("\\s+");
					nProcessed++;

					boolean accepted = true;						

					double weight = 0d;
					double tiltDist = INFTY;
					double twistDist = INFTY;
					double symDist = INFTY;
					double impropDist = INFTY;							
					double tiltAngle = INFTY;
					double twistAngle = INFTY;
					
					double minTtc = INFTY;
					double maxTtc = INFTY;
					double symTtc = INFTY;
					double impropTtc = INFTY;

					int pos = 8;

					if(header.isExperimental()) {
						pos++;
						weight = Double.parseDouble(num[pos]);
						pos++;						
					}

					if(header.containsTiltDist()) {
						tiltDist = Double.parseDouble(num[pos]);
						pos++;
					}

					if(header.containsTwistDist()) {
						twistDist = Double.parseDouble(num[pos]);
						pos++;
					}

					if(header.containsSymDist()) {
						symDist = Double.parseDouble(num[pos]);
						pos++;
					}

					if(header.containsImpropDist()) {										
						impropDist = Double.parseDouble(num[pos]);
						pos++;
					}

					if(header.containsTiltAngle()) {
						tiltAngle = Double.parseDouble(num[pos]);
						pos++;
					}

					if(header.containsTwistAngle()) {
						twistAngle = Double.parseDouble(num[pos]);
						pos++;
					}
					
					if(header.containsMinTTC()) {
						minTtc = Double.parseDouble(num[pos]);
						pos++;
					}
					
					if(header.containsMaxTTC()) {
						maxTtc = Double.parseDouble(num[pos]);
						pos++;
					}

					if(header.containsSymTTC()) {
						symTtc = Double.parseDouble(num[pos]);
						pos++;
					}
					
					if(header.containsImpropTTC()) {
						impropTtc = Double.parseDouble(num[pos]);
						pos++;
					}
					
					boolean tooBig = false;
					
					if(eliminate && weight > areaThr) {
						tooBig = true;
						accepted = false;
					}

					if(tiltDistChB.isSelected())
						if(tiltDist > tiltTol) accepted = false;

					if(twistDistChB.isSelected())
						if(twistDist > twistTol) accepted = false;

					if(symDistChB.isSelected())
						if(symDist > symTol) accepted = false;

					if(impropDistChB.isSelected())
						if(impropDist > impropTol) accepted = false;

					if(tiltAngleChB.isSelected())
						if(twistAngle > maxTwistAngle) accepted = false;

					if(twistAngleChB.isSelected())
						if(tiltAngle > maxTiltAngle) accepted = false;
					
					if(tiltTtcChB.isSelected())
						if(maxTtc > maxTiltTtc) accepted = false;
					
					if(twistTtcChB.isSelected())
						if(minTtc > maxTwistTtc) accepted = false;

					if(symTtcChB.isSelected())
						if(symTtc > maxSymTtc) accepted = false;
					
					if(impropTtcChB.isSelected())
						if(impropTtc > maxImpropTtc) accepted = false;

					if(accepted && cslChB.isSelected()) {

						final double phi1L = Math.toRadians(Double.parseDouble(num[0]));
						final double PhiL = Math.toRadians(Double.parseDouble(num[1]));
						final double phi2L = Math.toRadians(Double.parseDouble(num[2]));
						
						final double phi1R = Math.toRadians(Double.parseDouble(num[3]));
						final double PhiR = Math.toRadians(Double.parseDouble(num[4]));
						final double phi2R = Math.toRadians(Double.parseDouble(num[5]));

						final double znth = Math.toRadians(Double.parseDouble(num[6]));
						final double azmth = Math.toRadians(Double.parseDouble(num[7]));

						final EulerAngles eulL = new EulerAngles();
						eulL.set(phi1L, PhiL, phi2L);
						
						final EulerAngles eulR = new EulerAngles();
						eulR.set(phi1R, PhiR, phi2R);

						final Matrix3x3 ML = new Matrix3x3();
						ML.set(eulL);
						
						final Matrix3x3 MR = new Matrix3x3();
						MR.set(eulR);
						
						final Matrix3x3 M = new Matrix3x3(ML);
						M.timesTransposed(MR);

						final UnitVector m1 = new UnitVector();
						m1.set(znth, azmth);
						m1.transform(ML);

						final InterfaceMatrix B = new InterfaceMatrix(M, m1);

						tester.test(B);

						if(tester.getMinSigma() == 0) accepted = false;

						if(onlySigmaRb.isSelected()) if(tester.getMinSigma() != sigmaChosen) accepted = false; 

					}

					setProgress(Math.min((int)Math.round((double)nProcessed/(double)nTotal*100d), 100));				

					if(expData && !tooBig) weightTotal += weight; 
					if(accepted) {						
						if(expData) weightAccepted += weight; else N++;
					}

				}
				in.close();
			}

			boolean correctResult = true;
			double result = 0d;
			if(expData) {
				if(weightTotal < 1e-6d || isCancelled()) correctResult = false;
				else result = weightAccepted / weightTotal * 100d;				
			} else {
				if(nTotal < 1) correctResult = false;
				else result = (double) N / (double) nTotal * 100d;
			}

			DecimalFormat df = new DecimalFormat("0.######");
			
			if(correctResult) {
				resultFld.setText(df.format(result));
			} else {
				resultFld.setText("");
				JOptionPane.showMessageDialog(null,
						"There are no boundaries in the data files or\n" +
						"all boundaries have area less than specified criterion.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}

			abortBtn.setEnabled(false);
			processBtn.setEnabled(true);


			return null;
		}

		@Override
		public void done() {			
			if(isCancelled()) resultFld.setText("");
			processBtn.setEnabled(true);
			abortBtn.setEnabled(false);
		}
	}


	
	private final void refreshProc() {

		
		if(procTiltTTC) {
			tiltTtcChB.setEnabled(true);
			drawMaxTtcBtn.setEnabled(true);
		}
		
		if(procTwistTTC) {
			twistTtcChB.setEnabled(true);
			drawMinTtcBtn.setEnabled(true);
		}
		
		
		if(procTiltDist) {
			tiltDistChB.setEnabled(true);
			drawTiltDistBtn.setEnabled(true);
		}

		if(procTwistDist) {
			twistDistChB.setEnabled(true);
			drawTwistDistBtn.setEnabled(true);
		}

		if(procSymDist) {
			symDistChB.setEnabled(true);
			drawSymDistBtn.setEnabled(true);
		}

		if(procImpropDist) {
			impropDistChB.setEnabled(true);
			drawImpropDistBtn.setEnabled(true);
		}

		if(procTiltAngle) {
			tiltAngleChB.setEnabled(true);
			drawTwistAngleBtn.setEnabled(true);
		}

		if(procTwistAngle) {
			twistAngleChB.setEnabled(true);
			drawTiltAngleBtn.setEnabled(true);
		}
		
		if(procSymTTC) {
			symTtcChB.setEnabled(true);
			drawSymTtcBtn.setEnabled(true);
		}
		
		if(procImpropTTC) {
			impropTtcChB.setEnabled(true);
			drawImpropTtcBtn.setEnabled(true);
		}

		
		PointGroup ptGrp = gbFiles.get(0).getPointGrp();
		
		if(ptGrp == PointGroup.M3M || ptGrp == PointGroup._6MMM) {
			cslChB.setEnabled(true);
			sigmaHistBtn.setEnabled(true);
			allSigmaRb.setEnabled(true);
			onlySigmaRb.setEnabled(true);
		} else {
			cslChB.setToolTipText("This feature is not supported for this point group");
		}
		
		CardLayout cl_latticeCards = (CardLayout)latticeCards.getLayout();
		switch(ptGrp) {
			case M3M: cl_latticeCards.show(latticeCards, M3M); break;
			case _6MMM: cl_latticeCards.show(latticeCards, _6MMM); break;
			case _4MMM: cl_latticeCards.show(latticeCards, _6MMM); break;
			case MMM: cl_latticeCards.show(latticeCards, MMM); break;
			default: break;
		}
	}
}
