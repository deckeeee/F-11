/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.io.postgresql.padding;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.server.register.HolderString;

/**
 * �������M���O���R�[�h��⊮���郍�W�b�N�ł��B
 * 
 * @author maekawa
 *
 */
public interface PaddingLogic {
	/**
	 * �⊮���R�[�h��}�����܂��B
	 * 
	 * @param con �R�l�N�V����
	 * @param table �e�[�u����
	 * @param holderList ���M���O�Ώۃz���_�̃��X�g
	 * @param timestamp �J�����g���R�[�h�̃^�C���X�^���v
	 * @throws SQLException
	 */
	void insertPadding(
			Connection con,
			String table,
			List<HolderString> holderList,
			Timestamp timestamp) throws SQLException;
	

	/**
	 * �����̈�P�ʌ�̃^�C���X�^���v��Ԃ��܂��B
	 * 
	 * @param timestamp ��ƂȂ�^�C���X�^���v
	 * @return �����̈�P�ʌ�̃^�C���X�^���v��Ԃ��܂��B
	 */
	Timestamp afterTime(Timestamp timestamp);

	/**
	 * �����̈�P�ʑO�̃^�C���X�^���v��Ԃ��܂��B
	 * 
	 * @param timestamp ��ƂȂ�^�C���X�^���v
	 * @return �����̈�P�ʑO�̃^�C���X�^���v��Ԃ��܂��B
	 */
	Timestamp beforeTime(Timestamp timestamp);
}
