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

package org.F11.scada.server.alarm.print;

import java.io.IOException;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

/**
 * 警報印刷サービスオブジェクトを生成するファクトリーです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintFactory {

	public static AlarmDataStore getAlarmDataStore()
			throws IOException {

		AlarmDataStore store = null;
		String clazz =
			EnvironmentManager.get("/server/alarm/print/className", "");
		if (clazz == null || "".equals(clazz)) {
			store = new AlarmPrintPage();
		} else if (
			"org.F11.scada.server.alarm.print.AlarmPrintService".equals(
				clazz)) {
			S2Container container =
				S2ContainerFactory.create(
					"org/F11/scada/server/alarm/print/AlarmPrintService.dicon");
			container.init();
			store = (AlarmDataStore) container.getComponent("alarmPrintService");
		} else {
			store = new AlarmPrintPage();
		}

		return store;
	}
}
