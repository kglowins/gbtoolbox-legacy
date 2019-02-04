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
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import enums.PointGroup;
import gui_bricks.Dialog_GBPlusLimits;

import net.miginfocom.swing.MigLayout;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.FileUtils;
import utils.GBDatHeader;
import utils.GBPlusLimits;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.Transformations;
import utils.UnitVector;
import javax.swing.JScrollPane;
import javax.swing.JList;

import org.apache.commons.math3.util.FastMath;

public class SubsetsPanel extends JPanel implements ListSelectionListener {
	
	private JTextField inFld;
	private JTextField outFld;

	private FileUtils.GBDatFileFilter gbdatFilter = new FileUtils.GBDatFileFilter();

	private JButton abortBtn;
	private JButton fireBtn;

	private final DecimalFormat df2;
	
	private DefaultListModel listModel;

	private ExcludeTask task;

	private JProgressBar progressBar;
	private JLabel lblNewLabel;
	private JLabel lblTypeOfData;
	private JLabel typeLbl;
	
	private PointGroup pointGrp = PointGroup.M3M;
	private JButton addBtn;
	private JButton removeBtn;
	private JScrollPane scrollPane;
	private JList list;
	private JLabel summaryLbl;
	
	private Dialog_GBPlusLimits dialog;

	private ArrayList<GBPlusLimits> gbs = new ArrayList<GBPlusLimits>();
	
	
	
	public final JList getList() {
		return list;
	}



	public final DefaultListModel getListModel() {
		return listModel;
	}



	public final ArrayList<GBPlusLimits> getGbs() {
		return gbs;
	}



	@Override
	public void valueChanged(ListSelectionEvent evt) {

		if (evt.getValueIsAdjusting() == false) {
			
			DefaultListModel dlm = (DefaultListModel) list.getModel();
			int count = list.getSelectedIndices().length;
			
			
			if(count == 0) {			
				removeBtn.setEnabled(false); 
								

			} else {
				removeBtn.setEnabled(true); 
				
			}
		}
	}
	
	

