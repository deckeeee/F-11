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

import junit.framework.TestCase;

/**
 * @author hori
 */
public class ReadWriteLockTest extends TestCase {

	private ReadWriteLock lock = new ReadWriteLock();

	/**
	 * Constructor for ReadWriteLockTest.
	 * @param arg0
	 */
	public ReadWriteLockTest(String arg0) {
		super(arg0);
	}

	public void test001() throws Exception {
		lock.writeLock();
		Reader reader1 = new Reader();
		reader1.start();
		Reader reader2 = new Reader();
		reader2.start();
		// 読込みスレッドロック待ち
		while (!reader1.isAlive() || !reader2.isAlive()) {
			Thread.yield();
		}
		Thread.sleep(100);
		Writer writer1 = new Writer();
		writer1.start();
		// 書込みスレッドロック待ち
		while (!writer1.isAlive()) {
			Thread.yield();
		}
		Thread.sleep(100);
		Writer writer2 = new Writer();
		writer2.start();
		// 書込みスレッドロック待ち
		while (!writer2.isAlive()) {
			Thread.yield();
		}
		Thread.sleep(100);
		Writer writer3 = new Writer();
		writer3.start();
		// 書込みスレッドロック待ち
		while (!writer3.isAlive()) {
			Thread.yield();
		}
		Thread.sleep(100);

		System.out.println("main unlock");
		lock.writeUnlock();

		//writer3.interrupt();

		reader1.join();
		reader2.join();
		writer1.join();
		writer2.join();
		writer3.join();
		assertTrue(writer1.endtime < reader1.endtime);
		assertTrue(writer1.endtime < reader2.endtime);
		assertTrue(writer2.endtime < reader1.endtime);
		assertTrue(writer2.endtime < reader2.endtime);
		assertTrue(writer3.endtime < reader1.endtime);
		assertTrue(writer3.endtime < reader2.endtime);

		assertTrue(writer1.endtime < writer2.endtime);
		assertTrue(writer2.endtime < writer3.endtime);
	}

	/*
	 * テストスレッド 書込み
	 */
	private class Writer extends Thread {
		private long endtime;

		public void run() {
			try {
				lock.writeLock();
				System.out.println("write lock " + getName());
				sleep(100);
				endtime = System.currentTimeMillis();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.writeUnlock();
			}
		}
	}

	/*
	 * テストスレッド 読込み
	 */
	private class Reader extends Thread {
		private long endtime;

		public void run() {
			try {
				lock.readLock();
				System.out.println("read lock " + getName());
				sleep(100);
				endtime = System.currentTimeMillis();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.readUnlock();
			}
		}
	}
}
