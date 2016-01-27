/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/WifeCommandTest.java,v 1.8.4.3 2004/12/24 05:28:08 frdm Exp $
 * $Revision: 1.8.4.3 $
 * $Date: 2004/12/24 05:28:08 $
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
package org.F11.scada.server.event;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.F11.scada.server.entity.Item;

/**
 * WifeCommandクラスのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeCommandTest extends TestCase {
	WifeCommand rcom;
	WifeCommand wcom;
	WifeCommand nullCom;
	WifeCommand rcom2;

	/**
	 * Constructor for WifeCommandTest.
	 * @param arg0
	 */
	public WifeCommandTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		rcom = new WifeCommand("P1", 1000, 0, 0, 2000, 10);

		wcom = new WifeCommand("P1", 1000, 1, 0, 2000, 1);
		nullCom = WifeCommand.getNullCommand();
		Item item = new Item();
		item.setProvider("P1");
		item.setComCycle(1000);
		item.setComCycleMode(true);
		item.setComMemoryKinds(0);
		item.setComMemoryAddress(2000);
		item.setDataArgv("0");
		rcom2 = new WifeCommand(item);
	}

	/*
	 * void WifeCommand のテスト(String, int, int, int, long, int, byte[])
	 */
	public void testWifeCommandStringIIIJIBArray() {
		try {
			new WifeCommand(null, 0, 0, 0, 2000, 1);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetDeviceID() {
		assertEquals("P1", rcom.getDeviceID());
		assertEquals("P1", wcom.getDeviceID());
		assertEquals("NullWifeCommand", nullCom.getDeviceID());
	}

	public void testGetReadWriteMode() {
		assertEquals(0, rcom.getCycleMode());
		assertEquals(1, wcom.getCycleMode());
		assertEquals(0, nullCom.getCycleMode());
	}

	public void testGetMemoryMode() {
		assertEquals(0, rcom.getMemoryMode());
		assertEquals(0, wcom.getMemoryMode());
		assertEquals(0, nullCom.getMemoryMode());
	}

	public void testGetMemoryAddress() {
		assertEquals(2000, rcom.getMemoryAddress());
		assertEquals(2000, wcom.getMemoryAddress());
		assertEquals(0, nullCom.getMemoryAddress());
	}

	public void testGetWordLength() {
		assertEquals(10, rcom.getWordLength());
		assertEquals(1, wcom.getWordLength());
		assertEquals(0, nullCom.getWordLength());
	}

	public void testComparator() {
		SortedSet sset = new TreeSet(WifeCommand.comp);
		sset.add(new WifeCommand("P1", 1000, 0, 0, 4500, 1));
		sset.add(new WifeCommand("P1", 1000, 0, 0, 3500, 3));
		sset.add(new WifeCommand("P1", 1000, 0, 0, 4000, 2));
		sset.add(new WifeCommand("P1", 1000, 0, 0, 3000, 4));

		Iterator it = sset.iterator();
		WifeCommand wc = (WifeCommand) it.next();
		assertEquals(3000, wc.getMemoryAddress());
		assertEquals(4, wc.getWordLength());
		wc = (WifeCommand) it.next();
		assertEquals(3500, wc.getMemoryAddress());
		assertEquals(3, wc.getWordLength());
		wc = (WifeCommand) it.next();
		assertEquals(4000, wc.getMemoryAddress());
		assertEquals(2, wc.getWordLength());
		wc = (WifeCommand) it.next();
		assertEquals(4500, wc.getMemoryAddress());
		assertEquals(1, wc.getWordLength());
	}
	
	public void testWifeData() throws Exception {
	    assertEquals(new WifeCommand("P1", 1000, 1, 0, 2000, 1), rcom2);
	}
}
