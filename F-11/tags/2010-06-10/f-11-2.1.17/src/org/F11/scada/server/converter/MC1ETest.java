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
public class MC1ETest extends TestCase {

	private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
	private ByteBuffer recvBuffer = ByteBuffer.allocate(2048);
	private ByteBuffer recvData = ByteBuffer.allocate(2048);

	// ÉeÉXÉgëŒè€
	private Converter Fixture = new MC1E();

	/**
	 * Constructor for MC1ETest.
	 * @param arg0
	 */
	public MC1ETest(String arg0) {
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
			public String getPlcIpAddress2() {
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
			"8100",
			WifeUtilities.toString(Fixture.setEnvironment(device)));
	}

	public void testReadCommand() throws Exception {
		WifeCommand comm = new WifeCommand("1", 10, 0, 0, 0, 1);
		assertEquals(Fixture.getPacketMaxSize(comm), 256);
		assertFalse(Fixture.hasCommand());
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210000000000020440100",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		recvBuffer.clear();
		recvBuffer.put(WifeUtilities.toByteArray("81003412")).flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		recvData.clear();
		Fixture.getResponceData(recvBuffer, recvData);
		recvData.flip();
		assertEquals("1234", WifeUtilities.toString(recvData));

		comm = new WifeCommand("1", 21, 0, 0, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"01021000f401000020441a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 0, 0, 100, 550);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006400000020440000",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006401000020440000",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006402000020442600",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 0, 1, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"01021000401f0000204d1a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 0, 1, 100, 300);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"0102100040060000204d8000",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"01021000400e0000204d8000",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"0102100040160000204d2c00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 0, 10, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"01021000f401000020521a00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 0, 10, 100, 300);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006400000020520000",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006400000020520000",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006400000020520000",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006401000020522c00",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006401000020522c00",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210006401000020522c00",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 0, 0, 7003, 2);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210005b1b000020440200",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210005b1b000020440200",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"010210005b1b000020440200",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
	}

	public void testWriteCommand() throws Exception {
		byte[] data0000 = WifeUtilities.toByteArray("0000");
		byte[] dataFFFF = WifeUtilities.toByteArray("fffff00f");
		byte[] data1234 = WifeUtilities.toByteArray("010203045560708090a0b0cc");

		WifeCommand comm = new WifeCommand("1", 10, 1, 0, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000000000020440100" + "0000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 1, 0, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000f401000020440600" + "0201040360558070a090ccb0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 0, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000ffff000020440200" + "ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 10, 1, 1, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"0302100000000000204d0100" + "0000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 1, 1, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000401f0000204d0600" + "0201040360558070a090ccb0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 1, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000f0ff0f00204d0200" + "ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 10, 1, 10, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000000000020520100" + "0000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 1, 10, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000f401000020520600" + "0201040360558070a090ccb0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 10, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000ffff000020520200" + "ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000ffff000020520200" + "ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"03021000ffff000020520200" + "ffff0ff0",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		byte[] data40ob =
			WifeUtilities.toByteArray(
				"11102222333344445555666677778888999900001111222233334444555566667777888899990123"
					+ "11012222333344445555666677778888999900001111222233334444555566667777888899993210"
					+ "101122223333444455556666777788889999000011112222333344445555666677778888");

		comm = new WifeCommand("1", 15, 1, 0, 0, 40);
		Fixture.setWriteCommand(comm, data40ob);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000000000020442800"
				+ "10112222333344445555666677778888999900001111222233334444555566667777888899992301"
				+ "01112222333344445555666677778888999900001111222233334444555566667777888899991032",
		//					 "101122223333444455556666777788889999000011112222333344445555666677778888",
		WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
	}

	public void testMultiWrite() throws Exception {
		byte[] data = new byte[1100];
		for (int i = 0; i < data.length; i += 2) {
			data[i + 0] = (byte) (i / 100);
			data[i + 1] = (byte) (i % 100);
		}
		WifeCommand comm = new WifeCommand("1", 15, 1, 0, 0, 550);
		Fixture.setWriteCommand(comm, data);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000000000020440000",
			WifeUtilities.toString(sendBuffer, 0, 12));
		for (int i = 0; i < 512; i += 2) {
			assertEquals((byte) (i % 100), sendBuffer.get(12 + i));
			assertEquals((byte) (i / 100), sendBuffer.get(12 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000000000020440000",
			WifeUtilities.toString(sendBuffer, 0, 12));
		for (int i = 0; i < 512; i += 2) {
			assertEquals((byte) (i % 100), sendBuffer.get(12 + i));
			assertEquals((byte) (i / 100), sendBuffer.get(12 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer.put(WifeUtilities.toByteArray("8300")).flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000001000020440000",
			WifeUtilities.toString(sendBuffer, 0, 12));
		for (int i = 0; i < 512; i += 2) {
			assertEquals((byte) ((i + 512) % 100), sendBuffer.get(12 + i));
			assertEquals((byte) ((i + 512) / 100), sendBuffer.get(12 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000001000020440000",
			WifeUtilities.toString(sendBuffer, 0, 12));
		for (int i = 0; i < 512; i += 2) {
			assertEquals((byte) ((i + 512) % 100), sendBuffer.get(12 + i));
			assertEquals((byte) ((i + 512) / 100), sendBuffer.get(12 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer.put(WifeUtilities.toByteArray("8300")).flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000002000020442600",
			WifeUtilities.toString(sendBuffer, 0, 12));
		for (int i = 0; i < 38; i += 2) {
			assertEquals((byte) ((i + 1024) % 100), sendBuffer.get(12 + i));
			assertEquals((byte) ((i + 1024) / 100), sendBuffer.get(12 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"030210000002000020442600",
			WifeUtilities.toString(sendBuffer, 0, 12));
		for (int i = 0; i < 38; i += 2) {
			assertEquals((byte) ((i + 1024) % 100), sendBuffer.get(12 + i));
			assertEquals((byte) ((i + 1024) / 100), sendBuffer.get(12 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer.put(WifeUtilities.toByteArray("8300")).flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertFalse(Fixture.hasCommand());
	}

}
