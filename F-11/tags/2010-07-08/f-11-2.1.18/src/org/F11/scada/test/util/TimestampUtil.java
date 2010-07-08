/*
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
 */

package org.F11.scada.test.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 時刻オブジェクトに関するユーティリティークラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TimestampUtil {
	/**
	 * 'yyyy/MM/dd HH:mm:ss' 形式の文字列から Timestamp オブジェクトを生成します。
	 * 引数の文字列が不正な時は、null を返します。
	 * @param date 日付文字列
	 * @return Timestamp オブジェクト
	 */
	public static Timestamp parse(String date) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			return new Timestamp(fmt.parse(date).getTime());
		} catch (ParseException e) {
			return null;
		}
	}
}
