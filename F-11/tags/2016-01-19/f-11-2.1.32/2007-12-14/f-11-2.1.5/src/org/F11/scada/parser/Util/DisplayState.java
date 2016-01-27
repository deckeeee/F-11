/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/Util/DisplayState.java,v 1.4 2003/03/05 02:33:46 frdm Exp $
 * $Revision: 1.4 $
 * $Date: 2003/03/05 02:33:46 $
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
package org.F11.scada.parser.Util;

import java.util.Iterator;
import java.util.Stack;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.F11.scada.applet.symbol.table.ColumnGroup;
import org.F11.scada.applet.symbol.table.GroupableTableHeader;
import org.apache.log4j.Logger;

/**
 * 状態を整形して表示する為のユーティリティークラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class DisplayState {
	private static final Logger logger = Logger.getLogger(DisplayState.class);

	/**
	 * プライベートコンストラクタです。
	 * このオブジェクトのインスタンスを生成することはできません。
	 */
	private DisplayState() {}

	/**
	 * イベントが発生したタグ名称と、状態スタックトレースを文字列化します。
	 */
	public static String toString(String tagName, Stack stack) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("TagName : ").append(tagName).append("\n");
		
		for (int i = 0, s = stack.size(); i < s; i++) {
			buffer.append("    ").append(stack.get(i).getClass().getName()).append("\n");
		}
		return buffer.toString();
	}
	
	public static void displayColumnGroup(JTable listTable) {
		GroupableTableHeader header =
			(GroupableTableHeader) listTable.getTableHeader();
		TableColumnModel cm = listTable.getColumnModel();
		logger.debug("ColumnCount:" + cm.getColumnCount());

		for (int i = 0, count = listTable.getColumnCount();
				i < count; i++) {
			logger.debug("Column:" + cm.getColumn(i));
			Iterator it = header.getColumnGroups(cm.getColumn(i));
			if (it != null) {
				while (it.hasNext()) {
					Object obj = it.next();
					if (obj instanceof ColumnGroup) {
						ColumnGroup cg = (ColumnGroup) obj;
						logger.debug("ColumnGroup:" + cg + " text:" + cg.getHeaderValue());
					}
				}
			}
		}
	}

}
