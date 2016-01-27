/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/OnOffTime.java,v 1.2.6.2 2005/08/11 07:46:32 frdm Exp $
 * $Revision: 1.2.6.2 $
 * $Date: 2005/08/11 07:46:32 $
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

/**
 * �^�C���e�[�u����On / Off �̃f�[�^�̃Z�b�g�ł��B
 * �X�P�W���[���N���X�̓����Ŏg�p���܂��B
 * ���̃N���X�͕s�σN���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
final class OnOffTime implements Serializable {
	private static final long serialVersionUID = 734696440871603204L;
	/** On �̎��Ԃ�\���A�i���O�f�[�^�ł��B */
	private final WifeDataAnalog onTime;
	/** Off �̎��Ԃ�\���A�i���O�f�[�^�ł��B */
	private final WifeDataAnalog offTime;

	/**
	 * �R���X�g���N�^
	 * @param onTime On �̎���
	 * @param offTime Off �̎���
	 */
	OnOffTime(int onTime, int offTime) {
		this.onTime = WifeDataAnalog.valueOfBcdSingle(onTime);
		this.offTime = WifeDataAnalog.valueOfBcdSingle(offTime);
	}

	/**
	 * On �̎��Ԃ�Ԃ��܂��B
	 */
	int getOnTime() {
		return onTime.intValue();
	}

	/**
	 * Off �̎��Ԃ�Ԃ��܂��B
	 */
	int getOffTime() {
		return offTime.intValue();
	}

	/**
	 * On �̎��Ԃ𔽉f�����AOnOffTime�I�u�W�F�N�g��Ԃ��܂��B
	 */
	OnOffTime setOnTime(int onTime) {
		return new OnOffTime(onTime, this.offTime.intValue());
	}

	/**
	 * Off �̎��Ԃ𔽉f�����AOnOffTime�I�u�W�F�N�g��Ԃ��܂��B
	 */
	OnOffTime setOffTime(int offTime) {
		return new OnOffTime(this.onTime.intValue(), offTime);
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 */
	public String toString() {
		java.text.DecimalFormat format = new java.text.DecimalFormat("0000");
		return format.format(onTime.intValue())
			+ ":"
			+ format.format(offTime.intValue());
	}

	/**
	 * ���̃I�u�W�F�N�g�Ƒ��̃I�u�W�F�N�g�����������ǂ����������܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof OnOffTime)) {
			return false;
		}
		OnOffTime time = (OnOffTime) obj;
		return this.onTime.equals(time.onTime)
			&& this.offTime.equals(time.offTime);
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + onTime.hashCode();
		result = 37 * result + offTime.hashCode();
		return result;
	}

	/**
	 * �o�C�g�z����f�[�^�ɕϊ����܂��B
	 * <b>�A���A BCD �͈͊O�̃o�C�g�z�񂪁A�����Ŏw�肳�ꂽ�ꍇ�͖������� 0 �Ƃ��Ĉ����܂��B</b>
	 * @param b �o�C�g�z��
	 */
	OnOffTime valueOf(byte[] b) {
		if (b == null) {
			throw new IllegalArgumentException("Argument need not null.");
		}

		if (b.length != 4) {
			throw new IllegalArgumentException("Illegal byte[] length:" + b.length);
		}
		
		byte[] onByteData = { b[0], b[1] };
		byte[] offByteData = { b[2], b[3] };

		WifeDataAnalog onTime = null;
		WifeDataAnalog offTime = null;
		try {
			onTime = (WifeDataAnalog) this.onTime.valueOf(onByteData);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			byte[] zero = new byte[] {(byte) 0x00, (byte) 0x00 };
			onTime = (WifeDataAnalog) this.onTime.valueOf(zero);
		}

		try {
			offTime = (WifeDataAnalog) this.onTime.valueOf(offByteData);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			byte[] zero = new byte[] {(byte) 0x00, (byte) 0x00 };
			offTime = (WifeDataAnalog) this.offTime.valueOf(zero);
		}

		return new OnOffTime(onTime.intValue(), offTime.intValue());
	}

	/**
	 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
	 * @return �o�C�g�z��
	 */
	byte[] toByteArray() {
		byte[] onTimeByte = onTime.toByteArray();
		byte[] offTimeByte = offTime.toByteArray();
		byte[] returnByte = new byte[onTimeByte.length + offTimeByte.length];
		System.arraycopy(onTimeByte, 0, returnByte, 0, onTimeByte.length);
		System.arraycopy(
			offTimeByte,
			0,
			returnByte,
			onTimeByte.length,
			offTimeByte.length);
		return returnByte;
	}

	/**
	 * ���̃f�[�^�̃��[�h����Ԃ��܂��B
	 * @return ���̃f�[�^�̃��[�h��
	 */
	int getWordSize() {
		return onTime.getWordSize() + offTime.getWordSize();
	}

	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	Object readResolve() throws ObjectStreamException {
		return new OnOffTime(onTime.intValue(), offTime.intValue());
	}
}
