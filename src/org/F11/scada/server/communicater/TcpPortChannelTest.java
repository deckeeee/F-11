/*
 * 作成日: 2005/01/11
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
import org.F11.scada.test.util.TestUtil;

/**
 * @author hori
 */
public class TcpPortChannelTest extends TestCase {
	static TcpServer server1;
	static TcpServer server2;

	private PortSelector selector;

	/*
	 * テストケース単位の前処理、後処理
	 */
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite(TcpPortChannelTest.class);
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
		server1 = new TcpServer(5001);
		server2 = new TcpServer(5002);
	}
	static void oneTimeTearDown() throws Exception {
		server2.end();
		server1.end();
	}

	/**
	 * Constructor for TcpPortChannelTest.
	 * @param arg0
	 */
	public TcpPortChannelTest(String arg0) {
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
		TcpPortChannel port =
			new TcpPortChannel(
				selector,
				new InetSocketAddress(InetAddress.getLocalHost(), 5000));

		assertEquals(
			"FINS_010203040506",
			port.makeIDKey(
				ByteBuffer.wrap(
					WifeUtilities.toByteArray("C00002010203040506"))));
		assertEquals(
			"FINS_060504030201",
			port.makeIDKey(
				ByteBuffer.wrap(
					WifeUtilities.toByteArray("C10007060504030201"))));
		assertEquals(
			"MC3E_00ffff0300",
			port.makeIDKey(
				ByteBuffer.wrap(WifeUtilities.toByteArray("D00000FFFF0300"))));
		assertEquals(
			"MC3E_01ffff0302",
			port.makeIDKey(
				ByteBuffer.wrap(WifeUtilities.toByteArray("D00001FFFF0302"))));
		assertNull(
			port.makeIDKey(ByteBuffer.wrap(WifeUtilities.toByteArray("0000"))));

		selector.removeListener(port);
		assertFalse(selector.isActive());
	}

	/*
	 * １ポート ２リスナー
	 */
	public void test002() throws Exception {
		TcpPortChannel port =
			new TcpPortChannel(
				selector,
				new InetSocketAddress(InetAddress.getLocalHost(), 5001));
		Listener listener1 = new Listener("c00002010203040506");
		port.addListener(listener1);
		Listener listener2 = new Listener("c00002060504030201");
		port.addListener(listener2);

		// -> listener1
		port.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5001),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c0000201020304050607080910111213")));
		Thread.sleep(20L);
		// -> listener2
		port.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5001),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002060504030201070809101112")));
		Thread.sleep(20L);
		// -> ???
		port.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5001),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002000102030405070809")));
		Thread.sleep(20L);
		// -> listener2
		port.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5001),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray(
					"c000020605040302010708091011121314")));
		Thread.sleep(20L);
		// -> ???
		port.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5001),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c000020001020304050708091011")));

		TestUtil.sleep(20L);

		assertFalse(port.removeListener(listener2));
		assertTrue(port.removeListener(listener1));

		selector.removeListener(port);
		assertFalse(selector.isActive());

		assertEquals(
			"c0000201020304050607080910111213",
			WifeUtilities.toString(listener1.resdata));
		assertEquals(1, listener1.recvcnt);

		assertEquals(
			"c000020605040302010708091011121314",
			WifeUtilities.toString(listener2.resdata));
		assertEquals(2, listener2.recvcnt);
	}

	/*
	 * ２ポート ２リスナー
	 */
	public void test003() throws Exception {
		TcpPortChannel port1 =
			new TcpPortChannel(
				selector,
				new InetSocketAddress(InetAddress.getLocalHost(), 5001));
		TcpPortChannel port2 =
			new TcpPortChannel(
				selector,
				new InetSocketAddress(InetAddress.getLocalHost(), 5002));

		Listener listener1 = new Listener("c00002010203040506");
		port1.addListener(listener1);
		Listener listener2 = new Listener("c00002060504030201");
		port2.addListener(listener2);

		// port1 -> port1.listener1
		port1.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5001),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c0000201020304050607080910111213")));
		// port2 -> port2.listener2
		port2.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5002),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002060504030201070809101112")));
		// port1 -> port1.???
		port1.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5001),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c00002000102030405070809")));
		// port2 -> port2.listener2
		port2.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5002),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray(
					"c000020605040302010708091011121314")));
		// port2 -> port2.???
		port2.sendRequest(
			new InetSocketAddress(InetAddress.getLocalHost(), 5002),
			ByteBuffer.wrap(
				WifeUtilities.toByteArray("c000020001020304050708091011")));

		TestUtil.sleep(1000L);

		assertTrue(port2.removeListener(listener2));
		assertTrue(port1.removeListener(listener1));

		assertEquals(1, listener1.recvcnt);
		assertEquals(
			"c0000201020304050607080910111213",
			WifeUtilities.toString(listener1.resdata));

		assertEquals(2, listener2.recvcnt);
		assertEquals(
			"c000020605040302010708091011121314",
			WifeUtilities.toString(listener2.resdata));

		selector.removeListener(port2);
		assertTrue(selector.isActive());
		selector.removeListener(port1);
		assertFalse(selector.isActive());
	}

	/*
	 * テスト用リスナークラス
	 */
	private static class Listener implements RecvListener {
		private final byte[] head;
		private byte[] resdata = null;
		private int recvcnt = 0;

		public Listener(String strhead) {
			head = WifeUtilities.toByteArray(strhead);
		}

		// @see org.F11.scada.server.communicater.RecvListener#getRecvHeader()
		public byte[] getRecvHeader() {
			return head;
		}

		// @see org.F11.scada.server.communicater.RecvListener#recvPerformed(java.nio.ByteBuffer)
		public void recvPerformed(ByteBuffer data) {
			resdata = new byte[data.remaining()];
			data.get(resdata);
			System.out.println("Listener:" + WifeUtilities.toString(resdata));
			recvcnt++;
		}

		// @see org.F11.scada.server.communicater.RecvListener#getRecvAddress()
		public InetSocketAddress getRecvAddress() {
			return null;
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

			serverSocketChannel =
				SelectorProvider.provider().openServerSocketChannel();

			serverSocketChannel.configureBlocking(false);

			InetSocketAddress address =
				new InetSocketAddress(InetAddress.getLocalHost(), port);
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
							ServerSocketChannel serverSocketChannel =
								(ServerSocketChannel) key.channel();
							accept(serverSocketChannel);
						} else if (key.isReadable()) {
							// read
							SocketChannel socketChannel =
								(SocketChannel) key.channel();
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
