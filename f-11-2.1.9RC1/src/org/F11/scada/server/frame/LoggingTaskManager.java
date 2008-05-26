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

package org.F11.scada.server.frame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.F11.scada.server.io.PointNameDataStore;
import org.F11.scada.server.logging.LoggingTask;
import org.F11.scada.server.register.HolderString;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
class LoggingTaskManager {
	/** ロギングタスクマップの参照です */
	private final Map taskMap;

	/**
	 * ロギングタスクマネージャーを初期化します
	 * 
	 * @param taskMap ロギングタスクマップ
	 */
	LoggingTaskManager(Map taskMap) {
		this.taskMap = taskMap;
	}

	/**
	 * loggingNameで指定したロギングファイルに保存される項目の属性リストを返します。
	 * 
	 * @param loggingName ロギングファイル名
	 * @return 項目の属性リスト
	 */
	public List getLoggingHolders(String loggingName) {
		ArrayList taskItems = new ArrayList();
		try {
			PointNameDataStore dataStore = new PointNameDataStore();
			LoggingTask task = (LoggingTask) taskMap.get(loggingName);
			taskItems
				.addAll(dataStore.getAnalogNameList(task.getDataHolders()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return taskItems;
	}

	public List<HolderString> getHolders(
			String loggingName) {
		LoggingTask task = (LoggingTask) taskMap.get(loggingName);
		return task.getDataHolders();
	}
}
