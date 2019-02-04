package gui_modules;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.ImageIcon;

import utils.ConstantsAndStatic;
import utils.FileUtils;
import utils.GBDistReader;


public class NormalizeDistPanel extends JPanel {
	
	private JTextField expFld;
	private JTextField rndFld;
	private JTextField mrdFld;

	private final FileUtils.GBDistFileFilter gbdistFilter = new FileUtils.GBDistFileFilter(); 
	
	private JButton rndBtn;

	public NormalizeDistPanel() {
		
				
		setLayout(new MigLayout("", "[][]", "[][][][][][][][][][][]"));
		
		JLabel normLbl = new JLabel("<html><b>Divide (normalize) grain boundary distributions by adequate \"random\" distributions:</b>");
		add(normLbl, "cell 0 0 2 1");
		
		JLabel expLbl = new JLabel("Distribution to be normalized:");
		add(expLbl, "cell 0 1 2 1,gapx 10,gapy 5");
		
		expFld = new JTextField();
		add(expFld, "cell 0 2,growx,gapx 10");
		expFld.setColumns(32);
		
		JButton expBtn = new JButton();
		expBtn.setIcon(new ImageIcon(NormalizeDistPanel.class.getResource("/gui_bricks/folder.png")));
		add(expBtn, "cell 1 2");
		
		expBtn.setMaximumSize(new Dimension(24,24));
		expBtn.setMinimumSize(new Dimension(24,24));
		expBtn.setPreferredSize(new Dimension(24,24));
		
		JLabel rndLbl = new JLabel("\"Random\" distribution used for normalization:");
		rndLbl.setToolTipText("<html>You should use a distribution calculated for randomly generated boundaries.<br>\r\nImportant: For generating random boundaries, use whole misorientation space!<br>\r\nBoth distributions should contain the same number of sampling points<br>\r\nand should be calculated for the same misorientation with the same tolerance thresholds.");
		add(rndLbl, "cell 0 3 2 1,gapx 10,gapy 5");
		
		rndFld = new JTextField();
		add(rndFld, "cell 0 4,gapx 10");
		rndFld.setColumns(32);
		
		rndBtn = new JButton();
		rndBtn.setIcon(new ImageIcon(NormalizeDistPanel.class.getResource("/gui_bricks/folder.png")));
		add(rndBtn, "cell 1 4");
		
		rndBtn.setMaximumSize(new Dimension(24,24));
		rndBtn.setMinimumSize(new Dimension(24,24));
		rndBtn.setPreferredSize(new Dimension(24,24));
		
		JLabel mrdLbl = new JLabel("Result expressed as multiples of \"random\" distribution:");
		add(mrdLbl, "cell 0 5 2 1,gapx 10,gapy 5");
		
		mrdFld = new JTextField();
		add(mrdFld, "cell 0 6,growx,gapx 10");
		mrdFld.setColumns(32);
		
		JButton mrdBtn = new JButton();
		mrdBtn.setIcon(new ImageIcon(NormalizeDistPanel.class.getResource("/gui_bricks/folder.png")));
		add(mrdBtn, "cell 1 6");
		mrdBtn.setMaximumSize(new Dimension(24,24));
		mrdBtn.setMinimumSize(new Dimension(24,24));
		mrdBtn.setPreferredSize(new Dimension(24,24));
		
		JLabel info2Lbl = new JLabel("<html><font color=0000cc><small><b>Info:</b> an additional file containing errors of the distribution will be created<small>\r\n</font>");
		add(info2Lbl, "cell 0 7 2 1,gapx 10");
		
		JSeparator separator = new JSeparator();
		add(separator, "cell 0 8 2 1,growx,aligny center,gapy 5 5");
		
		JButton goBtn = new JButton("Normalize");
		add(goBtn, "cell 0 9 2 1");
		
		JLabel infoLbl = new JLabel("<html><small><font color=#cc0000><b>Warning: existing files will be overwritten!</b></font></small>");
		add(infoLbl, "cell 0 10 2 1,gapy 10");
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(gbdistFilter);
		
				
		
		expBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				int returnVal = fc.showDialog(NormalizeDistPanel.this, "Open");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					expFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
			}
		});
		
		rndBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				int returnVal = fc.showDialog(NormalizeDistPanel.this, "Open");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					rndFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
	
			}
		});
		
		
		mrdBtn.addActionListener(new ActionListener() { //TODO
			@Override
			public void actionPerformed(ActionEvent evt) {
							
				int returnVal = fc.showDialog(NormalizeDistPanel.this, "Export");
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
					
					mrdFld.setText(fName);
				}	


			}
		});
		
		
		goBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				File fTmp;

				fTmp = new File(expFld.getText());
				if(!fTmp.exists()) {

					JOptionPane.showMessageDialog(null,
							"Specified distribution does not exist.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}

				
				fTmp = new File(rndFld.getText());
				if(!fTmp.exists()) {
					JOptionPane.showMessageDialog(null,
							"Specified \"random\" distribution does not exist.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}
								
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		
				List DistPts1 = new ArrayList<DistPt>();
				List DistPts2 = new ArrayList<DistPt>();
					
				GBDistReader reader1 = new GBDistReader();
				GBDistReader reader2 = new GBDistReader();
				
				try {
					BufferedReader in = new BufferedReader(new FileReader(expFld.getText()));
				
					reader1.readHeaderLines(in);
						
					if(reader1.containsMRD_MIS() ||	reader1.containsMRD_PLANE()) {
						JOptionPane.showMessageDialog(null,
							    "You probably do not want to normalize the distribution which has been already normalized.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						in.close();
						return;
					}
						
							
					String line = null;
																			
					while ((line = in.readLine()) != null)   {
									
						if(!line.matches("^\\s*#.*")) {
									
							final String[] num = line.trim().split("\\s+");			
									
							final double x = Double.parseDouble(num[0]);
							final double y = Double.parseDouble(num[1]);
								
							final double zen = Double.parseDouble(num[2]);
							final double azim = Double.parseDouble(num[3]);
															
							final double val = Double.parseDouble(num[4]);
								
									
							DistPts1.add(new DistPt(x,y,zen,azim,val));
		
						} 
					}
					in.close();
								
				} catch (IOException | NumberFormatException e) {
	
					JOptionPane.showMessageDialog(null,
						    "I/O errors occurred during reading of distribution.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}	
					
						
				try {
					BufferedReader in = new BufferedReader(new FileReader(rndFld.getText()));
							
					reader2.readHeaderLines(in);
					
					if(reader2.containsMRD_MIS() ||	reader2.containsMRD_PLANE()) {
						JOptionPane.showMessageDialog(null,
							    "You probably do not want to divide by normalized distribution.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						in.close();
						return;
					}
						
					String line = null;
																			
					while ((line = in.readLine()) != null)   {
									
						if(!line.matches("^\\s*#.*")) {
									
							final String[] num = line.trim().split("\\s+");			
									
							final double x = Double.parseDouble(num[0]);
							final double y = Double.parseDouble(num[1]);
								
							final double zen = Double.parseDouble(num[2]);
							final double azim = Double.parseDouble(num[3]);
															
							final double val = Double.parseDouble(num[4]);
								
															
							DistPts2.add(new DistPt(x,y,zen,azim,val));
		
						} 
					}
								
					in.close();
								
				} catch (IOException | NumberFormatException e) {
	
					JOptionPane.showMessageDialog(null,
						    "I/O errors occurred during reading of random distribution.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}	
				
													
					
				if(! (reader1.containsFRAC_MIS() && reader2.containsFRAC_MIS() ) ) {
					JOptionPane.showMessageDialog(null,
							"Both distributions contain inconsistent data.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;		
				}
					
		
				
				if(DistPts1.size() != DistPts2.size()) {
	
					JOptionPane.showMessageDialog(null,
							"Specified distributions contain different numbers of sampling points.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;					
				}
					
									
				Collections.sort(DistPts1, new PtComparator());
				Collections.sort(DistPts2, new PtComparator());
					
				final DecimalFormat df4;
				final DecimalFormat df2;
					
				final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				df4 = new DecimalFormat("0.####", otherSymbols);
				df2 = new DecimalFormat("0.##", otherSymbols);			
					
				try {
				
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(mrdFld.getText())));
						
					out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + ".");
					out.println("# It contains a normalized distribution of grain boundaries for a fixed misorientation");
					out.println("# calculated using the approach based on distance functions.");		
					if(reader1.isExperimental()) {
						out.print("EXP ");
					} else {
						out.print("RANDOM ");
					}		

					out.println(df4.format(reader1.getNMeas()));		
					out.print("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH ");					
					out.println("MRD_FIXMISOR");
 
						
						
					String ext = FileUtils.getExtension(new File(mrdFld.getText()));		
					final PrintWriter outErr;		
					if(ext != null) {
						outErr = new PrintWriter(new BufferedWriter(new FileWriter(
							mrdFld.getText().substring(0, mrdFld.getText().length() - ext.length() - 1) + "_err." + ext)));
					} else {
						outErr = new PrintWriter(new BufferedWriter(new FileWriter(mrdFld.getText() + "_err")));
					}
						
					outErr.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + ".");
					outErr.println("# It contains errors of grain boundary distribution for a fixed misorientation");
					outErr.println("# calculated using the distance based on distance functions.");		
					if(reader1.isExperimental()) {
						outErr.print("EXP ");
					} else {
						outErr.print("RANDOM ");
					}		
					outErr.println(df4.format(reader1.getNMeas()));		
					outErr.print("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH ");												
					outErr.println("MRD_FIXMISOR_ERR");
						

					PtComparator cmp = new PtComparator();
					
					for(int i = 0; i < DistPts1.size(); i++) {
						
						if( cmp.compare((DistPt)DistPts1.get(i), (DistPt)DistPts2.get(i)) == 0) {
				
							if(((DistPt) DistPts2.get(i)).val < 1e-10d) {
								JOptionPane.showMessageDialog(null,
										"Division by zero. Statistics may be to small.",
										"Error",
										JOptionPane.ERROR_MESSAGE);
									setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
									out.close();
									outErr.close();
									return;
							}
								
							out.println(df4.format(( (DistPt) DistPts1.get(i)).x) + " " +
									df4.format(( (DistPt) DistPts1.get(i)).y) + " " +
									df4.format(( (DistPt) DistPts1.get(i)).zenith) + " " +
									df4.format(( (DistPt) DistPts1.get(i)).azimuth) + " " +
									df2.format(( (DistPt) DistPts1.get(i)).val / ((DistPt) DistPts2.get(i)).val)																
									);
								
								
							outErr.println(df4.format(( (DistPt) DistPts1.get(i)).x) + " " +
									df4.format(( (DistPt) DistPts1.get(i)).y) + " " +
									df4.format(( (DistPt) DistPts1.get(i)).zenith) + " " +
									df4.format(( (DistPt) DistPts1.get(i)).azimuth) + " " +
									df2.format(Math.sqrt(
											((DistPt) DistPts1.get(i)).val / reader1.getNMeas()	)
											/((DistPt) DistPts2.get(i)).val) );
								
						
						} else {
								JOptionPane.showMessageDialog(null,
									"Sampling points are inconsistent.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
								setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
								out.close();
								outErr.close();
								return;
						}
					}
					out.close();
					outErr.close();

				} catch(IOException e) {
					JOptionPane.showMessageDialog(null,
							"An I/O error occurred.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						return;										
				}
									
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					
			}
		});

	}
	
	
	private class DistPt {
		
		public double zenith;
		public double azimuth;
		public double val;
		public double x;
		public double y;		
		
		
		public DistPt(double x, double y, double zenith, double azimuth, double val) {
			this.x = x;
			this.y = y;
			this.zenith = zenith;
			this.azimuth = azimuth;
			this.val = val;

		}
	}
	
	private class PtComparator implements Comparator<DistPt> {

		@Override
		public int compare(DistPt pt1, DistPt pt2) {
			
			if(Math.abs(pt1.zenith - pt2.zenith) < 1e-4d) {
				
				if(Math.abs(pt1.azimuth - pt2.azimuth) < 1e-4d) {
					return 0;
				}
				else return (int)Math.signum(pt1.azimuth - pt2.azimuth);
				
			} 
			else return (int)Math.signum(pt1.zenith - pt2.zenith);
				
		}
		
	}

}
