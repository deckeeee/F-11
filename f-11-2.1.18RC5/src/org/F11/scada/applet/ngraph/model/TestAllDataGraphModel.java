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
import java.util.logging.Logger;


import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.LogData;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleList;

public class TestAllDataGraphModel extends AbstractGraphModel {
	private final static int MAX_RECORD = 6000;
	private final Logger logger =
		Logger.getLogger(TestAllDataGraphModel.class.getName());
	private final Map<String, List<DataSeed>> dataMap;

	public TestAllDataGraphModel(GraphProperties graphProperties) {
		super(graphProperties);
		dataMap = new HashMap<String, List<DataSeed>>();
		dataMap.put("log_minute", getDataSeed("log_minute", 200, 60));
		dataMap.put("log_tenminute", getDataSeed("log_tenminute", 210, 600));
		dataMap.put("log_hour", getDataSeed("log_hour", 220, 3600));
		setLogName("log_minute");
	}

	private List<DataSeed> getDataSeed(String logName, double n, int ca) {
		List<DataSeed> l =
			new ArrayList<DataSeed>(graphProperties.getSeriesGroups().size());
		l.add(new DataSeed(n, ca));
		l.add(new DataSeed(n + 100, ca));
		return l;
	}

	public int getMaxRecord() {
		return MAX_RECORD;
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
	}

	public boolean reachedMinimum() {
		getDataSeed().reachedMinimum();
		return false;
	}

	public boolean reachedMaximum() {
		getDataSeed().reachedMaximum();
		return false;
	}

	private class DataSeed {
		private LinkedList<LogData> logDatas;
		private int count;
		private boolean countFlag;
		private double updown;
		private boolean isUpDown;
		private final Calendar calendar;
		private static final int series = 6;
		private final double n;
		private final int calenderAddValue;

		public DataSeed(double n, int calenderAddValue) {
			calendar = Calendar.getInstance();
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			if (calenderAddValue >= 600) {
				calendar.set(Calendar.MINUTE, 0);
			}
			this.n = n;
			this.calenderAddValue = calenderAddValue;
			logDatas = getLogDataList();
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

		public List<LogData> getLogDatas() {
			return logDatas;
		}

		public void reachedMinimum() {
			LogData ld = logDatas.getFirst();
			logger.info("" + ld);
		}

		public void reachedMaximum() {
			LogData ld = logDatas.getLast();
			logger.info("" + ld);
		}
	}
}
