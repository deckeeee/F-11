/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.invoke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

public class TrendFileService implements InvokeHandler {
	private Logger logger = Logger.getLogger(TrendFileService.class);
	public static final String SAVE_OP =
		TrendFileService.class.getCanonicalName() + ".SAVE";
	public static final String READ_OP =
		TrendFileService.class.getCanonicalName() + ".READ";

	public Object invoke(Object[] args) {
		String op = (String) args[0];
		String file = (String) args[1];
		String xml = (String) args[2];
		try {
			return opration(op, file, xml);
		} catch (IOException e) {
			logger.error("XML�t�@�C�����o�͏����ŃG���[���������܂����B", e);
			throw new RuntimeException(e);
		}
	}

	private Object opration(String op, String file, String xml)
			throws IOException {
		if (SAVE_OP.equals(op)) {
			save(file, xml);
			return null;
		} else {
			return read(file);
		}
	}

	private void save(String file, String xml) throws IOException {
		BufferedWriter out = null;
		try {
			out =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					new File(file)), "UTF-8"));
			out.write(xml);
		} finally {
			if (null != out) {
				out.close();
			}
		}
	}

	private String read(String file) throws IOException {
		logger.info("file = " + file);
		BufferedReader xml = null;
		try {
			xml =
				new BufferedReader(new InputStreamReader(new FileInputStream(
					new File(file)), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (String s = xml.readLine(); s != null; s = xml.readLine()) {
				sb.append(s);
			}
			StringUtil util = new StringUtil();
			return util.replaceAllPointName(sb.toString());
		} finally {
			if (null != xml) {
				xml.close();
			}
		}
	}
}
