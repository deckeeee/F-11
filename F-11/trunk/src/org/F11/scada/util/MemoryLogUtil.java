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

package org.F11.scada.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * ヒープメモリの空き・使用容量を取得するクラスです。
 * 
 * @author maekawa
 * 
 */
public class MemoryLogUtil {
	private final static double TRANS_B_TO_M = 1024.0 * 1024.0;
	private static MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();

	/**
	 * 最大領域、確保領域、使用領域を返します。
	 * 
	 * @return 最大領域、確保領域、使用領域を返します。
	 */
	public static String getMemory() {
		MemoryUsage heapUsage = null;
		synchronized (mbean) {
			heapUsage = mbean.getHeapMemoryUsage();
		}
		double used = heapUsage.getUsed() / TRANS_B_TO_M;
		double max = heapUsage.getMax() / TRANS_B_TO_M;
		double commit = heapUsage.getCommitted() / TRANS_B_TO_M;
		return String.format(
			"max:%.2fM commit:%.2fM used:%.2fM",
			max,
			commit,
			used);
	}

	public static void main(String[] args) {
		System.out.println(MemoryLogUtil.getMemory());
	}
}
