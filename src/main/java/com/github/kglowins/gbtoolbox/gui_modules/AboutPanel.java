package com.github.kglowins.gbtoolbox.gui_modules;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextPane;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutPanel extends JPanel {

	private static final String REF = "ref";
	private static final String LIC = "lic";
	
	
	public AboutPanel() {
		
		
		setLayout(new MigLayout("", "[grow][620px:620px:620px,grow][grow]", "[grow][][grow][][grow]"));
		
		JLabel logoLbl = new JLabel();
		logoLbl.setIcon(new ImageIcon(AboutPanel.class.getResource("/gui_modules/gbtoolbox_logo2.png")));
		add(logoLbl, "cell 1 1,alignx center");
		
		JLabel licLbl = new JLabel(
				"<html>" +
				"<b><i>GBToolbox</i></b> version 1.0.2 Beta (May 24, 2022)<br>"+
				"Copyright \u00a9 2011-2022, Krzysztof G\u0142owi\u0144ski. "+
				"All rights reserved.<br><br>" +
				"<p align=justify>Redistribution and use in binary forms, without "+
				"modification, is permitted provided that the following conditions are met: " +
				"<ul><li><p align=justify>Redistributions in binary form must reproduce the above copyright " +
				"notice, this list of conditions and the following disclaimer in the " +
				"documentation and/or other materials provided with the distribution.</p></ul></p><br>" +
				
				"<p align=justify>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND " +
				"ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED " +
				"WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE " +
				"DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY " +
				"DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES " +
				"(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; " +
				"LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND " +
				"ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT " +
				"(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS " +
				"SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.</p><br>" +
				
				"<i><b>GBToolbox</b></i> utilizes the following open source libraries:<br>" +
				"&nbsp;&nbsp;- Appache Commons\u2122 Math 3.4.1 (<code>http://commons.apache.org/proper/commons-math</code>),<br>"+
				"&nbsp;&nbsp;- VectorGraphics2D 0.9 (<code>http://trac.erichseifert.de/vectorgraphics2d</code>),<br>" +
				"&nbsp;&nbsp;- JAMA 1.0.3 (<code>http://math.nist.gov/javanumerics/jama</code>),<br>"+
				"&nbsp;&nbsp;- hdf-java from HPE CCT (<code>https://github.com/hpe-cct/hdf-java</code>),<br>" +
				"&nbsp;&nbsp;- JMathPlot (an old version downloaded from <code>http://jmathtools.berlios.de</code>),<br>"+
				"&nbsp;&nbsp;- Java-ML 0.1.5 (<code>http://java-ml.sourceforge.net</code>),<br>"+
				"&nbsp;&nbsp;- JFontChooser 1.0.5 (<code>http://jfontchooser.osdn.jp</code>).<br><br>"+
				"<p align=justify>These libraries are included, without modifications, in this program. The "+
				"corresponding license agreements are in its directory.</p>");
		
		JTextPane refLbl = new JTextPane();
		JLabel refLbl0 = new JLabel("<html>If you find <i><b>GBToolbox</b></i> useful in your work, you may use to the following citations:");
		refLbl.setContentType("text/html");
		refLbl.setEditable(false);
		refLbl.setText("<html><ul>" +	
		
			"<li><p align=justify>Glowinski, K. and Morawiec, A.: <i>Analysis of experimental grain boundary " +
			"distributions based on boundary-space metrics</i>. Metall. Mater. Trans. A <b>45</b> (2014), p. 3189�94.</p>"+
			
			"<li><p align=justify>Glowinski, K. and Morawiec, A.: <i>Twist, tilt, and symmetric grain boundaries in " +
			"hexagonal materials</i>. J. Mater. Sci. <b>49</b> (2014), p. 3936-42.</p>" +


			"<li><p align=justify>Glowinski, K.: <i>On identification of symmetric and improperly quasi-symmetric grain "+
			"boundaries</i>. J. Appl. Cryst. <b>47</b> (2014), p. 726-31.</p>" +
		
			
			"<li><p align=justify>Morawiec, A. and Glowinski, K.: <i>On \"macroscopic\" characterization of mixed grain " +
			"boundaries</i>. Acta Mater. <b>61</b> (2013), p. 5756-67.</p>" +

		
			
				"<li><p align=justify>Glowinski, K. and Morawiec, A.: " +
			"<i>A toolbox for geometric grain boundary characterization</i>. " +
			"In: 1st International Conference on 3D Materials Science (2012), p. 119-24.</p>" 
	
		);
	
		
		final JPanel licPanel = new JPanel();
		licPanel.setLayout(new MigLayout("insets 0", "[]", "[]"));
		licPanel.add(licLbl, "cell 0 0,gapy 10");
		
		final JPanel refPanel = new JPanel();
		refPanel.setLayout(new MigLayout("insets 0", "[]", "[][]"));
		refPanel.add(refLbl0, "cell 0 0,gapy 60");
		refPanel.add(refLbl, "cell 0 1,gapy 5");
		
		 
		
		refPanel.setMinimumSize(new Dimension(620,400));
		refPanel.setMaximumSize(new Dimension(620,400));
		refPanel.setPreferredSize(new Dimension(620,400));
			
		
		final JPanel cards = new JPanel();
		add(cards, "cell 1 2,grow");
		cards.setLayout(new CardLayout(0, 0));
		
		cards.add(licPanel, LIC);
		cards.add(refPanel, REF);
		CardLayout cl = (CardLayout) cards.getLayout();
    	cl.show(cards, LIC); 
    	
    
		//add(licLbl, "cell 1 2,gapy 10");
		
		JButton refBtn = new JButton("Refrences");
		add(refBtn, "flowx,cell 1 3,gapy 10");
		
		JButton licBtn = new JButton("License");
		add(licBtn, "cell 1 3,gapy 10,gapy 10");
		
		
		refBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				CardLayout cl = (CardLayout) cards.getLayout();
            	cl.show(cards, REF); 
			}
		});
		
		licBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				CardLayout cl = (CardLayout) cards.getLayout();
            	cl.show(cards, LIC); 
			}
		});
		
	}

}
