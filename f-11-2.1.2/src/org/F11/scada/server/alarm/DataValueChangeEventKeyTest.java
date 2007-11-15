/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/DataValueChangeEventKeyTest.java,v 1.3.2.1 2004/11/29 07:12:48 frdm Exp $
 * $Revision: 1.3.2.1 $
 * $Date: 2004/11/29 07:12:48 $
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
package org.F11.scada.server.alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataValueChangeEvent;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * DataValueChangeEventKeyのテストケースです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DataValueChangeEventKeyTest extends TestCase {
	DataValueChangeEventKey onKey;
	DataValueChangeEventKey offKey;
	Date createDate;

	/**
	 * Constructor for DataBaseKeyTest.
	 * @param arg0
	 */
	public DataValueChangeEventKeyTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		DataProvider dp = TestUtil.createDataProvider2();
		DataHolder dh = dp.getDataHolder("D_1900002_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		createDate = date;
		onKey = new  DataValueChangeEventKey(
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD));
		offKey = new DataValueChangeEventKey(
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD));
	}

	public void testDataBaseKey() throws Exception {
		DataProvider dp = TestUtil.createDataProvider2();
		DataHolder dh = dp.getDataHolder("D_500_BcdSingle");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		try {
			new DataValueChangeEventKey(
				new DataValueChangeEvent(
					dh,
					WifeDataAnalog.valueOfBcdSingle(0),
					date,
					WifeQualityFlag.GOOD));
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetPoint() {
		assertEquals(1, onKey.getPoint());
		assertEquals(1, offKey.getPoint());
	}

	public void testGetProvider() {
		assertEquals("P1", onKey.getProvider());
		assertEquals("P1", offKey.getProvider());
	}

	public void testGetHolder() {
		assertEquals("D_1900002_Digital", onKey.getHolder());
		assertEquals("D_1900002_Digital", offKey.getHolder());
	}

	public void testGetValue() {
		assertEquals(Boolean.TRUE, onKey.getValue());
		assertEquals(Boolean.FALSE, offKey.getValue());
	}

	public void testGetTimeStamp() {
		assertEquals(createDate, onKey.getTimeStamp());
		assertEquals(createDate, offKey.getTimeStamp());
		Timestamp ts = onKey.getTimeStamp();
		ts.setTime(0);
		assertEquals(createDate, onKey.getTimeStamp());
	}

	public void testSetTimeStamp() {
		DataValueChangeEventKey key = onKey.setTimeStamp(new Timestamp(0));
		assertEquals(new Timestamp(0), key.getTimeStamp());
		
		try {
			key = onKey.setTimeStamp(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}
	
	public void testToString() {
		Timestamp ts = new Timestamp(createDate.getTime());
		assertEquals("point=1,provider=P1,holder=D_1900002_Digital,value=true,date=" + ts.toString(), onKey.toString());
	}
	
	public void testSerialize() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(onKey);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		DataValueChangeEventKey d = (DataValueChangeEventKey) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(onKey, d);
		assertEquals(onKey.hashCode(), d.hashCode());

		assertEquals(onKey.getPoint(), d.getPoint());
		assertEquals(onKey.getProvider(), d.getProvider());
		assertEquals(onKey.getHolder(), d.getHolder());
		assertEquals(onKey.getValue(), d.getValue());
		assertEquals(onKey.getTimeStamp(), d.getTimeStamp());

		temp.delete();
	}

}
