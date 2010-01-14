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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.converter.Converter;
import org.F11.scada.server.converter.FINS;
import org.F11.scada.server.event.WifeCommand;

/**
 * @author hori
 */
public class PlcCommunicaterTest extends TestCase {

	// テスト対象
	private PlcCommunicater commUdp;
	private PlcCommunicater commTcp;

	static UdpServer serverUdp;
	static TcpServer serverTcp;

	/*
	 * テストケース単位の前処理、後処理
	 */
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite(PlcCommunicaterTest.class);
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
		serverUdp = new UdpServer();
		serverTcp = new TcpServer(5001);
	}
	static void oneTimeTearDown() throws Exception {
		serverTcp.end();
		serverUdp.shutdown();
	}

	/**
	 * Constructor for PlcCommunicaterTest.
	 * @param arg0
	 */
	public PlcCommunicaterTest(String arg0) {
		super(arg0);
	}

	// @see junit.framework.TestCase#setUp()
	protected void setUp() throws Exception {
		super.setUp();

		System.out.println("setUp start");
		Environment device = new Environment() {
			public String getDeviceID() {
				return "P1";
			}
			public String getDeviceKind() {
				return "UDP";
			}
			public String getPlcIpAddress() {
				return "127.0.0.1";
			}
			public int getPlcPortNo() {
				return 9600;
			}
			public String getPlcCommKind() {
				return "FINS";
			}
			public int getPlcNetNo() {
				return 1;
			}
			public int getPlcNodeNo() {
				return 2;
			}
			public int getPlcUnitNo() {
				return 3;
			}
			public int getPlcWatchWait() {
				return 0;
			}
			public int getPlcTimeout() {
				return 1000;
			}
			public int getPlcRetryCount() {
				return 3;
			}
			public int getPlcRecoveryWait() {
				return 0;
			}
			public int getHostNetNo() {
				return 4;
			}
			public int getHostPortNo() {
				return 9601;
			}
			public String getHostIpAddress() {
				return "127.0.0.1";
			}
			public int getHostAddress() {
				return 5;
			}
		};
		Converter converter = new FINS();
		commUdp = new PlcCommunicater(device, converter);

		device = new Environment() {
			public String getDeviceID() {
				return "P2";
			}
			public String getDeviceKind() {
				return "TCP";
			}
			public String getPlcIpAddress() {
				return "127.0.0.1";
			}
			public int getPlcPortNo() {
				return 5001;
			}
			public String getPlcCommKind() {
				return "FINS";
			}
			public int getPlcNetNo() {
				return 1;
			}
			public int getPlcNodeNo() {
				return 2;
			}
			public int getPlcUnitNo() {
				return 3;
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
				return 4;
			}
			public int getHostPortNo() {
				return 0;
			}
			public String getHostIpAddress() {
				return "127.0.0.1";
			}
			public int getHostAddress() {
				return 5;
			}
		};
		commTcp = new PlcCommunicater(device, converter);
		System.out.println("setUp end");
	}

	// @see junit.framework.TestCase#tearDown()
	protected void tearDown() throws Exception {
		super.tearDown();

		System.out.println("tearDown start");
		commTcp.close();
		commUdp.close();
		System.out.println("tearDown end");
	}

	public void testSyncWriteUdp() throws Exception {
		WifeCommand comm = new WifeCommand("P1", 0, 0, 0, 0, 1);
		Map wcommands = new HashMap();
		wcommands.put(comm, WifeUtilities.toByteArray("1234"));
		commUdp.syncWrite(wcommands);
	}

	public void testSyncReadUdp() throws Exception {
		WifeCommand comm = new WifeCommand("P1", 0, 0, 0, 0, 1);
		List commands = new ArrayList();
		commands.add(comm);
		commUdp.addReadCommand(commands);
		Map response = commUdp.syncRead(commands);
		assertEquals(1, response.size());
		assertEquals(
			"1234",
			WifeUtilities.toString((byte[]) response.get(comm)));
	}

	public void testSyncReadUdp2() throws Exception {
		WifeCommand comm1 = new WifeCommand("P1", 0, 0, 0, 0, 4);
		WifeCommand comm2 = new WifeCommand("P1", 0, 0, 0, 1, 1);
		List commands = new ArrayList();

		commands.add(comm1);
		commands.add(comm2);
		commUdp.addReadCommand(commands);
		Map response = commUdp.syncRead(commands);
		assertEquals(2, response.size());
		assertEquals(
			"1234567887654321",
			WifeUtilities.toString((byte[]) response.get(comm1)));
		assertEquals(
			"5678",
			WifeUtilities.toString((byte[]) response.get(comm2)));

		WifeCommand comm3 = new WifeCommand("P1", 0, 0, 0, 2, 1);
		commands.clear();
		commands.add(comm3);
		commUdp.addReadCommand(commands);
		response = commUdp.syncRead(commands);
		assertEquals(1, response.size());
		assertEquals(
			"8765",
			WifeUtilities.toString((byte[]) response.get(comm3)));
	}

	public void testSyncErrUdp() throws Exception {
		WifeCommand comm = new WifeCommand("P1", 0, 0, 10, 0, 1);
		List commands = new ArrayList();
		commands.add(comm);
		commUdp.addReadCommand(commands);
		try {
			commUdp.syncRead(commands);
			fail();
		} catch (WifeException e) {
		}
	}

	public void testSyncWriteTcp() throws Exception {
		WifeCommand comm = new WifeCommand("P2", 0, 0, 0, 0, 1);
		Map wcommands = new HashMap();
		wcommands.put(comm, WifeUtilities.toByteArray("1234"));
		commTcp.syncWrite(wcommands);
	}

	public void testSyncReadTcp() throws Exception {
		WifeCommand comm = new WifeCommand("P2", 0, 0, 0, 0, 1);
		List commands = new ArrayList();
		commands.add(comm);
		commTcp.addReadCommand(commands);
		Map response = commTcp.syncRead(commands);
		assertEquals(1, response.size());
		assertEquals(
			"1234",
			WifeUtilities.toString((byte[]) response.get(comm)));
	}

	public void testSyncReadTcp2() throws Exception {
		WifeCommand comm1 = new WifeCommand("P2", 0, 0, 0, 0, 4);
		WifeCommand comm2 = new WifeCommand("P2", 0, 0, 0, 1, 1);
		List commands = new ArrayList();

		commands.add(comm1);
		commands.add(comm2);
		commTcp.addReadCommand(commands);
		Map response = commTcp.syncRead(commands);
		assertEquals(2, response.size());
		assertEquals(
			"1234567887654321",
			WifeUtilities.toString((byte[]) response.get(comm1)));
		assertEquals(
			"5678",
			WifeUtilities.toString((byte[]) response.get(comm2)));

		WifeCommand comm3 = new WifeCommand("P2", 0, 0, 0, 2, 1);
		commands.clear();
		commands.add(comm3);
		commTcp.addReadCommand(commands);
		response = commTcp.syncRead(commands);
		assertEquals(1, response.size());
		assertEquals(
			"8765",
			WifeUtilities.toString((byte[]) response.get(comm3)));
	}

	public void testSyncErrTcp() throws Exception {
		WifeCommand comm = new WifeCommand("P2", 0, 0, 10, 0, 1);
		List commands = new ArrayList();
		commands.add(comm);
		commTcp.addReadCommand(commands);
		try {
			commTcp.syncRead(commands);
			fail();
		} catch (WifeException e) {
		}
	}

	/*
	 * UDPレスポンス作成用サーバー
	 */
	static class UdpServer implements Runnable {
		private volatile Thread thread;
		private ByteBuffer recvBuffer = ByteBuffer.allocate(2048);
		private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);
		private Map resMap = new HashMap();

		public UdpServer() throws InterruptedException {
			resMap.put(
				"01028200000000011234",
				WifeUtilities.toByteArray("01020000"));
			resMap.put(
				"0101820000000001",
				WifeUtilities.toByteArray("010100001234"));
			resMap.put(
				"0101820000000004",
				WifeUtilities.toByteArray("010100001234567887654321"));
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
				channel.socket().bind(new InetSocketAddress("127.0.0.1", 9600));
				synchronized (this) {
					notifyAll();
				}
				while (thread == Thread.currentThread()) {
					SocketAddress target = null;
					recvBuffer.clear();
					try {
						target = channel.receive(recvBuffer);
						recvBuffer.flip();
						System.out.println(
							"server recv:"
								+ WifeUtilities.toString(recvBuffer));
					} catch (ClosedByInterruptException e) {
						break;
					}
					byte[] recvData = new byte[recvBuffer.remaining() - 10];
					recvBuffer.position(10);
					recvBuffer.get(recvData);
					byte[] sendData =
						(byte[]) resMap.get(WifeUtilities.toString(recvData));
					if (sendData != null) {
						Thread.yield();
						sendBuffer.clear();
						sendBuffer.put(WifeUtilities.toByteArray("c00002"));
						sendBuffer.put(recvBuffer.get(6));
						sendBuffer.put(recvBuffer.get(7));
						sendBuffer.put(recvBuffer.get(8));
						sendBuffer.put(recvBuffer.get(3));
						sendBuffer.put(recvBuffer.get(4));
						sendBuffer.put(recvBuffer.get(5));
						sendBuffer.put(recvBuffer.get(9));
						sendBuffer.put(sendData).flip();
						System.out.println(
							"server send:"
								+ WifeUtilities.toString(sendBuffer));
						channel.send(sendBuffer, target);
					}
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

	/*
	 * TCPサーバー
	 */
	static class TcpServer implements Runnable {
		private Thread thread;
		private Selector selector;
		private ServerSocketChannel serverSocketChannel;
		private final int port;
		private Map resMap = new HashMap();

		private ByteBuffer recvBuffer = ByteBuffer.allocateDirect(1024);
		private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);

		public TcpServer(int port) throws Exception {
			resMap.put(
				"01028200000000011234",
				WifeUtilities.toByteArray("01020000"));
			resMap.put(
				"0101820000000001",
				WifeUtilities.toByteArray("010100001234"));
			resMap.put(
				"0101820000000004",
				WifeUtilities.toByteArray("010100001234567887654321"));

			this.port = port;
			selector = SelectorProvider.provider().openSelector();

			serverSocketChannel =
				SelectorProvider.provider().openServerSocketChannel();

			serverSocketChannel.configureBlocking(false);

			InetSocketAddress address =
				new InetSocketAddress("127.0.0.1", port);
			serverSocketChannel.socket().bind(address);

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			thread = new Thread(this);
			thread.start();
		}
		public void end() {
			try {
				serverSocketChannel.close();
				selector.wakeup();
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

		private void sendBack(SocketChannel socketChannel)
			throws IOException, InterruptedException {
			recvBuffer.clear();
			if (socketChannel.read(recvBuffer) < 0) {
				socketChannel.close();
				return;
			}
			recvBuffer.flip();

			Thread.yield();

			byte[] recvData = new byte[recvBuffer.remaining() - 10];
			recvBuffer.position(10);
			recvBuffer.get(recvData);
			byte[] sendData =
				(byte[]) resMap.get(WifeUtilities.toString(recvData));
			if (sendData != null) {
				sendBuffer.clear();
				sendBuffer.put(WifeUtilities.toByteArray("c00002"));
				sendBuffer.put(recvBuffer.get(6));
				sendBuffer.put(recvBuffer.get(7));
				sendBuffer.put(recvBuffer.get(8));
				sendBuffer.put(recvBuffer.get(3));
				sendBuffer.put(recvBuffer.get(4));
				sendBuffer.put(recvBuffer.get(5));
				sendBuffer.put(recvBuffer.get(9));
				sendBuffer.put(sendData).flip();
				System.out.println(
					"server:" + WifeUtilities.toString(sendBuffer));
			} else {
				System.out.println("server:no resp");
			}

			socketChannel.write(sendBuffer);
		}
		// @see java.lang.Runnable#run()
		public void run() {
			try {
				System.out.println("server:start " + port);
				while (selector.select() > 0) {
					Iterator keyIterator = selector.selectedKeys().iterator();

					while (keyIterator.hasNext()) {
						SelectionKey key = (SelectionKey) keyIterator.next();
						keyIterator.remove();

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
				}
				System.out.println("server:stop");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					selector.close();
				} catch (Exception e) {
				}
			}
		}

	}

}
