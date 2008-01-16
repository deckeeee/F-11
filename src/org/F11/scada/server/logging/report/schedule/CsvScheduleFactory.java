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

package org.F11.scada.server.logging.report.schedule;

public class CsvScheduleFactory {
	public CsvSchedule getCsvSchedule(String name) {
		if ("NULL".equalsIgnoreCase(name)) {
			return new NullSchedule();
		} else if ("REGULAR".equalsIgnoreCase(name)) {
			return new RegularSchedule();
		} else if ("MINUTE".equalsIgnoreCase(name)) {
			return new MinuteSchedule();
		} else if (isTenminute(name)) {
			return new TenMinuteSchedule();
		} else if ("ONEMINUTE".equalsIgnoreCase(name)) {
			return new OneMinuteSchedule();
		} else if ("HOUR".equalsIgnoreCase(name)) {
			return new HourSchedule();
		} else if ("DAILY".equalsIgnoreCase(name)) {
			return new DailySchedule();
		} else if ("MONTHLY".equalsIgnoreCase(name)) {
			return new MonthlySchedule();
		} else if ("YEARLY".equalsIgnoreCase(name)) {
			return new YearlySchedule();
		} else if ("BMS".equalsIgnoreCase(name)) {
			return new BMSSchedule();
		} else if ("MINUTEHOUROUT".equalsIgnoreCase(name)) {
			return new MinuteHourOutSchedule();
		} else if ("ONEHOURMONTHOUT".equalsIgnoreCase(name)) {
			return new OneHourMonthOutSchedule();
		} else if ("GODA01".equals(name)) {
			return new GODA01Schedule();
		} else if (name.startsWith("GODA")) {
			return new GODASchedule();
		} else if ("MONTHLYMONTHOUT".equalsIgnoreCase(name)) {
			return new MonthlyMonthOutSchedule();
		} else {
			throw new IllegalArgumentException("指定されたスケジュールはありません。 " + name);
		}
	}

	private boolean isTenminute(String name) {
		return "TENMINUTE".equalsIgnoreCase(name)
				|| "QMINUTE".equalsIgnoreCase(name)
				|| "FIVEMINUTE".equalsIgnoreCase(name)
				|| "THIRTYMINUTE".equalsIgnoreCase(name)
				|| "SIXTYMINUTE".equalsIgnoreCase(name);
	}
}
