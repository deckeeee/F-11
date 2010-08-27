package org.F11.scada.applet.graph;

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

import java.awt.Color;
import java.awt.Insets;
import java.beans.PropertyChangeListener;

/**
 * �X�P�[���\���̃f�[�^���f���E�C���^�[�t�F�C�X�ł��B
 */
public interface VerticallyScaleModel {

	/**
	 * PropertyChangeListener �����X�i�[���X�g�ɒǉ����܂��B
	 * @param listener �ǉ����� PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * PropertyChangeListener �����X�i�[���X�g����폜���܂��B
	 * @param listener �폜���� PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * �ڐ���̕���Ԃ��܂��B
	 * @return �ڐ���̕�
	 */
	public int getScaleOneWidth();

	/**
	 * �ڐ���̍�����Ԃ��܂��B
	 * @return �ڐ���̍���
	 */
	public int getScaleOneHeight();

	/**
	 * �ڐ���̑�����Ԃ��܂��B
	 * @return �ڐ���̑���
	 */
	public int getScaleCount();

	/**
	 * �X�P�[���̍ŏ��l��Ԃ��܂��B
	 * @return �X�P�[���̍ŏ��l
	 */
	public double getScaleMin();

	/**
	 * �X�P�[���̍ő�l��Ԃ��܂��B
	 * @return �X�P�[���̍ő�l
	 */
	public double getScaleMax();

	/**
	 * �X�P�[���̍ŏ��l��ݒ肵�܂��B
	 * @param �X�P�[���̍ŏ��l
	 */
	// TODO ������String�ɕύX�H
	public void setScaleMin(double min);

	/**
	 * �X�P�[���̍ő�l��ݒ肵�܂��B
	 * @param �X�P�[���̍ő�l
	 */
	// TODO ������String�ɕύX�H
	public void setScaleMax(double max);

	/**
	 * �X�P�[���ڐ��蕶����̔z���Ԃ��܂��B
	 * @return �X�P�[���ڐ��蕶����̔z��
	 */
	public String[] getScaleStrings();

	/**
	 * �X�P�[���ڐ��蕶����̍ő啝��Ԃ��܂��B
	 * @param isLeft �����X�P�[�����ǂ���
	 * @return �X�P�[���ڐ��蕶����̍ő啝
	 */
	public int getScaleStringMaxWidth(boolean isLeft);

	/**
	 * �X�P�[���ڐ��蕶����̍ő卂��Ԃ��܂��B
	 * @return �X�P�[���ڐ��蕶����̍ő卂
	 */
	public int getScaleStringMaxHeight();

	/**
	 * �X�P�[���ڐ��蕶����̐F��Ԃ��܂��B
	 * @return �X�P�[���ڐ��蕶����̐F
	 */
	public Color getScaleStringColor();

	/**
	 * �`��G���A���̃C���Z�b�c��Ԃ��܂�
	 */
	public Insets getScaleInsets();

	/**
	 * �X�P�[���ő�X�p���ύX�̗L��
	 * @return �X�P�[���ő�X�p���ύX���ł���ꍇ�� true �����łȂ��ꍇ�� false
	 */
	public boolean isMaxEnable();

	/**
	 * �X�P�[���ŏ��X�p���ύX�̗L��
	 * @return �X�P�[���ŏ��X�p���ύX���ł���ꍇ�� true �����łȂ��ꍇ�� false
	 */
	public boolean isMinEnable();

	/**
	 * �X�P�[���ő�X�p���ύX�̗L����ݒ肵�܂��B
	 * @param enable �ύX�\�ɂ���ꍇ�� true�A�ύX�s�ɂ���ꍇ�� false
	 */
	public void setMaxEnable(boolean enable);

	/**
	 * �X�P�[���ŏ��X�p���ύX�̗L����ݒ肵�܂��B
	 * @param enable �ύX�\�ɂ���ꍇ�� true�A�ύX�s�ɂ���ꍇ�� false
	 */
	public void setMinEnable(boolean enable);

	/**
	 * �X�P�[���ɑΉ�����V���[�Y��Ԃ��܂��B
	 * �Ή�����V���[�Y�����݂��Ȃ��ꍇ��-1��Ԃ��܂��B
	 * 
	 * @return �X�P�[���ɑΉ�����V���[�Y��Ԃ��܂��B�Ή�����V���[�Y�����݂��Ȃ��ꍇ��-1��Ԃ��܂��B
	 */
	public int getSeries();
	
	/**
	 * �f�[�^�v���o�C�_����Ԃ��܂�
	 * @return �f�[�^�v���o�C�_����Ԃ��܂�
	 */
	public String getDataProviderName();
	
	/**
	 * �f�[�^�z���_����Ԃ��܂�
	 * @return �f�[�^�z���_����Ԃ��܂�
	 */
	public String getDataHolderName();
}
