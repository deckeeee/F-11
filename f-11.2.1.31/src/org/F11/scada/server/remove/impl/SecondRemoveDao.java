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

import java.sql.Timestamp;
import java.util.Calendar;

import org.F11.scada.server.remove.RemoveDao;
import org.F11.scada.server.remove.RemoveDto;
import org.F11.scada.server.remove.RemoveExecutor;
import org.F11.scada.server.remove.RemoveExecutorFactory;

/**
 * 実行時から何秒後のデータを残すかを指定するDaoクラスです。
 * @author maekawa
 *
 */
public class SecondRemoveDao implements RemoveDao {
	private RemoveExecutorFactory factory;

	public void setFactory(RemoveExecutorFactory factory) {
		this.factory = factory;
	}

	public int remove(RemoveDto dto) {
		Timestamp deleteDate = getTimestamp(dto.getRemoveValue());
		RemoveExecutor executor = factory.getRemoveExecutor(dto.getTableName());
		return executor.execute(dto, deleteDate);
	}

	private Timestamp getTimestamp(int removePeriod) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, removePeriod * -1);
		calendar.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calendar.getTimeInMillis());
	}
}
