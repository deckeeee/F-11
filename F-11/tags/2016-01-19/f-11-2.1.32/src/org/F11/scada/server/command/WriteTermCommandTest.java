/*
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

package org.F11.scada.server.command;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.test.util.TimestampUtil;

public class WriteTermCommandTest extends TestCase {
	private WriteTermCommand command;
	private DataValueChangeEventKey evt;
	private DataProvider dp;

	protected void setUp() throws Exception {
		dp = TestUtil.createDP();
		dp.addDataHolder(TestUtil.createDigitalHolder("H1_Digital"));
		dp.addDataHolder(TestUtil.createAnalogHolder("H1_Analog"));
		Manager.getInstance().addDataProvider(dp);
		assertNotNull(Manager.getInstance().findDataHolder("P1", "H1_Digital"));
		assertNotNull(Manager.getInstance().findDataHolder("P1", "H1_Analog"));
		command = new WriteTermCommand();
		evt = new DataValueChangeEventKey(
				0, "P1", "H1", Boolean.TRUE, TimestampUtil.parse("2006/01/01 00:00:00"));
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	/*
	 * Test method for 'org.F11.scada.server.command.WriteTermCommand.execute(DataValueChangeEventKey)'
	 */
	public void testExecuteError() throws Exception {
		try {
			command.execute(evt);
			fail();
			command.setProvider("P1");
			command.execute(evt);
			fail();
			command.setHolder("H1");
			command.execute(evt);
			fail();
			command.setProvider(null);
			command.setValue("0");
			command.execute(evt);
			fail();
		} catch (IllegalStateException e) {}
	}
	
	public void testExecuteAnalog() throws Exception {
		command.setProvider("P1");
		command.setHolder("H1_Analog");
		command.setValue("2000");
		command.execute(evt);
		TestUtil.sleep(1000L);
		DataHolder dh = dp.getDataHolder("H1_Analog");
		WifeDataAnalog data = (WifeDataAnalog) dh.getValue();
		assertEquals(WifeDataAnalog.valueOfBcdSingle(100.0), data);
	}
	
	public void testExecuteDigital() throws Exception {
		command.setProvider("P1");
		command.setHolder("H1_Digital");
		command.setValue("true");
		command.execute(evt);
		TestUtil.sleep(1000L);
		DataHolder dh = dp.getDataHolder("H1_Digital");
		WifeDataDigital data = (WifeDataDigital) dh.getValue();
		assertEquals(WifeDataDigital.valueOf(true, 0), data);
	}
}
