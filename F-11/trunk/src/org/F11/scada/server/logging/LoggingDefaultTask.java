/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/logging/LoggingTask.java,v 1.13.4.4 2006/05/26 05:51:07 frdm Exp $
 * $Revision: 1.13.4.4 $
 * $Date: 2006/05/26 05:51:07 $
 * 
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

package org.F11.scada.server.logging;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.io.ValueListHandlerManager;
import org.apache.log4j.Logger;

/**
 * ロギング用のタスククラスです。 データロギング処理する際に、ミリ秒と秒をゼロにします。
 */
public class LoggingDefaultTask extends LoggingTask {
	/** ロギングAPI */
	private final Logger logger = Logger.getLogger(LoggingDefaultTask.class);

	public LoggingDefaultTask(
			String name,
			List dataHolders,
			String factoryName,
			ValueListHandlerManager handlerManager,
			String schedule,
			List<String> tables) throws SQLException, MalformedURLException,
			RemoteException {
		super(name, dataHolders, factoryName, handlerManager, schedule, tables);
		logger.info(LoggingDefaultTask.class.getName() + "開始");
	}

	/**
	 * スケジュール開始の処理。
	 */
	@Override
	public void run() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		Timestamp today = new Timestamp(cal.getTimeInMillis());
		// dataHoldersより文字列→データホルダーしイベント発火する。
		LoggingDataEvent dataEvent =
			new LoggingDataEvent(this, today, dataHolders);
		fireChangeLoggingData(dataEvent);
	}
}
