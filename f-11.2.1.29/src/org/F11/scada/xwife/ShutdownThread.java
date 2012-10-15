package org.F11.scada.xwife;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

public class ShutdownThread implements Runnable {
	private final Logger logger = Logger.getLogger(ShutdownThread.class);
	private Thread thread;
	private ServerSocket serverSocket;
	private int port;

	public ShutdownThread(int port) {
		this.port = port;
		logger.info("終了コマンド受付ポート = " + port);
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	public void run() {
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(port);
			logger.info("終了コマンド要求待ち。 Port = " + serverSocket.getLocalPort());
			socket = serverSocket.accept();
			BufferedReader in =
				new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				logger.info("受信: " + line);
				if ("end".equalsIgnoreCase(line)) {
					logger.info("終了コマンドが実行されました。");
					System.exit(0);
				}
			}
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				logger.error("", e);
			}
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
