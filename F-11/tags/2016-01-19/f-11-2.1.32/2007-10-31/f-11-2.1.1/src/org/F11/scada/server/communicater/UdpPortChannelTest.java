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

package org.F11.scada.server.communicater;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;

/**
 * @author hori
 */
public class UdpPortChannelTest extends TestCase {

	private PortSelector selector;

	/**
	 * Constructor for UdpPortChannelTest.
	 * @param arg0
	 */
	public UdpPortChannelTest(String arg0) {
		super(arg0);
	}

	// @see junit.framework.TestCase#setUp()
	protected void setUp() throws Exception {
		super.setUp();

		selector = new PortSelector();
	}

	// @see junit.framework.TestCase#tearDown()
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * IDKey生成テスト 
	 */
	public void testMakeIDKey() throws Exception {
		InetSocketAddress local9600 =
			new InetSocketAddress(InetAddress.getLocalHost(), 9600);

		UdpPortChannel port1 = new UdpPortChannel(local9600, selector);

		assertEquals(
			"127.0.0.1:FINS_010203040506",
			port1.makeIDKey(
				new InetSocketAddress("127.0.0.1", 9610),
				ByteBuffer.wrap(
					WifeUtilities.toByteArray("C00002010203040506"))));
		assertEquals(
			"192.168.0.10:FINS_060504030201",
			port1.makeIDKey(
				new InetSocketAddress("192.168.0.10", 9610),
				ByteBuffer.wrap(
					WifeUtilities.toByteArray("C10007060504030201"))));
		assertEquals(
			"127.0.0.1:MC3E_00ffff0300",
			port1.makeIDKey(
				new InetSocketAddress("127.0.0.1", 9610),
				ByteBuffer.wrap(WifeUtilities.toByteArray("D00000FFFF0300"))));
		assertEquals(
			"192.168.0.10:MC3E_01ffff0302",
			port1.makeIDKey(
				new InetSocketAddress("192.168.0.10", 9610),
				ByteBuffer.wrap(WifeUtilities.toByteArray("D00001FFFF0302"))));
		assertEquals(
			"127.0.0.1:BACNET_",
			port1.makeIDKey(
				new InetSocketAddress("127.0.0.1", 9610),
				ByteBuffer.wrap(WifeUtilities.toByteArray("810A00000104"))));
		assertEquals(
			"192.168.0.10:BACNET_",
			port1.makeIDKey(
				new InetSocketAddress("192.168.0.10", 9610),
				ByteBuffer.wrap(WifeUtilities.toByteArray("810B00000100"))));
		assertNull(
			port1.makeIDKey(
				new InetSocketAddress("192.168.0.10", 9610),
				ByteBuffer.wrap(WifeUtilities.toByteArray("0000"))));

		selector.removeListener(port1);
		assertFalse(selector.isActive());
	}

	/*
	 * １ポート ２リスナー
	 */
	public void test002() throws Exception {
		InetSocketAddress local9601 =
			new InetSocketAddress(InetAddress.getLocalHost(), 9601);

		UdpPortChannel port1 = new UdpPortChannel(local9601, selector);
		Listener listener1 = new Listener(local9601, "c00002010203040506");
		port1.addListener(listener1);
		Listener listener2 = new Listener(local9601, "c00002060504030201");
		port1.addListener(listener2);

		// -> listener1
		port1.sendRequest(
			local9601,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c0000201020304050607080910111213")));
		// -> listener2
		port1.sendRequest(
			local9601,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002060504030201070809101112")));
		// -> ???
		port1.sendRequest(
			local9601,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002000102030405070809")));
		// -> listener2
		port1.sendRequest(
			local9601,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray(
					"c000020605040302010708091011121314")));
		// -> ???
		port1.sendRequest(
			local9601,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c000020001020304050708091011")));

		Thread.sleep(100);

		assertFalse(port1.removeListener(listener2));
		assertTrue(port1.removeListener(listener1));

		assertEquals(
			"c0000201020304050607080910111213",
			WifeUtilities.toString(listener1.resdata));
		assertEquals(1, listener1.recvcnt);

		assertEquals(
			"c000020605040302010708091011121314",
			WifeUtilities.toString(listener2.resdata));
		assertEquals(2, listener2.recvcnt);

		selector.removeListener(port1);
		assertFalse(selector.isActive());
	}

	/*
	 * ２ポート ２リスナー
	 */
	public void test003() throws Exception {
		InetSocketAddress local9602 =
			new InetSocketAddress(InetAddress.getLocalHost(), 9602);
		InetSocketAddress local9603 =
			new InetSocketAddress(InetAddress.getLocalHost(), 9603);

		UdpPortChannel port1 = new UdpPortChannel(local9602, selector);
		UdpPortChannel port2 = new UdpPortChannel(local9603, selector);

		Listener listener1 = new Listener(local9603, "c00002010203040506");
		port1.addListener(listener1);
		Listener listener2 = new Listener(local9602, "c00002060504030201");
		port2.addListener(listener2);

		// port2 -> port1.listener1
		port2.sendRequest(
			local9602,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c0000201020304050607080910111213")));
		port2.sendRequest(
			local9602,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c0000201020304050607080910111213")));
		// port1 -> port2.listener2
		port1.sendRequest(
			local9603,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002060504030201070809101112")));
		// port2 -> port1.???
		port2.sendRequest(
			local9602,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002000102030405070809")));
		// port1 -> port2.listener2
		port1.sendRequest(
			local9603,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray(
					"c000020605040302010708091011121314")));
		// port1 -> port2.???
		port1.sendRequest(
			local9603,
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c000020001020304050708091011")));

		Thread.sleep(100);

		assertTrue(port2.removeListener(listener2));
		assertTrue(port1.removeListener(listener1));

		assertEquals(
			"c0000201020304050607080910111213",
			WifeUtilities.toString(listener1.resdata));
		assertEquals(2, listener1.recvcnt);

		assertEquals(
			"c000020605040302010708091011121314",
			WifeUtilities.toString(listener2.resdata));
		assertEquals(2, listener2.recvcnt);

		selector.removeListener(port2);
		assertTrue(selector.isActive());
		selector.removeListener(port1);
		assertFalse(selector.isActive());
	}

	/*
	 * テスト用リスナークラス
	 */
	private class Listener implements RecvListener {
		private final byte[] head;
		private byte[] resdata = null;
		private int recvcnt = 0;
		private InetSocketAddress target;

		public Listener(InetSocketAddress target, String strhead)
			throws Exception {
			head = WifeUtilities.toByteArray(strhead);
			this.target = target;
		}

		// @see org.F11.scada.server.communicater.RecvListener#getRecvHeader()
		public byte[] getRecvHeader() {
			return head;
		}

		// @see org.F11.scada.server.communicater.RecvListener#recvPerformed(java.nio.ByteBuffer)
		public void recvPerformed(ByteBuffer data) {
			resdata = new byte[data.remaining()];
			data.get(resdata);
			recvcnt++;
		}

		// @see org.F11.scada.server.communicater.RecvListener#getRecvAddress()
		public InetSocketAddress getRecvAddress() {
			return target;
		}

	}
}
