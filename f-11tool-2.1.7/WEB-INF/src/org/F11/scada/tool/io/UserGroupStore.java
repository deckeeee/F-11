/*
 * Created on 2003/08/19
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.tool.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.F11.scada.tool.login.UserPermission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori
 */
public class UserGroupStore {
	protected Log log = LogFactory.getLog(this.getClass());

	public List getAllUser(StrategyUtility util) throws SQLException {
		List ret = util.executeQuery("/tool/user/read/all", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					ret.add(rs.getString("username"));
				}
				return ret;
			}
		});
		return ret;
	}

	public void removeUser(StrategyUtility util, final String user) throws SQLException {
		util.executeUpdate("/tool/user/remove", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, user);
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
	}

	public void insertUser(StrategyUtility util, final String user, final String pass)
		throws SQLException {
		util.executeUpdate("/tool/user/insert", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, user);
				st.setString(2, pass);
				st.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
	}

	public void updateUserPassword(StrategyUtility util, final String user, final String pass)
		throws SQLException {
		util.executeUpdate("/tool/user/update", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, pass);
				st.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				st.setString(3, user);
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
	}

	public boolean checkUserPassword(StrategyUtility util, final String user, final String pass)
		throws SQLException {
		List ret = util.executeQuery("/tool/user/read/password", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, user);
			}

			public List getResult(ResultSet rs) throws SQLException {
				if (rs.next()) {
					if (pass.equals(rs.getString("password"))) {
						return new ArrayList();
					}
				}
				return null;
			}
		});
		if (ret != null)
			return true;
		return false;
	}

	public UserPermission getUserPermission(StrategyUtility util, final String user)
		throws SQLException {
		List ret = util.executeQuery("/tool/user/read/permission", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, user);
				st.setString(2, user);
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					ret.add(rs.getString("name"));
				}
				return ret;
			}
		});

		UserPermission perm = new UserPermission();
		perm.setName(user);
		for (Iterator it = ret.iterator(); it.hasNext();) {
			perm.addPermission((String) it.next());
		}
		return perm;
	}

	public boolean isExistUser(StrategyUtility util, final String user) throws SQLException {
		List ret = util.executeQuery("/tool/user/read/password", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, user);
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				if (rs.next()) {
					ret.add(rs.getString("password"));
				}
				return ret;
			}
		});

		boolean exist = false;
		if (0 < ret.size())
			exist = true;
		return exist;
	}

	public List getAllGroups(StrategyUtility util) throws SQLException {
		List ret = util.executeQuery("/tool/group/read/all", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					ret.add(rs.getString("groupname"));
				}
				return ret;
			}
		});
		return ret;
	}

	public List getAssignUsers(StrategyUtility util, final String group)
		throws SQLException {
		List ret = util.executeQuery("/tool/group/read", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, group);
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					ret.add(rs.getString("username"));
				}
				return ret;
			}
		});
		return ret;
	}

	public void removeGroup(StrategyUtility util, final String group) throws SQLException {
		util.executeUpdate("/tool/group/remove", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, group);
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
	}

	public void setUserAssign(StrategyUtility util, String group, String[] users)
		throws SQLException {
		log.debug("editUserAssign start!");
		removeGroup(util, group);

		UserAssignStrategy query = new UserAssignStrategy(group);
		for (int i = 0; i < users.length; i++) {
			query.setUser(users[i]);
			util.executeUpdate("/tool/group/insert", query);
		}
	}

	private class UserAssignStrategy implements QueryStrategy {
		private String user;
		private String group;
		UserAssignStrategy(String group) {
			this.group = group;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			st.setString(1, user);
			st.setString(2, group);
			st.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		}

		public List getResult(ResultSet rs) throws SQLException {
			return null;
		}
	}

	public boolean isExistGroup(StrategyUtility util, final String group) throws SQLException {
		if (0 < getAssignUsers(util, group).size())
			return true;
		return false;
	}
}
