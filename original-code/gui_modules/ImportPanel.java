package gui_modules;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImportPanel extends JPanel {

	private static final String DREAM = "<html><font color=#0000cc><b>DREAM.3D</b></font>";
		
	private Import_DREAMPanel dreamPanel = new Import_DREAMPanel();
	
	private JPanel importCards;
	
	public ImportPanel() {
		setLayout(new MigLayout("", "[][][][]", "[][]"));
		
		JLabel importLbl = new JLabel("<html><b>Import boundary data from");
		add(importLbl, "cell 0 0,alignx trailing");
		
		JComboBox importCb = new JComboBox();
		importCb.setModel(new DefaultComboBoxModel(new String[] {DREAM /*, CMU*/}));
		add(importCb, "cell 1 0,growx");
		
		importCb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                JComboBox cb = (JComboBox) e.getSource();
                String sel = cb.getSelectedItem().toString();
                
                CardLayout cl = (CardLayout) importCards.getLayout();
            	cl.show(importCards, sel);            	            	
            }
		});
		
		JLabel outputLbl = new JLabel("<html><b>output files:</b>");
		add(outputLbl, "cell 2 0");
		
		importCards = new JPanel();
		add(importCards, "cell 0 1 4 1,grow");
		importCards.setLayout(new CardLayout(0, 0));
		
		importCards.add(dreamPanel, DREAM);
	}

}
