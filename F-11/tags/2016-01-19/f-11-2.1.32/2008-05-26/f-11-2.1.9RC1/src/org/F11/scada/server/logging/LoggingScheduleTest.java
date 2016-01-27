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
package org.F11.scada.server.logging;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import junit.framework.TestCase;

import org.F11.scada.test.util.TestUtil;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class LoggingScheduleTest extends TestCase {

    /**
     * Constructor for LoggingScheduleTest.
     * @param arg0
     */
    public LoggingScheduleTest(String arg0) {
        super(arg0);
    }
    
    public void testMinuteSchedule() throws Exception {
        LoggingSchedule schedule = LoggingSchedule.MINUTE;
        TestTimerTask task = new TestTimerTask();
        schedule.add(task);
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.MINUTE, 1);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		TestUtil.sleep(60000L);
        assertEquals(2, task.getExecuteCount());
		assertEquals(cal.getTime().toString(), task.getExecuteDate().toString());
    }

    public void testOneMinuteSchedule() throws Exception {
        LoggingSchedule schedule = LoggingSchedule.ONEMINUTE;
        TestTimerTask task = new TestTimerTask();
        schedule.add(task);
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.MINUTE, 1);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		TestUtil.sleep(60000L);
        assertEquals(1, task.getExecuteCount());
		assertEquals(cal.getTime().toString(), task.getExecuteDate().toString());
    }

    static class TestTimerTask extends TimerTask {
        private int executeCount;
        private Date executeDate;
        
        public int getExecuteCount() {
            return executeCount;
        }
        
        public Date getExecuteDate() {
            return executeDate;
        }

        public void run() {
            executeCount++;
            executeDate = new Date();
        }
    }
}
