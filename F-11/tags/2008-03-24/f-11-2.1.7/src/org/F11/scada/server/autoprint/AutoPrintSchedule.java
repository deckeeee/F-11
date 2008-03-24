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
 *
 */
package org.F11.scada.server.autoprint;

import java.awt.Frame;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author hori
 */
public interface AutoPrintSchedule {
	/**
	 * スケジュールの種類を返します。
	 * @return スケジュールの種類を返します。
	 */
	public String getScheduleName();
	/**
	 * 自動印字のOn/Offを返します。
	 * @return 自動印字のOn/Offを返します。
	 */
	public boolean isAutoOn();
	/**
	 * スケジュールが開始状態の時trueをそうでない時falseを返します。
	 * @return スケジュールが開始状態の時trueをそうでない時falseを返します。
	 */
	public boolean isNow();
	/**
	 * 保持している日時の文字列表現を返します。
	 */
	public String getDate();
	/**
	 * スケジュールが保持している日時を返します。
	 * @return スケジュールが保持している日時を返します。
	 */
	public Timestamp getTimestamp();
	/**
	 * 日付パラメタ入力ダイアログを返します。
	 * @param frame 親フレーム
	 * @return 日付パラメタ入力ダイアログを返します。
	 */
	public AutoPrintSchedule showParamDialog(Frame frame);
	/**
	 * データ抽出開始日時を返します。
	 * @return データ抽出開始日時を返します。
	 */
	public Timestamp getStartTime();
	/**
	 * データ抽出終了日時を返します。
	 * @return データ抽出終了日時を返します。
	 */
	public Timestamp getEndTime();


	static abstract class AbstractDaily implements AutoPrintSchedule {
		private final boolean autoOn;
		private final int hour;
		private final int minute;

		AbstractDaily(boolean AutoOn, int hour, int minute) {
			if (hour < 0 || 23 < hour || minute < 0 || 59 < minute)
				throw new IllegalArgumentException("範囲外");
			this.autoOn = AutoOn;
			this.hour = hour;
			this.minute = minute;
		}

		/**
		 * @return
		 */
		public boolean isAutoOn() {
			return autoOn;
		}

