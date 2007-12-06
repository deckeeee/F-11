/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/AlarmDataStore.java,v 1.1 2003/02/14 06:48:55 frdm Exp $
 * $Revision: 1.1 $
 * $Date: 2003/02/14 06:48:55 $
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
package org.F11.scada.server.alarm;

/**
 * �f�[�^�ύX�C�x���g���󂯎��ׂ̃C���^�[�t�F�C�X�ł��B
 * ���̃C���^�[�t�F�C�X�����������N���X�́A�ύX�C�x���g���i���I�f�o�C�X�ɋL��������A
 * �f�[�^���f����ύX���܂��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface AlarmDataStore {
	/**
	 * �f�[�^�ύX�C�x���g�l�𓊓����܂��B
	 * @param key �f�[�^�ύX�C�x���g�l
	 */
	public void put(DataValueChangeEventKey key);
}
