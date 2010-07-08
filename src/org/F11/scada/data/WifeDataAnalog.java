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
 * �A�i���O�f�[�^�N���X�ł��B ���̃N���X�͕s�σN���X�ł��Anew���Z�q�ŃC���X�^���X�𐶐����邱�Ƃ��ł��܂���B
 * valueOf�`���\�b�h���g�p���ăC���X�^���X���쐬���Ă��������B
 */
public final class WifeDataAnalog extends Number implements WifeData,
		Serializable {
	private static final long serialVersionUID = -3944410544830984985L;

	/** �A�i���O�f�[�^�l */
	private final double value;
	/** �A�i���O�f�[�^�̌`�� */
	private final AnalogType valueType;
	/** double -> byte�̑O�Ŋۂߏ������s�����̗L�� */
	private static final boolean analogRoundMode =
		Boolean.valueOf(EnvironmentManager.get("/server/analogRoundMode",
				"false"));

	/**
	 * �A�i���O�f�[�^�̌`����\���^�C�v�Z�[�tenum�N���X�ł��B �estatic�t�B�[���h���A�i���O��byte�z��f�[�^�̌`����\���C���X�^���X�ł��B
	 * byte�z��������ɂƂ�WifeDataAnalog��Ԃ����ۃN���X���������Ă��܂��B new���Z�q�ŃC���X�^���X�𐶐����邱�Ƃ͂ł��܂���B
	 */
	abstract static class AnalogType implements Serializable {
		private static final long serialVersionUID = -347534979180523729L;

		/**
		 * �����𐮐��Ŏl�̌ܓ����܂��B
		 *
		 * @param bd ����
		 * @return �l�̌ܓ���������
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

		/** �A�i���O��byte�z��f�[�^�̌`����HEX�ŁA0�o�C�g�ڎg�p��1/2���[�h���A�i���O�f�[�^�ł� */
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
					} else if (b.length == 1) { // �l�� 0 �̎�;
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						return b1;
					} else { // �O��0x00�����Ă���ꍇ�i�l��128�ȏ�j
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 1, b1, 0, b1.length);
						return HEX_ZERO_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** �A�i���O��byte�z��f�[�^�̌`����HEX�ŁA1�o�C�g�ڎg�p��1/2���[�h���A�i���O�f�[�^�ł� */
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
					} else { // 127�ȉ��̏ꍇ�i�O��0x00�����Ă��Ȃ��j
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 0, b1, 1, b.length);
						return HEX_ONE_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** �A�i���O��byte�z��f�[�^�̌`����HEX�ŁA1���[�h���A�i���O�f�[�^�ł� */
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
		/** �A�i���O��byte�z��f�[�^�̌`����HEX�ŁA2���[�h���A�i���O�f�[�^�ł� */
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

		/** �A�i���O��byte�z��f�[�^�̌`������������HEX�ŁA0�o�C�g�ڎg�p��1/2���[�h���A�i���O�f�[�^�ł� */
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
					} else if (b.length == 1) { // �l�� 0 �̎�;
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						return b1;
					} else { // �O��0x00�����Ă���ꍇ�i�l��128�ȏ�j
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 1, b1, 0, b1.length);
						return HEX_ZERO_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** �A�i���O��byte�z��f�[�^�̌`������������HEX�ŁA1�o�C�g�ڎg�p��1/2���[�h���A�i���O�f�[�^�ł� */
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
					} else { // 127�ȉ��̏ꍇ�i�O��0x00�����Ă��Ȃ��j
						byte[] b1 = new byte[2];
						Arrays.fill(b1, (byte) 0x00);
						System.arraycopy(b, 0, b1, 1, b.length);
						return HEX_ONE_HALF_WORD.byteSize.toByteArray(b1);
					}
				}
			};
		/** �A�i���O��byte�z��f�[�^�̌`������������HEX�ŁA1���[�h���A�i���O�f�[�^�ł� */
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
		/** �A�i���O��byte�z��f�[�^�̌`������������HEX�ŁA2���[�h���A�i���O�f�[�^�ł� */
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

		/** �A�i���O��byte�z��f�[�^�̌`����BCD�ŁA0�o�C�g�ڎg�p��1/2���[�h���A�i���O�f�[�^�ł� */
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
		/** �A�i���O��byte�z��f�[�^�̌`����BCD�ŁA1�o�C�g�ڎg�p��1/2���[�h���A�i���O�f�[�^�ł� */
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
		/** �A�i���O��byte�z��f�[�^�̌`����BCD�ŁA1���[�h���A�i���O�f�[�^�ł� */
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
		/** �A�i���O��byte�z��f�[�^�̌`����BCD�ŁA2���[�h���A�i���O�f�[�^�ł� */
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
		 * �A�i���O��byte�z��f�[�^�̌`����float(IEEE 754 ���������_�u�P���x�v�r�b�g�z��ɏ]�����A���������_�\��)
		 * �̃A�i���O�f�[�^�ł��B
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
								"IEEE 754 ���������_�u�P���x�v�r�b�g�z�� -> float�ϊ��ɂ��ς������B");
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 ���������_�u�P���x�v�r�b�g�z�� -> float�ϊ��ɂ��ς������B");
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
								"IEEE 754 ���������_�u�P���x�v�r�b�g�z�� -> float�ϊ��ɂ��ς������B");
					} finally {
						if (dos != null) {
							try {
								dos.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 ���������_�u�P���x�v�r�b�g�z�� -> float�ϊ��ɂ��ς������B");
							}
						}
					}
				}

				/**
				 * �I�[�o�[���C�h���܂��B
				 */
				void checkArgument(double value) {
					Float src = new Float(value);
					if (src.isInfinite() || src.isNaN())
						throw new IllegalArgumentException(
								"Argument value is Infinite or NaN");
				}
			};
		/**
		 * �A�i���O��byte�z��f�[�^�̌`����double(IEEE 754 ���������_�́u�_�u���t�H�[�}�b�g (double format)�v
		 * �r�b�g���C�A�E�g)�̃A�i���O�f�[�^�ł��B
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
								"IEEE 754 ���������_�u�{���x�v�r�b�g�z�� -> double�ϊ��ɂ��ς������B");
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 ���������_�u�{���x�v�r�b�g�z�� -> double�ϊ��ɂ��ς������B");
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
								"IEEE 754 ���������_�u�{���x�v�r�b�g�z�� -> double�ϊ��ɂ��ς������B");
					} finally {
						if (dos != null) {
							try {
								dos.close();
							} catch (IOException e) {
								e.printStackTrace();
								throw new IllegalArgumentException(
										"IEEE 754 ���������_�u�{���x�v�r�b�g�z�� -> double�ϊ��ɂ��ς������B");
							}
						}
					}
				}

				/**
				 * �I�[�o�[���C�h���܂��B
				 */
				void checkArgument(double value) {
					Double src = new Double(value);
					if (src.isInfinite() || src.isNaN())
						throw new IllegalArgumentException(
								"Argument value is Infinite or NaN");
				}
			};

		/** �A�i���O��byte�z��f�[�^�̌`���� */
		private final String name;
		/** �A�i���O���f�[�^�̃T�C�Y */
		private final ByteSize byteSize;
		/** ���̃A�i���O�f�[�^�̍ŏ��l�ł� */
		private final double analogTypeMinValue;
		/** ���̃A�i���O�f�[�^�̍ő�l�ł� */
		private final double analogTypeMaxValue;

		/**
		 * �R���X�g���N�^ new���Z�q�ŃC���X�^���X�𐶐��ł��܂���B static�t�B�[���h���Q�Ƃ��Ă��������B
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
		 * �A�i���O�f�[�^�^�C�v�̕����\����Ԃ��܂��B
		 */
		public String toString() {
			return name;
		}

		/**
		 * ������ϊ������l������WifeDataAnalog��Ԃ��܂��B �ϊ��̎����͂��ꂼ��̃^�C�v�ɂ���ĈقȂ�܂��B
		 * �A�i���O�l�̒l�݂̂��ύX����A����ȊO�̑����͕ێ�����܂��B
		 */
		abstract WifeDataAnalog valueOf(byte[] b);

		/**
		 * ������ϊ������l������WifeDataAnalog��Ԃ��܂��B �ϊ��̎����͂��ꂼ��̃^�C�v�ɂ���ĈقȂ�܂��B
		 * �A�i���O�l�̒l�݂̂��ύX����A����ȊO�̑����͕ێ�����܂��B
		 */
		abstract WifeDataAnalog valueOf(double value);

		/**
		 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
		 *
		 * @return �o�C�g�z��
		 */
		abstract byte[] toByteArray(double value);

		/**
		 * �����̐��������`�F�b�N���܂��B
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

		/** ���[�h����Ԃ��܂��B */
		int getWordSize() {
			return byteSize.getWordSize();
		}

		// �T�u�N���X��Object.equals���I�[�o�[���C�h����̂�h��
		public final boolean equals(Object obj) {
			return super.equals(obj);
		}

		public final int hashCode() {
			return super.hashCode();
		}

		// �V���A���C�Y�ׂ̈ɕK�v�ȏ���
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
		 * �A�i���O���f�[�^�̃T�C�Y�̃^�C�v�Z�[�temun�N���X�ł��B �estatic�t�B�[���h���A�i���O���f�[�^�̃T�C�Y��\���C���X�^���X�ł��B
		 * byte�z��������ɂƂ�WifeDataAnalog��Ԃ����ۃN���X���������Ă��܂��B
		 * new���Z�q�ŃC���X�^���X�𐶐����邱�Ƃ͂ł��܂���B
		 */
		abstract static class ByteSize implements Serializable {
			private static final long serialVersionUID = 354931199558298354L;
			/** �A�i���O���f�[�^�̃T�C�Y�ł� */
			private final int byteSize;
			/**
			 * 1/2���[�h���̃A�i���O�f�[�^�̏ꍇ�̂ݎg�p���܂��B
			 * 1/2���[�h���ȊO�̃A�i���O�f�[�^�ɂ́AHalfWordType.TYPE_ZERO���ݒ肳��܂��B
			 */
			private final HalfWordType halfWordType;

			/** 1/2���[�h�̃A�i���O�f�[�^�ł�(0�o�C�g�ڂ��g�p) */
			static final ByteSize ZERO_HALF_WORD_ANALOG =
				new ByteSize(1, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-6385568572265636088L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != SINGLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"�s���Ȓ�����byte�z�񂪈����Ɏg�p����܂����Blength = "
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
			/** 1/2���[�h�̃A�i���O�f�[�^�ł�(1�o�C�g�ڂ��g�p) */
			static final ByteSize ONE_HALF_WORD_ANALOG =
				new ByteSize(1, HalfWordType.TYPE_ONE) {
					private static final long serialVersionUID =
						3790365881542551006L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != SINGLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"�s���Ȓ�����byte�z�񂪈����Ɏg�p����܂����Blength = "
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
			/** 1���[�h�̃A�i���O�f�[�^�ł� */
			static final ByteSize SINGLE_WORD_ANALOG =
				new ByteSize(2, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-6025093020033537813L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != SINGLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"�s���Ȓ�����byte�z�񂪈����Ɏg�p����܂����Blength = "
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
			/** 2���[�h�̃A�i���O�f�[�^�ł� */
			static final ByteSize DOUBLE_WORD_ANALOG =
				new ByteSize(4, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-4525230963105073071L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != DOUBLE_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"�s���Ȓ�����byte�z�񂪈����Ɏg�p����܂����Blength = "
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
			/** 4���[�h�̃A�i���O�f�[�^�ł�(BACnet�݂̂Ŏg�p�H�@�o�C�g������͖����H) */
			static final ByteSize FOURTH_WORD_ANALOG =
				new ByteSize(8, HalfWordType.TYPE_ZERO) {
					private static final long serialVersionUID =
						-3026858847517993664L;

					byte[] canonicalBytes(byte[] b) {
						if (b.length != FOURTH_WORD_ANALOG.byteSize) {
							throw new IllegalArgumentException(
									"�s���Ȓ�����byte�z�񂪈����Ɏg�p����܂����Blength = "
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
			 * �C��byte���ɓ���ւ���byte�z���Ԃ��܂��B
			 *
			 * @param byteArray ���ɂȂ�z��B
			 * @param size ����ւ���T�C�Y�B
			 */
			byte[] swapBytes(byte[] byteArray, int size) {
				byte[] result = new byte[byteArray.length];
				System.arraycopy(byteArray, size, result, 0, size);
				System.arraycopy(byteArray, 0, result, size, size);
				return result;
			}

			/**
			 * �R���X�g���N�^ new���Z�q�ŃC���X�^���X�𐶐��ł��܂���B static�t�B�[���h���Q�Ƃ��Ă��������B
			 */
			ByteSize(int byteSize, HalfWordType halfWordType) {
				this.byteSize = byteSize;
				this.halfWordType = halfWordType;
			}

			/** byte�^�C�v�ɐ��K�������o�C�g�z���Ԃ��܂��B */
			abstract byte[] canonicalBytes(byte[] b);

			/** ������ byte�z��� WORD �ɐ��K�����܂��B */
			abstract byte[] toByteArray(byte[] b);

			/** ���[�h����Ԃ��܂��B */
			int getWordSize() {
				return (byteSize == 1) ? 1 : byteSize / 2;
			}

			// �T�u�N���X��Object.equals���I�[�o�[���C�h����̂�h��
			public final boolean equals(Object obj) {
				return super.equals(obj);
			}

			public final int hashCode() {
				return super.hashCode();
			}

			// �V���A���C�Y�ׂ̈ɕK�v�ȏ���
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
			 * 1/2���[�h�f�[�^�̃o�C�g�^�C�v��\���^�C�v�Z�[�tenum�N���X�ł��B
			 * �estatic�t�B�[���h��1/2���[�h�f�[�^�̃o�C�g�^�C�v��\���C���X�^���X�ł��B
			 * new���Z�q�ŃC���X�^���X�𐶐����邱�Ƃ͂ł��܂���B
			 */
			static class HalfWordType implements Serializable {
				private static final long serialVersionUID =
					2089788803489359021L;
				/** �o�C�g�^�C�v */
				private final int halfWordType;

				/** 0�o�C�g�ڂ��g�p����1/2���[�h���A�i���O�f�[�^ */
				static final HalfWordType TYPE_ZERO = new HalfWordType(0);
				/** 1�o�C�g�ڂ��g�p����1/2���[�h���A�i���O�f�[�^ */
				static final HalfWordType TYPE_ONE = new HalfWordType(1);

				/**
				 * �R���X�g���N�^ new���Z�q�ŃC���X�^���X�𐶐��ł��܂���B static�t�B�[���h���Q�Ƃ��Ă��������B
				 */
				HalfWordType(int halfWordType) {
					this.halfWordType = halfWordType;
				}

				/** �o�C�g�^�C�v��Ԃ��܂� */
				int valueOf() {
					return halfWordType;
				}

				// �T�u�N���X��Object.equals���I�[�o�[���C�h����̂�h��
				public final boolean equals(Object obj) {
					return super.equals(obj);
				}

				public final int hashCode() {
					return super.hashCode();
				}

				// �V���A���C�Y�ׂ̈ɕK�v�ȏ���
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
	 * �R���X�g���N�^�B
	 *
	 * @param value �A�i���O�l
	 * @param valuType �A�i���O���f�[�^�̃T�C�Y
	 */
	private WifeDataAnalog(double value, AnalogType valueType) {
		valueType.checkArgument(value);
		this.value = value;
		this.valueType = valueType;
	}

	/**
	 * BCD 1/2���[�h����0 byte�ڂ��g�p����WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfBcdZeroHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_ZERO_HALF_WORD);
	}

	/**
	 * BCD 1/2���[�h����1 byte�ڂ��g�p����WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfBcdOneHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_ONE_HALF_WORD);
	}

	/**
	 * BCD 1���[�h���ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfBcdSingle(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_SINGLE_WORD);
	}

	/**
	 * BCD 2���[�h���ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfBcdDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.BCD_DOUBLE_WORD);
	}

	/**
	 * HEX 1/2���[�h����0 byte�ڂ��g�p����WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfHexZeroHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_ZERO_HALF_WORD);
	}

	/**
	 * HEX 1/2���[�h����1 byte�ڂ��g�p����WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfHexOneHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_ONE_HALF_WORD);
	}

	/**
	 * HEX 1���[�h���ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfHexSingle(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_SINGLE_WORD);
	}

	/**
	 * HEX 2���[�h���ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfHexDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.HEX_DOUBLE_WORD);
	}

	/**
	 * ��������HEX 1/2���[�h����0 byte�ڂ��g�p����WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfShxZeroHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_ZERO_HALF_WORD);
	}

	/**
	 * ��������HEX 1/2���[�h����1 byte�ڂ��g�p����WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfShxOneHalf(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_ONE_HALF_WORD);
	}

	/**
	 * ��������HEX 1���[�h���ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfShxSingle(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_SINGLE_WORD);
	}

	/**
	 * ��������HEX 2���[�h���ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfShxDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.SHX_DOUBLE_WORD);
	}

	/**
	 * IEEE 754 ���������_�u�P���x�v�r�b�g�z��ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfFloat(double value) {
		return new WifeDataAnalog(value, AnalogType.FLOAT_DOUBLE_WORD);
	}

	/**
	 * IEEE 754 ���������_�u�{���x�v�r�b�g�z��ō\�������WifeDataAnalog�̃C���X�^���X��Ԃ��܂��B
	 *
	 * @param value WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog valueOfDouble(double value) {
		return new WifeDataAnalog(value, AnalogType.DOUBLE_FOURTH_WORD);
	}

	/**
	 * �o�C�g�z����A�i���O�l�ɕϊ���WifeDataAnalog��Ԃ��܂��B �A�i���O�l�ȊO�̃t�B�[���h�͈ȑO�̒l��ێ����܂��B
	 *
	 * @param b �o�C�g�z��
	 */
	public WifeData valueOf(byte[] b) {
		return valueType.valueOf(b);
	}

	/**
	 * �����̃A�i���O�l�ɕϊ���WifeDataAnalog��Ԃ��܂��B �A�i���O�l�ȊO�̃t�B�[���h�͈ȑO�̒l��ێ����܂��B
	 *
	 * @param value �A�i���O�l
	 */
	public WifeData valueOf(double value) {
		return valueType.valueOf(value);
	}

	/**
	 * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g���r���܂��B�A�i���O�l�Ɋւ��Ă�Double�N���X��equals���\�b�h�̋K����
	 * ���Ă͂܂�܂��B�܂��A���̃��\�b�h��<b>�A�i���O�f�[�^�̌`��</b>
	 * ���܂߂Ĕ�r���Ă��邱�Ƃɒ��ӂ��Ă��������B�A�i���O�l�������ł��A�i���O�f�[�^�̌`�����قȂ��false��Ԃ��܂��B
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
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		int result = 17;
		long dl = Double.doubleToLongBits(value);
		result = 37 * result + (int) (dl ^ (dl >>> 32));
		result = 37 * result + valueType.hashCode();
		return result;
	}

	/**
	 * �I�u�W�F�N�g�̕�����\����Ԃ��܂��B {�A�i���O�f�[�^;�A�i���O�f�[�^�̌`��}�̏����ŕ\������܂��B
	 * ���A���̕\���`���͏����ύX�����\��������܂��B
	 */
	public String toString() {
		return "{" + Double.toString(value) + ";" + valueType + "}";
	}

	/**
	 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
	 *
	 * @return �o�C�g�z��
	 */
	public byte[] toByteArray() {
		return valueType.toByteArray(value);
	}

	/**
	 * �w�肳�ꂽ�l�� int �^�Ƃ��ĕԂ��܂��B�l���ۂ߂邱�Ƃ�����܂��B
	 *
	 * @return ���̃I�u�W�F�N�g���\�����l�� int �^�ɕϊ������l
	 */
	public int intValue() {
		return (int) value;
	}

	/**
	 * �w�肳�ꂽ�l�� long �^�Ƃ��ĕԂ��܂��B�l���ۂ߂邱�Ƃ�����܂��B
	 *
	 * @return ���̃I�u�W�F�N�g���\�����l�� long �^�ɕϊ������l
	 */
	public long longValue() {
		return (long) value;
	}

	/**
	 * �w�肳�ꂽ�l�� float �^�Ƃ��ĕԂ��܂��B�l���ۂ߂邱�Ƃ�����܂��B
	 *
	 * @return ���̃I�u�W�F�N�g���\�����l�� float �^�ɕϊ������l
	 */
	public float floatValue() {
		return (float) value;
	}

	/**
	 * ���� Double �� double �l��Ԃ��܂��B
	 *
	 * @return ���̃I�u�W�F�N�g���\�� double �l
	 */
	public double doubleValue() {
		return value;
	}

	/**
	 * ���̃A�i���O�I�u�W�F�N�g�̃��[�h����Ԃ��܂��B �A���A1/2 ���[�h����1���[�h�Ƃ��ĕԂ��܂��B
	 *
	 * @return ���̃A�i���O�I�u�W�F�N�g�̃��[�h��
	 */
	public int getWordSize() {
		return valueType.getWordSize();
	}
}
