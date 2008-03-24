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

package org.F11.scada.tool.logdata.impl;

import java.rmi.RemoteException;
import java.sql.Connection;

import org.F11.scada.server.logging.F11LoggingHandler;
import org.F11.scada.server.logging.LoggingContentHandler;
import org.F11.scada.tool.logdata.DataConditionsForm;
import org.F11.scada.tool.logdata.MakeCsvDataLogic;
import org.F11.scada.tool.logdata.MakeCsvService;
import org.F11.scada.tool.logdata.MakeCsvServiceFactory;
import org.F11.scada.util.RmiUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MakeCsvServiceFactoryImpl implements MakeCsvServiceFactory {
	private final Log log = LogFactory.getLog(MakeCsvServiceFactoryImpl.class);
	private LoggingContentHandler handler;

	public MakeCsvServiceFactoryImpl() {
		handler = (LoggingContentHandler) RmiUtil
				.lookupServer(F11LoggingHandler.class);
	}

	public MakeCsvService getMakeCsvService(
			Connection con,
			DataConditionsForm dataCondForm) {
		try {
			String factoryName = handler.getFactoryName(dataCondForm
					.getTableString());
			if ("org.F11.scada.server.io.nio.LogTableHandlerFactory"
					.equals(factoryName)) {
				return new LogTableCsvDataLogic(con);
			}
		} catch (RemoteException e) {
			log.error("リモートエラーが発生しました。", e);
		}
		return new MakeCsvDataLogic(con, dataCondForm);
	}
}
