/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/HolderData.java,v 1.7 2003/10/31 04:38:50 frdm Exp $
 * $Revision: 1.7 $
 * $Date: 2003/10/31 04:38:50 $
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * �f�[�^�z���_�̒l�𐶐�����ׂ̏���ێ�����N���X�ł��B
 * RMI�ŃV���A���C�Y����DataHolder�ŒʐM����̂͏d���ׂ��̃N���X�o�R�ŒʐM���܂��B
 * ���̃N���X�͕s�σN���X�ł��B
 * 
 * <p><b>���ӁF���̃N���X��hashCode()���\�b�h�Aequals(Object o)���\�b�h�́A�Ӑ}�I�ɓ����t�B�[���h
 * value���l�����Ă��܂���B</b>
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class HolderData implements Serializable {
	private static final long serialVersionUID = 2668107215141257936L;
	/** �f�[�^�z���_�[�� */
	private final String holder;
	/** �f�[�^�l�̃o�C�g�\�� */
	private final byte[] value;
	/** �ŏI�X�V���� */
	private final long time;
	/** �f�}���h�O���t�f�[�^ */
	private final Map demandData;
	
	/**
	 * �R���X�g���N�^
	 * @param holder �f�[�^�z���_�[��
	 * @param value �f�[�^�l��byte�z��
	 * @param time �ŏI�X�V����
	 * @param �f�}���h�O���t�f�[�^
	 */
	public HolderData(String holder, byte[] value, long time, Map demandData) {
		if (holder == null) {
			throw new IllegalArgumentException("holder need not null.");
		}
		if (value == null) {
			throw new IllegalArgumentException("value need not null.");
		}
		this.holder = holder;
		this.value = new byte[value.length];
		System.arraycopy(value, 0, this.value, 0, value.length);
		this.time = time;
		if (demandData != null) {
			this.demandData = new LinkedHashMap(demandData);
		} else {
			this.demandData = null;
		}
	}

	/**
	 * �f�[�^�z���_�[����Ԃ��܂��B
	 * @return �f�[�^�z���_�[��
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * �f�[�^�l��byte�z���Ԃ��܂��B
	 * @return �f�[�^�l��byte�z��
	 */
	public byte[] getValue() {
		byte[] b = new byte[value.length];
		System.arraycopy(value, 0, b, 0, value.length);
		return b;
	}

	/**
	 * �f�[�^�̍X�V������Ԃ��܂��B
	 * @return long �X�V����
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * �f�}���h�O���t�f�[�^��Ԃ��܂��B
	 * @return �f�}���h�O���t�f�[�^��Ԃ��܂��B
	 */
	public Map getDemandData() {
		return demandData != null
			? Collections.unmodifiableMap(demandData)
			: null;
	}

	/**
	 * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g���r���܂��B
	 * ���̃��\�b�h�͈Ӑ}�I�Ƀz���_���݂̂��r����悤��������Ă��܂��B
	 * @param obj ��r�Ώۂ̃I�u�W�F�N�g
	 * @return �z���_�������l�Ȃ� true ���قȂ�Ȃ� false ��Ԃ��܂�
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof HolderData)) {
			return false;
		}
		HolderData hd = (HolderData)obj;
		return hd.holder.equals(holder);
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 * ���̃��\�b�h�͈Ӑ}�I�Ƀz���_���݂̂���A�n�b�V����Ԃ��悤��������Ă��܂��B
	 * 
	 * @return �z���_���̃n�b�V����Ԃ��܂�
	 */
	public int hashCode() {
		return holder.hashCode();
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new HolderData(holder, value, time, demandData);
	}

}
