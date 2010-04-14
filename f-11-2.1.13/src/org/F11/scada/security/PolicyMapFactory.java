package org.F11.scada.security;

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

import java.util.Map;

/**
 * <p>�Z�L�����e�B�|���V�[�� Map �\���N���X�𐶐�����t�@�N�g���[�N���X�ł��B
 * <p>createPolicyMap(String name) ���\�b�h�� Map �I�u�W�F�N�g�𐶐����܂��B
 */
public abstract class PolicyMapFactory {
	/**
	 * �w�肳�ꂽ Map �����N���X�ŁA�|���V�[ Map �𐶐����܂��B
	 * @param name ��������N���X
	 * @return �|���V�[ Map
	 * @throws ClassNotFoundException �N���X��������Ȃ��ꍇ
	 * @throws InstantiationException ���� Class �� abstract �N���X�A�C���^�t�F�[�X�A�z��N���X�A�v���~�e�B�u�^�A�܂��� void ��\���ꍇ�A�N���X�� null �R���X�g���N�^��ێ����Ȃ��ꍇ�A���邢�̓C���X�^���X�̐������ق��̗��R�Ŏ��s�����ꍇ
	 * @throws IllegalAccessException �N���X�܂��͂��� null �R���X�g���N�^�ɃA�N�Z�X�ł��Ȃ��ꍇ
	 */
	public static Map createPolicyMap(String name)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class cl = Class.forName(name);
		PolicyMapFactory pf = (PolicyMapFactory)cl.newInstance();
		return pf.createMap();
	}

	/**
	 * ���ۂɃ|���V�[��`���AMap �C���X�^���X�𐶐����܂��B
	 * @return �|���V�[ Map
	 */
	public abstract Map createMap();
}
