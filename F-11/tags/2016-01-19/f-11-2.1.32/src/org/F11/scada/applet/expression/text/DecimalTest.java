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

public class DecimalTest extends TestCase {

	public DecimalTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetScale() throws Exception {
		Decimal d = new Decimal("00.0");
		assertEquals("20.1", d.format(20.10D));
		assertEquals("20.1", d.format(20.14D));
		assertEquals("20.2", d.format(20.15D));
		assertEquals("20.2", d.format(20.19D));
		assertEquals("20.0", d.format(20.00D));
		assertEquals("20.0", d.format(20.04D));
		assertEquals("20.1", d.format(20.05D));
		assertEquals("20.1", d.format(20.09D));
	}
}
