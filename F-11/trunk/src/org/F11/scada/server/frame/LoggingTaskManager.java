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
	/** ���M���O�^�X�N�}�b�v�̎Q�Ƃł� */
	private final Map taskMap;

	/**
	 * ���M���O�^�X�N�}�l�[�W���[�����������܂�
	 * 
	 * @param taskMap ���M���O�^�X�N�}�b�v
	 */
	LoggingTaskManager(Map taskMap) {
		this.taskMap = taskMap;
	}

	/**
	 * loggingName�Ŏw�肵�����M���O�t�@�C���ɕۑ�����鍀�ڂ̑������X�g��Ԃ��܂��B
	 * 
	 * @param loggingName ���M���O�t�@�C����
	 * @return ���ڂ̑������X�g
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
