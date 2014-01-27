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

import javax.swing.tree.DefaultMutableTreeNode;

import org.F11.scada.xwife.applet.PageTreeNode;

/**
 * メニューの各ノードを読み込む為のクラスです。
 */
public class MenuNodeState {
	private DefaultMutableTreeNode node;
	private String name = "";
	private String page = "";

	/**
	 * 
	 */
	public MenuNodeState() {
		node = new DefaultMutableTreeNode();
	}

	public DefaultMutableTreeNode getNode() {
		synchronized (node) {
			node.setUserObject(new PageTreeNode(name, page));
			return node;
		}
	}

	public void add(MenuNodeState menu) {
		synchronized (node) {
			node.add(menu.getNode());
		}
	}

	public void setName(String name) {
		this.name = name;
	}
	public void setPage(String page) {
		this.page = page;
	}
}
