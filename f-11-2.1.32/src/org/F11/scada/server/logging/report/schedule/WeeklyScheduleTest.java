package org.F11.scada.server.logging.report.schedule;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;

public class WeeklyScheduleTest extends TestCase {

	public void testStartTime() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fmt.setLenient(false);

		WeeklySchedule s = new WeeklySchedule();
		assertEquals(
			fmt.parse("2012/05/21 01:01:00"),
			s.startTime(fmt.parse("2012/05/28 00:01:00").getTime(), false));
		assertEquals(
			fmt.parse("2012/05/28 01:01:00"),
			s.startTime(fmt.parse("2012/06/03 00:01:00").getTime(), false));
	}

	public void testEndTime() throws Exception{
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fmt.setLenient(false);

		WeeklySchedule s = new WeeklySchedule();
		assertEquals(
			fmt.parse("2012/05/28 01:01:00"),
			s.endTime(fmt.parse("2012/05/28 00:01:00").getTime(), false));
		assertEquals(
			fmt.parse("2012/06/04 01:01:00"),
			s.endTime(fmt.parse("2012/06/04 00:01:00").getTime(), false));
	}

}
