/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.parser.tree;

import java.io.Serializable;

import javax.swing.tree.TreeNode;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * メニューツリーと初期表示ページを保持するクラスです。
 */
public class TreeDefine implements Serializable {

	private static final long serialVersionUID = -8592167829997984446L;
	private TreeNode rootNode;
	private String initPage;

	/**
	 * 
	 */
	public TreeDefine() {
		super();
	}

	public TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public String getInitPage() {
		return initPage;
	}

	public void setInitPage(String initPage) {
		this.initPage = initPage;
	}

	public void setRootNode(MenuNodeState node) {
		this.rootNode = node.getNode();
	}

	/**
	 * このオブジェクトの文字列表現を返します
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
