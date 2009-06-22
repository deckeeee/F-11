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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.data.LoggingRowData;
import org.F11.scada.server.io.SelectHandler;
import org.F11.scada.server.io.postgresql.PostgreSQLValueListHandler;

public class LogTableSelectHandler implements SelectHandler {
	private LogTableSelectService service;

	public void setService(LogTableSelectService service) {
		this.service = service;
	}

	public List select(String name, List dataHolders) {
		return select(
			name,
			dataHolders,
			PostgreSQLValueListHandler.MAX_MAP_SIZE);
	}

	public List select(String name, List dataHolders, int limit) {
		return service.select(name, dataHolders, limit);
	}

	public List select(String name, List dataHolders, Timestamp time) {
		return service.select(name, dataHolders, time);
	}

	public LoggingRowData first(String name, List dataHolders) {
		return service.selectFirst(name, dataHolders);
	}

	public LoggingRowData last(String name, List dataHolders) {
		return service.selectLast(name, dataHolders);
	}

	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit) {
		return service.selectBeforeAfter(name, dataHolders, start, limit);
	}

	public List<LoggingRowData> select(
			String name,
			List dataHolders,
			int limit,
			List<String> table) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public List select(
			String name,
			List dataHolders,
			Timestamp time,
			List<String> tables) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit,
			List<String> tables) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	public LoggingRowData first(
			String name,
			List dataHolders,
			List<String> tables) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	public LoggingRowData last(
			String name,
			List dataHolders,
			List<String> tables) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public List selectPeriod(
			String name,
			List dataHolders,
			Timestamp start,
			Timestamp end) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public List selectPeriod(
			String name,
			List dataHolders,
			Timestamp start,
			Timestamp end,
			List<String> tables) throws SQLException {
		throw new UnsupportedOperationException();
	}
}
