/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/io/postgresql/PostgreSQLAlarmDataStore.java,v 1.8.2.3 2006/08/25 06:00:27 frdm Exp $
 * $Revision: 1.8.2.3 $
 * $Date: 2006/08/25 06:00:27 $
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
package org.F11.scada.server.io.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.F11.scada.Service;
import org.F11.scada.server.alarm.AlarmDataStore;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.HistoryCheck;
import org.F11.scada.server.event.LoggingDataEventQueue;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;

/**
 * PostgreSQLによるAlarmDataStoreの実装です。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLAlarmDataStore implements AlarmDataStore, HistoryCheck, Runnable, Service {
	private Thread thread;
	private final LoggingDataEventQueue queue;
	private final DataUpdater dataUpdater;
	private static Logger logger;

	public PostgreSQLAlarmDataStore() {
		queue = new LoggingDataEventQueue();
		logger = Logger.getLogger(getClass());
		dataUpdater = new PostgreSQLDataUpdater();
		start();
	}
	
	/**
	 * データ変更イベント値を投入します。
	 * @param key データ変更イベント値
	 */
	public void put(DataValueChangeEventKey key) {
		queue.enqueue(key);
	}
	
	/**
	 * ヒストリー確認欄更新処理を実行します。
	 * @param point ポイント
	 * @param provider データプロバイダ名
	 * @param holder データホルダー名
	 * @param date 確認処理時刻
	 */
	public void doHistoryCheck (
			Integer point,
			String provider,
			String holder,
			Timestamp date) throws SQLException {
		dataUpdater.doHistoryCheck(point, provider, holder, date);
	}


	public void run() {
		Thread ct = Thread.currentThread();
		// TODO データ更新が安全に完了するような、終了処理にすること。
		while (ct == thread) {
			//キューが空の間はwaitします。
			DataValueChangeEventKey key = (DataValueChangeEventKey) queue.dequeue();
			try {
				dataUpdater.updateEvent(key);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
    public void start() {
        if (thread == null) {
    		thread = new Thread(this);
    		thread.setName(getClass().getName());
    		thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            Thread th = thread;
            thread = null;
            th.interrupt();
        }
    }

	/**
	 * データ更新の基底クラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */	
	abstract static class DataUpdater {
		/**
		 * イベントを受け取りデータを永続化処理します。
		 * @param key データ変更イベント値
		 * <!-- GoF Template method pattern -->
		 */
		final void updateEvent(DataValueChangeEventKey key) throws SQLException {
			updateCareer(key);
			updateHistory(key);
			updateSummary(key);
		}

		abstract void updateSummary(DataValueChangeEventKey key) throws SQLException;
		abstract void updateHistory(DataValueChangeEventKey key) throws SQLException;
		abstract void updateCareer(DataValueChangeEventKey key) throws SQLException;
		
		abstract void doHistoryCheck (
				Integer point,
				String provider,
				String holder,
				Timestamp date) throws SQLException;
	}
	
	
	static class PostgreSQLDataUpdater extends DataUpdater {
		private final Context summaryContext;
		private final Context historyContext;
		private final Context careerContext;

		PostgreSQLDataUpdater() {
			summaryContext = new SummaryContext();
			historyContext = new HistoryContext();
			careerContext = new CareerContext();
		}
		
		void updateSummary(DataValueChangeEventKey key) throws SQLException {
			Connection con = null;
			PreparedStatement preSel = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				preSel =
					con.prepareStatement(
						"SELECT att.summary_mode FROM item_table i, attribute_table att WHERE i.attribute_id=att.attribute AND i.point=? AND i.provider=? AND i.holder=?");
				preSel.setInt(1, key.getPoint());
				preSel.setString(2, key.getProvider());
				preSel.setString(3, key.getHolder());
				rs = preSel.executeQuery();
				if (rs.next()) {
					int mode = rs.getInt("summary_mode");
					summaryContext.changeState(mode);
					summaryContext.execute(con, key);
				}
				rs.close();
				preSel.close();
				con.close();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						rs = null;
					}
				}
				if (preSel != null) {
					try {
						preSel.close();
					} catch (SQLException e) {
						preSel = null;
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}
		
		void updateHistory(DataValueChangeEventKey key) throws SQLException {
			Connection con = null;
			PreparedStatement preSel = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				preSel =
					con.prepareStatement(
						"SELECT att.history_mode FROM item_table i, attribute_table att WHERE i.attribute_id=att.attribute AND i.point=? AND i.provider=? AND i.holder=?");
				preSel.setInt(1, key.getPoint());
				preSel.setString(2, key.getProvider());
				preSel.setString(3, key.getHolder());
				rs = preSel.executeQuery();
				if (rs.next()) {
					int mode = rs.getInt("history_mode");
					historyContext.changeState(mode);
					historyContext.execute(con, key);
				}
				rs.close();
				preSel.close();
				con.close();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						rs = null;
					}
				}
				if (preSel != null) {
					try {
						preSel.close();
					} catch (SQLException e) {
						preSel = null;
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}
		
		void updateCareer(DataValueChangeEventKey key) throws SQLException {
			Connection con = null;
			PreparedStatement preSel = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				preSel =
					con.prepareStatement(
						"SELECT att.career_mode FROM item_table i, attribute_table att WHERE i.attribute_id=att.attribute AND i.point=? AND i.provider=? AND i.holder=?");
				preSel.setInt(1, key.getPoint());
				preSel.setString(2, key.getProvider());
				preSel.setString(3, key.getHolder());
				rs = preSel.executeQuery();
				if (rs.next()) {
					int mode = rs.getInt("career_mode");
					careerContext.changeState(mode);
					careerContext.execute(con, key);
				}
				rs.close();
				preSel.close();
				con.close();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						rs = null;
					}
				}
				if (preSel != null) {
					try {
						preSel.close();
					} catch (SQLException e) {
						preSel = null;
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}

		void doHistoryCheck (
				Integer point,
				String provider,
				String holder,
				Timestamp date) throws SQLException {
					
			Connection con = null;
			PreparedStatement preUp = null;
			PreparedStatement preUpUp = null;
			try {
				con = ConnectionUtil.getConnection();
				preUp = con.prepareStatement(
					"UPDATE history_table SET check_flag=? WHERE point=? AND provider=? AND holder=? AND on_date=?");
				preUp.setBoolean(1, true);
				preUp.setInt(2, point.intValue());
				preUp.setString(3, provider);
				preUp.setString(4, holder);
				preUp.setTimestamp(5, date);
				int rc = preUp.executeUpdate();
				logger.info("update rec : " + rc + " Timestamp : " + date);

				if (rc == 0) {
					preUpUp = con.prepareStatement(
						"UPDATE history_table SET check_flag=? WHERE point=? AND provider=? AND holder=? AND on_date>=?");
					Calendar cal = Calendar.getInstance();
					preUpUp.setBoolean(1, true);
					preUpUp.setInt(2, point.intValue());
					preUpUp.setString(3, provider);
					preUpUp.setString(4, holder);
					cal.setTimeInMillis(date.getTime());
					cal.set(Calendar.MILLISECOND, 0);
					preUpUp.setTimestamp(5, new Timestamp(cal.getTimeInMillis()));
					rc = preUpUp.executeUpdate();
					logger.info("update rec : " + rc + " Timestamp : " + new Timestamp(cal.getTimeInMillis()));
					preUpUp.close();
				}

				preUp.close();
				con.close();
			} finally {
				if (preUp != null) {
					try {
						preUp.close();
					} catch (SQLException e) {
						preUp = null;
					}
				}
				if (preUpUp != null) {
					try {
						preUpUp.close();
					} catch (SQLException e) {
						preUpUp = null;
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}


		/**
		 * 
		 * 
		 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
		 */
		static interface Context {
			public void changeState(int mode);
			public void execute(
					Connection con,
					DataValueChangeEventKey key) throws SQLException ;
		}

		/**
		 * モードを表すインターフェイスです。
		 * <!-- GoF State Pattern -->
		 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
		 */
		static interface ModeState {
			public void execute(
					Connection con,
					DataValueChangeEventKey key)
					throws SQLException;
		}


		static class SummaryContext implements Context {
			private ModeState modeState;

			public void changeState(int mode) {
				switch (mode) {
					case 0:
						modeState = SummaryModeState0.getInstance();
						break;
					case 1:
						modeState = SummaryModeState1.getInstance();
						break;
					case 2:
						modeState = SummaryModeState2.getInstance();
						break;
					case 3:
						modeState = SummaryModeState3.getInstance();
						break;
					case 4:
						modeState = SummaryModeState4.getInstance();
						break;
					case 5:
						modeState = SummaryModeState5.getInstance();
						break;
					default :
						throw new IllegalArgumentException("mode is Illegal. mode=" + mode);
				}
			}

			public void execute(
					Connection con,
					DataValueChangeEventKey key) throws SQLException {

				if (modeState == null) {
					throw new IllegalStateException("Not initialize.");
				}
				
				modeState.execute(con, key);
			}


			static class SummaryModeState0 implements ModeState {
				private final static SummaryModeState0 instance = new SummaryModeState0();
			
				private SummaryModeState0() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					// Non Operation.
				}
			}


			static class SummaryModeState1 implements ModeState {
				private final static SummaryModeState1 instance = new SummaryModeState1();
			
				private SummaryModeState1() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						return;
					}
					// キーで行を検索し、あればoff_dateに時刻を設定。無ければ行を追加。
					PreparedStatement prestmt = con.prepareStatement(
						"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
					prestmt.setInt(1, key.getPoint());
					prestmt.setString(2, key.getProvider());
					prestmt.setString(3, key.getHolder());
					ResultSet selrs = prestmt.executeQuery();
					if (selrs.next()) {
						//あればUPDATE
						PreparedStatement preUp = con.prepareStatement(
							"UPDATE summary_table SET bit_value=?, on_date='epoch', off_date=? WHERE point=?  AND provider=? AND holder=?");
						preUp.setBoolean(1, key.getValue().booleanValue());
						preUp.setTimestamp(2, key.getTimeStamp());
						preUp.setInt(3, key.getPoint());
						preUp.setString(4, key.getProvider());
						preUp.setString(5, key.getHolder());
						preUp.executeUpdate();
						preUp.close();
					} else {
						//なければINSERT
						PreparedStatement preIns = con.prepareStatement(
							"INSERT INTO summary_table VALUES (?, ?, ?, 'epoch', ?, ?)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.setBoolean(5, key.getValue().booleanValue());
						preIns.executeUpdate();
						preIns.close();
					}
					selrs.close();
					prestmt.close();
				}
			}
		

			static class SummaryModeState2 implements ModeState {
				private static final SummaryModeState2 instance = new SummaryModeState2();
			
				private SummaryModeState2() {
				}
			
				static ModeState getInstance() {
					return instance;
				}

				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						// キーで行を検索し、あればon_dateに時刻を設定。無ければ行を追加。
						PreparedStatement prestmt = con.prepareStatement(
							"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
						prestmt.setInt(1, key.getPoint());
						prestmt.setString(2, key.getProvider());
						prestmt.setString(3, key.getHolder());
						ResultSet selrs = prestmt.executeQuery();
						if (selrs.next()) {
							//あればUPDATE
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE summary_table SET bit_value=?, off_date='epoch', on_date=? WHERE point=?  AND provider=? AND holder=?");
							preUp.setBoolean(1, key.getValue().booleanValue());
							preUp.setTimestamp(2, key.getTimeStamp());
							preUp.setInt(3, key.getPoint());
							preUp.setString(4, key.getProvider());
							preUp.setString(5, key.getHolder());
							preUp.executeUpdate();
							preUp.close();
						} else {
							//なければINSERT
							PreparedStatement preIns = con.prepareStatement(
								"INSERT INTO summary_table VALUES (?, ?, ?, ?, 'epoch', ?)");
							preIns.setInt(1, key.getPoint());
							preIns.setString(2, key.getProvider());
							preIns.setString(3, key.getHolder());
							preIns.setTimestamp(4, key.getTimeStamp());
							preIns.setBoolean(5, key.getValue().booleanValue());
							preIns.executeUpdate();
							preIns.close();
						}
						selrs.close();
						prestmt.close();
					}
				}
			}
		

			static class SummaryModeState3 implements ModeState {
				private static final SummaryModeState3 instance = new SummaryModeState3();
			
				private SummaryModeState3() {
				}
			
				static ModeState getInstance() {
					return instance;
				}

				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						// キーで行を検索し、あればon_dateに時刻を設定。無ければ行を追加。
						PreparedStatement prestmt = con.prepareStatement(
							"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
						prestmt.setInt(1, key.getPoint());
						prestmt.setString(2, key.getProvider());
						prestmt.setString(3, key.getHolder());
						ResultSet selrs = prestmt.executeQuery();
						if (selrs.next()) {
							if (logger.isDebugEnabled()) {
								StringBuffer b = new StringBuffer();
								b.append("UPDATE summary_table SET bit_value=").append(key.getValue().booleanValue())
								.append(", off_date='epoch', on_date=").append(key.getTimeStamp()).append(" WHERE point=")
								.append(key.getPoint()).append(" AND provider=")
								.append(key.getProvider()).append(" AND holder=").append(key.getHolder());
								logger.debug(b.toString());
							}
							//あればUPDATE
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE summary_table SET bit_value=?, off_date='epoch', on_date=? WHERE point=?  AND provider=? AND holder=?");
							preUp.setBoolean(1, key.getValue().booleanValue());
							preUp.setTimestamp(2, key.getTimeStamp());
							preUp.setInt(3, key.getPoint());
							preUp.setString(4, key.getProvider());
							preUp.setString(5, key.getHolder());
							preUp.executeUpdate();
							preUp.close();
						} else {
							if (logger.isDebugEnabled()) {
								StringBuffer b = new StringBuffer();
								b.append("INSERT INTO summary_table VALUES (").append(key.getPoint())
								.append(", ").append(key.getProvider()).append(", ")
								.append(key.getHolder()).append(", ").append(key.getTimeStamp())
								.append(", 'epoch', ").append(key.getValue().booleanValue()).append(")");
								logger.debug(b.toString());
							}
							//なければINSERT
							PreparedStatement preIns = con.prepareStatement(
								"INSERT INTO summary_table VALUES (?, ?, ?, ?, 'epoch', ?)");
							preIns.setInt(1, key.getPoint());
							preIns.setString(2, key.getProvider());
							preIns.setString(3, key.getHolder());
							preIns.setTimestamp(4, key.getTimeStamp());
							preIns.setBoolean(5, key.getValue().booleanValue());
							preIns.executeUpdate();
							preIns.close();
						}
						selrs.close();
						prestmt.close();
					} else {
						if (logger.isDebugEnabled()) {
							StringBuffer b = new StringBuffer();
							b.append("SELECT point FROM summary_table WHERE point=").append(key.getPoint())
							.append(" AND provider=").append(key.getProvider())
							.append(" AND holder=").append(key.getHolder());
							logger.debug(b.toString());
						}
						//	キーで行を検索し、あればoff_dateに時刻を設定。
						PreparedStatement prestmt = con.prepareStatement(
							"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
						prestmt.setInt(1, key.getPoint());
						prestmt.setString(2, key.getProvider());
						prestmt.setString(3, key.getHolder());
						ResultSet selrs = prestmt.executeQuery();
						if (selrs.next()) {
							if (logger.isDebugEnabled()) {
								StringBuffer b = new StringBuffer();
								b.append("UPDATE summary_table SET bit_value=").append(key.getValue().booleanValue())
								.append(", off_date=").append(key.getTimeStamp())
								.append(" WHERE point=").append(key.getPoint())
								.append(" AND provider=").append(key.getProvider())
								.append(" AND holder=").append(key.getHolder());
								logger.debug(b.toString());
							}
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE summary_table SET bit_value=?, off_date=? WHERE point=?  AND provider=? AND holder=?");
							preUp.setBoolean(1, key.getValue().booleanValue());
							preUp.setTimestamp(2, key.getTimeStamp());
							preUp.setInt(3, key.getPoint());
							preUp.setString(4, key.getProvider());
							preUp.setString(5, key.getHolder());
							preUp.executeUpdate();
							preUp.close();
						}
						selrs.close();
						prestmt.close();
					}
				}
			}
		

			static class SummaryModeState4 implements ModeState {
				private static final SummaryModeState4 instance = new SummaryModeState4();
			
				private SummaryModeState4() {
				}
			
				static ModeState getInstance() {
					return instance;
				}

				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						// キーで行を検索し、あればon_dateに時刻を設定。無ければ行を追加。
						PreparedStatement prestmt = con.prepareStatement(
							"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
						prestmt.setInt(1, key.getPoint());
						prestmt.setString(2, key.getProvider());
						prestmt.setString(3, key.getHolder());
						ResultSet selrs = prestmt.executeQuery();
						if (selrs.next()) {
							//あればUPDATE
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE summary_table SET bit_value=?, off_date='epoch', on_date=? WHERE point=?  AND provider=? AND holder=?");
							preUp.setBoolean(1, key.getValue().booleanValue());
							preUp.setTimestamp(2, key.getTimeStamp());
							preUp.setInt(3, key.getPoint());
							preUp.setString(4, key.getProvider());
							preUp.setString(5, key.getHolder());
							preUp.executeUpdate();
							preUp.close();
						} else {
							//なければINSERT
							PreparedStatement preIns = con.prepareStatement(
								"INSERT INTO summary_table VALUES (?, ?, ?, ?, 'epoch', ?)");
							preIns.setInt(1, key.getPoint());
							preIns.setString(2, key.getProvider());
							preIns.setString(3, key.getHolder());
							preIns.setTimestamp(4, key.getTimeStamp());
							preIns.setBoolean(5, key.getValue().booleanValue());
							preIns.executeUpdate();
							preIns.close();
						}
						selrs.close();
						prestmt.close();
					} else {
						//	キーで行を検索し、あればoff_dateに時刻を設定。
						PreparedStatement prestmt = con.prepareStatement(
							"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
						prestmt.setInt(1, key.getPoint());
						prestmt.setString(2, key.getProvider());
						prestmt.setString(3, key.getHolder());
						ResultSet selrs = prestmt.executeQuery();
						if (selrs.next()) {
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE summary_table SET bit_value=?, off_date=? WHERE point=?  AND provider=? AND holder=?");
							preUp.setBoolean(1, key.getValue().booleanValue());
							preUp.setTimestamp(2, key.getTimeStamp());
							preUp.setInt(3, key.getPoint());
							preUp.setString(4, key.getProvider());
							preUp.setString(5, key.getHolder());
							preUp.executeUpdate();
							preUp.close();
						}
						selrs.close();
						prestmt.close();
					}
				}
			}
		

			static class SummaryModeState5 implements ModeState {
				private static final SummaryModeState5 instance = new SummaryModeState5();
			
				private SummaryModeState5() {
				}
			
				static ModeState getInstance() {
					return instance;
				}

				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						//	キーで行を検索し、あればon_dateに時刻を設定。
						PreparedStatement prestmt = con.prepareStatement(
							"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
						prestmt.setInt(1, key.getPoint());
						prestmt.setString(2, key.getProvider());
						prestmt.setString(3, key.getHolder());
						ResultSet selrs = prestmt.executeQuery();
						if (selrs.next()) {
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE summary_table SET bit_value=?, on_date=? WHERE point=?  AND provider=? AND holder=?");
							preUp.setBoolean(1, key.getValue().booleanValue());
							preUp.setTimestamp(2, key.getTimeStamp());
							preUp.setInt(3, key.getPoint());
							preUp.setString(4, key.getProvider());
							preUp.setString(5, key.getHolder());
							preUp.executeUpdate();
							preUp.close();
						}
						selrs.close();
						prestmt.close();
					} else {
						// キーで行を検索し、あればoff_dateに時刻を設定。無ければ行を追加。
						PreparedStatement prestmt = con.prepareStatement(
							"SELECT point FROM summary_table WHERE point=? AND provider=? AND holder=?");
						prestmt.setInt(1, key.getPoint());
						prestmt.setString(2, key.getProvider());
						prestmt.setString(3, key.getHolder());
						ResultSet selrs = prestmt.executeQuery();
						if (selrs.next()) {
							//あればUPDATE
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE summary_table SET bit_value=?, on_date='epoch', off_date=? WHERE point=?  AND provider=? AND holder=?");
							preUp.setBoolean(1, key.getValue().booleanValue());
							preUp.setTimestamp(2, key.getTimeStamp());
							preUp.setInt(3, key.getPoint());
							preUp.setString(4, key.getProvider());
							preUp.setString(5, key.getHolder());
							preUp.executeUpdate();
							preUp.close();
						} else {
							//なければINSERT
							PreparedStatement preIns = con.prepareStatement(
								"INSERT INTO summary_table VALUES (?, ?, ?, 'epoch', ?, ?)");
							preIns.setInt(1, key.getPoint());
							preIns.setString(2, key.getProvider());
							preIns.setString(3, key.getHolder());
							preIns.setTimestamp(4, key.getTimeStamp());
							preIns.setBoolean(5, key.getValue().booleanValue());
							preIns.executeUpdate();
							preIns.close();
						}
						selrs.close();
						prestmt.close();
					}
				}
			}
		}

		
		static class HistoryContext implements Context {
			private ModeState modeState;

			public void changeState(int mode) {
				switch (mode) {
					case 0:
						modeState = HistoryModeState0.getInstance();
						break;
					case 1:
						modeState = HistoryModeState1.getInstance();
						break;
					case 2:
						modeState = HistoryModeState2.getInstance();
						break;
					case 3:
						modeState = HistoryModeState3.getInstance();
						break;
					case 4:
						modeState = HistoryModeState4.getInstance();
						break;
					case 5:
						modeState = HistoryModeState5.getInstance();
						break;
					default :
						throw new IllegalArgumentException("mode is Illegal. mode=" + mode);
				}
			}

			public void execute(
					Connection con,
					DataValueChangeEventKey key) throws SQLException {

				if (modeState == null) {
					throw new IllegalStateException("Not initialize.");
				}
				
				modeState.execute(con, key);
			}

			
			static class HistoryModeState0 implements ModeState {
				private final static HistoryModeState0 instance = new HistoryModeState0();
			
				private HistoryModeState0() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					// Non Operation.
				}
			}

			
			static class HistoryModeState1 implements ModeState {
				private final static HistoryModeState1 instance = new HistoryModeState1();
			
				private HistoryModeState1() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						return;
					}

					// off_dateに設定して行を追加
					PreparedStatement preIns = con.prepareStatement(
					"INSERT INTO history_table VALUES (?, ?, ?, 'epoch', ?, NULL)");
					preIns.setInt(1, key.getPoint());
					preIns.setString(2, key.getProvider());
					preIns.setString(3, key.getHolder());
					preIns.setTimestamp(4, key.getTimeStamp());
					preIns.executeUpdate();
					preIns.close();
				}
			}

			
			static class HistoryModeState2 implements ModeState {
				private final static HistoryModeState2 instance = new HistoryModeState2();
			
				private HistoryModeState2() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						// on_dateに設定して行を追加
						PreparedStatement preIns = con.prepareStatement(
						"INSERT INTO history_table VALUES (?, ?, ?, ?, 'epoch', NULL)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.executeUpdate();
						preIns.close();
					}
				}
			}

			
			static class HistoryModeState3 implements ModeState {
				private final static HistoryModeState3 instance = new HistoryModeState3();
			
				private HistoryModeState3() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						// on_dateに設定して行を追加
						PreparedStatement preIns = con.prepareStatement(
						"INSERT INTO history_table VALUES (?, ?, ?, ?, 'epoch', NULL)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.executeUpdate();
						preIns.close();
					} else {
						// 該当レコードのoff_dateに時間を設定
						PreparedStatement preDelSel = con.prepareStatement(
							"SELECT on_date FROM history_table WHERE point=? AND provider=? AND holder=? ORDER BY on_date DESC LIMIT 1");
						preDelSel.setInt(1, key.getPoint());
						preDelSel.setString(2, key.getProvider());
						preDelSel.setString(3, key.getHolder());
						ResultSet delSelrs = preDelSel.executeQuery();
						if (delSelrs.next()) {
							Timestamp ondate = delSelrs.getTimestamp("on_date");
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE history_table SET off_date=? WHERE point=? AND provider=? AND holder=? AND on_date=?");
							preUp.setTimestamp(1, key.getTimeStamp());
							preUp.setInt(2, key.getPoint());
							preUp.setString(3, key.getProvider());
							preUp.setString(4, key.getHolder());
							preUp.setTimestamp(5, ondate);
							preUp.executeUpdate();
							preUp.close();
						}
						delSelrs.close();
						preDelSel.close();
					}
				}
			}

			
			static class HistoryModeState4 implements ModeState {
				private final static HistoryModeState4 instance = new HistoryModeState4();
			
				private HistoryModeState4() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						// on_dateに設定して行を追加
						PreparedStatement preIns = con.prepareStatement(
						"INSERT INTO history_table VALUES (?, ?, ?, ?, 'epoch', NULL)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.executeUpdate();
						preIns.close();
					} else {
						// 該当レコードのoff_dateに時間を設定
						PreparedStatement preDelSel = con.prepareStatement(
							"SELECT on_date FROM history_table WHERE point=? AND provider=? AND holder=? ORDER BY on_date DESC LIMIT 1");
						preDelSel.setInt(1, key.getPoint());
						preDelSel.setString(2, key.getProvider());
						preDelSel.setString(3, key.getHolder());
						ResultSet delSelrs = preDelSel.executeQuery();
						if (delSelrs.next()) {
							Timestamp ondate = delSelrs.getTimestamp("on_date");
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE history_table SET off_date=? WHERE point=? AND provider=? AND holder=? AND on_date=?");
							preUp.setTimestamp(1, key.getTimeStamp());
							preUp.setInt(2, key.getPoint());
							preUp.setString(3, key.getProvider());
							preUp.setString(4, key.getHolder());
							preUp.setTimestamp(5, ondate);
							preUp.executeUpdate();
							preUp.close();
						}
						delSelrs.close();
						preDelSel.close();
					}
				}
			}

			
			static class HistoryModeState5 implements ModeState {
				private final static HistoryModeState5 instance = new HistoryModeState5();
			
				private HistoryModeState5() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						// 該当レコードのon_dateに時間を設定
						PreparedStatement preDelSel = con.prepareStatement(
							"SELECT off_date FROM history_table WHERE point=? AND provider=? AND holder=? ORDER BY off_date DESC LIMIT 1");
						preDelSel.setInt(1, key.getPoint());
						preDelSel.setString(2, key.getProvider());
						preDelSel.setString(3, key.getHolder());
						ResultSet delSelrs = preDelSel.executeQuery();
						if (delSelrs.next()) {
							Timestamp offdate = delSelrs.getTimestamp("off_date");
							PreparedStatement preUp = con.prepareStatement(
								"UPDATE history_table SET on_date=? WHERE point=? AND provider=? AND holder=? AND off_date=?");
							preUp.setTimestamp(1, key.getTimeStamp());
							preUp.setInt(2, key.getPoint());
							preUp.setString(3, key.getProvider());
							preUp.setString(4, key.getHolder());
							preUp.setTimestamp(5, offdate);
							preUp.executeUpdate();
							preUp.close();
						}
						delSelrs.close();
						preDelSel.close();
					} else {
						// off_dateに設定して行を追加
						PreparedStatement preIns = con.prepareStatement(
						"INSERT INTO history_table VALUES (?, ?, ?, 'epoch', ?, NULL)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.executeUpdate();
						preIns.close();
					}
				}
			}
		}

		
		static class CareerContext implements Context {
			private ModeState modeState;

			public void changeState(int mode) {
				switch (mode) {
					case 0:
						modeState = CareerModeState0.getInstance();
						break;
					case 1:
						modeState = CareerModeState1.getInstance();
						break;
					case 2:
						modeState = CareerModeState2.getInstance();
						break;
					case 3:
						modeState = CareerModeState3.getInstance();
						break;
					case 4:
						modeState = CareerModeState4.getInstance();
						break;
					case 5:
						modeState = CareerModeState5.getInstance();
						break;
					default :
						throw new IllegalArgumentException("mode is Illegal. mode=" + mode);
				}
			}

			public void execute(
					Connection con,
					DataValueChangeEventKey key) throws SQLException {

				if (modeState == null) {
					throw new IllegalStateException("Not initialize.");
				}
				
				modeState.execute(con, key);
			}
			
			
			static class CareerModeState0 implements ModeState {
				private final static CareerModeState0 instance = new CareerModeState0();
			
				private CareerModeState0() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					// Non Operation.
				}
			}
			
			
			static class CareerModeState1 implements ModeState {
				private final static CareerModeState1 instance = new CareerModeState1();
			
				private CareerModeState1() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						return;
					}

					PreparedStatement preIns = con.prepareStatement(
						"INSERT INTO career_table VALUES (?, ?, ?, ?, ?)");
					preIns.setInt(1, key.getPoint());
					preIns.setString(2, key.getProvider());
					preIns.setString(3, key.getHolder());
					preIns.setTimestamp(4, key.getTimeStamp());
					preIns.setBoolean(5, key.getValue().booleanValue());
					preIns.executeUpdate();
					preIns.close();				
				}
			}
			
			
			static class CareerModeState2 implements ModeState {
				private final static CareerModeState2 instance = new CareerModeState2();
			
				private CareerModeState2() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						PreparedStatement preIns = con.prepareStatement(
							"INSERT INTO career_table VALUES (?, ?, ?, ?, ?)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.setBoolean(5, key.getValue().booleanValue());
						preIns.executeUpdate();
						preIns.close();				
					}
				}
			}
			
			
			static class CareerModeState3 implements ModeState {
				private final static CareerModeState3 instance = new CareerModeState3();
			
				private CareerModeState3() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					PreparedStatement preIns = con.prepareStatement(
						"INSERT INTO career_table VALUES (?, ?, ?, ?, ?)");
					preIns.setInt(1, key.getPoint());
					preIns.setString(2, key.getProvider());
					preIns.setString(3, key.getHolder());
					preIns.setTimestamp(4, key.getTimeStamp());
					preIns.setBoolean(5, key.getValue().booleanValue());
					preIns.executeUpdate();
					preIns.close();				
				}
			}
			
			
			static class CareerModeState4 implements ModeState {
				private final static CareerModeState4 instance = new CareerModeState4();
			
				private CareerModeState4() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						PreparedStatement preIns = con.prepareStatement(
							"INSERT INTO career_table VALUES (?, ?, ?, ?, ?)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.setBoolean(5, key.getValue().booleanValue());
						preIns.executeUpdate();
						preIns.close();				
					} else {
						PreparedStatement preDelSel = con.prepareStatement(
							"SELECT entrydate FROM career_table WHERE point=? AND provider=? AND holder=? ORDER BY entrydate DESC LIMIT 1");
						preDelSel.setInt(1, key.getPoint());
						preDelSel.setString(2, key.getProvider());
						preDelSel.setString(3, key.getHolder());
						
						ResultSet rsDelSel = preDelSel.executeQuery();
						if (rsDelSel.next()) {
							PreparedStatement preDel = con.prepareStatement(
								"DELETE  FROM career_table WHERE point=? AND provider=? AND holder=? AND entrydate=?");
							preDel.setInt(1, key.getPoint());
							preDel.setString(2, key.getProvider());
							preDel.setString(3, key.getHolder());
							preDel.setTimestamp(4, rsDelSel.getTimestamp("entrydate"));
							preDel.executeUpdate();
							preDel.close();
						}
						rsDelSel.close();
						preDelSel.close();
					}
				}
			}
			
			
			static class CareerModeState5 implements ModeState {
				private final static CareerModeState5 instance = new CareerModeState5();
			
				private CareerModeState5() {
				}
			
				static ModeState getInstance() {
					return instance;
				}
			
				public void execute(
						Connection con,
						DataValueChangeEventKey key)
						throws SQLException {
					if (key.getValue().booleanValue()) {
						PreparedStatement preDelSel = con.prepareStatement(
							"SELECT entrydate FROM career_table WHERE point=? AND provider=? AND holder=? ORDER BY entrydate DESC LIMIT 1");
						preDelSel.setInt(1, key.getPoint());
						preDelSel.setString(2, key.getProvider());
						preDelSel.setString(3, key.getHolder());
						
						ResultSet rsDelSel = preDelSel.executeQuery();
						if (rsDelSel.next()) {
							PreparedStatement preDel = con.prepareStatement(
								"DELETE  FROM career_table WHERE point=? AND provider=? AND holder=? AND entrydate=?");
							preDel.setInt(1, key.getPoint());
							preDel.setString(2, key.getProvider());
							preDel.setString(3, key.getHolder());
							preDel.setTimestamp(4, rsDelSel.getTimestamp("entrydate"));
							preDel.executeUpdate();
							preDel.close();
						}
						rsDelSel.close();
						preDelSel.close();
					} else {
						PreparedStatement preIns = con.prepareStatement(
							"INSERT INTO career_table VALUES (?, ?, ?, ?, ?)");
						preIns.setInt(1, key.getPoint());
						preIns.setString(2, key.getProvider());
						preIns.setString(3, key.getHolder());
						preIns.setTimestamp(4, key.getTimeStamp());
						preIns.setBoolean(5, key.getValue().booleanValue());
						preIns.executeUpdate();
						preIns.close();				
					}
				}
			}
		}
	}
}
