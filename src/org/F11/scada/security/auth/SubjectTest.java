/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/security/auth/SubjectTest.java,v 1.4.2.1 2004/11/29 07:12:53 frdm Exp $
 * $Revision: 1.4.2.1 $
 * $Date: 2004/11/29 07:12:53 $
 * 
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
package org.F11.scada.security.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Subject クラスのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SubjectTest extends TestCase {
	Subject sub;
	Subject NullSub;

	/**
	 * Constructor for SubjectTest.
	 * @param arg0
	 */
	public SubjectTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Set set = new HashSet();
		set.add("user1");
		sub = Subject.createSubject(set, "user1");
		NullSub = Subject.getNullSubject();
	}

	public void testHashCode() {
		Set set = new HashSet();
		set.add("user1");
		Subject s1 = Subject.createSubject(set, "user1");
		assertEquals(sub.hashCode(), s1.hashCode());

		set = new HashSet();
		set.add("user2");
		s1 = Subject.createSubject(set, "user1");

		assertFalse(s1.hashCode() == sub.hashCode());
	}

	public void testSubject() {
		// オブジェクト生成のテスト
		try { 
			Subject.createSubject(null, "");
			fail();
		} catch (IllegalArgumentException e) {
		}

		try { 
			Subject.createSubject(new HashSet(), null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testGetNullSubject() {
		assertSame(NullSub, Subject.getNullSubject());
	}

	public void testGetPrincipals() {
		Set p1 = sub.getPrincipals();
		assertTrue(p1.contains("user1"));
		assertFalse(p1.contains("user2"));
		
		p1 = NullSub.getPrincipals();
		assertFalse(p1.contains("user1"));
	}

	public void testGetUserName() {
		assertEquals("user1", sub.getUserName());
		assertEquals("", NullSub.getUserName());
	}

	/*
	 * boolean equals のテスト(Object)
	 */
	public void testEqualsObject() {
		Set set = new HashSet();
		set.add("user1");
		Subject s1 = Subject.createSubject(set, "user1");
		assertEquals(sub, s1);

		set = new HashSet();
		set.add("user2");
		s1 = Subject.createSubject(set, "user1");

		assertFalse(s1.equals(sub));
	}

	/*
	 * String toString のテスト()
	 */
	public void testToString() {
		System.out.println(sub);
		assertEquals("principals=[user1] ,userName=user1", sub.toString());
	}
	
	public void testSerialize() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(sub);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		Subject d = (Subject) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(d, sub);
		assertTrue(d.equals(sub));

		temp.delete();
	}

}
