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

package org.F11.scada.server.io.nio.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.nio.LogTableDao;
import org.F11.scada.server.io.nio.LogTableSelectService;
import org.F11.scada.server.io.nio.dto.LogDto;
import org.F11.scada.server.logging.column.ColumnManager;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleCollections;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.Lock;

public class LogTableSelectServiceImpl implements LogTableSelectService {
	private static final int VALUE_LENGTH = 5;
	private final Logger logger = Logger.getLogger(LogTableSelectServiceImpl.class);
	private LogTableDao logTableDao;
	private ItemUtil itemUtil;
	/** LogTableSelectService と共有ロックオブジェクト */
	private final Lock lock;
	private final ColumnManager manager = new ColumnManager();


	public LogTableSelectServiceImpl(Lock lock) {
		this.lock = lock;
	}

	public void setLogTableDao(LogTableDao logTableDao) {
		this.logTableDao = logTableDao;
		logger.info("setLogTableDao : " + logTableDao.getClass().getName());
	}

	public void setItemUtil(ItemUtil itemUtil) {
		this.itemUtil = itemUtil;
		logger.info("setItemUtil : " + itemUtil.getClass().getName());
	}

	public List select(String tableName, List dataHolders, int limit) {
		lock.lock();
		try {
			Map tempMap = new LinkedHashMap();
			Map convertValueMap = itemUtil.createConvertValueMap(dataHolders);
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				List logDtos = logTableDao.select(tableName, getHolderId(hs), limit);
				convertValue(getConvertValue(convertValueMap, hs), logDtos);
				setTempMap(tableName, tempMap, logDtos);
			}
			return getLoggingDatas(tempMap);
		} finally {
			lock.unlock();
		}
	}

	private void convertValue(ConvertValue convertValue, List logDtos) {
		for (Iterator i = logDtos.iterator(); i.hasNext();) {
			LogDto dto = (LogDto) i.next();
			dto.setValue(convertValue.convertDoubleValue(dto.getValue()));
		}
	}

	private ConvertValue getConvertValue(Map convertValueMap, HolderString hs) {
		return (ConvertValue) convertValueMap.get(hs);
	}

	private void setTempMap(String tableName, Map tempMap, List logDtos) {
		int columnSize = manager.getColumnSize(tableName);
		for (Iterator i = logDtos.iterator(); i.hasNext();) {
			LogDto dto = (LogDto) i.next();
			if (tempMap.containsKey(dto.getWritedate())) {
				ArrayDoubleList datas = (ArrayDoubleList) tempMap.get(dto.getWritedate());
				datas.set(getColumn(tableName, dto), dto.getValue());
			} else {
				ArrayDoubleList datas = getDoubleList(columnSize);
				datas.set(getColumn(tableName, dto), dto.getValue());
				tempMap.put(dto.getWritedate(), datas);
			}
		}
	}

	private ArrayDoubleList getDoubleList(int columnSize) {
		ArrayDoubleList datas = new ArrayDoubleList(columnSize);
		for (int i = 0; i < columnSize; i++) {
			datas.add(0D);
		}
		return datas;
	}

	private int getColumn(String tableName, LogDto dto) {
		return manager.getColumnIndex(tableName, dto.getHolderid());
	}

	private List getLoggingDatas(Map tempMap) {
		ArrayList loggingDatas = new ArrayList(tempMap.size());
		for (Iterator i = tempMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			loggingDatas.add(getLoggingData(entry));
		}
		return loggingDatas;
	}

	private Object getLoggingData(Entry entry) {
		Timestamp timestamp = (Timestamp) entry.getKey();
		DoubleList datas = (DoubleList) entry.getValue();
		return new LoggingData(timestamp, datas);
	}

	public List select(String tableName, List dataHolders, Timestamp time) {
		lock.lock();
		try {
			Map tempMap = new LinkedHashMap();
			Map convertValueMap = itemUtil.createConvertValueMap(dataHolders);
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				List logDtos = logTableDao.select(tableName, getHolderId(hs), time);
				convertValue(getConvertValue(convertValueMap, hs), logDtos);
				setTempMap(tableName, tempMap, logDtos);
			}
			return getLoggingDatas(tempMap);
		} finally {
			lock.unlock();
		}
	}

	private String getHolderId(HolderString hs) {
		return hs.getProvider() + "_" + hs.getHolder();
	}

	public LoggingRowData selectFirst(String tableName, List dataHolders) {
		if (dataHolders.isEmpty()) {
			return createEmptyLoggingData();
		} else {
			HolderString hs = (HolderString) dataHolders.get(0);
			lock.lock();
			try {
				List dtos = logTableDao.selectFirst(tableName, getHolderId(hs));
				return getLoggingRowData(dtos);
			} finally {
				lock.unlock();
			}
		}
	}

	private LoggingRowData createEmptyLoggingData() {
		return new LoggingData(new Timestamp(System.currentTimeMillis()),
				DoubleCollections.EMPTY_DOUBLE_LIST);
	}

	public LoggingRowData selectLast(String tableName, List dataHolders) {
		if (dataHolders.isEmpty()) {
			return createEmptyLoggingData();
		} else {
			HolderString hs = (HolderString) dataHolders.get(0);
			lock.lock();
			try {
				List dtos = logTableDao.selectLast(tableName, getHolderId(hs));
				return getLoggingRowData(dtos);
			} finally {
				lock.unlock();
			}
		}
	}

	private LoggingRowData getLoggingRowData(List dtos) {
		if (dtos.isEmpty()) {
			return createEmptyLoggingData();
		} else {
			LogDto dto = (LogDto) dtos.get(0);
			return new LoggingData(dto.getWritedate(),
					DoubleCollections.EMPTY_DOUBLE_LIST);
		}
	}

	public List selectBeforeAfter(String tableName, List dataHolders, Timestamp start, int limit) {
		lock.lock();
		try {
			Map tempMap = new LinkedHashMap();
			Map convertValueMap = itemUtil.createConvertValueMap(dataHolders);
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				List beforeLogDtos = logTableDao.selectBefore(tableName, getHolderId(hs), start, limit);
				convertValue(getConvertValue(convertValueMap, hs), beforeLogDtos);
				setTempMap(tableName, tempMap, beforeLogDtos);
				List afterLogDtos = logTableDao.selectAfter(tableName, getHolderId(hs), start, limit);
				convertValue(getConvertValue(convertValueMap, hs), afterLogDtos);
				setTempMap(tableName, tempMap, afterLogDtos);
			}
			return getLoggingDatas(tempMap);
		} finally {
			lock.unlock();
		}
	}

	public List select(String tableName, List dataHolders, Timestamp startTime, Timestamp endTime) {
		Map tempMap = new LinkedHashMap();
		Map convertValueMap = itemUtil.createConvertValueMap(dataHolders);
		for (Iterator i = dataHolders.iterator(); i.hasNext();) {
			HolderString hs = (HolderString) i.next();
			List logDtos = logTableDao.select(tableName, getHolderId(hs), startTime, endTime);
			setCsvTempMap(tableName, tempMap, logDtos, convertValueMap, hs);
		}
		return getLoggingCsvDatas(tempMap);
	}

	private void setCsvTempMap(String tableName, Map tempMap, List logDtos, Map convertValueMap, HolderString hs) {
		int columnSize = manager.getColumnSize(tableName);
		for (Iterator i = logDtos.iterator(); i.hasNext();) {
			LogDto dto = (LogDto) i.next();
			if (tempMap.containsKey(dto.getWritedate())) {
				ArrayList datas = (ArrayList) tempMap.get(dto.getWritedate());
				datas.set(getColumn(tableName, dto), convertStringValue(convertValueMap, hs, dto));
			} else {
				ArrayList datas = getArrayList(columnSize);
				datas.set(getColumn(tableName, dto), convertStringValue(convertValueMap, hs, dto));
				tempMap.put(dto.getWritedate(), datas);
			}
		}
	}

	private String convertStringValue(Map convertValueMap, HolderString hs, LogDto dto) {
		ConvertValue convertValue = (ConvertValue) convertValueMap.get(hs);
		return convertValue.convertStringValue(dto.getValue());
	}

	private ArrayList getArrayList(int columnSize) {
		ArrayList list = new ArrayList(columnSize);
		for (int i = 0; i < columnSize; i++) {
			list.add("");
		}
		return list;
	}

	private List getLoggingCsvDatas(Map tempMap) {
		ArrayList loggingDatas = new ArrayList(tempMap.size());
		for (Iterator i = tempMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			loggingDatas.add(getCsvString(entry));
		}
		return loggingDatas;
	}

	private String getCsvString(Entry entry) {
		Timestamp timestamp = (Timestamp) entry.getKey();
		List datas = (List) entry.getValue();
		StringBuffer b = new StringBuffer(datas.size() * VALUE_LENGTH);
		DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss,");
		b.append(fmt.format(timestamp));
		for (Iterator i = datas.iterator(); i.hasNext();) {
			String valueString = (String) i.next();
			b.append(valueString);
			if (i.hasNext()) {
				b.append(",");
			}
		}
		return b.toString();
	}
}
