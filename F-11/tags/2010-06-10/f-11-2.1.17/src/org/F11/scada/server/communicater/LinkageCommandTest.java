/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.server.communicater;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.converter.Converter;
import org.F11.scada.server.event.WifeCommand;
import org.apache.log4j.Logger;

/**
 * @author user To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class LinkageCommandTest extends TestCase {
	private static Logger log = Logger.getLogger(LinkageCommandTest.class);

	// テスト対象
	private LinkageCommand lcom;

	private WifeCommand wc1, wc2, wc3, wc4, wc5, wc6, wc7;

	/**
	 * Constructor for LinkageCommandTest.
	 * @param arg0
	 */
	public LinkageCommandTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		lcom = new LinkageCommand(new Conv());
		wc1 = new WifeCommand("P1", 1000, 0, 0, 2001, 1);
		wc2 = new WifeCommand("P1", 1000, 0, 0, 2050, 1);
		wc3 = new WifeCommand("P1", 1000, 0, 0, 3000, 2);
		wc4 = new WifeCommand("P1", 1000, 0, 0, 3900, 1);
		wc5 = new WifeCommand("P1", 1000, 0, 0, 5000, 2);
		wc6 = new WifeCommand("P1", 1000, 0, 0, 5002, 1);
		wc7 = new WifeCommand("P1", 1000, 0, 0, 5003, 1);
		lcom.addDefine(wc1);
		lcom.addDefine(wc2);
		lcom.addDefine(wc3);
		lcom.addDefine(wc4);
		lcom.addDefine(wc5);
		lcom.addDefine(wc6);
		lcom.addDefine(wc7);
		assertEquals(4, lcom.lk_commands.size());
	}

	public void testGetDefines() {
		List<WifeCommand> wcs = new ArrayList<WifeCommand>();
		wcs.add(wc1);
		wcs.add(wc3);
		wcs.add(wc4);
		Set<WifeCommand> result = new TreeSet<WifeCommand>(WifeCommand.comp);
		result.addAll(lcom.getDefines(wcs));

		assertEquals(3, result.size());
		Iterator<WifeCommand> it = result.iterator();
		WifeCommand wc = it.next();
		assertEquals(2001, wc.getMemoryAddress());
		assertEquals(50, wc.getWordLength());
		wc = it.next();
		assertEquals(3000, wc.getMemoryAddress());
		assertEquals(2, wc.getWordLength());
		wc = it.next();
		assertEquals(3900, wc.getMemoryAddress());
		assertEquals(1, wc.getWordLength());

		wcs.clear();
		wcs.add(wc6);
		result.clear();
		result.addAll(lcom.getDefines(wcs));

		assertEquals(1, result.size());
		it = result.iterator();
		wc = it.next();
		assertEquals(5000, wc.getMemoryAddress());
		assertEquals(4, wc.getWordLength());
	}

	public void testGetDefines2() {
		// プールされていないdefine
		WifeCommand wc = new WifeCommand("P1", 1000, 0, 0, 2900, 1);
		List<WifeCommand> wcs = new ArrayList<WifeCommand>();
		wcs.add(wc);
		try {
			lcom.getDefines(wcs);
			fail();
		} catch (IllegalArgumentException e) {
		}

		wc = new WifeCommand("P1", 1000, 0, 0, 5004, 1);
		wcs.clear();
		wcs.add(wc);
		try {
			lcom.getDefines(wcs);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testUpdateDefine() {
		// プールされているdefineの一つでeventを作成
		List<WifeCommand> wcs = new ArrayList<WifeCommand>();
		wcs.add(wc3);
		wcs.add(wc4);
		List<WifeCommand> result = new ArrayList<WifeCommand>(
				lcom.getDefines(wcs));
		assertEquals(2, result.size());
		WifeCommand wc = (WifeCommand) result.get(0);
		ByteBuffer readData = ByteBuffer.allocate(2048);
		readData.put(WifeUtilities.toByteArray("001122334455"));
		// 初回
		readData.flip();
		assertTrue(lcom.updateDefine(wc, readData));
		// データ変化なし
		readData.flip();
		assertFalse(lcom.updateDefine(wc, readData));
		readData.flip();
		assertFalse(lcom.updateDefine(wc, readData));
		// データ変化あり
		readData.clear();
		readData.put(WifeUtilities.toByteArray("501122336677"));
		readData.flip();
		assertTrue(lcom.updateDefine(wc, readData));
		readData.flip();
		assertFalse(lcom.updateDefine(wc, readData));
	}

	public void testGetHolderCommands() {
		Set<WifeCommand> result = new TreeSet<WifeCommand>(WifeCommand.comp);
		result.addAll(lcom.getHolderCommands(wc1));
		assertEquals(2, result.size());
		Iterator<WifeCommand> it = result.iterator();
		assertSame(wc1, it.next());
		assertSame(wc2, it.next());

		result.clear();
		result.addAll(lcom.getHolderCommands(wc3));
		assertEquals(1, result.size());
		it = result.iterator();
		assertSame(wc3, it.next());

		result.clear();
		result.addAll(lcom.getHolderCommands(wc4));
		assertEquals(1, result.size());
		it = result.iterator();
		assertSame(wc4, it.next());

		result.clear();
		result.addAll(lcom.getHolderCommands(wc7));
		assertEquals(3, result.size());
		it = result.iterator();
		assertSame(wc5, it.next());
		assertSame(wc6, it.next());
		assertSame(wc7, it.next());

		WifeCommand wc = new WifeCommand("P1", 1000, 0, 0, 3900, 2);
		lcom.addDefine(wc);
		result.clear();
		result.addAll(lcom.getHolderCommands(wc));
		assertEquals(2, result.size());
		it = result.iterator();
		assertSame(wc4, it.next());
		assertSame(wc, it.next());

		wc = new WifeCommand("P1", 1000, 0, 0, 5001, 1);
		lcom.addDefine(wc);
		result.clear();
		result.addAll(lcom.getHolderCommands(wc));
		assertEquals(4, result.size());
		it = result.iterator();
		assertSame(wc5, it.next());
		assertSame(wc, it.next());
		assertSame(wc6, it.next());
		assertSame(wc7, it.next());
	}

	public void testTime1() {
		assertEquals(4, lcom.lk_commands.size());
		List<WifeCommand> req = new ArrayList<WifeCommand>();
		long start = System.currentTimeMillis();
		for (long i = 0; i < 10000L; i++) {
			WifeCommand wc = new WifeCommand("P1", 0, 0, (int) (i % 10), i, 1);
			req.add(wc);
			lcom.addDefine(wc);
		}
		System.out.print(System.currentTimeMillis() - start);
		assertEquals(121, lcom.lk_commands.size());
		start = System.currentTimeMillis();
		assertEquals(121, lcom.getDefines(req).size());
		System.out.print("   ");
		System.out.println(System.currentTimeMillis() - start);
	}

	public void testTime10() {
		assertEquals(4, lcom.lk_commands.size());
		List<WifeCommand> req = new ArrayList<WifeCommand>();
		long start = System.currentTimeMillis();
		for (long i = 0; i < 100000L; i++) {
			WifeCommand wc = new WifeCommand("P1", 0, 0, (int) (i % 10), i, 1);
			// if (50000 < i)
			req.add(wc);
			lcom.addDefine(wc);
		}
		System.out.print(System.currentTimeMillis() - start);
		assertEquals(1121, lcom.lk_commands.size());
		start = System.currentTimeMillis();
		assertEquals(1121, lcom.getDefines(req).size());
		System.out.print("   ");
		System.out.println(System.currentTimeMillis() - start);
	}

	public void testSchedule() throws Exception {
		log.info("start");
		assertEquals(4, lcom.lk_commands.size());
		List<WifeCommand> req = new ArrayList<WifeCommand>();
		for (long i = 14000L, count = 0L; count < 64L; i += 112, count++) {
			WifeCommand wc = new WifeCommand("P1", 0, 0, (int) (i % 10), i, 112);
			req.add(wc);
			lcom.addDefine(wc);
		}
		// assertEquals(1121, lcom.lk_commands.size());
		// assertEquals(1121, lcom.getDefines(req).size());
		log.info("end");
	}

	private static class Conv implements Converter {
		public Conv() {
		}

		// @see
		// org.F11.scada.server.converter.Converter#checkCommandResponce(java.nio.ByteBuffer)
		public WifeException checkCommandResponce(ByteBuffer recvBuffer)
				throws WifeException {
			return null;
		}

		// @see
		// org.F11.scada.server.converter.Converter#getPacketMaxSize(org.F11.scada.server.event.WifeCommand)
		public int getPacketMaxSize(WifeCommand commdef) {
			return 900;
		}

		// @see
		// org.F11.scada.server.converter.Converter#getResponceData(java.nio.ByteBuffer,
		// java.nio.ByteBuffer)
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {

		}

		// @see org.F11.scada.server.converter.Converter#hasCommand()
		public boolean hasCommand() {
			return false;
		}

		// @see
		// org.F11.scada.server.converter.Converter#nextCommand(java.nio.ByteBuffer)
		public void nextCommand(ByteBuffer sendBuffer) {

		}

		// @see
		// org.F11.scada.server.converter.Converter#retryCommand(java.nio.ByteBuffer)
		public void retryCommand(ByteBuffer sendBuffer) {

		}

		// @see
		// org.F11.scada.server.converter.Converter#setEnvironment(org.F11.scada.server.communicater.Environment)
		public byte[] setEnvironment(Environment device) {
			return null;
		}

		// @see
		// org.F11.scada.server.converter.Converter#setReadCommand(org.F11.scada.server.event.WifeCommand)
		public void setReadCommand(WifeCommand commdef) throws WifeException {

		}

		// @see
		// org.F11.scada.server.converter.Converter#setWriteCommand(org.F11.scada.server.event.WifeCommand,
		// byte[])
		public void setWriteCommand(WifeCommand commdef, byte[] data)
				throws WifeException {

		}

	}
}
