/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2007 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.dialog;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * 常にテキストフィールドが選択済みの NumberEditor クラスです。
 * 
 * @author maekawa
 * 
 */
public class SelectedFieldNumberEditor extends JSpinner.NumberEditor {
	private static final long serialVersionUID = 1628926888808404680L;

	public SelectedFieldNumberEditor(
			JSpinner spinner,
			String decimalFormatPattern) {
		super(spinner, decimalFormatPattern);
		addListeners();
	}

	public SelectedFieldNumberEditor(JSpinner spinner) {
		super(spinner);
		addListeners();
	}

	private void addListeners() {
		JFormattedTextField text = getTextField();
		addFocusListener(text);
		addKeyListener(text);
	}

	private void addFocusListener(JFormattedTextField text) {
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (e.getSource() instanceof JTextComponent) {
					final JTextComponent textComp =
						(JTextComponent) e.getSource();
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							textComp.selectAll();
						}
					});
				}
			}
		});
		text.setFocusLostBehavior(JFormattedTextField.COMMIT);
	}

	private void addKeyListener(final JFormattedTextField text) {
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					JFormattedTextField field = getTextField();
					try {
						field.commitEdit();
					} catch (ParseException e1) {
						JOptionPane.showMessageDialog(
							text,
							"入力値がMIN未満又はMAXより上です。",
							this.getClass().getName(),
							JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
}
