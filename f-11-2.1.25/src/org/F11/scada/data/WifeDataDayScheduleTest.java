/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataDayScheduleTest.java,v 1.6 2004/06/21 07:16:33 frdm Exp $
 * $Revision: 1.6 $
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
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataDayScheduleTest extends TestCase {

	/**
	 * Constructor for WifeDataDayScheduleTest.
	 * @param arg0
	 */
	public WifeDataDayScheduleTest(String arg0) {
		super(arg0);
	}
	private WifeDataDaySchedule sc1;
	private WifeDataDaySchedule sc2;
	private WifeDataDaySchedule sc3;
	private int wordSize;

	public void testCreate() throws Exception {
		sc1 = WifeDataDaySchedule.valueOf(2);
		sc2 = WifeDataDaySchedule.valueOf(2);
		sc3 = WifeDataDaySchedule.valueOf(3);
		wordSize = 4;
		verify();

		try {
			sc1 = (WifeDataDaySchedule)sc1.valueOf(WifeUtilities.toByteArray("0000"));
			fail();
		} catch (IllegalArgumentException ex) {}

		byte[] b = WifeUtilities.toByteArray("0000111122223333");
		sc1 = (WifeDataDaySchedule)sc1.valueOf(b);
		sc2 = (WifeDataDaySchedule)sc2.valueOf(b);
		verify();
	}

	public void testGetDayIndex() throws Exception {
		sc1 = WifeDataDaySchedule.valueOf(2);
		byte[] b = WifeUtilities.toByteArray("0000111122223333");
		sc1 = (WifeDataDaySchedule)sc1.valueOf(b);
		assertEquals(2, sc1.getNumberSize());

		assertEquals(0000, sc1.getOnTime(0));
		assertEquals(2222, sc1.getOnTime(1));
		assertEquals(1111, sc1.getOffTime(0));
		assertEquals(3333, sc1.getOffTime(1));
	}

	public void testSetDayIndex() throws Exception {
		sc1 = WifeDataDaySchedule.valueOf(2);
		verify(1111);
		verify(2222);
		verify(3333);
		verify(4444);
		verify(5555);
		verify(6666);
		verify(7777);
		verify(8888);
		verify(9999);
		verify(0000);
	}

	public void testDeserialize() throws Exception {
		sc1 = WifeDataDaySchedule.valueOf(2);
		byte[] b = WifeUtilities.toByteArray("0000111122223333");
		sc1 = (WifeDataDaySchedule)sc1.valueOf(b);
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		try {
			ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(sc1);
			outs.flush();
			outs.close();
			ObjectInputStream ins = new ObjectInputStream(new FileInputStream(temp));
			sc2 = (WifeDataDaySchedule)ins.readObject();
			ins.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(sc2);
		assertEquals(sc1, sc2);
		temp.delete();
	}

	private void verify(int value) throws Exception {
		sc1 = sc1.setOnTime(0, value);
		assertEquals(value, sc1.getOnTime(0));
		sc1 = sc1.setOffTime(0, value);
		assertEquals(value, sc1.getOffTime(0));

		sc1 = sc1.setOnTime(1, value);
		assertEquals(value, sc1.getOnTime(1));
		sc1 = sc1.setOffTime(1, value);
		assertEquals(value, sc1.getOffTime(1));
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
