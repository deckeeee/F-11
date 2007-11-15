/*
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

package org.F11.scada.xwife.server.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.swing.event.TableModelListener;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import junit.framework.TestCase;

import org.F11.scada.Globals;
import org.F11.scada.WifeException;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.event.WifeEventListener;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.server.register.HolderRegister;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.impl.DigitalHolderRegister;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

public class WifeDataProviderImplTest extends TestCase {
	private Logger log = Logger.getLogger(WifeDataProviderImplTest.class);
	private WifeDataProviderImpl dp;
	private TestCommunicaterFactory factory;

	public WifeDataProviderImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		factory = new TestCommunicaterFactory();
		dp = new WifeDataProviderImpl(
				new TestEnvironment(),
				new TestItemDao(),
				new TestHolderRegisterBuilder(),
				new TestAlarmReferencer(),
				new TestAlarmReferencer(),
				factory);
		dp.setSendRequestSupport(new TestSendRequestSupport());
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	public void testGetDataHolder() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		assertNotNull(dh);
	}

	public void testAddDataHolder() throws Exception {
		DataHolder dh = new DataHolder();
		dh.setValueClass(WifeData.class);
		dh.setDataHolderName("D_1900001_Digital");
		dh.setValue(
				WifeDataDigital.valueOfFalse(1),
				new Date(),
				WifeQualityFlag.INITIAL);
		dh.setParameter(WifeDataProvider.PARA_NAME_CYCLEREAD, Boolean.TRUE);
		dh.setParameter(WifeDataProvider.PARA_NAME_COMAND, WifeCommand
				.getNullCommand());
		dp.addDataHolder(dh);
		assertNotNull(dp.getDataHolder("D_1900001_Digital"));
	}

	public void testReadDataHolder() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		WifeDataDigital d = (WifeDataDigital) dh.getValue();
		assertEquals(WifeDataDigital.valueOfFalse(0), d);
		dh.syncRead();
		TestUtil.sleep(1000L);
		d = (WifeDataDigital) dh.getValue();
		assertEquals(WifeDataDigital.valueOfTrue(0), d);

		List list = dp.getHoldersData(0, new Session());
		assertNotNull(list);
		assertEquals(1, list.size());
		HolderData hd = (HolderData) list.get(0);
		assertNull(hd.getDemandData());
		assertEquals("D_1900000_Digital", hd.getHolder());
		assertTrue(Arrays.equals(new byte[] { (byte) 0xFF, (byte) 0xFF }, hd
				.getValue()));
	}

	public void testReadError() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		factory.setIsError(true);
		dh.syncRead();
		TestUtil.sleep(1000L);
		DataHolder errdh = dp.getDataHolder(Globals.ERR_HOLDER);
		assertNotNull(errdh);

		/*
		 * 2.0.28よりこの操作では通信エラーはあがらない。
		 * assertEquals(WifeDataDigital.valueOfTrue(0), errdh.getValue()); List
		 * list = dp.getHoldersData(0, new Session()); assertNotNull(list);
		 * assertEquals(1, list.size()); HolderData hd = (HolderData)
		 * list.get(0); assertNull(hd.getDemandData());
		 * assertEquals(Globals.ERR_HOLDER, hd.getHolder());
		 * assertTrue(Arrays.equals(new byte[]{(byte) 0x00, (byte) 0x01},
		 * hd.getValue()));
		 */

		factory.setIsError(false);
		dh.syncRead();
		TestUtil.sleep(1000L);
		errdh = dp.getDataHolder(Globals.ERR_HOLDER);
		assertNotNull(errdh);
		assertEquals(WifeDataDigital.valueOfFalse(0), errdh.getValue());

		List list = dp.getHoldersData(0, new Session());
		assertNotNull(list);
		assertEquals(1, list.size());
		HolderData hd = (HolderData) list.get(0);
		assertNull(hd.getDemandData());
		assertEquals("D_1900000_Digital", hd.getHolder());
		// System.out.println(WifeUtilities.toString(hd.getValue()));
		assertTrue(Arrays.equals(new byte[] { (byte) 0xFF, (byte) 0xFF }, hd
				.getValue()));
	}

	public void testWriteDataHolder() throws Exception {
		DataHolder dh = dp.getDataHolder("D_1900000_Digital");
		WifeDataDigital d = (WifeDataDigital) dh.getValue();
		assertEquals(WifeDataDigital.valueOfFalse(0), d);
		dh.syncWrite();
		assertTrue(factory.isWrite());

		List list = dp.getHoldersData(0, new Session());
		assertNotNull(list);
		assertEquals(1, list.size());
		HolderData hd = (HolderData) list.get(0);
		assertNull(hd.getDemandData());
		assertEquals("D_1900000_Digital", hd.getHolder());
		assertTrue(Arrays.equals(new byte[] { (byte) 0x00, (byte) 0x00 }, hd
				.getValue()));
	}

	class TestEnvironment implements Environment {

		public String getDeviceID() {
			return "P1";
		}

		public String getDeviceKind() {
			return "UDP";
		}

		public int getHostAddress() {
			return 0;
		}

		public String getHostIpAddress() {
			return "127.0.0.1";
		}

		public int getHostNetNo() {
			return 0;
		}

		public int getHostPortNo() {
			return 9601;
		}

		public String getPlcCommKind() {
			return "FINS";
		}

		public String getPlcIpAddress() {
			return "127.0.0.1";
		}

		public int getPlcNetNo() {
			return 0;
		}

		public int getPlcNodeNo() {
			return 0;
		}

		public int getPlcPortNo() {
			return 9600;
		}

		public int getPlcRecoveryWait() {
			return 0;
		}

		public int getPlcRetryCount() {
			return 0;
		}

		public int getPlcTimeout() {
			return 0;
		}

		public int getPlcUnitNo() {
			return 0;
		}

		public int getPlcWatchWait() {
			return 0;
		}
	}

	class TestItemDao implements ItemDao {

		public Item getItem(HolderString holderString) {
			return getItem();
		}

		public Item[] getSystemItems(String provider, boolean system) {
			return new Item[] { getItem(), getErrItem() };
		}

		private Item getItem() {
			Item item = new Item();
			item.setPoint(new Integer(0));
			item.setProvider("P1");
			item.setHolder("D_1900000_Digital");
			item.setDataArgv("00");
			item.setComCycle(0);
			item.setComCycleMode(true);
			item.setComMemoryAddress(100);
			item.setComMemoryKinds(0);
			item.setOffDelay(new Integer(10));
			return item;
		}

		private Item getErrItem() {
			Item item = new Item();
			item.setPoint(new Integer(0));
			item.setProvider("P1");
			item.setHolder(Globals.ERR_HOLDER);
			item.setDataArgv("00");
			item.setComCycle(0);
			item.setComCycleMode(false);
			item.setComMemoryAddress(0);
			item.setComMemoryKinds(0);
			item.setOffDelay(new Integer(0));
			return item;
		}

		public Item selectItem(String provider, String holder) {
			return getItem();
		}

		public int updateItem(Item item) {
			return 1;
		}

		public Item[] getNoSystemItems() {
			return null;
		}

	}

	class TestHolderRegisterBuilder implements HolderRegisterBuilder {
		private HolderRegister hr = new DigitalHolderRegister();

		public void register(Item[] items) {
			for (int i = 0; i < items.length; i++) {
				hr.register(items[i]);
			}
		}

		public void unregister(Item[] items) {
			for (int i = 0; i < items.length; i++) {
				hr.unregister(items[i]);
			}
		}
	}

	class TestAlarmReferencer implements AlarmReferencer {

		public boolean addDataStore(AlarmDataStore store) {
			return false;
		}

		public void addReferencer(DataReferencer rf) {
		}

		public void removeReferencer(DataReferencer dr) {
		}

		public SortedSet getReferencers() {
			return null;
		}

		public void addTableModelListener(TableModelListener l) {
		}

		public Class getColumnClass(int columnIndex) {
			return null;
		}

		public int getColumnCount() {
			return 0;
		}

		public String getColumnName(int columnIndex) {
			return null;
		}

		public int getRowCount() {
			return 0;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return null;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		public void removeTableModelListener(TableModelListener l) {
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}

	}

	class TestCommunicaterFactory implements CommunicaterFactory {
		private boolean isError;
		private boolean isWrite;

		public Communicater createCommunicator(Environment device)
				throws Exception {
			return new TestCommunicater();
		}

		void setIsError(boolean b) {
			isError = b;
		}

		boolean isWrite() {
			return isWrite;
		}

		class TestCommunicater implements Communicater {

			public void addReadCommand(Collection commands) {
			}

			public void removeReadCommand(Collection commands) {
			}

			public void addWifeEventListener(WifeEventListener l) {
			}

			public void close() throws InterruptedException {
			}

			public void removeWifeEventListener(WifeEventListener l) {
			}

			public Map syncRead(Collection commands, boolean sameDataBalk)
					throws InterruptedException,
					IOException,
					WifeException {
				if (isError) {
					throw new WifeException();
				} else {
					Map map = new HashMap();
					for (Iterator i = commands.iterator(); i.hasNext();) {
						WifeCommand c = (WifeCommand) i.next();
						map.put(c, new byte[] { (byte) 0xFF, (byte) 0xFF });
					}
					return map;
				}
			}

			public Map syncRead(Collection commands)
					throws InterruptedException,
					IOException,
					WifeException {
				return syncRead(commands, true);
			}

			public void syncWrite(Map commands)
					throws InterruptedException,
					IOException,
					WifeException {
				isWrite = true;
				log.info("syncWrite");
			}

		}
	}

	class TestSendRequestSupport implements SendRequestSupport {
		public void setSendRequestDateMap(Session session, long time) {
		}
	}
}
