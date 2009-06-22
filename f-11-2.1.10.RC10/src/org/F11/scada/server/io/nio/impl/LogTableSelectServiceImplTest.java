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

package org.F11.scada.server.io.nio.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.nio.LogTableDao;
import org.F11.scada.server.io.nio.dto.LogDto;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TimestampUtil;
import org.apache.commons.collections.primitives.DoubleList;

import java.util.concurrent.locks.ReentrantLock;

public class LogTableSelectServiceImplTest extends TestCase {
	private LogTableSelectServiceImpl service;

	public LogTableSelectServiceImplTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		service = new LogTableSelectServiceImpl(new ReentrantLock());
		service.setLogTableDao(new TestLogTableDao());
		service.setItemUtil(new TestItemUtil());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSelect() throws Exception {
		ArrayList list = getHolderStringList();
		List datas = service.select("log_table_minute", list, 10);
		assertNotNull(datas);
		assertEquals(2, datas.size());
		LoggingData data = (LoggingData) datas.get(0);
		assertEquals(TimestampUtil.parse("2006/01/01 00:00:00"), data.getTimestamp());
		assertEquals(1.0D, data.getDouble(0), 0D);
		assertEquals(2.0D, data.getDouble(1), 0D);
		data = (LoggingData) datas.get(1);
		assertEquals(TimestampUtil.parse("2006/01/01 00:01:00"), data.getTimestamp());
		assertEquals(1.111D, data.getDouble(0), 0D);
		assertEquals(2.111D, data.getDouble(1), 0D);
	}

	private ArrayList getHolderStringList() {
		ArrayList list = new ArrayList();
		list.add(new HolderString("P1", "D_500_BcdSingle"));
		list.add(new HolderString("P1", "D_501_BcdSingle"));
		return list;
	}

	public void testSelectTime() throws Exception {
		ArrayList list = getHolderStringList();
		List datas = service.select("log_table_minute", list, new Timestamp(0));
		assertNotNull(datas);
		assertEquals(2, datas.size());
		LoggingData data = (LoggingData) datas.get(0);
		assertEquals(TimestampUtil.parse("2006/01/01 00:00:00"), data.getTimestamp());
		assertEquals(1.0D, data.getDouble(0), 0D);
		assertEquals(2.0D, data.getDouble(1), 0D);
		data = (LoggingData) datas.get(1);
		assertEquals(TimestampUtil.parse("2006/01/01 00:01:00"), data.getTimestamp());
		assertEquals(1.111D, data.getDouble(0), 0D);
		assertEquals(2.111D, data.getDouble(1), 0D);
	}

	public void testSelectFirstLast() throws Exception {
		LoggingRowData data = service.selectFirst("log_table_minute", getHolderStringList());
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2006/01/01 00:00:00"), data.getTimestamp());
		data = service.selectLast("log_table_minute", getHolderStringList());
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2006/01/01 00:10:00"), data.getTimestamp());
	}

	public void testBeforeAfter() throws Exception {
		ArrayList list = getHolderStringList();
		List datas = service.selectBeforeAfter("log_table_minute", list, new Timestamp(0), 10);
		assertNotNull(datas);
		assertEquals(4, datas.size());
		LoggingData data = (LoggingData) datas.get(0);
		assertEquals(TimestampUtil.parse("2006/01/01 00:00:00"), data.getTimestamp());
		assertEquals(1.0D, data.getDouble(0), 0D);
		assertEquals(1.0D, data.getDouble(1), 0D);
		data = (LoggingData) datas.get(1);
		assertEquals(TimestampUtil.parse("2006/01/01 00:01:00"), data.getTimestamp());
		assertEquals(1.111D, data.getDouble(0), 0D);
		assertEquals(1.111D, data.getDouble(1), 0D);
		data = (LoggingData) datas.get(2);
		assertEquals(TimestampUtil.parse("2006/01/01 00:02:00"), data.getTimestamp());
		assertEquals(1.0D, data.getDouble(0), 0D);
		assertEquals(1.0D, data.getDouble(1), 0D);
		data = (LoggingData) datas.get(3);
		assertEquals(TimestampUtil.parse("2006/01/01 00:03:00"), data.getTimestamp());
		assertEquals(1.111D, data.getDouble(0), 0D);
		assertEquals(1.111D, data.getDouble(1), 0D);
	}


