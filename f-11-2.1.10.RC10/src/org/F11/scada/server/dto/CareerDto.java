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

package org.F11.scada.server.dto;

import java.sql.Timestamp;

import org.apache.commons.lang.time.FastDateFormat;

public class CareerDto {
	public static final String TABLE = "career_table";

	private Timestamp entrydate;
	private String unit;
	private String name;
	private String attributeName;
	private String message;
	private String priorityName;

	public Timestamp getEntrydate() {
		return entrydate;
	}

	public void setEntrydate(Timestamp entrydate) {
		this.entrydate = entrydate;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPriorityName() {
		return priorityName;
	}

	public void setPriorityName(String priorityName) {
		this.priorityName = priorityName;
	}

	public String csvOut() {
		FastDateFormat f =
			FastDateFormat.getInstance("\"yyyy/MM/dd HH:mm:ss\"");
		return f.format(entrydate)
			+ csvOut(unit)
			+ csvOut(name)
			+ csvOut(attributeName)
			+ csvOut(message)
			+ csvOut(priorityName);
	}

	private String csvOut(String s) {
		return s == null ? "" : ",\"" + s + "\"";
	}

	@Override
	public String toString() {
		return entrydate
			+ " "
			+ unit
			+ " "
			+ name
			+ " "
			+ attributeName
			+ " "
			+ message
			+ " "
			+ priorityName;
	}
}
