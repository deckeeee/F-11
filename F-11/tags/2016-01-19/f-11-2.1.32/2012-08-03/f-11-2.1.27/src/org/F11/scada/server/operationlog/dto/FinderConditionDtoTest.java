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
public class FinderConditionDtoTest extends TestCase {

    /**
     * Constructor for FinderConditionDtoTest.
     * @param arg0
     */
    public FinderConditionDtoTest(String arg0) {
        super(arg0);
    }

    public void testFinderConditionDto() throws Exception {
        FinderConditionDto dto = new FinderConditionDto();
        dto.setStartDate(TimestampUtil.parse("2005/01/01 00:00:00"));
        dto.setEndDate(TimestampUtil.parse("2005/12/31 23:59:59"));
        dto.setOpeIp("192.168.0.0");
        dto.setOpeUser("hoge");
        dto.setOpeName("UNIT-0-1");
        
        FinderConditionDto dto2 = (FinderConditionDto) dto.clone();
        
        assertTrue(dto != dto2);
        assertEquals("%UNIT-0-1%", dto.getOpeName());
    }

    public void testConditionNullOpeName() throws Exception {
        FinderConditionDto dto = new FinderConditionDto();
        dto.setStartDate(TimestampUtil.parse("2005/01/01 00:00:00"));
        dto.setEndDate(TimestampUtil.parse("2005/12/31 23:59:59"));
        dto.setOpeIp("192.168.0.0");
        dto.setOpeUser("hoge");
        
        FinderConditionDto dto2 = (FinderConditionDto) dto.clone();
        
        assertTrue(dto != dto2);
        assertNull(dto.getOpeName());
    }
}
