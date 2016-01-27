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

package org.F11.scada.applet.schedule.point;

import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.event.SwingPropertyChangeSupport;

import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.schedule.GroupElement;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.applet.schedule.ScheduleRowModel;
import org.F11.scada.applet.schedule.ScheduleRowModelImpl;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.util.RmiErrorUtil;
import org.F11.scada.util.RmiUtil;
import org.apache.log4j.Logger;

public class SeparateScheduleModel implements ScheduleModel {
	private final Logger logger = Logger.getLogger(SeparateScheduleModel.class);
	/** �o�E���Y�v���p�e�B�ł��B */
	private SwingPropertyChangeSupport changeSupport;
	/** �X�P�W���[���f�[�^�ł� */
	private WifeDataSchedule dataSchedule;
	/** �X�P�W���[���|�C���g�T�[�r�X�̎Q�� */
	private final SchedulePointService schedulePointService;
	/** �Ώۍs�f�[�^ */
	private final SchedulePointRowDto rowDto;
	/** �e�R���|�[�l���g */
	private final JDialog dialog;

	/** �X�P�W���[�����f���ύX�C�x���g�� */
	private static final String SCHEDULE_MODEL_CHANGE = "org.F11.scada.applet.schedule.point.SeparateScheduleModel.SCHEDULE_MODEL_CHANGE";

	public SeparateScheduleModel(SchedulePointRowDto rowDto, JDialog dialog)
			throws RemoteException {
		this((SchedulePointService) RmiUtil
				.lookupServer(SchedulePointService.class), rowDto, dialog);
	}

	public SeparateScheduleModel(
			SchedulePointService schedulePointService,
			SchedulePointRowDto rowDto,
			JDialog dialog) throws RemoteException {
		this.schedulePointService = schedulePointService;
		this.rowDto = rowDto;
		this.dialog = dialog;
		dataSchedule = this.schedulePointService.getSeparateSchedule(rowDto);
	}

