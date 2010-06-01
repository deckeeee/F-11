/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/RowDataStrategy.java,v 1.2 2003/02/26 05:47:17 frdm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/02/26 05:47:17 $
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
package org.F11.scada.server.alarm.table;

import org.F11.scada.server.alarm.AlarmException;
import org.F11.scada.server.alarm.DataValueChangeEventKey;

/**
 * 行データに関するアルゴリズムを表すインターフェイスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface RowDataStrategy {
	/**
	 * データ変更イベントからテーブルモデルを更新します。
	 * @param key データ変更イベント
	 * @exception 行アルゴリズム実行時に発生した例外
	 */	
	public void renewRow(DataValueChangeEventKey key) throws AlarmException;
}
