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

package org.F11.scada.server.io;

import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.server.io.nio.LogTableSelectService;

public class LogTableAutoPrintDataService extends AbstractAutoPrintDataService {
//	private final Logger logger = Logger.getLogger(LogTableAutoPrintDataService.class);
	private LogTableSelectService service;

	public LogTableAutoPrintDataService() {
		super();
	}

	public void setService(LogTableSelectService service) {
		this.service = service;
	}

	public List getLoggingDataList(String tableName, Timestamp start,
			Timestamp end, List dataHolders) {
		return service.select(tableName, dataHolders, start, end);
	}
}
