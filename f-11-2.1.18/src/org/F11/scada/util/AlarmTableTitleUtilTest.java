package org.F11.scada.util;

import junit.framework.TestCase;

public class AlarmTableTitleUtilTest extends TestCase {

	public void testRepraceStrings() {
		AlarmTableTitleUtil util = new AlarmTableTitleUtil("Œš•¨–¼, İ”õ–¼, ‘®«3, 4");
		String s = util.repraceStrings("‘®«1, ‘®«2, ‘®«3");
		assertEquals(s, "Œš•¨–¼, İ”õ–¼, ‘®«3");
	}

	public void testGetAtrributeString() throws Exception {
		AlarmTableTitleUtil util = new AlarmTableTitleUtil("Œš•¨–¼, İ”õ–¼, ‘®«3, 4");
		assertEquals(util.getAttributeString("‘®«1"), "Œš•¨–¼");
	}
}
