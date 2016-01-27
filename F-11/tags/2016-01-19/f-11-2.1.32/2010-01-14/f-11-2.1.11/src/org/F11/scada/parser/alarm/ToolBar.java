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

package org.F11.scada.parser.alarm;

/**
 * クライアントのツールバーの設定を保持するクラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ToolBar {
	/** ログインボタン表示の有無 */
	private boolean displayLogin = true;

	/**
	 * ログインボタン表示の有無を返します
	 * @return 表示する場合は true を非表示の場合は false を返します
	 */
	public boolean isDisplayLogin() {
		return displayLogin;
	}

	/**
	 * ログインボタン表示の有無を設定します
	 * @param b 表示する場合は true を非表示の場合は false を設定します
	 */
	public void setDisplayLogin(boolean b) {
		displayLogin = b;
	}

}
