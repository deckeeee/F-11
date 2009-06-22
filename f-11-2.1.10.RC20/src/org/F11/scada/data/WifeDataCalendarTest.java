/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataCalendarTest.java,v 1.5 2004/02/05 06:29:55 frdm Exp $
 * $Revision: 1.5 $
 * $Date: 2004/02/05 06:29:55 $
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

import org.F11.scada.WifeUtilities;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataCalendarTest extends TestCase {

	/**
	 * Constructor for WifeDataCalendarTest.
	 * @param arg0
	 */
	public WifeDataCalendarTest(String arg0) {
		super(arg0);
	}
	/**
	 * 初期値データ生成時のテストです。
	 */
	public void testWordDataValueOf() throws Exception {
		WifeDataCalendar wd0 = WifeDataCalendar.valueOf(6);
		//		System.out.println(WifeDataCalendar.valueOf(2));
		//		System.out.println(wd0);
		assertEquals(WifeDataCalendar.valueOf(6), wd0);
		assertEquals(WifeDataCalendar.valueOf(6).hashCode(), wd0.hashCode());
		assertEquals(WifeDataCalendar.valueOf(6).toString(), wd0.toString());
		assertTrue(
			Arrays.equals(
				WifeDataCalendar.valueOf(6).toByteArray(),
				wd0.toByteArray()));
		assertEquals(144, wd0.getWordSize());
		assertFalse(wd0.equals(WifeDataCalendar.valueOf(5)));
		assertFalse((wd0.hashCode() == WifeDataCalendar.valueOf(5).hashCode()));
	}

	/**
	 * バイト配列を引数にする、データ生成のテストです。
	 */
	public void testWordDataValueOfByte() throws Exception {
		WifeDataCalendar wd0 = WifeDataCalendar.valueOf(6);
		// 6モード(休日+特殊日) × 12ヶ月
		int allCal = 6 * 12;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < allCal; i++) {
			sb.append("FFFFFFFF");
		}
		wd0 =
			(WifeDataCalendar) wd0.valueOf(
				WifeUtilities.toByteArray(sb.toString()));
		WifeDataCalendar wdMax =
			(WifeDataCalendar) wd0.valueOf(
				WifeUtilities.toByteArray(sb.toString()));
		assertEquals(wdMax, wd0);
		assertEquals(wdMax.hashCode(), wd0.hashCode());
		assertTrue(Arrays.equals(wdMax.toByteArray(), wd0.toByteArray()));

		sb = new StringBuffer();
		for (int i = 0; i < allCal; i++) {
			sb.append("00000000");
		}
		wd0 =
			(WifeDataCalendar) wd0.valueOf(
				WifeUtilities.toByteArray(sb.toString()));
		wdMax =
			(WifeDataCalendar) wd0.valueOf(
				WifeUtilities.toByteArray(sb.toString()));
		assertEquals(wdMax, wd0);
		assertEquals(wdMax.hashCode(), wd0.hashCode());
		assertTrue(Arrays.equals(wdMax.toByteArray(), wd0.toByteArray()));

		try {
			wd0 =
				(WifeDataCalendar) wd0.valueOf(
					WifeUtilities.toByteArray("0000000000"));
			fail();
		} catch (Exception ex) {
		}
	}

	/**
	 * ビットセットのテストです。
	 */
	public void testWordDataBitSet() throws Exception {
		WifeDataCalendar wd0 = WifeDataCalendar.valueOf(2);
		wd0 = wd0.setBit(0, 0, 0);
		WifeDataCalendar wdMax = WifeDataCalendar.valueOf(2);
		StringBuffer sb = new StringBuffer();
		int allCal = 2 * 12 - 1;
		for (int i = 0; i < allCal; i++) {
			sb.append("00000000");
		}
		wdMax =
			(WifeDataCalendar) wdMax.valueOf(
				WifeUtilities.toByteArray("00010000" + sb.toString()));
		assertEquals(wdMax, wd0);
		assertEquals(wdMax.hashCode(), wd0.hashCode());
		assertTrue(Arrays.equals(wdMax.toByteArray(), wd0.toByteArray()));

		wd0 = WifeDataCalendar.valueOf(2);
		wd0 = wd0.setBit(0, 0, 15);
		sb = new StringBuffer();
		allCal = 2 * 12 - 1;
		for (int i = 0; i < allCal; i++) {
			sb.append("00000000");
		}
		wdMax =
			(WifeDataCalendar) wdMax.valueOf(
				WifeUtilities.toByteArray("80000000" + sb.toString()));
		assertEquals(wdMax, wd0);

		wd0 = WifeDataCalendar.valueOf(2);
		wd0 = wd0.setBit(0, 0, 16);
		sb = new StringBuffer();
		allCal = 2 * 12 - 1;
		for (int i = 0; i < allCal; i++) {
			sb.append("00000000");
		}
		wdMax =
			(WifeDataCalendar) wdMax.valueOf(
				WifeUtilities.toByteArray("00000001" + sb.toString()));
		assertEquals(wdMax, wd0);

		wd0 = WifeDataCalendar.valueOf(2);
		wd0 = wd0.setBit(0, 0, 31);
		sb = new StringBuffer();
		allCal = 2 * 12 - 1;
		for (int i = 0; i < allCal; i++) {
			sb.append("00000000");
		}
		wdMax =
			(WifeDataCalendar) wdMax.valueOf(
				WifeUtilities.toByteArray("00008000" + sb.toString()));
		assertEquals(wdMax, wd0);

		wd0 = WifeDataCalendar.valueOf(2);
		wd0 = wd0.setBit(1, 0, 0);
		sb = new StringBuffer();
		for (int i = 0; i < 11; i++) {
			sb.append("00000000");
		}
		wdMax =
			(WifeDataCalendar) wdMax.valueOf(
				WifeUtilities.toByteArray(
					"00000000" + sb.toString() + "00010000" + sb.toString()));
		assertEquals(wdMax, wd0);
		assertEquals(wdMax.hashCode(), wd0.hashCode());
		assertTrue(Arrays.equals(wdMax.toByteArray(), wd0.toByteArray()));

		scenario(wd0);
	}

	/**
	 * ビットクリアのテストです。
	public void testWordDataClearBit() throws Exception {
		WifeDataCalendar wd0 = WifeDataCalendar.valueOf(2);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 23; i++) {
			sb.append("00000000");
		}
		wd0 = (WifeDataCalendar)wd0.valueOf(WifeUtilities.toByteArray("80000000" + sb.toString()));
		wd0 = wd0.clearBit(0, 0, 0);
		WifeDataCalendar wdMax = WifeDataCalendar.valueOf(2);
		wdMax = (WifeDataCalendar)wdMax.valueOf(WifeUtilities.toByteArray("00000000" + sb.toString()));
		assertEquals(wdMax, wd0);
		assertEquals(wdMax.hashCode(), wd0.hashCode());
		assertTrue(Arrays.equals(wdMax.toByteArray(), wd0.toByteArray()));
		scenario(wd0);
	}
	 */

	/**
	 * ビット判定のテストです。
	 */
	public void testWordDataTestBit() throws Exception {
		int mode = 2;
		WifeDataCalendar wd0 = WifeDataCalendar.valueOf(mode);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < (mode * 12 - 1); i++) {
			sb.append("00000000");
		}
		wd0 =
			(WifeDataCalendar) wd0.valueOf(
				WifeUtilities.toByteArray("FFFFBFFF" + sb.toString()));
		assertFalse(wd0.testBit(0, 0, 30));
		assertTrue(wd0.testBit(0, 0, 1));
		scenario(wd0);
	}

	private void scenario(WifeDataCalendar wd0) throws Exception {
		try {
			wd0.setBit(0, 0, -1);
			fail();
		} catch (Exception ex) {
		}
		try {
			wd0.setBit(0, 0, 32);
			fail();
		} catch (Exception ex) {
		}
		try {
			wd0.setBit(-1, 0, 0);
			fail();
		} catch (Exception ex) {
		}
		try {
			wd0.setBit(2, 0, 0);
			fail();
		} catch (Exception ex) {
		}
		try {
			wd0.setBit(0, -1, 0);
			fail();
		} catch (Exception ex) {
		}
		try {
			wd0.setBit(0, 12, 0);
			fail();
		} catch (Exception ex) {
		}
	}
}
