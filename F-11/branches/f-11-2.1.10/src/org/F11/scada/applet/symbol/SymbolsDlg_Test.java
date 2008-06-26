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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SymbolsDlg_Test extends JDialog {
	private static final long serialVersionUID = 5561753447389564972L;
	private boolean stat = false;
	private JPanel panel2 = new parentPane();

	/**
	 * Constructor for SymbolsDlg_Test.
	 * @throws HeadlessException
	 */
	public SymbolsDlg_Test(String title, String msg) throws HeadlessException {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setSize(400, 300);
		this.setTitle(title);

		panel2.setLayout(null);

		JButton butOK = new JButton();
		butOK.setText("OK");
		butOK.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stat = true;
				dispose();
			}
		});
		JButton butNG = new JButton();
		butNG.setText("NG");
		butNG.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JPanel panel3 = new JPanel();
		panel3.add(butOK);
		panel3.add(butNG);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(
			new JLabel(msg),
			BorderLayout.NORTH);
		panel1.add(panel2, BorderLayout.CENTER);
		panel1.add(panel3, BorderLayout.SOUTH);
		this.getContentPane().add(panel1);
	}

	public void addSymbol(Symbol symbol) {
		panel2.add(symbol);
		symbol.updateProperty();
	}

	public void addComponent(Component component) {
		panel2.setLayout(new BorderLayout());
		panel2.add(component, BorderLayout.CENTER);
	}

	/**
	 * Constructor for SymbolsDlg_Test.
	 * @throws HeadlessException
	 */
	public SymbolsDlg_Test(String title) throws HeadlessException {
		this(title, null);
	}
	
	public boolean getStat() {
		return stat;
	}

	class parentPane extends JPanel implements SymbolCollection {
		private static final long serialVersionUID = -768214578886168388L;

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.gray);
			for (int x = 0; x < this.getWidth(); x += 10) {
				g.drawLine(x, 0, x, this.getHeight());
			}
			for (int y = 0; y < this.getHeight(); y += 10) {
				g.drawLine(0, y, this.getWidth(), y);
			}
		}
		/**
		 * @see org.F11.scada.applet.symbol.SymbolCollection#listIterator(List)
		 */
		public ListIterator listIterator(List para) {
			return new PageListIterator(
				this,
				(Class) para.get(0),
				(Editable) para.get(1));
		}

		private class PageListIterator implements ListIterator {
			private java.util.List symbols;
			private ListIterator listIterator;
			private boolean isPreviousMode;

			PageListIterator(
				Container compo,
				Class symbolClass,
				Editable current) {
				symbols = new ArrayList();

				int no = 0;
				for (int i = 0; i < compo.getComponentCount(); i++) {
					if (current == compo.getComponent(i)) {
						no = i;
						break;
					}
				}
				for (int i = 0; i < compo.getComponentCount(); i++) {
					Object o = compo.getComponent(no);
					if (symbolClass.isInstance(o) && o instanceof Editable) {
						Editable edit = (Editable) o;
						if (edit.isEditable() && o instanceof JLabel) {
							JLabel label = (JLabel) o;
							Point po = label.getLocationOnScreen();
							po.translate(0, label.getHeight());
							edit.setPoint(po);
							symbols.add(edit);
						}
					}

					no++;
					if (compo.getComponentCount() <= no)
						no = 0;
				}
			}

			public boolean hasNext() {
				return true;
			}

			public Object next() {
				if (listIterator == null)
					listIterator = symbols.listIterator();

				if (isPreviousMode) {
					isPreviousMode = false;
					try {
						listIterator.next();
					} catch (NoSuchElementException ex) {
						listIterator = symbols.listIterator(symbols.size());
						listIterator.next();
					}
				}

				try {
					return listIterator.next();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator();
					return listIterator.next();
				}
			}

			public boolean hasPrevious() {
				return true;
			}

			public Object previous() {
				if (listIterator == null)
					listIterator = symbols.listIterator(symbols.size());
				if (!isPreviousMode) {
					isPreviousMode = true;
					try {
						listIterator.previous();
					} catch (NoSuchElementException ex) {
						listIterator = symbols.listIterator(symbols.size());
						listIterator.previous();
					}
				}

				try {
					return listIterator.previous();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					return listIterator.previous();
				}
			}

			public int nextIndex() {
				int index = listIterator.nextIndex();
				if (isPreviousMode && index == symbols.size()) {
					ListIterator lit = symbols.listIterator();
					index = lit.nextIndex();
				}
				return index;
			}

			public int previousIndex() {
				int index = listIterator.previousIndex();
				if (!isPreviousMode && index < 0) {
					ListIterator lit = symbols.listIterator(symbols.size());
					index = lit.previousIndex();
				}
				return index;
			}

			public void add(Object o) {
				// non suport
				throw new UnsupportedOperationException();
			}

			public void remove() {
				// non suport
				throw new UnsupportedOperationException();
			}

			public void set(Object o) {
				// non suport
				throw new UnsupportedOperationException();
			}
		}

	}

}
