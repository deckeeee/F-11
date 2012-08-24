/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada;

import junit.framework.TestCase;

public class WifeUtilitiesTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIsTrue() {
		assertTrue(WifeUtilities.isTrue(Boolean.TRUE));
		assertTrue(WifeUtilities.isTrue(new Integer(1)));
		assertTrue(WifeUtilities.isTrue("yes"));
		assertTrue(WifeUtilities.isTrue("true"));
		assertTrue(WifeUtilities.isTrue("1"));
		assertTrue(WifeUtilities.isTrue("t"));
		assertFalse(WifeUtilities.isTrue(Boolean.FALSE));
		assertFalse(WifeUtilities.isTrue(new Integer(0)));
		assertFalse(WifeUtilities.isTrue("no"));
		assertFalse(WifeUtilities.isTrue("false"));
		assertFalse(WifeUtilities.isTrue("0"));
		assertFalse(WifeUtilities.isTrue("f"));
	}
}
