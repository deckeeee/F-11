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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Formatter;
import java.util.List;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.io.ValueListHandler;

/**
 * @author maekawa
 */
public class FormatedCsvoutTask extends AbstractCsvoutTask {
	/** フォーマット定義ファイル */
	private final File formatFile;

	/**
	 * コンストラクタ
	 * 
	 * @param name ロギング名
	 * @param dataHolders データホルダーのリスト
	 * @param midOffset
	 * @param factoryName データ永続クラス名
	 * @exception SQLException DBMSに接続できなかったとき
	 */
	public FormatedCsvoutTask(
			String logg_name,
			ValueListHandler handlerManager,
			String schedule,
			List dataHolders,
			String currDir,
			String csv_head,
			String csv_mid,
			String csv_foot,
			int keepCount,
			long midOffset,
			File formatFile,
			List<String> tables) throws NoSuchFieldException,
			IllegalAccessException {
		super(
				logg_name,
				handlerManager,
				schedule,
				dataHolders,
				currDir,
				csv_head,
				csv_mid,
				csv_foot,
				keepCount,
				midOffset,
				tables);
		this.formatFile = formatFile;
	}

	/**
	 * CSVファイル作成
	 * 
	 * @param file 作成するCSVファイル
	 * @return 先頭レコードの日付
	 */
	protected Timestamp csvOut(File file) {
		logger.debug("csv out start!!");
		Timestamp startTime = null;
		BufferedWriter out = null;
		try {
			// csv 作成
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file),
					"Windows-31J"));
			dataHeadWrite(out);
			Timestamp st = csvSchedule.startTime(
					System.currentTimeMillis(),
					dataMode);
			handlerManager.findRecord(logg_name, st);
			startTime = nomalWrite(startTime, out);
			out.flush();
		} catch (IOException e) {
			logger.fatal("CSVファイル出力 IO エラー発生 : ", e);
		} catch (Exception e) {
			logger.fatal("CSVファイル出力 エラー発生 : ", e);
		} finally {
			try {
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				logger.fatal("CSVファイル出力 IO エラー発生 : ", e);
			}
		}
		return startTime;
	}

	/**
	 * @param out
	 * @throws IOException
	 */
	private void dataHeadWrite(BufferedWriter out) throws IOException {
		if (data_head) {
			LineNumberReader in = null;
			try {
				in = new LineNumberReader(new FileReader(formatFile));
				out.write(in.readLine());
				out.newLine();
			} finally {
				if (null != in) {
					in.close();
				}
			}
		}
	}

	/**
	 * @param df
	 * @param startTime
	 * @param out
	 * @param st
	 * @param endTime
	 * @return
	 * @throws IOException
	 */
	private Timestamp nomalWrite(Timestamp startTime, BufferedWriter out)
			throws IOException {
		Timestamp st = csvSchedule.startTime(
				System.currentTimeMillis(),
				dataMode);
		Timestamp endTime = csvSchedule.endTime(
				System.currentTimeMillis(),
				dataMode);
		logger.info(st + "～" + endTime);
		while (handlerManager.hasNext(logg_name)) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			if (data.getTimestamp().equals(st)
					|| (data.getTimestamp().after(st) && data.getTimestamp()
							.before(endTime))) {
				data.first();
				ConvertValue[] convertValues = util.createConvertValue(
						dataHolders,
						logg_name);
				LineNumberReader in = new LineNumberReader(new FileReader(
						formatFile));
				in.readLine();
				Formatter formatter = new Formatter(out);
				for (ConvertValue conv : convertValues) {
					double dd = data.next();
					String formatStr = in.readLine();
					String convertStringValue = conv.convertStringValue(conv
							.convertInputValue(dd));
					formatter.format(formatStr, convertStringValue);
				}
				if (startTime == null)
					startTime = data.getTimestamp();
			}
		}
		return startTime;
	}
}
