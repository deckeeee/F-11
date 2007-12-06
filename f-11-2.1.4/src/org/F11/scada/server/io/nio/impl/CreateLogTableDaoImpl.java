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

import org.F11.scada.server.io.nio.CreateLogTableDao;
import org.F11.scada.server.io.nio.dbms.Dbms;
import org.F11.scada.server.io.nio.dbms.DbmsManager;
import org.seasar.extension.jdbc.impl.BasicUpdateHandler;
import org.seasar.framework.container.S2Container;

public class CreateLogTableDaoImpl implements CreateLogTableDao {
	private S2Container container;

	public void setContainer(S2Container container) {
		this.container = container;
	}

	public void createTable(String name) {
		BasicUpdateHandler handler = getUpdateHandler();
		handler.setSql(getCreateSql(name));
		handler.execute(null);
		String[] sqls = getCreateIndexSql(name);
		for (int i = 0; i < sqls.length; i++) {
			handler.setSql(sqls[i]);
			handler.execute(null);
		}
	}

	private BasicUpdateHandler getUpdateHandler() {
		return (BasicUpdateHandler) container.getComponent("basicUpdate");
	}

	private String[] getCreateIndexSql(String name) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getCreateIndexSql(name);
	}

	private String getCreateSql(String name) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getCreateSql(name);
	}

	public void dropTable(String name) {
		BasicUpdateHandler handler = getUpdateHandler();
		handler.setSql(getDropSql(name));
		handler.execute(null);
	}

	private String getDropSql(String name) {
		Dbms dbms = DbmsManager.getDbms();
		return dbms.getDropSql(name);
	}
}
