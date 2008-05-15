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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.io.AutoPrintDataService;
import org.F11.scada.server.io.AutoPrintDataStore;
import org.F11.scada.server.io.ValueListHandler;
import org.F11.scada.server.logging.report.schedule.BMSSchedule;
import org.F11.scada.server.logging.report.schedule.GODAMarker;
import org.F11.scada.server.register.HolderString;

/**
 * @author hori
 */
public class CsvoutTask extends AbstractCsvoutTask {
	/** ファイル出力開始時間の種別 true = 0:0～23:59:59 false = 0:0:1～0:0:0 */
	private boolean dataMode;
	/** プリントデータ更新クラス */
	private final AutoPrintDataService stor = new AutoPrintDataStore();

	/**
	 * コンストラクタ
	 * 
	 * @param name ロギング名
	 * @param dataHolders データホルダーのリスト
	 * @param midOffset
	 * @param factoryName データ永続クラス名
	 * @exception SQLException DBMSに接続できなかったとき
	 */
	public CsvoutTask(
			String logg_name,
			ValueListHandler handlerManager,
			String schedule,
			List dataHolders,
			String currDir,
			String csv_head,
			String csv_mid,
			String csv_foot,
			int keepCount,
			boolean data_head,
			boolean dataMode,
			long midOffset) throws NoSuchFieldException, IllegalAccessException {
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
				midOffset);
		this.data_head = data_head;
		this.dataMode = dataMode;
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
			if (csvSchedule instanceof BMSSchedule) {
				startTime = bmsWrite(startTime, out);
			} else if (GODAMarker.class.isInstance(csvSchedule)) {
				startTime = nomalWrite(startTime, out, new SimpleDateFormat(
						"yyyy/MM/dd HH:mm"));
			} else if (dataMode) {
				startTime = nomalWrite(startTime, out, new SimpleDateFormat(
						"yyyy/MM/dd,HH:mm:ss"));
			} else {
				startTime = aAndAWrite(startTime, out);
			}
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
		if (csvSchedule instanceof BMSSchedule) {
			out.write("日付,時刻");
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				out.write(",");
				out.write(hs.getHolder());
			}
			out.newLine();
		} else if (GODAMarker.class.isInstance(csvSchedule)) {
			out.write("");
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				out.write(",");
				out.write(hs.getHolder());
			}
			out.newLine();
			List hl = stor.getLoggingHeddarList(logg_name, dataHolders);
			out.write("");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("name"));
			}
			out.newLine();
		} else if (data_head) {
			List hl = stor.getLoggingHeddarList(logg_name, dataHolders);
			out.write("日付,時刻");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",\"");
				out.write((String) rec.get("unit"));
				out.write("\"");
			}
			out.newLine();
			out.write("日付,時刻");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",\"");
				out.write((String) rec.get("name"));
				out.write("\"");
			}
			out.newLine();
			out.write("日付,時刻");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",\"");
				out.write((String) rec.get("unit_mark"));
				out.write("\"");
			}
			out.newLine();
		}
	}

	/**
	 * @param df
	 * @param startTime
	 * @param out
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private Timestamp aAndAWrite(Timestamp startTime, BufferedWriter out)
			throws IOException {
		Timestamp st = csvSchedule.startTime(
				System.currentTimeMillis(),
				dataMode);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");

		while (handlerManager.hasNext(logg_name)) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			if (st.after(data.getTimestamp()))
				continue;
			out.write(df.format(data.getTimestamp()));
			data.first();
			ConvertValue[] convertValues = util.createConvertValue(
					dataHolders,
					logg_name);

			for (int i = 0; i < convertValues.length; i++) {
				ConvertValue conv = convertValues[i];
				double dd = data.next();

				out.write(',');
				out.write(conv.convertStringValue(conv.convertInputValue(dd)));
			}
			out.newLine();

			if (startTime == null)
				startTime = data.getTimestamp();
		}
		return startTime;
	}

	/**
	 * @param df
	 * @param startTime
	 * @param out
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private Timestamp bmsWrite(Timestamp startTime, BufferedWriter out)
			throws IOException {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");

		LoggingData data = (LoggingData) handlerManager.next(logg_name);
		out.write(df.format(data.getTimestamp()));
		data.first();
		ConvertValue[] convertValues = util.createConvertValue(
				dataHolders,
				logg_name);
		for (int i = 0; i < convertValues.length; i++) {
			ConvertValue conv = convertValues[i];
			double dd = data.next();

			out.write(',');
			out.write(conv.convertStringValue(conv.convertInputValue(dd)));
		}
		out.newLine();

		if (startTime == null)
			startTime = data.getTimestamp();
		return startTime;
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
	private Timestamp nomalWrite(
			Timestamp startTime,
			BufferedWriter out,
			DateFormat df) throws IOException {
		Timestamp st = csvSchedule.startTime(
				System.currentTimeMillis(),
				dataMode);
		Timestamp endTime = csvSchedule.endTime(
				System.currentTimeMillis(),
				dataMode);
		logger.debug(st + "～" + endTime);
		while (handlerManager.hasNext(logg_name)) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			if (data.getTimestamp().equals(st)
					|| (data.getTimestamp().after(st) && data.getTimestamp()
							.before(endTime))) {
				out.write(df.format(data.getTimestamp()));
				data.first();
				ConvertValue[] convertValues = util.createConvertValue(
						dataHolders,
						logg_name);
				for (int i = 0; i < convertValues.length; i++) {
					ConvertValue conv = convertValues[i];
					double dd = data.next();
					out.write(',');
					out.write(conv.convertStringValue(conv
							.convertInputValue(dd)));
				}
				out.newLine();

				if (startTime == null)
					startTime = data.getTimestamp();
			}
		}
		return startTime;
	}
}
