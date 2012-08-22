/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/AlarmTableJournalTest.java,v 1.3.2.1 2004/11/29 07:12:48 frdm Exp $
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
import java.util.Arrays;
import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataValueChangeEvent;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * AlarmTableRowDataのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmTableJournalTest extends TestCase {
	AlarmTableJournal addData;
	AlarmTableJournal removeData;
	AlarmTableJournal modifyData;
	
	Date createDate;

	/**
	 * Constructor for AlarmTableJournalTest.
	 * @param arg0
	 */
	public AlarmTableJournalTest(String arg0) {
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
		Object[] rowData = {"A", "B"};
		addData =
			AlarmTableJournal.createRowDataAddOpe(
				new DataValueChangeEventKey(
					new DataValueChangeEvent(
						dh,
						WifeDataDigital.valueOfTrue(0),
						date,
						WifeQualityFlag.GOOD)),
				rowData);
		removeData =
			AlarmTableJournal.createRowDataRemoveOpe(
				new DataValueChangeEventKey(
					new DataValueChangeEvent(
						dh,
						WifeDataDigital.valueOfTrue(0),
						date,
						WifeQualityFlag.GOOD)),
				rowData);
				
		modifyData =
			AlarmTableJournal.createRowDataModifyOpe(
				new DataValueChangeEventKey(
					new DataValueChangeEvent(
						dh,
						WifeDataDigital.valueOfTrue(0),
						date,
						WifeQualityFlag.GOOD)),
				rowData);

// パラメータがnullの場合
		try {
			AlarmTableJournal.createRowDataAddOpe(null, rowData);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			AlarmTableJournal.createRowDataAddOpe(
				new DataValueChangeEventKey(
					new DataValueChangeEvent(
						dh,
						WifeDataDigital.valueOfTrue(0),
						date,
						WifeQualityFlag.GOOD)),
				null);
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			AlarmTableJournal.createRowDataRemoveOpe(null, rowData);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			AlarmTableJournal.createRowDataRemoveOpe(
				new DataValueChangeEventKey(
					new DataValueChangeEvent(
						dh,
						WifeDataDigital.valueOfTrue(0),
						date,
						WifeQualityFlag.GOOD)),
				null);
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			AlarmTableJournal.createRowDataModifyOpe(null, rowData);
			fail();
		} catch (IllegalArgumentException e) {
		}
		try {
			AlarmTableJournal.createRowDataModifyOpe(
				new DataValueChangeEventKey(
					new DataValueChangeEvent(
						dh,
						WifeDataDigital.valueOfTrue(0),
						date,
						WifeQualityFlag.GOOD)),
				null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetData() {
		Object[] rowData = {"A", "B"};
		assertTrue(Arrays.equals(addData.getData(), rowData));
		assertTrue(Arrays.equals(removeData.getData(), rowData));
		assertTrue(Arrays.equals(modifyData.getData(), rowData));
	}

	public void testGetOperationType() {
		assertEquals(AlarmTableJournal.INSERT_OPERATION, addData.getOperationType());
		assertEquals(AlarmTableJournal.REMOVE_OPERATION, removeData.getOperationType());
		assertEquals(AlarmTableJournal.MODIFY_OPERATION, modifyData.getOperationType());
	}

	public void testGetTimestamp() {
		assertEquals(createDate, addData.getTimestamp());
		assertEquals(createDate, removeData.getTimestamp());
		assertEquals(createDate, modifyData.getTimestamp());
	}

	public void testSetTimestamp() {
		AlarmTableJournal aj = addData.setTimestamp(new Timestamp(0));
		assertEquals(new Timestamp(0), aj.getTimestamp());
	}

	public void testGetPoint() {
		assertEquals(1, addData.getPoint());
		assertEquals(1, removeData.getPoint());
		assertEquals(1, modifyData.getPoint());
	}

	public void testGetProvider() {
		assertEquals("P1", addData.getProvider());
		assertEquals("P1", removeData.getProvider());
		assertEquals("P1", modifyData.getProvider());
	}

	public void testGetHolder() {
		assertEquals("D_1900002_Digital", addData.getHolder());
		assertEquals("D_1900002_Digital", removeData.getHolder());
		assertEquals("D_1900002_Digital", modifyData.getHolder());
	}

/*
	public void testIsSendData() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		assertTrue(addData.isSendData(ts));
		assertFalse(addData.isSendData(new Timestamp(createDate.getTime())));
	}
*/

	public void testSerialize() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(addData);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		AlarmTableJournal d = (AlarmTableJournal) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(addData, d);
		assertEquals(addData.hashCode(), d.hashCode());
		assertEquals(addData.getPoint(), d.getPoint());
		assertEquals(addData.getProvider(), d.getProvider());
		assertEquals(addData.getHolder(), d.getHolder());
		assertEquals(addData.getTimestamp(), d.getTimestamp());
		assertTrue(Arrays.equals(addData.getData(), d.getData()));
		assertEquals(addData.getOperationType(), d.getOperationType());

		temp.delete();
	}

}
