/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/TableState.java,v 1.9.4.2 2007/07/05 06:06:19 frdm Exp $
 * $Revision: 1.9.4.2 $
 * $Date: 2007/07/05 06:06:19 $
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
package org.F11.scada.parser;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.ListTable;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TableSymbol;
import org.F11.scada.applet.symbol.table.ColumnGroup;
import org.F11.scada.applet.symbol.table.GroupableTableHeader;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/table 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableState implements State {
	private static Logger logger;

	PageState pageState;

	SymbolProperty symbolProperty;
	ListTable listTable;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TableState(String tagName, Attributes atts, PageState pageState1) {

		this.pageState = pageState1;
		logger = Logger.getLogger(getClass().getName());

		listTable = new ListTable(new TableSymbol());

		symbolProperty = new SymbolProperty();
		Color color = ColorFactory.getColor(atts.getValue("foreground"));
		if (color != null) {
			// logger.debug("Atts:" + atts.getValue("foreground"));
			listTable.setForeground(color);
			symbolProperty.setProperty("foreground", atts
					.getValue("foreground"));
		}
		color = ColorFactory.getColor(atts.getValue("background"));
		if (color != null) {
			// logger.debug("Atts:" + atts.getValue("background"));
			listTable.setBackground(color);
			symbolProperty.setProperty("background", atts
					.getValue("background"));
		}
		color = ColorFactory.getColor(atts.getValue("header_foreground"));
		if (color != null) {
			// logger.debug("Atts:" +
			// atts.getValue("header_foreground"));
			listTable.getTableHeader().setForeground(color);
		}
		color = ColorFactory.getColor(atts.getValue("header_background"));
		if (color != null) {
			// logger.debug("Atts:" +
			// atts.getValue("header_background"));
			listTable.getTableHeader().setBackground(color);
		}
		listTable.setColumnSelectionAllowed(false);
		listTable.setRowSelectionAllowed(false);

		JScrollPane scroll = new JScrollPane(listTable);
		String loc_x = atts.getValue("x");
		String loc_y = atts.getValue("y");
		if (loc_x != null && loc_y != null) {
			// logger.debug(
			// "Atts:" + atts.getValue("x") + " " + atts.getValue("y"));
			scroll
					.setLocation(Integer.parseInt(loc_x), Integer
							.parseInt(loc_y));
		}
		loc_x = atts.getValue("width");
		loc_y = atts.getValue("height");
		if (loc_x != null && loc_y != null) {
			// logger.debug("Atts:" + loc_x + " " + loc_y);
			scroll.setSize(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		}

		pageState.basePage.addPageSymbol(scroll);
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("column")) {
			stack.push(new TableColumnState(tagName, atts, this));
		} else if (tagName.equals("data")) {
			stack.push(new TableDataState(tagName, atts, this));
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
		if (tagName.equals("table")) {
			if (logger.isDebugEnabled()) {
				logger.debug("TableState------------------------------------");
				DisplayState.displayColumnGroup(listTable);

				GroupableTableHeader header = (GroupableTableHeader) listTable
						.getTableHeader();
				List list = header.getColumnGroups();
				if (list != null) {
					for (Iterator it = list.iterator(); it.hasNext();) {
						ColumnGroup group = (ColumnGroup) it.next();
						logger.debug(group.getHeaderValue() + "--------------");
						for (Iterator it2 = group.getGroupList().iterator(); it2
								.hasNext();) {
							logger.debug(it2.next());
						}
					}
				}
			}

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int max_h = 16;
					for (int r = 0; r < listTable.getRowCount(); r++) {
						for (int c = 0; c < listTable.getColumnCount(); c++) {
							Object o = listTable.getValueAt(r, c);
							if (!(o instanceof JLabel))
								continue;
							Dimension d = ((JLabel) o).getPreferredSize();
							if (max_h < d.height)
								max_h = d.height;
						}
					}
					listTable.setRowHeight(max_h);
				}
			});
			stack.pop();
		} else {
			logger.info("tagName:" + tagName);
		}
	}
}
