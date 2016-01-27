/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.F11.scada.server.alarm.table.postgresql;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.F11.scada.server.alarm.HistoryCheck;
import org.F11.scada.server.alarm.table.AlarmListFinder;
import org.F11.scada.server.alarm.table.AttributeRecord;
import org.F11.scada.server.alarm.table.FindAlarmCondition;
import org.F11.scada.server.alarm.table.FindAlarmPosition;
import org.F11.scada.server.alarm.table.FindAlarmTable;
import org.F11.scada.server.alarm.table.Priority;
import org.F11.scada.server.alarm.table.FindAlarmCondition.RadioStat;
import org.F11.scada.server.io.StrategyUtility;
import org.F11.scada.server.io.postgresql.PostgreSQLAlarmDataStore;
import org.F11.scada.util.AttributesUtil;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;

/**
 * åxïÒàÍóó åüçıÉCÉìÉ^Å[ÉtÉFÉCÉXÇÃé¿ëïÇ≈Ç∑ÅB
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public class PostgreSQLAlarmListFinder implements AlarmListFinder {
	private static Logger log =
		Logger.getLogger(PostgreSQLAlarmListFinder.class);
	private final StrategyUtility utility;
	private final HistoryCheck historyCheck;

	/**
	 * 
	 */
	public PostgreSQLAlarmListFinder() throws IOException {
		super();
		utility = new StrategyUtility();
		historyCheck = new PostgreSQLAlarmDataStore();
	}

	/*
	 * (îÒ Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.alarm.table.AlarmListFinder#getSummaryList(org.F11
	 * .scada.server.alarm.table.FindAlarmCondition,
	 * org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getSummaryList(
			FindAlarmCondition cond,
			FindAlarmPosition fac,
			int order) throws SQLException {
		FindAlarmTable ret = null;

		Connection con = null;
		PreparedStatement preSel = null;
		ResultSet rs = null;

		String countHead = "SELECT COUNT(i.holder) AS count ";
		String selectHead =
			"SELECT i.jump_path, i.auto_jump_flag, i.auto_jump_priority"
				+ ", CASE WHEN s.bit_value = '1' THEN att.on_summary_color ELSE att.off_summary_color END AS alarm_color"
				+ ", i.point, i.provider, i.holder, s.on_date, s.off_date, p.unit"
				+ ", p.name AS kikiname, CASE WHEN att.message_mode THEN att.name ELSE NULL END AS attname"
				+ ", m.message,pri.name AS priorityname "
				+ ", p.attribute1, p.attribute2, p.attribute3 ";
		StringBuffer sbBody = new StringBuffer();
		sbBody
			.append("FROM summary_table s, point_table p, item_table i LEFT JOIN priority_table pri ON i.auto_jump_priority = pri.id, message_table m, attribute_table att ");
		sbBody
			.append("WHERE p.point = s.point AND s.point = i.point AND s.provider = i.provider AND s.holder = i.holder AND i.message_id = m.message_id AND s.bit_value = m.type AND i.attribute_id = att.attribute AND ((att.summary_mode=1 AND s.bit_value='0') OR (att.summary_mode=2 AND s.bit_value='1') OR (att.summary_mode=3) OR (att.summary_mode=4 AND s.bit_value='1') OR (att.summary_mode=5 AND s.bit_value='0')) ");

		if (cond.isSt_enable()) {
			sbBody
				.append("AND ((s.on_date IS NOT NULL AND s.on_date >= ?) OR (s.off_date IS NOT NULL AND s.off_date >= ?)) ");
		}
		if (cond.isEd_enable()) {
			sbBody
				.append("AND ((s.on_date IS NOT NULL AND s.on_date < ?) OR (s.off_date IS NOT NULL AND s.off_date < ?)) ");
		}
		int[] attrs = cond.getSelectKind();
		if (0 < attrs.length) {
			sbBody.append("AND (");
			for (int i = 0; i < attrs.length; i++) {
				if (0 < i) {
					sbBody.append("OR ");
				}
				sbBody.append("att.attribute = ? ");
			}
			sbBody.append(") ");
		}
		List priList = setPriorityQuery(cond, sbBody);
		if (RadioStat.SELECTTRUE.equals(cond.getBitvalSelect())) {
			sbBody.append("AND s.bit_value='1' ");
		} else if (RadioStat.SELECTFALSE.equals(cond.getBitvalSelect())) {
			sbBody.append("AND s.bit_value='0' ");
		}

		setUnitCondition(cond, sbBody);
		setNameCondition(cond, sbBody);
		setAttributeCondition(cond, sbBody);

		String orderStr;
		switch (order) {
		default:
		case 0: // ïWèÄ
			orderStr = "ORDER BY p.unit, kikiname ";
			break;
		case 1: // ãtèá
			orderStr = "ORDER BY p.unit DESC, kikiname ";
			break;
		case 2: // î≠ê∂ÅEâ^ì]ì˙éû è∏èá
			orderStr = "ORDER BY s.on_date, p.unit, kikiname ";
			break;
		case 3: // î≠ê∂ÅEâ^ì]ì˙éû ç~èá
			orderStr = "ORDER BY s.on_date DESC, p.unit, kikiname ";
			break;
		case 4: // ïúãåÅEí‚é~ì˙éû è∏èá
			orderStr = "ORDER BY s.off_date, p.unit, kikiname ";
			break;
		case 5: // ïúãåÅEí‚é~ì˙éû ç~èá
			orderStr = "ORDER BY s.off_date DESC, p.unit, kikiname ";
			break;
		case 6: // ñºèÃ è∏èá
			orderStr = "ORDER BY kikiname, p.unit ";
			break;
		case 7: // ñºèÃ ç~èá
			orderStr = "ORDER BY kikiname DESC, p.unit ";
			break;
		}
		String hutter = "LIMIT ? OFFSET ?";

		try {
			long maxrec = 0;
			con = ConnectionUtil.getConnection();
			preSel =
				con.prepareStatement(
					countHead + sbBody.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int index = 1;
			if (cond.isSt_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
			}
			if (cond.isEd_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
			}
			for (int i = 0; i < attrs.length; i++) {
				preSel.setInt(index++, attrs[i]);
			}
			index = setPriority(preSel, priList, index);
			index = setUnitConditionValue(cond, preSel, index);
			index = setNameConditionValue(cond, preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute1(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute2(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute3(), preSel, index);
			rs = preSel.executeQuery();
			if (rs.next()) {
				maxrec = rs.getLong("count");
			}
			rs.close();
			rs = null;
			preSel.close();
			preSel = null;

			if (log.isDebugEnabled()) {
				log.debug(selectHead + sbBody.toString() + orderStr + hutter);
			}
			preSel =
				con.prepareStatement(
					selectHead + sbBody.toString() + orderStr + hutter,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			index = 1;
			if (cond.isSt_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
			}
			if (cond.isEd_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
			}
			for (int i = 0; i < attrs.length; i++) {
				preSel.setInt(index++, attrs[i]);
			}
			index = setPriority(preSel, priList, index);
			index = setUnitConditionValue(cond, preSel, index);
			index = setNameConditionValue(cond, preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute1(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute2(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute3(), preSel, index);
			preSel.setLong(index++, fac.getLimit());
			preSel.setLong(index++, fac.getOffset());
			rs = preSel.executeQuery();
			ret = new FindAlarmTable(maxrec, getObjectArray(rs));
			rs.close();
			preSel.close();
			con.close();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (preSel != null) {
				try {
					preSel.close();
				} catch (SQLException e) {
					preSel = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		return ret;
	}

	private int setPriority(PreparedStatement preSel, List priList, int index)
			throws SQLException {
		for (Iterator i = priList.iterator(); i.hasNext();) {
			Priority priority = (Priority) i.next();
			preSel.setInt(index++, priority.getId());
		}
		return index;
	}

	private List setPriorityQuery(FindAlarmCondition cond, StringBuffer sbBody) {
		List priList = cond.getPriorities();
		if (!priList.isEmpty()) {
			sbBody.append("AND (");
			for (Iterator i = priList.iterator(); i.hasNext();) {
				i.next();
				sbBody.append("i.auto_jump_priority = ? ");
				if (i.hasNext()) {
					sbBody.append("OR ");
				}
			}
			sbBody.append(") ");
		}
		return priList;
	}

	private void setUnitCondition(FindAlarmCondition cond, StringBuffer sbBody) {
		String unit = cond.getUnit();
		if (!AttributesUtil.isSpaceOrNull(unit)) {
			setLike(sbBody, "p.unit", unit);
		}
	}

	private StringBuffer setLike(
			StringBuffer sbBody,
			String fieldName,
			String cond) {
		sbBody.append("AND (");
		for (StringTokenizer t = new StringTokenizer(cond, " "); t
			.hasMoreTokens();) {
			t.nextToken();
			sbBody.append(fieldName).append(" LIKE ? ");
			if (t.hasMoreTokens()) {
				sbBody.append("AND ");
			}
		}
		return sbBody.append(") ");
	}

	private void setNameCondition(FindAlarmCondition cond, StringBuffer sbBody) {
		String name = cond.getName();
		if (!AttributesUtil.isSpaceOrNull(name)) {
			setLike(sbBody, "p.name", name);
		}
	}

	private void setAttributeCondition(
			FindAlarmCondition cond,
			StringBuffer sbBody) {
		setAttribute(sbBody, "p.attribute1", cond.getAttribute1());
		setAttribute(sbBody, "p.attribute2", cond.getAttribute2());
		setAttribute(sbBody, "p.attribute3", cond.getAttribute3());
	}

	private void setAttribute(
			StringBuffer sbBody,
			String field,
			String attribute) {
		if (!AttributesUtil.isSpaceOrNull(attribute)) {
			setLike(sbBody, field, attribute);
		}
	}

	private int setUnitConditionValue(
			FindAlarmCondition cond,
			PreparedStatement preSel,
			int index) throws SQLException {
		String unit = cond.getUnit();
		if (!AttributesUtil.isSpaceOrNull(unit)) {
			index = setLikeValue(preSel, unit, index);
		}
		return index;
	}

	private int setLikeValue(PreparedStatement preSel, String cond, int index)
			throws SQLException {
		for (StringTokenizer t = new StringTokenizer(cond, " "); t
			.hasMoreTokens(); index++) {
			String value = t.nextToken();
			preSel.setString(index, "%" + value + "%");
		}
		return index;
	}

	private int setNameConditionValue(
			FindAlarmCondition cond,
			PreparedStatement preSel,
			int index) throws SQLException {
		String name = cond.getName();
		if (!AttributesUtil.isSpaceOrNull(name)) {
			index = setLikeValue(preSel, name, index);
		}
		return index;
	}

	private int setAttributeConditionValue(
			String attribute,
			PreparedStatement preSel,
			int index) throws SQLException {
		if (!AttributesUtil.isSpaceOrNull(attribute)) {
			index = setLikeValue(preSel, attribute, index);
		}
		return index;
	}

	/*
	 * (îÒ Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.alarm.table.AlarmListFinder#getHistoryList(org.F11
	 * .scada.server.alarm.table.FindAlarmCondition,
	 * org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getHistoryList(
			FindAlarmCondition cond,
			FindAlarmPosition fac,
			int order) throws SQLException {
		FindAlarmTable ret = null;

		Connection con = null;
		PreparedStatement preSel = null;
		ResultSet rs = null;

		String countHead = "SELECT COUNT(*) AS count ";
		String selectHead =
			"SELECT i.jump_path, i.auto_jump_flag, i.auto_jump_priority"
				+ ",CASE WHEN h.off_date='"
				+ EpochUtil.getEpoch()
				+ "' THEN att.on_alarm_color ELSE att.off_alarm_color END AS alarm_color"
				+ ",h.point, h.provider, h.holder, h.on_date,h.off_date, p.unit"
				+ ", p.name AS kikiname, CASE WHEN att.message_mode THEN att.name ELSE NULL END AS attname"
				+ ",pri.name AS priorityname"
				+ ", p.attribute1, p.attribute2, p.attribute3"
				+ ",CASE WHEN att.check_type = '0' THEN 'ÑüÑüÑüÑü' WHEN att.check_type = '1'"
				+ " AND h.check_flag = '1' THEN 'ÅñÅñÅñÅñ' ELSE NULL END AS histry_check ";
		StringBuffer sbBody = new StringBuffer();
		sbBody
			.append("FROM history_table h, point_table p, item_table i LEFT JOIN priority_table pri ON i.auto_jump_priority = pri.id, attribute_table att ");
		sbBody
			.append("WHERE p.point = h.point AND h.point = i.point AND h.provider = i.provider AND h.holder = i.holder AND i.attribute_id = att.attribute AND ((att.history_mode=1 AND h.off_date!='"
				+ EpochUtil.getEpoch()
				+ "') OR (att.history_mode=2 AND h.on_date!='"
				+ EpochUtil.getEpoch()
				+ "') OR (att.history_mode=3) OR (att.history_mode=4 AND h.on_date!='"
				+ EpochUtil.getEpoch()
				+ "' AND h.off_date='"
				+ EpochUtil.getEpoch()
				+ "') OR (att.history_mode=5 AND h.on_date='"
				+ EpochUtil.getEpoch()
				+ "' AND h.off_date!='"
				+ EpochUtil.getEpoch()
				+ "')) ");

		if (cond.isSt_enable()) {
			sbBody
				.append("AND ((h.on_date IS NOT NULL AND h.on_date >= ?) OR (h.off_date IS NOT NULL AND h.off_date >= ?)) ");
		}
		if (cond.isEd_enable()) {
			sbBody
				.append("AND ((h.on_date IS NOT NULL AND h.on_date < ?) OR (h.off_date IS NOT NULL AND h.off_date < ?)) ");
		}
		int[] attrs = cond.getSelectKind();
		if (0 < attrs.length) {
			sbBody.append("AND (");
			for (int i = 0; i < attrs.length; i++) {
				if (0 < i) {
					sbBody.append("OR ");
				}
				sbBody.append("att.attribute = ? ");
			}
			sbBody.append(") ");
		}
		List priList = setPriorityQuery(cond, sbBody);
		if (RadioStat.SELECTTRUE.equals(cond.getBitvalSelect())) {
			sbBody.append("AND h.on_date!='"
				+ EpochUtil.getEpoch()
				+ "' AND h.off_date='"
				+ EpochUtil.getEpoch()
				+ "' ");
		} else if (RadioStat.SELECTFALSE.equals(cond.getBitvalSelect())) {
			sbBody.append("AND h.off_date!='" + EpochUtil.getEpoch() + "' ");
		}
		if (RadioStat.SELECTTRUE.equals(cond.getHistckSelect())) {
			sbBody.append("AND (h.check_flag='1' OR att.check_type!='1') ");
		} else if (RadioStat.SELECTFALSE.equals(cond.getHistckSelect())) {
			sbBody
				.append("AND (h.check_flag IS NULL OR h.check_flag!='1') AND att.check_type='1' ");
		}
		setUnitCondition(cond, sbBody);
		setNameCondition(cond, sbBody);
		setAttributeCondition(cond, sbBody);

		String orderStr;
		switch (order) {
		default:
		case 0: // ïWèÄ
			orderStr = "ORDER BY h.on_date DESC ";
			break;
		case 1: // ãtèá
			orderStr = "ORDER BY h.on_date ";
			break;
		case 2: // ïúãåÅEí‚é~ì˙éû è∏èá
			orderStr = "ORDER BY h.off_date ";
			break;
		case 3: // ïúãåÅEí‚é~ì˙éû ç~èá
			orderStr = "ORDER BY h.off_date DESC ";
			break;
		case 4: // ãLçÜ è∏èá
			orderStr = "ORDER BY p.unit, h.on_date DESC ";
			break;
		case 5: // ãLçÜ ç~èá
			orderStr = "ORDER BY p.unit DESC, h.on_date DESC ";
			break;
		case 6: // ñºèÃ è∏èá
			orderStr = "ORDER BY kikiname, h.on_date DESC ";
			break;
		case 7: // ñºèÃ ç~èá
			orderStr = "ORDER BY kikiname DESC, h.on_date DESC ";
			break;
		case 8: // ämîF è∏èá
			orderStr = "ORDER BY histry_check , h.on_date DESC ";
			break;
		case 9: // ämîF ç~èá
			orderStr = "ORDER BY histry_check DESC, h.on_date DESC ";
			break;
		}
		String hutter = "LIMIT ? OFFSET ? ";

		try {
			long maxrec = 0;
			con = ConnectionUtil.getConnection();
			preSel =
				con.prepareStatement(
					countHead + sbBody.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int index = 1;
			if (cond.isSt_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
			}
			if (cond.isEd_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
			}
			for (int i = 0; i < attrs.length; i++) {
				preSel.setInt(index++, attrs[i]);
			}
			index = setPriority(preSel, priList, index);
			index = setUnitConditionValue(cond, preSel, index);
			index = setNameConditionValue(cond, preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute1(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute2(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute3(), preSel, index);
			rs = preSel.executeQuery();
			if (rs.next()) {
				maxrec = rs.getLong("count");
			}
			rs.close();
			rs = null;
			preSel.close();
			preSel = null;

			preSel =
				con.prepareStatement(
					selectHead + sbBody.toString() + orderStr + hutter,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			index = 1;
			if (cond.isSt_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
			}
			if (cond.isEd_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
			}
			for (int i = 0; i < attrs.length; i++) {
				preSel.setInt(index++, attrs[i]);
			}
			index = setPriority(preSel, priList, index);
			index = setUnitConditionValue(cond, preSel, index);
			index = setNameConditionValue(cond, preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute1(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute2(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute3(), preSel, index);
			preSel.setLong(index++, fac.getLimit());
			preSel.setLong(index++, fac.getOffset());
			rs = preSel.executeQuery();
			ret = new FindAlarmTable(maxrec, getObjectArray(rs));
			rs.close();
			preSel.close();
			con.close();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (preSel != null) {
				try {
					preSel.close();
				} catch (SQLException e) {
					preSel = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		return ret;
	}

	/*
	 * (îÒ Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.alarm.table.AlarmListFinder#getCareerList(org.F11
	 * .scada.server.alarm.table.FindAlarmCondition,
	 * org.F11.scada.server.alarm.table.FindAlarmPosition)
	 */
	public FindAlarmTable getCareerList(
			FindAlarmCondition cond,
			FindAlarmPosition fac,
			int order) throws SQLException {
		FindAlarmTable ret = null;

		Connection con = null;
		PreparedStatement preSel = null;
		ResultSet rs = null;

		String countHead = "SELECT COUNT(*) AS count ";
		String selectHead =
			"SELECT i.jump_path,CASE WHEN c.bit_value='1' THEN i.auto_jump_flag ELSE '0' END as auto_jump_flag"
				+ ", i.auto_jump_priority,CASE WHEN c.bit_value='1' THEN att.on_alarm_color ELSE att.off_alarm_color END as alarm_color"
				+ ", i.point, i.provider, i.holder, att.sound_type, CASE WHEN c.bit_value='1' THEN i.on_sound_path ELSE i.off_sound_path END AS sound_path"
				+ ", i.email_group_id, i.email_send_mode, c.entrydate,p.unit"
				+ ", p.name AS kikiname, CASE WHEN att.message_mode THEN att.name ELSE NULL END AS attname"
				+ ", m.message, pri.name AS priorityname "
				+ ", p.attribute1, p.attribute2, p.attribute3 ";
		StringBuffer sbBody = new StringBuffer();
		sbBody
			.append("FROM point_table p,career_table c,item_table i LEFT JOIN priority_table pri ON i.auto_jump_priority = pri.id,message_table m,attribute_table att ");
		sbBody
			.append("WHERE p.point=i.point AND c.point=i.point AND c.provider=i.provider AND c.holder=i.holder AND i.message_id=m.message_id AND c.bit_value=m.type AND i.attribute_id=att.attribute AND ((att.career_mode=1 AND c.bit_value='0') OR (att.career_mode=2 AND c.bit_value='1') OR (att.career_mode=3) OR (att.career_mode=4 AND c.bit_value='1') OR (att.career_mode=5 AND c.bit_value='0')) ");

		if (cond.isSt_enable()) {
			sbBody
				.append("AND ((c.entrydate IS NOT NULL AND c.entrydate >= ?) OR (c.entrydate IS NOT NULL AND c.entrydate >= ?)) ");
		}
		if (cond.isEd_enable()) {
			sbBody
				.append("AND ((c.entrydate IS NOT NULL AND c.entrydate < ?) OR (c.entrydate IS NOT NULL AND c.entrydate < ?)) ");
		}
		int[] attrs = cond.getSelectKind();
		if (0 < attrs.length) {
			sbBody.append("AND (");
			for (int i = 0; i < attrs.length; i++) {
				if (0 < i) {
					sbBody.append("OR ");
				}
				sbBody.append("att.attribute = ? ");
			}
			sbBody.append(") ");
		}
		List priList = setPriorityQuery(cond, sbBody);
		if (RadioStat.SELECTTRUE.equals(cond.getBitvalSelect())) {
			sbBody.append("AND c.bit_value='1' ");
		} else if (RadioStat.SELECTFALSE.equals(cond.getBitvalSelect())) {
			sbBody.append("AND c.bit_value='0' ");
		}
		setUnitCondition(cond, sbBody);
		setNameCondition(cond, sbBody);
		setAttributeCondition(cond, sbBody);

		String orderStr;
		switch (order) {
		default:
		case 0: // ïWèÄ
			orderStr = "ORDER BY entrydate DESC ";
			break;
		case 1: // ãtèá
			orderStr = "ORDER BY entrydate ";
			break;
		case 2: // ãLçÜ è∏èá
			orderStr = "ORDER BY p.unit, entrydate DESC ";
			break;
		case 3: // ãLçÜ ç~èá
			orderStr = "ORDER BY p.unit DESC, entrydate DESC ";
			break;
		case 4: // ñºèÃ è∏èá
			orderStr = "ORDER BY kikiname, entrydate DESC ";
			break;
		case 5: // ñºèÃ ç~èá
			orderStr = "ORDER BY kikiname DESC, entrydate DESC ";
			break;
		}

		String hutter = "LIMIT ? OFFSET ? ";

		try {
			long maxrec = 0;
			con = ConnectionUtil.getConnection();
			if (log.isDebugEnabled()) {
				log.debug(countHead + sbBody.toString());
			}
			preSel =
				con.prepareStatement(
					countHead + sbBody.toString(),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int index = 1;
			if (cond.isSt_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
			}
			if (cond.isEd_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
			}
			for (int i = 0; i < attrs.length; i++) {
				preSel.setInt(index++, attrs[i]);
			}
			index = setPriority(preSel, priList, index);
			index = setUnitConditionValue(cond, preSel, index);
			index = setNameConditionValue(cond, preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute1(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute2(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute3(), preSel, index);
			rs = preSel.executeQuery();
			if (rs.next()) {
				maxrec = rs.getLong("count");
			}
			rs.close();
			rs = null;
			preSel.close();
			preSel = null;

			if (log.isDebugEnabled()) {
				log.debug(selectHead + sbBody.toString() + orderStr + hutter);
			}
			preSel =
				con.prepareStatement(
					selectHead + sbBody.toString() + orderStr + hutter,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			index = 1;
			if (cond.isSt_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getSt_calendar()
					.getTimeInMillis()));
			}
			if (cond.isEd_enable()) {
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
				preSel.setTimestamp(index++, new Timestamp(cond
					.getEd_calendar()
					.getTimeInMillis()));
			}
			for (int i = 0; i < attrs.length; i++) {
				preSel.setInt(index++, attrs[i]);
			}
			index = setPriority(preSel, priList, index);
			index = setUnitConditionValue(cond, preSel, index);
			index = setNameConditionValue(cond, preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute1(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute2(), preSel, index);
			index =
				setAttributeConditionValue(cond.getAttribute3(), preSel, index);
			preSel.setLong(index++, fac.getLimit());
			preSel.setLong(index++, fac.getOffset());
			rs = preSel.executeQuery();
			ret = new FindAlarmTable(maxrec, getObjectArray(rs));
			rs.close();
			preSel.close();
			con.close();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (preSel != null) {
				try {
					preSel.close();
				} catch (SQLException e) {
					preSel = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		return ret;
	}

	private Object[][] getObjectArray(ResultSet rs) throws SQLException {
		Object[][] data = null;
		ResultSetMetaData rsMeta = rs.getMetaData();

		rs.last();
		data = new Object[rs.getRow()][rsMeta.getColumnCount()];

		rs.beforeFirst();
		for (int row = 0; rs.next(); row++) {
			for (int i = 1, column = 0, columnCount = rsMeta.getColumnCount(); i <= columnCount; i++, column++) {
				// System.out.println(rsMeta.getColumnClassName(i) + " : " +
				// rsMeta.getColumnType(i));
				// PostgreSQL, MySQLÇÕÇ±ÇÍÇ≈Ç¢ÇØÇÈÇ‡ÇÊÇ§ÅB
				switch (rsMeta.getColumnType(i)) {
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					data[row][column] = rs.getString(i);
					break;
				case Types.INTEGER:
					data[row][column] = new Integer(rs.getInt(i));
					break;
				case Types.DOUBLE:
					data[row][column] = new Double(rs.getDouble(i));
					break;
				case Types.FLOAT:
					data[row][column] = new Float(rs.getFloat(i));
					break;
				case Types.BOOLEAN:
				case Types.BIT:
				case Types.TINYINT:
					if (rs.wasNull()) {
						data[row][column] = null;
					} else {
						data[row][column] = Boolean.valueOf(rs.getBoolean(i));
					}
					break;
				case Types.TIMESTAMP:
					data[row][column] = rs.getTimestamp(i);
					break;
				default:
					data[row][column] = rs.getString(i);
					break;
				}
			}
		}
		return data;
	}

	public AttributeRecord[] getAttributeRecords()
			throws SQLException,
			RemoteException {
		AttributeRecord[] ret = null;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement("/attributetable/read/all"),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			rs = stmt.executeQuery();
			List data = new ArrayList();
			for (int i = 0; rs.next(); i++) {
				int attribute = rs.getInt("attribute");
				String name = rs.getString("name");
				if (name != null && 0 < name.trim().length()) {
					data.add(new AttributeRecord(attribute, name));
				}
			}
			ret = new AttributeRecord[data.size()];
			for (int i = 0; i < ret.length && i < data.size(); i++) {
				ret[i] = (AttributeRecord) data.get(i);
			}
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			con.close();
			con = null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					stmt = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		return ret;
	}

	/*
	 * (îÒ Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.alarm.table.AlarmListFinder#setHistoryCheck(int,
	 * java.lang.String, java.lang.String, java.sql.Timestamp)
	 */
	public void setHistoryCheck(
			Integer point,
			String provider,
			String holder,
			Timestamp on_date) throws SQLException, RemoteException {
		historyCheck.doHistoryCheck(point, provider, holder, on_date);
	}

	/*
	 * (îÒ Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.alarm.table.AlarmListFinder#setHistoryCheckAll()
	 */
	public void setHistoryCheckAll() throws SQLException, RemoteException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ConnectionUtil.getConnection();
			stmt =
				con.prepareStatement(
					utility.getPrepareStatement("/history/check/all"),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.execute();
			stmt.close();
			stmt = null;
			con.close();
			con = null;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					stmt = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
	}

	public List getPriorityTable() throws RemoteException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			st =
				con.prepareStatement(
					"SELECT id, name FROM priority_table ORDER BY id",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery();
			ArrayList list = new ArrayList();
			while (rs.next()) {
				Priority priority =
					new Priority(rs.getInt("id"), rs.getString("name"));
				list.add(priority);
			}
			return list;
		} catch (SQLException e) {
			throw new RemoteException("ÉvÉâÉCÉIÉäÉeÉBñºÉeÅ[ÉuÉãéÊìæéûÇ…ÉGÉâÅ[î≠ê∂", e);
		} finally {
			if (null != rs) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (null != st) {
				try {
					st.close();
				} catch (SQLException e) {
					st = null;
				}
			}
			if (null != con) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
	}
}
