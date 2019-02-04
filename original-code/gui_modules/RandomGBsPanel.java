package gui_modules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import parallel.TaskResultRandomGB;
import parallel.TaskTestRandomGB;

import net.miginfocom.swing.MigLayout;
import utils.AxisAngle;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.FileUtils;
import utils.Matrix3x3;
import utils.MyMath;
import utils.Transformations;
import utils.UnitVector;
import enums.PointGroup;
import javax.swing.JRadioButton;

import algorithms.MitchellMooreGenerator;

public class RandomGBsPanel extends JPanel {
	private JTextField numberFld;
	private JTextField outFld;
	
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
	
	private static final String M3M = "m3\u0305m";
	private static final String _6MMM = "6/mmm";
	private static final String _4MMM = "4/mmm";
	private static final String MMM = "mmm";
	
	private static final String _2M = "2/m";
	private static final String _3M = "3\u0305m";
	
	private static final String _1 = "1\u0305";
	
	private static final String RAND_MIS = "<html><u>Random misorientations and random planes</u>";
	private static final String FIXED_MIS = "<html><u>Random planes, but a fixed misorientation:</u>";
	
	private boolean fixedMisorientation = false;

	
	private JButton abortBtn;
	private JButton processBtn;

	private JCheckBox calcTiltChB;
	private JCheckBox calcTwistChB;
	private JCheckBox decompChB;
	private JCheckBox calcSymChB;
	private JCheckBox calcImpropChB;

	private final DecimalFormat df2;
	private final DecimalFormat df4;

	private RandomGBsProcessTask task;

	private FileUtils.GBDatFileFilter randgbFilter = new FileUtils.GBDatFileFilter();

	private PointGroup pointGrp;
	private JProgressBar progressBar;
	private JLabel overwrittenLbl;
	private JLabel lblyouCan;
	private JLabel symmetryLbl;
	private JCheckBox BminChB;
	private JCheckBox BTChB;
	private JRadioButton randMisRb;
	private JRadioButton fixedMisRb;
	private JLabel phi1Lbl;
	private JTextField phi1Fld;
	private JLabel PhiLbl;
	private JTextField PhiFld;
	private JLabel phi2Lbl;
	private JTextField phi2Fld;
	private JLabel lblNewLabel_3;
	private JButton sigmaBtn;
	private JCheckBox ttcChB;
	private JCheckBox ttcDisChB;
	private JCheckBox disAnglesChB;
	private JCheckBox ttcSymChB;

