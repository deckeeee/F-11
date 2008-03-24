/*
 * Created on 2003/08/22 To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.io;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.tool.emailgroup.master.EmailMasterBean;
import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori
 */
public class EmailgroupMasterStore {
	protected Log log = LogFactory.getLog(this.getClass());

	public List getAllEmailgroupMaster(StrategyUtility util)
			throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/master/read/all",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						int gno = -1;
						EmailMasterBean bean = new EmailMasterBean();
						while (rs.next()) {
							if (gno != rs.getInt("email_group_id")) {
								gno = rs.getInt("email_group_id");
								bean = new EmailMasterBean();
								bean.setGroup_id(new Integer(gno));
								ret.add(bean);
							}

							if (rs.getInt("kind") == 0) {
								bean.setGroup_name(rs
										.getString("email_address"));
							} else {
								StringBuffer addr = new StringBuffer(bean
										.getAddress());
								if (0 < addr.length()) {
									addr.append(", ");
								}
								addr.append(rs.getString("email_address"));
								bean.setAddress(addr.toString());
							}
						}
						return ret;
					}
				});
		return ret;
	}

	public List getEmailgroupMaster(StrategyUtility util, final int group_id)
			throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/master/read",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, group_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						while (rs.next()) {
							ret.add(rs.getString("email_address"));
						}
						return ret;
					}
				});
		return ret;
	}

	public Integer getNextGroupID(StrategyUtility util) throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/master/max_id",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						if (rs.next()) {
							ret.add(new Integer(rs.getInt("max") + 1));
						}
						return ret;
					}
				});
		return (Integer) ret.get(0);
	}

	public void removeEmailgroupMaster(StrategyUtility util, final int group_id)
			throws IOException, SQLException {
		util.executeUpdate("/tool/emailgroup/master/remove",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, group_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}

	public void insertEmailgroupMaster(StrategyUtility util,
			final int group_id, final int kind, final String address)
			throws IOException, SQLException {
		util.executeUpdate("/tool/emailgroup/master/insert",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, group_id);
						st.setInt(2, kind);
						st.setString(3, address);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}

	public boolean getGroupID(StrategyUtility util, final int group_id) throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/master/checkremove",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, group_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						if (rs.next()) {
							ret.add(new Integer(rs.getInt("checkcount")));
						}
						return ret;
					}
				});
		return ((Integer) ret.get(0)).intValue() > 0;
	}
}
