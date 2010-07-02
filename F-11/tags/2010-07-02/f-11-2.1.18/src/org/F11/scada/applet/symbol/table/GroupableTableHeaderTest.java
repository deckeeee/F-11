/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/symbol/table/GroupableTableHeaderTest.java,v 1.2.6.1 2004/12/08 09:18:38 frdm Exp $
 * $Revision: 1.2.6.1 $
 * $Date: 2004/12/08 09:18:38 $
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

import java.awt.Dimension;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import junit.framework.TestCase;

/**
 * GroupableTableHeaderのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GroupableTableHeaderTest extends TestCase {
	JTable table;
	ColumnGroup group;
	ColumnGroup group1;
	TableColumnModel cm;
	GroupableTableHeader header;

	/**
	 * Constructor for GroupableTableHeaderTest.
	 * @param arg0
	 */
	public GroupableTableHeaderTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Object[][] data = {
			{"1.1", "1.2", "2"},
		};
		Object[] title = {"Group1.1", "Group1.2", "Group2"};
		table = new JTable(new DefaultTableModel(data, title));

		cm = table.getColumnModel();
		header = new GroupableTableHeader(cm);
		
		group = new ColumnGroup("Group");
		group1 = new ColumnGroup("Group1");
		group1.add(cm.getColumn(0));
		group1.add(cm.getColumn(1));
		group.add(group1);
	}

	public void testSetReorderingAllowed() {
		header.setReorderingAllowed(false);
		try {
			header.setReorderingAllowed(true);
			fail();
		} catch (IllegalArgumentException ex) {}
	}

	public void testAddColumnGroup() {
		header.addColumnGroup(group);
		assertNotNull(header.getColumnGroups(cm.getColumn(0)));
	}

	public void testSetColumnMargin() {
		header.addColumnGroup(group);
		List old = new LinkedList();
		for (Iterator it = header.getColumnGroups(cm.getColumn(0));
				it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof ColumnGroup) {
				ColumnGroup cg = (ColumnGroup) obj;
				old.add(cg.getSize(table));
			}
		}

		header.getColumnModel().setColumnMargin(2);
		header.setColumnMargin();
		List newV = new LinkedList();
		for (Iterator it = header.getColumnGroups(cm.getColumn(0));
				it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof ColumnGroup) {
				ColumnGroup cg = (ColumnGroup) obj;
				newV.add(cg.getSize(table));
			}
		}

		for (Iterator ito = old.iterator(), itn = newV.iterator();
				ito.hasNext() && itn.hasNext();) {
			Dimension od = (Dimension) ito.next();
			Dimension nd = (Dimension) itn.next();
			od.width += 2;
			assertEquals(od, nd);
		}
	}
}
