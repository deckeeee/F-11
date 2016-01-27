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
package org.F11.scada.applet.graph.bargraph;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;

/**
 * 
 * @author hori <hori@users.sourceforge.jp>
 */
public abstract class BarGraphStep {

	private BarGraphStep() {
	};

	public static BarGraphStep createBarGraphStep(String kind, long step) {
		if ("HOUR00".equals(kind)) {
			return new BarGraphStepHOUR00();
		} else if ("HOUR".equals(kind)) {
			return new BarGraphStepHOUR();
		} else if ("DAY".equals(kind)) {
			return new BarGraphStepDAY();
		} else if ("MONTH".equals(kind)) {
			return new BarGraphStepMONTH();
		}
		return new BarGraphStepETC(step);
	}

	public abstract long omitTime(long time);

	public abstract int getBarCount(long fullScale);

	public abstract String getAxisString(long axisTime);

	public abstract long indexToTime(long currentTime, int index);

	public abstract String getReferenceTimeString(long referenceTime);

	// ñ⁄ê∑ï∂éöóÒÇQçsóp
	public abstract String getAxisString1(long axisTime);

	public abstract String getAxisString2(long axisTime);

	private static class BarGraphStepHOUR00 extends BarGraphStep {
		private static FastDateFormat timeFormat = FastDateFormat
				.getInstance("HH");
		private static FastDateFormat timefmt = FastDateFormat
				.getInstance("yyyy/MM/dd HH:mm");
		private static FastDateFormat timeFormat1 = FastDateFormat
				.getInstance("MM/dd");
		private static FastDateFormat timeFormat2 = FastDateFormat
				.getInstance("HH:mm");

		private BarGraphStepHOUR00() {
		}

		public long omitTime(long time) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTimeInMillis();
		}

		public int getBarCount(long fullScale) {
			return (int) (fullScale / 3600000);
		}

		public String getAxisString(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat.format(new Date(axisTime));
		}

