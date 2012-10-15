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


public abstract class AbstractDbms implements Dbms {
	public String[] getCreateIndexSql(String tableName) {
		return new String[]{
				"CREATE INDEX " + tableName + "_writedate_idx" + " ON " + tableName + " (writedate)"
				, "CREATE INDEX " + tableName + "_holderid_idx" + " ON " + tableName + " (holderid)"
				, "CREATE INDEX " + tableName + "_revision_idx" + " ON " + tableName + " (revision)"
				};
	}

	public String getDropSql(String tableName) {
		return "DROP TABLE " + tableName;
	}

	public String getInsertSql(String tableName) {
		return "INSERT INTO " + tableName + "(writedate, revision, holderid, value) VALUES(?, ?, ?, ?)";
	}

	public String getRevisionSql(String tableName) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE writedate = ? AND holderid = ? ORDER BY revision DESC";
	}

	public String getSelectSql(String tableName) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0";
	}

	public String getSelectHolderIdSql(String tableName, int limit) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? ORDER BY writedate DESC LIMIT " + limit;
	}

	public String getSelectTimeSql(String tableName) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? AND writedate > ? ORDER BY writedate DESC";
	}

	public String getSelectFirstSql(String tableName) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? ORDER BY writedate LIMIT 1"; 
	}

	public String getSelectLastSql(String tableName) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? ORDER BY writedate DESC LIMIT 1"; 
	}

	public String getSelectAfterSql(String tableName, int limit) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? AND writedate >= ? ORDER BY writedate ASC LIMIT " + limit; 
	}

	public String getSelectBeforeSql(String tableName, int limit) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? AND writedate < ? ORDER BY writedate DESC LIMIT " + limit; 
	}

	public String getSelectBetweenTimeSql(String tableName) {
		return "SELECT id, writedate, revision, holderid, value FROM " + tableName + " WHERE revision = 0 AND holderid = ? AND writedate BETWEEN ? AND ? ORDER BY writedate";
	}
}
