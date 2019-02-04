package gui_bricks;

import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
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
import java.util.Iterator;
import java.util.Locale;

import net.miginfocom.swing.MigLayout;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.GBDatHeader;
import utils.Matrix3x3;
import utils.Transformations;

public class ExportDisorAngles extends JFrame {

	private JPanel contentPane;
	private JTextField outFld;
	private JButton exportBtn;
	
	private static final double INFTY = Double.MAX_VALUE;

	private boolean omit;
	private double areaThr;
	private ArrayList<GBDatHeader> gbFiles;


	public ExportDisorAngles(ArrayList<GBDatHeader> gbf, boolean __omit, double __areaThr) {
		
		gbFiles = gbf;
		omit = __omit;
		areaThr = __areaThr;
		
		setTitle("GBToolbox: Export disorientation angles");
		setIconImage(Toolkit.getDefaultToolkit().getImage(ExportDisorAngles.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][]", "[][][]"));
		
		JLabel lblChooseOutputFile = new JLabel("<html><b>Choose output file:</b>");
		contentPane.add(lblChooseOutputFile, "cell 0 0 2 1");
		
		outFld = new JTextField();
		contentPane.add(outFld, "cell 0 1,growx");
		outFld.setColumns(24);
		
		JButton outBtn = new JButton();
		outBtn.setIcon(new ImageIcon(ExportDisorAngles.class.getResource("/gui_bricks/folder.png")));
		outBtn.setMinimumSize(new Dimension(24,24));
		outBtn.setMaximumSize(new Dimension(24,24));
		outBtn.setPreferredSize(new Dimension(24,24));
		contentPane.add(outBtn, "cell 1 1");
		
		exportBtn = new JButton("Export");
		contentPane.add(exportBtn, "cell 0 2 2 1,alignx center,gapy 5");
		
		pack();
		setResizable(false);
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		outBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent evt) {
				
				int returnVal = fc.showDialog(ExportDisorAngles.this, "Choose");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = fc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					outFld.setText(fName);
				}					
			}					
		});
		
		
		exportBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent evt) {
				
				try {
					
				
					final Matrix3x3[] setC = Transformations.getSymmetryTransformations(gbFiles.get(0).getPointGrp());
													
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));
					out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + ". It contains disorientation angles.");
					out.println("# DISOR_ANG AREA");
					
					final DecimalFormat df4;
					final DecimalFormat df7;
									
					final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
					df4 = new DecimalFormat("0.####", otherSymbols);
					df7 = new DecimalFormat("0.#######", otherSymbols);
					
					Iterator<GBDatHeader> iterator = gbFiles.iterator();
					
					final boolean isExp = gbFiles.get(0).isExperimental();
					
					while(iterator.hasNext()) {
	
						GBDatHeader header = iterator.next();			
						final BufferedReader in = new BufferedReader(new FileReader(header.getPath()));
						
						GBDatHeader.skipHeaderLines(in);
						
						String line = null;
						
						while ((line = in.readLine()) != null) {
	
							final String[] num = line.trim().split("\\s+");
								
							double area = INFTY;
							
							if(isExp) area = Double.parseDouble(num[9]);
							
							if(!omit || (omit && area <= areaThr)) {
								
								final double phi1L = Math.toRadians(Double.parseDouble(num[0]));
								final double PhiL = Math.toRadians(Double.parseDouble(num[1]));
								final double phi2L = Math.toRadians(Double.parseDouble(num[2]));
								
								final double phi1R = Math.toRadians(Double.parseDouble(num[3]));
								final double PhiR = Math.toRadians(Double.parseDouble(num[4]));
								final double phi2R = Math.toRadians(Double.parseDouble(num[5]));
											
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
													
								double omegaMin = INFTY;
									
								for(Matrix3x3 C : setC) { //TODO is it enough?
									
									final Matrix3x3 CM = new Matrix3x3(M);
									CM.leftMul(C);
									final double omega = CM.rotationAngle();
									if(omega < omegaMin) omegaMin = omega;
								}
							
								if(isExp) out.println(df4.format( Math.toDegrees(omegaMin) ) + ' ' + df7.format( area ));
								else out.println(df4.format( Math.toDegrees(omegaMin) ) + " 1");
							}
						}			
					}
					
					out.close();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				} catch(IOException | NumberFormatException exc) {
					JOptionPane.showMessageDialog(null,
							"An I/O error occurred.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}
			}					
		});
		
		pack();
		setResizable(false);
	}

}
