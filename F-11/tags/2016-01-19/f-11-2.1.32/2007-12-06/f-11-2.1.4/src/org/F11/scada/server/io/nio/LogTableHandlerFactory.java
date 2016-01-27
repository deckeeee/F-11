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

package org.F11.scada.server.io.nio;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.HandlerFactory;
import org.F11.scada.server.io.SelectiveAllDataValueListHandlerElement;
import org.F11.scada.server.io.SelectiveValueListHandlerElement;
import org.F11.scada.server.io.ValueListHandlerElement;
import org.F11.scada.server.io.postgresql.PostgreSQLSelectiveAllDataValueListHandler;
import org.F11.scada.server.io.postgresql.PostgreSQLSelectiveValueListHandler;
import org.F11.scada.server.io.postgresql.PostgreSQLValueListHandler;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.seasar.framework.container.S2Container;

public class LogTableHandlerFactory extends HandlerFactory {
	private S2Container container = S2ContainerUtil.getS2Container();

	public LoggingDataListener createStoreHandler(String device)
			throws SQLException {
		LogTableStoreHandler handler =
			(LogTableStoreHandler) container.getComponent(LogTableStoreHandler.class);
		handler.setDeviceName(device);
		handler.start();
		return handler;
	}

	public ValueListHandlerElement createValueListHandler(String device,
			List dataHolders) throws MalformedURLException, RemoteException,
			SQLException {
		return new PostgreSQLValueListHandler(device, dataHolders, getSelectHandler());
	}

	private LogTableSelectHandler getSelectHandler() {
		return (LogTableSelectHandler) container.getComponent(LogTableSelectHandler.class);
	}

	public SelectiveValueListHandlerElement createSelectviveHandler(String device) {
		return new PostgreSQLSelectiveValueListHandler(device, getSelectHandler());
	}

	public SelectiveAllDataValueListHandlerElement createAllDataSelectviveHandler(String device) {
		return new PostgreSQLSelectiveAllDataValueListHandler(device, getSelectHandler());
	}
}
