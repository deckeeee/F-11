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

package org.F11.scada.server.autoprint;

import java.rmi.RemoteException;

import org.F11.scada.server.io.AutoPrintDataService;
import org.F11.scada.server.io.AutoPrintDataStore;
import org.F11.scada.server.io.LogTableAutoPrintDataService;
import org.F11.scada.server.logging.F11LoggingHandler;
import org.F11.scada.server.logging.LoggingContentHandler;
import org.F11.scada.util.RmiUtil;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

public class AutoPrintDataServiceFactoryImpl implements
		AutoPrintDataServiceFactory {
	private final Logger logger = Logger
			.getLogger(AutoPrintDataServiceFactoryImpl.class);
	private S2Container container;
	private LoggingContentHandler handler;

	public void setContainer(S2Container container) {
		this.container = container;
	}

	public AutoPrintDataService getAutoPrintDataService(String tableName) {
		handler = (LoggingContentHandler) RmiUtil
				.lookupServer(F11LoggingHandler.class);
		try {
			String factoryName = handler.getFactoryName(tableName);
			logger.info("factory name : " + factoryName);
			if ("org.F11.scada.server.io.nio.LogTableHandlerFactory"
					.equals(factoryName)) {
				return (LogTableAutoPrintDataService) container
						.getComponent(LogTableAutoPrintDataService.class);
			}
		} catch (RemoteException e) {
			logger.error("リモートエラーが発生しました。", e);
		}
		return new AutoPrintDataStore();
	}
}
