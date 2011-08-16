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

package org.F11.scada.util;

import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageHistoryImplTest extends TestCase {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(PageHistoryImplTest.class);
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

	/**
	 * Constructor for PageHistoryImplTest.
	 * 
	 * @param name
	 */
	public PageHistoryImplTest(String name) {
		super(name);
	}

	public void testCapacity() throws Exception {
		PageHistory ph = new PageHistoryImpl(3);
		for (int i = 0; i < 5; i++) {
			ph.set("page" + i);
		}
		assertEquals("page3", ph.previous());
		assertEquals("page2", ph.previous());
		assertEquals(PageHistory.EOP, ph.previous());
		assertEquals("page3", ph.next());
		assertEquals("page4", ph.next());
		assertEquals(PageHistory.EOP, ph.next());
	}

	public void testSet() throws Exception {
		PageHistory ph = new PageHistoryImpl();
		assertEquals(PageHistory.EOP, ph.previous());
		ph.set("page1");
		assertEquals(PageHistory.EOP, ph.previous());
		ph.set("page1");
		ph.set("page2");
		ph.set("page3");
		assertEquals("page2", ph.previous());
		assertEquals("page1", ph.previous());
		assertEquals("page1", ph.previous());
		assertEquals(PageHistory.EOP, ph.previous());
		ph.set("page1");
		ph.set("page2");
		ph.set("page3");
		assertEquals("page2", ph.previous());
		assertEquals("page1", ph.previous());
		assertEquals("page2", ph.next());
		ph.set("page4");
		assertEquals("page2", ph.previous());
		assertEquals("page4", ph.next());
		assertEquals(PageHistory.EOP, ph.next());
	}

	public void testPageHistory() throws Exception {
		PageHistory ph = new PageHistoryImpl();

		assertEquals(PageHistory.EOP, ph.next());
		assertEquals(PageHistory.EOP, ph.previous());

		ph.set("page1");
		ph.set("page2");
		ph.set("page3");
		assertEquals("page2", ph.previous());
		assertEquals("page1", ph.previous());
		assertEquals(PageHistory.EOP, ph.previous());
		assertEquals("page2", ph.next());
		ph.set("table1");
		ph.set("page1");
		assertEquals("table1", ph.previous());
		assertEquals("page2", ph.previous());
		assertEquals("page1", ph.previous());
		assertEquals(PageHistory.EOP, ph.previous());
		assertEquals("page2", ph.next());
		assertEquals("table1", ph.next());
		assertEquals("page1", ph.next());
		assertEquals(PageHistory.EOP, ph.next());
	}

	public void testPreviousToSet() throws Exception {
		PageHistory ph = new PageHistoryImpl();
		ph.set("page1");
		ph.set("page2");
		assertEquals("page1", ph.previous());
		ph.set("page2");
		assertEquals("page1", ph.previous());
	}

	public void testPreviousToForword() throws Exception {
		PageHistory ph = new PageHistoryImpl();
		ph.set("page1");
		ph.set("page2");

		assertEquals("page1", ph.previous());
		assertEquals("page2", ph.next());
		assertEquals("page1", ph.previous());

	}
}
