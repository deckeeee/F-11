package org.F11.scada.applet.symbol;

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

/**
 * �e���L�[�_�C�A���O�Ő��l�ҏW������V���{���̃C���^�[�t�F�C�X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface TenkeyEditable extends Editable {
	/**
	 * �V���{���̒l��Ԃ��܂�
	 */
	public String getValue();
	/**
	 * �V���{���ɒl��ݒ肵�܂�
	 */
	public void setValue(String value);
	/**
	 * �ŏ��l��Ԃ��܂�
	 */
	public double getConvertMin();
	/**
	 * �ő�l��Ԃ��܂�
	 */
	public double getConvertMax();
	/**
	 * ���l�\���t�H�[�}�b�g�������Ԃ��܂�
	 */
	public String getFormatString();
	/**
	 * �_�C�A���O�^�C�g����Ԃ��܂��B
	 * @return �_�C�A���O�^�C�g��
	 */
	String getDialogTitle();
}
