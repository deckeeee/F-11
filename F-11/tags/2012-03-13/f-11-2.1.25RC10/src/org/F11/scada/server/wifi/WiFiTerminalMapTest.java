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
package org.F11.scada.server.wifi;

import org.F11.scada.server.wifi.dto.WiFiTerminal;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class WiFiTerminalMapTest extends S2TestCase {
    private WiFiTerminalMap map;

    public WiFiTerminalMapTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        include("WiFiTerminalMapTest.dicon");
    }
    
    public void testWiFiTerminalMap() throws Exception {
        WiFiTerminal t1 = map.get("P1", "H1");
        assertEquals("P1", t1.getProvier());
        assertEquals("H1", t1.getHolder());
        assertEquals("id1", t1.getId());
        assertEquals(1, t1.getFloorId());
        assertEquals(0, t1.getX());
        assertEquals(0, t1.getY());
        assertEquals(100, t1.getWidth());
        assertEquals(100, t1.getHeight());

        WiFiTerminal t2 = map.get("P1", "H2");
        assertEquals("P1", t2.getProvier());
        assertEquals("H2", t2.getHolder());
        assertEquals("id1", t2.getId());
        assertEquals(2, t2.getFloorId());
        assertEquals(0, t2.getX());
        assertEquals(0, t2.getY());
        assertEquals(100, t2.getWidth());
        assertEquals(100, t2.getHeight());
    }
}
