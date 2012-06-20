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

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

/**
 * データ変更イベント値をデータベースに格納し、印刷設定件数以上なら印刷を開始するクラス
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintService extends AbstractPrintService {
	/** 1ページに印刷する行数 */
	private final int maxLine;
	/** ロギングAPI */
	private static Logger log = Logger.getLogger(AlarmPrintService.class);

	/**
	 * 警報印刷サービスを初期化します。データベースに未印刷のレコードが存在すれば、全て取得し内部データを初期化します。
	 * 
	 * @param printDAO 警報印刷データベースDAO
	 * @param printer プリンターオブジェクト
	 * @throws SQLException データベースエラー発生時
	 */
	public AlarmPrintService(AlarmPrintDAO printDAO, AlarmPrinter printer) {
		super(printDAO, printer);
		this.maxLine =
			Integer.parseInt(EnvironmentManager.get(
				"/server/alarm/print/pagelines",
				"10"));
		start();
		log.info("constracted AlarmPrintService.");
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
			if (printLineDatas == null) {
				// 印刷内容を初期化
				printLineDatas =
					new ArrayList<PrintLineData>(printDAO.findAll());
				print();
			}
			if (printDAO.isAlarmPrint(key)) {
				printDAO.insert(key);
				PrintLineData data = printDAO.find(key);
				printLineDatas.add(data);
				print();
			}
		} catch (SQLException e) {
			log.error("印刷中にDBエラーが発生しました。", e);
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
			if (maxLine <= printLineDatas.size()) {
				printer.print(printLineDatas);
				printDAO.deleteAll();
				printLineDatas.clear();
			}
		} finally {
			lock.unlock();
		}
	}
}
