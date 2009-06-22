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

package org.F11.scada.server.logging.report;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.ValueListHandler;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.logging.report.schedule.CsvSchedule;
import org.F11.scada.server.logging.report.schedule.CsvScheduleFactory;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * @author maekawa
 */
public abstract class AbstractCsvoutTask implements LoggingDataListener {
	/** ロギングテーブル名 */
	protected String logg_name;
	/** ハンドラ */
	protected ValueListHandler handlerManager;
	/** データホルダのリスト */
	protected List<HolderString> dataHolders;
	/** CSVファイルの保存ディレクトリ名 */
	protected String currDir;
	/** CSVファイル名の先頭文字 */
	protected String csv_head;
	/** CSVファイル名の日付フォーマット */
	protected String csv_mid;
	/** CSVファイル名の末尾文字 */
	protected String csv_foot;
	/** CSVファイルの保存件数 */
	protected int keepCount;
	/** レコードヘッダの付加フラグ */
	protected boolean data_head = true;
	/** レコード検索日付を算出するヘルパクラス */
	protected CsvSchedule csvSchedule;
	/** ファイル出力開始時間の種別 true = 0:0～23:59:59 false = 0:0:1～0:0:0 */
	protected boolean dataMode = true;
	/** アイテムデータ取得ユーティリティー */
	protected final ItemUtil util;
	/** ファイル名日時オフセット(ミリ秒) */
	protected final long midOffset;
	/** 結合するテーブル */
	protected final List<String> tables;

	/** ロギングAPI */
	protected static Logger logger = Logger.getLogger(AbstractCsvoutTask.class);

	/**
	 * コンストラクタ
	 * 
	 * @param name ロギング名
	 * @param dataHolders データホルダーのリスト
	 * @param midOffset
	 * @param factoryName データ永続クラス名
	 * @exception SQLException DBMSに接続できなかったとき
	 */
	public AbstractCsvoutTask(
			String logg_name,
			ValueListHandler handlerManager,
			String schedule,
			List<HolderString> dataHolders,
			String currDir,
			String csv_head,
			String csv_mid,
			String csv_foot,
			int keepCount,
			long midOffset,
			List<String> tables) throws NoSuchFieldException, IllegalAccessException {
		super();

		this.logg_name = logg_name;
		this.dataHolders = dataHolders;
		this.currDir = currDir;
		this.csv_head = csv_head;
		this.csv_mid = csv_mid;
		this.csv_foot = csv_foot;
		this.keepCount = keepCount;
		this.handlerManager = handlerManager;
		this.midOffset = midOffset;
		this.tables = tables;
		CsvScheduleFactory factory = new CsvScheduleFactory();
		csvSchedule = factory.getCsvSchedule(schedule);
		S2Container container = S2ContainerUtil.getS2Container();
		util = (ItemUtil) container.getComponent("itemutil");
	}

	public void changeLoggingData(LoggingDataEvent event) {
		if (csvSchedule.isOutput()) {
			File dir = new File(currDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 一時ファイル作成
			File tmpFile = new File(currDir + logg_name + csv_foot);
			Timestamp startTime = csvOut(tmpFile);
			if (startTime != null) {
				// ファイル名変更
				DateFormat fileDf = new SimpleDateFormat(csv_mid);
				File newFile = new File(currDir + csv_head
						+ fileDf.format(getMidTime(startTime)) + csv_foot);
				newFile.delete();
				tmpFile.renameTo(newFile);
			}
			// 旧ファイル削除
			FilenameFilter filter = new CsvFilter(csv_head, csv_foot);
			removeOldFile(dir.listFiles(filter), keepCount);
		}
	}

	private Timestamp getMidTime(Timestamp startTime) {
		return new Timestamp(startTime.getTime() - midOffset);
	}

	/**
	 * CSVファイル作成
	 * 
	 * @param file 作成するCSVファイル
	 * @return 先頭レコードの日付
	 */
	abstract protected Timestamp csvOut(File file);

	/**
	 * 指定件数を残し、編集日付の古い順にファイルを削除する
	 * 
	 * @param files ファイルの一覧
	 * @param cnt 残す件数
	 */
	protected void removeOldFile(File[] files, int cnt) {
		if (null == files || files.length <= cnt)
			return;

		ArrayList<File> fileList = new ArrayList<File>(files.length);
		for (int i = 0; i < files.length; i++) {
			fileList.add(files[i]);
		}
		while (cnt < fileList.size()) {
			long first = System.currentTimeMillis();
			File firstFile = null;
			for (Iterator<File> it = fileList.iterator(); it.hasNext();) {
				File file = it.next();
				if (file.lastModified() < first) {
					first = file.lastModified();
					firstFile = file;
				}
			}
			firstFile.delete();
			fileList.remove(firstFile);
		}
	}

	/**
	 * 先頭文字と末尾文字を指定するファイルフィルタークラス
	 * 
	 * @author hori
	 * 
	 */
	protected class CsvFilter implements FilenameFilter {
		private String head;
		private String foot;

		public CsvFilter(String head, String foot) {
			this.head = head;
			this.foot = foot;
		}

		public boolean accept(File dir, String name) {
			return name.startsWith(head) && name.endsWith(foot);
		}
	}
}
