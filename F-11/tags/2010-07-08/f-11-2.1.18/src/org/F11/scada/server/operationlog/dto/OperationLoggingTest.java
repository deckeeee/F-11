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
package org.F11.scada.server.operationlog.dto;

import org.F11.scada.test.util.TimestampUtil;

import junit.framework.TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingTest extends TestCase {

    /**
     * Constructor for OperationLoggingTest.
     * @param arg0
     */
    public OperationLoggingTest(String arg0) {
        super(arg0);
    }

    public void testOperationLogging() throws Exception {
        OperationLogging log = new OperationLogging();
        log.setId(5);
        log.setOpeDate(TimestampUtil.parse("2005/07/01 13:00:00"));
        log.setOpeIp("192.168.0.1");
        log.setOpeUser("user");
        log.setOpeBeforeValue("100");
        log.setOpeAfterValue("500");
        log.setOpeProvider("P1");
        log.setOpeHolder("H1");
        
        OperationLogging log2 = new OperationLogging();
        log2.setId(5);
        log2.setOpeDate(TimestampUtil.parse("2005/07/01 13:00:00"));
        log2.setOpeIp("192.168.0.1");
        log2.setOpeUser("user");
        log2.setOpeBeforeValue("100");
        log2.setOpeAfterValue("500");
        log2.setOpeProvider("P1");
        log2.setOpeHolder("H1");
        
        assertEquals(log, log2);
        assertEquals(log.hashCode(), log2.hashCode());
        
        log2.setId(1);
        assertFalse(log.equals(log2));
        assertFalse(log.hashCode() == log2.hashCode());
    }
}
