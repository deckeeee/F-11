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

package org.F11.scada.applet.ngraph.model;


import java.beans.PropertyChangeListener;
import java.util.List;

import org.F11.scada.applet.ngraph.SeriesGroup;


/**
 * �O���t���f��
 *
 * @author maekawa
 *
 */
public interface GraphModel {
	/** ���f�������������ꂽ�Ƃ��ɒʒm���܂� */
	final static String INITIALIZE = GraphModel.class.getName() + ".initialize";
	/** ���f�����̃f�[�^�l���ύX���ꂽ�Ƃ��ɒʒm���܂� */
	final static String VALUE_CHANGE =
		GraphModel.class.getName() + ".value.change";
	/** �J�����g�O���[�v���ύX���ꂽ�Ƃ��ɒʒm���܂� */
	final static String GROUP_CHANGE =
		GraphModel.class.getName() + ".group.change";

	/**
	 * ���f���̃v���p�e�B�ύX���X�i�[��o�^���܂�
	 *
	 * @param listener �v���p�e�B�ύX���X�i�[
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * ���f���̃v���p�e�B�ύX���X�i�[���폜���܂�
	 *
	 * @param listener �v���p�e�B�ύX���X�i�[
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * ���f�������������܂��B
	 */
	void initialize();

	/**
	 * ���f�����I�����܂��B
	 */
	void shutdown();

	/**
	 * ���f�����ێ�����f�[�^���R�[�h����Ԃ��܂�
	 *
	 * @return ���f�����ێ�����f�[�^���R�[�h����Ԃ��܂�
	 */
	int getMaxRecord();

	/**
	 * �O���[�v��ݒ肵�A���f���̓��e��ΏۃO���[�v�ɕύX���܂��B
	 *
	 * @param group �ΏۃO���[�v
	 */
	void setGroupNo(int group);

	/**
	 * �J�����g�O���[�vNo.��Ԃ��܂��B
	 *
	 * @return �O���[�vNo��Ԃ��܂��B
	 */
	int getGroupNo();

	/**
	 * �J�����g�O���[�v����Ԃ��܂��B
	 *
	 * @return �J�����g�O���[�v����Ԃ��܂�
	 */
	String getGroupName();

	/**
	 * �O���[�v�̃��X�g��Ԃ��܂��B
	 *
	 * @return �O���[�v�̃��X�g��Ԃ��܂��B
	 */
	List<SeriesGroup> getSeriesGroups();

	/**
	 * �f�[�^�擾�Ώۃ��O����Ԃ��܂�
	 *
	 * @return �f�[�^�擾�Ώۃ��O����Ԃ��܂�
	 */
	String getLogName();

	/**
	 * �f�[�^�擾�Ώۃ��O����ݒ肵�܂�
	 *
	 * @param logName �f�[�^�擾�Ώۃ��O����ݒ肵�܂�
	 */
	void setLogName(String logName);

	/**
	 * �X�N���[���o�[���ŏ��l�ɂȂ����ꍇ�ʒm���܂��B
	 * @return TODO
	 */
	boolean reachedMinimum();

	/**
	 * �X�N���[���o�[���ő�l�ɂȂ����ꍇ�ʒm���܂��B
	 * @return TODO
	 */
	boolean reachedMaximum();

	/**
	 * �v���p�e�B�ύX���X�i�[��S�ď������܂��B
	 */
	void removePropertyChangeListeners();

}