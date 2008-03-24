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

package org.F11.scada.tool.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.F11.scada.server.ServerConstruction;
import org.apache.commons.logging.Log;

abstract public class ServerConstructionUtil {

	/**
	 * �J�����g�}�V�������C���ł���� true �������łȂ���� false ��Ԃ��܂��B
	 * 
	 * @param construction �T�[�o�[�E�T�u�V�X�e���\��
	 * @param log ���OAPI
	 * @return �J�����g�}�V�������C���ł���� true �������łȂ���� false ��Ԃ��܂��B
	 */
	public static boolean isMainSystem(ServerConstruction construction, Log log) {
		try {
			return construction.isMainSystem(InetAddress.getLocalHost()
					.getHostAddress());
		} catch (UnknownHostException e) {
			// ���[�J���z�X�g��getHostAddress()�Ȃ̂ŁA��O���������邱�Ƃ͖����B
			log.error("�h���C�����邢��IP�A�h���X�̌`�����s���ł�", e);
			return false;
		}
	}
}
