package gui_modules;

import enums.MisorientationAs;
import enums.PointGroup;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import say.swing.JFontChooser;
import utils.AxisAngle;
import utils.EulerAngles;
import utils.FileUtils;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.Quaternion;
import utils.RodriguesParams;
import utils.Transformations;
import algorithms.SymmetryFigure;

public class SymmetriesFrame extends JFrame {

	private JPanel contentPane;
		
	private Color cFont;
		
	private FileUtils.PNGFileFilter pngFilter = new FileUtils.PNGFileFilter();
	private FileUtils.EPSFileFilter epsFilter = new FileUtils.EPSFileFilter();
		
	private Font font;
	private int maxIdx;
	
	private PointGroup ptGrp;

	private double a, b, c;

	public SymmetriesFrame(Matrix3x3 M, PointGroup ptGrp, MisorientationAs misorAs, double a, double b, double c, int idx) {
		
		setTitle("GBToolbox: Symmetries of grain boundary distributions");
		setIconImage(Toolkit.getDefaultToolkit().getImage(SymmetriesFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
		
		//init
		maxIdx = idx;
		
		this.a = a;
		this.b = b;
		this.c = c;
		
		cFont = Color.BLACK;
		
		font = new Font("Arial", Font.PLAIN, 12);
		
		this.ptGrp = ptGrp;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][][][]", "[][][][]"));
		
		JLabel lblMisorientation = new JLabel("<html><b>Misorientation:</b>");
		contentPane.add(lblMisorientation, "cell 0 0 4 1");
		
		final SymmetryFigure polePane = new SymmetryFigure(M, ptGrp, a, b, c, maxIdx);
		contentPane.add(polePane, "cell 4 0 1 4,gapx 20");
		
		JLabel lblMisorDetails = new JLabel(misorToLabel(M, misorAs));
		contentPane.add(lblMisorDetails, "cell 0 1 4 1,gapx 10");
		
		JLabel lblPtGrpDetails = new JLabel("<html><b>Point group:</b>");
		contentPane.add(lblPtGrpDetails, "flowx,cell 0 2 4 1,gapy 5");
		
		final JFontChooser fontChooser = new JFontChooser();
		
		JButton pngBtn = new JButton("PNG");
		pngBtn.setToolTipText("<html>Export the <i>map</i> to a PNG file");
		contentPane.add(pngBtn, "flowx,cell 0 3 4 1,alignx center,aligny top,gapy 20");
		
		JButton epsBtn = new JButton("EPS");
		epsBtn.setToolTipText("<html>Export the <i>map</i> to an EPS file");
		contentPane.add(epsBtn, "cell 0 3 4 1,alignx center,aligny top,gapy 20");
		
		JLabel ptGrpLbl = new JLabel(pointGrpToString(ptGrp));
		contentPane.add(ptGrpLbl, "cell 0 2 4 1,gapy 5");
		
		
		final JFileChooser epsFc = new JFileChooser() {
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
						if(ext.compareTo("eps") != 0) fName += ".eps";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "eps";
						else fName += ".eps";
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
		};
		epsFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		epsFc.setAcceptAllFileFilterUsed(false);
		epsFc.addChoosableFileFilter(epsFilter);
		
		epsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				int returnVal = epsFc.showDialog(SymmetriesFrame.this, "Export");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						final File f = epsFc.getSelectedFile();
						String fName = f.getAbsolutePath();						
						final String ext = FileUtils.getExtension(f);
						
						if(ext != null) {
							if(ext.compareTo("eps") != 0) fName += ".eps";
						} else {							
							if(fName.charAt(fName.length() - 1) == '.') fName += "eps";
							else fName += ".eps";
						}
						
						polePane.exportToEPS(fName);
												
					} catch(Exception exc) {
						exc.printStackTrace();
						JOptionPane.showMessageDialog(null,
								"An I/O error occured.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
				}	
				
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				

			}
		});
		
		final JFileChooser pngFc = new JFileChooser() {
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
						if(ext.compareTo("png") != 0) fName += ".png";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "png";
						else fName += ".png";
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
		};
		pngFc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		pngFc.setAcceptAllFileFilterUsed(false);
		pngFc.addChoosableFileFilter(pngFilter);
		
		pngBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
							
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
				int returnVal = pngFc.showDialog(SymmetriesFrame.this, "Export");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						final File f = pngFc.getSelectedFile();
						String fName = f.getAbsolutePath();						
						final String ext = FileUtils.getExtension(f);
						
						if(ext != null) {
							if(ext.compareTo("png") != 0) fName += ".png";
						} else {							
							if(fName.charAt(fName.length() - 1) == '.') fName += "png";
							else fName += ".png";
						}
						
						polePane.exportToPNG(fName);

					} catch(IOException exc) {
						JOptionPane.showMessageDialog(null,
								"An I/O error occured.",
							    "Error",
							    JOptionPane.ERROR_MESSAGE);
						return;
					}
				}	
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}
		});
		
		pack();
		setResizable(false);
	}
	
	public final String pointGrpToString(PointGroup ptGrp) {
	
		switch(ptGrp) {
		case M3M: return "m3\u0305m";
		case _6MMM: 
			return "6/mmm, (c/a)\u00b2 = " + (int)Math.round(c*c) + "/" + (int)Math.round(a*a);
		case _4MMM: 
	
			
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US); 
			DecimalFormat df = new DecimalFormat("0.###", otherSymbols);			
			
			return "4/mmm, a=" + df.format(a) + ", c=" + df.format(c) + " \u212b";
		case MMM:
			otherSymbols = new DecimalFormatSymbols(Locale.US); 
			df = new DecimalFormat("0.###", otherSymbols);	
		
			return "4/mmm, a=" + df.format(a) + ", b=" + df.format(b) + ", c=" + df.format(c) + " \u212b";
		default: break;			
		}
		return "";
				
	}
	
	
	public final String misorToLabel(Matrix3x3 M, MisorientationAs misorAs) {
		
				
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US); 
		DecimalFormat df = new DecimalFormat("0.####", otherSymbols);
		
		switch (misorAs) {
		case AXISANGLE:
			//MATRIX->AXISANGLE
			AxisAngle aa = new AxisAngle();
			aa.set(M);
			String millerStr = "";
			final MillerIndices mill = new MillerIndices();
			
			switch(ptGrp) {
				case M3M:
					mill.setAsCubic(aa.axis(), maxIdx);
					millerStr = mill.h() + "," + mill.k() + "," + mill.l();
					break;
					
				case _6MMM:									
					mill.setAsHexAxis3to4(aa.axis(), maxIdx, Transformations.getCartesianToHex(a, c));
					millerStr = mill.h() + "," + mill.k() + "," + (-mill.h()-mill.k()) + "," + mill.l();
					break;
					
				case _4MMM:
					mill.setAsNonCubicAxis(aa.axis(), maxIdx, Transformations.getCartesianToTetr(a, c));
					millerStr = mill.h() + "," + mill.k() + "," + mill.l();
					break;

				case MMM:					
					mill.setAsNonCubicAxis(aa.axis(), maxIdx, Transformations.getCartesianToOrth(a, b, c));
					millerStr = mill.h() + "," + mill.k() + "," + mill.l();
					break;

					default: break;
			}
			
			
			return "<html><b>n</b> = [" + millerStr + "]; \u03c9 = " + df.format(Math.toDegrees(aa.angle())) + "\u00b0";
			

		case EULER:
			//MATRIX->EULER
			EulerAngles eul = new EulerAngles();
			eul.set(M);
			return "<html>\u03c6<sub>1</sub> = " + df.format(Math.toDegrees(eul.phi1())) + "\u00b0;&nbsp;&nbsp;\u03a6 = " +  df.format(Math.toDegrees(eul.Phi())) +"\u00b0;&nbsp;&nbsp;  \u03c6<sub>2</sub> = " + df.format(Math.toDegrees(eul.phi2()))+"\u00b0";			
			
		case MATRIX:
			//MATRIX->MATRIX	
			df = new DecimalFormat("0.#####");	
			return "<html><table><tr><td>&nbsp;</td><td>" + df.format(M.e00()) + "</td><td>" + df.format(M.e01()) + "</td><td>" + df.format(M.e02()) + "</td></tr><tr><td>M =&nbsp;</td>" +
				   df.format(M.e10()) + "</td><td>" + df.format(M.e11()) + "</td><td>" + df.format(M.e12()) + "</td></tr><tr><td>&nbsp;</td><td>" +
				   df.format(M.e20()) + "</td><td>" + df.format(M.e21()) + "</td><td>" + df.format(M.e22()) + "</td></tr></table>";
			
		case QUATERNION:
			//MATRIX->QUATERNION
			df = new DecimalFormat("0.#####");	

			Quaternion quat = new Quaternion();
			quat.set(M);
			return "<html>q<sub>0</sub> = " + df.format(quat.q0()) + ";&nbsp;&nbsp;q<sub>1</sub> = " +  df.format(quat.q1()) + ";&nbsp;&nbsp;q<sub>2</sub> = " +  df.format(quat.q2()) + ";&nbsp;&nbsp;q<sub>3</sub> = " +  df.format(quat.q3());						
								
		case RODRIGUES:
			//MATRIX-RODRIGUES
			
			df = new DecimalFormat("0.#####");	

			RodriguesParams rod = new RodriguesParams();
			rod.set(M);
			return "<html>r<sub>1</sub> = " + df.format(rod.r1()) + ";&nbsp;&nbsp;r<sub>2</sub> = " +  df.format(rod.r2()) + ";&nbsp;&nbsp;r<sub>3</sub> = " +  df.format(rod.r3());	
			
		default:
			break;
		}
		return "";
	}

}
