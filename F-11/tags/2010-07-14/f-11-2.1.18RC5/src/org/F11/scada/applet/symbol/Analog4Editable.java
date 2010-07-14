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
 *
 */
package org.F11.scada.applet.symbol;

import org.F11.scada.data.ConvertValue;

/**
 * �S�̃A�i���O�l��ҏW������V���{���̃C���^�[�t�F�C�X�ł��B
 * @author hori
 */
public interface Analog4Editable extends Editable {
	/**
	 * �V���{���̒l��Ԃ��܂�
	 */
	public String[] getValues();
	/**
	 * �V���{���ɒl��ݒ肵�܂�
	 */
	public void setValue(String[] values);
	/**
	 * ConvertValue��Ԃ��܂� 
	 */
	public ConvertValue getConvertValue();
	/**
	 * ���l�\���t�H�[�}�b�g�������Ԃ��܂�
	 */
	public String getFormatString();
	/**
	 * �l��ҏW����ׂ̃_�C�A���O����Ԃ��܂��B
	 */
	public String getSecondDialogName();
	/**
	 * �l��ҏW����ׂ̃_�C�A���O����ݒ肵�܂��B
	 */
	public void setSecondDialogName(String secondDialogName);
	/**
	 * �_�C�A���O�̃^�C�g����Ԃ��܂�
	 * @return �_�C�A���O�̃^�C�g����Ԃ��܂�
	 */
	String getDialogName();
}
