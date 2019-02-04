package gui_modules;

import enums.GBCD_GBPD_Method;
import gui_bricks.GBCD_GBPD_BinsOptsPanel;
import gui_bricks.GBPDSample_DistFunOptsPanel;
import gui_bricks.GBPD_DistFunOptsPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import algorithms.GBPD_BinsWorker;
import algorithms.GBPD_DistFunWorker;
import algorithms.GBPD_OuterFrameWorker;

import utils.FileUtils;
import utils.GBDatHeader;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;


public class Stats_PlanesIndepMisorFrame extends JFrame {

	private static final String BINS = "Based on a partition into bins (crystallite's frame)";
	private static final String DIST_FUN = "Based on a metric (crystallite's frame)";
	private static final String DIST_FUN_OUT = "Based on a metric (sample's frame)";
	
	private final FileUtils.GBDistFileFilter gbdistFilter = new FileUtils.GBDistFileFilter(); 

	
	private JLabel omitLbl;
	
	private JPanel contentPane;
	private JTextField outFld;
		
	private ArrayList<GBDatHeader> gbFiles;
	
	private JPanel methodCards; 
	
	private GBCD_GBPD_Method gbcdMethod;
	
	private GBPD_DistFunOptsPanel distOpts;
	private GBPDSample_DistFunOptsPanel sampleOpts;
	private GBCD_GBPD_BinsOptsPanel binsOpts;
	
	private JButton fireBtn;
	private JButton abortBtn;	
	private JProgressBar progressBar;
	
	private boolean eliminate;
	private double areaThreshold;
	
	private GBPD_BinsWorker gbpdBins;
	private GBPD_DistFunWorker gbpdDistFun;
	private GBPD_OuterFrameWorker gbpdDistFunOut;
	
