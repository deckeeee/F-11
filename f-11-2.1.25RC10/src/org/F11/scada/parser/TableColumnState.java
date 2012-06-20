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
package org.F11.scada.parser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.F11.scada.applet.symbol.TableSymbol;
import org.F11.scada.applet.symbol.table.ColumnGroup;
import org.F11.scada.applet.symbol.table.GroupableTableHeader;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath="page/table/column" 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableColumnState implements State {
	private static Logger logger;
	TableState state;
	Vector width;
	Vector renderer;
	List columnGroups;

	/**
	 * 状態オブジェクトを生成します。
	 */
	public TableColumnState(String tagName, Attributes atts, TableState state) {
		this.state = state;
		logger = Logger.getLogger(getClass().getName());
		width = new Vector();
		renderer = new Vector();
		columnGroups = new LinkedList();
		TableColumnTitleState.resetColumnCount();
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("title")) {
			stack.push(new TableColumnTitleState(tagName, atts, this));
		} else if (tagName.equals("columngroup")) {
			stack.push(new TableColumnGroupState(tagName, atts, this));
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("column")) {
			TableSymbol tableSymbol = (TableSymbol) state.listTable.getModel();
			tableSymbol.fireTableStructureChanged();

			TableColumnModel dc = state.listTable.getColumnModel();
			for (int i = 0; i < dc.getColumnCount(); i++) {
				TableColumn column = dc.getColumn(i);
				column.setPreferredWidth(Integer.parseInt((String) width.get(i)));
				column.setHeaderRenderer((TableCellRenderer) renderer.get(i));
			}

			// この列グループに対象列を追加する。
			GroupableTableHeader header = (GroupableTableHeader) state.listTable.getTableHeader();
			TableColumnModel cm = state.listTable.getColumnModel();
			for (Iterator it = columnGroups.iterator(); it.hasNext();) {
				ColumnGroups cps = (ColumnGroups) it.next();
				ColumnGroup cg = new ColumnGroup(cps.getTableCellRenderer(), cps.getText());
				for (Iterator intIt = cps.iterator(); intIt.hasNext();) {
					Integer ci = (Integer) intIt.next();
					cg.add(cm.getColumn(ci.intValue()));
				}
				header.addColumnGroup(cg);
			}
			stack.pop();
		} else {
			logger.info("tagName:" + tagName);
		}
	}
}

/**
 * ColumnGroupオブジェクトを生成する情報を保持するヘルパークラスです。
 */
class ColumnGroups {
	String text;
	TableCellRenderer renderer;
	List groupColumn;

	ColumnGroups(TableCellRenderer renderer, String text) {
		this.text = text;
		this.renderer = renderer;
		groupColumn = new LinkedList();
	}

	void add(Integer i) {
		groupColumn.add(i);
	}

	String getText() {
		return text;
	}

	Iterator iterator() {
		return groupColumn.iterator();
	}

	TableCellRenderer getTableCellRenderer() {
		return renderer;
	}
}
