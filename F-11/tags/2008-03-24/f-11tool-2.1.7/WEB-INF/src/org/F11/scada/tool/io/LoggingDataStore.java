/*
 * 作成日: 2005/10/27
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.server.entity.AnalogType;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.impl.RegisterUtil;
import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.F11.scada.tool.io.parser.Column;
import org.F11.scada.tool.logdata.LoggingName;
import org.F11.scada.tool.logdata.PointItemConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Administrator
 * 
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java - コード・スタイル -
 * コード・テンプレート
 */
public class LoggingDataStore {
    protected Log log = LogFactory.getLog(this.getClass());

    public List getLoggtableNameList(StrategyUtility util) throws SQLException {
        List ret = util.executeQuery("/tool/logdata/read/all",
                new QueryStrategy() {
                    public void setQuery(PreparedStatement st)
                            throws SQLException {
                    }

                    public List getResult(ResultSet rs) throws SQLException {
                        List ret = new ArrayList();
                        while (rs.next()) {
                            ret.add(new LoggingName(rs.getString("name"), rs.getString("logging_table_name")));
                        }
                        return ret;
                    }
                });

        return ret;
    }

    public String getLoggingTableName(StrategyUtility util, final String name)
            throws SQLException {
        List ret = util.executeQuery("/tool/logdata/read", new QueryStrategy() {
            public void setQuery(PreparedStatement st) throws SQLException {
                st.setString(1, name);
            }

            public List getResult(ResultSet rs) throws SQLException {
                List ret = new ArrayList();
                if (rs.next()) {
                    ret.add(rs.getString("logging_table_name"));
                }
                return ret;
            }
        });

        return (String) ret.get(0);
    }

    public Map getLoggingItemMap(StrategyUtility util, Collection columns)
            throws SQLException {
        Map ret = new HashMap();
        Item item = new Item();
        AnalogType type = new AnalogType();
        item.setAnalogType(type);

        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = util.getPrepareStatement("/tool/logdata/point/read");
            for (Iterator it = columns.iterator(); it.hasNext();) {
                Column col = (Column) it.next();
                st.setString(1, col.getProvider());
                st.setString(2, col.getHolder());

                rs = st.executeQuery();
                if (rs.next()) {
                    PointItemConverter pointItem = new PointItemConverter();
                    pointItem.setProvider(col.getProvider());
                    pointItem.setHolder(col.getHolder());

                    pointItem.setPointunit(rs.getString("unit"));
                    pointItem.setPointname(rs.getString("name"));
                    pointItem.setPointunit_mark(rs.getString("unit_mark"));

                    item.setDataType(rs.getInt("data_type"));
                    type.setConvertMin(new Double(rs.getDouble("convert_min")));
                    type.setConvertMax(new Double(rs.getDouble("convert_max")));
                    type.setInputMin(new Double(rs.getDouble("input_min")));
                    type.setInputMax(new Double(rs.getDouble("input_max")));
                    type.setFormat(rs.getString("format"));
                    type.setConvertType(rs.getString("convert_type"));
                    pointItem.setConvertValue(RegisterUtil
                            .getConvertValue(item));

                    ret.put(col.getProviderHolder(), pointItem);
                }
                rs.close();
                rs = null;
            }
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

    public Collection getLoggingData(PreparedStatement st, Collection columns) throws SQLException {
        Collection dataLines = new ArrayList();
        ResultSet rs = null;
        try {
            rs = st.executeQuery();
            while (rs.next()) {
                Map datas = new HashMap();
                datas.put("f_date", rs.getTimestamp("f_date"));
                for (Iterator it = columns.iterator(); it.hasNext();) {
                    Column col = (Column) it.next();
                    Double data = new Double(rs.getDouble("f_"
                            + col.getProviderHolder()));
                    datas.put(col.getProviderHolder(), data);
                }
                dataLines.add(datas);
            }
            rs.close();
            return dataLines;
        } finally {
            if (rs != null) {
            	rs.close();
            }
        }
    }
}