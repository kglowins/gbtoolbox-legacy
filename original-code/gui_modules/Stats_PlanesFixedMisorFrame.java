package gui_modules;

import enums.GBCD_GBPD_Method;
import enums.PointGroup;
import gui_bricks.GBCD_GBPD_BinsOptsPanel;
import gui_bricks.GBCD_DistFunOptsPanel;
import gui_bricks.LatticePanel;
import gui_bricks.MisorPanel;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import utils.Transformations;

import net.miginfocom.swing.MigLayout;
import algorithms.GBCD_BinsWorker;
import algorithms.GBCD_DistFunWorker;

import utils.AxisAngle;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.FileUtils;
import utils.GBDatHeader;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.UnitVector;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;


public class Stats_PlanesFixedMisorFrame extends JFrame {

	private static final String BINS = "Based on space binning";
	private static final String DIST_FUN = "Based on distance functon";
	
	private final FileUtils.GBDistFileFilter gbdistFilter = new FileUtils.GBDistFileFilter(); 

	
	private JLabel omitLbl;
	
	private JPanel contentPane;
	private JTextField outFld;
	
	private MisorPanel misorPane;
	private ArrayList<GBDatHeader> gbFiles;
	private JTextField maxFld;
	
	private LatticePanel latticePane;
	private JPanel methodCards; 
	
	private GBCD_GBPD_Method gbcdMethod;
	private GBCD_DistFunOptsPanel distOpts;
	private GBCD_GBPD_BinsOptsPanel binsOpts;
	
	private JButton fireBtn;
	private JButton abortBtn;	
	private JProgressBar progressBar;
	
	private Matrix3x3 Mfix;

	private boolean eliminate;
	private double areaThreshold;
	
	private GBCD_BinsWorker gbcdBins;
	private GBCD_DistFunWorker gbcdDistFun;
	
