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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

public abstract class TableUtil {
	private static Logger logger = Logger.getLogger(TableUtil.class);

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

	public static void setColumnWidth(JTable table, String columnName, int width) {
		TableColumn tc = table.getColumn(columnName);
		setWidth(tc, width);
	}

	public static void removeColumn(JTable table, int column) {
		table.removeColumn(table.getColumn(table.getColumnName(column)));
	}

	public static void removeColumn(JTable table, String columnName) {
		table.removeColumn(table.getColumn(columnName));
	}

	public static int getModelColumn(MouseEvent e) {
		JTable table = (JTable) e.getSource();
		int clickColumn = table.columnAtPoint(e.getPoint());
		return table.getTableHeader().getColumnModel().getColumn(clickColumn)
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

	/**
	 * 設定ファイルより、「列名1:移動先の列1, 列名2:移動先の列2... ,列名n:移動先の列n」の文字列を受け取り、
	 * テーブル列の移動処理を行います。
	 *
	 * @param table 入れ替え処理するテーブル
	 * @param columnConfig 「列名1:移動先の列1, 列名2:移動先の列2... ,列名n:移動先の列n」の文字列
	 */
	public static void moveColumn(JTable table, String columnConfig) {
		Map<String, Integer> map = getMoveMap(columnConfig);
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			moveColumn(table, entry.getKey(), entry.getValue());
		}
	}

	private static void moveColumn(JTable table, String string, int i) {
		TableColumnModel model = table.getColumnModel();
		try {
			table.moveColumn(model.getColumnIndex(string), i);
		} catch (IllegalArgumentException e) {
			logger.error("警報一覧の列変更設定で列名か列番号が不正です 列名=" + string + " 列番号=" + i , e);
		}
	}

	public static Map<String, Integer> getMoveMap(String s) {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		if (null != s && s.length() > 0) {
			String[] tmpStrs = s.split("[:\\$]");
			for (int i = 0; i < tmpStrs.length; i += 2) {
				String s1 = tmpStrs[i].trim();
				String s2 = tmpStrs[i + 1].trim();
				map.put(s1, Integer.valueOf(s2));
			}
		}
		return map;
	}
}
