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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * 指定されたパスにビット情報を出力するクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FileWriteCommand implements Command {
	/** パス名 */
	private String path;
	/** 書き込みエラーリトライ間隔 */
	private long errorRetryTime = 1000;
	/** 書き込みエラーリトライ回数 */
	private int errorRetryCount = 1;
	/** Logging API */
	private static Logger log = Logger.getLogger(FileWriteCommand.class);
	/** スレッドプール実行クラス */
	private static Executor executor = Executors.newCachedThreadPool();

	/**
	 * コマンドを実行します
	 * @param evt データ変更イベント
	 */
	public void execute(DataValueChangeEventKey evt) {
		if (path == null) {
			throw new IllegalStateException("path not setting.");
		}

		try {
			executor.execute(new FileWriteCommandTask(evt));
		} catch (RejectedExecutionException e) {}
	}

	/**
	 * パス名を設定します
	 * @param string パス名
	 */
	public void setPath(String string) {
		path = string;
	}

	/**
	 * 書き込みエラーリトライ回数を設定します
	 * @param i 書き込みエラーリトライ回数
	 */
	public void setErrorRetryCount(int i) {
		errorRetryCount = i;
	}

	/**
	 * 書き込みエラーリトライ間隔を設定します。
	 * @param i 書き込みエラーリトライ間隔
	 */
	public void setErrorRetryTime(int i) {
		errorRetryTime = i;
	}


	/**
	 * Executor で実行されるタスクのクラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class FileWriteCommandTask implements Runnable {
		/** データ変更イベントの参照 */
		private final DataValueChangeEventKey evt;

		/**
		 * タスクを初期化します
		 * @param evt データ変更イベント
		 */
		FileWriteCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor により実行されるメソッドです。
		 */
		public void run() {
			PrintWriter out = null;
			for (int i = 1; i <= errorRetryCount; i++) {
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
					if (evt.getValue().booleanValue()) {
						out.write("1");
					} else {
						out.write("0");
					}
					break;
				} catch (IOException e) {
					log.error(
						"書き込みエラーが発生しました。"
							+ errorRetryTime
							+ "ミリ秒後にリトライします。 ("
							+ i
							+ "/"
							+ errorRetryCount
							+ ")");
					sleep(errorRetryTime);
					continue;
				} finally {
					if (out != null) {
						out.close();
					}
				}
			}
		}
	
		private void sleep(long l) {
			try {
				Thread.sleep(l);
			} catch (InterruptedException e) {}
		}
	}
}
