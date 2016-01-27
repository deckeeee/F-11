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

package org.F11.scada.tool.conf.timeset;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.F11.scada.tool.conf.TimeSetManager;
import org.F11.scada.tool.conf.io.TimeSetTaskBean;

public class TimeSetTaskTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -5081424581509230856L;
	private static final String[] title =
		{ "タスク名", "スケジュール", "オフセット", "オフセット単位" };
	private final List<TimeSetTaskBean> data;
	private final TimeSetManager manager;

	public TimeSetTaskTableModel(TimeSetManager manager) {
		data = manager.getTimeSetTask();
		this.manager = manager;
	}

	public int getColumnCount() {
		return title.length;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return data.get(rowIndex).get("name");
		case 1:
			return data.get(rowIndex).get("schedule");
		case 2:
			return data.get(rowIndex).get("offset");
		case 3:
			return data.get(rowIndex).get("milliOffsetMode");
		default:
			throw new IllegalStateException("不正な列=" + columnIndex);
		}
	}

	@Override
	public String getColumnName(int column) {
		return title[column];
	}

	void insert(TimeSetTaskBean bean) {
		data.add(bean);
		int size = data.size();
		fireTableRowsInserted(size, size);
		manager.setTimeSetTask(bean);
	}

	void remove(int row) {
		if (0 <= row) {
			TimeSetTaskBean bean = data.remove(row);
			fireTableRowsDeleted(row, row);
			manager.removeTimeSetTask(bean);
		}
	}

	TimeSetTaskBean get(int row) {
		return data.get(row);
	}

	void update(TimeSetTaskBean bean, int row) {
		data.set(row, bean);
		fireTableRowsUpdated(row, row);
		manager.setTimeSetTask(bean);
	}
}
