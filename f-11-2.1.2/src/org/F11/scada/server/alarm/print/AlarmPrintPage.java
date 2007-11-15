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
package org.F11.scada.server.alarm.print;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.io.AlarmPrintDataStore;
import org.apache.log4j.Logger;

/**
 * 印刷実行日時より印刷データを判定する警報印刷サービスクラスです。
 * 
 * 再起動時に未印刷警報を印刷できない等、不具合があるのでこのクラスは使用
 * しないで下さい
 * @author hori
 */
public class AlarmPrintPage implements AlarmDataStore, Runnable, Service {
	private static Logger logger;
	private Thread thread;
	private final AlarmPrintDataStore dataStore;

	private boolean requestFlag = false;
	private final AlarmPrinter printer;

	/**
	 * 
	 */
	public AlarmPrintPage() throws IOException {
		logger = Logger.getLogger(getClass());
		dataStore = new AlarmPrintDataStore();
		this.printer = new AlarmPrinterImpl();
		start();
		logger.info("constracted AlarmPrintPage.");
	}

	/* (non-Javadoc)
	 * @see org.F11.scada.server.alarm.AlarmDataStore#put(org.F11.scada.server.alarm.DataValueChangeEventKey)
	 */
	public void put(DataValueChangeEventKey key) {
		synchronized (this) {
			requestFlag = true;
			notifyAll();
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		int max =
			Integer.parseInt(
				EnvironmentManager.get("/server/alarm/print/pagelines", "10"));
		String printService =
			EnvironmentManager.get("/server/alarm/print/printservice", "");
		List printWaitData = new ArrayList();
		while (thread == Thread.currentThread()) {
			try {
				synchronized (this) {
					while (!requestFlag) {
						wait(10000);
					}
					requestFlag = false;
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
			if (0 < printService.length()) {
				Timestamp lastTime = dataStore.getLastPrint();
				if (0 < dataStore.getPrintCount(lastTime)) {
					/* 未印字データ取得 */
					printWaitData.addAll(dataStore.getPrintList(lastTime));
					/* 取得済みデータ時刻保存 */
					PrintLineData pd =
						(PrintLineData) printWaitData.get(printWaitData.size() - 1);
					dataStore.setLastPrint(pd.getEntryDate());
				}
				/* 未印字件数取得 */
				if (max <= printWaitData.size()) {
					requestFlag = true;
					/* 印字 */
					List data = printWaitData.subList(0, max);
					this.printer.print(data);
					/* 印字済み削除 */
					printWaitData.subList(0, max).clear();
				}
			} else {
				/* 取得済みデータ時刻保存 */
				dataStore.setLastPrint(
					new Timestamp(System.currentTimeMillis()));
			}
		}
	}

	
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.setName(getClass().getName());
            thread.start();
        }
    }

	public void stop() {
		Thread th = thread;
		thread = null;
		th.interrupt();
	}

}
