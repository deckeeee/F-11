package org.F11.scada.security.auth.login;

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

import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.security.auth.Subject;
import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * �Z�L�����e�B���F�̃C���^�[�t�F�C�X�ł��B
 * <p>
 * ���̃C���^�[�t�F�C�X�́AWIFE �ɂ�����Z�L�����e�B�Ǘ����������܂��B
 * <p>
 * �f�t�H���g�̎����ł́ApostgreSQL �̃e�[�u���ɁA�Z�L�����e�B ��`���ꂽ�f�[�^���g�p���AAccessControl �N���X�� RMI
 * �Ăяo������ �A�N�Z�X�����𔻒肵�܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Authenticationable {
	/*
	 * �w�肳�ꂽ���O�̃A�N�Z�X���𔻒肵�܂��B @param name ���O�i�f�[�^�z���_�[���j @return �A�N�Z�X��������ꍇ
	 * true�A�����ꍇ false ��Ԃ��܂��B public boolean checkPermission(String name);
	 */

	/**
	 * ���[�U�[�ؑփ_�C�A���O��\�����܂��B
	 */
	public void showAuthenticationDialog();

	/**
	 * ���O�A�E�g���������܂��B
	 */
	public void logout();

	/**
	 * �ҏW�\�V���{�������X�i�[�o�^���܂��B���X�i�[�o�^���ꂽ�V���{���́A���[�U�[�F��
	 * ���s���ă��[�U�[���ύX���ꂽ�ꍇ�ɁA�������g�̕ҏW�\�t���O���X�V���܂��B
	 * 
	 * @param symbol �ҏW�\�V���{���I�u�W�F�N�g
	 */
	public void addEditable(Editable symbol);

	/**
	 * �ҏW�\�V���{�������X�i�[����폜���܂��B
	 * 
	 * @param symbol �ҏW�\�V���{���I�u�W�F�N�g
	 */
	public void removeEditable(Editable symbol);

	/**
	 * ���[�U�[����Ԃ��܂��B
	 * 
	 * @return ���[�U�[����Ԃ��܂��B
	 */
	Subject getSubject();

	/**
	 * �N���C�A���g�ݒ��Ԃ��܂�
	 * 
	 * @return �N���C�A���g�ݒ��Ԃ��܂�
	 */
	Configuration getConfiguration();
}
