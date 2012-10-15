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
package org.F11.scada.server.operationlog.impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jp.gr.javacons.jim.DataHolder;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeDataCalendar;
import org.F11.scada.data.WifeDataDaySchedule;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeDataTimestamp;
import org.F11.scada.server.operationlog.OperationLoggingUtil;
import org.F11.scada.server.operationlog.dto.OperationLogging;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingUtilImpl implements OperationLoggingUtil {
	public OperationLogging getOperationLogging(
			DataHolder dh,
			Object dataValue,
			String user,
			String ip,
			Timestamp timestamp) {
		OperationLogging log = new OperationLogging();
		log.setOpeDate(timestamp);
		log.setOpeUser(user);
		log.setOpeIp(ip);
		log.setOpeHolder(dh.getDataHolderName());
		log.setOpeProvider(dh.getDataProvider().getDataProviderName());

		ValueConvertUtil util = getValueConvertUtil(dataValue);
		log.setOpeBeforeValue(util.getBeforeString(dh, dataValue));
		log.setOpeAfterValue(util.getAfterString(dh, dataValue));

		return log;
	}

	private ValueConvertUtil getValueConvertUtil(Object value) {
		try {
			Field field = ValueConvertUtil.class.getField(getClassName(value
					.getClass()));
			return (ValueConvertUtil) field.get(ValueConvertUtil.class);
		} catch (Exception e) {
			throw new NoClassDefFoundError(e.getMessage());
		}
	}

	private String getClassName(Class clazz) {
		String className = clazz.getName();
		int lastIndex = className.lastIndexOf('.');
		return className.substring(lastIndex + 1);
	}

	private abstract static class ValueConvertUtil {
		protected final String scheduleText = EnvironmentManager.get(
				"/server/operationlog/impl/OperationLoggingUtilImpl",
				"");
		protected final boolean scheduleCount = Boolean
				.valueOf(
						EnvironmentManager
								.get(
										"/server/operationlog/impl/OperationLoggingUtilImpl/scheduleCount",
										"")).booleanValue();

		String getBeforeString(DataHolder dh, Object value) {
			WifeData data = (WifeData) dh.getValue();
			return getAfterString(dh, data);
		}

		int getScheduleCount(int i) {
			return scheduleCount ? i + 1 : i;
		}

		abstract String getAfterString(DataHolder dh, Object value);

		/** アナログデータの値 */
		public static ValueConvertUtil WifeDataAnalog = new ValueConvertUtil() {
			String getAfterString(DataHolder dh, Object value) {
				WifeDataAnalog analog = (WifeDataAnalog) value;
				ConvertValue convert = (ConvertValue) dh
						.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
				return convert.convertStringValue(analog.doubleValue());
			}
		};

		/** アナログ4データの値 */
		public static ValueConvertUtil WifeDataAnalog4 = new ValueConvertUtil() {
			String getAfterString(DataHolder dh, Object value) {
				WifeDataAnalog4 analog = (WifeDataAnalog4) value;
				ConvertValue convert = (ConvertValue) dh
						.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
				double[] values = analog.doubleValues();
				StringBuffer b = new StringBuffer(100);
				for (int i = 0; i < values.length; i++) {
					b.append(convert.convertStringValue(values[i]));
					if ((values.length - 1) != i) {
						b.append(", ");
					}
				}
				return b.toString();
			}
		};

		/** スケジュールデータの値 */
		public static ValueConvertUtil WifeDataSchedule = new ValueConvertUtil() {
			/**
			 * 変更前後データの比較結果を返す為、元のロジックをオーバーライドしています。
			 */
			String getBeforeString(DataHolder dh, Object value) {
				return getCompareResult(dh, value)[0];
			}

			String getAfterString(DataHolder dh, Object value) {
				return getCompareResult(dh, value)[1];
			}

			private String[] getCompareResult(DataHolder dh, Object value) {
				WifeDataSchedule src = (WifeDataSchedule) dh.getValue();
				WifeDataSchedule dst = (WifeDataSchedule) value;
				StringBuffer before = new StringBuffer(100);
				StringBuffer after = new StringBuffer(100);
				boolean[] patterns = new boolean[src.getPatternSize()];
				String[] patternNames = new String[src.getPatternSize()];
				Arrays.fill(patterns, false);
				for (int i = 0; i < src.getPatternSize(); i++) {
					for (int j = 0; j < src.getNumberSize(); j++) {
						createTimeString(
								src,
								dst,
								before,
								after,
								patterns,
								patternNames,
								i,
								j);
					}
					boolean pattern = patterns[i];
					if (before.length() > 0 && pattern) {
						before.delete(before.length() - 2, before.length());
						after.delete(after.length() - 2, after.length());
					}
					if (pattern) {
						before.insert(getStartIndex(before), patternNames[i]
								+ "{");
						after.insert(getStartIndex(after), patternNames[i]
								+ "{");
						before.append("}, ");
						after.append("}, ");
					}
				}
				return new String[] { deleteLastComma(before),
						deleteLastComma(after) };
			}

			private int getStartIndex(StringBuffer b) {
				int start = Math.max(0, b.lastIndexOf("}, "));
				return start != 0 ? start += 3 : start;
			}

			private String deleteLastComma(StringBuffer b) {
				return b.length() > 0 ? b
						.delete(b.lastIndexOf(","), b.length()).toString() : "";
			}

			private void createTimeString(
					WifeDataSchedule src,
					WifeDataSchedule dst,
					StringBuffer before,
					StringBuffer after,
					boolean[] patterns,
					String[] patternNames,
					int i,
					int j) {
				int srcOn = src.getOnTime(i, j);
				int dstOn = dst.getOnTime(i, j);
				int srcOff = src.getOffTime(i, j);
				int dstOff = dst.getOffTime(i, j);
				if (srcOn != dstOn || srcOff != dstOff) {
					DecimalFormat f = new DecimalFormat("0000");
					before.append(getScheduleCount(j)).append(scheduleText)
							.append("=").append(f.format(srcOn)).append(":")
							.append(f.format(srcOff));
					after.append(getScheduleCount(j)).append(scheduleText)
							.append("=").append(f.format(dstOn)).append(":")
							.append(f.format(dstOff));
					before.append(", ");
					after.append(", ");

					patterns[i] = true;
					patternNames[i] = src.getDayIndexName(i);
				}
			}
		};

		/** 個別スケジュールデータの値 */
		public static ValueConvertUtil WifeDataDaySchedule = new ValueConvertUtil() {
			/**
			 * 変更前後データの比較結果を返す為、元のロジックをオーバーライドしています。
			 */
			String getBeforeString(DataHolder dh, Object value) {
				return getCompareResult(dh, value)[0];
			}

			String getAfterString(DataHolder dh, Object value) {
				return getCompareResult(dh, value)[1];
			}

			private String[] getCompareResult(DataHolder dh, Object value) {
				WifeDataDaySchedule src = (WifeDataDaySchedule) dh.getValue();
				WifeDataDaySchedule dst = (WifeDataDaySchedule) value;
				StringBuffer before = new StringBuffer(100);
				StringBuffer after = new StringBuffer(100);
				for (int i = 0; i < src.getNumberSize(); i++) {
					createTimeString(src, dst, before, after, i);
				}
				before.delete(before.length() - 2, before.length());
				after.delete(after.length() - 2, after.length());
				before.insert(0, "{");
				after.insert(0, "{");
				before.append("}, ");
				after.append("}, ");

				before.delete(before.length() - 2, before.length());
				after.delete(after.length() - 2, after.length());
				return new String[] { before.toString(), after.toString() };
			}

			private void createTimeString(
					WifeDataDaySchedule src,
					WifeDataDaySchedule dst,
					StringBuffer before,
					StringBuffer after,
					int i) {
				int srcOn = src.getOnTime(i);
				int dstOn = dst.getOnTime(i);
				int srcOff = src.getOffTime(i);
				int dstOff = dst.getOffTime(i);
				if (srcOn != dstOn || srcOff != dstOff) {
					DecimalFormat f = new DecimalFormat("0000");
					before.append(getScheduleCount(i)).append(scheduleText)
							.append("=").append(f.format(srcOn)).append(":")
							.append(f.format(srcOff));
					after.append(getScheduleCount(i)).append(scheduleText)
							.append("=").append(f.format(dstOn)).append(":")
							.append(f.format(dstOff));
					before.append(", ");
					after.append(", ");
				}
			}
		};

		/** カレンダーデータの値 */
		public static ValueConvertUtil WifeDataCalendar = new ValueConvertUtil() {
			private Map modeNameMap;
			private String COMMA_SPACE = ", ";

			/**
			 * 変更前後データの比較結果を返す為、元のロジックをオーバーライドしています。
			 */
			String getBeforeString(DataHolder dh, Object value) {
				return getCompareResult(dh, value)[0];
			}

			String getAfterString(DataHolder dh, Object value) {
				return getCompareResult(dh, value)[1];
			}

			private String[] getCompareResult(DataHolder dh, Object value) {
				WifeDataCalendar src = (WifeDataCalendar) dh.getValue();
				WifeDataCalendar dst = (WifeDataCalendar) value;
				StringBuffer before = new StringBuffer(100);
				StringBuffer after = new StringBuffer(100);
				boolean[] modes = new boolean[src.getSpecialDayCount() + 1];
				String[] modeNames = new String[src.getSpecialDayCount() + 1];
				Arrays.fill(modes, false);
				for (int i = 0; i < (src.getSpecialDayCount() + 1); i++) {
					for (int j = 0; j < 12; j++) {
						for (int k = 0; k < 31; k++) {
							boolean srcBit = src.testBit(i, j, k);
							boolean dstBit = dst.testBit(i, j, k);
							if (srcBit != dstBit) {
								before.append(j + 1).append("/").append(k + 1)
										.append("=").append(srcBit).append(
												COMMA_SPACE);
								after.append(j + 1).append("/").append(k + 1)
										.append("=").append(dstBit).append(
												COMMA_SPACE);
								modes[i] = true;
								modeNames[i] = getModeName(i);
							}
						}
					}
					if (before.length() > 0 && modes[i]) {
						deleteLastComma(before);
						deleteLastComma(after);
					}
					if (modes[i]) {
						before
								.insert(getStartIndex(before), modeNames[i]
										+ "{");
						after.insert(getStartIndex(after), modeNames[i] + "{");
						before.append("}, ");
						after.append("}, ");
					}
				}
				deleteLastComma(before);
				deleteLastComma(after);
				return new String[] { before.toString(), after.toString() };
			}

			private void deleteLastComma(StringBuffer b) {
				b.delete(b.length() - COMMA_SPACE.length(), b.length());
			}

			private int getStartIndex(StringBuffer b) {
				int start = Math.max(0, b.lastIndexOf("}, "));
				return start != 0 ? start += 3 : start;
			}

			private String getModeName(int mode) {
				if (modeNameMap == null) {
					modeNameMap = new HashMap();
					modeNameMap.put(new Integer(0), "休日");
					modeNameMap.put(new Integer(1), "特殊日1");
					modeNameMap.put(new Integer(2), "特殊日2");
					modeNameMap.put(new Integer(3), "特殊日3");
					modeNameMap.put(new Integer(4), "特殊日4");
					modeNameMap.put(new Integer(5), "特殊日5");
				}
				if (modeNameMap.containsKey(new Integer(mode))) {
					return (String) modeNameMap.get(new Integer(mode));
				} else {
					throw new IllegalArgumentException(
							"Not found this mode. : " + mode);
				}
			}
		};

		/** タイムスタンプデータの値 */
		public static ValueConvertUtil WifeDataTimestamp = new ValueConvertUtil() {
			String getAfterString(DataHolder dh, Object value) {
				WifeDataTimestamp timestamp = (WifeDataTimestamp) value;
				return timestamp.toString();
			}
		};

		/** デジタルデータの値 */
		public static ValueConvertUtil WifeDataDigital = new ValueConvertUtil() {
			String getAfterString(DataHolder dh, Object value) {
				WifeDataDigital digital = (WifeDataDigital) value;
				return digital.isOnOff(true) ? "true" : "false";
			}
		};
	}
}
