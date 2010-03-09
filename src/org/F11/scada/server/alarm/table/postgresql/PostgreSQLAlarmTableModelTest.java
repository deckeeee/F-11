/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/postgresql/PostgreSQLAlarmTableModelTest.java,v 1.7.2.5 2006/09/26 07:05:01 frdm Exp $
 * $Revision: 1.7.2.5 $
 * $Date: 2006/09/26 07:05:01 $
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
package org.F11.scada.server.alarm.table.postgresql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.swing.table.DefaultTableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataValueChangeEvent;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.applet.alarm.event.CheckTableListener;
import org.F11.scada.xwife.applet.alarm.event.TestTableModel;
import org.F11.scada.xwife.server.AlarmDataProvider;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * PostgreSQLAlarmTableModelのテストケースです。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLAlarmTableModelTest extends TestCase {
	static final String[] careerTitle =
		{
			"ジャンプパス",
			"自動ジャンプ",
			"優先順位",
			"表示色",
			"point",
			"provider",
			"holder",
			"サウンドパス",
			"Emailグループ",
			"Emailモード",
			"日時",
			"記号",
			"名称",
			"警報・状態" };
	private AlarmTableModel careerModel;

	private DataProvider dp;
	private Object[] careerData;
	private Timestamp createTime;

	/**
	 * Constructor for PostgreSQLAlarmTableModelTest.
	 *
	 * @param arg0
	 */
	public PostgreSQLAlarmTableModelTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		careerModel =
			PostgreSQLAlarmTableModel
					.createDefaultAlarmTableModel(new DefaultTableModel(
							careerTitle, 0), createTitleMap(), "");

		dp = TestUtil.createDataProvider2();
		createTime = new Timestamp(System.currentTimeMillis());
		careerData =
			new Object[] {
				"JP",
				Boolean.TRUE,
				new Integer(0),
				"RED",
				new Integer(0),
				"P1",
				"D_1900000_Digital",
				"SoundPath",
				"EMailGroup",
				new Integer(3),
				createTime,
				"AHU 1-1-1",
				"空調機ファン",
				"停止" };
	}

	private Map createTitleMap() {
		HashMap map = new HashMap(careerTitle.length);
		for (int i = 0; i < careerTitle.length; i++) {
			map.put(careerTitle[i], new Integer(i));
		}
		return map;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPostgreSQLAlarmTableModel() {
		try {
			PostgreSQLAlarmTableModel.createDefaultAlarmTableModel(null,
					createTitleMap(), "");
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetAlarmJournalCareer() {
		DataValueChangeEventKey evt =
			CreateEvent.create(dp, "D_1900000_Digital");

		careerModel.insertRow(0, careerData, evt);
		careerModel.insertRow(0, careerData, evt);
		Map map = careerModel.getAlarmJournal(0);
		assertEquals(2, map.size());
		List list = new ArrayList(map.values());
		AlarmTableJournal jn = (AlarmTableJournal) list.get(0);
		AlarmTableJournal jn2 =
			AlarmTableJournal.createRowDataAddOpe(evt, careerData);
		assertEquals(jn2.hashCode(), jn.hashCode());
		assertEquals(jn2, jn);

		Calendar cal = Calendar.getInstance();
		cal.set(9999, Calendar.DECEMBER, 31, 23, 59, 59);
		map = careerModel.getAlarmJournal(cal.getTimeInMillis());
		assertEquals(0, map.size());
	}

	public void testIsCellEditable() {
		assertFalse(careerModel.isCellEditable(0, 0));
	}

	/*
	 * Test for void setValueAt(Object[], int, int, DataValueChangeEventKey)
	 */
	public void testSetValueAt() {
		DataValueChangeEventKey evt =
			CreateEvent.create(dp, "D_1900000_Digital");
		careerModel.insertRow(0, careerData, evt);
		assertEquals("RED", careerModel.getValueAt(0, "表示色"));

		Timestamp ts = new Timestamp(0);
		Object[] newData =
			new Object[] {
				"JP",
				Boolean.TRUE,
				new Integer(0),
				"GREEN",
				new Integer(0),
				"P1",
				"D_1900000_Digital",
				"",
				"",
				new Integer(0),
				ts,
				"AHU 1-1-1",
				"空調機ファン",
				"停止" };
		careerModel.setValueAt(newData, 0, 3, evt);

		assertEquals("JP", careerModel.getValueAt(0, "ジャンプパス"));
		assertEquals(Boolean.TRUE, careerModel.getValueAt(0, "自動ジャンプ"));
		assertEquals(new Integer(0), careerModel.getValueAt(0, "優先順位"));
		assertEquals("GREEN", careerModel.getValueAt(0, "表示色"));
		assertEquals(new Integer(0), careerModel.getValueAt(0, "point"));
		assertEquals("P1", careerModel.getValueAt(0, "provider"));
		assertEquals("D_1900000_Digital", careerModel.getValueAt(0, "holder"));
		assertEquals("SoundPath", careerModel.getValueAt(0, "サウンドパス"));
		assertEquals("EMailGroup", careerModel.getValueAt(0, "Emailグループ"));
		assertEquals(new Integer(3), careerModel.getValueAt(0, "Emailモード"));
		assertEquals(createTime, careerModel.getValueAt(0, "日時"));
		assertFalse(ts.equals(careerModel.getValueAt(0, "日時")));
		assertEquals("AHU 1-1-1", careerModel.getValueAt(0, "記号"));
		assertEquals("空調機ファン", careerModel.getValueAt(0, "名称"));
		assertEquals("停止", careerModel.getValueAt(0, "警報・状態"));

	}

	public void testInsertRow() {
		assertEquals("3が返される", 3, careerModel.getColumn("表示色"));

		careerModel.insertRow(0, careerData, CreateEvent.create(dp,
				"D_1900000_Digital", createTime));
		assertEquals("JP", careerModel.getValueAt(0, "ジャンプパス"));
		assertEquals(Boolean.TRUE, careerModel.getValueAt(0, "自動ジャンプ"));
		assertEquals(new Integer(0), careerModel.getValueAt(0, "優先順位"));
		assertEquals("RED", careerModel.getValueAt(0, "表示色"));
		assertEquals(new Integer(0), careerModel.getValueAt(0, "point"));
		assertEquals("P1", careerModel.getValueAt(0, "provider"));
		assertEquals("D_1900000_Digital", careerModel.getValueAt(0, "holder"));
		assertEquals("SoundPath", careerModel.getValueAt(0, "サウンドパス"));
		assertEquals("EMailGroup", careerModel.getValueAt(0, "Emailグループ"));
		assertEquals(new Integer(3), careerModel.getValueAt(0, "Emailモード"));
		assertEquals(createTime, careerModel.getValueAt(0, "日時"));
		assertEquals("AHU 1-1-1", careerModel.getValueAt(0, "記号"));
		assertEquals("空調機ファン", careerModel.getValueAt(0, "名称"));
		assertEquals("停止", careerModel.getValueAt(0, "警報・状態"));

		Map ajsMap = careerModel.getAlarmJournal(0);
		assertEquals(1, ajsMap.size());
		List ajs = new ArrayList(ajsMap.values());

		AlarmTableJournal aj = (AlarmTableJournal) ajs.get(0);
		assertEquals(0, aj.getPoint());
		assertEquals("P1", aj.getProvider());
		assertEquals("D_1900000_Digital", aj.getHolder());
		assertEquals(AlarmTableJournal.INSERT_OPERATION, aj.getOperationType());
		assertEquals(createTime, aj.getTimestamp());
		assertTrue(Arrays.equals(careerData, aj.getData()));
	}

	public void testRemoveRow() {
		careerModel.insertRow(0, careerData, CreateEvent.create(dp,
				"D_1900000_Digital"));

		careerModel.removeRow(0, CreateEvent.create(dp, "D_1900000_Digital"));
		Map ajsMap = careerModel.getAlarmJournal(0);
		assertEquals(2, ajsMap.size());

		List ajs = new ArrayList(ajsMap.values());
		AlarmTableJournal aj = (AlarmTableJournal) ajs.get(0);
		assertEquals(0, aj.getPoint());
		assertEquals("P1", aj.getProvider());
		assertEquals("D_1900000_Digital", aj.getHolder());
		assertEquals(AlarmTableJournal.INSERT_OPERATION, aj.getOperationType());
		assertEquals(createTime, aj.getTimestamp());
		assertTrue(Arrays.equals(careerData, aj.getData()));

		aj = (AlarmTableJournal) ajs.get(1);
		assertEquals(0, aj.getPoint());
		assertEquals("P1", aj.getProvider());
		assertEquals("D_1900000_Digital", aj.getHolder());
		assertEquals(AlarmTableJournal.REMOVE_OPERATION, aj.getOperationType());
		// Calendar cal = Calendar.getInstance();
		// cal.setTime(createTime);
		// cal.add(Calendar.MILLISECOND, 1);
		// assertEquals(createTime, aj.getTimestamp());
		System.out.println(aj.getData());
		assertTrue(Arrays.equals(careerData, aj.getData()));
	}

	public void testSetValue() {
		careerModel.insertRow(0, careerData, CreateEvent.create(dp,
				"D_1900000_Digital"));

		Timestamp ts = new Timestamp(0);
		Object[] newData =
			new Object[] {
				"JP",
				Boolean.TRUE,
				new Integer(0),
				"GREEN",
				new Integer(0),
				"P1",
				"D_1900000_Digital",
				"",
				"",
				new Integer(0),
				ts,
				"AHU 1-1-1",
				"空調機ファン",
				"停止" };
		careerModel.setValueAt(newData, 0, 3, CreateEvent.create(dp,
				"D_1900000_Digital"));
		careerModel.removeRow(0, CreateEvent.create(dp, "D_1900000_Digital"));

		SortedMap ajsMap = careerModel.getAlarmJournal(0);
		assertEquals(3, ajsMap.size());

		AlarmTableModel model =
			PostgreSQLAlarmTableModel
					.createDefaultAlarmTableModel(new DefaultTableModel(
							careerTitle, 0), createTitleMap(), "");
		model.setValue(ajsMap);
		assertEquals(0, model.getRowCount());
	}

	public void testGetLastJournal() {
		careerModel.insertRow(0, careerData, CreateEvent.create(dp,
				"D_1900000_Digital"));
		AlarmTableJournal aj = careerModel.getLastJournal();
		assertNotNull(aj);
	}

	public void testSetJournal() throws Exception {
		Timestamp time = new Timestamp(1000);
		Object[] newData =
			new Object[] {
				"JP",
				Boolean.TRUE,
				new Integer(0),
				"GREEN",
				new Integer(0),
				"P1",
				"D_1900000_Digital",
				"",
				"",
				new Integer(0),
				time,
				"AHU 1-1-1",
				"空調機ファン",
				"停止" };
		careerModel.setJournal(AlarmTableJournal.createRowDataAddOpe(
				CreateEvent.create(dp, "D_1900000_Digital"), newData));
		SortedMap map = careerModel.getAlarmJournal(999);
		assertEquals("1件のジャーナル", 1, map.size());
	}

	public void testCheckEvent() throws Exception {
		TestCheckTableListener l = new TestCheckTableListener();
		careerModel.addCheckTableListener(l);
		careerModel.fireCheckEvent(new CheckEvent(AlarmDataProvider.NONCHECK,
				new TestTableModel(), 0, new Timestamp(999)));
		assertNotNull(l.getEvt());
		SortedMap map = careerModel.getCheckJournal(0);
		assertEquals("1件のジャーナル", 1, map.size());
		careerModel.fireCheckEvent(new CheckEvent(AlarmDataProvider.NONCHECK,
				new TestTableModel(Boolean.TRUE, "P3", "H10", new Timestamp(
						2000), new Integer(3)), 0, new Timestamp(1000)));
		CheckEvent evt = careerModel.getLastCheckEvent();
		assertNotNull(evt);
		assertEquals("最後のイベントが返される", new Timestamp(1000), evt.getTimestamp());
	}

	private static class CreateEvent {
		static DataValueChangeEventKey create(DataProvider dp, String holder) {
			DataHolder dh = dp.getDataHolder(holder);
			dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
			Date date = new Date();
			DataValueChangeEvent evt =
				new DataValueChangeEvent(dh, WifeDataDigital.valueOfTrue(0),
						date, WifeQualityFlag.GOOD);
			return new DataValueChangeEventKey(evt);
		}

		static DataValueChangeEventKey create(
			DataProvider dp,
			String holder,
			Timestamp time) {
			DataHolder dh = dp.getDataHolder(holder);
			dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
			Date date = new Date(time.getTime());
			DataValueChangeEvent evt =
				new DataValueChangeEvent(dh, WifeDataDigital.valueOfTrue(0),
						date, WifeQualityFlag.GOOD);
			return new DataValueChangeEventKey(evt);
		}
	}

	private static class TestCheckTableListener implements CheckTableListener {
		private CheckEvent evt;

		public void checkedEvent(CheckEvent evt) {
			this.evt = evt;
		}

		public CheckEvent getEvt() {
			return evt;
		}
	}
}
