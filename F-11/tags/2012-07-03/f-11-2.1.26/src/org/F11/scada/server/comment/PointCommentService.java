/*
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

package org.F11.scada.server.comment;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * �|�C���g�R�����g�@�\�̃C���^�[�t�F�C�X�ł��B
 * @author maekawa
 */
public interface PointCommentService extends Remote {
	/**
	 * �Ώۂ̃|�C���g�R�����g���擾���܂�
	 * @param dto �擾����v���o�C�_�A�z���_���ݒ肳�ꂽ�|�C���g�R�����gDTO
	 * @return �Ώۂ̃|�C���g�R�����g���擾���܂�
	 * @throws RemoteException
	 */
	PointCommentDto getPointCommentDto(PointCommentDto dto) throws RemoteException;
	/**
	 * �|�C���g�R�����g��ݒ肵�܂��B
	 * @param dto �|�C���g�R�����gDTO
	 */
	void setPointComment(PointCommentDto dto) throws RemoteException;
}
