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
 */
package org.F11.scada.util;

import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class ThreadUtil {
	private static final Logger logger = Logger.getLogger(ThreadUtil.class);

	private ThreadUtil() {
	}

	/**
	 * 指定されたミリ秒スリープします。
	 * @param millis スリープするミリ秒
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 現在のメソッドのスタックトレースを表示します。
	 *
	 */
	public static void printSS() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			if (0 != i) {
				builder.append("\t");
			}
			builder.append(elements[i]).append("\n");
		}
		logger.info(builder.toString());
	}
}
