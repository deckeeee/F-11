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

package org.F11.scada.server.logging;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.ValueListHandlerElement;
import org.F11.scada.server.io.ValueListHandlerManager;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.log4j.Logger;

public class DummyValueListHandlerManager implements ValueListHandlerManager {
	private final Logger logger =
		Logger.getLogger(DummyValueListHandlerManager.class);

	public void addValueListHandlerElement(
			String name,
			ValueListHandlerElement handler) throws RemoteException {
		logger.info("addValueListHandler name=" + name + " handler=" + handler);
		ThreadUtil.printSS();
	}

	public void removeValueListHandlerElement(String name)
			throws RemoteException {
		logger.info("addValueListHandler name=" + name);
		ThreadUtil.printSS();
	}

	public void findRecord(String name, Timestamp key) throws RemoteException {
		logger.info("findRecord name=" + name + " timestamp=" + key);
		ThreadUtil.printSS();
	}

	public Object firstKey(String name) throws RemoteException {
		logger.info("firstKey name=" + name);
		ThreadUtil.printSS();
		return new Timestamp(0);
	}

	public SortedMap getInitialData(String name) throws RemoteException {
		logger.info("getInitialData name=" + name);
		ThreadUtil.printSS();
		return new TreeMap();
	}

	public Map getUpdateLoggingData(String name, Timestamp key)
			throws RemoteException {
		logger.info("getUpdateLoggingData name=" + name + " timestamp=" + key);
		ThreadUtil.printSS();
		return Collections.EMPTY_MAP;
	}

	public boolean hasNext(String name) throws RemoteException {
		logger.info("hasNext name=" + name);
		ThreadUtil.printSS();
		return false;
	}

	public Object lastKey(String name) throws RemoteException {
		logger.info("lastKey name=" + name);
		ThreadUtil.printSS();
		return new Timestamp(0);
	}

	public Object next(String name) throws RemoteException {
		logger.info("next name=" + name);
		ThreadUtil.printSS();
		return new LoggingData(new Timestamp(0), new ArrayDoubleList());
	}

	public ValueListHandlerElement getValueListHandlerElement(String name) {
		ThreadUtil.printSS();
		return new ValueListHandlerElement() {

			public void addLoggingDataListener(LoggingDataListener listener) {
			}

			public void findRecord(Timestamp key) {
			}

			public Object firstKey() {
				return new Timestamp(0);
			}

			public SortedMap getInitialData() {
				return new TreeMap();
			}

			public Map getUpdateLoggingData(Timestamp key) {
				return Collections.EMPTY_MAP;
			}

			public boolean hasNext() {
				return false;
			}

			public Object lastKey() {
				return new Timestamp(0);
			}

			public Object next() {
				return new LoggingData(new Timestamp(0), new ArrayDoubleList());
			}

			public void removeLoggingDataListener(LoggingDataListener listener) {
			}

			public void changeLoggingData(LoggingDataEvent event) {
			}
			
			public List<HolderString> getHolders() {
				return Collections.EMPTY_LIST;
			}
		};
	}
}
