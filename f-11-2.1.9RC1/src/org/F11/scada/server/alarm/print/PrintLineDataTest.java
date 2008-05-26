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

package org.F11.scada.server.alarm.print;

import java.awt.Color;
import java.sql.Timestamp;

import junit.framework.TestCase;

/**
 * PrintLineDataのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PrintLineDataTest extends TestCase {

	/**
	 * Constructor for PrintLineDataTest.
	 * @param arg0
	 */
	public PrintLineDataTest(String arg0) {
		super(arg0);
	}

	public void testPrintLineData() {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		PrintLineData p =
			new PrintLineData("red", ts, "AHU-1-1-1", "空調ファン", "発停", "開始");
		assertEquals(Color.RED, p.getColor());
		assertEquals(ts, p.getEntryDate());
		assertEquals(ts + "  AHU-1-1-1  空調ファン  発停  開始  ", p.toString());
//		System.out.println(p.toString());
	}
}
