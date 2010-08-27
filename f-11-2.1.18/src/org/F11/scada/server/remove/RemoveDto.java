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

package org.F11.scada.server.remove;


public class RemoveDto {
	/** 削除対象テーブル */
	private String tableName;
	/** 削除対象日時フィールド名 */
	private String dateFieldName;
	/** 削除判定値 */
	private int removeValue;

	/**
	 * 削除対象日時フィールド名を返します
	 * @return 削除対象日時フィールド名を返します
	 */
	public String getDateFieldName() {
		return dateFieldName;
	}
	/**
	 * 削除対象日時フィールド名を設定します
	 * @param dateFieldName 削除対象日時フィールド名を設定します
	 */
	public void setDateFieldName(String dateFieldName) {
		this.dateFieldName = dateFieldName;
	}
	/**
	 * 削除判定値を返します
	 * @return 削除判定値を返します
	 */
	public int getRemoveValue() {
		return removeValue;
	}
	/**
	 * 削除判定値を設定します
	 * @param removePeriod 削除判定値を設定します
	 */
	public void setRemoveValue(int removePeriod) {
		this.removeValue = removePeriod;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String toString() {
		return "tableName=" + tableName + ", dateFieldName=" + dateFieldName
				+ ", removePeriod=" + removeValue;
	}
}
