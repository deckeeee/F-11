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

package org.F11.scada.server.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.F11.scada.Globals;

/**
 * Class定義クラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ClassConfig {
	/** 実行するコマンドクラス名 */
	private String className;
	/** 引数のプロパティ */
	private Properties properties;
	
	/**
	 * 実行するコマンドクラス名を返します
	 * @return 実行するコマンドクラス名
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * 実行するコマンドクラス名を設定します
	 * @param string 実行するコマンドクラス名
	 */
	public void setClassName(String string) {
		className = string;
	}

	/**
	 * 引数プロパティを返します
	 * @param key プロパティ名
	 * @return プロパティ値
	 */
	public String getProperty(String key) {
		if (properties == null) {
			return Globals.NULL_STRING;
		}
		return properties.getProperty(key);
	}

	/**
	 * プロパティを追加します
	 * @param key プロパティ名
	 * @param value プロパティ値
	 */
	public void addProperty(String key, String value) {
		if (properties == null) {
			properties = new Properties();
		}
		properties.setProperty(key, value);
	}

	/**
	 * プロパティのマップを返します
	 * @return プロパティのマップを返します
	 */
	public Map getProperties() {
		return null == properties ? new HashMap() : Collections.unmodifiableMap(properties);
	}
}
