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
import java.io.FileOutputStream;

import junit.framework.TestCase;

import org.F11.scada.test.util.TestUtil;
import org.apache.log4j.Logger;

/**
 * PageFileDeploymentScannerのテストケース
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageFileDeploymentScannerTest extends TestCase {
	private static final Logger logger = Logger
			.getLogger(PageFileDeploymentScannerTest.class);

	/**
	 * Constructor for PageFileDeploymentScannerTest.
	 * 
	 * @param arg0
	 */
	public PageFileDeploymentScannerTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		File root = new File("utest");
		File adir = new File("utest/a");
		File abdir = new File(adir, "b");

		new File(abdir, "ab.xml").delete();
		new File(adir, "a.xml").delete();
		new File(root, "root.xml").delete();
		abdir.delete();
		adir.delete();
		root.delete();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		File root = new File("utest");
		File adir = new File("utest/a");
		File abdir = new File(adir, "b");

		new File(abdir, "ab.xml").delete();
		new File(adir, "a.xml").delete();
		new File(root, "root.xml").delete();
		abdir.delete();
		adir.delete();
		root.delete();
	}

	/*
	 * ファイルを作成・更新・削除して配備・非配備の回数をテストします。
	 */
	public void testPageFileDeloy() throws Exception {
		TestDeployer deployer = new TestDeployer();
		PageFileDeploymentScanner scanner = new PageFileDeploymentScanner(
				deployer,
				1000);
		long sleep = scanner.getPeriod() * 2L;

		File root = new File("utest");
		root.mkdir();
		scanner.addFile(root);

		File adir = new File("utest/a");
		adir.mkdir();

		File abdir = new File(adir, "b");
		abdir.mkdir();

		File rootXml = new File(root, "root.xml");
		rootXml.createNewFile();

		File aXml = new File(adir, "a.xml");
		aXml.createNewFile();

		File abXml = new File(abdir, "ab.xml");
		abXml.createNewFile();
		TestUtil.sleep(sleep);

		FileOutputStream out = new FileOutputStream(abXml, true);
		out.write(new byte[] { (byte) 0x00 });
		out.close();
		TestUtil.sleep(sleep);

		rootXml.delete();
		TestUtil.sleep(sleep);

		aXml.delete();
		TestUtil.sleep(sleep);

		adir.delete();
		TestUtil.sleep(sleep);

		logger.info(deployer);

		scanner.terminate();

		assertEquals(4, deployer.getDeployCount());
		assertEquals(3, deployer.getUndeployCount());
	}

	/**
	 * テスト配備オブジェクトのクラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static private class TestDeployer implements Deployer {
		volatile int deployCount;
		volatile int undeployCount;

		public void deploy(File file) {
			incDeploy();
			logger.info("deploy:" + deployCount);
		}

		public void undeploy(File file) {
			incUndeploy();
			logger.info("undeploy:" + undeployCount);
		}

		public String toString() {
			return "deployCount=" + deployCount + ", undeployCount="
					+ undeployCount;
		}

		public boolean isDeployed(String pageName) {
			return false;
		}

		public synchronized void incDeploy() {
			deployCount++;
		}

		public synchronized void incUndeploy() {
			undeployCount++;
		}

		public synchronized int getDeployCount() {
			return deployCount;
		}

		public synchronized int getUndeployCount() {
			return undeployCount;
		}
	}
}
