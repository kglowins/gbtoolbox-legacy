package gui_bricks;


import enums.PointGroup;
import gui_bricks.LatticePanel;
import gui_bricks.MisorPanel;
import gui_modules.SubsetsPanel;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import utils.AxisAngle;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.GBPlusLimits;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.UnitVector;
import java.awt.CardLayout;
import javax.swing.JRadioButton;



public class Dialog_GBPlusLimits extends JFrame {
	
	
	private static int gbcount;
	
	private JPanel contentPane;
	private static final String HEX = "HEX";
	private static final String NON_HEX = "NON_HEX";
	
	private MisorPanel misorPane;
	private JTextField maxFld;
	
	private LatticePanel latticePane;
		
	private Matrix3x3 Mfix;
	private UnitVector M1fix;

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
	
	private JLabel lblplaneIndices;
	private JLabel lbltoleranceForThe_1;
	private JTextField planeTolFld;
	private JLabel label_2;
	private JLabel label_3;
	private JPanel planeCards;
	
	private MillerPlaneFields millerPlaneFields;	
	private HexMillerPlaneFields hexPlaneFlds;
	
	private JLabel lbltoleranceForThe;
	private JTextField misorTolFld;
	private JRadioButton arbitPlaneRb;
	private JRadioButton specPlaneRb;
	private JRadioButton arbitMisRb;
	private JRadioButton specMisRb;
	private JLabel label_5;
	private JSeparator separator_3;
	private JButton addBtn;
	private JButton cancelBtn;
	private JLabel lblboundaryMisorientation;
	private JLabel lblboundaryPlane;
	private JLabel lblNewLabel;
	
	private static final String ARBIT_MIS = "Arbitrary misorientation";
	private static final String FIXED_MIS = "Selected misorientation";
	private static final String ARBIT_PLANE = "Arbitrary plane";
	private static final String FIXED_PLANE = "Selected plane";
		
	private boolean fixedMisorientation = true;
	private boolean fixedPlane = false;
	
	private JSeparator separator_2;
	private JLabel lblNewLabel_1;
	private JTextField flagFld;

	private final DecimalFormat df2;
	private final DecimalFormat df3;

	private GBPlusLimits gbToAdd;
	
	public final void setFlag() {
		flagFld.setText("GB type " + gbcount);
	}
	
	public Dialog_GBPlusLimits(PointGroup pointGrp, final SubsetsPanel subsetsPanel) {
		
		gbcount = 1;
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df2 = new DecimalFormat("0.##", otherSymbols);
		df3 = new DecimalFormat("0.###", otherSymbols);
		
		final MisorListener misorListener = new MisorListener();
		final PlaneListener planeListener = new PlaneListener();

		setTitle("GBToolbox: Add a new boundary to the list");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Dialog_GBPlusLimits.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][]", "[][][][][][][][][][][][][][][][][][]"));
		
		JLabel structLbl = new JLabel("<html><b>Crystal symmetry</b> (necessery to convert Miller indices to Cartesian coordinates):");
		contentPane.add(structLbl, "cell 0 0 2 1");
		
		latticePane = new LatticePanel(null, misorPane);
		contentPane.add(latticePane, "cell 0 1 2 1,gapx 20,gapy 5");
		
		JLabel lblMaximumValueFor = new JLabel("<html><b>Maximum Miller index</b> (for representing misorientation axes):");
		contentPane.add(lblMaximumValueFor, "flowx,cell 0 2 2 1,gapy 5");
		
		maxFld = new JTextField();
		maxFld.setHorizontalAlignment(SwingConstants.RIGHT);
		maxFld.setText("30");
		contentPane.add(maxFld, "cell 0 2 2 1,gapy 5");
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
					
					JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
							"Maximum allowed value for Miller indices must be a positive integer not greater than " + ConstantsAndStatic.MAXMILLER + ".",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					maxFld.requestFocus();
					return;
				}
			}
		});
		
		JSeparator separator_1 = new JSeparator();
		contentPane.add(separator_1, "cell 0 3 2 1,growx,aligny center,gapy 5 5");
		
		misorPane = new MisorPanel("<html><u>Fixed misorientation</u>:", latticePane, maxFld);
		latticePane.setMisorReference(misorPane);
		
		latticePane.setPointGroupLocked(pointGrp);
		
		lblboundaryMisorientation = new JLabel("<html><b>Boundary misorientation:</b>");
		contentPane.add(lblboundaryMisorientation, "cell 0 4 2 1,gapy 5");
		
		arbitMisRb = new JRadioButton("Arbitrary");
		arbitMisRb.setActionCommand(ARBIT_MIS);
		arbitMisRb.addActionListener(misorListener);
		contentPane.add(arbitMisRb, "flowx,cell 0 5 2 1,gapx 10,gapy 5");
		
		contentPane.add(misorPane, "flowx,cell 0 6 2 1,gapx 10");
		
		lbltoleranceForThe = new JLabel("<html><u>Tolerance for the specified misorientation</u>:");
		contentPane.add(lbltoleranceForThe, "flowx,cell 0 7 2 1,gapx 10,gapy 5");
		
		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 8 2 1,growx,aligny center,gapy 5 5");
		
	
		
		predefMisBtn = new JButton();
		predefMisBtn.setToolTipText("Use a predefined misorientation");
		predefMisBtn.setMaximumSize(new Dimension(24,24));
		predefMisBtn.setMinimumSize(new Dimension(24,24));
		predefMisBtn.setPreferredSize(new Dimension(24,24));
		predefMisBtn.setIcon(new ImageIcon(Dialog_GBPlusLimits.class.getResource("/gui_bricks/sum-icon.png")));
		contentPane.add(predefMisBtn, "cell 0 6 2 1,alignx left,gapx 20,aligny top");
		
		lblboundaryPlane = new JLabel("<html><b>Boundary plane:</b>");
		contentPane.add(lblboundaryPlane, "cell 0 9 2 1");
		
		arbitPlaneRb = new JRadioButton("Arbitrary");
		arbitPlaneRb.setActionCommand(ARBIT_PLANE);
		arbitPlaneRb.addActionListener(planeListener);
		arbitPlaneRb.setSelected(true);
		contentPane.add(arbitPlaneRb, "flowx,cell 0 10 2 1,gapx 10,gapy 5");
		
		lblplaneIndices = new JLabel("<html><u>Plane indices</u>:");
		contentPane.add(lblplaneIndices, "flowx,cell 0 11,gapx 10,gapy 5");
		
		label_2 = new JLabel("[Miller indices]");
		contentPane.add(label_2, "cell 1 11,gapx 20,gapy 5");
		
		lbltoleranceForThe_1 = new JLabel("<html><u>Tolerance for the specified plane</u>:");
		contentPane.add(lbltoleranceForThe_1, "flowx,cell 0 12,gapx 10,gapy 5");
		
		planeTolFld = new JTextField();
		planeTolFld.setText("10");
		planeTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		planeTolFld.setColumns(3);
		contentPane.add(planeTolFld, "cell 0 12,gapy 5");
		
		label_3 = new JLabel("[degrees]");
		contentPane.add(label_3, "cell 1 12,gapx 20");
		
		planeCards = new JPanel();
		contentPane.add(planeCards, "cell 0 11,gapy 5");
		planeCards.setLayout(new CardLayout(0, 0));
		
		millerPlaneFields = new MillerPlaneFields();
		millerPlaneFields.getKFld().setToolTipText("Miller indices of the boundary plane in the reference frame of the first crystallite");
		
		hexPlaneFlds = new HexMillerPlaneFields();
		planeCards.add(millerPlaneFields, NON_HEX);
		planeCards.add(hexPlaneFlds, HEX);
		
		CardLayout cl_planeCards = (CardLayout) planeCards.getLayout();
		if(pointGrp == PointGroup._6MMM) cl_planeCards.show(planeCards, HEX);
		else cl_planeCards.show(planeCards, NON_HEX);
		
		misorTolFld = new JTextField();
		misorTolFld.setText("5");
		misorTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		misorTolFld.setColumns(3);
		contentPane.add(misorTolFld, "cell 0 7 2 1");
		
		specPlaneRb = new JRadioButton("Specified");
		
		specPlaneRb.setActionCommand(FIXED_PLANE);
		specPlaneRb.addActionListener(planeListener);
		final ButtonGroup groupPlane = new ButtonGroup();
	    groupPlane.add(arbitPlaneRb);
		groupPlane.add(specPlaneRb);
		
		contentPane.add(specPlaneRb, "cell 0 10 2 1");
		
		specMisRb = new JRadioButton("Specified");
		specMisRb.setSelected(true);
		
		specMisRb.setActionCommand(FIXED_MIS);
		specMisRb.addActionListener(misorListener);
		final ButtonGroup groupMisor = new ButtonGroup();
	    groupMisor.add(arbitMisRb);
		groupMisor.add(specMisRb);
		
		
		contentPane.add(specMisRb, "cell 0 5 2 1");
		
		label_5 = new JLabel("[degrees]");
		contentPane.add(label_5, "cell 0 7 2 1,gapx 20");
		
		separator_2 = new JSeparator();
		contentPane.add(separator_2, "cell 0 13 2 1,growx,gapy 5 5");
		
		lblNewLabel_1 = new JLabel("<html><b>Flag</b> (only for user, may be empty):");
		contentPane.add(lblNewLabel_1, "flowx,cell 0 14 2 1");
		
		separator_3 = new JSeparator();
		contentPane.add(separator_3, "cell 0 15 2 1,growx,gapy 5 5");
		
		lblNewLabel = new JLabel("<html><font color=#0000bb><small>If you want to use arbitrary misorientations (planes), then just do not fill in the corresponding fields.</small></font>");
		contentPane.add(lblNewLabel, "cell 0 16 2 1,alignx center,gapy 10 10");
		
		addBtn = new JButton("Add");
		addBtn.setIcon(new ImageIcon(Dialog_GBPlusLimits.class.getResource("/gui_bricks/add-icon.png")));
		contentPane.add(addBtn, "flowx,cell 0 17 2 1,alignx center");
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.setIcon(new ImageIcon(Dialog_GBPlusLimits.class.getResource("/gui_bricks/abort.png")));
		contentPane.add(cancelBtn, "cell 0 17 2 1");
		
		flagFld = new JTextField();
		contentPane.add(flagFld, "cell 0 14");
		flagFld.setColumns(16);
		
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
		
		final JFrame thisFrame = this;
		
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				thisFrame.setVisible(false);
			}
		});
		
		
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				if(arbitMisRb.isSelected() && arbitPlaneRb.isSelected()) {
					JOptionPane.showMessageDialog(null,
							"Both misorientation and plane cannot be arbitrary. Specify at least one.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				//read misor
				double misTol = Double.MAX_VALUE;
				Mfix = new Matrix3x3();
				if(specMisRb.isSelected()) {
					
					try {

						misTol = Math.toRadians(Double.parseDouble(misorTolFld.getText()));
						if(misTol <= 0d) throw new NumberFormatException();

					} catch(NumberFormatException exc) {

						JOptionPane.showMessageDialog(null,
								"The tolerance for misorientations should be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					
					switch(misorPane.getMisorAs()) {
					case AXISANGLE:
						
						double theta;					
						try {
							theta = misorPane.getAxisAngleFlds().getAngle();
						} catch(NumberFormatException exc) {
							JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
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
								JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,						
										"Miller indices of the boundary plane must be integers.",
										"Error",
										JOptionPane.ERROR_MESSAGE);
							} else if(latticePane.getPointGroup() != PointGroup._6MMM) {
								JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,	
									"Miller indices of the boundary plane must be integers.\n" +
									"Lattice parameters must be positive decimal numbers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);					
							} else {
								JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
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
							JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
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
							JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
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
										Dialog_GBPlusLimits.this,
									    "Provided rotation matrix is not orthogonal.\n" +
										"Would you like to replace it by the nearest orthogonal matrix and continue?",
									    "Warning",
									    JOptionPane.YES_NO_OPTION);
								if(answer == JOptionPane.NO_OPTION) return; 		
							} else {
								JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
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
							JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
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
							JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
									"Rodrigues parameters must be decimal numbers.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						break;
					default: break;
					}
					
				}
				
				
				//read plane
				M1fix = new UnitVector();
				double planeTol = Double.MAX_VALUE;
				
				if(specPlaneRb.isSelected()) {
					
					try {

						planeTol = Math.toRadians(Double.parseDouble(planeTolFld.getText()));
						if(planeTol <= 0d) throw new NumberFormatException();

					} catch(NumberFormatException exc) {

						JOptionPane.showMessageDialog(null,
								"The tolerance for boundary planes should be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					
					try {
						switch(latticePane.getPointGroup()) {
						
						case M3M:
							int h = Integer.parseInt(millerPlaneFields.getHFld().getText());
							int k = Integer.parseInt(millerPlaneFields.getKFld().getText());
							int l = Integer.parseInt(millerPlaneFields.getLFld().getText());
							
							M1fix.set(h, k, l);
							break;
							
							
						case _6MMM:
							h = Integer.parseInt(hexPlaneFlds.getHFld().getText());
							k = Integer.parseInt(hexPlaneFlds.getKFld().getText());
							l = Integer.parseInt(hexPlaneFlds.getLFld().getText());
							
							int a0Sq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
							int c0Sq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
							
							if(a0Sq <= 0 || c0Sq <= 0) throw new NumberFormatException();
							
							double a0 = Math.sqrt(a0Sq);
							double c0 = Math.sqrt(c0Sq);
							
							MillerIndices planeMiller = new MillerIndices();
							planeMiller.set(h, k, l);
							
							M1fix.setAsHexagonalPlane(planeMiller, a0, c0);
							break;
							
						case _4MMM:
							
							h = Integer.parseInt(millerPlaneFields.getHFld().getText());
							k = Integer.parseInt(millerPlaneFields.getKFld().getText());
							l = Integer.parseInt(millerPlaneFields.getLFld().getText());
										
							a0 = Double.parseDouble(latticePane.getACPane().getaFld().getText().replace(",", "."));
							c0 = Double.parseDouble(latticePane.getACPane().getcFld().getText().replace(",", "."));
							
							if(a0 <= 0d || c0 <= 0d) throw new NumberFormatException();
							
							planeMiller = new MillerIndices();
							planeMiller.set(h, k, l);
							
							M1fix.setAsTetragonalPlane(planeMiller, a0, c0);
							break;
							
						case MMM:
							h = Integer.parseInt(millerPlaneFields.getHFld().getText());
							k = Integer.parseInt(millerPlaneFields.getKFld().getText());
							l = Integer.parseInt(millerPlaneFields.getLFld().getText());
							
							a0 = Double.parseDouble(latticePane.getABCPane().getaFld().getText().replace(",", "."));
							double b0 = Double.parseDouble(latticePane.getABCPane().getbFld().getText().replace(",", "."));
							c0 = Double.parseDouble(latticePane.getABCPane().getcFld().getText().replace(",", "."));
							
							if(a0 <= 0d || b0 <= 0d ||  c0 <= 0d) throw new NumberFormatException();
							
							planeMiller = new MillerIndices();
							planeMiller.set(h, k, l);
							
							M1fix.setAsOrthorombicPlane(planeMiller, a0, b0, c0);
							break;
							
							default: break;
							
						}
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Dialog_GBPlusLimits.this,
								"Miller indices should be positive numbers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
					
					
					
				}
				
				String flag = flagFld.getText();
				
				GBPlusLimits gbToAdd = new GBPlusLimits(
						flag,
						!fixedMisorientation,
						!fixedPlane,
						misTol,
						planeTol,
						Mfix,
						M1fix
						);
				
				AxisAngle Maa = new AxisAngle();
				Maa.set(Mfix);
				
				
				String gbLabel = "";
				
				gbLabel += flag + ":        M ";
				if(fixedMisorientation) {
					gbLabel += " = "+
							df2.format(Math.toDegrees(Maa.angle())) + "\u00b0 / "
							+"[" + df3.format(Maa.axis().x()) + "," 
							+ df3.format(Maa.axis().y()) + "," 
							+ df3.format(Maa.axis().z()) + "]   (\u00b1" + df2.format(Math.toDegrees(misTol)) +"\u00b0)        m\u2081 ";
				} else {
					gbLabel += "is arbitrary        m\u2081 ";
				}
				
				if(fixedPlane) {
					gbLabel += " = ["+ df3.format(M1fix.x()) + "," 
							+ df3.format(M1fix.y()) + "," 
							+ df3.format(M1fix.z()) + "]   (\u00b1" + df2.format(Math.toDegrees(planeTol)) +"\u00b0)";
				} else {
					gbLabel += "is arbitrary";
				}
				
				subsetsPanel.getListModel().add(subsetsPanel.getListModel().getSize(), gbLabel	);
				subsetsPanel.getGbs().add(gbToAdd);
				
				subsetsPanel.getList().ensureIndexIsVisible(subsetsPanel.getListModel().getSize() - 1);
				subsetsPanel.getRemoveBtn().setEnabled(true);
				
				thisFrame.setVisible(false);
				gbcount++;
			}
		});
		
		
		
		pack();
		setResizable(false);
	}

	
	private class PlaneListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent evt) {
		    String s = evt.getActionCommand().toString();		                            
                        
            switch(s) {
            case ARBIT_PLANE:  
            	fixedPlane = false;            	
            	break;
            case FIXED_PLANE:
            	fixedPlane = true;
            	break;
            	
            default: break;
            }   
		}
	}

	
	
	private class MisorListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent evt) {
		    String s = evt.getActionCommand().toString();		                            
                        
            switch(s) {
            case ARBIT_MIS:  
            	fixedMisorientation = false;            	
            	break;
            case FIXED_MIS:
            	fixedMisorientation = true;
            	break;
            	
            default: break;
            }   
		}
	}
	
	

}
