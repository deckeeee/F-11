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

package org.F11.scada.server.logging.report.schedule;

import java.sql.Timestamp;

/**
 * レコード検索日付を算出するヘルパクラスインターフェイス 内部に実装を持つ
 * 
 * @author hori
 * 
 */
public interface CsvSchedule {
	/**
	 * 出力するデータの開始日時を返します。
	 * 
	 * @param now 日時
	 * @param startMode 各種モード
	 * @return 出力するデータの開始日時を返します。
	 */
	Timestamp startTime(long now, boolean startMode);

	/**
	 * 出力するデータの終了日時を返します。
	 * 
	 * @param now 日時
	 * @param startMode 各種モード
	 * @return 出力するデータの終了日時を返します。
	 */
	Timestamp endTime(long now, boolean startMode);

	/**
	 * 出力タイミングの有無を返します
	 * 
	 * @return 出力タイミングの有無を返します
	 */
	boolean isOutput();
}
