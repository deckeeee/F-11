/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/postgresql/Attic/PostgreSQLSelectiveValueListHandler.java,v 1.1.2.2 2006/02/09 01:09:18 frdm Exp $
 * $Revision: 1.1.2.2 $
 * $Date: 2006/02/09 01:09:18 $
 * 
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

package org.F11.scada.server.io.postgresql;

import static org.F11.scada.cat.util.CollectionUtil.$;
import static org.F11.scada.cat.util.CollectionUtil.map;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.SelectHandler;
import org.F11.scada.server.io.SelectiveValueListHandlerElement;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * ロギングデータのハンドラクラスです。 定義されたロギングデータをデータストレージより読みとります。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLSelectiveValueListHandler implements
		SelectiveValueListHandlerElement {

	private final Logger logger =
		Logger.getLogger(PostgreSQLSelectiveValueListHandler.class);
	private final String name;
	private final SelectHandler handler;
	private final Map<String, List<String>> tableMap;

	public PostgreSQLSelectiveValueListHandler(
			String name,
			SelectHandler handler,
			List<String> tables) {
		this.name = name;
		this.handler = handler;
		tableMap = getTableMap(name, tables);
	}

	private Map<String, List<String>> getTableMap(String hname, List<String> tables) {
		if (tables.isEmpty()) {
			return Collections.emptyMap();
		} else {
			return map($(name, tables));
		}
	}

	public SortedMap<Timestamp, DoubleList> getInitialData(List holderStrings) {
		TreeMap<Timestamp, DoubleList> map =
			new TreeMap<Timestamp, DoubleList>();
		try {
			List list = null;
			if (tableMap.containsKey(name)) {
				list =
					handler.select(
						name,
						holderStrings,
						PostgreSQLValueListHandler.MAX_MAP_SIZE,
						tableMap.get(name));
			} else {
				list = handler.select(name, holderStrings);
			}
			for (Iterator it = list.iterator(); it.hasNext();) {
				LoggingRowData data = (LoggingRowData) it.next();
				map.put(data.getTimestamp(), data.getList());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return map;
	}

	public SortedMap<Timestamp, DoubleList> getInitialData(
			List holderStrings,
			int limit) {
		TreeMap<Timestamp, DoubleList> map =
			new TreeMap<Timestamp, DoubleList>();
		List list = null;
		try {
			if (tableMap.containsKey(name)) {
				list =
					handler.select(name, holderStrings, limit, tableMap
						.get(name));
			} else {
				list = handler.select(name, holderStrings, limit);
			}
			for (Iterator it = list.iterator(); it.hasNext();) {
				LoggingRowData data = (LoggingRowData) it.next();
				map.put(data.getTimestamp(), data.getList());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return map;
	}

	public Map<Timestamp, DoubleList> getUpdateLoggingData(
			Timestamp key,
			List holderStrings) {
		Map<Timestamp, DoubleList> map = new HashMap<Timestamp, DoubleList>();
		try {
			List list = null;
			if (tableMap.containsKey(name)) {
				list =
					handler
						.select(name, holderStrings, key, tableMap.get(name));
			} else {
				list = handler.select(name, holderStrings, key);
			}
			for (Iterator it = list.iterator(); it.hasNext();) {
				LoggingRowData data = (LoggingRowData) it.next();
				map.put(data.getTimestamp(), data.getList());
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return map;
	}
}
