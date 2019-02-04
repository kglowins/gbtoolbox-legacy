package gui_modules;

import enums.Layer;
import enums.MisorientationAs;
import enums.PointGroup;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import say.swing.JFontChooser;
import utils.AxisAngle;
import utils.EulerAngles;
import utils.FileUtils;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.Transformations;
import algorithms.Catalog_AnalyticalFigure;

public class Catalog_AnalyticalFrame extends JFrame {

	private JPanel contentPane;
	
	
	private Color cTilt;
	private Color cTwist;
	private Color cSym;
	private Color cImprop;
	private Color cFont;
	
	private JButton tiltColBtn;
	private JButton twistColBtn;
	private JButton symColBtn;
	private JButton impropColBtn;
	
	private static final String TILT = "Tilt GBs";
	private static final String TWIST = "Twist GBs";
	private static final String SYM = "Symmetric GBs";
	private static final String IMPROP = "Improp. quasi-symmetric GBs";
	private static final String EMPTY = "Empty";
	
	
	private FileUtils.PNGFileFilter pngFilter = new FileUtils.PNGFileFilter();
	private FileUtils.EPSFileFilter epsFilter = new FileUtils.EPSFileFilter();
	
	
	private Font font;
	private int maxIdx;
	
	private File lastDir = null;

	private PointGroup ptGrp;

	private double a, b, c;

