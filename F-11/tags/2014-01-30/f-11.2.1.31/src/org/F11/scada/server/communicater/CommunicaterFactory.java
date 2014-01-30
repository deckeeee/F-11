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


/**
 * Communicater�̃t�@�N�g���ł��B
 */
public interface CommunicaterFactory {
	/**
	 * �V����Communicater���쐬����Ɠ����ɁA���X�i�[�o�^���܂��B
	 * 
	 * @param device �f�o�C�X���
	 * @param listener �o�^���郊�X�i�[
	 * @return �V����Communicater�I�u�W�F�N�g
	 * @throws Exception
	 */
	public Communicater createCommunicator(Environment device) throws Exception;
}
