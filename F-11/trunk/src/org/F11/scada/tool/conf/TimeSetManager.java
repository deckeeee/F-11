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

package org.F11.scada.tool.conf;

import java.util.List;

import org.F11.scada.tool.conf.io.TimeSetBean;
import org.F11.scada.tool.conf.io.TimeSetTaskBean;

/**
 * TimeSet.xml�̃X�g���[���C���^�[�t�F�C�X�ł�
 * 
 * @author maekawa
 *
 */
public interface TimeSetManager {
	/**
	 * �Ώۃ^�X�N�̃v���p�e�B��Ԃ��܂�
	 * 
	 * @param name �Ώۃ^�X�N
	 * @param key �v���p�e�B��
	 * @param def �v���p�e�B�����l
	 * @return �Ώۃ^�X�N�̃v���p�e�B��Ԃ��܂�
	 */
	String getTimeSet(String name, String key, String def);

	/**
	 * �Ώۃ^�X�N�̏������X�g��Ԃ��܂�
	 * @param name �Ώۃ^�X�N
	 * @return �Ώۃ^�X�N�̏������X�g��Ԃ��܂�
	 */
	List<TimeSetBean> getTimeSetBeansList(String name);

	/**
	 * �Ώۃ^�X�N�̃v���p�e�B��ݒ肵�܂�
	 * @param name �Ώۃ^�X�N
	 * @param key �v���p�e�B��
	 * @param value �v���p�e�B�l 
	 */
	void setTimeSet(String name, String key, String value);

	/**
	 * �Ώۃ^�X�N�ɏ������X�g��ݒ肵�܂�
	 * 
	 * @param name �Ώۃ^�X�N
	 * @param list �������X�g
	 */
	void setTimeSetBeansList(String name, List<TimeSetBean> list);

	/**
	 * �^�X�N��ݒ肵�܂�
	 * 
	 * @param bean �^�X�N
	 */
	void setTimeSetTask(TimeSetTaskBean bean);

	/**
	 * �Ώۃ^�X�N���폜���܂�
	 * @param bean �Ώۃ^�X�N
	 * @return �폜�����^�X�N
	 */
	TimeSetTaskBean removeTimeSetTask(TimeSetTaskBean bean);
	
	/**
	 * �^�X�N�̃��X�g��Ԃ��܂�
	 * 
	 * @return �^�X�N�̃��X�g��Ԃ��܂�
	 */
	List<TimeSetTaskBean> getTimeSetTask();
}