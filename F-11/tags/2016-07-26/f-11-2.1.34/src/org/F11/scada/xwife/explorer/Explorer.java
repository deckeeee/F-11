/*
 * =============================================================================
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

package org.F11.scada.xwife.explorer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.F11.scada.EnvironmentManager;

public class Explorer extends JPanel {
	private static final long serialVersionUID = 1404986561164570079L;
	private Map map = new LinkedHashMap();
	private DefaultMutableTreeNode root;

	public Explorer(Component component) {
		super(new BorderLayout());
		JSplitPane splitPane = new JSplitPane();
		root = new DefaultMutableTreeNode(getServerName());
		JTree tree = new JTree(root);
		tree.addTreeSelectionListener(new TreeSelectionListenerImpl(
				splitPane,
				map));
		splitPane.setRightComponent(component);
		splitPane.setLeftComponent(new JScrollPane(tree));
		add(splitPane, BorderLayout.CENTER);
	}

	private String getServerName() {
		return EnvironmentManager.get("/server/title", "F-11 サーバー");
	}

	public synchronized void put(String name, ExplorerElement element) {
		map.put(name, element.getComponent());
		synchronized (root) {
			root.add(new DefaultMutableTreeNode(name));
		}
	}

	private static class TreeSelectionListenerImpl implements
			TreeSelectionListener {
		private final JSplitPane splitPane;
		private final Map map;

		public TreeSelectionListenerImpl(JSplitPane splitPane, Map map) {
			this.splitPane = splitPane;
			this.map = map;
		}

		public void valueChanged(TreeSelectionEvent e) {
			JTree tree = (JTree) e.getSource();
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (null != selNode && selNode.isLeaf()) {
				String menu = (String) selNode.getUserObject();
				Component component = (Component) map.get(menu);
				int loc = splitPane.getDividerLocation();
				splitPane.setRightComponent(component);
				splitPane.setDividerLocation(loc);
				splitPane.revalidate();
			}
		}
	}
}
