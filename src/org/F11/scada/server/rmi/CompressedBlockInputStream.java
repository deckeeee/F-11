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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class CompressedBlockInputStream extends FilterInputStream {
	/**
	 * Buffer of compressed data read from the stream
	 */
	private byte[] inBuf = null;

	/**
	 * Length of data in the input data
	 */
	private int inLength = 0;

	/**
	 * Buffer of uncompressed data
	 */
	private byte[] outBuf = null;

	/**
	 * Offset and length of uncompressed data
	 */
	private int outOffs = 0;
	private int outLength = 0;

	/**
	 * Inflater for decompressing
	 */
	private Inflater inflater = null;

	public CompressedBlockInputStream(InputStream is) throws IOException {
		super(is);
		inflater = new Inflater();
	}

	private void readAndDecompress() throws IOException {
		// Read the length of the compressed block
		int ch1 = in.read();
		int ch2 = in.read();
		int ch3 = in.read();
		int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		inLength = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));

		ch1 = in.read();
		ch2 = in.read();
		ch3 = in.read();
		ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0)
			throw new EOFException();
		outLength = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));

		// Make sure we've got enough space to read the block
		if ((inBuf == null) || (inLength > inBuf.length)) {
			inBuf = new byte[inLength];
		}

		if ((outBuf == null) || (outLength > outBuf.length)) {
			outBuf = new byte[outLength];
		}

		// Read until we're got the entire compressed buffer.
		// read(...) will not necessarily block until all
		// requested data has been read, so we loop until
		// we're done.
		int inOffs = 0;
		while (inOffs < inLength) {
			int inCount = in.read(inBuf, inOffs, inLength - inOffs);
			if (inCount == -1) {
				throw new EOFException();
			}
			inOffs += inCount;
		}

		inflater.setInput(inBuf, 0, inLength);
		try {
			inflater.inflate(outBuf);
		} catch (DataFormatException dfe) {
			throw new IOException("Data format exception - " + dfe.getMessage());
		}

		// Reset the inflator so we can re-use it for the
		// next block
		inflater.reset();

		outOffs = 0;
	}

	public int read() throws IOException {
		if (outOffs >= outLength) {
			try {
				readAndDecompress();
			} catch (EOFException eof) {
				return -1;
			}
		}

		return outBuf[outOffs++] & 0xff;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int count = 0;
		while (count < len) {
			if (outOffs >= outLength) {
				try {
					// If we've read at least one decompressed
					// byte and further decompression would
					// require blocking, return the count.
					if ((count > 0) && (in.available() == 0))
						return count;
					else
						readAndDecompress();
				} catch (EOFException eof) {
					if (count == 0)
						count = -1;
					return count;
				}
			}

			int toCopy = Math.min(outLength - outOffs, len - count);
			System.arraycopy(outBuf, outOffs, b, off + count, toCopy);
			outOffs += toCopy;
			count += toCopy;
		}

		return count;
	}

	public int available() throws IOException {
		// This isn't precise, but should be an adequate
		// lower bound on the actual amount of available data
		return (outLength - outOffs) + in.available();
	}

}