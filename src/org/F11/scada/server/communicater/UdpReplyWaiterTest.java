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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.spi.SelectorProvider;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;

/**
 * @author hori
 */
public class UdpReplyWaiterTest extends TestCase {

	private Server server;

	/**
	 * Constructor for UdpReplyWaiterTest.
	 * @param arg0
	 */
	public UdpReplyWaiterTest(String arg0) {
		super(arg0);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		server = new Server();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

	public void testSyncSendRecv001() throws Exception {
		// 作成
		ReplyWaiter waiter = new UdpReplyWaiter(makeEnvironment("UDP",
				InetAddress.getLocalHost().getHostAddress(), 9601,
				InetAddress.getLocalHost().getHostAddress(), 9600),
				WifeUtilities.toByteArray("c00002010203040506"));
		ByteBuffer recvData = ByteBuffer.allocate(2048);

		ByteBuffer sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c00002010203040506070809"));

		// 送受信
		waiter.syncSendRecv(sendData, recvData);

		assertEquals(12, recvData.remaining());
		byte[] data = new byte[recvData.remaining()];
		recvData.get(data);
		assertEquals("c00002010203040506070809", WifeUtilities.toString(data));

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000201020304050607080910ff"));

		// 送受信
		waiter.syncSendRecv(sendData, recvData);

		assertEquals(14, recvData.remaining());
		data = new byte[recvData.remaining()];
		recvData.get(data);
		assertEquals("c0000201020304050607080910ff",
				WifeUtilities.toString(data));

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000209080706050403020110ff"));
		waiter.syncSendRecv(sendData, recvData);
		assertEquals(0, recvData.remaining());

		// クローズ
		waiter.close();
	}

	public void testSyncSendRecv002() throws Exception {
		// 作成
		ReplyWaiter waiter = new UdpReplyWaiter(makeEnvironment2("UDP",
				InetAddress.getLocalHost().getHostAddress(), 9601,
				InetAddress.getLocalHost().getHostAddress(), 9600),
				WifeUtilities.toByteArray("c00002010203040506"));
		ByteBuffer recvData = ByteBuffer.allocate(2048);

		ByteBuffer sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c00002010203040506070809"));

		// 送受信
		waiter.syncSendRecv(sendData, recvData);

		assertEquals(12, recvData.remaining());
		byte[] data = new byte[recvData.remaining()];
		recvData.get(data);
		assertEquals("c00002010203040506070809", WifeUtilities.toString(data));

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000201020304050607080910ff"));

		// 送受信
		waiter.syncSendRecv(sendData, recvData);

		assertEquals(14, recvData.remaining());
		data = new byte[recvData.remaining()];
		recvData.get(data);
		assertEquals("c0000201020304050607080910ff",
				WifeUtilities.toString(data));

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000209080706050403020110ff"));
		waiter.syncSendRecv(sendData, recvData);
		assertEquals(0, recvData.remaining());

		// 二重化切り替え
		waiter.change2sub();

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000201020304050607080910ff"));

		// 送受信
		waiter.syncSendRecv(sendData, recvData);

		assertEquals(14, recvData.remaining());
		data = new byte[recvData.remaining()];
		recvData.get(data);
		assertEquals("c0000201020304050607080910ff",
				WifeUtilities.toString(data));

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000209080706050403020110ff"));
		waiter.syncSendRecv(sendData, recvData);
		assertEquals(0, recvData.remaining());

		// 二重化切り替え
		waiter.change2sub();

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000201020304050607080910ff"));

		// 送受信
		waiter.syncSendRecv(sendData, recvData);

		assertEquals(14, recvData.remaining());
		data = new byte[recvData.remaining()];
		recvData.get(data);
		assertEquals("c0000201020304050607080910ff",
				WifeUtilities.toString(data));

		sendData = ByteBuffer.wrap(WifeUtilities.toByteArray("c0000209080706050403020110ff"));
		waiter.syncSendRecv(sendData, recvData);
		assertEquals(0, recvData.remaining());

		// クローズ
		waiter.close();
	}

	/*
	 * Environment生成関数
	 */
	private Environment makeEnvironment(final String kind, final String plcIp,
			final int plcPort, final String hostIp, final int hostPort) {
		return new Environment() {
			public String getDeviceID() {
				return null;
			}
			public String getDeviceKind() {
				return kind;
			}
			public String getPlcIpAddress() {
				return plcIp;
			}
			public String getPlcIpAddress2() {
				return null;
			}
			public int getPlcPortNo() {
				return plcPort;
			}
			public String getPlcCommKind() {
				return null;
			}
			public int getPlcNetNo() {
				return 0;
			}
			public int getPlcNodeNo() {
				return 0;
			}
			public int getPlcUnitNo() {
				return 0;
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
				return 0;
			}
			public int getHostPortNo() {
				return hostPort;
			}
			public String getHostIpAddress() {
				return hostIp;
			}
			public int getHostAddress() {
				return 0;
			}
		};
	}
	private Environment makeEnvironment2(final String kind, final String plcIp,
			final int plcPort, final String hostIp, final int hostPort) {
		return new Environment() {
			public String getDeviceID() {
				return null;
			}
			public String getDeviceKind() {
				return kind;
			}
			public String getPlcIpAddress() {
				return plcIp;
			}
			public String getPlcIpAddress2() {
				return plcIp;
			}
			public int getPlcPortNo() {
				return plcPort;
			}
			public String getPlcCommKind() {
				return null;
			}
			public int getPlcNetNo() {
				return 0;
			}
			public int getPlcNodeNo() {
				return 0;
			}
			public int getPlcUnitNo() {
				return 0;
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
				return 0;
			}
			public int getHostPortNo() {
				return hostPort;
			}
			public String getHostIpAddress() {
				return hostIp;
			}
			public int getHostAddress() {
				return 0;
			}
		};
	}

	/*
	 * レスポンス作成用サーバー 受信データをそのまま送信
	 */
	private class Server implements Runnable {
		private volatile Thread thread;
		private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);

		public Server() throws InterruptedException {
			startup();
		}

		public synchronized void startup() throws InterruptedException {
			thread = new Thread(this);
			thread.start();
			wait();
			System.out.println("server start.");
		}

		public void shutdown() throws InterruptedException {
			Thread th = thread;
			thread.interrupt();
			thread = null;
			th.join();
			System.out.println("server stop.");
		}

		public void run() {
			DatagramChannel channel = null;
			try {
				channel = SelectorProvider.provider().openDatagramChannel();
				channel.configureBlocking(true);
				channel.socket().setBroadcast(true);
				channel.socket().bind(
						new InetSocketAddress(
								InetAddress.getLocalHost().getHostAddress(),
								9601));
				synchronized (this) {
					notifyAll();
				}
				while (thread == Thread.currentThread()) {
					SocketAddress target = null;
					sendBuffer.clear();
					try {
						target = channel.receive(sendBuffer);
					} catch (ClosedByInterruptException e) {
						break;
					}
					Thread.yield();
					sendBuffer.flip();
					channel.send(sendBuffer, target);
				}
				channel.close();
				channel = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (channel != null) {
					try {
						channel.close();
					} catch (Exception e) {
					}
				}
			}
		}

	}
}
