package gui_bricks;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import utils.AxisAngle;
import utils.EulerAngles;
import utils.Matrix3x3;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.UnitVector;
import enums.MisorientationAs;


public class MisorPanel extends JPanel {
	
	private AxisAngleFields aaFlds;
	private EulerAnglesFields eulFlds;
	private MisorMatrixFields matrixFlds;
	private QuaternionFields quatFlds;
	private RodriguesParamsFields rodrFlds;
	private JPanel misorCards;
	
	
	private static final String AXISANGLE = "as an axis and an angle";
	private static final String EULER = "as Euler angles";
	private static final String MATRIX = "as a rotation matrix";
	private static final String QUATERNION = "as a quaternion";
	private static final String RODRIGUES = "as Rodrigues parameters";
	
		
	private static final String MILLER_TO_DEFAULT = "<html>Incorrect maximum allowed value of Miller indices<br>is changed to the default value, i.e. 20.";
	
	private MisorientationAs misorAs;
	private LatticePanel latticePane;
	private JTextField maxFld;

	
	public final MisorientationAs getMisorAs() {
		return misorAs;
	}
 
	public final AxisAngleFields getAxisAngleFlds() {
		return aaFlds;
	}
	
	public final EulerAnglesFields getEulerFlds() {
		return eulFlds;
	}
	
	public final QuaternionFields getQuatFlds() {
		return quatFlds;
	}
	
	public final RodriguesParamsFields getRodriguesFlds() {
		return rodrFlds;
	}
	
	public final MisorMatrixFields getMatrixFlds() {
		return matrixFlds;
	}
	
	
	private boolean fourIndices;
	

	public final void setFourIndicesVisible(boolean b) {
		
		fourIndices = b;
			
		if(misorAs == MisorientationAs.AXISANGLE) {
			aaFlds.setFourIndicesVisible(b);
		}
	}
	


