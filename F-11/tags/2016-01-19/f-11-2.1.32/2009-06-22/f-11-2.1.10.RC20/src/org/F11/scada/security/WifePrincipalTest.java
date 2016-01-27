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

import javax.security.auth.x500.X500Principal;

import junit.framework.TestCase;

/**
 * 
 * @auther Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifePrincipalTest extends TestCase {
	WifePrincipal user;

	/**
	 * Constructor for WifePrincipalTest.
	 * @param arg0
	 */
	public WifePrincipalTest(String arg0) {
		super(arg0);
	}

	protected void setUp() {
		user = new WifePrincipal("user");
	}
	
	public void testWifePrincipal() throws Exception {
		try {
			new WifePrincipal(null);
			fail();
		} catch (IllegalArgumentException ex) {
		}
	}

	public void testEquals() {
		assertEquals(new WifePrincipal("user"), user);
		assertEquals(user, user);
		assertTrue(!user.equals(new X500Principal("CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US")));
	}

	public void testGetName() {
		assertEquals("user", user.getName());
	}

	public void testHashCode() {
		assertEquals(new WifePrincipal("user").hashCode(), user.hashCode());
	}

	public void testToString() {
		assertEquals("user", user.toString());
	}

	public void testReadResolve() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(user);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		WifePrincipal d = (WifePrincipal) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(d, user);
		assertTrue(d.equals(user));
		temp.delete();
	}
}
