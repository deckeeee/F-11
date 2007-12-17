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

package org.F11.scada.server.alarm.print;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.seasar.extension.jdbc.SelectHandler;
import org.seasar.extension.jdbc.UpdateHandler;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

/**
 * AlarmPrintDAO����������N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmPrintDAOImpl implements AlarmPrintDAO {
	/** S2�R���e�i */
	private final S2Container container;

	/**
	 * ������S2�R���e�i�ŏ��������܂�
	 * @param container S2�R���e�i
	 */
	public AlarmPrintDAOImpl(String path) {
		this.container = S2ContainerFactory.create(path);
		this.container.init();
	}

	/**
	 * ������̃f�[�^��S�ĕԂ��܂��B
	 * @return PrintLineData�I�u�W�F�N�g�̃��X�g
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public List findAll() throws SQLException {
		SelectHandler handler =
			(SelectHandler) container.getComponent("findAllBeanListHandler");
		List result = (List) handler.execute(null);
		return result;
	}

	/**
	 * ���X�g�̓��e���f�[�^�x�[�X�ɑ}�����܂�
	 * @param key �f�[�^�ύX�C�x���g
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public void insert(DataValueChangeEventKey key) throws SQLException {
		UpdateHandler handler =
			(UpdateHandler) container.getComponent("insertHandler");
		Object[] obj = new Object[5];
		obj[0] = new Integer(key.getPoint());
		obj[1] = key.getProvider();
		obj[2] = key.getHolder();
		obj[3] = key.getTimeStamp();
		obj[4] = key.getValue();
		handler.execute(obj);
	}

	/**
	 * �����̃C�x���g���L�[�ɂ��Ĉ���f�[�^�I�u�W�F�N�g��Ԃ��܂�
	 * @param key �f�[�^�ύX�C�x���g
	 * @return ����f�[�^�I�u�W�F�N�g
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public PrintLineData find(DataValueChangeEventKey key)
		throws SQLException {
		SelectHandler handler =
			(SelectHandler) container.getComponent("findBeanHandler");
		Object[] obj = new Object[5];
		obj[0] = new Integer(key.getPoint());
		obj[1] = key.getProvider();
		obj[2] = key.getHolder();
		obj[3] = key.getTimeStamp();
		obj[4] = key.getValue();
		PrintLineData result = (PrintLineData) handler.execute(obj);
		return result;
	}

	/**
	 * �x�񃁃b�Z�[�W����f�[�^��S�č폜���܂�
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public void deleteAll() throws SQLException {
		UpdateHandler handler =
			(UpdateHandler) container.getComponent("deleteAllHandler");
		handler.execute(null);
	}

	/**
	 * �f�[�^�ύX�C�x���g���x�񃁃b�Z�[�W����̑Ώۂ��ǂ����𔻒肵�܂��B
	 * @param key �f�[�^�ύX�C�x���g
	 * @return �x�񃁃b�Z�[�W����ΏۂȂ� true �� �ΏۂłȂ���� false ��Ԃ��܂�
	 * @exception SQLException �f�[�^�x�[�X�G���[������
	 */
	public boolean isAlarmPrint(DataValueChangeEventKey key) throws SQLException {
		DataSource ds = (DataSource) this.container.getComponent("dataSource");
		Connection con = ds.getConnection();
		try {
			String sql =
				"SELECT a.printer_mode FROM item_table i, attribute_table a, point_table p " +				"WHERE i.attribute_id = a.attribute AND p.point = i.point AND " +				"i.point = ? AND i.provider = ? AND i.holder = ?";
			PreparedStatement st = con.prepareStatement(sql);
			try {
				st.setInt(1, key.getPoint());
				st.setString(2, key.getProvider());
				st.setString(3, key.getHolder());
				ResultSet rs = st.executeQuery();
				try {
					if (rs.next()) {
						return rs.getBoolean("printer_mode");
					}
				} finally {
					if (rs != null) {
						rs.close();
					}
				}
			} finally {
				if (st != null) {
					st.close();
				}
			}
		} finally {
			if (con != null) {
				con.close();
			}
		}

		return false;
	}
}
