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
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.F11.scada.WifeUtilities;
import org.F11.scada.util.ConnectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ページ定義XMLファイルパステーブルをアクセスするDAOクラスです
 * このクラスは java.sql.Connection をオープンするので、使用後は
 * 必ず close メソッドを呼び出して接続を閉じて下さい。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageListDAO {
	/** データベース接続の参照です */
	private final Connection con;

	/** Logging API */
	private static Log log = LogFactory.getLog(PageListDAO.class);

	/**
	 * このオブジェクトを初期化します。
	 * Preperence.properties より、データベースコネクションを生成します
	 * 
	 * @throws SQLException データベースコネクションでエラーが発生した場合
	 */
	public PageListDAO() throws SQLException {
		con = ConnectionUtil.getConnection();
	}

	/**
	 * 引数のデータベースコネクションを使用して、このオブジェクトを初期化します。
	 * @param con データベースコネクション
	 */
	public PageListDAO(Connection con) {
		this.con = con;
	}

	/**
	 * データベースコネクションを閉じます。
	 * 
	 * @throws SQLException データベースコネクションでエラーが発生した場合
	 */	
	public void close() throws SQLException {
		con.close();
	}

	/**
	 * ページ定義XMLファイルパステーブルより、全ての行の PageListBean オブジェクト
	 * のコレクションで返します。
	 * 
	 * @return PageListBean オブジェクトのコレクション
	 */
	public Collection getPageList() throws IOException, SQLException {
		Properties properties = new Properties();
		URL url = getClass().getResource("/resources/Sqldefine.properties");
		if (url == null) {
			throw new IllegalStateException("not found property file : /resources/Sqldefine.properties");
		}
		InputStream is = null;
		Statement st = null;
		ResultSet rs = null;
		List list = Collections.EMPTY_LIST;
		DatabaseMetaData metaData = con.getMetaData();
		
		try {
			rs = metaData.getTables("", "", "page_define_table", null);
			// テーブルが存在するか調査
			rs.last();
			if (rs.getRow() <= 0) {
				return list;
			}

			is = url.openStream();
			properties.load(is);
			String keyName = "/" + WifeUtilities.getDBMSName() + "/tool/page/read/all";
			String sql = properties.getProperty(keyName);
			if (sql == null) {
				throw new IllegalStateException(
					"not found property : " + keyName);
			}
			st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(sql);
			rs.last();
			list = new ArrayList(rs.getRow());
			rs.beforeFirst();
			while (rs.next()) {
				PageListBean pb = new PageListBean();
				pb.setPageName(rs.getString("page_name"));
				pb.setPageXmlPath(rs.getString("page_xml_path"));
				list.add(pb);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (is != null) {
				is.close();
			}
		}
		
		return list;
	}
}
