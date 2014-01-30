package org.F11.scada.scheduling;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;

public class WeeklyIteratorTest extends TestCase {

	public void testNext() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		fmt.setLenient(false);

		WeeklyIterator i = new WeeklyIterator(0, 1, 0, fmt.parse("2012/05/25 00:00:00"));
		assertEquals(fmt.parse("2012/05/28 00:01:00"), i.next());
		assertEquals(fmt.parse("2012/06/04 00:01:00"), i.next());
		assertEquals(fmt.parse("2012/06/11 00:01:00"), i.next());
	}
}
