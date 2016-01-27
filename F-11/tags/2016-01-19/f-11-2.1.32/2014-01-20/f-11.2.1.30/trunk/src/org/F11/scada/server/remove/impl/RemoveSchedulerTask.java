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

package org.F11.scada.server.remove.impl;

import org.F11.scada.scheduling.SchedulerTask;
import org.F11.scada.server.remove.RemoveDao;
import org.F11.scada.server.remove.RemoveDto;
import org.apache.log4j.Logger;

public class RemoveSchedulerTask extends SchedulerTask {
	private static Logger logger = Logger.getLogger(RemoveSchedulerTask.class);
	private final RemoveDao dao;
	private final RemoveDto dto;

	public RemoveSchedulerTask(RemoveDao dao, RemoveDto dto) {
		this.dao = dao;
		this.dto = dto;
		logger.info("タスク開始:" + dto.toString());
	}

	public void run() {
		try {
			dao.remove(dto);
		} catch (Exception e) {
			logger.warn("SQL実行時にエラーが発生。", e);
		}
	}
}
