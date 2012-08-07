/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/CreateHolderData.java,v 1.4.6.1 2004/11/29 07:12:47 frdm Exp $
 * $Revision: 1.4.6.1 $
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.gr.javacons.jim.QualityFlag;

/**
 * �f�[�^�z���_�[�𐶐�����ׂɕK�v�ȏ���ێ�����N���X�B
 * WifeDataProviderProxy�I�u�W�F�N�g�������ɁA�T�[�o�[����RMI�o�R�œ]����
 * �T�[�o�[�ɕێ�����Ă���DataHolder���Č����܂��B
 * ���̃N���X�͕s�σN���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class CreateHolderData implements Serializable {
	private static final long serialVersionUID = 4735383098026717379L;
	private final String holderName;
	private final ConvertValue convertValue;
	private final Map demandData;
	private final WifeData wifeData;
	private final Date date;
	private final QualityFlag qualityFlag;
	private final String provider;

	/**
	 * �R���X�g���N�^
	 * @param holder �f�[�^�z���_�[��
	 * @param value �f�[�^�l�� byte�z��
	 * @param convertValue �l�ϊ��I�u�W�F�N�g
	 * @param demandData �f�}���h�f�[�^
	 * @param wifeData �l�I�u�W�F�N�g
	 * @param date �f�[�^�X�V����
	 * @param qualityFlag �N�H���e�B�t���O
	 */
	public CreateHolderData(
			String holderName,
			WifeData wifeData,
			ConvertValue convertValue,
			Map demandData,
			Date date,
			QualityFlag qualityFlag,
			String provider) {

	    if (holderName == null) {
			throw new IllegalArgumentException("Argument `holderName' need not null.");
		}
		if (wifeData == null) {
			throw new IllegalArgumentException("Argument `wifeData' need not null.");
		}
	    if (provider == null) {
			throw new IllegalArgumentException("Argument `provider' need not null.");
		}
		this.holderName = holderName;
		this.wifeData = wifeData.valueOf(wifeData.toByteArray());
		this.convertValue = convertValue;
		if (demandData != null) {
			this.demandData = new HashMap(demandData);
		} else {
			this.demandData = null;
		}
		if (date == null) {
			throw new IllegalArgumentException("date is null.");
		}
		if (qualityFlag == null) {
			throw new IllegalArgumentException("qualityFlag is null.");
		}
		this.date = new Date(date.getTime());
		this.qualityFlag = qualityFlag;
		this.provider = provider;
	}

	/**
	 * �f�[�^�z���_�[����Ԃ��܂��B
	 * @return �f�[�^�z���_�[�� 
	 */
	public String getHolder() {
		return holderName;
	}
	
	/**
	 * �l�ϊ��I�u�W�F�N�g��Ԃ��܂��B
	 * @return �l�ϊ��I�u�W�F�N�g
	 */
	public ConvertValue getConvertValue() {
		return convertValue;
	}

	/**
	 * �f�}���h�f�[�^��Ԃ��܂��B
	 * @return �f�}���h�f�[�^
	 */
	public Map getDemandData() {
		if (demandData != null){
			return Collections.unmodifiableMap(demandData);
		} else {
			return Collections.unmodifiableMap(new HashMap());
		}
	}

	/**
	 * �f�[�^�l��Ԃ��܂��B
	 * @return �f�[�^�l
	 */
	public WifeData getWifeData() {
		return wifeData.valueOf(wifeData.toByteArray());
	}
	
	/**
	 * ���̃I�u�W�F�N�g�̕�����`����Ԃ��܂��B
	 * �Ԃ���镶����́A�����I�ɕύX�����\��������܂��B�J�����̃f�o�b�O�ȊO��
	 * �g�p���鎖�͐�������܂���B
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("provider=").append(provider)
		.append(", holderName=").append(holderName)
		.append(", convertValue=").append(convertValue)
		.append(", demandData=").append(demandData)
		.append(", wifeData=").append(wifeData)
		.append(", date=").append(date)
		.append(", qualityFlag=").append(qualityFlag);

		return b.toString();
	}
	
	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new CreateHolderData(
				holderName,
				wifeData,
				convertValue,
				demandData,
				date,
				qualityFlag,
				provider);
	}

	/**
	 * �f�[�^�X�V������Ԃ��܂��B
	 * @return �f�[�^�X�V����
	 */
	public Date getDate() {
		return new Date(date.getTime());
	}

	/**
	 * �N�H���e�B�t���O��Ԃ��܂��B
	 * @return �N�H���e�B�t���O
	 */
	public QualityFlag getQualityFlag() {
		return qualityFlag;
	}

	public String getProvider() {
	    return provider;
	}
}
