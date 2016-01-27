/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/symbol/table/GroupableTableHeaderUI.java,v 1.1.6.1 2005/03/11 06:50:48 frdm Exp $
 * $Revision: 1.1.6.1 $
 * $Date: 2005/03/11 06:50:48 $
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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * GroupableTableHeade 用のプラグイン可能な Look & Feel インタフェースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GroupableTableHeaderUI extends BasicTableHeaderUI {
	/**
	 * カラムヘッダを画面に描画します。
	 * @param g グラフィックコンテキスト
	 * @param c 親コンポーネント
	 */
	public void paint(Graphics g, JComponent c) {
		Rectangle clipBounds = g.getClipBounds();
		if (header.getColumnModel() == null)
			return;
		((GroupableTableHeader) header).setColumnMargin();
		int column = 0;
		Dimension size = header.getSize();
		Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
		Hashtable h = new Hashtable();
		int columnMargin = header.getColumnModel().getColumnMargin() - 1;

		for (Enumeration enumeration = header.getColumnModel().getColumns();
				enumeration.hasMoreElements();) {
			cellRect.height = size.height;
			cellRect.y = 0;
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			Iterator cGroups =
				((GroupableTableHeader) header).getColumnGroups(aColumn);
			if (cGroups != null) {
				int groupHeight = 0;
				while (cGroups.hasNext()) {
					ColumnGroup cGroup = (ColumnGroup) cGroups.next();
					Rectangle groupRect = (Rectangle) h.get(cGroup);
					if (groupRect == null) {
						groupRect = new Rectangle(cellRect);
						Dimension d = cGroup.getSize(header.getTable());
						groupRect.width = d.width;
						groupRect.height = d.height;
						h.put(cGroup, groupRect);
					}
					paintCell(g, groupRect, cGroup);
					groupHeight += groupRect.height;
					cellRect.height = size.height - groupHeight;
					cellRect.y = groupHeight;
				}
			}
			cellRect.width = aColumn.getWidth() + columnMargin;
			if (cellRect.intersects(clipBounds)) {
				paintCell(g, cellRect, column);
			}
			cellRect.x += cellRect.width;
			column++;
		}
	}

	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
		TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		TableCellRenderer renderer = aColumn.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}
		Component component =
			renderer.getTableCellRendererComponent(
				header.getTable(),
				aColumn.getHeaderValue(),
				false,
				false,
				-1,
				columnIndex);
		rendererPane.add(component);
		rendererPane.paintComponent(
			g,
			component,
			header,
			cellRect.x,
			cellRect.y,
			cellRect.width,
			cellRect.height,
			true);
	}

	private void paintCell(
			Graphics g,
			Rectangle cellRect,
			ColumnGroup cGroup) {
		TableCellRenderer renderer = cGroup.getHeaderRenderer();
		Component component =
			renderer.getTableCellRendererComponent(
				header.getTable(),
				cGroup.getHeaderValue(),
				false,
				false,
				-1,
				-1);
		rendererPane.add(component);
		rendererPane.paintComponent(
			g,
			component,
			header,
			cellRect.x,
			cellRect.y,
			cellRect.width,
			cellRect.height,
			true);
	}

	private int getHeaderHeight() {
		int height = 0;
		TableColumnModel columnModel = header.getColumnModel();
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			TableColumn aColumn = columnModel.getColumn(column);
			TableCellRenderer renderer = aColumn.getHeaderRenderer();
		    if (renderer == null) {
				renderer = header.getDefaultRenderer();
		    }
			Component comp =
				renderer.getTableCellRendererComponent(
					header.getTable(),
					aColumn.getHeaderValue(),
					false,
					false,
					-1,
					column);
			int cHeight = comp.getPreferredSize().height;
			Iterator e =
				((GroupableTableHeader) header).getColumnGroups(aColumn);
			if (e != null) {
				while (e.hasNext()) {
					ColumnGroup cGroup = (ColumnGroup) e.next();
					cHeight += cGroup.getSize(header.getTable()).height;
				}
			}
			height = Math.max(height, cHeight);
		}
		return height;
	}

	private Dimension createHeaderSize(long width) {
		TableColumnModel columnModel = header.getColumnModel();
		width += columnModel.getColumnMargin() * columnModel.getColumnCount();
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, getHeaderHeight());
	}

	/**
	 * このヘッダのサイズを返します。
	 * @param c ダミー引数（メソッド内部では使用していません。）
	 * @return サイズのDimensionオブジェクトを返します。
	 */
	public Dimension getPreferredSize(JComponent c) {
		long width = 0;
		for (Enumeration enumeration = header.getColumnModel().getColumns();
				enumeration.hasMoreElements();) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return createHeaderSize(width);
	}
}
