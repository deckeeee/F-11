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

package org.F11.scada.server.alarm.table;

public interface AlarmIndividualSettingDao {
	public static final Class BEAN = AlarmIndividualSettingDto.class;
	
	public static final String getAlarmIndividualSetting_QUERY = "provider = ? AND holder = ?";
	AlarmIndividualSettingDto getAlarmIndividualSetting(String provider, String holder);

	public static final String insertAlarmIndividualSetting_NO_PERSISTENT_PROPS = "alarmIndividualSettingId";	
	int insertAlarmIndividualSetting(AlarmIndividualSettingDto dto);
	
	public static final String updateAlarmIndividualSetting_PERSISTENT_PROPS = "type";
	int updateAlarmIndividualSetting(AlarmIndividualSettingDto dto);
	
	int deleteAlarmIndividualSetting(AlarmIndividualSettingDto dto);
}
