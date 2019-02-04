package gui_modules;

import enums.PointGroup;
import gui_bricks.LatticePanel;
import gui_bricks.MisorPanel;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import utils.AxisAngle;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.Transformations;
import utils.UnitVector;
import algorithms.RandomMisorGenerator;

public class Catalog_AnalyticalPanel extends JPanel {

	private LatticePanel firstLatticePane;
	private MisorPanel misorPane;
	private JTextField maxFld;
	private JLabel maxLbl;
	
	
	private Matrix3x3 M;
	private double a;
	private double b;
	private double c;
	private int maxIndex;
	
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
	
	/*private static final String HEX_S7 ="\u03A37: (21.79\u00b0; [0001])";
	private static final String HEX_S13 ="\u03A313: (27.80\u00b0; [0001])";
	
	private static final String HEX_83_S10 ="\u03A310 (c\u00B2/a\u00B2=8/3 only): (78.46\u00b0; [101\u03050])";
	private static final String HEX_83_S11 ="\u03A311 (c\u00B2/a\u00B2=8/3 only): (62.96\u00b0; [101\u03050])";
	private static final String HEX_83_S14 ="\u03A314 (c\u00B2/a\u00B2=8/3 only): (44.42\u00b0; [101\u03050])";
	private static final String HEX_83_S17 ="\u03A317 (c\u00B2/a\u00B2=8/3 only): (86.63\u00b0; [21\u03051\u03050])";
	private static final String HEX_83_S38a ="\u03A338a (c\u00B2/a\u00B2=8/3 only): (26.53\u00b0; [21\u03051\u03050])";
	
	private static final String HEX_52_S7 = "\u03A37 (c\u00B2/a\u00B2=5/2 only): (64.62\u00b0; [101\u03050])";
	private static final String HEX_52_S11a = "\u03A311a (c\u00B2/a\u00B2=5/2 only): (35.10\u00b0; [101\u03050])";
	private static final String HEX_52_S11b = "\u03A311b (c\u00B2/a\u00B2=5/2 only): (84.78\u00b0; [21\u03051\u03050])";*/

	
	private final RandomMisorGenerator generator;

