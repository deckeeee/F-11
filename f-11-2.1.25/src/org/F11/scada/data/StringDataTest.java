/*
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

import java.util.Arrays;

import junit.framework.TestCase;

public class StringDataTest extends TestCase {
	public StringDataTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testValueOfString() throws Exception {
		StringData d = StringData.valueOf("0123456789");
		assertTrue(Arrays.equals(new byte[] { (byte) 0x30, (byte) 0x31,
				(byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35,
				(byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39 }, d
				.toByteArray()));
		assertEquals(5, d.getWordSize());
		d = StringData.valueOf("aZ");
		assertTrue(Arrays.equals(new byte[] { (byte) 0x61, (byte) 0x5a }, d
				.toByteArray()));
		assertEquals(1, d.getWordSize());
	}

	public void testValueOfByte() throws Exception {
		StringData d = StringData.valueOf();
		d = (StringData) d.valueOf(new byte[] { (byte) 0x30, (byte) 0x31,
				(byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35 });
		assertEquals("012345", d.toString());
		assertEquals(3, d.getWordSize());
	}

	public void testEquals() throws Exception {
		StringData d = StringData.valueOf("0123456789");
		assertEquals(StringData.valueOf("0123456789"), d);
		assertEquals(StringData.valueOf("0123456789").hashCode(), d.hashCode());
		assertFalse(StringData.valueOf("012345678").equals(d));
		assertFalse(StringData.valueOf("012345678").hashCode() == d.hashCode());
	}

	/**
	 * 文字列以外のバイト配列の入力テスト
	 * 
	 * @throws Exception
	 */
	public void testIllegalBytes() throws Exception {
		StringData d = StringData.valueOf();
		d = (StringData) d.valueOf(new byte[] { (byte) 0x30, (byte) 0x19,
				(byte) 0x32, (byte) 0x33 });
		assertEquals("0", d.toString());
		assertEquals(2, d.getWordSize());

		d = (StringData) d.valueOf(new byte[] { (byte) 0x30, (byte) 0x20,
				(byte) 0x32, (byte) 0x33 });
		assertEquals("0 23", d.toString());
		assertEquals(2, d.getWordSize());

		d = (StringData) d.valueOf(new byte[] { (byte) 0x30, (byte) 0x31,
				(byte) 0x7F, (byte) 0x33 });
		assertEquals("01", d.toString());
		assertEquals(2, d.getWordSize());
	}

	public void testSetNullBytes() throws Exception {
		StringData d = StringData.valueOf();
		byte[] b = null;
		d = (StringData) d.valueOf(b);
		assertEquals("", d.toString());
		assertEquals(0, d.getWordSize());
	}
}
