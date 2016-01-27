package org.F11.scada.xwife.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.log4j.Logger;

public class EndCommand {
	private static final String IP = "127.0.0.1";
	private static final Logger LOGGER = Logger
		.getLogger(EndCommand.class);
	private static int port;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("引数に -server 又は -client を指定して下さい。");
			return;
		}
		if ("-server".equalsIgnoreCase(args[0])) {
			port = WifeMain.END_PORT;
		} else if ("-client".equalsIgnoreCase(args[0])) {
			port = AbstractWifeApplet.END_PORT;
		} else {
			System.out.println("引数に -server 又は -client を指定して下さい。");
			return;
		}

		Socket socket = null;
		try {
			socket = new Socket(IP, port);
			LOGGER.info("F-11に接続しました" + socket.getRemoteSocketAddress());
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("end");
		} catch (IOException e) {
			LOGGER.error("F-11が起動していません。");
			// LOGGER.error("", e);
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				LOGGER.error("", e);
			}
		}
	}
}
