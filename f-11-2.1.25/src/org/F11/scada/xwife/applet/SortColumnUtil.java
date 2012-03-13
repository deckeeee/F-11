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

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.F11.scada.util.TableUtil;
import org.apache.commons.configuration.Configuration;

public abstract class SortColumnUtil {
	public static boolean getShowSortColumn(Configuration configuration) {
		return configuration.getBoolean(
				"org.F11.scada.xwife.applet.alarm.showSortColumn",
				false);
	}

	public static boolean getShowSortColumn(AbstractWifeApplet wifeApplet) {
		return getShowSortColumn(wifeApplet.getConfiguration());
	}

	public static void removeSortColumn(
			JTable table,
			int column,
			AbstractWifeApplet wifeApplet,
			int width) {
		if (getShowSortColumn(wifeApplet)) {
			TableColumn tc = table.getColumn(table.getColumnName(column));
			tc.setPreferredWidth(width);
			tc.setMaxWidth(tc.getPreferredWidth());
		} else {
			table.removeColumn(table.getColumn(table.getColumnName(column)));
		}
	}

	public static void removeSortColumn(
			JTable table,
			int column,
			AbstractWifeApplet wifeApplet,
			String s) {
		if (getShowSortColumn(wifeApplet)) {
			TableUtil.setColumnWidth(table, column, s);
		} else {
			table.removeColumn(table.getColumn(table.getColumnName(column)));
		}
	}
}
