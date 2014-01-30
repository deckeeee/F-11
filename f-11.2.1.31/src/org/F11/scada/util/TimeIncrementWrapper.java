/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/util/TimeIncrementWrapper.java,v 1.2 2003/10/31 04:38:51 frdm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/31 04:38:51 $
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
package org.F11.scada.util;

import java.util.Map;

/**
 * 引数の Map 内に同じキー値が存在する場合には、キー値をインクリメントして
 * マップに格納する為のラッパークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class TimeIncrementWrapper {
	private TimeIncrementWrapper() {}
	
	/**
	 * 引数の Map 内に同じキー値が存在する場合には、同じキーが無くなるまで
	 * キー値をインクリメントして、そのキー値でマップに格納します。
	 * @param key 元になるキー値
	 * @param value Map に格納するオブジェクト
	 * @param map 対象の Map オブジェクト
	 * @return 格納したオブジェクト
	 */
	public static Object put(long key, Object value, Map map) {
		Long t = null;
		for (long i = 0; i < Long.MAX_VALUE; i++) {
			t = new Long(key + i);
			if (!map.containsKey(t)) {
				break;
			}
		}
		return map.put(new Long(t.longValue()), value);
	}
}
