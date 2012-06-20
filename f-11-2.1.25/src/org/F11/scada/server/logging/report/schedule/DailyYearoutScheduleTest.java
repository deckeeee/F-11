package org.F11.scada.server.logging.report.schedule;

import junit.framework.TestCase;

import org.F11.scada.test.util.TimestampUtil;

public class DailyYearoutScheduleTest extends TestCase {

	public void testStartTime() {
		DailyYearoutSchedule s = new DailyYearoutSchedule();
		assertEquals(s.startTime(TimestampUtil
			.parse("2012/05/29 00:01:00")
			.getTime(), true), TimestampUtil.parse("2012/01/01 00:00:00"));
	}

	public void testEndTime() {
		DailyYearoutSchedule s = new DailyYearoutSchedule();
		assertEquals(s.endTime(TimestampUtil
			.parse("2012/05/29 00:01:00")
			.getTime(), true), TimestampUtil.parse("2013/01/01 00:00:00"));
	}

}
