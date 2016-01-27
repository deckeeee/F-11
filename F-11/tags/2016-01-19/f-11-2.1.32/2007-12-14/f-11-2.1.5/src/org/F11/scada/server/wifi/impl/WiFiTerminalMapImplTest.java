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
package org.F11.scada.server.wifi.impl;

import junit.framework.TestCase;

import org.F11.scada.server.wifi.dto.WiFiTerminal;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class WiFiTerminalMapImplTest extends TestCase {

    /**
     * Constructor for WiFiTerminalMapTest.
     * @param arg0
     */
    public WiFiTerminalMapImplTest(String arg0) {
        super(arg0);
    }

    public void testWiFiTerminalMap() {
        WiFiTerminalMapImpl map = new WiFiTerminalMapImpl();
        WiFiTerminal t1 = new WiFiTerminal();
        t1.setProvier("P1");
        t1.setHolder("H1");
        t1.setId("id1");
        t1.setFloorId(1);
        t1.setX(0);
        t1.setY(0);
        t1.setWidth(100);
        t1.setHeight(100);
        map.put(t1);
        
        try {
            map.put(t1);
            fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        WiFiTerminal t2 = new WiFiTerminal();
        t2.setProvier("P1");
        t2.setHolder("H2");
        t2.setId("id1");
        t2.setFloorId(2);
        t2.setX(0);
        t2.setY(0);
        t2.setWidth(100);
        t2.setHeight(100);
        map.put(t2);
        
        assertEquals(t1, map.get("P1", "H1"));
        assertEquals(t2, map.get("P1", "H2"));
        assertFalse(t1.equals(t2));
    }
}
