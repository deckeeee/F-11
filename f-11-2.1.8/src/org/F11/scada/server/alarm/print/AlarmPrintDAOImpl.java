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

package org.F11.scada.server.alarm.print;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.seasar.extension.jdbc.SelectHandler;
import org.seasar.extension.jdbc.UpdateHandler;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

/**
 * AlarmPrintDAOを実装するクラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintDAOImpl implements AlarmPrintDAO {
	/** S2コンテナ */
	private final S2Container container;

	/**
	 * 引数のS2コンテナで初期化します
	 * @param container S2コンテナ
	 */
	public AlarmPrintDAOImpl(String path) {
		this.container = S2ContainerFactory.create(path);
		this.container.init();
	}

	/**
	 * 未印刷のデータを全て返します。
	 * @return PrintLineDataオブジェクトのリスト
	 * @exception SQLException データベースエラー発生時
	 */
	public List findAll() throws SQLException {
		SelectHandler handler =
			(SelectHandler) container.getComponent("findAllBeanListHandler");
		List result = (List) handler.execute(null);
		return result;
	}

	/**
	 * リストの内容をデータベースに挿入します
	 * @param key データ変更イベント
	 * @exception SQLException データベースエラー発生時
	 */
	public void insert(DataValueChangeEventKey key) throws SQLException {
		UpdateHandler handler =
			(UpdateHandler) container.getComponent("insertHandler");
		Object[] obj = new Object[5];
		obj[0] = new Integer(key.getPoint());
		obj[1] = key.getProvider();
		obj[2] = key.getHolder();
		obj[3] = key.getTimeStamp();
		obj[4] = key.getValue();
		handler.execute(obj);
	}

	/**
	 * 引数のイベントをキーにして印刷データオブジェクトを返します
	 * @param key データ変更イベント
	 * @return 印刷データオブジェクト
	 * @exception SQLException データベースエラー発生時
	 */
	public PrintLineData find(DataValueChangeEventKey key)
		throws SQLException {
		SelectHandler handler =
			(SelectHandler) container.getComponent("findBeanHandler");
		Object[] obj = new Object[5];
		obj[0] = new Integer(key.getPoint());
		obj[1] = key.getProvider();
		obj[2] = key.getHolder();
		obj[3] = key.getTimeStamp();
		obj[4] = key.getValue();
		PrintLineData result = (PrintLineData) handler.execute(obj);
		return result;
	}

	/**
	 * 警報メッセージ印刷データを全て削除します
	 * @exception SQLException データベースエラー発生時
	 */
	public void deleteAll() throws SQLException {
		UpdateHandler handler =
			(UpdateHandler) container.getComponent("deleteAllHandler");
		handler.execute(null);
	}

	/**
	 * データ変更イベントが警報メッセージ印刷の対象かどうかを判定します。
	 * @param key データ変更イベント
	 * @return 警報メッセージ印刷対象なら true を 対象でなければ false を返します
	 * @exception SQLException データベースエラー発生時
	 */
	public boolean isAlarmPrint(DataValueChangeEventKey key) throws SQLException {
		DataSource ds = (DataSource) this.container.getComponent("dataSource");
		Connection con = ds.getConnection();
		try {
			String sql =
				"SELECT a.printer_mode FROM item_table i, attribute_table a, point_table p " +				"WHERE i.attribute_id = a.attribute AND p.point = i.point AND " +				"i.point = ? AND i.provider = ? AND i.holder = ?";
			PreparedStatement st = con.prepareStatement(sql);
			try {
				st.setInt(1, key.getPoint());
				st.setString(2, key.getProvider());
				st.setString(3, key.getHolder());
				ResultSet rs = st.executeQuery();
				try {
					if (rs.next()) {
						return rs.getBoolean("printer_mode");
					}
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			} finally {
				if (st != null) {
					st.close();
				}
			}
		} finally {
			if (con != null) {
				con.close();
			}
		}

		return false;
	}
}
