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

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * �|�[�g�փf�[�^���M���A���ʂ���M����܂ő҂N���X�̃C���^�[�t�F�[�X�ł��B
 */
public interface ReplyWaiter {
	/**
	 * �|�[�g���N���[�Y���܂��B
	 */
	public void close() throws InterruptedException;

	/**
	 * �f�[�^�𑗎�M���܂��B
	 * ��M���̓^�C���A�E�g����܂ŕԂ�܂���B�^�C���A�E�g���̎�M�o�b�t�@�͋�ł��B
	 * @param sendBuffer ���M�o�b�t�@�ւ̎Q��
	 * @param recvBuffer ��M�o�b�t�@�ւ̎Q��
	 */
	public void syncSendRecv(ByteBuffer sendBuffer, ByteBuffer recvBuffer)
		throws IOException, InterruptedException;

}
