package org.F11.scada.applet.schedule;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * �X�P�W���[���N���X�𐶐����钊�ۃN���X�ł��B �X�P�W���[���r���[�̃o���G�[�V�����́A���̃N���X���p�������t�@�N�g���N���X���쐬���Ă��������B
 * AbstractFactory Pattern ���g�p���Ă��܂��B
 * 
 * @todo �X�P�W���[���̃O���[�v���ƁA�@��ʃX�P�W���[���̑Ή��B
 */
public abstract class ScheduleFactory {
	/** �X�P�W���[�����f���̎Q�Ƃł� */
	protected ScheduleModel scheduleModel;

	private static Logger logger = Logger.getLogger(ScheduleFactory.class);

	/**
	 * �R���X�g���N�^
	 * 
	 * @see getFactory
	 * @param alarmRef �����[�g�f�[�^�I�u�W�F�N�g
	 */
	public ScheduleFactory(ScheduleModel scheduleModel) {
		this.scheduleModel = scheduleModel;
	}

	/**
	 * �X�^�e�B�b�N�E�t�@�N�g�����\�b�h�ł��B ���̃��\�b�h���g�p���āA�X�P�W���[���I�u�W�F�N�g�̃C���X�^���X�𐶐����܂��B
	 * 
	 * @param alarmRef �����[�g�f�[�^�I�u�W�F�N�g
	 * @param viewClass �g�p����r���[�N���X��
	 * @param isSort ���ԃ\�[�g�̗L��
	 * @param isNonTandT �����E�����r���[�̗L��
	 * 
	 */
	public static ScheduleFactory getFactory(
			ScheduleModel scheduleModel,
			String viewClass,
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient) {
		ScheduleFactory factory = null;
		String realClassName = "org.F11.scada.applet.schedule." + viewClass;
		logger.debug("Class Name : " + realClassName);

		try {
			Class factoryClass = Class.forName(realClassName);
			Class[] param = { ScheduleModel.class, Boolean.TYPE, Boolean.TYPE,
					String.class, Boolean.TYPE };
			Constructor constructor = factoryClass.getConstructor(param);
			factory = (ScheduleFactory) constructor.newInstance(new Object[] {
					scheduleModel, Boolean.valueOf(isSort),
					Boolean.valueOf(isNonTandT), pageId,
					Boolean.valueOf(isLenient) });
		} catch (ClassNotFoundException ex) {
			logger.warn("class not found = " + viewClass);
		} catch (InvocationTargetException ex2) {
			ex2.getTargetException().printStackTrace();
			// ex2.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return factory;
	}

	/**
	 * �r���[���܂񂾃R���|�[�l���g��Ԃ����ۃ��\�b�h�ł��B �T�u�N���X�Ŏ������܂��B
	 * 
	 * @return �r���[���܂񂾃R���|�[�l���g(�ʏ�̓X�N���[���y�C�����Ԃ���܂�)
	 */
	public abstract JComponent createView();

	/**
	 * �c�[���o�[���܂񂾃R���|�[�l���g��Ԃ����ۃ��\�b�h�ł��B �T�u�N���X�Ŏ������܂��B
	 * 
	 * @return �c�[���o�[���܂񂾃R���|�[�l���g
	 */
	public abstract JComponent createToolBar();
}
