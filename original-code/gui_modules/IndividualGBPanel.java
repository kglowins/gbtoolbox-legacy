package gui_modules;
import enums.PointGroup;
import gui_bricks.BoundaryParamsPanel;
import gui_bricks.Clipboard;
import gui_bricks.LatticePanel;
import gui_bricks.TestCSLOptsPanel;
import gui_bricks.TestDecompOptsPanel;
import gui_bricks.TestMinimizationOptsPanel;
import gui_bricks.TestTTCOptsPanel;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import utils.AxisAngle;
import utils.CSLMisor;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.Transformations;
import utils.UnitVector;
import algorithms.CSLMisorientations;
import algorithms.IndividualGBTester;
import algorithms.RandomGBGenerator;


public class IndividualGBPanel extends JPanel {

	private LatticePanel latticePanel;
	private BoundaryParamsPanel paramsPanel;
	private TestMinimizationOptsPanel minimizePanel;
	private TestDecompOptsPanel decompPanel;
	private TestCSLOptsPanel cslPanel;
	private TestTTCOptsPanel ttcPanel;
	private Clipboard clipboard;

	private final IndividualGBTester tester;
	private final RandomGBGenerator generator;

	private JTextField maxFld;
	private JButton resetBtn;
	private JCheckBox BTChB;
	private JCheckBox BminChB;
	private JButton reportBtn;

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
	

	private JTextField mulFld;
	

	public IndividualGBPanel() {

		generator = new RandomGBGenerator();

		tester = new IndividualGBTester();
		tester.setSaveDetails(true);		

		setLayout(new MigLayout("", "[][][][]", "[][][][][][][][][][][][][][][][][]"));

		JLabel latticeLbl = new JLabel("<html><b>Crystal structure:</b>");
		add(latticeLbl, "cell 0 0");

		paramsPanel = null;

		JLabel lblMaximalValue = new JLabel("<html><b>Maximum Miller index</b> (needed for conversions and imports):");
		lblMaximalValue.setToolTipText("<html>This value is used for rounding coordinates of<br>misorientation axes and boundary plane normals<br>to corresponding Miller indices");
		add(lblMaximalValue, "flowx,cell 1 1 3 1");

		latticePanel = new LatticePanel(paramsPanel, null);
		add(latticePanel, "cell 0 1 1 2,gapx 10,grow");

		JLabel interchangeLbl = new JLabel("<html><b>Symmetries assumed:</b>");
		interchangeLbl.setToolTipText("<html>If additional symmetries are assumed,<br>then there exist more symmetrically equivalent representations of a given physical boundary");
		add(interchangeLbl, "flowx,cell 1 2 3 1");

		JSeparator sep1 = new JSeparator();
		add(sep1, "cell 0 3 4 1,growx,aligny center,gapy 5 5");

		JLabel gbParamsLbl = new JLabel("<html><b>Boundary parameters:</b>");
		add(gbParamsLbl, "cell 0 4 2 1");

		JLabel clipboardLbl = new JLabel("<html><b>Clipboard:</b>");
		add(clipboardLbl, "cell 2 4,gapx 70");

		maxFld = new JTextField();
		maxFld.setToolTipText("<html>This value is used for rounding coordinates of<br>misorientation axes and boundary plane normals<br>to corresponding Miller indices");

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

		paramsPanel = new BoundaryParamsPanel(latticePanel, maxFld);
		latticePanel.setParamsReference(paramsPanel);
		add(paramsPanel, "cell 0 5 1 5,gapx 10,grow");

		BTChB = new JCheckBox("Grain exchange");
		BminChB = new JCheckBox("Inversion");
		BminChB.setEnabled(false);
		BminChB.setSelected(true);
		BminChB.setToolTipText("<html>If additional symmetries are assumed,<br>then there exist more symmetrically equivalent representations of a given physical boundary");


		clipboard = new Clipboard(paramsPanel, maxFld, BTChB, BminChB, latticePanel);

		JButton randGbBtn = new JButton();
		randGbBtn.setToolTipText("Generate random grain boundary parameters");
		randGbBtn.setMinimumSize(new Dimension(24,24));
		randGbBtn.setMaximumSize(new Dimension(24,24));
		randGbBtn.setPreferredSize(new Dimension(24,24));		
		randGbBtn.setIcon(new ImageIcon(IndividualGBPanel.class.getResource("/gui_bricks/random.png")));

		randGbBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

				InterfaceMatrix B = generator.nextGB();

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

				try {					
					paramsPanel.setPlane(B.m1(), maxIndex);					

				} catch(NumberFormatException exc) {

					if(latticePanel.getPointGroup() != PointGroup._6MMM) {
						JOptionPane.showMessageDialog(null,							
								"Lattice parameters must be positive decimal numbers.\n",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					return;
				}

				switch(paramsPanel.getMisorPane().getMisorAs()) {
				case AXISANGLE:

					AxisAngle aa = new AxisAngle();
					aa.set(B.M());

					paramsPanel.getMisorPane().getAxisAngleFlds().setAxis(aa.axis(), maxIndex);
					paramsPanel.getMisorPane().getAxisAngleFlds().setAngle(aa.angle());

					break;

				case EULER:
					EulerAngles eul = new EulerAngles();
					eul.set(B.M());
					paramsPanel.getMisorPane().getEulerFlds().setAngles(eul);
					break;

				case MATRIX:
					paramsPanel.getMisorPane().getMatrixFlds().setMatrix(B.M());
					break;

				case QUATERNION:
					Quaternion quat = new Quaternion();
					quat.set(B.M());
					paramsPanel.getMisorPane().getQuatFlds().setQuaternion(quat);
					break;

				case RODRIGUES:
					RodriguesParams rodr = new RodriguesParams();
					rodr.set(B.M());
					paramsPanel.getMisorPane().getRodriguesFlds().setRodrigues(rodr);
					break;

				default: break;
				}
			}		
		});

