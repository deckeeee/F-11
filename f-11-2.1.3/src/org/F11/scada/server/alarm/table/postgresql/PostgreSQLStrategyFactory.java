/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/postgresql/PostgreSQLStrategyFactory.java,v 1.13.2.11 2007/10/23 09:03:50 frdm Exp $
 * $Revision: 1.13.2.11 $
 * $Date: 2007/10/23 09:03:50 $
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.alarm.AlarmException;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.mail.AlarmMail;
import org.F11.scada.server.alarm.mail.AlarmMailFactory;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.alarm.table.RowDataStrategy;
import org.F11.scada.server.alarm.table.SoundStrategy;
import org.F11.scada.server.alarm.table.StrategyFactory;
import org.F11.scada.server.io.StrategyUtility;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * PostgreSQL用のテーブルモデル操作アルゴリズム・ファクトリークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLStrategyFactory extends StrategyFactory {
	/** 警報音発生ロジック */
	private SoundStrategy soundStrategy;

	/**
	 * PostgreSQL用のテーブルモデル操作アルゴリズム・ファクトリーを生成します。
	 * 
	 * @throws IOException SQL定義プロパティ読込時の例外です。
	 * @throws SQLException SQL実行時例外です。
	 */
	public PostgreSQLStrategyFactory() {
	}

	public void setSoundStrategy(SoundStrategy soundStrategy) {
		this.soundStrategy = soundStrategy;
	}

	/**
	 * PostgreSQL用の履歴テーブルモデル操作アルゴリズムを返します。
	 * 
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	public RowDataStrategy createCareerStrategy(AlarmTableModel model) {
		return new CareerStrategy(new StrategyUtility(), model, soundStrategy);
	}

	/**
	 * PostgreSQL用のヒストリーテーブルモデル操作アルゴリズムを返します。
	 * 
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	public RowDataStrategy createHistoryStrategy(AlarmTableModel model) {
		return new HistoryStrategy(new StrategyUtility(), model);
	}

	/**
	 * PostgreSQL用のサマリーテーブルモデル操作アルゴリズムを返します。
	 * 
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	public RowDataStrategy createSummaryStrategy(AlarmTableModel model) {
		return new SummaryStrategy(new StrategyUtility(), model);
	}

	/**
	 * PostgreSQL用の未復旧テーブルモデル操作アルゴリズムを返します。
	 * 
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	public RowDataStrategy createOccurrenceStrategy(AlarmTableModel model) {
		return new OccurrenceStrategy(new StrategyUtility(), model);
	}

	/**
	 * PostgreSQL用の未確認テーブルモデル操作アルゴリズムを返します。
	 * 
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	public RowDataStrategy createNoncheckStrategy(AlarmTableModel model) {
		return new NoncheckStrategy(new StrategyUtility(), model);
	}

	/**
	 * PostgreSQL用の履歴テーブルモデル操作アルゴリズムの実装クラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static final class CareerStrategy implements RowDataStrategy {
		private static final Logger logger = Logger
				.getLogger(CareerStrategy.class);
		private final StrategyUtility utility;
		private final AlarmTableModel model;
		private final AlarmMail alarmMail;
		private final SoundStrategy strategy;

		CareerStrategy(
				StrategyUtility utility,
				AlarmTableModel model,
				SoundStrategy strategy) {
			this.utility = utility;
			this.model = model;
			S2Container container = S2ContainerUtil.getS2Container();
			AlarmMailFactory factory = (AlarmMailFactory) container
					.getComponent(AlarmMailFactory.class);
			this.alarmMail = factory.getAlarmMail();
			this.strategy = strategy;
		}

		/**
		 * イベントをテーブルモデルに反映します。
		 * 
		 * @param evt データ変更イベント
		 * @see org.F11.scada.server.alarm.table.RowDataStrategy#renewRow(jp.gr.
		 *      javacons.jim.DataValueChangeEvent, javax.swing.table.
		 *      AlarmTableModel)
		 */
		public void renewRow(DataValueChangeEventKey key) throws AlarmException {

			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				stmt = con.prepareStatement(utility
						.getPrepareStatement("/career/renewsql"));
				stmt.setInt(1, key.getPoint());
				stmt.setString(2, key.getProvider());
				stmt.setString(3, key.getHolder());
				stmt.setBoolean(4, key.getValue().booleanValue());
				rs = stmt.executeQuery();
				if (rs.next()) {
					doInsert(rs, model, key);
					alarmMail.send(rs, key);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Not insert. key=" + key);
					}
				}
			} catch (Exception e) {
				throw new AlarmException(e);
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
		 * 登録モードを判定してテーブルモデルを更新します。
		 */
		private void doInsert(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			int mode = rs.getInt("career_mode");
			boolean value = key.getValue().booleanValue();

			if ((mode == 1 && value == false) || (mode == 2 && value == true)
					|| (mode == 3)) {
				addRow(rs, model, key);
			} else if (mode == 4) {
				if (value == true) {
					addRow(rs, model, key);
				} else {
					removeRow(model, key);
				}
			} else if (mode == 5) {
				if (value == true) {
					removeRow(model, key);
				} else {
					addRow(rs, model, key);
				}
			}
		}

		/**
		 * 行を追加します。
		 */
		private void addRow(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			Object[] insRow = new Object[model.getColumnCount()];
			insRow[model.getColumn("ジャンプパス")] = rs.getString("jump_path");
			insRow[model.getColumn("自動ジャンプ")] = Boolean.valueOf(rs
					.getBoolean("auto_jump_flag"));
			insRow[model.getColumn("優先順位")] = new Integer(rs
					.getInt("auto_jump_priority"));
			insRow[model.getColumn("表示色")] = rs.getString("alarm_color");
			insRow[model.getColumn("point")] = new Integer(key.getPoint());
			insRow[model.getColumn("provider")] = key.getProvider();
			insRow[model.getColumn("holder")] = key.getHolder();
			insRow[model.getColumn("サウンドタイプ")] = strategy.getSoundType(rs
					.getInt("sound_type"), key);
			insRow[model.getColumn("サウンドパス")] = rs.getString("sound_path");
			insRow[model.getColumn("Emailグループ")] = new Integer(rs
					.getInt("email_group_id"));
			insRow[model.getColumn("Emailモード")] = new Integer(rs
					.getInt("email_send_mode"));
			insRow[model.getColumn("onoff")] = Boolean.valueOf(WifeUtilities
					.isTrue(rs.getString("onoff")));
			insRow[model.getColumn("日時")] = key.getTimeStamp();
			insRow[model.getColumn("記号")] = rs.getString("unit");
			insRow[model.getColumn("名称")] = rs.getString("kikiname");
			insRow[model.getColumn("警報・状態")] = rs.getString("message");
			insRow[model.getColumn("種別")] = rs.getString("priorityname");
			model.insertRow(0, insRow, key);
		}

		/**
		 * キーで一番上の行から検索し、その行があれば削除します。
		 */
		private void removeRow(
				AlarmTableModel model,
				DataValueChangeEventKey key) {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, "point")).intValue();
				String pro = (String) model.getValueAt(row, "provider");
				String hol = (String) model.getValueAt(row, "holder");
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					model.removeRow(row, key);
					break;
				}
			}
		}
	}

	/**
	 * PostgreSQL用のヒストリーテーブルモデル操作アルゴリズムの実装クラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static final class HistoryStrategy implements RowDataStrategy {
		private static final Logger logger = Logger
				.getLogger(HistoryStrategy.class);
		private final StrategyUtility utility;
		private final AlarmTableModel model;

		HistoryStrategy(StrategyUtility utility, AlarmTableModel model) {
			this.utility = utility;
			this.model = model;
		}

		public void renewRow(DataValueChangeEventKey key) throws AlarmException {

			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				stmt = con.prepareStatement(utility
						.getPrepareStatement("/history/renewsql"));
				stmt.setInt(1, key.getPoint());
				stmt.setString(2, key.getProvider());
				stmt.setString(3, key.getHolder());
				stmt.setBoolean(4, key.getValue().booleanValue());
				rs = stmt.executeQuery();
				if (rs.next()) {
					doInsert(rs, model, key);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Not insert. key=" + key);
					}
				}
			} catch (Exception e) {
				throw new AlarmException(e);
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
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}

		private void doInsert(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			int mode = rs.getInt("history_mode");
			boolean value = key.getValue().booleanValue();

			if (mode == 1 && value == false) {
				addRowOffdate(rs, model, key);
			} else if (mode == 2 && value == true) {
				addRowOndate(rs, model, key);
			} else if (mode == 3) {
				if (value == true) {
					addRowOndate(rs, model, key);
				} else {
					modifyRow(rs, model, key);
				}
			} else if (mode == 4) {
				if (value == true) {
					addRowOndate(rs, model, key);
				} else {
					removeRow(model, key);
				}
			} else if (mode == 5) {
				if (value == true) {
					removeRow(model, key);
				} else {
					addRowOffdate(rs, model, key);
				}
			}
		}

		private void addRowOndate(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			Object[] insRow = new Object[model.getColumnCount()];
			addRowCommon(rs, model, key, insRow);
			insRow[model.getColumn("発生・運転")] = key.getTimeStamp();
			insRow[model.getColumn("復旧・停止")] = null;
			model.insertRow(0, insRow, key);
		}

		private void addRowOffdate(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			Object[] insRow = new Object[model.getColumnCount()];
			addRowCommon(rs, model, key, insRow);
			insRow[model.getColumn("発生・運転")] = null;
			insRow[model.getColumn("復旧・停止")] = key.getTimeStamp();
			model.insertRow(0, insRow, key);
		}

		private void addRowCommon(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key,
				Object[] insRow) throws SQLException {
			insRow[model.getColumn("ジャンプパス")] = rs.getString("jump_path");
			insRow[model.getColumn("自動ジャンプ")] = Boolean.valueOf(rs
					.getBoolean("auto_jump_flag"));
			insRow[model.getColumn("優先順位")] = new Integer(rs
					.getInt("auto_jump_priority"));
			insRow[model.getColumn("表示色")] = rs.getString("alarm_color");
			insRow[model.getColumn("point")] = new Integer(key.getPoint());
			insRow[model.getColumn("provider")] = key.getProvider();
			insRow[model.getColumn("holder")] = key.getHolder();
			insRow[model.getColumn("記号")] = rs.getString("unit");
			insRow[model.getColumn("名称")] = rs.getString("kikiname");
			if (rs.getBoolean("check_type")) {
				insRow[model.getColumn("確認")] = null;
			} else {
				insRow[model.getColumn("確認")] = "────";
			}
			insRow[model.getColumn("種別")] = rs.getString("priorityname");
		}

		private void removeRow(
				AlarmTableModel model,
				DataValueChangeEventKey key) {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, "point")).intValue();
				String pro = (String) model.getValueAt(row, "provider");
				String hol = (String) model.getValueAt(row, "holder");
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					model.removeRow(row, key);
					break;
				}
			}
		}

		private void modifyRow(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, "point")).intValue();
				String pro = (String) model.getValueAt(row, "provider");
				String hol = (String) model.getValueAt(row, "holder");
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					int colSize = model.getColumnCount();
					Object[] obj = new Object[colSize];
					for (int col = 0; col < colSize; col++) {
						obj[col] = model.getValueAt(row, col);
					}
					obj[model.getColumn("表示色")] = rs.getString("alarm_color");
					obj[model.getColumn("復旧・停止")] = key.getTimeStamp();
					model.setValueAt(obj, row, model.getColumn("表示色"), key);
					model.setValueAt(obj, row, model.getColumn("復旧・停止"), key);
					break;
				}
			}
		}
	}

	/**
	 * PostgreSQL用のサマリーテーブルモデル操作アルゴリズムの実装クラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static final class SummaryStrategy implements RowDataStrategy {
		private static final Logger logger = Logger
				.getLogger(SummaryStrategy.class);
		private final StrategyUtility utility;
		private final AlarmTableModel model;

		SummaryStrategy(StrategyUtility utility, AlarmTableModel model) {
			this.utility = utility;
			this.model = model;
		}

		public void renewRow(DataValueChangeEventKey key) throws AlarmException {

			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				stmt = con.prepareStatement(utility
						.getPrepareStatement("/summary/renewsql"));
				stmt.setInt(1, key.getPoint());
				stmt.setString(2, key.getProvider());
				stmt.setString(3, key.getHolder());
				stmt.setBoolean(4, key.getValue().booleanValue());
				rs = stmt.executeQuery();
				if (rs.next()) {
					doInsert(rs, model, key);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Not insert. key=" + key);
					}
				}
			} catch (Exception e) {
				throw new AlarmException(e);
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
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}

		private void doInsert(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			int mode = rs.getInt("summary_mode");
			boolean value = key.getValue().booleanValue();

			if (mode == 1 && value == false) {
				addRowOffdate(rs, model, key);
			} else if (mode == 2 && value == true) {
				addRowOndate(rs, model, key);
			} else if (mode == 3) {
				if (value == true) {
					addRowOndate(rs, model, key);
				} else {
					modifyRow(rs, model, key);
				}
			} else if (mode == 4) {
				if (value == true) {
					addRowOndate(rs, model, key);
				} else {
					removeRow(model, key);
				}
			} else if (mode == 5) {
				if (value == true) {
					removeRow(model, key);
				} else {
					addRowOffdate(rs, model, key);
				}
			}
		}

		private void addRowOndate(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			int row = model.searchRow(key);
			if (row < 0) {
				Object[] insRow = new Object[model.getColumnCount()];
				addRowCommon(rs, model, key, insRow);
				insRow[model.getColumn("発生・運転")] = key.getTimeStamp();
				insRow[model.getColumn("復旧・停止")] = null;
				model.insertRow(0, insRow, key);
			} else {
				int colSize = model.getColumnCount();
				Object[] obj = new Object[colSize];
				for (int col = 0; col < colSize; col++) {
					obj[col] = model.getValueAt(row, col);
				}
				obj[model.getColumn("表示色")] = rs.getString("alarm_color");
				obj[model.getColumn("発生・運転")] = key.getTimeStamp();
				obj[model.getColumn("復旧・停止")] = null;
				obj[model.getColumn("警報・状態")] = rs.getString("message");
				model.setValueAt(obj, row, model.getColumn("表示色"), key);
				model.setValueAt(obj, row, model.getColumn("発生・運転"), key);
				model.setValueAt(obj, row, model.getColumn("復旧・停止"), key);
				model.setValueAt(obj, row, model.getColumn("警報・状態"), key);
			}
		}

		private void addRowOffdate(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			int row = model.searchRow(key);
			if (row < 0) {
				Object[] insRow = new Object[model.getColumnCount()];
				addRowCommon(rs, model, key, insRow);
				insRow[model.getColumn("発生・運転")] = null;
				insRow[model.getColumn("復旧・停止")] = key.getTimeStamp();
				model.insertRow(0, insRow, key);
			} else {
				int colSize = model.getColumnCount();
				Object[] obj = new Object[colSize];
				for (int col = 0; col < colSize; col++) {
					obj[col] = model.getValueAt(row, col);
				}
				obj[model.getColumn("表示色")] = rs.getString("alarm_color");
				obj[model.getColumn("発生・運転")] = null;
				obj[model.getColumn("復旧・停止")] = key.getTimeStamp();
				obj[model.getColumn("警報・状態")] = rs.getString("message");
				model.setValueAt(obj, row, model.getColumn("表示色"), key);
				model.setValueAt(obj, row, model.getColumn("発生・運転"), key);
				model.setValueAt(obj, row, model.getColumn("復旧・停止"), key);
				model.setValueAt(obj, row, model.getColumn("警報・状態"), key);
			}
		}

		private void addRowCommon(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key,
				Object[] insRow) throws SQLException {
			insRow[model.getColumn("ジャンプパス")] = rs.getString("jump_path");
			insRow[model.getColumn("自動ジャンプ")] = Boolean.valueOf(rs
					.getBoolean("auto_jump_flag"));
			insRow[model.getColumn("優先順位")] = new Integer(rs
					.getInt("auto_jump_priority"));
			insRow[model.getColumn("表示色")] = rs.getString("alarm_color");
			insRow[model.getColumn("point")] = new Integer(key.getPoint());
			insRow[model.getColumn("provider")] = key.getProvider();
			insRow[model.getColumn("holder")] = key.getHolder();
			insRow[model.getColumn("記号")] = rs.getString("unit");
			insRow[model.getColumn("名称")] = rs.getString("kikiname");
			insRow[model.getColumn("警報・状態")] = rs.getString("message");
			insRow[model.getColumn("種別")] = rs.getString("priorityname");
		}

		private void removeRow(
				AlarmTableModel model,
				DataValueChangeEventKey key) {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, model
						.getColumn("point"))).intValue();
				String pro = (String) model.getValueAt(row, model
						.getColumn("provider"));
				String hol = (String) model.getValueAt(row, model
						.getColumn("holder"));
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					model.removeRow(row, key);
					break;
				}
			}
		}

		private void modifyRow(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, model
						.getColumn("point"))).intValue();
				String pro = (String) model.getValueAt(row, model
						.getColumn("provider"));
				String hol = (String) model.getValueAt(row, model
						.getColumn("holder"));
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					int colSize = model.getColumnCount();
					Object[] obj = new Object[colSize];
					for (int col = 0; col < colSize; col++) {
						obj[col] = model.getValueAt(row, col);
					}
					obj[model.getColumn("表示色")] = rs.getString("alarm_color");
					obj[model.getColumn("復旧・停止")] = key.getTimeStamp();
					obj[model.getColumn("警報・状態")] = rs.getString("message");
					model.setValueAt(obj, row, model.getColumn("表示色"), key);
					model.setValueAt(obj, row, model.getColumn("復旧・停止"), key);
					model.setValueAt(obj, row, model.getColumn("警報・状態"), key);
					break;
				}
			}
		}
	}

	/**
	 * PostgreSQL用の未復旧テーブルモデル操作アルゴリズムの実装クラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static final class OccurrenceStrategy implements RowDataStrategy {
		private final StrategyUtility utility;
		private final AlarmTableModel model;

		OccurrenceStrategy(StrategyUtility utility, AlarmTableModel model) {
			this.utility = utility;
			this.model = model;
		}

		public void renewRow(DataValueChangeEventKey key) throws AlarmException {

			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				stmt = con.prepareStatement(utility
						.getPrepareStatement("/occurrence/renewsql"));
				stmt.setInt(1, key.getPoint());
				stmt.setString(2, key.getProvider());
				stmt.setString(3, key.getHolder());
				stmt.setBoolean(4, key.getValue().booleanValue());
				rs = stmt.executeQuery();
				// att.check_type = '1'は無視する
				if (rs.next()) {
					doInsert(rs, model, key);
				}
			} catch (Exception e) {
				throw new AlarmException(e);
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
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}

		private void doInsert(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			boolean value = key.getValue().booleanValue();
			if (value) {
				addRowOndate(rs, model, key);
			} else {
				removeRow(model, key);
			}
		}

		private void addRowOndate(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			int row = model.searchRow(key);
			if (row < 0) {
				Object[] insRow = new Object[model.getColumnCount()];
				addRowCommon(rs, model, key, insRow);
				insRow[model.getColumn("発生・運転")] = key.getTimeStamp();
				insRow[model.getColumn("復旧・停止")] = null;
				model.insertRow(0, insRow, key);
			} else {
				int colSize = model.getColumnCount();
				Object[] obj = new Object[colSize];
				for (int col = 0; col < colSize; col++) {
					obj[col] = model.getValueAt(row, col);
				}
				obj[model.getColumn("表示色")] = rs.getString("alarm_color");
				obj[model.getColumn("発生・運転")] = key.getTimeStamp();
				obj[model.getColumn("復旧・停止")] = null;
				obj[model.getColumn("警報・状態")] = rs.getString("message");
				model.setValueAt(obj, row, model.getColumn("表示色"), key);
				model.setValueAt(obj, row, model.getColumn("発生・運転"), key);
				model.setValueAt(obj, row, model.getColumn("復旧・停止"), key);
				model.setValueAt(obj, row, model.getColumn("警報・状態"), key);
			}
		}

		private void addRowCommon(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key,
				Object[] insRow) throws SQLException {
			insRow[model.getColumn("ジャンプパス")] = rs.getString("jump_path");
			insRow[model.getColumn("自動ジャンプ")] = Boolean.valueOf(rs
					.getBoolean("auto_jump_flag"));
			insRow[model.getColumn("優先順位")] = new Integer(rs
					.getInt("auto_jump_priority"));
			insRow[model.getColumn("表示色")] = rs.getString("alarm_color");
			insRow[model.getColumn("point")] = new Integer(key.getPoint());
			insRow[model.getColumn("provider")] = key.getProvider();
			insRow[model.getColumn("holder")] = key.getHolder();
			insRow[model.getColumn("記号")] = rs.getString("unit");
			insRow[model.getColumn("名称")] = rs.getString("kikiname");
			insRow[model.getColumn("警報・状態")] = rs.getString("message");
			insRow[model.getColumn("種別")] = rs.getString("priorityname");
		}

		private void removeRow(
				AlarmTableModel model,
				DataValueChangeEventKey key) {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, "point")).intValue();
				String pro = (String) model.getValueAt(row, "provider");
				String hol = (String) model.getValueAt(row, "holder");
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					model.removeRow(row, key);
					break;
				}
			}
		}
	}

	/**
	 * PostgreSQL用の未確認テーブルモデル操作アルゴリズムの実装クラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	static final class NoncheckStrategy implements RowDataStrategy {
		// private static final Logger logger =
		// Logger.getLogger(NoncheckStrategy.class);
		private final StrategyUtility utility;
		private final AlarmTableModel model;

		NoncheckStrategy(StrategyUtility utility, AlarmTableModel model) {
			this.utility = utility;
			this.model = model;
		}

		public void renewRow(DataValueChangeEventKey key) throws AlarmException {

			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;
			try {
				con = ConnectionUtil.getConnection();
				stmt = con.prepareStatement(utility
						.getPrepareStatement("/noncheck/renewsql"));
				stmt.setInt(1, key.getPoint());
				stmt.setString(2, key.getProvider());
				stmt.setString(3, key.getHolder());
				stmt.setBoolean(4, key.getValue().booleanValue());
				rs = stmt.executeQuery();
				// att.check_type = '1'は無視する
				if (rs.next()) {
					doInsert(rs, model, key);
				}
			} catch (Exception e) {
				throw new AlarmException(e);
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
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						con = null;
					}
				}
			}
		}

		private void doInsert(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			int mode = rs.getInt("history_mode");
			boolean value = key.getValue().booleanValue();

			if (mode == 1 && value == false) {
				addRowOffdate(rs, model, key);
			} else if (mode == 2 && value == true) {
				addRowOndate(rs, model, key);
			} else if (mode == 3) {
				if (value == true) {
					addRowOndate(rs, model, key);
				} else {
					modifyRow(rs, model, key);
				}
			} else if (mode == 4) {
				if (value == true) {
					addRowOndate(rs, model, key);
				} else {
					removeRow(model, key);
				}
			} else if (mode == 5) {
				if (value == true) {
					removeRow(model, key);
				} else {
					addRowOffdate(rs, model, key);
				}
			}
		}

		private void addRowOndate(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			Object[] insRow = new Object[model.getColumnCount()];
			addRowCommon(rs, model, key, insRow);
			insRow[model.getColumn("発生・運転")] = key.getTimeStamp();
			insRow[model.getColumn("復旧・停止")] = null;
			model.insertRow(0, insRow, key);
		}

		private void addRowOffdate(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			Object[] insRow = new Object[model.getColumnCount()];
			addRowCommon(rs, model, key, insRow);
			insRow[model.getColumn("発生・運転")] = null;
			insRow[model.getColumn("復旧・停止")] = key.getTimeStamp();
			model.insertRow(0, insRow, key);
		}

		private void addRowCommon(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key,
				Object[] insRow) throws SQLException {
			insRow[model.getColumn("ジャンプパス")] = rs.getString("jump_path");
			insRow[model.getColumn("自動ジャンプ")] = Boolean.valueOf(rs
					.getBoolean("auto_jump_flag"));
			insRow[model.getColumn("優先順位")] = new Integer(rs
					.getInt("auto_jump_priority"));
			insRow[model.getColumn("表示色")] = rs.getString("alarm_color");
			insRow[model.getColumn("point")] = new Integer(key.getPoint());
			insRow[model.getColumn("provider")] = key.getProvider();
			insRow[model.getColumn("holder")] = key.getHolder();
			insRow[model.getColumn("記号")] = rs.getString("unit");
			insRow[model.getColumn("名称")] = rs.getString("kikiname");
			insRow[model.getColumn("種別")] = rs.getString("priorityname");
			if (rs.getBoolean("check_type")) {
				insRow[model.getColumn("確認")] = null;
			} else {
				insRow[model.getColumn("確認")] = "────";
			}
		}

		private void removeRow(
				AlarmTableModel model,
				DataValueChangeEventKey key) {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, "point")).intValue();
				String pro = (String) model.getValueAt(row, "provider");
				String hol = (String) model.getValueAt(row, "holder");
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					model.removeRow(row, key);
					break;
				}
			}
		}

		private void modifyRow(
				ResultSet rs,
				AlarmTableModel model,
				DataValueChangeEventKey key) throws SQLException {
			for (int row = 0, mc = model.getRowCount(); row < mc; row++) {
				int po = ((Integer) model.getValueAt(row, "point")).intValue();
				String pro = (String) model.getValueAt(row, "provider");
				String hol = (String) model.getValueAt(row, "holder");
				if (key.getPoint() == po && pro.equals(key.getProvider())
						&& hol.equals(key.getHolder())) {
					int colSize = model.getColumnCount();
					Object[] obj = new Object[colSize];
					for (int col = 0; col < colSize; col++) {
						obj[col] = model.getValueAt(row, col);
					}
					obj[model.getColumn("表示色")] = rs.getString("alarm_color");
					obj[model.getColumn("復旧・停止")] = key.getTimeStamp();
					model.setValueAt(obj, row, model.getColumn("表示色"), key);
					model.setValueAt(obj, row, model.getColumn("復旧・停止"), key);
					break;
				}
			}
		}
	}
}
