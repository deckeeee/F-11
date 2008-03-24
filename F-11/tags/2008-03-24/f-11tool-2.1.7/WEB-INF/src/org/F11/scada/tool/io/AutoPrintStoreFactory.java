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
import java.sql.Connection;

import org.F11.scada.server.autoprint.jasper.JasperAutoPrint;
import org.F11.scada.xwife.server.AutoPrintPanel;

/**
 * ��������̃p�����[�^�i�[������Ԃ��܂�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AutoPrintStoreFactory {
	/** �f�[�^�[�x�[�X�R�l�N�V���� */
	private final Connection con;

	/**
	 * ��������p�����[�^�i�[�����̃t�@�N�g���[�𐶐����܂��B
	 * @param con �f�[�^�[�x�[�X�R�l�N�V����
	 */
	public AutoPrintStoreFactory(Connection con) {
		this.con = con;
	}

	/**
	 * �����̃N���X�ɑ΂��鎩������p�����[�^�i�[�����I�u�W�F�N�g��Ԃ��܂�
	 * @param clazz ��������T�[�o�[����
	 * @return �����̃N���X�ɑ΂��鎩������p�����[�^�i�[�����I�u�W�F�N�g��Ԃ��܂�
	 * @throws IOException
	 */
	public AutoPrintStore getAutoPrintStore(String clazz) throws IOException {
		if (AutoPrintPanel.class.getName().equals(clazz)) {
			return new AutoPrintPanelStore(con);
		} else if (JasperAutoPrint.class.getName().equals(clazz)) {
			return new JasperAutoPrintStore(con);
		} else {
			throw new IllegalArgumentException("Server Class not found. -> " + clazz);
		}
	}
}
