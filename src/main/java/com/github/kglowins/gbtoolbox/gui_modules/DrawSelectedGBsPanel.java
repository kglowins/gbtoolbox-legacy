package com.github.kglowins.gbtoolbox.gui_modules;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.IntStream;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.kglowins.gbtoolbox.enums.PointGroup;
import com.github.kglowins.gbtoolbox.gui_bricks.Dialog_GBPlusLimitsForDraw;


import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import net.miginfocom.swing.MigLayout;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.FileUtils;
import com.github.kglowins.gbtoolbox.utils.GBPlusLimits;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.Transformations;
import com.github.kglowins.gbtoolbox.utils.UnitVector;

import javax.swing.JScrollPane;
import javax.swing.JList;

import org.apache.commons.math3.util.FastMath;

import javax.swing.JCheckBox;



public class DrawSelectedGBsPanel extends JPanel implements ListSelectionListener {
	private JTextField outVtkFld;

	private FileUtils.VTKFileFilter vtkFilter = new FileUtils.VTKFileFilter();

	private JButton abortBtn;
	private JButton fireBtn;

	private final DecimalFormat df2;
	
	private DefaultListModel listModel;
	private JLabel lblNewLabel;
	
	private JButton addBtn;
	private JButton removeBtn;
	private JScrollPane scrollPane;
	private JList list;
	
	private Dialog_GBPlusLimitsForDraw dialog;

	private ArrayList<GBPlusLimits> gbs = new ArrayList<GBPlusLimits>();
	private JLabel voxelLbl;
	private JLabel lblCurrentlyWorks;
	private JTextField voxelFld;
	private JButton voxelBtn;
	private JLabel statusLbl;
	
	
	private final DecimalFormat df4;
	
	private Importer task;
	private JCheckBox negateChB;

	
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
	
	

