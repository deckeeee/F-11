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

import org.F11.scada.EnvironmentManager;
import org.apache.log4j.Logger;

public class SmtpEnvironments {
	private final Logger logger = Logger.getLogger(SmtpEnvironments.class);
	private String servername;
	private int serverPort;
	private String user;
	private String password;
	private String from;
	private String subject;
	private String disableProvider = "";
	private String disableHolder = "";

	public SmtpEnvironments() {
		servername = EnvironmentManager.get("/server/mail/smtp/servername", "");
		from = EnvironmentManager.get("/server/mail/message/from", "");
		subject = EnvironmentManager.get("/server/mail/message/subject", "");
		String name =
			EnvironmentManager.get("/server/mail/smtp/disableHolder", "");
		int p = name.indexOf('_');
		if (0 < p) {
			disableProvider = name.substring(0, p);
			disableHolder = name.substring(p + 1);
		}
		String portStr = EnvironmentManager.get("/server/mail/smtp/port", "25");
		try {
			serverPort = Integer.parseInt(portStr);
		} catch (Exception e) {
			serverPort = 25;
			logger.error(String.format(
				"/server/mail/smtp/portで指定された\"%s\"を数値変換できません。初期値の25を設定します。",
				portStr));
		}
		user = EnvironmentManager.get("/server/mail/smtp/user", "");
		password = EnvironmentManager.get("/server/mail/smtp/password", "");
	}

	public String getServername() {
		return servername;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getFrom() {
		return from;
	}

	public String getSubject() {
		return subject;
	}

	public String getDisableProvider() {
		return disableProvider;
	}

	public String getDisableHolder() {
		return disableHolder;
	}

	@Override
	public String toString() {
		return "servername="
			+ servername
			+ ", serverPort="
			+ serverPort
			+ ", user="
			+ user
			+ ", password="
			+ password
			+ ", user="
			+ from
			+ ", subject="
			+ subject
			+ ", disableProvider="
			+ disableProvider
			+ ", disableHolder="
			+ disableHolder;
	}
}
