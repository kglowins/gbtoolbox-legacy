package gui_modules;

import enums.PointGroup;

import gui_bricks.LatticePanel;
import gui_bricks.MisorPanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingWorker;


import javax.swing.SwingConstants;



import utils.AxisAngle;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.FileUtils;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.Transformations;
import utils.UnitVector;
import javax.swing.JProgressBar;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;

import org.apache.commons.math3.util.FastMath;

import parallel.TaskResultGridOfNormals;
import parallel.TaskTestOneFromGrid;



public class Catalog_NumericalPanel extends JPanel {

	private static final double HALFPI = 0.5d * Math.PI;

	private LatticePanel latticePane;
	private MisorPanel misorPane;
	private JTextField gridFld;
	private JLabel gridLbl;
	private JCheckBox calcDistTiltChB;
	private JCheckBox calcDistTwistChB;
	private JCheckBox calcDistSymChB;
	private JCheckBox calcDistImpropChB;
	private JCheckBox calcAngleTiltChB;
	private JCheckBox calcAngleTwistChB;
	private JProgressBar progressBar;
	private JButton calcBtn;
	private JTextField tiltDistFld;
	private JTextField twistDistFld;
	private JTextField symDistFld;
	private JTextField impropDistFld;
	private JTextField angleTiltFld;
	private JTextField angleTwistFld;
	private JButton tiltDistBtn;
	private JButton twistDistBtn;
	private JButton symDistBtn;
	private JButton impropDistBtn;
	private JButton angleTiltBtn;
	private JButton angleTwistBtn;
	private JLabel calcLbl;
	private JButton abortBtn;
	private JLabel calcAndSaveLbl;
	private JLabel lblOutputFile;
	private JLabel lblOutput2File;
	private JLabel lblOutput3File;
	private JLabel lblOutput4File;
	private JLabel lblOutput5File;
	private JLabel lblOutput6File;
	private JLabel lblNewLabel;
	private JSeparator separator;
	private JSeparator separator_1;
	private JLabel lblNewLabel_2;
	private JLabel starLbl;

	private GridOfNormalsProcessTask task = null;
	private JLabel maxLbl;
	private JTextField maxFld;
	private JButton predefMisBtn;
	
	private static final String S3 = "\u03A33 (60\u00b0; [111])";
	private static final String S5 = "\u03A35: (36.87\u00b0; [100])";
	private static final String S7 = "\u03A37: (38.21\u00b0; [111])";
	private static final String S9 = "\u03A39: (38.94\u00b0; [110])";
	private static final String S11 = "\u03A311: (50.48\u00b0; [110])";
	
	private static final String S13a = "\u03A313a: (22.62\u00b0; [100])";
	private static final String S13b = "\u03A313b: (27.8\u00b0; [111])";
	private static final String S15 = "\u03A315: (48.19\u00b0; [210])";
	private static final String S17a = "\u03A317a: (28.07\u00b0; [100])";
	private static final String S17b = "\u03A317b: (61.93\u00b0; [221])";
	private static final String S19a = "\u03A319a: (26.53\u00b0; [110])";
	private static final String S19b = "\u03A319b: (46.83\u00b0; [111])";
	private static final String S21a = "\u03A321a: (21.79\u00b0; [111])";
	private static final String S21b = "\u03A321b: (44.42\u00b0; [211])";
	private static final String S23 = "\u03A323: (40.46\u00b0; [311])";
	private static final String S25a = "\u03A325a: (16.26\u00b0; [100])";
	private static final String S25b = "\u03A325b: (51.68\u00b0; [331])";
	
	private static final String S27a = "\u03A327a: (31.59\u00b0; [110])";
	private static final String S27b = "\u03A327b: (35.43\u00b0; [210])";
	
	private static final String S29a = "\u03A329a: (43.6\u00b0; [100])";
	private static final String S29b = "\u03A329b: (46.4\u00b0; [221])";
	private static final String S31a = "\u03A331a: (17.9\u00b0; [111])";
	private static final String S31b = "\u03A331b: (52.2\u00b0; [211])";

	private static final String S39b = "\u03A339b: (50.13\u00b0; [321])";

	private static final String M_0001_30 = "(30\u00b0; [0001])";
	private static final String M_1010_90 = "(90\u00b0; [101\u03050])";
	private static final String M_2110_90 = "(90\u00b0; [2\u0305110])";
	private JCheckBox minTtcChB;
	private JLabel lblOutputFile7;
	private JTextField minTtcFld;
	private JButton minTtcBtn;
	private JCheckBox maxTtcChB;
	private JLabel lblOutputFile8;
	private JTextField maxTtcFld;
	private JButton maxTtcBtn;
	private JCheckBox symTTCChB;
	private JCheckBox impropTTCChB;
	private JLabel label;
	private JLabel label_1;
	private JTextField symTTCFld;
	private JTextField impropTTCFld;
	private JButton symTTCBtn;
	private JButton impropTTCBtn;
	
	private final FileUtils.GBDistFileFilter gbdistFilter = new FileUtils.GBDistFileFilter(); 


