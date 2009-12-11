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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import javax.swing.table.DefaultTableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.wifi.LocationHistorysDao;
import org.F11.scada.server.wifi.WiFiTerminalMap;
import org.F11.scada.server.wifi.dto.LocationHistorys;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.applet.Session;
import org.apache.log4j.Logger;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class LocationHistorysDataProviderTest extends S2TestCase {
    private WiFiTerminalMap terminals;
    private LocationHistorysDataProvider provider;
    private LocationHistorysDao dao;
    private AlarmReferencer alarm;
    private ItemDao itemDao;
    private HolderRegisterBuilder builder;
    private SendRequestSupport support;

    private static Logger logger = Logger.getLogger(LocationHistorysDataProviderTest.class);

    /**
     * Constructor for LocationHistorysDataProviderTest.
     * @param arg0
     */
    public LocationHistorysDataProviderTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        include("../WiFiTerminalMapTest.dicon");

        dao = new TestLocationHistorysDao();
        alarm = new TestAlarmReferencer();
        itemDao = new TestItemDao();
        builder = new TestHolderRegisterBuilder();
        support = new TestSendRequestSupport();
    }

    protected void tearDown() throws Exception {
        TestUtil.crearJIM();
    }

    public void testLocationHistorysDataProvider() throws Exception {
        provider = new LocationHistorysDataProvider("P1", 1000, terminals, dao, alarm, itemDao, builder);
        provider.setSendRequestSupport(support);

        DataHolder dh = TestUtil.createDigitalHolder("H1");
        TestDataValueChangeListener listener = new TestDataValueChangeListener();
        dh.addDataValueChangeListener(listener);
        provider.addDataHolder(dh);
        provider.start();

        sleep(10000);

        provider.stop();

        List evts = listener.getEvents();
        boolean isTrue = false;
        for (Iterator i = evts.iterator(); i.hasNext();) {
            DataValueChangeEvent evt = (DataValueChangeEvent) i.next();
            WifeDataDigital d = (WifeDataDigital) evt.getValue();
            if (isTrue) {
                assertTrue(d.isOnOff(true));
                isTrue = false;
            } else {
                assertTrue(d.isOnOff(false));
                isTrue = true;
            }
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {}
    }

    static class TestLocationHistorysDao implements LocationHistorysDao {
        private int count;

        public List findLocationHistorys(String id, int floorId,
                Timestamp timestamp) {
            return getLocationHistorys();
        }

        private List getLocationHistorys() {
            ArrayList list = new ArrayList();
            list.add(getLocationHistory());
            return list;
        }

        private LocationHistorys getLocationHistory() {
            LocationHistorys l = new LocationHistorys();
            l.setRowNum(count);
            l.setFloorId(1);
            l.setId("id1");
            l.setTimeStamp(new Timestamp(0));
            if (count == 0 || count % 2 == 0) {
                l.setXPosition(50);
                l.setYPosition(50);
            } else {
                l.setXPosition(200);
                l.setYPosition(200);
            }
            count++;
            logger.info(l);
            return l;
        }

        public void insertLocationHistorys(LocationHistorys historys) {}
    }


    static class TestAlarmReferencer extends DefaultTableModel implements AlarmReferencer {
        private static final long serialVersionUID = 4854836433719625071L;
		public boolean addDataStore(AlarmDataStore store) {
            return false;
        }
        public void addReferencer(DataReferencer rf) {
        }
        public SortedSet getReferencers() {
            return null;
        }
        public void removeReferencer(DataReferencer dr) {
        }
    }

    static class TestItemDao implements ItemDao {
        public Item getItem(HolderString holderString) {
            return null;
        }
        public Item[] getSystemItems(String provider, boolean system) {
            return null;
        }
		public Item selectItem(String provider, String holder) {
			return null;
		}
		public int updateItem(Item item) {
			return 0;
		}
		public Item[] getNoSystemItems() {
			return null;
		}
		public int updateJumpPage(String page, String provider, String holder) {
			return 0;
		}
    }

    static class TestHolderRegisterBuilder implements HolderRegisterBuilder {
        public void register(Item[] items) {
        }
        public void unregister(Item[] items) {
        }
    }

    static class TestSendRequestSupport implements SendRequestSupport {
        public void setSendRequestDateMap(Session session, long time) {
        }
    }

    static class TestDataValueChangeListener implements DataValueChangeListener {
        private ArrayList evts = new ArrayList();

        public void dataValueChanged(DataValueChangeEvent evt) {
            logger.info(evt);
            evts.add(evt);
        }

        List getEvents() {
            return evts;
        }
    }
}
