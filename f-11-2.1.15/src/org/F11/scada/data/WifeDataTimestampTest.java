/*
 * Projrct    F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All
 * Rights Reserved.
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
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;
/**
 * @author hori
 */
public class WifeDataTimestampTest extends TestCase {
	private byte[] src =
		WifeUtilities.toByteArray("20030003002800050012005800300001");
	WifeDataTimestamp ts =
		WifeDataTimestamp.valueOfType1(
			new GregorianCalendar(2003, 2, 28, 12, 58, 30).getTimeInMillis());

	/**
	 * Constructor for WifeDataTimestampTest.
	 * @param arg0
	 */
	public WifeDataTimestampTest(String arg0) {
		super(arg0);
	}

	public void testGetWordSize() {
		assertEquals(8, ts.getWordSize());
	}

	public void testToByteArray() {
		byte[] dist = ts.toByteArray();
		assertEquals(
			WifeUtilities.toString(src, src.length),
			WifeUtilities.toString(dist, dist.length));
	}

	public void testValueOf() {
		WifeData ts2 = ts.valueOf(src);
		assertTrue(ts != ts2);
		assertEquals(ts.toString(), ts2.toString());
	}

	public void testTimestampValue() {
		Calendar cc = ts.calendarValue();
		assertEquals(2003, cc.get(Calendar.YEAR));
		assertEquals(2, cc.get(Calendar.MONTH));
		assertEquals(28, cc.get(Calendar.DAY_OF_MONTH));
		assertEquals(6, cc.get(Calendar.DAY_OF_WEEK));
		assertEquals(12, cc.get(Calendar.HOUR_OF_DAY));
		assertEquals(58, cc.get(Calendar.MINUTE));
		assertEquals(30, cc.get(Calendar.SECOND));
	}

	public void testSerializable() throws Exception {
		//シリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(ts);
		outs.flush();
		outs.close();

		ObjectInputStream ins = new ObjectInputStream(new FileInputStream(temp));
		WifeDataTimestamp tsin = (WifeDataTimestamp) ins.readObject();
		ins.close();

		assertNotNull(tsin);
		assertEquals(ts, tsin);
		assertEquals(ts.hashCode(), tsin.hashCode());
		assertEquals(ts.toString(), tsin.toString());
	}
}
