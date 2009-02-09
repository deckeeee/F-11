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

package org.F11.scada.applet.graph;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.io.SelectiveAllDataValueListHandler;
import org.F11.scada.server.io.SelectiveValueListHandler;
import org.apache.commons.collections.primitives.DoubleList;

public class DefaultGraphModelFactoryTest extends TestCase {
    private static final String SELECTIVALL_MODENAME = "全てのデータ";
    private static final String SELECTIVE_MODENAME = "自動更新";
    private static final String SELECTIVEGRAPHMODEL = "org.F11.scada.applet.graph.DefaultSelectiveGraphModel";
	private static final String SELECTIVEALLDATAGRAPHMODEL = "org.F11.scada.applet.graph.DefaultSelectiveAllDataGraphModel";

	protected void setUp() throws Exception {
        int regPort = Integer.parseInt(EnvironmentManager.get("/server/rmi/managerdelegator/port", "1099"));
        try {
            LocateRegistry.createRegistry(regPort);
        } catch (ExportException e) {}
		Naming.rebind(WifeUtilities.createRmiSelectiveValueListHandlerManager(), new TestSelectiveValueListHandler());
		Naming.rebind(WifeUtilities.createRmiSelectiveAllDataValueListHandlerManager(), new TestSelectiveAllDataValueListHandler());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.applet.graph.DefaultGraphModelFactory.getGraphModel(String, List)'
	 */
	public void testGetGraphModel() {
		GraphModelFactory factory = new DefaultGraphModelFactory(SELECTIVEGRAPHMODEL, new TestGraphPropertyModel(), SELECTIVE_MODENAME);
		assertNotNull(factory.getGraphModel("", Collections.EMPTY_LIST));
		assertEquals(SELECTIVE_MODENAME, factory.getModeName());
		factory = new DefaultGraphModelFactory(SELECTIVEALLDATAGRAPHMODEL, new TestGraphPropertyModel(), SELECTIVALL_MODENAME);
		assertNotNull(factory.getGraphModel("", Collections.EMPTY_LIST));
		assertEquals(SELECTIVALL_MODENAME, factory.getModeName());
	}

	
	static class TestSelectiveValueListHandler implements SelectiveValueListHandler, Serializable {
		private static final long serialVersionUID = 1L;

		public SortedMap<Timestamp, DoubleList> getInitialData(String name, List holderStrings) throws RemoteException {
			return new TreeMap();
		}

		public SortedMap<Timestamp, DoubleList> getInitialData(String name, List holderStrings, int limit) throws RemoteException {
			return new TreeMap();
		}

		public Map<Timestamp, DoubleList> getUpdateLoggingData(String name, Timestamp key, List holderStrings) throws RemoteException {
			return Collections.EMPTY_MAP;
		}
	}
	
	static class TestSelectiveAllDataValueListHandler implements SelectiveAllDataValueListHandler, Serializable {
		private static final long serialVersionUID = 2L;

		public Timestamp firstTime(String name, List holderStrings) throws RemoteException {
			return null;
		}

		public SortedMap getLoggingData(String name, List holderStrings, Timestamp start, int limit) throws RemoteException {
			return null;
		}

		public Timestamp lastTime(String name, List holderStrings) throws RemoteException {
			return null;
		}

		public SortedMap<Timestamp, DoubleList> getInitialData(String name, List holderStrings) throws RemoteException {
			return new TreeMap();
		}

		public SortedMap<Timestamp, DoubleList> getInitialData(String name, List holderStrings, int limt) throws RemoteException {
			return new TreeMap();
		}

		public Map<Timestamp, DoubleList> getUpdateLoggingData(String name, Timestamp key, List holderStrings) throws RemoteException {
			return Collections.EMPTY_MAP;
		}
	}
}
