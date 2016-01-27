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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.server.remove.RemoveDto;
import org.F11.scada.server.remove.RemoveExecutor;
import org.apache.log4j.Logger;
import org.seasar.extension.jdbc.SelectHandler;
import org.seasar.extension.jdbc.impl.BasicUpdateHandler;
import org.seasar.framework.container.S2Container;

public class RemoveExecutorEmailOther implements RemoveExecutor {
	private static Logger logger = Logger.getLogger(RemoveExecutorEmailOther.class);
	private S2Container container;

	public void setContainer(S2Container container) {
		this.container = container;
	}

	public int execute(RemoveDto dto, Timestamp timestamp) {
		List deleteIds = getDeleteIds(timestamp);
		for (Iterator i = deleteIds.iterator(); i.hasNext();) {
			Map map = (Map) i.next();
			removeAlarmEmailAddresses(map);
		}
		if (deleteIds.isEmpty()) {
			return 0;
		} else {
			BasicUpdateHandler handler = (BasicUpdateHandler) container
					.getComponent("org_F11_scada_server_remove.removeHandler");
			String sql = SQLUtil.replace(dto, handler.getSql());
			handler.setSql(sql);
			logger.info("execute sql = " + SQLUtil.getSql(handler.getSql(), timestamp));
			return handler.execute(new Object[]{timestamp});
		}
	}

	private List getDeleteIds(Timestamp timestamp) {
		SelectHandler handler = (SelectHandler) container
			.getComponent("org_F11_scada_server_remove.selectAlarmMailHandler");
		return (List) handler.execute(new Object[]{timestamp});
	}

	private void removeAlarmEmailAddresses(Map map) {
		BasicUpdateHandler handler = (BasicUpdateHandler) container
				.getComponent("org_F11_scada_server_remove.removeAlarmEmailAddresesHandler");
		Integer id = (Integer) map.get("alarm_email_sent_id");
		logger.info("execute sql = " + SQLUtil.getSql(handler.getSql(), id));
		handler.execute(new Object[]{id});
	}
}
