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
 *
 */

package org.F11.scada.server.frame;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.ScheduleHolderOwner;
import org.F11.scada.server.alarm.table.PointTableBean;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.impl.RegisterUtil;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.applet.Session;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

import org.F11.scada.server.dao.ItemDao;

/**
 * FrameDefineManagerのテストケース
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FrameDefineManagerTest extends TestCase {
	private final Logger logger = Logger.getLogger(FrameDefineManagerTest.class);
	private FrameDefineManager df;
	private DataProvider dp;
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		try {
			int port = Integer.parseInt(EnvironmentManager.get(
					"/server/rmi/managerdelegator/port", "1099"));
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
		}

		dp = TestUtil.createDP();
		Manager.getInstance().addDataProvider(dp);

		df = new FrameDefineManager(50000,
				new TestHolderRegisterBuilder());
		df.setItemUtil(new TestItemUtil());
		df.setScheduleHolderOwner(new TestScheduleHolderOwner());
		df.setItemDao(new TestItemDao());
		df.init();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	/**
	 * Constructor for FrameDefineManagerTest.
	 * 
	 * @param name
	 */
	public FrameDefineManagerTest(String name) {
		super(name);
	}

	public void testGetPage() throws Exception {
		df.putAll(getPageMap());
		assertNotNull("nullではない", df.getPage("test", 0, new Session()));
	}

	public void testRemovePages() throws Exception {
		df.putAll(getPageMap());
		df.getPage("test", 2, new Session());
		assertNotNull("ホルダーが登録されている", dp.getDataHolder("D_1900000_Digital"));
		df.removePages(3L);
		assertFalse("ページが削除されている", df.isSendRequestDateMap(new Long(1)));
		assertNull("ホルダーが削除されている", dp.getDataHolder("D_1900000_Digital"));
	}

	public void testRemovePageString() throws Exception {
		df.putAll(getPageMap());
		df.getPage("test", 0, new Session());
		assertNotNull("ホルダーが登録されている", dp.getDataHolder("D_1900000_Digital"));
		df.removePageString("test");
		assertNull("ホルダーが削除されている", dp.getDataHolder("D_1900000_Digital"));
	}

	public void testSetPageEditTime() throws Exception {
		df.putAll(getPageMap());
		df.getPage("test", 0, new Session());
//		assertTrue(df.getPointNameCache().containsKey("0_unit"));
		df.setPageEditTime(new PointTableBean(0, "unit", "ユニット0", ""), null);
		assertFalse(df.getPointNameCache().containsKey("0_unit"));
	}

	public void testSetPageString() throws Exception {
		df.setPageString("test", getXml());
		assertTrue(df.containsKey("test"));
	}

	public void testSetSendRequestDateMap() throws Exception {
		df.setSendRequestDateMap(new Session(), 1);
		assertTrue(df.isSendRequestDateMap(new Long(1)));
	}

	private HashMap getPageMap() {
		HashMap pageMap = new HashMap();
		pageMap.put("test", getPageDefine());
		return pageMap;
	}

	private PageDefine getPageDefine() {
		return new PageDefine(System.currentTimeMillis(), getXml());
	}

	private String getXml() {
		return "<?xml version=\"1.0\" encoding=\"Windows-31J\"?>" +
		"<f11:page_map xmlns:f11=\"http://www.F-11.org/scada\">" +
		"<f11:page name=\"test\" value=\"/images/Base/BACK.png\">" +
		"<f11:imagesymbol visible=\"false\">" +
		"<f11:if value=\"P1_D_1900000_Digital\" visible=\"false\">" +
		"<f11:property value=\"/images/kasai.png\" blink=\"true\" visible=\"true\" />" +
		"<f11:property value=\"/images/kasai.png\" blink=\"false\" visible=\"false\" />" +
		"</f11:if></f11:imagesymbol>" +
		"<f11:textsymbol value=\"$(0_unit)\" />" +
		"</f11:page></f11:page_map>";
	}



	private static class TestHolderRegisterBuilder implements HolderRegisterBuilder {
		private final Logger logger = Logger.getLogger(TestHolderRegisterBuilder.class);
		
		public TestHolderRegisterBuilder() {
		}

		public void register(Item[] items) {
			for (int i = 0; i < items.length; i++) {
				Item item = items[i];
				RegisterUtil.addDataHolder(TestUtil.createDigitalHolder(item.getHolder()), item);
				logger.info("regist item : " + item.getHolder());
			}
		}

		public void unregister(Item[] items) {
			for (int i = 0; i < items.length; i++) {
				Item item = items[i];
				RegisterUtil.removeDataHolder(item);
				logger.info("unregist item : " + item.getHolder());
			}
		}
	}

	private static class TestItemDao implements ItemDao {
		public Item getItem(HolderString holderString) {
			return getItem();
		}

		public Item[] getNoSystemItems() {
			Item[] items = new Item[1];
			items[0] = getItem();
			return items;
		}

		public Item[] getSystemItems(String provider, boolean system) {
			Item[] items = new Item[1];
			items[0] = getItem();
			return items;
		}

		public Item selectItem(String provider, String holder) {
			return getItem();
		}

		public int updateItem(Item item) {
			return 0;
		}

		private Item getItem() {
			Item item = new Item();
			item.setProvider("P1");
			item.setHolder("D_1900000_Digital");
			item.setDataType(0);
			item.setDataArgv("0");
			return item;
		}

	}

	private static class TestItemUtil implements ItemUtil {

		public ConvertValue[] createConvertValue(Collection holders,
				String tablename) {
			return null;
		}

		public Map createConvertValueMap(Collection holders, String tableTame) {
			return null;
		}

		public Map createConvertValueMap(Collection holders) {
			return null;
		}

		public Map createDateHolderValuesMap(Collection dataHolders,
				String tableName, MultiRecordDefine multiRecordDefine) {
			return null;
		}

		public DoubleList createHolderValue(Collection dataHolders,
				String tableName) {
			return null;
		}

		public Item getItem(HolderString dataHolder, Map itemPool) {
			return null;
		}

		public Map getItemMap(Item[] items) {
			return Collections.EMPTY_MAP;
		}

		public Item[] getItems(Collection holders, Map itemPool) {
			return null;
		}

	}

	private static class TestScheduleHolderOwner implements ScheduleHolderOwner {

		public DataHolder putScheduleHolder(Integer point, DataHolder dh) {
			return null;
		}

		public DataHolder removeScheduleHolder(Integer point) {
			return null;
		}

	}
}
