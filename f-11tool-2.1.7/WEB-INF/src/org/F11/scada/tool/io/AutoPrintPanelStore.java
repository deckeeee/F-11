/*
 * Created on 2003/08/22
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
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
import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori
 */
public class AutoPrintPanelStore implements AutoPrintStore {
	protected Log log = LogFactory.getLog(this.getClass());
	private final StrategyUtility util;

	public AutoPrintPanelStore(Connection con) throws IOException {
		util = new StrategyUtility(con);
	}

	public List getAllAutoPrint() throws SQLException {
		List ret = util.executeQuery("/tool/autoprint/read/all", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				while (rs.next()) {
					AutoPrintForm form = new AutoPrintForm();
					form.setName(rs.getString("name"));
					form.setSchedule(rs.getString("schedule"));
					form.setAutoflag(rs.getBoolean("auto_flag"));
					form.setParamdate(rs.getTimestamp("paramdate"));
					form.setDisplayname(rs.getString("displayname"));
					ret.add(form);
				}
				return ret;
			}
		});
		return ret;
	}

	public AutoPrintForm getAutoPrint(final String name)
		throws IOException, SQLException {
		List ret = util.executeQuery("/tool/autoprint/read", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setString(1, name);
			}

			public List getResult(ResultSet rs) throws SQLException {
				List ret = new ArrayList();
				if (rs.next()) {
					AutoPrintForm form = new AutoPrintForm();
					form.setName(rs.getString("name"));
					form.setSchedule(rs.getString("schedule"));
					form.setAutoflag(rs.getBoolean("auto_flag"));
					form.setParamdate(rs.getTimestamp("paramdate"));
					form.setDisplayname(rs.getString("displayname"));
					ret.add(form);
				}
				return ret;
			}
		});
		if (ret.size() <= 0)
			return new AutoPrintForm();
		return (AutoPrintForm) ret.get(0);
	}

	public void updateAutoPrint(final AutoPrintForm form)
		throws IOException, SQLException {
		util.executeUpdate("/tool/autoprint/update", new QueryStrategy() {
			public void setQuery(PreparedStatement st) throws SQLException {
				st.setBoolean(1, form.getAutoflag());
				st.setTimestamp(2, form.getParamdate());
				st.setString(3, form.getName());
			}

			public List getResult(ResultSet rs) throws SQLException {
				return null;
			}
		});
		return;
	}
}
