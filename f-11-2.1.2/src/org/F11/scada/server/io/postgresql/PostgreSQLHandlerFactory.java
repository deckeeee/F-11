package org.F11.scada.server.io.postgresql;

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

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.HandlerFactory;
import org.F11.scada.server.io.SelectHandler;
import org.F11.scada.server.io.SelectiveAllDataValueListHandlerElement;
import org.F11.scada.server.io.SelectiveValueListHandlerElement;
import org.F11.scada.server.io.ValueListHandlerElement;

/**
 * PostgreSQL のハンドラーファクトリークラスを生成します。
 */
public class PostgreSQLHandlerFactory extends HandlerFactory {
	public LoggingDataListener createStoreHandler(String device) throws SQLException {
		return new PostgreSQLStoreHandler(device);
	}

	/*
	 * @see org.F11.scada.server.io.ValueListHandlerFactory#createValueListHandler(String)
	 */
	public ValueListHandlerElement createValueListHandler(
			String device,
			List dataHolders)
			throws MalformedURLException, RemoteException, SQLException {
		return new PostgreSQLValueListHandler(device, dataHolders, createSelectHandlerForReport());
	}

	private SelectHandler createSelectHandlerForReport() {
		return new PostgreSQLSelectHandlerForReport();
	}

	public SelectiveValueListHandlerElement createSelectviveHandler(String device) {
		return new PostgreSQLSelectiveValueListHandler(device, createSelectHandler());
	}

	public SelectiveAllDataValueListHandlerElement createAllDataSelectviveHandler(String device) {
		return new PostgreSQLSelectiveAllDataValueListHandler(device, createSelectHandler());
	}

	private PostgreSQLSelectHandler createSelectHandler() {
		return new PostgreSQLSelectHandler();
	}
}
