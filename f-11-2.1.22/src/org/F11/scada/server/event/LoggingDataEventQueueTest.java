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
package org.F11.scada.server.event;

import java.sql.Timestamp;
import java.util.List;

import junit.framework.TestCase;

import org.F11.scada.test.util.TestUtil;



/**
 * ロギングデータ変更イベントキューのテストケースです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class LoggingDataEventQueueTest extends TestCase {
	List dataHolders;
	QueueTestThread thread;
	LoggingDataEvent event1;
	LoggingDataEvent event2;

	/**
	 * Constructor for LoggingDataEventQueueTest.
	 * @param arg0
	 */
	public LoggingDataEventQueueTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		dataHolders = java.util.Arrays.asList(TestUtil.createDataProvider().getDataHolders());
		thread = new QueueTestThread();
	}

	public void testEnqueue() {
		event1 =
			new LoggingDataEvent(
				this,
				new Timestamp(System.currentTimeMillis()),
				dataHolders);
		event2 =
			new LoggingDataEvent(
				this,
				new Timestamp(System.currentTimeMillis()),
				dataHolders);
				
		assertEquals(0, thread.getSize());
		thread.enqueue(event1);
		assertEquals(1, thread.getSize());
		thread.enqueue(event2);
		assertEquals(2, thread.getSize());

		try {
			Thread.sleep(600L);
		} catch (InterruptedException e) {
		}
		assertSame(thread.getEvent(), event1);
		assertEquals(1, thread.getSize());

		try {
			Thread.sleep(1500L);
		} catch (InterruptedException e) {
		}
		assertSame(thread.getEvent(), event2);
		assertEquals(0, thread.getSize());
	}


	
	private static class QueueTestThread implements Runnable {
		Thread thread;
		LoggingDataEventQueue queue = new LoggingDataEventQueue();
		LoggingDataEvent event;
		
		QueueTestThread() {
			thread = new Thread(this);
			thread.start();
		}
		
		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			for (int i = 0; i < 2; i++) {
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
				}
				event = (LoggingDataEvent) queue.dequeue();
			}
		}
		
		public void enqueue(LoggingDataEvent event) {
			queue.enqueue(event);
		}

		public LoggingDataEvent getEvent() {
			return event;
		}
		
		public int getSize() {
			return queue.size();
		}
	}

}
