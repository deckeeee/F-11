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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.F11.scada.applet.ngraph.editor.SeriesPropertyData;

/**
 * エディタのシリーズプロパティのテーブルモデル
 * 
 * @author maekawa
 * 
 */
public class SeriesPropertyTableModel extends AbstractTableModel implements
		MouseListener, KeyListener {
	private static final long serialVersionUID = -5850823694570225954L;
	private static final String[] titles =
		{ "", "最小値", "最大値", "記号", "機器名称", "単位" };
	private List<SeriesPropertyData> seriesPropertyDatas;
	private List<SeriesPropertyData> backupSeriesPropertyDatas;
	private Map<Integer, String> colorMap;

	public SeriesPropertyTableModel(String seriesColors) {
		colorMap = getColorMap(seriesColors);
	}

	private Map<Integer, String> getColorMap(String seriesColors) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		String[] seriesColorStr = seriesColors.split(",");
		int i = 0;
		for (String color : seriesColorStr) {
			map.put(i++, color.trim());
		}
		return map;
	}

	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return null != seriesPropertyDatas ? seriesPropertyDatas.size() : 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (null != seriesPropertyDatas) {
			SeriesPropertyData rowData = seriesPropertyDatas.get(rowIndex);
			DecimalFormat f = new DecimalFormat(rowData.getVerticalFormat());
			switch (columnIndex) {
			case 0:
				return rowData.getColorObject();
			case 1:
				return f.format(rowData.getMin());
			case 2:
				return f.format(rowData.getMax());
			case 3:
				return rowData.getUnit();
			case 4:
				return rowData.getName();
			case 5:
				return rowData.getMark();
			default:
				throw new IllegalStateException();
			}
		} else {
			return "";
		}
	}

	@Override
	public String getColumnName(int column) {
		return titles[column];
	}

	public void setValueAt(List<SeriesPropertyData> seriesPropertyDatas) {
		this.seriesPropertyDatas = seriesPropertyDatas;
		fireTableDataChanged();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		performMouseEvent(e);
	}

	public void mouseReleased(MouseEvent e) {
	}

	private void performMouseEvent(MouseEvent e) {
		JTable table = (JTable) e.getSource();
		if (table.isEnabled()) {
			int row = table.rowAtPoint(e.getPoint());
			fireRowChanged(table, row);
		}
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
		seriesPropertyDatas = model.getUnitDatas(row);
		fireTableDataChanged();
	}

	public void removeRow(int row) {
		if (null == backupSeriesPropertyDatas) {
			backupSeriesPropertyDatas =
				new ArrayList<SeriesPropertyData>(seriesPropertyDatas.size());
			for (SeriesPropertyData ud : seriesPropertyDatas) {
				backupSeriesPropertyDatas.add(new SeriesPropertyData(ud));
			}
		}
		seriesPropertyDatas.remove(row);
		resetIndexAndColor();
		fireTableRowsDeleted(row, row);
	}

	private void resetIndexAndColor() {
		int i = 0;
		for (SeriesPropertyData spd : seriesPropertyDatas) {
			spd.setColor(colorMap.get(i % colorMap.size()));
			spd.setIndex(i++);
		}
	}

	public void undo() {
		if (null != backupSeriesPropertyDatas) {
			seriesPropertyDatas.clear();
			seriesPropertyDatas.addAll(backupSeriesPropertyDatas);
			fireTableDataChanged();
			backupSeriesPropertyDatas = null;
		}
	}

	public SeriesPropertyData getRow(int row) {
		return seriesPropertyDatas.get(row);
	}

	public void insertRow(SeriesPropertyData seriesPropertyData) {
		seriesPropertyDatas.add(seriesPropertyData);
		resetIndexAndColor();
		fireTableRowsInserted(seriesPropertyDatas.size(), seriesPropertyDatas
			.size());
	}

	public void commit() {
		if (null != backupSeriesPropertyDatas) {
			backupSeriesPropertyDatas = null;
		}
	}
}
