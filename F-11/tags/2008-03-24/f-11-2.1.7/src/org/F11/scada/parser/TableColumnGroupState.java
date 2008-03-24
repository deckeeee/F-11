/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/TableColumnGroupState.java,v 1.4.4.2 2006/02/16 04:59:00 frdm Exp $
 * $Revision: 1.4.4.2 $
 * $Date: 2006/02/16 04:59:00 $
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

import java.awt.Component;
import java.awt.Font;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableColumnGroupState implements State {
	private static Logger logger;

	private TableColumnState state;

	private ColumnGroups columnGroups;

	/**
	 * Constructor for TableColumnGroupState.
	 */
	public TableColumnGroupState(String tagName, Attributes atts, TableColumnState state) {

		logger = Logger.getLogger(getClass().getName());
		this.state = state;

		String text = atts.getValue("value");
		if (text == null) {
			throw new IllegalArgumentException("Attribute `value' is null.");
		}

		final String fontname = atts.getValue("font");
		String fontstyle = atts.getValue("font_style");
		String fontsize = atts.getValue("font_size");
		DefaultTableCellRenderer renderer = null;
		if (fontname != null && fontstyle != null && fontsize != null) {
			final int size = Integer.parseInt(fontsize);
			try {
				java.lang.reflect.Field field = Font.class.getField(fontstyle.toUpperCase());
				final int style = ((Integer) field.get(null)).intValue();

				renderer = new DefaultTableCellRenderer() {
					private static final long serialVersionUID = 6231561189376085898L;

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
							setFont(new Font(fontname, style, size));
						}
						setHorizontalAlignment(JLabel.CENTER);
						setText((value == null) ? "" : value.toString());
						setBorder(UIManager.getBorder("TableHeader.cellBorder"));
						return this;
					}
				};
			} catch (Exception e) {
				throw new IllegalArgumentException("error para font");
			}
		} else {
			throw new IllegalArgumentException("null para font");
		}

		columnGroups = new ColumnGroups(renderer, text);
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("columngroup")) {
			stack.push(new TableColumnGroupState(tagName, atts, state));
		} else if (tagName.equals("title")) {
			stack.push(new TableColumnTitleState(tagName, atts, state));
			logger.debug("getColumnCount:" + TableColumnTitleState.getColumnCount());
			columnGroups.add(new Integer(TableColumnTitleState.getColumnCount()));
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
		if (tagName.equals("columngroup")) {
			state.columnGroups.add(columnGroups);
			stack.pop();
		} else {
			logger.info("tagName:" + tagName);
		}
	}
}
