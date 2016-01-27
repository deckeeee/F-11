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

package org.F11.scada.server.timeset;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.test.util.TestUtil;

public class LifeCheckTaskTest extends TestCase {
	private DataProvider dp;
	private DataHolder holder;

	protected void setUp() throws Exception {
		dp = TestUtil.createDP();
		holder = TestUtil.createDigitalHolder("H1", false);
		dp.addDataHolder(holder);
		Manager.getInstance().addDataProvider(dp);
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	/*
	 * Test method for 'org.F11.scada.server.timeset.LifeCheckTask.run()'
	 */
	public void testRun() {
		LifeCheckTask task = new LifeCheckTask();
		task.addWriteProvidertHolder("P1_H1");
		task.run();
		WifeDataDigital digital = (WifeDataDigital) holder.getValue();
		assertTrue(digital.isOnOff(true));
	}
}
