/*
 * 作成日: 2005/10/27
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.logdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.tool.io.LoggingDataStore;
import org.F11.scada.tool.io.StrategyUtility;
import org.F11.scada.tool.io.parser.Column;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MakeCsvDataLogic implements MakeCsvService {
	private final Log log = LogFactory.getLog(MakeCsvDataLogic.class);
	private final Connection con;
	private final DataConditionsForm dataCondForm;

	/**
	 * @param dataCondForm
	 * 
	 */
	public MakeCsvDataLogic(Connection con, DataConditionsForm dataCondForm) {
		super();
		this.con = con;
		this.dataCondForm = dataCondForm;
	}

	public List getHeaderData(DataConditionsForm form) {
		CsvCreatorUtil creator = new CsvCreatorUtil();
		return creator.getHeaderData(form, con);
	}

	public List getCsvData(DataConditionsForm form) {
		PreparedStatement st = null;
		try {
			String loggingName = form.getTableString();

			CsvCreatorUtil creator = new CsvCreatorUtil();
			List columns = creator.getColumns(loggingName);

			LoggingDataStore store = new LoggingDataStore();
			StrategyUtility util = new StrategyUtility(con);
			Map itemMap = store.getLoggingItemMap(util, columns);

			String sql = getSql(loggingName);
			st = con.prepareStatement(
					sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			if (form.isStEneble()) {
				st.setTimestamp(1, creator.getStartTime(form));
			} else {
				st.setTimestamp(1, new Timestamp(0));
			}
			if (form.isEtEneble()) {
				st.setTimestamp(2, creator.getEndTime(form));
			} else {
				st.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			}
			Collection dataList = store.getLoggingData(st, columns);
			st.close();
			return getCsvLoggingData(dataList, columns, itemMap);
		} catch (Exception e) {
			log.error("sql exeute error : ", e);
			return Collections.EMPTY_LIST;
		} catch (OutOfMemoryError e) {
			log.error("メモリ不足になったと思われます。もうすこし短い期間の日付指定を入力してください。");
			log.error("sql exeute error : ", e);
			throw e;
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

	private String getSql(String loggingName) {
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("SELECT * FROM ").append(loggingName);
		sqlbuf.append(" WHERE f_revision=0 AND ?<=f_date AND f_date<?");
		sqlbuf.append(" ORDER BY f_date");
		String sql = sqlbuf.toString();
		return sql;
	}

	private List getCsvLoggingData(
			Collection dataList,
			Collection columns,
			Map itemMap) {
		ArrayList csvLines = new ArrayList();
		for (Iterator it = dataList.iterator(); it.hasNext();) {
			Map datas = (Map) it.next();
			StringBuffer sb = new StringBuffer();

			Timestamp datetime = (Timestamp) datas.get("f_date");
			DateFormat dateformater = getFormat();
			sb.append(dateformater.format(new Date(datetime.getTime())));

			for (Iterator it2 = columns.iterator(); it2.hasNext();) {
				Column col = (Column) it2.next();
				Double data = (Double) datas.get(col.getProviderHolder());
				PointItemConverter item = (PointItemConverter) itemMap.get(col
						.getProviderHolder());
				ConvertValue cv = item.getConvertValue();

				sb.append(",");
				sb.append(cv.convertStringValue(data.doubleValue()));
			}
			csvLines.add(sb.toString());
		}
		return csvLines;
	}

	private DateFormat getFormat() {
		return "goda".equals(dataCondForm.getHeadString())
				? new SimpleDateFormat("yyyy/MM/dd HH:mm")
				: new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");
	}

}