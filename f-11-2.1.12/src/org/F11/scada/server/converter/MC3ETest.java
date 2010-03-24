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

package org.F11.scada.server.converter;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * @author hori
 */
public class MC3ETest extends TestCase {

	private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
	private ByteBuffer recvBuffer = ByteBuffer.allocate(2048);
	private ByteBuffer recvData = ByteBuffer.allocate(2048);

	// ÉeÉXÉgëŒè€
	private Converter Fixture = new MC3E();

	/**
	 * Constructor for MC3ETest.
	 * @param arg0
	 */
	public MC3ETest(String arg0) {
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		Environment device = new Environment() {
			public String getDeviceID() {
				return null;
			}
			public String getDeviceKind() {
				return null;
			}
			public String getPlcIpAddress() {
				return null;
			}
			public int getPlcPortNo() {
				return 0;
			}
			public String getPlcCommKind() {
				return null;
			}
			public int getPlcNetNo() {
				return 1;
			}
			public int getPlcNodeNo() {
				return 2;
			}
			public int getPlcUnitNo() {
				return 0;
			}
			public int getPlcWatchWait() {
				return 16;
			}
			public int getPlcTimeout() {
				return 500;
			}
			public int getPlcRetryCount() {
				return 0;
			}
			public int getPlcRecoveryWait() {
				return 0;
			}
			public int getHostNetNo() {
				return 0;
			}
			public int getHostPortNo() {
				return 20480;
			}
			public String getHostIpAddress() {
				return "127.0.0.7";
			}
			public int getHostAddress() {
				return 7;
			}
		};
		assertEquals(
			"d0000102ff0300",
			WifeUtilities.toString(Fixture.setEnvironment(device)));
	}

