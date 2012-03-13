/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.io.postgresql.padding;

import java.util.HashMap;
import java.util.Map;

import org.F11.scada.server.io.postgresql.PostgreSQLUtility;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.seasar.framework.container.S2Container;

/**
 * レコード補完ロジックのマップを生成します
 *
 * @author maekawa
 *
 */
public abstract class PaddingMapFactory {
	/**
	 * レコード補完ロジックのマップを生成します
	 *
	 * @return レコード補完ロジックのマップを生成します
	 */
	public static Map<String, PaddingLogic> createLogicMap() {
		S2Container container = S2ContainerUtil.getS2Container();
		PostgreSQLUtility utility =
			(PostgreSQLUtility) container.getComponent(PostgreSQLUtility.class);
		HashMap<String, PaddingLogic> map = new HashMap<String, PaddingLogic>();
		map.put("MINUTE", new Minute(utility, 1));
		map.put("TENMINUTE", new Minute(utility, 10));
		map.put("HOUR", new Hour(utility));
		map.put("TDHOUR", new Hour(utility));
		map.put("DAILY", new Daily(utility));
		map.put("TMDAILY", new Daily(utility));
		map.put("MONTHLY", new Monthly(utility));
		map.put("MONTHLY4", new Monthly(utility));
		map.put("TYMONTHLY4", new Monthly(utility));
		map.put("YEARLY", new Yearly(utility));
		map.put("ONESECOND", new Second(utility, 1));
		map.put("QMINUTE", new Minute(utility, 15));
		map.put("FIVEMINUTE", new Minute(utility, 5));
		map.put("THIRTYMINUTE", new Minute(utility, 30));
		map.put("SIXTYMINUTE", new Minute(utility, 60));
		map.put("ONEHOURMONTHOUT", new Hour(utility));
		map.put("MONTHLYMONTHOUT", new Monthly(utility));
		map.put("GODA", new Minute(utility, 10));
		map.put("GODA01", new Minute(utility, 1));
		map.put("GODA05", new Minute(utility, 5));
		map.put("GODA10", new Minute(utility, 10));
		map.put("GODA30", new Minute(utility, 30));
		map.put("GODA60", new Minute(utility, 60));
		map.put("ONEMINUTE", new Minute(utility, 1));
		map.put("BMS", new Minute(utility, 1));
		map.put("MINUTEHOUROUT", new Minute(utility, 1));
		map.put("ONEHOURMONTHOUT2", new Hour(utility));
		return map;
	}
}
