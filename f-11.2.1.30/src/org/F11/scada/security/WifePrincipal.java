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

package org.F11.scada.security;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.Principal;

/**
 * WIFE の主体を表します。具体的にはユーザーとグループを表します。
 */
public class WifePrincipal implements Principal, Serializable {
	private static final long serialVersionUID = 8159333082688116879L;
	/** 主体名 */
	private final String name;

	/**
	 * 名前で初期化されたオブジェクトを生成します。
	 * @param name 主体名
	 */
	public WifePrincipal(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Argument `name' is null.");
		}
		this.name = name;
	}

	/**
	 * 主体名を返します
	 * @return 主体名
	 */
	public String getName() {
		return name;
	}

	public boolean equals(Object another) {
		if (another == this) {
			return true;
		}
		if (!(another instanceof WifePrincipal)) {
			return false;
		}
		WifePrincipal principal = (WifePrincipal) another;
		return principal.name.equals(this.name);
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public String toString() {
		return this.name;
	}

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifePrincipal(name); 
	}
}
