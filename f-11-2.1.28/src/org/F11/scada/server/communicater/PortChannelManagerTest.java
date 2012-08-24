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
public class PortChannelManagerTest extends TestCase {

	/**
	 * Constructor for PortChannelManagerTest.
	 * @param arg0
	 */
	public PortChannelManagerTest(String arg0) throws Exception {
		super(arg0);
	}

	public void test001() throws Exception {
		InetSocketAddress local5000 =
			new InetSocketAddress(InetAddress.getLocalHost(), 5000);
		InetSocketAddress local9600 =
			new InetSocketAddress(InetAddress.getLocalHost(), 9600);
		InetSocketAddress local9601 =
			new InetSocketAddress(InetAddress.getLocalHost(), 9601);

		assertEquals(0, PortChannelManager.portChannels.size());
		assertNull(PortChannelManager.portSelector);
		Listener listener1 = new Listener(local5000, "C00002010203040506");
		// ポート追加
		PortChannel port1 =
			PortChannelManager.getInstance().addPortListener(
				"UDP",
				local5000,
				local9600,
				listener1);
		assertTrue(port1 instanceof UdpPortChannel);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		Listener listener2 = new Listener(local5000, "C00002060504030201");
		// ポート取得
		PortChannel port2 =
			PortChannelManager.getInstance().addPortListener(
				"UDP",
				local5000,
				local9600,
				listener2);
		assertTrue(port2 instanceof UdpPortChannel);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		Listener listener3 = new Listener(local5000, "C00002040506010203");
		// ポート追加
		PortChannel port3 =
			PortChannelManager.getInstance().addPortListener(
				"UDP",
				local5000,
				local9601,
				listener3);
		assertTrue(port3 instanceof UdpPortChannel);
		assertEquals(2, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);

		assertSame(port1, port2);
		assertNotSame(port1, port3);

		// リスナー削除、ポート削除
		PortChannelManager.getInstance().removePortListener(
			local9601,
			listener3);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		// リスナー削除
		PortChannelManager.getInstance().removePortListener(
			local9600,
			listener2);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		// リスナー削除、ポート削除
		PortChannelManager.getInstance().removePortListener(
			local9600,
			listener1);
		assertEquals(0, PortChannelManager.portChannels.size());
		assertNull(PortChannelManager.portSelector);
	}

	public void test002() throws Exception {
		InetSocketAddress local5000 =
			new InetSocketAddress(InetAddress.getLocalHost(), 5000);
		InetSocketAddress local5001 =
			new InetSocketAddress(InetAddress.getLocalHost(), 5001);
		InetSocketAddress local9600 =
			new InetSocketAddress(InetAddress.getLocalHost(), 9600);

		assertEquals(0, PortChannelManager.portChannels.size());
		assertNull(PortChannelManager.portSelector);
		Listener listener1 = new Listener(local5001, "C00002010203040506");
		// ポート追加
		PortChannel port1 =
			PortChannelManager.getInstance().addPortListener(
				"TCP",
				local5000,
				local9600,
				listener1);
		assertTrue(port1 instanceof TcpPortChannel);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		Listener listener2 = new Listener(local5001, "C00002060504030201");
		// ポート取得
		PortChannel port2 =
			PortChannelManager.getInstance().addPortListener(
				"TCP",
				local5000,
				local9600,
				listener2);
		assertTrue(port2 instanceof TcpPortChannel);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		Listener listener3 = new Listener(local5001, "C00002040506010203");
		// ポート追加
		PortChannel port3 =
			PortChannelManager.getInstance().addPortListener(
				"TCP",
				local5001,
				local9600,
				listener3);
		assertTrue(port3 instanceof TcpPortChannel);
		assertEquals(2, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);

		assertSame(port1, port2);
		assertNotSame(port1, port3);

		// リスナー削除、ポート削除
		PortChannelManager.getInstance().removePortListener(
			local5001,
			listener3);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		// リスナー削除
		PortChannelManager.getInstance().removePortListener(
			local5000,
			listener2);
		assertEquals(1, PortChannelManager.portChannels.size());
		assertNotNull(PortChannelManager.portSelector);
		// リスナー削除、ポート削除
		PortChannelManager.getInstance().removePortListener(
			local5000,
			listener1);
		assertEquals(0, PortChannelManager.portChannels.size());
		assertNull(PortChannelManager.portSelector);
	}

	/*
	 * テスト用リスナー
	 */
	private class Listener implements RecvListener {
		private final byte[] head;
		private byte[] resdata = null;
		private int recvcnt = 0;
		private InetSocketAddress target;

		public Listener(InetSocketAddress target, String strhead) {
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
