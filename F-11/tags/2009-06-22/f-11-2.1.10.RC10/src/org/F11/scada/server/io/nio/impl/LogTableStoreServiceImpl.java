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

import java.util.List;

import org.F11.scada.server.dao.DatabaseMetaDataUtil;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.io.nio.CreateLogTableDao;
import org.F11.scada.server.io.nio.LogDtoCreator;
import org.F11.scada.server.io.nio.LogTableDao;
import org.F11.scada.server.io.nio.LogTableStoreService;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.Lock;

public class LogTableStoreServiceImpl implements LogTableStoreService {
	private Logger logger = Logger.getLogger(LogTableStoreServiceImpl.class);
	private LogDtoCreator logDtoCreator;
	private LogTableDao logTableDao;
	private CreateLogTableDao createLogTableDao;
	private boolean tableCheck;
	/** LogTableSelectService と共有ロックオブジェクト */
	private final Lock lock;

	public LogTableStoreServiceImpl(Lock lock) {
		this.lock = lock;
	}

	public void setLogDtoCreator(LogDtoCreator logDtoCreator) {
		this.logDtoCreator = logDtoCreator;
	}

	public void setLogTableDao(LogTableDao logTableDao) {
		this.logTableDao = logTableDao;
	}

	public void setCreateLogTableDao(CreateLogTableDao createLogTableDao) {
		this.createLogTableDao = createLogTableDao;
	}

	public void store(String tableName, LoggingDataEvent event) {
		lock.lock();
		try {
			checkTable(tableName);
			List dtos = logDtoCreator.getLogDtoList(event);
			logTableDao.insert(tableName, dtos);
		} finally {
			lock.unlock();
		}
	}

	private void checkTable(String tableName) {
		if (!tableCheck) {
			if (!DatabaseMetaDataUtil.existsTable(tableName)) {
				createLogTableDao.createTable(tableName);
				logger.info("table created : " + tableName);
			}
			tableCheck = true;
		}
	}
}
