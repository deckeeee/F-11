/*
 * =============================================================================
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

package org.F11.scada.server.io.nio;

import org.F11.scada.Service;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class LogTableStoreHandler implements Runnable, LoggingDataListener, Service {
	/** ロギングオブジェクト */
	private final Logger logger = Logger.getLogger(LogTableStoreHandler.class);
	/** 使用するデバイス名(テーブル名) */
	private String deviceName;
	/** スレッドオブジェクト */
	private Thread thread;
	/** イベントキュー */
	private BlockingQueue queue;
	/** ロギングサービス */
	private LogTableStoreService service;

	public void setService(LogTableStoreService service) {
		this.service = service;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setQueue(BlockingQueue queue) {
		this.queue = queue;
	}

	public synchronized void start() {
		if (null == thread) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
	}

	public synchronized void stop() {
		if (null != thread) {
			Thread ct = thread;
			thread = null;
			ct.interrupt();
		}
	}

	public void run() {
		Thread ct = Thread.currentThread();

		while (thread == ct) {
			try {
				LoggingDataEvent event = (LoggingDataEvent) queue.take();
				service.store(deviceName, event);
			} catch (InterruptedException e) {
				logger.info("take interrupted", e);
			}
		}
	}

	public void changeLoggingData(LoggingDataEvent event) {
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			logger.info("put interrupted", e);
		}
	}

}
