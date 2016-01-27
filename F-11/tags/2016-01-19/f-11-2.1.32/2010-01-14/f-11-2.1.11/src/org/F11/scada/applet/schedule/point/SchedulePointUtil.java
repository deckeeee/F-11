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

package org.F11.scada.applet.schedule.point;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import jp.gr.javacons.jim.Manager;

import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.xwife.applet.WifeDataProviderProxy;
import org.apache.log4j.Logger;

abstract public class SchedulePointUtil {
	private static final Logger logger = Logger
			.getLogger(SchedulePointUtil.class);

	public static void setLoggingField(SchedulePointRowDto dto) {
		try {
			WifeDataProviderProxy proxy = (WifeDataProviderProxy) Manager
					.getInstance().getDataProvider(dto.getGroupNoProvider());
			String user = proxy.getSubject().getUserName();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			dto.setUser(user);
			dto.setTimestamp(timestamp);
			dto.setIpAddress(ipAddress);
		} catch (UnknownHostException e) {
			logger.error("IPアドレス取得時にエラー発生", e);
		}
	}
}