	public Catalog_AnalyticalFrame(Matrix3x3 M, PointGroup ptGrp, MisorientationAs misorAs, double a, double b, double c, int idx) {
		
		setTitle("GBToolbox: Geometrically characteristic boundaries for a fixed misorientation");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Catalog_AnalyticalFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
		
		//init
		maxIdx = idx;
		
		this.a = a;
		this.b = b;
		this.c = c;
		
	
		cTilt = Color.CYAN;
		cTwist = Color.RED;
		cSym = Color.BLUE;
		cImprop = Color.GREEN;
		cFont = Color.BLACK;
		
		font = new Font("Arial", Font.PLAIN, 12);
		//this.latticePane = latticePane;
		
		this.ptGrp = ptGrp;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	//	setBounds(100, 100, 1020, 670);
	//	setMinimumSize(new Dimension(1000,660));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][][][]", "[][][][][][][][][][][][][][][][][][]"));
		
		JLabel lblMisorientation = new JLabel("<html><b>Misorientation:</b>");
		contentPane.add(lblMisorientation, "cell 0 0 4 1");
		
		final Catalog_AnalyticalFigure polePane = new Catalog_AnalyticalFigure(M, ptGrp, a, b, c, maxIdx);
		contentPane.add(polePane, "cell 4 0 1 18,gapx 20");
		
		JLabel lblMisorDetails = new JLabel(misorToLabel(M, misorAs));
		contentPane.add(lblMisorDetails, "cell 0 1 4 1,gapx 10");
		
		JLabel lblPtGrpDetails = new JLabel("<html><b>Point group:</b>");
		contentPane.add(lblPtGrpDetails, "flowx,cell 0 2 4 1,gapy 5");
		
		JLabel lblContent = new JLabel("<html><b>Layers to be drawn:</b>");
		contentPane.add(lblContent, "flowx,cell 0 3 4 1,gapy 10");
		
		final JComboBox layer1Cb = new JComboBox();
		layer1Cb.setModel(new DefaultComboBoxModel(new String[] {TILT, TWIST, SYM, IMPROP, EMPTY}));
		layer1Cb.setSelectedIndex(0);
		contentPane.add(layer1Cb, "cell 0 4 2 1,gapx 10,gapy 5");
		
		JLabel firstLayerLbl = new JLabel("(bottommost)");
		contentPane.add(firstLayerLbl, "cell 2 4 2 1,alignx left");
		
		final JComboBox layer2Cb = new JComboBox();
		layer2Cb.setModel(new DefaultComboBoxModel(new String[] {TILT, TWIST, SYM, IMPROP, EMPTY}));
		layer2Cb.setSelectedIndex(3);
		contentPane.add(layer2Cb, "cell 0 5 2 1,gapx 10");
		
		final JComboBox layer3Cb = new JComboBox();
		layer3Cb.setModel(new DefaultComboBoxModel(new String[] {TILT, TWIST, SYM, IMPROP, EMPTY}));
		layer3Cb.setSelectedIndex(2);
		contentPane.add(layer3Cb, "cell 0 6 2 1,gapx 10");
		
		final JComboBox layer4Cb = new JComboBox();
		layer4Cb.setModel(new DefaultComboBoxModel(new String[] {TILT, TWIST, SYM, IMPROP, EMPTY}));
		layer4Cb.setSelectedIndex(1);
		contentPane.add(layer4Cb, "cell 0 7 2 1,gapx 10");
		
		JLabel fourthLayerLbl = new JLabel("(topmost)");
		contentPane.add(fourthLayerLbl, "cell 2 7 2 1,alignx left");
		
		JLabel optsLbl = new JLabel("<html><b>Setings:</b>");
		contentPane.add(optsLbl, "cell 0 8 4 1,gapy 10");
		
		JLabel tiltLbl = new JLabel("Tilt GBs:");
		contentPane.add(tiltLbl, "cell 0 9,alignx left,gapx 10");
		
		tiltColBtn = new JButton();
		tiltColBtn.setToolTipText("Choose color");
		tiltColBtn.setPreferredSize(new Dimension(22, 22));
		tiltColBtn.setMinimumSize(new Dimension(22, 22));
		tiltColBtn.setMaximumSize(new Dimension(22, 22));
		tiltColBtn.setBackground(cTilt);
		tiltColBtn.setContentAreaFilled(false);
		tiltColBtn.setOpaque(true);
		contentPane.add(tiltColBtn, "cell 1 9,gapx 10");
		
		
		tiltColBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Color newColor = JColorChooser.showDialog(Catalog_AnalyticalFrame.this, "Choose a color for representing tilt GBs", cTilt);
				if (newColor != null) {
					cTilt = newColor;
					tiltColBtn.setBackground(cTilt);					
				}
			}
		});
		
		JLabel tiltWidthLbl = new JLabel("Line width:");
		contentPane.add(tiltWidthLbl, "cell 2 9,gapx 10");
		
		final JSpinner tiltWidthSpin = new JSpinner();
		tiltWidthSpin.setPreferredSize(new Dimension(50,22));
		tiltWidthSpin.setMinimumSize(new Dimension(50,22));
	//	tiltWidthSpin.setMaximumSize(new Dimension(50,22));
		tiltWidthSpin.setModel(new SpinnerNumberModel(new Float(1.0f), new Float(0.5f), new Float(10.0f), new Float(0.5f)));
		contentPane.add(tiltWidthSpin, "cell 3 9");
		
		JLabel twistLbl = new JLabel("Twist GBs:");
		contentPane.add(twistLbl, "cell 0 10,alignx left,gapx 10");
		
		
		twistColBtn = new JButton();
		twistColBtn.setToolTipText("Choose color");
		contentPane.add(twistColBtn, "cell 1 10,gapx 10");
		twistColBtn.setPreferredSize(new Dimension(22, 22));
		twistColBtn.setMinimumSize(new Dimension(22, 22));
		twistColBtn.setMaximumSize(new Dimension(22, 22));
		twistColBtn.setBackground(cTwist);
		twistColBtn.setContentAreaFilled(false);
		twistColBtn.setOpaque(true);
		
		twistColBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Color newColor = JColorChooser.showDialog(Catalog_AnalyticalFrame.this, "Choose a color for representing twist GBs", cTwist);
				if (newColor != null) {
					cTwist = newColor;
					twistColBtn.setBackground(cTwist);					
				}
			}
		});
		
		JLabel twistSizeLbl = new JLabel("Point size:");
		contentPane.add(twistSizeLbl, "cell 2 10,gapx 10");
		
		final JSpinner twistSizeSpin = new JSpinner();
		twistSizeSpin.setPreferredSize(new Dimension(50,22));
		twistSizeSpin.setMinimumSize(new Dimension(50,22));
		//twistSizeSpin.setMaximumSize(new Dimension(50,22));
		twistSizeSpin.setModel(new SpinnerNumberModel(10, 5, 25, 1));
		contentPane.add(twistSizeSpin, "cell 3 10");
		
		JLabel symLbl = new JLabel("Symmetric GBs:");
		contentPane.add(symLbl, "cell 0 11,alignx left,gapx 10");
		
		symColBtn = new JButton();
		symColBtn.setToolTipText("Choose color");
		contentPane.add(symColBtn, "cell 1 11,gapx 10");
		symColBtn.setPreferredSize(new Dimension(22, 22));
		symColBtn.setMinimumSize(new Dimension(22, 22));
		symColBtn.setMaximumSize(new Dimension(22, 22));
		symColBtn.setBackground(cSym);
		symColBtn.setContentAreaFilled(false);
		symColBtn.setOpaque(true);
		
		symColBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Color newColor = JColorChooser.showDialog(Catalog_AnalyticalFrame.this, "Choose a color for representing symmetric GBs", cSym);
				if (newColor != null) {
					cSym = newColor;
					symColBtn.setBackground(cSym);					
				}
			}
		});
		
		JLabel symSizeLbl = new JLabel("Point size:");
		contentPane.add(symSizeLbl, "cell 2 11,gapx 10");
		
		final JSpinner symSizeSpin = new JSpinner();
		symSizeSpin.setPreferredSize(new Dimension(50,22));
		symSizeSpin.setMinimumSize(new Dimension(50,22));
		//symSizeSpin.setMaximumSize(new Dimension(50,22));
		symSizeSpin.setModel(new SpinnerNumberModel(18, 5, 25, 1));
		contentPane.add(symSizeSpin, "cell 3 11");
		
		JLabel impropLbl = new JLabel("Improp. quasi-symmetric GBs:");
		contentPane.add(impropLbl, "cell 0 13,alignx left,gapx 10");
		
		impropColBtn = new JButton();
		impropColBtn.setToolTipText("Choose color");
		contentPane.add(impropColBtn, "cell 1 13,gapx 10");
		impropColBtn.setPreferredSize(new Dimension(22, 22));
		impropColBtn.setMinimumSize(new Dimension(22, 22));
		impropColBtn.setMaximumSize(new Dimension(22, 22));
		impropColBtn.setBackground(cImprop);
		impropColBtn.setContentAreaFilled(false);
		impropColBtn.setOpaque(true);
		
		impropColBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Color newColor = JColorChooser.showDialog(Catalog_AnalyticalFrame.this, "Choose a color for representing improperly quasi-symmetric GBs", cImprop);
				if (newColor != null) {
					cImprop = newColor;
					impropColBtn.setBackground(cImprop);					
				}
			}
		});
		
		JLabel impropWidthLbl = new JLabel("Line width:");
		contentPane.add(impropWidthLbl, "cell 2 13,gapx 10");
		
		final JSpinner impropWidthSpin = new JSpinner();
		impropWidthSpin.setPreferredSize(new Dimension(50,22));
		impropWidthSpin.setMinimumSize(new Dimension(50,22));
	//	impropWidthSpin.setMaximumSize(new Dimension(50,20));
		impropWidthSpin.setModel(new SpinnerNumberModel(new Float(3.0f), new Float(0.5f), new Float(10.0f), new Float(0.5f)));
		contentPane.add(impropWidthSpin, "cell 3 13");
		
		final JCheckBox symFillChB = new JCheckBox("Mark symmetric GBs with filled circle");
		contentPane.add(symFillChB, "cell 0 12 4 1,gapx 10");
		
		final JCheckBox millerChB = new JCheckBox("Show Miller indices:");
		millerChB.setToolTipText("Display Miller indices corresponding to twist and symmetric GBs");
		contentPane.add(millerChB, "cell 0 14,gapx 10");
		
		final JButton fontColBtn = new JButton();
		fontColBtn.setToolTipText("Choose color");
		fontColBtn.setPreferredSize(new Dimension(22, 22));
		fontColBtn.setMinimumSize(new Dimension(22, 22));
		fontColBtn.setMaximumSize(new Dimension(22, 22));
		fontColBtn.setBackground(Color.BLACK);
		contentPane.add(fontColBtn, "cell 1 14,gapx 10");
		fontColBtn.setContentAreaFilled(false);
		fontColBtn.setOpaque(true);
		
		fontColBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Color newColor = JColorChooser.showDialog(Catalog_AnalyticalFrame.this, "Choose a color for displaying Miller indices", cFont);
				if (newColor != null) {
					cFont = newColor;
					fontColBtn.setBackground(cFont);					
				}
			}
		});
		
		JLabel lblChooseFont = new JLabel("Font:");
		contentPane.add(lblChooseFont, "cell 2 14,gapx 10");
		
		JButton fontStyleBtn = new JButton();
		fontStyleBtn.setToolTipText("Choose font");
		fontStyleBtn.setPreferredSize(new Dimension(22,22));
		fontStyleBtn.setMinimumSize(new Dimension(22,22));
		fontStyleBtn.setMaximumSize(new Dimension(22,22));
		fontStyleBtn.setIcon(new ImageIcon(Catalog_AnalyticalFrame.class.getResource("/gui_bricks/fonts.png")));
		contentPane.add(fontStyleBtn, "cell 3 14");
		
		final JFontChooser fontChooser = new JFontChooser();
		fontStyleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				 int result = fontChooser.showDialog(Catalog_AnalyticalFrame.this);
				 if (result == JFontChooser.OK_OPTION)
				 {
					 font = fontChooser.getSelectedFont(); 
				 }
			}
		});
		
		final JRadioButton miller1Rb = new JRadioButton("in the 1st crystallite");
		miller1Rb.setSelected(true);
		contentPane.add(miller1Rb, "cell 0 15 4 1,gapx 15");
		
		JRadioButton miller2Rb = new JRadioButton("in the 2nd crystallite");
		contentPane.add(miller2Rb, "cell 0 16 4 1,gapx 15");
		
		JButton redrawBtn = new JButton("(Re)plot");
		redrawBtn.setToolTipText("Replot the <i>map</i> with updated settings");
		contentPane.add(redrawBtn, "flowx,cell 0 17 4 1,alignx center,gapy 20");
		
		ButtonGroup group = new ButtonGroup();
	    group.add(miller1Rb);
	    group.add(miller2Rb);
		
		redrawBtn.addActionListener(new ActionListener() {
			@Override 
			public void actionPerformed(ActionEvent evt) {
				
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				final int sizeTwist = (Integer) twistSizeSpin.getValue();
				final int sizeSym = (Integer) symSizeSpin.getValue();
				final float widthTilt = (Float) tiltWidthSpin.getValue();
				final float widthImprop = (Float) impropWidthSpin.getValue();
				polePane.setSize(sizeTwist, sizeSym, widthTilt, widthImprop);
				polePane.setColors(cTwist, cTilt, cSym, cImprop);				
				
				
				boolean calcTwist = false;
				boolean calcSym = false;
		
				
				Layer layer1 = null;
				switch((String)layer1Cb.getSelectedItem()) {
					case TILT: layer1 = Layer.TILT;  break;
					case TWIST: layer1 = Layer.TWIST; calcTwist = true; break;
					case SYM: layer1 = Layer.SYMMETRIC; calcSym = true; break;
					case IMPROP: layer1 = Layer.IMPROP;  break;
					case EMPTY: layer1 = Layer.EMPTY; break;
					default: break;
				}
				
				Layer layer2 = null;
				switch((String)layer2Cb.getSelectedItem()) {
					case TILT: layer2 = Layer.TILT;  break;
					case TWIST: layer2 = Layer.TWIST; calcTwist = true; break;
					case SYM: layer2 = Layer.SYMMETRIC; calcSym = true; break;
					case IMPROP: layer2 = Layer.IMPROP;  break;
					case EMPTY: layer2 = Layer.EMPTY; break;
					default: break;
				}
				
				Layer layer3 = null;
				switch((String)layer3Cb.getSelectedItem()) {
					case TILT: layer3 = Layer.TILT;  break;
					case TWIST: layer3 = Layer.TWIST; calcTwist = true; break;
					case SYM: layer3 = Layer.SYMMETRIC; calcSym = true; break;
					case IMPROP: layer3 = Layer.IMPROP;  break;
					case EMPTY: layer3 = Layer.EMPTY; break;
					default: break;
				}
				
				Layer layer4 = null;
				switch((String)layer4Cb.getSelectedItem()) {
					case TILT: layer4 = Layer.TILT; break;
					case TWIST: layer4 = Layer.TWIST; calcTwist = true; break;
					case SYM: layer4 = Layer.SYMMETRIC; calcSym = true; break;
					case IMPROP: layer4 = Layer.IMPROP;  break;
					case EMPTY: layer4 = Layer.EMPTY; break;
					default: break;
				}

				polePane.recalculate(calcTwist, calcSym);
				polePane.setLayers(layer1, layer2, layer3, layer4);
				
				polePane.setFilledSym(symFillChB.isSelected());
				
				if(millerChB.isSelected()) {
				
					polePane.setMillerColor(cFont);
					polePane.setMillerFont(font);
					if(miller1Rb.isSelected()) {
						polePane.setShowMiller(1);
					} else {
						polePane.setShowMiller(2);
					}
				
				} else {
					polePane.setShowMiller(0);
				}
				
				polePane.repaint();
				
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}
		});
		
		JButton pngBtn = new JButton("PNG");
		pngBtn.setToolTipText("<html>Export the <i>map</i> to a PNG file");
		contentPane.add(pngBtn, "cell 0 17 4 1,alignx center,gapy 20");
		
		JButton epsBtn = new JButton("EPS");
		epsBtn.setToolTipText("<html>Export the <i>map</i> to an EPS file");
		contentPane.add(epsBtn, "cell 0 17 4 1,alignx center,gapy 20");
		
		JLabel ptGrpLbl = new JLabel(pointGrpToString(ptGrp));
		contentPane.add(ptGrpLbl, "cell 0 2 4 1,gapy 5");
		
		
		
		//final JFileChooser fc = new JFileChooser();
		
		
		final JFileChooser epsFc = new JFileChooser() {
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
				} else { 			  

					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);

					if(ext != null) {
						if(ext.compareTo("eps") != 0) fName += ".eps";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "eps";
						else fName += ".eps";
					}

					File alterF = new File(fName) ;

					if ( alterF.exists() ) {  
						String msg = "The file \"{0}\" already exists!\nAre you sure you want to replace it?";  
						msg = MessageFormat.format( msg, new Object[] { fName } );  
						String title = getDialogTitle();  
						int option = JOptionPane.showConfirmDialog( this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );  
						if ( option == JOptionPane.NO_OPTION ) {  
							return;  
						}  
					}  	
				}
				super.approveSelection();  
			}   
		};
		epsFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		epsFc.setAcceptAllFileFilterUsed(false);
		epsFc.addChoosableFileFilter(epsFilter);
		
		epsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				if(lastDir != null) epsFc.setCurrentDirectory(lastDir);
				
				int returnVal = epsFc.showDialog(Catalog_AnalyticalFrame.this, "Export");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						final File f = epsFc.getSelectedFile();
						String fName = f.getAbsolutePath();						
						final String ext = FileUtils.getExtension(f);
						
						if(ext != null) {
							if(ext.compareTo("eps") != 0) fName += ".eps";
						} else {							
							if(fName.charAt(fName.length() - 1) == '.') fName += "eps";
							else fName += ".eps";
						}
						
						polePane.exportToEPS(fName);
						
						lastDir = f.getParentFile();
						
					} catch(Exception exc) {
						exc.printStackTrace();
						JOptionPane.showMessageDialog(null,
								"An I/O error occured.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
				}	
				
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				

			}
		});
		
		final JFileChooser pngFc = new JFileChooser() {
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
				} else { 			  

					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);

					if(ext != null) {
						if(ext.compareTo("png") != 0) fName += ".png";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "png";
						else fName += ".png";
					}

					File alterF = new File(fName) ;

					if ( alterF.exists() ) {  
						String msg = "The file \"{0}\" already exists!\nAre you sure you want to replace it?";  
						msg = MessageFormat.format( msg, new Object[] { fName } );  
						String title = getDialogTitle();  
						int option = JOptionPane.showConfirmDialog( this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );  
						if ( option == JOptionPane.NO_OPTION ) {  
							return;  
						}  
					}  	
				}
				super.approveSelection();  
			}   
		};
		pngFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		pngFc.setAcceptAllFileFilterUsed(false);
		pngFc.addChoosableFileFilter(pngFilter);
		
		pngBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
							
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				if(lastDir != null) pngFc.setCurrentDirectory(lastDir);

				int returnVal = pngFc.showDialog(Catalog_AnalyticalFrame.this, "Export");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						final File f = pngFc.getSelectedFile();
						String fName = f.getAbsolutePath();						
						final String ext = FileUtils.getExtension(f);
						
						if(ext != null) {
							if(ext.compareTo("png") != 0) fName += ".png";
						} else {							
							if(fName.charAt(fName.length() - 1) == '.') fName += "png";
							else fName += ".png";
						}
						
						polePane.exportToPNG(fName);
						lastDir = f.getParentFile();

					} catch(IOException exc) {
						JOptionPane.showMessageDialog(null,
								"An I/O error occured.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
				}	
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}
		});
		
		pack();
		setResizable(false);
		//setMinimumSize(this.getPreferredSize());
	}
	
	public final String pointGrpToString(PointGroup ptGrp) {
	
		switch(ptGrp) {
		case M3M: return "m3\u0305m";
		case _6MMM: 
			return "6/mmm, (c/a)\u00b2 = " + (int)Math.round(c*c) + "/" + (int)Math.round(a*a);
		case _4MMM: 
	
			
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US); 
			DecimalFormat df = new DecimalFormat("0.###", otherSymbols);			
			
			return "4/mmm, a=" + df.format(a) + ", c=" + df.format(c) + " \u212b";
		case MMM:
			otherSymbols = new DecimalFormatSymbols(Locale.US); 
			df = new DecimalFormat("0.###", otherSymbols);	
		
			return "4/mmm, a=" + df.format(a) + ", b=" + df.format(b) + ", c=" + df.format(c) + " \u212b";
		default: break;			
		}
		return "";
		
		/*switch(ptGrp) {
		case M3M: return "m3\u0305m";
		case _6MMM: 
			final int a = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
			final int c = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());	
			return "6/mmm, (c/a)\u00b2 = " + c + "/" + a;
		case _4MMM: 
			double a0 = Double.parseDouble(latticePane.getACPane().getaFld().getText());
			double c0 = Double.parseDouble(latticePane.getACPane().getcFld().getText());	
			
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US); 
			DecimalFormat df = new DecimalFormat("0.###", otherSymbols);			
			
			return "4/mmm, a=" + df.format(a0) + ", c=" + df.format(c0) + " \u212b";
		case MMM:
			otherSymbols = new DecimalFormatSymbols(Locale.US); 
			df = new DecimalFormat("0.###", otherSymbols);	
			a0 = Double.parseDouble(latticePane.getABCPane().getaFld().getText());
			double b0 = Double.parseDouble(latticePane.getABCPane().getbFld().getText());
			c0 = Double.parseDouble(latticePane.getABCPane().getcFld().getText());				
			return "4/mmm, a=" + df.format(a0) + ", b=" + df.format(b0) + ", c=" + df.format(c0) + " \u212b";
		default: break;			
		}
		return "";*/
	}
	
	
	public final String misorToLabel(Matrix3x3 M, MisorientationAs misorAs) {
		
				
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US); 
		DecimalFormat df = new DecimalFormat("0.####", otherSymbols);
		
		switch (misorAs) {
		case AXISANGLE:
			//MATRIX->AXISANGLE
			AxisAngle aa = new AxisAngle();
			aa.set(M);
			String millerStr = "";
			final MillerIndices mill = new MillerIndices();
			
			switch(ptGrp) {
				case M3M:
					mill.setAsCubic(aa.axis(), maxIdx);
					millerStr = mill.h() + "," + mill.k() + "," + mill.l();
					break;
					
				case _6MMM:
					//double a = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getnFld().getText()));
					//double c = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getmFld().getText()));					
					mill.setAsHexAxis3to4(aa.axis(), maxIdx, Transformations.getCartesianToHex(a, c));
					millerStr = mill.h() + "," + mill.k() + "," + (-mill.h()-mill.k()) + "," + mill.l();
					break;
					
				case _4MMM:
					//a = Double.parseDouble(latticePane.getACPane().getaFld().getText());
				//	c = Double.parseDouble(latticePane.getACPane().getcFld().getText());					
					mill.setAsNonCubicAxis(aa.axis(), maxIdx, Transformations.getCartesianToTetr(a, c));
					millerStr = mill.h() + "," + mill.k() + "," + mill.l();
					break;

				case MMM:
				//	a = Double.parseDouble(latticePane.getABCPane().getaFld().getText());
				//	double b = Double.parseDouble(latticePane.getABCPane().getbFld().getText());
				//	c = Double.parseDouble(latticePane.getABCPane().getcFld().getText());					
					mill.setAsNonCubicAxis(aa.axis(), maxIdx, Transformations.getCartesianToOrth(a, b, c));
					millerStr = mill.h() + "," + mill.k() + "," + mill.l();
					break;

					default: break;
			}
			
			
			return "<html><b>n</b> = [" + millerStr + "]; \u03c9 = " + df.format(Math.toDegrees(aa.angle())) + "\u00b0";
			

		case EULER:
			//MATRIX->EULER
			EulerAngles eul = new EulerAngles();
			eul.set(M);
			return "<html>\u03c6<sub>1</sub> = " + df.format(Math.toDegrees(eul.phi1())) + "\u00b0;&nbsp;&nbsp;\u03a6 = " +  df.format(Math.toDegrees(eul.Phi())) +"\u00b0;&nbsp;&nbsp;  \u03c6<sub>2</sub> = " + df.format(Math.toDegrees(eul.phi2()))+"\u00b0";			
			
		case MATRIX:
			//MATRIX->MATRIX	
			df = new DecimalFormat("0.#####");	
			return "<html><table><tr><td>&nbsp;</td><td>" + df.format(M.e00()) + "</td><td>" + df.format(M.e01()) + "</td><td>" + df.format(M.e02()) + "</td></tr><tr><td>M =&nbsp;</td>" +
				   df.format(M.e10()) + "</td><td>" + df.format(M.e11()) + "</td><td>" + df.format(M.e12()) + "</td></tr><tr><td>&nbsp;</td><td>" +
				   df.format(M.e20()) + "</td><td>" + df.format(M.e21()) + "</td><td>" + df.format(M.e22()) + "</td></tr></table>";
			
		case QUATERNION:
			//MATRIX->QUATERNION
			df = new DecimalFormat("0.#####");	

			Quaternion quat = new Quaternion();
			quat.set(M);
			return "<html>q<sub>0</sub> = " + df.format(quat.q0()) + ";&nbsp;&nbsp;q<sub>1</sub> = " +  df.format(quat.q1()) + ";&nbsp;&nbsp;q<sub>2</sub> = " +  df.format(quat.q2()) + ";&nbsp;&nbsp;q<sub>3</sub> = " +  df.format(quat.q3());						
								
		case RODRIGUES:
			//MATRIX-RODRIGUES
			
			df = new DecimalFormat("0.#####");	

			RodriguesParams rod = new RodriguesParams();
			rod.set(M);
			return "<html>r<sub>1</sub> = " + df.format(rod.r1()) + ";&nbsp;&nbsp;r<sub>2</sub> = " +  df.format(rod.r2()) + ";&nbsp;&nbsp;r<sub>3</sub> = " +  df.format(rod.r3());	
			
		default:
			break;
		}
		return "";
	}

}
