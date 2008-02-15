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
 *
 */

package org.F11.scada;


/**
 * 環境設定マネージャーです。RMI, JDBC 等のサーバー環境を管理します。
 */
public class EnvironmentManager {
	/** シングルトンパターンです。 */
    private static final EnvironmentManager instance = new EnvironmentManager();
	private final EnvironmentManagerStrategy strategy;

	/**
	 * プライベートコンストラクタ。
	 */
	private EnvironmentManager() {
	    EnvironmentManagerStrategyFactory factory =
	        new EnvironmentManagerStrategyFactory();
	    strategy = factory.getEnvironmentManagerStrategy();
	}

	/**
	 * キーを指定して対応する値を返します。指定したキーが存在しない場合は、デフォルト値を返します。
	 * @param key キー
	 * @param def デフォルト値
	 * @return キーに対応する値
	 */
	public static String get(String key, String def) {
	    return instance.strategy.get(key, def);
	}
}