		add(randGbBtn, "cell 1 5,gapx 10,aligny top");
		add(clipboard, "cell 2 5 2 5,gapx 80,grow");

		JButton sigmaBtn = new JButton();
		sigmaBtn.setIcon(new ImageIcon(IndividualGBPanel.class.getResource("/gui_bricks/sum-icon.png")));
		sigmaBtn.setToolTipText("<html>Use a predefined misorientation");
		sigmaBtn.setPreferredSize(new Dimension(24, 24));
		sigmaBtn.setMinimumSize(new Dimension(24, 24));
		sigmaBtn.setMaximumSize(new Dimension(24, 24));
		add(sigmaBtn, "cell 1 6,gapx 10,aligny top");

		sigmaBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				Object[] possibilities = null;

				if(latticePanel.getPointGroup() == PointGroup.M3M) {

					possibilities = new Object[]{S3,S5,S7,S9,S11,S13a,S13b,S15,S17a,S17b,S19a,S19b,S21a,S21b,S23,S25a,S25b,S27a,S27b,S29a,S29b,S31a,S31b,S39b		};	

				} else if(latticePanel.getPointGroup() == PointGroup._6MMM) {

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
							cSq = Integer.parseInt(latticePanel.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(latticePanel.getCDivAPane().getnFld().getText());
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
							cSq = Integer.parseInt(latticePanel.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(latticePanel.getCDivAPane().getnFld().getText());
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
							cSq = Integer.parseInt(latticePanel.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(latticePanel.getCDivAPane().getnFld().getText());
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

					switch(paramsPanel.getMisorPane().getMisorAs()) {
					case AXISANGLE:					

						paramsPanel.getMisorPane().getAxisAngleFlds().setAxis(aa.axis(), maxIndex);
						paramsPanel.getMisorPane().getAxisAngleFlds().setAngle(aa.angle());				
						break;

					case EULER:
						EulerAngles eul = new EulerAngles();
						eul.set(aa);
						paramsPanel.getMisorPane().getEulerFlds().setAngles(eul);
						break;

					case MATRIX:
						final Matrix3x3 M = new Matrix3x3();
						M.set(aa);
						paramsPanel.getMisorPane().getMatrixFlds().setMatrix(M);
						break;

					case QUATERNION:
						Quaternion quat = new Quaternion();
						quat.set(aa);
						paramsPanel.getMisorPane().getQuatFlds().setQuaternion(quat);
						break;

					case RODRIGUES:
						RodriguesParams rodr = new RodriguesParams();
						rodr.set(aa);
						paramsPanel.getMisorPane().getRodriguesFlds().setRodrigues(rodr);
						break;

					default: break;
					}

				}
			}
		});

		JButton swapGrainsBtn = new JButton();
		swapGrainsBtn.setToolTipText("Swap grains");
		swapGrainsBtn.setIcon(new ImageIcon(IndividualGBPanel.class.getResource("/gui_bricks/BT.png")));
		add(swapGrainsBtn, "cell 1 7,gapx 10");