	public SubsetsPanel() {

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df2 = new DecimalFormat("0.##", otherSymbols);
	

		setLayout(new MigLayout("", "[][]", "[][][][][][][][][][][]"));

		JLabel lblHeader = new JLabel("<html><b>Extract subsets of grain boundary data:</b>");
		add(lblHeader, "cell 0 0");

		JLabel lblInput = new JLabel("<html><u><code>gbdat</code> file to be reprocessed</u>:");
		add(lblInput, "flowx,cell 0 1,gapx 10,gapy 5");
		
		lblTypeOfData = new JLabel("<html><small>Type of data:<small>");
		add(lblTypeOfData, "flowx,cell 0 2,gapx 20,gapy 5");

		JLabel lblOutput = new JLabel("<html><u>Output <code>gbdat</code> file</u>:");
		add(lblOutput, "flowx,cell 0 3,gapx 10,gapy 5");

		JLabel lblValues = new JLabel("<html><u>Boundaries to be removed from the data</u>:");
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
						else type += "<html><small><b>Random</b> "; 
						
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
						
						
						
														
						if(pointGrp == PointGroup.M3M || pointGrp == PointGroup._6MMM) {
							
							fireBtn.setEnabled(true);
							addBtn.setEnabled(true);
							
							dialog = new Dialog_GBPlusLimits(pointGrp, SubsetsPanel.this);
							
						} else {
							
							fireBtn.setEnabled(false);
							addBtn.setEnabled(false);
							type += "   (Subsets are not supperted for this point group)";

						}
						
						removeBtn.setEnabled(false);
						typeLbl.setText(type);
						gbs.clear();
						listModel.clear();
						return;

					} catch (NumberFormatException | IOException exc) {
						// Nothing is necessary here I guess
						
					}
				}

				fireBtn.setEnabled(false);
				addBtn.setEnabled(false);
				removeBtn.setEnabled(false);
				typeLbl.setText("");
				gbs.clear();
				listModel.clear();
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
				int returnVal = inputFc.showOpenDialog(SubsetsPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = inputFc.getSelectedFile();
					inFld.setText(f.getAbsolutePath());
				}
			}
		});

		inBtn.setIcon(new ImageIcon(SubsetsPanel.class.getResource("/gui_bricks/folder.png")));
		add(inBtn, "cell 0 1,gapy 5");

		outFld = new JTextField();
		add(outFld, "cell 0 3,gapy 5");
		outFld.setColumns(18);

		JButton outBtn = new JButton();
		outBtn.setPreferredSize(new Dimension(24,24));
		outBtn.setMinimumSize(new Dimension(24,24));
		outBtn.setMaximumSize(new Dimension(24,24));
		outBtn.setIcon(new ImageIcon(SubsetsPanel.class.getResource("/gui_bricks/folder.png")));
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

			

				int returnVal = outputFc.showSaveDialog(SubsetsPanel.this);
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
		
		scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 5 1 2,gapx 10,gapy 5,grow");
		
	
		list = new JList();
		listModel = new DefaultListModel();
		list.setModel(listModel);
		list.getSelectionModel().addListSelectionListener(this);
		
		
		
		
		scrollPane.setViewportView(list);
		scrollPane.setPreferredSize(new Dimension(550,120));
		
		addBtn = new JButton();
		addBtn.setEnabled(false);
		addBtn.setIcon(new ImageIcon(SubsetsPanel.class.getResource("/gui_bricks/add-icon.png")));
		addBtn.setToolTipText("Add boundary type(s) to the list");
		addBtn.setPreferredSize(new Dimension(24, 24));
		addBtn.setMinimumSize(new Dimension(24, 24));
		addBtn.setMaximumSize(new Dimension(24, 24));
		add(addBtn, "cell 1 5,gapy 5");
		
		removeBtn = new JButton();
		removeBtn.setIcon(new ImageIcon(SubsetsPanel.class.getResource("/gui_bricks/remove.png")));
		removeBtn.setToolTipText("Remove boundary type(s) from the list");
		removeBtn.setPreferredSize(new Dimension(24, 24));
		removeBtn.setMinimumSize(new Dimension(24, 24));
		removeBtn.setMaximumSize(new Dimension(24, 24));
		removeBtn.setEnabled(false);
		add(removeBtn, "cell 1 6,aligny top");

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 7 2 1,growx,aligny center,gapy 5 5");

		//TODO
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				dialog.setVisible(true);
				dialog.setFlag();

			}
		});
		
		removeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				// !! sprawdzic czy to dziala
				DefaultListModel dlm = (DefaultListModel) list.getModel();

				for(int i = list.getSelectedIndices().length - 1; i >= 0; i--) {
					int j = list.getSelectedIndices()[i];
					gbs.remove(j);
				}
				
				
				if(list.getSelectedIndices().length > 0) {
					int[] tmp = list.getSelectedIndices();
					int[] selectedIndices = list.getSelectedIndices();

					for (int i = tmp.length-1; i >=0; i--) {
						selectedIndices = list.getSelectedIndices();
						dlm.removeElementAt(selectedIndices[i]);
						//gbFiles.remove(selectedIndices[i]); //TODO !!!!
					} // end-for
				} // end-if

				if(dlm.getSize() == 0) {
					removeBtn.setEnabled(false);
				}
			}
		});



		fireBtn = new JButton("Exclude");

		fireBtn.setEnabled(false);
		add(fireBtn, "flowx,cell 0 8 2 1,aligny center");

		fireBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				
				summaryLbl.setText("<html>&nbsp;");
				
				
				

				
				try {
					task = new ExcludeTask();
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
		add(progressBar, "cell 0 8 2 1,gapx 20,aligny center");

		progressBar.setStringPainted(true);

		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setEnabled(false);
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(SubsetsPanel.class.getResource("/gui_bricks/abort.png")));
		add(abortBtn, "cell 0 8 2 1,gapx 20,aligny center");
		
		summaryLbl = new JLabel(" ");
		add(summaryLbl, "cell 0 9 2 1,gapx 10,gapy 10");
		
		lblNewLabel = new JLabel("<html><font color=cc0000><small><b>Warning: existing files will be overwritten!</small></font></b>");
		add(lblNewLabel, "cell 0 10 2 1,gapy 20");
		
		typeLbl = new JLabel(" ");
		add(typeLbl, "cell 0 2,gapy 5");
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				task.cancel(true);				
			}			
		});

	}

	
	private final class ExcludeTask extends SwingWorker<Void, Void> {

		private static final double INFTY = Double.MAX_VALUE;
		private final PrintWriter out;
		private final BufferedReader in;
		private final GBDatHeader header;
		
		private final Matrix3x3[] setC;
		

		

		public ExcludeTask() throws IOException {					
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

			// quite stupid way for rewriting the header, but it does not matter
		    out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
			out.println("# it contains grain boundary parameters and corresponding additional values");	
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
			if(header.containsTiltDist() ) fields += "DIST_TILT ";
			if(header.containsTwistDist() ) fields += "DIST_TWIST ";
			if(header.containsSymDist() ) fields += "DIST_SYM ";
			if(header.containsImpropDist() ) fields += "DIST_180-TILT ";
			if(header.containsTiltAngle() ) fields += "F_TILT_ANGLE F_TWIST_ANGLE ";
			
			if(header.containsMinTTC() ) fields += "APPROX_D_TWIST APPROX_D_TILT "; 
			if(header.containsSymTTC() ) fields += "APPROX_D_SYM APPROX_D_180-TILT ";
			
			
			if(header.containsDisorTTC() ) fields += "DISOR_TTC "; 
			
			if(header.containsDisTiltAngle() ) fields += "DISOR_TILT_A DISOR_TWIST_A ";

			out.println(fields);
			
			
			int counter = 0;
			int totalN = header.getNumberOfGBs();
			
			double totalArea = 0d;
			double accArea = 0d;
			
			boolean stop;					
		

			GBDatHeader.skipHeaderLines(in); 

			String line = null;

			while ((line = in.readLine()) != null && !isCancelled()) {
						
				final String[] num = line.trim().split("\\s+");
								
				stop = false;			
				progressBar.setValue((int) ((double)counter / (double)totalN * 100d));
				counter++;				
				double area = 1d;
				if(header.isExperimental()) area = Double.parseDouble(num[9]);
				totalArea += area;

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
				
				final Matrix3x3 ML = new Matrix3x3();
				ML.set(eulL);
					
				final EulerAngles eulR = new EulerAngles();
				eulR.set(phi1R, PhiR, phi2R);
				
				final Matrix3x3 MR = new Matrix3x3();
				MR.set(eulR);
								
				final Matrix3x3 M = new Matrix3x3(ML);
				M.timesTransposed(MR);
				
				final UnitVector m1 = new UnitVector();
				m1.set(znth, azmth);		
				m1.transform(ML); 
				
				final InterfaceMatrix B = new InterfaceMatrix(M, m1);
				
				
				//double omegaMin = Double.MAX_VALUE;
				//double gammaMin = Double.MAX_VALUE;
																						
				for(boolean t : new boolean[]{false, true})
				{
					for(boolean min : new boolean[]{false, true})
					{
						for(Matrix3x3 C1 : setC)
						{
							for(Matrix3x3 C2 : setC)								
							{
								
								final InterfaceMatrix CBC = new InterfaceMatrix(B);
								CBC.applySymmetry1(C1);
								CBC.applySymmetry2(C2);
							
								if(t) CBC.transpose();
								if(min) CBC.toMinus();
								
								// loop over the list
								for(GBPlusLimits gb : gbs) {
								
									if(!gb.isArbitraryMisor() && !gb.isArbitraryPlane()) {
										final Matrix3x3 CMC = new Matrix3x3();
										CMC.set(CBC.M());
										CMC.timesTransposed(gb.getM());
										
										final double omega = CMC.rotationAngle();

										final UnitVector Cm1 = new UnitVector();
										Cm1.set(CBC.m1());

										final double gamma = FastMath.acos(Cm1.dot(gb.getM1()));
									
								
										if(omega < gb.getMisorLimit() && gamma < gb.getPlaneLimit()) {
									
												stop = true;
												break;		
										}
																													
									}
									
									
									if(!gb.isArbitraryMisor() && gb.isArbitraryPlane()) {
									
										final Matrix3x3 CMC = new Matrix3x3();
										CMC.set(CBC.M());
										CMC.timesTransposed(gb.getM());
							
										final double omega = CMC.rotationAngle();
								
									
								
										if(omega < gb.getMisorLimit()) {
											stop = true;
											break;
										}
									}
								
									if(!gb.isArbitraryPlane() && gb.isArbitraryMisor()) {
									
										final UnitVector Cm1 = new UnitVector();
										Cm1.set(CBC.m1());
									
										final double gamma = FastMath.acos(Cm1.dot(gb.getM1()));
									

										if(gamma < gb.getPlaneLimit()) {
											stop = true;
											break;
										}
									}	
								} // gbs
								if(stop) break;
								
							} // C
							if(stop) break;							
						} // C	
						if(stop) break;
					} // mt
					if(stop) break;
				} //mt
				
				if(!stop) { // TODO
						
					accArea += area;
					out.println(line.trim());
				}
				
			}

			progressBar.setValue(100);
			summaryLbl.setText("<html><font color=0000bb>" + df2.format((1d-accArea/totalArea)*100) + "% of boundaries have been removed.</font></b>");
			in.close();
			out.close();
			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time elapsed: " + estimatedTime + " microsec.");
			return null;
		}

		@Override
		public void done() {		
			fireBtn.setEnabled(true);
			abortBtn.setEnabled(false);
		}
	}


	public final JButton getRemoveBtn() {
		return removeBtn;
	}
	
	

}
