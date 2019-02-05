package com.github.kglowins.gbtoolbox.gui_modules;


import hdfloader.Hdf5NativesLoader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import com.github.kglowins.gbtoolbox.utils.ConstantsAndStatic;


public class Main {

	static {
		Hdf5NativesLoader.load();
	}

	private JFrame frmGbToolbox;
	private JPanel mainPanel;
	
	private Catalog_AnalyticalPanel catAnaPanel;
	private IndividualGBPanel indivGBPanel;
	private Catalog_NumericalPanel catNumPanel;
	private RandomGBsPanel randomPanel;
	private StatisticalAnalysisPanel statsPanel;	
	private ImportPanel importPanel;
	private ReprocessPanel reprPanel;
	private HexToSqrAngsPanel hex2sqrPanel;
	private PreviewGBCD_GBPDPanel previewPanel;
	private NormalizeDistPanel normPanel;
	private SubsetsPanel subsetsPanel;	
	private AboutPanel aboutPanel;
	
	private DrawSelectedGBsPanel drawSelPanel;

	
	private static final String INDIV = "Characterization of individual GBs";
	private static final String CATALOG_ANA = "Distribution symmetries and locations of special GBs";
	private static final String CATALOG_NUM = "Locations of special GBs for various tolerance levels";
	private static final String RANDOM = "Generation of random GB datasets";
	private static final String IMPORT = "Import of experimental GB data";
	private static final String SUBSETS = "Subsets of GB data";
	private static final String REPROC = "Characterization of GBs in datasets";
	private static final String STATS = "Quantitative analysis of GB datasets";
	private static final String NORM = "Normalization of GB distributions";
	private static final String HEX2SQ = "Conversion of .ang files";
	private static final String GBCD_GBPD_PREVIEW = "Plots";
	
	private static final String DRAWGBS = "Draw selected GBs";
	
	private static final String ABOUT = "Home";
		
	private JButton indivToolbarBtn; 
	private JButton catAnaToolbarBtn;
	private JButton catNumToolbarBtn;
	private JButton randToolbarBtn;
	private JButton importToolbarBtn;
	private JButton reprocBtn;
	private JButton statToolbarBtn;	
	private JButton previewToolbarBtn;
	private JButton normToolbarBtn;

	private JMenu menuDataAnalysis;
	private JMenu menuInterpretation;
	
	private JRadioButtonMenuItem itemSubsets;
	private JButton subsetsBtn;
//	private JMenuItem itemHelp;
	
	static Desktop desktop = null;
	private JButton drawSelBtn;
			
