/*
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
import java.util.Arrays;

/**
 * ������f�[�^�N���X�B0x00�`0x19�̃o�C�g�l�́A�\�����鎞�ɏI�[�q�Ƃ��Ĉ����B 0x30, 0x31, 0x00, 0x00
 * �Ƃ����o�C�g�z��̏ꍇ�A"01"�Ƃ����������\������B
 * 
 * @author maekawa
 * 
 */
public final class StringData implements WifeData, Serializable {
	private static final long serialVersionUID = 6120279582664201908L;
	/** ������f�[�^�̃o�C�g�z�� */
	private final byte[] stringByte;
	/** ���̃C���X�^���X�̃n�b�V���R�[�h�ł� */
	private transient volatile int hashCode;

	/**
	 * �v���C�x�[�g�R���X�g���N�^�B���̃N���X�̃C���X�^���X�𐶐�����ɂ́A{@link #valueOf()}��{@link #valueOf(String)}���g�p���Ă��������B
	 * 
	 * @param stringByte
	 */
	private StringData(byte[] stringByte) {
		if (null == stringByte) {
			this.stringByte = new byte[0];
		} else {
			this.stringByte = new byte[stringByte.length];
			System.arraycopy(
					stringByte,
					0,
					this.stringByte,
					0,
					stringByte.length);
		}
	}

	public int getWordSize() {
		return stringByte.length / 2;
	}

	public byte[] toByteArray() {
		byte[] ba = new byte[stringByte.length];
		System.arraycopy(stringByte, 0, ba, 0, stringByte.length);
		return ba;
	}

	public WifeData valueOf(byte[] b) {
		return new StringData(b);
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof StringData)) {
			return false;
		}
		StringData sd = (StringData) obj;

		return Arrays.equals(stringByte, sd.stringByte);
	}

	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			for (int i = 0; i < stringByte.length; i++) {
				result = 37 * result + (int) stringByte[i];
			}
			hashCode = result;
		}
		return hashCode;
	}

	/**
	 * �o�C�g�z��ŕ\�������20�`7E�܂ł̕�����f�[�^��Ԃ��܂��B
	 */
	public String toString() {
		int i = 0;
		for (; i < stringByte.length && stringByte[i] >= (byte) 0x20
				&& stringByte[i] < (byte) 0x7F; i++) {
		}
		byte[] dispStr = new byte[i];
		System.arraycopy(stringByte, 0, dispStr, 0, dispStr.length);
		return new String(dispStr);
	}

	/**
	 * �����̕�����ŃC���X�^���X�𐶐����܂��Bnull����͂����ꍇ�ɂ͋󕶎���̃C���X�^���X�𐶐����܂��B
	 * 
	 * @param s ������
	 * @return �����̕�����ŃC���X�^���X�𐶐����܂��Bnull����͂����ꍇ�ɂ͋󕶎���̃C���X�^���X�𐶐����܂��B
	 */
	static StringData valueOf(String s) {
		if (null == s) {
			return new StringData(new byte[0]);
		} else {
			return new StringData(s.getBytes());
		}
	}

	/**
	 * �󕶎���̃C���X�^���X�𐶐����܂��B
	 * 
	 * @return �󕶎���̃C���X�^���X�𐶐����܂��B
	 */
	static StringData valueOf() {
		return new StringData(new byte[0]);
	}

	/**
	 * 0x00�ŏ���������size���[�h�̕�����l�𐶐����܂��B
	 * 
	 * @param size ������(���[�h)
	 * @return 0x00�ŏ���������size���[�h�̕�����l�𐶐����܂��B
	 */
	public static StringData valueOf(int size) {
		byte[] data = new byte[size * 2];
		Arrays.fill(data, (byte) 0x00);
		return new StringData(data);
	}

	/**
	 * �h��IreadResolve���\�b�h�B �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * 
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new StringData(stringByte);
	}
}
