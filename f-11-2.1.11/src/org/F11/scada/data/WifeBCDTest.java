/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeBCDTest.java,v 1.4 2003/02/05 06:52:06 frdm Exp $
 * $Revision: 1.4 $
 * $Date: 2003/02/05 06:52:06 $
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

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * WifeBCDのテストケースクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeBCDTest extends TestCase {
	private byte[] bytes_00000001;
	private byte[] bytes_01234567;
	private byte[] bytes_err;

	public WifeBCDTest(String name) {
		super(name);
	}
	
	protected void setUp() {
		bytes_00000001 = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01};
		bytes_01234567 = new byte[]{(byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67};
		bytes_err = new byte[]{(byte)0xA1, (byte)0x23, (byte)0x45, (byte)0x67};
	}
	
	public void testToString() {
		assertEquals("00000001", WifeBCD.toString(bytes_00000001));
		assertEquals("01234567", WifeBCD.toString(bytes_01234567));
		try {
			WifeBCD.toString(bytes_err);
			fail();
		} catch (BCDConvertException ex) {
		}
	}
	
	public void testValueOf() {
		assertEquals(1D, WifeBCD.valueOf(bytes_00000001), 0D);
		assertEquals(1234567D, WifeBCD.valueOf(bytes_01234567), 0D);
	}
	
	public void testToByteArray() {
		assertTrue(Arrays.equals(bytes_00000001, WifeBCD.toByteArray("00000001")));
		assertTrue(Arrays.equals(bytes_01234567, WifeBCD.toByteArray("01234567")));
	}
}
