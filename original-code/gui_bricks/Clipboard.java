package gui_bricks;

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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import enums.PointGroup;

import net.miginfocom.swing.MigLayout;
import utils.AxisAngle;
import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.UnitVector;

public class Clipboard extends JPanel implements TableModelListener, ListSelectionListener  {
	private DefaultListModel listModel;

	private JButton pasteBtn;	
	private JButton removeBtn;

	private int boundaryId;

	private TreeMap<String, InterfaceMatrix> tMap;

	private JButton exportBtn;

	private JTextField maxFld;

	private BoundaryParamsPanel gbParamsPane;
	private LatticePanel latticePane;
	private JButton distBtn;
	private JTextField distFld;
	private JLabel lblCmp;

	

	private DecimalFormat df;
//	private JTextField misorDeltaFld;
	private JLabel distLbl;
//	private JLabel misorLbl;
	//private JLabel label;
	private JLabel label_1;
	private JButton importBtn;
	private JTable table;
	private DefaultTableModel tabModel;

	private String lastName;
	private JTextField distMFld;
	private JTextField distPFld;
	private JLabel distMlbl;
	private JLabel distPlbl;
	private JLabel label_3;
	private JLabel label_4;


	@Override
	public void valueChanged(ListSelectionEvent evt) {

		if (evt.getValueIsAdjusting() == false) {

			int count = table.getSelectedRowCount();

			if(count == 0) {
				pasteBtn.setEnabled(false);
				removeBtn.setEnabled(false); 
				distBtn.setEnabled(false);

			} else if(count == 1) {
				pasteBtn.setEnabled(true);
				removeBtn.setEnabled(true); 
				distBtn.setEnabled(false);

				lastName = (String)tabModel.getValueAt(table.getSelectedRow(), 0);

			} else if(count == 2) {
				pasteBtn.setEnabled(false);
				removeBtn.setEnabled(true); 
				distBtn.setEnabled(true);
			} else {
				pasteBtn.setEnabled(false);
				removeBtn.setEnabled(true); 
				distBtn.setEnabled(false);
			}
		}
	}


