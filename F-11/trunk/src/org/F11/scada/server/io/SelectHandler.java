package org.F11.scada.server.io;

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

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.F11.scada.data.LoggingRowData;

/**
 * ���������̃N�G���[�y�ёS�f�[�^�̃��R�[�h�Z�b�g��Ԃ��C���^�[�t�F�C�X�ł��B
 */
public interface SelectHandler {
	/** �e�[�u�������݂��Ȃ��ꍇ�̗�O������ł� */
	public static final String TABLE_NOT_FOUND = "Table not found";

	/**
	 * �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g(��̏��)
	 * @return �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * @exception SQLException SQL�G���[�����������ꍇ
	 */
	public List select(String name, List dataHolders) throws SQLException;

	/**
	 * �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g(��̏��)
	 * @return �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * @exception SQLException SQL�G���[�����������ꍇ
	 */
	public List select(String name, List dataHolders, int limit)
			throws SQLException;

	/**
	 * �w�肳�ꂽ��� LoggingRowData��time�ȍ~�̃��X�g��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g
	 * @param time �Ԃ��f�[�^�̌�����������
	 * @return �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * @throws SQLException
	 */
	public List select(String name, List dataHolders, Timestamp time)
			throws SQLException;

	/**
	 * �e�[�u���̍ł��Â����R�[�h��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g(��̏��)
	 * @return �e�[�u���̍ł��Â����R�[�h��Ԃ��܂��B
	 * @throws SQLException
	 */
	public LoggingRowData first(String name, List dataHolders)
			throws SQLException;

	/**
	 * �e�[�u���̍ł��V�������R�[�h��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g(��̏��)
	 * @return �e�[�u���̍ł��V�������R�[�h��Ԃ��܂��B
	 * @throws SQLException
	 */
	public LoggingRowData last(String name, List dataHolders)
			throws SQLException;

	/**
	 * �w�肳�ꂽ��� LoggingRowData�̎w�肵�������Ԃ̃��X�g��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g
	 * @param start �Ԃ��f�[�^�̌����������Ԃ̓���
	 * @param limit �ő僌�R�[�h����
	 * @return �w�肳�ꂽ��� LoggingRowData�̎w�肵�������Ԃ̃��X�g��Ԃ��܂��B
	 * @throws SQLException
	 */
	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit) throws SQLException;

	/**
	 * �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g(��̏��)
	 * @param limit �擾�f�[�^���R�[�h��
	 * @param table �g�p����e�[�u����
	 * @return �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * @exception SQLException SQL�G���[�����������ꍇ
	 */
	public List<LoggingRowData> select(
			String name,
			List dataHolders,
			int limit,
			List<String> table) throws SQLException;

	/**
	 * �w�肳�ꂽ��� LoggingRowData��time�ȍ~�̃��X�g��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g
	 * @param time �Ԃ��f�[�^�̌�����������
	 * @param tables �g�p����e�[�u����
	 * @return �w�肳�ꂽ��� LoggingRowData�̃��X�g��Ԃ��܂��B
	 * @throws SQLException
	 */
	public List select(
			String name,
			List dataHolders,
			Timestamp time,
			List<String> tables) throws SQLException;

	/**
	 * �w�肳�ꂽ��� LoggingRowData�̎w�肵�������Ԃ̃��X�g��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g
	 * @param start �Ԃ��f�[�^�̌����������Ԃ̓���
	 * @param limit �ő僌�R�[�h����
	 * @param tables �g�p����e�[�u����
	 * @return �w�肳�ꂽ��� LoggingRowData�̎w�肵�������Ԃ̃��X�g��Ԃ��܂��B
	 * @throws SQLException
	 */
	public List selectBeforeAfter(
			String name,
			List dataHolders,
			Timestamp start,
			int limit,
			List<String> tables) throws SQLException;

	/**
	 * �e�[�u���̍ł��Â����R�[�h��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g(��̏��)
	 * @return �e�[�u���̍ł��Â����R�[�h��Ԃ��܂��B
	 * @throws SQLException
	 */
	public LoggingRowData first(
			String name,
			List dataHolders,
			List<String> tables) throws SQLException;

	/**
	 * �e�[�u���̍ł��V�������R�[�h��Ԃ��܂��B
	 * 
	 * @param name �f�[�^�\�[�X��
	 * @param dataHolders �f�[�^�z���_�̃��X�g(��̏��)
	 * @return �e�[�u���̍ł��V�������R�[�h��Ԃ��܂��B
	 * @throws SQLException
	 */
	public LoggingRowData last(
			String name,
			List dataHolders,
			List<String> tables) throws SQLException;

}
