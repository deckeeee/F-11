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

package org.F11.scada.server.command;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * CommandProviderのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CommandProviderTest extends TestCase {
	DataProvider dp;
	DataHolder dh1;
	DataHolder dh2;

	/**
	 * Constructor for CommandProviderTest.
	 * @param arg0
	 */
	public CommandProviderTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		dp = TestUtil.createDP();
		dh1 = TestUtil.createDigitalHolder("H1");
		dh1.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(0));
		dp.addDataHolder(dh1);
		dh2 = TestUtil.createDigitalHolder("H2");
		dh2.setParameter(WifeDataProvider.PARA_NAME_POINT, new Integer(1));
		dp.addDataHolder(dh2);
		Manager.getInstance().addDataProvider(dp);
	}

	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
	}

	public void testCommandProvider() throws Exception {
		CommandProvider cp = new CommandProvider("command.xml");
		Map cm = cp.getCommands();
		assertNotNull(cm);
		List cmds = (List) cm.get("P1_D_1900000_Digital");
		Command cmd = (Command) cmds.get(0);
		assertNotNull(cmd);
		assertTrue(cmd instanceof FileWriteCommand);

		cmds = (List) cm.get("P1_D_1900001_Digital");
		cmd = (Command) cmds.get(0);
		assertNotNull(cmd);
		assertTrue(cmd instanceof TestCommandClass);
		TestCommandClass tc = (TestCommandClass) cmd;
		assertEquals("value", tc.getKey());
		assertEquals("name", tc.getName());
		cmd.execute(null);
		cmd = (Command) cmds.get(1);
		assertNotNull(cmd);
		assertTrue(cmd instanceof TestCommandClass);
		tc = (TestCommandClass) cmd;
		assertEquals("value2", tc.getKey());
		assertEquals("name2", tc.getName());
		cmd.execute(null);

		cm = cp.getExpressionCommands();
		Set entrySet = cm.entrySet();
		Iterator i = entrySet.iterator();
		assertTrue(i.hasNext());
		Map.Entry entry = (Map.Entry) i.next();
		cmds = (List) entry.getValue();
		assertNotNull(cmds);
		cmd = (Command) cmds.get(0);
		assertNotNull(cmd);
		assertTrue(cmd instanceof WriteTermCommand);
	}

	public void testValueChange() throws Exception {
		CommandProvider cp = new CommandProvider("command2.xml");
		Map cm = cp.getCommands();
		dh1.setValue(WifeDataDigital.valueOfTrue(0), new Date(), WifeQualityFlag.GOOD);
		List l = (List) cm.get("P1_H1");
		TestCommand tc = (TestCommand) l.get(0);
		assertTrue(tc.value);
		dh1.setValue(WifeDataDigital.valueOfFalse(0), new Date(), WifeQualityFlag.GOOD);
		l = (List) cm.get("P1_H1");
		tc = (TestCommand) l.get(0);
		assertFalse(tc.value);
		
		cm = cp.getExpressionCommands();
		dh2.setValue(WifeDataDigital.valueOfTrue(0), new Date(), WifeQualityFlag.GOOD);
		l = (List) cm.values().iterator().next();
		tc = (TestCommand) l.get(0);
		assertTrue(tc.value);
		dh2.setValue(WifeDataDigital.valueOfFalse(0), new Date(), WifeQualityFlag.GOOD);
		l = (List) cm.values().iterator().next();
		tc = (TestCommand) l.get(0);
		assertFalse(tc.value);
	}
}
