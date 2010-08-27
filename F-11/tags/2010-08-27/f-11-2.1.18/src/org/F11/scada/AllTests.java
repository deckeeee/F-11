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
 *
 */
package org.F11.scada;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AllTests {

	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite("Test for org.F11.scada.AllTest");
		// $JUnit-BEGIN$
		suite.addTest(org.F11.scada.applet.AllTests.suite());
		suite.addTest(org.F11.scada.applet.expression.AllTests.suite());
		suite.addTest(org.F11.scada.applet.expression.text.AllTests.suite());
		suite.addTest(org.F11.scada.applet.graph.AllTests.suite());
		suite.addTest(org.F11.scada.applet.graph.bargraph.AllTests.suite());
		suite.addTest(org.F11.scada.applet.symbol.table.AllTests.suite());
		suite.addTest(org.F11.scada.data.AllTests.suite());
		suite.addTest(org.F11.scada.parser.AllTests.suite());
		suite.addTest(org.F11.scada.parser.alarm.AllTests.suite());
		suite.addTest(org.F11.scada.scheduling.AllTests.suite());
		suite.addTest(org.F11.scada.security.AllTests.suite());
		suite.addTest(org.F11.scada.security.auth.AllTests.suite());
		suite.addTest(org.F11.scada.server.alarm.AllTests.suite());
		suite.addTest(org.F11.scada.server.alarm.print.AllTests.suite());
		suite.addTest(org.F11.scada.server.alarm.table.AllTests.suite());
		suite.addTest(org.F11.scada.server.alarm.table.postgresql.AllTests
				.suite());
		suite.addTest(org.F11.scada.server.autoprint.jasper.AllTests.suite());
		suite.addTest(org.F11.scada.server.command.AllTests.suite());
		suite.addTest(org.F11.scada.server.communicater.AllTests.suite());
		suite.addTest(org.F11.scada.server.converter.AllTests.suite());
		suite.addTest(org.F11.scada.server.deploy.AllTests.suite());
		suite.addTest(org.F11.scada.server.event.AllTests.suite());
		suite.addTest(org.F11.scada.server.io.postgresql.AllTests.suite());
		suite.addTest(org.F11.scada.server.logging.AllTests.suite());
		suite.addTest(org.F11.scada.server.logging.column.AllTests.suite());
		suite.addTest(org.F11.scada.server.logging.report.AllTests.suite());
		suite.addTest(org.F11.scada.server.output.dto.AllTests.suite());
		suite.addTest(org.F11.scada.server.output.impl.AllTests.suite());
		suite.addTest(org.F11.scada.server.register.AllTests.suite());
		suite.addTest(org.F11.scada.server.register.impl.AllTests.suite());
//		suite.addTest(org.F11.scada.server.wifi.AllTests.suite());
//		suite.addTest(org.F11.scada.server.wifi.impl.AllTests.suite());
		suite.addTest(org.F11.scada.util.AllTests.suite());
		suite.addTest(org.F11.scada.xwife.applet.AllTests.suite());
		suite.addTest(org.F11.scada.xwife.server.io.AllTests.suite());
		suite.addTest(org.F11.scada.server.operationlog.AllTests.suite());
		suite.addTest(org.F11.scada.server.comment.AllTests.suite());
		suite.addTest(org.F11.scada.server.alarm.mail.dao.AllTests.suite());
		suite.addTest(org.F11.scada.applet.operationlog.AllTests.suite());
		suite.addTest(org.F11.scada.xwife.server.impl.AllTests.suite());
		suite.addTest(org.F11.scada.server.io.nio.AllTests.suite());
		suite.addTest(org.F11.scada.server.io.nio.impl.AllTests.suite());
		suite.addTest(org.F11.scada.server.autoprint.AllTests.suite());
		suite.addTest(org.F11.scada.server.io.AllTests.suite());
		suite.addTest(org.F11.scada.server.frame.AllTests.suite());
		suite.addTest(org.F11.scada.xwife.applet.alarm.event.AllTests.suite());
		suite.addTest(org.F11.scada.xwife.applet.alarm.AllTests.suite());
		suite.addTest(org.F11.scada.server.goda.impl.AllTests.suite());
		suite.addTest(org.F11.scada.applet.schedule.point.AllTests.suite());
		suite.addTest(org.F11.scada.server.schedule.point.dao.AllTests.suite());
		suite.addTest(org.F11.scada.server.schedule.point.dto.AllTests.suite());

		// $JUnit-END$
		return suite;
	}
}
