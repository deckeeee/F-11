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
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FINSTest extends TestCase {

	private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
	private ByteBuffer recvBuffer = ByteBuffer.allocate(2048);
	private ByteBuffer recvData = ByteBuffer.allocate(2048);

	// ÉeÉXÉgëŒè€
	private FINS Fixture = new FINS();

	/**
	 * Constructor for FINSTest.
	 * @param arg0
	 */
	public FINSTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		Fixture.sid = 0;
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
				return 3;
			}
			public int getPlcNodeNo() {
				return 4;
			}
			public int getPlcUnitNo() {
				return 5;
			}
			public int getPlcWatchWait() {
				return 0;
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
				return 6;
			}
			public int getHostPortNo() {
				return 9601;
			}
			public String getHostIpAddress() {
				return "127.0.0.7";
			}
			public int getHostAddress() {
				return 7;
			}
		};
		assertEquals(
			"c00002060700030405",
			WifeUtilities.toString(Fixture.setEnvironment(device)));
	}

	public void testReadCommand() throws Exception {
		assertEquals(Fixture.getPacketMaxSize(null), 998);
		WifeCommand comm = new WifeCommand("1", 10, 0, 0, 0, 1);
		assertFalse(Fixture.hasCommand());
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertFalse(Fixture.hasCommand());
		assertEquals(
			"800002030405060700010101820000000001",
			WifeUtilities.toString(sendBuffer));

		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("c0000206070003040501010100001234"))
			.flip();
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
		assertFalse(Fixture.hasCommand());
		assertEquals(
			"8000020304050607000201018201f400001a",
			WifeUtilities.toString(sendBuffer));

		comm = new WifeCommand("1", 15, 0, 0, 100, 1000);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000301018200640003e6",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"80000203040506070004010182044a000002",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 0, 1, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700050101b001f400001a",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 0, 1, 100, 1000);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700060101b000640003e6",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700070101b0044a000002",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 0, 10, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700080101a001f400001a",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 0, 10, 100, 1000);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700090101a000640003e6",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000a0101a0044a000002",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 0, 17, 500, 26);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000b0101a701f400001a",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 0, 17, 100, 1000);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000c0101a700640003e6",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000d0101a700640003e6",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000e0101a700640003e6",
			WifeUtilities.toString(sendBuffer));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000f0101a7044a000002",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700100101a7044a000002",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700110101a7044a000002",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
	}

	public void testWriteCommand() throws Exception {
		byte[] data0000 = new byte[] {(byte) 0x00, (byte) 0x00 };
		byte[] dataFFFF =
			new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xf0, (byte) 0x0f };
		byte[] data1234 =
			new byte[] {
				(byte) 0x01,
				(byte) 0x02,
				(byte) 0x03,
				(byte) 0x04,
				(byte) 0x55,
				(byte) 0x60,
				(byte) 0x70,
				(byte) 0x80,
				(byte) 0x90,
				(byte) 0xa0,
				(byte) 0xb0,
				(byte) 0xcc };
		byte[] data40ob =
			WifeUtilities.toByteArray(
				"11102222333344445555666677778888999900001111222233334444555566667777888899990000"
					+ "11012222333344445555666677778888999900001111222233334444555566667777888899990000"
					+ "101122223333444455556666777788889999000011112222333344445555666677778888");

		WifeCommand comm = new WifeCommand("1", 10, 1, 0, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000101028200000000010000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 1, 0, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000201028201f4000006010203045560708090a0b0cc",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 0, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"80000203040506070003010282ffff000002fffff00f",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 10, 1, 1, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700040102b000000000010000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 1, 1, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700050102b001f4000006010203045560708090a0b0cc",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 1, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700060102b0ffff000002fffff00f",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 10, 1, 10, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700070102a000000000010000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 1, 10, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700080102a001f4000006010203045560708090a0b0cc",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 10, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700090102a0ffff000002fffff00f",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 10, 1, 17, 0, 1);
		Fixture.setWriteCommand(comm, data0000);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000a0102a700000000010000",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 21, 1, 17, 500, 6);
		Fixture.setWriteCommand(comm, data1234);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000b0102a701f4000006010203045560708090a0b0cc",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 17, 65535, 2);
		Fixture.setWriteCommand(comm, dataFFFF);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000c0102a7ffff000002fffff00f",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000d0102a7ffff000002fffff00f",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000e0102a7ffff000002fffff00f",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		comm = new WifeCommand("1", 15, 1, 0, 0, 40);
		Fixture.setWriteCommand(comm, data40ob);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000f0102820000000028"
				+ "11102222333344445555666677778888999900001111222233334444555566667777888899990000"
				+ "11012222333344445555666677778888999900001111222233334444555566667777888899990000",
		//					 "101122223333444455556666777788889999000011112222333344445555666677778888",
		WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700100102820000000028"
				+ "11102222333344445555666677778888999900001111222233334444555566667777888899990000"
				+ "11012222333344445555666677778888999900001111222233334444555566667777888899990000",
		//					 "101122223333444455556666777788889999000011112222333344445555666677778888",
		WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());
	}

	public void testPlcStatusArea() throws Exception {
		WifeCommand comm = new WifeCommand("1", 10, 0, 90, 0, 1);
		assertFalse(Fixture.hasCommand());
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700010601",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700020601",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700030601",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		recvBuffer.clear();
		recvBuffer
			.put(
				WifeUtilities.toByteArray(
					"c000020607000304050306010000"
						+ "1110222233334444555566667777888899990000111122223333"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));
		recvData.clear();
		Fixture.getResponceData(recvBuffer, recvData);
		recvData.flip();
		assertEquals(
			"1110222233334444555566667777888899990000111122223333",
			WifeUtilities.toString(recvData));

		comm = new WifeCommand("1", 21, 0, 90, 2, 3);
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700040601",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700050601",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700060601",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		recvBuffer.clear();
		recvBuffer
			.put(
				WifeUtilities.toByteArray(
					"c000020607000304050606010000"
						+ "1110222233334444555566667777888899990000111122223333"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));
		recvData.clear();
		Fixture.getResponceData(recvBuffer, recvData);
		recvData.flip();
		assertEquals(
			"33334444555566667777888899990000111122223333",
			WifeUtilities.toString(recvData));
	}

	public void testTimeArea() throws Exception {
		WifeCommand comm = new WifeCommand("1", 10, 0, 91, 0, 0);
		assertFalse(Fixture.hasCommand());
		Fixture.setReadCommand(comm);
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700010701",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700020701",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"800002030405060700030701",
			WifeUtilities.toString(sendBuffer));
		assertFalse(Fixture.hasCommand());

		recvBuffer.clear();
		recvBuffer
			.put(
				WifeUtilities.toByteArray(
					"c00002060700030405030701000003071018283504"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));
		recvData.clear();
		Fixture.getResponceData(recvBuffer, recvData);
		recvData.flip();
		assertEquals(
			"20030007001000040018002800350001",
			WifeUtilities.toString(recvData));

		comm = new WifeCommand("1", 21, 1, 91, 0, 0);
		Fixture.setWriteCommand(
			comm,
			WifeUtilities.toByteArray("20030007001000040018002800350001"));
		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"80000203040506070004070203071018283504",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"80000203040506070005070203071018283504",
			WifeUtilities.toString(sendBuffer));
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"80000203040506070006070203071018283504",
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
			"8000020304050607000101028200000003e1",
			WifeUtilities.toString(sendBuffer, 0, 18));
		for (int i = 0; i < 1986; i += 2) {
			assertEquals((byte) (i / 100), sendBuffer.get(18 + i));
			assertEquals((byte) (i % 100), sendBuffer.get(18 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000201028200000003e1",
			WifeUtilities.toString(sendBuffer, 0, 18));
		for (int i = 0; i < 1986; i += 2) {
			assertEquals((byte) (i / 100), sendBuffer.get(18 + i));
			assertEquals((byte) (i % 100), sendBuffer.get(18 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("c000020607000304050201020000"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000301028203e10003e1",
			WifeUtilities.toString(sendBuffer, 0, 18));
		for (int i = 0; i < 1986; i += 2) {
			assertEquals((byte) ((i + 1986) / 100), sendBuffer.get(18 + i));
			assertEquals((byte) ((i + 1986) % 100), sendBuffer.get(18 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000401028203e10003e1",
			WifeUtilities.toString(sendBuffer, 0, 18));
		for (int i = 0; i < 1986; i += 2) {
			assertEquals((byte) ((i + 1986) / 100), sendBuffer.get(18 + i));
			assertEquals((byte) ((i + 1986) % 100), sendBuffer.get(18 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("c000020607000304050401020000"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertTrue(Fixture.hasCommand());
		sendBuffer.clear();
		Fixture.nextCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000501028207c200000e",
			WifeUtilities.toString(sendBuffer, 0, 18));
		for (int i = 0; i < 28; i += 2) {
			assertEquals((byte) ((i + 3972) / 100), sendBuffer.get(18 + i));
			assertEquals((byte) ((i + 3972) % 100), sendBuffer.get(18 + i + 1));
		}
		sendBuffer.clear();
		Fixture.retryCommand(sendBuffer);
		sendBuffer.flip();
		assertEquals(
			"8000020304050607000601028207c200000e",
			WifeUtilities.toString(sendBuffer, 0, 18));
		for (int i = 0; i < 28; i += 2) {
			assertEquals((byte) ((i + 3972) / 100), sendBuffer.get(18 + i));
			assertEquals((byte) ((i + 3972) % 100), sendBuffer.get(18 + i + 1));
		}
		recvBuffer.clear();
		recvBuffer
			.put(WifeUtilities.toByteArray("c000020607000304050601020000"))
			.flip();
		assertNull(Fixture.checkCommandResponce(recvBuffer));

		assertFalse(Fixture.hasCommand());
	}

}
