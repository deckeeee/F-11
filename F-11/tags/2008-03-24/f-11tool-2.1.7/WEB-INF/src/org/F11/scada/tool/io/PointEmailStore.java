/*
 * Created on 2003/08/22
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.io;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.F11.scada.tool.point.email.PointEmailBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori
 */
public class PointEmailStore {
	protected Log log = LogFactory.getLog(this.getClass());

	public List getAllPointEmail(StrategyUtility util) throws SQLException {
		List ret =
			util.executeQuery("/tool/email/read/all", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					PointEmailBean bean = new PointEmailBean();
					bean.setGroup_id(rs.getInt("email_group_id"));
					bean.setAddress(rs.getString("email_address"));
					ret.add(bean);
				}
				return ret;
			}
		});
		return ret;
	}
	public int getCountPointEmail(
		StrategyUtility util,
		final PointEmailBean bean)
		throws IOException, SQLException {
		List ret = util.executeQuery("/tool/email/read", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setInt(1, bean.getGroup_id());
				st.setString(2, bean.getAddress());
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					PointEmailBean bean = new PointEmailBean();
					bean.setGroup_id(rs.getInt("email_group_id"));
					bean.setAddress(rs.getString("email_address"));
					ret.add(bean);
				}
				return ret;
			}
		});
		return ret.size();
	}

	public void updatePointEmail(
		StrategyUtility util,
		final PointEmailBean newBean,
		final PointEmailBean oldBean)
		throws IOException, SQLException {
			util.executeUpdate("/tool/email/update", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setInt(1, newBean.getGroup_id());
				st.setString(2, newBean.getAddress());
				st.setInt(3, oldBean.getGroup_id());
				st.setString(4, oldBean.getAddress());
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}

	public void removePointEmail(
		StrategyUtility util,
		final PointEmailBean bean)
		throws IOException, SQLException {
			util.executeUpdate("/tool/email/remove", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setInt(1, bean.getGroup_id());
				st.setString(2, bean.getAddress());
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}

	public void insertPointEmail(
		StrategyUtility util,
		final PointEmailBean bean)
		throws IOException, SQLException {
			util.executeUpdate("/tool/email/insert", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setInt(1, bean.getGroup_id());
				st.setString(2, bean.getAddress());
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}
}
