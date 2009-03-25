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

package org.F11.scada.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public abstract class TableUtil {
	public static void setWidth(
			int width,
			int column,
			TableColumnModel columnModel) {
		TableColumn tableColumn = columnModel.getColumn(column);
		tableColumn.setMinWidth(width);
		tableColumn.setMaxWidth(width);
	}

	public static void setPreferredWidth(
			int width,
			int column,
			TableColumnModel columnModel) {
		TableColumn tableColumn = columnModel.getColumn(column);
		tableColumn.setPreferredWidth(width);
	}

	public static void setColumnWidth(JTable table, int column, String s) {
		TableColumn tc = table.getColumn(table.getColumnName(column));
		FontMetrics metrics = table.getFontMetrics(table.getFont());
		int width = metrics.stringWidth(s) + 8;
		setWidth(tc, width);
	}

	private static void setWidth(TableColumn tc, int width) {
		tc.setPreferredWidth(width);
		tc.setMaxWidth(width);
	}

	public static void setColumnWidth(JTable table, int column, int width) {
		TableColumn tc = table.getColumn(table.getColumnName(column));
		setWidth(tc, width);
	}

	public static void removeColumn(JTable table, int column) {
		table.removeColumn(table.getColumn(table.getColumnName(column)));
	}

	public static int getModelColumn(MouseEvent e) {
		JTable table = (JTable) e.getSource();
		int clickColumn = table.columnAtPoint(e.getPoint());
		return table
			.getTableHeader()
			.getColumnModel()
			.getColumn(clickColumn)
			.getModelIndex();
	}

	/**
	 * セルよりダイアログ表示位置を算出。
	 * 
	 * @param table
	 * @param row
	 * @param column
	 * @return
	 */
	public static Point getDialogPoint(JTable table, int row, int column) {
		Rectangle r = table.getCellRect(row, column, false);
		Point p = table.getLocationOnScreen();
		r.translate(p.x, p.y);
		r.translate(0, table.getRowHeight());
		return r.getLocation();
	}

	/**
	 * 先頭カラムから n カラムを削除します。
	 * 
	 * @param table 対象のテーブル
	 * @param removeColumnCount 削除するカラム数
	 */
	public static void removeColumns(JTable table, int removeColumnCount) {
		for (int i = removeColumnCount - 1; i >= 0; i--) {
			table.removeColumn(table.getColumn(table.getColumnName(0)));
		}
	}

	public static TableCellRenderer getColorTableCellRenderer() {
		return new DefaultTableCellRenderer() {
			private static final long serialVersionUID = -7941680905414950619L;

			@Override
			protected void setValue(Object value) {
				if (value instanceof Color) {
					Color c = (Color) value;
					setBackground(c);
				} else {
					super.setValue(value);
				}
			}
		};
	}
}
