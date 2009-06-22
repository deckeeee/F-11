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

import jp.gr.javacons.jim.Manager;

import org.F11.scada.server.entity.AnalogType;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.test.util.TestUtil;
import org.seasar.extension.unit.S2TestCase;

/**
 * HolderRegisterBuilderImplのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class HolderRegisterBuilderImplTest extends S2TestCase {
    private HolderRegisterBuilder holderRegisterBuilder_;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(HolderRegisterBuilderImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        include("org/F11/scada/server/register/HolderRegisterBuilder.dicon");
        Manager manager = Manager.getInstance();
        manager.addDataProvider(TestUtil.createDP());
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        TestUtil.crearJIM();
        super.tearDown();
    }

    /**
     * Constructor for HolderRegisterBuilderImplTest.
     * @param arg0
     */
    public HolderRegisterBuilderImplTest(String arg0) {
        super(arg0);
    }

    public void testRegister() {
        Item[] items = new Item[6];
        items[0] = createDigital();
        items[1] = createAnalog4();
        items[2] = createAnalog();
        items[3] = createCalendar();
        items[4] = createSchedule();
        items[5] = createTimestamp();
        /*
        holderRegisterBuilder_.register(items);
        */
        
        T1 t1 = new T1(holderRegisterBuilder_, items);
        T1 t2 = new T1(holderRegisterBuilder_, items);
        t1.start();
        t2.start();
    }

    private Item createDigital() {
        Item item = new Item();
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("D_1900000_Digital");
        item.setDataType(0);
        item.setDataArgv("0");
        return item;
    }

    private Item createAnalog4() {
        Item item = new Item();
        item.setDataType(18);
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("analog4");
        item.setDataArgv("00");
        item.setComCycle(0);
        item.setComCycleMode(true);
        item.setComMemoryAddress(100);
        item.setComMemoryKinds(0);
        item.setOffDelay(new Integer(10));
        AnalogType at = new AnalogType();
        at.setAnalogTypeId(18);
        at.setAnalogTypeName("DEMAND");
        at.setConvertMax(new Double(400));
        at.setConvertMin(new Double(0));
        at.setInputMax(new Double(100));
        at.setInputMin(new Double(0));
        item.setAnalogType(at);
        return item;
    }
    
    private Item createAnalog() {
        Item item = new Item();
        item.setDataType(1);
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("analog");
        item.setDataArgv("00");
        item.setComCycle(0);
        item.setComCycleMode(true);
        item.setComMemoryAddress(100);
        item.setComMemoryKinds(0);
        item.setOffDelay(new Integer(10));
        AnalogType at = new AnalogType();
        at.setAnalogTypeId(18);
        at.setAnalogTypeName("DEMAND");
        at.setConvertMax(new Double(400));
        at.setConvertMin(new Double(0));
        at.setInputMax(new Double(100));
        at.setInputMin(new Double(0));
        item.setAnalogType(at);
        return item;
    }
    
    private Item createCalendar() {
        Item item = new Item();
        item.setDataType(15);
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("cal");
        item.setDataArgv("6");
        item.setComCycle(0);
        item.setComCycleMode(true);
        item.setComMemoryAddress(100);
        item.setComMemoryKinds(0);
        item.setOffDelay(new Integer(10));
        return item;
    }
    
    private Item createSchedule() {
        Item item = new Item();
        item.setDataType(16);
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("sche");
        item.setDataArgv("00");
        item.setComCycle(0);
        item.setComCycleMode(true);
        item.setComMemoryAddress(100);
        item.setComMemoryKinds(0);
        item.setOffDelay(new Integer(10));
        item.setDataArgv("TwoDays, 4");

        AnalogType at = new AnalogType();
        at.setAnalogTypeId(16);
        at.setAnalogTypeName("");
        at.setConvertMax(new Double(400));
        at.setConvertMin(new Double(0));
        at.setInputMax(new Double(100));
        at.setInputMin(new Double(0));
        item.setAnalogType(at);
        return item;
    }
    
    private Item createTimestamp() {
        Item item = new Item();
        item.setDataType(17);
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("ts");
        item.setDataArgv("00");
        item.setComCycle(0);
        item.setComCycleMode(true);
        item.setComMemoryAddress(100);
        item.setComMemoryKinds(0);
        item.setOffDelay(new Integer(10));
        return item;
    }
    
    class T1 extends Thread {
        private HolderRegisterBuilder holderRegisterBuilder_;
        private Item[] items;
        
        T1(HolderRegisterBuilder holderRegisterBuilder_, Item[] items) {
            this.holderRegisterBuilder_ = holderRegisterBuilder_;
            this.items = items;
        }

        public void run() {
            holderRegisterBuilder_.register(items);
        }
    }
}
