/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet.alarm.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;

import junit.framework.TestCase;

import org.F11.scada.xwife.server.AlarmDataProvider;

public class CheckEventTest extends TestCase {

	public CheckEventTest(String arg0) {
		super(arg0);
	}

	public void testSetTimestamp() throws Exception {
		CheckEvent evt = new CheckEvent(
				AlarmDataProvider.NONCHECK,
				new TestTableModel(),
				0,
				new Timestamp(System.currentTimeMillis()));
		evt.setTimestamp(new Timestamp(0));
		assertEquals("NOCHECKになる", AlarmDataProvider.NONCHECK, evt.getSource());
	}

	//デシリアライズのテスト。
	public void testDeSerialize() throws Exception {
		CheckEvent evt = new CheckEvent(
				AlarmDataProvider.NONCHECK,
				new TestTableModel(),
				0,
				new Timestamp(System.currentTimeMillis()));

		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		CheckEvent e = null;
		ObjectOutputStream outs = null;
		ObjectInputStream ins = null;
		try {
			outs = new ObjectOutputStream(new FileOutputStream(temp));
			outs.writeObject(evt);
			outs.flush();
			ins = new ObjectInputStream(new FileInputStream(temp));
			e = (CheckEvent) ins.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		} finally {
			if (outs != null) {
				outs.close();
			}
			if (ins != null) {
				ins.close();
			}
			temp.delete();
		}
		assertNotNull(e);
	}

	public void testEqualsKey() throws Exception {
		CheckEvent evt1 = new CheckEvent(
				AlarmDataProvider.NONCHECK,
				new TestTableModel(),
				0,
				new Timestamp(0));
		CheckEvent evt2 = new CheckEvent(
				AlarmDataProvider.NONCHECK,
				new TestTableModel(),
				0,
				new Timestamp(1));
		assertTrue(evt1.equalsKey(evt2));

		TestTableModel model = new TestTableModel();
		model.setValueAt("P2", 0, 5);
		evt2 = new CheckEvent(
				AlarmDataProvider.NONCHECK,
				model,
				0,
				new Timestamp(1));
		assertFalse(evt1.equalsKey(evt2));
	}
}
