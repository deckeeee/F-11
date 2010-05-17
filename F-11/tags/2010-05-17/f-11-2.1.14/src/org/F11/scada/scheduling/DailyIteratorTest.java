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
 * DailyIteratorのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DailyIteratorTest extends TestCase {
	private SimpleDateFormat fmt;

	/**
	 * Constructor for DailyIteratorTest.
	 * @param arg0
	 */
	public DailyIteratorTest(String arg0) {
		super(arg0);
		fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fmt.setLenient(false);
	}

	public void testNext() throws Exception {
		DailyIterator it =
			new DailyIterator(0, 0, 0, fmt.parse("2004/12/01 00:00:00"));
		assertEquals(fmt.parse("2004/12/01 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/02 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/03 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/04 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/05 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/06 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/07 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/08 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/09 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/10 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/11 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/12 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/13 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/14 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/15 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/16 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/17 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/18 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/19 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/20 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/21 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/22 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/23 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/24 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/25 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/26 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/27 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/28 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/29 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/30 00:00:00"), it.next());
		assertEquals(fmt.parse("2004/12/31 00:00:00"), it.next());
		assertEquals(fmt.parse("2005/01/01 00:00:00"), it.next());

		it =
			new DailyIterator(1, 0, 0, fmt.parse("2004/12/01 20:00:00"));
		assertEquals(fmt.parse("2004/12/02 01:00:00"), it.next());
	}

}
