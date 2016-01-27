/*
 * =============================================================================
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

package org.F11.scada.xwife.applet;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.TableModel;

class PageJumpNewLines  extends MouseAdapter {
	private final int lineno;
	private final TableModel model;
	private final AbstractWifeApplet wifeApplet;

	public PageJumpNewLines(TableModel model, AbstractWifeApplet wifeApplet,
			int lineno) {
		this.lineno = lineno;
		this.model = model;
		this.wifeApplet = wifeApplet;
	}
	public void mouseReleased(MouseEvent e) {
		if (e.getClickCount() == 2) {
			Object o = model.getValueAt(lineno, 0);
			if (o instanceof String) {
				String s = (String) o;
				PageChangeEvent che = new PageChangeEvent(this, s,
						false);
				wifeApplet.changePage(che);
			}
		}
	}
}