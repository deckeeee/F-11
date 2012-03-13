/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.F11.scada.applet.symbol;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import junit.framework.TestCase;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ImageAnimeSymbolTest extends TestCase {

	/**
	 * Constructor for ImageAnimeSymbolTest.
	 * @param arg0
	 */
	public ImageAnimeSymbolTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUpdateProperty() {
		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("visible","true");
		prop.setProperty("blink","false");
		prop.setProperty("x","40");
		prop.setProperty("y","20");
		prop.setProperty("value","/images/AHU_R_0.png");
		prop.setProperty("move","true");
		ImageAnimeSymbol fix = new ImageAnimeSymbol(prop);
		DlgSymbol dlg = new DlgSymbol(fix);
		dlg.show();
		if (!dlg.getStat())
			fail();
	}

	class DlgSymbol extends JDialog {
		private static final long serialVersionUID = -2355732063021846258L;
		private boolean stat = false;
		private JPanel panel1 = new JPanel();
		private JPanel panel2 = new JPanel(){
			private static final long serialVersionUID = 432066171708237272L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for (int x = 0; x < this.getWidth(); x += 10) {
					g.drawLine(x, 0, x, this.getHeight());
				}
				for (int y = 0; y < this.getHeight(); y += 10) {
					g.drawLine(0, y, this.getWidth(), y);
				}
			}
		};
		private JPanel panel3 = new JPanel();
		private JButton butOK = new JButton() {

			private static final long serialVersionUID = -324884636829836880L;
			
		};
		private JButton butNG = new JButton();
		
		/**
		 * Constructor for DlgSymbol.
		 * @param owner
		 * @throws HeadlessException
		 */
		public DlgSymbol(Symbol fix) {
			super();
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setModal(true);
			this.setSize(400,300);
			this.setTitle("ImageAnimeSymbolTest");
			
			panel2.setLayout(null);
			panel2.add(fix);
			fix.updateProperty();
			
			
			butOK.setText("OK");
	        butOK.addActionListener(new java.awt.event.ActionListener() {
    	        public void actionPerformed(ActionEvent e) {
    	        	stat = true;
    	        	dispose();
            	}
	        });
			butNG.setText("NG");
	        butNG.addActionListener(new java.awt.event.ActionListener() {
    	        public void actionPerformed(ActionEvent e) {
    	        	dispose();
            	}
	        });
			panel3.add(butOK);
			panel3.add(butNG);
			panel1.setLayout(new BorderLayout());
			panel1.add(new JLabel("ImageAnimeSymbol ‚Í•\Ž¦‚³‚ê‚Ä‚¢‚Ü‚·‚©H"), BorderLayout.NORTH);
			panel1.add(panel2, BorderLayout.CENTER);
			panel1.add(panel3, BorderLayout.SOUTH);
			this.getContentPane().add(panel1);
		}
		
		public boolean getStat() {
			return stat;
		}
	}
}
