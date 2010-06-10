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

package org.F11.scada.util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

import org.F11.scada.EnvironmentManager;

import junit.framework.TestCase;

public class RmiUtilTest extends TestCase {

	public RmiUtilTest(String name) {
		super(name);
        try {
            String regPort = EnvironmentManager.get("/server/rmi/managerdelegator/port", "1099");
            try {
                LocateRegistry.createRegistry(Integer.parseInt(regPort));
            } catch (ExportException e) {}
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            String regPort = EnvironmentManager.get("/server/rmi/collectorserver/port", "1099");
            try {
                LocateRegistry.createRegistry(Integer.parseInt(regPort));
            } catch (ExportException e) {}
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.util.RmiUtil.registryServer(Remote, Class)'
	 */
	public void testRegistryAndLookupServer() throws Exception {
		new TestServer();
		TestRmiInterface server =
			(TestRmiInterface) RmiUtil.lookupServer(TestRmiInterface.class);
		assertEquals("TestServer", server.test());
	}

	
	interface TestRmiInterface extends Remote {
		String test() throws RemoteException;
	}
	
	class TestServer implements TestRmiInterface {
		TestServer() {
			RmiUtil.registryServer(this, TestRmiInterface.class);
		}
		public String test() {
			return "TestServer";
		}
	}
}
