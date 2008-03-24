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
 */

package org.F11.scada.tool.io;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.F11.scada.tool.autoprint.AutoPrintForm;

/**
 * �����󎚂̃p�����[�^�[�擾�E�i�[����C���^�[�t�F�C�X�ł�
 * @author hori
 */
public interface AutoPrintStore {
	/**
	 * ��������̃p�����[�^�[���A�N�V�����t�H�[���̃��X�g�ŕԂ��܂�
	 * @return ��������̃p�����[�^�[���A�N�V�����t�H�[���̃��X�g�ŕԂ��܂�
	 * @throws SQLException
	 */
	public List getAllAutoPrint() throws SQLException;

	/**
	 * �^�X�N���̎�������̃p�����[�^�[���A�N�V�����t�H�[���ŕԂ��܂�
	 * @param name �^�X�N��
	 * @return �^�X�N���̎�������̃p�����[�^�[���A�N�V�����t�H�[���ŕԂ��܂�
	 * @throws IOException
	 * @throws SQLException
	 */
	public AutoPrintForm getAutoPrint(final String name)
		throws IOException, SQLException;

	/**
	 * �w�肵���A�N�V�����t�H�[���̓��e�Ńp�����[�^�[���i�[���܂�
	 * @param form �A�N�V�����t�H�[��
	 * @throws IOException
	 * @throws SQLException
	 */
	public void updateAutoPrint(final AutoPrintForm form)
		throws IOException, SQLException;
}
