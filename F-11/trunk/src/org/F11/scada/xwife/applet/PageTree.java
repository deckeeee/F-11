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

package org.F11.scada.xwife.applet;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.F11.scada.util.FontUtil;
import org.F11.scada.util.PageHistory;
import org.apache.commons.configuration.Configuration;

/**
 * 頁履歴を更新する JTree クラス
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageTree extends JTree {
	private static final long serialVersionUID = 6039971135751256843L;
	/** 頁履歴 */
	private final PageHistory history;
	/** 最終表示ページ */
	private String lastPageKey;

	/**
	 * 指定された TreeNode をルートに持つ、ルートノードを表示する JTree を返します。
	 * 
	 * @param root TreeNode オブジェクト
	 * @param history 頁履歴オブジェクト
	 */
	public PageTree(
			TreeNode root,
			PageHistory history,
			Configuration configuration) {
		super(root);
		this.history = history;
		String fontSize =
			configuration.getString(
				"org.F11.scada.xwife.applet.pagetree.font",
				"12");
		FontUtil.setFont("dialog", "PLAIN", fontSize, this);
	}

	/**
	 * 指定されたパスで識別されるノードを選択します。PageChangeEvent#isHistory()が true の場合、頁履歴を更新しません。
	 * 
	 * @param path 選択するノードを指定する TreePath
	 * @param evt 頁変更イベント
	 */
	public void setSelectionPath(TreePath path, PageChangeEvent evt) {
		if (!evt.isHistory()) {
			history.set(evt.getKey());
		}
		lastPageKey = evt.getKey();
		super.setSelectionPath(path);
	}

	public void setSelectionPath(TreePath path) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode) path.getLastPathComponent();
		if (node.isLeaf()) {
			PageTreeNode pgnode = (PageTreeNode) node.getUserObject();
			history.set(pgnode.getKey());
			lastPageKey = pgnode.getKey();
		}
		super.setSelectionPath(path);
	}

	public void setRootTreeNode(TreeNode root) {
		super.setModel(new DefaultTreeModel(root));
	}

	public String getLastPageKey() {
		return lastPageKey;
	}

}