	private JSeparator separator_2;
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
	
		
	public Stats_PlanesFixedMisorFrame(ArrayList<GBDatHeader> gbs, boolean elimin, double areaThr) {
		
		this.eliminate = elimin;
		this.areaThreshold = areaThr;
		
		gbFiles = gbs;
		omitLbl = new JLabel();
	
		if(elimin) omitLbl.setText("<html><font color=#0000ff>Mesh triangles with area greater than " + areaThr + " will not be taken into account</i></font>");
		else omitLbl.setText("<html><font color=#0000ff>All mesh triangles will be taken into account</i></font>");
		
		gbcdMethod = GBCD_GBPD_Method.DIST_FUN;
		distOpts = new GBCD_DistFunOptsPanel(gbFiles.get(0).getPointGrp());
		binsOpts = new GBCD_GBPD_BinsOptsPanel();
		
		setTitle("GBToolbox: Distribution of boundary planes for fixed misorientations");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_PlanesFixedMisorFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[]", "[][][][][][][][][][][][][][]"));
		
		contentPane.add(omitLbl, "cell 0 0");
		
		separator_2 = new JSeparator();
		contentPane.add(separator_2, "cell 0 1,growx,aligny center,gapy 5 5");
		
		JLabel specifyLbl = new JLabel("<html><b>Specify lattice parameters, set options and calculate distributions of boundary planes:</b>");
		contentPane.add(specifyLbl, "cell 0 2");
		
		JLabel structLbl = new JLabel("<html><u>Crystal structure</u>:");
		contentPane.add(structLbl, "cell 0 3,gapx 10,gapy 5");
		
		latticePane = new LatticePanel(null, misorPane);
		contentPane.add(latticePane, "cell 0 4,gapx 20,gapy 5");
		
		JLabel lblMaximumValueFor = new JLabel("<html><u>Maximum Miller index (for representing misorientation axes)</u>:");
		contentPane.add(lblMaximumValueFor, "flowx,cell 0 5,gapx 10,gapy 5");
		
		maxFld = new JTextField();
		maxFld.setHorizontalAlignment(SwingConstants.RIGHT);
		maxFld.setText("30");
		contentPane.add(maxFld, "cell 0 5,gapy 5");
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
					
					JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
							"Maximum allowed value for Miller indices must be a positive integer not greater than " + ConstantsAndStatic.MAXMILLER + ".",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					maxFld.requestFocus();
					return;
				}
			}
		});
		
		JSeparator separator_1 = new JSeparator();
		contentPane.add(separator_1, "cell 0 6,growx,aligny center,gapy 5 5");
		
		misorPane = new MisorPanel("<html><u>Fixed misorientation</u>:", latticePane, maxFld);
		latticePane.setMisorReference(misorPane);
		
		latticePane.setPointGroupLocked(gbs.get(0).getPointGrp());
		
		contentPane.add(misorPane, "flowx,cell 0 7,gapx 10");
		
		JLabel lblmethod = new JLabel("<html><u>Method</u>:");
		contentPane.add(lblmethod, "flowx,cell 0 8,gapx 10,gapy 5");
		
		methodCards = new JPanel();
		contentPane.add(methodCards, "cell 0 9,gapx 20,gapy 5,grow");
		methodCards.setLayout(new CardLayout(0, 0));
		
	
		
		JLabel outLbl = new JLabel("<html><u>Output <code>dist</code> file</u>:");
		contentPane.add(outLbl, "flowx,cell 0 10,gapx 10,gapy 5");
		
		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 11,growx,aligny center,gapy 5 5");
		
		fireBtn = new JButton("Calculate");
		contentPane.add(fireBtn, "flowx,cell 0 12");
		
		JLabel overwriteLbl = new JLabel("<html><font color=#cc0000><small>Warning: <b>Existing files will be overwritten!</b></small></font>");
		contentPane.add(overwriteLbl, "cell 0 13,gapy 5");
		
		outFld = new JTextField();
		contentPane.add(outFld, "cell 0 10,gapy 5");
		outFld.setColumns(18);
		
		JButton outBtn = new JButton();
		outBtn.setMinimumSize(new Dimension(24,24));
		outBtn.setMaximumSize(new Dimension(24,24));
		outBtn.setPreferredSize(new Dimension(24,24));
		outBtn.setIcon(new ImageIcon(Stats_PlanesFixedMisorFrame.class.getResource("/gui_bricks/folder.png")));
		contentPane.add(outBtn, "cell 0 10,gapy 5");
		
		JComboBox methodCb = new JComboBox();
		methodCb.setModel(new DefaultComboBoxModel(new String[] {DIST_FUN,BINS}));
		contentPane.add(methodCb, "cell 0 8,gapy 5");
		
		methodCb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                JComboBox cb = (JComboBox) e.getSource();
                String sel = cb.getSelectedItem().toString();
                
                CardLayout cl = (CardLayout) methodCards.getLayout();
            	cl.show(methodCards, sel);
            	
            	switch(sel) {
            	case DIST_FUN: gbcdMethod = GBCD_GBPD_Method.DIST_FUN; break;
            	case BINS: gbcdMethod = GBCD_GBPD_Method.BINS; break;
            	default: break;
            	}
            	
            }
		});
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(gbdistFilter);
		
		outBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {			
				
				int returnVal = fc.showDialog(Stats_PlanesFixedMisorFrame.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = fc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					outFld.setText(fName);
				}	
				
								
			}	
		});
		
		
		fireBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				Mfix = new Matrix3x3();
				
				// read the misorientation
				switch(misorPane.getMisorAs()) {
				case AXISANGLE:
					
					double theta;					
					try {
						theta = misorPane.getAxisAngleFlds().getAngle();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Rotation angle must be a decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
						
					UnitVector n;
					try {					
						n = misorPane.getAxisAngleFlds().getAxis();	
					} catch(NumberFormatException exc) {

						if(latticePane.getPointGroup() == PointGroup.M3M) {
							JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,						
									"Miller indices of the boundary plane must be integers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						} else if(latticePane.getPointGroup() != PointGroup._6MMM) {
							JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,	
								"Miller indices of the boundary plane must be integers.\n" +
								"Lattice parameters must be positive decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);					
						} else {
							JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
									"Miller indices of the boundary plane must be integers.\n" +
									"(c/a)\u00b2 ratio must be a rational number.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
						return;
					}
										
					AxisAngle aa = new AxisAngle();
					aa.set(n, theta);
					Mfix.set(aa);
						
					break;
						
				case EULER:
					try {
						EulerAngles eul = misorPane.getEulerFlds().getAngles();
						Mfix.set(eul);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Euler angles must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				case MATRIX:
					try {
						Mfix = misorPane.getMatrixFlds().getMatrix();
											
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Matrix elements must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					final Matrix3x3 orth = Mfix.nearestOrthogonal();
					final double dist = Mfix.distSq(orth);
										
					if(dist < 1e-4d && Math.abs(Mfix.det() - 1d) < 1e-3d) {
						Mfix = orth;						
					} else {						
						Mfix = orth;						
						if(Mfix.det() > 0d) {
							int answer = JOptionPane.showConfirmDialog(
									Stats_PlanesFixedMisorFrame.this,
								    "Provided rotation matrix is not orthogonal.\n" +
									"Would you like to replace it by the nearest orthogonal matrix and continue?",
								    "Warning",
								    JOptionPane.YES_NO_OPTION);
							if(answer == JOptionPane.NO_OPTION) return; 		
						} else {
							JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
									"Provided matrix is not a proper rotation matrix.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;							
						}																													
					}
					misorPane.getMatrixFlds().setMatrix(Mfix);
					
					break;
				case QUATERNION:
					try {
						Quaternion quat = misorPane.getQuatFlds().getQuaternion();
						Mfix.set(quat);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Quaternion components must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				case RODRIGUES:
					try {
						RodriguesParams rodr = misorPane.getRodriguesFlds().getRodrigues();
						Mfix.set(rodr);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Rodrigues parameters must be decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				default: break;
				}
				
				// calculate GBCD
				
				
				switch(gbcdMethod) {
				
				case DIST_FUN:
					
					final double misTol;
					final double planeTol;
					try {
						misTol = distOpts.getMisorTol();
						if(misTol < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Tolerance must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
					
						
						
								
					try {
						planeTol = distOpts.getPlaneTol();
						if(planeTol < 0d) throw new NumberFormatException();																		
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Tolerance must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
					

					
		
					int nBins;
					try {
						nBins = distOpts.getNBins();
						if(nBins < 0) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Number of bins must be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
					
					
					gbcdDistFun = new GBCD_DistFunWorker(Mfix, gbFiles,
							misTol, planeTol, nBins, eliminate, areaThreshold,  
							outFld,
							fireBtn, abortBtn, progressBar, distOpts.doNormalize()
							);
					
					gbcdDistFun.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer)evt.getNewValue());
							}						
						}				
					});
					
					try {
						
						gbcdDistFun.execute();
						
					} catch(Exception e) {
						abortBtn.setEnabled(false);
						fireBtn.setEnabled(true);
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"An error occurred.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;	
					}
										
					
					break;
					
				case BINS: 
					
					
					final int D1;
					try {
						D1 = binsOpts.getD1();
						if(D1 <= 0) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Number of bins must be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
					final int D2;
					try {
						D2 = binsOpts.getD2();
						if(D2 <= 0) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"Number of bins must be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}

					gbcdBins = new GBCD_BinsWorker(Mfix, gbFiles,
							D1, D2, eliminate, areaThreshold, outFld, fireBtn, abortBtn, progressBar);
					
					gbcdBins.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer)evt.getNewValue());
							}						
						}				
					});
														
					try {
						
						gbcdBins.execute();
						
					} catch(Exception e) {
						e.printStackTrace();
						abortBtn.setEnabled(false);
						fireBtn.setEnabled(true);
						JOptionPane.showMessageDialog(Stats_PlanesFixedMisorFrame.this,
								"An error occurred.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						return;	
					}
					
					
					break;
					
					default: break;
				}
																	
			}
		});
		
		methodCards.add(distOpts, DIST_FUN);
		methodCards.add(binsOpts, BINS);
		
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar, "cell 0 12,gapx 20");
		
		abortBtn = new JButton();
		abortBtn.setEnabled(false);
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(Stats_PlanesFixedMisorFrame.class.getResource("/gui_bricks/abort.png")));
		contentPane.add(abortBtn, "cell 0 12,gapx 20");
		
		predefMisBtn = new JButton();
		predefMisBtn.setToolTipText("Use a predefined misorientation");
		predefMisBtn.setMaximumSize(new Dimension(24,24));
		predefMisBtn.setMinimumSize(new Dimension(24,24));
		predefMisBtn.setPreferredSize(new Dimension(24,24));
		predefMisBtn.setIcon(new ImageIcon(Stats_PlanesFixedMisorFrame.class.getResource("/gui_bricks/sum-icon.png")));
		contentPane.add(predefMisBtn, "cell 0 7,alignx left,gapx 20,aligny top");
		
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
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
			
				switch(gbcdMethod) {
				
					case DIST_FUN: gbcdDistFun.cancel(true); break;
					case BINS: gbcdBins.cancel(true); break;
				}
			}			
		});
		
		
		pack();
		setResizable(false);
	}
	
}
