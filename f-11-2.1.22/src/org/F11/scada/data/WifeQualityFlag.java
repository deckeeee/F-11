/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeQualityFlag.java,v 1.3.6.1 2004/11/29 07:12:47 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2004/11/29 07:12:47 $
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

import jp.gr.javacons.jim.QualityFlag;

/**
 * WifeData �� QualityFlag �N���X�ł��B
 * �^�C�v�Z�[�t enum �N���X�E�p�^�[�����g�p���āAQualityFlag�C���^�[�t�F�C�X��
 * ���b�v���Ă��܂��B
 * ���̃N���X�͕s�σN���X�ł��B
 */
public final class WifeQualityFlag implements QualityFlag, Serializable {
	/** �V���A���o�[�W����UID�ł��B */
	private static final long serialVersionUID = 7268587145123423407L;
	/** �N�H���e�B�Ɋւ���g�b�v���x���̏��Ƃ��āC�ُ�l�ł��邱�Ƃ�\���B */
	public static final WifeQualityFlag BAD = new WifeQualityFlag(QualityFlag.BAD);
	/** �N�H���e�B�Ɋւ���g�b�v���x���̏��Ƃ��āC���킩�ُ킩�𔻒�ł��Ȃ����Ƃ�\���B */
	public static final WifeQualityFlag UNCERTAIN = new WifeQualityFlag(QualityFlag.UNCERTAIN);
	/** �N�H���e�B�Ɋւ���g�b�v���x���̏��Ƃ��āC����l�ł��邱�Ƃ�\���B */
	public static final WifeQualityFlag GOOD = new WifeQualityFlag(QualityFlag.GOOD);
	/** �N�H���e�B�Ɋւ���g�b�v���x���̏��Ƃ��āC�f�[�^�I�u�W�F�N�g�l�ɏ����l���ݒ肳��Ă��邱�Ƃ�\���B */
	public static final WifeQualityFlag INITIAL = new WifeQualityFlag(2);
	//�V���A���C�Y�̂��߂ɕK�v�ȏ���
	private static int nextOrdinal = 0;
	private final int ordinal = nextOrdinal++;
	private static final WifeQualityFlag[] VALUES = {
		BAD, UNCERTAIN, GOOD, INITIAL,
	};

	/** �N�H���e�B��\���l�ł��B */
	private final transient int quality;

	/** �N�H���e�B�̕����\�� */
	private static final String[] qualityString = {
	        "BAD", "UNCERTAIN", "INITIAL", "GOOD",
	};

	/**
	 * �R���X�g���N�^�ł��B
	 * �N�H���e�B��\���萔���A�����Ɏ����܂��Bprivate �R���X�g���N�^�Ȃ̂ŁA
	 * �p���A�� new ���Z�q�ŃC���X�^���X�𐶐����邱�Ƃ��ł��܂���B
	 * @param quality �N�H���e�B��\���萔
	 */
	private WifeQualityFlag(int quality) {
		this.quality = quality;
	}

	/**
	 * QualityFlag �C���^�[�t�F�C�X�̎����ł��B
	 * @return �N�H���e�B��\���萔
	 */
	public int getQuality() {
		return quality;
	}

	/**
	 * �f�V���A���C�Y�ɂ��C���X�^���X�̐�����h���܂��B
	 */
	private Object readResolve() throws ObjectStreamException {
		return VALUES[ordinal];
	}

	/**
	 * QualityFlag �C���^�[�t�F�C�X�̎����ł��B���~�b�g�͎�������Ă��܂���B
	 */
	public int getLimit() {
		return QualityFlag.LIMIT_NOT_LIMITED;
	}

	/**
	 * QualityFlag �C���^�[�t�F�C�X�̎����ł��B�X�e�[�^�X�͎�������Ă��܂���B
	 */
	public int getSubStatus() {
		return QualityFlag.SUBSTATUS_NON_SPECIFIC;
	}
	
	/* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return qualityString[quality];
    }
}
