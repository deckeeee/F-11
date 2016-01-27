/*
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
 */
package org.F11.scada.tool.conf.io;

public class RemoveDefineBean {
	private String tableName = "log_";
	private String dateFieldName = "f_date";
	private String removeValue = "0";
	private boolean daily = true;
	private String executeDay = "1";
	private String executeHour = "0";
	private String executeMinute = "0";
	private String executeSecond = "0";

	public boolean isDaily() {
		return daily;
	}
	public void setDaily(boolean daily) {
		this.daily = daily;
	}
	public String getDateFieldName() {
		return dateFieldName;
	}
	public void setDateFieldName(String dateFieldName) {
		this.dateFieldName = dateFieldName;
	}
	public String getExecuteDay() {
		return executeDay;
	}
	public void setExecuteDay(String executeDay) {
		this.executeDay = executeDay;
	}
	public String getExecuteHour() {
		return executeHour;
	}
	public void setExecuteHour(String executeHour) {
		this.executeHour = executeHour;
	}
	public String getExecuteMinute() {
		return executeMinute;
	}
	public void setExecuteMinute(String executeMinute) {
		this.executeMinute = executeMinute;
	}
	public String getExecuteSecond() {
		return executeSecond;
	}
	public void setExecuteSecond(String executeSecond) {
		this.executeSecond = executeSecond;
	}
	public String getRemoveValue() {
		return removeValue;
	}
	public void setRemoveValue(String removeValue) {
		this.removeValue = removeValue;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
