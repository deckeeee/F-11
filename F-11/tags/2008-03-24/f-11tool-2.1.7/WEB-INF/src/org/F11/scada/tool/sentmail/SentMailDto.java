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

package org.F11.scada.tool.sentmail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class SentMailDto implements Serializable {
	private static final long serialVersionUID = -6732867116268705812L;
	public static final String TABLE = "alarm_email_sent_table";
	
	private long alarmEmailSentId;
	private String provider;
	private String holder;
	private Timestamp sentdate;
	private String unit;
	private String name;
	private String message;
	private boolean value;
	private List addresses;

	public long getAlarmEmailSentId() {
		return alarmEmailSentId;
	}
	public void setAlarmEmailSentId(long alarmEmailSentId) {
		this.alarmEmailSentId = alarmEmailSentId;
	}
	public String getHolder() {
		return holder;
	}
	public void setHolder(String holder) {
		this.holder = holder;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public Timestamp getSentdate() {
		return sentdate;
	}
	public void setSentdate(Timestamp sentdate) {
		this.sentdate = sentdate;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	public List getAddresses() {
		return addresses;
	}
	public void setAddresses(List addresses) {
		this.addresses = addresses;
	}
	public String toString() {
		return "alarmEmailSentId=" + alarmEmailSentId
		+ ", provider=" + provider
		+ ", holder=" + holder
		+ ", sentdate=" + sentdate
		+ ", unit=" + unit
		+ ", name=" + name
		+ ", message=" + message
		+ ", value=" + value
		+ ", addresses=" + addresses;
	}
}
