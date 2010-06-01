/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.server.io;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.postgresql.PostgreSQLSelectHandler;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

public class BarGraph2ValueListHandlerManager extends UnicastRemoteObject
		implements
			BarGraph2ValueListHandler {
	private static final long serialVersionUID = 8619543587820599580L;
	/** ロギングAPI */
	private static Logger logger = Logger.getLogger(BarGraph2ValueListHandlerManager.class);

	private final SelectHandler handler;
	/** ハンドラ名とテーブル名のマップです */
	private Map<String, List<String>> handlerMap;

	public BarGraph2ValueListHandlerManager(int recvPort)
			throws RemoteException, MalformedURLException {
		super(recvPort);

		handler = new PostgreSQLSelectHandler();

		logger.info("BarGraph2ValueListHandlerManager:"
				+ WifeUtilities.createRmiBarGraph2DataValueListHandlerManager());
		Naming.rebind(
				WifeUtilities.createRmiBarGraph2DataValueListHandlerManager(),
				this);
		logger.info("BarGraph2ValueListHandlerManager bound in registry");
	}

	public void addValueListHandlerElement(String name, List<String> tables) {
		if (handlerMap == null) {
			handlerMap = new HashMap<String, List<String>>();
		}
		handlerMap.put(name, tables);
	}

	public Date getFirstDateTime(String name, List<HolderString> dataHolders)
			throws RemoteException, SQLException {
		List<String> tables = handlerMap.get(name);
		LoggingRowData data = null;
		if (tables.isEmpty())
			data = handler.first(name, dataHolders);
		else
			data = handler.first(name, dataHolders, tables);
		return new Date(data.getTimestamp().getTime());
	}

	public Date getLastDateTime(String name, List<HolderString> dataHolders)
			throws RemoteException, SQLException {
		List<String> tables = handlerMap.get(name);
		LoggingRowData data = null;
		if (tables.isEmpty())
			data = handler.last(name, dataHolders);
		else
			data = handler.last(name, dataHolders, tables);
		return new Date(data.getTimestamp().getTime());
	}

	public SortedMap<Timestamp, DoubleList> getLoggingData(String name,
			List<HolderString> dataHolders, Date first, Date last)
			throws RemoteException, SQLException {
		List<String> tables = handlerMap.get(name);
		List<LoggingData> list = null;
		if (tables.isEmpty())
			list = handler.selectPeriod(name, dataHolders, new Timestamp(
					first.getTime()), new Timestamp(last.getTime()));
		else
			list = handler.selectPeriod(name, dataHolders, new Timestamp(
					first.getTime()), new Timestamp(last.getTime()), tables);
		SortedMap<Timestamp, DoubleList> map = new TreeMap<Timestamp, DoubleList>();
		for (LoggingData data : list)
			map.put(data.getTimestamp(), data.getList());
		return map;
	}

}
