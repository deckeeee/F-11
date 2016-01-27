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
import java.util.Collections;
import java.util.List;

import org.F11.scada.server.io.nio.LogTableDao;
import org.F11.scada.server.io.nio.dbms.Dbms;
import org.F11.scada.server.io.nio.dbms.DbmsManager;
import org.F11.scada.server.io.nio.dto.LogDto;
import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.impl.BasicBatchHandler;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.exception.SQLRuntimeException;

public class LogTableDaoImpl implements LogTableDao {
	private S2Container container;
	private final Logger logger = Logger.getLogger(LogTableDaoImpl.class);

	public void setContainer(S2Container container) {
		this.container = container;
	}

	public int insert(String tableName, List dtos) {
		BasicBatchHandler handler =
			(BasicBatchHandler) container.getComponent("basicBatchUpdate");
		handler.setSql(getInsertSql(tableName));
		return handler.execute(dtos);
	}

	private String getInsertSql(String name) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getInsertSql(name);
	}

	public int getRevision(String tableName, Timestamp time, String holderId) {
		BasicSelectHandler handler =
			(BasicSelectHandler) container.getComponent("selectBean");
		handler.setSql(getRevisionSql(tableName));
		LogDto dto = (LogDto) handler.execute(new Object[]{time, holderId});
		return getRevision(dto);
	}

	private int getRevision(LogDto dto) {
		return null == dto ? 0 : dto.getRevision() + 1;
	}

	private String getRevisionSql(String tableName) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getRevisionSql(tableName);
	}

	public List select(String tableName) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectSql(tableName));
			return (List) handler.execute(null);
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private void logInfo(SQLRuntimeException e) {
		if (logger.isDebugEnabled()) {
			logger.warn("SQL実行時エラーです。空のリストを返します。 : ", e);
		}
	}

	private BasicSelectHandler getSelectHandler() {
		return (BasicSelectHandler) container.getComponent("selectBeanList");
	}

	private String getSelectSql(String tableName) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectSql(tableName);
	}

	public List select(String tableName, String holderId, int limit) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectHolderIdSql(tableName, limit));
			return (List) handler.execute(new Object[]{holderId});
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private String getSelectHolderIdSql(String tableName, int limit) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectHolderIdSql(tableName, limit);
	}

	public List select(String tableName, String holderId, Timestamp time) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectTimeSql(tableName));
			return (List) handler.execute(new Object[]{holderId, time});
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private String getSelectTimeSql(String tableName) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectTimeSql(tableName);
	}

	public List selectFirst(String tableName, String holderId) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectFirstSql(tableName));
			return (List) handler.execute(new Object[]{holderId});
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private String getSelectFirstSql(String tableName) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectFirstSql(tableName);
	}

	public List selectLast(String tableName, String holderId) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectLastSql(tableName));
			return (List) handler.execute(new Object[]{holderId});
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private String getSelectLastSql(String tableName) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectLastSql(tableName);
	}

	public List selectAfter(String tableName, String holderId, Timestamp start, int limit) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectAfterSql(tableName, limit));
			return (List) handler.execute(new Object[]{holderId, start});
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private String getSelectAfterSql(String tableName, int limit) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectAfterSql(tableName, limit);
	}

	public List selectBefore(String tableName, String holderId, Timestamp start, int limit) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectBeforeSql(tableName, limit));
			return (List) handler.execute(new Object[]{holderId, start});
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private String getSelectBeforeSql(String tableName, int limit) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectBeforeSql(tableName, limit);
	}

	public List select(String tableName, String holderId, Timestamp startTime, Timestamp endTime) {
		try {
			BasicSelectHandler handler = getSelectHandler();
			handler.setSql(getSelectBetweenTimeSql(tableName));
			return (List) handler.execute(new Object[]{holderId, startTime, endTime});
		} catch (SQLRuntimeException e) {
			logInfo(e);
			return Collections.EMPTY_LIST;
		}
	}

	private String getSelectBetweenTimeSql(String tableName) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getSelectBetweenTimeSql(tableName);
	}
}
