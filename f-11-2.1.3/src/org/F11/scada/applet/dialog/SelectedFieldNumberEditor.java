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

import java.beans.PropertyChangeEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;

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
	}

	public SelectedFieldNumberEditor(JSpinner spinner) {
		super(spinner);
	}

	public void propertyChange(PropertyChangeEvent e) {
		super.propertyChange(e);
		final Object source = e.getSource();
		if (source instanceof JFormattedTextField) {
			if (SwingUtilities.isEventDispatchThread()) {
				selectAll(source);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						selectAll(source);
					}
				});
			}
		}
	}

	private void selectAll(Object source) {
		JFormattedTextField field = (JFormattedTextField) source;
		field.selectAll();
	}
}
