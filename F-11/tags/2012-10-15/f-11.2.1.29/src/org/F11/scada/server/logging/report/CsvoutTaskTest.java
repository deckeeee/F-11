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
 *
 */

package org.F11.scada.server.logging.report;

import java.sql.Timestamp;
import java.util.Calendar;

import junit.framework.TestCase;

import org.F11.scada.server.logging.report.schedule.CsvSchedule;
import org.F11.scada.server.logging.report.schedule.CsvScheduleFactory;

/**
 * @author hori
 */
public class CsvoutTaskTest extends TestCase {
	private CsvScheduleFactory factory;

	/**
	 * Constructor for CsvoutTaskTest.
	 *
	 * @param arg0
	 */
	public CsvoutTaskTest(String arg0) {
		super(arg0);
		factory = new CsvScheduleFactory();
	}

	public void testSchedule() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2003, Calendar.NOVEMBER, 7, 18, 10, 11);
		Timestamp src = new Timestamp(cal.getTimeInMillis());
		// Timestamp src = new Timestamp(2003 - 1900, 10, 7, 18, 10, 11, 5);

		CsvSchedule csvSchedule = factory.getCsvSchedule("REGULAR");
		Timestamp tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("2003-11-07 00:01:00.0", tm.toString());
		assertEquals("2003-11-07 00:00:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2003-11-08 00:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());

		csvSchedule = factory.getCsvSchedule("MINUTE");
		tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("2003-11-07 00:01:00.0", tm.toString());
		assertEquals("2003-11-07 00:00:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2003-11-08 00:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());

		csvSchedule = factory.getCsvSchedule("TENMINUTE");
		tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("2003-11-07 00:01:00.0", tm.toString());
		assertEquals("2003-11-07 00:00:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2003-11-08 00:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());

		csvSchedule = factory.getCsvSchedule("HOUR");
		tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("2003-11-07 01:00:00.0", tm.toString());
		assertEquals("2003-11-07 00:00:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2003-11-08 00:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());

		csvSchedule = factory.getCsvSchedule("DAILY");
		tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("2003-11-02 00:00:00.0", tm.toString());
		assertEquals("2003-11-01 00:00:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2003-12-01 00:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());

		csvSchedule = factory.getCsvSchedule("MONTHLY");
		tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("2003-02-01 00:00:00.0", tm.toString());
		assertEquals("2003-01-01 00:00:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2004-01-01 00:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());

		csvSchedule = factory.getCsvSchedule("YEARLY");
		tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("2002-11-07 18:10:11.0", tm.toString());
		assertEquals("2002-11-07 18:10:11.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2003-11-07 18:10:11.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());

		csvSchedule = factory.getCsvSchedule("NULL");
		tm = csvSchedule.startTime(src.getTime(), false);
		assertEquals("1970-01-01 09:00:00.0", tm.toString());
		assertEquals("1970-01-01 09:00:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("1970-01-01 09:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());
	}

	public void testKanden30() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2003, Calendar.NOVEMBER, 7, 18, 10, 11);
		Timestamp src = new Timestamp(cal.getTimeInMillis());
		CsvSchedule csvSchedule = factory.getCsvSchedule("KANDEN30");
		Timestamp tm = csvSchedule.startTime(src.getTime(), true);
		assertEquals("2003-11-07 07:30:00.0", tm.toString());
		assertEquals("2003-11-07 07:30:00.0", csvSchedule.startTime(
				src.getTime(),
				true).toString());
		assertEquals("2003-11-08 07:00:00.0", csvSchedule.endTime(
				src.getTime(),
				true).toString());
	}
}
