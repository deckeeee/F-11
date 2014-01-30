/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataAnalogTest.java,v 1.8.2.2 2005/07/06 02:20:44 frdm Exp $
 * $Revision: 1.8.2.2 $
 * $Date: 2005/07/06 02:20:44 $
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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.F11.scada.WifeUtilities;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataAnalogTest extends TestCase {
	private final byte[] WORD_0001 = {(byte) 0x00, (byte) 0x01 };
	private final byte[] WORD_0100 = {(byte) 0x01, (byte) 0x00 };
	private final byte[] DWORD_00000001 =
		{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
	private final byte[] DWORD_00010000 =
		{(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00 };

	private static byte[] FLOAT_BIT_INT;
	private static byte[] DOUBLE_BIT_LONG;

	private double minValue;
	private double maxValue;
	private WifeDataAnalog minAnalog;
	private WifeDataAnalog maxAnalog;
	private byte[] minByteArray;
	private byte[] maxByteArray;
	private byte[] verifyByteArray;
	private byte[] IligalByteArray1;
	private byte[] IligalByteArray2;
	private int size;

	private double midValue;
	private WifeDataAnalog midAnalog;
	private byte[] midByteArray;

	/**
	 * Constructor for WifeDataAnalogTest.
	 * @param arg0
	 */
	public WifeDataAnalogTest(String arg0) {
		super(arg0);
	}

	public static Test suite() {
		return new TestSuite(WifeDataAnalogTest.class);
	}
	protected void setUp() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			DataOutputStream oos = new DataOutputStream(os);
			oos.writeFloat(Float.intBitsToFloat(0x7f7fffff));
			oos.flush();
			FLOAT_BIT_INT = swapBytes(os.toByteArray(), 2);
			oos.close();

			os = new ByteArrayOutputStream();
			oos = new DataOutputStream(os);
			oos.writeDouble(Double.longBitsToDouble(0x7fefffffffffffffL));
			oos.flush();
			DOUBLE_BIT_LONG = os.toByteArray();
			oos.close();

			os = new ByteArrayOutputStream();
			oos = new DataOutputStream(os);
			oos.writeDouble(0);
			oos.flush();
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	protected void tearDown() {
	}

	public void testStringToByte() {
		double d = 1.0D;
		byte[] b = WifeBCD.valueOf(d, "0000");
		assertTrue(Arrays.equals(new byte[]{(byte)0x00,(byte)0x01}, b));
		d = 11.0D;
		b = WifeBCD.valueOf(d, "0000");
		assertTrue(Arrays.equals(new byte[]{(byte)0x00, (byte)0x11}, b));
		d = 111.0D;
		b = WifeBCD.valueOf(d, "0000");
		assertTrue(Arrays.equals(new byte[]{(byte)0x01, (byte)0x11}, b));
		d = 1111.0D;
		b = WifeBCD.valueOf(d, "0000");
		assertTrue(Arrays.equals(new byte[]{(byte)0x11, (byte)0x11}, b));
	}

	public void testWifeDataAnalog() throws IOException {
		//WifeDataAnalog.AnalogType.HEX_ZERO_HALF_WORDのテスト
		WifeDataAnalog an0 = WifeDataAnalog.valueOfHexZeroHalf(0);
		WifeDataAnalog an1 = WifeDataAnalog.valueOfHexZeroHalf(0);
		WifeDataAnalog an2 = WifeDataAnalog.valueOfHexZeroHalf(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;HEX_ZERO_HALF_WORD}", an0.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(WORD_0100);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_ZERO_HALF_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_ZERO_HALF_WORD}", an0.toString());
		// バイト配列に変換
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		byte[] tba = an0.toByteArray();
		assertTrue(Arrays.equals(WORD_0100, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(256);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("FFFF"));
		assertEquals(255, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("FF00"),
				an1.toByteArray()));
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("0000"));
		assertEquals(0, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("0000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.HEX_ONE_HALF_WORDのテスト
		an0 = WifeDataAnalog.valueOfHexOneHalf(0);
		an1 = WifeDataAnalog.valueOfHexOneHalf(0);
		an2 = WifeDataAnalog.valueOfHexOneHalf(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;HEX_ONE_HALF_WORD}", an0.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(WORD_0001);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_ONE_HALF_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_ONE_HALF_WORD}", an0.toString());
		// バイト配列に変換
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(WORD_0001, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(256);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("FFFF"));
		assertEquals(255, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("00FF"),
				an1.toByteArray()));
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("0000"));
		assertEquals(0, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("0000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.HEX_SINGLE_WORDのテスト
		an0 = WifeDataAnalog.valueOfHexSingle(0);
		an1 = WifeDataAnalog.valueOfHexSingle(0);
		an2 = WifeDataAnalog.valueOfHexSingle(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;HEX_SINGLE_WORD}", an0.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(WORD_0001);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_SINGLE_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_SINGLE_WORD}", an0.toString());
		// バイト配列に変換
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(WORD_0001, tba));
		an0 = (WifeDataAnalog) an0.valueOf(WORD_0100);
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(WORD_0100, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(65536);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("FFFF"));
		assertEquals(65535, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("FFFF"),
				an1.toByteArray()));
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("0000"));
		assertEquals(0, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("0000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.HEX_DOUBLE_WORDのテスト
		an0 = WifeDataAnalog.valueOfHexDouble(0);
		an1 = WifeDataAnalog.valueOfHexDouble(0);
		an2 = WifeDataAnalog.valueOfHexDouble(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;HEX_DOUBLE_WORD}", an0.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(DWORD_00010000);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_DOUBLE_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;HEX_DOUBLE_WORD}", an0.toString());
		// バイト配列に変換
		an0 = (WifeDataAnalog) an0.valueOf(DWORD_00000001);
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(DWORD_00000001, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(4294967296D);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 =
			(WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("FFFFFFFF"));
		assertEquals(4294967295D, an1.doubleValue(), 4294967295D);
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("FFFFFFFF"),
				an1.toByteArray()));
		an1 =
			(WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("00000000"));
		assertEquals(0D, an1.doubleValue(), 0D);
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("00000000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.BCD_ZERO_HALF_WORDのテスト
		an0 = WifeDataAnalog.valueOfBcdZeroHalf(0);
		an1 = WifeDataAnalog.valueOfBcdZeroHalf(0);
		an2 = WifeDataAnalog.valueOfBcdZeroHalf(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;BCD_ZERO_HALF_WORD}", an0.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(WORD_0100);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_ZERO_HALF_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_ZERO_HALF_WORD}", an0.toString());
		// バイト配列に変換
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(WORD_0100, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(256);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("9999"));
		assertEquals(99, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("9900"),
				an1.toByteArray()));
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("0000"));
		assertEquals(0, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("0000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.BCD_ONE_HALF_WORDのテスト
		an0 = WifeDataAnalog.valueOfBcdOneHalf(0);
		an1 = WifeDataAnalog.valueOfBcdOneHalf(0);
		an2 = WifeDataAnalog.valueOfBcdOneHalf(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;BCD_ONE_HALF_WORD}", an0.toString());
		//0 byte目をアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(WORD_0001);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_ONE_HALF_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_ONE_HALF_WORD}", an0.toString());
		// バイト配列に変換
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(WORD_0001, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(256);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("9999"));
		assertEquals(99, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("0099"),
				an1.toByteArray()));
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("0000"));
		assertEquals(0, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("0000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.BCD_SINGLE_WORDのテスト
		an0 = WifeDataAnalog.valueOfBcdSingle(0);
		an1 = WifeDataAnalog.valueOfBcdSingle(0);
		an2 = WifeDataAnalog.valueOfBcdSingle(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;BCD_SINGLE_WORD}", an0.toString());
		//1 wordをアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(WORD_0001);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_SINGLE_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_SINGLE_WORD}", an0.toString());
		// バイト配列に変換
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(WORD_0001, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(65536);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("9999"));
		assertEquals(9999, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("9999"),
				an1.toByteArray()));
		an1 = (WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("0000"));
		assertEquals(0, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("0000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.BCD_DOUBLE_WORDのテスト
		an0 = WifeDataAnalog.valueOfBcdDouble(0);
		an1 = WifeDataAnalog.valueOfBcdDouble(0);
		an2 = WifeDataAnalog.valueOfBcdDouble(1);
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;BCD_DOUBLE_WORD}", an0.toString());
		//1 wordをアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(DWORD_00010000);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_DOUBLE_WORD}", an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		assertEquals("{1.0;BCD_DOUBLE_WORD}", an0.toString());
		// バイト配列に変換
		tba = an0.toByteArray();
		assertTrue(Arrays.equals(DWORD_00010000, tba));
		try {
			an0 = (WifeDataAnalog) an0.valueOf(4294967296D);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		an1 =
			(WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("99999999"));
		assertEquals(99999999, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("99999999"),
				an1.toByteArray()));
		an1 =
			(WifeDataAnalog) an0.valueOf(WifeUtilities.toByteArray("00000000"));
		assertEquals(0, an1.intValue());
		assertTrue(
			Arrays.equals(
				WifeUtilities.toByteArray("00000000"),
				an1.toByteArray()));

		//WifeDataAnalog.AnalogType.FLOAT_DOUBLE_WORDのテスト
		an0 = WifeDataAnalog.valueOfFloat(0);
		an1 = WifeDataAnalog.valueOfFloat(0);
		an2 = WifeDataAnalog.valueOfFloat(Float.intBitsToFloat(0x7f7fffff));
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;FLOAT_DOUBLE_WORD}", an0.toString());
		//1 wordをアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(FLOAT_BIT_INT);
		assertEquals(Float.intBitsToFloat(0x7f7fffff), an0.doubleValue(), 1F);
		assertEquals(an0, an2);
		//		System.out.println(an0.toString());
		// 既にあるインスタンスよりアナログ値を生成
		an0 = (WifeDataAnalog) an0.valueOf(1.0D);
		an2 = (WifeDataAnalog) an0.valueOf(1.0D);
		assertEquals(1.0D, an0.doubleValue(), 1.0D);
		assertEquals(an0, an2);
		// バイト配列に変換
		an0 = (WifeDataAnalog) an0.valueOf(FLOAT_BIT_INT);
		tba = an0.toByteArray();
		//		System.out.println("tba : " + hexDump.encodeBuffer(tba));
		assertTrue(Arrays.equals(FLOAT_BIT_INT, tba));

		//WifeDataAnalog.AnalogType.DOUBLE_FOURTH_WORDのテスト
		an0 = WifeDataAnalog.valueOfDouble(0);
		an1 = WifeDataAnalog.valueOfDouble(0);
		an2 =
			WifeDataAnalog.valueOfDouble(
				Double.longBitsToDouble(0x7fefffffffffffffL));
		assertEquals(an0, an1);
		assertEquals(an0.hashCode(), an1.hashCode());
		assertEquals(an0.toString(), an1.toString());
		assertEquals("{0.0;DOUBLE_FOURTH_WORD}", an0.toString());
		//1 wordをアナログデータに変換する。1になる。
		an0 = (WifeDataAnalog) an0.valueOf(DOUBLE_BIT_LONG);
		assertEquals(
			Double.longBitsToDouble(0x7fefffffffffffffL),
			an0.doubleValue(),
			1D);
		assertEquals(an0.doubleValue(), an2.doubleValue(), 1D);
		assertEquals(an0, an2);
		//		System.out.println(an0.toString());
		// バイト配列に変換
		tba = an0.toByteArray();
		//		System.out.println("tba : " + hexDump.encodeBuffer(tba));
		assertTrue(Arrays.equals(DOUBLE_BIT_LONG, tba));

		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		WifeDataAnalog d2 = null;
		try {
			ObjectOutputStream outs =
				new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(an2);
			outs.flush();
			outs.close();
			ObjectInputStream ins =
				new ObjectInputStream(new FileInputStream(temp));
			d2 = (WifeDataAnalog) ins.readObject();
			ins.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(d2);
		assertEquals(an2, d2);

		//equalsのテスト
		int MAX_ENUM = 10;
		WifeData[] wdas = new WifeData[MAX_ENUM];
		wdas[0] = WifeDataAnalog.valueOfBcdZeroHalf(0);
		wdas[1] = WifeDataAnalog.valueOfBcdOneHalf(0);
		wdas[2] = WifeDataAnalog.valueOfBcdSingle(0);
		wdas[3] = WifeDataAnalog.valueOfBcdDouble(0);
		wdas[4] = WifeDataAnalog.valueOfHexZeroHalf(0);
		wdas[5] = WifeDataAnalog.valueOfHexOneHalf(0);
		wdas[6] = WifeDataAnalog.valueOfHexSingle(0);
		wdas[7] = WifeDataAnalog.valueOfHexDouble(0);
		wdas[8] = WifeDataAnalog.valueOfFloat(0);
		wdas[9] = WifeDataAnalog.valueOfDouble(0);

		for (int i = 0; i < MAX_ENUM; i++) {
			for (int j = 0; j < MAX_ENUM; j++) {
				if (i == j) {
					assertTrue(wdas[i].equals(wdas[j]));
				} else {
					assertTrue(!wdas[i].equals(wdas[j]));
				}
			}
		}

	}

	/**
	 * 任意byte毎に入れ替えたbyte配列を返します。
	 * @param byteArray 元になる配列。
	 * @param size 入れ替え基準サイズ。
	 */
	byte[] swapBytes(byte[] byteArray, int size) {
		byte[] result = new byte[byteArray.length];
		System.arraycopy(byteArray, size, result, 0, size);
		System.arraycopy(byteArray, 0, result, size, size);
		return result;
	}

	/**
	 * 16進数 1/2ワード 0バイト目使用のテスト
	 */
	public void testHexZeroHelfWord() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 255;
		minAnalog = WifeDataAnalog.valueOfHexZeroHalf(minValue);
		maxAnalog = WifeDataAnalog.valueOfHexZeroHalf(maxValue);
		minByteArray = WifeUtilities.toByteArray("0000");
		maxByteArray = WifeUtilities.toByteArray("FFFF");
		verifyByteArray = WifeUtilities.toByteArray("FF00");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = 128;
		midAnalog = WifeDataAnalog.valueOfHexZeroHalf(midValue);
		midByteArray = WifeUtilities.toByteArray("8000");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfHexZeroHalf(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfHexZeroHalf(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * 16進数 1/2ワード 1バイト目使用のテスト
	 */
	public void testHexOneHelfWord() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 255;
		minAnalog = WifeDataAnalog.valueOfHexOneHalf(minValue);
		maxAnalog = WifeDataAnalog.valueOfHexOneHalf(maxValue);
		minByteArray = WifeUtilities.toByteArray("0000");
		maxByteArray = WifeUtilities.toByteArray("FFFF");
		verifyByteArray = WifeUtilities.toByteArray("00FF");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = 128;
		midAnalog = WifeDataAnalog.valueOfHexOneHalf(midValue);
		midByteArray = WifeUtilities.toByteArray("0080");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfHexOneHalf(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfHexOneHalf(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * 16進数 1ワードのテスト
	 */
	public void testHexSingle() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 65535;
		minAnalog = WifeDataAnalog.valueOfHexSingle(minValue);
		maxAnalog = WifeDataAnalog.valueOfHexSingle(maxValue);
		minByteArray = WifeUtilities.toByteArray("0000");
		maxByteArray = WifeUtilities.toByteArray("FFFF");
		verifyByteArray = WifeUtilities.toByteArray("FFFF");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = 32768;
		midAnalog = WifeDataAnalog.valueOfHexSingle(midValue);
		midByteArray = WifeUtilities.toByteArray("8000");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfHexSingle(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfHexSingle(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * 16進数 2ワードのテスト
	 */
	public void testHexDouble() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 4294967295D;
		minAnalog = WifeDataAnalog.valueOfHexDouble(minValue);
		maxAnalog = WifeDataAnalog.valueOfHexDouble(maxValue);
		minByteArray = WifeUtilities.toByteArray("00000000");
		maxByteArray = WifeUtilities.toByteArray("FFFFFFFF");
		verifyByteArray = WifeUtilities.toByteArray("FFFFFFFF");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("0000000000");
		size = 2;
		midValue = 2147483648D;
		midAnalog = WifeDataAnalog.valueOfHexDouble(midValue);
		midByteArray = WifeUtilities.toByteArray("00008000");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfHexDouble(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfHexDouble(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * 符号あり16進数 1/2ワード 0バイト目使用のテスト
	 */
	public void testShxZeroHelfWord() throws Exception {
		//インスタンス生成時引数チェック
		minValue = -128;
		maxValue = 127;
		minAnalog = WifeDataAnalog.valueOfShxZeroHalf(minValue);
		maxAnalog = WifeDataAnalog.valueOfShxZeroHalf(maxValue);
		minByteArray = WifeUtilities.toByteArray("8000");
		maxByteArray = WifeUtilities.toByteArray("7FFF");
		verifyByteArray = WifeUtilities.toByteArray("7F00");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = -1;
		midAnalog = WifeDataAnalog.valueOfShxZeroHalf(midValue);
		midByteArray = WifeUtilities.toByteArray("ff00");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfShxZeroHalf(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfShxZeroHalf(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * 16進数 1/2ワード 1バイト目使用のテスト
	 */
	public void testShxOneHelfWord() throws Exception {
		//インスタンス生成時引数チェック
		minValue = -128;
		maxValue = 127;
		minAnalog = WifeDataAnalog.valueOfShxOneHalf(minValue);
		maxAnalog = WifeDataAnalog.valueOfShxOneHalf(maxValue);
		minByteArray = WifeUtilities.toByteArray("0080");
		maxByteArray = WifeUtilities.toByteArray("FF7F");
		verifyByteArray = WifeUtilities.toByteArray("007F");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = -1;
		midAnalog = WifeDataAnalog.valueOfShxOneHalf(midValue);
		midByteArray = WifeUtilities.toByteArray("00ff");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfShxOneHalf(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfShxOneHalf(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * 16進数 1ワードのテスト
	 */
	public void testShxSingle() throws Exception {
		//インスタンス生成時引数チェック
		minValue = -32768;
		maxValue = 32767;
		minAnalog = WifeDataAnalog.valueOfShxSingle(minValue);
		maxAnalog = WifeDataAnalog.valueOfShxSingle(maxValue);
		minByteArray = WifeUtilities.toByteArray("8000");
		maxByteArray = WifeUtilities.toByteArray("7FFF");
		verifyByteArray = WifeUtilities.toByteArray("7FFF");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = -1;
		midAnalog = WifeDataAnalog.valueOfShxSingle(midValue);
		midByteArray = WifeUtilities.toByteArray("ffff");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfShxSingle(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfShxSingle(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * 16進数 2ワードのテスト
	 */
	public void testShxDouble() throws Exception {
		//インスタンス生成時引数チェック
		minValue = -2147483648;
		maxValue = 2147483647;
		minAnalog = WifeDataAnalog.valueOfShxDouble(minValue);
		maxAnalog = WifeDataAnalog.valueOfShxDouble(maxValue);
		minByteArray = WifeUtilities.toByteArray("00008000");
		maxByteArray = WifeUtilities.toByteArray("FFFF7FFF");
		verifyByteArray = WifeUtilities.toByteArray("FFFF7FFF");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("0000000000");
		size = 2;
		midValue = -1;
		midAnalog = WifeDataAnalog.valueOfShxDouble(midValue);
		midByteArray = WifeUtilities.toByteArray("ffffffff");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfShxDouble(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfShxDouble(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * BCD進数 1/2ワード 0バイト目使用のテスト
	 */
	public void testBcdZeroHelfWord() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 99;
		minAnalog = WifeDataAnalog.valueOfBcdZeroHalf(minValue);
		maxAnalog = WifeDataAnalog.valueOfBcdZeroHalf(maxValue);
		minByteArray = WifeUtilities.toByteArray("0000");
		maxByteArray = WifeUtilities.toByteArray("9999");
		verifyByteArray = WifeUtilities.toByteArray("9900");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = 80;
		midAnalog = WifeDataAnalog.valueOfBcdZeroHalf(midValue);
		midByteArray = WifeUtilities.toByteArray("8000");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfBcdZeroHalf(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfBcdZeroHalf(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * BCD進数 1/2ワード 1バイト目使用のテスト
	 */
	public void testBcdOneHelfWord() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 99;
		minAnalog = WifeDataAnalog.valueOfBcdOneHalf(minValue);
		maxAnalog = WifeDataAnalog.valueOfBcdOneHalf(maxValue);
		minByteArray = WifeUtilities.toByteArray("0000");
		maxByteArray = WifeUtilities.toByteArray("9999");
		verifyByteArray = WifeUtilities.toByteArray("0099");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = 80;
		midAnalog = WifeDataAnalog.valueOfBcdOneHalf(midValue);
		midByteArray = WifeUtilities.toByteArray("0080");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfBcdOneHalf(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfBcdOneHalf(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * BCD進数 1ワードのテスト
	 */
	public void testBcdSingle() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 9999;
		minAnalog = WifeDataAnalog.valueOfBcdSingle(minValue);
		maxAnalog = WifeDataAnalog.valueOfBcdSingle(maxValue);
		minByteArray = WifeUtilities.toByteArray("0000");
		maxByteArray = WifeUtilities.toByteArray("9999");
		verifyByteArray = WifeUtilities.toByteArray("9999");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("000000");
		size = 1;
		midValue = 8000;
		midAnalog = WifeDataAnalog.valueOfBcdSingle(midValue);
		midByteArray = WifeUtilities.toByteArray("8000");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfBcdSingle(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfBcdSingle(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * BCD進数 2ワードのテスト
	 */
	public void testBcdDouble() throws Exception {
		//インスタンス生成時引数チェック
		minValue = 0;
		maxValue = 99999999;
		minAnalog = WifeDataAnalog.valueOfBcdDouble(minValue);
		maxAnalog = WifeDataAnalog.valueOfBcdDouble(maxValue);
		minByteArray = WifeUtilities.toByteArray("00000000");
		maxByteArray = WifeUtilities.toByteArray("99999999");
		verifyByteArray = WifeUtilities.toByteArray("99999999");
		IligalByteArray1 = WifeUtilities.toByteArray("00");
		IligalByteArray2 = WifeUtilities.toByteArray("0000000000");
		size = 2;
		midValue = 80000000;
		midAnalog = WifeDataAnalog.valueOfBcdDouble(midValue);
		midByteArray = WifeUtilities.toByteArray("00008000");
		scenario();
		scenario2();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfBcdDouble(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfBcdDouble(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * Float のテスト
	 */
	public void testFloat() throws Exception {
		//インスタンス生成時引数チェック
		minValue = Float.MIN_VALUE;
		maxValue = Float.MAX_VALUE;
		minAnalog = WifeDataAnalog.valueOfFloat(minValue);
		maxAnalog = WifeDataAnalog.valueOfFloat(maxValue);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DataOutputStream oos = new DataOutputStream(os);
		oos.writeFloat(Float.MAX_VALUE);
		oos.flush();
		maxByteArray = swapBytes(os.toByteArray(), 2);
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeFloat(Float.MIN_VALUE);
		oos.flush();
		minByteArray = swapBytes(os.toByteArray(), 2);
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeFloat(Float.POSITIVE_INFINITY);
		oos.flush();
		IligalByteArray1 = swapBytes(os.toByteArray(), 2);
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeFloat(Float.NEGATIVE_INFINITY);
		oos.flush();
		IligalByteArray2 = swapBytes(os.toByteArray(), 2);
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeFloat(Float.NaN);
		oos.flush();
		byte[] IligalByteArray3 = swapBytes(os.toByteArray(), 2);
		oos.close();

		verifyByteArray = WifeUtilities.toByteArray("ffff7f7f");

		size = 2;
		midValue = -1.0;
		midAnalog = WifeDataAnalog.valueOfFloat(midValue);
		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeFloat(-1.0f);
		oos.flush();
		midByteArray = swapBytes(os.toByteArray(), 2);
		oos.close();
		scenario();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfFloat(Float.NaN);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfFloat(Float.NEGATIVE_INFINITY);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfFloat(Float.POSITIVE_INFINITY);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			maxAnalog.valueOf(IligalByteArray1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			maxAnalog.valueOf(IligalByteArray2);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			maxAnalog.valueOf(IligalByteArray3);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	/**
	 * Double のテスト
	 */
	public void testDouble() throws Exception {
		//インスタンス生成時引数チェック
		minValue = Double.MIN_VALUE;
		maxValue = Double.MAX_VALUE;
		minAnalog = WifeDataAnalog.valueOfDouble(minValue);
		maxAnalog = WifeDataAnalog.valueOfDouble(maxValue);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DataOutputStream oos = new DataOutputStream(os);
		oos.writeDouble(Double.MAX_VALUE);
		oos.flush();
		maxByteArray = os.toByteArray();
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeDouble(Double.MIN_VALUE);
		oos.flush();
		minByteArray = os.toByteArray();
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeDouble(Double.POSITIVE_INFINITY);
		oos.flush();
		IligalByteArray1 = os.toByteArray();
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeDouble(Double.NEGATIVE_INFINITY);
		oos.flush();
		IligalByteArray2 = os.toByteArray();
		oos.close();

		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeDouble(Double.NaN);
		oos.flush();
		byte[] IligalByteArray3 = os.toByteArray();
		oos.close();

		verifyByteArray = WifeUtilities.toByteArray("7fefffffffffffff");

		size = 4;
		midValue = -1.0;
		midAnalog = WifeDataAnalog.valueOfDouble(midValue);
		os = new ByteArrayOutputStream();
		oos = new DataOutputStream(os);
		oos.writeDouble(-1.0);
		oos.flush();
		midByteArray = os.toByteArray();
		oos.close();
		scenario();
		//インスタンス生成時引数チェック
		try {
			WifeDataAnalog.valueOfFloat(Double.NaN);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfFloat(Double.NEGATIVE_INFINITY);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			WifeDataAnalog.valueOfFloat(Double.POSITIVE_INFINITY);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			maxAnalog.valueOf(IligalByteArray1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			maxAnalog.valueOf(IligalByteArray2);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			maxAnalog.valueOf(IligalByteArray3);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	private void scenario() throws Exception {
		verify(minAnalog, minValue);
		assertTrue(Arrays.equals(minByteArray, minAnalog.toByteArray()));
		verify(maxAnalog, maxValue);
		assertTrue(Arrays.equals(verifyByteArray, maxAnalog.toByteArray()));

		minAnalog = (WifeDataAnalog) minAnalog.valueOf(minByteArray);
		maxAnalog = (WifeDataAnalog) maxAnalog.valueOf(maxByteArray);
		verify(minAnalog, minValue);
		assertTrue(Arrays.equals(minByteArray, minAnalog.toByteArray()));
		verify(maxAnalog, maxValue);
		assertTrue(Arrays.equals(verifyByteArray, maxAnalog.toByteArray()));

		minAnalog = (WifeDataAnalog) minAnalog.valueOf(minValue);
		maxAnalog = (WifeDataAnalog) maxAnalog.valueOf(maxValue);
		verify(minAnalog, minValue);
		assertTrue(Arrays.equals(minByteArray, minAnalog.toByteArray()));
		verify(maxAnalog, maxValue);
		assertTrue(Arrays.equals(verifyByteArray, maxAnalog.toByteArray()));

		verify(midAnalog, midValue);
		byte[] by = midAnalog.toByteArray();
		assertTrue(WifeUtilities.toString(by, by.length), Arrays.equals(midByteArray, by));
		midAnalog = (WifeDataAnalog) midAnalog.valueOf(midByteArray);
		verify(midAnalog, midValue);
		by = midAnalog.toByteArray();
		assertTrue(WifeUtilities.toString(by, by.length), Arrays.equals(midByteArray, by));
	}

	private void scenario2() throws Exception {
		//インスタンス生成時引数チェック
		try {
			minAnalog.valueOf(minValue - 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			maxAnalog.valueOf(maxValue + 1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			minAnalog.valueOf(IligalByteArray1);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
		try {
			minAnalog.valueOf(IligalByteArray2);
			fail();
		} catch (java.lang.IllegalArgumentException ex) {
		}
	}

	private void verify(WifeDataAnalog an, double ex) throws Exception {
		//比較、ハッシュ
		assertEquals(an.valueOf(ex), an);
		assertEquals(an.valueOf(ex).hashCode(), an.hashCode());

		//値取得
		assertEquals((byte) ex, an.byteValue());
		assertEquals((short) ex, an.shortValue());
		assertEquals((int) ex, an.intValue());
		assertEquals((long) ex, an.longValue());
		assertEquals(ex, an.doubleValue(), 0);
		assertEquals((float) ex, an.floatValue(), 0);

		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		WifeDataAnalog d = null;
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(an);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		d = (WifeDataAnalog) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(an, d);

		//サイズのテスト
		assertEquals(size, an.getWordSize());

	}
}
