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
 * グループ化テーブルヘッダのエレメントクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColumnGroup {
	/** テーブルヘッダーセルレンダラーの参照 */
	private TableCellRenderer renderer;
	/** ColumnGroup と TableColumn オブジェクトのリスト */
	private List groupList;
	/** ヘッダーに表示するテキスト */
	private String text;
	/** ヘッダ間のマージン */
	private int margin;

	/**
	 * デフォルトのヘッダレンダラーで、ColumnGroupオブジェクトを生成します。
	 * ColumnGroupは入れ子にすることが可能で、描画されるヘッダも入れ子になります。
	 * @param text ヘッダに表示するテキスト
	 */
	public ColumnGroup(String text) {
		this(null, text);
	}

	/**
	 * ヘッダのレンダラーを指定して、ColumnGroupオブジェクトを生成します。
	 * ColumnGroupは入れ子にすることが可能で、描画されるヘッダも入れ子になります。
	 * @param renderer ヘッダのレンダラーオブジェクト
	 * @param text ヘッダに表示するテキスト
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
	 * ColumnGroupを子供に追加します。
	 * @param obj ColumnGroupオブジェクト
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
	 * TableColumnを子供に追加します。
	 * @param obj TableColumnオブジェクト
	 */
	public void add(TableColumn obj) {
		if (obj == null) {
			throw new IllegalArgumentException("TableColumn is null Object.");
		}
		groupList.add(obj);
	}

	/**
	 * 引数のTableColumnを含む、階層化したColumnGroupのリストオブジェクトを返します。
	 * @param c TableColumn
	 * @param g ColumnGroups
	 * @return 引数のTableColumnが存在した場合、階層化したColumnGroupのリストオブジェクト。存在しない場合は null を返します。
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
	 * TableCellRendererオブジェクトを返します。
	 * @return TableCellRendererオブジェクト
	 */
	public TableCellRenderer getHeaderRenderer() {
		return renderer;
	}

	/**
	 * TableCellRendererを設定します。
	 * @param renderer TableCellRendererオブジェクト
	 */
	public void setHeaderRenderer(TableCellRenderer renderer) {
		if (renderer != null) {
			this.renderer = renderer;
		}
	}

	/**
	 * ヘッダ値（タイトル名）を返します。
	 * @return ヘッダ値（タイトル名）
	 */
	public Object getHeaderValue() {
		return text;
	}

	/**
	 * この列のサイズを返します。
	 * @param table ヘッダが属しているテーブル
	 * @return Dimensionオブジェクト
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
	 * 子の列ヘッダ間のマージンを設定します。
	 * @param margin 子の列ヘッダ間のマージン
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
