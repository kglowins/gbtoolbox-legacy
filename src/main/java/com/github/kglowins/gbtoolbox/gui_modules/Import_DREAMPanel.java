package com.github.kglowins.gbtoolbox.gui_modules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;




import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import net.miginfocom.swing.MigLayout;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.UnitVector;

import javax.swing.JCheckBox;

public class Import_DREAMPanel extends JPanel {

	private JTextField outFld;

	private JButton processBtn;

	private final DecimalFormat df4;
	private final DecimalFormat df7;

	private JTextField nodesFld;
	private JTextField triFld;
	private JTextField voxelFld;
	private JTextField dirFld;
	private JTextField maxGbFld;

	private JButton abortBtn;

	private JLabel statusLbl;
	
	private int maxPerFile;
	private String outDir;
	private String outPrefix;
	
	private JCheckBox splitChB;
	private JCheckBox simplifyChB;
	
	private Importer task;
	private JTextField reduceFld;
	
	private JLabel reduceLbl;
	private JLabel reduce2Lbl;
	
	private JCheckBox vtkNetChB;
	private JCheckBox vtkGrainsChB;
	private JLabel minLbl;
	private JTextField minFld;
	private JLabel lblNewLabel;
	private JCheckBox noSurfGrainsChB;
	private JCheckBox tripleChB;
	
	
	//private IndividualGBTester tester;
	

