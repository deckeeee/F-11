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
import org.F11.scada.tool.point.name.PointNameBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori
 */
public class PointItemStore {
	protected Log log = LogFactory.getLog(this.getClass());

	public PointNameBean getPointName(StrategyUtility util, final int point)
			throws IOException,
			SQLException {
		List ret = util.executeQuery("/tool/point/read", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setInt(1, point);
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				if (rs.next()) {
					PointNameBean bean = new PointNameBean();
					bean.setPoint(rs.getInt("point"));
					bean.setUnit(rs.getString("unit"));
					bean.setName(rs.getString("name"));
					bean.setUnit_mark(rs.getString("unit_mark"));
					bean.setAttribute1(rs.getString("attribute1"));
					bean.setAttribute2(rs.getString("attribute2"));
					bean.setAttribute3(rs.getString("attribute3"));
					ret.add(bean);
				}
				return ret;
			}
		});
		if (ret.size() <= 0)
			return new PointNameBean();
		return (PointNameBean) ret.get(0);
	}

	public void updatePointName(StrategyUtility util, final PointNameBean point)
			throws IOException,
			SQLException {
		util.executeUpdate("/tool/point/update", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, point.getUnit());
				st.setString(2, point.getName());
				st.setString(3, point.getUnit_mark());
				st.setString(4, point.getAttribute1());
				st.setString(5, point.getAttribute2());
				st.setString(6, point.getAttribute3());
				st.setInt(7, point.getPoint());
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}

	public void removePointName(StrategyUtility util, final int point)
			throws IOException,
			SQLException {
		util.executeUpdate("/tool/point/remove", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setInt(1, point);
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}

	public void insertPointName(StrategyUtility util, final PointNameBean point)
			throws IOException,
			SQLException {
		util.executeUpdate("/tool/point/insert", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setInt(1, point.getPoint());
				st.setString(2, point.getUnit());
				st.setString(3, point.getName());
				st.setString(4, point.getUnit_mark());
				st.setString(5, point.getAttribute1());
				st.setString(6, point.getAttribute2());
				st.setString(7, point.getAttribute3());
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}
}
