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
package org.F11.scada.server.operationlog.dao;

import java.util.List;

import org.F11.scada.server.operationlog.dto.FinderConditionDto;
import org.F11.scada.server.operationlog.dto.OperationLogging;
import org.F11.scada.test.util.TimestampUtil;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingFinderDaoTest extends S2DaoTestCase {
    private OperationLoggingFinderDao dao;
    private OperationLoggingDao loggingDao;

    public OperationLoggingFinderDaoTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        include("OperationLoggingFinderDaoTest.dicon");
    }

    public void testSelectTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        List l = dao.select(finder);
        
        assertEquals(2, l.size());
        
        assertOperationLogging(o2, (OperationLogging) l.get(0));
        assertOperationLogging(o, (OperationLogging) l.get(1));
    }

    public void testSelectDateTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        finder.setStartDate(TimestampUtil.parse("2005/01/03 04:05:06"));
        finder.setEndDate(TimestampUtil.parse("2005/01/03 04:05:06"));
        List l = dao.select(finder);
        
        assertEquals(1, l.size());
        
        assertOperationLogging(o2, (OperationLogging) l.get(0));
    }

    public void testSelectUserTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        finder.setOpeUser("user");
        List l = dao.select(finder);
        
        assertEquals(1, l.size());
        
        assertOperationLogging(o2, (OperationLogging) l.get(0));
    }

    public void testSelectIpTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        finder.setOpeIp("192.168.0.1");
        List l = dao.select(finder);
        
        assertEquals(1, l.size());
        
        assertOperationLogging(o2, (OperationLogging) l.get(0));
    }

    public void testSelectUnitTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        finder.setOpeName("1F BŽº ‹ó’²‹@");
        List l = dao.select(finder);
        
        assertEquals(1, l.size());
        
        assertOperationLogging(o2, (OperationLogging) l.get(0));
    }

    public void testSelectDateAndLimitTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        finder.setStartDate(TimestampUtil.parse("2005/01/02 03:04:05"));
        finder.setEndDate(TimestampUtil.parse("2005/01/03 04:05:06"));
        finder.setLimit(new Integer(1));
        List l = dao.select(finder);
        
        assertEquals(1, l.size());
        
        assertOperationLogging(o2, (OperationLogging) l.get(0));
    }

    public void testSelectUnitAndMessageTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        finder.setOpeName("1F BŽº ‹ó’²‹@");
        finder.setOpeMessage("•œ‹Œ");

        List l = dao.select(finder);
        
        assertEquals(1, l.size());
        
        assertOperationLogging(o2, (OperationLogging) l.get(0));
    }

    public void testGetCountTx() throws Exception {
        OperationLogging o = create1();
        loggingDao.insert(o);

        OperationLogging o2 = create2();
        loggingDao.insert(o2);

        FinderConditionDto finder = new FinderConditionDto();
        finder.setStartDate(TimestampUtil.parse("2005/01/02 03:04:05"));
        finder.setEndDate(TimestampUtil.parse("2005/01/03 04:05:06"));
        finder.setCurrentId(new Long(10)); // id‚Í–³Ž‹‚³‚ê‚é
        finder.setLimit(new Integer(1)); // LIMIT‚Í–³Ž‹‚³‚ê‚é
        int count = dao.getCount(finder);
        
        assertEquals(2, count);
    }
    
    private OperationLogging create1() {
        OperationLogging o = new OperationLogging();
        o.setOpeDate(TimestampUtil.parse("2005/01/02 03:04:05"));
        o.setOpeIp("192.168.0.0");
        o.setOpeUser("user user");
        o.setOpeBeforeValue("0");
        o.setOpeAfterValue("1");
        o.setOpeProvider("P1");
        o.setOpeHolder("D_1900000_Digital");
        return o;
    }
    
    private OperationLogging create2() {
        OperationLogging o2 = new OperationLogging();
        o2.setOpeDate(TimestampUtil.parse("2005/01/03 04:05:06"));
        o2.setOpeIp("192.168.0.1");
        o2.setOpeUser("user");
        o2.setOpeBeforeValue("0");
        o2.setOpeAfterValue("1");
        o2.setOpeProvider("P1");
        o2.setOpeHolder("D_1900002_Digital");
        return o2;
    }
    
    private void assertOperationLogging(OperationLogging src, OperationLogging dst) throws Exception {
        assertEquals(src.getOpeDate(), dst.getOpeDate());
        assertEquals(src.getOpeIp(), dst.getOpeIp());
        assertEquals(src.getOpeUser(), dst.getOpeUser());
        assertEquals(src.getOpeBeforeValue(), dst.getOpeBeforeValue());
        assertEquals(src.getOpeAfterValue(), dst.getOpeAfterValue());
        assertEquals(src.getOpeProvider(), dst.getOpeProvider());
        assertEquals(src.getOpeHolder(), dst.getOpeHolder());
    }

}
