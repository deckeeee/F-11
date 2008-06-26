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

package org.F11.scada.server.alarm.print;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.event.LoggingDataEventQueue;
import org.apache.log4j.Logger;

/**
 * データ変更イベント値をデータベースに格納し、印刷設定件数以上なら印刷を開始するクラス
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintService implements AlarmDataStore, Runnable, Service {
	/** 警報印刷データベースDAO */
	private final AlarmPrintDAO printDAO;
	/** 印刷データリスト */
	private List printLineDatas;
	/** 1ページに印刷する行数 */
	private final int maxLine;
	/** イベントキュー */
	private final LoggingDataEventQueue queue;
	/** スレッド */
	private Thread thread;
	/** プリンタークラス */
	private final AlarmPrinter printer;
	/** ロギングAPI */
	private static Logger log = Logger.getLogger(AlarmPrintService.class);

	/**
	 * 警報印刷サービスを初期化します。データベースに未印刷のレコードが存在すれば、全て取得し内部データを初期化します。
	 * @param printDAO 警報印刷データベースDAO
	 * @param printer プリンターオブジェクト
	 * @throws SQLException データベースエラー発生時
	 */
	public AlarmPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		this.printDAO = printDAO;
		this.maxLine =
			Integer.parseInt(
				EnvironmentManager.get("/server/alarm/print/pagelines", "10"));
		this.queue = new LoggingDataEventQueue();
		this.printer = printer;
		start();
		log.info("constracted AlarmPrintService.");
	}

	/**
	 * データ変更イベント値を投入します。
	 * @param key データ変更イベント値
	 */
	public void put(DataValueChangeEventKey key) {
		this.queue.enqueue(key);
	}

	/**
	 * キューよりデータ変更イベントを取り出し、印刷データリストに追加しデータベースに追加する。
	 */
	public void run() {
		Thread ct = Thread.currentThread();
		while (ct == this.thread) {
			insertEvent((DataValueChangeEventKey) this.queue.dequeue());
		}
	}

	/**
	 * サーバースレッドを開始します。
	 */
	public void start() {
	    if (thread == null) {
			this.thread = new Thread(this);
			this.thread.setName(getClass().getName());
			this.thread.start();
	    }
	}

	/**
	 * サーバースレッドを停止します。
	 */
	public void stop() {
	    if (thread != null) {
	        Thread th = thread;
			this.thread = null;
			th.interrupt();
	    }
	}

	/**
	 * データ変更イベントをデータベースとリストに追加します
	 * このメソッドが public になっているのは、Acpect によるトランザクションを可能にする為です。
	 * @param key データ変更イベント
	 */
	public void insertEvent(DataValueChangeEventKey key) {
		try {
			if (this.printLineDatas == null) {
				// 印刷内容を初期化
				this.printLineDatas =
						new ArrayList(this.printDAO.findAll());
				print();
			}
			if (this.printDAO.isAlarmPrint(key)) {
				this.printDAO.insert(key);
				PrintLineData data = this.printDAO.find(key);
				this.printLineDatas.add(data);
				print();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 印刷データリストが1ページに印刷する行数以上であれば印刷処理を開始する。
	 * その後保持した印刷データリストとデータベースをクリアーする。
	 * 
	 * @exception SQLException データベースエラー発生時
	 */
	private void print() throws SQLException {
		if (this.maxLine <= this.printLineDatas.size()) {
//			System.out.println(this.printLineDatas);
			this.printer.print(this.printLineDatas);
			this.printDAO.deleteAll();
			this.printLineDatas.clear();
//			System.out.println("Clear printLineDatas.");
		}
	}
}