		public boolean isNow() {
			if (!autoOn)
				return false;
			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.HOUR_OF_DAY) == hour && cal.get(Calendar.MINUTE) == minute)
				return true;
			return false;
		}
		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getDate()
		 */
		public String getDate() {
			StringBuffer sb = new StringBuffer();
			sb.append(hour).append("時 ").append(minute).append("分");
			return sb.toString();
		}

		public Timestamp getTimestamp() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			return new Timestamp(cal.getTimeInMillis());
		}

	}

	public static final class Daily extends AbstractDaily {
		public Daily(boolean AutoOn, int hour, int minute) {
			super(AutoOn, hour, minute);
		}

		public String getScheduleName() {
			return "日報";
		}

		public AutoPrintSchedule showParamDialog(Frame frame) {
			NippoParamDialog dlg = new NippoParamDialog(frame, this, new DailyOkActionListener());
			return dlg.getParam();
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getStartTime()
		 */
		public Timestamp getStartTime() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DATE, -1);
			return new Timestamp(cal.getTimeInMillis());
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getEndTime()
		 */
		public Timestamp getEndTime() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return new Timestamp(cal.getTimeInMillis());
		}

	}

	public static final class Monthly implements AutoPrintSchedule {
		private final boolean autoOn;
		private final int day;
		private final int hour;
		private final int minute;

		public String getScheduleName() {
			return "月報";
		}

		public Monthly(boolean AutoOn, int day, int hour, int minute) {
			if (day < 1 || 31 < day || hour < 0 || 23 < hour || minute < 0 || 59 < minute)
				throw new IllegalArgumentException("範囲外");
			this.autoOn = AutoOn;
			this.day = day;
			this.hour = hour;
			this.minute = minute;
		}

		/**
		 * @return
		 */
		public boolean isAutoOn() {
			return autoOn;
		}

		public boolean isNow() {
			if (!autoOn)
				return false;
			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.DAY_OF_MONTH) == day
				&& cal.get(Calendar.HOUR_OF_DAY) == hour
				&& cal.get(Calendar.MINUTE) == minute)
				return true;
			return false;
		}
		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getDate()
		 */
		public String getDate() {
			StringBuffer sb = new StringBuffer();
			sb.append(day).append("日 ").append(hour).append("時 ").append(minute).append("分");
			return sb.toString();
		}

		public Timestamp getTimestamp() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			return new Timestamp(cal.getTimeInMillis());
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#showParamDialog()
		 */
		public AutoPrintSchedule showParamDialog(Frame frame) {
			GeppoParamDialog dlg = new GeppoParamDialog(frame, this);
			return dlg.getParam();
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getStartTime()
		 */
		public Timestamp getStartTime() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.MONTH, -1);
			return new Timestamp(cal.getTimeInMillis());
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getEndTime()
		 */
		public Timestamp getEndTime() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return new Timestamp(cal.getTimeInMillis());
		}
	}


	public static final class Yearly implements AutoPrintSchedule {
		private final boolean autoOn;
		private final int month;
		private final int day;
		private final int hour;
		private final int minute;

		public String getScheduleName() {
			return "年報";
		}

		public Yearly(boolean AutoOn, int month, int day, int hour, int minute) {
			if (month < 1 || 12 < month || day < 1 || 31 < day || hour < 0 || 23 < hour || minute < 0 || 59 < minute)
				throw new IllegalArgumentException("範囲外");
			this.autoOn = AutoOn;
			this.month = month;
			this.day = day;
			this.hour = hour;
			this.minute = minute;
		}

		/**
		 * @return
		 */
		public boolean isAutoOn() {
			return autoOn;
		}

		public boolean isNow() {
			if (!autoOn)
				return false;
			Calendar cal = Calendar.getInstance();
			if ( cal.get(Calendar.MONTH) == (month - 1)
				&& cal.get(Calendar.DAY_OF_MONTH) == day
				&& cal.get(Calendar.HOUR_OF_DAY) == hour
				&& cal.get(Calendar.MINUTE) == minute)
				return true;
			return false;
		}
		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getDate()
		 */
		public String getDate() {
			StringBuffer sb = new StringBuffer();
			sb.append(month).append("月 ")
			.append(day).append("日 ").append(hour)
			.append("時 ").append(minute).append("分");
			return sb.toString();
		}

		public Timestamp getTimestamp() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, month - 1);
			cal.set(Calendar.DATE, day);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			return new Timestamp(cal.getTimeInMillis());
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#showParamDialog()
		 */
		public AutoPrintSchedule showParamDialog(Frame frame) {
			NenpoParamDialog dlg = new NenpoParamDialog(frame, this);
			return dlg.getParam();
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getStartTime()
		 */
		public Timestamp getStartTime() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.MONTH, 0);
			cal.add(Calendar.YEAR, -1);
			return new Timestamp(cal.getTimeInMillis());
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getEndTime()
		 */
		public Timestamp getEndTime() {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return new Timestamp(cal.getTimeInMillis());
		}

	}

	public static final class DailyAnalog extends AbstractDaily {
		
		public DailyAnalog(boolean AutoOn, int hour, int minute) {
			super(AutoOn, hour, minute);
		}

		public String getScheduleName() {
			return "日報(アナログ用)";
		}

		public AutoPrintSchedule showParamDialog(Frame frame) {
			NippoParamDialog dlg = new NippoParamDialog(frame, this, new DailyAnalogOkActionListener());
			return dlg.getParam();
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getStartTime()
		 */
		public Timestamp getStartTime() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.DATE, -1);
			return new Timestamp(cal.getTimeInMillis());
		}

		/* (non-Javadoc)
		 * @see org.F11.scada.server.autoprint.AutoPrintSchedule#getEndTime()
		 */
		public Timestamp getEndTime() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 59);
			cal.add(Calendar.DATE, -1);
			return new Timestamp(cal.getTimeInMillis());
		}

	}
}