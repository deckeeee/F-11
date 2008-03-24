/*
 * $Header: /home/cvsroot/f-11tool/WEB-INF/src/org/F11/scada/tool/io/StrategyUtility.java,v 1.4.2.2 2007/01/11 08:06:17 maekawa Exp $
 * $Revision: 1.4.2.2 $
 * $Date: 2007/01/11 08:06:17 $
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

package org.F11.scada.tool.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.F11.scada.WifeUtilities;
import org.F11.scada.tool.alist.RefConditionsForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * データベース接続とプロパティモデルを生成します。
 * 
 * @author Hideaki Maekawa <maekawa@frdm.co.jp>
 */
public final class StrategyUtility {
	private final Log log = LogFactory.getLog(StrategyUtility.class);
	/** データベース接続 */
	private final Connection con;
	/** プロパティーシート */
	private final Properties properties;

	/**
	 * /resources/Sqldefine.propertiesを読みプロパティを生成します。
	 */
	public StrategyUtility(Connection con) throws IOException {
		properties = new Properties();
		URL url = getClass().getResource("/resources/Sqldefine.properties");
		if (url == null) {
			throw new IllegalStateException(
					"not found property file : /resources/Sqldefine.properties");
		}

		InputStream is = null;
		try {
			is = url.openStream();
			properties.load(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
		this.con = con;
	}

	/**
	 * プリコンパイルされたSQLステートメントを返します。
	 * 
	 * @param key SQL取得プロパティーキー値
	 * @return PreparedStatement
	 */
	public PreparedStatement getPrepareStatement(String key)
			throws SQLException {
		PreparedStatement sp = con.prepareStatement(getProperty(key));
		if (sp == null) {
			throw new IllegalStateException("not found property : /"
					+ WifeUtilities.getDBMSName() + key);
		}
		return sp;
	}

	private String getProperty(String key) {
		String keyName = "/" + WifeUtilities.getDBMSName() + key;
		return properties.getProperty(keyName);
	}

	public List executeQuery(String key, QueryStrategy query)
			throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = getPrepareStatement(key);
			query.setQuery(st);

			rs = st.executeQuery();
			List ret = query.getResult(rs);
			rs.close();
			rs = null;
			st.close();
			st = null;
			return ret;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					st = null;
				}
			}
		}
	}

	public int executeUpdate(String key, QueryStrategy query)
			throws SQLException {
		PreparedStatement st = null;
		try {
			st = getPrepareStatement(key);
			query.setQuery(st);

			int ret = st.executeUpdate();
			st.close();
			st = null;
			return ret;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					st = null;
				}
			}
		}
	}

	public List executeQuery(
			String key,
			QueryStrategy query,
			RefConditionsForm form) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = getPrepareStatement(key, form);
			query.setQuery(st);

			rs = st.executeQuery();
			List ret = query.getResult(rs);
			rs.close();
			rs = null;
			st.close();
			st = null;
			return ret;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					st = null;
				}
			}
		}
	}

	private PreparedStatement getPrepareStatement(
			String key,
			RefConditionsForm form) throws SQLException {
		PreparedStatement sp = con.prepareStatement(getProperty(key, form));
		if (sp == null) {
			throw new IllegalStateException("not found property : /"
					+ WifeUtilities.getDBMSName() + key);
		}
		return sp;
	}

	private String getProperty(String key, RefConditionsForm form) {
		return createLike(getProperty(key), form);
	}

	private String createLike(String sql, RefConditionsForm form) {
		String lastsql = sql.replaceFirst("\\$LIKE", createLike(form));
		if (log.isDebugEnabled()) {
			log.debug(lastsql);
		}
		return lastsql;
	}

	private String createLike(RefConditionsForm form) {
		StringBuffer buffer = new StringBuffer();
		if (isNotNull(form)) {
			for (StringTokenizer tokenizer = new StringTokenizer(form
					.getFindString(), " "); tokenizer.hasMoreTokens(); tokenizer
					.nextToken()) {
				if (tokenizer.hasMoreTokens()) {
					buffer.append("AND ");
				}
				buffer.append("(p.name LIKE ? OR p.unit LIKE ?) ");
			}
		}
		return buffer.toString();
	}

	private boolean isNotNull(RefConditionsForm form) {
		String findStr = form.getFindString();
		return null != findStr && !"".equals(findStr);
	}

	public interface QueryStrategy {
		public void setQuery(PreparedStatement st) throws SQLException;

		public List getResult(ResultSet rs) throws SQLException;
	}

}