	public MisorPanel(String misorText, LatticePanel latticePane, JTextField maxFld) {
		
		
		misorAs = MisorientationAs.AXISANGLE;
		fourIndices = false;
		this.latticePane = latticePane;
		this.maxFld = maxFld;
		
		setLayout(new MigLayout("insets 0", "[][]", "[][]"));
		
		JLabel misorLbl = new JLabel(misorText);
		add(misorLbl, "cell 0 0,alignx trailing,gapx 0");
		
		JComboBox misorCb = new JComboBox();
		misorCb.setToolTipText("<html>If correct misorientation parameters are provided,<br>then parameterization selection allows for conversions between different misorienation representations");
		misorCb.setModel(new DefaultComboBoxModel(new String[] {AXISANGLE, EULER, MATRIX, QUATERNION, RODRIGUES}));
		
		misorCb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JComboBox cb = (JComboBox) evt.getSource();
                String s = cb.getSelectedItem().toString();
                    
                final MisorientationAs oldMisor = misorAs;
                
                switch(s) {
                case AXISANGLE:    
                	                	
                	misorAs = MisorientationAs.AXISANGLE;
                	aaFlds.setFourIndicesVisible(fourIndices); 
                	CardLayout cl = (CardLayout) misorCards.getLayout();
                	cl.show(misorCards, AXISANGLE);  
                	                	                	
                	break;
                case EULER:
                	
                	misorAs = MisorientationAs.EULER;
                	cl = (CardLayout) misorCards.getLayout();
                	cl.show(misorCards, EULER);                	
                	                	
                	break;
                case MATRIX:
                	
                	misorAs = MisorientationAs.MATRIX;
                	cl = (CardLayout) misorCards.getLayout();
                	cl.show(misorCards, MATRIX);                	
                	                	
                	break;
                case QUATERNION:
                	
                	misorAs = MisorientationAs.QUATERNION;
                	cl = (CardLayout) misorCards.getLayout();
                	cl.show(misorCards, QUATERNION);                	
                	                	
                	break;
                case RODRIGUES:
                	
                	misorAs = MisorientationAs.RODRIGUES;
                	cl = (CardLayout) misorCards.getLayout();
                	cl.show(misorCards, RODRIGUES);                	
                	                	
                	break;
                	                	
                	default: break;
                }
                
                convert(oldMisor, misorAs);
            }
        });		
		
		add(misorCb, "cell 1 0,alignx left");
		
		misorCards = new JPanel();
		add(misorCards, "cell 1 1,grow");
		
		aaFlds = new AxisAngleFields(latticePane);
		eulFlds = new EulerAnglesFields();
		matrixFlds = new MisorMatrixFields();
		quatFlds = new QuaternionFields();
		rodrFlds = new RodriguesParamsFields();
		misorCards.setLayout(new CardLayout(0, 0));
		
		misorCards.add(aaFlds, AXISANGLE);
		misorCards.add(eulFlds, EULER);
		misorCards.add(matrixFlds, MATRIX);
		misorCards.add(quatFlds, QUATERNION);
		misorCards.add(rodrFlds, RODRIGUES);
		

	}
	
	
	private final void convert(MisorientationAs from, MisorientationAs to) {
				
		switch (from) {
		case AXISANGLE:
			
			boolean clear = false;
			
			double omega = 0;
			try {
				omega = aaFlds.getAngle();
			} catch(NumberFormatException exc) {
				clear = true;
			}
			
			UnitVector n = null;
			try {						
				n = aaFlds.getAxis();
			} catch(NumberFormatException exc) {
				clear = true;
			} catch(IllegalArgumentException exc) {
				clear = true;
			}
			
			AxisAngle aa = null;
			if(!clear) {
				aa = new AxisAngle();
				aa.set(n, omega);
			}
			
			switch (to) {
			case AXISANGLE:
				//AXISANGLE->AXISANGLE
				if(clear) aaFlds.clear(); else {
					
					int maxIdx = 20;
					boolean warn = false;
					try {
						maxIdx = Integer.parseInt(maxFld.getText());
						
					} catch(NumberFormatException exc) {
						maxIdx = 20;
						maxFld.setText("20");
						warn = true;	
					}	
					
					aaFlds.setAngle(omega);
					aaFlds.setAxis(n, maxIdx);
					
					if(warn) JOptionPane.showMessageDialog(null,
						    MILLER_TO_DEFAULT,
						    "Warning", JOptionPane.INFORMATION_MESSAGE);
				}
				
				break;
			case EULER:
				//AXISANGLE->EULER
				if(clear) eulFlds.clear(); else {
					EulerAngles eul = new EulerAngles();
					eul.set(aa);
					eulFlds.setAngles(eul);
				}
				
				break;
			case MATRIX:
				//AXISANGLE->MATRIX
				if(clear) matrixFlds.clear(); else {
					Matrix3x3 M = new Matrix3x3();
					M.set(aa);
					matrixFlds.setMatrix(M);
				}
				
				break;
			case QUATERNION:
				//AXISANGLE->QUATERNION
				if(clear) quatFlds.clear(); else {
					Quaternion quat = new Quaternion();
					quat.set(aa);
					quatFlds.setQuaternion(quat);
				}
				
				break;						
			case RODRIGUES:
				//AXISANGLE->RODRIGUES
				if(clear) rodrFlds.clear(); else {
					RodriguesParams rod = new RodriguesParams();
					rod.set(aa);
					rodrFlds.setRodrigues(rod);
				}
				
				break;
			default:
				break;
			}
			
			break;
			
		case EULER:
			
			clear = false;
			EulerAngles eul = null;
			try {
				eul = eulFlds.getAngles();
			} catch(NumberFormatException exc) {
				clear = true;
			}
			
			switch (to) {
			case AXISANGLE:
				//EULER->AXISANGLE
				
				if(clear) aaFlds.clear(); else {
					
					int maxIdx = 20;
					try {
						maxIdx = Integer.parseInt(maxFld.getText());
						
					} catch(NumberFormatException exc) {
						maxIdx = 20;
						maxFld.setText("20");
						
						JOptionPane.showMessageDialog(null,
							    MILLER_TO_DEFAULT,
							    "Warning", JOptionPane.INFORMATION_MESSAGE);
					}				
					
					aa = new AxisAngle();
					aa.set(eul);
					aaFlds.setAngle(aa.angle());
					aaFlds.setAxis(aa.axis(), maxIdx);
				}				
				
				break;
			case EULER:
				//EULER->EULER
				if(clear) eulFlds.clear(); else eulFlds.setAngles(eul);
				
				break;
			case MATRIX:
				//EULER->MATRIX				
				if(clear) matrixFlds.clear(); else {
					Matrix3x3 M = new Matrix3x3();
					M.set(eul);					
					matrixFlds.setMatrix(M);
				}
				
				break;
			case QUATERNION:
				//EULER->QUATERNION
				if(clear) quatFlds.clear(); else {					
					Quaternion quat = new Quaternion();
					quat.set(eul);	
					quatFlds.setQuaternion(quat);
				}
				
				break;						
			case RODRIGUES:
				//EULER->RODRIGUES
				if(clear) rodrFlds.clear(); else {
					RodriguesParams rod = new RodriguesParams();
					rod.set(eul);
					rodrFlds.setRodrigues(rod);
				}
				
				break;
			default:
				break;
			}
									
			break;
			
		case MATRIX:
			
			clear = false;
			Matrix3x3 M = null;
			try {
				M = matrixFlds.getMatrix();
			} catch(NumberFormatException exc) {
				clear = true;
			}
			
			switch (to) {
			case AXISANGLE:
				//MATRIX->AXISANGLE
				if(clear) aaFlds.clear(); else {
					int maxIdx = 20;
					try {
						maxIdx = Integer.parseInt(maxFld.getText());
						
					} catch(NumberFormatException exc) {
						maxIdx = 20;
						maxFld.setText("20");
						
						JOptionPane.showMessageDialog(null,
							    MILLER_TO_DEFAULT,
							    "Warning", JOptionPane.INFORMATION_MESSAGE);
					}				
					
					aa = new AxisAngle();
					aa.set(M);
					aaFlds.setAngle(aa.angle());
					aaFlds.setAxis(aa.axis(), maxIdx);
				}
				
				break;
			case EULER:
				//MATRIX->EULER
				if(clear) eulFlds.clear(); else {
					eul = new EulerAngles();
					eul.set(M);
					eulFlds.setAngles(eul);
				}
				
				break;
			case MATRIX:
				//MATRIX->MATRIX						
				if(clear) matrixFlds.clear(); else matrixFlds.setMatrix(M);
				
				break;
			case QUATERNION:
				//MATRIX->QUATERNION
				if(clear) quatFlds.clear(); else {
					Quaternion quat = new Quaternion();
					quat.set(M);
					quatFlds.setQuaternion(quat);
				}
				
				break;						
			case RODRIGUES:
				//MATRIX->RODRIGUES
				if(clear) rodrFlds.clear(); else {
					RodriguesParams rod = new RodriguesParams();
					rod.set(M);
					rodrFlds.setRodrigues(rod);
				}
				
				break;
			default:
				break;
			}
			
			break;
			
		case QUATERNION:

			clear = false;
			Quaternion quat = null;
			try {
				quat = quatFlds.getQuaternion();
			} catch(NumberFormatException exc) {
				clear = true;
			}
			
			switch (to) {
			case AXISANGLE:
				//QUAT->AXISANGLE
				if(clear) aaFlds.clear(); else {
					int maxIdx = 20;
					try {
						maxIdx = Integer.parseInt(maxFld.getText());
						
					} catch(NumberFormatException exc) {
						maxIdx = 20;
						maxFld.setText("20");
						
						JOptionPane.showMessageDialog(null,
							    MILLER_TO_DEFAULT,
							    "Warning", JOptionPane.INFORMATION_MESSAGE);
					}				
				
					aa = new AxisAngle();
					aa.set(quat);
					aaFlds.setAngle(aa.angle());
					aaFlds.setAxis(aa.axis(), maxIdx);
				}
				break;
			case EULER:
				//QUAT->EULER
				if(clear) eulFlds.clear(); else {
					eul = new EulerAngles();
					eul.set(quat);
					eulFlds.setAngles(eul);
				}
				
				break;
			case MATRIX:
				//QUAT->MATRIX
				if(clear) matrixFlds.clear(); else {
					M = new Matrix3x3();
					M.set(quat);
					matrixFlds.setMatrix(M);
				}
				
				break;
			case QUATERNION:
				//QUAT->QUATERNION
				if(clear) quatFlds.clear(); else quatFlds.setQuaternion(quat);
				
				break;						
			case RODRIGUES:
				//QUAT->RODRIGUES
				if(clear) rodrFlds.clear(); else {
					RodriguesParams rod = new RodriguesParams();
					rod.set(quat);
					rodrFlds.setRodrigues(rod);
				}
				break;
			default:
				break;
			}
			
			break;
			
		case RODRIGUES:
			
			clear = false;
			RodriguesParams rod = null;
			try {
				rod = rodrFlds.getRodrigues();
			} catch(NumberFormatException exc) {
				clear = true;
			}
			
			switch (to) {
			case AXISANGLE:
				//RODRIGUES->AXISANGLE
				if(clear) aaFlds.clear(); else {
					
					int maxIdx = 20;
					try {
						maxIdx = Integer.parseInt(maxFld.getText());
						
					} catch(NumberFormatException exc) {
						maxIdx = 20;
						maxFld.setText("20");
						
						JOptionPane.showMessageDialog(null,
							    MILLER_TO_DEFAULT,
							    "Warning", JOptionPane.INFORMATION_MESSAGE);
					}				
					aa = new AxisAngle();
					aa.set(rod);
					aaFlds.setAngle(aa.angle());
					aaFlds.setAxis(aa.axis(), maxIdx);
				}
				
				break;
			case EULER:
				//RODRIGUES->EULER
				if(clear) eulFlds.clear(); else {
					eul = new EulerAngles();
					eul.set(rod);
					eulFlds.setAngles(eul);
				}
				
				break;
			case MATRIX:
				//RODRIGUES->MATRIX
				if(clear) matrixFlds.clear(); else {
					M = new Matrix3x3();
					M.set(rod);
					matrixFlds.setMatrix(M);
				}
				
				break;
			case QUATERNION:
				//RODRIGUES->QUATERNION
				if(clear) quatFlds.clear(); else {
					quat = new Quaternion();
					quat.set(rod);
					quatFlds.setQuaternion(quat);
				}
				break;						
			case RODRIGUES:
				//RODRIGUES->GIBBS
				if(clear) rodrFlds.clear(); else rodrFlds.setRodrigues(rod);
				
				break;
			default:
				break;
			}
			
			break;
			
		default:
			break;
			
		}
	}
	
}
