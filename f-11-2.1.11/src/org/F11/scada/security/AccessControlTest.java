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
package org.F11.scada.security;

import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPasswordField;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.security.auth.Crypt;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.test.util.TestConnectionUtil;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

/**
 * AccessControl クラスのテストスイートです。
 * テストを実行する前に、AccessControl サーバーを実行する必要があります。
 * また、postgreSQL の実行環境及び、Policy 定義テーブルを作成する必要があります。
 *
 * start rmiregistry
 * java -server -Djava.rmi.server.codebase=file:lib/F-11.jar -Djava.security.policy=file:policy -classpath lib/F-11.jar jp.co.frdm.scada.security.AccessControl
 * @auther Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AccessControlTest extends DatabaseTestCase {
	WifePermission permission;
	AccessControlable ac;

	/**
	 * Constructor for AccessControlTest.
	 * @param arg0
	 */
	public AccessControlTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		try {
	        String portStr = EnvironmentManager.get("/server/rmi/managerdelegator/port", "1099");
	        int port = Integer.parseInt(portStr);
            LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
//            e.printStackTrace();
        }
		ac = new AccessControl(50000);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(TestConnectionUtil.getTestConnection());
	}

	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(new FileInputStream("src/org/F11/scada/security/dbTest.xml"));
	}

	/**
	 * ユーザー認証
	 * @throws Exception
	 */
	public void testCheckPermission() throws Exception {
		Set principal = new HashSet();
		principal.add(new WifePrincipal("user1"));
		
		String[][] dest = {
			{"P1_D_500_BcdSingle"},
			{"P1_D_501_BcdSingle"},
			{"a"},
		};

		List list = ac.checkPermission(Subject.createSubject(principal, "user1"), dest);
		Boolean[] b = (Boolean[]) list.get(0);
		assertTrue(b[0].booleanValue());
		b = (Boolean[]) list.get(1);
		assertTrue(b[0].booleanValue());
		b = (Boolean[]) list.get(2);
		assertFalse(b[0].booleanValue());
	}

	/**
	 * グループ認証
	 * @throws Exception
	 */
	public void testGroupCheckPermission() throws Exception {
		Set principal = new HashSet();
		principal.add(new WifePrincipal("group1"));

		String[][] dest = {
			{"P1_D_1903002_Digital"},
			{"a"},
		};
		List list = ac.checkPermission(Subject.createSubject(principal, "user1"), dest);
		Boolean[] b = (Boolean[]) list.get(0);
		assertTrue(b[0].booleanValue());
		b = (Boolean[]) list.get(1);
		assertFalse(b[0].booleanValue());
	}

	/**
	 * ユーザー認証＆グループ認証
	 * @throws Exception
	 */
	public void testUserAndGroupPermission() throws Exception {
		Set principal = new HashSet();
		principal.add(new WifePrincipal("user1"));
		principal.add(new WifePrincipal("group1"));

		String[][] dest = {
			{"P1_D_500_BcdSingle"},
			{"P1_D_501_BcdSingle"},
			{"P1_D_1903002_Digital"},
			{"a"},
		};

		List list = ac.checkPermission(Subject.createSubject(principal, "user1"), dest);
		Boolean[] b = (Boolean[]) list.get(0);
		assertTrue(b[0].booleanValue());
		b = (Boolean[]) list.get(1);
		assertTrue(b[0].booleanValue());
		b = (Boolean[]) list.get(2);
		assertTrue(b[0].booleanValue());
		b = (Boolean[]) list.get(3);
		assertFalse(b[0].booleanValue());
	}

	public void testUserAuthentication() throws Exception {
		JPasswordField pf = new JPasswordField("user1");
		assertNotNull(ac.checkAuthentication("user1", Crypt.crypt(pf.getPassword())));
		/* Subject subject = */ ac.checkAuthentication("user1", Crypt.crypt(pf.getPassword()));
//		Set principals = subject.getPrincipals();
//		for (Iterator it = principals.iterator(); it.hasNext();) {
//			Principal p = (Principal)it.next();
//			System.out.println(p);
//		}

		assertNull(ac.checkAuthentication("hogehoge", ""));
		assertNull(ac.checkAuthentication("user1", "hogehoge"));
	}
}
