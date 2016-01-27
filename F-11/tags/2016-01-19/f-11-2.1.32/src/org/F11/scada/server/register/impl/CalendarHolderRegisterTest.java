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

package org.F11.scada.server.register.impl;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataCalendar;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderRegister;
import org.F11.scada.test.util.TestUtil;

/**
 * CalendarHolderRegisterのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CalendarHolderRegisterTest extends TestCase {
    private DataProvider dp;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(Analog4HolderRegisterTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        dp = TestUtil.createDataProvider();
        Manager.getInstance().addDataProvider(dp);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        Manager.getInstance().removeDataProvider(dp);
    }

    /**
     * Constructor for CalendarHolderRegisterTest.
     * @param arg0
     */
    public CalendarHolderRegisterTest(String arg0) {
        super(arg0);
    }

    public void testRegister() {
        HolderRegister hr = new CalendarHolderRegister();
        Item item = new Item();
        item.setDataType(15);
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("Test01");
        item.setDataArgv("6");
        item.setComCycle(0);
        item.setComCycleMode(true);
        item.setComMemoryAddress(100);
        item.setComMemoryKinds(0);
        item.setOffDelay(new Integer(10));
        hr.register(item);
        Manager manager = Manager.getInstance();
        assertNotNull(dp);
        assertSame(dp, manager.getDataProvider("P1")); 
        DataHolder dh = dp.getDataHolder("Test01");
        assertNotNull(dh);
        assertNotNull(dh.getValue());
        assertTrue(dh.getValue() instanceof WifeDataCalendar);
        hr.unregister(item);
        assertNull(dp.getDataHolder("Test01"));
    }

}
