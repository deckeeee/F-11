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

package org.F11.scada.xwife.applet.alarm;

import java.sql.Timestamp;

import javax.swing.table.TableModel;

import org.F11.scada.xwife.applet.alarm.event.CheckEvent;

class TableRowModel {
	public static final TableRowModel INIT_ROW_MODEL = new TableRowModel(
			"",
			false,
			Integer.MIN_VALUE,
			"",
			Integer.MIN_VALUE,
			"",
			"",
			Integer.MIN_VALUE,
			"",
			Integer.MIN_VALUE,
			Integer.MIN_VALUE,
			new Timestamp(0),
			"",
			"",
			"",
			false);
	private final String jumpPath;
	private final boolean autoJumpFlag;
	private final int autoJumpPriority;
	private final String alarmColor;
	private final int point;
	private final String provider;
	private final String holder;
	private final int soundType;
	private final String soundPath;
	private final int emailGroupId;
	private final int emailSendMode;
	private final boolean onoff;
	private final Timestamp timestamp;
	private final String unit;
	private final String kikiname;
	private final String message;

	public TableRowModel(TableModel model) {
		jumpPath = getString(model, 0, 0);
		autoJumpFlag = getBoolean(model, 0, 1);
		autoJumpPriority = getInteger(model, 0, 2);
		alarmColor = getString(model, 0, 3);
		point = getInteger(model, 0, 4);
		provider = getString(model, 0, 5);
		holder = getString(model, 0, 6);
		soundType = getInteger(model, 0, 7);
		soundPath = getString(model, 0, 8);
		emailGroupId = getInteger(model, 0, 9);
		emailSendMode = getInteger(model, 0, 10);
		onoff = getBoolean(model, 0, 11);
		timestamp = getTimestamp(model, 0, 12);
		unit = getString(model, 0, 13);
		kikiname = getString(model, 0, 14);
		message = getString(model, 0, 15);
	}

	private TableRowModel(
			String jumpPath,
			boolean autoJumpFlag,
			int autoJumpPriority,
			String alarmColor,
			int point,
			String provider,
			String holder,
			int soundType,
			String soundPath,
			int emailGroupId,
			int emailSendMode,
			Timestamp timestamp,
			String unit,
			String kikiname,
			String message,
			boolean onoff) {

		this.jumpPath = jumpPath;
		this.autoJumpFlag = autoJumpFlag;
		this.autoJumpPriority = autoJumpPriority;
		this.alarmColor = alarmColor;
		this.point = point;
		this.provider = provider;
		this.holder = holder;
		this.soundType = soundType;
		this.soundPath = soundPath;
		this.emailGroupId = emailGroupId;
		this.emailSendMode = emailSendMode;
		this.timestamp = timestamp;
		this.unit = unit;
		this.kikiname = kikiname;
		this.message = message;
		this.onoff = onoff;
	}

	private String getString(TableModel model, int row, int column) {
		return (String) model.getValueAt(row, column);
	}

	private boolean getBoolean(TableModel model, int row, int column) {
		try {
			return ((Boolean) model.getValueAt(row, column)).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}

	private int getInteger(TableModel model, int row, int column) {
		try {
			return ((Integer) model.getValueAt(row, column)).intValue();
		} catch (Exception e) {
			return 0;
		}
	}

	private Timestamp getTimestamp(TableModel model, int row, int column) {
		return (Timestamp) model.getValueAt(row, column);
	}

	/**
	 * このイベントがOnなのかOffなのかを返します
	 * 
	 * @return このイベントがOnなのかOffなのかを返します
	 */
	public boolean isOnoff() {
		return onoff;
	}

	public boolean isNotZero() {
		return autoJumpPriority != 0;
	}

	/**
	 * このオブジェクトが引数のものより小さい場合にtrueを返します。
	 * 
	 * @param row 比較するオブジェクト
	 * @return このオブジェクトが引数のものより小さい場合にtrueを返します。
	 */
	public boolean comparePriority(TableRowModel row) {
		return autoJumpPriority <= row.autoJumpPriority;
	}

	public boolean equalsKey(TableRowModel row) {
		return point == row.point && provider.equals(row.provider)
				&& holder.equals(row.holder);
	}

	public boolean equalsKey(CheckEvent evt) {
		return jumpPath.equals(evt.getJumpPath())
				&& autoJumpFlag == evt.isAutoJumpFlag()
				&& autoJumpPriority == evt.getAutoJumpPriority()
				&& alarmColor.equals(evt.getAlarmColor())
				&& point == evt.getPoint()
				&& provider.equals(evt.getProvider())
				&& holder.equals(evt.getHolder());
	}

	public String toString() {
		return "jumpPath=" + jumpPath + ", autoJumpFlag=" + autoJumpFlag
				+ ", autoJumpPriority=" + autoJumpPriority + ", alarmColor="
				+ alarmColor + ", point=" + point + ", provider=" + provider
				+ ", holder=" + holder + ", soundType=" + soundType
				+ ", soundPath=" + soundPath + ", emailGroupId=" + emailGroupId
				+ ", emailSendMode=" + emailSendMode + ", timestamp="
				+ timestamp + ", unit=" + unit + ", kikiname=" + kikiname
				+ ", message=" + message + ", onoff=" + onoff;
	}

	/**
	 * テスト用の確認メソッド
	 * 
	 * @return プロバイダ_ホルダ
	 */
	String getHolderId() {
		return provider + "_" + holder;
	}
}
