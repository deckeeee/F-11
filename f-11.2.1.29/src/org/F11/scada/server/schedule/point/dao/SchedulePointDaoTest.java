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

package org.F11.scada.server.schedule.point.dao;

import java.util.List;

import org.F11.scada.server.schedule.point.dto.SchedulePointInsertDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;
import org.seasar.dao.pager.PagerViewHelper;
import org.seasar.dao.unit.S2DaoTestCase;

public class SchedulePointDaoTest extends S2DaoTestCase {
	SchedulePointDao dao;
	SchedulePointInsertDao insertDao;

	public SchedulePointDaoTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		include("SchedulePointDaoTest.dicon");
	}

	private void insertTestData() throws Exception {
		insertDao.deleteAll();
		SchedulePointInsertDto dto = new SchedulePointInsertDto();
		dto.setProvider("P1");
		dto.setHolder("D_1900001_Digital");
		dto.setPageId("schedule01");
		dto.setGroupNo(2);
		dto.setGroupProvider("P1");
		dto.setGroupHolder("D_601_BcdSingle");
		dto.setSort(false);
		dto.setSeparateProvider("");
		dto.setSeparateHolder("");
		insertDao.insert(dto);

		dto.setProvider("P1");
		dto.setHolder("D_1900002_Digital");
		dto.setPageId("schedule01");
		dto.setGroupNo(3);
		dto.setGroupProvider("P1");
		dto.setGroupHolder("D_602_BcdSingle");
		dto.setSort(false);
		dto.setSeparateProvider("");
		dto.setSeparateHolder("");
		insertDao.insert(dto);

		dto.setProvider("P1");
		dto.setHolder("D_1900000_Digital");
		dto.setPageId("schedule01");
		dto.setGroupNo(1);
		dto.setGroupProvider("P1");
		dto.setGroupHolder("D_600_BcdSingle");
		dto.setSort(true);
		dto.setSeparateProvider("P1");
		dto.setSeparateHolder("D_2000_Schedule");
		insertDao.insert(dto);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetAllGroupPoint() throws Exception {
		insertTestData();
		List data = dao.getAllSchedulePoint();
		assertEquals(3, data.size());
	}

	public void testGetGroupPointTx() throws Exception {
		insertTestData();
		ScheduleSearchDto dto = new ScheduleSearchDto();
		dto.setGroupNo(new Integer(0));
		PagerViewHelper helper = new PagerViewHelper(dto);
		dto.setLimit(1);
		List data = dao.getSchedulePoint(dto);
		assertTrue(helper.isNext());
		assertFalse(helper.isPrev());
		assertEquals(1, helper.getPageCount());
		assertEquals(3, helper.getCount());

		dto.setOffset(1);
		data = dao.getSchedulePoint(dto);
		assertTrue(helper.isNext());
		assertTrue(helper.isPrev());
		assertEquals(2, helper.getPageCount());
		assertEquals(3, helper.getCount());

		dto.setOffset(2);
		data = dao.getSchedulePoint(dto);
		assertFalse(helper.isNext());
		assertTrue(helper.isPrev());
		assertEquals(3, helper.getPageCount());
		assertEquals(3, helper.getCount());
	}

	public void testUpdateTx() throws Exception {
		insertTestData();
		ScheduleSearchDto sdto = new ScheduleSearchDto();
		sdto.setGroupNo(new Integer(0));
		sdto.setUnit("AHU-1-2");
		sdto.setLimit(10);
		List data = dao.getSchedulePoint(sdto);
		assertEquals(1, data.size());

		SchedulePointRowDto row = (SchedulePointRowDto) data.get(0);
		row.setGroupNo(new Integer(3));
		assertEquals(1, dao.updateSchedulePoint(row));

		data = dao.getSchedulePoint(sdto);
		assertEquals(1, data.size());
		row = (SchedulePointRowDto) data.get(0);
		assertEquals(new Integer(3), row.getGroupNo());
	}

	public void testGetScheduleByGroupTx() throws Exception {
		insertTestData();
		ScheduleSearchDto sdto = new ScheduleSearchDto();
		sdto.setGroupNo(new Integer(2));
		sdto.setPageId("schedule01");
		sdto.setLimit(10);
		List data = dao.getSchedulePointByGroup(sdto);
		assertEquals(1, data.size());
		SchedulePointRowDto row = (SchedulePointRowDto) data.get(0);
		System.out.println(row);
		assertEquals("", row.getGroupName());
	}
}
