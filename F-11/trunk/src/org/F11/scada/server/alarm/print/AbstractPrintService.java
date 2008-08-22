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

package org.F11.scada.server.alarm.print;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

public abstract class AbstractPrintService implements AlarmDataStore, Runnable,
		Service {
	/** 警報印刷データベースDAO */
	protected final AlarmPrintDAO printDAO;
	/** 印刷データリスト */
	protected List<PrintLineData> printLineDatas;
	/** イベントキュー */
	protected final BlockingQueue<DataValueChangeEventKey> queue;
	/** スレッド */
	protected Thread thread;
	/** プリンタークラス */
	protected final AlarmPrinter printer;
	/** ロックオブジェクト */
	protected final ReentrantLock lock = new ReentrantLock();
	/** ロギングAPI */
	private static Logger log = Logger.getLogger(AbstractPrintService.class);

	protected AbstractPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		this.printDAO = printDAO;
		queue = new LinkedBlockingQueue<DataValueChangeEventKey>();
		this.printer = printer;
	}

	/**
	 * データ変更イベント値を投入します。
	 * 
	 * @param key データ変更イベント値
	 */
	public void put(DataValueChangeEventKey key) {
		try {
			if (isPrint()) {
				queue.put(key);
			}
		} catch (InterruptedException e) {
			log.info("割り込みが発生しました", e);
		}
	}

	private boolean isPrint() {
		String dataHolderID =
			EnvironmentManager.get("/server/alarm/print/enable", "");
		if (null == dataHolderID || "".equals(dataHolderID)) {
			log.info("印字設定ホルダが設定されていません。常に印字します");
			return true;
		} else {
			DataHolder hd = Manager.getInstance().findDataHolder(dataHolderID);
			if (hd == null) {
				log.info("印字設定ホルダが設定されていません。常に印字します");
				return true;
			} else {
				Object obj = hd.getValue();
				if (WifeDataDigital.class.isInstance(obj)) {
					WifeDataDigital d = (WifeDataDigital) obj;
					return d.isOnOff(true);
				} else {
					log.info("印字設定ホルダがデジタルタイプではありません。常に印字します");
					return true;
				}
			}
		}
	}

	/**
	 * キューよりデータ変更イベントを取り出し、印刷データリストに追加しデータベースに追加する。
	 */
	public void run() {
		Thread ct = Thread.currentThread();
		while (ct == thread) {
			try {
				insertEvent(queue.take());
			} catch (InterruptedException e) {
				log.info("割り込みが発生しました", e);
			}
		}
	}

	/**
	 * データ変更イベントをデータベースとリストに追加します このメソッドが public になっているのは、Acpect
	 * によるトランザクションを可能にする為です。
	 * 
	 * @param key データ変更イベント
	 */
	abstract public void insertEvent(DataValueChangeEventKey key);

	/**
	 * サーバースレッドを開始します。
	 */
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
	}

	/**
	 * サーバースレッドを停止します。
	 */
	public void stop() {
		if (thread != null) {
			Thread th = thread;
			thread = null;
			th.interrupt();
		}
	}
}
