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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.scheduling.DailyIterator;
import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.Scheduler;
import org.F11.scada.scheduling.SchedulerTask;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

/**
 * データ変更イベント値をデータベースに格納し、日替わりで印刷を開始するクラス
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmDailyPrintService implements AlarmDataStore, Runnable,
		Service {
	/** 警報印刷データベースDAO */
	private final AlarmPrintDAO printDAO;
	/** 印刷データリスト */
	private List<PrintLineData> printLineDatas;
	/** イベントキュー */
	private final BlockingQueue<DataValueChangeEventKey> queue;
	/** スレッド */
	private Thread thread;
	/** プリンタークラス */
	private final AlarmPrinter printer;
	/** ロギングAPI */
	private static Logger log = Logger.getLogger(AlarmDailyPrintService.class);
	/** ロックオブジェクト */
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * 警報印刷サービスを初期化します。データベースに未印刷のレコードが存在すれば、全て取得し内部データを初期化します。
	 * 
	 * @param printDAO 警報印刷データベースDAO
	 * @param printer プリンターオブジェクト
	 * @throws SQLException データベースエラー発生時
	 */
	public AlarmDailyPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		this.printDAO = printDAO;
		queue = new LinkedBlockingQueue<DataValueChangeEventKey>();
		this.printer = printer;
		// 印刷内容を初期化
		try {
			printLineDatas = new ArrayList<PrintLineData>(printDAO.findAll());
		} catch (SQLException e) {
			log.error("警報メッセージ印刷初期化に失敗しました", e);
		}
		setScheduler();
		start();
		log.info("constracted AlarmDailyPrintService.");
	}

	private void setScheduler() {
		Schedule schedule =
			new Schedule(new DailyPrintTask(), getDailyIterator());
		Scheduler scheduler = new Scheduler();
		scheduler.schedule(schedule);
	}

	private DailyIterator getDailyIterator() {
		String time =
			EnvironmentManager.get("/server/alarm/print/printdate", "00:00:00");
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
		String clazz =
			EnvironmentManager.get("/server/alarm/print/className", "");
		try {
			Date d = f.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			if ("org.F11.scada.server.alarm.print.AlarmDailyPrintService"
				.equals(clazz)) {
				log.info("毎時" + time + "に警報メッセージを印刷します");
			}
			return new DailyIterator(cal.get(Calendar.HOUR_OF_DAY), cal
				.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
		} catch (ParseException e) {
			if ("org.F11.scada.server.alarm.print.AlarmDailyPrintService"
				.equals(clazz)) {
				log
					.warn("/server/alarm/print/printdateが不正です。00:00:00印刷で起動します。");
			}
			return new DailyIterator(0, 0, 0);
		}
	}

	/**
	 * データ変更イベント値を投入します。
	 * 
	 * @param key データ変更イベント値
	 */
	public void put(DataValueChangeEventKey key) {
		try {
			queue.put(key);
		} catch (InterruptedException e) {
			log.info("割り込みが発生しました", e);
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

	/**
	 * データ変更イベントをデータベースとリストに追加します このメソッドが public になっているのは、Acpect
	 * によるトランザクションを可能にする為です。
	 * 
	 * @param key データ変更イベント
	 */
	public void insertEvent(DataValueChangeEventKey key) {
		lock.lock();
		try {
			if (printDAO.isAlarmPrint(key)) {
				printDAO.insert(key);
				PrintLineData data = printDAO.find(key);
				printLineDatas.add(data);
			}
		} catch (SQLException e) {
			log.error("メッセージ印刷DB挿入でエラー発生", e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 印刷データリストが1ページに印刷する行数以上であれば印刷処理を開始する。 その後保持した印刷データリストとデータベースをクリアーする。
	 * 
	 * @exception SQLException データベースエラー発生時
	 */
	private void print() throws SQLException {
		lock.lock();
		try {
			printer.print(printLineDatas);
			printDAO.deleteAll();
			printLineDatas.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 毎設定時刻に印刷するタスク
	 * 
	 * @author maekawa
	 * 
	 */
	private class DailyPrintTask extends SchedulerTask {
		@Override
		public void run() {
			try {
				print();
			} catch (SQLException e) {
				log.error("メッセージ印刷中DBエラー発生", e);
			}
		}
	}
}
