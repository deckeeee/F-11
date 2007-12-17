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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Command定義クラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CommandConfig implements ClassConfigContainer {
	/** プロバイダ名です */
	private String provider;
	/** ホルダ名です */
	private String holder;
	/** コマンド起動 Class 定義のリスト */
	private ArrayList classConfigs = new ArrayList();
	
	/**
	 * ホルダ名を返します
	 * @return ホルダ名
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * プロバイダ名を返します
	 * @return プロバイダ名
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * ホルダ名を設定します
	 * @param string ホルダ名
	 */
	public void setHolder(String string) {
		holder = string;
	}

	/**
	 * プロバイダ名を設定します
	 * @param string プロバイダ名
	 */
	public void setProvider(String string) {
		provider = string;
	}
	
	/**
	 * コマンド起動 Class 定義を追加します
	 * @param config コマンド起動 Class 定義
	 */
	public void addClassConfig(ClassConfig config) {
		classConfigs.add(config);
	}
	
	/**
	 * コマンド起動 Class 定義のリストを返します
	 * @return コマンド起動 Class 定義のリスト
	 */
	public List getdClassConfigs() {
		return Collections.unmodifiableList(classConfigs);
	}
}
