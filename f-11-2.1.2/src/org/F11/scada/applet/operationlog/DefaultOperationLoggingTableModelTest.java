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
package org.F11.scada.applet.operationlog;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.F11.scada.server.operationlog.OperationLoggingFinderService;
import org.F11.scada.server.operationlog.dto.FinderConditionDto;
import org.F11.scada.server.operationlog.dto.OperationLoggingFinderDto;
import org.F11.scada.test.util.TimestampUtil;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class DefaultOperationLoggingTableModelTest extends TestCase {

    /**
     * Constructor for DefaultOperationLoggingTableModelTest.
     * @param arg0
     */
    public DefaultOperationLoggingTableModelTest(String arg0) {
        super(arg0);
    }

    public void testFind() {
        DefaultOperationLoggingTableModel model =
            new DefaultOperationLoggingTableModel(new TestOperationLoggingFinderService());
        FinderConditionDto finder = new FinderConditionDto();
        finder.setCurrentId(new Long(59));
        model.find(finder);
        assertEquals(3, model.getAllPage());
        assertEquals(1, model.getCurrentPage());

        assertEquals("2005/07/01 13:00:59", model.getValueAt(0, 1));
        assertEquals("192.168.0.1", model.getValueAt(0, 2));
        assertEquals("user", model.getValueAt(0, 3));
        assertEquals("100", model.getValueAt(0, 4));
        assertEquals("500", model.getValueAt(0, 5));
        assertEquals("AHU-1-1", model.getValueAt(0, 6));

        assertEquals("2005/07/01 13:00:58", model.getValueAt(1, 1));
        assertEquals("192.168.0.1", model.getValueAt(1, 2));
        assertEquals("user", model.getValueAt(1, 3));
        assertEquals("100", model.getValueAt(1, 4));
        assertEquals("500", model.getValueAt(1, 5));
        assertEquals("AHU-1-1", model.getValueAt(1, 6));
    }

    public void testNextPrevious() {
        DefaultOperationLoggingTableModel model =
            new DefaultOperationLoggingTableModel(new TestOperationLoggingFinderService());
        FinderConditionDto finder = new FinderConditionDto();
        finder.setCurrentId(new Long(59));
        model.find(finder);

        // 2ページ目
        model.next();
        assertEquals(3, model.getAllPage());
        assertEquals(2, model.getCurrentPage());

        assertEquals("2005/07/01 13:00:39", model.getValueAt(0, 1));
        assertEquals("192.168.0.1", model.getValueAt(0, 2));
        assertEquals("user", model.getValueAt(0, 3));
        assertEquals("100", model.getValueAt(0, 4));
        assertEquals("500", model.getValueAt(0, 5));
        assertEquals("AHU-1-1", model.getValueAt(0, 6));

        assertEquals("2005/07/01 13:00:38", model.getValueAt(1, 1));
        assertEquals("192.168.0.1", model.getValueAt(1, 2));
        assertEquals("user", model.getValueAt(1, 3));
        assertEquals("100", model.getValueAt(1, 4));
        assertEquals("500", model.getValueAt(1, 5));
        assertEquals("AHU-1-1", model.getValueAt(1, 6));

        // 3ページ目
        model.next();
        assertEquals(3, model.getCurrentPage());

        assertEquals("2005/07/01 13:00:19", model.getValueAt(0, 1));
        assertEquals("192.168.0.1", model.getValueAt(0, 2));
        assertEquals("user", model.getValueAt(0, 3));
        assertEquals("100", model.getValueAt(0, 4));
        assertEquals("500", model.getValueAt(0, 5));
        assertEquals("AHU-1-1", model.getValueAt(0, 6));

        assertEquals("2005/07/01 13:00:18", model.getValueAt(1, 1));
        assertEquals("192.168.0.1", model.getValueAt(1, 2));
        assertEquals("user", model.getValueAt(1, 3));
        assertEquals("100", model.getValueAt(1, 4));
        assertEquals("500", model.getValueAt(1, 5));
        assertEquals("AHU-1-1", model.getValueAt(1, 6));

        // 4ページ目は無いので3ページ目
        model.next();
        assertEquals(3, model.getCurrentPage());

        assertEquals("2005/07/01 13:00:19", model.getValueAt(0, 1));
        assertEquals("192.168.0.1", model.getValueAt(0, 2));
        assertEquals("user", model.getValueAt(0, 3));
        assertEquals("100", model.getValueAt(0, 4));
        assertEquals("500", model.getValueAt(0, 5));
        assertEquals("AHU-1-1", model.getValueAt(0, 6));

        assertEquals("2005/07/01 13:00:18", model.getValueAt(1, 1));
        assertEquals("192.168.0.1", model.getValueAt(1, 2));
        assertEquals("user", model.getValueAt(1, 3));
        assertEquals("100", model.getValueAt(1, 4));
        assertEquals("500", model.getValueAt(1, 5));
        assertEquals("AHU-1-1", model.getValueAt(1, 6));

        // 2ページ目
        model.previous();
        assertEquals(2, model.getCurrentPage());

        assertEquals("2005/07/01 13:00:39", model.getValueAt(0, 1));
        assertEquals("192.168.0.1", model.getValueAt(0, 2));
        assertEquals("user", model.getValueAt(0, 3));
        assertEquals("100", model.getValueAt(0, 4));
        assertEquals("500", model.getValueAt(0, 5));
        assertEquals("AHU-1-1", model.getValueAt(0, 6));

        assertEquals("2005/07/01 13:00:38", model.getValueAt(1, 1));
        assertEquals("192.168.0.1", model.getValueAt(1, 2));
        assertEquals("user", model.getValueAt(1, 3));
        assertEquals("100", model.getValueAt(1, 4));
        assertEquals("500", model.getValueAt(1, 5));
        assertEquals("AHU-1-1", model.getValueAt(1, 6));

        // 1ページ目
        model.previous();
        assertEquals(1, model.getCurrentPage());

        assertEquals("2005/07/01 13:00:59", model.getValueAt(0, 1));
        assertEquals("192.168.0.1", model.getValueAt(0, 2));
        assertEquals("user", model.getValueAt(0, 3));
        assertEquals("100", model.getValueAt(0, 4));
        assertEquals("500", model.getValueAt(0, 5));
        assertEquals("AHU-1-1", model.getValueAt(0, 6));

        assertEquals("2005/07/01 13:00:58", model.getValueAt(1, 1));
        assertEquals("192.168.0.1", model.getValueAt(1, 2));
        assertEquals("user", model.getValueAt(1, 3));
        assertEquals("100", model.getValueAt(1, 4));
        assertEquals("500", model.getValueAt(1, 5));
        assertEquals("AHU-1-1", model.getValueAt(1, 6));

        // 0ページ目は無いので1ページ
        model.previous();
        assertEquals(1, model.getCurrentPage());

        assertEquals("2005/07/01 13:00:59", model.getValueAt(0, 1));
        assertEquals("192.168.0.1", model.getValueAt(0, 2));
        assertEquals("user", model.getValueAt(0, 3));
        assertEquals("100", model.getValueAt(0, 4));
        assertEquals("500", model.getValueAt(0, 5));
        assertEquals("AHU-1-1", model.getValueAt(0, 6));

        assertEquals("2005/07/01 13:00:58", model.getValueAt(1, 1));
        assertEquals("192.168.0.1", model.getValueAt(1, 2));
        assertEquals("user", model.getValueAt(1, 3));
        assertEquals("100", model.getValueAt(1, 4));
        assertEquals("500", model.getValueAt(1, 5));
        assertEquals("AHU-1-1", model.getValueAt(1, 6));
    }
    
    static class TestOperationLoggingFinderService implements OperationLoggingFinderService {
        private TreeMap map = new TreeMap(new Comparator() {
        	public int compare(Object o1, Object o2) {
        		Long l1 = (Long) o1;
        		Long l2 = (Long) o2;
        		if (l1.longValue() < l2.longValue()) {
        			return 1;
        		} else if (l1.longValue() > l2.longValue()) {
        			return -1;
        		}
        		return 0;
        	}
        });
        
        TestOperationLoggingFinderService() {
            createMap();
        }
        
        public int getCount(FinderConditionDto finder) throws RemoteException {
            return map.size();
        }

        public List getOperationLogging(FinderConditionDto finder)
                throws RemoteException {
            SortedMap smap = map.tailMap(new Long(finder.getCurrentId().longValue()));
            ArrayList l = new ArrayList();
            int max = 0;
            for (Iterator i = smap.values().iterator(); i.hasNext() && max < 20; max++) {
                OperationLoggingFinderDto dto = (OperationLoggingFinderDto) i.next();
                l.add(dto);
            }
            System.out.println(l);
            return l;
        }

        private void createMap() {
            Timestamp time = TimestampUtil.parse("2005/07/01 13:00:00");
            for (int i = 0; i < 60; i++, time = new Timestamp(time.getTime() + 1000L)) {
                OperationLoggingFinderDto dto =  new OperationLoggingFinderDto();
                dto.setId(i);
                dto.setOpeDate(time);
                dto.setOpeIp("192.168.0.1");
                dto.setOpeUser("user");
                dto.setOpeBeforeValue("100");
                dto.setOpeAfterValue("500");
                dto.setOpeProvider("P1");
                dto.setOpeHolder("H1");
                dto.setUnit("AHU-1-1");
                dto.setName("1F 空調機");
                map.put(new Long(i), dto);
            }
//            System.out.println(map);
        }

        public boolean isPrefix() {
        	return true;
        }
    }
}
