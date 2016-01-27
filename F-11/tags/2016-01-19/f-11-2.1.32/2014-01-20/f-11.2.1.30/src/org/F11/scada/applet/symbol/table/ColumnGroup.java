/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/symbol/table/ColumnGroup.java,v 1.1.6.1 2005/08/11 07:46:35 frdm Exp $
 * $Revision: 1.1.6.1 $
 * $Date: 2005/08/11 07:46:35 $
 * 
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
package org.F11.scada.applet.symbol.table;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * �O���[�v���e�[�u���w�b�_�̃G�������g�N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColumnGroup {
	/** �e�[�u���w�b�_�[�Z�������_���[�̎Q�� */
	private TableCellRenderer renderer;
	/** ColumnGroup �� TableColumn �I�u�W�F�N�g�̃��X�g */
	private List groupList;
	/** �w�b�_�[�ɕ\������e�L�X�g */
	private String text;
	/** �w�b�_�Ԃ̃}�[�W�� */
	private int margin;

	/**
	 * �f�t�H���g�̃w�b�_�����_���[�ŁAColumnGroup�I�u�W�F�N�g�𐶐����܂��B
	 * ColumnGroup�͓���q�ɂ��邱�Ƃ��\�ŁA�`�悳���w�b�_������q�ɂȂ�܂��B
	 * @param text �w�b�_�ɕ\������e�L�X�g
	 */
	public ColumnGroup(String text) {
		this(null, text);
	}

	/**
	 * �w�b�_�̃����_���[���w�肵�āAColumnGroup�I�u�W�F�N�g�𐶐����܂��B
	 * ColumnGroup�͓���q�ɂ��邱�Ƃ��\�ŁA�`�悳���w�b�_������q�ɂȂ�܂��B
	 * @param renderer �w�b�_�̃����_���[�I�u�W�F�N�g
	 * @param text �w�b�_�ɕ\������e�L�X�g
	 */
	public ColumnGroup(TableCellRenderer renderer, String text) {
		if (renderer == null) {
			this.renderer = new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 7153428198215003103L;

				public Component getTableCellRendererComponent(
						JTable table,
						Object value,
						boolean isSelected,
						boolean hasFocus,
						int row,
						int column) {
					JTableHeader header = table.getTableHeader();
					if (header != null) {
						setForeground(header.getForeground());
						setBackground(header.getBackground());
						setFont(header.getFont());
					}
					setHorizontalAlignment(JLabel.CENTER);
					setText((value == null) ? "" : value.toString());
					setBorder(UIManager.getBorder("TableHeader.cellBorder"));
					return this;
				}
			};
		} else {
			this.renderer = renderer;
		}
		this.text = text;
		groupList = new LinkedList();
	}

	/**
	 * ColumnGroup���q���ɒǉ����܂��B
	 * @param obj ColumnGroup�I�u�W�F�N�g
	 */
	public void add(ColumnGroup obj) {
		if (obj == null) {
			throw new IllegalArgumentException("ColumnGroup is null Object.");
		}
		if (obj == this) {
			throw new IllegalArgumentException("ColumnGroup is `this' Object.");
		}
		groupList.add(obj);
	}

	/**
	 * TableColumn���q���ɒǉ����܂��B
	 * @param obj TableColumn�I�u�W�F�N�g
	 */
	public void add(TableColumn obj) {
		if (obj == null) {
			throw new IllegalArgumentException("TableColumn is null Object.");
		}
		groupList.add(obj);
	}

	/**
	 * ������TableColumn���܂ށA�K�w������ColumnGroup�̃��X�g�I�u�W�F�N�g��Ԃ��܂��B
	 * @param c TableColumn
	 * @param g ColumnGroups
	 * @return ������TableColumn�����݂����ꍇ�A�K�w������ColumnGroup�̃��X�g�I�u�W�F�N�g�B���݂��Ȃ��ꍇ�� null ��Ԃ��܂��B
	 */
	public List getColumnGroups(TableColumn c, List g) {
		g.add(this);
		if (groupList.contains(c)) {
			return g;
		}
		for (Iterator it = groupList.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof ColumnGroup) {
				List g2 = new LinkedList(g);
				List groups =
					((ColumnGroup) obj).getColumnGroups(c, g2);
				if (groups != null) {
					return groups;
				}
			}
		}
		return null;
	}

	/**
	 * TableCellRenderer�I�u�W�F�N�g��Ԃ��܂��B
	 * @return TableCellRenderer�I�u�W�F�N�g
	 */
	public TableCellRenderer getHeaderRenderer() {
		return renderer;
	}

	/**
	 * TableCellRenderer��ݒ肵�܂��B
	 * @param renderer TableCellRenderer�I�u�W�F�N�g
	 */
	public void setHeaderRenderer(TableCellRenderer renderer) {
		if (renderer != null) {
			this.renderer = renderer;
		}
	}

	/**
	 * �w�b�_�l�i�^�C�g�����j��Ԃ��܂��B
	 * @return �w�b�_�l�i�^�C�g�����j
	 */
	public Object getHeaderValue() {
		return text;
	}

	/**
	 * ���̗�̃T�C�Y��Ԃ��܂��B
	 * @param table �w�b�_�������Ă���e�[�u��
	 * @return Dimension�I�u�W�F�N�g
	 */
	public Dimension getSize(JTable table) {
		Component comp =
			renderer.getTableCellRendererComponent(
				table,
				getHeaderValue(),
				false,
				false,
				-1,
				-1);
		int height = comp.getPreferredSize().height;
		int width = 0;
		for (Iterator it = groupList.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof TableColumn) {
				TableColumn aColumn = (TableColumn) obj;
				width += aColumn.getWidth();
				width += margin;
			} else {
				width += ((ColumnGroup) obj).getSize(table).width;
			}
		}
		return new Dimension(width, height);
	}

	/**
	 * �q�̗�w�b�_�Ԃ̃}�[�W����ݒ肵�܂��B
	 * @param margin �q�̗�w�b�_�Ԃ̃}�[�W��
	 */
	public void setColumnMargin(int margin) {
		this.margin = margin;
		for (Iterator it = groupList.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof ColumnGroup) {
				((ColumnGroup) obj).setColumnMargin(margin);
			}
		}
	}
	
	public List getGroupList() {
		return groupList;
	}
}
