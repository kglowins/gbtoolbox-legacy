package com.github.kglowins.gbtoolbox.gui_modules;

import com.github.kglowins.gbtoolbox.gui_bricks.GBPD_DistFunOptsPanel;
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
import com.github.kglowins.gbtoolbox.algorithms.MisAxD_DistFunWorker;

import com.github.kglowins.gbtoolbox.utils.FileUtils;
import com.github.kglowins.gbtoolbox.utils.GBDatHeader;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;


public class Stats_MisorAxesFrame extends JFrame {

	
	private final FileUtils.GBDistFileFilter gbdistFilter = new FileUtils.GBDistFileFilter(); 
	
	private JLabel omitLbl;
	
	private JPanel contentPane;
	private JTextField outFld;
		
	private ArrayList<GBDatHeader> gbFiles;
	
	private JPanel methodCards; 
	
	private GBPD_DistFunOptsPanel distOpts;
	
	private JButton fireBtn;
	private JButton abortBtn;	
	private JProgressBar progressBar;
	
	private boolean eliminate;
	private double areaThreshold;
	
	
	private MisAxD_DistFunWorker misaxDistFun;
	
	
	private JSeparator separator_2;
	

		
	public Stats_MisorAxesFrame(ArrayList<GBDatHeader> gbs, boolean elimin, double areaThr) {
		
		this.eliminate = elimin;
		this.areaThreshold = areaThr;
		
		gbFiles = gbs;
		omitLbl = new JLabel();
		
	
	
		if(elimin) omitLbl.setText("<html><font color=#0000ff>Mesh triangles with area greater than " + areaThr + " will not be taken into account</i></font>");
		else omitLbl.setText("<html><font color=#0000ff>All mesh triangles will be taken into account</i></font>");
		
		
		distOpts = new GBPD_DistFunOptsPanel(true);
		
		
		setTitle("GBToolbox: Distribution of misorientation axes");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_MisorAxesFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[]", "[][][][][][][][]"));
		
		contentPane.add(omitLbl, "cell 0 0");
		
		separator_2 = new JSeparator();
		contentPane.add(separator_2, "cell 0 1,growx,aligny center,gapy 5 5");
		
		JLabel specifyLbl = new JLabel("<html><b>Specify options and calculate distributions of misorientation axes:</b>");
		contentPane.add(specifyLbl, "cell 0 2");
		
		methodCards = new JPanel();
		contentPane.add(methodCards, "cell 0 3,gapx 10,gapy 5,grow");
		methodCards.setLayout(new CardLayout(0, 0));
		
	
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(gbdistFilter);
		
		JLabel outLbl = new JLabel("<html><u>Output <code>dist</code> file</u>:");
		contentPane.add(outLbl, "flowx,cell 0 4,gapx 10,gapy 5");
		
		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 5,growx,aligny center,gapy 5 5");
		
		fireBtn = new JButton("Calculate");
		contentPane.add(fireBtn, "flowx,cell 0 6");
		
		JLabel overwriteLbl = new JLabel("<html><font color=#cc0000><small>Warning: <b>Existing files will be overwritten!</b></small></font>");
		contentPane.add(overwriteLbl, "cell 0 7,gapy 5");
		
		outFld = new JTextField();
		contentPane.add(outFld, "cell 0 4,gapy 5");
		outFld.setColumns(18);
		
		JButton outBtn = new JButton();
		outBtn.setMinimumSize(new Dimension(24,24));
		outBtn.setMaximumSize(new Dimension(24,24));
		outBtn.setPreferredSize(new Dimension(24,24));
		outBtn.setIcon(new ImageIcon(Stats_MisorAxesFrame.class.getResource("/gui_bricks/folder.png")));
		contentPane.add(outBtn, "cell 0 4,gapy 5");
		
		
		outBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {			
				
				int returnVal = fc.showDialog(Stats_MisorAxesFrame.this, "Save");
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
	
					
					final double tol;
					try {
						tol = distOpts.getPlaneTol();
						if(tol < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(Stats_MisorAxesFrame.this,
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
					
					
					
					misaxDistFun = new MisAxD_DistFunWorker(gbFiles,
							tol, nBins, eliminate, areaThreshold, outFld, fireBtn, abortBtn, progressBar);
					
					misaxDistFun.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer)evt.getNewValue());
							}						
						}				
					});
					
					try {
						
						 misaxDistFun.execute();
						
					} catch(Exception e) {
						abortBtn.setEnabled(false);
						fireBtn.setEnabled(true);
						JOptionPane.showMessageDialog(Stats_MisorAxesFrame.this,
								"An error occurred.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;	
					}
										
					
																	
			}
		});
		
		
		methodCards.add(distOpts, "");	

		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar, "cell 0 6,gapx 20");
		
		abortBtn = new JButton();
		abortBtn.setEnabled(false);
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(Stats_MisorAxesFrame.class.getResource("/gui_bricks/abort.png")));
		contentPane.add(abortBtn, "cell 0 6,gapx 20");
		
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				 misaxDistFun.cancel(true); 
				
			}			
		});
		
		
		pack();
		setResizable(false);
	}
	
}
