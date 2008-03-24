/*
 * 作成日: 2005/09/21 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.io;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.tool.emailgroup.individual.EmailIndividualBean;
import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 *         コード・スタイル - コード・テンプレート
 */
public class EmailgroupIndividualStore {
	protected Log log = LogFactory.getLog(this.getClass());

	public List getAllEmailgroupIndividual(StrategyUtility util)
			throws SQLException {
//		List ret = util.executeQuery("/tool/emailgroup/indv/read/all",
		List ret = util.executeQuery("/tool/emailgroup/indv/read/all2",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						while (rs.next()) {
							EmailIndividualBean bean = new EmailIndividualBean();
							bean.setPoint(new Integer(rs.getInt("point")));
							bean.setProvider(rs.getString("provider"));
							bean.setHolder(rs.getString("holder"));
							bean.setP_unit(rs.getString("unit"));
							bean.setP_name(rs.getString("name"));
							bean.setP_attrname(rs.getString("attrname"));
							bean
									.setEmail_address(rs
											.getString("email_address"));
							ret.add(bean);
						}
						return ret;
					}
				});
		for (Iterator i = ret.iterator(); i.hasNext();) {
			EmailIndividualBean bean = (EmailIndividualBean) i.next();
			List groups = getGroupIndividual(util, bean.getProvider(), bean.getHolder());
			StringBuffer b = new StringBuffer();
			for (Iterator i2 = groups.iterator(); i2.hasNext();) {
				String groupId = (String) i2.next();
				b.append(groupId);
				if (i2.hasNext()) {
					b.append(", ");
				}
			}
			String email = bean.getEmail_address();
			if (isEmailAddress(b, email)) {
				b.append(", ");
			}
			if (email.length() > 0) {
				b.append(email);
			}

			bean.setEmail_address(b.toString());
		}
		return ret;
	}

	private boolean isEmailAddress(StringBuffer b, String email) {
		return email.length() > 0 && b.length() > 0;
	}

	public List getGroupIndividual(StrategyUtility util, final String provider, final String holder) throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/indv/read3",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setString(1, provider);
						st.setString(2, holder);
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						while (rs.next()) {
							ret.add(rs.getString("email_group_id"));
						}
						return ret;
					}
				});
		return ret;
	}

	public EmailIndividualBean getEmailgroupIndividual(StrategyUtility util,
			final String provider, final String holder) throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/indv/read",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setString(1, provider);
						st.setString(2, holder);
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						if (rs.next()) {
							EmailIndividualBean bean = new EmailIndividualBean();
							bean.setPoint(new Integer(rs.getInt("point")));
							bean.setProvider(rs.getString("provider"));
							bean.setHolder(rs.getString("holder"));
							bean.setP_unit(rs.getString("unit"));
							bean.setP_name(rs.getString("name"));
							bean.setP_attrname(rs.getString("attrname"));
							bean
									.setEmail_address(rs
											.getString("email_address"));
							ret.add(bean);
						}
						return ret;
					}
				});
		return (EmailIndividualBean) ret.get(0);
	}

	public int updateEmailgroupIndividual(StrategyUtility util,
			final String provider, final String holder, final String address)
			throws IOException, SQLException {
		int ret = util.executeUpdate("/tool/emailgroup/indv/update",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setString(1, address);
						st.setString(2, provider);
						st.setString(3, holder);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
		return ret;
	}

	public void insertEmailgroupIndividual(StrategyUtility util,
			final String provider, final String holder, final String address)
			throws IOException, SQLException {
		util.executeUpdate("/tool/emailgroup/indv/insert",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setString(1, provider);
						st.setString(2, holder);
						st.setString(3, address);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}

	public void deleteEmailgroupIndividual(StrategyUtility util,
			final String provider, final String holder) throws IOException,
			SQLException {
		util.executeUpdate("/tool/emailgroup/indv/delete",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setString(1, provider);
						st.setString(2, holder);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}

	public void insertEmailgroupIndividual2(StrategyUtility util,
			final String provider, final String holder, final int groupId)
			throws IOException, SQLException {
		util.executeUpdate("/tool/emailgroup/indv/insert2",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setString(1, provider);
						st.setString(2, holder);
						st.setInt(3, groupId);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}

	public void insertEmailgroupIndividual3(StrategyUtility util,
			final String provider, final String holder, final String address)
			throws IOException, SQLException {
		util.executeUpdate("/tool/emailgroup/indv/insert",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setString(1, provider);
						st.setString(2, holder);
						st.setString(3, address);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}

	public int updateEmailgroupIndividual(StrategyUtility util, final String address, final int attribute_id) throws SQLException {
		int ret = util.executeUpdate("/tool/emailgroup/indv/update2",
				new QueryStrategy() {
			public void setQuery(PreparedStatement st)
					throws SQLException {
				st.setString(1, address);
				st.setInt(2, attribute_id);
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return ret;
	}
}
