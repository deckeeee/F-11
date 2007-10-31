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

import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.DataSelector;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.SelectHandler;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

public class PostgreSQLSelectHandlerForReport implements SelectHandler {
	/** ロギングオブジェクト */
	private static Logger logger;
	/** SQLユーティリティー */
	private final PostgreSQLUtility utility;
	private final ItemUtil util;
	private final DataSelector selector;

	/**
	 * コンストラクタ
	 */
	public PostgreSQLSelectHandlerForReport() {
		super();
		logger = Logger.getLogger(getClass().getName());
	    S2Container container = S2ContainerUtil.getS2Container();
		this.utility = (PostgreSQLUtility) container.getComponent(PostgreSQLUtility.class);
		util = (ItemUtil) container.getComponent("itemutil");
		selector = new PostgreSQLDataSelector();
	}

	/**
	 * 指定された列の LoggingRowDataのリストを返します。
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @exception SQLException SQLエラーが発生した場合
	 */
	public List select(String name, List dataHolders) throws SQLException {
		return select(name, dataHolders, PostgreSQLValueListHandler.MAX_MAP_SIZE);
	}
	
	/**
	 * 指定された列の LoggingRowDataのリストを返します。
	 * @param name データソース名
	 * @param dataHolders データホルダのリスト(列の情報)
	 * @return 指定された列の LoggingRowDataのリストを返します。
	 * @exception SQLException SQLエラーが発生した場合
	 */
	public List select(String name, List dataHolders, int limit) throws SQLException {
		String sql = utility.getSelectAllString(name, dataHolders, limit);
		logger.debug(sql);
		
		return getSelectData(name, dataHolders, sql);
	}
	
    public List select(String name, List dataHolders, Timestamp time)
            throws SQLException {
        throw new UnsupportedOperationException();
    }

    private List getSelectData(String name, List dataHolders, String sql) throws SQLException {
		Map converValueMap = util.createConvertValueMap(dataHolders, name);
		
		return selector.getSelectData(name, dataHolders, sql, converValueMap);
    }
    
    public LoggingRowData first(String name, List dataHolders) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    public LoggingRowData last(String name, List dataHolders) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    public List selectBeforeAfter(String name, List dataHolders, Timestamp start, int limit) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
