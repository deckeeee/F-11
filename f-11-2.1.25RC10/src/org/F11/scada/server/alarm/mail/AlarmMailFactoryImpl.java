/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2007 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.alarm.mail;

import java.util.HashMap;
import java.util.Map;

import org.F11.scada.EnvironmentManager;

public class AlarmMailFactoryImpl implements AlarmMailFactory {
	private final Map<String, AlarmMail> mailMap;

	public AlarmMailFactoryImpl() {
		mailMap = new HashMap<String, AlarmMail>();
	}

	public void addAlarmMail(String name, AlarmMail alarmMail) {
		mailMap.put(name, alarmMail);
	}

	public AlarmMail getAlarmMail() {
		String name = EnvironmentManager.get(
				"/server/mail/smtp/sender",
				"alarmMail");
		System.out.println(mailMap.get(name).getClass().getName());
		return mailMap.get(name);
	}
}
