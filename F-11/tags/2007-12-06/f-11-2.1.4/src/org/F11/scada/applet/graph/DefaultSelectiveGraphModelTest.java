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
package org.F11.scada.applet.graph;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.F11.scada.server.io.SelectiveValueListHandler;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;
import org.apache.commons.collections.primitives.ArrayDoubleList;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class DefaultSelectiveGraphModelTest extends TestCase {
    private static final String LOG_TABLE_MINUTE = "log_table_minute";
    private GraphModel model;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        ArrayList list = new ArrayList();
        list.add(createHolderString("P1", "H1"));
        model = new DefaultSelectiveGraphModel(LOG_TABLE_MINUTE, list, new TestSelectiveValueListHandlerFactory(), null, 4096);
        TestUtil.sleep(5000L);
    }
    
    private HolderString createHolderString(String provider, String holder) {
        HolderString hs = new HolderString();
        hs.setProvider(provider);
        hs.setHolder(holder);
        return hs;
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for DefaultSelectiveGraphModelTest.
     * @param arg0
     */
    public DefaultSelectiveGraphModelTest(String arg0) {
        super(arg0);
    }


	public void testNext() throws Exception {
		while(model.next(LOG_TABLE_MINUTE)) {
			LoggingData data = (LoggingData) model.get(LOG_TABLE_MINUTE);
			assertNotNull(data);
			assertNotNull(data.getTimestamp());
			assertEquals(1.0, data.next(), 0);
			assertEquals(2.0, data.next(), 0);
		}
	}
	
	public void testFindKey() throws Exception {
	    assertEquals(TimestampUtil.parse("2005/01/01 00:00:00"),model.firstKey(LOG_TABLE_MINUTE));
	    assertEquals(TimestampUtil.parse("2005/01/01 00:00:03"),model.lastKey(LOG_TABLE_MINUTE));

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/01/01 00:00:01"));
	    model.next(LOG_TABLE_MINUTE);
		LoggingData data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/01/01 00:00:01"), data.getTimestamp());
		assertEquals(1.0, data.next(), 0);
		assertEquals(2.0, data.next(), 0);

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2004/01/01 00:00:00"));
	    model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/01/01 00:00:00"), data.getTimestamp());
		assertEquals(1.0, data.next(), 0);
		assertEquals(2.0, data.next(), 0);
	}

    static class TestSelectiveValueListHandlerFactory implements SelectiveValueListHandlerFactory {
        public SelectiveValueListHandler getSelectiveValueListHandler() {
            return new TestSelectiveValueListHandler();
        }
        
        static class TestSelectiveValueListHandler implements SelectiveValueListHandler {
            public SortedMap getInitialData(String name, List holderStrings)
                    throws RemoteException {
                TreeMap map = new TreeMap();
                insertSortedMap(map, "2005/01/01 00:00:00", 1.0, 2.0);
                insertSortedMap(map, "2005/01/01 00:00:01", 1.0, 2.0);
                return map;
            }

            public SortedMap getInitialData(String name, List holderStrings, int lmit)
            	throws RemoteException {
		        TreeMap map = new TreeMap();
		        insertSortedMap(map, "2005/01/01 00:00:00", 1.0, 2.0);
		        insertSortedMap(map, "2005/01/01 00:00:01", 1.0, 2.0);
		        return map;
		    }

            public Map getUpdateLoggingData(String name, Timestamp key,
                    List holderStrings) throws RemoteException {
                TreeMap map = new TreeMap();
                insertSortedMap(map, "2005/01/01 00:00:02", 1.0, 2.0);
                insertSortedMap(map, "2005/01/01 00:00:03", 1.0, 2.0);
                return map;
            }
            private void insertSortedMap(SortedMap map, String time, double data1, double data2) {
                Timestamp timestamp = TimestampUtil.parse(time);
                ArrayDoubleList list = new ArrayDoubleList();
                list.add(data1);
                list.add(data2);
                map.put(timestamp, list);
            }
        }
    }
}
