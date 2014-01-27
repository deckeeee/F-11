/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataCalendar.java,v 1.5.2.1 2005/07/06 02:20:44 frdm Exp $
 * $Revision: 1.5.2.1 $
 * $Date: 2005/07/06 02:20:44 $
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
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * �J�����_�[�̃f�[�^��\���܂��B
 */
public class WifeDataCalendar implements WifeData, java.io.Serializable {
	/** �V���A��ID */
	private static final long serialVersionUID = 3766236079110879046L;
	/** ���[�h�f�[�^��\�� BigInteger �C���X�^���X */
	private final BigInteger[][] bitSet;
	/** �ݒ肳��Ă��郂�[�h�� */
	private final int modeCount;
	/** �ő�r�b�g�� */
	private static final int MAX_BIT = 31;
	/** �ŏ��r�b�g�� */
	private static final int MIN_BIT = 0;
	/** �ꌎ���̃o�C�g�� */
	private static final int MONTH_DATA_BYTE = 4;
	/** �P�N���̌��̐� */
	private static final int MONTH_YEAR_COUNT = 12;
	/** ���̃C���X�^���X�̃n�b�V���R�[�h�ł� */
	private transient volatile int hashCode;

	/**
	 * �R���X�g���N�^
	 * private �A�N�Z�X�Ȃ̂ŁAnew ���Z�q�ŃC���X�^���X�����͂ł��܂���B
	 * ����� valueOf() ���\�b�h���g�p���āA�C���X�^���X�������Ă��������B
	 */
	private WifeDataCalendar(BigInteger[][] bitSet, int modeCount) {
		this.bitSet = bitSet;
		this.modeCount = modeCount;
	}

	/**
	 * 0 �ŏ��������� WifeDataCalendar �C���X�^���X���쐬����A�t�@�N�g�����\�b�h�ł��B
	 * @param modeCount ���̎�ސ����w�肵�܂��B�i�ʏ�x�� + ������j
	 * @return 0 �ŏ��������� WifeDataCalendar �C���X�^���X
	 */
	static public WifeDataCalendar valueOf(int modeCount) {
		byte[] b = creataZeroByteArray(MONTH_DATA_BYTE);
		BigInteger[][] bi = new BigInteger[modeCount][MONTH_YEAR_COUNT];
		for (int j = modeCount - 1; j >= 0; j--) {
			for (int i = MONTH_YEAR_COUNT - 1; i >= 0; i--) {
				bi[j][i] = new BigInteger(1, b);
			}
		}
		return new WifeDataCalendar(bi, modeCount);
	}

	/**
	 * �����������o�C�g�f�[�^�𐶐����܂��B
	 * @param size ��������o�C�g�z��̃T�C�Y
	 * @return 0 �ŏ��������ꂽ�o�C�g�z��
	 */
	private static byte[] creataZeroByteArray(int size) {
		byte[] b = new byte[size];
		Arrays.fill(b, (byte)0x00);
		return b;
	}

	/**
	 * �o�C�g�z����f�[�^�ɕϊ����܂��B
	 * @param b �o�C�g�z��
	 * @return �����ŏ��������ꂽ�AWordData �C���X�^���X(WifeData�Ƀ_�E���L���X�g)
	 */
	public WifeData valueOf(byte[] b) {
		if (b.length != MONTH_DATA_BYTE * MONTH_YEAR_COUNT * modeCount)
			throw new IllegalArgumentException("IllegalArgument length = " + b.length);

		BigInteger[][] bi = new BigInteger[modeCount][MONTH_YEAR_COUNT];
		ByteArrayInputStream is = new ByteArrayInputStream(b);
		byte[] b1 = new byte[MONTH_DATA_BYTE];

		for (int i = 0; i < modeCount; i++) {
			for (int j = 0; j < MONTH_YEAR_COUNT; j++) {
				is.read(b1, 0, MONTH_DATA_BYTE);
				bi[i][j] = new BigInteger(1, b1);
			}
		}

		return new WifeDataCalendar(bi, modeCount);
	}

	/**
	 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
	 * @return �o�C�g�z��
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream(MONTH_DATA_BYTE * MONTH_YEAR_COUNT * modeCount);

		for (int i = 0; i < modeCount; i++) {
			for (int j = 0; j < MONTH_YEAR_COUNT; j++) {
				byte[] b = bitSet[i][j].toByteArray();
				if (b.length <= MONTH_DATA_BYTE) {
					byte[] rb = creataZeroByteArray(MONTH_DATA_BYTE);
					System.arraycopy(b, 0, rb, rb.length - b.length, b.length);
					try {
						os.write(rb);
					} catch (java.io.IOException ex) {
						ex.printStackTrace();
					}
				} else {
					byte[] rb = creataZeroByteArray(MONTH_DATA_BYTE);
					System.arraycopy(b, b.length - rb.length, rb, 0, rb.length);
					try {
						os.write(rb);
					} catch (java.io.IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return os.toByteArray();
	}

	/**
	 * �w�肵���r�b�g��ݒ肵�� WifeDataCalendar ��Ԃ��܂��B
	 * @param mode �ݒ胂�[�h
	 * @param month ��
	 * @param bitNumber ��
	 * @return �r�b�g��ݒ肵���AWifeDataCalendar �C���X�^���X�B
	 */
	public WifeDataCalendar setBit(int mode, int month, int bitNumber) {
		if (isNormalArgument(mode, month, bitNumber)) {
			int n = reverse(bitNumber);
			for (int i = 0; i < bitSet.length; i++) {
				if (i == mode) {
					if (bitSet[i][month].testBit(n)) {
						bitSet[i][month] = bitSet[i][month].clearBit(n);
					} else {
						bitSet[i][month] = bitSet[i][month].setBit(n);
					}
				} else {
					bitSet[i][month] = bitSet[i][month].clearBit(n);
				}
			}
			return new WifeDataCalendar(bitSet, modeCount);
		} else {
			throw new java.lang.IllegalArgumentException("IllegalArgument mode:" +
														 mode +
														 " month:" +
														 month +
														 " bitNumber:" +
														 bitNumber
														 );
		}
	}

	/**
	 * �w�肵���r�b�g���N���A���� WifeDataCalendar ��Ԃ��܂��B
	 * @return �r�b�g���N���A�����AWifeDataCalendar �C���X�^���X�B
	public WifeDataCalendar clearBit(int mode, int month, int bitNumber) {
		if (isNormalArgument(mode, month, bitNumber)) {
			int n = reverse(bitNumber);
			bitSet[mode][month] = bitSet[mode][month].clearBit(n);
			return new WifeDataCalendar(bitSet, modeCount);
		} else {
			throw new java.lang.IllegalArgumentException("IllegalArgument mode:" +
														 mode +
														 " month:" +
														 month +
														 " bitNumber:" +
														 bitNumber
														 );
		}
	}
	 */

	/**
	 * �w�肵���r�b�g���e�X�g���܂��B
	 * @param mode �ݒ胂�[�h
	 * @param month ��
	 * @param bitNumber ��
	 * @return �w�肵���r�b�g�� On �Ȃ� true�AOff �Ȃ� false ��Ԃ��܂��B
	 */
	public boolean testBit(int mode, int month, int bitNumber) {
		if (isNormalArgument(mode, month, bitNumber)) {
			int n = reverse(bitNumber);
			return bitSet[mode][month].testBit(n);
		} else {
			throw new java.lang.IllegalArgumentException("IllegalArgument mode:" +
														 mode +
														 " month:" +
														 month +
														 " bitNumber:" +
														 bitNumber
														 );
		}
	}

	/**
	 * �r�b�g���Z�̈������������ǂ����𔻒肵�܂��B
	 * @param mode �ݒ胂�[�h
	 * @param month ��
	 * @param bitNumber ��
	 * @return �S�Ă̈���������Ȃ� true�A�Ȃ���� false ��Ԃ��܂��B
	 */
	private boolean isNormalArgument(int mode, int month, int bitNumber) {
		if (mode < 0 || mode >= modeCount)
			return false;
		if (month < 0 || month >= MONTH_YEAR_COUNT)
			return false;
		if (bitNumber < MIN_BIT || bitNumber > MAX_BIT)
			return false;

		return true;
	}

	/**
	 * �r�b�g�ʒu�𔽓]�����܂��B
	 */
	private int reverse(int n) {
		return n < 16 ? 16 + n : n - 16;
	}

	/**
	 * �I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g���r���܂��B
	 * �����r�b�g�\���̔�r�́ABigInteger �N���X�� equals(Object x) �Ɉˑ����Ă��܂��B
	 */
	public boolean equals(Object x) {
		if (x == this) {
			return true;
		}
		if (!(x instanceof WifeDataCalendar)) {
			return false;
		}

		WifeDataCalendar wd = (WifeDataCalendar)x;
		if (wd.modeCount != this.modeCount)
			return false;

		for (int i = 0; i < bitSet.length; i++) {
			if (!Arrays.equals(bitSet[i], wd.bitSet[i]))
				return false;
		}
		return true;
	}

	/**
	 * �n�b�V���R�[�h�l��Ԃ��܂��B
	 * �����r�b�g�\���̃n�b�V���R�[�h�l�́ABigInteger �N���X�� hashCode() �Ɉˑ����Ă��܂��B
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + modeCount;
			for (int i = 0; i < bitSet.length; i++) {
				for (int j = 0; j < bitSet[i].length; j++) {
					result = 37 * result + bitSet[i][j].hashCode();
				}
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * ������\����Ԃ��܂��B
	 * �����r�b�g�̕�����\���́ABigInteger �N���X�� toString(16) �Ɉˑ����Ă��܂��B
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("modeCount:" + modeCount);
		buffer.append(",bit:");
		for (int i = 0; i < modeCount; i++) {
			for (int j = 0; j < MONTH_YEAR_COUNT; j++) {
				buffer.append(bitSet[i][j].toString(16).toUpperCase());
			}
			if (i != (modeCount - 1)) {
			    buffer.append(" ");
			}
		}
		return buffer.toString();
	}

	/**
	 * ���̃f�[�^�̃��[�h����Ԃ��܂��B
	 * @return ���̃f�[�^�̃��[�h��
	 */
	public int getWordSize() {
		return (MONTH_DATA_BYTE * MONTH_YEAR_COUNT * modeCount) / 2;
	}
	
	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new  WifeDataCalendar(bitSet, modeCount);
	}

	/**
	 * ���̃J�����_�[�Ŏg�p���������̐���Ԃ��܂��B
	 * @return ���̃J�����_�[�Ŏg�p���������̐���Ԃ��܂��B
	 */	
	public int getSpecialDayCount() {
		return modeCount - 1;
	}
}