	public Catalog_NumericalPanel() {


		setLayout(new MigLayout("", "[][][][][]", "[][][][][][][][][][][][][][][][][]"));

		calcLbl = new JLabel("<html><b>Calculate values for a grid of boundary plane normals for a fixed misorientation:</b>");
		add(calcLbl, "cell 0 0 5 1");

		JLabel latticeLbl = new JLabel("<html><u>Crystal structure</u>:");
		add(latticeLbl, "cell 0 1 5 1,alignx left,gapx 10,gapy 5");


		misorPane = null;		
		latticePane = new LatticePanel(null, misorPane);
		add(latticePane, "cell 0 2 5 1,alignx left,gapx 20");
		
		maxFld = new JTextField();
		maxFld.setHorizontalAlignment(SwingConstants.RIGHT);
		maxFld.setText("30");
		
		maxFld.setColumns(4);
		
		maxFld.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
				try {
				
					int maxIndex = Integer.parseInt(maxFld.getText());
					if(maxIndex < 1 || maxIndex > ConstantsAndStatic.MAXMILLER) throw new NumberFormatException();
					
				} catch(NumberFormatException exc) {
					
					JOptionPane.showMessageDialog(null,
							"Maximum allowed value for Miller indices must be a positive integer not greater than " + ConstantsAndStatic.MAXMILLER + ".",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					maxFld.requestFocus();
					return;
				}
			}
		});

		misorPane = new MisorPanel("<html><u>Misorientation</u>:", latticePane, maxFld);

		latticePane.setMisorReference(misorPane);
		
		maxLbl = new JLabel("<html><u>Maximum value for Miller indices (needed for representing misorientation axes)</u>:");
		add(maxLbl, "flowx,cell 0 4 5 1,gapx 10,gapy 5");

		separator = new JSeparator();
		add(separator, "cell 0 5 5 1,growx,aligny center,gapy 5 5");

		calcAndSaveLbl = new JLabel("<html><u>Values to be computed</u>:");
		add(calcAndSaveLbl, "cell 0 7 2 1,gapx 10,gapy 5");

		calcDistTiltChB = new JCheckBox("<html><b>\u03B4<sub>L</sub></b>");
		calcDistTiltChB.setToolTipText("<html>Distances to the nearest tilt boundaries (minimization method).<br>\r\n<font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcDistTiltChB, "flowx,cell 0 8,gapx 10");

		lblOutputFile = new JLabel("Output file:");
		add(lblOutputFile, "flowx,cell 1 8,gapx 10");
		
		starLbl = new JLabel("<html><font color=#0000cc>(*)</font>");
		starLbl.setToolTipText("<html><font color=#0000cc>(*) Results of the computations will be saved in selected text files; Each file will contain lines consisting of three numbers: x y \u03BE,<br>where x and y are coordinates of stereographic projections of boundary plane normals, \u03BE denotes computed distances or angles.</font>");
		add(starLbl, "cell 2 8");
		
		maxTtcChB = new JCheckBox("<html><b>\u03B1<sub>L</sub></b>");
		maxTtcChB.setToolTipText("Approximate distances to the nearest tilt boundaries (based on 'tilt/twist component' (TTC) parameters)");
		add(maxTtcChB, "cell 3 8,gapx 30");
		
		maxTtcChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					maxTtcBtn.setEnabled(true);
					maxTtcFld.setEnabled(true);
				} else {
					maxTtcBtn.setEnabled(false);
					maxTtcFld.setEnabled(false);
				}
			}
		});
		
		lblOutputFile8 = new JLabel("Output file:");
		add(lblOutputFile8, "flowx,cell 4 8,gapx 10");

		calcDistTwistChB = new JCheckBox("<html><b>\u03B4<sub>N</sub></b>");
		calcDistTwistChB.setToolTipText("<html>Distances to the nearest twist boundaries (minimization method).<br>\r\n<font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcDistTwistChB, "flowx,cell 0 9,gapx 10");

		lblOutput2File = new JLabel("Output file:");
		add(lblOutput2File, "flowx,cell 1 9,gapx 10");
		
		minTtcChB = new JCheckBox("<html><b>\u03B1<sub>N</sub></b>");
		minTtcChB.setToolTipText("Approximate distances to the nearest twist boundaries (based on TTC parameters)");
		add(minTtcChB, "flowx,cell 3 9,gapx 30,aligny top");
		
		
				minTtcChB.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						JCheckBox src = (JCheckBox)evt.getSource();
						if(src.isSelected()) {
							minTtcBtn.setEnabled(true);
							minTtcFld.setEnabled(true);
						} else {
							minTtcBtn.setEnabled(false);
							minTtcFld.setEnabled(false);
						}
					}
				});
		
		lblOutputFile7 = new JLabel("Output file:");
		add(lblOutputFile7, "flowx,cell 4 9,gapx 10");

		calcDistSymChB = new JCheckBox("<html><b>\u03B4<sub>S</sub></b>");
		calcDistSymChB.setToolTipText("<html>Distances to the nearest symmetric boundaries (minimization method).<br>\r\n<font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcDistSymChB, "flowx,cell 0 10,gapx 10");

		lblOutput3File = new JLabel("Output file:");
		add(lblOutput3File, "flowx,cell 1 10,gapx 10");
		
		symTTCChB = new JCheckBox("<html><b>\u03B1<sub>S</sub></b>");
		symTTCChB.setToolTipText("Approximate distances to the nearest symmetric boundaries (based on TTC parameters)");
		add(symTTCChB, "flowx,cell 3 10,gapx 30");
		
		symTTCChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					symTTCBtn.setEnabled(true);
					symTTCFld.setEnabled(true);
				} else {
					symTTCBtn.setEnabled(false);
					symTTCFld.setEnabled(false);
				}
			}
		});
		
		label = new JLabel("Output file:");
		add(label, "flowx,cell 4 10,gapx 10");

		calcDistImpropChB = new JCheckBox("<html><b>\u03B4<sub>I</sub></b>");
		calcDistImpropChB.setToolTipText("<html>Distances to the nearest 180\u00B0-tilt boundaries (minimization method).<br>\r\n<font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcDistImpropChB, "flowx,cell 0 11,gapx 10");

		lblOutput4File = new JLabel("Output file:");
		add(lblOutput4File, "flowx,cell 1 11,gapx 10");
		
		impropTTCChB = new JCheckBox("<html><b>\u03B1<sub>I</sub></b>");
		impropTTCChB.setToolTipText("Approximate distances to the nearest 180\u00B0-tilt boundaries (based on TTC parameters)");
		add(impropTTCChB, "flowx,cell 3 11,gapx 30");
		
		impropTTCChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					impropTTCBtn.setEnabled(true);
					impropTTCFld.setEnabled(true);
				} else {
					impropTTCBtn.setEnabled(false);
					impropTTCFld.setEnabled(false);
				}
			}
		});
		
		label_1 = new JLabel("Output file:");
		add(label_1, "flowx,cell 4 11,gapx 10");

		calcAngleTiltChB = new JCheckBox("<html><b>\u03BB</b>");
		calcAngleTiltChB.setToolTipText("Angles of tilt components (Fortes decomposition)");
		add(calcAngleTiltChB, "flowx,cell 0 12,gapx 10");

		lblOutput5File = new JLabel("Output file:");
		add(lblOutput5File, "flowx,cell 1 12,gapx 10");

		calcAngleTwistChB = new JCheckBox("<html><b>\u03BD</b>");
		calcAngleTwistChB.setToolTipText("Angles of twist components (Fortes decomposition)");
		add(calcAngleTwistChB, "flowx,cell 0 13,gapx 10");

		lblOutput6File = new JLabel("Output file:");
		add(lblOutput6File, "flowx,cell 1 13,gapx 10");

		separator_1 = new JSeparator();
		add(separator_1, "cell 0 14 5 1,growx,gapy 5 5");

		calcBtn = new JButton("Compute");
		add(calcBtn, "flowx,cell 0 15 5 1,gapx 10");

		calcBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

				// get misorientation
				Matrix3x3 M = new Matrix3x3();

				switch(misorPane.getMisorAs()) {
				case AXISANGLE:
					double theta;					
					try {
						theta = misorPane.getAxisAngleFlds().getAngle();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"A rotation angle must be a decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					
					
					UnitVector n;
					
					try {					
						n =  misorPane.getAxisAngleFlds().getAxis();	
					} catch(NumberFormatException exc) {

						if(latticePane.getPointGroup() == PointGroup.M3M) {
							JOptionPane.showMessageDialog(null,						
									"Miller indices of the boundary plane must be integers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						} else if(latticePane.getPointGroup() != PointGroup._6MMM) {
							JOptionPane.showMessageDialog(null,	
								"Miller indices of the boundary plane must be integers.\n" +
								"Lattice parameters must be positive decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);					
						} else {
							JOptionPane.showMessageDialog(null,
									"Miller indices of the boundary plane must be integers.\n" +
									"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
						return;
					}
														
					AxisAngle aa = new AxisAngle();
					aa.set(n, theta);
					M.set(aa);

					break;

				case EULER:
					try {
						EulerAngles eul =  misorPane.getEulerFlds().getAngles();
						M.set(eul);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Euler angles must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				case MATRIX:
					try {
						M =  misorPane.getMatrixFlds().getMatrix();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Matrix elements must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					final Matrix3x3 orth = M.nearestOrthogonal();
					final double dist = M.distSq(orth);

					if(dist < 1e-4d && Math.abs(M.det() - 1d) < 1e-3d) {
						M = orth;						
					} else {						
						M = orth;						
						if(M.det() > 0d) {
							int answer = JOptionPane.showConfirmDialog(
									null,
									"Provided rotation matrix is not orthogonal.\n" +
											"Would you like to replace it by the nearest orthogonal matrix and continue?",
											"Warning",
											JOptionPane.YES_NO_OPTION);
							if(answer == JOptionPane.NO_OPTION) return; 		
						} else {
							JOptionPane.showMessageDialog(null,
									"Provided matrix is not a proper rotation matrix.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;							
						}																													
					}
					misorPane.getMatrixFlds().setMatrix(M);

					break;
				case QUATERNION:
					try {
						Quaternion quat =  misorPane.getQuatFlds().getQuaternion();
						M.set(quat);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Quaternion components must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				case RODRIGUES:
					try {
						RodriguesParams rodr =  misorPane.getRodriguesFlds().getRodrigues();
						M.set(rodr);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Rodrigues parameters must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				default: break;
				}

				// get density
				int density;
				try {
					String str = gridFld.getText();
					density = Integer.parseInt(str);
					if(density < 1) throw new NumberFormatException();
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"Number of points must be a positive integer.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}

				// get settings and calculate
				calcBtn.setEnabled(false);
				abortBtn.setEnabled(true);

				task = new GridOfNormalsProcessTask(density, M, latticePane.getPointGroup());
				task.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().compareTo("progress") == 0) {
							int progress = (Integer) evt.getNewValue();
							progressBar.setValue(progress);
						} 		
					}	
				});
				if(calcDistTiltChB.isSelected()) task.setCalcTiltDist(tiltDistFld.getText()); 
				if(calcDistTwistChB.isSelected()) task.setCalcTwistDist(twistDistFld.getText());
				if(calcDistSymChB.isSelected()) task.setCalcSymDist(symDistFld.getText());
				if(calcDistImpropChB.isSelected()) task.setCalcImpropDist(impropDistFld.getText());
				if(calcAngleTiltChB.isSelected()) task.setCalcTiltAngle(angleTiltFld.getText());
				if(calcAngleTwistChB.isSelected()) task.setCalcTwistAngle(angleTwistFld.getText());
				if(minTtcChB.isSelected()) task.setCalcMinTTC(minTtcFld.getText());
				if(maxTtcChB.isSelected()) task.setCalcMaxTTC(maxTtcFld.getText());
				
				if(symTTCChB.isSelected()) task.setCalcSymTTC(symTTCFld.getText());
				if(impropTTCChB.isSelected()) task.setCalcImpropTTC(impropTTCFld.getText());
				
				task.execute();
				


			}
		});

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		add(progressBar, "cell 0 15,gapx 20");

		gridLbl = new JLabel("<html><u>Grid density</u>:");
		gridLbl.setToolTipText("Selected values will be calculated for a given number of boundary plane normals spread uniformly on a hemisphere");
		add(gridLbl, "flowx,cell 0 6 5 1,gapx 10");


		add(misorPane, "flowx,cell 0 3 5 1,alignx left,gapx 10,gapy 5");


		gridFld = new JTextField();
		gridFld.setToolTipText("Selected values will be calculated for a given number of boundary plane normals spread uniformly on a hemisphere");
		gridFld.setText("5000");
		gridFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(gridFld, "cell 0 6 2 1");
		gridFld.setColumns(7);

		final JFileChooser outputFc = new JFileChooser();/* {
			@Override
			public void approveSelection() {  			  
				File f = getSelectedFile();  			  
				if ( f.exists() ) {  
					String msg = "The file \"{0}\" already exists!\nAre you sure you want to replace it?";  
					msg = MessageFormat.format( msg, new Object[] { f.getName() } );  
					String title = getDialogTitle();  
					int option = JOptionPane.showConfirmDialog( this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );  
					if ( option == JOptionPane.NO_OPTION ) {  
						return;  
					}  
				}  			  
				super.approveSelection();  
			}   
		};*/
		
		outputFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		outputFc.setAcceptAllFileFilterUsed(false);
		outputFc.addChoosableFileFilter(gbdistFilter);

		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/abort.png")));
		abortBtn.setPreferredSize(new Dimension(24, 24));
		abortBtn.setMinimumSize(new Dimension(24, 24));
		abortBtn.setMaximumSize(new Dimension(24, 24));
		abortBtn.setEnabled(false);
		add(abortBtn, "cell 0 15,gapx 20");

		tiltDistFld = new JTextField();
		tiltDistFld.setEnabled(false);
		add(tiltDistFld, "cell 1 8");
		tiltDistFld.setColumns(16);

		tiltDistBtn = new JButton();
		tiltDistBtn.setEnabled(false);
		tiltDistBtn.setPreferredSize(new Dimension(24,24));
		tiltDistBtn.setMinimumSize(new Dimension(24,24));
		tiltDistBtn.setMaximumSize(new Dimension(24,24));
		tiltDistBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		add(tiltDistBtn, "cell 1 8");

		twistDistFld = new JTextField();
		twistDistFld.setEnabled(false);
		twistDistFld.setColumns(16);
		add(twistDistFld, "cell 1 9");

		symDistFld = new JTextField();
		symDistFld.setEnabled(false);
		symDistFld.setColumns(16);
		add(symDistFld, "cell 1 10");

		impropDistFld = new JTextField();
		impropDistFld.setEnabled(false);
		impropDistFld.setColumns(16);
		add(impropDistFld, "cell 1 11");

		angleTiltFld = new JTextField();
		angleTiltFld.setEnabled(false);
		angleTiltFld.setColumns(16);
		add(angleTiltFld, "cell 1 12");

		angleTwistFld = new JTextField();
		angleTwistFld.setEnabled(false);
		angleTwistFld.setColumns(16);
		add(angleTwistFld, "cell 1 13");

		twistDistBtn = new JButton();
		twistDistBtn.setEnabled(false);
		twistDistBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		twistDistBtn.setPreferredSize(new Dimension(24, 24));
		twistDistBtn.setMinimumSize(new Dimension(24, 24));
		twistDistBtn.setMaximumSize(new Dimension(24, 24));
		add(twistDistBtn, "cell 1 9");

		symDistBtn = new JButton();
		symDistBtn.setEnabled(false);
		symDistBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		symDistBtn.setPreferredSize(new Dimension(24, 24));
		symDistBtn.setMinimumSize(new Dimension(24, 24));
		symDistBtn.setMaximumSize(new Dimension(24, 24));
		add(symDistBtn, "cell 1 10");

		impropDistBtn = new JButton();
		impropDistBtn.setEnabled(false);
		impropDistBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		impropDistBtn.setPreferredSize(new Dimension(24, 24));
		impropDistBtn.setMinimumSize(new Dimension(24, 24));
		impropDistBtn.setMaximumSize(new Dimension(24, 24));
		add(impropDistBtn, "cell 1 11");

		angleTiltBtn = new JButton();
		angleTiltBtn.setEnabled(false);
		angleTiltBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		angleTiltBtn.setPreferredSize(new Dimension(24, 24));
		angleTiltBtn.setMinimumSize(new Dimension(24, 24));
		angleTiltBtn.setMaximumSize(new Dimension(24, 24));
		add(angleTiltBtn, "cell 1 12");

		angleTwistBtn = new JButton();
		angleTwistBtn.setEnabled(false);
		angleTwistBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		angleTwistBtn.setPreferredSize(new Dimension(24, 24));
		angleTwistBtn.setMinimumSize(new Dimension(24, 24));
		angleTwistBtn.setMaximumSize(new Dimension(24, 24));
		add(angleTwistBtn, "cell 1 13");

		lblNewLabel = new JLabel("[number of directions on the upper hemi-sphere]");
		lblNewLabel.setToolTipText("Selected values will be calculated for a given number of boundary plane normals spread uniformly on a hemisphere");
		add(lblNewLabel, "cell 0 6 2 1,gapx 40");
		
		add(maxFld, "cell 0 4 2 1,gapy 5");
		
		predefMisBtn = new JButton();
		predefMisBtn.setMinimumSize(new Dimension(24,24));
		predefMisBtn.setMaximumSize(new Dimension(24,24));
		predefMisBtn.setPreferredSize(new Dimension(24,24));
		predefMisBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/sum-icon.png")));
		predefMisBtn.setToolTipText("Use a predefined misorientation");
		add(predefMisBtn, "cell 0 3 2 1,alignx left,gapx 20,aligny top");
		
		minTtcFld = new JTextField();
		minTtcFld.setEnabled(false);
		minTtcFld.setColumns(16);
		add(minTtcFld, "cell 4 9");
		
		minTtcBtn = new JButton();
		minTtcBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		minTtcBtn.setPreferredSize(new Dimension(24, 24));
		minTtcBtn.setMinimumSize(new Dimension(24, 24));
		minTtcBtn.setMaximumSize(new Dimension(24, 24));
		minTtcBtn.setEnabled(false);
		add(minTtcBtn, "cell 4 9");
		
		maxTtcFld = new JTextField();
		maxTtcFld.setEnabled(false);
		maxTtcFld.setColumns(16);
		add(maxTtcFld, "cell 4 8");
		
		maxTtcBtn = new JButton();
		maxTtcBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		maxTtcBtn.setPreferredSize(new Dimension(24, 24));
		maxTtcBtn.setMinimumSize(new Dimension(24, 24));
		maxTtcBtn.setMaximumSize(new Dimension(24, 24));
		maxTtcBtn.setEnabled(false);
		add(maxTtcBtn, "cell 4 8");
		
		symTTCFld = new JTextField();
		symTTCFld.setEnabled(false);
		symTTCFld.setColumns(16);
		add(symTTCFld, "cell 4 10");
		
		symTTCBtn = new JButton();
		symTTCBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		symTTCBtn.setPreferredSize(new Dimension(24, 24));
		symTTCBtn.setMinimumSize(new Dimension(24, 24));
		symTTCBtn.setMaximumSize(new Dimension(24, 24));
		symTTCBtn.setEnabled(false);
		add(symTTCBtn, "cell 4 10");
		
		impropTTCFld = new JTextField();
		impropTTCFld.setEnabled(false);
		impropTTCFld.setColumns(16);
		add(impropTTCFld, "cell 4 11");
		
		impropTTCBtn = new JButton();
		impropTTCBtn.setIcon(new ImageIcon(Catalog_NumericalPanel.class.getResource("/gui_bricks/folder.png")));
		impropTTCBtn.setPreferredSize(new Dimension(24, 24));
		impropTTCBtn.setMinimumSize(new Dimension(24, 24));
		impropTTCBtn.setMaximumSize(new Dimension(24, 24));
		impropTTCBtn.setEnabled(false);
		add(impropTTCBtn, "cell 4 11");
		
		lblNewLabel_2 = new JLabel("<html><small><font color=cc0000><b>Warning: all existing files will be overwritten!</b></font></small>");
		add(lblNewLabel_2, "cell 0 16 5 1,gapx 10");
		
		impropTTCBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					impropTTCFld.setText(fName);
				}			
			}
		});
		
		symTTCBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					symTTCFld.setText(fName);
				}			
			}
		});
		
		maxTtcBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					maxTtcFld.setText(fName);
				}			
			}
		});
		
		minTtcBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					minTtcFld.setText(fName);
				}			
			}
		});
		
		
		predefMisBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				Object[] possibilities = null;

				if(latticePane.getPointGroup() == PointGroup.M3M) {

					possibilities = new Object[]{S3,S5,S7,S9,S11,S13a,S13b,S15,S17a,S17b,S19a,S19b,S21a,S21b,S23,S25a,S25b,S27a,S27b,S29a,S29b,S31a,S31b,S39b		};	

				} else if(latticePane.getPointGroup() == PointGroup._6MMM) {

					possibilities = new Object[]{M_0001_30, M_1010_90, M_2110_90};	

				} else {

					JOptionPane.showMessageDialog(null,							
							"This feature is not supported for the selected point group.\n",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				String s = (String)JOptionPane.showInputDialog(
						null,
						"Use one of the following misorientations:",
						"Select",
						JOptionPane.PLAIN_MESSAGE,
						null,
						possibilities,
						S3);

				if ((s != null) && (s.length() > 0)) {

					UnitVector n = null;
					double w = 0d;

					switch(s) {

					case S3:
						n = new UnitVector();
						n.set(1d, 1d, 1d);
						w = Math.toRadians(60d);

						break;

					case S5:
						n = new UnitVector();
						n.set(1d, 0d, 0d);
						w = Math.toRadians(36.8699d);
						break;

					case S7:
						n = new UnitVector();
						n.set(1d, 1d, 1d);
						w = Math.toRadians(38.2132d);
						break;

					case S9:
						n = new UnitVector();
						n.set(1d, 1d, 0d);
						w = Math.toRadians(38.9424d);
						break;

					case S11:
						n = new UnitVector();
						n.set(1d, 1d, 0d);
						w = Math.toRadians(50.4788d);
						break;
						
					case S13a:
						n = new UnitVector();
						n.set(1d, 0d, 0d);
						w = Math.toRadians(22.6199d);
						break;
						
					case S13b:
						n = new UnitVector();
						n.set(1d, 1d, 1d);
						w = Math.toRadians(27.7958d);
						break;
						
						
					case S15:
						n = new UnitVector();
						n.set(2d, 1d, 0d);
						w = Math.toRadians(48.1897d);
						break;
									
						
					case S17a:
						n = new UnitVector();
						n.set(1d, 0d, 0d);
						w = Math.toRadians(28.0725d);
						break;
						
					case S17b:
						n = new UnitVector();
						n.set(2d, 2d, 1d);
						w = Math.toRadians(61.9275d);
						break;
						
						
					case S19a:
						n = new UnitVector();
						n.set(1d, 1d, 0d);
						w = Math.toRadians(26.5254d);
						break;
						
					case S19b:
						n = new UnitVector();
						n.set(1d, 1d, 1d);
						w = Math.toRadians(46.8264d);
						break;
						
						
					case S21a:
						n = new UnitVector();
						n.set(1d, 1d, 1d);
						w = Math.toRadians(21.7868d);
						break;
						
					case S21b:
						n = new UnitVector();
						n.set(2d, 1d, 1d);
						w = Math.toRadians(44.4153d);
						break;
						
						
					case S23:
						n = new UnitVector();
						n.set(3d, 1d, 1d);
						w = Math.toRadians(40.4591d);
						break;
						
						
					case S25a:
						n = new UnitVector();
						n.set(1d, 0d, 0d);
						w = Math.toRadians(16.2602d);
						break;
						
					case S25b:
						n = new UnitVector();
						n.set(3d, 3d, 1d);
						w = Math.toRadians(51.6839d);
						break;
						
						
					
						

					case S27a:
						n = new UnitVector();
						n.set(1d, 1d, 0d);
						w = Math.toRadians(31.5863d);
						break;

				
						
					case S27b:
						n = new UnitVector();
						n.set(2d, 1d, 0d);
						w = Math.toRadians(35.4309d);
						break;
						
					
						
					case S29a:
						n = new UnitVector();
						n.set(1d, 0d, 0d);
						w = Math.toRadians(43.6028d);
						break;
						
					case S29b:
						n = new UnitVector();
						n.set(2d, 2d, 1d);
						w = Math.toRadians(46.3972d);
						break;
						
						
					case S31a:
						n = new UnitVector();
						n.set(1d, 1d, 1d);
						w = Math.toRadians(17.8966d);
						break;
						
					case S31b:
						n = new UnitVector();
						n.set(2d, 1d, 1d);
						w = Math.toRadians(52.2003d);
						break;
						

					case S39b:
						n = new UnitVector();
						n.set(3d, 2d, 1d);
						w = Math.toRadians(50.132d);
						break;
					

					case M_0001_30:

						n = new UnitVector();

						MillerIndices miller = new MillerIndices();
						miller.set(0, 0, 1);

						double a, c;
						int aSq, cSq;
						try {
							cSq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
							if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
							c = Math.sqrt(cSq);				
							a = Math.sqrt(aSq);
						} catch(NumberFormatException e) {
							JOptionPane.showMessageDialog(null,							
									"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						n.setAsHexAxis4to3(miller, Transformations.getHexToCartesian(a, c));

						w = Math.toRadians(30d);
						break;

					case M_1010_90:

						n = new UnitVector();

						miller = new MillerIndices();
						miller.set(1, 0, 0);

						try {
							cSq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
							if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
							c = Math.sqrt(cSq);				
							a = Math.sqrt(aSq);
						} catch(NumberFormatException e) {
							JOptionPane.showMessageDialog(null,							
									"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						n.setAsHexAxis4to3(miller, Transformations.getHexToCartesian(a, c));

						w = Math.toRadians(90d);
						break;

					case M_2110_90:

						n = new UnitVector();

						miller = new MillerIndices();
						miller.set(-2, 1, 0);
						
						try {
							cSq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
							if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
							c = Math.sqrt(cSq);				
							a = Math.sqrt(aSq);
						} catch(NumberFormatException e) {
							JOptionPane.showMessageDialog(null,							
									"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						n.setAsHexAxis4to3(miller, Transformations.getHexToCartesian(a, c));

						w = Math.toRadians(90d);
						break;
					default: break;

					}

					final AxisAngle aa = new AxisAngle();
					aa.set(n, w);


					//save boundary params

					int maxIndex;

					try {

						maxIndex = Integer.parseInt(maxFld.getText());
						if(maxIndex < 1 || maxIndex > ConstantsAndStatic.MAXMILLER) throw new NumberFormatException();

					} catch(NumberFormatException exc) {

						JOptionPane.showMessageDialog(null,
								"Maximum allowed value for Miller indices must be a positive integer not greater than " + ConstantsAndStatic.MAXMILLER + ".",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					switch(misorPane.getMisorAs()) {
					case AXISANGLE:					

						misorPane.getAxisAngleFlds().setAxis(aa.axis(), maxIndex);
						misorPane.getAxisAngleFlds().setAngle(aa.angle());				
						break;

					case EULER:
						EulerAngles eul = new EulerAngles();
						eul.set(aa);
						misorPane.getEulerFlds().setAngles(eul);
						break;

					case MATRIX:
						final Matrix3x3 M = new Matrix3x3();
						M.set(aa);
						misorPane.getMatrixFlds().setMatrix(M);
						break;

					case QUATERNION:
						Quaternion quat = new Quaternion();
						quat.set(aa);
						misorPane.getQuatFlds().setQuaternion(quat);
						break;

					case RODRIGUES:
						RodriguesParams rodr = new RodriguesParams();
						rodr.set(aa);
						misorPane.getRodriguesFlds().setRodrigues(rodr);
						break;

					default: break;
					}

				}
			}
		});

		angleTwistBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					angleTwistFld.setText(fName);
				}			
			}
		});

		angleTiltBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					angleTiltFld.setText(fName);
				}			
			}
		});

		impropDistBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					impropDistFld.setText(fName);
				}			
			}
		});

		symDistBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					symDistFld.setText(fName);
				}			
			}
		});

		twistDistBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					twistDistFld.setText(fName);
				}			
			}
		});

		tiltDistBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = outputFc.showDialog(Catalog_NumericalPanel.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					tiltDistFld.setText(fName);
				}			
			}
		});
		
		calcDistTiltChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					tiltDistBtn.setEnabled(true);
					tiltDistFld.setEnabled(true);
				} else {
					tiltDistBtn.setEnabled(false);
					tiltDistFld.setEnabled(false);
				}
			}
		});

		calcDistTwistChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					twistDistBtn.setEnabled(true);
					twistDistFld.setEnabled(true);
				} else {
					twistDistBtn.setEnabled(false);
					twistDistFld.setEnabled(false);
				}
			}
		});

		calcDistSymChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					symDistBtn.setEnabled(true);
					symDistFld.setEnabled(true);
				} else {
					symDistBtn.setEnabled(false);
					symDistFld.setEnabled(false);
				}
			}
		});

		calcDistImpropChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					impropDistBtn.setEnabled(true);
					impropDistFld.setEnabled(true);
				} else {
					impropDistBtn.setEnabled(false);
					impropDistFld.setEnabled(false);
				}
			}
		});



		calcAngleTiltChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					angleTiltBtn.setEnabled(true);
					angleTiltFld.setEnabled(true);
				} else {
					angleTiltBtn.setEnabled(false);
					angleTiltFld.setEnabled(false);
				}
			}
		});

		calcAngleTwistChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox)evt.getSource();
				if(src.isSelected()) {
					angleTwistBtn.setEnabled(true);
					angleTwistFld.setEnabled(true);
				} else {
					angleTwistBtn.setEnabled(false);
					angleTwistFld.setEnabled(false);
				}
			}
		});

		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				task.cancel(true);
				
			}			
		});

	}


	private final class GridOfNormalsProcessTask extends SwingWorker<Void, Void> {

		// constants
		private static final double TWOPI = 2d * Math.PI;

		// input
		private final Matrix3x3 M;

		// settings
		private final int nPts;
		private final Matrix3x3[] symTransf;
		private final ArrayList<Double> zenith = new ArrayList<Double>();
		private final ArrayList<Double> azimuth = new ArrayList<Double>();

		private boolean calcTiltDist;
		private boolean calcTwistDist;
		private boolean calcSymDist;
		private boolean calcImpropDist;
		private boolean calcTiltAngle;
		private boolean calcTwistAngle;		
		private boolean calcMinTTC;
		private boolean calcMaxTTC;
		private boolean calcSymTTC;
		private boolean calcImpropTTC;
		

		private String tiltDistFile = null;
		private String twistDistFile = null;
		private String symDistFile = null;
		private String impropDistFile = null;
		private String tiltAngleFile = null;
		private String twistAngleFile = null;
		private String minTtcFile = null;
		private String maxTtcFile = null;
		private String symTtcFile = null;
		private String impropTtcFile = null;
		
		private final PointGroup ptGrp;

		public void setCalcTiltDist(String fName) { calcTiltDist = true; tiltDistFile = fName; }
		public void setCalcTwistDist(String fName) { calcTwistDist = true; twistDistFile = fName; }
		public void setCalcSymDist(String fName) { calcSymDist = true; symDistFile = fName; }
		public void setCalcImpropDist(String fName) { calcImpropDist = true; impropDistFile = fName; }
		public void setCalcTiltAngle(String fName) { calcTiltAngle = true; tiltAngleFile = fName; }
		public void setCalcTwistAngle(String fName) { calcTwistAngle = true; twistAngleFile = fName; }
		public void setCalcMinTTC(String fName) { calcMinTTC = true; minTtcFile = fName; }
		public void setCalcMaxTTC(String fName) { calcMaxTTC = true; maxTtcFile = fName; }
		public void setCalcSymTTC(String fName) { calcSymTTC = true; symTtcFile = fName; }
		public void setCalcImpropTTC(String fName) { calcImpropTTC = true; impropTtcFile = fName; }
		
		private List futuresList;

		public GridOfNormalsProcessTask(int nPts, Matrix3x3 M, PointGroup pointGrp) {
			
		
			
			setProgress(0);
			progressBar.setValue(0);

			this.nPts = 2 * nPts;
			
			final double margin = 4d / Math.sqrt(nPts);

			calcTiltDist = false;
			calcTwistDist = false;
			calcSymDist = false;
			calcImpropDist = false;
			calcTiltAngle = false;
			calcTwistAngle = false;
			calcMinTTC = false;
			calcMaxTTC = false;

			this.M = M;
			symTransf = Transformations.getSymmetryTransformations(pointGrp);
			
			ptGrp = pointGrp;

			final double p = 0.5d;
			final double a = 1d - 2d*p / (this.nPts-3);
			final double b = p * (this.nPts+1) / (this.nPts-3);

			double rLast = 0d;
			double phiLast = 0d;

			final double C = 3.6d/Math.sqrt(this.nPts);
			final double D = 1d / (this.nPts - 1);
					
			for(int k = 2; k < this.nPts; k++) {

				final double kPrime = a*k + b;
				final double hk = -1d + 2d * (kPrime-1) * D;						
				final double rk = Math.sqrt(1d - hk*hk);

				final double theta_k = FastMath.acos(hk);
				final double phi_k = Math.IEEEremainder(phiLast + C * 2d/(rLast+rk), TWOPI);

				if(theta_k <= HALFPI + margin) {
					zenith.add(theta_k);
					azimuth.add(phi_k);
				}

				rLast = rk;
				phiLast = phi_k;
			}	
			zenith.add(0d);
			azimuth.add(0d);
			
		}

		@Override
		public Void doInBackground() {
			
			try {
				
			
			long startTime = System.currentTimeMillis();  	
			
			final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat df = new DecimalFormat("0.####", otherSymbols);
			
			PrintWriter tiltDistWrt = null;
			PrintWriter twistDistWrt = null;
			PrintWriter symDistWrt = null;
			PrintWriter impropDistWrt = null;
			PrintWriter tiltAngleWrt = null;
			PrintWriter twistAngleWrt = null;
			PrintWriter minTtcWrt = null;
			PrintWriter maxTtcWrt = null;
			PrintWriter symTtcWrt = null;
			PrintWriter impropTtcWrt = null;
			
			if(calcDistTiltChB.isSelected()){
				tiltDistWrt = new PrintWriter(new BufferedWriter(new FileWriter(tiltDistFile)));
				tiltDistWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				tiltDistWrt.println("# it contains distances to the nearest tilt boundaries");
				tiltDistWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				tiltDistWrt.println("FIXED");
				tiltDistWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH DIST_TILT");
			}
			if(calcDistTwistChB.isSelected()){
				twistDistWrt = new PrintWriter(new BufferedWriter(new FileWriter(twistDistFile)));
				twistDistWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				twistDistWrt.println("# it contains distances to the nearest twist boundaries");
				twistDistWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				twistDistWrt.println("FIXED");
				twistDistWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH DIST_TWIST");				
			};
			if(calcDistSymChB.isSelected()){
				symDistWrt = new PrintWriter(new BufferedWriter(new FileWriter(symDistFile)));
				symDistWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				symDistWrt.println("# it contains distances to the nearest symmetric boundaries");
				symDistWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				symDistWrt.println("FIXED");
				symDistWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH DIST_SYM");
			};
			if(calcDistImpropChB.isSelected()){
				impropDistWrt = new PrintWriter(new BufferedWriter(new FileWriter(impropDistFile)));
				impropDistWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				impropDistWrt.println("# it contains distances to the nearest 180 deg.-tilt boundaries");
				impropDistWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				impropDistWrt.println("FIXED");
				impropDistWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH DIST_180-TILT");
			};
			if(calcAngleTiltChB.isSelected()){
				tiltAngleWrt = new PrintWriter(new BufferedWriter(new FileWriter(tiltAngleFile)));
				tiltAngleWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				tiltAngleWrt.println("# it contains angles of tilt components (from Fortes decomposition)");
				tiltAngleWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				tiltAngleWrt.println("FIXED");
				tiltAngleWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH F_TILT_ANGLE");
			};
			if(calcAngleTwistChB.isSelected()){
				twistAngleWrt = new PrintWriter(new BufferedWriter(new FileWriter(twistAngleFile)));
				twistAngleWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				twistAngleWrt.println("# it contains angles of twist components (from Fortes decomposition)");
				twistAngleWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				twistAngleWrt.println("FIXED");
				twistAngleWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH F_TWIST_ANGLE");
			};
			if(minTtcChB.isSelected()){
				minTtcWrt = new PrintWriter(new BufferedWriter(new FileWriter(minTtcFile)));
				minTtcWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				minTtcWrt.println("# it contains approximate distances to the nearest twist boundaries");
				minTtcWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				minTtcWrt.println("FIXED");
				minTtcWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH APPROX_D_TWIST");
			};
			if(maxTtcChB.isSelected()){
				maxTtcWrt = new PrintWriter(new BufferedWriter(new FileWriter(maxTtcFile)));
				maxTtcWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				maxTtcWrt.println("# it contains approximate distances to the nearest tilt boundaries");
				maxTtcWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				maxTtcWrt.println("FIXED");
				maxTtcWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH APPROX_D_TILT");
			};
			
			if(symTTCChB.isSelected()){
				symTtcWrt = new PrintWriter(new BufferedWriter(new FileWriter(symTtcFile)));
				symTtcWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				symTtcWrt.println("# it contains approximate distances to the nearest symmetric boundaries");
				symTtcWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				symTtcWrt.println("FIXED");
				symTtcWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH APPROX_D_SYM");
			};
			if(impropTTCChB.isSelected()){
				impropTtcWrt = new PrintWriter(new BufferedWriter(new FileWriter(impropTtcFile)));
				impropTtcWrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
				impropTtcWrt.println("# it contains approximate distances to the nearest 180deg-tilt boundaries");
				impropTtcWrt.println("# for a grid of boundary plane normals and a fixed misorientation.");
				impropTtcWrt.println("FIXED");
				impropTtcWrt.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH APPROX_D_180-TILT");
			};
			
			int nThreads = Runtime.getRuntime().availableProcessors();
			ExecutorService eservice = Executors.newFixedThreadPool(nThreads);
		    CompletionService < Object > cservice = new ExecutorCompletionService < Object > (eservice);
		    
		    futuresList = new ArrayList();

		    	
			for(int index = 0; index < zenith.size(); index++) {
				
				futuresList.add(cservice.submit(new TaskTestOneFromGrid(M, zenith.get(index), azimuth.get(index), 
						 calcTiltDist, calcTwistDist, calcSymDist, calcImpropDist, 
						 calcTiltAngle || calcTwistAngle, 
						 calcMinTTC, calcMaxTTC, calcSymTTC, calcImpropTTC,
						 symTransf) ) );				
			}
			
						
			int index = 0;
			while(index < zenith.size() && !isCancelled()) {
							
				try {
					final TaskResultGridOfNormals taskResult = (TaskResultGridOfNormals) cservice.take().get();
							            		            
            		final String coords = df.format(taskResult.stereoProjX) + ' ' + df.format(taskResult.stereoProjY) + ' '             		
            				+  df.format(Math.toDegrees(taskResult.zenith)) + ' ' + df.format(Math.toDegrees(taskResult.azimuth)) + ' ';

        			if(calcTiltDist) tiltDistWrt.println(coords + df.format(Math.toDegrees(Math.sqrt(taskResult.tiltDist )) ));	    					
        			if(calcTwistDist) twistDistWrt.println(coords + df.format(Math.toDegrees(Math.sqrt(taskResult.twistDist )) ));
        			if(calcSymDist) symDistWrt.println(coords + df.format(Math.toDegrees(Math.sqrt(taskResult.symDist )) ));				
        			if(calcImpropDist) impropDistWrt.println(coords + df.format(Math.toDegrees(Math.sqrt(taskResult.impropDist )) ));				
        			if(calcTiltAngle) tiltAngleWrt.println(coords + df.format(Math.toDegrees(taskResult.tiltAngle ) ));				
        			if(calcTwistAngle) twistAngleWrt.println(coords + df.format(Math.toDegrees(taskResult.twistAngle ) ));
        			if(calcMinTTC) minTtcWrt.println(coords + df.format(Math.toDegrees(taskResult.minTtc ) ));
        			if(calcMaxTTC) maxTtcWrt.println(coords + df.format(90d - Math.toDegrees(taskResult.maxTtc ) ));
        			
        			if(calcSymTTC) symTtcWrt.println(coords + df.format(Math.toDegrees(Math.sqrt(taskResult.symTtc )) ));
        			if(calcImpropTTC) impropTtcWrt.println(coords + df.format(Math.toDegrees(Math.sqrt(taskResult.impropTtc )) ));
        			        			
        			
        			index++;
        			setProgress((int)((double)index / (double)zenith.size() * 100d));
		            		           
				} catch (InterruptedException | ExecutionException exc) { 
					
					return null;
				}
		    }
					
			
			if(calcTiltDist) tiltDistWrt.close();
			if(calcTwistDist) twistDistWrt.close();
			if(calcSymDist) symDistWrt.close();
			if(calcImpropDist) impropDistWrt.close();
			if(calcTiltAngle) tiltAngleWrt.close();
			if(calcTwistAngle) twistAngleWrt.close();
			if(calcMinTTC) minTtcWrt.close();
			if(calcMaxTTC) maxTtcWrt.close();
			
			if(calcSymTTC) symTtcWrt.close();
			if(calcImpropTTC) impropTtcWrt.close();

			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time elapsed: " + estimatedTime + " microsec.");

			} catch(IOException exc) {
				exc.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"An I/O error occurred. Please check if paths to the output files are correct.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return null;				
			}
			return null;
		}

		@Override
		public void done() {
			
			if(isCancelled()) if(futuresList != null) for(Object future : futuresList) ((Future) future).cancel(true);

			calcBtn.setEnabled(true);
			abortBtn.setEnabled(false);
		}
	}

}
