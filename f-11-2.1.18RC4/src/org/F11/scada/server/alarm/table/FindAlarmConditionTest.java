/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.alarm.table;

import java.util.Arrays;

import junit.framework.TestCase;

public class FindAlarmConditionTest extends TestCase {

	public FindAlarmConditionTest(String arg0) {
		super(arg0);
	}

	/*
	 * Test method for 'org.F11.scada.server.alarm.table.FindAlarmCondition.getAttributes()'
	 */
	public void testGetAttributes() {
		assertTrue("0 2‚ª•Ô‚³‚ê‚é", Arrays.equals(new int[]{0, 2}, FindAlarmCondition.getAttributes()));
	}

}
