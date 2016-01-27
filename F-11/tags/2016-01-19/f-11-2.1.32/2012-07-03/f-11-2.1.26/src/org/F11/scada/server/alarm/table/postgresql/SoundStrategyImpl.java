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

package org.F11.scada.server.alarm.table.postgresql;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.table.AlarmIndividualSettingDao;
import org.F11.scada.server.alarm.table.AlarmIndividualSettingDto;
import org.F11.scada.server.alarm.table.SoundStrategy;
import org.apache.log4j.Logger;

public class SoundStrategyImpl implements SoundStrategy {
	private final Logger logger = Logger.getLogger(SoundStrategyImpl.class);
	private AlarmIndividualSettingDao dao;
	private final boolean isAttributeMode;

	public SoundStrategyImpl() {
		isAttributeMode =
			Boolean.valueOf(EnvironmentManager.get(
				"/server/alarm/sound/attributemode",
				"true"));
		logger.info("�x�񉹓��샂�[�h = " + (isAttributeMode ? "�����D��" : "�|�C���g�D��"));
	}

	public void setDao(AlarmIndividualSettingDao dao) {
		this.dao = dao;
	}

	public Integer getSoundType(
			int attributeSoundType,
			DataValueChangeEventKey key) {
		return getSoundType(attributeSoundType, getSoundTypeFromDao(key));
	}

	private Integer getSoundType(int attributeSoundType, int soundTypeFromDao) {
		if (isAttributeMode) {
			return (soundTypeFromDao != 0)
				? new Integer(soundTypeFromDao)
				: new Integer(attributeSoundType);
		} else {
			return soundTypeFromDao;
		}
	}

	private int getSoundTypeFromDao(DataValueChangeEventKey key) {
		AlarmIndividualSettingDto dto =
			dao.getAlarmIndividualSetting(key.getProvider(), key.getHolder());
		return dto == null ? 0 : dto.getType();
	}
}
