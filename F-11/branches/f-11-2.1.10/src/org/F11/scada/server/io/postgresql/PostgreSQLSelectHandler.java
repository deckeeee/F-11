/*
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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.DataSelector;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.SQLUtility;
import org.F11.scada.server.io.SelectHandler;
import org.apache.commons.collections.primitives.DoubleCollections;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

public class PostgreSQLSelectHandler implements SelectHandler {
	/** ロギングオブジェクト */
	private static Logger logger;
	/** SQLユーティリティー */
	private SQLUtility sqlUtility;
	private final ItemUtil itemUtil;
	private final DataSelector selector;

	/**
	 * コンストラクタ
	 */
	public PostgreSQLSelectHandler() {
		super();
		logger = Logger.getLogger(getClass().getName());
		S2Container container = S2ContainerUtil.getS2Container();
		sqlUtility = (SQLUtility) container.getComponent(SQLUtility.class);
		itemUtil = (ItemUtil) container.getComponent("itemutil");
		selector = new PostgreSQLDataSelector();
	}

	void setPostgreSQLUtility(SQLUtility utility) {
		this.sqlUtility = utility;
	}

	/**
	 * 指定された列の LoggingRowDataのリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @exception SQLException SQLエラーが発生した場合
	 */
	public List select(String name, List dataHolders) throws SQLException {
		return select(
			name,
			dataHolders,
			PostgreSQLValueListHandler.MAX_MAP_SIZE);
	}

	/**
	 * 指定された列の LoggingRowDataのリストを返します。
	 * 
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @exception SQLException SQLエラーが発生した場合
	 */
	public List select(String name, List dataHolders, int limit)
			throws SQLException {
		String sql = sqlUtility.getSelectAllString(name, dataHolders, limit);
		logger.debug(sql);

		return getSelectData(name, dataHolders, sql);
	}

	public List select(
			String name,
			List dataHolders,
			int limit,
			List<String> tables) throws SQLException {
		String sql =
			sqlUtility.getSelectAllString(name, dataHolders, limit, tables);
		logger.debug(sql);

		return getSelectData(name, dataHolders, sql);
	}

	public List select(String name, List dataHolders, Timestamp time)
			throws SQLException {

		String sql = sqlUtility.getSelectTimeString(name, dataHolders, time);
		logger.debug(sql);

		return getSelectData(name, dataHolders, sql);
	}

	private List getSelectData(String name, List dataHolders, String sql)
			throws SQLException {
		Map converValueMap = itemUtil.createConvertValueMap(dataHolders);

		return selector.getSelectData(name, dataHolders, sql, converValueMap);
	}

	public LoggingRowData first(String name, List dataHolders)
			throws SQLException {
		String sql = sqlUtility.getFirstData(name, dataHolders);
		logger.debug(sql);

		List l = getSelectData(name, dataHolders, sql);
		return getLoggingRowData(l);
	}

	public LoggingRowData last(String name, List dataHolders)
			throws SQLException {
		String sql = sqlUtility.getLastData(name, dataHolders);
		logger.debug(sql);

		List l = getSelectData(name, dataHolders, sql);
		return getLoggingRowData(l);
	}

	private LoggingRowData getLoggingRowData(List l) {
		if (l.isEmpty()) {
			return new LoggingData(
				new Timestamp(System.currentTimeMillis()),
				DoubleCollections.EMPTY_DOUBLE_LIST);
		} else {
			return (LoggingRowData) l.get(0);
		}
	}

	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit) throws SQLException {
		String sql =
			sqlUtility.getSelectBefore(name, dataHolders, start, limit);
		logger.debug(sql);
		List before = getSelectData(name, dataHolders, sql);

		sql = sqlUtility.getSelectAfter(name, dataHolders, start, limit);
		logger.debug(sql);
		List after = getSelectData(name, dataHolders, sql);

		before.addAll(after);

		return before;
	}

	public List select(
			String name,
			List dataHolders,
			Timestamp time,
			List<String> tables) throws SQLException {
		String sql =
			sqlUtility.getSelectTimeString(name, dataHolders, time, tables);
		logger.debug(sql);

		return getSelectData(name, dataHolders, sql);
	}

	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit,
			List<String> tables) throws SQLException {
		String sql =
			sqlUtility.getSelectBefore(name, dataHolders, start, limit, tables);
		logger.debug(sql);
		List before = getSelectData(name, dataHolders, sql);

		sql =
			sqlUtility.getSelectAfter(name, dataHolders, start, limit, tables);
		logger.debug(sql);
		List after = getSelectData(name, dataHolders, sql);

		before.addAll(after);

		return before;
	}


	public LoggingRowData first(String name, List dataHolders, List<String> tables)
			throws SQLException {
		String sql = sqlUtility.getFirstData(name, dataHolders, tables);
		logger.debug(sql);

		List l = getSelectData(name, dataHolders, sql);
		return getLoggingRowData(l);
	}

	public LoggingRowData last(String name, List dataHolders, List<String> tables)
			throws SQLException {
		String sql = sqlUtility.getLastData(name, dataHolders, tables);
		logger.debug(sql);

		List l = getSelectData(name, dataHolders, sql);
		return getLoggingRowData(l);
	}

}
