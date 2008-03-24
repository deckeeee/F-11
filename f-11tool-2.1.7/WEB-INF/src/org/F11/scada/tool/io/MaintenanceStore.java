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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.tool.OperationMasterList;
import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.F11.scada.tool.maintenance.MainteItemBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori
 */
public class MaintenanceStore {
	protected Log log = LogFactory.getLog(this.getClass());

	private OperationMasterList operationList;

	public MaintenanceStore() {
		operationList = new OperationMasterList();
	}

	public List getAllMenteItemList(StrategyUtility util, String name)
		throws IOException, SQLException {
		List list = new ArrayList();
		Map permit = getPermitOperation(util, name);
		for (Iterator it = operationList.getKeys().iterator(); it.hasNext();) {
			String opeKey = (String) it.next();
			Boolean value = (Boolean) permit.get(opeKey);
			String opeName = operationList.getName(opeKey);
			if (value != null && value.booleanValue())
				list.add(new MainteItemBean(opeKey, opeName, true));
			else
				list.add(new MainteItemBean(opeKey, opeName, false));
		}
		return list;
	}

	private Map getPermitOperation(StrategyUtility util, final String name) throws SQLException {
		List ret = util.executeQuery("/tool/maintenance/read", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, name);
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					ret.add(rs.getString("name"));
				}
				return ret;
			}
		});

		Boolean value = new Boolean(true);
		Map map = new HashMap();
		for (Iterator it = ret.iterator(); it.hasNext();) {
			map.put(it.next(), value);
		}
		return map;
	}

	public void modifiGroupAssign(StrategyUtility util, String name, String select[])
		throws SQLException {
		removeGroup(util, name);

		ModifiGroupAssignStrategy query = new ModifiGroupAssignStrategy(name);
		for (int i = 0; i < select.length; i++) {
			query.setSelect(select[i]);
			util.executeUpdate("/tool/maintenance/insert", query);
		}
		log.debug("editUserAssign add end : " + select.length);
	}

	private class ModifiGroupAssignStrategy implements QueryStrategy {
		private final String name;
		private String select;

		ModifiGroupAssignStrategy(String name) {
			this.name = name;
		}

		public void setSelect(String select) {
			this.select = select;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			st.setString(1, name);
			st.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			st.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
			st.setString(2, select);
			log.debug(st.toString());
		}

		public List getResult(ResultSet rs) throws SQLException {
			return null;
		}

	}

	public void removeGroup(StrategyUtility util, final String groupName) throws SQLException {
		util.executeUpdate("/tool/maintenance/remove/group", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, groupName);
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}
}
