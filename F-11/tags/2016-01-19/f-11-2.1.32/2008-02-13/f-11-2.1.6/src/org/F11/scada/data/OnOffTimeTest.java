/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/OnOffTimeTest.java,v 1.3.2.2 2005/07/06 02:20:44 frdm Exp $
 * $Revision: 1.3.2.2 $
 * $Date: 2005/07/06 02:20:44 $
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
package org.F11.scada.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * OnOffTimeのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class OnOffTimeTest extends TestCase {
	OnOffTime onOffTime;
	OnOffTime destonOffTime;

	/**
	 * Constructor for OnOffTimeTest.
	 * @param arg0
	 */
	public OnOffTimeTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		onOffTime = new OnOffTime(1122, 3344);
		destonOffTime = new OnOffTime(1122, 3344);
	}
	
	// コンストラクタのテスト
	public void testOnOffTime() {
		try {
			new OnOffTime(-1, 0);
			fail();
		} catch (IllegalArgumentException ex) {}

		try { 
			new OnOffTime(0, -1);
			fail();
		} catch (IllegalArgumentException ex) {}
	}

	public void testHashCode() {
		assertEquals(destonOffTime.hashCode(), onOffTime.hashCode());
	}

	public void testGetOnTime() {
		assertEquals(1122, onOffTime.getOnTime());
	}

	public void testGetOffTime() {
		assertEquals(3344, onOffTime.getOffTime());
	}

	public void testSetOnTime() {
		assertEquals(5566, onOffTime.setOnTime(5566).getOnTime());
		try {
			onOffTime.setOnTime(-1);
			fail();
		} catch (IllegalArgumentException ex) {}
	}

	public void testSetOffTime() {
		assertEquals(7788, onOffTime.setOffTime(7788).getOffTime());
		try {
			onOffTime.setOffTime(-1);
			fail();
		} catch (IllegalArgumentException ex) {}
	}

	/*
	 * String toString のテスト()
	 */
	public void testToString() {
//		System.out.println(onOffTime.toString());
		assertEquals("1122:3344", onOffTime.toString());
	}

	/*
	 * boolean equals のテスト(Object)
	 */
	public void testEqualsObject() {
		assertEquals(destonOffTime, onOffTime);
	}

	public void testValueOf() {
		byte[] b = new byte[]{(byte)0x11, (byte)0x22, (byte)0x33, (byte)0x44};
		OnOffTime on = onOffTime.valueOf(b);
		assertEquals(onOffTime, on);
		try {
			b = null;
			onOffTime.valueOf(b);
			fail();
		} catch (IllegalArgumentException ex) {}
		try {
			b = new byte[]{(byte)0x11};
			onOffTime.valueOf(b);
			fail();
		} catch (IllegalArgumentException ex) {}
		try {
			b = new byte[]{(byte)0x11, (byte)0x22, (byte)0x33, (byte)0x44, (byte)0x55};
			onOffTime.valueOf(b);
			fail();
		} catch (IllegalArgumentException ex) {}
	}

	public void testToByteArray() {
		byte[] bd = new byte[]{(byte)0x11, (byte)0x22, (byte)0x33, (byte)0x44};
		byte[] b = onOffTime.toByteArray();
		assertTrue(Arrays.equals(b, bd));
	}

	public void testGetWordSize() {
		assertEquals(2, onOffTime.getWordSize());
	}

	public void testReadResolve() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(onOffTime);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		OnOffTime d = (OnOffTime) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(d, onOffTime);
		assertTrue(d.equals(onOffTime));
		temp.delete();
	}

}
