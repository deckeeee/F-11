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
 * �O���[�s���O�\�ȃe�[�u���w�b�_�N���X�ł��B
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
	/** ���̃N���X�̃��b�N���t�B�[��ID�ł� */
//	private static final String uiClassID = "GroupableTableHeaderUI";
	/** ColumnGroup�̃��X�g�ł��B */
	private List columnGroups;

	/**
	 * �R���X�g���N�^
	 * �O���[�s���O�\�ȃe�[�u���w�b�_�I�u�W�F�N�g�𐶐����܂��B
	 * @param model �e�[�u���񃂃f���I�u�W�F�N�g
	 */
	public GroupableTableHeader(TableColumnModel model) {
		super(model);
		setUI(new GroupableTableHeaderUI());
		setReorderingAllowed(false);
	}

	/**
	 * ���[�U���w�b�_���h���b�O���ė�̔z�u�̂�蒼���͋֎~����Ă��܂��B
	 * true ��ݒ肷��ƕK���AIllegalArgumentException���X���[���܂��B
	 * @exception IllegalArgumentException true ��ݒ肷��ƕK���X���[���܂��B
	 */
	public void setReorderingAllowed(boolean b) {
		if (b == true) {
			throw new IllegalArgumentException("Unsupport true opration.");
		} else {
			reorderingAllowed = b;
		}
	}

	/**
	 * ColumnGroup��ǉ����܂��B
	 * @param g ColumnGroup�I�u�W�F�N�g
	 */
	public void addColumnGroup(ColumnGroup g) {
		if (columnGroups == null) {
			columnGroups = new LinkedList();
		}
		columnGroups.add(g);
	}

	/**
	 * ColumnGroup�̔����q��Ԃ��܂��B
	 * @param col ���o����TableColumn
	 * @return ColumnGroup�̔����q
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
	
//�e�X�g�p���\�b�h
	public List getColumnGroups() {
		return columnGroups;
	}

	/**
	 * ��}�[�W����ݒ肵�܂��B
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
		dm.addColumnTitle("�L��");
		dm.addColumnTitle("�ώZ����");
		dm.addColumnTitle("����");
		dm.addColumnTitle("���");
		dm.addColumnTitle("�P��");
		dm.addColumnTitle("�{��");
		dm.addColumnTitle("�O��");
		dm.addColumnTitle("�{��");
		dm.addColumnTitle("�O��");

		TableColumnModel cm = table.getColumnModel();
		ColumnGroup hour = new ColumnGroup("���Ԏg�p��");
		hour.add(cm.getColumn(3));
		hour.add(cm.getColumn(4));
		ColumnGroup daily = new ColumnGroup("���g�p��");
		daily.add(cm.getColumn(6));
		daily.add(cm.getColumn(7));
		ColumnGroup month = new ColumnGroup("���g�p��");
		month.add(cm.getColumn(8));
		month.add(cm.getColumn(9));
/*
		ColumnGroup titleColumn = new ColumnGroup("�ώZ�ꗗ�\");
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
