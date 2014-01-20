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
 *
 */
package org.F11.scada.applet.graph.bargraph;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

/**
 * 
 * @author hori <hori@users.sourceforge.jp>
 */
public class BarGraphStepTest extends TestCase {

	private BarGraphStep target;

	/**
	 * Constructor for BarGraphStepTest.
	 * @param arg0
	 */
	public BarGraphStepTest(String arg0) {
		super(arg0);
	}

	public void testStepHOUR00() {
		Calendar cal = new GregorianCalendar(2003, 2, 7, 13, 24, 50);
		long srcTime = cal.getTimeInMillis();
		cal.clear();

		target = BarGraphStep.createBarGraphStep("HOUR00", 0);
		
		// omitTime()
		cal.set(2003, 2, 7, 13, 0, 0);
		assertEquals(cal.getTimeInMillis(), target.omitTime(srcTime));

		// getBarCount()
		assertEquals(24, target.getBarCount(86400000L));

		// indexToTime()
		assertEquals(0L, target.indexToTime(0, 0));
		assertEquals(3600000L, target.indexToTime(0, 1));
		assertEquals(7200000L, target.indexToTime(0, 2));
		cal.set(2003, 2, 7, 14, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 1));
		cal.set(2003, 2, 7, 15, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 2));
		cal.set(2003, 2, 8, 0, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 11));

		// getAxisString()
		cal.set(2003, 2, 7, 14, 0, 0);
		assertEquals("13", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 7, 15, 0, 0);
		assertEquals("14", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 8, 1, 0, 0);
		assertEquals("00", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 9, 0, 0, 0);
		assertEquals("23", target.getAxisString(cal.getTimeInMillis()));
	}

	public void testStepHOUR() {
		Calendar cal = new GregorianCalendar(2003, 2, 7, 13, 24, 50);
		long srcTime = cal.getTimeInMillis();
		cal.clear();

		target = BarGraphStep.createBarGraphStep("HOUR", 0);
		
		// omitTime()
		cal.set(2003, 2, 7, 13, 0, 0);
		assertEquals(cal.getTimeInMillis(), target.omitTime(srcTime));

		// getBarCount()
		assertEquals(24, target.getBarCount(86400000L));

		// indexToTime()
		assertEquals(0L, target.indexToTime(0, 0));
		assertEquals(3600000L, target.indexToTime(0, 1));
		assertEquals(7200000L, target.indexToTime(0, 2));
		cal.set(2003, 2, 7, 14, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 1));
		cal.set(2003, 2, 7, 15, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 2));
		cal.set(2003, 2, 8, 0, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 11));

		// getAxisString()
		cal.set(2003, 2, 7, 14, 0, 0);
		assertEquals("14", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 7, 15, 0, 0);
		assertEquals("15", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 8, 1, 0, 0);
		assertEquals("01", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 9, 0, 0, 0);
		assertEquals("24", target.getAxisString(cal.getTimeInMillis()));
	}

	public void testStepDAY() {
		Calendar cal = new GregorianCalendar(2003, 2, 7, 13, 24, 50);
		long srcTime = cal.getTimeInMillis();
		cal.clear();

		target = BarGraphStep.createBarGraphStep("DAY", 0);
		
		// omitTime()
		cal.set(2003, 2, 7, 0, 0, 0);
		assertEquals(cal.getTimeInMillis(), target.omitTime(srcTime));

		// getBarCount()
		assertEquals(31, target.getBarCount(2678400000L));

		// indexToTime()
		assertEquals(0L, target.indexToTime(0, 0));
		assertEquals(86400000L, target.indexToTime(0, 1));
		assertEquals(172800000L, target.indexToTime(0, 2));
		cal.set(2003, 2, 8, 13, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 1));
		cal.set(2003, 2, 9, 13, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 2));
		cal.set(2003, 3, 1, 13, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 25));

		// getAxisString()
		cal.clear();
		cal.set(2003, 2, 8);
		assertEquals("07", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 9);
		assertEquals("08", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 3, 1);
		assertEquals("31", target.getAxisString(cal.getTimeInMillis()));
	}

	public void testStepMONTH() {
		Calendar cal = new GregorianCalendar(2003, 2, 7, 13, 24, 50);
		long srcTime = cal.getTimeInMillis();
		cal.clear();

		target = BarGraphStep.createBarGraphStep("MONTH", 0);
		
		// omitTime()
		cal.set(2003, 2, 1, 0, 0, 0);
		assertEquals(cal.getTimeInMillis(), target.omitTime(srcTime));

		// getBarCount()
		assertEquals(12, target.getBarCount(32140800000L));

		// indexToTime()
		assertEquals(0L, target.indexToTime(0L, 0));
		assertEquals(2678400000L, target.indexToTime(0, 1));
		assertEquals(5097600000L, target.indexToTime(0, 2));
		assertEquals(srcTime, target.indexToTime(srcTime, 0));
		cal.set(2003, 3, 7, 13, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 1));
		cal.set(2003, 4, 7, 13, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 2));
		cal.set(2004, 0, 7, 13, 24, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 10));

		// getAxisString()
		cal.clear();
		cal.set(2003, 3, 1);
		assertEquals("03", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 4, 1);
		assertEquals("04", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2004, 1, 1);
		assertEquals("01", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2004, 0, 1);
		assertEquals("12", target.getAxisString(cal.getTimeInMillis()));
	}

	public void testStepETC() {
		Calendar cal = new GregorianCalendar(2003, 2, 7, 13, 24, 50);
		long srcTime = cal.getTimeInMillis();
		cal.clear();

		target = BarGraphStep.createBarGraphStep("ETC", 600000);
		
		// omitTime()
		cal.set(2003, 2, 7, 13, 20, 0);
		assertEquals(cal.getTimeInMillis(), target.omitTime(srcTime));

		// getBarCount()
		assertEquals(24, target.getBarCount(14400000L));

		// indexToTime()
		assertEquals(0L, target.indexToTime(0, 0));
		assertEquals(600000L, target.indexToTime(0, 1));
		assertEquals(1200000L, target.indexToTime(0, 2));
		cal.set(2003, 2, 7, 13, 34, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 1));
		cal.set(2003, 2, 7, 13, 44, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 2));
		cal.set(2003, 2, 7, 14, 4, 50);
		assertEquals(cal.getTimeInMillis(), target.indexToTime(srcTime, 4));

		// getAxisString()
		cal.set(2003, 2, 7, 13, 30, 0);
		assertEquals("13:20", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 7, 13, 40, 0);
		assertEquals("13:30", target.getAxisString(cal.getTimeInMillis()));
		cal.set(2003, 2, 7, 14, 0, 0);
		assertEquals("13:50", target.getAxisString(cal.getTimeInMillis()));
	}

}
