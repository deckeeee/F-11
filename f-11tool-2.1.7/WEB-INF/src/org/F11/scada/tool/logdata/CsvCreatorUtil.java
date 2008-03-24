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

package org.F11.scada.tool.logdata;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.server.logging.F11LoggingHandler;
import org.F11.scada.server.logging.LoggingContentHandler;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.tool.io.LoggingDataStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.io.parser.Column;
import org.F11.scada.util.RmiUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CsvCreatorUtil {
	private final Log log = LogFactory.getLog(CsvCreatorUtil.class);
	private static final String HEAD_DATETIME = "日付,時刻";
	private LoggingContentHandler handler;

	public CsvCreatorUtil() {
		handler = (LoggingContentHandler) RmiUtil
				.lookupServer(F11LoggingHandler.class);
	}

	public List getHolderDatas(String loggingName) {
		try {
			handler = (LoggingContentHandler) RmiUtil
					.lookupServer(F11LoggingHandler.class);
			return handler.getHolderStrings(loggingName);
		} catch (RemoteException e) {
			log.error("ホルダ取得に失敗しました。", e);
		}
		return Collections.EMPTY_LIST;
	}

	public List getColumns(List holders) {
		ArrayList columns = new ArrayList(holders.size());
		for (Iterator i = holders.iterator(); i.hasNext();) {
			HolderString hs = (HolderString) i.next();
			columns.add(new Column(hs.getProvider(), hs.getHolder()));
		}
		return columns;
	}

	public List getColumns(String loggingName) {
		return getColumns(getHolderDatas(loggingName));
	}

	public List getHeaderList(DataConditionsForm form, List columns, Map itemMap) {
		List csvLines = new ArrayList();
		if ("name".equals(form.getHeadString())) {
			csvLines.addAll(getCsvHeaderName(columns, itemMap));
		} else if ("bms".equals(form.getHeadString())) {
			csvLines.addAll(getCsvHeaderBms(columns));
		} else if ("goda".equals(form.getHeadString())) {
			csvLines.addAll(getCsvHeaderGoda(columns, itemMap));
		}
		return csvLines;
	}

	private Collection getCsvHeaderName(Collection columns, Map itemMap) {
		Collection head = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append(HEAD_DATETIME);
		for (Iterator it = columns.iterator(); it.hasNext();) {
			Column col = (Column) it.next();
			PointItemConverter item = (PointItemConverter) itemMap.get(col
					.getProviderHolder());
			sb.append(',').append(item.getPointunit());
		}
		head.add(sb.toString());
		sb = new StringBuffer();
		sb.append(HEAD_DATETIME);
		for (Iterator it = columns.iterator(); it.hasNext();) {
			Column col = (Column) it.next();
			PointItemConverter item = (PointItemConverter) itemMap.get(col
					.getProviderHolder());
			sb.append(',').append(item.getPointname());
		}
		head.add(sb.toString());
		sb = new StringBuffer();
		sb.append(HEAD_DATETIME);
		for (Iterator it = columns.iterator(); it.hasNext();) {
			Column col = (Column) it.next();
			PointItemConverter item = (PointItemConverter) itemMap.get(col
					.getProviderHolder());
			sb.append(',').append(item.getPointunit_mark());
		}
		head.add(sb.toString());
		return head;
	}

	private Collection getCsvHeaderBms(Collection columns) {
		Collection head = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append(HEAD_DATETIME);
		for (Iterator it = columns.iterator(); it.hasNext();) {
			Column col = (Column) it.next();
			sb.append(',').append(col.getHolder());
		}
		head.add(sb.toString());
		return head;
	}

	private Collection getCsvHeaderGoda(List columns, Map itemMap) {
		Collection head = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append("");
		for (Iterator it = columns.iterator(); it.hasNext();) {
			Column col = (Column) it.next();
			sb.append(',').append(col.getHolder());
		}
		head.add(sb.toString());
		sb = new StringBuffer();
		sb.append("");
		for (Iterator it = columns.iterator(); it.hasNext();) {
			Column col = (Column) it.next();
			PointItemConverter item = (PointItemConverter) itemMap.get(col
					.getProviderHolder());
			sb.append(',').append(item.getPointname());
		}
		head.add(sb.toString());
		return head;
	}

	public Timestamp getEndTime(DataConditionsForm form) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 999);
		cal.set(form.getEtYear(), form.getEtMonth() - 1, form.getEtDay(), form
				.getEtHour(), form.getEtMinute(), form.getEtSecond());
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		log.info("end   : " + timestamp);
		return timestamp;
	}

	public Timestamp getStartTime(DataConditionsForm form) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(form.getStYear(), form.getStMonth() - 1, form.getStDay(), form
				.getStHour(), form.getStMinute(), form.getStSecond());
		Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
		log.info("start : " + timestamp);
		return timestamp;
	}

	public List getHeaderData(DataConditionsForm form, Connection con) {
		try {
			String loggingName = form.getTableString();
			List holderDatas = getHolderDatas(loggingName);
			List columns = getColumns(holderDatas);
			LoggingDataStore store = new LoggingDataStore();
			StrategyUtility util = new StrategyUtility(con);
			Map itemMap = store.getLoggingItemMap(util, columns);
			return getHeaderList(form, columns, itemMap);
		} catch (Exception e) {
			log.error("sql exeute error : ", e);
			return Collections.EMPTY_LIST;
		}
	}
}
