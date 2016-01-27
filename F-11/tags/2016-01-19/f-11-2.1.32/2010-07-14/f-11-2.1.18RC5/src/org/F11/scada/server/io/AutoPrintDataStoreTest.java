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

package org.F11.scada.server.io;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.F11.scada.server.autoprint.AutoPrintSchedule;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TimestampUtil;

public class AutoPrintDataStoreTest extends TestCase {

	public AutoPrintDataStoreTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetAutoPrintSchedules() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		Map map = store.getAutoPrintSchedules();
		assertEquals("2つ", 2, map.size());
	}

	public void testGeppo() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		Map map = store.getAutoPrintSchedules();
		AutoPrintSchedule schedule = (AutoPrintSchedule) map.get("geppo1");
		assertNotNull("not null", schedule);
		assertEquals("月報", "月報", schedule.getScheduleName());
		assertFalse("false", schedule.isAutoOn());
		assertFalse("false", schedule.isNow());
		assertEquals("初期値が返る", "1日 0時 0分", schedule.getDate());
		assertTrue("AutoPrintSchedule.Monthlyのインスタンスが返るはず", AutoPrintSchedule.Monthly.class.isInstance(schedule));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		assertEquals("今月の一日が返る", new Timestamp(cal.getTimeInMillis()), schedule.getEndTime());
		cal.add(Calendar.MONTH, -1);
		assertEquals("前月の一日が返る", new Timestamp(cal.getTimeInMillis()), schedule.getStartTime());
	}

	public void testNippo() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		Map map = store.getAutoPrintSchedules();
		AutoPrintSchedule schedule = (AutoPrintSchedule) map.get("nippo1");
		assertNotNull("not null", schedule);
		assertEquals("日報", "日報", schedule.getScheduleName());
		assertFalse("false", schedule.isAutoOn());
		assertFalse("false", schedule.isNow());
		assertEquals("初期値が返る", "0時 0分", schedule.getDate());
		assertTrue("AutoPrintSchedule.Dailyのインスタンスが返るはず", AutoPrintSchedule.Daily.class.isInstance(schedule));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		assertEquals("本日の一時が返る", new Timestamp(cal.getTimeInMillis()), schedule.getEndTime());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		assertEquals("昨日の一時が返る", new Timestamp(cal.getTimeInMillis()), schedule.getStartTime());
	}

	public void testGetLoggingHeddarList() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		HolderString[] holders = {new HolderString("P1", "D_500_BcdSingle"), new HolderString("P1", "D_501_BcdSingle"), };
		List list = store.getLoggingHeddarList("log_table_daily", Arrays.asList(holders));
		assertNotNull(list);
		assertEquals("2つのMap", 2, list.size());
		Map map = (Map) list.get(0);
		assertEquals("unit記号", "AHU-1-1", map.get("unit"));
		assertEquals("unit名", "1F A室 空調機 電流", map.get("name"));
		assertNull("unit単位記号", map.get("unit_mark"));
		map = (Map) list.get(1);
		assertEquals("unit記号", "AHU-1-2", map.get("unit"));
		assertEquals("unit名", "1F B室 空調機 電流", map.get("name"));
		assertNull("unit単位記号", map.get("unit_mark"));
	}

	public void testGetLoggingData() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		HolderString[] holders = {new HolderString("P1", "D_500_BcdSingle"), new HolderString("P1", "D_501_BcdSingle"), };
		List list = store.getLoggingDataList(
				"log_table_daily",
				TimestampUtil.parse("2004/05/19 01:01:00"),
				TimestampUtil.parse("2004/05/19 05:01:00"),
				Arrays.asList(holders));
		assertNotNull(list);
		assertEquals("4行", 4, list.size());
		assertEquals("CSVデータ", "2004/05/19,01:01:00,50.3,52.5", list.get(0));
		assertEquals("CSVデータ", "2004/05/19,02:01:00,50.5,52.6", list.get(1));
		assertEquals("CSVデータ", "2004/05/19,03:01:00,50.8,52.6", list.get(2));
		assertEquals("CSVデータ", "2004/05/19,04:01:00,51.0,52.6", list.get(3));
	}
}