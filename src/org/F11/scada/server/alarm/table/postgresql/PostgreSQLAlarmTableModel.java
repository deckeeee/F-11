/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/postgresql/PostgreSQLAlarmTableModel.java,v 1.11.2.8 2006/09/11 06:19:35 frdm Exp $
 * $Revision: 1.11.2.8 $
 * $Date: 2006/09/11 06:19:35 $
 * 
 * =============================================================================
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
package org.F11.scada.server.alarm.table.postgresql;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.alarm.AlarmTableJournal;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.xwife.applet.alarm.event.CheckEvent;
import org.F11.scada.xwife.applet.alarm.event.CheckTable;
import org.F11.scada.xwife.applet.alarm.event.CheckTableListener;
import org.F11.scada.xwife.applet.alarm.event.CheckTableSupport;
import org.apache.log4j.Logger;

/**
 * DefaultTableModelによるAlarmTableModelの実装です。
 * 継承不能のクラスです。このクラスの機能を使用する場合は、継承ではなく委譲モデルを使用して下さい。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class PostgreSQLAlarmTableModel implements AlarmTableModel,
		Serializable {
	private static final long serialVersionUID = 2016684698797332527L;
	private static final int MAX_CLIENT = 10;

	/** テーブルモデルの保持最大行 */
	private static int MAX_ROW =
		Integer
			.parseInt(EnvironmentManager.get("/server/alarm/maxrow", "5000"));
	/** ジャーナルデータ保持最大行 */
	private static int MAX_JOURNAL = 5000;
	/** DefaultTableModelオブジェクト */
	private final DefaultTableModel model;
	/** ジャーナルデータのSortedMap */
	private final SortedMap journal;
	/** 行検索オブジェクトの参照 */
	private final Searcher searcher;
	/** 確認サポート */
	private final CheckTable checkTable;
	/** 確認イベントのジャーナル */
	private final SortedMap checkJournal;
	/** ロギング */
	private static final Logger logger =
		Logger.getLogger(PostgreSQLAlarmTableModel.class);
	private final Map titleMap;

	private PostgreSQLAlarmTableModel(
			DefaultTableModel model,
			Searcher searcher,
			Map titleMap) {
		if (model == null) {
			throw new IllegalArgumentException("model is null.");
		}

		this.model = model;
		this.journal = Collections.synchronizedSortedMap(new TreeMap());
		this.searcher = searcher;
		checkTable = new CheckTableSupport();
		this.checkJournal = Collections.synchronizedSortedMap(new TreeMap());
		this.titleMap = titleMap;
	}

	public static PostgreSQLAlarmTableModel createDefaultAlarmTableModel(
			DefaultTableModel model,
			Map titleMap) {
		return new PostgreSQLAlarmTableModel(
			model,
			new DefaultSearcher(model),
			titleMap);
	}

	public static PostgreSQLAlarmTableModel createHistoryAlarmTableModel(
			DefaultTableModel model,
			Map titleMap) {
		return new PostgreSQLAlarmTableModel(
			model,
			new HistorySearcher(model),
			titleMap);
	}

	public SortedMap getAlarmJournal(long t) {
		synchronized (journal) {
			return new TreeMap(journal.tailMap(new Long(t + 1)));
		}
	}

	/**
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void addTableModelListener(TableModelListener l) {
		model.addTableModelListener(l);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		return model.getColumnClass(columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return model.getColumnCount();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		return model.getColumnName(columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return model.getRowCount();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		return model.getValueAt(rowIndex, columnIndex);
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	/**
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener l) {
		model.removeTableModelListener(l);
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		model.setValueAt(aValue, rowIndex, columnIndex);
		model.fireTableRowsUpdated(rowIndex, rowIndex);
	}

	public void setValueAt(
			Object[] data,
			int rowIndex,
			int columnIndex,
			DataValueChangeEventKey key) {
		model.setValueAt(data[columnIndex], rowIndex, columnIndex);
		addJournal(AlarmTableJournal.createRowDataModifyOpe(key, data));
	}

	public void insertRow(int row, Object[] data, DataValueChangeEventKey key) {
		model.insertRow(row, data);
		trimTableModelRow();
		addJournal(AlarmTableJournal.createRowDataAddOpe(key, data));
	}

	public void removeRow(int row, DataValueChangeEventKey key) {
		Object[] removeRow = new Object[model.getColumnCount()];
		for (int col = 0, mc = model.getColumnCount(); col < mc; col++) {
			removeRow[col] = model.getValueAt(row, col);
		}
		model.removeRow(row);
		addJournal(AlarmTableJournal.createRowDataRemoveOpe(key, removeRow));
	}

	public void setJournal(AlarmTableJournal aj) {
		operationJournal(aj);
		addJournal(aj);
	}

	private void addJournal(AlarmTableJournal aj) {
		Long t = null;
		synchronized (journal) {
			for (long i = 0; i < Long.MAX_VALUE; i++) {
				t = new Long(aj.getTimestamp().getTime() + i);
				if (!journal.containsKey(t)) {
					break;
				}
			}
			journal.put(t, aj.setTimestamp(new Timestamp(t.longValue())));
			trimJournal();
		}
	}

	private void trimJournal() {
		for (int size = journal.size(), cnt = size - MAX_JOURNAL; cnt > 0; cnt--) {
			journal.remove(journal.firstKey());
		}
	}

	private void trimTableModelRow() {
		for (int size = model.getRowCount(), cnt = size - MAX_ROW; cnt > 0; cnt--) {
			model.removeRow(model.getRowCount() - 1);
		}
	}

	/**
	 * クライアントで使用するテーブルモデルのメソッド
	 * 
	 * @param value
	 */
	public void setValue(SortedMap value) {
		for (Iterator it = value.values().iterator(); it.hasNext();) {
			AlarmTableJournal jn = (AlarmTableJournal) it.next();
			operationJournal(jn);
		}
		synchronized (journal) {
			journal.putAll(value);
			trimJournal();
		}
		synchronized (model) {
			trimTableModelRow();
		}
	}

	private void operationJournal(AlarmTableJournal jn) {
		switch (jn.getOperationType()) {
		case AlarmTableJournal.INSERT_OPERATION:
			model.insertRow(0, jn.getData());
			break;
		case AlarmTableJournal.REMOVE_OPERATION:
			int row = searcher.searchRow(jn);
			if (row < 0) {
				logger.error("Row not found : jn=" + jn);
			} else {
				model.removeRow(row);
			}
			break;
		case AlarmTableJournal.MODIFY_OPERATION:
			row = searcher.searchRow(jn);
			if (row >= 0) {
				for (int i = 0, mc = model.getColumnCount(); i < mc; i++) {
					model.setValueAt(jn.getData()[i], row, i);
				}
			} else {
				// 更新する行がテーブルモデル内に存在しない。
				logger.error("Row not found : jn=" + jn);
			}
			break;
		}
	}

	public AlarmTableJournal getLastJournal() {
		synchronized (journal) {
			if (journal.size() > 0) {
				return (AlarmTableJournal) journal.get(journal.lastKey());
			} else {
				return null;
			}
		}
	}

	/**
	 * キーを含む最初の行を返します。
	 * 
	 * @param key データ変更イベント値キーオブジェクト
	 * @return int キーの行が存在した場合は、その行を返します。存在しない場合は負数(-1)を返します。
	 */
	public int searchRow(DataValueChangeEventKey key) {
		int retRow = -1;
		for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
			int po = ((Integer) model.getValueAt(row, 4)).intValue();
			String pro = (String) model.getValueAt(row, 5);
			String hol = (String) model.getValueAt(row, 6);
			if (key.getPoint() == po
				&& pro.equals(key.getProvider())
				&& hol.equals(key.getHolder())) {
				retRow = row;
			}
		}
		return retRow;
	}

	/**
	 * 指定した行にデータを挿入します。
	 * 
	 * @param row データを挿入する行
	 * @param data 挿入するデータの配列
	 */
	public void insertRow(int row, Object[] data) {
		model.insertRow(row, data);
	}

	public void addCheckTableListener(CheckTableListener listener) {
		checkTable.addCheckTableListener(listener);
	}

	public void removeCheckTableListener(CheckTableListener listener) {
		checkTable.removeCheckTableListener(listener);
	}

	public void fireCheckEvent(CheckEvent evt) {
		if (!isDuplicate(evt)) {
			checkTable.fireCheckEvent(evt);
			addCheckJournal(evt);
		}
	}

	private void addCheckJournal(CheckEvent evt) {
		Long t = null;
		synchronized (checkJournal) {
			for (long i = 0; i < Long.MAX_VALUE; i++) {
				t = new Long(evt.getTimestamp().getTime() + i);
				if (!checkJournal.containsKey(t)) {
					break;
				}
			}
			checkJournal.put(t, evt.setTimestamp(new Timestamp(t.longValue())));
			trimCheckJournal();
		}
	}

	private boolean isDuplicate(CheckEvent evt) {
		// 既に同じ確認イベントがあればボーキング
		TreeMap tempMap = null;
		synchronized (checkJournal) {
			tempMap =
				new TreeMap(checkJournal.tailMap(new Long(evt
					.getOnDate()
					.getTime() - 1L)));
		}
		for (Iterator i = tempMap.values().iterator(); i.hasNext();) {
			CheckEvent value = (CheckEvent) i.next();
			if (value.equalsKey(evt)) {
				return true;
			}
		}
		return false;
	}

	private void trimCheckJournal() {
		for (int size = checkJournal.size(), cnt = size - MAX_JOURNAL; cnt > 0; cnt--) {
			checkJournal.remove(checkJournal.firstKey());
		}
	}

	public SortedMap getCheckJournal(long t) {
		synchronized (checkJournal) {
			return new TreeMap(checkJournal.tailMap(new Long(t + 1L)));
		}
	}

	public CheckEvent getLastCheckEvent() {
		synchronized (checkJournal) {
			if (checkJournal.size() > 0) {
				return (CheckEvent) checkJournal.get(checkJournal.lastKey());
			} else {
				return null;
			}
		}
	}

	public Object getValueAt(int row, String columnName) {
		return model.getValueAt(row, getColumn(columnName));
	}

	public int getColumn(String columnName) {
		if (titleMap.containsKey(columnName)) {
			return ((Integer) titleMap.get(columnName)).intValue();
		} else {
			throw new IllegalArgumentException("列名 <" + columnName + "> はありません");
		}
	}

	public void removeRow(int row) {
		model.removeRow(row);
	}

	/**
	 * 行検索オブジェクトのインターフェイスです。
	 * 
	 * @author maekawa
	 * 
	 */
	static interface Searcher {
		public int searchRow(AlarmTableJournal jn);
	}

	/**
	 * 標準行検索クラスです。
	 * 
	 * @author maekawa
	 * 
	 */
	static class DefaultSearcher implements Searcher, Serializable {
		private static final long serialVersionUID = 8867454984946361662L;
		private final DefaultTableModel model;

		DefaultSearcher(DefaultTableModel model) {
			this.model = model;
		}

		public int searchRow(AlarmTableJournal jn) {
			int point = jn.getPoint();
			String provider = jn.getProvider();
			String holder = jn.getHolder();

			int retRow = -1;
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				Object obj = model.getValueAt(row, 4);
				if (obj == null || !(obj instanceof Integer)) {
					continue;
				}
				int po = ((Integer) obj).intValue();
				String pro = (String) model.getValueAt(row, 5);
				String hol = (String) model.getValueAt(row, 6);
				if (po == point && pro.equals(provider) && hol.equals(holder)) {
					retRow = row;
					break;
				}
			}
			return retRow;
		}
	}

	/**
	 * ヒストリ用の行検索クラスです。
	 * 
	 * @author maekawa
	 * 
	 */
	static class HistorySearcher implements Searcher, Serializable {
		private static final long serialVersionUID = -8186346906661550286L;
		private final DefaultTableModel model;

		HistorySearcher(DefaultTableModel model) {
			this.model = model;
		}

		public int searchRow(AlarmTableJournal jn) {
			int point = jn.getPoint();
			String provider = jn.getProvider();
			String holder = jn.getHolder();

			int retRow = -1;
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int tablePoint =
					((Integer) model.getValueAt(row, 4)).intValue();
				String tableProvider = (String) model.getValueAt(row, 5);
				String tableHolder = (String) model.getValueAt(row, 6);

				Timestamp tableTime = (Timestamp) model.getValueAt(row, 7);
				Timestamp time = (Timestamp) jn.getData()[7];

				if (tableTime == null) {
					tableTime = (Timestamp) model.getValueAt(row, 8);
					time = (Timestamp) jn.getData()[8];
				}

				if (tablePoint == point
					&& tableProvider.equals(provider)
					&& tableHolder.equals(holder)
					&& isEqualTime(tableTime, time)) {
					retRow = row;
					break;
				}
			}
			return retRow;
		}

		private boolean isEqualTime(Timestamp ts1, Timestamp ts2) {
			if (ts1 == null && ts2 == null) {
				return true;
			}

			if (ts1 != null && ts2 == null) {
				return false;
			}

			if (ts1 == null && ts2 != null) {
				return false;
			}

			return ts1.equals(ts2);
		}
	}
}
