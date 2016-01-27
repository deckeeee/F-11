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

import java.util.ArrayList;
import java.util.List;

import org.F11.scada.server.io.nio.CreateLogTableDao;
import org.F11.scada.server.io.nio.LogTableDao;
import org.F11.scada.server.io.nio.dto.LogDto;
import org.F11.scada.test.util.TimestampUtil;
import org.seasar.extension.unit.S2TestCase;

public class LogTableDaoImplTest extends S2TestCase {
	CreateLogTableDao createLogTableDao;
	LogTableDao dao;

	public LogTableDaoImplTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		include("TestCreateLogTableDaoImpl.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInsertSelectTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H0", 0.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H0", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H2", 2.0).toObjectArray());
			assertEquals(4, dao.insert("test_log_table", list));
			List res = dao.select("test_log_table");
			assertEquals(4, res.size());
			System.out.println(res);
			res = dao.select("test_log_table", "H0", 1);
			assertEquals(1, res.size());
			System.out.println(res);
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testGetRevisionTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			assertEquals("return 0", 0, dao.getRevision("test_log_table",
					TimestampUtil.parse("2006/01/01 00:00:00"), "H0"));
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H0", 0.0).toObjectArray());
			assertEquals("1åèë}ì¸", 1, dao.insert("test_log_table", list));
			assertEquals("1Ç™ï‘ÇÈÇÕÇ∏", 1, dao.getRevision("test_log_table",
					TimestampUtil.parse("2006/01/01 00:00:00"), "H0"));
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testSelectTimeTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H0", 0.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H0", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H2", 2.0).toObjectArray());
			assertEquals(4, dao.insert("test_log_table", list));
			List res = dao.select("test_log_table", "H0", TimestampUtil.parse("2006/01/01 00:00:00"));
			assertEquals(1, res.size());
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testSelectFirstTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H0", 0.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H0", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:02:00"), 0, "H1", 1.0).toObjectArray());
			assertEquals(5, dao.insert("test_log_table", list));
			List res = dao.selectFirst("test_log_table", "H0");
			assertEquals(1, res.size());
			LogDto dto = (LogDto) res.get(0);
			assertEquals(TimestampUtil.parse("2006/01/01 00:00:00"), dto.getWritedate());
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testSelectLastTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H0", 0.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H0", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:02:00"), 0, "H1", 1.0).toObjectArray());
			assertEquals(5, dao.insert("test_log_table", list));
			List res = dao.selectLast("test_log_table", "H1");
			assertEquals(1, res.size());
			LogDto dto = (LogDto) res.get(0);
			assertEquals(TimestampUtil.parse("2006/01/01 00:02:00"), dto.getWritedate());
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testSelectBeforeTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:02:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:03:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:04:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:05:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:06:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:07:00"), 0, "H1", 1.0).toObjectArray());
			assertEquals(8, dao.insert("test_log_table", list));
			List res = dao.selectBefore("test_log_table", "H1", TimestampUtil.parse("2006/01/01 00:04:00"), 10);
			assertEquals(4, res.size());
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testSelectAfterTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:02:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:03:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:04:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:05:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:06:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:07:00"), 0, "H1", 1.0).toObjectArray());
			assertEquals(8, dao.insert("test_log_table", list));
			List res = dao.selectAfter("test_log_table", "H1", TimestampUtil.parse("2006/01/01 00:04:00"), 10);
			assertEquals(4, res.size());
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testSelectBetweenTx() throws Exception {
		try {
			createLogTableDao.createTable("test_log_table");
			ArrayList list = new ArrayList();
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:00:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:01:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:02:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:03:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:04:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:05:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:06:00"), 0, "H1", 1.0).toObjectArray());
			list.add(new LogDto(TimestampUtil.parse("2006/01/01 00:07:00"), 0, "H1", 1.0).toObjectArray());
			assertEquals(8, dao.insert("test_log_table", list));
			List res = dao.select("test_log_table", "H1", TimestampUtil.parse("2006/01/01 00:04:00"), TimestampUtil.parse("2006/01/01 00:06:00"));
			assertEquals(3, res.size());
		} finally {
			createLogTableDao.dropTable("test_log_table");
		}
	}

	public void testSelectEmptyTx() throws Exception {
		List res = dao.select("test_log_table");
		assertEquals(0, res.size());
		res = dao.select("test_log_table", "H0", 10);
		assertEquals(0, res.size());
		res = dao.select("test_log_table", "H0", TimestampUtil.parse("2006/01/01 00:00:00"));
		assertEquals(0, res.size());
		res = dao.selectFirst("test_log_table", "H0");
		assertEquals(0, res.size());
		res = dao.selectLast("test_log_table", "H0");
		assertEquals(0, res.size());
		res = dao.selectBefore("test_log_table", "H1", TimestampUtil.parse("2006/01/01 00:04:00"), 10);
		assertEquals(0, res.size());
		res = dao.selectBefore("test_log_table", "H1", TimestampUtil.parse("2006/01/01 00:04:00"), 10);
		assertEquals(0, res.size());
	}
}