	public RandomGBsPanel() {

		pointGrp = PointGroup.M3M;

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df2 = new DecimalFormat("0.##", otherSymbols);
		df4 = new DecimalFormat("0.####", otherSymbols);


		setLayout(new MigLayout("", "[]", "[][][][][][][][][][][][][][][][][][][]"));

		JLabel setLbl = new JLabel("<html><b>Generate random grain boundaries (and compute additional values simultaneously):</b>");
		add(setLbl, "cell 0 0");
		
		final MisorListener listener = new MisorListener();
		
		randMisRb = new JRadioButton(RAND_MIS);
		randMisRb.setSelected(true);
		randMisRb.setActionCommand(RAND_MIS);
		randMisRb.addActionListener(listener);
		
		add(randMisRb, "cell 0 2,gapx 10,gapy 5");
		
		
		fixedMisRb = new JRadioButton(FIXED_MIS);
		fixedMisRb.setActionCommand(FIXED_MIS);
		fixedMisRb.addActionListener(listener);
		final ButtonGroup group = new ButtonGroup();
	    group.add(randMisRb);
		group.add(fixedMisRb);
		add(fixedMisRb, "flowx,cell 0 3,gapx 10");

		JLabel numberLbl = new JLabel("<html><u>Number of random boundaries</u>:");
		add(numberLbl, "flowx,cell 0 4,gapx 10,gapy 5");

		JLabel groupLbl = new JLabel("<html><u>Point group</u>:");
		groupLbl.setToolTipText("<html>By choosing a point group, the set of symmetry transformations is determined;<br>The same crystal lattice is assumed for both adjacent grains");
		add(groupLbl, "flowx,cell 0 1,gapx 10,gapy 5");

		numberFld = new JTextField();
		numberFld.setHorizontalAlignment(SwingConstants.RIGHT);
		numberFld.setText("5000");
		add(numberFld, "cell 0 4,gapy 5");
		numberFld.setColumns(8);

		JComboBox groupCb = new JComboBox();
		groupCb.setToolTipText("<html>By choosing a point group, the set of symmetry transformations is determined;<br>The same crystal lattice is assumed for both adjacent grains");
		groupCb.setModel(new DefaultComboBoxModel(new String[] {M3M, _6MMM, _4MMM, _3M, MMM, _2M, _1}));

		groupCb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JComboBox cb = (JComboBox) e.getSource();
				String sel = cb.getSelectedItem().toString();

				switch(sel) {
				case M3M:
					pointGrp = PointGroup.M3M; break;
				case _6MMM:
					pointGrp = PointGroup._6MMM; break;
				case _4MMM:
					pointGrp = PointGroup._4MMM; break;            		
				case MMM:
					pointGrp = PointGroup.MMM; break;
					
				case _3M:
					pointGrp = PointGroup._3M; break;					
				case _2M:
					pointGrp = PointGroup._2M; break;					
			
				case _1:
					pointGrp = PointGroup._1; break;					
			

				default: break;
				}

			}
		});
		add(groupCb, "cell 0 1,gapy 5");

		JLabel lblOutputFile = new JLabel("<html><u>Output <code>gbdat</code> file</u>:");
		add(lblOutputFile, "flowx,cell 0 5,gapx 10,gapy 5");

		JLabel valuesLbl = new JLabel("<html><u>Additional values to be computed and stored in the output file</u><font color=#0000cc> (*)</font>:\r\n");
		add(valuesLbl, "cell 0 6,gapx 10,gapy 5");

		JLabel lblDistances = new JLabel("<html><b>distances</b> to the nearest pure");
		lblDistances.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(lblDistances, "flowx,cell 0 9,gapx 20,aligny center");
		
		ttcChB = new JCheckBox("<html><b>approximate distances</b> to the nearest <b>tilt</b> and <b>twist</b> boundaries (based on 'tilt/twist component (TTC) parameters')");
		ttcChB.setSelected(true);
		add(ttcChB, "cell 0 7,gapx 20,gapy 5");
		
		ttcSymChB = new JCheckBox("<html><b>approximate distances</b> to the nearest <b>symmetric</b> and <b>180\u00B0-tilt</b> boundaries");
		ttcSymChB.setSelected(true);
		add(ttcSymChB, "cell 0 8,gapx 20");
		
		ttcDisChB = new JCheckBox("<html><b>TTC parameters</b> for <i>dis</i>orientations");
		add(ttcDisChB, "cell 0 11,gapx 20");
		
		disAnglesChB = new JCheckBox("<html><b>angles</b> of tilt and twist <b>components</b> for <i>dis</i>orientations");
		add(disAnglesChB, "cell 0 12,gapx 20");
		
		symmetryLbl = new JLabel("<html><u>Symmetries to be taken into consideration (if additional values are to be calculated)</u>:\r\n");
		add(symmetryLbl, "cell 0 13,gapx 10,gapy 5");
		
		BminChB = new JCheckBox("Inversion");
		BminChB.setEnabled(false);
		BminChB.setSelected(true);
		add(BminChB, "flowx,cell 0 14,gapx 20");

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 15,growx,aligny center,gapy 5 5");

		processBtn = new JButton("Generate & compute");
		add(processBtn, "flowx,cell 0 16,alignx left,aligny center,gapy 5");


		processBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				progressBar.setValue(0);

				final int nGB;
				try {
					nGB = Integer.parseInt(numberFld.getText());
					if(nGB < 1) throw new NumberFormatException();
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"Number of boundaries should be a positive integer.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					task = new RandomGBsProcessTask(nGB);
				} catch (IOException exc) {
					JOptionPane.showMessageDialog(null,
							"Please check the output file path.",
							"I/O Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} catch (NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"Euler angles should be decimal numbers.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
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

		progressBar = new JProgressBar();
		add(progressBar, "cell 0 16,gapx 20,aligny center,gapy 5");
		progressBar.setStringPainted(true);

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
				} else { 			  

					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);

					if(ext != null) {
						if(ext.compareTo("gbdat") != 0) fName += ".gbdat";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "gbdat";
						else fName += ".gbdat";
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
		};*/

		outFld = new JTextField();
		add(outFld, "cell 0 5,gapy 5");
		outFld.setColumns(18);

		JButton outBtn = new JButton();
		outBtn.setIcon(new ImageIcon(RandomGBsPanel.class.getResource("/gui_bricks/folder.png")));
		outBtn.setPreferredSize(new Dimension(24, 24));
		outBtn.setMinimumSize(new Dimension(24, 24));
		outBtn.setMaximumSize(new Dimension(24, 24));
		add(outBtn, "cell 0 5,gapy 5");

		outBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				outputFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				outputFc.setAcceptAllFileFilterUsed(false);
				outputFc.addChoosableFileFilter(randgbFilter);

				int returnVal = outputFc.showSaveDialog(RandomGBsPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);

					if(ext != null) {
						if(ext.compareTo("gbdat") != 0) fName += ".gbdat";
					} else {							
						//if(fName.charAt(fName.length() - 1) == '.') fName += "gbdat";
						//else
						fName += ".gbdat";
					}
					outFld.setText(fName);
				}	
				outputFc.removeChoosableFileFilter(randgbFilter);
				outputFc.setAcceptAllFileFilterUsed(true);
			}
		});

		calcTiltChB = new JCheckBox("<html><b>tilt</b>");
		calcTiltChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcTiltChB, "cell 0 9,aligny center,gapy 5");

		calcTwistChB = new JCheckBox("<html><b>twist</b>");
		calcTwistChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcTwistChB, "cell 0 9,aligny center,gapy 5");

		decompChB = new JCheckBox("<html><b>angles</b> of tilt and twist <b>components</b> (Fortes decompopsition)");
		add(decompChB, "flowx,cell 0 10,gapx 20");

		calcSymChB = new JCheckBox("<html><b>symmetric</b>");
		calcSymChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcSymChB, "cell 0 9,aligny center,gapy 5");

		calcImpropChB = new JCheckBox("<html><b>180\u00B0-tilt</b>");
		calcImpropChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(calcImpropChB, "cell 0 9,aligny center,gapy 5");

		JLabel gbsLbl = new JLabel("boundaries");
		add(gbsLbl, "cell 0 9,gapy 5");

		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setEnabled(false);
		abortBtn.setPreferredSize(new Dimension(24, 24));
		abortBtn.setMinimumSize(new Dimension(24, 24));
		abortBtn.setMaximumSize(new Dimension(24, 24));
		abortBtn.setIcon(new ImageIcon(RandomGBsPanel.class.getResource("/gui_bricks/abort.png")));
		add(abortBtn, "cell 0 16,gapx 20,aligny center,gapy 5");
		
		overwrittenLbl = new JLabel("<html><font color=#cc0000><small><b>Warning: existing files will be overwritten!</b></small></font>");
		add(overwrittenLbl, "cell 0 17,gapy 20");
		
		lblyouCan = new JLabel("<html><font color=#0000cc>* <small><b>Note:</b> if you do not want to compute any of these values now, you can add them to the data file in the future<br>using the module for reprocessing boundary data files.</font></small>");
		add(lblyouCan, "cell 0 18,gapy 10");
		
		BTChB = new JCheckBox("Grain order interchange");
		BTChB.setSelected(true);
		add(BTChB, "cell 0 14");
		
		phi1Lbl = new JLabel("<html>\u03c6<sub>1</sub> =");
		phi1Lbl.setEnabled(false);
		add(phi1Lbl, "cell 0 3,gapx 10");
		
		phi1Fld = new JTextField();
		phi1Fld.setEnabled(false);
		phi1Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(phi1Fld, "cell 0 3");
		phi1Fld.setColumns(8);
		
		PhiLbl = new JLabel("<html>\u03a6 =");
		PhiLbl.setEnabled(false);
		add(PhiLbl, "cell 0 3,gapx 10");
		
		PhiFld = new JTextField();
		PhiFld.setEnabled(false);
		PhiFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(PhiFld, "cell 0 3");
		PhiFld.setColumns(8);
		
		phi2Lbl = new JLabel("<html>\u03c6<sub>2</sub> =");
		phi2Lbl.setEnabled(false);
		add(phi2Lbl, "cell 0 3,gapx 10");
		
		phi2Fld = new JTextField();
		phi2Fld.setEnabled(false);
		phi2Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(phi2Fld, "cell 0 3");
		phi2Fld.setColumns(8);
		
		lblNewLabel_3 = new JLabel("[\u00b0]");
		add(lblNewLabel_3, "cell 0 3,gapx 20");
		
		sigmaBtn = new JButton("");
		sigmaBtn.setToolTipText("Use a predefined misorientation");
		sigmaBtn.setEnabled(false);
		sigmaBtn.setIcon(new ImageIcon(RandomGBsPanel.class.getResource("/gui_bricks/sum-icon.png")));
		add(sigmaBtn, "cell 0 3,gapx 20");
		sigmaBtn.setMaximumSize(new Dimension(24,24));
		sigmaBtn.setMinimumSize(new Dimension(24,24));
		sigmaBtn.setPreferredSize(new Dimension(24,24));
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				task.cancel(true);
			}
		});

		
		sigmaBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
			
				Object[] possibilities = null;

				if(pointGrp == PointGroup.M3M) {

					possibilities = new Object[]{S3,S5,S7,S9,S11,S13a,S13b,S15,S17a,S17b,S19a,S19b,S21a,S21b,S23,S25a,S25b,S27a,S27b,S29a,S29b,S31a,S31b,S39b		};	


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
						
						default: break;
					}

					final AxisAngle aa = new AxisAngle();
					aa.set(n, w);
					
					final EulerAngles eul = new EulerAngles();
					eul.set(aa);

					DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US); 
					DecimalFormat df = new DecimalFormat("0.####", otherSymbols);			
					
					phi1Fld.setText(df.format(Math.toDegrees(eul.phi1())));
					PhiFld.setText(df.format(Math.toDegrees(eul.Phi())));
					phi2Fld.setText(df.format(Math.toDegrees(eul.phi2())));
				

				}
			}
		});
		
		ttcDisChB.addActionListener(new ActionListener() { //TODO

			@Override
			public void actionPerformed(ActionEvent evt) {
				 JCheckBox src = (JCheckBox) evt.getSource();
				 
				 if(src.isSelected()) {
					 if(!BTChB.isSelected()) {
						  src.setSelected(false);
						 JOptionPane.showMessageDialog(null,
									"Grain order interchange symmetry must be included.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
						 
						
						 return;
					 }
					 
					 if(fixedMisorientation) {
						  src.setSelected(false);
						 JOptionPane.showMessageDialog(null,
									"This option makes (almost) no sense for a fixed misorientation.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
						 
						
						 return;
					 }
					 
					 if(pointGrp != PointGroup.M3M &&
						pointGrp != PointGroup._6MMM &&
						pointGrp != PointGroup._4MMM &&
						pointGrp != PointGroup.MMM) {
						 
						  src.setSelected(false);
							 JOptionPane.showMessageDialog(null,
										"This option does not work for the selected point group.",
										"Warning",
										JOptionPane.WARNING_MESSAGE);
							 
							
							 return;
					 }
				 }
				 
			}
			
		});
	}

	
	private final class RandomGBsProcessTask extends SwingWorker<Void, Void> {

		private PrintWriter wrt = null;
		private int nGB;
		private final Matrix3x3[] setC;
		
		private static final double ONEPI = Math.PI;
		private final MitchellMooreGenerator generator = new MitchellMooreGenerator();
		
		private List futuresList;
		
		private Matrix3x3 fixM = new Matrix3x3();

		
		public RandomGBsProcessTask(int nGB) throws IOException, NumberFormatException {

			setProgress(0);
			progressBar.setValue(0);
			
			this.nGB = nGB;
			
			
			if(fixedMisorientation) {
				EulerAngles angles = new EulerAngles();
			
				String str = new String(phi1Fld.getText().replace(",", "."));		
				double phi1 = Double.parseDouble(str);
			
				str = new String(PhiFld.getText().replace(",", "."));		
				double Phi = Double.parseDouble(str);
			
				str = new String(phi2Fld.getText().replace(",", "."));		
				double phi2 = Double.parseDouble(str);
			
				angles.set(Math.toRadians(phi1), Math.toRadians(Phi), Math.toRadians(phi2));
			
				fixM.set(angles);
			}
			
			
			wrt = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));
			
		    wrt.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
			wrt.println("# it contains grain boundary parameters and corresponding additional values");
			wrt.println("RANDOM");

			switch(pointGrp) {
			case M3M: wrt.println("m-3m"); break;
			case _6MMM: wrt.println("6/mmm"); break;
			case _4MMM: wrt.println("4/mmm"); break;
			case MMM: wrt.println("mmm"); break;
			case _3M: wrt.println("-3m"); break;
			case _2M: wrt.println("2/m"); break;
			case _1: wrt.println("-1"); break;
		
			default: throw new IOException("Unknown point group");
			}

			String fields = "L_PHI1 L_PHI L_PHI2 R_PHI1 R_PHI R_PHI2 ZENITH AZIMUTH ";
			if(calcTiltChB.isSelected()) fields += "DIST_TILT ";
			if(calcTwistChB.isSelected()) fields += "DIST_TWIST ";
			if(calcSymChB.isSelected()) fields += "DIST_SYM ";
			if(calcImpropChB.isSelected()) fields += "DIST_180-TILT ";
			if(decompChB.isSelected()) fields += "F_TILT_ANGLE F_TWIST_ANGLE ";
			
			if(ttcChB.isSelected()) fields += "APPROX_D_TWIST APPROX_D_TILT ";
			
			if(ttcSymChB.isSelected()) fields += "APPROX_D_SYM APPROX_D_180-TILT ";
			
			if(ttcDisChB.isSelected()) fields += "DISOR_TTC ";
			if(disAnglesChB.isSelected()) fields += "DISOR_TILT_A DISOR_TWIST_A "; 
			
			wrt.println(fields);

			setC = Transformations.getSymmetryTransformations(pointGrp);
			
			
		}

		@Override
		public Void doInBackground() {

			abortBtn.setEnabled(true);
			processBtn.setEnabled(false);
			
			long startTime = System.currentTimeMillis();
			
			int nThreads = Runtime.getRuntime().availableProcessors();
			final ExecutorService eservice = Executors.newFixedThreadPool(nThreads);
		    final CompletionService < Object > cservice = new ExecutorCompletionService < Object > (eservice);		    		
		    	
		    futuresList = new ArrayList();
		    
			for(int index = 0; index < nGB; index++) {
				
				final double phi1L = 2d * ONEPI * generator.nextDouble();
				final double phi2L = 2d * ONEPI * generator.nextDouble();
				final double PhiL = MyMath.acos(2d * generator.nextDouble() - 1d);
				
				double phi1R;
				double phi2R;
				double PhiR;
				
				if(fixedMisorientation) {
					
					final Matrix3x3 R = new Matrix3x3();
					final EulerAngles Eul = new EulerAngles();
					
					Eul.set(phi1L, PhiL, phi2L);
					R.set(Eul);
					
					R.leftMulTransposed(fixM);
					Eul.set(R);
					
					phi1R = Eul.phi1();
					PhiR = Eul.Phi();
					phi2R = Eul.phi2();
					
					
					
				} else {									
					phi1R = 2d * ONEPI * generator.nextDouble();
					phi2R = 2d * ONEPI * generator.nextDouble();
					PhiR = MyMath.acos(2d * generator.nextDouble() - 1d);
				}
				
				final double azimuth = 2d * ONEPI * generator.nextDouble();
				final double zenith = MyMath.acos(2d*generator.nextDouble() - 1d);
				
				final UnitVector m1 = new UnitVector();
				m1.set(zenith, azimuth);
				
				//TODO
				futuresList.add(cservice.submit(new TaskTestRandomGB(BTChB.isSelected(),BminChB.isSelected(),phi1L, PhiL, phi2L, phi1R, PhiR, phi2R, m1.zenith(), m1.azimuth(),
						calcTiltChB.isSelected(), calcTwistChB.isSelected(), calcSymChB.isSelected(), calcImpropChB.isSelected(), 
						decompChB.isSelected(), ttcChB.isSelected(), ttcSymChB.isSelected(), ttcDisChB.isSelected(), disAnglesChB.isSelected(),
						 pointGrp,
						setC) ) );				
			}
			
			int index = 0;
			while(index < nGB && !isCancelled()) {
						
				try {
					final TaskResultRandomGB taskResult = (TaskResultRandomGB) cservice.take().get();
			
					final StringBuilder line = new StringBuilder();
					
			
					line.append(df4.format(Math.toDegrees(taskResult.phi1L)));
					line.append(' ');
					line.append(df4.format(Math.toDegrees(taskResult.PhiL)));
					line.append(' ');
					line.append(df4.format(Math.toDegrees(taskResult.phi2L)));
					line.append(' ');
					line.append(df4.format(Math.toDegrees(taskResult.phi1R)));
					line.append(' ');
					line.append(df4.format(Math.toDegrees(taskResult.PhiR)));
					line.append(' ');
					line.append(df4.format(Math.toDegrees(taskResult.phi2R)));
					line.append(' ');
					line.append(df4.format(Math.toDegrees(taskResult.zenith)));
					line.append(' ');
					line.append(df4.format(Math.toDegrees(taskResult.azimuth)));
					line.append(' ');

					if(calcTiltChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(Math.sqrt(taskResult.tiltDist)) ));
						line.append(' ');
					}
					if(calcTwistChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(Math.sqrt(taskResult.twistDist)) ));
						line.append(' ');
					}
					if(calcSymChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(Math.sqrt(taskResult.symDist)) ));
						line.append(' ');
					}
					if(calcImpropChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(Math.sqrt(taskResult.impropDist)) ));
						line.append(' ');
					}
					if(decompChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(taskResult.tiltAngle)) );
						line.append(' ');
						line.append(df2.format( Math.toDegrees(taskResult.twistAngle)) );
						line.append(' ');
					}
					if(ttcChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(taskResult.minTtc)) );
						line.append(' ');
						line.append(df2.format( 90d - Math.toDegrees(taskResult.maxTtc)) );
						line.append(' ');
					}
					
					if(ttcSymChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(Math.sqrt(taskResult.symTtc))) );
						line.append(' ');
						line.append(df2.format( Math.toDegrees(Math.sqrt(taskResult.impropTtc))) );
						line.append(' ');
					}
					
					if(ttcDisChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(taskResult.disTtc)) );
						line.append(' ');
					}
					
					if(disAnglesChB.isSelected()) {
						line.append(df2.format( Math.toDegrees(taskResult.disTiltAngle)) );
						line.append(' ');
						line.append(df2.format( Math.toDegrees(taskResult.disTwistAngle)) );
						//line.append(' ');
					}

					wrt.println(line);

					index++;
					setProgress((int)Math.round((double)index/(double)nGB*100d));				
				
				} catch (InterruptedException | ExecutionException exc) { 
					return null;
				}
			}	
			wrt.close();
			long estimatedTime = System.currentTimeMillis() - startTime;
			
			System.out.println("Time elapsed: " + estimatedTime + " microsec.");
					
			return null;
		}

		@Override
		public void done() {		
			
			if(isCancelled()) if(futuresList != null) for(Object future : futuresList) ((Future) future).cancel(true);
			
			processBtn.setEnabled(true);
			abortBtn.setEnabled(false);
			
		}
	}
	
	private class MisorListener implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent evt) {
		    String s = evt.getActionCommand().toString();		                            
                        
            switch(s) {
            case RAND_MIS:  
            	sigmaBtn.setEnabled(false);
            	fixedMisorientation = false;
            	phi1Lbl.setEnabled(false);
            	phi1Fld.setEnabled(false);
            	PhiLbl.setEnabled(false);
            	PhiFld.setEnabled(false);
            	phi2Lbl.setEnabled(false);
            	phi2Fld.setEnabled(false);
            	
            	break;
            case FIXED_MIS:
            	sigmaBtn.setEnabled(true);
            	fixedMisorientation = true;
            	phi1Lbl.setEnabled(true);
            	phi1Fld.setEnabled(true);
            	PhiLbl.setEnabled(true);
            	PhiFld.setEnabled(true);
            	phi2Lbl.setEnabled(true);
            	phi2Fld.setEnabled(true);
            	break;
            	
            default: break;
            }   
		}
	}
}
