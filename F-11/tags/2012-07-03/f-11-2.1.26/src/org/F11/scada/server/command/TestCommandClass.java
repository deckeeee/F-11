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

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command実装のテストクラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TestCommandClass implements Command {
	/** プロパティー */
	private String key;
	/** プロパティー */
	private String name;

	/**
	 * デフォルトコンストラクタ
	 */	
	public TestCommandClass() {}

	/**
	 * データ変更イベント発生時に、実行する内容を実装します。
	 */
	public void execute(DataValueChangeEventKey evt) {
		System.out.println(this);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * プロパティーのgetter
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * プロパティーのgetter
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * プロパティーのsetter
	 * @param string
	 */
	public void setKey(String string) {
		key = string;
	}

	/**
	 * プロパティーのsetter
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

}
