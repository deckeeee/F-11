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

import org.F11.scada.tool.emailgroup.attribute.EmailAttributeBean;
import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 *         コード・スタイル - コード・テンプレート
 */
public class EmailgroupAttributeStore {
	protected Log log = LogFactory.getLog(this.getClass());

	public List getAllEmailgroupAttribute(StrategyUtility util)
			throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/attr/read/all",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						while (rs.next()) {
							EmailAttributeBean bean = new EmailAttributeBean();
							bean.setAttribute_id(new Integer(rs
									.getInt("attribute")));
							bean.setAttribute_name(rs.getString("name"));
							ret.add(bean);
						}
						return ret;
					}
				});
		for (Iterator i = ret.iterator(); i.hasNext();) {
			EmailAttributeBean bean = (EmailAttributeBean) i.next();
			List groups = getAllEmailgroupAttribute2(util, bean.getAttribute_id().intValue());
			StringBuffer b = new StringBuffer();
			for (Iterator i2 = groups.iterator(); i2.hasNext();) {
				String groupId = (String) i2.next();
				b.append(groupId);
				if (i2.hasNext()) {
					b.append(", ");
				}
			}
			List emails = getAllEmailgroupAttribute3(util, bean.getAttribute_id().intValue());
			if (isEmailAddress(b, emails)) {
				b.append(", ");
			}
			if (!emails.isEmpty()) {
				b.append(emails.get(0));
			}
			bean.setEmail_address(b.toString());
		}
		return ret;
	}

	private boolean isEmailAddress(StringBuffer b, List emails) {
		if (emails.isEmpty()) {
			return false;
		}
		String email = (String) emails.get(0);
		return email.length() > 0 && b.length() > 0;
	}

	public List getAllEmailgroupAttribute2(StrategyUtility util, final int attributeId)
			throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/attr/read/all2",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attributeId);
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

	private List getAllEmailgroupAttribute3(StrategyUtility util, final int attributeId)
			throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/attr/read/all3",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attributeId);
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

	public EmailAttributeBean getEmailgroupAttribute(StrategyUtility util,
			final int attribute_id) throws SQLException {
		List ret = util.executeQuery("/tool/emailgroup/attr/read",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attribute_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						if (rs.next()) {
							EmailAttributeBean bean = new EmailAttributeBean();
							bean.setAttribute_id(new Integer(rs
									.getInt("attribute")));
							bean.setAttribute_name(rs.getString("name"));
							bean
									.setEmail_address(rs
											.getString("email_address"));
							ret.add(bean);
						}
						return ret;
					}
				});
		return (EmailAttributeBean) ret.get(0);
	}

	public int updateEmailgroupAttribute(StrategyUtility util,
			final int attribute_id, final String address) throws IOException,
			SQLException {
		int ret = util.executeUpdate("/tool/emailgroup/attr/update",
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

	public void insertEmailgroupAttribute(StrategyUtility util,
			final int attribute_id, final String address) throws IOException,
			SQLException {
		util.executeUpdate("/tool/emailgroup/attr/insert",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attribute_id);
						st.setString(2, address);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
		return;
	}

	public int deleteEmailgroupAttribute(StrategyUtility util,
			final int attribute_id) throws IOException,
			SQLException {
		int ret = util.executeUpdate("/tool/emailgroup/attr/delete",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attribute_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
		deleteEmailgroupAttributeIndividual(util, attribute_id);
		return ret;
	}

	private void deleteEmailgroupAttributeIndividual(StrategyUtility util,
			final int attribute_id) throws SQLException {
		util.executeUpdate("/tool/emailgroup/attr/item/delete",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attribute_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
	}

	public void insertEmailgroupAttribute2(StrategyUtility util,
			final int attribute_id, final int groupId) throws IOException,
			SQLException {
		util.executeUpdate("/tool/emailgroup/attr/insert2",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attribute_id);
						st.setInt(2, groupId);
					}

					public List getResult(ResultSet rs) throws SQLException {
						return null;
					}
				});
		return;
	}

	public void updateEmailgroupIndividual(StrategyUtility util,
			final int attribute_id, final String address) throws IOException,
			SQLException {
		List ret = util.executeQuery("/tool/emailgroup/attr/item/read",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attribute_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						while (rs.next()) {
							AttributeKeyItemBean bean = new AttributeKeyItemBean();
							bean.setProvider(rs.getString("provider"));
							bean.setHolder(rs.getString("holder"));
							ret.add(bean);
						}
						return ret;
					}
				});
		log.debug("item_table rec=" + ret.size() + " attr=" + attribute_id);
		EmailgroupIndividualStore indibidualStor = new EmailgroupIndividualStore();

		for (Iterator it = ret.iterator(); it.hasNext();) {
			AttributeKeyItemBean bean = (AttributeKeyItemBean) it.next();
			indibidualStor.insertEmailgroupIndividual3(util, bean.getProvider(),
					bean.getHolder(), address);
		}

	}

	public void updateEmailgroupIndividual2(StrategyUtility util,
			final int attribute_id, final int groupId) throws IOException,
			SQLException {
		List ret = util.executeQuery("/tool/emailgroup/attr/item/read",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
						st.setInt(1, attribute_id);
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						while (rs.next()) {
							AttributeKeyItemBean bean = new AttributeKeyItemBean();
							bean.setProvider(rs.getString("provider"));
							bean.setHolder(rs.getString("holder"));
							ret.add(bean);
						}
						return ret;
					}
				});
		log.debug("item_table rec=" + ret.size() + " attr=" + attribute_id);
		EmailgroupIndividualStore indibidualStor = new EmailgroupIndividualStore();

		for (Iterator it = ret.iterator(); it.hasNext();) {
			AttributeKeyItemBean bean = (AttributeKeyItemBean) it.next();
			indibidualStor.insertEmailgroupIndividual2(util, bean.getProvider(),
					bean.getHolder(), groupId);
		}
	}
}
