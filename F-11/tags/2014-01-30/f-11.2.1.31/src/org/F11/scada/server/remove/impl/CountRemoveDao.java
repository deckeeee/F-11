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

package org.F11.scada.server.remove.impl;

import java.sql.Timestamp;
import java.util.Map;

import org.F11.scada.server.remove.RemoveDao;
import org.F11.scada.server.remove.RemoveDto;
import org.F11.scada.server.remove.RemoveExecutor;
import org.F11.scada.server.remove.RemoveExecutorFactory;
import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.impl.BasicSelectHandler;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.exception.SQLRuntimeException;

/**
 * 残レコード件数を指定して、レコードを削除するDaoクラスです。
 *
 * @author maekawa
 *
 */
public class CountRemoveDao implements RemoveDao {
	private static Logger logger = Logger.getLogger(CountRemoveDao.class);
	private S2Container container;
	private RemoveExecutorFactory factory;

	public void setContainer(S2Container container) {
		this.container = container;
	}

	public void setFactory(RemoveExecutorFactory factory) {
		this.factory = factory;
	}

	public int remove(RemoveDto dto) {
		logger.info(SQLUtil.getDatabaseProductName(container));
		if (isRemove(dto)) {
			Timestamp deleteDate = getTimestamp(dto);
			RemoveExecutor executor =
				factory.getRemoveExecutor(dto.getTableName());
			return executor.execute(dto, deleteDate);
		} else {
			return 0;
		}
	}

	private boolean isRemove(RemoveDto dto) {
		try {
			return getCount(dto).intValue() > dto.getRemoveValue();
		} catch (SQLRuntimeException e) {
			logger.warn("件数カウントでエラーが発生。単にまだ、作成されていないテーブルかもしれません。", e);
			return false;
		}
	}

	private Number getCount(RemoveDto dto) {
		BasicSelectHandler handler = (BasicSelectHandler) container
				.getComponent("org_F11_scada_server_remove.selectCountHandler");
		String sql = SQLUtil.replace(dto, handler.getSql());
		handler.setSql(sql);
		Map map = (Map) handler.execute(null);
		return (Number) map.get("RECCOUNT");
	}

	private Timestamp getTimestamp(RemoveDto dto) {
		BasicSelectHandler handler =
			(BasicSelectHandler) container
				.getComponent("org_F11_scada_server_remove.selectTimestampHandler");
		String sql = SQLUtil.replace(dto, handler.getSql());
		handler.setSql(sql);
		Map map =
			(Map) handler.execute(new Object[] { new Integer(dto
				.getRemoveValue() - 1) });
		return (Timestamp) map.get("DELETETIME");
	}
}
