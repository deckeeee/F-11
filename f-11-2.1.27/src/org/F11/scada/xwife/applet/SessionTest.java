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

package org.F11.scada.xwife.applet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

/**
 * Sessionのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SessionTest extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(SessionTest.class);
    }

    /**
     * Constructor for SessionTest.
     * @param arg0
     */
    public SessionTest(String arg0) {
        super(arg0);
    }

    public void testSession() throws Exception {
        Session session1 = new Session();
        Session session2 = new Session();
        assertEquals(session1, session1);
        assertEquals(session1.hashCode(), session1.hashCode());
        assertFalse(session1.equals(session2));
        assertFalse(session1.hashCode() == session2.hashCode());
        System.out.println(session1);
        System.out.println(session2);
    }
    
    public void testSerial() throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		Session orig = new Session();
		outs.writeObject(orig);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		Session other = (Session) ins.readObject();
		ins.close();
		assertNotNull(other);
		assertEquals(orig, other);
		assertEquals(orig.getId(), other.getId());
		assertEquals(orig.hashCode(), other.hashCode());
		temp.delete();
    }
}
