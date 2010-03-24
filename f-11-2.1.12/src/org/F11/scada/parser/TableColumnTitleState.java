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

import java.awt.Component;
import java.awt.Font;
import java.util.Stack;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.F11.scada.applet.symbol.TableSymbol;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=page/table/column/titleを表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableColumnTitleState implements State {
	private static Logger logger;
	private static volatile int columnCount;

	/**
	 * 状態オブジェクトを生成します。
	 */
	public TableColumnTitleState(String tagName, Attributes atts, TableColumnState state) {
		logger = Logger.getLogger(getClass().getName());

		if ((atts.getValue("value") != null) && (atts.getValue("width") != null)) {
			//			logger.debug("Title:" + atts.getValue("value") + " width:" + atts.getValue("width"));
			((TableSymbol) state.state.listTable.getModel()).addColumnTitle(atts.getValue("value"));
			state.width.add(atts.getValue("width"));
		} else {
			throw new IllegalArgumentException("null para value or width");
		}

		final String fontname = atts.getValue("font");
		String fontstyle = atts.getValue("font_style");
		String fontsize = atts.getValue("font_size");
		if (fontname != null && fontstyle != null && fontsize != null) {
			final int size = Integer.parseInt(fontsize);
			try {
				java.lang.reflect.Field field = Font.class.getField(fontstyle.toUpperCase());
				final int style = ((Integer) field.get(null)).intValue();

				state.renderer.add(new DefaultTableCellRenderer() {
					private static final long serialVersionUID = 8633690680584900711L;

					public Component getTableCellRendererComponent(
						JTable table,
						Object value,
						boolean isSelected,
						boolean hasFocus,
						int row,
						int column) {
						setFont(new Font(fontname, style, size));
						setHorizontalAlignment(JLabel.CENTER);
						setText((value == null) ? "" : value.toString());
						setBorder(UIManager.getBorder("TableHeader.cellBorder"));
						return this;
					}
				});
			} catch (Exception e) {
				throw new IllegalArgumentException("error para font");
			}
		} else {
			throw new IllegalArgumentException("null para font");
		}
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("title")) {
			stack.pop();
			columnCount++;
		} else {
			logger.info("tagName:" + tagName);
		}
	}

	/**
	 * タイトルの列順を返します。
	 * @return タイトルの列順
	 */
	public static int getColumnCount() {
		return columnCount;
	}

	/**
	 * タイトルの列順をリセットします。
	 */
	public static void resetColumnCount() {
		columnCount = 0;
	}
}
