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

package org.F11.scada.theme;

import java.io.IOException;
import java.util.Properties;

public class Version {
	private static final Version instance = new Version();
	private String version = "";
	private String license1 = "";
	private String license2 = "";

	private Version() {
		Properties properties = new Properties();
		try {
			properties.load(Version.class.getResourceAsStream("/resources/version.txt"));
		} catch (IOException e) {
		}
		version = properties.getProperty("version", "2.0.x");
		license1 = properties.getProperty("license1");
		license2 = properties.getProperty("license2");
	}

	public static String getVersion() {
		return instance.version;
	}
	
	public static String getLicense() {
		String versionText = "Version. " + getVersion() + "<br><br>";
		return instance.license1 + versionText + instance.license2;
	}
}
