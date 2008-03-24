/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataDigitalTest.java,v 1.6 2004/06/21 07:16:33 frdm Exp $
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataDigitalTest extends TestCase {

	/**
	 * Constructor for WifeDataDigitalTest.
	 * @param arg0
	 */
	public WifeDataDigitalTest(String arg0) {
		super(arg0);
	}
	// Add test methods here, they have to start with 'test' name.
	// for example:
	// public void testHello() {}
	public void testWifeDataDigital() throws IOException {
		WifeDataDigital d = WifeDataDigital.valueOfFalse(0);
		assertEquals("false", d.toString());
		byte[] b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(0);
		assertEquals("0", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x01, b[1]);

		d = WifeDataDigital.valueOfTrue(1);
		assertEquals("1", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x02, b[1]);

		d = WifeDataDigital.valueOfTrue(2);
		assertEquals("2", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x04, b[1]);

		d = WifeDataDigital.valueOfTrue(3);
		assertEquals("3", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x08, b[1]);

		d = WifeDataDigital.valueOfTrue(4);
		assertEquals("4", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x10, b[1]);

		d = WifeDataDigital.valueOfTrue(5);
		assertEquals("5", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x20, b[1]);

		d = WifeDataDigital.valueOfTrue(6);
		assertEquals("6", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x40, b[1]);

		d = WifeDataDigital.valueOfTrue(7);
		assertEquals("7", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x80, b[1]);

		d = WifeDataDigital.valueOfTrue(8);
		assertEquals("8", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x01, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(9);
		assertEquals("9", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x02, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(10);
		assertEquals("10", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x04, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(11);
		assertEquals("11", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x08, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(12);
		assertEquals("12", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x10, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(13);
		assertEquals("13", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x20, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(14);
		assertEquals("14", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x40, b[0]);
		assertEquals((byte)0x00, b[1]);

		d = WifeDataDigital.valueOfTrue(15);
		assertEquals("15", d.toString());
		b = d.toByteArray();
		assertEquals((byte)0x80, b[0]);
		assertEquals((byte)0x00, b[1]);

		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		WifeDataDigital d2 = null;
		try {
			ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(d);
			outs.flush();
			outs.close();
			ObjectInputStream ins = new ObjectInputStream(new FileInputStream(temp));
			d2 = (WifeDataDigital)ins.readObject();
			ins.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(d2);
		assertSame(d2, d);
		b = d.toByteArray();
		assertEquals((byte)0x80, b[0]);
		assertEquals((byte)0x00, b[1]);

		WifeDataDigital d3 = (WifeDataDigital)d2.valueOf(new byte[]{(byte)0xFF, (byte)0x00});
		b = d3.toByteArray();
		assertEquals((byte)0x80, b[0]);
		assertEquals((byte)0x00, b[1]);

		d3 = (WifeDataDigital)d2.valueOf(new byte[]{(byte)0x00, (byte)0x00});
		b = d3.toByteArray();
		assertEquals((byte)0x00, b[0]);
		assertEquals((byte)0x00, b[1]);

		assertTrue(d2.equals(d));

		//生成速度のテスト
		int MAX_DATA = 1000000;
		WifeData[] wd = new WifeData[MAX_DATA];
		System.out.println("生成開始" + new Date());
		for (int i = 0; i < MAX_DATA; i++) {
			wd[i] = WifeDataDigital.valueOfFalse(0);
		}
		System.out.println("生成終了" + new Date());
		//アクセス速度のテスト
		WifeDataDigital eq = WifeDataDigital.valueOfFalse(0);
		b = WifeUtilities.toByteArray("0000");
		System.out.println("アクセス開始" + new Date());
		for (int i = 0; i < MAX_DATA; i++) {
			if (wd[i].equals(eq)) {
				wd[i] = wd[i].valueOf(b);
			}
		}
		System.out.println("アクセス終了" + new Date());

		WifeData wdata = WifeDataDigital.valueOfTrue(0);
		byte[] cutdata = {(byte)0x00, (byte)0x01};
		WifeData cutwdata = wdata.valueOf(cutdata);
		System.out.println(wdata + " : " + cutwdata);
		assertEquals(wdata, wdata.valueOf(cutdata));

		wdata = WifeDataDigital.valueOfFalse(0);
		cutdata = new byte[]{(byte)0xFF, (byte)0xFE};
		cutwdata = wdata.valueOf(cutdata);
		assertEquals(wdata, cutwdata);
		System.out.println(wdata + " : " + cutwdata);

		wdata = WifeDataDigital.valueOfTrue(0);
		cutdata = new byte[]{(byte)0xFF, (byte)0xFF};
		cutwdata = wdata.valueOf(cutdata);
		assertEquals(wdata, cutwdata);
		System.out.println(wdata + " : " + cutwdata);
		cutdata = new byte[]{(byte)0x00, (byte)0x01};
		assertTrue(Arrays.equals(cutdata, cutwdata.toByteArray()));

		assertEquals(1, wdata.getWordSize());
		temp.delete();
	}
}
