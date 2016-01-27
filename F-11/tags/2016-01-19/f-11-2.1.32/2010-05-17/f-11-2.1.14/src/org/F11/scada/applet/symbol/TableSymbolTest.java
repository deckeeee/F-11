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

package org.F11.scada.applet.symbol;

import java.awt.event.ActionListener;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * TableSymbolのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableSymbolTest extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TableSymbolTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for TableSymbolTest.
     * @param name
     */
    public TableSymbolTest(String name) {
        super(name);
    }

    public void testTableSymbol() throws Exception {
        TableSymbol t = new TableSymbol();
        WifeTimer timer = WifeTimer.getInstance();
        ActionListener[] l = timer.getActionListeners();
        System.out.println(Arrays.asList(l));
        assertEquals(2, l.length);
    }
}
