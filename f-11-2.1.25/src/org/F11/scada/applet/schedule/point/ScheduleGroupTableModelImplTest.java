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

package org.F11.scada.applet.schedule.point;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;

public class ScheduleGroupTableModelImplTest extends TestCase {

	public ScheduleGroupTableModelImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGroupTable() throws Exception {
		ScheduleGroupTableModelImpl model = new ScheduleGroupTableModelImpl(
				new SchedulePointServiceProxyTest(),
				"schedule01",
				false);
		assertEquals(new Long(0), model.getValueAt(0, 0));
		assertEquals(new Integer(1), model.getValueAt(0, 1));
		assertEquals("グループ名1", model.getValueAt(0, 2));
		assertEquals(new Long(1), model.getValueAt(1, 0));
		assertEquals(new Integer(2), model.getValueAt(1, 1));
		assertEquals("グループ名2", model.getValueAt(1, 2));
		assertEquals(new Long(2), model.getValueAt(2, 0));
		assertEquals(new Integer(3), model.getValueAt(2, 1));
		assertEquals("グループ名3", model.getValueAt(2, 2));
	}

	private static class SchedulePointServiceProxyTest implements
			SchedulePointService {
		public void init() {
		}

		public List getScheduleGroup(ScheduleGroupDto adto)
				throws RemoteException {
			ArrayList data = new ArrayList();
			for (int i = 0; i < 3; i++) {
				ScheduleGroupDto dto = new ScheduleGroupDto();
				dto.setId(new Long(i));
				dto.setGroupNo(new Integer(i + 1));
				dto.setGroupName("グループ名" + (i + 1));
				data.add(dto);
			}
			return data;
		}

		public SchedulePointDto getSchedulePoint(ScheduleSearchDto dto)
				throws RemoteException {
			return null;
		}

		public SchedulePointDto getSchedulePointByGroup(ScheduleSearchDto dto)
				throws RemoteException {
			return null;
		}

		public int updateSchedulePoint(SchedulePointRowDto dto)
				throws RemoteException {
			return 0;
		}

		public void duplicateSeparateSchedule(
				ScheduleGroupDto src,
				SchedulePointRowDto[] dest) throws RemoteException {
		}

		public void duplicateSeparateSchedule(DuplicateSeparateScheduleDto dto)
				throws RemoteException {
		}

		public WifeDataSchedule getSeparateSchedule(SchedulePointRowDto dto) {
			return null;
		}

		public void updateSeperateSchedule(
				SchedulePointRowDto dto,
				Date date,
				WifeDataSchedule data) throws RemoteException {
		}
	}
}
