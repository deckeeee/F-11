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

import java.util.ArrayList;
import java.util.SortedSet;

import javax.swing.table.DefaultTableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderRegister;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * DigitalHolderRegisterのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DigitalHolderRegisterTest extends TestCase {
    private DataProvider dp;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(DigitalHolderRegisterTest.class);
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
     * Constructor for DigitalHolderRegisterTest.
     * @param arg0
     */
    public DigitalHolderRegisterTest(String arg0) {
        super(arg0);
    }

    public void testRegister() {
        AlarmReferencerTest ar = new AlarmReferencerTest();
        dp.setParameter(WifeDataProvider.PARA_NAME_ALARM, ar);
        HolderRegister hr = new DigitalHolderRegister();
        Item item = new Item();
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("Test01");
        item.setDataArgv("00");
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
        assertEquals(1, ar.ref.size());
        assertNotNull(dh.getValue());
        assertTrue(dh.getValue() instanceof WifeDataDigital);
        hr.unregister(item);
        assertNull(dp.getDataHolder("Test01"));
        assertEquals(0, ar.ref.size());
    }

    static class AlarmReferencerTest extends DefaultTableModel
    		implements AlarmReferencer {
        private static final long serialVersionUID = -2261056060827639007L;
		ArrayList ref = new ArrayList();
        public boolean addDataStore(AlarmDataStore store) {
            return true;
        }
        public void addReferencer(DataReferencer rf) {
            ref.add(rf);
        }
        public void removeReferencer(DataReferencer dr) {
        	ref.remove(dr);
        }
        public SortedSet getReferencers() {
            return null;
        }
    }
}
