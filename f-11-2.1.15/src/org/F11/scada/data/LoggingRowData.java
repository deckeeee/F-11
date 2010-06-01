/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/LoggingRowData.java,v 1.3.4.1 2004/09/07 01:44:23 frdm Exp $
 * $Revision: 1.3.4.1 $
 * $Date: 2004/09/07 01:44:23 $
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

package org.F11.scada.data;

import java.sql.Timestamp;

import org.apache.commons.collections.primitives.DoubleList;

public interface LoggingRowData {

	/**
	 * レコードのタイムスタンプを返します
	 * @return レコードのタイムスタンプ
	 */
	public Timestamp getTimestamp();

	/**
	 * このレコードの指定列値を返します。
	 * @param column 指定列
	 * @return このレコードの指定列値
	 */
	public double getDouble(int column);
	
	/**
	 * このレコードのデータのリストを返します。
	 * @return このレコードのデータのリストを返します。
	 */
	public DoubleList getList();
}
