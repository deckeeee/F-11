/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.ngraph;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.text.JTextComponent;

/**
 * 編集中テキストフィールドが選択されているテキストエディタークラス
 * 
 * @author maekawa
 *
 */
public class SelectedFieldNumberEditor extends NumberEditor {
	private static final long serialVersionUID = 1724776012602772513L;

	public SelectedFieldNumberEditor(
			JSpinner spinner,
			String decimalFormatPattern) {
		super(spinner, decimalFormatPattern);
		getTextField().addFocusListener(new SelectedFocusListener());
	}

	@Override
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

	private static class SelectedFocusListener extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			if (e.getSource() instanceof JTextComponent) {
				final JTextComponent textComponent =
					((JTextComponent) e.getSource());
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						textComponent.selectAll();
					}
				});
			}
		}
	}
}
