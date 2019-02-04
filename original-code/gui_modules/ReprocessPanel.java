package gui_modules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import enums.PointGroup;

import net.miginfocom.swing.MigLayout;
import parallel.TaskResultReprocess;
import parallel.TaskTestAgain;
import utils.ConstantsAndStatic;
import utils.FileUtils;
import utils.GBDatHeader;
import utils.Matrix3x3;
import utils.Transformations;
import utils.UnitVector;

public class ReprocessPanel extends JPanel {
	
	private JTextField inFld;
	private JTextField outFld;

	private FileUtils.GBDatFileFilter gbdatFilter = new FileUtils.GBDatFileFilter();

	private JCheckBox tiltChB;
	private JCheckBox twistChB;
	private JCheckBox symChB;
	private JCheckBox impropChB;
	private JCheckBox anglesChB;

	private JButton abortBtn;
	private JButton fireBtn;

	private final DecimalFormat df2;
	private final DecimalFormat df4;
	private final DecimalFormat df7;

	private ReprocessTask task;

	private JProgressBar progressBar;
	private JLabel lblNewLabel;
	private JLabel lblTypeOfData;
	private JLabel typeLbl;
	private JLabel symmetryLbl;
	private JCheckBox BminChB;
	private JCheckBox BTChB;
	private JCheckBox ttcChB;
	private JCheckBox ttcDisChB;
	
	private PointGroup pointGrp = PointGroup.M3M;
	private JCheckBox disAnglesChB;
	private JCheckBox ttcSymChB;


	public ReprocessPanel() {

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df2 = new DecimalFormat("0.##", otherSymbols);
		df4 = new DecimalFormat("0.####", otherSymbols);
		df7 = new DecimalFormat("0.#######", otherSymbols);

		setLayout(new MigLayout("", "[]", "[][][][][][][][][][][][][][][][]"));

		JLabel lblHeader = new JLabel("<html><b>Characterize boundaries stored in <code>gbdat</code> files:</b>");
		add(lblHeader, "cell 0 0");

		JLabel lblInput = new JLabel("<html><u><code>gbdat</code> file to be reprocessed</u>:");
		add(lblInput, "flowx,cell 0 1,gapx 10,gapy 5");
		
		lblTypeOfData = new JLabel("<html><small>Type of data:<small>");
		add(lblTypeOfData, "flowx,cell 0 2,gapx 20,gapy 5");

		JLabel lblOutput = new JLabel("<html><u>Output <code>gbdat</code> file</u>:");
		add(lblOutput, "flowx,cell 0 3,gapx 10,gapy 5");

		JLabel lblValues = new JLabel("<html><u>Values to be calculated and added to the data file</u>:");
		add(lblValues, "flowx,cell 0 4,gapx 10,gapy 5");

		inFld = new JTextField();
		add(inFld, "cell 0 1,gapy 5");
		inFld.setColumns(18);


		inFld.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				refresh();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				refresh();
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				refresh();
			}


