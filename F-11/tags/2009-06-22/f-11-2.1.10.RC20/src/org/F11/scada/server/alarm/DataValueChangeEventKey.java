/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/DataValueChangeEventKey.java,v 1.3.6.1 2006/03/07 09:33:56 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2006/03/07 09:33:56 $
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
package org.F11.scada.server.alarm;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataValueChangeEvent;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * DataValueChangeEvent ����e�l���ȒP�ɃA�N�Z�X����ׂ̕⏕�N���X�ł��B
 * ���̃N���X�̓V���A���C�Y���邱�Ƃ��\�ł��B�f�W�^���f�[�^���܂ރf�[�^�ύX�C�x���g�́A
 * ���̃N���X����ĒʐM���܂��B
 * 
 * ���̃N���X�͕s�σN���X�ł��B�N���X�̋@�\���܂߂������́A�p���ł͂Ȃ��Ϗ����f�����g�p���ĉ������B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class DataValueChangeEventKey implements Serializable {
	private static final long serialVersionUID = 299536944241600068L;
	/**
	 * �|�C���g
	 * @serial ���̃f�[�^��������|�C���g
	 */ 
	private final int point;
	/**
	 * �f�[�^�v���o�C�_��
	 * @serial �f�[�^�v���o�C�_��
	 */
	private final String provider;
	/**
	 * �f�[�^�z���_�[��
	 * @serial �f�[�^�z���_�[��
	 */
	private final String holder;
	/**
	 * �f�[�^�l
	 * @serial �f�[�^�l
	 */
	private final Boolean value;
	/**
	 * �f�[�^�ύX����
	 * @serial �f�[�^�ύX����
	 */
	private final Timestamp date;

	/**
	 * WifeDataDigital�̃f�[�^�l�ύX�C�x���g���e�l�ɕϊ����܂��B
	 * @param evt WifeDataDigital�̃f�[�^�l�ύX�C�x���g
	 */
	public DataValueChangeEventKey(DataValueChangeEvent evt) {
		this(evt, true);
	}

	public DataValueChangeEventKey(DataValueChangeEvent evt, boolean digtalCheck) {
		DataHolder dh = (DataHolder) evt.getSource();
		Date date = evt.getTimeStamp();
		point =
			((Integer) dh.getParameter(WifeDataProvider.PARA_NAME_POINT))
				.intValue();
		provider = dh.getDataProvider().getDataProviderName();
		holder = dh.getDataHolderName();
		Object obj = evt.getValue();
		if (digtalCheck) {
			if (obj instanceof WifeDataDigital) {
				if (((WifeDataDigital) obj).toString().equals("false")) {
					value = Boolean.FALSE;
				} else {
					value = Boolean.TRUE;
				}
			} else {
				throw new IllegalArgumentException("DataHolder is not WifeDataDigital.");
			}
		} else {
			value = Boolean.FALSE;
		}
		this.date = new Timestamp(date.getTime());
	}

	public DataValueChangeEventKey(
			int point,
			String provider,
			String holder,
			Boolean value,
			Timestamp date) {
		this.point = point;
		this.provider = provider;
		this.holder = holder;
		this.value = value;
		this.date = new Timestamp(date.getTime());
	}

	/**
	 * �|�C���g��Ԃ��܂��B
	 * @return int
	 */
	public int getPoint() {
		return point;
	}
	
	/**
	 * �f�[�^�v���o�C�_����Ԃ��܂��B
	 * @return String
	 */
	public String getProvider() {
		return provider;
	}
	
	/**
	 * �f�[�^�z���_�[����Ԃ��܂��B
	 * @return String
	 */
	public String getHolder() {
		return holder;
	}
	
	/**
	 * �f�[�^�l��Ԃ��܂��B
	 * @return Boolean
	 */
	public Boolean getValue() {
		return value;
	}
	
	/**
	 * �f�[�^�ύX������Ԃ��܂��B
	 * @return Timestamp
	 */
	public Timestamp getTimeStamp() {
		return new Timestamp(date.getTime());
	}
	
	/**
	 * �f�[�^�ύX������ݒ肵�� DataValueChangeEventKey �I�u�W�F�N�g��Ԃ��܂��B
	 * @param t �ݒ莞��
	 * @return DataValueChangeEventKey
	 */
	public DataValueChangeEventKey setTimeStamp(Timestamp t) {
		if (t == null) {
			throw new IllegalArgumentException("Set Time is null.");
		}
		
		return new DataValueChangeEventKey(
			point, provider, holder, value, new Timestamp(t.getTime()));
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("point=").append(point);
		buffer.append(",provider=").append(provider);
		buffer.append(",holder=").append(holder);
		buffer.append(",value=").append(value);
		buffer.append(",date=").append(date);
		return buffer.toString();
	}
	
	/**
	 * ���̃I�u�W�F�N�g�̒l���ׁA�����Ȃ�� true ��Ԃ��܂��B
	 * @param obj ��r�Ώۂ̃I�u�W�F�N�g
	 * @return ���̃I�u�W�F�N�g�̒l���ׁA�����Ȃ�� true ��Ԃ��܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {	
			return true;
		}
		
		if (!(obj instanceof DataValueChangeEventKey)) {
			return false;
		}
		
		DataValueChangeEventKey dv = (DataValueChangeEventKey) obj;
		return point == dv.point
				&& provider.equals(dv.provider)
				&& holder.equals(dv.holder)
				&& value.equals(dv.value)
				&& date.equals(dv.date);
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V����Ԃ��܂�
	 * @return ���̃I�u�W�F�N�g�̃n�b�V��
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + point;
		result = 37 * result + provider.hashCode();
		result = 37 * result + holder.hashCode();
		result = 37 * result + value.hashCode();
		result = 37 * result + date.hashCode();
		return result;
	}

	
	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new DataValueChangeEventKey(point, provider, holder, value, date);
	}
}
