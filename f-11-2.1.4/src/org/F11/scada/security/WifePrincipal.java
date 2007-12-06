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

package org.F11.scada.security;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.Principal;

/**
 * WIFE �̎�̂�\���܂��B��̓I�ɂ̓��[�U�[�ƃO���[�v��\���܂��B
 */
public class WifePrincipal implements Principal, Serializable {
	private static final long serialVersionUID = 8159333082688116879L;
	/** ��̖� */
	private final String name;

	/**
	 * ���O�ŏ��������ꂽ�I�u�W�F�N�g�𐶐����܂��B
	 * @param name ��̖�
	 */
	public WifePrincipal(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Argument `name' is null.");
		}
		this.name = name;
	}

	/**
	 * ��̖���Ԃ��܂�
	 * @return ��̖�
	 */
	public String getName() {
		return name;
	}

	public boolean equals(Object another) {
		if (another == this) {
			return true;
		}
		if (!(another instanceof WifePrincipal)) {
			return false;
		}
		WifePrincipal principal = (WifePrincipal) another;
		return principal.name.equals(this.name);
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public String toString() {
		return this.name;
	}

	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifePrincipal(name); 
	}
}
