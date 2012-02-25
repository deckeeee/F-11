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

package org.F11.scada.server.remove.impl;

import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.ScheduleIterator;
import org.F11.scada.scheduling.Scheduler;
import org.F11.scada.server.remove.RemoveDao;
import org.F11.scada.server.remove.RemoveDto;
import org.F11.scada.server.remove.RemoveService;

public class RemoveServiceImpl implements RemoveService {
	private final Scheduler scheduler;
	private RemoveDao dao;

	public RemoveServiceImpl() {
		scheduler = new Scheduler();
	}

	public void setDao(RemoveDao dao) {
		this.dao = dao;
	}

	public void addSchedule(RemoveDto dto, ScheduleIterator iterator) {
		Schedule schedule = new Schedule();
		schedule.setScheduleIterator(iterator);
		schedule.setTask(new RemoveSchedulerTask(dao, dto));
		scheduler.schedule(schedule);
	}

	public void start() {
		// NOP
	}

	public void stop() {
		scheduler.cancel();
	}
}
