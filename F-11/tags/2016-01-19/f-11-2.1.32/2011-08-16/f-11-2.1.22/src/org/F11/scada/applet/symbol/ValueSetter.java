/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2003 Freedom, Inc. All Rights Reserved.
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


/**
 * �f�W�^���y�уA�i���O�̒l��ݒ肷��C���^�[�t�F�C�X�ł��B
 * @author hori
 */
public interface ValueSetter {

	/**
	 * �l���z���_�ɐݒ肵�A�����݃��\�b�h���Ăяo���܂��B
	 * @param variableValue �ݒ肷��l�i�C���X�^���X�ɂ���Ă͖�������܂��j
	 */
	public void writeValue(Object variableValue);

	/**
	 * �f�[�^�v���o�C�_���{�f�[�^�z���_�[�����A���_�[�o�[�Ō�������������z���Ԃ��܂��B
	 * @return �f�[�^�v���o�C�_���{�f�[�^�z���_�[�����A���_�[�o�[�Ō�������������z��
	 */
	public String getDestination();
	
	/**
	 * �l�̐ݒ肪�Œ�l�Ȃ�true���A�C�ӂ̓��͒l�Ȃ�false��Ԃ��܂��B
	 * @return �l�̐ݒ肪�Œ�l�Ȃ�true���A�C�ӂ̓��͒l�Ȃ�false��Ԃ��܂��B
	 */
	public boolean isFixed();
}