	public Clipboard(BoundaryParamsPanel boundaryParamsPane, JTextField maxMillerFld, final JCheckBox BTChB, final JCheckBox BminChB, LatticePanel lattPane) {


		this.latticePane = lattPane;
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);		
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);	

		lastName = null;
		
		gbParamsPane = boundaryParamsPane;
		maxFld = maxMillerFld;

		boundaryId = 0;
		tMap = new TreeMap<String, InterfaceMatrix>();

		setLayout(new MigLayout("insets 0", "[][][]", "[][][][][]"));

		JButton copyBtn = new JButton();
		copyBtn.setToolTipText("Copy boundary parameters to the clipboard");
		copyBtn.setIcon(new ImageIcon(Clipboard.class.getResource("/gui_bricks/add-icon.png")));
		copyBtn.setMinimumSize(new Dimension(24,24));
		copyBtn.setMaximumSize(new Dimension(24,24));
		copyBtn.setPreferredSize(new Dimension(24,24));
		add(copyBtn, "cell 0 0");


		String[] columnNames = { "Label" };
		Object[][] data = {  };
		tabModel = new DefaultTableModel(data, columnNames);
		table = new JTable(tabModel);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		table.getModel().addTableModelListener(this);
		table.getSelectionModel().addListSelectionListener(this);
		table.getColumnModel().getSelectionModel().addListSelectionListener(this);
		table.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setToolTipText("To edit the label of a boundary, double-click on a given item of the list");

		//	scrollPane.setMaximumSize(new Dimension(85,130));
		//	scrollPane.setMinimumSize(new Dimension(85,130));
		scrollPane.setPreferredSize(new Dimension(110,135));
		add(scrollPane, "cell 1 0 1 5,aligny top");

		table.setFillsViewportHeight(false);
		scrollPane.setViewportView(table);
		listModel = new DefaultListModel();

		lblCmp = new JLabel("<html>Distance between<br>two boundaries:");
		lblCmp.setToolTipText("<html>Select two boundaries from the clipboard using Ctrl and Shift keys and the left mouse button,<br>then press the button below");
		add(lblCmp, "flowx,cell 2 0,gapx 20");

		pasteBtn = new JButton();
		pasteBtn.setToolTipText("Paste parameters of the selected boundary");
		pasteBtn.setEnabled(false);
		pasteBtn.setMinimumSize(new Dimension(24,24));
		pasteBtn.setMaximumSize(new Dimension(24,24));
		pasteBtn.setPreferredSize(new Dimension(24,24));
		pasteBtn.setIcon(new ImageIcon(Clipboard.class.getResource("/gui_bricks/back.png")));
		add(pasteBtn, "cell 0 1");
		
				distBtn = new JButton();
				distBtn.setToolTipText("<html>Select two boundaries from the clipboard using Ctrl and Shift keys and the left mouse button,<br>then press this button");
				distBtn.setEnabled(false);
				distBtn.setIcon(new ImageIcon(Clipboard.class.getResource("/gui_bricks/dist.png")));
				distBtn.setPreferredSize(new Dimension(24,24));
				distBtn.setMinimumSize(new Dimension(24,24));
				distBtn.setMaximumSize(new Dimension(24,24));
				add(distBtn, "flowx,cell 2 1,alignx right,gapx 20 5");
				
						distBtn.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent evt) {
				
				
								final int[] rows = table.getSelectedRows();
				
								String s = (String)tabModel.getValueAt(rows[0], 0);
								InterfaceMatrix B1 = tMap.get(s);
				
								s = (String)tabModel.getValueAt(rows[1], 0);
								InterfaceMatrix B2 = tMap.get(s);
				
								distFld.setText(df.format(Math.toDegrees(B1.distance(B2, gbParamsPane.getLatticePane().getPointGroup(), BTChB.isSelected(), BminChB.isSelected()))));
								
								distMFld.setText(df.format(Math.toDegrees(B1.distanceMisorSpace(B2, gbParamsPane.getLatticePane().getPointGroup(), BTChB.isSelected()))));
								
								distPFld.setText(df.format(Math.toDegrees(B1.distancePlaneSpace(B2, gbParamsPane.getLatticePane().getPointGroup(), BTChB.isSelected(), BminChB.isSelected()))));
				
							//	misorDeltaFld.setText( df.format(Math.toDegrees(B1.misorDiff(B2, gbParamsPane.getLatticePane().getPointGroup(), allowChB.isSelected()))) );
				
							}
						});

		removeBtn = new JButton();
		removeBtn.setToolTipText("<html>Remove boundaries from the clipboard;<br>\r\nSelect boundaries to be removed using Ctrl and Shift keys and the left mouse button");
		removeBtn.setEnabled(false);
		removeBtn.setMinimumSize(new Dimension(24,24));
		removeBtn.setMaximumSize(new Dimension(24,24));
		removeBtn.setPreferredSize(new Dimension(24,24));
		removeBtn.setIcon(new ImageIcon(Clipboard.class.getResource("/gui_bricks/remove.png")));
		add(removeBtn, "cell 0 2,aligny top");
				
						distLbl = new JLabel("\u03B4 =");
						distLbl.setToolTipText("Distance between the selected boundaries defined in the five-dimensional boundary parameter space");
						add(distLbl, "flowx,cell 2 2,alignx right,gapx 20");
		
				//misorLbl = new JLabel("\u0394\u03A9 =");
				//misorLbl.setToolTipText("Difference in misorientation between selected boundaries");
				//add(misorLbl, "flowx,cell 2 2,alignx right");
		
				distFld = new JTextField();
				distFld.setToolTipText("Distance between the selected boundaries defined in the five-dimensional boundary parameter space");
				distFld.setHorizontalAlignment(SwingConstants.RIGHT);
				distFld.setEditable(false);
				add(distFld, "cell 2 2,alignx right");
				distFld.setColumns(4);

		exportBtn = new JButton();
		exportBtn.setToolTipText("Export content of the clipboard to a file");
		exportBtn.setIcon(new ImageIcon(Clipboard.class.getResource("/gui_bricks/export.png")));

		exportBtn.setMinimumSize(new Dimension(24,24));
		exportBtn.setMaximumSize(new Dimension(24,24));
		exportBtn.setPreferredSize(new Dimension(24,24));
		add(exportBtn, "cell 0 3,aligny top");
		
		distMlbl = new JLabel("<html>\u03B4<sub>m</sub>=");
		distMlbl.setToolTipText("Distance between the selected boundaries in the misorientation space");
		add(distMlbl, "flowx,cell 2 3,alignx right");
		
		distMFld = new JTextField();
		distMFld.setToolTipText("Distance between the selected boundaries in the misorientation subspace");
		distMFld.setHorizontalAlignment(SwingConstants.RIGHT);
		distMFld.setEditable(false);
		add(distMFld, "cell 2 3,alignx right");
		distMFld.setColumns(4);

		importBtn = new JButton();
		importBtn.setToolTipText("Import boundaries from a file");
		importBtn.setIcon(new ImageIcon(Clipboard.class.getResource("/gui_bricks/folder.png")));
		importBtn.setPreferredSize(new Dimension(24, 24));
		importBtn.setMinimumSize(new Dimension(24, 24));
		importBtn.setMaximumSize(new Dimension(24, 24));
		add(importBtn, "cell 0 4,aligny top");
		
		distPlbl = new JLabel("<html>\u03B4<sub>p</sub>=");
		distPlbl.setToolTipText("Distance between the selected boundaries in the boundary plane space");
		add(distPlbl, "flowx,cell 2 4,alignx right");
		
		distPFld = new JTextField();
		distPFld.setToolTipText("Distance between the selected boundaries in the boundary plane subspace");
		distPFld.setHorizontalAlignment(SwingConstants.RIGHT);
		distPFld.setEditable(false);
		add(distPFld, "cell 2 4,alignx right");
		distPFld.setColumns(4);
		
		label_4 = new JLabel("\u00B0");
		add(label_4, "cell 2 4,alignx right");
		
		label_3 = new JLabel("\u00B0");
		add(label_3, "cell 2 3,alignx right");
		
				//misorDeltaFld = new JTextField();
				//misorDeltaFld.setToolTipText("Difference in misorientation between selected boundaries");
				//misorDeltaFld.setEditable(false);
				//misorDeltaFld.setHorizontalAlignment(SwingConstants.RIGHT);
				//add(misorDeltaFld, "cell 2 2,alignx right");
				//misorDeltaFld.setColumns(4);
		
				//label = new JLabel("\u00B0");
				//add(label, "cell 2 2,alignx right");
		
				label_1 = new JLabel("\u00B0");
				add(label_1, "cell 2 2,alignx right");

		final JFileChooser importFc = new JFileChooser() ;
	//	importFc.setAcceptAllFileFilterUsed(false);
		importFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		importFc.setFileFilter(new FileUtils.ClipboardFileFilter());

		importBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {

				if(tMap.size() > 0) {
					int answer = JOptionPane.showConfirmDialog(
							null,
							"All unsaved boundaries will be lost\n" +
									"Do you want to continue?",
									"Warning",
									JOptionPane.YES_NO_OPTION);

					if(answer == JOptionPane.NO_OPTION) return;
				}

				importFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int returnVal = importFc.showDialog(Clipboard.this, "Import");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					ArrayList<String> keys = new ArrayList<String>();
					ArrayList<InterfaceMatrix> gbs = new ArrayList<InterfaceMatrix>();
					try {

						final BufferedReader in = new BufferedReader(new FileReader(importFc.getSelectedFile()));

												String line = null;

						while ((line = in.readLine()) != null) {

							if(!line.matches("^\\s*#.*")) {
															
								final String[] num = line.trim().split("\\s+");
														
	
								final EulerAngles eul = new EulerAngles();
								eul.set(Math.toRadians(Double.parseDouble(num[0])),
										Math.toRadians(Double.parseDouble(num[1])),
										Math.toRadians(Double.parseDouble(num[2])));
	
								final Matrix3x3 M = new Matrix3x3();
								M.set(eul);
	
								final UnitVector m1 = new UnitVector();
								m1.set(Math.toRadians(Double.parseDouble(num[3])), Math.toRadians(Double.parseDouble(num[4])));
	
								final InterfaceMatrix B = new InterfaceMatrix(M, m1);
	
								final StringBuilder str = new StringBuilder();
								for(int i = 5; i < num.length; i++) {
									str.append(num[i]);
									if(i != num.length - 1) str.append(' ');
								}
								keys.add(str.toString());
								gbs.add(B);
							}
						}
					} catch(IOException | NumberFormatException exc) {

						JOptionPane.showMessageDialog(null,
								"An I/O error occurred",
								"Import failed",
								JOptionPane.ERROR_MESSAGE);								
						return;
					} 

					boundaryId = 0;
					tMap.clear();
					while(tabModel.getRowCount() > 0) tabModel.removeRow(0);
					for(int i = 0; i < keys.size(); i++) {

						tMap.put(keys.get(i), gbs.get(i));
						tabModel.addRow(new Object[]{keys.get(i)});			

						table.scrollRectToVisible(table.getCellRect(tabModel.getRowCount() - 1, 0, true));  

					}
				}	
			}	
		});


		final JFileChooser exportFc = new JFileChooser() {
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
				}/* else { 			  

					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);

					if(ext != null) {
						if(ext.compareTo("clipboard") != 0) fName += ".clipboard";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "clipboard";
						else fName += ".clipboard";
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
				}*/
				super.approveSelection();  
			}   
		};

	//	exportFc.setAcceptAllFileFilterUsed(false);
		exportFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	//	exportFc.setFileFilter(new FileUtils.ClipboardFileFilter());

		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				DefaultCellEditor dce = (DefaultCellEditor)table.getCellEditor();
				if (dce != null) dce.stopCellEditing();

				if(tMap.size() < 1) {
					JOptionPane.showMessageDialog(null,
							"There are no boundaries in the clipboard.",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				int returnVal = exportFc.showDialog(Clipboard.this, "Export");
				if(returnVal == JFileChooser.APPROVE_OPTION) {

					DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);										
					DecimalFormat df4 = new DecimalFormat("0.####", otherSymbols);

					try {

						final File f = exportFc.getSelectedFile();
						String fName = f.getAbsolutePath();						
						/*final String ext = FileUtils.getExtension(f);

						if(ext != null) {
							if(ext.compareTo("clipboard") != 0) fName += ".clipboard";
						} else {							
							if(fName.charAt(fName.length() - 1) == '.') fName += "clipboard";
							else fName += ".clipboard";
						}*/

						final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fName)));

					    writer.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
						writer.println("# it contains boundary parameters exported from the clipboard");
						writer.println("# which is included it the module for testing of individual boundaries.");
						writer.println("# Subsequent columns: M_EUL_PHI1 M_EUL_PHI M_EUL_PHI2 M1_ZENITH M1_AZIMUTH LABEL");

						for (Map.Entry<String,InterfaceMatrix> entry : tMap.entrySet()) {
							final InterfaceMatrix B = entry.getValue();
							final String key = entry.getKey();

							final EulerAngles eul = new EulerAngles();
							eul.set(B.M());

							writer.println(df4.format(Math.toDegrees(eul.phi1())) + " " +
									df4.format(Math.toDegrees(eul.Phi())) + " " +
									df4.format(Math.toDegrees(eul.phi2())) + " " +
									df4.format(Math.toDegrees(B.m1().zenith())) + " " +
									df4.format(Math.toDegrees(B.m1().azimuth())) + " " + key );

						}

						writer.close();
					} catch(IOException exc) {	
						JOptionPane.showMessageDialog(null,
								"An I/O error occured.",
								"Export failed",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

				}
			}
		});


		removeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

				DefaultCellEditor dce = (DefaultCellEditor)table.getCellEditor();
				if (dce != null) dce.stopCellEditing();

				if(table.getSelectedRows().length > 0) {

					int[] tmp = table.getSelectedRows();
					int[] selectedRows = table.getSelectedRows();

					for (int i = tmp.length-1; i >=0; i--) {
						selectedRows = table.getSelectedRows();


						String s = (String)tabModel.getValueAt(selectedRows[i], 0);
						tMap.remove(s);
						tabModel.removeRow(selectedRows[i]);


					} // end-for
				} // end-if


			}
		});


		pasteBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

				DefaultCellEditor dce = (DefaultCellEditor)table.getCellEditor();
				if (dce != null) dce.stopCellEditing();

				String s = (String)tabModel.getValueAt(table.getSelectedRow(), 0);								
				InterfaceMatrix B = tMap.get(s);


				int maxIndex;
				
				try {
					
					maxIndex = Integer.parseInt(maxFld.getText());
					if(maxIndex < 1 || maxIndex > ConstantsAndStatic.MAXMILLER) throw new NumberFormatException();
					
				} catch(NumberFormatException exc) {
					
					JOptionPane.showMessageDialog(null,
							"Maximum allowed value for Miller indices must be a positive integer not greater than " + ConstantsAndStatic.MAXMILLER + ".",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

			
				try {					
					gbParamsPane.setPlane(B.m1(), maxIndex);		
				} catch(NumberFormatException exc) {

					if(latticePane.getPointGroup() != PointGroup._6MMM) {
						JOptionPane.showMessageDialog(null,							
							"Lattice parameters must be positive decimal numbers.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					return;
				}

																				
				switch(gbParamsPane.getMisorPane().getMisorAs()) {
				case AXISANGLE:					
					AxisAngle aa = new AxisAngle();
					aa.set(B.M());						
					gbParamsPane.getMisorPane().getAxisAngleFlds().setAxis(aa.axis(), maxIndex);
					gbParamsPane.getMisorPane().getAxisAngleFlds().setAngle(aa.angle());						
					break;

				case EULER:
					EulerAngles eul = new EulerAngles();
					eul.set(B.M());
					gbParamsPane.getMisorPane().getEulerFlds().setAngles(eul);
					break;

				case MATRIX:
					gbParamsPane.getMisorPane().getMatrixFlds().setMatrix(B.M());
					break;

				case QUATERNION:
					Quaternion quat = new Quaternion();
					quat.set(B.M());
					gbParamsPane.getMisorPane().getQuatFlds().setQuaternion(quat);
					break;

				case RODRIGUES:
					RodriguesParams rodr = new RodriguesParams();
					rodr.set(B.M());
					if(rodr.isHalfTurn()) {
						JOptionPane.showMessageDialog(null,
								"<html>Provided misorientation is a half-turn. Rodrigues parameters are infinite.<br>Another parameterization is recommended.",
								"Warning",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					gbParamsPane.getMisorPane().getRodriguesFlds().setRodrigues(rodr);
					break;

				default: break;
				}


			}
		});


		copyBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {

				DefaultCellEditor dce = (DefaultCellEditor)table.getCellEditor();
				if (dce != null) dce.stopCellEditing();

				Matrix3x3 M = new Matrix3x3();

				UnitVector m1;
				try {					
					m1 = gbParamsPane.getPlaneNormal();	
				} catch(NumberFormatException exc) {

					if(latticePane.getPointGroup() == PointGroup.M3M) {
						JOptionPane.showMessageDialog(null,						
								"Miller indices of the boundary plane must be integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					} else if(latticePane.getPointGroup() != PointGroup._6MMM) {
						JOptionPane.showMessageDialog(null,	
							"Miller indices of the boundary plane must be integers.\n" +
							"Lattice parameters must be positive decimal numbers.",
							"Error",
							JOptionPane.ERROR_MESSAGE);					
					} else {
						JOptionPane.showMessageDialog(null,
								"Miller indices of the boundary plane must be integers.\n" +
								"Nominator and denominator of (c/a)\u00b2 ratio must be positive integers.",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					return;
				}
									
				
				switch(gbParamsPane.getMisorPane().getMisorAs()) {
				case AXISANGLE:
					double theta;					
					try {
						theta = gbParamsPane.getMisorPane().getAxisAngleFlds().getAngle();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Rotation angle must be a decimal number.",
								"Could not save the boundary",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					UnitVector n;
					try {
						n = gbParamsPane.getMisorPane().getAxisAngleFlds().getAxis();								
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Miller indices of the rotation axis must be integers.",
								"Could not save the boundary",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					AxisAngle aa = new AxisAngle();
					aa.set(n, theta);
					M.set(aa);

					break;

				case EULER:
					try {
						EulerAngles eul = gbParamsPane.getMisorPane().getEulerFlds().getAngles();
						M.set(eul);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Euler angles must be decimal numbers.",
								"Could not save the boundary",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				case MATRIX:
					try {
						M = gbParamsPane.getMisorPane().getMatrixFlds().getMatrix();
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Matrix elements must be decimal numbers.",
								"Could not save the boundary",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					final Matrix3x3 orth = M.nearestOrthogonal();
					final double dist = M.distSq(orth);

					if(dist < 1e-4d && Math.abs(M.det() - 1d) < 1e-3d) {
						M = orth;						
					} else {						
						M = orth;						
						if(M.det() > 0d) {
							int answer = JOptionPane.showConfirmDialog(
									null,
									"Provided rotation matrix is not orthogonal.\n" +
											"Would you like to replace it by the nearest orthogonal matrix and continue?",
											"Warning",
											JOptionPane.YES_NO_OPTION);
							if(answer == JOptionPane.NO_OPTION) return; 		
						} else {
							JOptionPane.showMessageDialog(null,
									"Provided matrix is not a proper rotation matrix.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
							return;							
						}																													
					}
					gbParamsPane.getMisorPane().getMatrixFlds().setMatrix(M);

					break;
				case QUATERNION:
					try {
						Quaternion quat = gbParamsPane.getMisorPane().getQuatFlds().getQuaternion();
						M.set(quat);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Quaternion components must be decimal numbers.",
								"Could not save the boundary",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				case RODRIGUES:
					try {
						RodriguesParams rodr = gbParamsPane.getMisorPane().getRodriguesFlds().getRodrigues();
						M.set(rodr);
					} catch(NumberFormatException exc) {
						JOptionPane.showMessageDialog(null,
								"Rodrigues parameters must be decimal numbers.",
								"Could not save the boundary",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					break;
				default: break;
				}



				final InterfaceMatrix B = new InterfaceMatrix(M, m1);

				saveGB(B);

			}

		});
	}


	private final boolean contains(String desc) {

		final int size = table.getModel().getRowCount();

		for(int i = 0; i < size; i++) if(desc.compareTo( (String) table.getModel().getValueAt(i, 0)) == 0) return true;

		return false;
	}


	private final boolean contains(String desc, int index) {

		final int size = table.getModel().getRowCount();

		for(int i = 0; i < size; i++) if(i != index) if(desc.compareTo( (String) table.getModel().getValueAt(i, 0)) == 0) return true;

		return false;
	}



	public final void saveGB(InterfaceMatrix B) {


		if(tMap.size() < 100) {

			String str;
			do {		
				boundaryId++;
				str = "User's GB " + boundaryId;	
			} while(contains(str) == true);

			tMap.put(str, B);
			tabModel.addRow(new Object[]{str});			

			table.scrollRectToVisible(table.getCellRect(tabModel.getRowCount()-1, 0, true));  

		} else {
			JOptionPane.showMessageDialog(null,
					"Too many boundaries in the clipboard.",
					"Could not save the boundary",
					JOptionPane.ERROR_MESSAGE);	
		}
	}


	@Override
	public void tableChanged(TableModelEvent e) {


		int row1 = e.getFirstRow();
		int row2 = e.getLastRow();
		int col = e.getColumn();
		int type = e.getType();

		if(type == TableModelEvent.UPDATE && col == 0 && row1 == row2) {

			String newName = (String)tabModel.getValueAt(row1, col);

			if(newName.compareTo("") == 0) {
				JOptionPane.showMessageDialog(null,
						"Description of the boundary cannot be empty.",
						"Wrong boundary description",
						JOptionPane.ERROR_MESSAGE);
				tabModel.setValueAt(lastName, row1, col);
				return;
			}

			if(contains(newName, row1) == true) {
				JOptionPane.showMessageDialog(null,
						"Two boundaries must not have the same description.\n" +
								"Please choose different description for this boundary.",
								"Wrong boundary description.",
								JOptionPane.ERROR_MESSAGE);

				tabModel.setValueAt(lastName, row1, col);
				return;
			}
			InterfaceMatrix B = tMap.get(lastName);
			tMap.remove(lastName);
			tMap.put((String)tabModel.getValueAt(row1, col), B);

		}


	}

}
