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
 */
package org.F11.scada.xwife.applet;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;
import jp.gr.javacons.jim.QualityFlag;
import junit.framework.TestCase;

import org.F11.scada.Globals;
import org.F11.scada.applet.DataAccessableFactory;
import org.F11.scada.applet.DataProviderDesc;
import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.exception.RemoteRuntimeException;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class WifeDataProviderProxyTest extends TestCase {
	private static Logger log = Logger
			.getLogger(WifeDataProviderProxyTest.class);

	private WifeDataProviderProxy p;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	/**
	 * Constructor for WifeDataProviderProxyTest.
	 * 
	 * @param arg0
	 */
	public WifeDataProviderProxyTest(String arg0) {
		super(arg0);
	}

	public void testWifeDataProviderProxy() throws Exception {
		p = new WifeDataProviderProxy(
				new Session(),
				new TestDataAccessableFactory(),
				new TestAuthenticationable());
		p.setDataProviderName("P1");
		DataHolder dh = TestUtil.createDigitalHolder("H1");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		p.addDataHolder(dh);
		Manager.getInstance().addDataProvider(p);
		dh = p.getDataHolder("H1");
		WifeDataDigital dd = (WifeDataDigital) dh.getValue();
		assertTrue(dd.isOnOff(true));
		p.start();
		TestUtil.sleep(2000L);
		dd = (WifeDataDigital) dh.getValue();
		assertTrue(dd.isOnOff(false));
		p.stop();
	}

	public void testSyncRead() throws Exception {
		p = new WifeDataProviderProxy(
				new Session(),
				new TestDataAccessableFactory(),
				new TestAuthenticationable());
		p.setDataProviderName("P1");
		// trueのデータをセット
		DataHolder dh = TestUtil.createDigitalHolder("H1");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		p.addDataHolder(dh);
		Manager.getInstance().addDataProvider(p);
		p.syncRead();
		dh = p.getDataHolder("H1");
		WifeDataDigital dd = (WifeDataDigital) dh.getValue();
		assertTrue("syncReadでfalseになるはず", dd.isOnOff(false));
	}

	public void testSyncWrite() throws Exception {
		TestDataAccessableFactory factory = new TestDataAccessableFactory();
		p = new WifeDataProviderProxy(
				new Session(),
				factory,
				new TestAuthenticationable());
		p.setDataProviderName("P1");
		// trueのデータをセット
		DataHolder dh = TestUtil.createDigitalHolder("H1");
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		p.addDataHolder(dh);
		Manager.getInstance().addDataProvider(p);
		dh.setValue(
				WifeDataDigital.valueOfFalse(0),
				new Date(),
				WifeQualityFlag.GOOD);
		p.syncWrite(dh);
		assertTrue("同じ内容の配列のはず", Arrays.equals(
				new Object[] { "P1", "H1", WifeDataDigital.valueOfFalse(0),
						"testuser", "192.168.0.130" },
				factory.getValus()));
	}

	public void testLookupError() throws Exception {
		try {
			p = new WifeDataProviderProxy(
					new Session(),
					new DataAccessableFactory() {
						public DataAccessable getDataAccessable() {
							throw new RemoteRuntimeException();
						}
					},
					new TestAuthenticationable());
			fail("Not throw RemoteException.");
		} catch (RemoteException e) {
		}
	}

	public void testErrorHolder() throws Exception {
		TestErrorDataAccessableFactory factory = new TestErrorDataAccessableFactory();
		p = new WifeDataProviderProxy(
				new Session(),
				factory,
				new TestAuthenticationable());
		DataHolder dh = TestUtil.createDigitalHolder(Globals.ERR_HOLDER);
		p.addDataHolder(dh);
		DataHolder h1 = TestUtil.createDigitalHolder("H1");
		p.addDataHolder(h1);
		p.start();
		TestUtil.sleep(2000L);
		assertEquals(h1.getQualityFlag(), WifeQualityFlag.BAD);
		factory.setReturnData(new byte[] { (byte) 0x00, (byte) 0x00 });
		TestUtil.sleep(2000L);
		assertEquals(h1.getQualityFlag(), WifeQualityFlag.GOOD);
		p.stop();
	}

	private static class TestDataAccessableFactory implements
			DataAccessableFactory {
		private Object[] valus;

		public DataAccessable getDataAccessable() {
			return new TestDataAccessable(this);
		}

		public Object[] getValus() {
			return valus;
		}

		public void setValus(Object[] setValus) {
			this.valus = setValus;
		}

		private static class TestDataAccessable implements DataAccessable {
			private final TestDataAccessableFactory factory;

			TestDataAccessable(TestDataAccessableFactory factory) {
				this.factory = factory;
			}

			public DataHolder findDataHolder(String dpname, String dhname)
					throws RemoteException {
				return null;
			}

			public SortedMap getAlarmJournal(
					long t,
					String provider,
					String holder) throws RemoteException {
				return null;
			}

			public List getCreateHolderDatas(Collection holderStrings)
					throws RemoteException {
				return createCreateHolderData();
			}

			public List getCreateHolderDatas(String dataProvider)
					throws RemoteException {
				return createCreateHolderData();
			}

			private List createCreateHolderData() {
				return Collections.EMPTY_LIST;
			}

			public List getDataProviders() throws RemoteException {
				DataProviderDesc desc = new DataProviderDesc(
						"P1",
						WifeDataProvider.class);
				ArrayList descs = new ArrayList();
				descs.add(desc);
				return descs;
			}

			public List getHoldersData(String provider, long t, Session session)
					throws RemoteException {
				HashMap map = new HashMap();
				map.put("A", "1");
				HolderData hd = new HolderData("H1", new byte[] { (byte) 0x00,
						(byte) 0x00 }, 1000, map);
				ArrayList list = new ArrayList();
				list.add(hd);
				return list;
			}

			public List getHoldersData(String provider) throws RemoteException {
				HashMap map = new HashMap();
				map.put("A", "1");
				HolderData hd = new HolderData("H1", new byte[] { (byte) 0x00,
						(byte) 0x00 }, 0, map);
				ArrayList list = new ArrayList();
				list.add(hd);
				return list;
			}

			public Object getParameta(
					String dpname,
					String dhname,
					String paraName) throws RemoteException {
				return null;
			}

			public SortedMap getPointJournal(long t) throws RemoteException {
				return null;
			}

			public QualityFlag getQualityFlag(String dpname, String dhname)
					throws RemoteException {
				return null;
			}

			public Object getValue(String dpname, String dhname)
					throws RemoteException {
				return null;
			}

			public void setHistoryTable(
					Integer point,
					String dpname,
					String dhname,
					Timestamp date,
					Integer row) throws RemoteException {
			}

			public void setValue(String dpname, String dhname, Object dataValue)
					throws RemoteException {
				log.info(dpname + " " + dhname + " " + dataValue);
			}

			public void setValue(
					String dpname,
					String dhname,
					Object dataValue,
					String user,
					String ip) throws RemoteException {
				Object[] obj = new Object[] { dpname, dhname, dataValue, user,
						ip };
				factory.setValus(obj);
			}

			public Object invoke(String method, Object[] args) {
				return null;
			}
		}
	}

	private static class TestErrorDataAccessableFactory implements
			DataAccessableFactory {
		private TestErrorDataAccessable accessable;

		public DataAccessable getDataAccessable() {
			accessable = new TestErrorDataAccessable();
			return accessable;
		}

		public void setReturnData(byte[] data) {
			accessable.setReturnData(data);
		}

		static class TestErrorDataAccessable implements DataAccessable {
			private byte[] returnData = new byte[] { (byte) 0x00, (byte) 0x01 };

			public void setReturnData(byte[] data) {
				returnData = data;
			}

			public DataHolder findDataHolder(String dpname, String dhname)
					throws RemoteException {
				return null;
			}

			public SortedMap getAlarmJournal(
					long t,
					String provider,
					String holder) throws RemoteException {
				return null;
			}

			public List getCreateHolderDatas(Collection holderStrings)
					throws RemoteException {
				return Collections.EMPTY_LIST;
			}

			public List getCreateHolderDatas(String dataProvider)
					throws RemoteException {
				return Collections.EMPTY_LIST;
			}

			public List getDataProviders() throws RemoteException {
				DataProviderDesc desc = new DataProviderDesc(
						"P1",
						WifeDataProvider.class);
				ArrayList descs = new ArrayList();
				descs.add(desc);
				return descs;
			}

			public List getHoldersData(String provider, long t, Session session)
					throws RemoteException {
				HashMap map = new HashMap();
				map.put("A", "1");
				HolderData hd = new HolderData(
						Globals.ERR_HOLDER,
						returnData,
						0,
						map);
				ArrayList list = new ArrayList();
				list.add(hd);
				return list;
			}

			public List getHoldersData(String provider) throws RemoteException {
				HashMap map = new HashMap();
				map.put("A", "1");
				HolderData hd = new HolderData(
						Globals.ERR_HOLDER,
						returnData,
						0,
						map);
				ArrayList list = new ArrayList();
				list.add(hd);
				return list;
			}

			public Object getParameta(
					String dpname,
					String dhname,
					String paraName) throws RemoteException {
				return null;
			}

			public SortedMap getPointJournal(long t) throws RemoteException {
				return null;
			}

			public QualityFlag getQualityFlag(String dpname, String dhname)
					throws RemoteException {
				return null;
			}

			public Object getValue(String dpname, String dhname)
					throws RemoteException {
				return null;
			}

			public void setHistoryTable(
					Integer point,
					String dpname,
					String dhname,
					Timestamp date,
					Integer row) throws RemoteException {
			}

			public void setValue(String dpname, String dhname, Object dataValue)
					throws RemoteException {
				log.info(dpname + " " + dhname + " " + dataValue);
			}

			public void setValue(
					String dpname,
					String dhname,
					Object dataValue,
					String user,
					String ip) throws RemoteException {
			}

			public Object invoke(String method, Object[] args) {
				return null;
			}
		}
	}

	private static class TestAuthenticationable implements Authenticationable {

		public void addEditable(Editable symbol) {
		}

		public Subject getSubject() {
			return Subject.createSubject(Collections.EMPTY_SET, "testuser");
		}

		public void logout() {
		}

		public void removeEditable(Editable symbol) {
		}

		public void showAuthenticationDialog() {
		}

		public Configuration getConfiguration() {
			return null;
		}
	}
}
