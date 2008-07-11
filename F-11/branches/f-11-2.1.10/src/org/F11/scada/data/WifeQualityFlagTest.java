/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeQualityFlagTest.java,v 1.5.2.1 2004/11/29 07:12:47 frdm Exp $
 * $Revision: 1.5.2.1 $
 * $Date: 2004/11/29 07:12:47 $
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

import jp.gr.javacons.jim.QualityFlag;
import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeQualityFlagTest extends TestCase {

	/**
	 * Constructor for WifeQualityFlagTest.
	 * @param arg0
	 */
	public WifeQualityFlagTest(String arg0) {
		super(arg0);
	}

	public void testWifeQualityFlag() throws IOException {
		assertSame(WifeQualityFlag.BAD, WifeQualityFlag.BAD);
		assertSame(WifeQualityFlag.GOOD, WifeQualityFlag.GOOD);
		assertSame(WifeQualityFlag.INITIAL, WifeQualityFlag.INITIAL);
		assertSame(WifeQualityFlag.UNCERTAIN, WifeQualityFlag.UNCERTAIN);

		assertEquals(QualityFlag.BAD, WifeQualityFlag.BAD.getQuality());
		assertEquals(QualityFlag.GOOD, WifeQualityFlag.GOOD.getQuality());
		assertEquals(2, WifeQualityFlag.INITIAL.getQuality());
		assertEquals(QualityFlag.UNCERTAIN, WifeQualityFlag.UNCERTAIN.getQuality());

		assertEquals("BAD", WifeQualityFlag.BAD.toString());
		assertEquals("GOOD", WifeQualityFlag.GOOD.toString());
		assertEquals("INITIAL", WifeQualityFlag.INITIAL.toString());
		assertEquals("UNCERTAIN", WifeQualityFlag.UNCERTAIN.toString());

		QualityFlag qf = (QualityFlag)WifeQualityFlag.INITIAL;
		assertSame(WifeQualityFlag.INITIAL, qf);

		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		WifeQualityFlag d2 = null;
		try {
			ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(qf);
			outs.flush();
			outs.close();
			ObjectInputStream ins = new ObjectInputStream(new FileInputStream(temp));
			d2 = (WifeQualityFlag)ins.readObject();
			ins.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
		assertNotNull(d2);
		assertEquals(WifeQualityFlag.INITIAL, d2);
		assertEquals(qf, d2);
	}
}