		swapGrainsBtn.setPreferredSize(new Dimension(24, 24));
		swapGrainsBtn.setMinimumSize(new Dimension(24, 24));
		swapGrainsBtn.setMaximumSize(new Dimension(24, 24));

		swapGrainsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				//get boundary parameters
				Matrix3x3 M = new Matrix3x3();
				UnitVector m1;

				try {					
					m1 = paramsPanel.getPlaneNormal();
				} catch(NumberFormatException exc) {

					if(latticePanel.getPointGroup() == PointGroup.M3M) {
						JOptionPane.showMessageDialog(null,						
								"Miller indices of the boundary plane must be integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else if(latticePanel.getPointGroup() != PointGroup._6MMM) {
						JOptionPane.showMessageDialog(null,	
								"Miller indices of the boundary plane must be integers.\n" +
										"Lattice parameters must be positive decimal numbers.",
										"Error",
										JOptionPane.ERROR_MESSAGE);					
					} else {
						JOptionPane.showMessageDialog(null,
								"Miller indices of the boundary plane must be integers.\n" +
										"(c/a)\u00b2 ratio must be a rational number.",
										"Error",
										JOptionPane.ERROR_MESSAGE);
					}
					return;
				}

				switch(paramsPanel.getMisorPane().getMisorAs()) {
				case AXISANGLE:
					double theta;					
					try {
						theta = paramsPanel.getMisorPane().getAxisAngleFlds().getAngle();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Rotation angle must be a decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					UnitVector n;
					try {
						n = paramsPanel.getMisorPane().getAxisAngleFlds().getAxis();								
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Miller indices of the rotation axis must be integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					AxisAngle aa = new AxisAngle();
					aa.set(n, theta);
					M.set(aa);

					break;

				case EULER:
					try {
						EulerAngles eul = paramsPanel.getMisorPane().getEulerFlds().getAngles();
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
						M = paramsPanel.getMisorPane().getMatrixFlds().getMatrix();

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
					paramsPanel.getMisorPane().getMatrixFlds().setMatrix(M);

					break;
				case QUATERNION:
					try {
						Quaternion quat = paramsPanel.getMisorPane().getQuatFlds().getQuaternion();
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
						RodriguesParams rodr = paramsPanel.getMisorPane().getRodriguesFlds().getRodrigues();
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

				final InterfaceMatrix B = new InterfaceMatrix(M, m1);

				B.transpose();

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

				try {					
					paramsPanel.setPlane(B.m1(), maxIndex);
				} catch(NumberFormatException exc) {

					if(latticePanel.getPointGroup() != PointGroup._6MMM) {
						JOptionPane.showMessageDialog(null,							
								"Lattice parameters must be positive decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.", 
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					return;
				}

				switch(paramsPanel.getMisorPane().getMisorAs()) {
				case AXISANGLE:					

					AxisAngle aa = new AxisAngle();
					aa.set(B.M());				
					paramsPanel.getMisorPane().getAxisAngleFlds().setAxis(aa.axis(), maxIndex);
					paramsPanel.getMisorPane().getAxisAngleFlds().setAngle(aa.angle());				
					break;

				case EULER:
					EulerAngles eul = new EulerAngles();
					eul.set(B.M());
					paramsPanel.getMisorPane().getEulerFlds().setAngles(eul);
					break;

				case MATRIX:
					paramsPanel.getMisorPane().getMatrixFlds().setMatrix(B.M());
					break;

				case QUATERNION:
					Quaternion quat = new Quaternion();
					quat.set(B.M());
					paramsPanel.getMisorPane().getQuatFlds().setQuaternion(quat);
					break;

				case RODRIGUES:
					RodriguesParams rodr = new RodriguesParams();
					rodr.set(B.M());
					paramsPanel.getMisorPane().getRodriguesFlds().setRodrigues(rodr);
					break;

				default: break;
				}
			}
		});

		JButton minusBtn = new JButton();
		minusBtn.setIcon(new ImageIcon(IndividualGBPanel.class.getResource("/gui_bricks/Bminus.png")));
		minusBtn.setToolTipText("Apply inversion");
		minusBtn.setPreferredSize(new Dimension(24, 24));
		minusBtn.setMinimumSize(new Dimension(24, 24));
		minusBtn.setMaximumSize(new Dimension(24, 24));
		add(minusBtn, "cell 1 8,gapx 10");


		minusBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				//get plane
				UnitVector m1;

				try {					
					m1 = paramsPanel.getPlaneNormal();
				} catch(NumberFormatException exc) {

					if(latticePanel.getPointGroup() == PointGroup.M3M) {
						JOptionPane.showMessageDialog(null,						
								"Miller indices of the boundary plane must be integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else if(latticePanel.getPointGroup() != PointGroup._6MMM) {
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

				m1.negate();

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

				try {					
					paramsPanel.setPlane(m1, maxIndex);
				} catch(NumberFormatException exc) {

					if(latticePanel.getPointGroup() != PointGroup._6MMM) {
						JOptionPane.showMessageDialog(null,							
								"Lattice parameters must be positive decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					return;
				}				
			}
		});

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 10 4 1,growx,aligny center,gapy 5 5");

		JLabel checkLbl = new JLabel("<html><b>Check whether the boundary is of any geometrically characteristic type:</b>");
		add(checkLbl, "flowx,cell 0 11 4 1");

		minimizePanel = new TestMinimizationOptsPanel(clipboard, tester);
		add(minimizePanel, "cell 0 12 4 1,gapx 10");

		decompPanel = new TestDecompOptsPanel();
		add(decompPanel, "cell 0 14 4 1,gapx 10,growy,gapy 15");
		
		ttcPanel = new TestTTCOptsPanel();
		add(ttcPanel, "cell 0 13 4 1,gapx 10,gapy 15,grow");

		cslPanel = new TestCSLOptsPanel();
		add(cslPanel, "flowx,cell 0 15 4 1,gapx 10,growy,gapy 10");


		maxFld.setText("30");
		maxFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(maxFld, "cell 1 1 3 1");
		maxFld.setColumns(4);

		latticePanel.setCSLOpts(cslPanel);

		reportBtn = new JButton();
		reportBtn.setToolTipText("Details of the computations using the minimization method");
		reportBtn.setEnabled(false);
		reportBtn.setPreferredSize(new Dimension(24, 24));
		reportBtn.setMinimumSize(new Dimension(24, 24));
		reportBtn.setMaximumSize(new Dimension(24, 24));
		reportBtn.setIcon(new ImageIcon(IndividualGBPanel.class.getResource("/gui_bricks/details.png")));
		add(reportBtn, "cell 3 11,alignx right,gapx 0 5");
		BTChB.setToolTipText("<html>If additional symmetries are assumed,<br>then there exist more symmetrically equivalent representations of a given physical boundary");

		final JButton asymBtn = new JButton();

		BTChB.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox chb = (JCheckBox) evt.getSource();
				if(chb.isSelected())
					asymBtn.setToolTipText("<html>Find symmetrically equivalent representation of a given boundary<br>with the smallest misorientation angle and the rotation axis in the standard stereographic triangle (SST)");
				else 
					asymBtn.setToolTipText("<html>Find symmetrically equivalent representation of a given boundary<br>with the smallest misorientation angle");				
			}			
		});

		add(BminChB, "cell 1 2 3 1");


		add(BTChB, "cell 1 2 3 1");



		asymBtn.setToolTipText("<html>Find symmetrically equivalent representation of a given boundary<br>with the smallest misorientation angle");

		asymBtn.setIcon(new ImageIcon(IndividualGBPanel.class.getResource("/gui_bricks/Ci.png")));

		asymBtn.setPreferredSize(new Dimension(24, 24));
		asymBtn.setMinimumSize(new Dimension(24, 24));
		asymBtn.setMaximumSize(new Dimension(24, 24));
		add(asymBtn, "cell 1 9,gapx 10,aligny top");
		
		JLabel mulLbl = new JLabel("<html><font color=#0000bb>Boundary multiplicity is</font>");
		add(mulLbl, "flowx,cell 0 16 4 1,gapx 10,gapy 10");
		
		mulFld = new JTextField();
		mulFld.setEditable(false);
		mulFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(mulFld, "cell 0 16,gapy 10");
		mulFld.setColumns(3);
		
		
			JButton fireBtn = new JButton("Test");
			
					// FIRE BUTTON
					fireBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent evt) {
			
			
							if(!(cslPanel.isChecked() || decompPanel.isChecked() || minimizePanel.isChecked() || ttcPanel.isChecked() ) ) {
			
								JOptionPane.showMessageDialog(null,
										"Select at least one test to carry out.",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
								return;
							}
			
							tester.setSymmetryTransformations(Transformations.getSymmetryTransformations(latticePanel.getPointGroup()));
			
							reset();
			
							//get boundary parameters
							Matrix3x3 M = new Matrix3x3();
							UnitVector m1;
			
							try {					
								m1 = paramsPanel.getPlaneNormal();	
								
								System.out.println(m1);
							} catch(NumberFormatException exc) {
			
								if(latticePanel.getPointGroup() == PointGroup.M3M) {
									JOptionPane.showMessageDialog(null,						
											"Miller indices of the boundary plane must be integers.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
								} else if(latticePanel.getPointGroup() != PointGroup._6MMM) {
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
			
			
							switch(paramsPanel.getMisorPane().getMisorAs()) {
							case AXISANGLE:
								double theta;					
								try {
									theta = paramsPanel.getMisorPane().getAxisAngleFlds().getAngle();
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"Rotation angle must be a decimal number.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
									return;
								}
			
								UnitVector n;
								try {
									n = paramsPanel.getMisorPane().getAxisAngleFlds().getAxis();								
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"Miller indices of the rotation axis must be integers.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
									return;
								}
								AxisAngle aa = new AxisAngle();
								aa.set(n, theta);
								M.set(aa);
			
								break;
			
							case EULER:
								try {
									EulerAngles eul = paramsPanel.getMisorPane().getEulerFlds().getAngles();
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
									M = paramsPanel.getMisorPane().getMatrixFlds().getMatrix();
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
								paramsPanel.getMisorPane().getMatrixFlds().setMatrix(M);
			
								break;
							case QUATERNION:
								try {
									Quaternion quat = paramsPanel.getMisorPane().getQuatFlds().getQuaternion();
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
									RodriguesParams rodr = paramsPanel.getMisorPane().getRodriguesFlds().getRodrigues();
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
			
							InterfaceMatrix B = new InterfaceMatrix(M, m1);
													
							double minimizeTol = 0d;			
							double maxAngle = 0d;				
							double tolTTC = 0d;
							
			
							if(minimizePanel.isChecked()) {
			
								tester.useMinimization(true, true, true, true);
			
								try {						
									minimizeTol = Math.toRadians(minimizePanel.getDeviation());
									if(minimizeTol <= 0d) throw new NumberFormatException(); 
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"The tolerance for the distance minimization approach must be a positive decimal number.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
									return;
								}	
			
							}
			
			
							if(decompPanel.isChecked()) {
			
								tester.useDecomp(true);
			
								try {
									maxAngle = Math.toRadians(decompPanel.getMaxAngle());
									if(maxAngle <= 0d) throw new NumberFormatException(); 
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"The tolerance in Fortes decomposition must be a positive decimal number.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
									return;
								}
							
							}
							
							if(ttcPanel.isChecked()) {
			
								tester.useTTC(true);
			
								try {
									tolTTC = Math.toRadians(ttcPanel.getTolTTC());
									if(tolTTC <= 0d) throw new NumberFormatException(); 
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"The tolerance for the TTC-based parameters must be a decimal number.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
									return;
								}
			
							}
			
							double p = 0d;
							double omega0 = 0d;
			
							if(cslPanel.isChecked()) {
			
								try {
									p = cslPanel.getP();
									if(p > 1d || p < ConstantsAndStatic.MINBPOWER) throw new NumberFormatException();
			
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"p must be a decimal number between " + ConstantsAndStatic.MINBPOWERST + " and 1.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
									return;
								}
			
								try {
									omega0 = Math.toRadians( cslPanel.getOmega0() );
									if(omega0 <= 0d) throw new NumberFormatException();
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"<html>\u03C9<sub>0</sub> must be a positive decimal number.",
											"Error",
											JOptionPane.ERROR_MESSAGE);
									return;
								}
			
								CSLMisor[] csl = null;
			
								int maxSigma;
								try {
									maxSigma = cslPanel.getMaxSigma();
									if (maxSigma > ConstantsAndStatic.MAXSIGMA || maxSigma < 1) throw new NumberFormatException();
								} catch(NumberFormatException exc) {
									JOptionPane.showMessageDialog(null,
											"Maximum \u03a3 value must be a positive integer not greater than " + ConstantsAndStatic.MAXSIGMA + ".",
											"Error",
											JOptionPane.ERROR_MESSAGE);	
									return;
								}
			
			
								switch(latticePanel.getPointGroup()) {
								case M3M:
									csl = CSLMisorientations.getForCubic(maxSigma);
									break;
			
								case _6MMM:
									int m;
									int n;
			
									try {
										m = Integer.parseInt(latticePanel.getCDivAPane().getmFld().getText());
										n = Integer.parseInt(latticePanel.getCDivAPane().getnFld().getText());
			
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
			
								tester.testCSL(p, omega0, csl);
			
							}				
							
							tester.setSymmetriesInvolved(BTChB.isSelected(), BminChB.isSelected());
			
							setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
							tester.test(B);
			
							if(minimizePanel.isChecked()) {
								minimizePanel.setTilt(tester.getNearestTiltDist() <= minimizeTol);
								minimizePanel.setTwist(tester.getNearestTwistDist() <= minimizeTol);
								minimizePanel.setSymmetric(tester.getNearestSymDist() <= minimizeTol);
								minimizePanel.setImpropQuasi(tester.getNearestImpropDist() <= minimizeTol);
			
								minimizePanel.setTiltDist(tester.getNearestTiltDist());
								minimizePanel.setTwistDist(tester.getNearestTwistDist());
								minimizePanel.setSymmetricDist(tester.getNearestSymDist());
								minimizePanel.setImpropQuasiDist(tester.getNearestImpropDist());
			
								minimizePanel.setButtonsEnabled(true);	
							}
			
							if(decompPanel.isChecked()) {
			
								decompPanel.setTilt(tester.getMinTwistLange() <=  maxAngle);
								decompPanel.setTwist(tester.getMinTiltLange() <= maxAngle);
			
								decompPanel.setTiltAngle(tester.getMinTiltLange());
								decompPanel.setTwistAngle(tester.getMinTwistLange());
							}
							
							if(ttcPanel.isChecked()) {
			
								ttcPanel.setTwist(tester.getAlphaN() <=  tolTTC);
								ttcPanel.setTilt(tester.getAlphaL() <= tolTTC);
								ttcPanel.setSym(tester.getAlphaS() <=  tolTTC);
								ttcPanel.setImprop(tester.getAlphaI() <= tolTTC);
			
								ttcPanel.setAlphaN(tester.getAlphaN());
								ttcPanel.setAlphaL(tester.getAlphaL());
								ttcPanel.setAlphaS(tester.getAlphaS());
								ttcPanel.setAlphaI(tester.getAlphaI());
							}
			
							if(cslPanel.isChecked()) {
			
								if(tester.getMinSigma() > 0) cslPanel.setCSL(true, Integer.toString(tester.getMinSigma()) );
								else cslPanel.setCSL(false, "");							
							}
			
							resetBtn.setEnabled(true);
							if(minimizePanel.isChecked()) reportBtn.setEnabled(true);
							
							
							mulFld.setText(new Integer(tester.getMultiplicity()).toString());
			
							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			
						}			
					});
		
resetBtn = new JButton();
resetBtn.setToolTipText("Clear all results");
resetBtn.setEnabled(false);
resetBtn.setIcon(new ImageIcon(IndividualGBPanel.class.getResource("/gui_bricks/eraser.png")));
resetBtn.setMinimumSize(new Dimension(24,24));
resetBtn.setMaximumSize(new Dimension(24,24));
resetBtn.setPreferredSize(new Dimension(24,24));
add(resetBtn, "cell 3 16,alignx right,gapx 0 10,aligny bottom");

		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
reset();
			}
		});
					
		add(fireBtn, "cell 3 16,alignx right,gapx 0 5,aligny bottom");

		asymBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {

				//get boundary parameters
				Matrix3x3 M = new Matrix3x3();
				UnitVector m1;

				try {					
					m1 = paramsPanel.getPlaneNormal();
				} catch(NumberFormatException exc) {

					if(latticePanel.getPointGroup() == PointGroup.M3M) {
						JOptionPane.showMessageDialog(null,						
								"Miller indices of the boundary plane must be integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else if(latticePanel.getPointGroup() != PointGroup._6MMM) {
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


				switch(paramsPanel.getMisorPane().getMisorAs()) {
				case AXISANGLE:
					double theta;					
					try {
						theta = paramsPanel.getMisorPane().getAxisAngleFlds().getAngle();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Rotation angle must be a decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					UnitVector n;
					try {
						n = paramsPanel.getMisorPane().getAxisAngleFlds().getAxis();								
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Miller indices of the rotation axis must be integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					AxisAngle aa = new AxisAngle();
					aa.set(n, theta);
					M.set(aa);

					break;

				case EULER:
					try {
						EulerAngles eul = paramsPanel.getMisorPane().getEulerFlds().getAngles();
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
						M = paramsPanel.getMisorPane().getMatrixFlds().getMatrix();
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
					paramsPanel.getMisorPane().getMatrixFlds().setMatrix(M);

					break;
				case QUATERNION:
					try {
						Quaternion quat = paramsPanel.getMisorPane().getQuatFlds().getQuaternion();
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
						RodriguesParams rodr = paramsPanel.getMisorPane().getRodriguesFlds().getRodrigues();
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

				final InterfaceMatrix B = new InterfaceMatrix(M, m1);
				final InterfaceMatrix Basym = B.getRepresWithDisor(latticePanel.getPointGroup(), BTChB.isSelected());

				//SAVE EQUIVALENT

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

				try {					
					paramsPanel.setPlane(Basym.m1(), maxIndex);					
				} catch(NumberFormatException exc) {

					if(latticePanel.getPointGroup() != PointGroup._6MMM) {
						JOptionPane.showMessageDialog(null,							
								"Lattice parameters must be positive decimal numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					return;
				}

				switch(paramsPanel.getMisorPane().getMisorAs()) {
				case AXISANGLE:					
					AxisAngle aa = new AxisAngle();
					aa.set(Basym.M());						
					paramsPanel.getMisorPane().getAxisAngleFlds().setAxis(aa.axis(), maxIndex);
					paramsPanel.getMisorPane().getAxisAngleFlds().setAngle(aa.angle());						
					break;

				case EULER:
					EulerAngles eul = new EulerAngles();
					eul.set(Basym.M());
					paramsPanel.getMisorPane().getEulerFlds().setAngles(eul);
					break;

				case MATRIX:
					paramsPanel.getMisorPane().getMatrixFlds().setMatrix(Basym.M());
					break;

				case QUATERNION:
					Quaternion quat = new Quaternion();
					quat.set(Basym.M());
					paramsPanel.getMisorPane().getQuatFlds().setQuaternion(quat);
					break;

				case RODRIGUES:
					RodriguesParams rodr = new RodriguesParams();
					rodr.set(Basym.M());
					if(rodr.isHalfTurn()) {
						JOptionPane.showMessageDialog(null,
								"<html>Provided misorientation is a half-turn. Rodrigues parameters are infinite.<br>Another parameterization is recommended.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					paramsPanel.getMisorPane().getRodriguesFlds().setRodrigues(rodr);
					break;

				default: break;
				}

			}

		});

		reportBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

				DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
				final StringBuilder html = new StringBuilder();

				html.append("<html><body bgcolor=#ffffff textcolor=#000000>");

				html.append("<font color=blue><u>Interface matrix of the tested grain boundary</u>:</font><br><br>");				
				html.append("<table><tr><td><b>B</b><sub>test</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.lastTested().toHTMLTable() + "</td></tr></table><br><br>");

				if(tester.isTestTiltMin()) {					
					html.append("<hr><br><u><font color=blue>Symmetrically equivalent representation of the tested boundary<br>with the smallest distance to the nearest pure-tilt boundary</u>:</font><br><br>");
					html.append("<b>B</b>'&nbsp;&nbsp;=&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;<b>B</b><sub>test</sub>");
					if(tester.equivTiltT) html.append("<sup>T</sup>");
					if(tester.equivTiltM) html.append("<sup><b>-</b></sup>");
					html.append("&nbsp;<b>C</b><sub>2</sub><sup>T</sup>, where<br><br>");
					html.append("<table><tr><td><b>B</b>'&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getEquivForTilt().toHTMLTable() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC1tilt().toHTMLTable4() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>2</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC2tilt().toHTMLTable4() + "</td>" +
							"<tr></table><br>");
					html.append("Distance to the nearest tilt boundary is: "+ df.format(Math.toDegrees(tester.getNearestTiltDist())) +" deg.<br><br>");
					html.append("Interface matrix of the nearest tilt boundary is:<br><br>");
					html.append("<table><tr><td><b>B</b><sub>tilt</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getNearestTilt().toHTMLTable() + "</td></tr></table><br><br>");		
				}

				if(tester.isTestTwistMin()) {					
					html.append("<hr><br><font color=blue><u>Symmetrically equivalent representation of the tested boundary<br>with the smallest distance to the nearest pure-twist boundary</u>:</font><br><br>");
					html.append("<b>B</b>'&nbsp;&nbsp;=&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;<b>B</b><sub>test</sub>");
						
					if(tester.equivTwistT) html.append("<sup>T</sup>");
					if(tester.equivTwistM) html.append("<sup><b>-</b></sup>");
							
					html.append("&nbsp;<b>C</b><sub>2</sub><sup>T</sup>, where<br><br>");
					html.append("<table><tr><td><b>B</b>'&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getEquivForTwist().toHTMLTable() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC1twist().toHTMLTable4() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>2</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC2twist().toHTMLTable4() + "</td>" +
							"<tr></table><br>");
					html.append("Distance to the nearest twist boundary is: "+ df.format(Math.toDegrees(tester.getNearestTwistDist())) +" deg.<br><br>");
					html.append("Interface matrix of the nearest twist boundary is:<br><br>");
					html.append("<table><tr><td><b>B</b><sub>twist</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getNearestTwist().toHTMLTable() + "</td></tr></table><br><br>");		
				}

				if(tester.isTestSymMin()) {					
					html.append("<hr><br><font color=blue><u>Symmetrically equivalent representation of the tested boundary<br>with the smallest distance to the nearest pure-symmetric boundary</u>:</font><br><br>");
					html.append("<b>B</b>'&nbsp;&nbsp;=&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;<b>B</b><sub>test</sub>");
							
					if(tester.equivSymT) html.append("<sup>T</sup>");
					if(tester.equivSymM) html.append("<sup><b>-</b></sup>");
							
					html.append("&nbsp;<b>C</b><sub>2</sub><sup>T</sup>, where<br><br>");
					html.append("<table><tr><td><b>B</b>'&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getEquivForSymmetric().toHTMLTable() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC1symmetric().toHTMLTable4() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>2</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC2symmetric().toHTMLTable4() + "</td>" +
							"<tr></table><br>");
					html.append("Distance to the nearest symmetric boundary is: "+ df.format(Math.toDegrees(tester.getNearestSymDist())) +" deg.<br><br>");
					html.append("Interface matrix of the nearest symmetric boundary is:<br><br>");
					html.append("<table><tr><td><b>B</b><sub>sym</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getNearestSymmetric().toHTMLTable() + "</td></tr></table><br><br>");		
				}

				if(tester.isTestImpropMin()) {					
					html.append("<hr><br><font color=blue><u>Symmetrically equivalent representation of the tested boundary<br>with the smallest distance to the nearest ideal improperly quasi-symmetric boundary</u>:</font><br><br>");
					html.append("<b>B</b>'&nbsp;&nbsp;=&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;<b>B</b><sub>test</sub>"); 
							
					if(tester.equivImpT) html.append("<sup>T</sup>");
					if(tester.equivImpM) html.append("<sup><b>-</b></sup>");
					html.append("&nbsp;<b>C</b><sub>2</sub><sup>T</sup>, where<br><br>");
					html.append("<table><tr><td><b>B</b>'&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getEquivForImprop().toHTMLTable() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>1</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC1improp().toHTMLTable4() + "</td>" +
							"<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>C</b><sub>2</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getC2improp().toHTMLTable4() + "</td>" +
							"<tr></table><br>");
					html.append("Distance to the nearest improperly quasi-symmetric boundary is: "+ df.format(Math.toDegrees(tester.getNearestImpropDist())) +" deg.<br><br>");
					html.append("Interface matrix of the nearest improperly quasi-symmetric boundary is:<br><br>");
					html.append("<table><tr><td><b>B</b><sub>improp</sub>&nbsp;&nbsp;&nbsp;=&nbsp;&nbsp;</td><td>" + tester.getNearestImprop().toHTMLTable() + "</td></tr></table><br><br>");		
				}


				html.append("</font></body></html>");

				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							JTextPane tp = new JTextPane();
							JScrollPane js = new JScrollPane();
							js.setViewportView(tp);
							JFrame jf = new JFrame();
							jf.getContentPane().add(js);
							jf.pack();
							jf.setSize(780,580);
							jf.setVisible(true); 
							jf.setIconImage(Toolkit.getDefaultToolkit().getImage(IndividualGBPanel.class.getResource("/gui_bricks/gbtoolbox.png")));
							jf.setTitle("GBToolbox: details of the minimization-based computations");
							jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
							tp.setEditable(false);
							tp.setContentType("text/html");
							tp.setText(html.toString());		
							tp.setCaretPosition(0);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			}
		});


	}

	private final void reset() {
		minimizePanel.reset();
		minimizePanel.setButtonsEnabled(false);
		decompPanel.reset();
		cslPanel.reset();
		resetBtn.setEnabled(false);
		reportBtn.setEnabled(false);
		ttcPanel.reset();
		mulFld.setText("");
	}

}