	/**
	 * �I�u�W�F�N�g�ɒl��ݒ肵�܂��B
	 * 
	 * @exception RemoteException
	 */
	public void setValue() {
		try {
			SchedulePointUtil.setLoggingField(rowDto);
			schedulePointService.updateSeperateSchedule(
					rowDto,
					new Date(),
					dataSchedule);
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, dialog);
		}
	}

	/**
	 * �f�[�^���ҏW���ꂽ�ꍇ�� true ��Ԃ��܂��B
	 */
	public boolean isEditing() {
		return false;
	}

	/**
	 * �ێ����Ă���X�P�W���[���f�[�^���X�V���܂��B
	 */
	public void writeData() {
	}

	/**
	 * �ێ����Ă���X�P�W���[���f�[�^���A���h�D���܂��B
	 */
	public void undoData() {
	}

	/**
	 * �X�P�W���[���f�[�^�̃O���[�v���w�肵���C���f�b�N�X�ɕύX���܂��B
	 * 
	 * @param index �O���[�v�C���f�b�N�X
	 */
	public void setGroupNo(int index) {
	}

	/**
	 * �X�P�W���[���p�^�[���̃C���f�b�N�X��Ԃ��܂��B
	 * 
	 * @param index ���ځi�j���j
	 * @return ���ڕ\���C���f�b�N�X
	 */
	public int getDayIndex(int index) {
		return dataSchedule.getDayIndex(index);
	}

	/**
	 * �O���[�vNo �̍ő吔��Ԃ��܂�
	 * 
	 * @return �O���[�vNo �̍ő吔
	 */
	public int getGroupNoMax() {
		return 1;
	}

	/**
	 * �X�P�W���[���p�^�[���̃C���f�b�N�X����Ԃ��܂��B
	 * 
	 * @param index
	 */
	public String getDayIndexName(int index) {
		return dataSchedule.getDayIndexName(index);
	}

	/**
	 * ������̃C���f�b�N�X��Ԃ��܂��B
	 * 
	 * @param index
	 */
	public int getSpecialDayOfIndex(int index) {
		return dataSchedule.getSpecialDayOfIndex(index);
	}

	/**
	 * ���ڃp�^�[���̃T�C�Y��Ԃ��܂��B
	 * 
	 * @return ���ڃp�^�[���̃T�C�Y
	 */
	public int getPatternSize() {
		if (dataSchedule == null) {
			return 0;
		}
		return dataSchedule.getPatternSize();
	}

	/**
	 * �X�P�W���[�� On/Off �̍ő�񐔂�Ԃ��܂��B
	 * 
	 * @return �X�P�W���[�� On/Off �̍ő��
	 */
	public int getNumberSize() {
		return dataSchedule.getNumberSize();
	}

	/**
	 * �X�P�W���[���s���f����Ԃ��܂��B
	 * 
	 * @param index �Ԃ��s
	 * @return �X�P�W���[���s���f����Ԃ��܂��B
	 */
	public ScheduleRowModel getScheduleRowModel(int index) {
		return new ScheduleRowModelImpl(this, index);
	}

	/**
	 * �f�[�^���f�����ύX����邽�тɒʒm����郊�X�g�Ƀ��X�i�[��ǉ����܂��B
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (changeSupport == null)
			changeSupport = new SwingPropertyChangeSupport(this);
		changeSupport
				.addPropertyChangeListener(SCHEDULE_MODEL_CHANGE, listener);
	}

	/**
	 * �f�[�^���f�����ύX����邽�тɒʒm����郊�X�g���烊�X�i�[���폜���܂��B
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (changeSupport == null)
			return;
		changeSupport.removePropertyChangeListener(
				SCHEDULE_MODEL_CHANGE,
				listener);
	}

	/**
	 * ���X�i�[�Ƀo�E���Y�v���p�e�B�ύX�C�x���g��ʒm���܂��B
	 * 
	 * @param oldValue �ύX�O�̒l
	 * @param newValue �ύX��̒l
	 */
	public void firePropertyChange(Object oldValue, Object newValue) {
		if (changeSupport == null)
			return;
		changeSupport.firePropertyChange(
				SCHEDULE_MODEL_CHANGE,
				oldValue,
				newValue);
	}

	/**
	 * �O���[�vNo ��Ԃ��܂�
	 */
	public int getGroupNo() {
		return 0;
	}

	/**
	 * �O���[�v��ǉ����܂��B
	 * 
	 * @param dataProvider
	 * @param dataHolder
	 * @param flagHolder
	 */
	public void addGroup(
			String dataProvider,
			String dataHolder,
			String flagHolder) {
	}

	/**
	 * �ҏW����ׂ̃_�C�A���O��Ԃ��܂��B
	 * 
	 * @param window �e�E�B���h�E
	 * @param collection �x�[�X�N���X�̃C���X�^���X
	 * @param �C�ӂ̃p�����[�^���X�g
	 * @todo �C�ӂ̃p�����[�^�͂��������A�^����������ׂ������B
	 */
	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		throw new UnsupportedOperationException();
	}

	/**
	 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��Ԃ��܂��B
	 */
	public Point getPoint() {
		throw new UnsupportedOperationException();
	}

	/**
	 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��ݒ肵�܂��B
	 */
	public void setPoint(Point point) {
		throw new UnsupportedOperationException();
	}

	/**
	 * ���̃V���{���̕ҏW�\�t���O��ݒ肵�܂��B
	 * 
	 * @param editable �ҏW�\�ɂ���Ȃ� true �������łȂ���� false ��ݒ肵�܂��B
	 */
	public void setEditable(boolean[] editable) {
	}

	/**
	 * ���̃V���{�����ҏW�\���ǂ�����Ԃ��܂��B
	 * 
	 * @return �ҏW�\�ȏꍇ�� true �������łȂ���� false ��Ԃ��܂��B
	 */
	public boolean isEditable() {
		// TODO AccessControlable#checkPermission�Ō�������
		return true;
	}

	/*
	 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
	 */
	public String[] getDestinations() {
		return new String[0];
	}

	/**
	 * �������ݐ�̒ǉ��͂��Ȃ��B
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
	 */
	public void addDestination(Map atts) {
	}

	/**
	 * �������ݐ�̒ǉ��͂��Ȃ��B
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
	 */
	public void addValueSetter(ValueSetter setter) {
	}

	/**
	 * �O���[�v���̂�Ԃ��܂�
	 * 
	 * @return �O���[�v���̂�Ԃ��܂�
	 */
	public String getGroupName() {
		return dataSchedule.getGroupName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.schedule.ScheduleModel#disConnect()
	 */
	public void disConnect() {
	}

	public void duplicateGroup(int[] dest) {
	}

	public void duplicateWeekOfDay(int src, int[] dest) {
	}

	public GroupElement[] getGroupNames() {
		return new GroupElement[0];
	}

	public int getTopSize() {
		return dataSchedule.getTopSize();
	}

	public WifeDataSchedule getDataSchedule() {
		return dataSchedule;
	}

	public void setDataSchedule(WifeDataSchedule schedule) {
		dataSchedule = schedule;
	}
}
