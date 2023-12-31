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

package org.F11.scada.server.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.register.HolderString;

/**
 * 指定されたパスにビット情報を出力するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ScheduledOnlyFirstWrite extends AbstractScheduledCommand {
	/** スレッドプール実行クラス */
	private static ScheduledExecutorService executor =
		Executors.newScheduledThreadPool(1);
	private static Map<HolderString, ScheduledFuture<?>> map =
		new ConcurrentHashMap<HolderString, ScheduledFuture<?>>();

	protected void schedule(DataValueChangeEventKey evt) {
		try {
			HolderString key = new HolderString(getProvider(), getHolder());
			if (!map.containsKey(key)) {
				WriteTermCommandTask command =
					new WriteTermCommandTask(
						evt,
						getProvider(),
						getHolder(),
						getValue(),
						map);
				map.put(key, executor.schedule(
					command,
					getDelay(),
					TimeUnit.SECONDS));
			}
		} catch (RejectedExecutionException e) {
		}
	}
}
