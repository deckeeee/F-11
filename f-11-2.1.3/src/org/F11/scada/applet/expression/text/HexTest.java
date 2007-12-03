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

package org.F11.scada.applet.expression.text;

import junit.framework.TestCase;

public class HexTest extends TestCase {
	public void testFormat() {
		Hex hex = new Hex("0X4");
		assertEquals("0000", hex.format(0D));
		hex = new Hex("0x4");
		assertEquals("0000", hex.format(0D));

		hex = new Hex("0X4");
		assertEquals("0010", hex.format(16D));
		hex = new Hex("0x4");
		assertEquals("0010", hex.format(16D));

		hex = new Hex("0X4");
		assertEquals("0100", hex.format(256D));
		hex = new Hex("0x4");
		assertEquals("0100", hex.format(256D));

		hex = new Hex("0X4");
		assertEquals("FFFF", hex.format(65535D));
		hex = new Hex("0x4");
		assertEquals("ffff", hex.format(65535D));

		try {
			hex = new Hex("hoge");
			fail("illegal argument.");
		} catch (IllegalArgumentException e) {
		}
		
		try {
			hex = new Hex("0xGGG");
			fail("illegal argument.");
		} catch (NumberFormatException e) {
		}
	}
	
	public void testFormatOverLength() throws Exception {
		Hex hex = new Hex("0X2");
		assertEquals("00", hex.format(0D));
		hex = new Hex("0x2");
		assertEquals("00", hex.format(0D));

		hex = new Hex("0X2");
		assertEquals("10", hex.format(16D));
		hex = new Hex("0x2");
		assertEquals("10", hex.format(16D));

		hex = new Hex("0X2");
		assertEquals("00", hex.format(256D));
		hex = new Hex("0x2");
		assertEquals("00", hex.format(256D));

		hex = new Hex("0X2");
		assertEquals("FF", hex.format(65535D));
		hex = new Hex("0x2");
		assertEquals("ff", hex.format(65535D));
	}
}
