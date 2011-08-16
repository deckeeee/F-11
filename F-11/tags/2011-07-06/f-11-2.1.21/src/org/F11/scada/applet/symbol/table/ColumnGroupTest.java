/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/symbol/table/ColumnGroupTest.java,v 1.2.6.1 2004/12/08 09:18:38 frdm Exp $
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
import java.util.LinkedList;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import junit.framework.TestCase;

/**
 * ColumnGroupのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ColumnGroupTest extends TestCase {
	JTable table;
	ColumnGroup group;
	ColumnGroup group1;
	TableColumnModel cm;

	/**
	 * Constructor for ColumnGroupTest.
	 * @param arg0
	 */
	public ColumnGroupTest(String arg0) {
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
		group = new ColumnGroup("Group");
		group1 = new ColumnGroup("Group1");
		group1.add(cm.getColumn(0));
		group1.add(cm.getColumn(1));
	}

	/*
	 * void add のテスト
	 */
	public void testAddColumnGroup() {
		group.add(group1);
		assertNotNull(group.getColumnGroups(cm.getColumn(0), new LinkedList()));
		assertNull(group.getColumnGroups(cm.getColumn(2), new LinkedList()));
	}

	public void testGetHeaderRenderer() {
		assertNotNull(group.getHeaderRenderer());
		assertTrue((group.getHeaderRenderer() instanceof DefaultTableCellRenderer));
	}

	public void testSetHeaderRenderer() {
		DefaultTableCellRenderer ren = new DefaultTableCellRenderer();
		group.setHeaderRenderer(ren);
		assertSame(ren, group.getHeaderRenderer());
	}

	public void testGetHeaderValue() {
		assertEquals("Group", group.getHeaderValue());
	}

	public void testGetSize() {
		assertEquals(new Dimension(0, 16), group.getSize(table));
	}

	public void testSetColumnMargin() {
		group.add(group1);
		assertEquals(new Dimension(150, 16), group.getSize(table));
		group1.setColumnMargin(100);
		assertEquals(new Dimension(150 + 2 * 100, 16), group.getSize(table));
	}

}
