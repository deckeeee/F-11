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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AllPermission;
import java.security.PermissionCollection;

import junit.framework.TestCase;

/**
 * 
 * @auther Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifePermissionTest extends TestCase {
	WifePermission READ;
	WifePermission WRITE;
	WifePermission EXECUTE;
	WifePermission DELETE;
	WifePermission ALL;

	/**
	 * Constructor for WifePermissionTest.
	 * @param arg0
	 */
	public WifePermissionTest(String arg0) {
		super(arg0);
	}
	protected void setUp() {
		READ = new WifePermission("user","read");
		WRITE = new WifePermission("user","write");
		EXECUTE = new WifePermission("user","execute");
		DELETE = new WifePermission("user","delete");
		ALL = new WifePermission("user","read,write,execute,delete");
	}
	public void testGetActions() {
		assertEquals("read", READ.getActions());
		assertEquals("write", WRITE.getActions());
		assertEquals("execute", EXECUTE.getActions());
		assertEquals("delete", DELETE.getActions());
		assertEquals("execute,write,read,delete", ALL.getActions());
	}

	public void testEquals() {
		assertTrue(READ.equals(new WifePermission("user","read")));
		assertTrue(WRITE.equals(new WifePermission("user","write")));
		assertTrue(EXECUTE.equals(new WifePermission("user","execute")));
		assertTrue(DELETE.equals(new WifePermission("user","delete")));
		assertTrue(ALL.equals(new WifePermission("user","read,write,execute,delete")));

		assertTrue(!READ.equals(new WifePermission("user","write")));
		assertTrue(!WRITE.equals(new WifePermission("user","read")));
		assertTrue(!EXECUTE.equals(new WifePermission("user","delete")));
		assertTrue(!DELETE.equals(new WifePermission("user","execute")));
		assertTrue(!ALL.equals(new WifePermission("user","read")));
	}

	public void testHashCode() {
		assertEquals(READ.hashCode() ,new WifePermission("user","read").hashCode());
		assertEquals(WRITE.hashCode() ,new WifePermission("user","write").hashCode());
		assertEquals(EXECUTE.hashCode() ,new WifePermission("user","execute").hashCode());
		assertEquals(DELETE.hashCode() ,new WifePermission("user","delete").hashCode());
		assertEquals(ALL.hashCode() ,new WifePermission("user","read,write,execute,delete").hashCode());

		assertTrue(ALL.hashCode() != new WifePermission("user1","read,write,execute,delete").hashCode());
	}

	public void testImplies() {
		assertTrue(ALL.implies(new WifePermission("user","read,write,execute,delete")));
		assertTrue(ALL.implies(new WifePermission("user","read,write")));
		assertTrue(!READ.implies(new WifePermission("user","read,write,execute,delete")));
	}

	public void testIlligalAction() {
		try {
			new WifePermission("user", "red");
			fail();
		} catch (IllegalArgumentException ex) {
		}
	}

	public void testAllPermission() throws Exception {
		AllPermission ap = new AllPermission();
		assertTrue(ap.implies(READ));
		assertTrue(ap.implies(WRITE));
		assertTrue(ap.implies(EXECUTE));
		assertTrue(ap.implies(DELETE));
	}

	public void testReadResolve() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(READ);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		WifePermission d = (WifePermission) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(d, READ);
		assertTrue(d.equals(READ));
		temp.delete();
	}
	
	public void testWifePermissionCollection() throws Exception {
		PermissionCollection pc = READ.newPermissionCollection();
		pc.add(READ);
		assertTrue(pc.implies(READ));

		pc = READ.newPermissionCollection();
		pc.add(WRITE);
		assertTrue(pc.implies(WRITE));

		pc = READ.newPermissionCollection();
		pc.add(EXECUTE);
		assertTrue(pc.implies(EXECUTE));

		pc = READ.newPermissionCollection();
		pc.add(DELETE);
		assertTrue(pc.implies(DELETE));
		
		pc = READ.newPermissionCollection();
		pc.add(DELETE);
		assertTrue(!pc.implies(READ));
		assertTrue(!pc.implies(WRITE));
		assertTrue(!pc.implies(EXECUTE));
		assertTrue(pc.implies(DELETE));
				
		pc = READ.newPermissionCollection();
		pc.add(new WifePermission("user","read,write,execute,delete"));
		assertTrue(pc.implies(READ));
		assertTrue(pc.implies(WRITE));
		assertTrue(pc.implies(EXECUTE));
		assertTrue(pc.implies(DELETE));
		
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(pc);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		PermissionCollection d = (PermissionCollection) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertTrue(d.implies(READ));
		assertTrue(d.implies(WRITE));
		assertTrue(d.implies(EXECUTE));
		assertTrue(d.implies(DELETE));

		temp.delete();
	}
}
