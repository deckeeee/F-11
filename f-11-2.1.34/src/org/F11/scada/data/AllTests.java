/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/AllTests.java,v 1.6.6.1 2007/01/17 05:02:25 frdm Exp $
 * $Revision: 1.6.6.1 $
 * $Date: 2007/01/17 05:02:25 $
 * 
 * =============================================================================
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
 *
 */
package org.F11.scada.data;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.F11.scada.data");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(ConvertValueTest.class));
		suite.addTest(new TestSuite(CreateHolderDataTest.class));
		suite.addTest(new TestSuite(DefaultSchedulePatternTest.class));
		suite.addTest(new TestSuite(HolderDataTest.class));
		suite.addTest(new TestSuite(OnOffTimeTest.class));
		suite.addTest(new TestSuite(WifeBCDTest.class));
		suite.addTest(WifeDataAnalogTest.suite());
		suite.addTest(new TestSuite(WifeDataCalendarTest.class));
		suite.addTest(new TestSuite(WifeDataDayScheduleTest.class));
		suite.addTest(new TestSuite(WifeDataDigitalTest.class));
		suite.addTest(new TestSuite(WifeDataScheduleTest.class));
		suite.addTest(new TestSuite(WifeQualityFlagTest.class));
		suite.addTest(new TestSuite(WifeDataTimestampTest.class));
		suite.addTest(new TestSuite(StringDataTest.class));
		//$JUnit-END$
		return suite;
	}
}
