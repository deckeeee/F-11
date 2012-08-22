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

public class BMSSchedule implements CsvSchedule {
	public Timestamp startTime(long now, boolean startMode) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MINUTE, -1);
		return new Timestamp(cal.getTimeInMillis());
	}

	public Timestamp endTime(long now, boolean startMode) {
		Timestamp start = startTime(now, startMode);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(start.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return new Timestamp(cal.getTimeInMillis());
	}

	public boolean isOutput() {
		return true;
	}
}
