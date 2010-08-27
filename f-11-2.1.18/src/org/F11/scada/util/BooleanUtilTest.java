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

package org.F11.scada.util;

import java.util.BitSet;

import junit.framework.TestCase;

public class BooleanUtilTest extends TestCase {
	public void testGetDigitalValue() throws Exception {
		assertEquals("true", BooleanUtil.getDigitalValue(true));
		assertEquals("false", BooleanUtil.getDigitalValue(false));
	}
	
	public void testGetBitSet() throws Exception {
		BitSet set = BooleanUtil.getBitSet("001");
		assertFalse(set.get(0));
		assertFalse(set.get(1));
		assertTrue(set.get(2));
		BitSet set2 = BooleanUtil.getBitSet("a#‚Ê");
		assertFalse(set2.get(0));
		assertFalse(set2.get(1));
		assertFalse(set2.get(2));
		BitSet set3 = BooleanUtil.getBitSet(null);
		assertFalse(set3.get(0));
		assertFalse(set3.get(1));
		assertFalse(set3.get(2));
	}
}
