/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet.alarm.event;

import java.sql.Timestamp;
import java.util.SortedMap;

import javax.swing.event.TableModelListener;

import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.table.AlarmTableModel;

public class TestTableModel implements AlarmTableModel {
	private Boolean onoff;
	private String provider;
	private String holder;
	private Timestamp timestamp;
	private Integer priority;

	public TestTableModel() {
		this(Boolean.TRUE, "P1", "H1", new Timestamp(0), new Integer(3));
	}

	public TestTableModel(String provider, String holder, Timestamp timestamp, Integer priority) {
		this(Boolean.TRUE, provider, holder, timestamp, priority);
	}

	public TestTableModel(Boolean onoff, String provider, String holder, Timestamp timestamp, Integer priority) {
		this.onoff = onoff;
		this.provider = provider;
		this.holder = holder;
		this.timestamp = timestamp;
		this.priority = priority;
	}

	public void addTableModelListener(TableModelListener l) {
	}

	public Class getColumnClass(int columnIndex) {
		return null;
	}

	public int getColumnCount() {
		return 0;
	}

	public String getColumnName(int columnIndex) {
		return null;
	}

	public int getRowCount() {
		return 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "jumpPath";
		case 1:
			return Boolean.TRUE;
		case 2:
			return priority;
		case 3:
			return "black";
		case 4:
			return new Integer(0);
		case 5:
			return provider;
		case 6:
			return holder;
		case 7:
			return new Integer(0);
		case 8:
			return "";
		case 9:
			return new Integer(0);
		case 10:
			return new Integer(0);
		case 11:
			return onoff;
		case 12:
			return timestamp;
		case 13:
			return "unit";
		case 14:
			return "kiki";
		case 15:
			return "message";

		default:
			return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void removeTableModelListener(TableModelListener l) {
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 2:
			priority = (Integer) aValue;
			break;
		case 5:
			provider = (String) aValue;
			break;
		case 6:
			holder = (String) aValue;
			break;
		case 11:
			onoff = (Boolean) aValue;
			break;
		case 12:
			timestamp = (Timestamp) aValue;
			break;
		default:
			break;
		}
	}

	public SortedMap getAlarmJournal(long t) {
		return null;
	}

	public SortedMap getCheckJournal(long t) {
		return null;
	}

	public int getColumn(String columnName) {
		if ("ジャンプパス".equals(columnName)) {
			return 0;
		} else if ("自動ジャンプ".equals(columnName)) {
			return 1;
		} else if ("優先順位".equals(columnName)) {
			return 2;
		} else if ("表示色".equals(columnName)) {
			return 3;
		} else if ("point".equals(columnName)) {
			return 4;
		} else if ("provider".equals(columnName)) {
			return 5;
		} else if ("holder".equals(columnName)) {
			return 6;
		}
		return 0;
	}

	public CheckEvent getLastCheckEvent() {
		return null;
	}

	public AlarmTableJournal getLastJournal() {
		return null;
	}

	public Object getValueAt(int row, String columnName) {
		return null;
	}

	public void insertRow(int row, Object[] data, DataValueChangeEventKey key) {
	}

	public void insertRow(int row, Object[] data) {
	}

	public void removeRow(int row, DataValueChangeEventKey key) {
	}

	public int searchRow(DataValueChangeEventKey key) {
		return 0;
	}

	public void setJournal(AlarmTableJournal aj) {
	}

	public void setValue(SortedMap value) {
	}

	public void setValueAt(
			Object[] data,
			int rowIndex,
			int columnIndex,
			DataValueChangeEventKey key) {
	}

	public void addCheckTableListener(CheckTableListener listener) {
	}

	public void fireCheckEvent(CheckEvent evt) {
	}

	public void removeCheckTableListener(CheckTableListener listener) {
	}
}
