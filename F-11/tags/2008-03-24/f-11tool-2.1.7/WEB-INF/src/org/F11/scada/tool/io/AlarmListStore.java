/*
 * Created on 2003/10/10
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
import java.util.List;
import java.util.StringTokenizer;

import org.F11.scada.tool.alist.RefConditionsForm;
import org.F11.scada.tool.alist.career.CareerRecordBean;
import org.F11.scada.tool.alist.history.HistoryRecordBean;
import org.F11.scada.tool.alist.summary.SummaryRecordBean;
import org.F11.scada.tool.io.StrategyUtility.QueryStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hori
 */
public class AlarmListStore {
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * 属性名一覧をリストで返します
	 * 
	 * @param util
	 * @return
	 * @throws SQLException
	 */
	public List getAttributeNameList(StrategyUtility util) throws SQLException {
		List ret = util.executeQuery(
				"/tool/attribute/read",
				new QueryStrategy() {
					public void setQuery(PreparedStatement st)
							throws SQLException {
					}

					public List getResult(ResultSet rs) throws SQLException {
						List ret = new ArrayList();
						while (rs.next()) {
							ret.add(rs.getString("name"));
						}
						return ret;
					}
				});

		return ret;
	}

	/**
	 * 履歴一覧CSVダウンロードのデータを返します
	 * 
	 * @param util
	 * @param refcond
	 * @return
	 * @throws SQLException
	 */
	public List getAllCareerList(StrategyUtility util, RefConditionsForm refcond)
			throws SQLException {
		List ret = util.executeQuery(
				"/tool/career/readlike",
				new CareerListMultiFindStrategy(refcond, 65535, 0),
				refcond);

		return ret;
	}

	/**
	 * 履歴一覧検索のデータを返します
	 * 
	 * @param util
	 * @param refcond
	 * @param offset
	 * @return
	 * @throws SQLException
	 */
	public List getCareerList(
			StrategyUtility util,
			RefConditionsForm refcond,
			long offset) throws SQLException {
		List ret = util.executeQuery(
				"/tool/career/readlike",
				new CareerListMultiFindStrategy(
						refcond,
						refcond.getLimit(),
						offset),
				refcond);

		return ret;
	}

	private abstract class AbstractCareerStrategy implements QueryStrategy {
		protected void setCareerCondition(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			Timestamp now = new Timestamp(System.currentTimeMillis());

			if (refcond.getStEneble()) {
				st.setTimestamp(1, refcond.getStTime());
			} else {
				st.setTimestamp(1, new Timestamp(0));
			}
			if (refcond.getEtEneble()) {
				st.setTimestamp(2, refcond.getEtTime());
			} else {
				st.setTimestamp(2, now);
			}
			String messtr = refcond.getMsgString();
			if (messtr != null && 0 < messtr.length()) {
				st.setString(3, messtr);
			} else {
				st.setString(3, "%");
			}
			if ("true".equals(refcond.getBitval())) {
				st.setBoolean(4, true);
				st.setBoolean(5, true);
			} else if ("false".equals(refcond.getBitval())) {
				st.setBoolean(4, false);
				st.setBoolean(5, false);
			} else {
				st.setBoolean(4, true);
				st.setBoolean(5, false);
			}
			findString(st, refcond);
		}

		protected abstract void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException;
	}

