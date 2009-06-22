package org.F11.scada.security.auth;

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

import org.F11.scada.EnvironmentManager;

/**
 * <p>Authentication �I�u�W�F�N�g�𐶐�����t�@�N�g���N���X�ł��B
 * <p>�f�t�H���g�ł� PostgreSQLAuthentication �̃C���X�^���X�𐶐����ĕԂ��܂��B
 * @todo �v���p�e�B�t�@�C�����Ȃ񂩂ŁA�������� Authentication �I�u�W�F�N�g��
 * �ύX�ł���悤�ɂ���B
 */
public class AuthenticationFactory {
	/** �f�t�H���g�̃��[�U�[�F�؃N���X */
	private static final String DEFAULT_AUTHENTICATION = "org.F11.scada.security.postgreSQL.PostgreSQLAuthentication";

	private AuthenticationFactory() {}

	/**
	 * <p>�f�t�H���g�ł� PostgreSQLAuthentication �̃C���X�^���X�𐶐����ĕԂ��܂��B
	 * @return Authentication �I�u�W�F�N�g
	 * @throws ClassNotFoundException �N���X��������Ȃ��ꍇ
	 * @throws InstantiationException ���� Class �� abstract �N���X�A�C���^�t�F�[�X�A�z��N���X�A�v���~�e�B�u�^�A�܂��� void ��\���ꍇ�A�N���X�� null �R���X�g���N�^��ێ����Ȃ��ꍇ�A���邢�̓C���X�^���X�̐������ق��̗��R�Ŏ��s�����ꍇ
	 * @throws IllegalAccessException �N���X�܂��͂��� null �R���X�g���N�^�ɃA�N�Z�X�ł��Ȃ��ꍇ
	 */
	public static Authentication createAuthentication()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class cl =
				Class.forName(EnvironmentManager.get("/server/policy/authentication", DEFAULT_AUTHENTICATION));
		Authentication at = (Authentication)cl.newInstance();
		return at;
	}
}
