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
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.lang.time.FastDateFormat;

/**
 * @author hori
 */
public class CsvoutTask extends AbstractCsvoutTask {
	private static final String TIMESTAMP_LABEL = "日付,時刻";
	private static final String TABLE_NOTFOUND_ERROR =
		"指定されたテーブルが事前に取得できません。JOINするテーブルは前方で定義してください。";
	private static final Format DATE_FORMAT =
		FastDateFormat.getInstance("yyyy/MM/dd,HH:mm:ss");
	/** ファイル出力開始時間の種別 true = 0:0～23:59:59 false = 0:0:1～0:0:0 */
	private boolean dataMode;
	/** プリントデータ更新クラス */
	private final AutoPrintDataService stor = new AutoPrintDataStore();
	/** 属性ヘッダ出力設定 */
	private final BitSet attributeSet;

	/**
	 *
	 * @param logg_name
	 * @param handlerManager
	 * @param schedule
	 * @param dataHolders
	 * @param currDir
	 * @param csv_head
	 * @param csv_mid
	 * @param csv_foot
	 * @param keepCount
	 * @param data_head
	 * @param dataMode
	 * @param midOffset
	 * @param tables
	 * @param attributeSet
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
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
			List<String> tables,
			BitSet attributeSet) throws NoSuchFieldException,
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
		this.attributeSet = attributeSet;
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
			Timestamp st = getStartTime();
			handlerManager.findRecord(logg_name, st);
			if (csvSchedule instanceof BMSSchedule) {
				startTime = bmsWrite(startTime, out);
			} else if (GODAMarker.class.isInstance(csvSchedule)) {
				startTime =
					nomalWrite(startTime, out, new SimpleDateFormat(
						"yyyy/MM/dd HH:mm"));
			} else if (dataMode) {
				startTime = nomalWrite(startTime, out, DATE_FORMAT);
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
		s.append(TIMESTAMP_LABEL);
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
			singleTableGodaHeader(list);
		} else {
			multiTableGodaHeader(list);
		}
	}

	private void singleTableGodaHeader(List<StringBuilder> list) {
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
	}

	private void multiTableGodaHeader(List<StringBuilder> list)
			throws RemoteException {
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
			singleTableHeaderWrite(list);
		} else {
			multiTableHeaderWrite(list);
		}
	}

	private void singleTableHeaderWrite(List<StringBuilder> list) {
		List<Map<String, String>> hl =
			stor.getLoggingHeddarList(logg_name, dataHolders);
		setHeaderString(list, hl, true);
	}

	private void multiTableHeaderWrite(List<StringBuilder> list)
			throws RemoteException {
		boolean isFirst = true;
		for (String table : tables) {
			List<Map<String, String>> hl =
				stor.getLoggingHeddarList(table, getHolder(table));
			setHeaderString(list, hl, isFirst);
			isFirst = false;
		}
	}

	private void setHeaderString(
			List<StringBuilder> list,
			List<Map<String, String>> hl,
			boolean isFirst) {
		getColumn(list, hl, isFirst, "unit", 0);
		getColumn(list, hl, isFirst, "name", 1);
		getColumn(list, hl, isFirst, "unit_mark", 2);
		if (attributeSet.get(0)) {
			getColumn(list, hl, isFirst, "attribute1", 3);
		}
		if (attributeSet.get(1)) {
			getColumn(list, hl, isFirst, "attribute2", attributeSet.get(0)
				? 4
				: 3);
		}
		if (attributeSet.get(2)) {
			getColumn(list, hl, isFirst, "attribute3", attributeSet.get(0)
				? (attributeSet.get(1) ? 5 : 4)
				: 3);
		}
	}

	private void getColumn(
			List<StringBuilder> list,
			List<Map<String, String>> hl,
			boolean isFirst,
			String column,
			int row) {
		StringBuilder s = new StringBuilder();
		if (isFirst) {
			s.append(TIMESTAMP_LABEL);
		}
		for (Map<String, String> rec : hl) {
			s.append(",\"");
			s.append(rec.get(column));
			s.append("\"");
		}
		addList(list, s, row, isFirst);
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
		if (tables.isEmpty()) {
			return singleTableAAndAWrite(startTime, out);
		} else {
			return multiTableAAndAWrite(startTime, out);
		}
	}

	private Timestamp singleTableAAndAWrite(
			Timestamp startTime,
			BufferedWriter out) throws RemoteException, IOException {
		List<StringBuilder> list = list();
		while (handlerManager.hasNext(logg_name)) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			Timestamp st = getStartTime();
			if (st.after(data.getTimestamp())) {
				continue;
			}
			StringBuilder b = new StringBuilder();
			b.append(DATE_FORMAT.format(data.getTimestamp()));
			data.first();
			writeStringBuilder(data, b);
			list.add(b);

			if (startTime == null) {
				startTime = data.getTimestamp();
			}
		}
		writeString(list, out);
		return startTime;
	}

	private Timestamp multiTableAAndAWrite(
			Timestamp startTime,
			BufferedWriter out) throws RemoteException, IOException {
		boolean isFirst = true;
		TreeMap<Timestamp, StringBuilder> map =
			new TreeMap<Timestamp, StringBuilder>();
		for (String table : tables) {
			Timestamp st = getStartTime();
			handlerManager.findRecord(table, st);
			while (handlerManager.hasNext(table)) {
				LoggingData data = (LoggingData) handlerManager.next(table);
				if (st.after(data.getTimestamp())) {
					continue;
				}
				StringBuilder b = new StringBuilder();
				if (isFirst) {
					b.append(DATE_FORMAT.format(data.getTimestamp()));
				}
				data.first();
				writeStringBuilder(table, data, b);
				addMap(map, data.getTimestamp(), b);

				if (startTime == null) {
					startTime = data.getTimestamp();
				}
			}
			isFirst = false;
			ThreadUtil.sleep(1000L);
		}
		writeMap(map, out);
		return startTime;
	}

	private Timestamp getStartTime() {
		return csvSchedule.startTime(System.currentTimeMillis(), dataMode);
	}

	private void addMap(
			TreeMap<Timestamp, StringBuilder> map,
			Timestamp timestamp,
			StringBuilder b) {
		if (map.containsKey(timestamp)) {
			StringBuilder sb = map.get(timestamp);
			sb.append(b);
		} else {
			map.put(timestamp, b);
		}
	}

	private void writeMap(
			TreeMap<Timestamp, StringBuilder> map,
			BufferedWriter out) throws IOException {
		for (StringBuilder b : map.values()) {
			out.write(b.toString());
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
	private Timestamp bmsWrite(Timestamp startTime, BufferedWriter out)
			throws IOException {
		if (tables.isEmpty()) {
			return singleTableBmsWrite(startTime, out);
		} else {
			return muiltiTableBmsWrite(startTime, out);
		}
	}

	private Timestamp singleTableBmsWrite(
			Timestamp startTime,
			BufferedWriter out) throws RemoteException, IOException {
		LoggingData data = (LoggingData) handlerManager.next(logg_name);
		out.write(DATE_FORMAT.format(data.getTimestamp()));
		data.first();
		ConvertValue[] convertValues =
			util.createConvertValue(dataHolders, logg_name);
		bmsDataWrite(out, data, convertValues);
		out.newLine();
		if (startTime == null) {
			startTime = data.getTimestamp();
		}
		return startTime;
	}

	private Timestamp muiltiTableBmsWrite(
			Timestamp startTime,
			BufferedWriter out) throws RemoteException, IOException {
		boolean isFirst = true;
		for (String table : tables) {
			Timestamp st = getStartTime();
			handlerManager.findRecord(table, st);
			LoggingData data = (LoggingData) handlerManager.next(table);
			if (isFirst) {
				out.write(DATE_FORMAT.format(data.getTimestamp()));
			}
			data.first();
			ConvertValue[] convertValues =
				util.createConvertValue(getHolder(table), table);
			bmsDataWrite(out, data, convertValues);
			if (startTime == null) {
				startTime = data.getTimestamp();
			}
			isFirst = false;
			ThreadUtil.sleep(1000L);
		}
		out.newLine();
		return startTime;
	}

	private void bmsDataWrite(
			BufferedWriter out,
			LoggingData data,
			ConvertValue[] convertValues) throws IOException {
		for (int i = 0; i < convertValues.length; i++) {
			ConvertValue conv = convertValues[i];
			double dd = data.next();
			out.write(',');
			out.write(conv.convertStringValue(conv.convertInputValue(dd)));
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
	private Timestamp nomalWrite(
			Timestamp startTime,
			BufferedWriter out,
			Format df) throws IOException {
		if (tables.isEmpty()) {
			return singleTableNomalWrite(startTime, out, df);
		} else {
			return multiTableNomalWrite(startTime, out, df);
		}
	}

	private Timestamp singleTableNomalWrite(
			Timestamp startTime,
			BufferedWriter out,
			Format df) throws RemoteException, IOException {
		List<StringBuilder> list = list();
		while (handlerManager.hasNext(logg_name)) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			if (isNomalWrite(data)) {
				StringBuilder b = new StringBuilder();
				b.append(df.format(data.getTimestamp()));
				data.first();
				writeStringBuilder(data, b);
				list.add(b);

				if (startTime == null) {
					startTime = data.getTimestamp();
				}
			}
		}
		writeString(list, out);
		return startTime;
	}

	private void writeStringBuilder(LoggingData data, StringBuilder b) {
		ConvertValue[] convertValues =
			util.createConvertValue(dataHolders, logg_name);
		writeStringBuilder(data, b, convertValues);
	}

	private void writeStringBuilder(
			LoggingData data,
			StringBuilder b,
			ConvertValue[] convertValues) {
		for (int i = 0; i < convertValues.length; i++) {
			ConvertValue conv = convertValues[i];
			double dd = data.next();
			b.append(',');
			b.append(conv.convertStringValue(conv.convertInputValue(dd)));
		}
	}

	private void writeStringBuilder(
			String table,
			LoggingData data,
			StringBuilder b) throws RemoteException {
		ConvertValue[] convertValues =
			util.createConvertValue(getHolder(table), table);
		writeStringBuilder(data, b, convertValues);
	}

	private Timestamp multiTableNomalWrite(
			Timestamp startTime,
			BufferedWriter out,
			Format df) throws RemoteException, IOException {
		boolean isFirst = true;
		TreeMap<Timestamp, StringBuilder> map =
			new TreeMap<Timestamp, StringBuilder>();
		for (String table : tables) {
			Timestamp st = getStartTime();
			handlerManager.findRecord(table, st);
			while (handlerManager.hasNext(table)) {
				LoggingData data = (LoggingData) handlerManager.next(table);
				if (isNomalWrite(data)) {
					StringBuilder b = new StringBuilder();
					if (isFirst) {
						b.append(df.format(data.getTimestamp()));
					}
					data.first();
					writeStringBuilder(table, data, b);
					addMap(map, data.getTimestamp(), b);

					if (startTime == null) {
						startTime = data.getTimestamp();
					}
				}
			}
			isFirst = false;
			ThreadUtil.sleep(1000L);
		}
		writeMap(map, out);
		return startTime;
	}

	private boolean isNomalWrite(LoggingData data) {
		Timestamp startTime = getStartTime();
		Timestamp endTime =
			csvSchedule.endTime(System.currentTimeMillis(), dataMode);
		return data.getTimestamp().equals(startTime)
			|| (data.getTimestamp().after(startTime) && data
				.getTimestamp()
				.before(endTime));
	}
}
