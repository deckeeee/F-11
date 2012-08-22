/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataAnalog.java,v 1.5.2.3 2006/03/15 00:01:03 frdm Exp $
 * $Revision: 1.5.2.3 $
 * $Date: 2006/03/15 00:01:03 $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;

import org.F11.scada.EnvironmentManager;

/**
 * アナログデータクラスです。 このクラスは不変クラスでかつ、new演算子でインスタンスを生成することができません。
 * valueOf～メソッドを使用してインスタンスを作成してください。
 */
public final class WifeDataAnalog extends Number implements WifeData,
		Serializable {
	private static final long serialVersionUID = -3944410544830984985L;

	/** アナログデータ値 */
	private final double value;
	/** アナログデータの形式 */
	private final AnalogType valueType;
	/** double -> byteの前で丸め処理を行うかの有無 */
	private static final boolean analogRoundMode =
		Boolean.valueOf(EnvironmentManager.get("/server/analogRoundMode",
				"false"));

	/**
	 * アナログデータの形式を表すタイプセーフenumクラスです。 各staticフィールドがアナログ元byte配列データの形式を表すインスタンスです。
	 * byte配列を引数にとりWifeDataAnalogを返す抽象クラスを実装しています。 new演算子でインスタンスを生成することはできません。
	 */
	abstract static class AnalogType implements Serializable {
		private static final long serialVersionUID = -347534979180523729L;

		/**
		 * 引数を整数で四捨五入します。
		 *
		 * @param bd 実数
		 * @return 四捨五入した結果
		 */
		BigInteger round(BigDecimal bd) {
			if (analogRoundMode) {
				double tmpValue = bd.doubleValue();
				if (1.0D > tmpValue && 0.0D <= tmpValue) {
					if (0.5D <= tmpValue) {
						return new BigDecimal("1").toBigInteger();
					} else {
						return new BigDecimal("0").toBigInteger();
					}
				} else if (-1.0D < tmpValue && 0.0D > tmpValue) {
					if (-0.5D < tmpValue) {
						return new BigDecimal("0").toBigInteger();
					} else {
						return new BigDecimal("-1").toBigInteger();
					}
				} else {
					BigDecimal tmpBd =
						bd.round(new MathContext(bd.precision() - bd.scale()));
					return tmpBd.toBigInteger();
				}
			} else {
				return bd.toBigInteger();
			}
		}

		/** アナログ元byte配列データの形式がHEXで、0バイト目使用の1/2ワード長アナログデータです */
		static final AnalogType HEX_ZERO_HALF_WORD =
			new AnalogType(0, ByteSize.ZERO_HALF_WORD_ANALOG,
					"HEX_ZERO_HALF_WORD", 0, 255) {
				private static final long serialVersionUID =
					3001100906618864417L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(1, HEX_ZERO_HALF_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfHexZeroHalf(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfHexZeroHalf(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = bd.toBigInteger();
					byte[] b = bi.shiftLeft(8).toByteArray();
					if (b.length == 2) {
						return HEX_ZERO_HALF_WORD.byteSize.toByteArray(b);
					} else if (b.length == 1) { // 値が 0 の時;
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						return b1;
					} else { // 前に0x00がついている場合（値が128以上）
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 1, b1, 0, b1.length);
						return HEX_ZERO_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** アナログ元byte配列データの形式がHEXで、1バイト目使用の1/2ワード長アナログデータです */
		static final AnalogType HEX_ONE_HALF_WORD =
			new AnalogType(0, ByteSize.ONE_HALF_WORD_ANALOG,
					"HEX_ONE_HALF_WORD", 0, 255) {
				private static final long serialVersionUID =
					-6595712322778359083L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(1, HEX_ONE_HALF_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfHexOneHalf(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfHexOneHalf(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = bd.toBigInteger();
					byte[] b = bi.toByteArray();
					if (b.length == 2) {
						return HEX_ONE_HALF_WORD.byteSize.toByteArray(b);
					} else { // 127以下の場合（前に0x00がついていない）
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 0, b1, 1, b.length);
						return HEX_ONE_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** アナログ元byte配列データの形式がHEXで、1ワード長アナログデータです */
		static final AnalogType HEX_SINGLE_WORD =
			new AnalogType(0, ByteSize.SINGLE_WORD_ANALOG, "HEX_SINGLE_WORD",
					0, 65535) {
				private static final long serialVersionUID =
					7541518316060757625L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(1, HEX_SINGLE_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfHexSingle(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfHexSingle(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = round(bd);
					byte[] b = bi.toByteArray();
					if (b.length == 2) {
						return HEX_SINGLE_WORD.byteSize.toByteArray(b);
					} else if (b.length == 1) {
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 0, b1, 1, b.length);
						return HEX_SINGLE_WORD.byteSize.toByteArray(b1);
					} else {
						byte[] b2 = new byte[2];
						Arrays.fill(b2, (byte) 0x00);
						System.arraycopy(b, 1, b2, 0, b2.length);
						return HEX_SINGLE_WORD.byteSize.toByteArray(b2);
					}
				}
			};
		/** アナログ元byte配列データの形式がHEXで、2ワード長アナログデータです */
		static final AnalogType HEX_DOUBLE_WORD =
			new AnalogType(0, ByteSize.DOUBLE_WORD_ANALOG, "HEX_DOUBLE_WORD",
					0, 4294967295D) {
				private static final long serialVersionUID =
					-3026934469719700166L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(1, HEX_DOUBLE_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfHexDouble(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfHexDouble(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = round(bd);
					byte[] b = bi.toByteArray();
					if (b.length == 4) {
						return HEX_DOUBLE_WORD.byteSize.toByteArray(b);
					} else if (b.length > 4) {
						byte[] b1 = new byte[4];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 1, b1, 0, b1.length);
						return HEX_DOUBLE_WORD.byteSize.toByteArray(b1);
					} else {
						byte[] b2 = new byte[4];
						Arrays.fill(b2, (byte) 0x00);
						System.arraycopy(b, 0, b2, b2.length - b.length,
								b.length);
						return HEX_DOUBLE_WORD.byteSize.toByteArray(b2);
					}
				}
			};

		/** アナログ元byte配列データの形式が符号ありHEXで、0バイト目使用の1/2ワード長アナログデータです */
		static final AnalogType SHX_ZERO_HALF_WORD =
			new AnalogType(0, ByteSize.ZERO_HALF_WORD_ANALOG,
					"SHX_ZERO_HALF_WORD", Byte.MIN_VALUE, Byte.MAX_VALUE) {
				private static final long serialVersionUID =
					7793468773198476758L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(HEX_ZERO_HALF_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfShxZeroHalf(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfShxZeroHalf(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = bd.toBigInteger();
					byte[] b = bi.shiftLeft(8).toByteArray();
					if (b.length == 2) {
						return HEX_ZERO_HALF_WORD.byteSize.toByteArray(b);
					} else if (b.length == 1) { // 値が 0 の時;
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						return b1;
					} else { // 前に0x00がついている場合（値が128以上）
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 1, b1, 0, b1.length);
						return HEX_ZERO_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** アナログ元byte配列データの形式が符号ありHEXで、1バイト目使用の1/2ワード長アナログデータです */
		static final AnalogType SHX_ONE_HALF_WORD =
			new AnalogType(0, ByteSize.ONE_HALF_WORD_ANALOG,
					"SHX_ONE_HALF_WORD", Byte.MIN_VALUE, Byte.MAX_VALUE) {
				private static final long serialVersionUID =
					-3227886227896475778L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(HEX_ONE_HALF_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfShxOneHalf(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfShxOneHalf(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = bd.toBigInteger();
					byte[] b = bi.toByteArray();
					if (b.length == 2) {
						return HEX_ONE_HALF_WORD.byteSize.toByteArray(b);
					} else { // 127以下の場合（前に0x00がついていない）
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 0, b1, 1, b.length);
						return HEX_ONE_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** アナログ元byte配列データの形式が符号ありHEXで、1ワード長アナログデータです */
		static final AnalogType SHX_SINGLE_WORD =
			new AnalogType(0, ByteSize.SINGLE_WORD_ANALOG, "SHX_SINGLE_WORD",
					Short.MIN_VALUE, Short.MAX_VALUE) {
				private static final long serialVersionUID =
					-5369305759733297768L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(HEX_SINGLE_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfShxSingle(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfShxSingle(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = round(bd);
					byte[] b = bi.toByteArray();
					if (b.length == 2) {
						return HEX_SINGLE_WORD.byteSize.toByteArray(b);
					} else if (b.length == 1) {
						byte[] b1 = new byte[2];
						if (bi.signum() < 0)
							Arrays.fill(b1, (byte) 0xff);
						else
							Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 0, b1, 1, b.length);
						return HEX_SINGLE_WORD.byteSize.toByteArray(b1);
					} else {
						byte[] b2 = new byte[2];
						if (bi.signum() < 0)
							Arrays.fill(b2, (byte) 0xff);
						else
							Arrays.fill(b2, (byte) 0x00);
						System.arraycopy(b, 1, b2, 0, b2.length);
						return HEX_SINGLE_WORD.byteSize.toByteArray(b2);
					}
				}
			};
		/** アナログ元byte配列データの形式が符号ありHEXで、2ワード長アナログデータです */
		static final AnalogType SHX_DOUBLE_WORD =
			new AnalogType(0, ByteSize.DOUBLE_WORD_ANALOG, "SHX_DOUBLE_WORD",
					Integer.MIN_VALUE, Integer.MAX_VALUE) {
				private static final long serialVersionUID =
					2083293025537772580L;

				WifeDataAnalog valueOf(byte[] b) {
					BigInteger bi =
						new BigInteger(HEX_DOUBLE_WORD.byteSize
								.canonicalBytes(b));
					return WifeDataAnalog.valueOfShxDouble(bi.doubleValue());
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfShxDouble(value);
				}

				byte[] toByteArray(double value) {
					BigDecimal bd = new BigDecimal(value);
					BigInteger bi = round(bd);
					byte[] b = bi.toByteArray();
					if (b.length == 4) {
						return HEX_DOUBLE_WORD.byteSize.toByteArray(b);
					} else if (b.length > 4) {
						byte[] b1 = new byte[4];
						if (bi.signum() < 0)
							Arrays.fill(b1, (byte) 0xff);
						else
							Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 1, b1, 0, b1.length);
						return HEX_DOUBLE_WORD.byteSize.toByteArray(b1);
					} else {
						byte[] b2 = new byte[4];
						if (bi.signum() < 0)
							Arrays.fill(b2, (byte) 0xff);
						else
							Arrays.fill(b2, (byte) 0x00);
						System.arraycopy(b, 0, b2, b2.length - b.length,
								b.length);
						return HEX_DOUBLE_WORD.byteSize.toByteArray(b2);
					}
				}
			};

		/** アナログ元byte配列データの形式がBCDで、0バイト目使用の1/2ワード長アナログデータです */
		static final AnalogType BCD_ZERO_HALF_WORD =
			new AnalogType(1, ByteSize.ZERO_HALF_WORD_ANALOG,
					"BCD_ZERO_HALF_WORD", 0, 99) {
				private static final long serialVersionUID =
					-1698054779788165929L;

				WifeDataAnalog valueOf(byte[] b) {
					return WifeDataAnalog.valueOfBcdZeroHalf(WifeBCD
							.valueOf(BCD_ZERO_HALF_WORD.byteSize
									.canonicalBytes(b)));
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfBcdZeroHalf(value);
				}

				byte[] toByteArray(double value) {
					return BCD_ZERO_HALF_WORD.byteSize.toByteArray(WifeBCD
							.valueOf(value, "00"));
				}
			};
		/** アナログ元byte配列データの形式がBCDで、1バイト目使用の1/2ワード長アナログデータです */
		static final AnalogType BCD_ONE_HALF_WORD =
			new AnalogType(1, ByteSize.ONE_HALF_WORD_ANALOG,
					"BCD_ONE_HALF_WORD", 0, 99) {
				private static final long serialVersionUID =
					-376668186497495188L;

				WifeDataAnalog valueOf(byte[] b) {
					return WifeDataAnalog.valueOfBcdOneHalf(WifeBCD
							.valueOf(BCD_ONE_HALF_WORD.byteSize
									.canonicalBytes(b)));
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfBcdOneHalf(value);
				}

				byte[] toByteArray(double value) {
					return BCD_ONE_HALF_WORD.byteSize.toByteArray(WifeBCD
							.valueOf(value));
				}
			};
		/** アナログ元byte配列データの形式がBCDで、1ワード長アナログデータです */
		static final AnalogType BCD_SINGLE_WORD =
			new AnalogType(1, ByteSize.SINGLE_WORD_ANALOG, "BCD_SINGLE_WORD",
					0, 9999) {
				private static final long serialVersionUID =
					7932049750401319460L;

				WifeDataAnalog valueOf(byte[] b) {
					return WifeDataAnalog
							.valueOfBcdSingle(WifeBCD
									.valueOf(BCD_SINGLE_WORD.byteSize
											.canonicalBytes(b)));
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfBcdSingle(value);
				}

				byte[] toByteArray(double value) {
					return BCD_SINGLE_WORD.byteSize.toByteArray(WifeBCD
							.valueOf(value));
				}
			};
		/** アナログ元byte配列データの形式がBCDで、2ワード長アナログデータです */
		static final AnalogType BCD_DOUBLE_WORD =
			new AnalogType(1, ByteSize.DOUBLE_WORD_ANALOG, "BCD_DOUBLE_WORD",
					0, 99999999) {
				private static final long serialVersionUID =
					7328479680285566556L;

				WifeDataAnalog valueOf(byte[] b) {
					return WifeDataAnalog
							.valueOfBcdDouble(WifeBCD
									.valueOf(BCD_DOUBLE_WORD.byteSize
											.canonicalBytes(b)));
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfBcdDouble(value);
				}

				byte[] toByteArray(double value) {
					return BCD_DOUBLE_WORD.byteSize.toByteArray(WifeBCD
							.valueOf(value, "00000000"));
				}
			};
		/**
		 * アナログ元byte配列データの形式がfloat(IEEE 754 浮動小数点「単精度」ビット配列に従った、浮動小数点表現)
		 * のアナログデータです。
		 */
		static final AnalogType FLOAT_DOUBLE_WORD =
			new AnalogType(2, ByteSize.DOUBLE_WORD_ANALOG, "FLOAT_DOUBLE_WORD",
					Float.MIN_VALUE, Float.MAX_VALUE) {
				private static final long serialVersionUID =
					-30860737092117702L;

				WifeDataAnalog valueOf(byte[] b) {
					DataInputStream in = null;
					try {
						in =
							new DataInputStream(new ByteArrayInputStream(
									FLOAT_DOUBLE_WORD.byteSize
											.canonicalBytes(b)));
						return WifeDataAnalog.valueOfFloat(new Float(Float
								.intBitsToFloat(in.readInt())).doubleValue());
					} catch (IOException ex) {
						ex.printStackTrace();
						throw new IllegalArgumentException(
								"IEEE 754 浮動小数点「単精度」ビット配列 -> float変換にしぱいした。");
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 浮動小数点「単精度」ビット配列 -> float変換にしぱいした。");
							}
						}
					}
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfFloat(value);
				}

				byte[] toByteArray(double value) {
					DataOutputStream dos = null;
					try {
						int i = Float.floatToIntBits((float) value);
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						dos = new DataOutputStream(os);
						dos.writeInt(i);
						dos.flush();
						return FLOAT_DOUBLE_WORD.byteSize.toByteArray(os
								.toByteArray());
					} catch (IOException ex) {
						ex.printStackTrace();
						throw new IllegalArgumentException(
								"IEEE 754 浮動小数点「単精度」ビット配列 -> float変換にしぱいした。");
					} finally {
						if (dos != null) {
							try {
								dos.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 浮動小数点「単精度」ビット配列 -> float変換にしぱいした。");
							}
						}
					}
				}

				/**
				 * オーバーライドします。
				 */
				void checkArgument(double value) {
					Float src = new Float(value);
					if (src.isInfinite() || src.isNaN())
						throw new IllegalArgumentException(
								"Argument value is Infinite or NaN");
				}
			};
		/**
		 * アナログ元byte配列データの形式がdouble(IEEE 754 浮動小数点の「ダブルフォーマット (double format)」
		 * ビットレイアウト)のアナログデータです。
		 */
		static final AnalogType DOUBLE_FOURTH_WORD =
			new AnalogType(3, ByteSize.FOURTH_WORD_ANALOG,
					"DOUBLE_FOURTH_WORD", Double.MIN_VALUE, Double.MAX_VALUE) {
				private static final long serialVersionUID =
					7765706268117247534L;

				WifeDataAnalog valueOf(byte[] b) {
					DataInputStream in = null;
					try {
						in =
							new DataInputStream(new ByteArrayInputStream(
									DOUBLE_FOURTH_WORD.byteSize
											.canonicalBytes(b)));
						return WifeDataAnalog.valueOfDouble(Double
								.longBitsToDouble(in.readLong()));
					} catch (IOException ex) {
						ex.printStackTrace();
						throw new IllegalArgumentException(
								"IEEE 754 浮動小数点「倍精度」ビット配列 -> double変換にしぱいした。");
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 浮動小数点「倍精度」ビット配列 -> double変換にしぱいした。");
							}
						}
					}
				}

				WifeDataAnalog valueOf(double value) {
					checkArgument(value);
					return WifeDataAnalog.valueOfDouble(value);
				}

				byte[] toByteArray(double value) {
					DataOutputStream dos = null;
					try {
						long d = Double.doubleToLongBits(value);
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						dos = new DataOutputStream(os);
						dos.writeLong(d);
						dos.flush();
						return DOUBLE_FOURTH_WORD.byteSize.toByteArray(os
								.toByteArray());
					} catch (IOException ex) {
						ex.printStackTrace();
						throw new IllegalArgumentException(
								"IEEE 754 浮動小数点「倍精度」ビット配列 -> double変換にしぱいした。");
					} finally {
						if (dos != null) {
							try {
								dos.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 浮動小数点「倍精度」ビット配列 -> double変換にしぱいした。");
							}
						}
					}
				}

				/**
				 * オーバーライドします。
				 */
				void checkArgument(double value) {
					Double src = new Double(value);
					if (src.isInfinite() || src.isNaN())
						throw new IllegalArgumentException(
								"Argument value is Infinite or NaN");
				}
			};

		/** アナログ元byte配列データの形式名 */
		private final String name;
		/** アナログ元データのサイズ */
		private final ByteSize byteSize;
		/** このアナログデータの最小値です */
		private final double analogTypeMinValue;
		/** このアナログデータの最大値です */
		private final double analogTypeMaxValue;

		/**
		 * コンストラクタ new演算子でインスタンスを生成できません。 staticフィールドを参照してください。
		 */
		AnalogType(
				int type,
				ByteSize byteSize,
				String name,
				double analogTypeMinValue,
				double analogTypeMaxValue) {
			this.byteSize = byteSize;
			this.name = name;
			this.analogTypeMinValue = analogTypeMinValue;
			this.analogTypeMaxValue = analogTypeMaxValue;
		}

		/**
		 * アナログデータタイプの文字表現を返します。
		 */
		public String toString() {
			return name;
		}

		/**
		 * 引数を変換した値を持つWifeDataAnalogを返します。 変換の実装はそれぞれのタイプによって異なります。
		 * アナログ値の値のみが変更され、それ以外の属性は保持されます。
		 */
		abstract WifeDataAnalog valueOf(byte[] b);

		/**
		 * 引数を変換した値を持つWifeDataAnalogを返します。 変換の実装はそれぞれのタイプによって異なります。
		 * アナログ値の値のみが変更され、それ以外の属性は保持されます。
		 */
		abstract WifeDataAnalog valueOf(double value);

		/**
		 * このデータの値をバイト配列変換して返します。
		 *
		 * @return バイト配列
		 */
		abstract byte[] toByteArray(double value);

		/**
		 * 引数の正当性をチェックします。
		 */
		void checkArgument(double value) {
			if (value < analogTypeMinValue || value > analogTypeMaxValue)
				throw new IllegalArgumentException("MinValue = "
					+ analogTypeMinValue
					+ " MaxValue = "
					+ analogTypeMaxValue
					+ " ArgumentValue = "
					+ value);
		}

		/** ワード長を返します。 */
		int getWordSize() {
			return byteSize.getWordSize();
		}

		// サブクラスがObject.equalsをオーバーライドするのを防ぐ
		public final boolean equals(Object obj) {
			return super.equals(obj);
		}

		public final int hashCode() {
			return super.hashCode();
		}

		// シリアライズの為に必要な処理
		private static int nextOrdinal = 0;
		private final int ordinal = nextOrdinal++;
		private static final AnalogType[] VALUES =
			{
				HEX_ZERO_HALF_WORD,
				HEX_ONE_HALF_WORD,
				HEX_SINGLE_WORD,
				HEX_DOUBLE_WORD,
				SHX_ZERO_HALF_WORD,
				SHX_ONE_HALF_WORD,
				SHX_SINGLE_WORD,
				SHX_DOUBLE_WORD,
				BCD_ZERO_HALF_WORD,
				BCD_ONE_HALF_WORD,
				BCD_SINGLE_WORD,
				BCD_DOUBLE_WORD,
				FLOAT_DOUBLE_WORD,
				DOUBLE_FOURTH_WORD };

		Object readResolve() throws ObjectStreamException {
			return VALUES[ordinal];
		}

		/**
		 * アナログ元データのサイズのタイプセーフemunクラスです。 各staticフィールドがアナログ元データのサイズを表すインスタンスです。
		 * byte配列を引数にとりWifeDataAnalogを返す抽象クラスを実装しています。
		 * new演算子でインスタンスを生成することはできません。
		 */
		abstract static class ByteSize implements Serializable {
			private static final long serialVersionUID = 354931199558298354L;
			/** アナログ元データのサイズです */
			private final int byteSize;
			/**
			 * 1/2ワード長のアナログデータの場合のみ使用します。
			 * 1/2ワード長以外のアナログデータには、HalfWordType.TYPE_ZEROが設定されます。
			 */
			private final HalfWordType halfWordType;

			/** 1/2ワードのアナログデータです(0バイト目を使用) */
			static final ByteSize ZERO_HALF_WORD_ANALOG =
				new ByteSize(1, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-6385568572265636088L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != SINGLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"不正な長さのbyte配列が引数に使用されました。length = "
										+ b.length);
						}
						return new byte[] { b[ZERO_HALF_WORD_ANALOG.halfWordType
								.valueOf()] };
					}

					byte[] toByteArray(byte[] b) {
						return new byte[] {
							b[ZERO_HALF_WORD_ANALOG.halfWordType.valueOf()],
							(byte) 0x00 };
					}
				};
			/** 1/2ワードのアナログデータです(1バイト目を使用) */
			static final ByteSize ONE_HALF_WORD_ANALOG =
				new ByteSize(1, HalfWordType.TYPE_ONE) {
					private static final long serialVersionUID =
						3790365881542551006L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != SINGLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"不正な長さのbyte配列が引数に使用されました。length = "
										+ b.length);
						}
						return new byte[] { b[ONE_HALF_WORD_ANALOG.halfWordType
								.valueOf()] };
					}

					byte[] toByteArray(byte[] b) {
						return new byte[] {
							(byte) 0x00,
							b[ONE_HALF_WORD_ANALOG.halfWordType.valueOf()] };
					}
				};
			/** 1ワードのアナログデータです */
			static final ByteSize SINGLE_WORD_ANALOG =
				new ByteSize(2, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-6025093020033537813L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != SINGLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"不正な長さのbyte配列が引数に使用されました。length = "
										+ b.length);
						}
						return b;
					}

					byte[] toByteArray(byte[] b) {
						byte[] rb = new byte[SINGLE_WORD_ANALOG.byteSize];
						Arrays.fill(rb, (byte) 0x00);
						System.arraycopy(b, 0, rb, SINGLE_WORD_ANALOG.byteSize
							- b.length, b.length);
						return rb;
					}
				};
			/** 2ワードのアナログデータです */
			static final ByteSize DOUBLE_WORD_ANALOG =
				new ByteSize(4, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-4525230963105073071L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != DOUBLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"不正な長さのbyte配列が引数に使用されました。length = "
										+ b.length);
						}
						return swapBytes(b, DOUBLE_WORD_ANALOG.byteSize / 2);
					}

					byte[] toByteArray(byte[] b) {
						byte[] rb = new byte[DOUBLE_WORD_ANALOG.byteSize];
						Arrays.fill(rb, (byte) 0x00);
						System.arraycopy(b, 0, rb, DOUBLE_WORD_ANALOG.byteSize
							- b.length, b.length);
						return swapBytes(rb, DOUBLE_WORD_ANALOG.byteSize / 2);
					}
				};
			/** 4ワードのアナログデータです(BACnetのみで使用？　バイト列交換は無し？) */
			static final ByteSize FOURTH_WORD_ANALOG =
				new ByteSize(8, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-3026858847517993664L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != FOURTH_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"不正な長さのbyte配列が引数に使用されました。length = "
										+ b.length);
						}
						return b;
					}

					byte[] toByteArray(byte[] b) {
						byte[] rb = new byte[FOURTH_WORD_ANALOG.byteSize];
						System.arraycopy(b, 0, rb, 0,
								FOURTH_WORD_ANALOG.byteSize);
						return rb;
					}
				};

			/**
			 * 任意byte毎に入れ替えたbyte配列を返します。
			 *
			 * @param byteArray 元になる配列。
			 * @param size 入れ替え基準サイズ。
			 */
			byte[] swapBytes(byte[] byteArray, int size) {
				byte[] result = new byte[byteArray.length];
				System.arraycopy(byteArray, size, result, 0, size);
				System.arraycopy(byteArray, 0, result, size, size);
				return result;
			}

			/**
			 * コンストラクタ new演算子でインスタンスを生成できません。 staticフィールドを参照してください。
			 */
			ByteSize(int byteSize, HalfWordType halfWordType) {
				this.byteSize = byteSize;
				this.halfWordType = halfWordType;
			}

			/** byteタイプに正規化したバイト配列を返します。 */
			abstract byte[] canonicalBytes(byte[] b);

			/** 引数の byte配列を WORD に正規化します。 */
			abstract byte[] toByteArray(byte[] b);

			/** ワード長を返します。 */
			int getWordSize() {
				return (byteSize == 1) ? 1 : byteSize / 2;
			}

			// サブクラスがObject.equalsをオーバーライドするのを防ぐ
			public final boolean equals(Object obj) {
				return super.equals(obj);
			}

			public final int hashCode() {
				return super.hashCode();
			}

			// シリアライズの為に必要な処理
			private static int nextOrdinal = 0;
			private final int ordinal = nextOrdinal++;
			private static final ByteSize[] VALUES =
				{
					ZERO_HALF_WORD_ANALOG,
					ONE_HALF_WORD_ANALOG,
					SINGLE_WORD_ANALOG,
					DOUBLE_WORD_ANALOG,
					FOURTH_WORD_ANALOG };

			Object readResolve() throws ObjectStreamException {
				return VALUES[ordinal];
			}

			/**
			 * 1/2ワードデータのバイトタイプを表すタイプセーフenumクラスです。
			 * 各staticフィールドが1/2ワードデータのバイトタイプを表すインスタンスです。
			 * new演算子でインスタンスを生成することはできません。
			 */
			static class HalfWordType implements Serializable {
				private static final long serialVersionUID =
					2089788803489359021L;
				/** バイトタイプ */
				private final int halfWordType;

				/** 0バイト目を使用する1/2ワード長アナログデータ */
				static final HalfWordType TYPE_ZERO = new HalfWordType(0);
				/** 1バイト目を使用する1/2ワード長アナログデータ */
				static final HalfWordType TYPE_ONE = new HalfWordType(1);

				/**
				 * コンストラクタ new演算子でインスタンスを生成できません。 staticフィールドを参照してください。
				 */
				HalfWordType(int halfWordType) {
					this.halfWordType = halfWordType;
				}

				/** バイトタイプを返します */
				int valueOf() {
					return halfWordType;
				}

				// サブクラスがObject.equalsをオーバーライドするのを防ぐ
				public final boolean equals(Object obj) {
					return super.equals(obj);
				}

				public final int hashCode() {
					return super.hashCode();
				}

				// シリアライズの為に必要な処理
				private static int nextOrdinal = 0;
				private final int ordinal = nextOrdinal++;
				private static final HalfWordType[] VALUES =
					{ TYPE_ZERO, TYPE_ONE };

				Object readResolve() throws ObjectStreamException {
					return VALUES[ordinal];
				}
			}
		}
	}

	/**
	 * コンストラクタ。
	 *
	 * @param value アナログ値
	 * @param valuType アナログ元データのサイズ
	 */
	private WifeDataAnalog(double value, AnalogType valueType) {
		valueType.checkArgument(value);
		this.value = value;
		this.valueType = valueType;
	}

	/**
	 * BCD 1/2ワード長で0 byte目を使用するWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfBcdZeroHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_ZERO_HALF_WORD);
	}

	/**
	 * BCD 1/2ワード長で1 byte目を使用するWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfBcdOneHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_ONE_HALF_WORD);
	}

	/**
	 * BCD 1ワード長で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfBcdSingle(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_SINGLE_WORD);
	}

	/**
	 * BCD 2ワード長で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfBcdDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_DOUBLE_WORD);
	}

	/**
	 * HEX 1/2ワード長で0 byte目を使用するWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfHexZeroHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_ZERO_HALF_WORD);
	}

	/**
	 * HEX 1/2ワード長で1 byte目を使用するWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfHexOneHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_ONE_HALF_WORD);
	}

	/**
	 * HEX 1ワード長で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfHexSingle(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_SINGLE_WORD);
	}

	/**
	 * HEX 2ワード長で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfHexDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_DOUBLE_WORD);
	}

	/**
	 * 符号ありHEX 1/2ワード長で0 byte目を使用するWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfShxZeroHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_ZERO_HALF_WORD);
	}

	/**
	 * 符号ありHEX 1/2ワード長で1 byte目を使用するWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfShxOneHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_ONE_HALF_WORD);
	}

	/**
	 * 符号ありHEX 1ワード長で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfShxSingle(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_SINGLE_WORD);
	}

	/**
	 * 符号ありHEX 2ワード長で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfShxDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_DOUBLE_WORD);
	}

	/**
	 * IEEE 754 浮動小数点「単精度」ビット配列で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfFloat(double value) {
		return new WifeDataAnalog(value, AnalogType.FLOAT_DOUBLE_WORD);
	}

	/**
	 * IEEE 754 浮動小数点「倍精度」ビット配列で構成されるWifeDataAnalogのインスタンスを返します。
	 *
	 * @param value WifeDataAnalogによって表される値
	 */
	public static WifeDataAnalog valueOfDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.DOUBLE_FOURTH_WORD);
	}

	/**
	 * バイト配列をアナログ値に変換しWifeDataAnalogを返します。 アナログ値以外のフィールドは以前の値を保持します。
	 *
	 * @param b バイト配列
	 */
	public WifeData valueOf(byte[] b) {
		return valueType.valueOf(b);
	}

	/**
	 * 引数のアナログ値に変換しWifeDataAnalogを返します。 アナログ値以外のフィールドは以前の値を保持します。
	 *
	 * @param value アナログ値
	 */
	public WifeData valueOf(double value) {
		return valueType.valueOf(value);
	}

	/**
	 * このオブジェクトと指定されたオブジェクトを比較します。アナログ値に関してはDoubleクラスのequalsメソッドの規則が
	 * あてはまります。また、このメソッドは<b>アナログデータの形式</b>
	 * も含めて比較していることに注意してください。アナログ値が同じでもアナログデータの形式が異なればfalseを返します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataAnalog)) {
			return false;
		}
		WifeDataAnalog wd = (WifeDataAnalog) obj;
		return Double.doubleToLongBits(wd.value) == Double
				.doubleToLongBits(value)
			&& wd.valueType == valueType;
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		int result = 17;
		long dl = Double.doubleToLongBits(value);
		result = 37 * result + (int) (dl ^ (dl >>> 32));
		result = 37 * result + valueType.hashCode();
		return result;
	}

	/**
	 * オブジェクトの文字列表現を返します。 {アナログデータ;アナログデータの形式}の書式で表示されます。
	 * 尚、この表示形式は将来変更される可能性があります。
	 */
	public String toString() {
		return "{" + Double.toString(value) + ";" + valueType + "}";
	}

	/**
	 * このデータの値をバイト配列変換して返します。
	 *
	 * @return バイト配列
	 */
	public byte[] toByteArray() {
		return valueType.toByteArray(value);
	}

	/**
	 * 指定された値を int 型として返します。値を丸めることもあります。
	 *
	 * @return このオブジェクトが表す数値を int 型に変換した値
	 */
	public int intValue() {
		return (int) value;
	}

	/**
	 * 指定された値を long 型として返します。値を丸めることもあります。
	 *
	 * @return このオブジェクトが表す数値を long 型に変換した値
	 */
	public long longValue() {
		return (long) value;
	}

	/**
	 * 指定された値を float 型として返します。値を丸めることもあります。
	 *
	 * @return このオブジェクトが表す数値を float 型に変換した値
	 */
	public float floatValue() {
		return (float) value;
	}

	/**
	 * この Double の double 値を返します。
	 *
	 * @return このオブジェクトが表す double 値
	 */
	public double doubleValue() {
		return value;
	}

	/**
	 * このアナログオブジェクトのワード長を返します。 但し、1/2 ワード長は1ワードとして返します。
	 *
	 * @return このアナログオブジェクトのワード長
	 */
	public int getWordSize() {
		return valueType.getWordSize();
	}
}
