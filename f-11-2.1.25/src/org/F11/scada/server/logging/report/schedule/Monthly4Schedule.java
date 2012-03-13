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

import java.sql.Timestamp;
import java.util.Calendar;

public class Monthly4Schedule implements CsvSchedule {
	private final OutputState state = new DummyOutputState();

	public Timestamp startTime(long now, boolean startMode) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH);
		cal.clear();
		if (m <= Calendar.APRIL) {
			cal.add(Calendar.YEAR, -1);
		}
		if (startMode) {
			cal.set(y, Calendar.MAY, 1);
		} else {
			cal.set(y, Calendar.MAY, 1);
		}
		System.out.println("S:" + new Timestamp(cal.getTimeInMillis()));
		return new Timestamp(cal.getTimeInMillis());
	}

	public Timestamp endTime(long now, boolean startMode) {
		Timestamp start = startTime(now, startMode);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(start.getTime());
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH);
		if (m >= Calendar.MAY) {
			cal.add(Calendar.YEAR, 1);
		}
		if (startMode) {
			cal.set(y, Calendar.APRIL, 30, 23, 59);
		} else {
			cal.set(y, Calendar.APRIL, 30, 23, 59);
		}
		System.out.println("E:" + new Timestamp(cal.getTimeInMillis()));
		return new Timestamp(cal.getTimeInMillis());
	}

	public boolean isOutput() {
		return state.isOutput();
	}

}