			private final void refresh() {
				File f = new File(inFld.getText());

				if(f.exists() && !f.isDirectory() && FileUtils.getExtension(f).compareTo("gbdat") == 0) {
					try {
						final GBDatHeader head = new GBDatHeader(f);

						String type = "";
						if(head.isExperimental()) type += "<html><b>Experimental</b> ";
						else type += "<html><small><b>Random</b> "; //TODO
						
						pointGrp = head.getPointGrp();
						
						switch(pointGrp) {
						case M3M: type += ", <i>m3\u0305m</i>&nbsp;</small>"; break;
						case _6MMM: type += ", <i>6/mmm</i>&nbsp;</small>"; break;
						case _4MMM: type += ", <i>4/mmm</i>&nbsp;</small>"; break;
						case MMM: type += ", <i>mmm</i>&nbsp;</small>"; break;
						case _3M: type += ", <i>3\u0305m</i>&nbsp;</small>"; break;
						case _2M: type += ", <i>2/m</i>&nbsp;</small>"; break;
						case _1: type += ", <i>1\u0305</i>&nbsp;</small>"; break;
						default: throw new IOException("Unknown point group");
						}
						
						typeLbl.setText(type);
						
					/*	tiltChB.setSelected(!head.containsTiltDist());
						twistChB.setSelected(!head.containsTwistDist());
						symChB.setSelected(!head.containsSymDist());
						impropChB.setSelected(!head.containsImpropDist());
						anglesChB.setSelected(!head.containsTiltAngle());*/
						
						tiltChB.setSelected(false);
						twistChB.setSelected(false);
						symChB.setSelected(false);
						impropChB.setSelected(false);
						anglesChB.setSelected(false);
						
						ttcChB.setSelected(!head.containsMinTTC());
						
						ttcSymChB.setSelected(!head.containsSymTTC());
						
						
						//if(pointGrp != PointGroup._3M && pointGrp != PointGroup._2M) ttcDisChB.setSelected(!head.containsDisorTTC());
						//else
						ttcDisChB.setSelected(false);
						
						//if(pointGrp != PointGroup._3M && pointGrp != PointGroup._2M) disAnglesChB.setSelected(!head.containsDisTiltAngle());
						//else
						disAnglesChB.setSelected(false);

						tiltChB.setEnabled(!head.containsTiltDist());
						twistChB.setEnabled(!head.containsTwistDist());
						symChB.setEnabled(!head.containsSymDist());
						impropChB.setEnabled(!head.containsImpropDist());
						anglesChB.setEnabled(!head.containsTiltAngle());
						
						ttcChB.setEnabled(!head.containsMinTTC());
						ttcSymChB.setEnabled(!head.containsSymTTC());

						if(pointGrp != PointGroup._3M && pointGrp != PointGroup._2M && pointGrp != PointGroup._1) ttcDisChB.setEnabled(!head.containsDisorTTC());
						else ttcDisChB.setEnabled(false);
						
						if(pointGrp != PointGroup._3M && pointGrp != PointGroup._2M && pointGrp != PointGroup._1) disAnglesChB.setEnabled(!head.containsDisTiltAngle());
						else disAnglesChB.setEnabled(false);

						if(!head.containsTiltDist() ||
								!head.containsTwistDist() ||
								!head.containsSymDist() ||
								!head.containsImpropDist() ||
								!head.containsTiltAngle() || !head.containsMinTTC() || !head.containsSymTTC() ||
								(!head.containsDisorTTC() && pointGrp != PointGroup._3M && pointGrp != PointGroup._2M && pointGrp != PointGroup._1) ||
								(!head.containsDisTiltAngle() && pointGrp != PointGroup._3M && pointGrp != PointGroup._2M && pointGrp != PointGroup._1) ) {
							fireBtn.setEnabled(true);
						}
						return;

					} catch (NumberFormatException | IOException exc) {
						// Nothing is necessary here I guess
						
					}
				}

				tiltChB.setSelected(false);
				twistChB.setSelected(false);
				symChB.setSelected(false);
				impropChB.setSelected(false);
				anglesChB.setSelected(false);
				
				ttcChB.setSelected(false);
				ttcDisChB.setSelected(false);
				ttcSymChB.setSelected(false);
				disAnglesChB.setSelected(false);
				
				tiltChB.setEnabled(false);
				twistChB.setEnabled(false);
				symChB.setEnabled(false);
				impropChB.setEnabled(false);
				anglesChB.setEnabled(false);
				fireBtn.setEnabled(false);
				
				ttcChB.setEnabled(false);
				ttcSymChB.setEnabled(false);
				ttcDisChB.setEnabled(false);
				
				disAnglesChB.setEnabled(false);
				
				typeLbl.setText("");
			}
		});



		final JFileChooser inputFc = new JFileChooser(); 
		inputFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		inputFc.setAcceptAllFileFilterUsed(false);
		inputFc.addChoosableFileFilter(new FileUtils.GBDatFileFilter());
		inputFc.setMultiSelectionEnabled(true);

		JButton inBtn = new JButton();
		inBtn.setPreferredSize(new Dimension(24,24));
		inBtn.setMinimumSize(new Dimension(24,24));
		inBtn.setMaximumSize(new Dimension(24,24));

		inBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int returnVal = inputFc.showOpenDialog(ReprocessPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = inputFc.getSelectedFile();
					inFld.setText(f.getAbsolutePath());
				}
			}
		});

		inBtn.setIcon(new ImageIcon(ReprocessPanel.class.getResource("/gui_bricks/folder.png")));
		add(inBtn, "cell 0 1,gapy 5");

		outFld = new JTextField();
		add(outFld, "cell 0 3,gapy 5");
		outFld.setColumns(18);

		JButton outBtn = new JButton();
		outBtn.setPreferredSize(new Dimension(24,24));
		outBtn.setMinimumSize(new Dimension(24,24));
		outBtn.setMaximumSize(new Dimension(24,24));
		outBtn.setIcon(new ImageIcon(ReprocessPanel.class.getResource("/gui_bricks/folder.png")));
		add(outBtn, "cell 0 3,gapy 5");

		final JFileChooser outputFc = new JFileChooser(); /* {
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
		
		outputFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		outputFc.setAcceptAllFileFilterUsed(false);

		outputFc.addChoosableFileFilter(gbdatFilter);
		
		outBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

			

				int returnVal = outputFc.showSaveDialog(ReprocessPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);

					if(ext != null) {
						if(ext.compareTo("gbdat") != 0) fName += ".gbdat";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "gbdat";
						else fName += ".gbdat";
					}

					outFld.setText(fName);
				}	
			//	outputFc.removeChoosableFileFilter(gbdatFilter);
			//	outputFc.setAcceptAllFileFilterUsed(true);
			}			
		});



		JLabel lblDistancesToNearest = new JLabel("<html><b>distances</b> to the nearest pure");
		lblDistancesToNearest.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		add(lblDistancesToNearest, "flowx,cell 0 7,gapx 20");

		tiltChB = new JCheckBox("<html><b>tilt</b>");
		tiltChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		tiltChB.setEnabled(false);
		add(tiltChB, "cell 0 7,gapy 5");

		twistChB = new JCheckBox("<html><b>twist</b>");
		twistChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		twistChB.setEnabled(false);
		add(twistChB, "cell 0 7,gapy 5");

		symChB = new JCheckBox("<html><b>symmetric</b>");
		symChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		symChB.setEnabled(false);
		add(symChB, "cell 0 7,gapy 5");

		impropChB = new JCheckBox("<html><b>180\u00B0-tilt</b>");
		impropChB.setToolTipText("<html><font color=#cc0000>Warning: computation of distances may take long times.</font>");
		impropChB.setEnabled(false);
		add(impropChB, "cell 0 7,gapy 5");
		
		ttcSymChB = new JCheckBox("<html><b>approximate distances</b> to the nearest <b>symmetric</b> and <b>180\u00B0-tilt</b> boundaries");
		ttcSymChB.setEnabled(false);
		add(ttcSymChB, "cell 0 6,gapx 20");

		anglesChB = new JCheckBox("<html><b>angles</b> of tilt and twist <b>components</b> (the decomposition method)");
		anglesChB.setEnabled(false);
		add(anglesChB, "cell 0 8,gapx 20");

		JLabel lblGBs = new JLabel("boundaries");
		add(lblGBs, "cell 0 7,gapy 5");
		
		ttcChB = new JCheckBox("<html><b>approximate distances</b> to the nearest <b>tilt</b> and <b>twist</b> boundaries (based on 'tilt/twist component (TTC) parameters')");
		ttcChB.setEnabled(false);
		add(ttcChB, "cell 0 5,gapx 20,gapy 5");
		
		ttcDisChB = new JCheckBox("<html><b>TTC parameters</b> for <i>dis</i>orientations");
		ttcDisChB.setEnabled(false);
		add(ttcDisChB, "cell 0 9,gapx 20");
		
		disAnglesChB = new JCheckBox("<html><b>angles</b> of tilt and twist <b>components</b> for <i>dis</i>orientations");
		disAnglesChB.setEnabled(false);
		add(disAnglesChB, "cell 0 10,gapx 20");
		
		symmetryLbl = new JLabel("<html><u>Symmetries to be taken into account</u>:");
		add(symmetryLbl, "cell 0 11,gapx 10,gapy 5");
		
		BminChB = new JCheckBox("Inversion");
		BminChB.setEnabled(false);
		BminChB.setSelected(true);
		add(BminChB, "flowx,cell 0 12,gapx 20");

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 13,growx,aligny center,gapy 5 5");


		fireBtn = new JButton("Reprocess");

		fireBtn.setEnabled(false);
		add(fireBtn, "flowx,cell 0 14,aligny center");

		fireBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				try {
					task = new ReprocessTask();
				} catch (IOException exc) {
					JOptionPane.showMessageDialog(null,
							"An I/O error occurred.",
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
		add(progressBar, "cell 0 14,gapx 20,aligny center");

		progressBar.setStringPainted(true);

		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setEnabled(false);
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(ReprocessPanel.class.getResource("/gui_bricks/abort.png")));
		add(abortBtn, "cell 0 14,gapx 20,aligny center");
		
		lblNewLabel = new JLabel("<html><font color=cc0000><small><b>Warning: existing files will be overwritten!</small></font></b>");
		add(lblNewLabel, "cell 0 15,gapy 20");
		
		typeLbl = new JLabel(" ");
		add(typeLbl, "cell 0 2,gapy 5");
		
		BTChB = new JCheckBox("Grain order interchange");
		BTChB.setSelected(true);
		add(BTChB, "cell 0 12");
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				task.cancel(true);				
			}			
		});
		
		ttcDisChB.addActionListener(new ActionListener() { //TODO

			@Override
			public void actionPerformed(ActionEvent evt) {
				 JCheckBox src = (JCheckBox) evt.getSource();
				 
				 if(src.isSelected()) {
					/* if(!BTChB.isSelected()) {
						  src.setSelected(false);
						 JOptionPane.showMessageDialog(null,
									"Grain order interchange symmetry must be included.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
						 
						
						 return;
					 }*/
					 
					
					 
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
		
		
		disAnglesChB.addActionListener(new ActionListener() { //TODO

			@Override
			public void actionPerformed(ActionEvent evt) {
				 JCheckBox src = (JCheckBox) evt.getSource();
				 
				 if(src.isSelected()) {
					/* if(!BTChB.isSelected()) {
						  src.setSelected(false);
						 JOptionPane.showMessageDialog(null,
									"Grain order interchange symmetry must be included.",
									"Warning",
									JOptionPane.WARNING_MESSAGE);
						 
						
						 return;
					 }*/
					 
					
					 
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

	
	private final class ReprocessTask extends SwingWorker<Void, Void> {

		private static final double INFTY = Double.MAX_VALUE;
		private final PrintWriter out;
		private final BufferedReader in;
		private final GBDatHeader header;
		
		private final Matrix3x3[] setC;
		
		private List futuresList;

		public ReprocessTask() throws IOException {					
			setProgress(0);
			progressBar.setValue(0);
			out = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));
			header = new GBDatHeader(new File(inFld.getText()));
			in = new BufferedReader(new FileReader(header.getPath()));
			setC = Transformations.getSymmetryTransformations(header.getPointGrp());
		}

		@Override
		public Void doInBackground() throws IOException {
				
			abortBtn.setEnabled(true);
			fireBtn.setEnabled(false);
			
			long startTime = System.currentTimeMillis();

		/*	tester.setSymmetryTransformations(Transformations.getSymmetryTransformations(header.getPointGrp()));
			tester.useMinimization( tiltChB.isSelected(),
					twistChB.isSelected(),
					symChB.isSelected(),
					impropChB.isSelected());
			tester.useDecomp(anglesChB.isSelected());
*/
		    out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
			out.println("# it contains grain boundary parameters and corresponding additional values");
	
			//out.println("# This file was generated by GBToolbox");
	//		out.println("# It contains grain boundary parameters and corresponding additional values");		
			if(header.isExperimental()) out.println("EXP"); else out.println("RANDOM");

			switch(header.getPointGrp()) {
			case M3M: out.println("m-3m"); break;
			case _6MMM: out.println("6/mmm"); break;
			case _4MMM: out.println("4/mmm"); break;
			case MMM: out.println("mmm"); break;
			case _3M: out.println("-3m"); break;
			case _2M: out.println("2/m"); break;
			case _1: out.println("-1"); break;
			default: throw new IOException("Unknown point group");
			}

			String fields = "L_PHI1 L_PHI L_PHI2 R_PHI1 R_PHI R_PHI2 ZENITH AZIMUTH ";

			if(header.isExperimental()) fields += "CORRELAT AREA ";
			if(header.containsTiltDist() || tiltChB.isSelected()) fields += "DIST_TILT ";
			if(header.containsTwistDist() || twistChB.isSelected()) fields += "DIST_TWIST ";
			if(header.containsSymDist() || symChB.isSelected()) fields += "DIST_SYM ";
			if(header.containsImpropDist() || impropChB.isSelected()) fields += "DIST_180-TILT ";
			if(header.containsTiltAngle() || anglesChB.isSelected()) fields += "F_TILT_ANGLE F_TWIST_ANGLE ";
			
			if(header.containsMinTTC() || ttcChB.isSelected()) fields += "APPROX_D_TWIST APPROX_D_TILT "; 
			if(header.containsSymTTC() || ttcSymChB.isSelected()) fields += "APPROX_D_SYM APPROX_D_180-TILT ";
			
			
			if(header.containsDisorTTC() || ttcDisChB.isSelected()) fields += "DISOR_TTC "; 
			
			if(header.containsDisTiltAngle() || disAnglesChB.isSelected()) fields += "DISOR_TILT_A DISOR_TWIST_A ";
			
			out.println(fields);
						
			int nThreads = Runtime.getRuntime().availableProcessors();
			System.out.println("nThreads = " + nThreads);
			final ExecutorService eservice = Executors.newFixedThreadPool(nThreads);
		    final CompletionService < Object > cservice = new ExecutorCompletionService < Object > (eservice);		    		
		
		    futuresList = new ArrayList();

			GBDatHeader.skipHeaderLines(in); 

			String line = null;

			while ((line = in.readLine()) != null && !isCancelled()) {

				final String[] num = line.trim().split("\\s+");

				final double phi1L = Math.toRadians(Double.parseDouble(num[0]));
				final double PhiL = Math.toRadians(Double.parseDouble(num[1]));
				final double phi2L = Math.toRadians(Double.parseDouble(num[2]));
				
				final double phi1R = Math.toRadians(Double.parseDouble(num[3]));
				final double PhiR = Math.toRadians(Double.parseDouble(num[4]));
				final double phi2R = Math.toRadians(Double.parseDouble(num[5]));

				final double znth = Math.toRadians(Double.parseDouble(num[6]));
				final double azmth = Math.toRadians(Double.parseDouble(num[7]));
				
				double area = 0d;
				
				int correlat = 0;
				
				double tiltDist = INFTY;
				double twistDist = INFTY;
				double symDist = INFTY;
				double impropDist = INFTY;
				double tiltAngle = INFTY;
				double twistAngle = INFTY;
				
				double minTtc = INFTY;
				double maxTtc = 0d;
				
				double symTtc = INFTY;
				double impropTtc = INFTY;
				
				double disTtc = INFTY;
				
				double disTiltAngle = INFTY;
				double disTwistAngle = INFTY;
				
				int pos = 8;
				
				if(header.isExperimental()) {
					correlat = Integer.parseInt(num[pos]);
					pos++;
					area = Double.parseDouble(num[pos]);
					pos++; 			
				}

				if(header.containsTiltDist()) {
					tiltDist =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				}
				
				if(header.containsTwistDist()) {
					twistDist =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				} 
				
				if(header.containsSymDist()) {
					symDist =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				} 
				
				if(header.containsImpropDist()) {
					impropDist =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				} 

				if(header.containsTiltAngle()) {
					tiltAngle =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				}
				
				if(header.containsTwistAngle()) {
					twistAngle =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				} 
				
				if(header.containsMinTTC()) {
					minTtc =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				}
				
				if(header.containsMaxTTC()) {
					maxTtc =  Math.toRadians(90d - Double.parseDouble(num[pos]));
					pos++;
				} 
				
				
				if(header.containsSymTTC()) {
					symTtc =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				}
				
				if(header.containsImpropTTC()) {
					impropTtc =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				}
				
				
				if(header.containsDisorTTC()) {
					disTtc =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				} 
				
				if(header.containsDisTiltAngle()) {
					disTiltAngle =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				}
				
				if(header.containsDisTwistAngle()) {
					disTwistAngle =  Math.toRadians(Double.parseDouble(num[pos]));
					pos++;
				} 
					
				final UnitVector m1 = new UnitVector();
				m1.set(znth, azmth);
				
				futuresList.add(cservice.submit(new TaskTestAgain(BTChB.isSelected(), BminChB.isSelected(), phi1L, PhiL, phi2L, phi1R, PhiR, phi2R , m1.zenith(), m1.azimuth(), correlat, area,
						tiltDist, twistDist, symDist, impropDist, tiltAngle, twistAngle, minTtc, maxTtc,
						symTtc, impropTtc,
						disTtc, disTiltAngle, disTwistAngle,
						tiltChB.isSelected(), twistChB.isSelected(), symChB.isSelected(), impropChB.isSelected(), 
						anglesChB.isSelected(), ttcChB.isSelected(), 
						ttcSymChB.isSelected(),
						ttcDisChB.isSelected(), disAnglesChB.isSelected(), header.getPointGrp(),
						setC) ) );						

			}

			int index = 0;
			while(index < header.getNumberOfGBs() && !isCancelled()) {
						
				try {
					final TaskResultReprocess taskResult = (TaskResultReprocess) cservice.take().get();
			
					final StringBuilder lineOut = new StringBuilder();
		
					lineOut.append(df4.format(Math.toDegrees( taskResult.phi1L )));
					lineOut.append(' ');
					lineOut.append(df4.format(Math.toDegrees( taskResult.PhiL )));
					lineOut.append(' ');
					lineOut.append(df4.format(Math.toDegrees( taskResult.phi2L )));
					lineOut.append(' ');
					
					lineOut.append(df4.format(Math.toDegrees( taskResult.phi1R )));
					lineOut.append(' ');
					lineOut.append(df4.format(Math.toDegrees( taskResult.PhiR )));
					lineOut.append(' ');
					lineOut.append(df4.format(Math.toDegrees( taskResult.phi2R )));
					lineOut.append(' ');
					
					lineOut.append(df4.format(Math.toDegrees( taskResult.zenith )));
					lineOut.append(' ');
					lineOut.append(df4.format(Math.toDegrees( taskResult.azimuth )));
					lineOut.append(' ');
	
					if(header.isExperimental()) {
						
						lineOut.append( taskResult.correlat );
						lineOut.append(' ');
						
						lineOut.append( df7.format(taskResult.area ));
						lineOut.append(' ');												
					}
					
					if(header.containsTiltDist() || tiltChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.tiltDist) ));
						lineOut.append(' ');
					}
					if(header.containsTwistDist() || twistChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.twistDist)) );
						lineOut.append(' ');
					}
					if(header.containsSymDist() || symChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.symDist)) );
						lineOut.append(' ');
					}
					if(header.containsImpropDist() || impropChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.impropDist)) );
						lineOut.append(' ');
					}
					if(header.containsTiltAngle() || anglesChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.tiltAngle)) );
						lineOut.append(' ');			
						lineOut.append( df2.format(Math.toDegrees(taskResult.twistAngle)) );
						lineOut.append(' ');
					}
					
					if(header.containsMinTTC() || ttcChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.minTtc)) );
						lineOut.append(' ');			
						lineOut.append( df2.format(90d - Math.toDegrees(taskResult.maxTtc)) );
						lineOut.append(' ');
					}
					
					
					if(header.containsSymTTC() || ttcSymChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.symTtc)) );
						lineOut.append(' ');			
						lineOut.append( df2.format(Math.toDegrees(taskResult.impropTtc)) );
						lineOut.append(' ');
					}
					
					
					if(header.containsDisorTTC() || ttcDisChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.disTtc)) );
						lineOut.append(' ');
					}
					
					if(disAnglesChB.isSelected()) {
						lineOut.append( df2.format(Math.toDegrees(taskResult.disTiltAngle)) );
						lineOut.append(' ');
						lineOut.append( df2.format(Math.toDegrees(taskResult.disTwistAngle)) );
					}
					
					out.println(lineOut);
					
					index++;
					setProgress((int)Math.round((double)index/(double)header.getNumberOfGBs()*100d));			
					
				} catch (InterruptedException exc) { 
					
					return null;
				} catch (ExecutionException exc) { 
					exc.printStackTrace();
					return null;
				}
			}	
				
			in.close();
			out.close();
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time elapsed: " + estimatedTime + " microsec.");
			return null;
		}

		@Override
		public void done() {		
			if(isCancelled()) if(futuresList != null) for(Object future : futuresList) ((Future) future).cancel(true);

			fireBtn.setEnabled(true);
			abortBtn.setEnabled(false);
		}
	}

}
