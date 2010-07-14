/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.alarm.csvout;

import java.util.List;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.Scheduler;
import org.apache.log4j.Logger;

/**
 * 1日分の履歴をCSV出力するサービス
 * 
 * @author maekawa
 * 
 */
public class CareerCsvout implements Service {
	private static final String SERVICE_NAME = "警報・状態出力サービス ";
	private final Logger logger = Logger.getLogger(CareerCsvout.class);
	private final Scheduler scheduler;
	private final ScheduleFactory factory;

	public CareerCsvout(ScheduleFactory factory) {
		scheduler = new Scheduler();
		this.factory = factory;
	}

	public void start() {
		String s = EnvironmentManager.get("/server/alarm/csvout", "false");
		boolean isCsvout = Boolean.parseBoolean(s);
		if (isCsvout) {
			List<Schedule> list = factory.getSchedules();
			for (Schedule schedule : list) {
				scheduler.schedule(schedule);
			}
			logger.info(SERVICE_NAME + "スケジュール開始");
		}
	}

	public void stop() {
		scheduler.cancel();
	}

	@Override
	public String toString() {
		return SERVICE_NAME + getClass().getName();
	}
}