	public Import_DREAMPanel() {
		
		//TODO
		
	//	tester = new IndividualGBTester();
	//	tester.setSymmetriesInvolved(true, true);
	//	tester.useTTC(true);
		
		

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df7 = new DecimalFormat("0.#######", otherSymbols);				 
		df4 = new DecimalFormat("0.####", otherSymbols);

		setLayout(new MigLayout("insets 0", "[][grow]", "[][][][][][][][][][][][][][][][][][][][grow]"));

		JLabel nodeLbl = new JLabel("<html><u>Nodes data file</u> <font color=#0000cc><sup>(1)</sup></font>:");
		add(nodeLbl, "flowx,cell 0 0,gapx 10");

		nodesFld = new JTextField();
		nodesFld.setColumns(18);
		add(nodesFld, "flowx,cell 1 0");

		JLabel triLbl = new JLabel("<html><u>Triangles data file</u> <font color=#0000cc><sup>(1)</sup></font>:");
		add(triLbl, "flowx,cell 0 1,gapx 10");

		triFld = new JTextField();
		triFld.setColumns(18);
		add(triFld, "flowx,cell 1 1");

		JLabel voxelLbl = new JLabel("<html><u>DREAM.3D data file (after clean-up and with average orientations calculated)</u> <font color=#0000cc><sup>(2)</sup></font>:");
		add(voxelLbl, "flowx,cell 0 2,gapx 10");

		voxelFld = new JTextField();
		voxelFld.setColumns(18);
		add(voxelFld, "flowx,cell 1 2");
		
		noSurfGrainsChB = new JCheckBox("<html><u>Do not include boundaries between grains lying on (cut by) the sample surface</u><font color=#0000cc><sup>(3)</sup></font>");
		add(noSurfGrainsChB, "cell 0 3 2 1,gapx 10");
		
		tripleChB = new JCheckBox("<html><u>Do not include mesh triangles neighboring triple lines (triple points) and sample surface</u>");
		tripleChB.setSelected(true);
		add(tripleChB, "cell 0 4,gapx 10");
		
		lblNewLabel = new JLabel("<html><small><font color=#0000cc><sup>(1)</sup> To obtain node and triangle data files, 'IO / Write Nodes/Triangles from Surface Mesh' filter of DREAM.3D must be used.<br>\r\n<sup>(2)</sup> .dream3d data file must contain grain orientations computed by\r\n'Statistics / Find Field Average Oreintations' filter.<br><sup>(3)</sup> To use this option, you must first use 'Generic / Find Surface Fields' filter of DREAM.3D.</small>\r\n\r\n");
		add(lblNewLabel, "cell 0 5 2 1,gapx 10,gapy 5");
		
		JSeparator separator_2 = new JSeparator();
		add(separator_2, "cell 0 6 2 1,growx,aligny center,gapy 5 5");
		
		simplifyChB = new JCheckBox("<html><b>Use mesh simplification (runs QSlim program by M. Garland in batch):</b>");
		simplifyChB.setToolTipText("<html><font color=#cc0000>Note: <i>QSlim</i> executables must be copied to <i>GBToolbox</i> working directory.</font><br>\r\n<i>QSlim</i> can be downloaded from:<br>\r\nhttp://mgarland.org/software/qslim.html");
		add(simplifyChB, "cell 0 7 2 1,gapx 10");
		
		reduceLbl = new JLabel("- Reduce the number of mesh triangles to");
		reduceLbl.setEnabled(false);
		add(reduceLbl, "flowx,cell 0 8 2 1,gapx 30");
		
		minLbl = new JLabel("- At the same time, set the minimum number of triangles per an individual boundary to");
		minLbl.setEnabled(false);
		add(minLbl, "flowx,cell 0 9 2 1,gapx 30");
		
		vtkNetChB = new JCheckBox("<html>Write a .vtk file with the simplified boundary network (boundary IDs only)");
		vtkNetChB.setToolTipText("This file will be saved in the output directory.");
		vtkNetChB.setEnabled(false);
		vtkNetChB.setSelected(true);
		add(vtkNetChB, "cell 0 10 2 1,gapx 30");
		
		vtkGrainsChB = new JCheckBox("<html>Write a .vtk file with simplified grains (boundary IDs & grain IDs; \r\nthere are two segments with the same boundary ID)");
		vtkGrainsChB.setToolTipText("This file will be saved in the output directory.");
		vtkGrainsChB.setEnabled(false);
		vtkGrainsChB.setSelected(true);
		add(vtkGrainsChB, "cell 0 11 2 1,gapx 30");

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 12 2 1,growx,aligny center,gapy 5 5");

		JLabel lblDirectoryForOutput = new JLabel("<html><u>Directory for output <code>gbdat</code> file(s)</u>:");
		add(lblDirectoryForOutput, "flowx,cell 0 13,gapx 10");

		dirFld = new JTextField();
		add(dirFld, "flowx,cell 1 13");
		dirFld.setColumns(18);

		JLabel lblOutputFile = new JLabel("<html><u>Output file name (or name prefix)</u>:");
		lblOutputFile.setToolTipText("<html>If you do not split data into multiple files, it will be the name of the output file.<br>\r\nOtherwise, it will be used as a prefix for the files and subsequent indices will be attached to this prefix automatically.");
		add(lblOutputFile, "cell 0 14,gapx 10");

		outFld = new JTextField();
		outFld.setToolTipText("<html>If you do not split data into multiple files, it will be the name of the output file.<br>");
		add(outFld, "flowx,cell 1 14");
		outFld.setColumns(18);

		splitChB = new JCheckBox("<html>Split data into multiple files with the maximum number of mesh segments per file set to:");
		add(splitChB, "flowx,cell 0 15,gapx 10 20");

		splitChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox) evt.getSource();
				if(src.isSelected()) maxGbFld.setEnabled(true);
				else maxGbFld.setEnabled(false);				
			}			
		});

		maxGbFld = new JTextField();
		maxGbFld.setHorizontalAlignment(SwingConstants.RIGHT);
		maxGbFld.setText("200000");
		add(maxGbFld, "cell 1 15");
		maxGbFld.setColumns(7);

		JSeparator separator_1 = new JSeparator();
		add(separator_1, "cell 0 16 2 1,growx,aligny center,gapy 5 5");

		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		JButton nodesBtn = new JButton();
		nodesBtn.setIcon(new ImageIcon(Import_DREAMPanel.class.getResource("/gui_bricks/folder.png")));
		nodesBtn.setPreferredSize(new Dimension(24, 24));
		nodesBtn.setMinimumSize(new Dimension(24, 24));
		nodesBtn.setMaximumSize(new Dimension(24, 24));
		add(nodesBtn, "cell 1 0,gapy 5");

		JButton triBtn = new JButton();
		triBtn.setIcon(new ImageIcon(Import_DREAMPanel.class.getResource("/gui_bricks/folder.png")));
		triBtn.setPreferredSize(new Dimension(24, 24));
		triBtn.setMinimumSize(new Dimension(24, 24));
		triBtn.setMaximumSize(new Dimension(24, 24));
		add(triBtn, "cell 1 1");

		JButton voxelBtn = new JButton();
		voxelBtn.setIcon(new ImageIcon(Import_DREAMPanel.class.getResource("/gui_bricks/folder.png")));
		voxelBtn.setPreferredSize(new Dimension(24, 24));
		voxelBtn.setMinimumSize(new Dimension(24, 24));
		voxelBtn.setMaximumSize(new Dimension(24, 24));
		add(voxelBtn, "cell 1 2");

		JButton dirBtn = new JButton();
		dirBtn.setIcon(new ImageIcon(Import_DREAMPanel.class.getResource("/gui_bricks/folder.png")));
		dirBtn.setPreferredSize(new Dimension(24, 24));
		dirBtn.setMinimumSize(new Dimension(24, 24));
		dirBtn.setMaximumSize(new Dimension(24, 24));
		add(dirBtn, "cell 1 13");

		processBtn = new JButton("Import");
		add(processBtn, "flowx,cell 1 17,alignx right,gapx 20");

		processBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				File fTmp;

				fTmp = new File(nodesFld.getText());
				if(!fTmp.exists()) {

					JOptionPane.showMessageDialog(null,
							"Nodes data file does not exist.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}

				fTmp = new File(triFld.getText());
				if(!fTmp.exists()) {

					JOptionPane.showMessageDialog(null,
							"Triangles data file does not exist.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}


				fTmp = new File(voxelFld.getText());
				if(!fTmp.exists()) {

					JOptionPane.showMessageDialog(null,
							"DREAM.3D data file does not exist.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}


				outDir = dirFld.getText();
				outPrefix = outFld.getText();

				fTmp = new File(outDir);								

				if(!fTmp.exists() || !fTmp.isDirectory()) {

					JOptionPane.showMessageDialog(null,
							"Directory selected for output files does not exist.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}

				if(outPrefix.length() < 1) {

					JOptionPane.showMessageDialog(null,
							"Output file name (or name prefix) cannot be empty.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}

				maxPerFile = Integer.MAX_VALUE;

				if(splitChB.isSelected()) {
					try {
						maxPerFile = Integer.parseInt(maxGbFld.getText());
						if(maxPerFile < 0) throw new NumberFormatException();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Maximum number of boundaries stored in one file should be a positive integer.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				processBtn.setEnabled(false);
				abortBtn.setEnabled(true);
				
				task = new Importer();
				task.execute();
	
			}

		});

		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setEnabled(false);
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(Import_DREAMPanel.class.getResource("/gui_bricks/abort.png")));
		add(abortBtn, "cell 1 17,alignx right");

		JLabel commentLbl = new JLabel("<html><small><font color=cc0000><b>Warning: all existing files will be overwritten!</b></font></small>");
		add(commentLbl, "cell 0 18 2 1,aligny top,gapy 5");

		statusLbl = new JLabel("<html>&nbsp;");
		add(statusLbl, "cell 0 19 2 1,aligny bottom");
		
		reduceFld = new JTextField();
		reduceFld.setEnabled(false);
		reduceFld.setHorizontalAlignment(SwingConstants.RIGHT);
		reduceFld.setText("25");
		add(reduceFld, "cell 0 8 2 1");
		reduceFld.setColumns(3);
		
		reduce2Lbl = new JLabel("% of the initial number");
		reduce2Lbl.setEnabled(false);
		add(reduce2Lbl, "cell 0 8 2 1");
		
		minFld = new JTextField();
		minFld.setEnabled(false);
		minFld.setText("50");
		minFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(minFld, "cell 0 9");
		minFld.setColumns(3);

		dirBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnVal = fc.showSaveDialog(Import_DREAMPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = fc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					dirFld.setText(fName);
				}							
			}
		});

		voxelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				//fc.setAcceptAllFileFilterUsed(false);
				//fc.addChoosableFileFilter(h5voxelFilter);

				int returnVal = fc.showDialog(Import_DREAMPanel.this, "Import");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					voxelFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
				//fc.removeChoosableFileFilter(h5voxelFilter);
				//fc.setAcceptAllFileFilterUsed(true);
			}
		});

		triBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				//	fc.setAcceptAllFileFilterUsed(false);
				//	fc.addChoosableFileFilter(binFilter);

				int returnVal = fc.showDialog(Import_DREAMPanel.this, "Import");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					triFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
				//	fc.removeChoosableFileFilter(binFilter);
				//	fc.setAcceptAllFileFilterUsed(true);
			}
		});

		nodesBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				//	fc.setAcceptAllFileFilterUsed(false);
				//	fc.addChoosableFileFilter(binFilter);

				int returnVal = fc.showDialog(Import_DREAMPanel.this, "Import");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					nodesFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
				//	fc.removeChoosableFileFilter(binFilter);
				//	fc.setAcceptAllFileFilterUsed(true);
			}
		});
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				statusLbl.setText("Import aborted");
				task.cancel(true);
			}
		});
		
		
		simplifyChB.addActionListener(new ActionListener() { 

			@Override
			public void actionPerformed(ActionEvent evt) {
				 JCheckBox src = (JCheckBox) evt.getSource();
				 
				 if(src.isSelected()) {
					 
					 reduceLbl.setEnabled(true);
					 reduce2Lbl.setEnabled(true);
					 reduceFld.setEnabled(true);
					 vtkNetChB.setEnabled(true);
					 vtkGrainsChB.setEnabled(true);
				
					
					 minLbl.setEnabled(true);
					 minFld.setEnabled(true);
					
				 } else {
					 
					 reduceLbl.setEnabled(false);
					 reduce2Lbl.setEnabled(false);
					 reduceFld.setEnabled(false);
					 vtkNetChB.setEnabled(false);
					 vtkGrainsChB.setEnabled(false);
					
					 
				
					 minLbl.setEnabled(false);
					 minFld.setEnabled(false);
					 
				 }		 									
			}
			
		});


	}


	private class Importer extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {
			
			double reduction = 1d;
			
			int minSeg = 0;
			
			
			statusLbl.setText("Import started");
			
			if(simplifyChB.isSelected()) { 
							
				
				try {
					
					reduction = Double.parseDouble(reduceFld.getText().replace(",","."));
					if(reduction < 0d || reduction > 100d) throw new NumberFormatException();
					reduction /= 100d;
					
				} catch(NumberFormatException exc) {
					statusLbl.setText("Simplification failed");
					JOptionPane.showMessageDialog(null,
							"Percentage reduction of the number of segments should be a decimal number greater than 0 and less than 100",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return null;
				}
				
				try {
					
					minSeg = Integer.parseInt(minFld.getText());
					if( minSeg < 0 ) throw new NumberFormatException();
					
				} catch(NumberFormatException exc) {
					statusLbl.setText("Simplification failed");
					JOptionPane.showMessageDialog(null,
							"Minimum number of segments should be a positive integer.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return null;
				}
				
				
				
				
			}
		
			
			// READ NODES
			float[] nodeX = null;
			float[] nodeY = null;
			float[] nodeZ = null;
			int[] types =null;

			try {
				final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(nodesFld.getText())));
				final Scanner sc = new Scanner(in);
				sc.useLocale(Locale.US);
				final int nNodes = sc.nextInt();

				statusLbl.setText("Import in progress - Reading " + nNodes + " nodes...");

				nodeX = new float[nNodes];
				nodeY = new float[nNodes];
				nodeZ = new float[nNodes];
				types = new int[nNodes];

				for(int i = 0; i < nNodes; i++) {
					
					if(isCancelled()) {
						in.close();
						return null;
					}

					final int id = sc.nextInt();						
					final int type = sc.nextInt(); 
					final float x = sc.nextFloat();
					final float y = sc.nextFloat();
					final float z = sc.nextFloat();


					nodeX[id] = x;
					nodeY[id] = y;
					nodeZ[id] = z;
					
					types[id] = type;
				}
				in.close();

				statusLbl.setText("Reading nodes completed");
				
			} catch (IOException e) {
				
				statusLbl.setText("Reading nodes failed");
				JOptionPane.showMessageDialog(null,
						"An I/O error occured.",
						"Reading nodes file failed.",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}


			// READ TRIANGLES
			int[] tNode1 = null;
			int[] tNode2 = null;
			int[] tNode3 = null;
			int[] spin1 = null;
			int[] spin2 = null;		
			

			try {
				final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(triFld.getText())));

				final Scanner sc = new Scanner(in);
				sc.useLocale(Locale.US);
				final int nTriangles = sc.nextInt();

				statusLbl.setText("Import in progrss - Reading " + nTriangles + " triangles...");					

				tNode1 = new int[nTriangles];
				tNode2 = new int[nTriangles];
				tNode3 = new int[nTriangles];
				spin1 = new int[nTriangles];
				spin2 = new int[nTriangles];
				
				

				for(int i = 0; i < nTriangles; i++) {
					
					if(isCancelled()) {
						in.close();
						return null;
					}

					final int id = sc.nextInt();						
					final int nod1 = sc.nextInt();
					final int nod2 = sc.nextInt();
					final int nod3 = sc.nextInt();

					sc.nextInt(); sc.nextInt(); sc.nextInt(); // skip edges

					final int sp1 = sc.nextInt();
					final int sp2 = sc.nextInt();

					tNode1[id] = nod1;
					tNode2[id] = nod2;
					tNode3[id] = nod3;
					spin1[id] = sp1;
					spin2[id] = sp2;
					
					
				}
				in.close();					
				statusLbl.setText("Reading triangles completed");

			} catch (IOException e) {
				
				statusLbl.setText("Reading triangles failed");
				JOptionPane.showMessageDialog(null,
						"An I/O error occured.",
						"Reading triangles file failed.",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
			
			// READ VOXELS
			try {

				statusLbl.setText("Import in progress - Reading DREAM.3D data file...");

				//phases
				final int[] hdf_cryststruct;

				//grains
				final int[] hdf_phases;
				final float[] hdf_euler;
				byte[] surfGrains = null;

				final FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

				if (fileFormat == null) throw new IOException("Cannot find HDF5 FileFormat");

				// open the file with read and write access
				final FileFormat testFile = fileFormat.open(voxelFld.getText(), FileFormat.READ);
				if (testFile == null) throw new IOException("Failed to open file");

				// open the file and retrieve the file structure
				testFile.open();

				// phase data
				Dataset dataset = (Dataset)testFile.get("VoxelDataContainer/ENSEMBLE_DATA/CrystalStructures");		        
				hdf_cryststruct = (int[])dataset.read();
				
				System.out.println(hdf_cryststruct.length);

				// grain data					
				dataset = (Dataset)testFile.get("VoxelDataContainer/FIELD_DATA/Phases");		        
				hdf_phases = (int[])dataset.read();
				
				if(noSurfGrainsChB.isSelected()) {
					dataset = (Dataset)testFile.get("VoxelDataContainer/FIELD_DATA/SurfaceFields");		        
					surfGrains = (byte[])dataset.read();
				
				
					int countSurf = 0;
					for(byte b : surfGrains) if(b==1) countSurf++;
					System.out.println("There were " + countSurf + " surface grains.");
				}

				dataset = (Dataset)testFile.get("VoxelDataContainer/FIELD_DATA/EulerAngles");		        
				hdf_euler = (float[])dataset.read();


				// find the number of grains of each phase
				int[] phaseCounter = new int[hdf_cryststruct.length];
				for(int i = 0; i < phaseCounter.length; i++) phaseCounter[i] = 0;

				for(int i = 0; i < hdf_phases.length; i++) phaseCounter[ hdf_phases[i] ]++;

				// find EulerAngles					
				final EulerAngles[] eul = new EulerAngles[hdf_phases.length];

				for(int i = 0; i < hdf_phases.length; i++) { 
					eul[i] = new EulerAngles();
					final int threeI = 3 * i;
					eul[i].set(hdf_euler[threeI], hdf_euler[threeI+1], hdf_euler[threeI+2] );
				}

				// choose the phase
				statusLbl.setText("Import in progess: select phase");

				final Object[] possibilities = new Object[hdf_cryststruct.length];
				for(int i = 0; i < hdf_cryststruct.length; i++) {

					possibilities[i] = "Phase " + i + ": " +  phaseCounter[i] + " grains, " + ptGrpName(hdf_cryststruct[i]);
							//+ ", " + phTypeName(hdf_cryststruct[i]);
				}

				String s = "";
				int usedPhase = -1;
				
				boolean correctPhase = false;
				
				while(!correctPhase) {
					s = (String)JOptionPane.showInputDialog(
							null,
							"Boundaries between grains only of selected phase will be imported.",
							"Select phase",
							JOptionPane.QUESTION_MESSAGE,
							null,
							possibilities,
							possibilities[0] );
					if (s == null) {
						statusLbl.setText("Import aborted");
						return null;
					} else {
						usedPhase = -1;
						for(int i = 0; i < hdf_cryststruct.length; i++) if(s.compareTo((String)possibilities[i]) == 0) {
							usedPhase = i;
							break;
						}
						if(hdf_cryststruct[usedPhase] >= 0 && hdf_cryststruct[usedPhase] <= 2 ) {
							correctPhase = true;
						} else {
							JOptionPane.showMessageDialog(null,
									"Cannot select unknown/unsupported point group.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}							
				}
				
				// triangles per GB
				final int numberOfSpins = hdf_phases.length;
				
				final ArrayList<Integer>[][] triList = new ArrayList[numberOfSpins][numberOfSpins];
				
				for(int i = 0; i < numberOfSpins; i++)
					for(int j = 0; j <= i; j++) triList[i][j] = new ArrayList<Integer>();
				
				int borderTris = 0;
				
				int skippedNodes = 0;
				
				for(int i = 0; i < tNode1.length; i++) {
					if( (spin1[i] > 0 && spin2[i] > 0) && 
						(hdf_phases[spin1[i]] == hdf_phases[spin2[i]]) &&
						(hdf_phases[spin1[i]] == usedPhase) ) {
						
						if(tripleChB.isSelected()) 
						{
							if(!(types[tNode1[i]] == 2 && types[tNode2[i]] == 2 && types[tNode3[i]] == 2)) {
						
								skippedNodes++;
								continue;
							}
						}
						
						if(noSurfGrainsChB.isSelected()) 
						{
							if(surfGrains[spin1[i]] == 1 && surfGrains[spin2[i]] == 1) 
							{
								borderTris++;
								continue;
							}
						}
						
						if(spin1[i] >= spin2[i]) triList[spin1[i]][spin2[i]].add(i);
						else triList[spin2[i]][spin1[i]].add(i);
						
						
					}
				}
				
				System.out.println(borderTris + " tris belong to \"surface\" boundaries");
				System.out.println(skippedNodes + " tris belong to tripl/4 lines/points boundaries");
				
															
				if(simplifyChB.isSelected()) { 
					
	
																	
					PrintWriter gbdat = null;
															
					
					ArrayList<Double> vtkVertexX = null;
					ArrayList<Double> vtkVertexY = null;
					ArrayList<Double> vtkVertexZ = null;
					
					ArrayList<Integer> vtkTrisN1 = null;
					ArrayList<Integer> vtkTrisN2 = null;
					ArrayList<Integer> vtkTrisN3 = null;
					
					ArrayList<Integer> vtkGrainID = null;					
					ArrayList<Integer> vtkGrainID2 = null;
					
					ArrayList<Integer> vtkBoundaryID = null;
					
					//TODO
					//final boolean drawSym = true;
				//	ArrayList<Double> flags = null;
					
					
					
					
					if(vtkNetChB.isSelected() || vtkGrainsChB.isSelected()) {
						vtkVertexX = new ArrayList<Double>();
						vtkVertexY = new ArrayList<Double>();
						vtkVertexZ = new ArrayList<Double>();
						
						vtkTrisN1 = new ArrayList<Integer>();
						vtkTrisN2 = new ArrayList<Integer>();
						vtkTrisN3 = new ArrayList<Integer>();
						
						vtkBoundaryID = new ArrayList<Integer>();
						
				/*		if(drawSym) { //TODO
							flags = new ArrayList<Double>();
							
							switch(hdf_cryststruct[usedPhase]) {
							case 1: tester.setSymmetryTransformations(Transformations.getSymmetryTransformations(PointGroup.M3M)); break; 
							case 0: tester.setSymmetryTransformations(Transformations.getSymmetryTransformations(PointGroup._6MMM)); break;
							
							default: throw new IOException("Something went wrong with selection of the point group");
							} 
						}*/
					}
										
					if(vtkGrainsChB.isSelected()) {
						vtkGrainID = new ArrayList<Integer>();					
						vtkGrainID2 = new ArrayList<Integer>();
					}
					
																			

					int nBoundaries = 0;
					for(int i = 0; i < numberOfSpins; i++) for(int j = 0; j <= i; j++) 
						if(triList[i][j].size() > 0) nBoundaries++;
					
					int simplifiedBoundaries = 0;					
					int segmentsWritten = 0;					
					int trianglesSkipped = 0;
					int boundaryIndex = 0;
					
					
					
					for(int i = 0; i < numberOfSpins; i++) {
						for(int j = 0; j <= i; j++) {
							
							if(isCancelled()) {
								
								return null;
							}
							
							if(triList[i][j].size() > 0) {
								
								//int onEdge = 0;
								
								boundaryIndex++;
								
								statusLbl.setText("Import in progress - Simplifing boundaries... (" + simplifiedBoundaries +"/" + nBoundaries +")");
								
								int flag = -1;
								
								final TreeMap<Integer,Integer> tMap = new TreeMap<Integer,Integer>();
								
								PrintWriter tmp = new PrintWriter(new BufferedWriter(new FileWriter(dirFld.getText() 
										+ System.getProperty("file.separator") + "gb_to_be_simplified.smf")));
								
								for(int k = 0; k < triList[i][j].size(); k++) {
									
									if(!tMap.containsKey(tNode1[ triList[i][j].get(k) ])) tMap.put(tNode1[ triList[i][j].get(k) ], ++flag);
									if(!tMap.containsKey(tNode2[ triList[i][j].get(k) ])) tMap.put(tNode2[ triList[i][j].get(k) ], ++flag);
									if(!tMap.containsKey(tNode3[ triList[i][j].get(k) ])) tMap.put(tNode3[ triList[i][j].get(k) ], ++flag);									
								}
								
								
								int[] order = new int[tMap.size()];
								for (Map.Entry<Integer,Integer> entry : tMap.entrySet()) {
									final int oldNode = entry.getKey();
									final int newNode = entry.getValue();
									order[newNode] = oldNode;
								//	if(types[oldNode] != 2) onEdge++;
								}
								
								
								for(int m = 0; m < order.length; m++) {
									tmp.println("v  "+ nodeX[order[m]] + " " + nodeY[order[m]] + " " + nodeZ[order[m]]);
								}

								
								for(int k = 0; k < triList[i][j].size(); k++) {
									
									tmp.println("f "
									 + (tMap.get(tNode1[ triList[i][j].get(k) ]) + 1 )+ " "
									 + (tMap.get(tNode2[ triList[i][j].get(k) ]) + 1 )+ " "
									 + (tMap.get(tNode3[ triList[i][j].get(k) ]) + 1 ));
								}
								
							//	System.out.print(onEdge + " " + triList[i][j].size() + " ");
								
								tmp.close();
								
								
								// run QSlim
								try {
									String line;
								      														
									String[] params = new String[14];
				                    params[0] = "QSlim.exe";				                    
				                    params[1] = "-O";
				                    params[2] = "2";
				                    params[3] = "-B";
				                    params[4] = "999999999";
				                    params[5] = "-W";
				                    params[6] = "0";
				                    params[7] = "-M";
				                    params[8] = "smf";				                    
				                    params[9] = "-t";
				                    
				                    double scale = triList[i][j].size();
				                    if(triList[i][j].size() >= minSeg) scale = Math.max( triList[i][j].size() * reduction, minSeg);
				                    	
				                    params[10] = Integer.toString( (int)scale );
				                    params[11] = "-o";
				                    params[12] = dirFld.getText() + System.getProperty("file.separator") + "simplified_gb.smf";
				                    params[13] = dirFld.getText() + System.getProperty("file.separator") + "gb_to_be_simplified.smf";

				                    Process p = Runtime.getRuntime().exec(params);
				                    BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				                    BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				                    
								    while ((line = bri.readLine()) != null) {
								    // 	System.out.println(line);
								    }
								    bri.close();
								    
								    while ((line = bre.readLine()) != null) {
								    // 	System.out.println(line);
								    }
								    bre.close();
								    
								    p.waitFor();			    
								}
									catch (Exception err) {
									err.printStackTrace();
								}

									
								// import results and save gbdat
								int nTris = 0;
								String line = null;
								BufferedReader res = new BufferedReader(new FileReader(dirFld.getText() + System.getProperty("file.separator") + "simplified_gb.smf"));
								
								while ((line = res.readLine()) != null)   {									
									final String[] list = line.trim().split("\\s+");									
									if(list[0].compareTo("f") == 0) nTris++;
								}
								res.close();
								
								
								res = new BufferedReader(new FileReader(dirFld.getText() + System.getProperty("file.separator") + "simplified_gb.smf"));
								
								int vtkindex = 0;
								if(vtkNetChB.isSelected() || vtkGrainsChB.isSelected()) vtkindex = vtkVertexX.size();
								
								
								ArrayList<Double> x = new ArrayList<Double>();
								ArrayList<Double> y = new ArrayList<Double>();
								ArrayList<Double> z = new ArrayList<Double>();
								
																
								
								while ((line = res.readLine()) != null)   {
									
									final String[] list = line.trim().split("\\s+");
									
									if(list[0].compareTo("v") == 0) {
										
										x.add(Double.parseDouble(list[1]));
										y.add(Double.parseDouble(list[2]));
										z.add(Double.parseDouble(list[3]));
										
										if(vtkNetChB.isSelected() || vtkGrainsChB.isSelected()) {
											vtkVertexX.add(Double.parseDouble(list[1]));
											vtkVertexY.add(Double.parseDouble(list[2]));
											vtkVertexZ.add(Double.parseDouble(list[3]));
										}
									}
									
									if(list[0].compareTo("f") == 0) {
										
										try {
											
											int n1 = Integer.parseInt(list[1]) - 1;
											int n2 = Integer.parseInt(list[2]) - 1;
											int n3 = Integer.parseInt(list[3]) - 1;
											
											
											
											
																					
											final double x1 = x.get(n1);
											final double y1 = y.get(n1);
											final double z1 = z.get(n1); 

											final double x2 = x.get(n2);
											final double y2 = y.get(n2);
											final double z2 = z.get(n2);

											final double x3 = x.get(n3);
											final double y3 = y.get(n3);
											final double z3 = z.get(n3);
										
											final double X = y3* (z1 - z2) + y1* (z2 - z3) + y2* (-z1 + z3); 
											final double Y = x3* (-z1 + z2) + x2* (z1 - z3) + x1* (-z2 + z3); 
											final double Z = x3* (y1 - y2) + x1* (y2 - y3) + x2* (-y1 + y3);
										
											final double area = 0.5d * Math.sqrt(X*X + Y*Y + Z*Z);

											final UnitVector v1 = new UnitVector();
											final UnitVector v2 = new UnitVector();
											v1.set( x2-x1, y2-y1, z2-z1 );
											v2.set( x3-x1, y3-y1, z3-z1 );									

											v1.cross(v2);
											
											
											if(vtkNetChB.isSelected() || vtkGrainsChB.isSelected()) {
												vtkTrisN1.add(vtkindex + n1);
												vtkTrisN2.add(vtkindex + n2);
												vtkTrisN3.add(vtkindex + n3);
											}
											
											if(vtkGrainsChB.isSelected()) {
												vtkGrainID.add(i);
												vtkGrainID2.add(j);
											}
											
											if(vtkNetChB.isSelected() || vtkGrainsChB.isSelected()) {
												vtkBoundaryID.add(boundaryIndex);		
																								
											}
											
											
											if(segmentsWritten % maxPerFile == 0) {
												if(gbdat != null) gbdat.close();
												final int fileIndex = (segmentsWritten / maxPerFile) + 1;
												if(splitChB.isSelected()) {
													gbdat = new PrintWriter(new BufferedWriter(new FileWriter(outDir + System.getProperty("file.separator") + outPrefix + fileIndex + ".gbdat")));
												} else {
													gbdat = new PrintWriter(new BufferedWriter(new FileWriter(outDir + System.getProperty("file.separator") + outPrefix + ".gbdat")));
												}
												
												gbdat.println("# This file was generated by GBToolbox");
												gbdat.println("EXP");
												switch(hdf_cryststruct[usedPhase]) {
												case 1: gbdat.println("m-3m"); break; 
												case 0: gbdat.println("6/mmm"); break;
												case 2: gbdat.println("mmm"); break;
												default: throw new IOException("Something went wrong with selection of the point group");
												} 
												gbdat.println("L_PHI1 L_PHI L_PHI2 R_PHI1 R_PHI R_PHI2 LAB_ZENITH LAB_AZIMUTH CORRELAT AREA");
											}
										
										
											gbdat.println(df4.format( Math.toDegrees( eul[i].phi1())) + " " +
														df4.format( Math.toDegrees( eul[i].Phi())) + " " +
														df4.format( Math.toDegrees( eul[i].phi2())) + " " +
														
														df4.format( Math.toDegrees( eul[j].phi1())) + " " +
														df4.format( Math.toDegrees( eul[j].Phi())) + " " +
														df4.format( Math.toDegrees( eul[j].phi2())) + " " +
														
														df4.format( Math.toDegrees( v1.zenith())) + " " +
														df4.format( Math.toDegrees( v1.azimuth())) + " " +
														
														nTris + " " +
														
														df7.format(area) );
											
											segmentsWritten++;
											
									/*		if(drawSym) { //TODO
												
												Matrix3x3 ML = new Matrix3x3();
												ML.set(eul[i]);
												
												Matrix3x3 MR = new Matrix3x3();
												MR.set(eul[j]);
												
												UnitVector m1 = new UnitVector();
												m1.set(v1.zenith(), v1.azimuth());
												
											
												final Matrix3x3 M = new Matrix3x3(ML);
												M.timesTransposed(MR);		
												m1.transform(ML); 
												
												InterfaceMatrix B = new InterfaceMatrix(M, m1);
												
												tester.test(B);
												
												double alphS = tester.getAlphaS();
												double alphI = tester.getAlphaI();
												
												flags.add(alphS);
												final double thr = 8d * Math.PI / 180d;
												
												if(alphS < thr && alphI < thr) {
													flags.add(1);
												} else if(alphS < thr) {
													flags.add(2);
												} else if(alphI < thr) {
													flags.add(3);
												} else {
													flags.add(4);
												}
												
												
												
											}*/
											
											
										}  catch(IllegalArgumentException e) {
											
											trianglesSkipped++;
											continue;
										}
									}
									
								}
								
							
								res.close();
								
														
								simplifiedBoundaries++;								
							}							
						}
					}
					
					if(gbdat != null) gbdat.close();

					if(vtkNetChB.isSelected() && !isCancelled()) {
						
												
						final PrintWriter vtkNet = new PrintWriter(new BufferedWriter(new FileWriter(outDir + System.getProperty("file.separator") + outPrefix + "_simpl_gbIDsOnly.vtk")));;

						vtkNet.println("# vtk DataFile Version 2.0");
						vtkNet.println("Data set from QSlim/GBToolbox with simplified triangular mesh of boundary network");
						vtkNet.println("ASCII");
						vtkNet.println("DATASET POLYDATA"); 
						vtkNet.println("POINTS "+vtkVertexX.size()+" float"); 
					
						for(int i =0; i< vtkVertexX.size(); i++) {
							vtkNet.println(df4.format(vtkVertexX.get(i)) + " " + df4.format(vtkVertexY.get(i)) + " " +df4.format(vtkVertexZ.get(i)) );					
						}
						
						vtkNet.println("POLYGONS " + vtkTrisN1.size() +" "+ (4*vtkTrisN1.size()));
					
						for(int i =0; i<  vtkTrisN1.size(); i++) {
							vtkNet.println("3 " + df4.format(vtkTrisN1.get(i)) + " " + df4.format(vtkTrisN2.get(i)) + " " +df4.format(vtkTrisN3.get(i)) );					
						}
					
						vtkNet.println("CELL_DATA " + vtkTrisN1.size());
						vtkNet.println("SCALARS BoundaryID int 1");
						vtkNet.println("LOOKUP_TABLE default");
					
						for(int i = 0; i < vtkTrisN1.size(); i++) {
							vtkNet.println(vtkBoundaryID.get(i));					
						}
						
					
						//TODO
					/*	vtkNet.println("SCALARS symflag float");
						vtkNet.println("LOOKUP_TABLE default");
					
						for(int i = 0; i < flags.size(); i++) {
							vtkNet.println(flags.get(i));					
						}
					
						vtkNet.close();*/
					}
					
					if(vtkGrainsChB.isSelected() && !isCancelled()) {
						
						final PrintWriter vtkGrains = new PrintWriter(new BufferedWriter(new FileWriter(outDir + System.getProperty("file.separator") + outPrefix + "_simpl_WithGrainIDs.vtk")));;

						vtkGrains.println("# vtk DataFile Version 2.0");
						vtkGrains.println("Data set from QSlim/GBToolbox with simplified grains");
						vtkGrains.println("ASCII");
						vtkGrains.println("DATASET POLYDATA"); 
						vtkGrains.println("POINTS "+vtkVertexX.size()+" float"); 
					
						for(int i =0; i< vtkVertexX.size(); i++) {
							vtkGrains.println(df4.format(vtkVertexX.get(i)) + " " + df4.format(vtkVertexY.get(i)) + " " +df4.format(vtkVertexZ.get(i)) );					
						}
						
						vtkGrains.println("POLYGONS " + (2*vtkTrisN1.size()) +" "+ (8*vtkTrisN1.size()));
					
						for(int i =0; i < vtkTrisN1.size(); i++) 
						{
							vtkGrains.println("3 " + df4.format(vtkTrisN1.get(i)) + " " + df4.format(vtkTrisN2.get(i)) + " " +df4.format(vtkTrisN3.get(i)) );					
							vtkGrains.println("3 " + df4.format(vtkTrisN1.get(i)) + " " + df4.format(vtkTrisN2.get(i)) + " " +df4.format(vtkTrisN3.get(i)) );
						}
					
						vtkGrains.println("CELL_DATA " + (2*vtkTrisN1.size()));
						vtkGrains.println("SCALARS GrainID int 1");
						vtkGrains.println("LOOKUP_TABLE default");
					
						for(int i = 0; i < vtkTrisN1.size(); i++)
						{
							vtkGrains.println(vtkGrainID.get(i));
							vtkGrains.println(vtkGrainID2.get(i));
						}
						
						vtkGrains.println("SCALARS BoundaryID int 1");
						vtkGrains.println("LOOKUP_TABLE default");
					
						for(int i = 0; i < vtkTrisN1.size(); i++) 
						{
							vtkGrains.println(vtkBoundaryID.get(i));
							vtkGrains.println(vtkBoundaryID.get(i));			
						}
					
						vtkGrains.close();
					}
					
					statusLbl.setText("Import and simplification complete. " + segmentsWritten + " mesh triangles saved. " + trianglesSkipped + " skipped.");										
					
				} else { // NO SIMPLIFICATION


					// process triangles
				
					int trianglesProcessed = 0;
					int trianglesSkipped = 0;					
					PrintWriter wrt = null;
					
					final boolean nosurf = noSurfGrainsChB.isSelected();
	
	
					statusLbl.setText("Import in progress - Task 4/4 - Processing mesh triangles, saving boundary parameters...");
	
					for(int i = 0; i < tNode1.length; i++) {
						
						if(i % 1000==0)
							statusLbl.setText("Import in progress - Task 4/4 - Processing mesh triangles, saving boundary parameters (" + i + " / " + tNode1.length + ")...");
						
						if(isCancelled()) {			
							if(wrt != null) wrt.close();
							return null;
						}
	
						if(i == 0 || (trianglesProcessed > 0 && trianglesProcessed % maxPerFile == 0)) { 
							

							if(wrt != null) wrt.close();
							final int fileIndex = (trianglesProcessed / maxPerFile) + 1;
							if(splitChB.isSelected()) {
								wrt = new PrintWriter(new BufferedWriter(new FileWriter(outDir + System.getProperty("file.separator") + outPrefix + fileIndex + ".gbdat")));
							} else {
								wrt = new PrintWriter(new BufferedWriter(new FileWriter(outDir + System.getProperty("file.separator") + outPrefix + ".gbdat")));
							}
	
							wrt.println("# This file was created by GBToolbox");
							wrt.println("# It contains boundary parameters imported from DREAM.3D output files");
							wrt.println("EXP");
	
							switch(hdf_cryststruct[usedPhase]) {
							case 1: wrt.println("m-3m"); break; 
							case 0: wrt.println("6/mmm"); break;
							case 2: wrt.println("mmm"); break;
							default: throw new IOException("Something went wrong with selection of the point group");
							} 
							wrt.println("L_PHI1 L_PHI L_PHI2 R_PHI1 R_PHI R_PHI2 ZENITH AZIMUTH CORRELAT AREA");
						}
						
						final int s1 = spin1[i];
						final int s2 = spin2[i];
	
						if( (s1 > 0 && s2 > 0) && 
								(!nosurf ||
								(nosurf && !(surfGrains[s1] == 1 && surfGrains[s2] == 1) ) ) &&
																
								(hdf_phases[s1] == hdf_phases[s2]) &&
								(hdf_phases[s2] == usedPhase) 

							) {
							
							if(tripleChB.isSelected())  {
								if(!(types[tNode1[i]] == 2 && types[tNode2[i]] == 2 && types[tNode3[i]] == 2)) {
								
									continue;
								}
							}
		
							try {
								
								final double x1 = nodeX[ tNode1[i] ];
								final double y1 = nodeY[ tNode1[i] ];
								final double z1 = nodeZ[ tNode1[i] ]; 
	
								final double x2 = nodeX[ tNode2[i] ];
								final double y2 = nodeY[ tNode2[i] ];
								final double z2 = nodeZ[ tNode2[i] ];
	
								final double x3 = nodeX[ tNode3[i] ];
								final double y3 = nodeY[ tNode3[i] ];
								final double z3 = nodeZ[ tNode3[i] ];
								
								final double X = y3* (z1 - z2) + y1* (z2 - z3) + y2* (-z1 + z3); 
								final double Y = x3* (-z1 + z2) + x2* (z1 - z3) + x1* (-z2 + z3); 
								final double Z = x3* (y1 - y2) + x1* (y2 - y3) + x2* (-y1 + y3);
								
								final double area = 0.5d * Math.sqrt(X*X + Y*Y + Z*Z);
	
								final UnitVector v1 = new UnitVector();
								final UnitVector v2 = new UnitVector();
								v1.set( x2-x1, y2-y1, z2-z1 );
								v2.set( x3-x1, y3-y1, z3-z1 );
								
	
								v1.cross(v2);
								
						
	
								wrt.println(
										df4.format( Math.toDegrees( eul[spin1[i]].phi1())) + " "+
										df4.format( Math.toDegrees( eul[spin1[i]].Phi()))+ " "+
										df4.format( Math.toDegrees( eul[spin1[i]].phi2()))+ " "+
												
										df4.format( Math.toDegrees( eul[spin2[i]].phi1())) + " "+
										df4.format( Math.toDegrees( eul[spin2[i]].Phi()))+ " "+
										df4.format( Math.toDegrees( eul[spin2[i]].phi2()))+ " "+
												
										df4.format( Math.toDegrees( v1.zenith()))+ " "+
										df4.format( Math.toDegrees( v1.azimuth()))+ " "+
												
										triList[Math.max(s1,s2)][Math.min(s1,s2)].size() + " " +
										
										df7.format(area) );
								
							
								
								trianglesProcessed++;
							
							} catch(IllegalArgumentException e) {
												
								trianglesSkipped++;
								continue;
							}
	
						} else {
							continue;
						}
	
					}
	
					if(wrt != null) wrt.close();
	
					statusLbl.setText("Import complete. " + trianglesProcessed + " mesh triangles saved. " + trianglesSkipped + " skipped.");
				
				}

			} catch(Exception exc) {
				exc.printStackTrace();
				statusLbl.setText("DREAM.3D data file import failed");
				JOptionPane.showMessageDialog(null,
						"An I/O error occured.",
						"DREAM.3D data file import failed",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}		
			
			
			return null;
		}

		@Override
		protected void done() {
			processBtn.setEnabled(true);
			abortBtn.setEnabled(false);
		}	
	}

	private final String ptGrpName(int cryst) {
		switch(cryst) {
		case 1: return "m3\u0305m"; 
		case 0: return "6/mmm";
		case 2: return "mmm";
		default: return "Unknown/unsupported"; 
		}
	}

	


}
