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
package org.F11.scada.parser.tree;

import org.apache.commons.digester.Digester;

/**
 * メニューツリーを読み込む為のハンドラクラスです。
 */
public class MenuTreeDefineHandler {
	private TreeDefine treeDefine;
	
	/**
	 * 
	 */
	public MenuTreeDefineHandler() {
		super();
	}

	/**
	 * ダイジェスタにルールを追加します。
	 */
	public void addPageRule(Digester digester) {
		digester.addObjectCreate("tree", TreeDefine.class);
		digester.addSetProperties("tree", "initpage", "initPage");
		digester.addSetNext("tree", "setTreeDefine");

		digester.addObjectCreate("tree/root", MenuNodeState.class);
		digester.addSetProperties("tree/root");
		digester.addSetNext("tree/root", "setRootNode");

		digester.addObjectCreate("*/node", MenuNodeState.class);
		digester.addSetProperties("*/node");
		digester.addSetNext("*/node", "add");
	}
	
	
	public TreeDefine getTreeDefine() {
		return treeDefine;
	}
	public void setTreeDefine(TreeDefine treeDefine) {
		this.treeDefine = treeDefine;
	}
}
