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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * チェックボックスを持つ項目を表示するJListクラスです。該当する項目をクリックすることで、チェックボックスをトグル操作を制御します。
 * Listのコンストラクタが追加されている以外は、JListと同様です。
 * 
 * @author maekawa
 * 
 */
public class CheckList extends JList {
	private static final long serialVersionUID = -7989600317625150706L;

	public CheckList() {
		super();
		init();
	}

	public CheckList(ListModel dataModel) {
		super(dataModel);
		init();
	}

	public CheckList(Object[] listData) {
		super(listData);
		init();
	}

	public CheckList(Vector<?> listData) {
		super(listData);
		init();
	}

	public <T> CheckList(final List<T> listData) {
		this(new AbstractListModel() {
			private static final long serialVersionUID = -7667811338375234737L;

			public Object getElementAt(int index) {
				return listData.get(index);
			}

			public int getSize() {
				return listData.size();
			}
		});
	}

	private void init() {
		setCellRenderer(new CheckListRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setBorder(new EmptyBorder(0, 4, 0, 0));
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem) getModel().getElementAt(
						index);
				item.setSelected(!item.isSelected());
				Rectangle rect = getCellBounds(index, index);
				repaint(rect);
			}
		});
	}

	private static class CheckListRenderer extends JCheckBox implements
			ListCellRenderer {
		private static final long serialVersionUID = 3087008713313760678L;

		public CheckListRenderer() {
			setBackground(UIManager.getColor("List.textBackground"));
			setForeground(UIManager.getColor("List.textForeground"));
		}

		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean hasFocus) {
			setEnabled(list.isEnabled());
			setSelected(((CheckableItem) value).isSelected());
			setFont(list.getFont());
			setText(value.toString());
			return this;
		}
	}
}
