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

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.F11.scada.applet.graph.DefaultSelectiveValueListHandlerFactory;
import org.F11.scada.applet.graph.SelectiveValueListHandlerFactory;
import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.LogData;
import org.F11.scada.applet.ngraph.SeriesGroup;
import org.F11.scada.applet.ngraph.SeriesProperties;
import org.F11.scada.server.io.SelectiveValueListHandler;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

public class DefaultGraphModel extends AbstractGraphModel {
	private final Logger logger = Logger.getLogger(DefaultGraphModel.class);
	/** データハンドラマネージャーです。 */
	private SelectiveValueListHandler valueListHandler;
	/** データ更新スケジュール */
	private ScheduledExecutorService service;

	public DefaultGraphModel(GraphProperties graphProperties) {
		super(graphProperties);
		service = Executors.newScheduledThreadPool(1);
		SelectiveValueListHandlerFactory factory =
			new DefaultSelectiveValueListHandlerFactory();
		valueListHandler = factory.getSelectiveValueListHandler();
	}

	public void initialize() {
		List<LogData> logData = getLogData();
		if (0 < logData.size()) {
			service.scheduleAtFixedRate(
				getLastLogData(logData),
				getInitialDelay(),
				60,
				TimeUnit.SECONDS);
			changeSupport.firePropertyChange(INITIALIZE, null, logData);
		}
	}

	private LoggingTask getLastLogData(List<LogData> logData) {
		return new LoggingTask(logData.get(logData.size() - 1));
	}

	private long getInitialDelay() {
		Calendar cal = Calendar.getInstance();
		int s = cal.get(Calendar.SECOND);
		return s == 0 ? 5 : 65 - (s % 60);
	}

	private List<LogData> getLogData() {
		List<LogData> list = Collections.emptyList();
		try {
			SortedMap<Timestamp, DoubleList> map =
				valueListHandler.getInitialData(
					getLogName(),
					getHolderStrings());
			list = convertLogDataList(map);
		} catch (RemoteException e) {
			serverError(e);
		}
		return list;
	}

	private List<HolderString> getHolderStrings() {
		SeriesGroup sg = graphProperties.getSeriesGroup();
		ArrayList<HolderString> list = new ArrayList<HolderString>();
		for (SeriesProperties seriesProperties : sg.getSeriesProperties()) {
			list.add(seriesProperties.getHolderString());
		}
		return list;
	}

	private List<LogData> convertLogDataList(
			SortedMap<Timestamp, DoubleList> map) {
		ArrayList<LogData> list = new ArrayList<LogData>(map.size());
		for (Map.Entry<Timestamp, DoubleList> entry : map.entrySet()) {
			list.add(new LogData(entry.getKey(), entry.getValue()));
		}
		return list;
	}

	private void serverError(RemoteException e) {
		String errorMessage = "データ取り込みにてサーバーでエラーが発生しました。\n";
		logger.error(errorMessage, e);
		JOptionPane.showMessageDialog(
			null,
			errorMessage + printStackTrace(e.getStackTrace()),
			"サーバーエラー",
			JOptionPane.ERROR_MESSAGE);
	}

	private String printStackTrace(StackTraceElement[] stackTrace) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < stackTrace.length; i++) {
			if (0 != i) {
				b.append("\t");
			}
			b.append(stackTrace[i]).append("\n");
		}
		return b.toString();
	}

	public boolean reachedMaximum() {
		return false;
	}

	public boolean reachedMinimum() {
		return false;
	}

	public void shutdown() {
		service.shutdown();
	}

	private class LoggingTask implements Runnable {
		/** 最新のログデータ */
		private Timestamp currentTimestamp;

		public LoggingTask(LogData lastLogData) {
			this.currentTimestamp =
				new Timestamp(lastLogData.getDate().getTime());
		}

		public void run() {
			try {
				Map<Timestamp, DoubleList> map =
					valueListHandler.getUpdateLoggingData(
						getLogName(),
						currentTimestamp,
						getHolderStrings());
				performPropertyChange(map);
				setCurrentTimestamp(map);
			} catch (RemoteException e) {
				serverError(e);
			}
		}

		private void performPropertyChange(Map<Timestamp, DoubleList> map) {
			for (Map.Entry<Timestamp, DoubleList> entry : map.entrySet()) {
				changeSupport.firePropertyChange(
					GraphModel.VALUE_CHANGE,
					null,
					new LogData(entry.getKey(), entry.getValue()));
			}
		}

		private void setCurrentTimestamp(Map<Timestamp, DoubleList> map) {
			for (Timestamp timestamp : map.keySet()) {
				if (currentTimestamp.after(timestamp)) {
					currentTimestamp = timestamp;
				}
			}
		}
	}
}
