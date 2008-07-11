/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.cat.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;

class CheckRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 3584899861697585043L;
	private JCheckBox checkBox;
	private TreeLabel treeLabel;

	public CheckRenderer() {
		setLayout(null);
		add(checkBox = new JCheckBox());
		add(treeLabel = new TreeLabel());
		checkBox.setBackground(UIManager.getColor("Tree.textBackground"));
		treeLabel.setForeground(UIManager.getColor("Tree.textForeground"));
	}

	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean isSelected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {
		String stringValue = tree.convertValueToText(
				value,
				isSelected,
				expanded,
				leaf,
				row,
				hasFocus);
		setEnabled(tree.isEnabled());
		checkBox.setSelected(((CheckNode) value).isSelected());
		treeLabel.setFont(tree.getFont());
		treeLabel.setText(stringValue);
		treeLabel.setSelected(isSelected);
		treeLabel.setFocus(hasFocus);
		if (leaf) {
			treeLabel.setIcon(UIManager.getIcon("Tree.leafIcon"));
		} else if (expanded) {
			treeLabel.setIcon(UIManager.getIcon("Tree.openIcon"));
		} else {
			treeLabel.setIcon(UIManager.getIcon("Tree.closedIcon"));
		}
		return this;
	}

	public Dimension getPreferredSize() {
		Dimension dCheck = checkBox.getPreferredSize();
		Dimension dLabel = treeLabel.getPreferredSize();
		return new Dimension(
				dCheck.width + dLabel.width,
				(dCheck.height < dLabel.height ? dLabel.height : dCheck.height));
	}

	public void doLayout() {
		Dimension dCheck = checkBox.getPreferredSize();
		Dimension dLabel = treeLabel.getPreferredSize();
		int yCheck = 0;
		int yLabel = 0;
		if (dCheck.height < dLabel.height) {
			yCheck = (dLabel.height - dCheck.height) / 2;
		} else {
			yLabel = (dCheck.height - dLabel.height) / 2;
		}
		checkBox.setLocation(0, yCheck);
		checkBox.setBounds(0, yCheck, dCheck.width, dCheck.height);
		treeLabel.setLocation(dCheck.width, yLabel);
		treeLabel.setBounds(dCheck.width, yLabel, dLabel.width, dLabel.height);
	}

	public void setBackground(Color color) {
		if (color instanceof ColorUIResource)
			color = null;
		super.setBackground(color);
	}

	private static class TreeLabel extends JLabel {
		private static final long serialVersionUID = 7947998700175925579L;
		boolean isSelected;
		boolean hasFocus;

		public TreeLabel() {
		}

		public void setBackground(Color color) {
			if (color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}

		public void paint(Graphics g) {
			String str;
			if ((str = getText()) != null) {
				if (0 < str.length()) {
					if (isSelected) {
						g.setColor(UIManager
								.getColor("Tree.selectionBackground"));
					} else {
						g.setColor(UIManager.getColor("Tree.textBackground"));
					}
					Dimension d = getPreferredSize();
					int imageOffset = 0;
					Icon currentI = getIcon();
					if (currentI != null) {
						imageOffset = currentI.getIconWidth()
								+ Math.max(0, getIconTextGap() - 1);
					}
					g.fillRect(
							imageOffset,
							0,
							d.width - 1 - imageOffset,
							d.height);
					if (hasFocus) {
						g.setColor(UIManager
								.getColor("Tree.selectionBorderColor"));
						g.drawRect(
								imageOffset,
								0,
								d.width - 1 - imageOffset,
								d.height - 1);
					}
				}
			}
			super.paint(g);
		}

		public Dimension getPreferredSize() {
			Dimension retDimension = super.getPreferredSize();
			if (retDimension != null) {
				retDimension = new Dimension(
						retDimension.width + 3,
						retDimension.height);
			}
			return retDimension;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public void setFocus(boolean hasFocus) {
			this.hasFocus = hasFocus;
		}
	}
}