	public Catalog_AnalyticalPanel() {
				
		generator = new RandomMisorGenerator();
		
		setLayout(new MigLayout("", "[][]", "[][][::10px][][][::10px][][::10px][]"));
		
		JLabel latticeLbl = new JLabel("<html><b>Crystal structure:</b>");
		add(latticeLbl, "cell 0 0 2 1,alignx left");
		maxFld = new JTextField();
		
		misorPane = null;		
		firstLatticePane = new LatticePanel(null, misorPane);
		misorPane = new MisorPanel("<html><b>Misorientation</b>:", firstLatticePane, maxFld);
		firstLatticePane.setMisorReference(misorPane);
		
		
		add(firstLatticePane, "cell 0 1 2 1,alignx left,gapx 10,growy");
		
		
		JSeparator separator = new JSeparator();
		add(separator, "cell 0 2 2 1,growx,aligny center,gapy 5 5");
		
		final JButton randomBtn = new JButton();
		randomBtn.setToolTipText("Generate a random misorientation");
		randomBtn.setIcon(new ImageIcon(Catalog_AnalyticalPanel.class.getResource("/gui_bricks/random.png")));
		add(randomBtn, "cell 1 3,alignx right,aligny top");
		randomBtn.setPreferredSize(new Dimension(24,24));
		randomBtn.setMinimumSize(new Dimension(24,24));
		randomBtn.setMaximumSize(new Dimension(24,24));
		
		
		randomBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				EulerAngles eul = generator.nextMisor();
																
				switch (misorPane.getMisorAs()) {
				case AXISANGLE:
										
					int maxIdx;

					try {
						
						maxIdx = Integer.parseInt(maxFld.getText());
						if(maxIdx < 1 || maxIdx > ConstantsAndStatic.MAXMILLER) throw new NumberFormatException();
						
					} catch(NumberFormatException exc) {
						
						JOptionPane.showMessageDialog(null,
								"Maximum allowed value for Miller indices must be a positive integer not greater than " + ConstantsAndStatic.MAXMILLER + ".",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						
						return;
					}
					
					AxisAngle aa = new AxisAngle();
					aa.set(eul);
					aa = new AxisAngle();
					aa.set(eul);
										
					try {
						misorPane.getAxisAngleFlds().setAngle(aa.angle());
						misorPane.getAxisAngleFlds().setAxis(aa.axis(), maxIdx);
						
					} catch(NumberFormatException exc) {

						if(firstLatticePane.getPointGroup() != PointGroup._6MMM) {
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
					
					
														
					break;
					
				case EULER:
					misorPane.getEulerFlds().setAngles(eul);
					
					break;
				case MATRIX:								
						Matrix3x3 M = new Matrix3x3();
						M.set(eul);					
						misorPane.getMatrixFlds().setMatrix(M);
					
					
					break;
				case QUATERNION:						
						Quaternion quat = new Quaternion();
						quat.set(eul);	
						misorPane.getQuatFlds().setQuaternion(quat);					
					
					break;						
				case RODRIGUES:
				
						RodriguesParams rod = new RodriguesParams();
						rod.set(eul);
						misorPane.getRodriguesFlds().setRodrigues(rod);
					break;
				default:
					break;
				}
	
			}
			
		});
		
		JButton sigmaBtn = new JButton();
		sigmaBtn.setIcon(new ImageIcon(Catalog_AnalyticalPanel.class.getResource("/gui_bricks/sum-icon.png")));
		sigmaBtn.setToolTipText("Use a predefined misorientation");
		sigmaBtn.setPreferredSize(new Dimension(24, 24));
		sigmaBtn.setMinimumSize(new Dimension(24, 24));
		sigmaBtn.setMaximumSize(new Dimension(24, 24));
		add(sigmaBtn, "cell 1 4,alignx right,aligny top");
		
		sigmaBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
			
				Object[] possibilities = null;

				if(firstLatticePane.getPointGroup() == PointGroup.M3M) {

					possibilities = new Object[]{S3,S5,S7,S9,S11,S13a,S13b,S15,S17a,S17b,S19a,S19b,S21a,S21b,S23,S25a,S25b,S27a,S27b,S29a,S29b,S31a,S31b,S39b		};	

				} else if(firstLatticePane.getPointGroup() == PointGroup._6MMM) {

					possibilities = new Object[]{M_0001_30, M_1010_90, M_2110_90/*, HEX_S7, HEX_S13, HEX_83_S10, HEX_83_S11, HEX_83_S14, HEX_83_S17, HEX_83_S38a, HEX_52_S7, HEX_52_S11a, HEX_52_S11b*/};	

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
							cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
						
						
						// HEX_S7, HEX_S13, HEX_83_S10, HEX_83_S11, HEX_83_S14, HEX_83_S17, HEX_52_S7, HEX_52_S11a, HEX_52_S11b
						
						
						/*case HEX_S7:
							
							n = new UnitVector();
							miller = new MillerIndices();
							miller.set(0, 0, 1);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							
							w = Math.toRadians(21.7868d);
							
						break;
						case HEX_S13:
							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(0, 0, 1);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							
							w = Math.toRadians(27.7958d);
						break;
						
						case HEX_83_S10:

							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(1, 0, 0);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							w = Math.toRadians(78.4630d);
						break;
						case HEX_83_S11:
						
							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(1, 0, 0);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							w = Math.toRadians(62.9643d);
						break;
						case HEX_83_S14:

							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(1, 0, 0);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							w = Math.toRadians(44.4153d);
						break;
						case HEX_83_S17:
							
							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(2, -1, 0);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							w = Math.toRadians(86.6277d);
						break;
						
						case HEX_83_S38a:
							
							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(2, -1, 0);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							w = Math.toRadians(26.5254d);
						break;
						
						// 5/2
						case HEX_52_S7:
							
							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(1, 0, 0);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							w = Math.toRadians(64.62d);
						break;
						
						case HEX_52_S11a:
							
							n = new UnitVector();

							miller = new MillerIndices();
							miller.set(1, 0, 0);
							
							try {
								cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
								aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
							w = Math.toRadians(35.10d);
						break;
						
					case HEX_52_S11b:
						
						n = new UnitVector();
					
						miller = new MillerIndices();
						miller.set(2, -1, 0);
						
						try {
							cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
							aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
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
						w = Math.toRadians(84.78d);
					break;*/
						
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
		
		
		
		maxLbl = new JLabel("<html><b>Maximum Miller index</b> (for representing misorientation axes):");
		maxLbl.setToolTipText("This value is used for rounding Miller indices of misorientation axes an boundary planes");
		add(maxLbl, "flowx,cell 0 6 2 1");
		
		
		maxFld.setToolTipText("This value is used for rounding Miller indices of misorientation axes and boundary planes");
		maxFld.setHorizontalAlignment(SwingConstants.RIGHT);
		maxFld.setText("30");
		add(maxFld, "cell 0 6 2 1");
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
		
		JSeparator separator_3 = new JSeparator();
		add(separator_3, "cell 0 5 2 1,growx,aligny center,gapy 5 5");
		
		
		add(misorPane, "cell 0 3 1 2,alignx left,growy");
		
		JSeparator separator_1 = new JSeparator();
		add(separator_1, "cell 0 7 2 1,growx,aligny center,gapy 5 5");
		
		JButton symmetryBtn = new JButton("Symmetries of GB distributions");
		add(symmetryBtn, "flowx,cell 0 8 2 1,alignx center");
		
		JButton specialBtn = new JButton("Characteristic GBs");
		specialBtn.setToolTipText("Show poles of planes corresponding to geometrically characteristic boundaries");
		
		// FIRE BUTTON
		specialBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
			
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
				
				// read misorientation and lattice
				M = new Matrix3x3();
				
				switch(misorPane.getMisorAs()) {
				case AXISANGLE:
										
					UnitVector n; 
					try {
						n = misorPane.getAxisAngleFlds().getAxis();						
						
					} catch(NumberFormatException exc) {
					
						JOptionPane.showMessageDialog(null,
							    "Miller indices of the rotation axis must be integers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						
						return;			
					}
					
					double omega;	
					try {	
						omega = misorPane.getAxisAngleFlds().getAngle();
						
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
							    "Rotation angle must be a decimal number.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					AxisAngle aa = new AxisAngle();
					aa.set(n, omega);
					M.set(aa);
					
					break;
					
				case EULER:
					try {
						EulerAngles eul = misorPane.getEulerFlds().getAngles();
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
						M = misorPane.getMatrixFlds().getMatrix();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
							    "Rotation matrix entries must be decimal numbers.",
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
						Quaternion quat = misorPane.getQuatFlds().getQuaternion();
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
						RodriguesParams rodr = misorPane.getRodriguesFlds().getRodrigues();
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
				
					
				//maxIndex = Integer.parseInt(maxFld.getText());
									
				a = 1d;
				b = 1d;
				c = 1d;
				
				switch(firstLatticePane.getPointGroup()) {
				
				case M3M:
					
					break;
					
				case _6MMM:
				
					try {
						int aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
						int cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
						if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
						a = Math.sqrt(aSq);
						b = a;
						c = Math.sqrt(cSq);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
							return;
					}
					break;

				case _4MMM:
					
					try {
						a = Double.parseDouble(firstLatticePane.getACPane().getaFld().getText().replace(",","."));
						b = a;
						c = Double.parseDouble(firstLatticePane.getACPane().getcFld().getText().replace(",","."));
						if(a <= 0d || c <= 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Lattice constants must be positive decimal numbers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
							return;
					}
					break;
					
				case MMM:
					
					try {
						a = Double.parseDouble(firstLatticePane.getABCPane().getaFld().getText().replace(",","."));
						b = Double.parseDouble(firstLatticePane.getABCPane().getbFld().getText().replace(",","."));
						c = Double.parseDouble(firstLatticePane.getABCPane().getcFld().getText().replace(",","."));
						if(a <= 0d || b <= 0d || c <= 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Lattice constants must be positive decimal numbers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
							return;
					}
					break;
					
					default:
						break;
				}
				
				
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							Catalog_AnalyticalFrame frame = new Catalog_AnalyticalFrame(M, firstLatticePane.getPointGroup(), misorPane.getMisorAs(), a, b, c, maxIndex);				
							frame.setVisible(true);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		});
		
		
		
		
		//TODO
		symmetryBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
			
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
				
				// read misorientation and lattice
				M = new Matrix3x3();
				
				switch(misorPane.getMisorAs()) {
				case AXISANGLE:
										
					UnitVector n; 
					try {
						n = misorPane.getAxisAngleFlds().getAxis();						
						
					} catch(NumberFormatException exc) {
					
						JOptionPane.showMessageDialog(null,
							    "Miller indices of the rotation axis must be integers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						
						return;			
					}
					
					double omega;	
					try {	
						omega = misorPane.getAxisAngleFlds().getAngle();
						
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
							    "Rotation angle must be a decimal number.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					AxisAngle aa = new AxisAngle();
					aa.set(n, omega);
					M.set(aa);
					
					break;
					
				case EULER:
					try {
						EulerAngles eul = misorPane.getEulerFlds().getAngles();
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
						M = misorPane.getMatrixFlds().getMatrix();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
							    "Rotation matrix entries must be decimal numbers.",
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
						Quaternion quat = misorPane.getQuatFlds().getQuaternion();
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
						RodriguesParams rodr = misorPane.getRodriguesFlds().getRodrigues();
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
				
					
				//maxIndex = Integer.parseInt(maxFld.getText());
									
				a = 1d;
				b = 1d;
				c = 1d;
				
				switch(firstLatticePane.getPointGroup()) {
				
				case M3M:
					
					break;
					
				case _6MMM:
				
					try {
						int aSq = Integer.parseInt(firstLatticePane.getCDivAPane().getnFld().getText());
						int cSq = Integer.parseInt(firstLatticePane.getCDivAPane().getmFld().getText());
						if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
						a = Math.sqrt(aSq);
						b = a;
						c = Math.sqrt(cSq);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
							return;
					}
					break;

				case _4MMM:
					
					try {
						a = Double.parseDouble(firstLatticePane.getACPane().getaFld().getText().replace(",","."));
						b = a;
						c = Double.parseDouble(firstLatticePane.getACPane().getcFld().getText().replace(",","."));
						if(a <= 0d || c <= 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Lattice constants must be positive decimal numbers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
							return;
					}
					break;
					
				case MMM:
					
					try {
						a = Double.parseDouble(firstLatticePane.getABCPane().getaFld().getText().replace(",","."));
						b = Double.parseDouble(firstLatticePane.getABCPane().getbFld().getText().replace(",","."));
						c = Double.parseDouble(firstLatticePane.getABCPane().getcFld().getText().replace(",","."));
						if(a <= 0d || b <= 0d || c <= 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Lattice constants must be positive decimal numbers.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
							return;
					}
					break;
					
					default:
						break;
				}
				
				
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							SymmetriesFrame frame = new SymmetriesFrame(M, firstLatticePane.getPointGroup(), misorPane.getMisorAs(), a, b, c, maxIndex);				
							frame.setVisible(true);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		});
		
		
		
		add(specialBtn, "cell 0 8,alignx center");

	}
	


}
