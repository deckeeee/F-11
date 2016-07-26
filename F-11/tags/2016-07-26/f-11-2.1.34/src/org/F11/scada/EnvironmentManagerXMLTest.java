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
package org.F11.scada;

import junit.framework.TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class EnvironmentManagerXMLTest extends TestCase {

    /**
     * Constructor for EnvironmentManagerXMLTest.
     * @param arg0
     */
    public EnvironmentManagerXMLTest(String arg0) {
        super(arg0);
    }

    public void testGet() {
        EnvironmentManagerXML e = new EnvironmentManagerXML(
                "/org/F11/scada/EnvironmentManagerXMLTest.xml");
        assertEquals("a", e.get("server", "b"));
        assertEquals("aa", e.get("server/prop1", "bb"));
        assertEquals("aaa", e.get("server/prop1/prop2", "bbb"));
        assertEquals("b", e.get("servernon", "b"));
        assertEquals("bb", e.get("servernon/prop1", "bb"));
        assertEquals("bbb", e.get("servernon/prop1/prop2", "bbb"));
    }

}
