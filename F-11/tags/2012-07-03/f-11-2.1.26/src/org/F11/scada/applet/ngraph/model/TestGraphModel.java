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

package org.F11.scada.applet.ngraph.model;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.LogData;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleList;

public class TestGraphModel extends AbstractGraphModel {
	private final static int MAX_RECORD = 6000;
	private ScheduledExecutorService service;
	private final Map<String, List<DataSeed>> dataMap;

	public TestGraphModel(GraphProperties graphProperties) {
		super(graphProperties);
		service = Executors.newScheduledThreadPool(1);
		dataMap = new HashMap<String, List<DataSeed>>();
		dataMap.put("log_minute", getDataSeed("log_minute", 200, 60));
		dataMap.put("log_tenminute", getDataSeed("log_tenminute", 210, 600));
		dataMap.put("log_hour", getDataSeed("log_hour", 220, 3600));
		setLogName("log_minute");
	}

	private List<DataSeed> getDataSeed(String logName, double n, int ca) {
		List<DataSeed> l =
			new ArrayList<DataSeed>(graphProperties.getSeriesGroups().size());
		l.add(new DataSeed(n, ca, 0, logName));
		l.add(new DataSeed(n + 100, ca, 1, logName));
		return l;
	}

	private DataSeed getDataSeed() {
		int groupNo = graphProperties.getGroupNo();
		return dataMap.get(getLogName()).get(groupNo);
	}

	public void initialize() {
		changeSupport.firePropertyChange(INITIALIZE, null, getDataSeed()
			.getLogDatas());
	}

	public void shutdown() {
		service.shutdown();
	}

	public int getMaxRecord() {
		return MAX_RECORD;
	}

	public boolean reachedMinimum() {
		return false;
	}

	public boolean reachedMaximum() {
		return false;
	}

	private class DataSeed implements Runnable {
		private LinkedList<LogData> logDatas;
		private int count;
		private boolean countFlag;
		private double updown;
		private boolean isUpDown;
		private final Calendar calendar;
		private static final int series = 6;
		private final double n;
		private final int calenderAddValue;

		private final int groupNo;
		private final String logName;

		public DataSeed(
				double n,
				int calenderAddValue,
				int groupNo,
				String logName) {
			calendar = Calendar.getInstance();
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			if (calenderAddValue >= 600) {
				calendar.set(Calendar.MINUTE, 0);
			}
			this.n = n;
			this.calenderAddValue = calenderAddValue;
			this.groupNo = groupNo;
			this.logName = logName;
			logDatas = getLogDataList();
			service.scheduleAtFixedRate(
				this,
				getInitialDelay(),
				this.calenderAddValue,
				TimeUnit.SECONDS);
		}

		private long getInitialDelay() {
			Calendar cal = Calendar.getInstance();
			int s = cal.get(Calendar.SECOND);
			return s == 0 ? 0 : 60 - (s % 60);
			// return 0;
		}

		private LinkedList<LogData> getLogDataList() {
			Calendar cal = (Calendar) calendar.clone();
			ArrayList<LogData> list = new ArrayList<LogData>();
			for (int i = 0; i < getMaxRecord() + 1; i++) {
				DoubleList value = getValueList();
				list.add(new LogData(cal.getTime(), value));
				cal.add(Calendar.SECOND, calenderAddValue * -1);
			}
			Collections.reverse(list);
			return new LinkedList<LogData>(list);
		}

		private DoubleList getValueList() {
			ArrayDoubleList l = new ArrayDoubleList(series);
			for (int j = 0; j < series; j++) {
				if (j == 0) {
					l.add(updown);
					if (isUpDown) {
						updown--;
						if (updown <= 0) {
							isUpDown = false;
						}
					} else {
						updown++;
						if (updown >= 100) {
							isUpDown = true;
						}
					}
				} else if (j == 1) {
					double r = PI * 2 / count * n;
					l.add(45 - cos(r) * 40);
				} else if (j == 2) {
					double r = PI * 2 / count * n;
					l.add(45 - sin(r) * 40);
				} else {
					Random random = new SecureRandom();
					double r = random.nextDouble();
					double v = r * 100D;
					l.add(v);
				}
			}
			if (countFlag) {
				count--;
				if (count <= 0) {
					countFlag = false;
				}
			} else {
				count++;
				if (count >= 512) {
					countFlag = true;
				}
			}
			return l;
		}

		public void run() {
			LogData newValue = addLogData();
			if (TestGraphModel.this.getLogName().equals(this.logName)
				&& groupNo == graphProperties.getGroupNo()) {
				changeSupport.firePropertyChange(
					GraphModel.VALUE_CHANGE,
					null,
					newValue);
			}
		}

		public List<LogData> getLogDatas() {
			return logDatas;
		}

		private LogData addLogData() {
			DoubleList value = getValueList();
			calendar.add(Calendar.SECOND, calenderAddValue);
			LogData logData = new LogData(calendar.getTime(), value);
			logDatas.add(logData);
			logDatas.removeFirst();
			return logData;
		}
	}
}
