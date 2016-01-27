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

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.F11.scada.applet.ngraph.editor.SeriesData;
import org.F11.scada.applet.ngraph.editor.SeriesPropertyData;
import org.F11.scada.applet.ngraph.editor.Trend3Data;

/**
 * エディタのシリーズテーブルモデル
 * 
 * @author maekawa
 *
 */
public class SeriesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -5442648308048607648L;
	private String[] titles = { "No", "グループ名称" };
	private List<SeriesData> seriesDatas;

	public SeriesTableModel(Trend3Data trend3Data) {
		seriesDatas = trend3Data.getSeriesDatas();
	}

	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return seriesDatas.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.format("No.%03d", seriesDatas
				.get(rowIndex)
				.getGroupNo());
		case 1:
			return seriesDatas.get(rowIndex).getGroupName();
		default:
			throw new IllegalStateException("不正なcolumnIndexです : " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return titles[column];
	}

	public SeriesData getGroupData(int index) {
		return index >= 0 ? seriesDatas.get(index) : null;
	}

	public List<SeriesPropertyData> getUnitDatas(int index) {
		return index >= 0 ? getGroupData(index).getSeriesProperties() : null;
	}

	public void insertRow(SeriesData groupData) {
		seriesDatas.add(groupData);
		int size = seriesDatas.size();
		fireTableRowsInserted(size, size);
	}

	public void removeRow(int row) {
		seriesDatas.remove(row);
		renumber();
		fireTableRowsDeleted(row, row);
	}

	private void renumber() {
		int i = 1;
		for (SeriesData gd : seriesDatas) {
			gd.setGroupNo(i++);
		}
	}

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
	}

	public SeriesData getRow(int row) {
		return seriesDatas.get(row);
	}
}
