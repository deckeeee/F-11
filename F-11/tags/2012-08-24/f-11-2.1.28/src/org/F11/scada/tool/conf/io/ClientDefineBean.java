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
package org.F11.scada.tool.conf.io;

public class ClientDefineBean {
	private String ipAddress;
	private String defaultUserName;

	/**
	 * 
	 */
	public ClientDefineBean() {
	}

	/**
	 * @param ipAddress
	 * @param defaultUserName
	 */
	public ClientDefineBean(ClientDefineBean bean) {
		super();
		this.ipAddress = bean.ipAddress;
		this.defaultUserName = bean.defaultUserName;
	}

	public String getDefaultUserName() {
		return defaultUserName;
	}
	public void setDefaultUserName(String defaultUserName) {
		this.defaultUserName = defaultUserName;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
