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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * 一覧表示するテーブルクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class TableSymbol extends AbstractTableModel {
	private static final long serialVersionUID = -438063889776159013L;
	private List colTitles = new ArrayList();
	private List rows = new ArrayList();

	public TableSymbol() {
	}

	/**
	 *
	 */
	public void addColumnTitle(String title) {
		colTitles.add(title);
		fireTableStructureChanged();
	}

	/**
	 *
	 */
	public void addRow(List rowData) {
		int firstRow = rows.size();
		rows.add(rowData);
		fireTableRowsInserted(firstRow, firstRow + 1);
	}

	/**
	 * TableModel interface
	 */
	public int getRowCount() {
		return rows.size();
	}
	public int getColumnCount() {
		return colTitles.size();
	}
	public String getColumnName(int columnIndex) {
		return (String)colTitles.get(columnIndex);
	}
	public Class getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		return ((List)rows.get(rowIndex)).get(columnIndex);
	}
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		throw new UnsupportedOperationException();
	}
}
