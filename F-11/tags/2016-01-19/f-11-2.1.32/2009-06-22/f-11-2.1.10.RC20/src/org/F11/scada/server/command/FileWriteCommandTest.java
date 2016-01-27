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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;

import junit.framework.TestCase;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.commons.beanutils.BeanUtils;

/**
 * FileWriteCommandのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FileWriteCommandTest extends TestCase {
	private static final String TEST_FILE = "FileWriteCommand.txt";

	/**
	 * Constructor for FileWriteCommandTest.
	 * @param arg0
	 */
	public FileWriteCommandTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
	}

	public void testDataValueChanged() throws Exception {
		Class clazz = Class.forName("org.F11.scada.server.command.FileWriteCommand");
		Command cmd = (Command) clazz.newInstance();
		try {
			cmd.execute(null);
			fail();
		} catch (IllegalStateException e) {
		}
		
		CommandConfig cfg = new CommandConfig();
		cfg.setHolder("P1");
		cfg.setProvider("D_1900000_Digital");
		ClassConfig ccfg = new ClassConfig();
		ccfg.setClassName("org.F11.scada.server.command.FileWriteCommand");
		ccfg.addProperty("path", TEST_FILE);
		cfg.addClassConfig(ccfg);

		BeanUtils.populate(cmd, ccfg.getProperties());
		cmd.execute(
			new DataValueChangeEventKey(
				0,
				"P1",
				"D_1900000_Digital",
				Boolean.FALSE,
				new Timestamp(System.currentTimeMillis())));
		File file = new File(TEST_FILE);
		sleep(1000L);
		assertTrue(file.exists());

		BufferedReader in = null;		
		try {
			in = new BufferedReader(new FileReader(TEST_FILE));
			assertEquals("0", in.readLine());
		} finally {
			if (in != null) {
				in.close();
			}
		}

		cmd.execute(
			new DataValueChangeEventKey(
				0,
				"P1",
				"D_1900000_Digital",
				Boolean.TRUE,
				new Timestamp(System.currentTimeMillis())));
		sleep(1000L);
		assertTrue(file.exists());

		try {
			in = new BufferedReader(new FileReader(TEST_FILE));
			assertEquals("1", in.readLine());
		} finally {
			if (in != null) {
				in.close();
			}
		}

		file.delete();
	}

	private void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {}
	}
}
