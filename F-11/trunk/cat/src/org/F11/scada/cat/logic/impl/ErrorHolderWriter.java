/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.cat.logic.impl;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Formatter;

/**
 * エラーの発生したホルダーの状況をログファイルにTAGS形式で出力するヘルパークラスです。
 * 
 * @author maekawa
 *
 */
class ErrorHolderWriter {
	private static final int INCLUDE_DEFINE_LINE = 2;
	private final String logFormat;
	private final String logFormatInc;

	public ErrorHolderWriter(String logFormat, String logFormatInc) {
		this.logFormat = logFormat;
		this.logFormatInc = logFormatInc;
	}

	void writeBadholder(ArrayList<ErrorHolder> badHolders, Formatter out)
			throws IOException {
		for (ErrorHolder badHolder : badHolders) {
			LineNumberReader in = null;
			try {
				// ホルダがページ定義XMLに存在するか
				boolean isFind = false;
				in = new LineNumberReader(new FileReader(badHolder.getFile()));
				for (String line = in.readLine(); null != line; line =
					in.readLine()) {
					if (isFindHolder(badHolder, line)) {
						write(out, badHolder, in.getLineNumber(), false);
						isFind = true;
					}
				}
				// ページ定義XMLに無かったので、実体参照によるincファイル内にあるはず。
				if (!isFind) {
					write(out, badHolder, INCLUDE_DEFINE_LINE, true);
				}
			} finally {
				if (null != in) {
					in.close();
				}
			}
		}
	}

	private boolean isFindHolder(ErrorHolder badHolder, String line) {
		return line.indexOf(badHolder.getHolderString().getHolder()) > 0;
	}

	private void write(
			Formatter out,
			ErrorHolder badHolder,
			int lineno,
			boolean notInc) {
		out.format(
			getFormat(notInc),
			badHolder.getFile().getAbsolutePath(),
			lineno,
			badHolder.getHolderString().getProvider(),
			badHolder.getHolderString().getHolder());
	}

	private String getFormat(boolean notInc) {
		return notInc ? logFormatInc : logFormat;
	}
}
