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

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.security.auth.Subject;

/**
 * WIFE �̃Z�L�����e�B�|���V�[��\���N���X�ł��B
 * ���[�U�[�ƃO���[�v�ɋ����ꂽ�p�[�~�b�V������ێ����܂��B
 * �w�肵�� Subject ���A�f�[�^�z���_�[�̋��������Ă���̂����肵�܂��B
 * ���̃N���X�͗B��̃C���X�^���X��ێ����܂�(Singleton �p�^�[��)
 */
public class WifePolicy extends Policy {
	/** WifePolicy �̃C���X�^���X */
	private static Policy _policy = new WifePolicy();
	/** WifePrincipal �� PermissionCollection �̃n�b�V���}�b�v�ł� */
	private Map policyMap;
	/** �f�t�H���g�� policyMap �t�@�N�g���N���X�ł� */
	private static final String DEFAULT_POLICYMAP =
			"org.F11.scada.security.postgreSQL.PostgreSQLPolicyMap";

	/**
	 * �w�肳�ꂽ Map �����N���X�ŁA�Z�L�����e�B�|���V�[�����������܂��B
	 * �f�t�H���g�ł́APostgreSQLPolicyMap �N���X���g�p���܂��B
	 */
	private WifePolicy() {
		try {
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �w�肳�ꂽ Map �����N���X�ŁA�Z�L�����e�B�|���V�[�����������܂��B
	 * @throws ClassNotFoundException Map �����N���X��������Ȃ��ꍇ
	 * @throws InstantiationException ���� Class �� abstract �N���X�A�C���^�t�F�[�X�A
	 * �z��N���X�A�v���~�e�B�u�^�A�܂��� void ��\���ꍇ�A
	 * �N���X�� null �R���X�g���N�^��ێ����Ȃ��ꍇ�A
	 * ���邢�̓C���X�^���X�̐������ق��̗��R�Ŏ��s�����ꍇ
	 * @throws IllegalAccessException �N���X�܂��͂��� null �R���X�g���N�^�ɃA�N�Z�X�ł��Ȃ��ꍇ
	 */
	private void init()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		policyMap =
				PolicyMapFactory.createPolicyMap(EnvironmentManager.get("/server/policy/policyMap", DEFAULT_POLICYMAP));
	}

	/**
	 * ���̃|���V�[�̃C���X�^���X��Ԃ��܂��B
	 * @return ���̃|���V�[�̃C���X�^���X
	 */
	public static Policy getPolicy() {
		return _policy;
	}

	/**
	 * ���̃��\�b�h�̓T�|�[�g����Ă��܂���B
	 * @param policy �Z�b�g����|���V�[
	 * @throws UnsupportedOperationException ��� UnsupportedOperationException ���X���[���܂�
	 */
	public static void setPolicy(Policy policy) {
		throw new UnsupportedOperationException();
	}

	/**
	 * ���̃��\�b�h�̓T�|�[�g����Ă��܂���B
	 * @param codesource �R�[�h�\�[�X
	 * @return PermissionCollection
	 * @throws UnsupportedOperationException ��� UnsupportedOperationException ���X���[���܂�
	 */
	public PermissionCollection getPermissions(CodeSource codesource) {
		throw new UnsupportedOperationException();
	}

	/**
	 * ���̃��\�b�h�̓T�|�[�g����Ă��܂���B
	 * @param domain ProtectionDomain
	 * @return PermissionCollection
	 * @throws UnsupportedOperationException ��� UnsupportedOperationException ���X���[���܂�
	 */
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		throw new UnsupportedOperationException();
	}

	/**
	 * ���̃��\�b�h�̓T�|�[�g����Ă��܂���B
	 * @param domain ProtectionDomain
	 * @param permission Permission
	 * @return true
	 * @throws UnsupportedOperationException ��� UnsupportedOperationException ���X���[���܂�
	 */
	public boolean implies(ProtectionDomain domain, Permission permission) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Subject �ɋ����ꂽ�A�N�Z�X���ɂ��ăO���[�o���|���V�[��]�����A
	 * ���̃A�N�Z�X����������Ă��邩�ǂ����𔻒肵�܂��B
	 * @param subject ����Ώۂ� Subject
	 * @param permission �܂܂�Ă��邩�ǂ����𔻒肷�� Permission �I�u�W�F�N�g
	 * @return permission ������ subject �ɋ����ꂽ�A�N�Z�X���̓K�؂ȃT�u�Z�b�g�̏ꍇ�� true�B
	 */
	public boolean implies(Subject subject, Permission permission) {
		Set principals = subject.getPrincipals();
		for (Iterator it = principals.iterator(); it.hasNext();) {
			Principal principal = (Principal) it.next();
			Object o = policyMap.get(principal);
			if (o != null) {
				PermissionCollection pc = (PermissionCollection) o;
//				System.out.println("principal:" + principal + " pc:" + pc);
				if (pc.implies(permission)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * �|���V�[�ݒ�����t���b�V���܂��͍ēǂݍ��݂��܂��B
	 * �f�t�H���g�� PostgreSQLPolicyMap �N���X�ł́A�f�[�^�x�[�X���ēǍ����ă|���V�[���č\�z���܂��B
	 */
	public void refresh() {
		try {
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ���̃|���V�[�̕�����\����Ԃ��܂��B
	 * ������̕\�����@��,�g�p����Ă��� Map �����Ɉˑ����܂��B�f�t�H���g�� PostgreSQLPolicyMap
	 * �ł́AHashMap �� toString ���g�p����܂��B
	 * @return �|���V�[�̕�����\��
	 */
	public String toString() {
		return policyMap.toString();
	}
}
