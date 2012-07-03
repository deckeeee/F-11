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
 * HolderStringSetのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class HolderStringSetTest extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(HolderStringSetTest.class);
    }

    public void testSetAnalogValue() {
        HolderStringSet s = new HolderStringSet();
        s.setValue("P1_H0+P1_H1-P2_H0");
//        System.out.println(s);
        assertTrue(s.contains(new HolderString("P1_H0")));
        assertTrue(s.contains(new HolderString("P1_H1")));
        assertTrue(s.contains(new HolderString("P2_H0")));
        assertFalse(s.contains(new HolderString("P2_H1")));
    }

}
