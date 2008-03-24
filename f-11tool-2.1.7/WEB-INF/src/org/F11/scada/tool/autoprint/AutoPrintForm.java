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

package org.F11.scada.tool.autoprint;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.struts.validator.ValidatorForm;

/**
 * 自動印刷パラメータ格納処理のアクションフォームクラスです
 * @author hori
 */
public class AutoPrintForm extends ValidatorForm {
	/** スケジュールタスク名 */
	private String name;
	/** スケジュール名 */
	private String schedule;
	/** 自動印刷有無 */
	private boolean auto_flag;
	/** パラメーターの日時表現 */
	private Timestamp paramdate;
	/** 表示名称 */
	private String displayname;
	/** 月 */
	private int month;
	/** 日 */
	private int day;
	/** 時 */
	private int hour;
	/** 分 */
	private int minute;

	/**
	 * このアクションフォームオブジェクトを初期化します。
	 * タスク名・スケジュール名は空白、自動印刷は無し、日時はEPOCHとなります。
	 */
	public AutoPrintForm() {
		super();
		setName("");
		setSchedule("");
		setAutoflag(false);
		setParamdate(new Timestamp(0));
		displayname = "";
	}

	/**
	 * 自動印刷の有無を返します
	 * @return 自動印刷の有無を返します
	 */
	public boolean getAutoflag() {
		return auto_flag;
	}

	/**
	 * 日を返します
	 * @return 日
	 */
	public int getDay() {
		return day;
	}

	/**
	 * 時を返します
	 * @return 時
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * 分を返します
	 * @return 分
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * 月を返します
	 * @return 月
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * タスク名を返します
	 * @return タスク名
	 */
	public String getName() {
		return name;
	}

	/**
	 * パラメーターの日時表現を返します
	 * @return パラメーターの日時表現
	 */
	public Timestamp getParamdate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		paramdate.setTime(cal.getTimeInMillis());
		return paramdate;
	}

	/**
	 * スケジュール名を返します
	 * @return スケジュール名
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * 自動印字の有無を設定します
	 * @param b 自動印字の有無
	 */
	public void setAutoflag(boolean b) {
		auto_flag = b;
	}

	/**
	 * 日を設定します
	 * @param i 日
	 */
	public void setDay(int i) {
		day = i;
	}

	/**
	 * 時を設定します
	 * @param i 時
	 */
	public void setHour(int i) {
		hour = i;
	}

	/**
	 * 分を設定します
	 * @param i 分
	 */
	public void setMinute(int i) {
		minute = i;
	}

	/**
	 * 月を設定します
	 * @param i 月
	 */
	public void setMonth(int i) {
		month = i;
	}

	/**
	 * タスク名を設定します
	 * @param string タスク名
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * パラメーターの日時表現を設定します
	 * @param timestamp パラメーターの日時表現
	 */
	public void setParamdate(Timestamp timestamp) {
		if (timestamp == null)
			return;
		paramdate = timestamp;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(paramdate.getTime());
		month = cal.get(Calendar.MONTH) + 1;
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
	}

	/**
	 * スケジュール名を設定します
	 * @param string スケジュール名
	 */
	public void setSchedule(String string) {
		schedule = string;
	}

    public String getDisplayname() {
        return displayname;
    }
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }
}
