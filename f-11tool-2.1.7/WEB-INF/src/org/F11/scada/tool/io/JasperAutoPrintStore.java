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

package org.F11.scada.tool.io;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.tool.autoprint.AutoPrintForm;

/**
 * Jasperreports使用の自動印刷サーバーのパラメータ格納処理クラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class JasperAutoPrintStore implements AutoPrintStore {
	/** データーベースコネクション */
	private final Connection con;

	/**
	 * Jasperreports使用の自動印刷サーバーのパラメータ格納処理オブジェクトを初期化します
	 * @param con データーベースコネクション
	 */
	public JasperAutoPrintStore(Connection con) {
		this.con = con;
	}

	/**
	 * 自動印刷のパラメーターをアクションフォームのリストで返します
	 * @return 自動印刷のパラメーターをアクションフォームのリストで返します
	 * @throws SQLException
	 */
	public List getAllAutoPrint() throws SQLException {
		String sql = "SELECT DISTINCT task_name FROM autoprint_property_table ORDER BY task_name";
		ArrayList list = new ArrayList();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = con.prepareStatement(sql);
			rs = st.executeQuery();
			while (rs.next()) {
				String taskName = rs.getString("task_name");
				list.add(createAutoPrintForm(taskName));
			}
			return list;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		}
	}

	/**
	 * タスク名の自動印刷のパラメーターをアクションフォームで返します
	 * @param name タスク名
	 * @return タスク名の自動印刷のパラメーターをアクションフォームで返します
	 * @throws IOException
	 * @throws SQLException
	 */
	public AutoPrintForm getAutoPrint(final String name)
			throws IOException, SQLException {
		return createAutoPrintForm(name);
	}

	private AutoPrintForm createAutoPrintForm(String name) throws SQLException {
		String sql = "SELECT value FROM autoprint_property_table WHERE task_name = ? AND property = ?";
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			AutoPrintForm form = new AutoPrintForm();
			form.setName(name);

			st = con.prepareStatement(sql);
			st.setString(1, name);
			st.setString(2, "auto_flag");
			rs = st.executeQuery();
			if (rs.next()) {
				form.setAutoflag(rs.getBoolean("value"));
			}
			rs.close();

			st.setString(2, "day");
			rs = st.executeQuery();
			if (rs.next()) {
				form.setDay(rs.getInt("value"));
				form.setSchedule("MONTHLY");
			}
			rs.close();

			st.setString(2, "hour");
			rs = st.executeQuery();
			if (rs.next()) {
				form.setHour(rs.getInt("value"));
			}
			rs.close();

			st.setString(2, "minute");
			rs = st.executeQuery();
			if (rs.next()) {
				form.setMinute(rs.getInt("value"));
			}
			rs.close();

			st.setString(2, "displayname");
			rs = st.executeQuery();
			if (rs.next()) {
				form.setDisplayname(rs.getString("value"));
			}
			rs.close();
			st.close();
			return form;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		}
	}

	/**
	 * 指定したアクションフォームの内容でパラメーターを格納します
	 * @param form アクションフォーム
	 * @throws IOException
	 * @throws SQLException
	 */
	public void updateAutoPrint(final AutoPrintForm form)
			throws IOException, SQLException {
		PreparedStatement st = null;
		try {
			String sql = "UPDATE autoprint_property_table SET value = ? WHERE task_name = ? AND property = ?";
			st = con.prepareStatement(sql);
			st.setString(2, form.getName());
			if ("MONTHLY".equals(form.getSchedule())) {
				st.setInt(1, form.getDay());
				st.setString(3, "day");
				st.execute();
			}
			st.setInt(1, form.getHour());
			st.setString(3, "hour");
			st.execute();

			st.setInt(1, form.getMinute());
			st.setString(3, "minute");
			st.execute();

			st.setString(1, booleanWrap(form.getAutoflag()));
			st.setString(3, "auto_flag");
			st.execute();
		} finally {
			if (st != null) {
				st.close();
			}
		}
	}
	
	private String booleanWrap(boolean b) {
	   return b ? "1" : "0";
	}
}
