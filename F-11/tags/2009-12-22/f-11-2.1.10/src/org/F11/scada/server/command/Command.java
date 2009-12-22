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

/**
 * コマンド実行クラスのインターフェイスです。
 * 
 * このインターフェイスを実装するクラスは、引数無しのコンストラクタを
 * 持つ必要があります。CommandProviderではまず引数無しのコンストラクタで、
 * オブジェクトを初期化した後プロパティーを設定します。
 * 
 * プロパティーの設定はBeanUtil#populateメソッドに委譲します。JavaBeans
 * の仕様にそってsetterを実装する必要があります。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Command {
	/**
	 * コマンド実行します。
	 * @param evt データ変更イベント
	 */
	public void execute(DataValueChangeEventKey evt);
}
