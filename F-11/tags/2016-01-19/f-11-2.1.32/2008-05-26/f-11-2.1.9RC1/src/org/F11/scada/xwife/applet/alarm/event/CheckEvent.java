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

import java.io.ObjectStreamException;
import java.sql.Timestamp;
import java.util.EventObject;

import javax.swing.table.TableModel;

import org.F11.scada.server.alarm.table.AlarmTableModel;

public class CheckEvent extends EventObject {
	private static final long serialVersionUID = -634218644151257401L;
	private final String checkEventSource;
	private final String jumpPath;
	private final boolean autoJumpFlag;
	private final int autoJumpPriority;
	private final String alarmColor;
	private final int point;
	private final String provider;
	private final String holder;
	private final Timestamp timestamp;
	private final Timestamp onDate;

	public CheckEvent(Object source, AlarmTableModel model, int row, Timestamp timestamp) {
		super(source);
		checkEventSource = (String) source;
		jumpPath = getString(model, row, model.getColumn("ジャンプパス"));
		autoJumpFlag = getBoolean(model, row, model.getColumn("自動ジャンプ"));
		autoJumpPriority = getInteger(model, row, model.getColumn("優先順位"));
		alarmColor = getString(model, row, model.getColumn("表示色"));
		point = getInteger(model, row, model.getColumn("point"));
		provider = getString(model, row, model.getColumn("provider"));
		holder = getString(model, row, model.getColumn("holder"));
		this.timestamp = timestamp;
		onDate = getOnDate(model, row, model.getColumn("発生・運転"));
	}

	private CheckEvent(CheckEvent src, Timestamp timestamp) {
		super(src.checkEventSource);
		checkEventSource = (String) source;
		jumpPath = src.getJumpPath();
		autoJumpFlag = src.isAutoJumpFlag();
		autoJumpPriority = src.getAutoJumpPriority();
		alarmColor = src.getAlarmColor();
		point = src.getPoint();
		provider = src.getProvider();
		holder = src.getHolder();
		this.timestamp = timestamp;
		onDate = src.onDate;
	}

	private String getString(TableModel model, int row, int column) {
		return (String) model.getValueAt(row, column);
	}

	private boolean getBoolean(TableModel model, int row, int column) {
		try {
			return ((Boolean) model.getValueAt(row, column)).booleanValue();
		} catch (NullPointerException e) {
			return false;
		}
	}

	private int getInteger(TableModel model, int row, int column) {
		try {
			return ((Integer) model.getValueAt(row, column)).intValue();
		} catch (ClassCastException e) {
			return 0;
		}
	}

	private Timestamp getOnDate(TableModel model, int row, int column) {
		try {
			return (Timestamp) model.getValueAt(row, column);
		} catch (ClassCastException e) {
			return new Timestamp(0);
		}
	}

	public String getAlarmColor() {
		return alarmColor;
	}

	public boolean isAutoJumpFlag() {
		return autoJumpFlag;
	}

	public int getAutoJumpPriority() {
		return autoJumpPriority;
	}

	public String getHolder() {
		return holder;
	}

	public String getJumpPath() {
		return jumpPath;
	}

	public int getPoint() {
		return point;
	}

	public String getProvider() {
		return provider;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public CheckEvent setTimestamp(Timestamp t) {
		return new CheckEvent(this, t);
	}

	public Timestamp getOnDate() {
		return onDate;
	}

	public String toString() {
		return super.toString()
		+ ", jumpPath=" + jumpPath 
		+ ", autoJumpFlag=" + autoJumpFlag
		+ ", autoJumpPriority=" + autoJumpPriority
		+ ", alarmColor=" + alarmColor
		+ ", point=" + point
		+ ", provider=" + provider
		+ ", holder=" + holder
		+ ", onDate=" + onDate;
	}

	public boolean equalsKey(CheckEvent dst) {
		return checkEventSource.equals(dst.checkEventSource)
			&& jumpPath.equals(dst.jumpPath)
			&& autoJumpFlag == dst.autoJumpFlag
			&& autoJumpPriority == dst.autoJumpPriority
			&& alarmColor.equals(alarmColor)
			&& point == dst.point
			&& provider.equals(dst.provider)
			&& holder.equals(dst.holder)
			&& onDate.equals(dst.onDate);
	}

	private Object readResolve() throws ObjectStreamException {
		return new CheckEvent(this, this.timestamp);
	}
}
