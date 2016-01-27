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

package org.F11.scada.xwife.applet;

import java.io.Serializable;

/**
 * ツリーのノードを表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageTreeNode implements Serializable {
	private static final long serialVersionUID = -5343871777897236533L;
	/** ツリーのノード名称です */
	private String nodeName = "";
	/** ノード名称に対するページキーです */
	private String key = "";

	/**
	 * デフォルトコンストラクタ
	 */
	public PageTreeNode() {
		this("", "");
	}

	/**
	 * ノードオブジェクトを初期化します
	 * @param nodeName ノード名称
	 * @param key ページキー
	 */
	public PageTreeNode(String nodeName, String key) {
		this.nodeName = nodeName;
		this.key = key;
	}

	/**
	 * このノードオブジェクトのノード名称を返します
	 * @return ノード名称
	 */
	public String toString() {
		return nodeName;
	}

	/**
	 * このノードオブジェクトのページキーを返します
	 * @return ページキー
	 */
	public String getKey() {
		return key;
	}
}