	/**
	 * select(String tableName, List dataHolders, Timestamp startTime, Timestamp endTime)‚ÌƒeƒXƒg
	 * @throws Exception
	 */
	public void testBetween() throws Exception {
		ArrayList list = getHolderStringList();
		List datas = service.select(
				"log_table_minute",
				list,
				TimestampUtil.parse("2006/01/01 00:01:00"),
				TimestampUtil.parse("2006/01/01 00:02:00"));
		assertNotNull(datas);
		assertEquals(2, datas.size());
		String data = (String) datas.get(0);
		assertEquals("2006/01/01,00:00:00,1.000,1.000,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,", data);
		data = (String) datas.get(1);
		assertEquals("2006/01/01,00:01:00,1.111,1.111,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,", data);
	}

	private static class TestLogTableDao implements LogTableDao {

		public int insert(String tableName, List dtos) {
			return 0;
		}

		public int getRevision(String tableName, Timestamp time, String holderId) {
			return 0;
		}

		public List select(String tableName, String holderId, int limit) {
			return getLogDtos(holderId);
		}

		public List select(String tableName) {
			throw new UnsupportedOperationException();
		}

		public List select(String tableName, String holderId, Timestamp time) {
			return getLogDtos(holderId);
		}

		private List getLogDtos(String holderId) {
			ArrayList list = new ArrayList();
			if ("P1_D_500_BcdSingle".equals(holderId)) {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "P1_D_500_BcdSingle", 1.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "P1_D_500_BcdSingle", 1.111));
			} else {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "P1_D_501_BcdSingle", 2.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "P1_D_501_BcdSingle", 2.111));
			}
			return list;
		}

		public List selectFirst(String tableName, String holderId) {
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "P1_D_500_BcdSingle", 1.000));
			return list;
		}

		public List selectLast(String tableName, String holderId) {
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:10:00"), 0, "P1_D_500_BcdSingle", 1.111));
			return list;
		}

		public List selectAfter(String tableName, String holderId, Timestamp start, int limit) {
			ArrayList list = new ArrayList();
			if ("P1_D_500_BcdSingle".equals(holderId)) {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:02:00"), 0, "P1_D_500_BcdSingle", 1.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:03:00"), 0, "P1_D_500_BcdSingle", 1.111));
			} else {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:02:00"), 0, "P1_D_501_BcdSingle", 1.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:03:00"), 0, "P1_D_501_BcdSingle", 1.111));
			}
			return list;
		}

		public List selectBefore(String tableName, String holderId, Timestamp start, int limit) {
			ArrayList list = new ArrayList();
			if ("P1_D_500_BcdSingle".equals(holderId)) {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "P1_D_500_BcdSingle", 1.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "P1_D_500_BcdSingle", 1.111));
			} else {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "P1_D_501_BcdSingle", 1.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "P1_D_501_BcdSingle", 1.111));
			}
			return list;
		}

		public List select(String tableName, String holderId, Timestamp startTime, Timestamp endTime) {
			ArrayList list = new ArrayList();
			if ("P1_D_500_BcdSingle".equals(holderId)) {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "P1_D_500_BcdSingle", 1.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "P1_D_500_BcdSingle", 1.111));
			} else {
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "P1_D_501_BcdSingle", 1.000));
				list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "P1_D_501_BcdSingle", 1.111));
			}
			return list;
		}
	}


	private static class TestItemUtil implements ItemUtil {

		public ConvertValue[] createConvertValue(Collection holders, String tablename) {
			return null;
		}

		public Map createConvertValueMap(Collection holders, String tableTame) {
			Map map = new HashMap();
			map.put(new HolderString("P1", "D_500_BcdSingle"), ConvertValue.valueOfANALOG(0, 4000, 0, 4000, "0.000"));
			map.put(new HolderString("P1", "D_501_BcdSingle"), ConvertValue.valueOfANALOG(0, 4000, 0, 4000, "0.000"));
			return map;
		}

		public Map createConvertValueMap(Collection holders) {
			Map map = new HashMap();
			map.put(new HolderString("P1", "D_500_BcdSingle"), ConvertValue.valueOfANALOG(0, 4000, 0, 4000, "0.000"));
			map.put(new HolderString("P1", "D_501_BcdSingle"), ConvertValue.valueOfANALOG(0, 4000, 0, 4000, "0.000"));
			return map;
		}

		public Map createDateHolderValuesMap(Collection dataHolders, String tableName, MultiRecordDefine multiRecordDefine) {
			throw new UnsupportedOperationException();
		}

		public DoubleList createHolderValue(Collection dataHolders, String tableName) {
			throw new UnsupportedOperationException();
		}

		public Item getItem(HolderString dataHolder, Map itemPool) {
			throw new UnsupportedOperationException();
		}

		public Map getItemMap(Item[] items) {
			throw new UnsupportedOperationException();
		}

		public Item[] getItems(Collection holders, Map itemPool) {
			throw new UnsupportedOperationException();
		}
	}
}
