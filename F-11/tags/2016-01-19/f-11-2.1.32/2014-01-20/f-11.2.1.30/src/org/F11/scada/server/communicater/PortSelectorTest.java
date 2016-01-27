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
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
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
public class PortSelectorTest extends TestCase {
	static TcpServer server;

	/*
	 * テストケース単位の前処理、後処理
	 */
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite(PortSelectorTest.class);
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

	/**
	 * Constructor for PortSelectorTest.
	 * @param arg0
	 */
	public PortSelectorTest(String arg0) {
		super(arg0);
	}

	public void testDatagramChannel() throws Exception {
		PortSelector selector = new PortSelector();

		DatagramChannel channel1 = SelectorProvider.provider().openDatagramChannel();
		channel1.configureBlocking(false);
		channel1.socket().setBroadcast(true);
		channel1.socket().bind(
				new InetSocketAddress(InetAddress.getLocalHost(), 9601));
		UdpListener listener1 = new UdpListener(selector);
		listener1.channel = channel1;
		selector.addListener(listener1);

		DatagramChannel channel2 = SelectorProvider.provider().openDatagramChannel();
		channel2.configureBlocking(false);
		channel2.socket().setBroadcast(true);
		channel2.socket().bind(
				new InetSocketAddress(InetAddress.getLocalHost(), 9602));
		UdpListener listener2 = new UdpListener(selector);
		listener2.channel = channel2;
		selector.addListener(listener2);

		// listener1 から listener2 へ送信
		listener1.target = new InetSocketAddress(InetAddress.getLocalHost(),
				9602);
		listener1.senddata = new byte[]{1, 2, 3, 4, 5, 6};
		selector.setInterestOps(listener1, SelectionKey.OP_WRITE);
		Thread.sleep(100);
		assertEquals(0, listener1.readCnt);
		assertEquals(1, listener1.writeCnt);
		assertEquals(1, listener2.readCnt);
		assertEquals(0, listener2.writeCnt);
		assertEquals("010203040506", WifeUtilities.toString(listener2.recvdata));

		// listener2 から listener1 へ送信
		listener2.target = new InetSocketAddress(InetAddress.getLocalHost(),
				9601);
		listener2.senddata = new byte[]{6, 5, 4, 3, 2, 1};
		selector.setInterestOps(listener2, SelectionKey.OP_WRITE);
		Thread.sleep(100);
		assertEquals(1, listener1.readCnt);
		assertEquals(1, listener1.writeCnt);
		assertEquals(1, listener2.readCnt);
		assertEquals(1, listener2.writeCnt);
		assertEquals("060504030201", WifeUtilities.toString(listener1.recvdata));

		// listener1 から listener2（ブロードキャスト） へ送信
		listener1.target = new InetSocketAddress("255.255.255.255", 9602);
		listener1.senddata = new byte[]{1, 2, 3, 4, 5};
		selector.setInterestOps(listener1, SelectionKey.OP_WRITE);
		Thread.sleep(100);
		assertEquals(1, listener1.readCnt);
		assertEquals(2, listener1.writeCnt);
		assertEquals(2, listener2.readCnt);
		assertEquals(1, listener2.writeCnt);
		assertEquals("0102030405", WifeUtilities.toString(listener2.recvdata));

		// listener2 から listener2 へ送信
		listener2.target = new InetSocketAddress(InetAddress.getLocalHost(),
				9602);
		listener2.senddata = new byte[]{5, 4, 3, 2, 1};
		selector.setInterestOps(listener2, SelectionKey.OP_WRITE);
		Thread.sleep(100);
		assertEquals(1, listener1.readCnt);
		assertEquals(2, listener1.writeCnt);
		assertEquals(3, listener2.readCnt);
		assertEquals(2, listener2.writeCnt);
		assertEquals("0504030201", WifeUtilities.toString(listener2.recvdata));

		assertTrue(listener1.channel.isOpen());
		assertTrue(listener2.channel.isOpen());

		// close
		selector.removeListener(listener1);
		assertFalse(listener1.channel.isOpen());
		assertTrue(listener2.channel.isOpen());
		selector.removeListener(listener2);
		assertFalse(listener2.channel.isOpen());
		assertFalse(selector.isActive());

		assertEquals(0, listener1.acceptCnt);
		assertEquals(0, listener1.connectCnt);
		assertEquals(0, listener2.acceptCnt);
		assertEquals(0, listener2.connectCnt);
	}

	public void testSocketChannel() throws Exception {
		PortSelector selector = new PortSelector();

		TcpListener listener1 = new TcpListener(selector);
		selector.addListener(listener1);

		Thread.sleep(100);
		assertTrue(listener1.channel.isConnected());

		assertEquals(1, listener1.connectCnt);
		assertEquals(0, listener1.readCnt);
		assertEquals(0, listener1.writeCnt);

		// listener1 から サーバー へ送信
		listener1.senddata = new byte[]{1, 2, 3, 4, 5, 6};
		selector.setInterestOps(listener1, SelectionKey.OP_WRITE);
		Thread.sleep(100);
		assertEquals(1, listener1.writeCnt);
		assertEquals(1, listener1.readCnt);
		assertEquals("010203040506", WifeUtilities.toString(listener1.recvdata));

		// listener1 から サーバー へ送信
		listener1.senddata = new byte[]{6, 5, 4, 3, 2, 1};
		selector.setInterestOps(listener1, SelectionKey.OP_WRITE);
		Thread.sleep(100);
		assertEquals(2, listener1.writeCnt);
		assertEquals(2, listener1.readCnt);
		assertEquals("060504030201", WifeUtilities.toString(listener1.recvdata));

		assertTrue(listener1.channel.isOpen());

		// close
		selector.removeListener(listener1);
		assertFalse(listener1.channel.isOpen());
		assertFalse(listener1.channel.isConnected());
		assertFalse(selector.isActive());

		assertEquals(0, listener1.acceptCnt);
	}

