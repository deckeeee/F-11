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

package org.F11.scada.server.alarm.mail.dao;

import java.io.Serializable;

public class AlarmEmailSentAddressesDto implements Serializable {
	private static final long serialVersionUID = -6908959778647965894L;
	public static final String TABLE = "alarm_email_sent_addresses_table";
	
	private long alarmEmailSentAddressesId;
	private long alarmEmailSentId;
	private String address;

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public long getAlarmEmailSentAddressesId() {
		return alarmEmailSentAddressesId;
	}
	public void setAlarmEmailSentAddressesId(long alarmEmailSentAddressesId) {
		this.alarmEmailSentAddressesId = alarmEmailSentAddressesId;
	}
	public long getAlarmEmailSentId() {
		return alarmEmailSentId;
	}
	public void setAlarmEmailSentId(long alarmEmailSentId) {
		this.alarmEmailSentId = alarmEmailSentId;
	}
}
