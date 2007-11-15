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

package org.F11.scada.server.register;

import junit.framework.TestCase;

/**
 * HolderStringのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class HolderStringTest extends TestCase {

    public void testSetValue() {
        HolderString h = new HolderString("P1_HOGEHOGE_ALARM_0");
        assertEquals("P1", h.getProvider());
        assertEquals("HOGEHOGE_ALARM_0", h.getHolder());
    }

    public void testObject() throws Exception {
        HolderString h = new HolderString("P1_HOGEHOGE_ALARM_0");
        HolderString h2 = new HolderString("P1_HOGEHOGE_ALARM_0");
        assertTrue(h.equals(h2));
        assertTrue(h.hashCode() == h2.hashCode());

        HolderString h3 = new HolderString("P1_HOGEHOGE_ALARM_1");
        assertFalse(h.equals(h3));
        assertFalse(h.hashCode() == h3.hashCode());

        HolderString h4 = new HolderString("P1", "HOGEHOGE_ALARM_1");
        assertFalse(h.equals(h4));
        assertFalse(h.hashCode() == h4.hashCode());
    }
}
