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

import org.F11.scada.server.operationlog.dto.OperationLogging;
import org.F11.scada.test.util.TimestampUtil;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingDaoTest extends S2DaoTestCase {
    private OperationLoggingDao dao;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        include("OperationLoggingDaoTest.dicon");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for OperationLoggingDaoTest.
     * @param arg0
     */
    public OperationLoggingDaoTest(String arg0) {
        super(arg0);
    }

    public void testInsertTx() {
        OperationLogging data = new OperationLogging();
        data.setOpeDate(TimestampUtil.parse("2005/06/28 17:00:00"));
        data.setOpeIp("192.168.0.123");
        data.setOpeUser("user1");
        data.setOpeBeforeValue("50");
        data.setOpeAfterValue("100");
        data.setOpeProvider("P1");
        data.setOpeHolder("H1");
        int r = dao.insert(data);
        assertEquals(1, r);
    }
}
