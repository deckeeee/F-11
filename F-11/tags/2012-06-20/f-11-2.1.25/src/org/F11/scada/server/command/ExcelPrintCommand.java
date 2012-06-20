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

package org.F11.scada.server.command;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

/**
 * 指定されたパスにビット情報を出力するクラスです。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ExcelPrintCommand implements Command {
	/** 実行ファイルパス名 */
	private String path;
	/** 帳票ファイルの保存フォルダ */
	private String xlsFolder;
	/** ファイル名のフォーマット */
	private String fileFormat;
	/** Logging API */
	private static Logger log = Logger.getLogger(ExcelPrintCommand.class);
	/** スレッドプール実行クラス */
	private static Executor executor = Executors.newCachedThreadPool();

	/**
	 * コマンドを実行します
	 *
	 * @param evt データ変更イベント
	 */
	public void execute(DataValueChangeEventKey evt) {
		if (path == null) {
			throw new IllegalStateException("path not setting.");
		}

		try {
			executor.execute(new FileExecuteCommandTask(evt));
		} catch (RejectedExecutionException e) {
		}
	}

	/**
	 * パス名を設定します
	 *
	 * @param string パス名
	 */
	public void setPath(String string) {
		path = string;
	}

	public void setXlsFolder(String xlsFolder) {
		this.xlsFolder = xlsFolder;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	/**
	 * Executor で実行されるタスクのクラスです。
	 *
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class FileExecuteCommandTask implements Runnable {
		/** データ変更イベントの参照 */
		private final DataValueChangeEventKey evt;

		/**
		 * タスクを初期化します
		 *
		 * @param evt データ変更イベント
		 */
		FileExecuteCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor により実行されるメソッドです。
		 */
		public void run() {
			if (evt.getValue().booleanValue()) {
				Runtime run = Runtime.getRuntime();
				try {
					log.info(path + " を実行します");
					SimpleDateFormat sf = new SimpleDateFormat(fileFormat);
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(evt.getTimeStamp().getTime());
					cal.add(Calendar.DATE, -1);
					run.exec(path + " " + xlsFolder + sf.format(cal.getTime()));
				} catch (IOException e) {
					log.error(path + "の実行に失敗しました。", e);
				}
			}
		}
	}
}
