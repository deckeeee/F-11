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
package org.F11.scada.server.alarm.table;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.F11.scada.applet.ClientConfiguration;
import org.apache.log4j.Logger;

/**
 * 警報一覧の検索条件を保持します。
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public class FindAlarmCondition implements Serializable {
	private static final long serialVersionUID = -1339605304823105797L;
	/** 開始日時有効フラグ */
	private final boolean st_enable;
	/** 開始日時 */
	private final Calendar st_calendar;
	/** 終了日時有効フラグ */
	private final boolean ed_enable;
	/** 終了日時 */
	private final Calendar ed_calendar;
	/** アトリビュートID 選択 */
	private final int[] selectKind;
	/** アトリビュート名 選択 */
	private final String[] selectKindString;
	/** 条件 */
	private final RadioStat bitvalSelect;
	/** 確認 */
	private final RadioStat histckSelect;
	/** 記号 */
	private final String unit;
	/** 名称 */
	private final String name;
	/** 選択プライオリティ名のリスト */
	private final List priorities;
	/** 属性1 */
	private final String attribute1;
	/** 属性2 */
	private final String attribute2;
	/** 属性3 */
	private final String attribute3;

	private static final ClientConfiguration configuration =
		new ClientConfiguration();

	private static final Logger logger =
		Logger.getLogger(FindAlarmCondition.class);

	/**
	 * 
	 */
	public FindAlarmCondition() {
		this(
			true,
			getPreviousMonth(),
			true,
			Calendar.getInstance(),
			getAttributes(),
			getStatus(false),
			getStatus(true),
			getAttributeNames(),
			"",
			"",
			getInitPriorities(),
			"",
			"",
			"");
	}

	private static Calendar getPreviousMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, configuration.getInt(
			"xwife.applet.Applet.alarm.table.search",
			1)
			* -1);
		return calendar;
	}

	static int[] getAttributes() {
		StringTokenizer st =
			new StringTokenizer(configuration.getString(
				"xwife.applet.Applet.alarm.table.search.attribute",
				""), "|");
		int[] atts = new int[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			atts[i] = Integer.parseInt(st.nextToken());
		}
		return atts;
	}

	static String[] getAttributeNames() {
		StringTokenizer st =
			new StringTokenizer(configuration.getString(
				"xwife.applet.Applet.alarm.table.search.attributename",
				""), "|");
		String[] atts = new String[st.countTokens()];
		for (int i = 0; st.hasMoreTokens(); i++) {
			atts[i] = st.nextToken();
		}
		return atts;
	}

	private static RadioStat getStatus(boolean isCheck) {
		String status = null;
		if (isCheck) {
			status =
				configuration.getString(
					"xwife.applet.Applet.alarm.table.search.checkon",
					"SELECTALL");
		} else {
			status =
				configuration.getString(
					"xwife.applet.Applet.alarm.table.search.alarmon",
					"SELECTALL");
		}
		try {
			Field statusField = RadioStat.class.getField(status);
			return (RadioStat) statusField.get(null);
		} catch (Exception e) {
			logger.error("検索条件初期設定に誤りがあります。初期値にSELECTALLを割りあてます。", e);
			return RadioStat.SELECTALL;
		}
	}

	private static List getInitPriorities() {
		StringTokenizer st =
			new StringTokenizer(configuration.getString(
				"xwife.applet.Applet.alarm.table.search.priority",
				""), "|");
		StringTokenizer namest =
			new StringTokenizer(configuration.getString(
				"xwife.applet.Applet.alarm.table.search.priorityname",
				""), "|");
		ArrayList priorities = new ArrayList(st.countTokens());
		while (st.hasMoreTokens() && namest.hasMoreTokens()) {
			priorities.add(new Priority(
				Integer.parseInt(st.nextToken()),
				namest.nextToken()));
		}
		return priorities;
	}

	public FindAlarmCondition(
			boolean st_enable,
			Calendar st_calendar,
			boolean ed_enable,
			Calendar ed_calendar,
			int[] selectKind,
			RadioStat bitvalSelect,
			RadioStat histckSelect,
			String[] selectKindString,
			String unit,
			String name,
			List priorities,
			String attribute1,
			String attribute2,
			String attribute3) {
		this.st_enable = st_enable;
		this.st_calendar = st_calendar;
		this.ed_enable = ed_enable;
		this.ed_calendar = ed_calendar;
		this.selectKind = selectKind;
		this.bitvalSelect = bitvalSelect;
		this.histckSelect = histckSelect;
		this.selectKindString = selectKindString;
		this.unit = unit;
		this.name = name;
		this.priorities = priorities;
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
		this.attribute3 = attribute3;
	}

	public RadioStat getBitvalSelect() {
		return bitvalSelect;
	}

	public Calendar getEd_calendar() {
		return ed_calendar;
	}

	public boolean isEd_enable() {
		return ed_enable;
	}

	public RadioStat getHistckSelect() {
		return histckSelect;
	}

	public int[] getSelectKind() {
		return selectKind;
	}

	public Calendar getSt_calendar() {
		return st_calendar;
	}

	public boolean isSt_enable() {
		return st_enable;
	}

	public String[] getSelectKindString() {
		return selectKindString;
	}

	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}

	public List getPriorities() {
		return priorities;
	}

	public AttributeRecord[] getAttributeRecord() {
		AttributeRecord[] record = new AttributeRecord[selectKind.length];
		for (int i = 0; i < selectKind.length && i < selectKindString.length; i++) {
			record[i] = new AttributeRecord(selectKind[i], selectKindString[i]);
		}
		return record;
	}

	public String getAttribute1() {
		return attribute1;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public String getAttribute3() {
		return attribute3;
	}

	public static class RadioStat implements Serializable {
		private static final long serialVersionUID = -3602143712465744770L;
		public static final RadioStat SELECTTRUE = new RadioStat(1);
		public static final RadioStat SELECTFALSE = new RadioStat(2);
		public static final RadioStat SELECTALL = new RadioStat(3);

		private int no;

		private RadioStat(int no) {
			this.no = no;
		}

		public int getNo() {
			return no;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof RadioStat)) {
				return false;
			}
			RadioStat stat = (RadioStat) obj;
			return no == stat.no;
		}

		public int hashCode() {
			int result = 17;
			result = 37 * result + no;
			return result;
		}
	}
}