	private JSeparator separator_2;
	

		
	public Stats_PlanesIndepMisorFrame(ArrayList<GBDatHeader> gbs, boolean elimin, double areaThr) {
		
		this.eliminate = elimin;
		this.areaThreshold = areaThr;
		
		gbFiles = gbs;
		omitLbl = new JLabel();
		
	
	
		if(elimin) omitLbl.setText("<html><font color=#0000ff>Mesh triangles with area greater than " + areaThr + " will not be taken into account</i></font>");
		else omitLbl.setText("<html><font color=#0000ff>All mesh triangles will be taken into account</i></font>");
		
		gbcdMethod = GBCD_GBPD_Method.DIST_FUN;
		distOpts = new GBPD_DistFunOptsPanel(false);
		sampleOpts = new GBPDSample_DistFunOptsPanel();
		binsOpts = new GBCD_GBPD_BinsOptsPanel();
		
		setTitle("GBToolbox: Distribution of boundary planes independent of misorientations");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_PlanesIndepMisorFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[]", "[][][][][][][][][]"));
		
		contentPane.add(omitLbl, "cell 0 0");
		
		separator_2 = new JSeparator();
		contentPane.add(separator_2, "cell 0 1,growx,aligny center,gapy 5 5");
		
		JLabel specifyLbl = new JLabel("<html><b>Specify options and calculate distributions of boundary planes:</b>");
		contentPane.add(specifyLbl, "cell 0 2");
		
					
		JLabel lblmethod = new JLabel("<html><u>Method & reference frame</u>:");
		contentPane.add(lblmethod, "flowx,cell 0 3,gapx 10,gapy 5");
		
		methodCards = new JPanel();
		contentPane.add(methodCards, "cell 0 4,gapx 20,gapy 5,grow");
		methodCards.setLayout(new CardLayout(0, 0));
		
	
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(gbdistFilter);
		
		JLabel outLbl = new JLabel("<html><u>Output <code>dist</code> file</u>:");
		contentPane.add(outLbl, "flowx,cell 0 5,gapx 10,gapy 5");
		
		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 6,growx,aligny center,gapy 5 5");
		
		fireBtn = new JButton("Calculate");
		contentPane.add(fireBtn, "flowx,cell 0 7");
		
		JLabel overwriteLbl = new JLabel("<html><font color=#cc0000><small>Warning: <b>Existing files will be overwritten!</b></small></font>");
		contentPane.add(overwriteLbl, "cell 0 8,gapy 5");
		
		outFld = new JTextField();
		contentPane.add(outFld, "cell 0 5,gapy 5");
		outFld.setColumns(18);
		
		JButton outBtn = new JButton();
		outBtn.setMinimumSize(new Dimension(24,24));
		outBtn.setMaximumSize(new Dimension(24,24));
		outBtn.setPreferredSize(new Dimension(24,24));
		outBtn.setIcon(new ImageIcon(Stats_PlanesIndepMisorFrame.class.getResource("/gui_bricks/folder.png")));
		contentPane.add(outBtn, "cell 0 5,gapy 5");
		
		JComboBox methodCb = new JComboBox();
		methodCb.setModel(new DefaultComboBoxModel(new String[] {DIST_FUN, DIST_FUN_OUT, BINS}));
		contentPane.add(methodCb, "cell 0 3,gapy 5");
		
		methodCb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                JComboBox cb = (JComboBox) e.getSource();
                String sel = cb.getSelectedItem().toString();
                
                CardLayout cl = (CardLayout) methodCards.getLayout();
            	cl.show(methodCards, sel);
            	
            	switch(sel) {
            	case DIST_FUN: gbcdMethod = GBCD_GBPD_Method.DIST_FUN; break;
            	
            	case DIST_FUN_OUT: gbcdMethod = GBCD_GBPD_Method.PLANE_OUT; break;
            	
            	case BINS: gbcdMethod = GBCD_GBPD_Method.BINS; break;
            	default: break;
            	}
            	
            }
		});
		
		
		outBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {			
				
				int returnVal = fc.showDialog(Stats_PlanesIndepMisorFrame.this, "Save");
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
				
								
				// calculate GBPD
				
				
				switch(gbcdMethod) {
				
				case DIST_FUN: //TODO
					
					final double tol;
					try {
						tol = distOpts.getPlaneTol();
						if(tol < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesIndepMisorFrame.this,
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
								"Number of sampling points must be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
					
					gbpdDistFun = new GBPD_DistFunWorker(gbFiles,
							tol, nBins, eliminate, areaThreshold, outFld, fireBtn, abortBtn, progressBar);
					
					gbpdDistFun.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer)evt.getNewValue());
							}						
						}				
					});
					
					try {
						
						gbpdDistFun.execute();
						
					} catch(Exception e) {
						abortBtn.setEnabled(false);
						fireBtn.setEnabled(true);
						JOptionPane.showMessageDialog(Stats_PlanesIndepMisorFrame.this,
								"An error occurred.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;	
					}
										
					
					break;
					
				case PLANE_OUT:
					
					
					try {
						tol = sampleOpts.getPlaneTol();
						if(tol < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesIndepMisorFrame.this,
								"Tolerance must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}

					
					try {
						nBins = sampleOpts.getNBins();
						if(nBins < 0) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Number of sampling points must be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
					
					//TODO read axis
					int axis = sampleOpts.getAxis(); // Z default
					
					
					gbpdDistFunOut = new GBPD_OuterFrameWorker(gbFiles,
							tol, nBins, eliminate, areaThreshold, outFld, fireBtn, abortBtn, progressBar, axis);
					
					gbpdDistFunOut.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer)evt.getNewValue());
							}						
						}				
					});
					
					try {
						
						gbpdDistFunOut.execute();
						
					} catch(Exception e) {
						abortBtn.setEnabled(false);
						fireBtn.setEnabled(true);
						JOptionPane.showMessageDialog(Stats_PlanesIndepMisorFrame.this,
								"An error occurred.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;	
					}
										
					
					break;
					
					
				case BINS: //TODO
					
					
					final int D1;
					try {
						D1 = binsOpts.getD1();
						if(D1 <= 0) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_PlanesIndepMisorFrame.this,
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
						JOptionPane.showMessageDialog(Stats_PlanesIndepMisorFrame.this,
								"Number of bins must be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}

					gbpdBins = new GBPD_BinsWorker(gbFiles,
							D1, D2, eliminate, areaThreshold, outFld, fireBtn, abortBtn, progressBar);
					
					gbpdBins.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer)evt.getNewValue());
							}						
						}				
					});
														
					try {
						
						gbpdBins.execute();
						
					} catch(Exception e) {
						e.printStackTrace();
						abortBtn.setEnabled(false);
						fireBtn.setEnabled(true);
						JOptionPane.showMessageDialog(Stats_PlanesIndepMisorFrame.this,
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
		methodCards.add(sampleOpts, DIST_FUN_OUT);
		
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar, "cell 0 7,gapx 20");
		
		abortBtn = new JButton();
		abortBtn.setEnabled(false);
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(Stats_PlanesIndepMisorFrame.class.getResource("/gui_bricks/abort.png")));
		contentPane.add(abortBtn, "cell 0 7,gapx 20");
		
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
			
				switch(gbcdMethod) {
				
					case DIST_FUN: gbpdDistFun.cancel(true); break;
					case PLANE_OUT: gbpdDistFunOut.cancel(true); break;
					case BINS: gbpdBins.cancel(true); break;
				}
			}			
		});
		
		
		pack();
		setResizable(false);
	}
	
}
