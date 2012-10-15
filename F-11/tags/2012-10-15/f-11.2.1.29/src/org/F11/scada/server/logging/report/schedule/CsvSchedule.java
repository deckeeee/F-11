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

package org.F11.scada.server.logging.report.schedule;

import java.sql.Timestamp;

/**
 * ���R�[�h�������t���Z�o����w���p�N���X�C���^�[�t�F�C�X �����Ɏ���������
 * 
 * @author hori
 * 
 */
public interface CsvSchedule {
	/**
	 * �o�͂���f�[�^�̊J�n������Ԃ��܂��B
	 * 
	 * @param now ����
	 * @param startMode �e�탂�[�h
	 * @return �o�͂���f�[�^�̊J�n������Ԃ��܂��B
	 */
	Timestamp startTime(long now, boolean startMode);

	/**
	 * �o�͂���f�[�^�̏I��������Ԃ��܂��B
	 * 
	 * @param now ����
	 * @param startMode �e�탂�[�h
	 * @return �o�͂���f�[�^�̏I��������Ԃ��܂��B
	 */
	Timestamp endTime(long now, boolean startMode);

	/**
	 * �o�̓^�C�~���O�̗L����Ԃ��܂�
	 * 
	 * @return �o�̓^�C�~���O�̗L����Ԃ��܂�
	 */
	boolean isOutput();
}
