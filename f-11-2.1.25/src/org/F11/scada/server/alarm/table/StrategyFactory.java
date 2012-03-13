/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/StrategyFactory.java,v 1.1.6.2 2006/08/11 02:24:33 frdm Exp $
 * $Revision: 1.1.6.2 $
 * $Date: 2006/08/11 02:24:33 $
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
package org.F11.scada.server.alarm.table;


/**
 * �s����A���S���Y���I�u�W�F�N�g�̐����t�@�N�g���[�N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public abstract class StrategyFactory {

	/**
	 * �s����A���S���Y���I�u�W�F�N�g�̐����t�@�N�g���[��Ԃ��܂��B
	 * @param className �s����A���S���Y�������N���X
	 * @return �s����A���S���Y���I�u�W�F�N�g�̐����t�@�N�g���[
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static StrategyFactory createStrategyFactory(Class className)
			throws InstantiationException, IllegalAccessException {
		return (StrategyFactory) className.newInstance();
	}

	/**
	 * �����e�[�u�����f������A���S���Y����Ԃ��܂��B
	 * @param model �e�[�u�����f��
	 * @return ����A���S���Y���I�u�W�F�N�g
	 */
	abstract public RowDataStrategy createCareerStrategy(AlarmTableModel model);

	/**
	 * �q�X�g���[�e�[�u�����f������A���S���Y����Ԃ��܂��B
	 * @param model �e�[�u�����f��
	 * @return ����A���S���Y���I�u�W�F�N�g
	 */
	abstract public RowDataStrategy createHistoryStrategy(AlarmTableModel model);

	/**
	 * �T�}���[�e�[�u�����f������A���S���Y����Ԃ��܂��B
	 * @param model �e�[�u�����f��
	 * @return ����A���S���Y���I�u�W�F�N�g
	 */
	abstract public RowDataStrategy createSummaryStrategy(AlarmTableModel model);

	/**
	 * �������e�[�u�����f������A���S���Y����Ԃ��܂��B
	 * @param model �e�[�u�����f��
	 * @return ����A���S���Y���I�u�W�F�N�g
	 */
	abstract public RowDataStrategy createOccurrenceStrategy(AlarmTableModel model);

	/**
	 * ���m�F�e�[�u�����f������A���S���Y����Ԃ��܂��B
	 * @param model �e�[�u�����f��
	 * @return ����A���S���Y���I�u�W�F�N�g
	 */
	abstract public RowDataStrategy createNoncheckStrategy(AlarmTableModel model);
}
