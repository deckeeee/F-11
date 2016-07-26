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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import junit.framework.TestCase;

/**
 * @author hori
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProgramExecuteButtonTest extends TestCase {

	/**
	 * Constructor for ImageSymbolTest.
	 * @param arg0
	 */
	public ProgramExecuteButtonTest(String arg0) {
		super(arg0);
	}

	public void testUpdateProperty() {
		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("command","c:\\Program Files\\Microsoft Office\\Office\\EXCEL.EXE");
		prop.setProperty("value","エクセル");
		prop.setProperty("x","10");
		prop.setProperty("y","20");
		/*
		prop.setProperty("tooltiptext","");
		prop.setProperty("opaque","");
		prop.setProperty("foreground","");
		prop.setProperty("background","");
		prop.setProperty("font","");
		prop.setProperty("font_style","");
		prop.setProperty("font_size","");
		prop.setProperty("width","");
		prop.setProperty("height","");
		*/
		ProgramExecuteButton fix = new ProgramExecuteButton(prop);
		fix.addExecParam("E:\\hori\\Product_J\\F-11\\ReportExcel\\ListOut.csv");
		fix.addExecParam("/r");

		DlgSymbol dlg = new DlgSymbol();
		dlg.addTestCompo(fix);
		dlg.show();
		if (!dlg.getStat())
			fail();
	}

	class DlgSymbol extends JDialog {
		private static final long serialVersionUID = -2280705176963898642L;
		private boolean stat = false;
		private JPanel panel1 = new JPanel();
		private JPanel panel2 = new JPanel(){
			private static final long serialVersionUID = 5347094423776644026L;

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

			private static final long serialVersionUID = 737375633877641834L;
			
		};
		private JButton butNG = new JButton();
		
		/**
		 * Constructor for DlgSymbol.
		 * @param owner
		 * @throws HeadlessException
		 */
		public DlgSymbol() {
			super();
			this.setModal(true);
			this.setSize(400,300);
			this.setTitle("ImageSymbolTest");
			
			panel2.setLayout(null);
			
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
			panel1.add(new JLabel("ImageSymbolは表示されていますか？"), BorderLayout.NORTH);
			panel1.add(panel2, BorderLayout.CENTER);
			panel1.add(panel3, BorderLayout.SOUTH);
			this.getContentPane().add(panel1);
		}
		
		public void addTestCompo(Component fix) {
			panel2.add(fix);
		}
		
		public boolean getStat() {
			return stat;
		}
	}
}
