/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.invoke;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.applet.ngraph.editor.SeriesPropertyData;
import org.F11.scada.server.dao.PointTableDao;
import org.F11.scada.server.dao.PointTableDto;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.logging.Column;
import org.F11.scada.server.logging.LoggingRuleSet;
import org.F11.scada.server.logging.Task;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;
import org.xml.sax.SAXException;

public class UnitSerachService implements InvokeHandler {
	private Logger logger = Logger.getLogger(UnitSerachService.class);
	private final PointTableDao dao;

	public UnitSerachService() {
		S2Container container = S2ContainerUtil.getS2Container();
		this.dao = (PointTableDao) container.getComponent(PointTableDao.class);
	}

	public Object invoke(Object[] args) {
		SeriesPropertyData unit = (SeriesPropertyData) args[0];
		String logName = (String) args[1];
		PointTableDto dto = getPointTableDto(unit);
		List<HolderString> l = Collections.emptyList();
		try {
			l = getHolders(logName);
			String holderSql = getHolderSql(l);
			List<PointTableDto> pointTable = dao.getPointTable(dto, holderSql);
			return pointTable;
		} catch (Exception e) {
			logger.error("ポイントテーブル検索時にエラーが発生しました。", e);
			throw new RuntimeException(e);
		}
	}

	private String getHolderSql(List<HolderString> l) {
		StringBuilder b = new StringBuilder(l.size() * 30);
		b.append("(");
		for (Iterator<HolderString> i = l.iterator(); i.hasNext();) {
			HolderString hs = i.next();
			if (i.hasNext()) {
				b
					.append("(i.provider='")
					.append(hs.getProvider())
					.append("' AND i.holder='")
					.append(hs.getHolder())
					.append("') OR ");
			} else {
				b
					.append("(i.provider='")
					.append(hs.getProvider())
					.append("' AND i.holder='")
					.append(hs.getHolder())
					.append("')");
			}
		}
		b.append(")");
		return b.toString();
	}

	private List<HolderString> getHolders(String logName) throws IOException,
			SAXException {
		Digester digester = new Digester();
		digester.addRuleSet(new LoggingRuleSet());
		HashMap<String, Task> map = new HashMap<String, Task>();
		digester.push(map);
		BufferedReader xml = null;
		try {
			xml =
				new BufferedReader(
					new InputStreamReader(
						UnitSerachService.class
							.getResourceAsStream("/resources/Logging.xml"),
						"Windows-31J"));
			digester.parse(xml);
		} finally {
			if (null != xml) {
				xml.close();
			}
		}
		return getHolders(logName, map);
	}

	private List<HolderString> getHolders(String logName,
			HashMap<String, Task> map) {
		if (map.isEmpty()) {
			return Collections.emptyList();
		} else {
			Task task = map.get(logName);
			String tables = task.getTables();
			if (null != tables) {
				return getHolders(map, tables);
			} else {
				return getHolders(task);
			}
		}
	}

	private List<HolderString> getHolders(HashMap<String, Task> map,
			String tables) {
		String[] tableNames = tables.split(",");
		ArrayList<HolderString> l = new ArrayList<HolderString>();
		for (String tableName : tableNames) {
			Task task = map.get(tableName.trim());
			l.addAll(getHolders(task));
		}
		return l;
	}

	private List<HolderString> getHolders(Task task) {
		List<Column> columns = task.getColumns();
		ArrayList<HolderString> l = new ArrayList<HolderString>(columns.size());
		for (Column column : columns) {
			l.add(new HolderString(column.getProvider(), column.getHolder()));
		}
		return l;
	}

	private PointTableDto getPointTableDto(SeriesPropertyData unit) {
		PointTableDto dto = new PointTableDto();
		dto.setUnit(getConstraction(unit.getUnit()));
		dto.setName(getConstraction(unit.getName()));
		dto.setUnitMark(getConstraction(unit.getMark()));
		return dto;
	}

	private String getConstraction(String s) {
		return "".equals(s) ? null : '%' + s + '%';
	}
}
