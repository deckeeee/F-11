/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/ConvertValue.java,v 1.8.2.4 2005/12/14 01:23:21 frdm Exp $
 * $Revision: 1.8.2.4 $
 * $Date: 2005/12/14 01:23:21 $
 * 
 * =============================================================================
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

package org.F11.scada.data;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.DecimalFormat;

import org.F11.scada.applet.expression.text.Format;
import org.F11.scada.applet.expression.text.FormatFactory;

/**
 * アナログデータ値のスケール変換の演算をするクラスです。
 * このクラスは不変クラスです。
 */
public final class ConvertValue implements Serializable {
	private static final long serialVersionUID = -737635947814754651L;
	/** 変換最小値と最大値です。 */
	private final double convertMin;
	private final double convertMax;
	/** 入力最小値と最大値です。 */
	private final double inputMin;
	private final double inputMax;
	/** デフォルトフォーマットパターンです。 */
	private final String pattern;

	/** データの数値変換タイプです。数値変換ロジックを実装します。 */
	private final ConvertValueType valueType;
	/** 力率変換時のフォーマットタイプです。フォーマット変換を実装します。 */
	private final PowerFactorType valuePFType;

	/**
	 * プライベートコンストラクタです。
	 * 直接インスタンスを作成することは出来ません。
	 * @param convertMin 変換最小値
	 * @param convertMax 変換最大値
	 * @param inputMin 入力最小値
	 * @param inputMax 入力最大値
	 * @param pattern デフォルトフォーマットパターン
	 * @param valueType 数値変換タイプ
	 * @param valuePFType 力率変換フォーマットタイプ
	 */
	private ConvertValue(
		double convertMin,
		double convertMax,
		double inputMin,
		double inputMax,
		String pattern,
		ConvertValueType valueType,
		PowerFactorType valuePFType) {
		this.convertMin = convertMin;
		this.convertMax = convertMax;
		this.inputMin = inputMin;
		this.inputMax = inputMax;
		this.pattern = pattern;
		this.valueType = valueType;
		this.valuePFType = valuePFType;
	}

	/**
	 * データの数値変換タイプです。数値変換ロジックを実装します。
	 */
	static abstract class ConvertValueType implements Serializable {
		private static final long serialVersionUID = 4749042954559578974L;

		private ConvertValueType() {
		};

		abstract double convertDoubleValue(
			double convertMin,
			double convertMax,
			double inputMin,
			double inputMax,
			double value);
		abstract String convertStringValue(
			double convertMin,
			double convertMax,
			double inputMin,
			double inputMax,
			double value,
			String pattern,
			PowerFactorType valuePFType);
		abstract double convertInputValue(
			double convertMin,
			double convertMax,
			double inputMin,
			double inputMax,
			String value,
			PowerFactorType valuePFType);
		abstract double convertInputValue(
			double convertMin,
			double convertMax,
			double inputMin,
			double inputMax,
			double value,
			PowerFactorType valuePFType);
		abstract String convertStringValueUnlimited(
			double convertMin,
			double convertMax,
			double inputMin,
			double inputMax,
			double value,
			String pattern,
			PowerFactorType valuePFType);
		abstract double convertInputValueUnlimited(
			double convertMin,
			double convertMax,
			double inputMin,
			double inputMax,
			double value,
			PowerFactorType valuePFType);

