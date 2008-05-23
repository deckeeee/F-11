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

package org.F11.scada.server.alarm.print;

import java.awt.Color;
import java.sql.Timestamp;

import org.F11.scada.applet.symbol.ColorFactory;

/**
 * 印刷データを表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PrintLineData {
	/** 印刷色名 */
	private String printer_color;
	/** 発生日時 */
	private Timestamp entrydate;
	/** 機器記号 */
	private String unit;
	/** 機器名称 */
	private String kikiname;
	/** 警報名称 */
	private String alarmname;
	/** 警報内容 */
	private String message;

	public PrintLineData() {}

	/**
	 * オブジェクトを初期化します
	 * @param printer_color
	 * @param entrydate
	 * @param unit
	 * @param kikiname
	 * @param alarmname
	 * @param message
	 */
	public PrintLineData(
			String printer_color,
			Timestamp entrydate,
			String unit,
			String kikiname,
			String alarmname,
			String message) {
		this.printer_color = printer_color;
		this.entrydate = entrydate;
		this.unit = unit;
		this.kikiname = kikiname;
		this.alarmname = alarmname;
		this.message = message;
	}

	/**
	 * 印刷色オブジェクトを返します
	 * @return 印刷色オブジェクトを返します
	 */
	public Color getColor() {
		return ColorFactory.getColor(printer_color);
	}

	/**
	 * 発生日を返します
	 * @return Timestamp 発生日を返します
	 */
	public Timestamp getEntryDate() {
		return entrydate;
	}

	/**
	 * このオブジェクトの文字列表現を返します
	 * @return String このオブジェクトの文字列表現を返します
	 */
	public String toString() {
		StringBuffer st = new StringBuffer();
		st.append(entrydate);
		st.append("  ");
		st.append(unit);
		st.append("  ");
		st.append(kikiname);
		if (null != alarmname && !"".equals(alarmname)) {
			st.append("  ");
			st.append(alarmname);
		}
		st.append("  ");
		st.append(message);
		st.append("  ");
		return st.toString();
	}
	/**
	 * @param string
	 */
	public void setAlarmname(String string) {
		alarmname = string;
	}

	/**
	 * @param timestamp
	 */
	public void setEntrydate(Timestamp timestamp) {
		entrydate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setKikiname(String string) {
		kikiname = string;
	}

	/**
	 * @param string
	 */
	public void setMessage(String string) {
		message = string;
	}

	/**
	 * @param string
	 */
	public void setPrintercolor(String string) {
		printer_color = string;
	}

	/**
	 * @param string
	 */
	public void setUnit(String string) {
		unit = string;
	}

	public String getAlarmname() {
		return alarmname;
	}

	public String getKikiname() {
		return kikiname;
	}

	public String getMessage() {
		return message;
	}

	public String getUnit() {
		return unit;
	}

}