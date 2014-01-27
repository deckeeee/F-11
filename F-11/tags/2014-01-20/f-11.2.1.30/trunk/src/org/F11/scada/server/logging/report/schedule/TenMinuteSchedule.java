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

package org.F11.scada.server.logging.report.schedule;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class TenMinuteSchedule implements CsvSchedule {
	private static final OutputState state = new DayOutputState();
	private static Logger logger = Logger.getLogger(TenMinuteSchedule.class);

	public static CsvSchedule getSomeMinuteSchedule(String name) {
		if ("TENMINUTE".equalsIgnoreCase(name)) {
			return TEN;
		} else if ("THIRTYMINUTE".equalsIgnoreCase(name)) {
			return THIRTY;
		} else if ("QMINUTE".equalsIgnoreCase(name)) {
			return QMINUTE;
		} else if ("FIVEMINUTE".equalsIgnoreCase(name)) {
			return FIVEMINUTE;
		} else if ("SIXTYMINUTE".equalsIgnoreCase(name)) {
			return TEN;
		} else {
			throw new IllegalArgumentException("指定されたロギングスケジュールが存在しません = "
					+ name);
		}
	}

	/**
	 * 10分毎ロギングのCSV出力範囲
	 */
	private static CsvSchedule TEN = new CsvSchedule() {
		public Timestamp startTime(long now, boolean startMode) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now);
			cal.add(Calendar.MINUTE, -1);
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			cal.clear();
			if (startMode) {
				cal.set(y, m, d, 0, 0);
			} else {
				cal.set(y, m, d, 0, 1);
			}
			return new Timestamp(cal.getTimeInMillis());
		}

		public Timestamp endTime(long now, boolean startMode) {
			Timestamp start = startTime(now, startMode);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(start.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return new Timestamp(cal.getTimeInMillis());
		}

		public boolean isOutput() {
			return state.isOutput();
		}
	};

	/**
	 * 30分毎ロギングのCSV出力範囲
	 */
	private static CsvSchedule THIRTY = new CsvSchedule() {
		public Timestamp startTime(long now, boolean startMode) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now);
			cal.add(Calendar.MINUTE, -1);
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			cal.clear();
			cal.set(y, m, d, 0, 30);
			return new Timestamp(cal.getTimeInMillis());
		}

		public Timestamp endTime(long now, boolean startMode) {
			Timestamp start = startTime(now, startMode);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(start.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return new Timestamp(cal.getTimeInMillis());
		}

		public boolean isOutput() {
			return state.isOutput();
		}
	};

	/**
	 * 15分毎ロギングのCSV出力範囲
	 */
	private static CsvSchedule QMINUTE = new CsvSchedule() {
		public Timestamp startTime(long now, boolean startMode) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now);
			cal.add(Calendar.MINUTE, -1);
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			cal.clear();
			cal.set(y, m, d, 0, 15);
			return new Timestamp(cal.getTimeInMillis());
		}

		public Timestamp endTime(long now, boolean startMode) {
			Timestamp start = startTime(now, startMode);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(start.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return new Timestamp(cal.getTimeInMillis());
		}

		public boolean isOutput() {
			return state.isOutput();
		}
	};

	/**
	 * 5分毎ロギングのCSV出力範囲
	 */
	private static CsvSchedule FIVEMINUTE = new CsvSchedule() {
		public Timestamp startTime(long now, boolean startMode) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(now);
			cal.add(Calendar.MINUTE, -1);
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			cal.clear();
			cal.set(y, m, d, 0, 5);
			return new Timestamp(cal.getTimeInMillis());
		}

		public Timestamp endTime(long now, boolean startMode) {
			Timestamp start = startTime(now, startMode);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(start.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			return new Timestamp(cal.getTimeInMillis());
		}

		public boolean isOutput() {
			return state.isOutput();
		}
	};

	public Timestamp startTime(long now, boolean startMode) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now);
		cal.add(Calendar.MINUTE, -1);
		int y = cal.get(Calendar.YEAR);
		int m = cal.get(Calendar.MONTH);
		int d = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		if (startMode) {
			cal.set(y, m, d, 0, 0);
		} else {
			cal.set(y, m, d, 0, 1);
		}
		return new Timestamp(cal.getTimeInMillis());
	}

	public Timestamp endTime(long now, boolean startMode) {
		Timestamp start = startTime(now, startMode);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(start.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return new Timestamp(cal.getTimeInMillis());
	}

	public boolean isOutput() {
		return state.isOutput();
	}
}
