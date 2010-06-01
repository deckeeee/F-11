/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/security/auth/Subject.java,v 1.7 2003/01/30 08:24:30 frdm Exp $
 * $Revision: 1.7 $
 * $Date: 2003/01/30 08:24:30 $
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

package org.F11.scada.security.auth;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>WIFE �ɂ������̂𒊏ۓI�ɕ\���N���X�ł��B
 * ��̓I�ɂ� Subject ���ɁA�֘A�������[�U�[�ƃO���[�v�̃v�����V�p�� Set �ێ����܂��B
 * <p>�v�����V�p���ɂ͕s�σI�u�W�F�N�g�� Set �������g�p���Ă���̂ŁA�����v�����V�p���̕ύX����͂ł��܂���B
 * ����ɐV�����v�����V�p����ێ������I�u�W�F�N�g�𐶐����Ă��������B
 * 
 * @author Hideaki Maekawa <frdm@users.sorceforge.jp>
 */
public final class Subject implements Serializable {
	private static final long serialVersionUID = -2449886602280598708L;

	/** �v�����V�p���������Ȃ������̃��[�U�[��\��Subject �N���X�̃C���X�^���X�ł��B */
	private static final Subject NULL_SUBJECT = new Subject(new HashSet(), "");
	/** �v�����V�p���� Set �ł� */
	private final Set principals;
	/** ���݂̃��[�U�[���ł� */
	private final String userName;

	/**
	 * �v���C�x�[�g�R���X�g���N�^
	 * �w�肵���v�����V�p���ŏ����������C���X�^���X�𐶐����܂��B
	 * @param set �v�����V�p��
	 * @param userName ���[�U�[��
	 */
	private Subject(Set set, String userName) {
		if (set == null) {
			throw new IllegalArgumentException("set is null.");
		}
		if (userName == null) {
			throw new IllegalArgumentException("userName is null.");
		}
		this.principals = Collections.unmodifiableSet(set);
		this.userName = userName;
	}

	/**
	 * Subject �I�u�W�F�N�g�𐶐�����t�@�N�g���[���\�b�h�ł��B
	 * �w�肳�ꂽ�����ŏ����������I�u�W�F�N�g��Ԃ��܂��B
	 * @param set �v�����V�p��
	 * @param userName ���[�U�[��
	 * @return Subject �I�u�W�F�N�g
	 */
	public static Subject createSubject(Set set, String userName) {
		return new Subject(set, userName);
	}

	/**
	 * �v�����V�p���������Ȃ������̃��[�U�[��\��Subject �N���X�̃C���X�^���X��Ԃ��܂��B
	 * @return Subject
	 */	
	public static Subject getNullSubject() {
		return NULL_SUBJECT;
	}

	/**
	 * ���� Subject �Ɋ֘A�Â���ꂽ�A�v�����V�p���̃Z�b�g��Ԃ��܂��B
	 * @return �v�����V�p���̃Z�b�g
	 */
	public Set getPrincipals() {
		return principals;
	}

	/**
	 * ���݂̃��[�U�[����Ԃ��܂��B
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g���r���܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Subject)) {
			return false;
		}
		Subject sub = (Subject)obj;
		return sub.principals.equals(principals)
				&& sub.userName.equals(userName); 
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + principals.hashCode();
		result = 37 * result + userName.hashCode();
		return result;
	}
	
	/**
	 * ���̃I�u�W�F�N�g�̕�����`����Ԃ��܂��B
	 * �Ԃ���镶����́A�����I�ɕύX�����\��������܂��B�J�����̃f�o�b�O�ȊO��
	 * �g�p���鎖�͐�������܂���B
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("principals=" + principals.toString());
		buffer.append(" ,userName=" + userName);
		return buffer.toString();
	}
	
	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new Subject(principals, userName);
	}
}
