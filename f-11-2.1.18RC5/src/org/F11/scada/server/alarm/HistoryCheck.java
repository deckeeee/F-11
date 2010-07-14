/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/HistoryCheck.java,v 1.2 2003/11/28 05:30:19 frdm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/11/28 05:30:19 $
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
package org.F11.scada.server.alarm;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * ヒストリー確認欄更新インターフェイスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface HistoryCheck {
	/**
	 * ヒストリー確認欄更新処理を実行します。
	 * @param point ポイント
	 * @param provider データプロバイダ名
	 * @param holder データホルダー名
	 * @param date 確認処理時刻
	 */
	public void doHistoryCheck(
			Integer point,
			String provider,
			String holder,
			Timestamp date) throws SQLException;
}
