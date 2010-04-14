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

package org.F11.scada.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

public class ServerConstruction {
	private final Logger logger = Logger.getLogger(ServerConstruction.class);
	private IPandPort mainSystem;
	private List<String> subSystems;

	public String getMainSystem() {
		return mainSystem.toString();
	}

	public void setMainSystem(String mainSystem, String port) {
		this.mainSystem = new IPandPort(mainSystem, getPort(port));
	}

	private int getPort(String port) {
		return null == port || "".equals(port) ? 1099 : Integer.parseInt(port);
	}

	public List<String> getSubSystems() {
		if (null == subSystems) {
			return Collections.emptyList();
		} else {
			return Collections.unmodifiableList(subSystems);
		}
	}

	public boolean addSubSystem(String address, String port) {
		if (null == subSystems) {
			subSystems = new ArrayList<String>();
		}
		return subSystems.add(address + ":" + getPort(port));
	}

	public boolean isMainSystem(String address) {
		try {
			InetAddress mainIp = InetAddress.getByName(mainSystem.getIp());
			InetAddress checkIp = InetAddress.getByName(address);
			return mainIp.equals(checkIp);
		} catch (UnknownHostException e) {
			logger.error("ドメインあるいはIPアドレスの形式が不正です : mainSystem="
				+ mainSystem
				+ " , address="
				+ address, e);
			throw new RuntimeException("ドメインあるいはIPアドレスの形式が不正です : mainSystem="
				+ mainSystem
				+ " , address="
				+ address, e);
		}
	}

	private static class IPandPort {
		private final String ip;
		private final int port;

		IPandPort(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		public String getIp() {
			return ip;
		}

		public String toString() {
			return ip + ":" + port;
		}
	}
}
