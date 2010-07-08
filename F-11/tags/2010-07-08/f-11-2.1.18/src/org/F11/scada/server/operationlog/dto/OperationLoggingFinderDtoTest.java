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

import junit.framework.TestCase;

import org.F11.scada.test.util.TimestampUtil;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingFinderDtoTest extends TestCase {

    /**
     * Constructor for OperationLoggingFinderDtoTest.
     * @param arg0
     */
    public OperationLoggingFinderDtoTest(String arg0) {
        super(arg0);
    }

    public void testOperationLoggingFinderDto() throws Exception {
        OperationLoggingFinderDto dto = new OperationLoggingFinderDto();
        dto.setId(5);
        dto.setOpeDate(TimestampUtil.parse("2005/07/01 13:00:00"));
        dto.setOpeIp("192.168.0.1");
        dto.setOpeUser("user");
        dto.setOpeBeforeValue("100");
        dto.setOpeAfterValue("500");
        dto.setOpeProvider("P1");
        dto.setOpeHolder("H1");
        dto.setUnit("AHU-1-1");
        dto.setName("1F 空調機");
        dto.setMessage("上々限");
        
        OperationLoggingFinderDto dto2 = new OperationLoggingFinderDto();
        dto2.setId(5);
        dto2.setOpeDate(TimestampUtil.parse("2005/07/01 13:00:00"));
        dto2.setOpeIp("192.168.0.1");
        dto2.setOpeUser("user");
        dto2.setOpeBeforeValue("100");
        dto2.setOpeAfterValue("500");
        dto2.setOpeProvider("P1");
        dto2.setOpeHolder("H1");
        dto2.setUnit("AHU-1-1");
        dto2.setName("1F 空調機");
        dto2.setMessage("上々限");
        
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
        
        dto2.setId(1);
        assertFalse(dto.equals(dto2));
        assertFalse(dto.hashCode() == dto2.hashCode());
    }
}
