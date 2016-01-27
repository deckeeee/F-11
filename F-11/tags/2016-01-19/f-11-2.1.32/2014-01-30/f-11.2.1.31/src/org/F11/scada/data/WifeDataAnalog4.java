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
package org.F11.scada.data;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * �A�i���O�f�[�^���Sword�A�˂��f�[�^�N���X�ł��B
 * ���̃N���X�͕s�σN���X�ł��Anew���Z�q�ŃC���X�^���X�𐶐����邱�Ƃ��ł��܂���B
 * valueOf�`���\�b�h���g�p���ăC���X�^���X���쐬���Ă��������B
 * @author hori
 */
public final class WifeDataAnalog4 implements WifeData, Serializable {
	static final long serialVersionUID = 6642498079225768096L;
	
	private transient static final int ANALOG4_SIZE = 4;
	/** �A�i���O�f�[�^�l */
	private final WifeDataAnalog[] value;

	/**
	 * �R���X�g���N�^
	 */
	private WifeDataAnalog4(WifeDataAnalog[] value) {
		if (value.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		this.value = value;
		/*
		for (int i = 0; i < ANALOG4_SIZE; i++) {
			this.value[i] = (WifeDataAnalog)value[i].valueOf(value[i].doubleValue());
		}
		*/
	}

	/**
	 * BCD 4���[�h���ō\�������WifeDataAnalog4�̃C���X�^���X��Ԃ��܂��B
	 * @param values WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog4 valueOfBcdSingle(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[ANALOG4_SIZE];
		for (int i = 0; i < ana.length; i++) {
			ana[i] = WifeDataAnalog.valueOfBcdSingle(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * HEX 4���[�h���ō\�������WifeDataAnalog4�̃C���X�^���X��Ԃ��܂��B
	 * @param values WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog4 valueOfHexSingle(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[ANALOG4_SIZE];
		for (int i = 0; i < ana.length; i++) {
			ana[i] = WifeDataAnalog.valueOfHexSingle(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * SHX 4���[�h���ō\�������WifeDataAnalog4�̃C���X�^���X��Ԃ��܂��B
	 * @param values WifeDataAnalog�ɂ���ĕ\�����l
	 */
	public static WifeDataAnalog4 valueOfShxSingle(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[ANALOG4_SIZE];
		for (int i = 0; i < ana.length; i++) {
			ana[i] = WifeDataAnalog.valueOfShxSingle(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * �o�C�g�z����A�i���O�l�ɕϊ���WifeDataAnalog4��Ԃ��܂��B
	 * �A�i���O�l�ȊO�̃t�B�[���h�͈ȑO�̒l��ێ����܂��B
	 * @param b �o�C�g�z��
	 */
	public WifeData valueOf(byte[] b) {
		WifeDataAnalog[] ana = new WifeDataAnalog[value.length];
		int pos = 0;
		for (int i = 0; i < value.length; i++) {
			byte[] tb = value[i].toByteArray();
			System.arraycopy(b, pos, tb, 0, tb.length);
			ana[i] = (WifeDataAnalog) value[i].valueOf(tb);
			pos += tb.length;
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * �����̃A�i���O�l��ϊ���WifeDataAnalog4��Ԃ��܂��B
	 * �A�i���O�l�ȊO�̃t�B�[���h�͈ȑO�̒l��ێ����܂��B
	 * @param values �A�i���O�l�̔z��
	 */
	public WifeData valueOf(double[] values) {
		if (values.length != ANALOG4_SIZE)
			throw new IllegalArgumentException();
		WifeDataAnalog[] ana = new WifeDataAnalog[this.value.length];
		for (int i = 0; i < this.value.length; i++) {
			ana[i] = (WifeDataAnalog) this.value[i].valueOf(values[i]);
		}
		return new WifeDataAnalog4(ana);
	}

	/**
	 * ���̃I�u�W�F�N�g�̒l�� double�̔z��^�Ƃ��ĕԂ��܂��B
	 * @return ���̃I�u�W�F�N�g���\�����l�� double�z��
	 */
	public double[] doubleValues() {
		double[] values = new double[value.length];
		for (int i = 0; i < value.length; i++) {
			values[i] = value[i].doubleValue();
		}
		return values;
	}

	/**
	 * ���̃I�u�W�F�N�g�̎w��C���f�b�N�X�l�� double�^�Ƃ��ĕԂ��܂��B
	 * @return ���̃I�u�W�F�N�g���\�����l�� double�z��
	 */
	public double doubleValue(int index) {
		return value[index].doubleValue();
	}

	/**
	 * ���̃I�u�W�F�N�g�̃��[�h����Ԃ��܂��B
	 * @return ���̃A�i���O�I�u�W�F�N�g�̃��[�h��
	 */
	public int getWordSize() {
		int ws = 0;
		for (int i = 0; i < value.length; i++) {
			ws += value[i].getWordSize();
		}
		return ws;
	}

	/**
	 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
	 * @return �o�C�g�z��
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream ost = new ByteArrayOutputStream();
		for (int i = 0; i < value.length; i++) {
			byte[] ba = value[i].toByteArray();
			ost.write(ba, 0, ba.length);
		}
		return ost.toByteArray();
	}

	/**
	 * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g���r���܂��B�X�̃A�i���O�l�Ɋւ��Ă�WifeDataAnalog�N���X��
	 * equals���\�b�h�̋K�������Ă͂܂�܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataAnalog4)) {
			return false;
		}
		WifeDataAnalog4 wd = (WifeDataAnalog4) obj;
		for (int i = 0; i < value.length; i++) {
			if (!wd.value[i].equals(value[i]))
				return false;
		}
		return true;
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		int result = 17;
		for (int i = 0; i < value.length; i++) {
			result += value[i].hashCode();
		}
		return result;
	}

	/**
	 * �I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 * {�A�i���O�f�[�^;�A�i���O�f�[�^�̌`��}�̏����ŕ\������܂��B
	 * ���A���̕\���`���͏����ύX�����\��������܂��B
	 */
	public String toString() {
		StringBuffer st = new StringBuffer();
		st.append("WifeDataAnalog4:");
		for (int i = 0; i < value.length; i++) {
			st.append(value[i].toString());
		}
		return st.toString();
	}

}