	public final static void showHelpWindow() throws URISyntaxException, IOException {
				
		if( !desktop.isSupported(Desktop.Action.BROWSE ) ) {
  
           JOptionPane.showMessageDialog(null,
					"Could not open the default web browser.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
           return;
        }

		URI uri = new URI("help.html");
		if (desktop != null) desktop.browse(uri); 
	}
		
	public static void main(String[] args) {
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        UIManager.put("swing.boldMetal", Boolean.FALSE);
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	    	UIManager.put("swing.boldMetal", Boolean.FALSE);
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		
		if(Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();			
		}
				
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Main window = new Main();
					window.frmGbToolbox.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public Main() {
		initialize();
	}

	
	private void initialize() {
		frmGbToolbox = new JFrame();
		frmGbToolbox.setResizable(false);
		frmGbToolbox.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/gui_bricks/gbtoolbox.png")));
		frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " + ABOUT);
	
		frmGbToolbox.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmGbToolbox.getContentPane().setLayout(new BorderLayout(0, 0));
		
		
		
		JToolBar toolBar = new JToolBar();
		frmGbToolbox.getContentPane().add(toolBar, BorderLayout.NORTH);
		
		mainPanel = new JPanel();
	
		frmGbToolbox.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		frmGbToolbox.setJMenuBar(menuBar);
		
				
		
		frmGbToolbox.addWindowListener(new WindowAdapter() {
			@Override
		    public void windowClosing(WindowEvent evt) {
				frmGbToolbox.dispose();
				System.exit(0);
		    }
	    });
		
				
		frmGbToolbox.setJMenuBar(menuBar);
		
		JMenu menuProgram = new JMenu("GBToolbox");
		menuBar.add(menuProgram);
		
		JMenuItem itemExit = new JMenuItem("Exit");
		itemExit.setIcon(new ImageIcon(Main.class.getResource("/gui_bricks/close.png")));
		itemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
		
		itemExit.addActionListener(new ActionListener() {
		      @Override
			public void actionPerformed(ActionEvent e) {
		        
		    	  frmGbToolbox.dispose();
		    	  System.exit(0);
		      }
		    });
		
		final ButtonGroup moduleGroup = new ButtonGroup();
		
		ModuleListener modListener = new ModuleListener();
		
		JRadioButtonMenuItem itemStart = new JRadioButtonMenuItem(ABOUT);
		itemStart.setIcon(new ImageIcon(Main.class.getResource("/gui_bricks/about.png")));
		
		itemStart.addActionListener(modListener);
		moduleGroup.add(itemStart);		
		menuProgram.add(itemStart);
		itemStart.setSelected(true);
		
	/*	itemHelp = new JMenuItem("User's guide");
		itemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.SHIFT_MASK));
		itemHelp.setIcon(new ImageIcon(Main.class.getResource("/gui_bricks/tutorial_small.png")));
		menuProgram.add(itemHelp);
		
		itemHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					showHelpWindow();
					
				} catch (URISyntaxException | IOException exc) {
					
					exc.printStackTrace();
				//	JOptionPane.showMessageDialog(SubsetsPanel.this,
					//		"Tolerance thresholds should be positive decimal numbers",
						//	"Error",
							//JOptionPane.ERROR_MESSAGE);
					return;
				} 
			}
		});*/
		
		
		menuProgram.add(itemExit);
		
		final JMenu menuModules = new JMenu("Modules");
		menuBar.add(menuModules);
		
	

		
		final JRadioButtonMenuItem itemIndiv = new JRadioButtonMenuItem(INDIV);
		itemIndiv.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/indiv16.png")));
		
		
		itemIndiv.addActionListener(modListener);
		moduleGroup.add(itemIndiv);
		menuModules.add(itemIndiv);
		
		menuDataAnalysis = new JMenu("Analysis of GB datasets");
		menuModules.add(menuDataAnalysis);
		
		final JRadioButtonMenuItem itemEBSD = new JRadioButtonMenuItem(IMPORT);
		menuDataAnalysis.add(itemEBSD);
		itemEBSD.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/import16.png")));
		itemEBSD.addActionListener(modListener);
		moduleGroup.add(itemEBSD);	
		
				
		final JRadioButtonMenuItem itemRandom = new JRadioButtonMenuItem(RANDOM);
		menuDataAnalysis.add(itemRandom);
		itemRandom.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/rand16.png")));
		itemRandom.addActionListener(modListener);
		moduleGroup.add(itemRandom);
				
				itemSubsets = new JRadioButtonMenuItem(SUBSETS);
				itemSubsets.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/subset16.png")));
				menuDataAnalysis.add(itemSubsets);
				itemSubsets.addActionListener(modListener);
				moduleGroup.add(itemSubsets);
		
				final JRadioButtonMenuItem itemReproc = new JRadioButtonMenuItem(REPROC);
				menuDataAnalysis.add(itemReproc);
				itemReproc.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/reproc16.png")));
				itemReproc.addActionListener(modListener);
				moduleGroup.add(itemReproc);
				
				

				
				
				final JRadioButtonMenuItem itemFreq = new JRadioButtonMenuItem(STATS);
				menuDataAnalysis.add(itemFreq);
				itemFreq.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/freq16.png")));
				itemFreq.addActionListener(modListener);
				moduleGroup.add(itemFreq);
				
				final JRadioButtonMenuItem itemNorm = new JRadioButtonMenuItem(NORM);
				menuDataAnalysis.add(itemNorm);
				itemNorm.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/norm16.png")));
				itemNorm.addActionListener(modListener);
				moduleGroup.add(itemNorm);
		

		
		menuInterpretation = new JMenu("Interpretation of GB distributions");
		menuModules.add(menuInterpretation);
		
		final JRadioButtonMenuItem itemAnalytical = new JRadioButtonMenuItem(CATALOG_ANA);
		menuInterpretation.add(itemAnalytical);
		itemAnalytical.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/map16.png")));
		itemAnalytical.addActionListener(modListener);
		moduleGroup.add(itemAnalytical);
		
		final JRadioButtonMenuItem itemGrid = new JRadioButtonMenuItem(CATALOG_NUM);
		menuInterpretation.add(itemGrid);
		itemGrid.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/num16.png")));
		itemGrid.addActionListener(modListener);
		moduleGroup.add(itemGrid);
		
		
		final JRadioButtonMenuItem drawSelMenuItem = new JRadioButtonMenuItem(DRAWGBS);
		menuModules.add(drawSelMenuItem);
		drawSelMenuItem.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/sel16.png")));
		drawSelMenuItem.addActionListener(modListener);
		moduleGroup.add(drawSelMenuItem);
				
		final JRadioButtonMenuItem itemGBCDPrev = new JRadioButtonMenuItem(GBCD_GBPD_PREVIEW);
		itemGBCDPrev.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/gbcd16.png")));
		menuModules.add(itemGBCDPrev);
		itemGBCDPrev.addActionListener(modListener);
		moduleGroup.add(itemGBCDPrev);
		
		final JRadioButtonMenuItem itemHexToSqr = new JRadioButtonMenuItem(HEX2SQ);
		itemHexToSqr.setToolTipText("<html>Converts <code>ang</code> files with hexagonal grids to files with square grids.");
		itemHexToSqr.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/hex2rect16.png")));
		menuModules.add(itemHexToSqr);
		itemHexToSqr.addActionListener(modListener);
		moduleGroup.add(itemHexToSqr);
		

		toolBar.setRollover(true);
		toolBar.setFloatable(false);
		toolBar.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
	
		
		indivToolbarBtn = new JButton();
		indivToolbarBtn.setToolTipText("<html><b>Characterization of individual GBs.</b><br>Example capabilities:<br>- Verification whether a given boundary has special geometry.<br>- Determination of similarity of two boundaries.");
		itemIndiv.setToolTipText("<html>Example capabilities:<br>- Verification whether a given boundary has special geometry.<br>- Determination of similarity of two boundaries.");
		indivToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/indiv32.png")));
		toolBar.add(indivToolbarBtn);
		indivToolbarBtn.setMinimumSize(new Dimension(48,48));
		indivToolbarBtn.setMaximumSize(new Dimension(48,48));
		indivToolbarBtn.setPreferredSize(new Dimension(48,48));
		indivToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, INDIV);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " + INDIV);
	            
	            final ButtonModel model = itemIndiv.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
		
		toolBar.addSeparator();
		
		importToolbarBtn = new JButton();
		importToolbarBtn.setToolTipText("<html><b>Import of experimental GB data.</b><br>It allows for import of reconstructed boundaries and for saving their parameters in <code>gbdat</code> files.");
		itemEBSD.setToolTipText("<html>It allows for import of reconstructed boundaries and for saving their parameters in <code>gbdat</code> files.");
		importToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/import32.png")));
		toolBar.add(importToolbarBtn);
		importToolbarBtn.setMinimumSize(new Dimension(48,48));
		importToolbarBtn.setMaximumSize(new Dimension(48,48));
		importToolbarBtn.setPreferredSize(new Dimension(48,48));
		importToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel,IMPORT);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  IMPORT);
	            final ButtonModel model = itemEBSD.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
		randToolbarBtn = new JButton();
		randToolbarBtn.setToolTipText("<html><b>Generation of random GB datasets.</b><br>The module creates <code>gbdat</code> files with random boundaries.");
		itemRandom.setToolTipText("<html>The module creates <code>gbdat</code> files with random boundaries.");
		randToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/rand32.png")));
		toolBar.add(randToolbarBtn);
		randToolbarBtn.setMinimumSize(new Dimension(48,48));
		randToolbarBtn.setMaximumSize(new Dimension(48,48));
		randToolbarBtn.setPreferredSize(new Dimension(48,48));
		randToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, RANDOM);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  RANDOM);
	            final ButtonModel model = itemRandom.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		itemSubsets.setToolTipText("<html>Exclude boundaries of specified types from <code>gbdat</code> files.");
		itemReproc.setToolTipText("<html>Characterize each boundary from a <code>gbdat</code> file and store the results in a new file.");
		
		statToolbarBtn = new JButton();
		statToolbarBtn.setToolTipText("<html><b>Quantitative analysis of GB datasets.</b><br>Example capabilities:<br>- Frequencies of occurrence of special GBs.<br>- Distributions of GBs and GB planes.");
		itemFreq.setToolTipText("<html>Example capabilities:<br>- Frequencies of occurrence of special GBs.<br>- Distributions of GBs and GB planes.");
		
	
		
		subsetsBtn = new JButton();
		subsetsBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/subset32.png")));
		subsetsBtn.setToolTipText("<html><b>Subsets of GB data.</b><br>Exclude boundaries of specified types from <code>gbdat</code> files.");
		subsetsBtn.setPreferredSize(new Dimension(48, 48));
		subsetsBtn.setMinimumSize(new Dimension(48, 48));
		subsetsBtn.setMaximumSize(new Dimension(48, 48));
		toolBar.add(subsetsBtn);
		subsetsBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, SUBSETS);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  SUBSETS);
	            final ButtonModel model = itemSubsets.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
		reprocBtn = new JButton();
		reprocBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/reproc32.png")));
		reprocBtn.setToolTipText("<html><b>Characterization of GBs in datasets.</b><br>Characterize each boundary from a <code>gbdat</code> file and store the results in a new file.");
		reprocBtn.setPreferredSize(new Dimension(48, 48));
		reprocBtn.setMinimumSize(new Dimension(48, 48));
		reprocBtn.setMaximumSize(new Dimension(48, 48));
		toolBar.add(reprocBtn);
		reprocBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, REPROC);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  REPROC);
	            final ButtonModel model = itemReproc.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
		
		statToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/freq32.png")));
		toolBar.add(statToolbarBtn);
		statToolbarBtn.setMinimumSize(new Dimension(48,48));
		statToolbarBtn.setMaximumSize(new Dimension(48,48));
		statToolbarBtn.setPreferredSize(new Dimension(48,48));
		statToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, STATS);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  STATS);
	            final ButtonModel model = itemFreq.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
	
		
		normToolbarBtn = new JButton();
		normToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/norm32.png")));
		normToolbarBtn.setToolTipText("<html><b>Normalization of GB distributions.</b><br>");
		normToolbarBtn.setPreferredSize(new Dimension(48, 48));
		normToolbarBtn.setMinimumSize(new Dimension(48, 48));
		normToolbarBtn.setMaximumSize(new Dimension(48, 48));
		toolBar.add(normToolbarBtn);
		normToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, NORM);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  NORM);
	            final ButtonModel model = itemNorm.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
		toolBar.addSeparator();
		
		catAnaToolbarBtn = new JButton();
		catAnaToolbarBtn.setToolTipText("<html><b>Distribution symmetries & locations of special GBs.</b><br>It determines boundary-plane distribution symmetries for a fixed misorientation<br>and locates boundaries of special geometries.");
		itemAnalytical.setToolTipText("<html>It determines boundary-plane distribution symmetries for a fixed misorientation<br>and locates boundaries of special geometries.");
		catAnaToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/map32.png")));
		toolBar.add(catAnaToolbarBtn);
		catAnaToolbarBtn.setMinimumSize(new Dimension(48,48));
		catAnaToolbarBtn.setMaximumSize(new Dimension(48,48));
		catAnaToolbarBtn.setPreferredSize(new Dimension(48,48));
		catAnaToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, CATALOG_ANA);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " + CATALOG_ANA);
	            final ButtonModel model = itemAnalytical.getModel();
	            moduleGroup.setSelected(model, true);	            
			}
		});
		
		catNumToolbarBtn = new JButton();
		catNumToolbarBtn.setToolTipText("<html><b>"+CATALOG_NUM+".</b><br>Compute distances to the nearest special GBs as functions of boundary planes for a fixed misorientation.");
		itemGrid.setToolTipText("<html>Compute distances to the nearest special GBs as functions of boundary planes for a fixed misorientation.");
		catNumToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/num32.png")));
		toolBar.add(catNumToolbarBtn);
		catNumToolbarBtn.setMinimumSize(new Dimension(48,48));
		catNumToolbarBtn.setMaximumSize(new Dimension(48,48));
		catNumToolbarBtn.setPreferredSize(new Dimension(48,48));
		
		catNumToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, CATALOG_NUM);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  CATALOG_NUM);
	            
	            final ButtonModel model = itemGrid.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
		toolBar.addSeparator();
		
		previewToolbarBtn = new JButton();
		previewToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/gbcd32.png")));
		previewToolbarBtn.setToolTipText("<html><b>Plots.</b><br>Plot distributions of GBs, GB planes, and distances to the nearest special GBs.");
		itemGBCDPrev.setToolTipText("<html>Plot distributions of GBs, GB planes, and distances to the nearest special GBs.");
		previewToolbarBtn.setPreferredSize(new Dimension(48, 48));
		previewToolbarBtn.setMinimumSize(new Dimension(48, 48));
		previewToolbarBtn.setMaximumSize(new Dimension(48, 48));
		toolBar.add(previewToolbarBtn);
		
		previewToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, GBCD_GBPD_PREVIEW);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " + GBCD_GBPD_PREVIEW);
	            final ButtonModel model = itemGBCDPrev.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
		drawSelBtn = new JButton();
		drawSelBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_modules/sel32.png")));
		drawSelBtn.setToolTipText("<html><b>Draw selected boundaries.</b><br>Visualizes boundaries of selected types. Creates a <code>vtk</code> file.");
		drawSelBtn.setPreferredSize(new Dimension(48, 48));
		drawSelBtn.setMinimumSize(new Dimension(48, 48));
		drawSelBtn.setMaximumSize(new Dimension(48, 48));
		toolBar.add(drawSelBtn);
		drawSelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {		                            
	            CardLayout cl = (CardLayout) mainPanel.getLayout();
	            cl.show(mainPanel, DRAWGBS);
	            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  DRAWGBS);
	            final ButtonModel model = drawSelMenuItem.getModel();
	            moduleGroup.setSelected(model, true);
			}
		});
		
				
		
		
		Component horizontalGlue = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue);
		
	//	JButton helpToolbarBtn = new JButton();
		/*helpToolbarBtn.setToolTipText("<html><b>User's guide.</b>");
		helpToolbarBtn.setIcon(new ImageIcon(Main.class.getResource("/gui_bricks/tutorial.png")));
		toolBar.add(helpToolbarBtn);
		helpToolbarBtn.setMinimumSize(new Dimension(48,48));
		helpToolbarBtn.setMaximumSize(new Dimension(48,48));
		helpToolbarBtn.setPreferredSize(new Dimension(48,48));
		helpToolbarBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					showHelpWindow();
					
				} catch (URISyntaxException | IOException exc) {
					
					exc.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Could not open the help pages.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} 
			}
		});*/
		
			
		mainPanel.setLayout(new CardLayout(0, 0));
		
		indivGBPanel = new IndividualGBPanel();
		catAnaPanel = new Catalog_AnalyticalPanel();		
		catNumPanel = new Catalog_NumericalPanel();
		randomPanel = new RandomGBsPanel();
		importPanel = new ImportPanel();		
		statsPanel = new StatisticalAnalysisPanel();
		reprPanel = new ReprocessPanel();
		hex2sqrPanel = new HexToSqrAngsPanel();
		previewPanel = new PreviewGBCD_GBPDPanel();
		normPanel = new NormalizeDistPanel();
		subsetsPanel = new SubsetsPanel();	
		aboutPanel = new AboutPanel();
		
		drawSelPanel = new DrawSelectedGBsPanel();
		
		mainPanel.add(aboutPanel, ABOUT);
		mainPanel.add(indivGBPanel, INDIV);
		mainPanel.add(catAnaPanel, CATALOG_ANA);		
		mainPanel.add(catNumPanel, CATALOG_NUM);
		mainPanel.add(subsetsPanel, SUBSETS);
		mainPanel.add(randomPanel, RANDOM);
		mainPanel.add(importPanel, IMPORT);
		mainPanel.add(reprPanel, REPROC);
		mainPanel.add(statsPanel, STATS);
		mainPanel.add(normPanel, NORM);
		mainPanel.add(hex2sqrPanel, HEX2SQ);
		mainPanel.add(previewPanel, GBCD_GBPD_PREVIEW);
		mainPanel.add(drawSelPanel, DRAWGBS);
		
		frmGbToolbox.pack();
		frmGbToolbox.setMinimumSize(frmGbToolbox.getPreferredSize());
		mainPanel.requestFocus();
	}

	
	private class ModuleListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
		    String s = evt.getActionCommand().toString();		                            
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, s);
            frmGbToolbox.setTitle(ConstantsAndStatic.GBTOOLBOX + ": " +  s);
            
            switch(s) {
            case INDIV: indivToolbarBtn.requestFocus(); break;
            case CATALOG_ANA: catAnaToolbarBtn.requestFocus(); break;
            case CATALOG_NUM: catNumToolbarBtn.requestFocus(); break;            
            case SUBSETS: subsetsBtn.requestFocus(); break;
            case RANDOM:  randToolbarBtn.requestFocus(); break;
            case IMPORT: importToolbarBtn.requestFocus();break;
            case REPROC: reprocBtn.requestFocus();break;
            case STATS: statToolbarBtn.requestFocus(); break;
            case NORM: normToolbarBtn.requestFocus(); break;
            case GBCD_GBPD_PREVIEW: previewToolbarBtn.requestFocus(); break;
            case HEX2SQ: mainPanel.requestFocus(); break;
            case ABOUT: mainPanel.requestFocus();  break;
            case DRAWGBS: drawSelPanel.requestFocus();  break;
            	
            default: break;
            }   
		}
	}
}
