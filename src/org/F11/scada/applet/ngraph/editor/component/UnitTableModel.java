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

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.F11.scada.applet.ngraph.editor.UnitData;

public class UnitTableModel extends AbstractTableModel implements
		MouseListener, KeyListener {
	private static final String[] titles = { "", "ãLçÜ", "ã@äÌñºèÃ", "íPà " };
	private List<UnitData> unitDatas;
	private List<UnitData> backupUnitDatas;
	private Map<Integer, Color> colorMap;

	public UnitTableModel() {
		colorMap = getColorMap();
	}

	private Map<Integer, Color> getColorMap() {
		HashMap<Integer, Color> map = new HashMap<Integer, Color>();
		map.put(0, Color.yellow);
		map.put(1, Color.magenta);
		map.put(2, Color.cyan);
		map.put(3, Color.red);
		map.put(4, Color.green);
		map.put(5, Color.white);
		return map;
	}

	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return null != unitDatas ? unitDatas.size() : 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (null != unitDatas) {
			switch (columnIndex) {
			case 0:
				return unitDatas.get(rowIndex).getUnitColor();
			case 1:
				return unitDatas.get(rowIndex).getUnitNo();
			case 2:
				return unitDatas.get(rowIndex).getUnitName();
			case 3:
				return unitDatas.get(rowIndex).getUnitMark();
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

	public void setValueAt(List<UnitData> unitDatas) {
		this.unitDatas = unitDatas;
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
		GroupTableModel model = (GroupTableModel) table.getModel();
		unitDatas = model.getUnitDatas(row);
		fireTableDataChanged();
	}

	public void removeRow(int row) {
		if (null == backupUnitDatas) {
			backupUnitDatas = new ArrayList<UnitData>(unitDatas.size());
			for (UnitData ud : unitDatas) {
				backupUnitDatas.add(new UnitData(ud));
			}
		}
		unitDatas.remove(row);
		resetColor();
		fireTableRowsDeleted(row, row);
	}

	private void resetColor() {
		int i = 0;
		for (UnitData ud : unitDatas) {
			ud.setUnitColor(colorMap.get(i++));
		}
	}

	public void undo() {
		if (null != backupUnitDatas) {
			unitDatas.clear();
			unitDatas.addAll(backupUnitDatas);
			fireTableDataChanged();
			backupUnitDatas = null;
		}
	}

	public UnitData getRow(int row) {
		return new UnitData(unitDatas.get(row));
	}

	public void insertRow(UnitData unitData) {
		setColor(unitData);
		unitDatas.add(unitData);
		fireTableRowsInserted(unitDatas.size(), unitDatas.size());
	}

	private void setColor(UnitData unitData) {
		switch (unitDatas.size() % 6) {
		case 0:
			unitData.setUnitColor(Color.yellow);
			break;
		case 1:
			unitData.setUnitColor(Color.magenta);
			break;
		case 2:
			unitData.setUnitColor(Color.cyan);
			break;
		case 3:
			unitData.setUnitColor(Color.red);
			break;
		case 4:
			unitData.setUnitColor(Color.green);
			break;
		case 5:
			unitData.setUnitColor(Color.white);
			break;

		default:
			break;
		}
	}
	
	public void commit() {
		if (null != backupUnitDatas) {
			backupUnitDatas = null;
		}
	}
}
