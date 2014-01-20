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
package org.F11.scada.parser.tree;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.TestCase;

import org.F11.scada.xwife.applet.PageTreeNode;
import org.apache.commons.digester.Digester;

public class MenuTreeDefineHandlerTest extends TestCase {

	/**
	 * Constructor for PageTimeoutTest.
	 * @param arg0
	 */
	public MenuTreeDefineHandlerTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParse() throws Exception {
		MenuTreeDefineHandler handler = new MenuTreeDefineHandler();

		Digester digester = new Digester();
		digester.push(handler);
		handler.addPageRule(digester);
		File file = new File("src/org/F11/scada/parser/tree/tree_test1.xml");
		BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(file));
		digester.parse(stream);
		stream.close();

		assertEquals("page111", handler.getTreeDefine().getInitPage());
		DefaultMutableTreeNode node0 = (DefaultMutableTreeNode) handler
				.getTreeDefine().getRootNode();
		assertEquals(4, node0.getChildCount());
		PageTreeNode pageNode = (PageTreeNode) node0.getUserObject();
		assertEquals("name000", pageNode.toString());
		assertEquals("", pageNode.getKey());

		DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) node0
				.getFirstChild();
		assertEquals(2, node1.getChildCount());
		pageNode = (PageTreeNode) node1.getUserObject();
		assertEquals("name100", pageNode.toString());
		assertEquals("", pageNode.getKey());

		DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) node1
				.getFirstChild();
		assertEquals(0, node2.getChildCount());
		pageNode = (PageTreeNode) node2.getUserObject();
		assertEquals("name101", pageNode.toString());
		assertEquals("page101", pageNode.getKey());

		node2 = (DefaultMutableTreeNode) node1.getChildAfter(node2);
		assertEquals(0, node2.getChildCount());
		pageNode = (PageTreeNode) node2.getUserObject();
		assertEquals("name102", pageNode.toString());
		assertEquals("page102", pageNode.getKey());
		assertNull(node1.getChildAfter(node2));

		node1 = (DefaultMutableTreeNode) node0.getChildAfter(node1);
		assertEquals(1, node1.getChildCount());
		pageNode = (PageTreeNode) node1.getUserObject();
		assertEquals("name200", pageNode.toString());
		assertEquals("", pageNode.getKey());

		node2 = (DefaultMutableTreeNode) node1.getFirstChild();
		assertEquals(0, node2.getChildCount());
		pageNode = (PageTreeNode) node2.getUserObject();
		assertEquals("name201", pageNode.toString());
		assertEquals("page201", pageNode.getKey());
		assertNull(node1.getChildAfter(node2));

		node1 = (DefaultMutableTreeNode) node0.getChildAfter(node1);
		assertEquals(0, node1.getChildCount());
		pageNode = (PageTreeNode) node1.getUserObject();
		assertEquals("name301", pageNode.toString());
		assertEquals("page301", pageNode.getKey());

		node1 = (DefaultMutableTreeNode) node0.getChildAfter(node1);
		assertEquals(2, node1.getChildCount());
		pageNode = (PageTreeNode) node1.getUserObject();
		assertEquals("name400", pageNode.toString());
		assertEquals("", pageNode.getKey());

		node2 = (DefaultMutableTreeNode) node1.getFirstChild();
		assertEquals(1, node2.getChildCount());
		pageNode = (PageTreeNode) node2.getUserObject();
		assertEquals("name410", pageNode.toString());
		assertEquals("", pageNode.getKey());

		DefaultMutableTreeNode node3 = (DefaultMutableTreeNode) node2
				.getFirstChild();
		assertEquals(0, node3.getChildCount());
		pageNode = (PageTreeNode) node3.getUserObject();
		assertEquals("name411", pageNode.toString());
		assertEquals("page411", pageNode.getKey());
		assertNull(node2.getChildAfter(node3));

		node2 = (DefaultMutableTreeNode) node1.getChildAfter(node2);
		assertEquals(1, node2.getChildCount());
		pageNode = (PageTreeNode) node2.getUserObject();
		assertEquals("name420", pageNode.toString());
		assertEquals("", pageNode.getKey());

		node3 = (DefaultMutableTreeNode) node2.getFirstChild();
		assertEquals(0, node3.getChildCount());
		pageNode = (PageTreeNode) node3.getUserObject();
		assertEquals("name421", pageNode.toString());
		assertEquals("page421", pageNode.getKey());
		assertNull(node2.getChildAfter(node3));
		assertNull(node1.getChildAfter(node2));
		assertNull(node0.getChildAfter(node1));
	}
}
