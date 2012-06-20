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
 * @author hori
 */
public class WifeDataAnalog4Test extends TestCase {

	/**
	 * Constructor for WifeDataAnalog4Test.
	 * @param arg0
	 */
	public WifeDataAnalog4Test(String arg0) {
		super(arg0);
	}

	public void testBcdSingle() {
		WifeDataAnalog4 an0 = WifeDataAnalog4.valueOfBcdSingle(new double[] { 0, 0, 0, 0 });
		WifeDataAnalog4 an1 = WifeDataAnalog4.valueOfBcdSingle(new double[] { 0, 0, 0, 0 });
		WifeDataAnalog4 an2 = WifeDataAnalog4.valueOfBcdSingle(new double[] { 1, 1, 1, 1 });
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("0001000100010001"));
		assertEquals(an0, an2);
		double[] da = an0.doubleValues();
		assertEquals(4, da.length);
		assertEquals(1.0D, da[0], 0.0);
		assertEquals(1.0D, da[1], 0.0);
		assertEquals(1.0D, da[2], 0.0);
		assertEquals(1.0D, da[3], 0.0);
		// バイト配列に変換
		byte[] tba = an0.toByteArray();
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("0001000100010001"), tba));
		an1 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("9991999299939994"));
		da = an1.doubleValues();
		assertEquals(4, da.length);
		assertEquals(9991, da[0], 0.0);
		assertEquals(9992, da[1], 0.0);
		assertEquals(9993, da[2], 0.0);
		assertEquals(9994, da[3], 0.0);
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("9991999299939994"), an1.toByteArray()));
		an1 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("0000000000000000"));
		da = an1.doubleValues();
		assertEquals(4, da.length);
		assertEquals(0.0, da[0], 0.0);
		assertEquals(0.0, da[1], 0.0);
		assertEquals(0.0, da[2], 0.0);
		assertEquals(0.0, da[3], 0.0);
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("0000000000000000"), an1.toByteArray()));
	}

	public void testHexSingle() {
		WifeDataAnalog4 an0 = WifeDataAnalog4.valueOfHexSingle(new double[] { 0, 0, 0, 0 });
		WifeDataAnalog4 an1 = WifeDataAnalog4.valueOfHexSingle(new double[] { 0, 0, 0, 0 });
		WifeDataAnalog4 an2 = WifeDataAnalog4.valueOfHexSingle(new double[] { 1, 1, 1, 1 });
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("0001000100010001"));
		assertEquals(an0, an2);
		double[] da = an0.doubleValues();
		assertEquals(4, da.length);
		assertEquals(1.0D, da[0], 0.0);
		assertEquals(1.0D, da[1], 0.0);
		assertEquals(1.0D, da[2], 0.0);
		assertEquals(1.0D, da[3], 0.0);
		// バイト配列に変換
		byte[] tba = an0.toByteArray();
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("0001000100010001"), tba));
		an1 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("fff1fff2fff3fff4"));
		da = an1.doubleValues();
		assertEquals(4, da.length);
		assertEquals(65521, da[0], 0.0);
		assertEquals(65522, da[1], 0.0);
		assertEquals(65523, da[2], 0.0);
		assertEquals(65524, da[3], 0.0);
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("fff1fff2fff3fff4"), an1.toByteArray()));
		an1 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("0000000000000000"));
		da = an1.doubleValues();
		assertEquals(4, da.length);
		assertEquals(0.0, da[0], 0.0);
		assertEquals(0.0, da[1], 0.0);
		assertEquals(0.0, da[2], 0.0);
		assertEquals(0.0, da[3], 0.0);
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("0000000000000000"), an1.toByteArray()));
	}

	public void testShxSingle() {
		WifeDataAnalog4 an0 = WifeDataAnalog4.valueOfShxSingle(new double[] { 0, 0, 0, 0 });
		WifeDataAnalog4 an1 = WifeDataAnalog4.valueOfShxSingle(new double[] { 0, 0, 0, 0 });
		WifeDataAnalog4 an2 = WifeDataAnalog4.valueOfShxSingle(new double[] { 1, 1, 1, 1 });
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("0001000100010001"));
		assertEquals(an0, an2);
		double[] da = an0.doubleValues();
		assertEquals(4, da.length);
		assertEquals(1.0D, da[0], 0.0);
		assertEquals(1.0D, da[1], 0.0);
		assertEquals(1.0D, da[2], 0.0);
		assertEquals(1.0D, da[3], 0.0);
		// バイト配列に変換
		byte[] tba = an0.toByteArray();
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("0001000100010001"), tba));
		an1 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("fffffffefffdfffc"));
		da = an1.doubleValues();
		assertEquals(4, da.length);
		assertEquals(-1, da[0], 0.0);
		assertEquals(-2, da[1], 0.0);
		assertEquals(-3, da[2], 0.0);
		assertEquals(-4, da[3], 0.0);
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("fffffffefffdfffc"), an1.toByteArray()));
		an1 = (WifeDataAnalog4) an0.valueOf(WifeUtilities.toByteArray("0000000000000000"));
		da = an1.doubleValues();
		assertEquals(4, da.length);
		assertEquals(0.0, da[0], 0.0);
		assertEquals(0.0, da[1], 0.0);
		assertEquals(0.0, da[2], 0.0);
		assertEquals(0.0, da[3], 0.0);
		assertTrue(Arrays.equals(WifeUtilities.toByteArray("0000000000000000"), an1.toByteArray()));
	}

	public void testSerializable() throws Exception {
		//シリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		WifeDataAnalog4 an0 = WifeDataAnalog4.valueOfShxSingle(new double[] { 0, 1, 2, 3 });
		ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(an0);
		outs.flush();
		outs.close();

		ObjectInputStream ins = new ObjectInputStream(new FileInputStream(temp));
		WifeDataAnalog4 an1 = (WifeDataAnalog4) ins.readObject();
		ins.close();

		assertNotNull(an1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
	}

}
