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

import org.F11.scada.server.io.SelectiveAllDataValueListHandler;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TimestampUtil;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class DefaultSelectiveAllDataGraphModelTest extends TestCase {
    private static final String LOG_TABLE_MINUTE = "log_table_minute";
    private GraphModel model;
    private static Logger log = Logger.getLogger(DefaultSelectiveAllDataGraphModelTest.class);

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        ArrayList list = new ArrayList();
        list.add(new HolderString("P1", "H1"));
        model = new DefaultSelectiveAllDataGraphModel(LOG_TABLE_MINUTE, list, new TestSelectiveAllDataValueListHandlerFactory(false), 4, new TestGraphPropertyModel());
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
    public DefaultSelectiveAllDataGraphModelTest(String arg0) {
        super(arg0);
    }


	public void testNext() throws Exception {
		model.next(LOG_TABLE_MINUTE);
		LoggingData data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:07:00"), data.getTimestamp());
		assertEquals(1.7D, data.next(), 0);
		assertEquals(2.7D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:08:00"), data.getTimestamp());
		assertEquals(1.8D, data.next(), 0);
		assertEquals(2.8D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:09:00"), data.getTimestamp());
		assertEquals(1.9D, data.next(), 0);
		assertEquals(2.9D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:10:00"), data.getTimestamp());
		assertEquals(2.0D, data.next(), 0);
		assertEquals(3.0D, data.next(), 0);

		assertFalse(model.next(LOG_TABLE_MINUTE));
	}

	
	public void testFindKey() throws Exception {
	    assertEquals(TimestampUtil.parse("2005/12/31 00:00:00"), model.firstKey(LOG_TABLE_MINUTE));
	    assertEquals(TimestampUtil.parse("2005/12/31 00:10:00"), model.lastKey(LOG_TABLE_MINUTE));

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:07:00"));
	    model.next(LOG_TABLE_MINUTE);
		LoggingData data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		// 1件前のデータが返る
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:06:00"), data.getTimestamp());
		assertEquals(1.6D, data.next(), 0);
		assertEquals(2.6D, data.next(), 0);

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/30 00:00:00"));
	    model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:00:00"), data.getTimestamp());
		assertEquals(1.0D, data.next(), 0);
		assertEquals(2.0D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:01:00"), data.getTimestamp());
		assertEquals(1.1D, data.next(), 0);
		assertEquals(2.1D, data.next(), 0);

		assertFalse(model.next(LOG_TABLE_MINUTE));

/*
		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:02:00"), data.getTimestamp());
		assertEquals(1.2D, data.next(), 0);
		assertEquals(2.2D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:03:00"), data.getTimestamp());
		assertEquals(1.3D, data.next(), 0);
		assertEquals(2.3D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:04:00"), data.getTimestamp());
		assertEquals(1.4D, data.next(), 0);
		assertEquals(2.4D, data.next(), 0);
*/
	}
	
	// 行って戻るとか、戻って行くとか、テストに色々バリエーションを付けてみる。
	public void testFindAndNext() throws Exception {
		model.next(LOG_TABLE_MINUTE);
		LoggingData data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:07:00"), data.getTimestamp());
		assertEquals(1.7D, data.next(), 0);
		assertEquals(2.7D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:08:00"), data.getTimestamp());
		assertEquals(1.8D, data.next(), 0);
		assertEquals(2.8D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:09:00"), data.getTimestamp());
		assertEquals(1.9D, data.next(), 0);
		assertEquals(2.9D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:10:00"), data.getTimestamp());
		assertEquals(2.0D, data.next(), 0);
		assertEquals(3.0D, data.next(), 0);
		
		assertFalse(model.next(LOG_TABLE_MINUTE));
	}

	public void testFindAndNext2() throws Exception {
	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:06:00"));

	    model.next(LOG_TABLE_MINUTE);
	    LoggingData data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:05:00"), data.getTimestamp());
		assertEquals(1.5, data.next(), 0);
		assertEquals(2.5, data.next(), 0);

	    model.next(LOG_TABLE_MINUTE);
	    data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:06:00"), data.getTimestamp());
		assertEquals(1.6, data.next(), 0);
		assertEquals(2.6, data.next(), 0);

		assertFalse(model.next(LOG_TABLE_MINUTE));

/*
		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:07:00"), data.getTimestamp());
		assertEquals(1.7D, data.next(), 0);
		assertEquals(2.7D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:08:00"), data.getTimestamp());
		assertEquals(1.8D, data.next(), 0);
		assertEquals(2.8D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:09:00"), data.getTimestamp());
		assertEquals(1.9D, data.next(), 0);
		assertEquals(2.9D, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:10:00"), data.getTimestamp());
		assertEquals(2.0D, data.next(), 0);
		assertEquals(3.0D, data.next(), 0);
		
		assertFalse(model.next(LOG_TABLE_MINUTE));
*/
		
	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:05:00"));

	    model.next(LOG_TABLE_MINUTE);
	    data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:04:00"), data.getTimestamp());
		assertEquals(1.4, data.next(), 0);
		assertEquals(2.4, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
	    data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:05:00"), data.getTimestamp());
		assertEquals(1.5, data.next(), 0);
		assertEquals(2.5, data.next(), 0);

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:04:00"));

	    model.next(LOG_TABLE_MINUTE);
	    data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:03:00"), data.getTimestamp());
		assertEquals(1.3, data.next(), 0);
		assertEquals(2.3, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
	    data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:04:00"), data.getTimestamp());
		assertEquals(1.4, data.next(), 0);
		assertEquals(2.4, data.next(), 0);

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:03:00"));

	    model.next(LOG_TABLE_MINUTE);
	    data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:02:00"), data.getTimestamp());
		assertEquals(1.2, data.next(), 0);
		assertEquals(2.2, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
	    data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:03:00"), data.getTimestamp());
		assertEquals(1.3, data.next(), 0);
		assertEquals(2.3, data.next(), 0);

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:02:00"));

	    model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:01:00"), data.getTimestamp());
		assertEquals(1.1, data.next(), 0);
		assertEquals(2.1, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:02:00"), data.getTimestamp());
		assertEquals(1.2, data.next(), 0);
		assertEquals(2.2, data.next(), 0);

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:01:00"));

	    model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:00:00"), data.getTimestamp());
		assertEquals(1.0, data.next(), 0);
		assertEquals(2.0, data.next(), 0);

		model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:01:00"), data.getTimestamp());
		assertEquals(1.1, data.next(), 0);
		assertEquals(2.1, data.next(), 0);

	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:00:00"));
	    model.next(LOG_TABLE_MINUTE);
		data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:00:00"), data.getTimestamp());
		assertEquals(1.0, data.next(), 0);
		assertEquals(2.0, data.next(), 0);
	}
	
	public void testFindAndNext3() throws Exception {
	    model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:00:00"));
	    model.next(LOG_TABLE_MINUTE);
	    LoggingData data = (LoggingData) model.get(LOG_TABLE_MINUTE);
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/12/31 00:00:00"), data.getTimestamp());
		assertEquals(1.0, data.next(), 0);
		assertEquals(2.0, data.next(), 0);
		
		assertTrue(model.next(LOG_TABLE_MINUTE));
		assertFalse(model.next(LOG_TABLE_MINUTE));
	}
	
	public void testEmptyData() throws Exception {
        ArrayList list = new ArrayList();
        list.add(new HolderString("P1", "H1"));
		GraphModel model = new DefaultSelectiveAllDataGraphModel(
				LOG_TABLE_MINUTE, list,
				new TestSelectiveAllDataValueListHandlerFactory(true), 4,
				new TestGraphPropertyModel());
		model.findRecord(LOG_TABLE_MINUTE, TimestampUtil.parse("2005/12/31 00:00:00"));
		assertFalse(model.next(LOG_TABLE_MINUTE));
		System.out.println(model.firstKey(LOG_TABLE_MINUTE));
		System.out.println(model.lastKey(LOG_TABLE_MINUTE));
	}

    static class TestSelectiveAllDataValueListHandlerFactory implements SelectiveAllDataValueListHandlerFactory {
    	private final boolean isEmpty;
    	TestSelectiveAllDataValueListHandlerFactory(boolean isEmpty) {
    		this.isEmpty = isEmpty;
    	}

        public SelectiveAllDataValueListHandler getSelectiveAllDataValueListHandler() {
            return new TestSelectiveAllDataValueListHandler(isEmpty);
        }
        
        static class TestSelectiveAllDataValueListHandler implements SelectiveAllDataValueListHandler {
            private SortedMap mainMap;

            TestSelectiveAllDataValueListHandler(boolean isEmpty) {
                mainMap = new TreeMap();
                if (!isEmpty) {
	                insertSortedMap(mainMap, "2005/12/31 00:00:00", 1.0, 2.0);
	                insertSortedMap(mainMap, "2005/12/31 00:01:00", 1.1, 2.1);
	                insertSortedMap(mainMap, "2005/12/31 00:02:00", 1.2, 2.2);
	                insertSortedMap(mainMap, "2005/12/31 00:03:00", 1.3, 2.3);
	                insertSortedMap(mainMap, "2005/12/31 00:04:00", 1.4, 2.4);
	                insertSortedMap(mainMap, "2005/12/31 00:05:00", 1.5, 2.5);
	                insertSortedMap(mainMap, "2005/12/31 00:06:00", 1.6, 2.6);
	                insertSortedMap(mainMap, "2005/12/31 00:07:00", 1.7, 2.7);
	                insertSortedMap(mainMap, "2005/12/31 00:08:00", 1.8, 2.8);
	                insertSortedMap(mainMap, "2005/12/31 00:09:00", 1.9, 2.9);
	                insertSortedMap(mainMap, "2005/12/31 00:10:00", 2.0, 3.0);
                }
            }

            private void insertSortedMap(SortedMap map, String time, double data1, double data2) {
                Timestamp timestamp = TimestampUtil.parse(time);
                ArrayDoubleList list = new ArrayDoubleList();
                list.add(data1);
                list.add(data2);
                map.put(timestamp, list);
            }

            public SortedMap getInitialData(String name, List holderStrings)
                    throws RemoteException {
                return mainMap.subMap(
                        TimestampUtil.parse("2005/12/31 00:07:00"),
                        TimestampUtil.parse("2005/12/31 00:11:00"));
            }
            public SortedMap getInitialData(String name, List holderStrings, int limit)
            	throws RemoteException {
            	return mainMap.subMap(
		                TimestampUtil.parse("2005/12/31 00:07:00"),
		                TimestampUtil.parse("2005/12/31 00:11:00"));
		    }

            public Map getUpdateLoggingData(String name, Timestamp key,
                    List holderStrings) throws RemoteException {
                throw new UnsupportedOperationException();
            }
            public Timestamp firstTime(String name, List holderStrings)
                    throws RemoteException {
                return TimestampUtil.parse("2005/12/31 00:00:00");
            }
            public Timestamp lastTime(String name, List holderStrings)
                    throws RemoteException {
                return TimestampUtil.parse("2005/12/31 00:10:00");
            }
            public SortedMap getLoggingData(String name, List holderStrings, Timestamp start, int limit) throws RemoteException {
                Timestamp t1 = new Timestamp((start.getTime() - 60000L * 4));
                log.info(t1 + " " + start);
                SortedMap rmap = mainMap.subMap(t1, new Timestamp(start.getTime() + 60001L));
                log.info(rmap);
                return rmap;
            }
        }
    }
}
