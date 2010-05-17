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

package org.F11.scada.server.alarm;

import java.util.ArrayList;
import java.util.Date;
import java.util.SortedSet;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderRegister;
import org.F11.scada.server.register.impl.DigitalHolderRegister;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.seasar.extension.unit.S2TestCase;

/**
 * AlarmReferencerImpのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmReferencerImplTest extends S2TestCase {
    private AlarmReferencer alarmReferencer;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AlarmReferencerImplTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        include("AlarmReferencerImplTest.dicon");
        alarmReferencer = (AlarmReferencer) getComponent(WifeDataProvider.PARA_NAME_ALARM);
        Manager manager = Manager.getInstance();
        DataProvider dp = TestUtil.createDP();
        dp.setParameter(WifeDataProvider.PARA_NAME_ALARM, alarmReferencer);
        manager.addDataProvider(dp);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        TestUtil.crearJIM();
    }

    /**
     * Constructor for AlarmReferencerImplTest.
     * @param arg0
     */
    public AlarmReferencerImplTest(String arg0) {
        super(arg0);
    }

    public void testAddReferencer() {
        HolderRegister hr = new DigitalHolderRegister();
        Item item = new Item();
        item.setPoint(new Integer(0));
        item.setProvider("P1");
        item.setHolder("D_1900000_Digital");
        item.setDataArgv("00");
        item.setComCycle(0);
        item.setComCycleMode(true);
        item.setComMemoryAddress(100);
        item.setComMemoryKinds(0);
        item.setOffDelay(new Integer(10));
        hr.register(item);

        DataStoreTest ds = new DataStoreTest();
        alarmReferencer.addDataStore(ds);
        SortedSet rfs = alarmReferencer.getReferencers();
        assertEquals(1, rfs.size());
        DataReferencer dr = (DataReferencer) rfs.first();
        assertEquals("P1", dr.getDataProviderName());
        assertEquals("D_1900000_Digital", dr.getDataHolderName());
        DataHolder dh = dr.getDataHolder();
        assertNotNull(dh);
        assertEquals(0, ds.eventList.size());
        
        // ホルダに値をセット
        dh.setValue(WifeDataDigital.valueOfTrue(1), new Date(), WifeQualityFlag.GOOD);
        assertEquals(1, ds.eventList.size());
    }

    static class DataStoreTest implements AlarmDataStore {
        ArrayList eventList = new ArrayList();

        public void put(DataValueChangeEventKey key) {
            eventList.add(key);
        }
    }
}
