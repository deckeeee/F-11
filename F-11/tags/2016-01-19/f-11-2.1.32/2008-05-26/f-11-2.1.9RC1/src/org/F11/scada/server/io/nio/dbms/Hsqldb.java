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

package org.F11.scada.server.io.nio.dbms;


public class Hsqldb extends AbstractDbms {
	public String getCreateSql(String tableName) {
		return "CREATE TABLE " + tableName + "(id IDENTITY, writedate DATETIME NOT NULL, revision INTEGER NOT NULL, holderid VARCHAR(255) NOT NULL, value double NOT NULL, PRIMARY KEY (id))";
	}

	public String getSelectHolderIdSql(String tableName, int limit) {
		return "SELECT TOP " + limit + " id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? ORDER BY writedate DESC";
	}

	public String getSelectFirstSql(String tableName) {
		return "SELECT TOP 1 id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? ORDER BY writedate"; 
	}

	public String getSelectLastSql(String tableName) {
		return "SELECT TOP 1 id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? ORDER BY writedate DESC"; 
	}

	public String getSelectAfterSql(String tableName, int limit) {
		return "SELECT TOP " + limit + " id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? AND writedate >= ? ORDER BY writedate ASC"; 
	}

	public String getSelectBeforeSql(String tableName, int limit) {
		return "SELECT TOP " + limit + " id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? AND writedate < ? ORDER BY writedate DESC"; 
	}
}
