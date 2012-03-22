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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.logging.Logger;

import org.F11.scada.applet.graph.DefaultSelectiveAllDataValueListHandlerFactory;
import org.F11.scada.applet.graph.SelectiveAllDataValueListHandlerFactory;
import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.LogData;
import org.F11.scada.applet.ngraph.SeriesGroup;
import org.F11.scada.applet.ngraph.SeriesProperties;
import org.F11.scada.server.io.SelectiveAllDataValueListHandler;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.DoubleList;

public class DefaultAllGraphModel extends AbstractGraphModel {
	private final Logger logger =
		Logger.getLogger(DefaultAllGraphModel.class.getName());
	/** データハンドラマネージャーです。 */
	private SelectiveAllDataValueListHandler valueListHandler;
	private Date startTimestamp;
	private Date endTimestamp;
	/** DB内の最小日時 */
	private Date firstTimestamp;
	/** DB内の最大日時 */
	private Date lastTimestamp;

	public DefaultAllGraphModel(GraphProperties graphProperties) {
		super(graphProperties);
		SelectiveAllDataValueListHandlerFactory factory =
			new DefaultSelectiveAllDataValueListHandlerFactory();
		valueListHandler = factory.getSelectiveAllDataValueListHandler();
	}

	public void initialize() {
		initialize(getLogData());
		try {
			firstTimestamp =
				valueListHandler.firstTime(getLogName(), getHolderStrings());
			lastTimestamp =
				valueListHandler.lastTime(getLogName(), getHolderStrings());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		logger.info(startTimestamp + "～" + endTimestamp);
	}

	private void initialize(List<LogData> logData) {
		startTimestamp = logData.get(0).getDate();
		endTimestamp = logData.get(logData.size() - 1).getDate();
		logger.info(startTimestamp + "～" + endTimestamp);
		changeSupport.firePropertyChange(INITIALIZE, null, logData);
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
			e.printStackTrace();
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

	public boolean reachedMaximum() {
		List<LogData> list = getLoggingData(endTimestamp);
		logger.info("size=" + list.size());
		initialize(list);
		Date date = list.get(list.size() - 1).getDate();
		return date.before(lastTimestamp);
	}

	public boolean reachedMinimum() {
		List<LogData> list = getLoggingData(startTimestamp);
		logger.info("size=" + list.size());
		initialize(list);
		Date date = list.get(0).getDate();
		return date.after(firstTimestamp);
	}

	private List<LogData> getLoggingData(Date date) {
		List<LogData> list = Collections.emptyList();
		try {
			SortedMap<Timestamp, DoubleList> map =
				valueListHandler.getLoggingData(
					getLogName(),
					getHolderStrings(),
					getTime(date),
					getMaxRecord() / 2);
			list = convertLogDataList(map);
			System.out.println("map size = " + map.size());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return list;
	}

	private Timestamp getTime(Date date) {
		return new Timestamp(date.getTime());
	}

	public void shutdown() {
		// NOP
	}
}
