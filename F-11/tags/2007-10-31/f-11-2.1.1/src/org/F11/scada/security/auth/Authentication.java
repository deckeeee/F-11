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

/**
 * ���[�U�[�F�؃��W�b�N�̃C���^�[�t�F�C�X�ł��B
 * ���[�U�[�F�؂�����N���X�́A���̃C���^�[�t�F�C�X����������K�v������܂��B
 */
public interface Authentication {
	/**
	 * ���[�U�[�̔F�؂��s���܂��B
	 * �F�؂����������ꍇ�A�F�؂������[�U�[�̃v�����V�p�����֘A�Â��� Subject ��Ԃ��܂��B
	 * �F�؂Ɏ��s�����ꍇ�� null ��Ԃ��܂��B
	 * @param user �F�؂��郆�[�U�[��
	 * @param password �F�؂��郆�[�U�[�̈Í������ꂽ�p�X���[�h
	 * @return �F�؂����������ꍇ�A�F�؂������[�U�[�̃v�����V�p�����֘A�Â��� Subject ��Ԃ��܂��B
	 * �F�؂Ɏ��s�����ꍇ�� null ��Ԃ��܂��B
	 */
	public Subject checkAuthentication(String user, String password);
}
