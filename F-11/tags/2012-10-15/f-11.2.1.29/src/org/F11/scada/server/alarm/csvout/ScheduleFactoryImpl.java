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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.scheduling.DailyIterator;
import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.SchedulerTask;
import org.F11.scada.server.dao.CareerDao;
import org.F11.scada.server.dto.CareerDto;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

public class ScheduleFactoryImpl implements ScheduleFactory {
	private final CareerDao dao;

	public ScheduleFactoryImpl(CareerDao dao) {
		this.dao = dao;
	}

	public List<Schedule> getSchedules() {
		int hh =
			Integer.parseInt(EnvironmentManager.get(
				"/server/alarm/csvout/hour",
				"0"));
		int mm =
			Integer.parseInt(EnvironmentManager.get(
				"/server/alarm/csvout/minute",
				"10"));
		ArrayList<Schedule> list = new ArrayList<Schedule>();
		list
			.add(new Schedule(new WriteTask(dao), new DailyIterator(hh, mm, 0)));
		return list;
	}

	private static class WriteTask extends SchedulerTask {
		private static final String INIT_PATH = "C:/careerbackup";
		private static final String INIT_FILE = "'career'yyyyMMdd'.csv'";
		private final Logger logger = Logger.getLogger(WriteTask.class);
		private final CareerDao dao;

		WriteTask(CareerDao dao) {
			this.dao = dao;
		}

		@Override
		public void run() {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			Format format = FastDateFormat.getInstance("yyyy/MM/dd");
			String ymd = format.format(cal.getTime());
			logger
				.info("åxïÒCSVèoóÕäJén : " + ymd + " 00:00:00Å`" + ymd + " 23:59:59");
			List<CareerDto> dtos =
				dao.getCareer(ymd + " 00:00:00", ymd + " 23:59:59");
			try {
				writeCsv(dtos, cal);
			} catch (IOException e) {
				logger.error("åxïÒCSVèoóÕÇ≈ÉGÉâÅ[î≠ê∂:", e);
			}
		}

		private void writeCsv(List<CareerDto> dtos, Calendar cal)
				throws IOException {
			File pathFile = getPathFile();
			String fileName = getFileName(cal);
			PrintWriter out = null;
			try {
				out =
					new PrintWriter(
						new FileWriter(new File(pathFile, fileName)),
						true);
				for (CareerDto careerDto : dtos) {
					out.println(careerDto.csvOut());
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}

		private File getPathFile() {
			String path =
				EnvironmentManager.get("/server/alarm/csvout/path", INIT_PATH);
			File pathFile = new File(path);
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			return pathFile;
		}

		private String getFileName(Calendar cal) {
			String file =
				EnvironmentManager.get("/server/alarm/csvout/file", INIT_FILE);
			Format format = FastDateFormat.getInstance(file);
			return format.format(cal.getTime());
		}
	}
}
