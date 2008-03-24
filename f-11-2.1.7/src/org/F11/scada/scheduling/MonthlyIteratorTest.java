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
 */

package org.F11.scada.scheduling;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;

/**
 * MonthlyIteratorのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class MonthlyIteratorTest extends TestCase {
	private SimpleDateFormat fmt;

	/**
	 * Constructor for MonthlyIteratorTest.
	 * @param arg0
	 */
	public MonthlyIteratorTest(String arg0) {
		super(arg0);
		fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fmt.setLenient(false);
	}

	public void testNext() throws Exception {
		MonthlyIterator it =
			new MonthlyIterator(1, 0, 0, 0, fmt.parse("2004/12/1 00:00:00"));
		assertEquals(fmt.parse("2004/12/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/01/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/02/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/03/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/04/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/05/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/06/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/07/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/08/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/09/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/10/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/11/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/12/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2006/01/01 00:00:00"), it.next());

		it =
			new MonthlyIterator(28, 0, 0, 0, fmt.parse("2004/12/28 00:00:00"));
		assertEquals(fmt.parse("2004/12/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/01/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/02/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/03/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/04/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/05/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/06/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/07/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/08/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/09/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/10/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/11/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/12/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2006/01/28 00:00:00"), it.next());

		it =
			new MonthlyIterator(2, 0, 0, 0, fmt.parse("2004/12/03 00:00:00"));
		assertEquals(fmt.parse("2005/01/02 00:00:00"), it.next());
	}
	
	public void testIllegal() throws Exception {
		try {
			new MonthlyIterator(0, 0, 0, 0);
			fail();
		} catch (IllegalArgumentException ex) {
		}

		try {
			new MonthlyIterator(29, 0, 0, 0);
			fail();
		} catch (IllegalArgumentException ex) {
		}
	}
}
