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

package org.F11.scada.applet.ngraph.editor.component;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.F11.scada.applet.ngraph.editor.SeriesData;

/**
 * エディタのラベルクラス
 * 
 * @author maekawa
 * 
 */
public class Label extends JLabel implements MouseListener, KeyListener {
	private static final long serialVersionUID = 3130779776179786966L;
	private final boolean isNo;

	public Label(boolean isNo) {
		super();
		this.isNo = isNo;
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		JTable table = (JTable) e.getSource();
		if (table.isEnabled()) {
			int row = table.rowAtPoint(e.getPoint());
			fireRowChanged(table, row);
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		JTable table = (JTable) e.getSource();
		if (table.isEnabled()) {
			int row = table.getSelectedRow();
			fireRowChanged(table, row);
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	private void fireRowChanged(JTable table, int row) {
		SeriesTableModel model = (SeriesTableModel) table.getModel();
		SeriesData sd = model.getGroupData(row);
		if (null != sd) {
			if (isNo) {
				setText(String.format("No.%03d", sd.getGroupNo()));
			} else {
				setText(sd.getGroupName());
			}
		}
	}
}