		public long indexToTime(long currentTime, int index) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(currentTime);
			cal.add(Calendar.HOUR_OF_DAY, index);
			return cal.getTimeInMillis();
		}

		public String getReferenceTimeString(long referenceTime) {
			referenceTime = omitTime(referenceTime - 1);
			return timefmt.format(new Date(referenceTime));
		}

		public String getAxisString1(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat1.format(new Date(axisTime));
		}

		public String getAxisString2(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat2.format(new Date(axisTime));
		}
	}

	private static class BarGraphStepHOUR extends BarGraphStep {
		private static DecimalFormat format = new DecimalFormat("00");
		private static FastDateFormat timefmt = FastDateFormat
				.getInstance("yyyy/MM/dd ");
		private static FastDateFormat timeFormat1 = FastDateFormat
				.getInstance("MM/dd");

		private BarGraphStepHOUR() {
		}

		public long omitTime(long time) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTimeInMillis();
		}

		public int getBarCount(long fullScale) {
			return (int) (fullScale / 3600000);
		}

		public String getAxisString(long axisTime) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(axisTime);
			int hh = cal.get(Calendar.HOUR_OF_DAY);
			if (hh <= 0)
				hh = 24;
			return format.format(hh);
		}

		public long indexToTime(long currentTime, int index) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(currentTime);
			cal.add(Calendar.HOUR_OF_DAY, index);
			return cal.getTimeInMillis();
		}

		public String getReferenceTimeString(long referenceTime) {
			long time = omitTime(referenceTime - 1);
			StringBuffer sb = new StringBuffer();
			sb.append(timefmt.format(new Date(time)));
			sb.append(getAxisString(referenceTime)).append(":00");
			return sb.toString();
		}

		public String getAxisString1(long axisTime) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(axisTime);
			cal.add(Calendar.HOUR_OF_DAY, -1);
			return timeFormat1.format(cal.getTime());
		}

		public String getAxisString2(long axisTime) {
			return getAxisString(axisTime) + ":00";
		}
	}

	private static class BarGraphStepDAY extends BarGraphStep {
		private static FastDateFormat timeFormat = FastDateFormat
				.getInstance("dd");
		private static FastDateFormat timefmt = FastDateFormat
				.getInstance("yyyy/MM/dd");
		private static FastDateFormat timeFormat1 = FastDateFormat
				.getInstance("yyyy ");
		private static FastDateFormat timeFormat2 = FastDateFormat
				.getInstance("MM/dd");

		private BarGraphStepDAY() {
		}

		public long omitTime(long time) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTimeInMillis();
		}

		public int getBarCount(long fullScale) {
			return (int) (fullScale / 86400000);
		}

		public String getAxisString(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat.format(new Date(axisTime));
		}

		public long indexToTime(long currentTime, int index) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(currentTime);
			cal.add(Calendar.DAY_OF_MONTH, index);
			return cal.getTimeInMillis();
		}

		public String getReferenceTimeString(long referenceTime) {
			referenceTime = omitTime(referenceTime - 1);
			return timefmt.format(new Date(referenceTime));
		}

		public String getAxisString1(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat1.format(new Date(axisTime));
		}

		public String getAxisString2(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat2.format(new Date(axisTime));
		}
	}

	private static class BarGraphStepMONTH extends BarGraphStep {
		private static FastDateFormat timeFormat = FastDateFormat
				.getInstance("MM");
		private static FastDateFormat timefmt = FastDateFormat
				.getInstance("yyyy/MM");
		private static FastDateFormat timeFormat1 = FastDateFormat
				.getInstance("yyyy");
		private static FastDateFormat timeFormat2 = FastDateFormat
				.getInstance("MM");

		private BarGraphStepMONTH() {
		}

		public long omitTime(long time) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(time);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return cal.getTimeInMillis();
		}

		public int getBarCount(long fullScale) {
			Calendar cal = Calendar.getInstance();
			cal.set(2000, 0, 1);
			cal.add(Calendar.DAY_OF_YEAR, (int) (fullScale / 86400000));
			return (cal.get(Calendar.YEAR) - 2000) * 12
					+ cal.get(Calendar.MONTH);
		}

		public String getAxisString(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat.format(new Date(axisTime));
		}

		public long indexToTime(long currentTime, int index) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(currentTime);
			cal.add(Calendar.MONTH, index);
			return cal.getTimeInMillis();
		}

		public String getReferenceTimeString(long referenceTime) {
			return timefmt.format(new Date(referenceTime));
		}

		public String getAxisString1(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat1.format(new Date(axisTime));
		}

		public String getAxisString2(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat2.format(new Date(axisTime));
		}
	}

	private static class BarGraphStepETC extends BarGraphStep {
		private static FastDateFormat timefmt = FastDateFormat
				.getInstance("yyyy/MM/dd HH:mm");
		private static FastDateFormat timeFormat = FastDateFormat
				.getInstance("HH:mm");
		private long step;
		private static FastDateFormat timeFormat1 = FastDateFormat
				.getInstance("MM/dd");
		private static FastDateFormat timeFormat2 = FastDateFormat
				.getInstance("HH:mm");

		private BarGraphStepETC(long step) {
			this.step = step;
		}

		public long omitTime(long time) {
			long cnt = time / step;
			return cnt * step;
		}

		public int getBarCount(long fullScale) {
			return (int) (fullScale / step);
		}

		public String getAxisString(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat.format(new Date(axisTime));
		}

		public long indexToTime(long currentTime, int index) {
			return currentTime + index * step;
		}

		public String getReferenceTimeString(long referenceTime) {
			referenceTime = omitTime(referenceTime - 1);
			return timefmt.format(new Date(referenceTime));
		}

		public String getAxisString1(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat1.format(new Date(axisTime));
		}

		public String getAxisString2(long axisTime) {
			axisTime = omitTime(axisTime - 1);
			return timeFormat2.format(new Date(axisTime));
		}
	}
}