	private abstract class DefaultCareerStrategy extends AbstractCareerStrategy {
		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			String findstr = refcond.getFindString();
			if (findstr != null && 0 < findstr.length()) {
				st.setString(6, "%" + findstr + "%");
			} else {
				st.setString(6, "%");
			}
		}
	}

	private class CareerListStrategy extends DefaultCareerStrategy {
		protected final RefConditionsForm refcond;
		protected final long limit;
		protected final long offset;

		CareerListStrategy(RefConditionsForm refcond, long limit, long offset) {
			this.refcond = refcond;
			this.limit = limit;
			this.offset = offset;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setCareerCondition(st, refcond);
			st.setLong(7, limit);
			st.setLong(8, offset);
			log.debug(st.toString());
		}

		public List getResult(ResultSet rs) throws SQLException {
			List ret = new ArrayList();
			while (rs.next()) {
				CareerRecordBean form = new CareerRecordBean();
				form.setAlarm_color(rs.getString("alarm_color"));
				form.setEntrydate(rs.getTimestamp("entrydate"));
				form.setUnit(rs.getString("unit"));
				form.setP_name(rs.getString("p_name"));
				form.setAtt_name(rs.getString("att_name"));
				form.setMessage(rs.getString("message"));
				ret.add(form);
			}
			return ret;
		}

	}

	/**
	 * 履歴一覧検索件数を返します
	 * 
	 * @param util
	 * @param refcond
	 * @return
	 * @throws SQLException
	 */
	public long getAllCareerCount(
			StrategyUtility util,
			RefConditionsForm refcond) throws SQLException {
		List ret = util.executeQuery(
				"/tool/career/countlike",
				new CareerCountMultiFindStrategy(refcond),
				refcond);

		return ((Long) ret.get(0)).longValue();
	}

	private class CareerCountStrategy extends DefaultCareerStrategy {
		protected final RefConditionsForm refcond;

		CareerCountStrategy(RefConditionsForm refcond) {
			this.refcond = refcond;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setCareerCondition(st, refcond);
			log.debug(st.toString());
		}

		public List getResult(ResultSet rs) throws SQLException {
			List ret = new ArrayList();
			if (rs.next()) {
				ret.add(new Long(rs.getLong("count")));
			}
			return ret;
		}

	}

	private class CareerListMultiFindStrategy extends CareerListStrategy {
		private static final int QUERY_INDEX = 6;

		CareerListMultiFindStrategy(
				RefConditionsForm refcond,
				long limit,
				long offset) {
			super(refcond, limit, offset);
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setCareerCondition(st, refcond);
			int index = getLimitCount(refcond);
			st.setLong(index, limit);
			st.setLong(index + 1, offset);
			log.debug(st.toString());
		}

		private int getLimitCount(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			StringTokenizer tokenizer = new StringTokenizer(findstr, " ");
			return tokenizer.countTokens() * 2 + QUERY_INDEX;
		}

		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			if (isNotNull(refcond)) {
				SetFindStringUtil.setFindString(st, refcond, QUERY_INDEX);
			}
		}

		private boolean isNotNull(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			return findstr != null && 0 < findstr.length();
		}
	}

	private class CareerCountMultiFindStrategy extends CareerCountStrategy {
		private static final int QUERY_INDEX = 6;

		CareerCountMultiFindStrategy(RefConditionsForm refcond) {
			super(refcond);
		}

		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			if (isNotNull(refcond)) {
				SetFindStringUtil.setFindString(st, refcond, QUERY_INDEX);
			}
		}

		private boolean isNotNull(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			return findstr != null && 0 < findstr.length();
		}
	}

	/**
	 * ヒストリ一覧ダウンロードのデータを返します
	 * 
	 * @param util
	 * @param refcond
	 * @return
	 * @throws SQLException
	 */
	public List getAllHistoryList(
			StrategyUtility util,
			RefConditionsForm refcond) throws SQLException {
		List ret = util.executeQuery(
				"/tool/history/readlike",
				new HistoryListMultiFindStrategy(refcond, 65535, 0),
				refcond);

		return ret;
	}

	/**
	 * ヒストリ一覧検索のデータを返します
	 * 
	 * @param util
	 * @param refcond
	 * @param offset
	 * @return
	 * @throws SQLException
	 */
	public List getHistoryList(
			StrategyUtility util,
			RefConditionsForm refcond,
			long offset) throws SQLException {
		List ret = util.executeQuery(
				"/tool/history/readlike",
				new HistoryListMultiFindStrategy(
						refcond,
						refcond.getLimit(),
						offset),
				refcond);

		return ret;
	}

	private abstract class AbstractHistoryStrategy implements QueryStrategy {
		protected void setHistoryCondition(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			log.debug(refcond);
			Timestamp epoch1 = new Timestamp(1000);
			Timestamp now = new Timestamp(System.currentTimeMillis());

			if (refcond.getStEneble()) {
				st.setTimestamp(1, refcond.getStTime());
				st.setTimestamp(3, refcond.getStTime());
			} else {
				st.setTimestamp(1, new Timestamp(0));
				st.setTimestamp(3, new Timestamp(0));
			}
			if (refcond.getEtEneble()) {
				st.setTimestamp(2, refcond.getEtTime());
				st.setTimestamp(4, refcond.getEtTime());
			} else {
				st.setTimestamp(2, now);
				st.setTimestamp(4, now);
			}
			String messtr = refcond.getMsgString();
			if (messtr != null && 0 < messtr.length()) {
				st.setString(5, messtr);
			} else {
				st.setString(5, "%");
			}
			if ("true".equals(refcond.getBitval())) {
				st.setString(6, "epoch");
				st.setTimestamp(7, epoch1);
			} else if ("false".equals(refcond.getBitval())) {
				st.setTimestamp(6, epoch1);
				st.setTimestamp(7, now);
			} else {
				st.setString(6, "epoch");
				st.setTimestamp(7, now);
			}
			if ("true".equals(refcond.getHistrycheck())) {
				st.setString(8, "false");
				st.setString(9, "true");
			} else if ("false".equals(refcond.getHistrycheck())) {
				st.setString(8, "true");
				st.setString(9, "false");
			} else {
				st.setString(8, "true");
				st.setString(9, "true");
			}
			findString(st, refcond);
		}

		abstract protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException;
	}

	private abstract class DefaultHistoryStrategy extends
			AbstractHistoryStrategy {
		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			String findstr = refcond.getFindString();
			if (findstr != null && 0 < findstr.length()) {
				st.setString(10, "%" + findstr + "%");
			} else {
				st.setString(10, "%");
			}
		}
	}

	private class HistoryListStrategy extends DefaultHistoryStrategy {
		protected final RefConditionsForm refcond;
		protected final long limit;
		protected final long offset;

		HistoryListStrategy(RefConditionsForm refcond, long limit, long offset) {
			this.refcond = refcond;
			this.limit = limit;
			this.offset = offset;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setHistoryCondition(st, refcond);
			st.setLong(11, limit);
			st.setLong(12, offset);
			log.debug(st.toString());
		}

		public List getResult(ResultSet rs) throws SQLException {
			List ret = new ArrayList();
			while (rs.next()) {
				HistoryRecordBean form = new HistoryRecordBean();
				form.setAlarm_color(rs.getString("alarm_color"));
				form.setOndate(rs.getTimestamp("on_date"));
				form.setOffdate(rs.getTimestamp("off_date"));
				form.setUnit(rs.getString("unit"));
				form.setP_name(rs.getString("p_name"));
				form.setAtt_name(rs.getString("att_name"));
				form.setHistry_check(rs.getString("histry_check"));
				ret.add(form);
			}
			return ret;
		}
	}

	private class HistoryListMultiFindStrategy extends HistoryListStrategy {
		private static final int QUERY_INDEX = 10;

		public HistoryListMultiFindStrategy(
				RefConditionsForm refcond,
				long limit,
				long offset) {
			super(refcond, limit, offset);
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setHistoryCondition(st, refcond);
			int index = getLimitCount(refcond);
			st.setLong(index, limit);
			st.setLong(index + 1, offset);
			log.debug(st.toString());
		}

		private int getLimitCount(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			StringTokenizer tokenizer = new StringTokenizer(findstr, " ");
			return tokenizer.countTokens() * 2 + QUERY_INDEX;
		}

		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			if (isNotNull(refcond)) {
				SetFindStringUtil.setFindString(st, refcond, QUERY_INDEX);
			}
		}

		private boolean isNotNull(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			return findstr != null && 0 < findstr.length();
		}
	}

	/**
	 * ヒストリ一覧検索の件数を返します
	 * 
	 * @param util
	 * @param refcond
	 * @return
	 * @throws SQLException
	 */
	public long getAllHistoryCount(
			StrategyUtility util,
			RefConditionsForm refcond) throws SQLException {
		List ret = util.executeQuery(
				"/tool/history/countlike",
				new HistoryCountMultiFindStrategy(refcond),
				refcond);

		return ((Long) ret.get(0)).longValue();
	}

	private class HistoryCountStrategy extends DefaultHistoryStrategy {
		protected final RefConditionsForm refcond;

		HistoryCountStrategy(RefConditionsForm refcond) {
			this.refcond = refcond;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setHistoryCondition(st, refcond);
		}

		public List getResult(ResultSet rs) throws SQLException {
			List ret = new ArrayList();
			if (rs.next()) {
				ret.add(new Long(rs.getLong("count")));
			}
			return ret;
		}
	}

	private class HistoryCountMultiFindStrategy extends HistoryCountStrategy {
		private static final int QUERY_INDEX = 10;

		HistoryCountMultiFindStrategy(RefConditionsForm refcond) {
			super(refcond);
		}

		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			if (isNotNull(refcond)) {
				SetFindStringUtil.setFindString(st, refcond, QUERY_INDEX);
			}
		}

		private boolean isNotNull(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			return findstr != null && 0 < findstr.length();
		}
	}

	public List getAllSummaryList(
			StrategyUtility util,
			RefConditionsForm refcond) throws SQLException {
		List ret = util.executeQuery(
				"/tool/summary/readlike",
				new SummaryListMultiFindStrategy(refcond, 65535, 0),
				refcond);

		return ret;
	}

	public List getSummaryList(
			StrategyUtility util,
			RefConditionsForm refcond,
			long offset) throws SQLException {
		List ret = util.executeQuery(
				"/tool/summary/readlike",
				new SummaryListMultiFindStrategy(
						refcond,
						refcond.getLimit(),
						offset),
				refcond);

		return ret;
	}

	private abstract class AbstractSummaryStrategy implements QueryStrategy {
		protected void setSummaryCondition(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			Timestamp now = new Timestamp(System.currentTimeMillis());

			if (refcond.getStEneble()) {
				st.setTimestamp(1, refcond.getStTime());
				st.setTimestamp(3, refcond.getStTime());
			} else {
				st.setTimestamp(1, new Timestamp(0));
				st.setTimestamp(3, new Timestamp(0));
			}
			if (refcond.getEtEneble()) {
				st.setTimestamp(2, refcond.getEtTime());
				st.setTimestamp(4, refcond.getEtTime());
			} else {
				st.setTimestamp(2, now);
				st.setTimestamp(4, now);
			}
			String messtr = refcond.getMsgString();
			if (messtr != null && 0 < messtr.length()) {
				st.setString(5, messtr);
			} else {
				st.setString(5, "%");
			}
			if ("true".equals(refcond.getBitval())) {
				st.setBoolean(6, true);
				st.setBoolean(7, true);
			} else if ("false".equals(refcond.getBitval())) {
				st.setBoolean(6, false);
				st.setBoolean(7, false);
			} else {
				st.setBoolean(6, true);
				st.setBoolean(7, false);
			}
			findString(st, refcond);
		}

		protected abstract void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException;
	}

	private abstract class DefaultSummaryStrategy extends
			AbstractSummaryStrategy {
		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			String findstr = refcond.getFindString();
			if (findstr != null && 0 < findstr.length()) {
				st.setString(8, "%" + findstr + "%");
			} else {
				st.setString(8, "%");
			}
		}
	}

	private class SummaryListStrategy extends DefaultSummaryStrategy {
		protected final RefConditionsForm refcond;
		protected final long limit;
		protected final long offset;

		SummaryListStrategy(RefConditionsForm refcond, long limit, long offset) {
			this.refcond = refcond;
			this.limit = limit;
			this.offset = offset;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setSummaryCondition(st, refcond);
			st.setLong(9, limit);
			st.setLong(10, offset);
			log.debug(st.toString());
		}

		public List getResult(ResultSet rs) throws SQLException {
			List ret = new ArrayList();
			while (rs.next()) {
				SummaryRecordBean form = new SummaryRecordBean();
				form.setAlarm_color(rs.getString("alarm_color"));
				form.setOndate(rs.getTimestamp("on_date"));
				form.setOffdate(rs.getTimestamp("off_date"));
				form.setUnit(rs.getString("unit"));
				form.setP_name(rs.getString("p_name"));
				form.setAtt_name(rs.getString("att_name"));
				form.setMessage(rs.getString("message"));
				ret.add(form);
			}
			return ret;
		}
	}

	private class SummaryListMultiFindStrategy extends SummaryListStrategy {
		private static final int QUERY_INDEX = 8;

		public SummaryListMultiFindStrategy(
				RefConditionsForm refcond,
				long limit,
				long offset) {
			super(refcond, limit, offset);
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setSummaryCondition(st, refcond);
			int index = getLimitCount(refcond);
			st.setLong(index, limit);
			st.setLong(index + 1, offset);
			log.debug(st.toString());
		}

		private int getLimitCount(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			StringTokenizer tokenizer = new StringTokenizer(findstr, " ");
			return tokenizer.countTokens() * 2 + QUERY_INDEX;
		}

		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			if (isNotNull(refcond)) {
				SetFindStringUtil.setFindString(st, refcond, QUERY_INDEX);
			}
		}

		private boolean isNotNull(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			return findstr != null && 0 < findstr.length();
		}
	}

	public long getAllSummaryCount(
			StrategyUtility util,
			RefConditionsForm refcond) throws SQLException {
		List ret = util.executeQuery(
				"/tool/summary/countlike",
				new SummaryCountMultiFindStrategy(refcond),
				refcond);

		return ((Long) ret.get(0)).longValue();
	}

	private class SummaryCountStrategy extends DefaultSummaryStrategy {
		private final RefConditionsForm refcond;

		SummaryCountStrategy(RefConditionsForm refcond) {
			this.refcond = refcond;
		}

		public void setQuery(PreparedStatement st) throws SQLException {
			setSummaryCondition(st, refcond);
		}

		public List getResult(ResultSet rs) throws SQLException {
			List ret = new ArrayList();
			if (rs.next()) {
				ret.add(new Long(rs.getLong("count")));
			}
			return ret;
		}
	}

	private class SummaryCountMultiFindStrategy extends SummaryCountStrategy {
		private static final int QUERY_INDEX = 8;

		SummaryCountMultiFindStrategy(RefConditionsForm refcond) {
			super(refcond);
		}

		protected void findString(
				PreparedStatement st,
				RefConditionsForm refcond) throws SQLException {
			if (isNotNull(refcond)) {
				SetFindStringUtil.setFindString(st, refcond, QUERY_INDEX);
			}
		}

		private boolean isNotNull(RefConditionsForm refcond) {
			String findstr = refcond.getFindString();
			return findstr != null && 0 < findstr.length();
		}
	}
}

abstract class SetFindStringUtil {
	static void setFindString(
			PreparedStatement st,
			RefConditionsForm refcond,
			int index) throws SQLException {
		String findstr = refcond.getFindString();
		for (StringTokenizer tokenizer = new StringTokenizer(findstr, " "); tokenizer
				.hasMoreTokens();) {
			String value = tokenizer.nextToken();
			st.setString(index++, "%" + value + "%");
			st.setString(index++, "%" + value + "%");
		}
	}
}
