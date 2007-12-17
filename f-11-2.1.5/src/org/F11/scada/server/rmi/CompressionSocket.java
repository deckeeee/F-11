/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2007 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class CompressionSocket extends Socket {
	private static final int BUFFER_SIZE = 40960;
	private InputStream in;
	private OutputStream out;

	public CompressionSocket() {
		super();
	}

	public CompressionSocket(String host, int port) throws IOException {
		super(host, port);
	}

	public InputStream getInputStream() throws IOException {
		if (in == null) {
			in = new CompressedBlockInputStream(super.getInputStream());
		}
		return in;
	}

	public OutputStream getOutputStream() throws IOException {
		if (out == null) {
			out = new CompressedBlockOutputStream(
					super.getOutputStream(),
					BUFFER_SIZE);
		}
		return out;
	}

	public synchronized void close() throws IOException {
		OutputStream o = getOutputStream();
		o.flush();
		super.close();
	}
}
