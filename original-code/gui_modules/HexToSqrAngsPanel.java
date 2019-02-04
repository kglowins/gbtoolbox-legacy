package gui_modules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;

import utils.EulerAngles;
import utils.Matrix3x3;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

public class HexToSqrAngsPanel extends JPanel {
	
	private static final double ONEPI = Math.PI;
	private static final double TWOPI = 2d*Math.PI;
	
	private JTextField dirFld;
	private JTextField prefFld;
	private JTextField sufFld;
	private JTextField firstFld;
	private JTextField lastFld;
	private JTextField digitFld;
	private JTextField outDirFld;
	private JTextField outPrefFld;
	private JTextField outSufFld;
	
	private JButton abortBtn;
	private JButton convertBtn;
	
	private JLabel statusLbl;
	
	private JCheckBox zerosChB;
		
	
	private final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
	private final DecimalFormat df = new DecimalFormat("0.######", otherSymbols);
	

	private ArrayList<Integer> corruptIdx;
	
	private Converter conv; 
	
	
	public HexToSqrAngsPanel() {
		setLayout(new MigLayout("", "[][][]", "[][][][][][][][][][][][][][grow]"));
		
		JLabel convertLbl = new JLabel("<html><b>Convert <code>.ang</code> files with hexagonal grids to files with square grids:</b>");
		add(convertLbl, "cell 0 0 3 1");
				
		JLabel dirLbl = new JLabel("<html>Directory with input <code>.ang</code> files:");
		add(dirLbl, "cell 0 1,alignx left,gapx 10");
		
		dirFld = new JTextField();
		add(dirFld, "cell 1 1,alignx left");
		dirFld.setColumns(24);
		
		JButton dirBtn = new JButton();
		dirBtn.setIcon(new ImageIcon(HexToSqrAngsPanel.class.getResource("/gui_bricks/folder.png")));
		dirBtn.setPreferredSize(new Dimension(24, 24));
		dirBtn.setMinimumSize(new Dimension(24, 24));
		dirBtn.setMaximumSize(new Dimension(24, 24));
		add(dirBtn, "cell 2 1");
		
		JLabel prefLbl = new JLabel("Input file name prefix:");
		add(prefLbl, "cell 0 2,alignx left,gapx 10");
		
		prefFld = new JTextField();
		add(prefFld, "cell 1 2,alignx left");
		prefFld.setColumns(12);
		
		JLabel sufLbl = new JLabel("Input file name suffix:");
		add(sufLbl, "cell 0 3,alignx left,gapx 10");
		
		sufFld = new JTextField();
		sufFld.setText(".ang");
		add(sufFld, "cell 1 3,alignx left");
		sufFld.setColumns(12);
		
		JLabel firstLbl = new JLabel("First index:");
		add(firstLbl, "cell 0 4,alignx left,gapx 10");
		
		firstFld = new JTextField();
		firstFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(firstFld, "cell 1 4,alignx left");
		firstFld.setColumns(4);
		
		JLabel lastLbl = new JLabel("Last index:");
		add(lastLbl, "cell 0 5,alignx left,gapx 10");
		
		lastFld = new JTextField();
		lastFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lastFld, "cell 1 5,alignx left");
		lastFld.setColumns(4);
		
		zerosChB = new JCheckBox("Leading zeros; Total number of digits:");
		zerosChB.setToolTipText("Use this option if the indices of your .ang files include leading zeros");
		add(zerosChB, "flowx,cell 0 6,alignx left,gapx 10 40");
		
