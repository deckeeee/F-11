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

package org.F11.scada.server.command;

import org.F11.scada.server.alarm.DataValueChangeEventKey;

/**
 * �R�}���h���s�N���X�̃C���^�[�t�F�C�X�ł��B
 * 
 * ���̃C���^�[�t�F�C�X����������N���X�́A���������̃R���X�g���N�^��
 * ���K�v������܂��BCommandProvider�ł͂܂����������̃R���X�g���N�^�ŁA
 * �I�u�W�F�N�g��������������v���p�e�B�[��ݒ肵�܂��B
 * 
 * �v���p�e�B�[�̐ݒ��BeanUtil#populate���\�b�h�ɈϏ����܂��BJavaBeans
 * �̎d�l�ɂ�����setter����������K�v������܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Command {
	/**
	 * �R�}���h���s���܂��B
	 * @param evt �f�[�^�ύX�C�x���g
	 */
	public void execute(DataValueChangeEventKey evt);
}
