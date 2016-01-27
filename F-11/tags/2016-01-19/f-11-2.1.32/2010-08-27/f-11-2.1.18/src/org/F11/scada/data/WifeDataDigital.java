/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeDataDigital.java,v 1.2.6.2 2005/04/18 09:48:59 frdm Exp $
 * $Revision: 1.2.6.2 $
 * $Date: 2005/04/18 09:48:59 $
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
import java.math.BigInteger;

/**
 * �f�W�^����`��\���N���X�ł��B
 * �l�𓾂�ꍇ��valueOf���\�b�h���g�p���Ă��������Bnew���Z�q���g�p�����C���X�^���X�쐬�͂ł��܂���B
 * @see #valueOf(boolean bit, int bitNo)
 */
public final class WifeDataDigital implements WifeData, Serializable {
	private static final long serialVersionUID = 3067843484717072714L;
	/**
	 * �f�W�^���r�b�g
	 */
	private final boolean bit;
	/**
	 * �r�b�g�ԍ�
	 */
	private final int bitNo;

	/** �r�b�g�ʒu�ɑ΂��郏�[�h�f�[�^�ibyte�z��j */
	private static final byte[][] bitNoToByte = {
		{(byte)0x00, (byte)0x01}, {(byte)0x00, (byte)0x02}, {(byte)0x00, (byte)0x04}, {(byte)0x00, (byte)0x08},
		{(byte)0x00, (byte)0x10}, {(byte)0x00, (byte)0x20}, {(byte)0x00, (byte)0x40}, {(byte)0x00, (byte)0x80},
		{(byte)0x01, (byte)0x00}, {(byte)0x02, (byte)0x00}, {(byte)0x04, (byte)0x00}, {(byte)0x08, (byte)0x00},
		{(byte)0x10, (byte)0x00}, {(byte)0x20, (byte)0x00}, {(byte)0x40, (byte)0x00}, {(byte)0x80, (byte)0x00},
	};

	/** �\�߃C���X�^���X���쐬���Ă����B�i�R�Q��ނ����K�v�Ȃ��ׁj */
	private static final int BIT_PER_WORD = 16;
	private static final WifeDataDigital[][] DIGITAL = new WifeDataDigital[2][BIT_PER_WORD];
	static {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < BIT_PER_WORD; j++) {
				if (i == 0) {
					DIGITAL[i][j] = new WifeDataDigital(false, j);
				} else {
					DIGITAL[i][j] = new WifeDataDigital(true, j);
				}
			}
		}

	}
	private static final byte[] DIGITAL_ALL_ZERO = {(byte)0x00, (byte)0x00};

	/**
	 * �R���X�g���N�^
	 * �f�W�^���r�b�g�N���X���쐬���܂��B
	 * @param bitNo ���̃f�W�^���r�b�g�̃��[�h�ɑ΂���r�b�g�ʒu���w�肵�܂��B
	 */
	private WifeDataDigital(boolean bit, int bitNo) {
		if (bitNo < 0 || bitNo > 15) {
			throw new IllegalArgumentException("Illegal WifeDataDigital : Digital bit " + bitNo);
		}
		this.bitNo = bitNo;
		this.bit = bit;
	}

	/**
	 * �f�W�^���r�b�g��ON�̏ꍇ�A�����̃r�b�g��݂̂��n�m�ɂ���byte�z���Ԃ��܂��B
	 * �f�W�^���r�b�g��OFF�̏ꍇ�A�l�� 0 ��byte�z���Ԃ��܂��B
	 */
	public byte[] toByteArray() {
		return bit ? bitNoToByte[bitNo] : DIGITAL_ALL_ZERO;
	}

	/**
	 * WifeDataDigital�̕�����\����Ԃ��܂��B
	 * �f�W�^���r�b�g��ON�ꍇ�A�r�b�gNo��10�i�\����Ԃ��܂��B
	 * �f�W�^���r�b�g��OFF�ꍇ�A"false"��Ԃ��܂��B
	 */
	public String toString() {
		return bit ? String.valueOf(bitNo) : "false";
	}

	/**
	 * ����WifeDataDigital��OnOff��ԂƁA�p�����[�^�̒l�Ƃ��r�������ʂ�Ԃ��܂��B
	 * @param onOff ��r�������l
	 * @return �p�����[�^�̒l�Ɠ�������� true ��Ԃ��܂��B
	 */
	public boolean isOnOff(boolean onOff) {
		if (bit == onOff) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �w�肵���l��������WifeDataDigital��Ԃ��܂��B
	 * @param bit true - on : false - off
	 * @param bitNo WORD���ɑ΂���bit�ԍ�
	 */
	public static WifeDataDigital valueOf(boolean bit, int bitNo) {
		return bit ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}

	/**
	 * �w�肵���r�b�g�ԍ���Off��WifeDataDigital��Ԃ��܂��B
	 */
	public static WifeDataDigital valueOfFalse(int bitNo) {
		return valueOf(false, bitNo);
	}

	/**
	 * �w�肵���r�b�g�ԍ���On��WifeDataDigital��Ԃ��܂��B
	 */
	public static WifeDataDigital valueOfTrue(int bitNo) {
		return valueOf(true, bitNo);
	}

	/**
	 * ���̃f�W�^���f�[�^�̈����̃C���X�^���X��Ԃ��܂��B
	 * @param b true or false
	 * @return ���̃f�W�^���f�[�^�̈����̃C���X�^���X��Ԃ��܂��B
	 */
	public WifeDataDigital valueOf(boolean b) {
	    return b ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}

	/**
	 * �w�肵���l��������WifeDataDigital��Ԃ��܂��B
	 * @param b �o�C�g�z��
	 */
	public WifeData valueOf(byte[] b) {
		BigInteger bi = new BigInteger(b);
		return bi.testBit(bitNo) ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}

	/**
	 * ���̃f�[�^�̃��[�h����Ԃ��܂��B
	 * @return ���̃f�[�^�̃��[�h��
	 */
	public int getWordSize() {
		return 1;
	}

	/**
	 * �f�V���A���C�Y�̍ې��������C���X�^���X��j�����ăI���W�i����Ԃ��B
	 */
	private Object readResolve() throws ObjectStreamException {
		return bit ? DIGITAL[1][bitNo] : DIGITAL[0][bitNo];
	}
}
