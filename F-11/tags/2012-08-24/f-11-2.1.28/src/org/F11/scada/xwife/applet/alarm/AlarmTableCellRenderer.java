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

package org.F11.scada.xwife.applet.alarm;

import java.awt.Component;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.F11.scada.Globals;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.apache.commons.lang.time.FastDateFormat;

public class AlarmTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = -2347835022092437355L;

	private static final FastDateFormat format = FastDateFormat.getInstance(
			"yyyy/MM/dd HH:mm:ss");

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);

		if (value instanceof Date) {
			Date d = (Date) value;
			if (d.after(Globals.EPOCH)) {
				super.setText(format.format(value));
			} else {
				super.setText(null);
			}
		}

		AlarmTableModel tm = (AlarmTableModel) table.getModel();
		String colorName = (String) tm.getValueAt(row, tm.getColumn("ï\é¶êF"));
		super.setForeground(ColorFactory.getColor(colorName));
		return this;
	}
}