	public void testReOpen() throws Exception {
		PortSelector selector = new PortSelector();

		InetSocketAddress local9600 = new InetSocketAddress(
				InetAddress.getLocalHost(), 9600);
		InetSocketAddress local9601 = new InetSocketAddress(
				InetAddress.getLocalHost(), 9601);
		UdpPortChannel port1 = new UdpPortChannel(local9600, selector);
		UdpPortChannel port2 = new UdpPortChannel(local9601, selector);

		selector.reopenChannel(port1);
		selector.reopenChannel(port2);
		selector.reopenChannel(port2);
		selector.reopenChannel(port1);

		selector.removeListener(port1);
		selector.removeListener(port2);
	}

	/*
	 * テスト用UDPポートリスナー
	 */
	private class UdpListener implements PortListener {
		private PortSelector selector;
		private DatagramChannel channel;
		private InetSocketAddress target;
		private byte[] senddata;
		private ByteBuffer recvbuffer = ByteBuffer.allocate(2048);

		private byte[] recvdata;
		private int acceptCnt = 0;
		private int connectCnt = 0;
		private int readCnt = 0;
		private int writeCnt = 0;

		private UdpListener(PortSelector selector) {
			this.selector = selector;
		}

		public void onAccept() throws IOException {
			acceptCnt++;
		}
		public void onConnect() throws IOException {
			connectCnt++;
		}
		public void onRead() throws IOException {
			recvbuffer.clear();
			channel.receive(recvbuffer);
			recvbuffer.flip();
			recvdata = new byte[recvbuffer.remaining()];
			recvbuffer.get(recvdata);
			readCnt++;
		}
		public synchronized void onWrite() throws IOException,
				InterruptedException {
			channel.send(ByteBuffer.wrap(senddata), target);
			senddata = null;
			target = null;
			writeCnt++;
			selector.resetInterestOps(this, SelectionKey.OP_WRITE);
		}

		public void close() throws IOException {
			channel.close();
		}

		// @see org.F11.scada.server.communicater.PortListener#open()
		public SelectableChannel open() throws IOException,
				InterruptedException {
			selector.setInterestOps(this, SelectionKey.OP_READ);
			return channel;
		}

	}

	/*
	 * テスト用TCPポートリスナー
	 */
	private class TcpListener implements PortListener {
		private PortSelector selector;
		private SocketChannel channel;
		private byte[] senddata;
		private ByteBuffer recvbuffer = ByteBuffer.allocate(2048);

		private byte[] recvdata;
		private int acceptCnt = 0;
		private int connectCnt = 0;
		private int readCnt = 0;
		private int writeCnt = 0;

		private TcpListener(PortSelector selector) {
			this.selector = selector;
		}

		public void onAccept() throws IOException {
			acceptCnt++;
		}
		public void onConnect() throws IOException, InterruptedException {
			if (channel.finishConnect()) {
				connectCnt++;
				selector.resetInterestOps(this, SelectionKey.OP_CONNECT);
				selector.setInterestOps(this, SelectionKey.OP_READ);
			}
		}
		public void onRead() throws IOException {
			recvbuffer.clear();
			if (0 < channel.read(recvbuffer)) {
				recvbuffer.flip();
				recvdata = new byte[recvbuffer.remaining()];
				recvbuffer.get(recvdata);
				readCnt++;
			}
		}
		public synchronized void onWrite() throws IOException,
				InterruptedException {
			channel.write(ByteBuffer.wrap(senddata));
			senddata = null;
			writeCnt++;
			selector.resetInterestOps(this, SelectionKey.OP_WRITE);
		}

		public void close() throws IOException {
			channel.socket().shutdownOutput();
			channel.close();
		}

		// @see org.F11.scada.server.communicater.PortListener#open()
		public SelectableChannel open() throws IOException,
				InterruptedException {
			channel = SelectorProvider.provider().openSocketChannel();
			channel.configureBlocking(false);
			channel.socket().setKeepAlive(true);

			channel.connect(new InetSocketAddress(InetAddress.getLocalHost(),
					5001));

			selector.setInterestOps(this, SelectionKey.OP_CONNECT);
			return channel;
		}

	}

	/*
	 * TCPサーバー
	 */
	static class TcpServer implements Runnable {
		private Thread thread;
		private Selector selector;
		private ServerSocketChannel serverSocketChannel;
		private final int port;

		private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

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

		private void sendBack(SocketChannel socketChannel) throws IOException {
			buffer.clear();
			if (socketChannel.read(buffer) < 0) {
				socketChannel.close();
				return;
			}
			buffer.flip();

			Thread.yield();
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
						keyIterator.remove();

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
				}
				System.out.println("server:stop");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
