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
 * �A�i���O�f�[�^�l�̃X�P�[���ϊ��̉��Z������N���X�ł��B
 * ���̃N���X�͕s�σN���X�ł��B
 */
public final class ConvertValue implements Serializable {
	private static final long serialVersionUID = -737635947814754651L;
	/** �ϊ��ŏ��l�ƍő�l�ł��B */
	private final double convertMin;
	private final double convertMax;
	/** ���͍ŏ��l�ƍő�l�ł��B */
	private final double inputMin;
	private final double inputMax;
	/** �f�t�H���g�t�H�[�}�b�g�p�^�[���ł��B */
	private final String pattern;

	/** �f�[�^�̐��l�ϊ��^�C�v�ł��B���l�ϊ����W�b�N���������܂��B */
	private final ConvertValueType valueType;
	/** �͗��ϊ����̃t�H�[�}�b�g�^�C�v�ł��B�t�H�[�}�b�g�ϊ����������܂��B */
	private final PowerFactorType valuePFType;

	/**
	 * �v���C�x�[�g�R���X�g���N�^�ł��B
	 * ���ڃC���X�^���X���쐬���邱�Ƃ͏o���܂���B
	 * @param convertMin �ϊ��ŏ��l
	 * @param convertMax �ϊ��ő�l
	 * @param inputMin ���͍ŏ��l
	 * @param inputMax ���͍ő�l
	 * @param pattern �f�t�H���g�t�H�[�}�b�g�p�^�[��
	 * @param valueType ���l�ϊ��^�C�v
	 * @param valuePFType �͗��ϊ��t�H�[�}�b�g�^�C�v
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
	 * �f�[�^�̐��l�ϊ��^�C�v�ł��B���l�ϊ����W�b�N���������܂��B
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
		 * �A�i���O�f�[�^�ϊ��N���X�ł��B
		 */
		static final ConvertValueType ANALOG = new ConvertValueType() {

			private static final long serialVersionUID = 8458685529603726549L;

			// PLC�l -> �g�����h�O���t�\���l�i���E�l�Ȃ��̕ϊ��j
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
			// PLC�l -> �\���l
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
			// �\���l -> PLC�l
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
			// �\���l -> PLC�l
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
		 * �͗��ϊ��N���X�ł��B
		 * ���\�b�h������ convertMin, convertMax �ɂ͑O���̕ϊ��ŏ��l�A�ϊ��ő�l��n������
		 */
		static final ConvertValueType POWERFACTOR = new ConvertValueType() {
			private static final long serialVersionUID = -4112293574239248548L;

			// PLC�l -> �g�����h�O���t�\���l�i���S�� 0 �A���E�l�Ȃ��̕ϊ��j
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
			// PLC�l -> �\���l
			String convertStringValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value,
					String pattern,
					PowerFactorType valuePFType) {
				// �㔼��O���̘A���Ƃ��Čv�Z
				double max = convertMax * 2 - convertMin;
				double result = value;
				if ((inputMax - inputMin) != 0) {
					double coefficient = (max - convertMin) / (inputMax - inputMin);
					result = coefficient * (value - inputMin) + convertMin;
				}
				if (value < ((inputMax + inputMin) / 2)) {
					// ���͒l�O��
					if (result <= Math.min(convertMax, convertMin))
						result = Math.min(convertMax, convertMin);
					if (result >= Math.max(convertMax, convertMin))
						result = Math.max(convertMax, convertMin);
					return valuePFType.formatFirst(result, pattern, convertMax);
				} else {
					// ���͒l�㔼
					double latterMax = convertMin * (-1.0);
					double latterMin = convertMax * (-1.0);
					// �{���̒l�ɃV�t�g
					result -= (convertMax * 2);
					if (result <= Math.min(latterMax, latterMin))
						result = Math.min(latterMax, latterMin);
					if (result >= Math.max(latterMax, latterMin))
						result = Math.max(latterMax, latterMin);
					return valuePFType.formatLatter(result, pattern, latterMin);
				}
			}
			// �\���l -> PLC�l
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					String value,
					PowerFactorType valuePFType) {
				double srcdata = valuePFType.doubleValue(value, convertMax);
				if (valuePFType.isFirst(value, convertMax)) {
					// ���͒l�O��
					if (srcdata < Math.min(convertMax, convertMin))
						srcdata = Math.min(convertMax, convertMin);
					if (srcdata > Math.max(convertMax, convertMin))
						srcdata = Math.max(convertMax, convertMin);
				} else {
					// ���͒l�㔼
					double latterMax = convertMin * (-1.0);
					double latterMin = convertMax * (-1.0);
					if (srcdata < Math.min(latterMax, latterMin))
						srcdata = Math.min(latterMax, latterMin);
					if (srcdata > Math.max(latterMax, latterMin))
						srcdata = Math.max(latterMax, latterMin);
					// �l���V�t�g
					srcdata += (convertMax * 2);
				}
				// �㔼��O���̘A���Ƃ��Čv�Z
				double max = convertMax * 2 - convertMin;
				double result = srcdata;
				if ((max - convertMin) != 0) {
					double coefficient = (inputMax - inputMin) / (max - convertMin);
					result = coefficient * (srcdata - convertMin) + inputMin;
				}
				return result;
			}
			// �g�����h�O���t�\���l�i���S�� 0 �A���E�l�Ȃ��̕ϊ��j -> PLC�l
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
				// �㔼��O���̘A���Ƃ��Čv�Z
				double max = convertMax * 2 - convertMin;
				double result = value;
				if ((inputMax - inputMin) != 0) {
					double coefficient = (max - convertMin) / (inputMax - inputMin);
					result = coefficient * (value - inputMin) + convertMin;
				}
				if (value < ((inputMax + inputMin) / 2)) {
					// ���͒l�O��
					return valuePFType.formatFirst(result, pattern, convertMax);
				} else {
					// ���͒l�㔼
//					double latterMax = convertMin * (-1.0);
					double latterMin = convertMax * (-1.0);
					// �{���̒l�ɃV�t�g
					result -= (convertMax * 2);
					return valuePFType.formatLatter(result, pattern, latterMin);
				}
			}

			// �g�����h�O���t�\���l�i���S�� 0 �A���E�l�Ȃ��̕ϊ��j -> PLC�l
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
		 * �f�W�^���f�[�^�ϊ��N���X�ł��B
		 */
		static final ConvertValueType DIGITAL = new ConvertValueType() {

			private static final long serialVersionUID = 6555301602824914005L;

			// PLC�l -> �g�����h�O���t�\���l�i���E�l�Ȃ��̕ϊ��j
			double convertDoubleValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					double value) {
				return value;
			}
			// PLC�l -> �\���l
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
			// �\���l -> PLC�l
			double convertInputValue(
					double convertMin,
					double convertMax,
					double inputMin,
					double inputMax,
					String value,
					PowerFactorType valuePFType) {
				return Double.parseDouble(value);
			}
			// �\���l -> PLC�l
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

		//�V���A���C�Y�ׂ̈ɕK�v�ȏ���
		private static int nextOrdinal = 0;
		private final int ordinal = nextOrdinal++;
		private static final ConvertValueType[] VALUES = { ANALOG, POWERFACTOR, DIGITAL };
		Object readResolve() throws ObjectStreamException {
			return VALUES[ordinal];
		}
	}

	/**
	 * �͗��ϊ����̃t�H�[�}�b�g�^�C�v�ł��B�t�H�[�}�b�g�ϊ����������܂��B
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
		 * �ϊ����ʂ̐擪�� "LE ","   ","LA " ��t�����܂��B
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
		 * �ϊ����ʂ̐擪�� "LA ","   ","LE " ��t�����܂��B
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
		 * �ϊ����ʂ̐擪�ɕ�����t�����܂��B
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

		//�V���A���C�Y�ׂ̈ɕK�v�ȏ���
		private static int nextOrdinal = 0;
		private final int ordinal = nextOrdinal++;
		private static final PowerFactorType[] VALUES = { LELA, LALE, DECIMAL };
		Object readResolve() throws ObjectStreamException {
			return VALUES[ordinal];
		}
	}

	/**
	 * ���̃I�u�W�F�N�g���g�̐������\�b�h�ł��B
	 * �ʏ�̃A�i���O�l��ϊ����܂��B
	 * @param convertMin �ϊ��ŏ��l
	 * @param convertMax �ϊ��ő�l
	 * @param inputMin ���͍ŏ��l
	 * @param inputMax ���͍ő�l
	 * @param pattern �t�H�[�}�b�g�p�^�[��
	 * @return �A�i���O�l�ϊ��I�u�W�F�N�g
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
	 * <p>�ϊ��l�����ʓI�ȕ\���p�^�[�����쐬���܂��B�V���w�I���l���������ꍇ�͎g�p�ł��܂���B</p>
	 * <ul>
	 * <li>�\���ő�l�� 10 �����̏ꍇ�́A"0.000"
	 * <li>�\���ő�l�� 100 �����̏ꍇ�́A"#0.00"
	 * <li>�\���ő�l�� 1000 �����̏ꍇ�́A"##0.0"
	 * <li>�\���ő�l�� 1000 �ȏ�̏ꍇ�́A�����_�Ȃ��̌������̕\���p�^�[��������
	 * </ul>
	 * @param convertMin �ϊ��ŏ��l
	 * @param convertMax �ϊ��ő�l
	 * @return �\���p�^�[���̕�����
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
	 * ���̃I�u�W�F�N�g���g�̐������\�b�h�ł��B
	 * �͗��l��ϊ����܂��B
	 * @param convHalfMin �ϊ��O���ŏ��l
	 * @param convHalfMax �ϊ��O���ő�l
	 * @param inputMin ���͍ŏ��l
	 * @param inputMax ���͍ő�l
	 * @param pattern �t�H�[�}�b�g�p�^�[��
	 * @return �͗��ϊ��I�u�W�F�N�g
	 */
	/** -0.5 �` -1,1 �` 0.5 */
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
	/** LE 0.5 �` 1 �` LA 0.5 */
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
	/** LA 0.5 �` 1 �` LE 0.5 */
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
	/** LA 0.5 �` 1 �` LE 0.5 */
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
	 * �ݒ肳�ꂽ�ϊ��X�p���ŁA���͒l��ϊ����܂��B
	 * PLC�l -> �ϊ����ʒl
	 * @param value �A�i���O�l�f�[�^
	 * @return �ϊ����ꂽ�l
	 */
	public double convertDoubleValue(double value) {
		return valueType.convertDoubleValue(convertMin, convertMax, inputMin, inputMax, value);
	}

	/**
	 * �ݒ肳�ꂽ�ϊ��X�p���ŁA���͒l��ϊ����܂��B
	 * PLC�l -> �ϊ����ʒl�i�t�H�[�}�b�g�t���j
	 * @param value �A�i���O�l�f�[�^
	 * @param pattern �t�H�[�}�b�g�p�^�[��
	 * @return �ϊ����ꂽ�l
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
	 * �ݒ肳�ꂽ�ϊ��X�p���ŁA���͒l��ϊ����܂��B
	 * PLC�l -> �ϊ����ʒl�i�f�t�H���g�t�H�[�}�b�g�j
	 * @param value �A�i���O�l�f�[�^
	 * @return �ϊ����ꂽ�l
	 */
	public String convertStringValue(double value) {
		return convertStringValue(value, pattern);
	}

	/**
	 * �ݒ肳�ꂽ�ϊ��X�p���ŁA���͒l��ϊ����܂��B
	 * ���[�U�[���͒l -> PLC�l
	 * @param value �A�i���O�l�f�[�^
	 * @return �ϊ����ꂽ�A�i���O�l�B
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
	 * �ݒ肳�ꂽ�ϊ��X�p���ŁA���͒l��ϊ����܂��B
	 * ���[�U�[���͒l -> PLC�l
	 * @param value �A�i���O�l�f�[�^
	 * @return �ϊ����ꂽ�A�i���O�l�B
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
	 * �ݒ肳�ꂽ�ϊ��ő�l��Ԃ��܂��B
	 * @return �ϊ��ő�l
	 */
	public double getConvertMax() {
		return convertMax;
	}

	/**
	 * �ݒ肳�ꂽ�ϊ��ŏ��l��Ԃ��܂��B
	 * @return �ϊ��ŏ��l
	 */
	public double getConvertMin() {
		return convertMin;
	}

	/**
	 * �ݒ肳�ꂽ�f�t�H���g�t�H�[�}�b�g�p�^�[����Ԃ��܂��B
	 * @return �f�t�H���g�t�H�[�}�b�g�p�^�[��
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * �ʂ̃I�u�W�F�N�g������ ConvertValue �Ɠ����ł��邩�ǂ����𔻒肵�܂��B
	 * ���ʂ́A������ null �łȂ��A���̃I�u�W�F�N�g�Ɠ����ϊ��ŏ��l�E�ϊ��ő�l�E���͍ŏ��l�E
	 * ���͍ő�l�E�t�H�[�}�b�g�p�^�[���E���l�ϊ��^�C�v�����ăt�H�[�}�b�g�^�C�v�̒l������
	 * ConvertValue �I�u�W�F�N�g�ł���ꍇ�ɂ����A true �ɂȂ�܂��B
	 * @see java.lang.Object#equals(Object)
	 * @param obj ���� ConvertValue �Ɠ��������ǂ��������肳���I�u�W�F�N�g
	 * @return �I�u�W�F�N�g�������ł���ꍇ�� true�A�����łȂ��ꍇ�� false
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
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
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
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
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
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
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
	 * �ݒ肳�ꂽ�ϊ��X�p���ŁA���͒l��ϊ����܂��B
	 * �A���A�ϊ����E�l���l�����܂���
	 * PLC�l -> �ϊ����ʒl�i�f�t�H���g�t�H�[�}�b�g�j
	 * @param value �A�i���O�l�f�[�^
	 * @return �ϊ����ꂽ�l
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
	 * �ݒ肳�ꂽ�ϊ��X�p���ŁA���͒l��ϊ����܂��B
	 * �A���A�ϊ����E�l���l�����܂���
	 * ���[�U�[���͒l -> PLC�l
	 * @param value �A�i���O�l�f�[�^
	 * @return �ϊ����ꂽ�A�i���O�l�B
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
	 * �R���o�[�^�[�̎�ނ𕶎���ŕԂ��܂�
	 * @return �A�i���O�Ȃ� "ANALOG", �͗��Ȃ� "LELA" "LALE"���̕������Ԃ��܂�
	 */
	public String getValueType() {
		String type = valueType.toString();
		return type.equals("ANALOG") ? type : valuePFType.toString();
	}
}
