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

import java.beans.PropertyChangeListener;

import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.data.WifeDataSchedule;

/**
 * �X�P�W���[���f�[�^���f���C���^�[�t�F�C�X�ł��B
 */
public interface ScheduleModel extends Editable {
	/**
	 * �����[�g�I�u�W�F�N�g�ɒl��ݒ肵�܂��B
	 */
	public void setValue();

	/**
	 * �f�[�^���ҏW���ꂽ�ꍇ�� true ��Ԃ��܂��B
	 */
	public boolean isEditing();

	/**
	 * �ێ����Ă���X�P�W���[���f�[�^���X�V���܂��B
	 */
	public void writeData();

	/**
	 * �ێ����Ă���X�P�W���[���f�[�^���A���h�D���܂��B
	 */
	public void undoData();

	/**
	 * �X�P�W���[���f�[�^�̃O���[�v���w�肵���C���f�b�N�X�ɕύX���܂��B
	 * 
	 * @param index �O���[�v�C���f�b�N�X
	 */
	public void setGroupNo(int index);

	/**
	 * �O���[�vNo ��Ԃ��܂�
	 */
	public int getGroupNo();

	/**
	 * �O���[�vNo �̍ő吔��Ԃ��܂�
	 */
	public int getGroupNoMax();

	/**
	 * �X�P�W���[���p�^�[���̃C���f�b�N�X��Ԃ��܂��B
	 * 
	 * @param index
	 */
	public int getDayIndex(int index);

	/**
	 * �X�P�W���[���p�^�[���̃C���f�b�N�X����Ԃ��܂��B
	 * 
	 * @param index
	 */
	public String getDayIndexName(int index);

	/**
	 * ������̃C���f�b�N�X��Ԃ��܂��B
	 * 
	 * @param index
	 */
	public int getSpecialDayOfIndex(int index);

	/**
	 * ���ڃp�^�[���̃T�C�Y��Ԃ��܂��B
	 * 
	 * @return ���ڃp�^�[���̃T�C�Y
	 */
	public int getPatternSize();

	/**
	 * �X�P�W���[�� On/Off �̍ő�񐔂�Ԃ��܂��B
	 * 
	 * @return �X�P�W���[�� On/Off �̍ő��
	 */
	public int getNumberSize();

	/**
	 * �X�P�W���[���s���f����Ԃ��܂��B
	 * 
	 * @param index �Ԃ��s
	 * @return �X�P�W���[���s���f����Ԃ��܂��B
	 */
	public ScheduleRowModel getScheduleRowModel(int index);

	/**
	 * �f�[�^���f�����ύX����邽�тɒʒm����郊�X�g�Ƀ��X�i�[��ǉ����܂��B
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * �f�[�^���f�����ύX����邽�тɒʒm����郊�X�g���烊�X�i�[���폜���܂��B
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * �O���[�v��ǉ����܂��B
	 * 
	 * @param dataProvider
	 * @param dataHolder
	 * @param flagHolder
	 */
	public void addGroup(String dataProvider, String dataHolder, String flagHolder);

	/**
	 * �O���[�v���̂�Ԃ��܂�
	 * 
	 * @return �O���[�v���̂�Ԃ��܂�
	 */
	public String getGroupName();

	/**
	 * �f�[�^���t�@�����T�[��ؒf���܂��B
	 */
	public void disConnect();

	/**
	 * �J�����g�̃X�P�W���[���f�[�^��Ώۂ̃O���[�v�ɕ������܂��B
	 * 
	 * @param dest ������̃X�P�W���[���O���[�v
	 */
	void duplicateGroup(int[] dest);

	/**
	 * �J�����g�̃X�P�W���[���f�[�^�̗j�����f�[�^��Ώۂ̗j���ɕ������܂��B
	 * 
	 * @param src �������j���ԍ�
	 * @param dest ������j���ԍ�
	 */
	void duplicateWeekOfDay(int src, int[] dest);

	/**
	 * �O���[�v�G�������g�̔z���Ԃ��܂��B
	 * 
	 * @return �O���[�v�G�������g�̔z��
	 */
	GroupElement[] getGroupNames();

	/**
	 * �X�P�W���[����ʂŏ���ɂ�����̌�(���������Ȃ�2, ����7���Ȃ�7�Ȃ�)��Ԃ��܂��B
	 * 
	 * @return �X�P�W���[����ʂŏ���ɂ�����̌�(���������Ȃ�2, ����7���Ȃ�7�Ȃ�)��Ԃ��܂��B
	 */
	int getTopSize();

	/**
	 * ���f���̒��̃X�P�W���[���l��Ԃ��܂��B
	 * 
	 * @return ���f���̒��̃X�P�W���[���l��Ԃ��܂��B
	 */
	WifeDataSchedule getDataSchedule();

	/**
	 * ���f���ɃX�P�W���[���l��ݒ肵�܂��B
	 * 
	 * @param schedule ���f���ɃX�P�W���[���l��ݒ肵�܂��B
	 */
	void setDataSchedule(WifeDataSchedule schedule);

	/**
	 * �f�[�^�ύX�C�x���g�𔭉΂��܂��B
	 * @param oldValue �ύX�O�̃I�u�W�F�N�g
	 * @param newValue �ύX��̃I�u�W�F�N�g
	 */
	void firePropertyChange(Object oldValue, Object newValue);
}