		/**
		 * アナログデータ変換クラスです。
		 */
		static final ConvertValueType ANALOG = new ConvertValueType() {

			private static final long serialVersionUID = 8458685529603726549L;

			// PLC値 -> トレンドグラフ表示値（限界値なしの変換）
			double convertDoubleValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value) {
				double result = value;
				if ((inputMax - inputMin) != 0) {
					double coefficient = (convertMax - convertMin) / (inputMax - inputMin);
					result = coefficient * (value - inputMin) + convertMin;
				}
				return result;
			}
			// PLC値 -> 表示値
			String convertStringValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					String pattern,
					PowerFactorType valuePFType) {
				double result =
					convertDoubleValue(convertMin, convertMax, inputMin, inputMax, value);
				if (result < Math.min(convertMax, convertMin))
					result = Math.min(convertMax, convertMin);
				if (result > Math.max(convertMax, convertMin))
					result = Math.max(convertMax, convertMin);
				FormatFactory factory = new FormatFactory();
				Format format = factory.getFormat(pattern);
//				DecimalFormat format = new DecimalFormat(pattern);
				/*
				StringBuffer buff = new StringBuffer(format.format(result));
				while (buff.length() < pattern.length()) {
					buff.insert(0, ' ');
				}
				return buff.toString();
				*/
				return format.format(result);
			}
			// 表示値 -> PLC値
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					String value,
					PowerFactorType valuePFType) {
				return convertInputValue(
					convertMin,
					convertMax,
					inputMin,
					inputMax,
					Double.parseDouble(value),
					valuePFType);
			}
			// 表示値 -> PLC値
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					PowerFactorType valuePFType) {
				double result = value;
				if ((convertMax - convertMin) != 0) {
					double coefficient = (inputMax - inputMin) / (convertMax - convertMin);
					result = coefficient * (value - convertMin) + inputMin;
				}
				if (result < Math.min(inputMax, inputMin))
					result = Math.min(inputMax, inputMin);
				if (result > Math.max(inputMax, inputMin))
					result = Math.max(inputMax, inputMin);
				return result;
			}

			public String toString() {
				return "ANALOG";
			}
			
			String convertStringValueUnlimited(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					String pattern,
					PowerFactorType valuePFType) {
				double result =
					convertDoubleValue(convertMin, convertMax, inputMin, inputMax, value);
				FormatFactory factory = new FormatFactory();
				Format format = factory.getFormat(pattern);
				return format.format(result);
			}
						
			double convertInputValueUnlimited(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					PowerFactorType valuePFType) {
				double result = value;
				if ((convertMax - convertMin) != 0) {
					double coefficient = (inputMax - inputMin) / (convertMax - convertMin);
					result = coefficient * (value - convertMin) + inputMin;
				}
				return result;
			}
		};

		/**
		 * 力率変換クラスです。
		 * メソッド引数の convertMin, convertMax には前半の変換最小値、変換最大値を渡すこと
		 */
		static final ConvertValueType POWERFACTOR = new ConvertValueType() {
			private static final long serialVersionUID = -4112293574239248548L;

			// PLC値 -> トレンドグラフ表示値（中心が 0 、限界値なしの変換）
			double convertDoubleValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value) {
				double min = convertMax - convertMin;
				double max = min * (-1);
				double result = value;
				if ((inputMax - inputMin) != 0) {
					double coefficient = (max - min) / (inputMax - inputMin);
					result = coefficient * (value - inputMin) + min;
				}
				return result;
			}
			// PLC値 -> 表示値
			String convertStringValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					String pattern,
					PowerFactorType valuePFType) {
				// 後半を前半の連続として計算
				double max = convertMax * 2 - convertMin;
				double result = value;
				if ((inputMax - inputMin) != 0) {
					double coefficient = (max - convertMin) / (inputMax - inputMin);
					result = coefficient * (value - inputMin) + convertMin;
				}
				if (value < ((inputMax + inputMin) / 2)) {
					// 入力値前半
					if (result <= Math.min(convertMax, convertMin))
						result = Math.min(convertMax, convertMin);
					if (result >= Math.max(convertMax, convertMin))
						result = Math.max(convertMax, convertMin);
					return valuePFType.formatFirst(result, pattern, convertMax);
				} else {
					// 入力値後半
					double latterMax = convertMin * (-1.0);
					double latterMin = convertMax * (-1.0);
					// 本来の値にシフト
					result -= (convertMax * 2);
					if (result <= Math.min(latterMax, latterMin))
						result = Math.min(latterMax, latterMin);
					if (result >= Math.max(latterMax, latterMin))
						result = Math.max(latterMax, latterMin);
					return valuePFType.formatLatter(result, pattern, latterMin);
				}
			}
			// 表示値 -> PLC値
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					String value,
					PowerFactorType valuePFType) {
				double srcdata = valuePFType.doubleValue(value, convertMax);
				if (valuePFType.isFirst(value, convertMax)) {
					// 入力値前半
					if (srcdata < Math.min(convertMax, convertMin))
						srcdata = Math.min(convertMax, convertMin);
					if (srcdata > Math.max(convertMax, convertMin))
						srcdata = Math.max(convertMax, convertMin);
				} else {
					// 入力値後半
					double latterMax = convertMin * (-1.0);
					double latterMin = convertMax * (-1.0);
					if (srcdata < Math.min(latterMax, latterMin))
						srcdata = Math.min(latterMax, latterMin);
					if (srcdata > Math.max(latterMax, latterMin))
						srcdata = Math.max(latterMax, latterMin);
					// 値をシフト
					srcdata += (convertMax * 2);
				}
				// 後半を前半の連続として計算
				double max = convertMax * 2 - convertMin;
				double result = srcdata;
				if ((max - convertMin) != 0) {
					double coefficient = (inputMax - inputMin) / (max - convertMin);
					result = coefficient * (srcdata - convertMin) + inputMin;
				}
				return result;
			}
			// トレンドグラフ表示値（中心が 0 、限界値なしの変換） -> PLC値
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					PowerFactorType valuePFType) {
				double min = convertMax - convertMin;
				double max = min * (-1);
				double result = value;
				if ((max - min) != 0) {
					double coefficient = (inputMax - inputMin) / (max - min);
					result = coefficient * (value - min) + inputMin;
				}
				if (result < Math.min(inputMax, inputMin))
					result = Math.min(inputMax, inputMin);
				if (result > Math.max(inputMax, inputMin))
					result = Math.max(inputMax, inputMin);
				return result;
			}

			public String toString() {
				return "POWERFACTOR";
			}

			String convertStringValueUnlimited(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					String pattern,
					PowerFactorType valuePFType) {
				// 後半を前半の連続として計算
				double max = convertMax * 2 - convertMin;
				double result = value;
				if ((inputMax - inputMin) != 0) {
					double coefficient = (max - convertMin) / (inputMax - inputMin);
					result = coefficient * (value - inputMin) + convertMin;
				}
				if (value < ((inputMax + inputMin) / 2)) {
					// 入力値前半
					return valuePFType.formatFirst(result, pattern, convertMax);
				} else {
					// 入力値後半
//					double latterMax = convertMin * (-1.0);
					double latterMin = convertMax * (-1.0);
					// 本来の値にシフト
					result -= (convertMax * 2);
					return valuePFType.formatLatter(result, pattern, latterMin);
				}
			}

			// トレンドグラフ表示値（中心が 0 、限界値なしの変換） -> PLC値
			double convertInputValueUnlimited(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					PowerFactorType valuePFType) {
				double min = convertMax - convertMin;
				double max = min * (-1);
				double result = value;
				if ((max - min) != 0) {
					double coefficient = (inputMax - inputMin) / (max - min);
					result = coefficient * (value - min) + inputMin;
				}
				return result;
			}
		};

		/**
		 * デジタルデータ変換クラスです。
		 */
		static final ConvertValueType DIGITAL = new ConvertValueType() {

			private static final long serialVersionUID = 6555301602824914005L;

			// PLC値 -> トレンドグラフ表示値（限界値なしの変換）
			double convertDoubleValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value) {
				return value;
			}
			// PLC値 -> 表示値
			String convertStringValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					String pattern,
					PowerFactorType valuePFType) {
				return Math.round(value) == 0L ? "0" : "1";
			}
			// 表示値 -> PLC値
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					String value,
					PowerFactorType valuePFType) {
				return Double.parseDouble(value);
			}
			// 表示値 -> PLC値
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					PowerFactorType valuePFType) {
				return value;
			}

			public String toString() {
				return "DIGITAL";
			}
			
			String convertStringValueUnlimited(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					String pattern,
					PowerFactorType valuePFType) {
				return Math.round(value) == 0L ? "0" : "1";
			}
						
			double convertInputValueUnlimited(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					PowerFactorType valuePFType) {
				return value;
			}
		};

		//シリアライズの為に必要な処理
		private static int nextOrdinal = 0;
		private final int ordinal = nextOrdinal++;
		private static final ConvertValueType[] VALUES = { ANALOG, POWERFACTOR, DIGITAL };
		Object readResolve() throws ObjectStreamException {
			return VALUES[ordinal];
		}
	}

	/**
	 * 力率変換時のフォーマットタイプです。フォーマット変換を実装します。
	 */
	static abstract class PowerFactorType implements Serializable {
		private static final long serialVersionUID = 3205223426864149286L;

		private PowerFactorType() {
		};

		/*
		private static String formatSizeble(double value, String pattern) {
			DecimalFormat format = new DecimalFormat(pattern);
			StringBuffer buff = new StringBuffer(format.format(value));
			while (buff.length() < pattern.length()) {
				buff.insert(0, ' ');
			}
			return buff.toString();
		}
		*/

		abstract String formatFirst(double value, String pattern, double max);
		abstract String formatLatter(double value, String pattern, double max);
		abstract double doubleValue(String value, double max);
		abstract boolean isFirst(String value, double max);
		/**
		 * 変換結果の先頭に "LE ","   ","LA " を付加します。
		 */
		static final PowerFactorType LELA = new PowerFactorType() {
			private static final long serialVersionUID = -5114726784511154213L;

			String formatFirst(double value, String pattern, double max) {
				DecimalFormat format = new DecimalFormat(pattern);
				String strval = format.format(Math.abs(value));
				//				String strval = formatSizeble(Math.abs(value), pattern);
				if (Math.abs(max) == Double.parseDouble(strval)) {
					return "  " + strval;
				}
				return "LE" + strval;
			}
			String formatLatter(double value, String pattern, double max) {
				DecimalFormat format = new DecimalFormat(pattern);
				String strval = format.format(Math.abs(value));
				//				String strval = formatSizeble(Math.abs(value), pattern);
				if (Math.abs(max) == Double.parseDouble(strval)) {
					return "  " + strval;
				}
				return "LA" + strval;
			}
			double doubleValue(String value, double max) {
				if (max < 0) {
					if (isFirst(value, max)) {
						return Double.parseDouble(value.substring(2)) * (-1.0);
					} else {
						return Double.parseDouble(value.substring(2));
					}
				} else {
					if (isFirst(value, max)) {
						return Double.parseDouble(value.substring(2));
					} else {
						return Double.parseDouble(value.substring(2)) * (-1.0);
					}
				}
			}
			boolean isFirst(String value, double max) {
				return value.startsWith("LE");
			}

			public String toString() {
				return "LELA";
			}
		};

		/**
		 * 変換結果の先頭に "LA ","   ","LE " を付加します。
		 */
		static final PowerFactorType LALE = new PowerFactorType() {
			private static final long serialVersionUID = 18129413288583713L;

			String formatFirst(double value, String pattern, double max) {
				DecimalFormat format = new DecimalFormat(pattern);
				String strval = format.format(Math.abs(value));
				//				String strval = formatSizeble(Math.abs(value), pattern);
				if (Math.abs(max) == Double.parseDouble(strval)) {
					return "  " + strval;
				}
				return "LA" + strval;
			}
			String formatLatter(double value, String pattern, double max) {
				DecimalFormat format = new DecimalFormat(pattern);
				String strval = format.format(Math.abs(value));
				//				String strval = formatSizeble(Math.abs(value), pattern);
				if (Math.abs(max) == Double.parseDouble(strval)) {
					return "  " + strval;
				}
				return "LE" + strval;
			}
			double doubleValue(String value, double max) {
				if (max < 0) {
					if (isFirst(value, max)) {
						return Double.parseDouble(value.substring(2)) * (-1.0);
					} else {
						return Double.parseDouble(value.substring(2));
					}
				} else {
					if (isFirst(value, max)) {
						return Double.parseDouble(value.substring(2));
					} else {
						return Double.parseDouble(value.substring(2)) * (-1.0);
					}
				}
			}
			boolean isFirst(String value, double max) {
				return value.startsWith("LA");
			}

			public String toString() {
				return "LALE";
			}
		};

		/**
		 * 変換結果の先頭に符合を付加します。
		 */
		static final PowerFactorType DECIMAL = new PowerFactorType() {
			private static final long serialVersionUID = -823616210356873091L;

			String formatFirst(double value, String pattern, double max) {
				DecimalFormat format = new DecimalFormat(pattern);
				String strval = format.format(value);
				if (Math.abs(max) == Math.abs(Double.parseDouble(strval)))
					return format.format(Math.abs(value));
				return strval;
				/*
				StringBuffer strval = new StringBuffer(format.format(Math.abs(value)));
				if (max < 0 && Math.abs(max) != Double.parseDouble(strval.toString())) {
					strval.insert(0, '-');
				}
				while (strval.length() < pattern.length()) {
					strval.insert(0, ' ');
				}
				return strval.toString();
				*/
			}
			String formatLatter(double value, String pattern, double max) {
				DecimalFormat format = new DecimalFormat(pattern);
				String strval = format.format(value);
				if (Math.abs(max) == Math.abs(Double.parseDouble(strval)))
					return format.format(Math.abs(value));
				return strval;
				/*
				StringBuffer strval = new StringBuffer(format.format(Math.abs(value)));
				if (max < 0) {
					strval.insert(0, '-');
				}
				while (strval.length() < pattern.length()) {
					strval.insert(0, ' ');
				}
				return strval.toString();
				*/
			}
			double doubleValue(String value, double max) {
				return Double.parseDouble(value);
			}
			boolean isFirst(String value, double max) {
				if (max < 0)
					return (value.indexOf('-') >= 0);
				else
					return (value.indexOf('-') < 0);
			}

			public String toString() {
				return "DECIMAL";
			}
		};

		//シリアライズの為に必要な処理
		private static int nextOrdinal = 0;
		private final int ordinal = nextOrdinal++;
		private static final PowerFactorType[] VALUES = { LELA, LALE, DECIMAL };
		Object readResolve() throws ObjectStreamException {
			return VALUES[ordinal];
		}
	}

	/**
	 * このオブジェクト自身の生成メソッドです。
	 * 通常のアナログ値を変換します。
	 * @param convertMin 変換最小値
	 * @param convertMax 変換最大値
	 * @param inputMin 入力最小値
	 * @param inputMax 入力最大値
	 * @param pattern フォーマットパターン
	 * @return アナログ値変換オブジェクト
	 */
	public static ConvertValue valueOfANALOG(
		double convertMin,
		double convertMax,
		double inputMin,
		double inputMax,
		String pattern) {
		if (pattern == null || pattern.equals("")) {
			return new ConvertValue(
				convertMin,
				convertMax,
				inputMin,
				inputMax,
				createPattern(convertMin, convertMax),
				ConvertValueType.ANALOG,
				PowerFactorType.DECIMAL);
		} else {
			return new ConvertValue(
				convertMin,
				convertMax,
				inputMin,
				inputMax,
				pattern,
				ConvertValueType.ANALOG,
				PowerFactorType.DECIMAL);
		}
	}

	/**
	 * <p>変換値から一般的な表示パターンを作成します。天文学的数値等を扱う場合は使用できません。</p>
	 * <ul>
	 * <li>表示最大値が 10 未満の場合は、"0.000"
	 * <li>表示最大値が 100 未満の場合は、"#0.00"
	 * <li>表示最大値が 1000 未満の場合は、"##0.0"
	 * <li>表示最大値が 1000 以上の場合は、小数点なしの桁数分の表示パターン文字列
	 * </ul>
	 * @param convertMin 変換最小値
	 * @param convertMax 変換最大値
	 * @return 表示パターンの文字列
	 */
	private static String createPattern(double convertMin, double convertMax) {
		double base = 0;
		if (String.valueOf((int) convertMin).length()
			< String.valueOf((int) convertMax).length()) {
			base = Math.abs(convertMax);
		} else {
			base = Math.abs(convertMin);
		}
		if (base < 10D) {
			return "0.000";
		} else if (base < 100D) {
			return "#0.00";
		} else if (base < 1000D) {
			return "##0.0";
		} else {
			StringBuffer pattern = new StringBuffer();
			pattern.append("0");
			for (int i = 0, length = (String.valueOf((long) base).length() - 1); i < length; i++) {
				pattern.append("#");
			}
			return pattern.reverse().toString();
		}
	}

	/**
	 * このオブジェクト自身の生成メソッドです。
	 * 力率値を変換します。
	 * @param convHalfMin 変換前半最小値
	 * @param convHalfMax 変換前半最大値
	 * @param inputMin 入力最小値
	 * @param inputMax 入力最大値
	 * @param pattern フォーマットパターン
	 * @return 力率変換オブジェクト
	 */
	/** -0.5 ～ -1,1 ～ 0.5 */
	public static ConvertValue valueOfDECIMAL(
		double convHalfMin,
		double convHalfMax,
		double inputMin,
		double inputMax,
		String pattern) {
		if (pattern == null || pattern.equals("")) {
			return new ConvertValue(
				convHalfMin,
				convHalfMax,
				inputMin,
				inputMax,
				createPattern(convHalfMin, convHalfMax),
				ConvertValueType.POWERFACTOR,
				PowerFactorType.DECIMAL);
		} else {
			return new ConvertValue(
				convHalfMin,
				convHalfMax,
				inputMin,
				inputMax,
				pattern,
				ConvertValueType.POWERFACTOR,
				PowerFactorType.DECIMAL);
		}
	}
	/** LE 0.5 ～ 1 ～ LA 0.5 */
	public static ConvertValue valueOfLELA(
		double convHalfMin,
		double convHalfMax,
		double inputMin,
		double inputMax,
		String pattern) {
		if (pattern == null || pattern.equals("")) {
			return new ConvertValue(
				convHalfMin,
				convHalfMax,
				inputMin,
				inputMax,
				createPattern(convHalfMin, convHalfMax),
				ConvertValueType.POWERFACTOR,
				PowerFactorType.LELA);
		} else {
			return new ConvertValue(
				convHalfMin,
				convHalfMax,
				inputMin,
				inputMax,
				pattern,
				ConvertValueType.POWERFACTOR,
				PowerFactorType.LELA);
		}
	}
	/** LA 0.5 ～ 1 ～ LE 0.5 */
	public static ConvertValue valueOfLALE(
		double convHalfMin,
		double convHalfMax,
		double inputMin,
		double inputMax,
		String pattern) {
		if (pattern == null || pattern.equals("")) {
			return new ConvertValue(
				convHalfMin,
				convHalfMax,
				inputMin,
				inputMax,
				createPattern(convHalfMin, convHalfMax),
				ConvertValueType.POWERFACTOR,
				PowerFactorType.LALE);
		} else {
			return new ConvertValue(
				convHalfMin,
				convHalfMax,
				inputMin,
				inputMax,
				pattern,
				ConvertValueType.POWERFACTOR,
				PowerFactorType.LALE);
		}
	}
	/** LA 0.5 ～ 1 ～ LE 0.5 */
	public static ConvertValue valueOfDIGITAL(
			double convHalfMin,
			double convHalfMax,
			double inputMin,
			double inputMax,
			String pattern) {
		return new ConvertValue(
			convHalfMin,
			convHalfMax,
			inputMin,
			inputMax,
			pattern,
			ConvertValueType.DIGITAL,
			PowerFactorType.DECIMAL);
	}

	/**
	 * 設定された変換スパンで、入力値を変換します。
	 * PLC値 -> 変換結果値
	 * @param value アナログ値データ
	 * @return 変換された値
	 */
	public double convertDoubleValue(double value) {
		return valueType.convertDoubleValue(convertMin, convertMax, inputMin, inputMax, value);
	}

	/**
	 * 設定された変換スパンで、入力値を変換します。
	 * PLC値 -> 変換結果値（フォーマット付き）
	 * @param value アナログ値データ
	 * @param pattern フォーマットパターン
	 * @return 変換された値
	 */
	public String convertStringValue(double value, String pattern) {
		return valueType.convertStringValue(
			convertMin,
			convertMax,
			inputMin,
			inputMax,
			value,
			pattern,
			valuePFType);
	}

	/**
	 * 設定された変換スパンで、入力値を変換します。
	 * PLC値 -> 変換結果値（デフォルトフォーマット）
	 * @param value アナログ値データ
	 * @return 変換された値
	 */
	public String convertStringValue(double value) {
		return convertStringValue(value, pattern);
	}

	/**
	 * 設定された変換スパンで、入力値を変換します。
	 * ユーザー入力値 -> PLC値
	 * @param value アナログ値データ
	 * @return 変換されたアナログ値。
	 */
	public double convertInputValue(String value) {
		return valueType.convertInputValue(
			convertMin,
			convertMax,
			inputMin,
			inputMax,
			value,
			valuePFType);
	}

	/**
	 * 設定された変換スパンで、入力値を変換します。
	 * ユーザー入力値 -> PLC値
	 * @param value アナログ値データ
	 * @return 変換されたアナログ値。
	 */
	public double convertInputValue(double value) {
		return valueType.convertInputValue(
			convertMin,
			convertMax,
			inputMin,
			inputMax,
			value,
			valuePFType);
	}

	/**
	 * 設定された変換最大値を返します。
	 * @return 変換最大値
	 */
	public double getConvertMax() {
		return convertMax;
	}

	/**
	 * 設定された変換最小値を返します。
	 * @return 変換最小値
	 */
	public double getConvertMin() {
		return convertMin;
	}

	/**
	 * 設定されたデフォルトフォーマットパターンを返します。
	 * @return デフォルトフォーマットパターン
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * 別のオブジェクトがこの ConvertValue と同じであるかどうかを判定します。
	 * 結果は、引数が null でなく、このオブジェクトと同じ変換最小値・変換最大値・入力最小値・
	 * 入力最大値・フォーマットパターン・数値変換タイプそしてフォーマットタイプの値を持つ
	 * ConvertValue オブジェクトである場合にだけ、 true になります。
	 * @see java.lang.Object#equals(Object)
	 * @param obj この ConvertValue と等しいかどうかが判定されるオブジェクト
	 * @return オブジェクトが同じである場合は true、そうでない場合は false
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ConvertValue)) {
			return false;
		}
		ConvertValue cv = (ConvertValue) obj;
		/*
				System.out.println("convertMin:" + (Double.doubleToLongBits(cv.convertMin) == Double.doubleToLongBits(convertMin)));
				System.out.println("convertMax:" + (Double.doubleToLongBits(cv.convertMax) == Double.doubleToLongBits(convertMax)));
				System.out.println("inputMin:" + (Double.doubleToLongBits(cv.inputMin) == Double.doubleToLongBits(inputMin)));
				System.out.println("inputMax:" + (Double.doubleToLongBits(cv.inputMax) == Double.doubleToLongBits(inputMax)));
				System.out.println("pattern:" + cv.pattern.equals(pattern));
				System.out.println("valueType:" + (cv.valueType == valueType));
				System.out.println("valuePFType:" + (cv.valuePFType == valuePFType));
		*/
		return Double.doubleToLongBits(cv.convertMin) == Double.doubleToLongBits(convertMin)
			&& Double.doubleToLongBits(cv.convertMax) == Double.doubleToLongBits(convertMax)
			&& Double.doubleToLongBits(cv.inputMin) == Double.doubleToLongBits(inputMin)
			&& Double.doubleToLongBits(cv.inputMax) == Double.doubleToLongBits(inputMax)
			&& cv.pattern.equals(pattern)
			&& cv.valueType == valueType
			&& cv.valuePFType == valuePFType;
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		int result = 17;
		long dl = Double.doubleToLongBits(convertMin);
		result = 37 * result + (int) (dl ^ (dl >>> 32));
		dl = Double.doubleToLongBits(convertMax);
		result = 37 * result + (int) (dl ^ (dl >>> 32));
		dl = Double.doubleToLongBits(inputMin);
		result = 37 * result + (int) (dl ^ (dl >>> 32));
		dl = Double.doubleToLongBits(inputMax);
		result = 37 * result + (int) (dl ^ (dl >>> 32));
		result = 37 * result + pattern.hashCode();
		result = 37 * result + valueType.hashCode();
		result = 37 * result + valuePFType.hashCode();
		return result;
	}

	/**
	 * このオブジェクトの文字列表現を返します。
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "{cnvMin="
			+ String.valueOf(convertMin)
			+ " cnvMax="
			+ String.valueOf(convertMax)
			+ " inMin="
			+ String.valueOf(inputMin)
			+ " inMax="
			+ String.valueOf(inputMax)
			+ " format="
			+ pattern
			+ " type="
			+ valueType.toString()
			+ " PFtype="
			+ valuePFType.toString()
			+ "}";
	}

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new ConvertValue(
			convertMin,
			convertMax,
			inputMin,
			inputMax,
			pattern,
			valueType,
			valuePFType);
	}

	/**
	 * 設定された変換スパンで、入力値を変換します。
	 * 但し、変換限界値を考慮しません
	 * PLC値 -> 変換結果値（デフォルトフォーマット）
	 * @param value アナログ値データ
	 * @return 変換された値
	 */
	public String convertStringValueUnlimited(double value) {
		return valueType.convertStringValueUnlimited(
			convertMin,
			convertMax,
			inputMin,
			inputMax,
			value,
			pattern,
			valuePFType);
	}

	/**
	 * 設定された変換スパンで、入力値を変換します。
	 * 但し、変換限界値を考慮しません
	 * ユーザー入力値 -> PLC値
	 * @param value アナログ値データ
	 * @return 変換されたアナログ値。
	 */
	public double convertInputValueUnlimited(double value) {
		return valueType.convertInputValueUnlimited(
			convertMin,
			convertMax,
			inputMin,
			inputMax,
			value,
			valuePFType);
	}
	
	/**
	 * コンバーターの種類を文字列で返します
	 * @return アナログなら "ANALOG", 力率なら "LELA" "LALE"等の文字列を返します
	 */
	public String getValueType() {
		String type = valueType.toString();
		return type.equals("ANALOG") ? type : valuePFType.toString();
	}
}
