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
		assertEquals("2��", 2, map.size());
	}

	public void testGeppo() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		Map map = store.getAutoPrintSchedules();
		AutoPrintSchedule schedule = (AutoPrintSchedule) map.get("geppo1");
		assertNotNull("not null", schedule);
		assertEquals("����", "����", schedule.getScheduleName());
		assertFalse("false", schedule.isAutoOn());
		assertFalse("false", schedule.isNow());
		assertEquals("�����l���Ԃ�", "1�� 0�� 0��", schedule.getDate());
		assertTrue("AutoPrintSchedule.Monthly�̃C���X�^���X���Ԃ�͂�", AutoPrintSchedule.Monthly.class.isInstance(schedule));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		assertEquals("�����̈�����Ԃ�", new Timestamp(cal.getTimeInMillis()), schedule.getEndTime());
		cal.add(Calendar.MONTH, -1);
		assertEquals("�O���̈�����Ԃ�", new Timestamp(cal.getTimeInMillis()), schedule.getStartTime());
	}

	public void testNippo() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		Map map = store.getAutoPrintSchedules();
		AutoPrintSchedule schedule = (AutoPrintSchedule) map.get("nippo1");
		assertNotNull("not null", schedule);
		assertEquals("����", "����", schedule.getScheduleName());
		assertFalse("false", schedule.isAutoOn());
		assertFalse("false", schedule.isNow());
		assertEquals("�����l���Ԃ�", "0�� 0��", schedule.getDate());
		assertTrue("AutoPrintSchedule.Daily�̃C���X�^���X���Ԃ�͂�", AutoPrintSchedule.Daily.class.isInstance(schedule));
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		assertEquals("�{���̈ꎞ���Ԃ�", new Timestamp(cal.getTimeInMillis()), schedule.getEndTime());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		assertEquals("����̈ꎞ���Ԃ�", new Timestamp(cal.getTimeInMillis()), schedule.getStartTime());
	}

	public void testGetLoggingHeddarList() throws Exception {
		AutoPrintDataStore store = new AutoPrintDataStore();
		HolderString[] holders = {new HolderString("P1", "D_500_BcdSingle"), new HolderString("P1", "D_501_BcdSingle"), };
		List list = store.getLoggingHeddarList("log_table_daily", Arrays.asList(holders));
		assertNotNull(list);
		assertEquals("2��Map", 2, list.size());
		Map map = (Map) list.get(0);
		assertEquals("unit�L��", "AHU-1-1", map.get("unit"));
		assertEquals("unit��", "1F A�� �󒲋@ �d��", map.get("name"));
		assertNull("unit�P�ʋL��", map.get("unit_mark"));
		map = (Map) list.get(1);
		assertEquals("unit�L��", "AHU-1-2", map.get("unit"));
		assertEquals("unit��", "1F B�� �󒲋@ �d��", map.get("name"));
		assertNull("unit�P�ʋL��", map.get("unit_mark"));
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
		assertEquals("4�s", 4, list.size());
		assertEquals("CSV�f�[�^", "2004/05/19,01:01:00,50.3,52.5", list.get(0));
		assertEquals("CSV�f�[�^", "2004/05/19,02:01:00,50.5,52.6", list.get(1));
		assertEquals("CSV�f�[�^", "2004/05/19,03:01:00,50.8,52.6", list.get(2));
		assertEquals("CSV�f�[�^", "2004/05/19,04:01:00,51.0,52.6", list.get(3));
	}
}