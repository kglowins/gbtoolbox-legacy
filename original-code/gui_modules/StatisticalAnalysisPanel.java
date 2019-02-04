package gui_modules;

import enums.PointGroup;
import gui_bricks.ExportDisorAngles;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;


import net.miginfocom.swing.MigLayout;

import org.math.plot.Plot2DPanel;
import org.math.plot.utils.Array;

import utils.EulerAngles;
import utils.FileUtils;
import utils.GBDatHeader;
import utils.Matrix3x3;
import utils.Transformations;
import javax.swing.SwingConstants;


public class StatisticalAnalysisPanel extends JPanel {

	private static final double INFTY = Double.MAX_VALUE;

	private JLabel commentLbl;

	private JList list;
	private DefaultListModel listModel;

	private ArrayList<GBDatHeader> gbFiles;

	private boolean ptGrpOK;
	private boolean dataTypeOK;
	private long nTotal;

	private boolean procTiltDist;
	private boolean procTwistDist;
	private boolean procSymDist;
	private boolean procImpropDist;	
	private boolean procTiltAngle;
	private boolean procTwistAngle;
	
	private boolean procMinTTC;
	private boolean procMaxTTC;
		
	private boolean procSymTTC;
	private boolean procImpropTTC;
		
	private boolean procDisTTC;
	
	private boolean procDisTiltAngle;
	private boolean procDisTwistAngle;
	
	private boolean procCommon;
	private boolean procExp;

	private double areaThr = INFTY;
	private JButton drawMisorBtn;
	private JButton planesFixedBtn;

	private Plot2DPanel plot = null;
	private String histFrameTitle = null; 
	private JLabel areaLbl;
	private JButton areaHistBtn;
	private JCheckBox areaThrChB;
	private JTextField areaThrFld;
	private JSeparator separator_2;
	private JLabel freqLbl;
	private JButton freqBtn;
	private JSeparator separator_1;
	private JLabel planesIndepLbl;
	private JButton planesIndepBtn;
	private JSeparator separator_3;
	private JLabel ttcLbl;
	private JButton ttcBtn;
	private JButton exportMisorBtn;
	private JLabel disTiltLbl;
	private JLabel disTwistLbl;
	private JButton disTiltBtn;
	private JButton disTwistBtn;
	private JLabel searchMaxLbl;
	private JButton searchMaxBtn;
	private JLabel astLbl;
	private JLabel secondGrainLbl;
	private JButton secondGrainBtn;
	private JLabel misorAxisLbl;
	private JButton misorAxisBtn;

