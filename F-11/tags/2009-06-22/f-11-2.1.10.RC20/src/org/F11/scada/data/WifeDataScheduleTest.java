/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataScheduleTest.java,v 1.7 2004/06/21 07:16:33 frdm Exp $
 * $Revision: 1.7 $
 * $Date: 2004/06/21 07:16:33 $
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;

/**
 * WifeDataScheduleのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataScheduleTest extends TestCase {
	private WifeDataSchedule sc1;
	private WifeDataSchedule sc2;
	private WifeDataSchedule sc3;
	private int wordSize;

	/**
	 * Constructor for WifeDataScheduleTest.
	 * @param arg0
	 */
	public WifeDataScheduleTest(String arg0) {
		super(arg0);
	}

	public void testCreate() throws Exception {
		sc1 = WifeDataSchedule.valueOf(1, 2);
		sc2 = WifeDataSchedule.valueOf(1, 2);
		sc3 = WifeDataSchedule.valueOf(1, 3);
		wordSize = 44;
		verify();

		try {
			sc1 =
				(WifeDataSchedule) sc1.valueOf(
					WifeUtilities.toByteArray("0000"));
			fail();
		} catch (IllegalArgumentException ex) {
		}

		byte[] b =
			WifeUtilities.toByteArray(
				"0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333");
		sc1 = (WifeDataSchedule) sc1.valueOf(b);
		sc2 = (WifeDataSchedule) sc2.valueOf(b);
		verify();
	}

	public void testCreateString() throws Exception {
		sc1 = WifeDataSchedule.valueOf(1, 2, "group1");
		sc2 = WifeDataSchedule.valueOf(1, 2, "group1");
		sc3 = WifeDataSchedule.valueOf(1, 3, "group2");
		wordSize = 44;
		verify();

		try {
			sc1 =
				(WifeDataSchedule) sc1.valueOf(
					WifeUtilities.toByteArray("0000"));
			fail();
		} catch (IllegalArgumentException ex) {
		}

		byte[] b =
			WifeUtilities.toByteArray(
				"0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333");
		sc1 = (WifeDataSchedule) sc1.valueOf(b);
		sc2 = (WifeDataSchedule) sc2.valueOf(b);
		verify();
	}

	public void testGetDayIndex() throws Exception {
		sc1 = WifeDataSchedule.valueOf(1, 2);
		byte[] b =
			WifeUtilities.toByteArray(
				"0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333");
		sc1 = (WifeDataSchedule) sc1.valueOf(b);
		assertEquals(2, sc1.getNumberSize());
		assertEquals(11, sc1.getPatternSize());

		assertEquals(0000, sc1.getOnTime(DefaultSchedulePattern.TODAY, 0));
		assertEquals(4444, sc1.getOnTime(DefaultSchedulePattern.TOMORROW, 0));
		assertEquals(8888, sc1.getOnTime(DefaultSchedulePattern.SUNDAY, 0));
		assertEquals(2222, sc1.getOnTime(DefaultSchedulePattern.MONDAY, 0));
		assertEquals(6666, sc1.getOnTime(DefaultSchedulePattern.TUESDAY, 0));
		assertEquals(0000, sc1.getOnTime(DefaultSchedulePattern.WEDNESDAY, 0));
		assertEquals(4444, sc1.getOnTime(DefaultSchedulePattern.THURSDAY, 0));
		assertEquals(8888, sc1.getOnTime(DefaultSchedulePattern.FRIDAY, 0));
		assertEquals(2222, sc1.getOnTime(DefaultSchedulePattern.SATURDAY, 0));
		assertEquals(6666, sc1.getOnTime(DefaultSchedulePattern.HOLIDAY, 0));
		assertEquals(0000, sc1.getOnTime(sc1.getSpecialDayOfIndex(0), 0));

		assertEquals(2222, sc1.getOnTime(DefaultSchedulePattern.TODAY, 1));
		assertEquals(6666, sc1.getOnTime(DefaultSchedulePattern.TOMORROW, 1));
		assertEquals(0000, sc1.getOnTime(DefaultSchedulePattern.SUNDAY, 1));
		assertEquals(4444, sc1.getOnTime(DefaultSchedulePattern.MONDAY, 1));
		assertEquals(8888, sc1.getOnTime(DefaultSchedulePattern.TUESDAY, 1));
		assertEquals(2222, sc1.getOnTime(DefaultSchedulePattern.WEDNESDAY, 1));
		assertEquals(6666, sc1.getOnTime(DefaultSchedulePattern.THURSDAY, 1));
		assertEquals(0000, sc1.getOnTime(DefaultSchedulePattern.FRIDAY, 1));
		assertEquals(4444, sc1.getOnTime(DefaultSchedulePattern.SATURDAY, 1));
		assertEquals(8888, sc1.getOnTime(DefaultSchedulePattern.HOLIDAY, 1));
		assertEquals(2222, sc1.getOnTime(sc1.getSpecialDayOfIndex(0), 1));

		assertEquals(1111, sc1.getOffTime(DefaultSchedulePattern.TODAY, 0));
		assertEquals(5555, sc1.getOffTime(DefaultSchedulePattern.TOMORROW, 0));
		assertEquals(9999, sc1.getOffTime(DefaultSchedulePattern.SUNDAY, 0));
		assertEquals(3333, sc1.getOffTime(DefaultSchedulePattern.MONDAY, 0));
		assertEquals(7777, sc1.getOffTime(DefaultSchedulePattern.TUESDAY, 0));
		assertEquals(1111, sc1.getOffTime(DefaultSchedulePattern.WEDNESDAY, 0));
		assertEquals(5555, sc1.getOffTime(DefaultSchedulePattern.THURSDAY, 0));
		assertEquals(9999, sc1.getOffTime(DefaultSchedulePattern.FRIDAY, 0));
		assertEquals(3333, sc1.getOffTime(DefaultSchedulePattern.SATURDAY, 0));
		assertEquals(7777, sc1.getOffTime(DefaultSchedulePattern.HOLIDAY, 0));
		assertEquals(1111, sc1.getOffTime(sc1.getSpecialDayOfIndex(0), 0));

		assertEquals(3333, sc1.getOffTime(DefaultSchedulePattern.TODAY, 1));
		assertEquals(7777, sc1.getOffTime(DefaultSchedulePattern.TOMORROW, 1));
		assertEquals(1111, sc1.getOffTime(DefaultSchedulePattern.SUNDAY, 1));
		assertEquals(5555, sc1.getOffTime(DefaultSchedulePattern.MONDAY, 1));
		assertEquals(9999, sc1.getOffTime(DefaultSchedulePattern.TUESDAY, 1));
		assertEquals(3333, sc1.getOffTime(DefaultSchedulePattern.WEDNESDAY, 1));
		assertEquals(7777, sc1.getOffTime(DefaultSchedulePattern.THURSDAY, 1));
		assertEquals(1111, sc1.getOffTime(DefaultSchedulePattern.FRIDAY, 1));
		assertEquals(5555, sc1.getOffTime(DefaultSchedulePattern.SATURDAY, 1));
		assertEquals(9999, sc1.getOffTime(DefaultSchedulePattern.HOLIDAY, 1));
		assertEquals(3333, sc1.getOffTime(sc1.getSpecialDayOfIndex(0), 1));
	}

	public void testSetDayIndex() throws Exception {
		sc1 = WifeDataSchedule.valueOf(1, 2);
		verify(DefaultSchedulePattern.TODAY, 1111);
		verify(DefaultSchedulePattern.TOMORROW, 2222);
		verify(DefaultSchedulePattern.SUNDAY, 3333);
		verify(DefaultSchedulePattern.MONDAY, 4444);
		verify(DefaultSchedulePattern.TUESDAY, 5555);
		verify(DefaultSchedulePattern.WEDNESDAY, 6666);
		verify(DefaultSchedulePattern.THURSDAY, 7777);
		verify(DefaultSchedulePattern.FRIDAY, 8888);
		verify(DefaultSchedulePattern.SATURDAY, 9999);
		verify(DefaultSchedulePattern.HOLIDAY, 0000);
		verify(sc1.getSpecialDayOfIndex(0), 1111);
	}

	public void testDeserialize() throws Exception {
		sc1 = WifeDataSchedule.valueOf(1, 2);
		byte[] b =
			WifeUtilities.toByteArray(
				"0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333");
		sc1 = (WifeDataSchedule) sc1.valueOf(b);

		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		try {
			ObjectOutputStream outs =
				new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(sc1);
			outs.flush();
			outs.close();
			ObjectInputStream ins =
				new ObjectInputStream(new FileInputStream(temp));
			sc2 = (WifeDataSchedule) ins.readObject();
			ins.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(sc2);
		assertEquals(sc1, sc2);
	}

	public void testDeserializeString() throws Exception {
		sc1 = WifeDataSchedule.valueOf(1, 2, "group1");
		byte[] b =
			WifeUtilities.toByteArray(
				"0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333"
					+ "4444555566667777"
					+ "8888999900001111"
					+ "2222333344445555"
					+ "6666777788889999"
					+ "0000111122223333");
		sc1 = (WifeDataSchedule) sc1.valueOf(b);

		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		try {
			ObjectOutputStream outs =
				new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(sc1);
			outs.flush();
			outs.close();
			ObjectInputStream ins =
				new ObjectInputStream(new FileInputStream(temp));
			sc2 = (WifeDataSchedule) ins.readObject();
			ins.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(sc2);
		assertEquals(sc1, sc2);
	}

	private void verify(int index, int value) throws Exception {
		sc1 = sc1.setOnTime(index, 0, value);
		assertEquals(value, sc1.getOnTime(index, 0));
		sc1 = sc1.setOffTime(index, 0, value);
		assertEquals(value, sc1.getOffTime(index, 0));

		sc1 = sc1.setOnTime(index, 1, value);
		assertEquals(value, sc1.getOnTime(index, 1));
		sc1 = sc1.setOffTime(index, 1, value);
		assertEquals(value, sc1.getOffTime(index, 1));
	}

	private void verify() throws Exception {
		assertEquals(sc1, sc2);
		assertTrue(!sc1.equals(sc3));
		assertEquals(wordSize, sc1.getWordSize());
		assertEquals(sc1.hashCode(), sc2.hashCode());
		assertTrue(Arrays.equals(sc1.toByteArray(), sc2.toByteArray()));
		//		System.out.println("sc1:\n" + dump.encodeBuffer(sc1.toByteArray()));
		//		System.out.println("sc2:\n" + dump.encodeBuffer(sc2.toByteArray()));
		assertEquals(sc1.toString(), sc2.toString());
	}

}
