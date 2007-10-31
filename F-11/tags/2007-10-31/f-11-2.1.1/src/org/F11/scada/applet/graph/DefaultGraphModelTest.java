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
package org.F11.scada.applet.graph;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.F11.scada.server.io.ValueListHandler;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;
import org.apache.commons.collections.primitives.ArrayDoubleList;

/**
 * DefaultGraphModelのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultGraphModelTest extends TestCase {
	private String[] names;
	private GraphModel model;

	/**
	 * Constructor for DefaultGraphModelTest.
	 * @param arg0
	 */
	public DefaultGraphModelTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		names = new String[]{"log_table_minute", "log_table_hour", "log_table_daily"};
		model = new DefaultGraphModel(names, new TestValueListHandlerFactory());
		TestUtil.sleep(5000L);
	}

	protected void tearDown() throws Exception {
	}

	public void testNext() throws Exception {
		for (int i = 0; i < names.length; i++) {
			while(model.next(names[i])) {
				LoggingData data = (LoggingData) model.get(names[i]);
				assertNotNull(data);
				assertNotNull(data.getTimestamp());
				assertEquals(0, data.next(), 1.0);
				assertEquals(0, data.next(), 2.0);
			}
		}
	}
	
	public void testFindKey() throws Exception {
	    assertEquals(TimestampUtil.parse("2005/01/01 00:00:00"),model.firstKey("log_table_minute"));
	    assertEquals(TimestampUtil.parse("2005/01/01 00:00:03"),model.lastKey("log_table_minute"));

	    model.findRecord("log_table_minute", TimestampUtil.parse("2005/01/01 00:00:01"));
	    model.next("log_table_minute");
		LoggingData data = (LoggingData) model.get("log_table_minute");
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/01/01 00:00:01"), data.getTimestamp());
		assertEquals(0, data.next(), 1.0);
		assertEquals(0, data.next(), 2.0);

	    model.findRecord("log_table_minute", TimestampUtil.parse("2004/01/01 00:00:00"));
	    model.next("log_table_minute");
		data = (LoggingData) model.get("log_table_minute");
		assertNotNull(data);
		assertEquals(TimestampUtil.parse("2005/01/01 00:00:00"), data.getTimestamp());
		assertEquals(0, data.next(), 1.0);
		assertEquals(0, data.next(), 2.0);
	}

	static class TestValueListHandlerFactory implements ValueListHandlerFactory {
        public ValueListHandler getValueListHandler() {
            return new TestValueListHandler();
        }
        
        static class TestValueListHandler implements ValueListHandler {
            
            public void findRecord(String name, Timestamp key)
                    throws RemoteException {
            }
            public Object firstKey(String name) throws RemoteException {
                return null;
            }
            public SortedMap getInitialData(String name) throws RemoteException {
                TreeMap map = new TreeMap();
                insertSortedMap(map, "2005/01/01 00:00:00", 1.0, 2.0);
                insertSortedMap(map, "2005/01/01 00:00:01", 1.0, 2.0);
                return map;
            }
            private void insertSortedMap(SortedMap map, String time, double data1, double data2) {
                Timestamp timestamp = TimestampUtil.parse(time);
                ArrayDoubleList list = new ArrayDoubleList();
                list.add(data1);
                list.add(data2);
                map.put(timestamp, list);
            }
            public Map getUpdateLoggingData(String name, Timestamp key)
                    throws RemoteException {
                TreeMap map = new TreeMap();
                insertSortedMap(map, "2005/01/01 00:00:02", 1.0, 2.0);
                insertSortedMap(map, "2005/01/01 00:00:03", 1.0, 2.0);
                return map;
            }
            public boolean hasNext(String name) throws RemoteException {
                return false;
            }
            public Object lastKey(String name) throws RemoteException {
                return null;
            }
            public Object next(String name) throws RemoteException {
                return null;
            }
        }
	}
}