	public StatisticalAnalysisPanel() {

		procTiltDist = false;
		procTwistDist = false;
		procSymDist = false;
		procImpropDist = false;	
		procTiltAngle = false;
		procTwistAngle = false;	
		
		procMinTTC = false;
		procMaxTTC = false;
		
		procSymTTC = false;
		procImpropTTC = false;
		
		procDisTTC = false;
		
		procDisTiltAngle = false;
		procDisTwistAngle = false;	
		
		
		procCommon = false;
		procExp = false;
		


		gbFiles = new ArrayList<GBDatHeader>();

		setLayout(new MigLayout("", "[][][]", "[][][][][][][][][][][][][][][][][][][]"));

		JLabel calcFreqLbl = new JLabel("<html><b>Open boundary data files and carry out quantitative analyses:</b>");
		add(calcFreqLbl, "cell 0 0 3 1");

		JLabel inputLbl = new JLabel("<html><u>Input <code>gbdat</code> file(s)</u>:");
		add(inputLbl, "cell 0 1,gapx 10,gapy 5");

		final JFileChooser inputFc = new JFileChooser(); 
		inputFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		inputFc.setAcceptAllFileFilterUsed(false);
		inputFc.addChoosableFileFilter(new FileUtils.GBDatFileFilter());
		inputFc.setMultiSelectionEnabled(true);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setToolTipText("");
		add(scrollPane, "cell 1 1 1 2,growx,gapy 5");

		list = new JList();
		list.setToolTipText("<html>A list of files that will be processed;<br>\r\nCircles preceeding the paths of files are filled if a given value is included in the file:<br>\r\n- 1st to 4th: approximate distances to the nearest tilt, twist, symmetric and improperly quasi-symmetric (180\u00B0-tilt) boundaries, respectively<br>\r\n- 5th to 8th: accurate distances determined via distance minimization<br>\r\n- 7th, 8th:  angles of tilt and twist components (from Fortes decomposition)<br>\r\n- 9th, 10th: angles of tilt and twist components (decomposition of <i>dis</i>orientations)<br>\r\n- 11th: TTC for <i>dis</i>orientations");
		scrollPane.setViewportView(list);

		scrollPane.setPreferredSize(new Dimension(500,120));

		listModel = new DefaultListModel();
		list.setModel(listModel);

		JButton addFileBtn = new JButton();
		addFileBtn.setToolTipText("Add file(s) to the list");
		addFileBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/add-icon.png")));
		addFileBtn.setMinimumSize(new Dimension(24,24));
		addFileBtn.setMaximumSize(new Dimension(24,24));
		addFileBtn.setPreferredSize(new Dimension(24,24));
		add(addFileBtn, "flowx,cell 2 1,aligny top,gapy 5");

		final JButton delFileBtn = new JButton();
		
		
		addFileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				int returnVal = inputFc.showOpenDialog(StatisticalAnalysisPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					
					final File[] fList = inputFc.getSelectedFiles();

					for(File f : fList) if(!f.isDirectory()) {

						// check if a given file has been already added
						boolean skip = false;
						for(GBDatHeader header : gbFiles) if(header.getPath().compareTo(f.getAbsolutePath()) == 0) {
							skip = true;
							break;
						}							
						if (skip) continue;

						//...if not
						try {
							final GBDatHeader newHeader = new GBDatHeader(f);
							
							System.out.println("Opening: " + f.getAbsolutePath() + "...");

							gbFiles.add(newHeader);

							final StringBuilder str = new StringBuilder();
							str.append('(');
							str.append(newHeader.getExpOrRand());
							str.append(", ");							
							str.append(newHeader.getPointGrpName());
							str.append(", ");

							str.append(GBDatHeader.isInFile(newHeader.containsMaxTTC()));
							str.append(GBDatHeader.isInFile(newHeader.containsMinTTC()));														
							str.append(GBDatHeader.isInFile(newHeader.containsSymTTC()));							
							str.append(GBDatHeader.isInFile(newHeader.containsImpropTTC()));			
							str.append('|');							
							str.append(GBDatHeader.isInFile(newHeader.containsTiltDist()));
							str.append(GBDatHeader.isInFile(newHeader.containsTwistDist()));
							str.append(GBDatHeader.isInFile(newHeader.containsSymDist()));
							str.append(GBDatHeader.isInFile(newHeader.containsImpropDist()));
							str.append('|');
							str.append(GBDatHeader.isInFile(newHeader.containsTiltAngle()));
							str.append(GBDatHeader.isInFile(newHeader.containsTwistAngle()));							
							str.append("|");
							str.append(GBDatHeader.isInFile(newHeader.containsDisorTTC()));
							str.append(GBDatHeader.isInFile(newHeader.containsDisTiltAngle()));
							str.append(GBDatHeader.isInFile(newHeader.containsDisTwistAngle()));
							
							str.append(", ");
							str.append(newHeader.getNumberOfGBs());
							str.append(") ");
							str.append(f.getAbsolutePath());

							listModel.add(listModel.getSize(), str);

							list.ensureIndexIsVisible(listModel.getSize() - 1);
							delFileBtn.setEnabled(true);
							
						} catch(IOException | NumberFormatException exc) {

							JOptionPane.showMessageDialog(null,
									"An I/O error occurred.",
									"Failed to open the files.",
									JOptionPane.ERROR_MESSAGE);
							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

							return;									
						}

					}
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}	
				checkContent();
			}
		});

		commentLbl = new JLabel("<html><font color=#0000cc<i>No file have been opened.</i></font>");
		add(commentLbl, "cell 0 3 3 1,gapx 10");

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 4 3 1,growx,aligny center,gapy 5 5");
		
		areaThrChB = new JCheckBox("<html><u>Omit mesh triangles with areas exceeding</u>:");
		areaThrChB.setToolTipText("By using this feature, one can eliminate huge triangles which sometimes appear due to errors in smoothing algorithms");
		areaThrChB.setEnabled(false);
		add(areaThrChB, "flowx,cell 0 6 3 1,gapx 20");
				
		JLabel misorLbl = new JLabel("<html><u>Preview the distribution of <i>dis</i>orientation angles</u>:");
		add(misorLbl, "flowx,cell 0 8 3 1,gapx 10");
		
		separator_1 = new JSeparator();
		add(separator_1, "cell 0 9 3 1,growx,aligny center,gapy 5 5");
		
		JLabel planesFixedLbl = new JLabel("<html><u>Distributions of boundary planes for fixed misorientations</u>:");
		add(planesFixedLbl, "flowx,cell 0 12 3 1,gapx 10");
		
		freqLbl = new JLabel("<html><u>Frequencies of occurrence of boundaries of particular geometries</u>:");
		add(freqLbl, "flowx,cell 0 10 3 1,gapx 10");
		
		separator_2 = new JSeparator();
		add(separator_2, "cell 0 7 3 1,growx,aligny center,gapy 5 5");

		delFileBtn.setEnabled(false);
		delFileBtn.setToolTipText("Remove file(s) from the list");
		delFileBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/remove.png")));
		add(delFileBtn, "cell 2 1,aligny top,gapy 5");
		delFileBtn.setMinimumSize(new Dimension(24,24));
		delFileBtn.setMaximumSize(new Dimension(24,24));
		delFileBtn.setPreferredSize(new Dimension(24,24));
		
		areaLbl = new JLabel("<html><u>Preview the distribution of areas of mesh triangles</u>:");
		areaLbl.setEnabled(false);
		add(areaLbl, "flowx,cell 0 5 3 1,gapx 10");
		
		areaHistBtn = new JButton();
		areaHistBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/bars.png")));
		areaHistBtn.setPreferredSize(new Dimension(24, 24));
		areaHistBtn.setMinimumSize(new Dimension(24, 24));
		areaHistBtn.setMaximumSize(new Dimension(24, 24));
		areaHistBtn.setEnabled(false);
		add(areaHistBtn, "cell 0 5 3 1");
		
		planesFixedBtn = new JButton();
		planesFixedBtn.setEnabled(false);
		planesFixedBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/pie_chart.png")));
		planesFixedBtn.setPreferredSize(new Dimension(24, 24));
		planesFixedBtn.setMinimumSize(new Dimension(24, 24));
		planesFixedBtn.setMaximumSize(new Dimension(24, 24));
		add(planesFixedBtn, "cell 0 12 3 1");

		drawMisorBtn = new JButton();
		drawMisorBtn.setEnabled(false);
		drawMisorBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/bars.png")));
		drawMisorBtn.setPreferredSize(new Dimension(24, 24));
		drawMisorBtn.setMinimumSize(new Dimension(24, 24));
		drawMisorBtn.setMaximumSize(new Dimension(24, 24));
		add(drawMisorBtn, "cell 0 8 3 1");
		
		freqBtn = new JButton();
		freqBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/pie_chart.png")));
		freqBtn.setPreferredSize(new Dimension(24, 24));
		freqBtn.setMinimumSize(new Dimension(24, 24));
		freqBtn.setMaximumSize(new Dimension(24, 24));
		freqBtn.setEnabled(false);
		add(freqBtn, "cell 0 10 3 1");
		
		areaThrFld = new JTextField();
		areaThrFld.setToolTipText("By using this feature, one can eliminate huge triangles which sometimes appear due to errors in smoothing algorithms");
		areaThrFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(areaThrFld, "cell 0 6 3 1");
		areaThrFld.setColumns(6);
		
		searchMaxLbl = new JLabel("<html><u>Search for maxima in the five-parameter boundary distribution</u>:");
		add(searchMaxLbl, "flowx,cell 0 11 3 1,gapx 10");
		
		planesIndepLbl = new JLabel("<html><u>Distributions of boundary planes independent of misorientations</u>:");
		add(planesIndepLbl, "flowx,cell 0 13 3 1,gapx 10");
		
		planesIndepBtn = new JButton();
		planesIndepBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/pie_chart.png")));
		planesIndepBtn.setPreferredSize(new Dimension(24, 24));
		planesIndepBtn.setMinimumSize(new Dimension(24, 24));
		planesIndepBtn.setMaximumSize(new Dimension(24, 24));
		planesIndepBtn.setEnabled(false);
		add(planesIndepBtn, "cell 0 13 3 1");
		
		secondGrainLbl = new JLabel("<html><u>Distributions of boundary planes in the second grain for a fixed plane in the first grain</u>:");
		add(secondGrainLbl, "flowx,cell 0 14 3 1,gapx 10");
		
		separator_3 = new JSeparator();
		add(separator_3, "cell 0 15 3 1,growx,aligny center,gapy 5 5");
		
		ttcLbl = new JLabel("<html><u>Distributions of TTC parameter for <i>dis</i>orientations</u>:");
		ttcLbl.setEnabled(false);
		add(ttcLbl, "flowx,cell 0 16 3 1,gapx 10");
		
		ttcBtn = new JButton();
		ttcBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/bars.png")));
		ttcBtn.setPreferredSize(new Dimension(24, 24));
		ttcBtn.setMinimumSize(new Dimension(24, 24));
		ttcBtn.setMaximumSize(new Dimension(24, 24));
		ttcBtn.setEnabled(false);
		add(ttcBtn, "cell 0 16 3 1");
		
		exportMisorBtn = new JButton();
		exportMisorBtn.setToolTipText("<html>Export <i>dis</i>orientation angles to a file");
		exportMisorBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/export.png")));
		exportMisorBtn.setPreferredSize(new Dimension(24, 24));
		exportMisorBtn.setMinimumSize(new Dimension(24, 24));
		exportMisorBtn.setMaximumSize(new Dimension(24, 24));
		exportMisorBtn.setEnabled(false);
		add(exportMisorBtn, "cell 0 8 3 1");
		
		disTiltLbl = new JLabel("<html><u>Distribution of tilt angle (from decomposition of <i>dis</i>orientations</u>):");
		disTiltLbl.setEnabled(false);
		add(disTiltLbl, "flowx,cell 0 17 3 1,gapx 10");
		
		disTwistLbl = new JLabel("<html><u>Distributions of twist angle (from decomposition of <i>dis</i>orientations</u>):");
		disTwistLbl.setEnabled(false);
		add(disTwistLbl, "flowx,cell 0 18 3 1,gapx 10");
		
		disTiltBtn = new JButton();
		disTiltBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/bars.png")));
		disTiltBtn.setPreferredSize(new Dimension(24, 24));
		disTiltBtn.setMinimumSize(new Dimension(24, 24));
		disTiltBtn.setMaximumSize(new Dimension(24, 24));
		disTiltBtn.setEnabled(false);
		add(disTiltBtn, "cell 0 17 3 1");
		
		disTwistBtn = new JButton();
		disTwistBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/bars.png")));
		disTwistBtn.setPreferredSize(new Dimension(24, 24));
		disTwistBtn.setMinimumSize(new Dimension(24, 24));
		disTwistBtn.setMaximumSize(new Dimension(24, 24));
		disTwistBtn.setEnabled(false);
		add(disTwistBtn, "cell 0 18 3 1");
		
		searchMaxBtn = new JButton();
		searchMaxBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/maximum.png")));
		searchMaxBtn.setPreferredSize(new Dimension(24, 24));
		searchMaxBtn.setMinimumSize(new Dimension(24, 24));
		searchMaxBtn.setMaximumSize(new Dimension(24, 24));
		searchMaxBtn.setEnabled(false);
		add(searchMaxBtn, "cell 0 11 3 1");
		
		astLbl = new JLabel("<html><font color=#0000cc>(*)</font>");
		astLbl.setEnabled(false);
		astLbl.setToolTipText("<html><font color=#0000cc>Moving Finite Element (MFE) smoothing of DREAM.3D\r\nsometimes leads to huge artificial mesh segments.<br>\r\nIf there are only a couple of them, they can be eliminated using this tool.</font>");
		add(astLbl, "cell 0 6 3 1");
		
		secondGrainBtn = new JButton();
		secondGrainBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/pie_chart.png")));
		secondGrainBtn.setPreferredSize(new Dimension(24, 24));
		secondGrainBtn.setMinimumSize(new Dimension(24, 24));
		secondGrainBtn.setMaximumSize(new Dimension(24, 24));
		secondGrainBtn.setEnabled(false);
		add(secondGrainBtn, "cell 0 14 3 1");
		
		misorAxisLbl = new JLabel("<html><u>Distribution of misorientation axes</u>:");
		//add(misorAxisLbl, "cell 0 8 3 1,gapx 40");
		
		misorAxisBtn = new JButton();
		misorAxisBtn.setIcon(new ImageIcon(StatisticalAnalysisPanel.class.getResource("/gui_bricks/pie_chart.png")));
		misorAxisBtn.setPreferredSize(new Dimension(24, 24));
		misorAxisBtn.setMinimumSize(new Dimension(24, 24));
		misorAxisBtn.setMaximumSize(new Dimension(24, 24));
		misorAxisBtn.setEnabled(false);
	//	add(misorAxisBtn, "cell 0 8");
		
		misorAxisBtn.addActionListener(new ActionListener() { 
			
			@Override
			public void actionPerformed(ActionEvent evt) {
			
				if(areaThrChB.isSelected()) {
					
					try {
						areaThr = Double.parseDouble(areaThrFld.getText().replace(",", "."));
						if(areaThr < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Limit for mesh triangle area must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
				}
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {							
							final Stats_MisorAxesFrame fr = new Stats_MisorAxesFrame(gbFiles, areaThrChB.isSelected(), areaThr);
							fr.setVisible(true);							
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				});
				
			}
		});
		
		
		exportMisorBtn.addActionListener(new ActionListener() { // TODO
			@Override
			public void actionPerformed(ActionEvent evt) {
															
				final boolean omit = areaThrChB.isSelected();
				
				if(omit) {
					try {
						String s = areaThrFld.getText().replace(",", ".");
						areaThr = Double.parseDouble(s);
						if(areaThr <= 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"The threshold for mesh triangle area must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
									
									
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							
							JFrame frame = new ExportDisorAngles(gbFiles, omit, areaThr);
							
							frame.setVisible(true);
							
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				});
				
				
			}
		});
				
		drawMisorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showMisorHist();
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							ImageObserver.ERROR);	
					return;
				}
			}
		});
		
		
		planesFixedBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				if(areaThrChB.isSelected()) {
				
					try {
						areaThr = Double.parseDouble(areaThrFld.getText().replace(",", "."));
						if(areaThr < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Limit for mesh triangle area must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
				}
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {							
							final Stats_PlanesFixedMisorFrame fr = new Stats_PlanesFixedMisorFrame(gbFiles, areaThrChB.isSelected(), areaThr);
							fr.setVisible(true);							
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				});
			}			
		});
		
		freqBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				if(areaThrChB.isSelected()) {
				
					try {
						areaThr = Double.parseDouble(areaThrFld.getText().replace(",", "."));
						if(areaThr < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Limit for mesh triangle area must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
				}
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {							
							final Stats_FrequenciesFrame fr = new Stats_FrequenciesFrame(
									gbFiles, areaThrChB.isSelected(), areaThr,
									procTiltDist, procTwistDist, procSymDist, procImpropDist,
									procTiltAngle, procTwistAngle, procMaxTTC, procMinTTC,
									procSymTTC, procImpropTTC									
									);
							fr.setVisible(true);							
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				});
			}			
		});
		
		
		searchMaxBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				if(areaThrChB.isSelected()) {
				
					try {
						areaThr = Double.parseDouble(areaThrFld.getText().replace(",", "."));
						if(areaThr < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Limit for mesh triangle area must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
				}
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {							
							final Stats_SearchMaxima fr = new Stats_SearchMaxima(gbFiles, areaThrChB.isSelected(), areaThr, nTotal);
							fr.setVisible(true);							
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				});
			}			
		});
		
		
		secondGrainBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				if(areaThrChB.isSelected()) {
				
					try {
						areaThr = Double.parseDouble(areaThrFld.getText().replace(",", "."));
						if(areaThr < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Limit for mesh triangle area must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
				}
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {							
							final Stats_SecondGrainFrame fr = new Stats_SecondGrainFrame (gbFiles, areaThrChB.isSelected(), areaThr, nTotal);
							fr.setVisible(true);							
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				});
			}			
		});
		
		
		
		planesIndepBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				if(areaThrChB.isSelected()) {
				
					try {
						areaThr = Double.parseDouble(areaThrFld.getText().replace(",", "."));
						if(areaThr < 0d) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Limit for mesh triangle area must be a positive decimal number.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;					
					}
				}
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {							
							final Stats_PlanesIndepMisorFrame fr = new Stats_PlanesIndepMisorFrame(gbFiles, areaThrChB.isSelected(), areaThr);
							fr.setVisible(true);							
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
					}
				});
			}			
		});
		
		areaHistBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showAreaHist();
				} catch(Exception e) {
					
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);	
					return;
					
				}
			}
		});

		delFileBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

				DefaultListModel dlm = (DefaultListModel) list.getModel();

				if(list.getSelectedIndices().length > 0) {
					int[] tmp = list.getSelectedIndices();
					int[] selectedIndices = list.getSelectedIndices();

					for (int i = tmp.length-1; i >=0; i--) {
						selectedIndices = list.getSelectedIndices();
						dlm.removeElementAt(selectedIndices[i]);
						gbFiles.remove(selectedIndices[i]);
					} // end-for
				} // end-if

				if(dlm.getSize() == 0) {
					delFileBtn.setEnabled(false);
				}
				checkContent();
			}
		});
		
		
		ttcBtn.addActionListener(new ActionListener() { //TODO
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				try {
					showDisHist(1);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							ImageObserver.ERROR);	
					return;
				}
				
			}			
		});
		
		disTiltBtn.addActionListener(new ActionListener() { //TODO
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				try {
					showDisHist(2);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							ImageObserver.ERROR);	
					return;
				}
				
			}			
		});
		
		disTwistBtn.addActionListener(new ActionListener() { //TODO
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				try {
					showDisHist(3);
				} catch(Exception e) { 
					e.printStackTrace();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,
							"An error occurred. The histogram may be empty.",
							"Error",
							ImageObserver.ERROR);	
					return;
				}
				
			}			
		});

	}


	private final void checkContent() {

		if(gbFiles.size() > 0) {

			// EXP/RAND
			dataTypeOK = true;
			boolean exp = gbFiles.get(0).isExperimental();
			for(GBDatHeader head : gbFiles) if(head.isExperimental() != exp) {
				dataTypeOK = false;
				break;
			}

			if(dataTypeOK == false) {
				
				procTiltDist = false;
				procTwistDist = false;
				procSymDist = false;
				procImpropDist = false;	
				procTiltAngle = false;
				procTwistAngle = false;
				
				procDisTiltAngle = false;
				procDisTwistAngle = false;
				
				procMinTTC = false;
				procMaxTTC = false;
				procSymTTC = false;
				procImpropTTC = false;
				procDisTTC = false;
				
				procCommon = false;
				procExp = false;

				commentLbl.setText("<html><font color=#cc0000><b>Error: you must not mix randomly generated boundaries with experimentally measured data</b>.</font>");

				refreshProc();
				return;
			}

			// POINT GROUP			
			ptGrpOK = true;
			PointGroup ptGrp = gbFiles.get(0).getPointGrp();
			for(GBDatHeader head : gbFiles) if(head.getPointGrp() != ptGrp) {
				ptGrpOK = false;
				break;
			}

			if(ptGrpOK == false) {
				
				procTiltDist = false;
				procTwistDist = false;
				procSymDist = false;
				procImpropDist = false;	
				procTiltAngle = false;
				procTwistAngle = false;
				
				procDisTiltAngle = false;
				procDisTwistAngle = false;
				procCommon = false;
				procExp = false;
				
				procMinTTC = false;
				procMaxTTC = false;
				procSymTTC = false;
				procImpropTTC = false;
				procDisTTC = false;

				commentLbl.setText("<html><font color=#cc0000><b>Error: you must not mix data of materials of different point groups</b>.</font>");

				refreshProc();
				return;
			}
		
			// CONTENT
			procTiltDist = true;
			procTwistDist = true;
			procSymDist = true;
			procImpropDist = true;	
			procTiltAngle = true;
			procTwistAngle = true;
			procDisTiltAngle = true;
			procDisTwistAngle = true;
			procMinTTC = true;
			procMaxTTC = true;
			procSymTTC = true;
			procImpropTTC = true;
			procDisTTC = true;

			procCommon = true;
			procExp = gbFiles.get(0).isExperimental();

			nTotal = 0;

			for(GBDatHeader head : gbFiles) {

				nTotal += head.getNumberOfGBs();

				if(!head.containsTiltDist()) procTiltDist = false;
				if(!head.containsTwistDist()) procTwistDist = false;
				if(!head.containsSymDist()) procSymDist = false;
				if(!head.containsImpropDist()) procImpropDist = false;
				if(!head.containsTiltAngle()) procTiltAngle = false;
				if(!head.containsTwistAngle()) procTwistAngle = false;							
				
				if(!head.containsMinTTC()) procMinTTC = false;
				if(!head.containsMaxTTC()) procMaxTTC = false;
				
				if(!head.containsSymTTC()) procSymTTC = false;
				if(!head.containsImpropTTC()) procImpropTTC = false;
				
				if(!head.containsDisorTTC()) procDisTTC = false;
				
				if(!head.containsDisTiltAngle()) procDisTiltAngle = false;
				if(!head.containsDisTwistAngle()) procDisTwistAngle = false;	
			}

			refreshProc();

			commentLbl.setText("<html><font color=#0000cc>The total number of boundaries is <b>" + nTotal + "</b>. Note the differences in the contents of the files.</font>");


		} else { // i.e. gbFiles.size() < 0
			
			commentLbl.setText("<html><font color=#0000cc<i>No file have been opened.</i></font>");
			
			procTiltDist = false;
			procTwistDist = false;
			procSymDist = false;
			procImpropDist = false;	
			procTiltAngle = false;
			procTwistAngle = false;
			
			procMinTTC = false;
			procMaxTTC = false;
			
			procSymTTC = false;
			procImpropTTC = false;
			
			procDisTTC = false;
			
			procDisTiltAngle = false;
			procDisTwistAngle = false;
			
			procCommon = false;
			procExp = false;
			
			refreshProc();
		}



	}

	private final void refreshProc() {

		if(!procCommon) {
			drawMisorBtn.setEnabled(false);
			exportMisorBtn.setEnabled(false);
			misorAxisBtn.setEnabled(false);
			planesFixedBtn.setEnabled(false);
			freqBtn.setEnabled(false);
			planesIndepBtn.setEnabled(false);
			secondGrainBtn.setEnabled(false);
			searchMaxBtn.setEnabled(false);
			
			planesFixedBtn.setToolTipText(null);
			planesIndepBtn.setToolTipText(null);
			searchMaxBtn.setToolTipText(null);
			secondGrainBtn.setToolTipText(null);
			
			
		} else {
			
			if(gbFiles.get(0).getPointGrp() != PointGroup._2M && 
					gbFiles.get(0).getPointGrp() != PointGroup._3M && 
					gbFiles.get(0).getPointGrp() != PointGroup._1) {
					planesFixedBtn.setEnabled(true);
					planesIndepBtn.setEnabled(true);
					searchMaxBtn.setEnabled(true);
					secondGrainBtn.setEnabled(true);
					
					planesFixedBtn.setToolTipText(null);
					planesIndepBtn.setToolTipText(null);
					searchMaxBtn.setToolTipText(null);
					secondGrainBtn.setToolTipText(null);
			} else {
				planesFixedBtn.setEnabled(false);
				planesIndepBtn.setEnabled(false);
				searchMaxBtn.setEnabled(false);
				secondGrainBtn.setEnabled(false);
				
				planesFixedBtn.setToolTipText("This feature does not work for trigonal, monoclinic, and triclinic symmetries");
				planesIndepBtn.setToolTipText("This feature does not work for trigonal, monoclinic, and triclinic symmetries");
				searchMaxBtn.setToolTipText("This feature does not work for trigonal, monoclinic, and triclinic symmetries");
				secondGrainBtn.setToolTipText("This feature does not work for trigonal, monoclinic, and triclinic symmetries");
				
			}
						
			freqBtn.setEnabled(true);
			drawMisorBtn.setEnabled(true);
			exportMisorBtn.setEnabled(true);
			misorAxisBtn.setEnabled(true);

			

		}
		
		if(!procExp) {
			
			areaHistBtn.setEnabled(false);
			areaLbl.setEnabled(false);
			
			areaThrChB.setSelected(false);
			areaThrChB.setEnabled(false);
			astLbl.setEnabled(false);
			
		} else {
			
			areaHistBtn.setEnabled(true);
			areaLbl.setEnabled(true);			
			areaThrChB.setEnabled(true);
			astLbl.setEnabled(true);

		}	
		
		if(!procDisTTC) {
			
			ttcBtn.setEnabled(false);
			ttcLbl.setEnabled(false);
						
		} else {
			
			ttcBtn.setEnabled(true);
			ttcLbl.setEnabled(true);	
		}	
		
		
		if(!procDisTiltAngle) {
			
			disTiltBtn.setEnabled(false);
			disTiltLbl.setEnabled(false);
						
		} else {
			
			disTiltBtn.setEnabled(true);
			disTiltLbl.setEnabled(true);
		}
		
		
		if(!procDisTwistAngle) {
			
			disTwistBtn.setEnabled(false);
			disTwistLbl.setEnabled(false);
						
		} else {
			
			disTwistBtn.setEnabled(true);
			disTwistLbl.setEnabled(true);
		}
		
			
	}
	
	
	
	private final void showMisorHist() throws IOException, NumberFormatException {
		
		final boolean omit = areaThrChB.isSelected();
		
		if(omit) {
			try {
				String s = areaThrFld.getText().replace(",", ".");
				areaThr = Double.parseDouble(s);
				if(areaThr <= 0d) throw new NumberFormatException();
			} catch(NumberFormatException exc) {
				JOptionPane.showMessageDialog(null,
						"The threshold for mesh triangle area must be a positive decimal number.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		final Matrix3x3[] setC = Transformations.getSymmetryTransformations(gbFiles.get(0).getPointGrp());
		
		final ArrayList<Double> angles = new ArrayList<Double>();
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		Iterator<GBDatHeader> iterator = gbFiles.iterator();
		
		while(iterator.hasNext()) {

			GBDatHeader header = iterator.next();			
			final BufferedReader in = new BufferedReader(new FileReader(header.getPath()));
			
			GBDatHeader.skipHeaderLines(in);
			
			String line = null;
			
			while ((line = in.readLine()) != null) {

				final String[] num = line.trim().split("\\s+");
					
				double area = INFTY;
				
				if(omit) area = Double.parseDouble(num[9]);
				
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
						
					for(Matrix3x3 C : setC) { 
						
						final Matrix3x3 CM = new Matrix3x3(M);
						CM.leftMul(C);
						final double omega = CM.rotationAngle();
						if(omega < omegaMin) omegaMin = omega;
					}
				
					angles.add( Math.toDegrees(omegaMin) );
				}
			}			
		}
		
		double[] anglesArr = new double[angles.size()];
		int index = -1;
		for(Double d : angles) {
			index++;
			anglesArr[index] = d;
		}
				
		plot = new Plot2DPanel();
		histFrameTitle = "Disorientation angles";
		final double max = Math.ceil(Array.max(anglesArr));
		plot.addHistogramPlot(histFrameTitle, Color.red, anglesArr, 0d, max, (int)max); 
		
		plot.setAxisLabels("\u03C9", "Counts");
		plot.addLegend("SOUTH");
							
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					
					JFrame frame = new JFrame("GBToolbox: " + histFrameTitle);
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage(StatisticalAnalysisPanel.class.getResource("/gui_bricks/gbtoolbox.png")));

					frame.setSize(600, 600);
					frame.setContentPane(plot);
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		});
		
		setCursor(Cursor.getDefaultCursor());
	}
	
	
	private final void showDisHist(int mode) throws IOException, NumberFormatException {
		
		final boolean omit = areaThrChB.isSelected();
		
		if(omit) {
			try {
				String s = areaThrFld.getText().replace(",", ".");
				areaThr = Double.parseDouble(s);
				if(areaThr <= 0d) throw new NumberFormatException();
			} catch(NumberFormatException exc) {
				JOptionPane.showMessageDialog(null,
						"The threshold for mesh triangle area must be a positive decimal number.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
				
		final ArrayList<Double> data = new ArrayList<Double>();		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
		Iterator<GBDatHeader> iterator = gbFiles.iterator();
		
		while(iterator.hasNext()) {

			GBDatHeader header = iterator.next();			
			final BufferedReader in = new BufferedReader(new FileReader(header.getPath()));
			
			GBDatHeader.skipHeaderLines(in);
			
			String line = null;
			
			while ((line = in.readLine()) != null) {

				final String[] num = line.trim().split("\\s+");
					
				double area = INFTY;
				
				if(omit) area = Double.parseDouble(num[9]);
				
				if(!omit || (omit && area <= areaThr)) {
					
					int pos = 8;
					
					if(header.isExperimental()) {
						pos++;
						pos++;
					}

					if(header.containsTiltDist()) {
						pos++;
					}

					if(header.containsTwistDist()) {
						pos++;
					}

					if(header.containsSymDist()) {
						pos++;
					}

					if(header.containsImpropDist()) {										
						pos++;
					}

					if(header.containsTiltAngle()) {
						pos++;
					}

					if(header.containsTwistAngle()) {
						pos++;
					}
					
					if(header.containsMinTTC()) {
						pos++;
					}
					
					if(header.containsMaxTTC()) {
						pos++;
					}
					

					if(mode > 1) {
						if(header.containsDisorTTC()) {
							pos++;
						}
					}
					
					if(mode > 2) {
						if(header.containsDisTiltAngle()) {
							pos++;
						}
					}

					final double nextVal = Double.parseDouble(num[pos]);
				
					data.add(nextVal );
				}
			}			
		}
		
		double[] dataArr = new double[data.size()];
		int index = -1;
		for(Double d : data) {
			index++;
			dataArr[index] = d;
		}
				
		plot = new Plot2DPanel();
		String angleLabel = "";
		switch(mode) {
			case 1: histFrameTitle = "TTC parameter (disorientations)"; angleLabel = "\u03b1"; break;
			case 2: histFrameTitle = "Tilt angles (disorientations)"; angleLabel = "\u03bb"; break;
			case 3: histFrameTitle = "Twist angles (disorientations)"; angleLabel = "\u03bd"; break;
		}
		final double max = Math.ceil(Array.max(dataArr));
		plot.addHistogramPlot(histFrameTitle, Color.red, dataArr, 0d, max, (int)max);
		
		plot.setAxisLabels(angleLabel, "Counts");
		plot.addLegend("SOUTH");
							
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					
					JFrame frame = new JFrame("GBToolbox: " + histFrameTitle);
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage(StatisticalAnalysisPanel.class.getResource("/gui_bricks/gbtoolbox.png")));

					frame.setSize(600, 600);
					frame.setContentPane(plot);
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		});
		
		setCursor(Cursor.getDefaultCursor());
	}
	
	
	private final void showAreaHist() throws IOException {
		
		final double[] area = new double[(int)nTotal];//TODO
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		double avg = 0d;
		
		int index = -1;
		Iterator<GBDatHeader> iterator = gbFiles.iterator();
		while(iterator.hasNext()) {

			GBDatHeader header = iterator.next();			
			final BufferedReader in = new BufferedReader(new FileReader(header.getPath()));
			
			GBDatHeader.skipHeaderLines(in);
			
			String line = null;
			
			while ((line = in.readLine()) != null) {
				
				final String[] num = line.trim().split("\\s+");
								
				final double A = Double.parseDouble(num[9]);

				index++;
				area[index] = A;
				avg += A;
			}			
		}
				
		plot = new Plot2DPanel();
		histFrameTitle = "Area of mesh triangles";
		
		avg = avg / (index+1);
		final double max = 5 * avg;
		
		plot.addHistogramPlot(histFrameTitle, Color.red, area, 0d, max, 50);
		
		plot.setAxisLabels("Area", "Counts");
		plot.addLegend("SOUTH");
						
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JFrame frame = new JFrame("GBToolbox: " + histFrameTitle);
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage(StatisticalAnalysisPanel.class.getResource("/gui_bricks/gbtoolbox.png")));

					frame.setSize(600, 600);
					frame.setContentPane(plot);
					frame.setVisible(true);
					
				} catch (Exception e) {
					
					e.printStackTrace();
					return;
				}
			}
		});
		
		setCursor(Cursor.getDefaultCursor());
	}

}
