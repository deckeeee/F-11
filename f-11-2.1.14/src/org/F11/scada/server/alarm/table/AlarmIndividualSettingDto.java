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

package org.F11.scada.server.alarm.table;

import java.io.Serializable;

public class AlarmIndividualSettingDto implements Serializable {
	private static final long serialVersionUID = -8269876173923104024L;
	
	public static final String TABLE = "alarm_individual_setting_table";

	private long alarmIndividualSettingId;
	private String provider;
	private String holder;
	private int type;

	public long getAlarmIndividualSettingId() {
		return alarmIndividualSettingId;
	}
	public void setAlarmIndividualSettingId(long alarmIndividualSettingId) {
		this.alarmIndividualSettingId = alarmIndividualSettingId;
	}
	public String getHolder() {
		return holder;
	}
	public void setHolder(String holder) {
		this.holder = holder;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return alarmIndividualSettingId
			+ " " + provider
			+ " " + holder
			+ " " + type;
	}
}
