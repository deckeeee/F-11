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
package org.F11.scada.applet;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.Manager;
import jp.gr.javacons.jim.QualityFlag;
import junit.framework.TestCase;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.CreateHolderData;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DataProviderProxyDefineTest extends TestCase implements
		DataReferencerOwner {
	private DataProviderProxyDefine proxyDefine;
	private TestDataProviderProxyFactory factory;

	private static final Class[][] INFO = { { WifeData.class, DataHolder.class } };

	/**
	 * Constructor for DataProviderProxyDefineTest.
	 * 
	 * @param arg0
	 */
	public DataProviderProxyDefineTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		try {
			factory = new TestDataProviderProxyFactory();
			proxyDefine = new DataProviderProxyDefine(
					new Session(),
					new TestDataAccessableFactory(),
					factory);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (DataProviderDoesNotSupportException e) {
			e.printStackTrace();
		}
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	public void testGetDataHolder() throws Exception {
		proxyDefine.addDataHolder(Collections.EMPTY_SET);
		DataReferencer rf = new DataReferencer("P1", "H1");
		rf.connect(this);
		DataHolder dh = rf.getDataHolder();
		assertNotNull(dh);

		Manager manager = Manager.getInstance();
		dh = manager.findDataHolder("P1", "H1");
		assertEquals("“¯‚¶‚à‚Ì‚ª‚ ‚é‚Í‚¸", ConvertValue.valueOfANALOG(
				0,
				100,
				0,
				4000,
				"##0"), dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT));
		Map demandData = new HashMap();
		demandData.put("DEMAND", "H2");
		assertEquals("“¯‚¶‚à‚Ì‚ª‚ ‚é‚Í‚¸", demandData, dh
				.getParameter(DemandDataReferencer.GRAPH_DATA));
		assertNotNull(dh);
		assertTrue(factory.isSyncRead);
		assertEquals("0‚É‚È‚é‚Í‚¸", 0, factory.getNewestTime());
	}

	/**
	 * @see jp.gr.javacons.jim.DataReferencerOwner#getReferableDataHolderTypeInfo(jp.gr.javacons.jim.DataReferencer)
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return INFO;
	}

	static class TestDataAccessableFactory implements DataAccessableFactory {
		public DataAccessable getDataAccessable() {
			return new TestDataAccessable();
		}

		static class TestDataAccessable implements DataAccessable {

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
				ConvertValue convertValue = ConvertValue.valueOfANALOG(
						0,
						100,
						0,
						4000,
						"##0");
				Map demandData = new HashMap();
				demandData.put("DEMAND", "H2");
				WifeData wifeData = WifeDataAnalog.valueOfBcdSingle(0);
				Date date = new Date();
				CreateHolderData data = new CreateHolderData(
						"H1",
						wifeData,
						convertValue,
						demandData,
						date,
						WifeQualityFlag.GOOD,
						"P1");
				return Arrays.asList(new CreateHolderData[] { data });
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
				return null;
			}

			public List getHoldersData(String provider) throws RemoteException {
				return null;
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

	private static class TestDataProviderProxyFactory implements
			DataProviderProxyFactory {
		private boolean isSyncRead;
		private long newestTime = Long.MAX_VALUE;

		public DataProvider createDataProvider(DataProviderDesc desc) {
			return new TestDataProviderProxy(desc, this);
		}

		private static class TestDataProviderProxy extends AbstractDataProvider
				implements DataProviderProxy {
			private final Logger logger = Logger
					.getLogger(TestDataProviderProxy.class);
			private static final long serialVersionUID = -8735024522387464332L;
			private final TestDataProviderProxyFactory factory;

			TestDataProviderProxy(
					DataProviderDesc desc,
					TestDataProviderProxyFactory factory) {
				this.factory = factory;
				setDataProviderName(desc.getProvider());
			}

			public Class[][] getProvidableDataHolderTypeInfo() {
				Class[][] c = { { DataHolder.class, WifeData.class } };
				return c;
			}

			public void asyncRead(DataHolder dh)
					throws DataProviderDoesNotSupportException {
			}

			public void asyncRead(DataHolder[] dhs)
					throws DataProviderDoesNotSupportException {
			}

			public void asyncWrite(DataHolder dh)
					throws DataProviderDoesNotSupportException {
			}

			public void asyncWrite(DataHolder[] dhs)
					throws DataProviderDoesNotSupportException {
			}

			public void syncRead(DataHolder dh)
					throws DataProviderDoesNotSupportException {
			}

			public void syncRead(DataHolder[] dhs)
					throws DataProviderDoesNotSupportException {
			}

			public void syncWrite(DataHolder dh)
					throws DataProviderDoesNotSupportException {
			}

			public void syncWrite(DataHolder[] dhs)
					throws DataProviderDoesNotSupportException {
			}

			public void run() {
			}

			public void setValueChangeNewestTime(long l) {
				factory.setNewestTime(l);
				logger.info("setValueChangeNewestTime=" + l);
			}

			public void syncRead() {
				factory.setSyncRead(true);
				logger.info("syncRead");
			}

			public void start() {
			}

			public void stop() {
			}

			public void lock() {
			}

			public void unlock() {
			}

		}

		public boolean isSyncRead() {
			return isSyncRead;
		}

		public void setSyncRead(boolean isSyncRead) {
			this.isSyncRead = isSyncRead;
		}

		public long getNewestTime() {
			return newestTime;
		}

		public void setNewestTime(long newestTime) {
			this.newestTime = newestTime;
		}

	}
}