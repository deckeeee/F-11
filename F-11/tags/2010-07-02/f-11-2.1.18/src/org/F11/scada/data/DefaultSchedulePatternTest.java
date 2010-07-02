/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/DefaultSchedulePatternTest.java,v 1.4 2003/02/05 06:52:07 frdm Exp $
 * $Revision: 1.4 $
 * $Date: 2003/02/05 06:52:07 $
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

import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultSchedulePatternTest extends TestCase {

	/**
	 * Constructor for DefaultSchedulePatternTest.
	 * @param arg0
	 */
	public DefaultSchedulePatternTest(String arg0) {
		super(arg0);
	}
	private DefaultSchedulePattern pt;
	private int specialDay = 10;
	private int specialDayMax;

	public void testDefault() throws Exception {
		specialDayMax = 5;
		pt = new DefaultSchedulePattern(specialDayMax);
		assertNotNull(pt);
		verify();
		assertEquals(10, pt.getSpecialDayOfIndex(0));
	}

	public void testObject() throws Exception {
		specialDayMax = 5;
		pt = new DefaultSchedulePattern(specialDayMax);
		assertNotNull(pt);
		DefaultSchedulePattern pt2 = new DefaultSchedulePattern(specialDayMax);
		assertNotNull(pt2);

		assertEquals(pt, pt2);
		assertEquals(pt.hashCode(), pt2.hashCode());
		assertEquals(pt.toString(), pt2.toString());
	}

	private void verify() {
		assertEquals(pt.getSpecialDayOfIndex(0) + specialDayMax, pt.size());
		assertEquals(pt.getSpecialDayOfIndex(0), pt.getSpecialDayOfIndex(0));
		assertEquals(0, pt.getDayIndex(DefaultSchedulePattern.TODAY));
		assertEquals(1, pt.getDayIndex(DefaultSchedulePattern.TOMORROW));
		assertEquals(2, pt.getDayIndex(DefaultSchedulePattern.SUNDAY));
		assertEquals(3, pt.getDayIndex(DefaultSchedulePattern.MONDAY));
		assertEquals(4, pt.getDayIndex(DefaultSchedulePattern.TUESDAY));
		assertEquals(5, pt.getDayIndex(DefaultSchedulePattern.WEDNESDAY));
		assertEquals(6, pt.getDayIndex(DefaultSchedulePattern.THURSDAY));
		assertEquals(7, pt.getDayIndex(DefaultSchedulePattern.FRIDAY));
		assertEquals(8, pt.getDayIndex(DefaultSchedulePattern.SATURDAY));
		assertEquals(9, pt.getDayIndex(DefaultSchedulePattern.HOLIDAY));
		for (int i = 0; i < specialDayMax; i++) {
			assertEquals(specialDay + i, pt.getDayIndex(pt.getSpecialDayOfIndex(i)));
		}

		assertEquals("¡“ú", pt.getDayIndexName(DefaultSchedulePattern.TODAY));
		assertEquals("–¾“ú", pt.getDayIndexName(DefaultSchedulePattern.TOMORROW));
		assertEquals("“ú—j", pt.getDayIndexName(DefaultSchedulePattern.SUNDAY));
		assertEquals("ŒŽ—j", pt.getDayIndexName(DefaultSchedulePattern.MONDAY));
		assertEquals("‰Î—j", pt.getDayIndexName(DefaultSchedulePattern.TUESDAY));
		assertEquals("…—j", pt.getDayIndexName(DefaultSchedulePattern.WEDNESDAY));
		assertEquals("–Ø—j", pt.getDayIndexName(DefaultSchedulePattern.THURSDAY));
		assertEquals("‹à—j", pt.getDayIndexName(DefaultSchedulePattern.FRIDAY));
		assertEquals("“y—j", pt.getDayIndexName(DefaultSchedulePattern.SATURDAY));
		assertEquals("‹x“ú", pt.getDayIndexName(DefaultSchedulePattern.HOLIDAY));
		for (int i = 0; i < specialDayMax; i++) {
			assertEquals("“ÁŽê“ú" + (i + 1), pt.getDayIndexName(pt.getSpecialDayOfIndex(i)));
		}

		try {
			pt.getDayIndex(pt.size());
			fail();
		} catch (IllegalArgumentException ex) {}
		try {
			pt.getDayIndexName(pt.size());
			fail();
		} catch (IllegalArgumentException ex) {}
	}

}
