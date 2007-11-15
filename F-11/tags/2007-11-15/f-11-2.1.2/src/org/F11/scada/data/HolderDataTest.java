/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/HolderDataTest.java,v 1.5 2004/06/21 07:16:33 frdm Exp $
 * $Revision: 1.5 $
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
import java.util.HashMap;

import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class HolderDataTest extends TestCase {
	HolderData data;

	/**
	 * Constructor for HolderDataTest.
	 * @param arg0
	 */
	public HolderDataTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		HashMap map = new HashMap();
		map.put("A", "1");
		data = new HolderData("H1", new byte[]{(byte)0xFF, (byte)0x01}, 0, map);
	}

	public void testGetHolder() {
		assertEquals("H1", data.getHolder());
		byte[] b1 = new byte[]{(byte)0xFF, (byte)0x01};
		assertTrue(Arrays.equals(b1, data.getValue()));
		HashMap map = new HashMap();
		map.put("A", "1");
		HolderData hd = new HolderData("H1", new byte[]{(byte)0xFF, (byte)0x01}, 0, map);
		assertTrue(hd.equals(data));
		hd = new HolderData("H1", new byte[]{(byte)0xFF, (byte)0x01}, 0, map);
		assertEquals(hd.hashCode(), data.hashCode());
		assertEquals(0, data.getTime());
		assertEquals(map, data.getDemandData());
	}

	public void testSerialize() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(data);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		HolderData d = (HolderData) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(d, data);
		assertTrue(d.equals(data));
		temp.delete();
	}
}