		digitFld = new JTextField();
		digitFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(digitFld, "cell 1 6,alignx left");
		digitFld.setColumns(4);
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		dirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				int returnVal = fc.showOpenDialog(HexToSqrAngsPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					dirFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
			}
		});
		
		JLabel outDirLbl = new JLabel("<html>Directory for output <code>.ang</code> files:");
		add(outDirLbl, "cell 0 7,alignx left,gapx 10");
		
		outDirFld = new JTextField();
		outDirFld.setColumns(24);
		add(outDirFld, "cell 1 7,alignx left");
		
		JButton outDirBtn = new JButton();
		outDirBtn.setIcon(new ImageIcon(HexToSqrAngsPanel.class.getResource("/gui_bricks/folder.png")));
		outDirBtn.setPreferredSize(new Dimension(24, 24));
		outDirBtn.setMinimumSize(new Dimension(24, 24));
		outDirBtn.setMaximumSize(new Dimension(24, 24));
		add(outDirBtn, "cell 2 7");
		
		JLabel outPrefLbl = new JLabel("Output file name prefix:");
		add(outPrefLbl, "cell 0 8,alignx left,gapx 10");
		
		outPrefFld = new JTextField();
		outPrefFld.setColumns(12);
		add(outPrefFld, "cell 1 8,alignx left");
		
		JLabel outSufLbl = new JLabel("Output file name suffix:");
		add(outSufLbl, "cell 0 9,alignx left,gapx 10");
		
		outSufFld = new JTextField();
		outSufFld.setText(".ang");
		outSufFld.setColumns(12);
		add(outSufFld, "cell 1 9,alignx left");

							
		JSeparator separator = new JSeparator();
		add(separator, "cell 0 10 3 1,growx,aligny center,gapy 5 5");
		
		convertBtn = new JButton("Convert");
		add(convertBtn, "flowx,cell 0 11 3 1");
		
		outDirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				int returnVal = fc.showOpenDialog(HexToSqrAngsPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					outDirFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
			}
		});
		
		convertBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
	
				conv = new Converter();
				conv.execute();				
			}
		});
		
	
		
		JLabel warnLbl = new JLabel("<html><small><font color=#cc0000><b>Warning: existing files will be overwritten!</font></b></small>");
		add(warnLbl, "cell 0 12 3 1,gapy 20");
		
		statusLbl = new JLabel("");
		add(statusLbl, "cell 0 13 3 1,aligny bottom");
		
		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setEnabled(false);
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(HexToSqrAngsPanel.class.getResource("/gui_bricks/abort.png")));
		add(abortBtn, "cell 0 11,alignx right,gapx 20,aligny top");
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
	
				
				conv.cancel(true);				
			}
		});

	}

	
	
	
	private final void convertHexToSqrGrid(String fIn, String fOut, int idx) throws IOException {
		
		BufferedReader in = new BufferedReader(new FileReader(fIn));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fOut)));
		
		String line = null;
		for(int lineNr = 1; lineNr < 20; lineNr++) {
			line = in.readLine();
			out.println(line);
		}
		
		line = in.readLine();
		out.println("# GRID: SqrGrid");
		
		line = in.readLine();
		String[] words = line.trim().split("\\s+");
		final double XSTEP = Double.parseDouble(words[words.length - 1]);
		out.println("# XSTEP: " + XSTEP);
		
		line = in.readLine();
		words = line.trim().split("\\s+");
		final double YSTEP = Double.parseDouble(words[words.length - 1]);
		out.println("# YSTEP: " + YSTEP);
		
		line = in.readLine();
		words = line.trim().split("\\s+");
		final int NCOLS_ODD = Integer.parseInt(words[words.length - 1]);
	
		
		line = in.readLine();
		words = line.trim().split("\\s+");
		final int NCOLS_EVEN = Integer.parseInt(words[words.length - 1]);
		
		final int NCOLS = Math.max(NCOLS_ODD, NCOLS_EVEN);
		
		out.println("# NCOLS_ODD: " + NCOLS);
		out.println("# NCOLS_EVEN: " + NCOLS);
		
		line = in.readLine();
		words = line.trim().split("\\s+");
		final int NROWS = Integer.parseInt(words[words.length - 1]);
		out.println("# NROWS: " + NROWS);
				
		for(int lineNr = 26; lineNr < 33; lineNr++) {
			line = in.readLine();
			out.println(line);
		}
		
		double[][] phi1 = new double[NROWS][NCOLS];
		double[][] Phi = new double[NROWS][NCOLS];
		double[][] phi2 = new double[NROWS][NCOLS];
		double[][] x = new double[NROWS][NCOLS];
		double[][] y = new double[NROWS][NCOLS];

		double[][] val1 = new double[NROWS][NCOLS];
		double[][] val2 = new double[NROWS][NCOLS];
		double[][] val3 = new double[NROWS][NCOLS];
		double[][] val4 = new double[NROWS][NCOLS];
		double[][] val5 = new double[NROWS][NCOLS];

		
		for(int row = 0; row < NROWS; row++) {
			int nCols = 0;
			if(row % 2 == 0) nCols = NCOLS_ODD; else nCols = NCOLS_EVEN;		
			
			for(int col = 0; col < nCols; col++) { 
								
				line = in.readLine();
				
				if(line == null) {
					statusLbl.setText("The file " + fIn + " is corrupted.");
					corruptIdx.add(idx);		
					in.close();
					out.close();
					
					return;
				}
				words = line.trim().split("\\s+");
					
				phi1[row][col] = Double.parseDouble(words[0]);
				Phi[row][col] = Double.parseDouble(words[1]);
				phi2[row][col] = Double.parseDouble(words[2]);
				x[row][col] = Double.parseDouble(words[3]);
				y[row][col] = Double.parseDouble(words[4]);
				val1[row][col] = Double.parseDouble(words[5]);
				val2[row][col] = Double.parseDouble(words[6]);
				val3[row][col] = Double.parseDouble(words[7]);
				val4[row][col] = Double.parseDouble(words[8]);
				val5[row][col] = Double.parseDouble(words[9]);
			}
		}
		
		
		for(int row = 0; row < NROWS; row++) {

			
			if(row % 2 == 0) {
				
				for(int col = 0; col < NCOLS; col++) { 					
					out.println(
							phi1[row][col] + " " +
							Phi[row][col] + " " +
							phi2[row][col] + " " +
							x[row][col] + " " +
							y[row][col] + " " +
							val1[row][col] + " " +
							val2[row][col] + " " +
							val3[row][col] + " " +
							val4[row][col] + " " +
							val5[row][col]		
					);				
				}
				
			} else {
				
				for(int col = 0; col < NCOLS; col++) { 			
					
					double whichCI = Double.NEGATIVE_INFINITY;
					int whichRow = Integer.MAX_VALUE;
					int whichCol = Integer.MAX_VALUE;
					
					boolean checkAround = false;					
					if(col == 0 || col == NCOLS - 1) checkAround = true;
					
					if(!checkAround)
					{
						if( phi1[row][col - 1] < TWOPI && Phi[row][col - 1] < ONEPI && phi2[row][col - 1] < TWOPI &&
							phi1[row][col] < TWOPI && Phi[row][col] < ONEPI && phi2[row][col] < TWOPI )
						{											
							final EulerAngles eul = new EulerAngles();
							final Matrix3x3 M1 = new Matrix3x3();
							final Matrix3x3 M2 = new Matrix3x3();
																		
							eul.set(phi1[row][col - 1], Phi[row][col - 1], phi2[row][col - 1]);
							M1.set(eul);
							eul.set(phi1[row][col], Phi[row][col], phi2[row][col]);
							M2.set(eul);
						
							M1.timesTransposed(M2);
							if(M1.rotationAngle() > 0.08726d) checkAround = true; // 5deg
							else {
								if(val2[row][col-1] >= val2[row][col]) {
									whichRow = row;
									whichCol = col - 1;
								} else {
									whichRow = row;
									whichCol = col;									
								}
							}
							
						} else {
							checkAround = true;
						}
					}
					
														
					if(checkAround) {
						
						if(val2[row - 1][col] > whichCI) {					
							whichCI = val2[row - 1][col];
							whichRow = row - 1;
							whichCol = col;
						}
						
						if(checkAround  && row < NROWS - 1) if(val2[row + 1][col] > whichCI) {
							whichCI = val2[row + 1][col];
							whichRow = row + 1;
							whichCol = col;
						}
					
						if(col > 0) if(val2[row][col - 1] > whichCI) {
							whichCI = val2[row][col - 1];
							whichRow = row;
							whichCol = col - 1;
						}
						
						if(col < NCOLS - 2) if(val2[row][col] > whichCI) {
							whichCI = val2[row][col];
							whichRow = row;
							whichCol = col;
						}
					}
					
					out.println(
							phi1[whichRow][whichCol] + " " +
							Phi[whichRow][whichCol] + " " +
							phi2[whichRow][whichCol] + " " +
							x[row - 1][col] + " " +
							y[row][0] + " " +
							val1[whichRow][whichCol] + " " +
							val2[whichRow][whichCol] + " " +
							val3[whichRow][whichCol] + " " +
							val4[whichRow][whichCol] + " " +
							val5[whichRow][whichCol]		
					);
								
				}				
			}						
		}
		
		in.close();
		out.close();
	}
	
	
	private class Converter extends SwingWorker<Void, Void> {
	       @Override
	       public Void doInBackground() {
	    	   
	    	   abortBtn.setEnabled(true);
	    	   convertBtn.setEnabled(false);
	    	   
	    	   
	    	   statusLbl.setText("");
				corruptIdx = new ArrayList<Integer>();
				int first;
				try {					
					first = Integer.parseInt(firstFld.getText());
					if(first < 0) throw new NumberFormatException();
				} catch(NumberFormatException exc) {					
					JOptionPane.showMessageDialog(null,
							"First index must be a non-negative integer.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return null;
				}
				
				int last;
				try {					
					last = Integer.parseInt(lastFld.getText());
					if(last < first) throw new NumberFormatException();
				} catch(NumberFormatException exc) {					
					JOptionPane.showMessageDialog(null,
							"Last index must be an integer not less than the first index.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return null;
				}
				
				
				int digits = 0;
				
				if(zerosChB.isSelected()) {
					try {					
						digits = Integer.parseInt(digitFld.getText());
						if(digits < 1) throw new NumberFormatException();
						
						int value = 1;
						int power = 0;
						while(last / value > 0) {
							power++;
							value *= 10;
						}
						if(digits < power) throw new NumberFormatException();
	
	
					} catch(NumberFormatException exc) {					
						JOptionPane.showMessageDialog(null,
								"Number of digits must be an integer greater than zero and large enough to store the last index.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return null;
					}
				}
				
				try {					
					for(int index = first; index <= last; index++) {
				
						if(isCancelled()) return null;
						
						
						String input = "";
						String output = "";
						
						input += dirFld.getText() + System.getProperty("file.separator") + prefFld.getText();
						output += outDirFld.getText() + System.getProperty("file.separator") + outPrefFld.getText();
						
						if(zerosChB.isSelected()) {
							if(index == 0) {
								for(int k = 1; k < digits; k++) {
									input += '0';
									output += '0';
								}
							} else {
								int value = 1;
								int power = 0;
								while(index / value > 0) {
									power++;
									value *= 10;
								}
								for(int k = 1; k <= digits - power; k++) {
									input += '0';
									output += '0';
								}
							}
						}
						input += index + sufFld.getText();
						output += index + outSufFld.getText();
																								
						statusLbl.setText("Converting " + input + " to " + output);

						
						convertHexToSqrGrid(input, output, index);
					}
				} catch(Exception exc) {
					exc.printStackTrace();
					statusLbl.setText("Conversion failed. An input/output error occurred.");
					JOptionPane.showMessageDialog(null,
							"An I/O error occurred.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return null;
				}
				String compl = "Conversion complete. " + corruptIdx.size() + " corrupted files:";
				for(Integer idx : corruptIdx) compl += " " + idx; 
				statusLbl.setText(compl);
	    	   
	           return null;
	       }

	       @Override
	       protected void done() {
	           
	    	   if(isCancelled()) statusLbl.setText("Conversion aborted.");
	    	   abortBtn.setEnabled(false);
	    	   convertBtn.setEnabled(true);
	       }
	   }

	
}
