/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/postgresql/PostgreSQLStrategyFactoryTest.java,v 1.4.2.6 2007/02/26 00:44:14 frdm Exp $
 * $Revision: 1.4.2.6 $
 * $Date: 2007/02/26 00:44:14 $
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

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataValueChangeEvent;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.alarm.table.RowDataStrategy;
import org.F11.scada.server.alarm.table.StrategyFactory;
import org.F11.scada.test.util.TestConnectionUtil;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

/**
 * PostgreSQLStrategyFactory及び生成するRowDataStrategyのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLStrategyFactoryTest extends DatabaseTestCase {
	private RowDataStrategy career;
	private RowDataStrategy history;
	private RowDataStrategy summary;
	private RowDataStrategy ocurrence;
	private RowDataStrategy check;
	
	private static final String[] careerTitle = {
		"ジャンプパス", "自動ジャンプ", "優先順位", "表示色" 
		,"point", "provider", "holder", "サウンドタイプ", "サウンドパス", "Emailグループ", "Emailモード"
		,"onoff", "日時", "記号", "名称", "警報・状態","種別","最新警報モード"
	};
	private static final String[] historyTitle = {
		"ジャンプパス", "自動ジャンプ", "優先順位", "表示色",
		"point", "provider", "holder", "発生・運転", "復旧・停止", "記号", "名称", "種別", "確認"
	};
	private static final String[] summaryTitle = {
		"ジャンプパス", "自動ジャンプ", "優先順位", "表示色",
		"point", "provider", "holder", "発生・運転", "復旧・停止",
		"記号", "名称", "警報・状態", "種別"
	};
	private static final String[] ocurrenceTitle = {
		"ジャンプパス", "自動ジャンプ", "優先順位", "表示色",
		"point", "provider", "holder", "発生・運転", "復旧・停止",
		"記号", "名称", "警報・状態", "種別"
	};
	private static final String[] checkTitle = {
		"ジャンプパス", "自動ジャンプ", "優先順位", "表示色",
		"point", "provider", "holder", "発生・運転", "復旧・停止", "記号", "名称", "種別", "確認"
	};
	private AlarmTableModel careerModel;
	private AlarmTableModel historyModel;
	private AlarmTableModel summaryModel;
	private AlarmTableModel ocurrenceModel;
	private AlarmTableModel checkModel;
	
	private DataProvider dp;

	/**
	 * Constructor for PostgreSQLStrategyFactoryTest.
	 * @param arg0
	 */
	public PostgreSQLStrategyFactoryTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		careerModel = PostgreSQLAlarmTableModel.createDefaultAlarmTableModel(
				new DefaultTableModel(careerTitle, 0),
				createTitleMap(careerTitle));
		historyModel = PostgreSQLAlarmTableModel.createHistoryAlarmTableModel(
				new DefaultTableModel(historyTitle, 0),
				createTitleMap(historyTitle));
		summaryModel = PostgreSQLAlarmTableModel.createDefaultAlarmTableModel(
				new DefaultTableModel(summaryTitle, 0),
				createTitleMap(summaryTitle));
		ocurrenceModel = PostgreSQLAlarmTableModel.createDefaultAlarmTableModel(
				new DefaultTableModel(ocurrenceTitle, 0),
				createTitleMap(ocurrenceTitle));
		checkModel = PostgreSQLAlarmTableModel.createHistoryAlarmTableModel(
				new DefaultTableModel(checkTitle, 0),
				createTitleMap(checkTitle));

		S2Container container = S2ContainerFactory.create("org/F11/scada/server/alarm/AlarmReferencer.dicon");
		StrategyFactory factory =
			(StrategyFactory) container.getComponent(StrategyFactory.class);

		career = factory.createCareerStrategy(careerModel);
		history = factory.createHistoryStrategy(historyModel);
		summary = factory.createSummaryStrategy(summaryModel);
		ocurrence = factory.createOccurrenceStrategy(ocurrenceModel);
		check = factory.createNoncheckStrategy(checkModel);

		dp = TestUtil.createDataProvider2();
	}

	private Map createTitleMap(String[] title) {
		HashMap map = new HashMap(title.length);
		for (int i = 0; i < title.length; i++) {
			map.put(title[i], new Integer(i));
		}
		return map;
	}

	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(TestConnectionUtil.getTestConnection());
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream("src/org/F11/scada/server/alarm/table/postgresql/database.xml"));
	}

	public void testCreateCareerStrategy0() throws Exception {
		// attribid = 0
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, careerModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, careerModel.getRowCount());
	}

	public void testCreateCareerStrategy1() throws Exception {
		// attribid = 1
		DataHolder dh = dp.getDataHolder("D_1900001_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, careerModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start + 1, careerModel.getRowCount());
	}

	public void testCreateCareerStrategy2() throws Exception {
		// attribid = 2
		DataHolder dh = dp.getDataHolder("D_1900002_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);

		int start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
/*
		assertEquals("/WifeProject/一覧表/状態一覧表", careerModel.getValueAt(0, 0));
		assertEquals(Boolean.TRUE, careerModel.getValueAt(0, 1));
		assertEquals(new Integer(0), careerModel.getValueAt(0, 2));
		assertNull(careerModel.getValueAt(0, 3));
		assertEquals(new Integer(1), careerModel.getValueAt(0, 4));
		assertEquals("P1", careerModel.getValueAt(0, 5));
		assertEquals("D_1900002_Digital", careerModel.getValueAt(0, 6));
		assertEquals(date, careerModel.getValueAt(0, 7));
		assertEquals("AHU-1-2", careerModel.getValueAt(0, 8));
		assertEquals("1F B室 空調機　", careerModel.getValueAt(0, 9));
		assertEquals("発生", careerModel.getValueAt(0, 10));
*/
		assertEquals(start + 1, careerModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, careerModel.getRowCount());
	}


	public void testCreateCareerStrategy3() throws Exception {
		// attribid = 3
		DataHolder dh = dp.getDataHolder("D_1900003_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start + 1, careerModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start + 1, careerModel.getRowCount());
	}

	public void testCreateCareerStrategy4() throws Exception {
		// attribid = 4
		DataHolder dh = dp.getDataHolder("D_1900004_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start + 1, careerModel.getRowCount());

		dh = dp.getDataHolder("D_1900004_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		date = new Date();
		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start - 1, careerModel.getRowCount());
	}

	public void testCreateCareerStrategy5() throws Exception {
		// attribid = 5
		DataHolder dh = dp.getDataHolder("D_1900005_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		int start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start + 1, careerModel.getRowCount());

		dh = dp.getDataHolder("D_1900005_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		date = new Date();
		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = careerModel.getRowCount();
		career.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start - 1, careerModel.getRowCount());
	}

	public void testCreateHistoryStrategy0() throws Exception {
		// attrib_id = 0
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, historyModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, historyModel.getRowCount());
	}

	public void testCreateHistoryStrategy1() throws Exception {
		// attrib_id = 1
		DataHolder dh = dp.getDataHolder("D_1900001_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		int start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertNull(historyModel.getValueAt(0, "発生・運転"));
		assertEquals(date, historyModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, historyModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, historyModel.getRowCount());
	}

	public void testCreateHistoryStrategy2() throws Exception {
		// attrib_id = 2
		DataHolder dh = dp.getDataHolder("D_1900002_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, historyModel.getValueAt(0, "発生・運転"));
		assertNull(historyModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, historyModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, historyModel.getRowCount());
	}

	public void testCreateHistoryStrategy3() throws Exception {
		// attrib_id = 3
		DataHolder dh = dp.getDataHolder("D_1900003_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, historyModel.getValueAt(0, "発生・運転"));
		assertNull(historyModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, historyModel.getRowCount());

		Date date2 = new Date();
		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date2,
				WifeQualityFlag.GOOD);
		start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, historyModel.getValueAt(0, "発生・運転"));
		assertEquals(date2, historyModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, historyModel.getRowCount());
	}

	public void testCreateHistoryStrategy4() throws Exception {
		// attrib_id = 4
		DataHolder dh = dp.getDataHolder("D_1900004_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, historyModel.getValueAt(0, "発生・運転"));
		assertNull(historyModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, historyModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start - 1, historyModel.getRowCount());
	}

	public void testCreateHistoryStrategy5() throws Exception {
		// attrib_id = 5
		DataHolder dh = dp.getDataHolder("D_1900005_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		int start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertNull(historyModel.getValueAt(0, "発生・運転"));
		assertEquals(date, historyModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, historyModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = historyModel.getRowCount();
		history.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start - 1, historyModel.getRowCount());
	}

	public void testCreateSummaryStrategy0() throws Exception {
		// attrib_id = 0
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		int start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, summaryModel.getRowCount());
	}

	public void testCreateSummaryStrategy1() throws Exception {
		// attrib_id = 1
		DataHolder dh = dp.getDataHolder("D_1900001_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		int start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertNull(summaryModel.getValueAt(0, "発生・運転"));
		assertEquals(date, summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertNull(summaryModel.getValueAt(0, "発生・運転"));
		assertEquals(date, summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, summaryModel.getRowCount());
	}

	public void testCreateSummaryStrategy2() throws Exception {
		// attrib_id = 2
		DataHolder dh = dp.getDataHolder("D_1900002_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, summaryModel.getValueAt(0, "発生・運転"));
		assertNull(summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, summaryModel.getValueAt(0, "発生・運転"));
		assertNull(summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start, summaryModel.getRowCount());
	}

	public void testCreateSummaryStrategy3() throws Exception {
		// attrib_id = 3
		DataHolder dh = dp.getDataHolder("D_1900003_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, summaryModel.getValueAt(0, "発生・運転"));
		assertNull(summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, summaryModel.getValueAt(0, "発生・運転"));
		assertNull(summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, summaryModel.getRowCount());

		Date date2 = new Date();
		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date2,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, summaryModel.getValueAt(0, "発生・運転"));
		assertEquals(date2, summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, summaryModel.getRowCount());
	}

	public void testCreateSummaryStrategy4() throws Exception {
		// attrib_id = 4
		DataHolder dh = dp.getDataHolder("D_1900004_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, summaryModel.getValueAt(0, "発生・運転"));
		assertNull(summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, summaryModel.getValueAt(0, "発生・運転"));
		assertNull(summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start - 1, summaryModel.getRowCount());
	}

	public void testCreateSummaryStrategy5() throws Exception {
		// attrib_id = 5
		DataHolder dh = dp.getDataHolder("D_1900005_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		int start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertNull(summaryModel.getValueAt(0, "発生・運転"));
		assertEquals(date, summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertNull(summaryModel.getValueAt(0, "発生・運転"));
		assertEquals(date, summaryModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, summaryModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = summaryModel.getRowCount();
		summary.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start - 1, summaryModel.getRowCount());
	}



	public void testCreateOcurrenceStrategy() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900004_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(2));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = ocurrenceModel.getRowCount();
		ocurrence.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, ocurrenceModel.getValueAt(0, "発生・運転"));
		assertNull(ocurrenceModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, ocurrenceModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		start = ocurrenceModel.getRowCount();
		ocurrence.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, ocurrenceModel.getValueAt(0, "発生・運転"));
		assertNull(ocurrenceModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, ocurrenceModel.getRowCount());

		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date,
				WifeQualityFlag.GOOD);
		start = ocurrenceModel.getRowCount();
		ocurrence.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(start - 1, ocurrenceModel.getRowCount());
	}



	public void testCreateCheckStrategy() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900003_Digital");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		Date date = new Date();
		DataValueChangeEvent evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfTrue(0),
				date,
				WifeQualityFlag.GOOD);
		int start = checkModel.getRowCount();
		check.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, checkModel.getValueAt(0, "発生・運転"));
		assertNull(checkModel.getValueAt(0, "復旧・停止"));
		assertEquals(start + 1, checkModel.getRowCount());

		Date date2 = new Date();
		evt =
			new DataValueChangeEvent(
				dh,
				WifeDataDigital.valueOfFalse(0),
				date2,
				WifeQualityFlag.GOOD);
		start = checkModel.getRowCount();
		check.renewRow(new DataValueChangeEventKey(evt));
		assertEquals(date, checkModel.getValueAt(0, "発生・運転"));
		assertEquals(date2, checkModel.getValueAt(0, "復旧・停止"));
		assertEquals(start, checkModel.getRowCount());
	}

}
