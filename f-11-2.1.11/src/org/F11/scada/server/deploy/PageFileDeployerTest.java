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

package org.F11.scada.server.deploy;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import junit.framework.TestCase;

import org.F11.scada.server.dao.ItemArrayDao;
import org.F11.scada.server.frame.FrameDefineManager;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.xwife.server.WifeMain;
import org.seasar.framework.container.S2Container;

/**
 * PageFileDeployerのテストケースです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageFileDeployerTest extends TestCase {
	private static final int PORT = WifeMain.RMI_RECV_PORT_SERVER + 10;
	private S2Container container;
	private HolderRegisterBuilder builder;
	private ItemArrayDao dao;

	/**
	 * Constructor for PageFileDeployerTest.
	 * @param arg0
	 */
	public PageFileDeployerTest(String arg0) {
		super(arg0);
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (RemoteException e) {
//			e.printStackTrace();
		}
		container = S2ContainerUtil.getS2Container();
		builder = (HolderRegisterBuilder) container.getComponent(HolderRegisterBuilder.class);
		dao = (ItemArrayDao) container.getComponent(ItemArrayDao.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testPageFileDeployer() throws Exception {
		FrameDefineManager fm =
			new FrameDefineManager(PORT, builder);
		PageFileDeployer deployer = new PageFileDeployer(fm);
		deployer.deploy(new File("src/org/F11/scada/server/deploy/testpage1.xml"));
		assertTrue(deployer.isDeployed("testpage1"));

		deployer.undeploy(new File("src/org/F11/scada/server/deploy/testpage1.xml"));
		assertFalse(deployer.isDeployed("testpage1"));
	}
	
	public void testPageFileDeployerError() throws Exception {
		FrameDefineManager fm =
			new FrameDefineManager(PORT, builder);
		PageFileDeployer deployer = new PageFileDeployer(fm);
		File file = new File("src/org/F11/scada/server/deploy/testpage2.xml");
		try {
			deployer.deploy(file);
			fail();
		} catch (DeploymentException e) {
			System.out.println("Failed to deploy: " + e);
			e.printStackTrace();
		}
		assertFalse(deployer.isDeployed("testpage2"));
	}
}
