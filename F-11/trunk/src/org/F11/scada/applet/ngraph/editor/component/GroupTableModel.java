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
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.F11.scada.applet.ngraph.editor.GroupData;
import org.F11.scada.applet.ngraph.editor.UnitData;

public class GroupTableModel extends AbstractTableModel {
	private String[] titles = { "No", "ÉOÉãÅ[ÉvñºèÃ" };
	private List<GroupData> groupDatas;

	public GroupTableModel() {
		groupDatas = getTestData();
	}

	private List<GroupData> getTestData() {
		ArrayList<GroupData> l = new ArrayList<GroupData>();
		l.add(new GroupData(0, "ébíË1F On 8:00 Of 17:00", getTestUnitList(1)));
		l.add(new GroupData(1, "ébíË1F On 8:00 Of 17:00", getTestUnitList(2)));
		l.add(new GroupData(2, "ébíË1F On 8:00 Of 17:00", getTestUnitList(3)));
		l.add(new GroupData(3, "ébíË1F On 8:00 Of 17:00", getTestUnitList(4)));
		l.add(new GroupData(4, "ébíË1F On 8:00 Of 17:00", getTestUnitList(5)));
		l.add(new GroupData(5, "ébíË1F On 8:00 Of 17:00", getTestUnitList(6)));
		l.add(new GroupData(6, "ébíË1F On 8:00 Of 17:00", getTestUnitList(7)));
		l.add(new GroupData(7, "ébíË1F On 8:00 Of 17:00", getTestUnitList(8)));
		l.add(new GroupData(8, "ébíË1F On 8:00 Of 17:00", getTestUnitList(9)));
		l.add(new GroupData(9, "ébíË1F On 8:00 Of 17:00", getTestUnitList(10)));
		l.add(new GroupData(10, "ébíË1F On 8:00 Of 17:00", getTestUnitList(11)));
		l.add(new GroupData(11, "ébíË1F On 8:00 Of 17:00", getTestUnitList(12)));
		l.add(new GroupData(12, "ébíË1F On 8:00 Of 17:00", getTestUnitList(13)));
		l.add(new GroupData(13, "ébíË1F On 8:00 Of 17:00", getTestUnitList(14)));
		l.add(new GroupData(14, "ébíË1F On 8:00 Of 17:00", getTestUnitList(15)));
		l.add(new GroupData(15, "ébíË1F On 8:00 Of 17:00", getTestUnitList(16)));
		l.add(new GroupData(16, "ébíË1F On 8:00 Of 17:00", getTestUnitList(17)));
		return l;
	}

	public static List<UnitData> getTestUnitList(int i) {
		ArrayList<UnitData> l = new ArrayList<UnitData>();
		l.add(new UnitData(
			Color.yellow,
			"CV-AHU-0101",
			"CVìè " + i + "F éññ±é∫ånìù",
			"",
			0F,
			100F));
		l.add(new UnitData(
			Color.magenta,
			"CV-AHU-0101",
			"CVìè " + i + "F éññ±é∫ånìù",
			"",
			0F,
			100F));
		l.add(new UnitData(
			Color.cyan,
			"CV-AHU-0101",
			"CVìè " + i + "F éññ±é∫ånìù",
			"",
			0F,
			100F));
		l.add(new UnitData(
			Color.red,
			"CV-AHU-0101",
			"CVìè " + i + "F éññ±é∫ånìù",
			"",
			0F,
			100F));
		l.add(new UnitData(
			Color.green,
			"CV-AHU-0101",
			"CVìè " + i + "F éññ±é∫ånìù",
			"",
			0F,
			100F));
		l.add(new UnitData(
			Color.white,
			"CV-AHU-0101",
			"CVìè " + i + "F éññ±é∫ånìù",
			"",
			0F,
			100F));
		return l;
	}

	public int getColumnCount() {
		return titles.length;
	}

	public int getRowCount() {
		return groupDatas.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.format("No.%03d", groupDatas
				.get(rowIndex)
				.getGroupNo());
		case 1:
			return groupDatas.get(rowIndex).getGroupName();
		default:
			throw new IllegalStateException("ïsê≥Ç»columnIndexÇ≈Ç∑ : " + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return titles[column];
	}

	public GroupData getGroupData(int index) {
		return index >= 0 ? groupDatas.get(index) : null;
	}

	public List<UnitData> getUnitDatas(int index) {
		return index >= 0 ? getGroupData(index).getUnitDatas() : null;
	}

	public void insertRow(GroupData groupData) {
		groupDatas.add(groupData);
		int size = groupDatas.size();
		fireTableRowsInserted(size, size);
	}

	public void removeRow(int row) {
		groupDatas.remove(row);
		renumber();
		fireTableRowsDeleted(row, row);
	}

	private void renumber() {
		int i = 0;
		for (GroupData gd : groupDatas) {
			gd.setGroupNo(i++);
		}
	}

	public void updateRow(int row) {
		fireTableRowsUpdated(row, row);
	}

	public GroupData getRow(int row) {
		return groupDatas.get(row);
	}
}
