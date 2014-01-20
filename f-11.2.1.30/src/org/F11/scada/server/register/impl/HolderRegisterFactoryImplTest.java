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

import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderRegisterFactory;
import org.seasar.extension.unit.S2TestCase;

/**
 * HolderRegisterFactoryImplのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class HolderRegisterFactoryImplTest extends S2TestCase {
    private HolderRegisterFactory holderRegisterFactory;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(HolderRegisterFactoryImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        include("org/F11/scada/server/register/HolderRegisterBuilder.dicon");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for HolderRegisterFactoryImplTest.
     * @param name
     */
    public HolderRegisterFactoryImplTest(String name) {
        super(name);
    }

    public void testGetHolderRegister() {
        Item item = new Item();
        item.setDataType(0);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof DigitalHolderRegister);
        item.setDataType(1);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof AnalogHolderRegister);
        item.setDataType(14);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof AnalogHolderRegister);
        item.setDataType(15);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof CalendarHolderRegister);
        item.setDataType(16);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof ScheduleHolderRegister);
        item.setDataType(17);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof TimestampHolderRegister);
        item.setDataType(18);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof Analog4HolderRegister);
        item.setDataType(20);
        assertTrue(holderRegisterFactory.getHolderRegister(item)
                instanceof Analog4HolderRegister);
        item.setDataType(-1);
        try {
            holderRegisterFactory.getHolderRegister(item);
            fail();
        } catch (IllegalArgumentException e) {}

        item.setDataType(Integer.MAX_VALUE);
        try {
            holderRegisterFactory.getHolderRegister(item);
            fail();
        } catch (IllegalArgumentException e) {}
    }
}
