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

package org.F11.scada.server.demand;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.table.AbstractTableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmComparator;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.dao.DatabaseMetaDataUtil;
import org.F11.scada.server.event.LoggingDataEventQueue;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

/**
 * デマンド監視データのリファレンサークラスです。
 */
public class DemandDataReferencer extends AbstractTableModel 
		implements DataReferencerOwner, DataValueChangeListener, AlarmReferencer {
	private static final long serialVersionUID = -7759325174060617741L;
	/** 過去データの Map オブジェクトのパラメタキーです。 */
	public static final String GRAPH_DATA = "org.F11.scada.server.demand.graphdata";
	/** 過去データを退避しているテーブル名のパラメタキーです。 */
	public static final String TABLE_NAME = "org.F11.scada.server.demand.tablename";
	/** このカウンター値に関連している、現在値データホルダ名のパラメタキーです。 */
	public static final String HOLDER_NAME = "org.F11.scada.server.demand.holdername";

	private static final Class[][] TYPE_INFO = {
		{DataHolder.class, Number.class, Map.class}
	};
	private SortedSet referencers;
	private EventUpdater updater;
	private static Logger logger;
	private volatile boolean isCreateTableModel;
	private List rowList;
	private String[] title = {"ポイント", "プロバイダ名", "ホルダ名", "値"};

	/**
	 * コンストラクタ
	 */
	public DemandDataReferencer() {
		logger = Logger.getLogger(getClass().getName());
		updater = new EventUpdater();
	}

	/**
	 * カウンタ値のデータ変更イベントより、デマンドデータを更新します。
	 * @param evt データ変更イベント @see DataValueChangeEvent
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		// ホルダの LinkedHashMap にグラフデータを生成し、DB のグラフデータを更新。
		updater.enQueue(evt);
	}

	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return TYPE_INFO;
	}

	public void addReferencer(DataReferencer dr) {
	    if (referencers == null) {
			referencers = new TreeSet(new AlarmComparator());
	    }
	    dr.connect(this);
	    referencers.add(dr);
	    isCreateTableModel = true;
	}

	public void removeReferencer(DataReferencer dr) {
		if (referencers != null) {
			referencers.remove(dr);
			dr.disconnect(this);
		}
	}

	/**
	 * リファレンサーの配列を返します。
	 * @return あればリファレンサーの配列、無ければ 0 長のリファレンサーの配列
	 */
	public SortedSet getReferencers() {
		if (referencers == null) {
			return new TreeSet(Collections.EMPTY_SET);
		}
		return Collections.unmodifiableSortedSet(referencers);
	}

    public boolean addDataStore(AlarmDataStore store) {
        throw new UnsupportedOperationException("addDataStore");
    }

	public int getColumnCount() {
		return title.length;
	}

	public Object getValueAt(int row, int col) {
		if (referencers == null) {
			return null;
		}
		if (isCreateTableModel) {
		    rowList = new ArrayList(referencers);
		    isCreateTableModel = false;
		}
		DataReferencer dr = (DataReferencer) rowList.get(row);
		switch (col) {
			case 0:
				if (dr.getDataHolder() != null) {
					return dr.getDataHolder().getParameter("point");
				}
			case 1:
				return dr.getDataProviderName();
			case 2:
				return dr.getDataHolderName();
			case 3:
				if (dr.getDataHolder() != null) {
					Object o = dr.getDataHolder().getValue();
					if (o instanceof WifeDataDigital) {
						if (!((WifeDataDigital)o).toString().equals("false")) {
							return "ON";
						} else {
							return "OFF";
						}
					} else {
						System.out.println("hoder null");
					}
				} else {
					System.out.println("hoder null");
				}
			default:
				return "エラー";
		}
	}

	public int getRowCount() {
		if (referencers == null) {
			return 0;
		}
		return referencers.size();
	}

	public String getColumnName(int col) {
		return title[col];
	}

	/**
	 * イベント更新の実装クラスです。
	 */
	private static class EventUpdater implements Runnable {
		private boolean isFoundTable;
		private Thread thread;
		private LoggingDataEventQueue queue;

		/**
		 * コンストラクタ
		 * @param con コネクションの参照
		 */
		EventUpdater() {
			queue = new LoggingDataEventQueue();
			thread = new Thread(this);
			thread.start();
		}
		
		/**
		 * キューに入れられたイベントを取り出し処理します。
		 */
		public void run() {
			Thread ct = Thread.currentThread();

			while (thread == ct) {
				DataValueChangeEvent evt = (DataValueChangeEvent) queue.dequeue();
				try {
					updateEvent(evt);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * データ変更イベントをキューに入れます。
		 * @param evt データ変更イベント
		 */
		public void enQueue(DataValueChangeEvent evt) {
			queue.enqueue(evt);
		}

		/**
		 * イベントデータを元に、各デマンドデータを更新します。
		 * @param evt データ変更イベント
		 * @throws SQLException SQL エラーが発生した場合にスローします。
		 */
		public void updateEvent(DataValueChangeEvent evt) throws SQLException {
			Object o = evt.getSource();
			DataHolder dh = (DataHolder) o;

			if (dh.getQualityFlag() == WifeQualityFlag.INITIAL) {
				return;
			}

			WifeDataAnalog wacounter = (WifeDataAnalog) dh.getValue();
			Integer counter = new Integer(wacounter.intValue());

			String holderName = (String) dh.getParameter(HOLDER_NAME);
			String tableName = (String) dh.getParameter(TABLE_NAME);

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
			}

			DataHolder valueHolder = dh.getDataProvider().getDataHolder(holderName);
			WifeDataAnalog wa = (WifeDataAnalog) valueHolder.getValue();
			ConvertValue convertValue =
				(ConvertValue) valueHolder.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
			Double value = new Double(wa.doubleValue());

			Connection con = null;
			try {
				con = ConnectionUtil.getConnection();
				checkTableName(tableName, con);

				// ハッシュマップを更新
				Map map = (Map) dh.getParameter(GRAPH_DATA);
				updateHashMap(map, tableName, counter, con, convertValue);
				map.put(
					counter,
					new Double(
						convertValue.convertDoubleValue(wa.doubleValue())));
				if (logger.isDebugEnabled()) {
					logger.debug(map);
				}

				// データベースを更新
				updateEvent(tableName, counter, value, con);
				con.close();
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}

		private void updateHashMap(
				Map map,
				String tableName,
				Integer key,
				Connection con,
				ConvertValue convertValue)
				throws SQLException {

			// 一つ前のデータがMap内に存在するかチェック
			int intKey = key.intValue() <= 0 ? 0 : key.intValue() - 1;
			Object o = map.get(new Integer(intKey));
			//なければカウント値0～一つ前のカウントまでをDBより構築する。
			if (o == null) {
				Statement stmt = null;
				ResultSet rs = null;
				try {
					stmt = con.createStatement();
					rs =
						stmt.executeQuery(
							SQLUtilities.getSelectString(
								tableName,
								key,
								"counter"));
					while (rs.next()) {
						map.put(
							new Integer(rs.getInt("counter")),
							new Double(convertDouble(convertValue, rs.getInt("value"))));
					}
					rs.close();
					stmt.close();
					rs = null;
					stmt = null;
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							rs = null;
						}
					}
					if (stmt != null) {
						try {
							stmt.close();
						} catch (SQLException e) {
							stmt = null;
						}
					}
				}
			}
		}
		
		private double convertDouble(ConvertValue convertValue, int value) {
			return convertValue.convertDoubleValue(new Double(value).doubleValue());
		}

		/**
		 * 引数のテーブルが存在するか検索します。もし、存在しなければテーブルを作成します。
		 * @param name テーブル名
		 * @exception SQLException データベースエラーが発生した場合
		 */
		private void checkTableName(String name, Connection con) throws SQLException {
			if (isFoundTable) {
				return;
			}

			ResultSet rs = null;
			DatabaseMetaData metaData = con.getMetaData();
			try {
				rs = DatabaseMetaDataUtil.getTables(metaData, "", "", name, null);
				// テーブルが存在するか調査
				rs.last();
				if (rs.getRow() <= 0) {
					logger.info("TRY TABLE CREATE!! : " + name);
					Statement stmt = null;
					try {
						stmt = con.createStatement();
						stmt.executeUpdate(SQLUtilities.getCreateString(name));
						isFoundTable = true;
						stmt.close();
						stmt = null;
					} finally {
						if (stmt != null) {
							try {
								stmt.close();
							} catch (SQLException e) {
								stmt = null;
							}
						}
					}
				}
				rs.close();
				rs = null;
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						rs = null;
					}
				}
			}
		}

		private void updateEvent(String tableName, Integer key, Double value, Connection con)
				throws SQLException {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = con.createStatement(ResultSet.CONCUR_READ_ONLY,
                        ResultSet.TYPE_SCROLL_INSENSITIVE);
				rs = stmt.executeQuery(SQLUtilities.getSelectString(tableName, key));
				rs.last();
				if (rs.getRow() <= 0) {
					insert(tableName, key, value, con);
				} else {
					update(tableName, key, value, con);
				}
				
				rs.close();
				stmt.close();
				
				rs = null;
				stmt = null;
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						rs = null;
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						stmt = null;
					}
				}
			}
		}

		private void insert(String tableName, Integer key, Double value, Connection con) throws SQLException {
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(SQLUtilities.getInsertString(tableName, key, value));
				stmt.close();
				stmt = null;
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						stmt = null;
					}
				}
			}
		}

		private void update(String tableName, Integer key, Double value, Connection con) throws SQLException {
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(SQLUtilities.getUpdateString(tableName, key, value));
				stmt.close();
				stmt = null;
			} finally {
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						stmt = null;
					}
				}
			}
		}

		/**
		 * SQL 文字列生成クラスです。
		 */
		private static class SQLUtilities {
			private static final FastDateFormat formater =
				FastDateFormat.getInstance("yyyy/MM/dd HH:mm:ss");
			private SQLUtilities() {}

			/**
			 * テーブル作成 DML 文字列を返します。
			 * @param tableName テーブル名
			 * @return SQL 文字列
			 */
			public static String getCreateString(String tableName) {
				StringBuffer bf = new StringBuffer();
				bf.append("CREATE TABLE ").append(tableName).append(" (");
				bf.append("counter INTEGER NOT NULL,");
				bf.append("mod_time TIMESTAMP,");
				bf.append("value INTEGER,");
				bf.append("PRIMARY KEY (counter)");
				bf.append(")");

				if (logger.isDebugEnabled()) {
					logger.debug(bf.toString());
				}

				return  bf.toString();
			}

			/**
			 * セレクト SQL 文字列を返します。
			 * @param tableName テーブル名
			 * @param key 選択キー
			 * @return SQL 文字列
			 */
			public static String getSelectString(String tableName, Integer key) {
				StringBuffer bf = new StringBuffer();
				bf.append("SELECT counter, mod_time, value");
				bf.append(" FROM ").append(tableName);
				bf.append(" WHERE counter=").append(key.intValue());

				if (logger.isDebugEnabled()) {
					logger.debug(bf.toString());
				}

				return  bf.toString();
			}

			public static String getSelectString(String tableName, Integer key, String order) {
				StringBuffer bf = new StringBuffer();
				bf.append("SELECT counter, value");
				bf.append(" FROM ").append(tableName);
				bf.append(" WHERE counter < ").append(key.intValue());
				bf.append(" ORDER BY ").append(order);

				if (logger.isDebugEnabled()) {
					logger.debug(bf.toString());
				}

				return  bf.toString();
			}

			/**
			 * 更新 SQL 文字列を返します。
			 * @param tableName テーブル名
			 * @param key 選択キー
			 * @param value 更新データ値
			 * @return SQL 文字列
			 */
			public static String getUpdateString(String tableName, Integer key, Double value) {
				StringBuffer bf = new StringBuffer();
				bf.append("UPDATE ").append(tableName);
				bf.append(" SET value=").append(value.toString());
				bf.append(", mod_time='").append(formater.format(new Date())).append("'");
				bf.append(" WHERE counter=").append(key.intValue());

				if (logger.isDebugEnabled()) {
					logger.debug(bf.toString());
				}

				return  bf.toString();
			}

			/**
			 * 挿入 SQL 文字列を返します。
			 * @param tableName テーブル名
			 * @param key 選択キー
			 * @param value 挿入データ値
			 * @return SQL 文字列
			 */
			public static String getInsertString(String tableName, Integer key, Double value) {
				StringBuffer bf = new StringBuffer();
				bf.append("INSERT INTO ").append(tableName);
				bf.append("(counter, mod_time, value)");
				bf.append(" VALUES(").append(key.intValue());
				bf.append(", '").append(formater.format(new Date())).append("'");
				bf.append(", ").append(value.toString()).append(")");

				if (logger.isDebugEnabled()) {
					logger.debug(bf.toString());
				}

				return  bf.toString();
			}
		}
	}
}
