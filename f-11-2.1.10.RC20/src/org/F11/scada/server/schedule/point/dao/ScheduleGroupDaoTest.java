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

import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.seasar.dao.unit.S2DaoTestCase;

public class ScheduleGroupDaoTest extends S2DaoTestCase {
	private ScheduleGroupDao dao;

	public ScheduleGroupDaoTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		include("ScheduleGroupDaoTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetScheduleGroup() throws Exception {
		ScheduleGroupDto dto = new ScheduleGroupDto();
		dto.setPageId("schedule01");
		List data = dao.getScheduleGroup(dto);
		System.out.println(data);
	}
}
