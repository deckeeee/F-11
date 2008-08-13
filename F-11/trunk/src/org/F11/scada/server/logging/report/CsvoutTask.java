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

import static org.F11.scada.cat.util.CollectionUtil.list;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.io.AutoPrintDataService;
import org.F11.scada.server.io.AutoPrintDataStore;
import org.F11.scada.server.io.ValueListHandler;
import org.F11.scada.server.io.ValueListHandlerElement;
import org.F11.scada.server.logging.report.schedule.BMSSchedule;
import org.F11.scada.server.logging.report.schedule.GODAMarker;
import org.F11.scada.server.register.HolderString;

/**
 * @author hori
 */
public class CsvoutTask extends AbstractCsvoutTask {
	private static final String TABLE_NOTFOUND_ERROR =
		"指定されたテーブルが事前に取得できません。JOINするテーブルは前方で定義してください。";
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
			List<HolderString> dataHolders,
			String currDir,
			String csv_head,
			String csv_mid,
			String csv_foot,
			int keepCount,
			boolean data_head,
			boolean dataMode,
			long midOffset,
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
			out =
				new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					file), "Windows-31J"));
			dataHeadWrite(out);
			Timestamp st =
				csvSchedule.startTime(System.currentTimeMillis(), dataMode);
			handlerManager.findRecord(logg_name, st);
			if (csvSchedule instanceof BMSSchedule) {
				startTime = bmsWrite(startTime, out);
			} else if (GODAMarker.class.isInstance(csvSchedule)) {
				startTime =
					nomalWrite(startTime, out, new SimpleDateFormat(
						"yyyy/MM/dd HH:mm"));
			} else if (dataMode) {
				startTime =
					nomalWrite(startTime, out, new SimpleDateFormat(
						"yyyy/MM/dd,HH:mm:ss"));
			} else {
				startTime = aAndAWrite(startTime, out);
			}
			out.flush();
		} catch (IOException e) {
			logger.fatal("CSVファイル出力 IO エラー発生 : ", e);
		} catch (Exception e) {
			logger.fatal("CSVファイル出力 エラー発生 : ", e);
			JOptionPane.showMessageDialog(
				null,
				"CSVファイル出力 エラー発生",
				"CSVファイル出力 エラー",
				JOptionPane.ERROR_MESSAGE);
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
		List<StringBuilder> list = list();
		if (csvSchedule instanceof BMSSchedule) {
			bmsHeaderWrite(list);
		} else if (GODAMarker.class.isInstance(csvSchedule)) {
			godaHeaderWrite(list);
		} else if (data_head) {
			headerWrite(list);
		}
		writeString(list, out);
	}

	private void bmsHeaderWrite(List<StringBuilder> list)
			throws RemoteException {
		StringBuilder s = new StringBuilder();
		s.append("日付,時刻");
		if (tables.isEmpty()) {
			for (HolderString hs : dataHolders) {
				s.append(",");
				s.append(hs.getHolder());
			}
		} else {
			for (String table : tables) {
				List<HolderString> hslist =
					handlerManager
						.getValueListHandlerElement(table)
						.getHolders();
				for (HolderString hs : hslist) {
					s.append(",");
					s.append(hs.getHolder());
				}
			}
		}
		addList(list, s, 0, true);
	}

	private void addList(
			List<StringBuilder> list,
			StringBuilder s,
			int i,
			boolean first) {
		if (first) {
			list.add(s);
		} else {
			StringBuilder sb = list.get(i);
			sb.append(s);
		}
	}

	private void godaHeaderWrite(List<StringBuilder> list) throws IOException {
		if (tables.isEmpty()) {
			StringBuilder s = new StringBuilder();
			for (HolderString hs : dataHolders) {
				s.append(",");
				s.append(hs.getHolder());
			}
			addList(list, s, 0, true);
			List<Map<String, String>> hl =
				stor.getLoggingHeddarList(logg_name, dataHolders);
			s = new StringBuilder();
			for (Map<String, String> map : hl) {
				s.append(",");
				s.append(map.get("name"));
			}
			addList(list, s, 1, true);
		} else {
			boolean isFirst = true;
			for (String table : tables) {
				StringBuilder s = new StringBuilder();
				List<HolderString> hslist = getHolder(table);
				for (HolderString hs : hslist) {
					s.append(",");
					s.append(hs.getHolder());
				}
				addList(list, s, 0, isFirst);
				List<Map<String, String>> hl =
					stor.getLoggingHeddarList(table, getHolder(table));
				s = new StringBuilder();
				for (Map<String, String> map : hl) {
					s.append(",");
					s.append(map.get("name"));
				}
				addList(list, s, 1, isFirst);
				isFirst = false;
			}
		}
	}

	private List<HolderString> getHolder(String table) throws RemoteException {
		ValueListHandlerElement element =
			handlerManager.getValueListHandlerElement(table);
		if (null == element) {
			throw new IllegalArgumentException(TABLE_NOTFOUND_ERROR + table);
		} else {
			return element.getHolders();
		}
	}

	private void headerWrite(List<StringBuilder> list) throws IOException {
		if (tables.isEmpty()) {
			StringBuilder s = new StringBuilder();
			List<Map<String, String>> hl =
				stor.getLoggingHeddarList(logg_name, dataHolders);
			s.append("日付,時刻");
			for (Map<String, String> rec : hl) {
				s.append(",\"");
				s.append(rec.get("unit"));
				s.append("\"");
			}
			addList(list, s, 0, true);
			s = new StringBuilder();
			s.append("日付,時刻");
			for (Map<String, String> rec : hl) {
				s.append(",\"");
				s.append(rec.get("name"));
				s.append("\"");
			}
			addList(list, s, 1, true);
			s = new StringBuilder();
			s.append("日付,時刻");
			for (Map<String, String> rec : hl) {
				s.append(",\"");
				s.append(rec.get("unit_mark"));
				s.append("\"");
			}
			addList(list, s, 2, true);
		} else {
			boolean isFirst = true;
			for (String table : tables) {
				List<Map<String, String>> hl =
					stor.getLoggingHeddarList(table, getHolder(table));
				StringBuilder s = new StringBuilder();
				if (isFirst) {
					s.append("日付,時刻");
				}
				for (Map<String, String> rec : hl) {
					s.append(",\"");
					s.append(rec.get("unit"));
					s.append("\"");
				}
				addList(list, s, 0, isFirst);
				s = new StringBuilder();
				if (isFirst) {
					s.append("日付,時刻");
				}
				for (Map<String, String> rec : hl) {
					s.append(",\"");
					s.append(rec.get("name"));
					s.append("\"");
				}
				addList(list, s, 1, isFirst);
				s = new StringBuilder();
				if (isFirst) {
					s.append("日付,時刻");
				}
				for (Map<String, String> rec : hl) {
					s.append(",\"");
					s.append(rec.get("unit_mark"));
					s.append("\"");
				}
				addList(list, s, 2, isFirst);
				isFirst = false;
			}
		}
	}

	private void writeString(List<StringBuilder> list, BufferedWriter out)
			throws IOException {
		for (StringBuilder sb : list) {
			out.write(sb.toString());
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
		Timestamp st =
			csvSchedule.startTime(System.currentTimeMillis(), dataMode);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
		List<StringBuilder> list = list();
		if (tables.isEmpty()) {
			while (handlerManager.hasNext(logg_name)) {
				LoggingData data = (LoggingData) handlerManager.next(logg_name);
				if (st.after(data.getTimestamp())) {
					continue;
				}
				StringBuilder b = new StringBuilder();
				b.append(df.format(data.getTimestamp()));
				data.first();
				ConvertValue[] convertValues =
					util.createConvertValue(dataHolders, logg_name);

				for (int i = 0; i < convertValues.length; i++) {
					ConvertValue conv = convertValues[i];
					double dd = data.next();
					b.append(',');
					b.append(conv
						.convertStringValue(conv.convertInputValue(dd)));
				}
				list.add(b);

				if (startTime == null) {
					startTime = data.getTimestamp();
				}
			}
		} else {
			boolean isFirst = true;
			for (String table : tables) {
				handlerManager.findRecord(table, st);
				for (int recode = 0; handlerManager.hasNext(table);) {
					LoggingData data = (LoggingData) handlerManager.next(table);
					if (st.after(data.getTimestamp())) {
						continue;
					}
					StringBuilder b = new StringBuilder();
					if (isFirst) {
						b.append(df.format(data.getTimestamp()));
					}
					data.first();
					ConvertValue[] convertValues =
						util.createConvertValue(getHolder(table), table);

					for (int i = 0; i < convertValues.length; i++) {
						ConvertValue conv = convertValues[i];
						double dd = data.next();
						b.append(',');
						b.append(conv.convertStringValue(conv
							.convertInputValue(dd)));
					}
					addList(list, b, recode, isFirst);
					recode++;

					if (startTime == null) {
						startTime = data.getTimestamp();
					}
				}
				isFirst = false;
			}
		}
		writeString(list, out);
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
		if (tables.isEmpty()) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			out.write(df.format(data.getTimestamp()));
			data.first();
			ConvertValue[] convertValues =
				util.createConvertValue(dataHolders, logg_name);
			for (int i = 0; i < convertValues.length; i++) {
				ConvertValue conv = convertValues[i];
				double dd = data.next();
				out.write(',');
				out.write(conv.convertStringValue(conv.convertInputValue(dd)));
			}
			out.newLine();
			if (startTime == null) {
				startTime = data.getTimestamp();
			}
		} else {
			boolean isFirst = true;
			for (String table : tables) {
				Timestamp st =
					csvSchedule.startTime(System.currentTimeMillis(), dataMode);
				handlerManager.findRecord(table, st);
				LoggingData data = (LoggingData) handlerManager.next(table);
				if (isFirst) {
					out.write(df.format(data.getTimestamp()));
				}
				data.first();
				ConvertValue[] convertValues =
					util.createConvertValue(getHolder(table), table);
				for (int i = 0; i < convertValues.length; i++) {
					ConvertValue conv = convertValues[i];
					double dd = data.next();
					out.write(',');
					out.write(conv.convertStringValue(conv
						.convertInputValue(dd)));
				}
				if (startTime == null) {
					startTime = data.getTimestamp();
				}
				isFirst = false;
			}
			out.newLine();
		}
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
		List<StringBuilder> list = list();
		if (tables.isEmpty()) {
			while (handlerManager.hasNext(logg_name)) {
				LoggingData data = (LoggingData) handlerManager.next(logg_name);
				if (isNomalWrite(data)) {
					StringBuilder b = new StringBuilder();
					b.append(df.format(data.getTimestamp()));
					data.first();
					ConvertValue[] convertValues =
						util.createConvertValue(dataHolders, logg_name);
					for (int i = 0; i < convertValues.length; i++) {
						ConvertValue conv = convertValues[i];
						double dd = data.next();
						b.append(',');
						b.append(conv.convertStringValue(conv
							.convertInputValue(dd)));
					}
					list.add(b);

					if (startTime == null)
						startTime = data.getTimestamp();
				}
			}
		} else {
			boolean isFirst = true;
			for (String table : tables) {
				Timestamp st =
					csvSchedule.startTime(System.currentTimeMillis(), dataMode);
				handlerManager.findRecord(table, st);
				logger.info(table);
				for (int recode = 0; handlerManager.hasNext(table);) {
					LoggingData data = (LoggingData) handlerManager.next(table);
					if (isNomalWrite(data)) {
						StringBuilder b = new StringBuilder();
						if (isFirst) {
							b.append(df.format(data.getTimestamp()));
						}
						data.first();
						ConvertValue[] convertValues =
							util.createConvertValue(getHolder(table), table);
						for (int i = 0; i < convertValues.length; i++) {
							ConvertValue conv = convertValues[i];
							double dd = data.next();
							b.append(',');
							b.append(conv.convertStringValue(conv
								.convertInputValue(dd)));
						}
						addList(list, b, recode, isFirst);
						recode++;

						if (startTime == null) {
							startTime = data.getTimestamp();
						}
					}
				}
				isFirst = false;
			}
		}
		writeString(list, out);
		return startTime;
	}

	private boolean isNomalWrite(LoggingData data) {
		Timestamp startTime =
			csvSchedule.startTime(System.currentTimeMillis(), dataMode);
		Timestamp endTime =
			csvSchedule.endTime(System.currentTimeMillis(), dataMode);
		return data.getTimestamp().equals(startTime)
			|| (data.getTimestamp().after(startTime) && data
				.getTimestamp()
				.before(endTime));
	}
}
