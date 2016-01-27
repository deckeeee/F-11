/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/symbol/table/GroupableTableHeader.java,v 1.2.6.3 2005/08/11 07:46:35 frdm Exp $
 * $Revision: 1.2.6.3 $
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.F11.scada.applet.symbol.ListTable;
import org.F11.scada.applet.symbol.TableSymbol;
import org.F11.scada.parser.Util.DisplayState;

/**
 * グルーピング可能なテーブルヘッダクラスです。
 * <pre>
 * 
 * Ex.
 * +------------------------+------------------------+
 * |         Group 1        |       Group 2          |
 * +-----------+------------+                        |
 * | Group 1.1 | Group 1.2  |                        |
 * +-----------+------------+------------------------+
 * 
 * 
 * </pre>
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GroupableTableHeader extends JTableHeader {
	private static final long serialVersionUID = 9120040104608476205L;
	/** このクラスのルック＆フィールIDです */
//	private static final String uiClassID = "GroupableTableHeaderUI";
	/** ColumnGroupのリストです。 */
	private List columnGroups;

	/**
	 * コンストラクタ
	 * グルーピング可能なテーブルヘッダオブジェクトを生成します。
	 * @param model テーブル列モデルオブジェクト
	 */
	public GroupableTableHeader(TableColumnModel model) {
		super(model);
		setUI(new GroupableTableHeaderUI());
		setReorderingAllowed(false);
	}

	/**
	 * ユーザがヘッダをドラッグして列の配置のやり直しは禁止されています。
	 * true を設定すると必ず、IllegalArgumentExceptionをスローします。
	 * @exception IllegalArgumentException true を設定すると必ずスローします。
	 */
	public void setReorderingAllowed(boolean b) {
		if (b == true) {
			throw new IllegalArgumentException("Unsupport true opration.");
		} else {
			reorderingAllowed = b;
		}
	}

	/**
	 * ColumnGroupを追加します。
	 * @param g ColumnGroupオブジェクト
	 */
	public void addColumnGroup(ColumnGroup g) {
		if (columnGroups == null) {
			columnGroups = new LinkedList();
		}
		columnGroups.add(g);
	}

	/**
	 * ColumnGroupの反復子を返します。
	 * @param col 抽出するTableColumn
	 * @return ColumnGroupの反復子
	 */
	public Iterator getColumnGroups(TableColumn col) {
		if (columnGroups == null) {
			return null;
		}
		for (Iterator it = columnGroups.iterator();
				it.hasNext();) {
			ColumnGroup cGroup = (ColumnGroup) it.next();
			List v_ret = cGroup.getColumnGroups(col, new LinkedList());
			if (v_ret != null) {
				return v_ret.iterator();
			}
		}
		return null;
	}
	
//テスト用メソッド
	public List getColumnGroups() {
		return columnGroups;
	}

	/**
	 * 列マージンを設定します。
	 */
	public void setColumnMargin() {
		if (columnGroups == null)
			return;
		int columnMargin = getColumnModel().getColumnMargin() - 1;
		for (Iterator it = columnGroups.iterator();
				it.hasNext();) {
			ColumnGroup cGroup = (ColumnGroup) it.next();
			cGroup.setColumnMargin(columnMargin);
		}
	}

	public static void main(String[] args) {
		new GroupableHeaderTest();
	}
}

class GroupableHeaderTest extends JFrame {

	private static final long serialVersionUID = -4971986526080424607L;

	GroupableHeaderTest() {
		super("Groupable Header Example");
		TableSymbol dm = new TableSymbol();
		ListTable table = new ListTable(dm);

		JScrollPane scroll = new JScrollPane(table);
		dm.addColumnTitle("No.");
		dm.addColumnTitle("記号");
		dm.addColumnTitle("積算名称");
		dm.addColumnTitle("現在");
		dm.addColumnTitle("上限");
		dm.addColumnTitle("単位");
		dm.addColumnTitle("本日");
		dm.addColumnTitle("前日");
		dm.addColumnTitle("本月");
		dm.addColumnTitle("前月");

		TableColumnModel cm = table.getColumnModel();
		ColumnGroup hour = new ColumnGroup("時間使用量");
		hour.add(cm.getColumn(3));
		hour.add(cm.getColumn(4));
		ColumnGroup daily = new ColumnGroup("日使用量");
		daily.add(cm.getColumn(6));
		daily.add(cm.getColumn(7));
		ColumnGroup month = new ColumnGroup("月使用量");
		month.add(cm.getColumn(8));
		month.add(cm.getColumn(9));
/*
		ColumnGroup titleColumn = new ColumnGroup("積算一覧表");
		titleColumn.add(cm.getColumn(0));
		titleColumn.add(cm.getColumn(1));
		titleColumn.add(cm.getColumn(2));
		titleColumn.add(hour);
		titleColumn.add(cm.getColumn(5));
		titleColumn.add(daily);
		titleColumn.add(month);
*/
		GroupableTableHeader header =
			(GroupableTableHeader) table.getTableHeader();
		header.addColumnGroup(hour);
		header.addColumnGroup(daily);
		header.addColumnGroup(month);
		getContentPane().add(scroll);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);

		DisplayState.displayColumnGroup(table);

		GroupableTableHeader header1 =
			(GroupableTableHeader) table.getTableHeader();
		List list = header1.getColumnGroups();
		if (list != null) {
			for (Iterator it = list.iterator(); it.hasNext();) {
				ColumnGroup group = (ColumnGroup) it.next();
				System.out.println(group.getHeaderValue() + "--------------");
				for (Iterator it2 = group.getGroupList().iterator(); it2.hasNext();) {
					System.out.println(it2.next());
				}
			}
		}
	}
}
