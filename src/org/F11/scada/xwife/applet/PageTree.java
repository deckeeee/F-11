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
 * �ŗ������X�V���� JTree �N���X
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageTree extends JTree {
	private static final long serialVersionUID = 6039971135751256843L;
	/** �ŗ��� */
	private final PageHistory history;
	/** �ŏI�\���y�[�W */
	private String lastPageKey;

	/**
	 * �w�肳�ꂽ TreeNode �����[�g�Ɏ��A���[�g�m�[�h��\������ JTree ��Ԃ��܂��B
	 * 
	 * @param root TreeNode �I�u�W�F�N�g
	 * @param history �ŗ����I�u�W�F�N�g
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
	 * �w�肳�ꂽ�p�X�Ŏ��ʂ����m�[�h��I�����܂��BPageChangeEvent#isHistory()�� true �̏ꍇ�A�ŗ������X�V���܂���B
	 * 
	 * @param path �I������m�[�h���w�肷�� TreePath
	 * @param evt �ŕύX�C�x���g
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
