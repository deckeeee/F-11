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
package org.F11.scada.server.operationlog.impl;

import java.sql.Timestamp;

import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeDataCalendar;
import org.F11.scada.data.WifeDataDaySchedule;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeDataTimestamp;
import org.F11.scada.server.operationlog.OperationLoggingUtil;
import org.F11.scada.server.operationlog.dto.OperationLogging;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import junit.framework.TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingUtilImplTest extends TestCase {
    private OperationLoggingUtil util;

    private DataProvider dp;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        util = new OperationLoggingUtilImpl();
        dp = TestUtil.createDP();
        DataHolder analog = TestUtil.createAnalogHolder("ANALOG");
        dp.addDataHolder(analog);
        DataHolder analog4 = TestUtil.createAnalog4Holder("ANALOG4");
        dp.addDataHolder(analog4);
        DataHolder schedule = TestUtil.createScheduleHolder("SCHEDULE");
        dp.addDataHolder(schedule);
        DataHolder daySchedule = TestUtil.createDayScheduleHolder("DAYSCHEDULE");
        dp.addDataHolder(daySchedule);
        DataHolder calendar = TestUtil.createCalendarHolder("CALENDAR");
        dp.addDataHolder(calendar);
        DataHolder timestamp = TestUtil.createTimestampHolder("TIMESTAMP");
        dp.addDataHolder(timestamp);
        DataHolder digital = TestUtil.createDigitalHolder("DIGITAL");
        dp.addDataHolder(digital);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        TestUtil.crearJIM();
        super.tearDown();
    }

    /**
     * Constructor for OperationLoggingUtilTest.
     * 
     * @param arg0
     */
    public OperationLoggingUtilImplTest(String arg0) {
        super(arg0);
    }

    public void testAnalog() throws Exception {
        Timestamp t = TimestampUtil.parse("2005/07/01 13:00:00");
        OperationLogging log = util.getOperationLogging(dp
                .getDataHolder("ANALOG"),
                WifeDataAnalog.valueOfBcdSingle(2000), "user", "192.168.0.123",
                t);

        OperationLogging dest = new OperationLogging();
        dest.setId(0);
        dest.setOpeDate(t);
        dest.setOpeUser("user");
        dest.setOpeIp("192.168.0.123");
        dest.setOpeBeforeValue("0.0");
        dest.setOpeAfterValue("100.0");
        dest.setOpeProvider("P1");
        dest.setOpeHolder("ANALOG");
        
        assertEquals(log, dest);
    }

    public void testAnalog4() throws Exception {
        Timestamp t = TimestampUtil.parse("2005/07/01 13:00:00");
        OperationLogging log = util.getOperationLogging(dp
                .getDataHolder("ANALOG4"),
                WifeDataAnalog4.valueOfBcdSingle(new double[]{2000D, 2500D, 3000D, 3500D}), "user", "192.168.0.123",
                t);

        OperationLogging dest = new OperationLogging();
        dest.setId(0);
        dest.setOpeDate(t);
        dest.setOpeUser("user");
        dest.setOpeIp("192.168.0.123");
        dest.setOpeBeforeValue("0, 0, 100, 100");
        dest.setOpeAfterValue("2000, 2500, 3000, 3500");
        dest.setOpeProvider("P1");
        dest.setOpeHolder("ANALOG4");
        
        assertEquals(log, dest);
    }
    
    public void testSchedule() throws Exception {
        Timestamp t = TimestampUtil.parse("2005/07/01 13:00:00");
        WifeDataSchedule schedule = WifeDataSchedule.valueOf(1, 4, "Group");
        schedule.setOnTime(0, 0, 800);
        schedule.setOffTime(0, 0, 900);
        schedule.setOnTime(0, 3, 1200);
        schedule.setOffTime(0, 3, 1300);
        schedule.setOnTime(2, 1, 1000);
        schedule.setOffTime(2, 1, 1100);
        schedule.setOnTime(8, 0, 1000);
        schedule.setOffTime(8, 0, 1100);
        schedule.setOnTime(8, 1, 1001);
        schedule.setOffTime(8, 1, 1101);
        schedule.setOnTime(8, 2, 1002);
        schedule.setOffTime(8, 2, 1102);
        schedule.setOnTime(8, 3, 1003);
        schedule.setOffTime(8, 3, 1103);
        OperationLogging log = util.getOperationLogging(
                dp.getDataHolder("SCHEDULE"), schedule, "user", "192.168.0.123", t);

        OperationLogging dest = new OperationLogging();
        dest.setId(0);
        dest.setOpeDate(t);
        dest.setOpeUser("user");
        dest.setOpeIp("192.168.0.123");
        dest.setOpeBeforeValue("ç°ì˙{0âÒñ⁄=0000:0000, 3âÒñ⁄=0000:0000}, ì˙ój{1âÒñ⁄=0000:0000}, ìyój{0âÒñ⁄=0000:0000, 1âÒñ⁄=0000:0000, 2âÒñ⁄=0000:0000, 3âÒñ⁄=0000:0000}");
        dest.setOpeAfterValue("ç°ì˙{0âÒñ⁄=0800:0900, 3âÒñ⁄=1200:1300}, ì˙ój{1âÒñ⁄=1000:1100}, ìyój{0âÒñ⁄=1000:1100, 1âÒñ⁄=1001:1101, 2âÒñ⁄=1002:1102, 3âÒñ⁄=1003:1103}");
        dest.setOpeProvider("P1");
        dest.setOpeHolder("SCHEDULE");
        
        assertEquals(log, dest);
//        System.out.println(log);
    }
    
    public void testDaySchedule() throws Exception {
        WifeDataDaySchedule schedule = WifeDataDaySchedule.valueOf(4);
        schedule.setOnTime(0, 1000);
        schedule.setOffTime(0, 1010);
        schedule.setOnTime(2, 1040);
        schedule.setOffTime(2, 1050);
        schedule.setOnTime(3, 1100);
        schedule.setOffTime(3, 1110);
  
        Timestamp t = TimestampUtil.parse("2005/07/01 13:00:00");
        OperationLogging log = util.getOperationLogging(
                dp.getDataHolder("DAYSCHEDULE"), schedule, "user", "192.168.0.123", t);
        
        OperationLogging dest = new OperationLogging();
        dest.setId(0);
        dest.setOpeDate(t);
        dest.setOpeUser("user");
        dest.setOpeIp("192.168.0.123");
        dest.setOpeBeforeValue("{0âÒñ⁄=0000:0000, 2âÒñ⁄=0000:0000, 3âÒñ⁄=0000:0000}");
        dest.setOpeAfterValue("{0âÒñ⁄=1000:1010, 2âÒñ⁄=1040:1050, 3âÒñ⁄=1100:1110}");
        dest.setOpeProvider("P1");
        dest.setOpeHolder("DAYSCHEDULE");
        
        assertEquals(log, dest);
//        System.out.println(log);
    }
    
    public void testCalendar() throws Exception {
        WifeDataCalendar calendar = WifeDataCalendar.valueOf(6);
        calendar.setBit(0, 0, 0);
        calendar.setBit(0, 0, 1);
        calendar.setBit(1, 8, 30);
        calendar.setBit(5, 11, 30);
        
        Timestamp t = TimestampUtil.parse("2005/07/01 13:00:00");
        OperationLogging log = util.getOperationLogging(
                dp.getDataHolder("CALENDAR"), calendar, "user", "192.168.0.123", t);

        OperationLogging dest = new OperationLogging();
        dest.setId(0);
        dest.setOpeDate(t);
        dest.setOpeUser("user");
        dest.setOpeIp("192.168.0.123");
        dest.setOpeBeforeValue("ãxì˙{1/1=false, 1/2=false}, ì¡éÍì˙1{9/31=false}, ì¡éÍì˙5{12/31=false}");
        dest.setOpeAfterValue("ãxì˙{1/1=true, 1/2=true}, ì¡éÍì˙1{9/31=true}, ì¡éÍì˙5{12/31=true}");
        dest.setOpeProvider("P1");
        dest.setOpeHolder("CALENDAR");

        assertEquals(log, dest);
//        System.out.println(log);
    }
    
    public void testTimestamp() throws Exception {
        Timestamp t = TimestampUtil.parse("2005/07/01 14:00:00");
        WifeDataTimestamp timestamp = WifeDataTimestamp.valueOfType1(t.getTime());
        OperationLogging log = util.getOperationLogging(
                dp.getDataHolder("TIMESTAMP"), timestamp, "user", "192.168.0.123", t);

        OperationLogging dest = new OperationLogging();
        dest.setId(0);
        dest.setOpeDate(t);
        dest.setOpeUser("user");
        dest.setOpeIp("192.168.0.123");
        dest.setOpeBeforeValue("{2005/07/01 ã‡ 13:00:00.000}");
        dest.setOpeAfterValue("{2005/07/01 ã‡ 14:00:00.000}");
        dest.setOpeProvider("P1");
        dest.setOpeHolder("TIMESTAMP");

        assertEquals(log, dest);
    }
    
    public void testDigital() throws Exception {
        WifeDataDigital digital = WifeDataDigital.valueOfFalse(0);
        Timestamp t = TimestampUtil.parse("2005/07/01 14:00:00");
        OperationLogging log = util.getOperationLogging(
                dp.getDataHolder("DIGITAL"), digital, "user", "192.168.0.123", t);

        OperationLogging dest = new OperationLogging();
        dest.setId(0);
        dest.setOpeDate(t);
        dest.setOpeUser("user");
        dest.setOpeIp("192.168.0.123");
        dest.setOpeBeforeValue("true");
        dest.setOpeAfterValue("false");
        dest.setOpeProvider("P1");
        dest.setOpeHolder("DIGITAL");

        assertEquals(log, dest);
    }
    
}