	public void testReadCommand() throws Exception {
		assertEquals(Fixture.getPacketMaxSize(null), 960);
		// D0,1
		WifeCommand comm = new WifeCommand("1", 10, 0, 0, 0, 1);
		assertFalse(Fixture.hasCommand());
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000000000a80100",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("d0000102ff0300040000003412"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		recvData.clear();
		Fixture.getResponceData(recvBuffer, recvData);
		recvData.flip();
		assertEquals("1234", WifeUtilities.toString(recvData));
		// D500,26
		comm = new WifeCommand("1", 21, 0, 0, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000f40100a81a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// D100,1000
		comm = new WifeCommand("1", 15, 0, 0, 100, 1000);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000640000a8c003",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000240400a82800",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// M500,26
		comm = new WifeCommand("1", 21, 0, 1, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000401f00901a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// M100,1000
		comm = new WifeCommand("1", 15, 0, 1, 100, 1000);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c0010000104000040060090c003",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000404200902800",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// R500,26
		comm = new WifeCommand("1", 21, 0, 10, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000f40100b01a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// R100,1000
		comm = new WifeCommand("1", 15, 0, 10, 100, 1000);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000640000b0c003",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000640000b0c003",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000640000b0c003",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000240400b02800",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000240400b02800",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000240400b02800",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// D7003,2
		comm = new WifeCommand("1", 21, 0, 0, 7003, 2);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c001000010400005b1b00a80200",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c001000010400005b1b00a80200",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c001000010400005b1b00a80200",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// SM500,26
		comm = new WifeCommand("1", 21, 0, 90, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000401f00911a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
	}

	public void testWriteCommand() throws Exception {
		byte[] data0000 = WifeUtilities.toByteArray("0000");
		byte[] dataFFFF = WifeUtilities.toByteArray("fffff00f");
		byte[] data1234 = WifeUtilities.toByteArray("010203045560708090a0b0cc");
		// D0,1
		WifeCommand comm = new WifeCommand("1", 10, 1, 0, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000e00100001140000000000a801000000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// D500,6
		comm = new WifeCommand("1", 21, 1, 0, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001800100001140000f40100a806000201040360558070a090ccb0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// D65535,2
		comm = new WifeCommand("1", 15, 1, 0, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001000100001140000ffff00a80200ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// M0,1
		comm = new WifeCommand("1", 10, 1, 1, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000e001000011400000000009001000000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// M500,6
		comm = new WifeCommand("1", 21, 1, 1, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001800100001140000401f009006000201040360558070a090ccb0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// M65535,2
		comm = new WifeCommand("1", 15, 1, 1, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001000100001140000f0ff0f900200ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// R0,1
		comm = new WifeCommand("1", 10, 1, 10, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000e00100001140000000000b001000000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// R500,6
		comm = new WifeCommand("1", 21, 1, 10, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001800100001140000f40100b006000201040360558070a090ccb0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// R65535,2
		comm = new WifeCommand("1", 15, 1, 10, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001000100001140000ffff00b00200ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001000100001140000ffff00b00200ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001000100001140000ffff00b00200ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		byte[] data40ob =
			WifeUtilities.toByteArray(
				"11102222333344445555666677778888999900001111222233334444555566667777888899990123"
					+ "11012222333344445555666677778888999900001111222233334444555566667777888899993210"
					+ "101122223333444455556666777788889999000011112222333344445555666677778888");
		// D0,40
		comm = new WifeCommand("1", 15, 1, 0, 0, 40);
		Fixture.setWriteCommand(comm, data40ob);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03005c00100001140000000000a82800"
				+ "10112222333344445555666677778888999900001111222233334444555566667777888899992301"
				+ "01112222333344445555666677778888999900001111222233334444555566667777888899991032",
		//					 "101122223333444455556666777788889999000011112222333344445555666677778888",
		WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		// SM500,6
		comm = new WifeCommand("1", 21, 1, 90, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03001800100001140000401f009106000201040360558070a090ccb0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
	}

	public void testMultiWrite() throws Exception {
		byte[] data = new byte[4000];
		for (int i = 0; i < data.length; i += 2) {
			data[i + 0] = (byte) (i / 100);
			data[i + 1] = (byte) (i % 100);
		}
		WifeCommand comm = new WifeCommand("1", 15, 1, 0, 0, 2000);
		Fixture.setWriteCommand(comm, data);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03008c07100001140000000000a8c003",
			WifeUtilities.toString(sendBuffer, 0, 21));
		for (int i = 0; i < 1920; i += 2) {
			assertEquals((byte) (i % 100), sendBuffer.get(21 + i));
			assertEquals((byte) (i / 100), sendBuffer.get(21 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03008c07100001140000000000a8c003",
			WifeUtilities.toString(sendBuffer, 0, 21));
		for (int i = 0; i < 1920; i += 2) {
			assertEquals((byte) (i % 100), sendBuffer.get(21 + i));
			assertEquals((byte) (i / 100), sendBuffer.get(21 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("d0000102ff030002000000"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03008c07100001140000c00300a8c003",
			WifeUtilities.toString(sendBuffer, 0, 21));
		for (int i = 0; i < 1920; i += 2) {
			assertEquals((byte) ((i + 1920) % 100), sendBuffer.get(21 + i));
			assertEquals((byte) ((i + 1920) / 100), sendBuffer.get(21 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03008c07100001140000c00300a8c003",
			WifeUtilities.toString(sendBuffer, 0, 21));
		for (int i = 0; i < 1920; i += 2) {
			assertEquals((byte) ((i + 1920) % 100), sendBuffer.get(21 + i));
			assertEquals((byte) ((i + 1920) / 100), sendBuffer.get(21 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("d0000102ff030002000000"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff0300ac00100001140000800700a85000",
			WifeUtilities.toString(sendBuffer, 0, 21));
		for (int i = 0; i < 160; i += 2) {
			assertEquals((byte) ((i + 3840) % 100), sendBuffer.get(21 + i));
			assertEquals((byte) ((i + 3840) / 100), sendBuffer.get(21 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff0300ac00100001140000800700a85000",
			WifeUtilities.toString(sendBuffer, 0, 21));
		for (int i = 0; i < 160; i += 2) {
			assertEquals((byte) ((i + 3840) % 100), sendBuffer.get(21 + i));
			assertEquals((byte) ((i + 3840) / 100), sendBuffer.get(21 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("d0000102ff030002000000"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertFalse(Fixture.hasCommand());
	}
	
	public void testBitRead() throws Exception {
		// L500,26
		WifeCommand comm = new WifeCommand("1", 21, 0, 5, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"50000102ff03000c00100001040000401f00921a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
		
	}

}
