/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.util;

import java.util.StringTokenizer;

/**
 * Java VMのバージョンをあらわすクラスです。
 * 
 * @author maekawa
 * 
 */
public final class JavaVersion implements Comparable {
	private final int major;
	private final int middle;
	private final int minor;
	private final int update;

	/**
	 * 実行しているJava VMのバージョンを生成します。
	 * 
	 */
	public JavaVersion() {
		this(System.getProperty("java.version"));
	}

	/**
	 * 引数で指定したバージョンを生成します。
	 * 
	 * @param major メジャー番号
	 * @param middle ミドル番号
	 * @param minor マイナー番号
	 * @param update アップデート番号
	 */
	public JavaVersion(int major, int middle, int minor, int update) {
		this.major = major;
		this.middle = middle;
		this.minor = minor;
		this.update = update;
	}

	JavaVersion(String version) {
		int[] versions = getVersions(version);
		this.major = versions[0];
		this.middle = versions[1];
		this.minor = versions[2];
		if (3 < versions.length) {
			this.update = versions[3];
		} else {
			this.update = 0;
		}
	}

	private static int[] getVersions(String version) {
		StringTokenizer st = new StringTokenizer(version, "._");
		int[] versions = new int[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			versions[i] = Integer.parseInt(st.nextToken());
		}
		return versions;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof JavaVersion)) {
			return false;
		}

		JavaVersion version = (JavaVersion) obj;

		return major == version.major && middle == version.middle
				&& minor == version.minor && update == version.update;
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + major;
		result = 37 * result + middle;
		result = 37 * result + minor;
		result = 37 * result + update;
		return result;
	}

	public String toString() {
		return major + "." + middle + "." + minor + "_" + update;
	}

	public int compareTo(Object obj) {
		JavaVersion version = (JavaVersion) obj;
		if (major > version.major) {
			return 1;
		}
		if (major < version.major) {
			return -1;
		}

		if (middle > version.middle) {
			return 1;
		}
		if (middle < version.middle) {
			return -1;
		}

		if (minor > version.minor) {
			return 1;
		}
		if (minor < version.minor) {
			return -1;
		}

		if (update > version.update) {
			return 1;
		}
		if (update < version.update) {
			return -1;
		}
		return 0;
	}
}