	public DrawSelectedGBsPanel() {
		
		dialog = new Dialog_GBPlusLimitsForDraw(PointGroup.M3M, DrawSelectedGBsPanel.this);

		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df2 = new DecimalFormat("0.##", otherSymbols);
		df4 = new DecimalFormat("0.####", otherSymbols);
	

		setLayout(new MigLayout("", "[][][][]", "[][][][][][][][][][][][][][]"));

		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		voxelLbl = new JLabel("<html><u>DREAM.3D data file (after clean-up)</u> <font color=#0000cc><sup>(2)</sup></font>:");
		add(voxelLbl, "cell 0 3");
		
		voxelFld = new JTextField();
		voxelFld.setColumns(24);
		add(voxelFld, "flowx,cell 1 3,alignx right");
		
		voxelBtn = new JButton();
		voxelBtn.setIcon(new ImageIcon(DrawSelectedGBsPanel.class.getResource("/gui_bricks/folder.png")));
		voxelBtn.setPreferredSize(new Dimension(24, 24));
		voxelBtn.setMinimumSize(new Dimension(24, 24));
		voxelBtn.setMaximumSize(new Dimension(24, 24));
		add(voxelBtn, "cell 2 3");
		
		voxelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				//fc.setAcceptAllFileFilterUsed(false);
				//fc.addChoosableFileFilter(h5voxelFilter);

				int returnVal = fc.showDialog(DrawSelectedGBsPanel.this, "Import");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					voxelFld.setText(fc.getSelectedFile().getAbsolutePath());
				}	
				//fc.removeChoosableFileFilter(h5voxelFilter);
				//fc.setAcceptAllFileFilterUsed(true);
			}
		});
		
		lblCurrentlyWorks = new JLabel("<html><small>\r\n<font color=#bb0000>* Currently works for cubic symmetry only.</font><br><font color=#0000cc><sup>(1)</sup> To obtain node and triangle data files, 'IO / Write Nodes/Triangles from Surface Mesh' filter of DREAM.3D must be used.<br>\r\n<sup>(2)</sup> .dream3d data file must contain grain orientations computed by\r\n'Statistics / Find Field Average Oreintations' filter.</small>\r\n\r\n");
		add(lblCurrentlyWorks, "cell 0 4 4 1");

		JLabel lblHeader = new JLabel("<html><b>Create a <code>vtk</code> file with selected boundaries:</b>\r\n<font color=#bb0000>*</font>");
		add(lblHeader, "cell 0 0 3 1");

		JLabel lblOutput = new JLabel("<html><u>Output <code>vtk</code> file</u>:");
		add(lblOutput, "flowx,cell 0 5 4 1,gapy 5");
		
		negateChB = new JCheckBox("Remove selected types");
		add(negateChB, "cell 0 6 4 1");
		
		lblNewLabel = new JLabel("<html><font color=cc0000><small><b>Warning: existing files will be overwritten!</small></font></b>");
		add(lblNewLabel, "cell 0 7 3 1,gapy 10 10");

		JLabel lblValues = new JLabel("<html><u>Boundaries to be saved in the file</u>:");
		add(lblValues, "flowx,cell 0 8 4 1,gapy 5");



		final JFileChooser inputFc = new JFileChooser(); 
		inputFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		inputFc.setAcceptAllFileFilterUsed(false);
		inputFc.addChoosableFileFilter(new FileUtils.GBDatFileFilter());
		inputFc.setMultiSelectionEnabled(true);

		outVtkFld = new JTextField();
		add(outVtkFld, "cell 0 5 4 1,gapy 5");
		outVtkFld.setColumns(18);

		JButton outVtkBtn = new JButton();
		outVtkBtn.setPreferredSize(new Dimension(24,24));
		outVtkBtn.setMinimumSize(new Dimension(24,24));
		outVtkBtn.setMaximumSize(new Dimension(24,24));
		outVtkBtn.setIcon(new ImageIcon(DrawSelectedGBsPanel.class.getResource("/gui_bricks/folder.png")));
		add(outVtkBtn, "cell 0 5 4 1,gapy 5");

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

		outputFc.addChoosableFileFilter(vtkFilter);
		
		outVtkBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

			

				int returnVal = outputFc.showSaveDialog(DrawSelectedGBsPanel.this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = outputFc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);

					if(ext != null) {
						if(ext.compareTo("vtk") != 0) fName += ".vtk";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "vtk";
						else fName += ".vtk";
					}

					outVtkFld.setText(fName);
				}	
			}			
		});
		
		scrollPane = new JScrollPane();
		add(scrollPane, "cell 0 9 2 2,gapy 5,grow");
		
	
		list = new JList();
		listModel = new DefaultListModel();
		list.setModel(listModel);
		list.getSelectionModel().addListSelectionListener(this);
		
		
		
		
		scrollPane.setViewportView(list);
		scrollPane.setPreferredSize(new Dimension(550,120));
		
		addBtn = new JButton();
		addBtn.setIcon(new ImageIcon(DrawSelectedGBsPanel.class.getResource("/gui_bricks/add-icon.png")));
		addBtn.setToolTipText("Add boundary type(s) to the list");
		addBtn.setPreferredSize(new Dimension(24, 24));
		addBtn.setMinimumSize(new Dimension(24, 24));
		addBtn.setMaximumSize(new Dimension(24, 24));
		add(addBtn, "cell 2 9,gapy 5");
		
				//TODO
				addBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						
						dialog.setVisible(true);
						dialog.setFlag();
		
					}
				});
		
		removeBtn = new JButton();
		removeBtn.setIcon(new ImageIcon(DrawSelectedGBsPanel.class.getResource("/gui_bricks/remove.png")));
		removeBtn.setToolTipText("Remove boundary type(s) from the list");
		removeBtn.setPreferredSize(new Dimension(24, 24));
		removeBtn.setMinimumSize(new Dimension(24, 24));
		removeBtn.setMaximumSize(new Dimension(24, 24));
		removeBtn.setEnabled(false);
		add(removeBtn, "cell 2 10,aligny top");
		
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

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 11 3 1,growx,aligny center,gapy 5 5");



		fireBtn = new JButton("Create");
		add(fireBtn, "flowx,cell 0 12 3 1,aligny center");

		fireBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				File fTmp;

				fTmp = new File(voxelFld.getText());
				if(!fTmp.exists()) {

					JOptionPane.showMessageDialog(null,
							"DREAM.3D data file does not exist.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}

				System.out.println("GB types " + gbs.size());
				fireBtn.setEnabled(false);
				abortBtn.setEnabled(true);
				
				task = new Importer();
				task.execute();
			}
		});

		abortBtn = new JButton();
		abortBtn.setToolTipText("Abort");
		abortBtn.setEnabled(false);
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(DrawSelectedGBsPanel.class.getResource("/gui_bricks/abort.png")));
		add(abortBtn, "cell 0 12 3 1,gapx 10,aligny center");
		
		statusLbl = new JLabel("<html>&nbsp;");
		add(statusLbl, "cell 0 13 3 1");
	
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				statusLbl.setText("Import aborted");
				task.cancel(true);
			}
		});
		
		
		
		
	}

	
	


	public final JButton getRemoveBtn() {
		return removeBtn;
	}
	
	
	
	
	
	private class Importer extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() {

			statusLbl.setText("Import started");

			int[] hdf_cryststruct;
			int[] hdf_phases;
			int usedPhase;
			EulerAngles[] eul;

			int[] tNode1;
			int[] tNode2;
			int[] tNode3;
			int[] spin1;
			int[] spin2;
			float[] nodeX;
			float[] nodeY;
			float[] nodeZ;
			byte[] types;


			// READ VOXELS
			try {

				statusLbl.setText("Import in progress - Reading DREAM.3D data file...");

				

				//grains				
				final float[] hdf_euler;
				final byte[] surfGrains;

				final FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

				if (fileFormat == null) throw new IOException("Cannot find HDF5 FileFormat");

				// open the file with read and write access
				final FileFormat testFile = fileFormat.open(voxelFld.getText(), FileFormat.READ);
				if (testFile == null) throw new IOException("Failed to open file");

				// open the file and retrieve the file structure
				testFile.open();

				// phase data
				Dataset dataset = (Dataset)testFile.get("DataContainers/DataContainer/Phase Data/CrystalStructures");
				hdf_cryststruct = (int[])dataset.read();

				// grain data					
				dataset = (Dataset)testFile.get("DataContainers/DataContainer/Grain Data/Phases");
				hdf_phases = (int[])dataset.read();
				
				dataset = (Dataset)testFile.get("DataContainers/DataContainer/Grain Data/SurfaceFeatures");
				surfGrains = (byte[])dataset.read();
				
				int countSurf = 0;
				for(byte b : surfGrains) if(b==1) countSurf++;
				System.out.println("There were " + countSurf + " surface grains.");

				dataset = (Dataset)testFile.get("DataContainers/DataContainer/Grain Data/AvgEulerAngles");
				hdf_euler = (float[])dataset.read();


				// find the number of grains of each phase
				int[] phaseCounter = new int[hdf_cryststruct.length];
				for(int i = 0; i < phaseCounter.length; i++) phaseCounter[i] = 0;

				for(int i = 0; i < hdf_phases.length; i++) phaseCounter[ hdf_phases[i] ]++;

				// find EulerAngles					
				eul = new EulerAngles[hdf_phases.length];

				for(int i = 0; i < hdf_phases.length; i++) { 
					eul[i] = new EulerAngles();
					final int threeI = 3 * i;
					eul[i].set(hdf_euler[threeI], hdf_euler[threeI+1], hdf_euler[threeI+2] );
				}



				// READ NODES
				dataset = (Dataset)testFile.get("DataContainers/TriangleDataContainer/_SIMPL_GEOMETRY/SharedVertexList");

				float[] rawNodeCoordinates = (float[]) dataset.read();
				nodeX = new float[rawNodeCoordinates.length / 3];
				nodeY = new float[rawNodeCoordinates.length / 3];
				nodeZ = new float[rawNodeCoordinates.length / 3];

				IntStream.range(0, rawNodeCoordinates.length / 3).forEach(nodeId -> {
					int idTimes3 = 3 * nodeId;
					nodeX[nodeId] = rawNodeCoordinates[idTimes3];
					nodeY[nodeId] = rawNodeCoordinates[idTimes3 + 1];
					nodeZ[nodeId] = rawNodeCoordinates[idTimes3 + 2];
				});

				dataset = (Dataset)testFile.get("DataContainers/TriangleDataContainer/VertexData/NodeType");
				types = (byte[])dataset.read();


				// READ TRIANGLES
				dataset = (Dataset)testFile.get("DataContainers/TriangleDataContainer/_SIMPL_GEOMETRY/SharedTriList");

				long[] rawTriList = (long[]) dataset.read();
				tNode1 = new int[rawTriList.length / 3];
				tNode2 = new int[rawTriList.length / 3];
				tNode3 = new int[rawTriList.length / 3];

				IntStream.range(0, rawTriList.length / 3).forEach(triId -> {
					int idTimes3 = 3 * triId;
					tNode1[triId] = Long.valueOf(rawTriList[idTimes3]).intValue();
					tNode2[triId] = Long.valueOf(rawTriList[idTimes3 + 1]).intValue();
					tNode3[triId] = Long.valueOf(rawTriList[idTimes3 + 2]).intValue();
				});


				dataset = (Dataset)testFile.get("DataContainers/TriangleDataContainer/FaceData/FaceLabels");
				int[] rawSpins = (int[]) dataset.read();
				spin1 = new int[rawSpins.length / 2];
				spin2 = new int[rawSpins.length / 2];

				IntStream.range(0, rawSpins.length / 2).forEach(spinId -> {
					int idTimes2 = 2 * spinId;
					spin1[spinId] = rawSpins[idTimes2];
					spin2[spinId] = rawSpins[idTimes2 + 1];
				});


				// choose the phase
				statusLbl.setText("Import in progess: select phase");

				final Object[] possibilities = new Object[hdf_cryststruct.length];
				for(int i = 0; i < hdf_cryststruct.length; i++) {

					possibilities[i] = "Phase " + i + ": " +  phaseCounter[i] + " grains, " + ptGrpName(hdf_cryststruct[i]);
							//+ ", " + phTypeName(hdf_cryststruct[i]);
				}

				String s = "";
				usedPhase = -1;
				
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
				
			} catch(Exception exc) {
				exc.printStackTrace();
				statusLbl.setText("DREAM.3D data file import failed");
				JOptionPane.showMessageDialog(null,
						"An I/O error occured.",
						"DREAM.3D data file import failed",
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
				
			// write tris to VTK
			
			try {
				
				statusLbl.setText("Import in progress - checking boundary types...");
				
				Matrix3x3[] setC = null;
				
				switch(hdf_cryststruct[usedPhase]) {
					case 1: setC = Transformations.getSymmetryTransformations(PointGroup.M3M); break; 
			//		case 0: setC = Transformations.getSymmetryTransformations(PointGroup._6MMM); break;					
					default: throw new IOException("Unsupported point group");
				}
			
								
				ArrayList<Integer> vtkTrisN1 = new ArrayList<Integer>();
				ArrayList<Integer> vtkTrisN2 = new ArrayList<Integer>();
				ArrayList<Integer> vtkTrisN3 = new ArrayList<Integer>();			
				ArrayList<Integer> triType = new ArrayList<Integer>();
					
					
				int skippedTriangles = 0;
				
				for(int i = 0; i < tNode1.length; i++) {
					
					if(i % 500 == 0) statusLbl.setText("Import in progress - checking boundary types ( " +
					i + " / " + tNode1.length + "tris)...");
					if( (spin1[i] > 0 && spin2[i] > 0) && 
						(hdf_phases[spin1[i]] == hdf_phases[spin2[i]]) &&
						(hdf_phases[spin1[i]] == usedPhase) ) {
						
						if(types[tNode1[i]] > 4 || types[tNode2[i]] > 4 || types[tNode3[i]] > 4) continue;
						
						//if good triangle
						//determine the type
						
						try {
							
																							
							final double x1 = nodeX[tNode1[i]];
							final double y1 = nodeY[tNode1[i]];
							final double z1 = nodeZ[tNode1[i]];
							
							final double x2 = nodeX[tNode2[i]];
							final double y2 = nodeY[tNode2[i]];
							final double z2 = nodeZ[tNode2[i]];
							
							final double x3 = nodeX[tNode3[i]];
							final double y3 = nodeY[tNode3[i]];
							final double z3 = nodeZ[tNode3[i]];

							final UnitVector v1 = new UnitVector();
							final UnitVector v2 = new UnitVector();
							v1.set( x2-x1, y2-y1, z2-z1 );
							v2.set( x3-x1, y3-y1, z3-z1 );									

							v1.cross(v2);
							
						
							
					
							Matrix3x3 ML = new Matrix3x3();
							ML.set(eul[spin1[i]]);
								
							Matrix3x3 MR = new Matrix3x3();
							MR.set(eul[spin2[i]]);
								
							UnitVector m1 = new UnitVector();
							m1.set(v1.zenith(), v1.azimuth());
								
							
							final Matrix3x3 M = new Matrix3x3(ML);
							M.timesTransposed(MR);		
							m1.transform(ML); 
								
							InterfaceMatrix B = new InterfaceMatrix(M, m1);
							
							
							
							
							
							// test B		
							
							boolean[] flags = new boolean[gbs.size()];
							for(int k = 0; k < flags.length; k++) flags[k] = false;
														
		
																									
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
											for(int k = 0; k < gbs.size(); k++) {
											
												GBPlusLimits gb = gbs.get(k);
												
												if(!gb.isArbitraryMisor() && !gb.isArbitraryPlane()) {
													
													final Matrix3x3 CMC = new Matrix3x3();
													CMC.set(CBC.M());
													CMC.timesTransposed(gb.getM());

													final double omega = CMC.rotationAngle();
													
													final UnitVector Cm1 = new UnitVector();
													Cm1.set(CBC.m1());

													final double gamma = FastMath.acos(Cm1.dot(gb.getM1()));
																					
													if(omega < gb.getMisorLimit() && gamma < gb.getPlaneLimit()) {
															flags[k] = true;
													}
																																
												}
												
												
												if(!gb.isArbitraryMisor() && gb.isArbitraryPlane()) {
												
													final Matrix3x3 CMC = new Matrix3x3();
													CMC.set(CBC.M());
													CMC.timesTransposed(gb.getM());
										
													final double omega = CMC.rotationAngle();
																						
													if(omega < gb.getMisorLimit()) {
														flags[k] = true;
													}
												}
											
												if(!gb.isArbitraryPlane() && gb.isArbitraryMisor()) {
												
													final UnitVector Cm1 = new UnitVector();
													Cm1.set(CBC.m1());
												
													final double gamma = FastMath.acos(Cm1.dot(gb.getM1()));
																						
													if(gamma < gb.getPlaneLimit()) {
														flags[k] = true;
													}
												}	
											}
											
										}
									}				
								}
							}
							
							// check the type
							
							if(negateChB.isSelected()) {
							
								int isAny = 0;
								for(int k = 0; k < flags.length; k++) if(flags[k]) isAny++;
								
								if(isAny == 0) {
									vtkTrisN1.add(tNode1[i]);
									vtkTrisN2.add(tNode2[i]);
									vtkTrisN3.add(tNode3[i]);
									triType.add(0);
								}
								
							} else {
							
								for(int k = 0; k < flags.length; k++) {
									if(flags[k] == false) {
										continue;
									} else {
										vtkTrisN1.add(tNode1[i]);
										vtkTrisN2.add(tNode2[i]);
										vtkTrisN3.add(tNode3[i]);
										triType.add(k);
										break;
									}
								}
							}
							
								
							
							
							
						}  catch(IllegalArgumentException e) {

							skippedTriangles++;
							continue;
						}
					}
					
				}
						
				statusLbl.setText("Import in progress - writing a VTK file...");
			
			
				
				final PrintWriter vtk = new PrintWriter(new BufferedWriter(new FileWriter(outVtkFld.getText())));;
	
				vtk.println("# vtk DataFile Version 2.0");
				vtk.println("Grain boundaries of selected types. Generated by GBToolbox.");
				vtk.println("ASCII");
				vtk.println("DATASET POLYDATA"); 
				vtk.println("POINTS "+ nodeX.length +" float"); 
						
				for(int i = 0; i < nodeX.length; i++) {
					vtk.println(df4.format(nodeX[i]) + " " + df4.format(nodeY[i]) + " " +df4.format(nodeZ[i]) );					
				}
							
					
				vtk.println("POLYGONS " + vtkTrisN1.size() + " " + (4*vtkTrisN1.size()));
						
				for(int i = 0; i < vtkTrisN1.size(); i++) {
					vtk.println("3 " + df4.format(vtkTrisN1.get(i)) + " " + df4.format(vtkTrisN2.get(i)) + " " +df4.format(vtkTrisN3.get(i)) );					
				}
					
					
				vtk.println("CELL_DATA " + vtkTrisN1.size());
				vtk.println("SCALARS GB_type int 1");
				vtk.println("LOOKUP_TABLE default");
						
				for(int i = 0; i < vtkTrisN1.size(); i++) {
					vtk.println(triType.get(i));					
				}
						
					
				vtk.close();
					
			} catch (IOException e) {
				
					
			}
				
			statusLbl.setText("Import in progress - import complete.");

			
			return null;
		}

		@Override
		protected void done() {
			fireBtn.setEnabled(true);
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
