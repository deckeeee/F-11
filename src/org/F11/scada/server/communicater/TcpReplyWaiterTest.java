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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.F11.scada.WifeUtilities;

/**
 * @author hori
 */
public class TcpReplyWaiterTest extends TestCase {

	static TcpServer server;

	/**
	 * Constructor for TcpReplyWaiterTest.
	 * @param arg0
	 */
	public TcpReplyWaiterTest(String arg0) {
		super(arg0);
	}

	public void testSyncSendRecv001() throws Exception {
		// 作成
		ReplyWaiter waiter = new TcpReplyWaiter(makeEnvironment("TCP",
				InetAddress.getLocalHost().getHostAddress(), 5001,
				InetAddress.getLocalHost().getHostAddress(), 0),
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
		ReplyWaiter waiter = new TcpReplyWaiter(makeEnvironment2("TCP",
				InetAddress.getLocalHost().getHostAddress(), 5001,
				InetAddress.getLocalHost().getHostAddress(), 0),
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
				return 1000;
			}
			public int getPlcRetryCount() {
				return 2;
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
				return 1000;
			}
			public int getPlcRetryCount() {
				return 2;
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
	 * テストケース単位の前処理、後処理
	 */
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite(TcpReplyWaiterTest.class);
		TestSetup wrapper = new TestSetup(suite) {
			protected void setUp() throws Exception {
				oneTimeSetUp();
			}

			protected void tearDown() throws Exception {
				oneTimeTearDown();
			}
		};
		return wrapper;
	}

	static void oneTimeSetUp() throws Exception {
		server = new TcpServer(5001);
	}
	static void oneTimeTearDown() throws Exception {
		server.end();
	}

	/*
	 * TCPサーバー レスポンス作成用サーバー 受信データをそのまま送信
	 */
	static class TcpServer implements Runnable {
		private Thread thread;
		private Selector selector;
		private ServerSocketChannel serverSocketChannel;
		private final int port;

		private ByteBuffer buffer = ByteBuffer.allocate(1024);

		public TcpServer(int port) throws Exception {
			this.port = port;
			selector = SelectorProvider.provider().openSelector();

			serverSocketChannel = SelectorProvider.provider().openServerSocketChannel();

			serverSocketChannel.configureBlocking(false);

			InetSocketAddress address = new InetSocketAddress(
					InetAddress.getLocalHost(), port);
			serverSocketChannel.socket().bind(address);

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			thread = new Thread(this);
			thread.start();
		}
		public void end() {
			try {
				serverSocketChannel.close();
				selector.wakeup();
				selector.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void accept(ServerSocketChannel serverSocketChannel)
				throws IOException {
			SocketChannel socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
			System.out.println("server:connect.");
		}

		private void sendBack(SocketChannel socketChannel) throws IOException,
				InterruptedException {
			buffer.clear();
			if (socketChannel.read(buffer) < 0) {
				socketChannel.close();
				return;
			}
			buffer.flip();

			Thread.sleep(10);
			System.out.println("server:" + WifeUtilities.toString(buffer));
			socketChannel.write(buffer);
		}
		// @see java.lang.Runnable#run()
		public void run() {
			try {
				System.out.println("server:start " + port);
				while (selector.select() > 0) {
					Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

					while (keyIterator.hasNext()) {
						SelectionKey key = keyIterator.next();

						if (key.isAcceptable()) {
							// accept
							ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
							accept(serverSocketChannel);
						} else if (key.isReadable()) {
							// read
							SocketChannel socketChannel = (SocketChannel) key.channel();
							sendBack(socketChannel);
						}
					}
					keyIterator.remove();
				}
				System.out.println("server:stop");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
