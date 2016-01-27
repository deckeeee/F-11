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

package org.F11.scada.applet.schedule;

import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.SwingPropertyChangeSupport;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;

/**
 * �X�P�W���[�����f���̎����N���X�ł��B
 */
public class DefaultScheduleModel implements ScheduleModel,
		DataReferencerOwner, DataValueChangeListener {
	/** DataHolder�^�C�v���ł��B */
	private static final Class[][] WIFE_TYPE_INFO =
		new Class[][] { { DataHolder.class, WifeData.class } };
	/** �o�E���Y�v���p�e�B�ł��B */
	private SwingPropertyChangeSupport changeSupport;
	/** �X�P�W���[���f�[�^�ł� */
	private WifeDataSchedule dataSchedule;
	/** ��������p�X�P�W���[���f�[�^�ł� */
	private WifeDataSchedule undoDataSchedule;
	/** �f�[�^���t�@�����T�̃��X�g�ł� */
	private List dataReferencerList;
	/** �Q�ƒ��̃��t�@�����T�C���f�b�N�X�ł� */
	private int referencerIndex;
	/** �ҏW�\�t���O�̔z��ł� */
	private boolean[] isEditable;
	/** �F�؃R���g���[���̎Q�� */
	private Authenticationable authentication;
	/** �ύX�t���O�f�[�^���t�@�����T�̃��X�g�ł� */
	private List flagReferencerList;

	/** �X�P�W���[�����f���ύX�C�x���g�� */
	private static final String SCHEDULE_MODEL_CHANGE = "SCHEDULE_MODEL_CHANGE";
	/** �ύX�t���O���s�v�ȏꍇ�̃_�~�[���t�@�����T */
	private static final DataReferencer NON_FLAG = new DataReferencer("", "");
	/** Logging API */
	private final Logger logger = Logger.getLogger(DefaultScheduleModel.class);

	/**
	 * �R���X�g���N�^
	 */
	public DefaultScheduleModel() {
		this(null);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param authentication �F�؃R���g���[���̎Q��
	 */
	public DefaultScheduleModel(Authenticationable authentication) {
		this.authentication = authentication;
		init();
	}

	private void init() {
		dataReferencerList = new ArrayList();
		flagReferencerList = new ArrayList();
		referencerIndex = -1;
		if (authentication != null) {
			authentication.addEditable(this);
		}
	}

	/**
	 * �f�[�^�ύX�C�x���g����
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		Object o = evt.getSource();
		if (!(o instanceof DataHolder)) {
			return;
		}

		if (0 <= referencerIndex) {
			if (!isEditing()) {
				setGroupNo(referencerIndex);
			}
		}
	}

	/**
	 * DataHolder�^�C�v����Ԃ��܂��B
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}

	/**
	 * �I�u�W�F�N�g�ɒl��ݒ肵�܂��B
	 * 
	 * @exception RemoteException
	 */
	public void setValue() {
		writeSchedule();
		writeFlag();
	}

	private void writeSchedule() {
		DataReferencer rf =
			(DataReferencer) dataReferencerList.get(referencerIndex);
		DataHolder dh = rf.getDataHolder();
		dh.setValue(dataSchedule, new Date(), WifeQualityFlag.GOOD);
		try {
			dh.syncWrite();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void writeFlag() {
		DataReferencer rf =
			(DataReferencer) flagReferencerList.get(referencerIndex);
		if (rf != NON_FLAG) {
			DataHolder dh = rf.getDataHolder();
			WifeDataDigital d = (WifeDataDigital) dh.getValue();
			dh.setValue(d.valueOf(true), new Date(), WifeQualityFlag.GOOD);
			try {
				dh.syncWrite();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * �f�[�^���ҏW���ꂽ�ꍇ�� true ��Ԃ��܂��B
	 */
	public boolean isEditing() {
		return !dataSchedule.equals(undoDataSchedule);
	}

	/**
	 * �ێ����Ă���X�P�W���[���f�[�^���X�V���܂��B
	 */
	public void writeData() {
		undoDataSchedule =
			(WifeDataSchedule) dataSchedule.valueOf(dataSchedule.toByteArray());
	}

	/**
	 * �ێ����Ă���X�P�W���[���f�[�^���A���h�D���܂��B
	 */
	public void undoData() {
		dataSchedule =
			(WifeDataSchedule) undoDataSchedule.valueOf(undoDataSchedule
				.toByteArray());
		firePropertyChange(null, null);
	}

	/**
	 * �X�P�W���[���f�[�^�̃O���[�v���w�肵���C���f�b�N�X�ɕύX���܂��B
	 * 
	 * @param index �O���[�v�C���f�b�N�X
	 */
	public void setGroupNo(int index) {
		DataReferencer rf = (DataReferencer) dataReferencerList.get(index);
		DataHolder dh = rf.getDataHolder();
		if (dh == null) {
			logger.error(rf.getDataProviderName()
				+ "_"
				+ rf.getDataHolderName()
				+ "���o�^����Ă��܂���B");
		}
		WifeDataSchedule schedule = (WifeDataSchedule) dh.getValue();
		undoDataSchedule =
			(WifeDataSchedule) schedule.valueOf(schedule.toByteArray());
		dataSchedule =
			(WifeDataSchedule) schedule.valueOf(schedule.toByteArray());
		referencerIndex = index;
		firePropertyChange(null, null);
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
		return dataReferencerList.size();
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
		// return dataSchedule.getGroupNo();
		return referencerIndex;
	}

	/**
	 * �O���[�v��ǉ����܂��B
	 * 
	 * @param dataProvider
	 * @param dataHolder
	 */
	private void addGroup(String dataProvider, String dataHolder) {
		DataReferencer dr = new DataReferencer(dataProvider, dataHolder);
		dataReferencerList.add(dr);
		dr.connect(this);
		if (referencerIndex < 0) {
			setGroupNo(0);
		}
	}

	public void addGroup(
			String dataProvider,
			String dataHolder,
			String flagHolder) {
		addGroup(dataProvider, dataHolder);
		if (AttributesUtil.isSpaceOrNull(flagHolder)) {
			flagReferencerList.add(NON_FLAG);
		} else {
			int index = flagHolder.indexOf("_");
			String provider = flagHolder.substring(0, index);
			String holder = flagHolder.substring(index + 1);
			DataReferencer dr = new DataReferencer(provider, holder);
			flagReferencerList.add(dr);
			dr.connect(this);
		}
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
			java.util.List para) {
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
		isEditable = editable;
	}

	/**
	 * ���̃V���{�����ҏW�\���ǂ�����Ԃ��܂��B
	 * 
	 * @return �ҏW�\�ȏꍇ�� true �������łȂ���� false ��Ԃ��܂��B
	 */
	public boolean isEditable() {
		if (isEditable != null) {
			return isEditable[referencerIndex];
		}
		return false;
	}

	/*
	 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
	 */
	public String[] getDestinations() {
		String[] ret = new String[dataReferencerList.size()];
		for (int i = 0; i < ret.length; i++) {
			DataReferencer dr = (DataReferencer) dataReferencerList.get(i);
			StringBuffer buffer = new StringBuffer();
			buffer.append(dr.getDataProviderName());
			buffer.append("_");
			buffer.append(dr.getDataHolderName());
			ret[i] = buffer.toString();
		}
		return ret;
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
		for (Iterator i = dataReferencerList.iterator(); i.hasNext();) {
			DataReferencer dr = (DataReferencer) i.next();
			dr.disconnect(this);
		}
		dataReferencerList.clear();

		for (Iterator i = flagReferencerList.iterator(); i.hasNext();) {
			DataReferencer dr = (DataReferencer) i.next();
			dr.disconnect(this);
		}
		flagReferencerList.clear();

		removePropertyChangeListeners();
		if (null != authentication) {
			authentication.removeEditable(this);
		}
	}

	public void removePropertyChangeListeners() {
		if (changeSupport != null) {
			PropertyChangeListener[] listeners =
				changeSupport.getPropertyChangeListeners();
			for (int i = 0; i < listeners.length; i++) {
				changeSupport.removePropertyChangeListener(listeners[i]);
			}
		}
	}

	public void duplicateGroup(int[] dest) {
		try {
			for (int i = 0; i < dest.length; i++) {
				DataReferencer ref =
					(DataReferencer) dataReferencerList.get(dest[i]);
				DataHolder dh = ref.getDataHolder();
				if (dh == null) {
					throw new NullPointerException(
						"not registered dataholder : "
							+ ref.getDataProviderName()
							+ "_"
							+ ref.getDataHolderName());
				}
				WifeDataSchedule src = (WifeDataSchedule) dh.getValue();
				dh.setValue(
					dataSchedule.duplicate(src),
					new Date(),
					WifeQualityFlag.GOOD);
				dh.syncWrite();
			}
		} catch (DataProviderDoesNotSupportException e) {
			e.printStackTrace();
		}
	}

	public void duplicateWeekOfDay(int src, int[] dest) {
		int maxNumber = dataSchedule.getNumberSize();
		int[] onTimes = new int[maxNumber];
		int[] offTimes = new int[maxNumber];
		for (int i = 0; i < maxNumber; i++) {
			onTimes[i] = dataSchedule.getOnTime(src, i);
			offTimes[i] = dataSchedule.getOffTime(src, i);
		}
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < maxNumber; j++) {
				dataSchedule = dataSchedule.setOnTime(dest[i], j, onTimes[j]);
				dataSchedule = dataSchedule.setOffTime(dest[i], j, offTimes[j]);
			}
		}
		setValue();
		firePropertyChange(null, null);
	}

	public GroupElement[] getGroupNames() {
		ArrayList ret = new ArrayList(dataReferencerList.size());
		for (int i = 0, size = dataReferencerList.size(); i < size; i++) {
			if (isEditable[i] && referencerIndex != i) {
				DataReferencer dr = (DataReferencer) dataReferencerList.get(i);
				DataHolder dh = dr.getDataHolder();
				WifeDataSchedule data = (WifeDataSchedule) dh.getValue();
				ret.add(new GroupElement(i, data.getGroupName()));
			}
		}
		return (GroupElement[]) ret.toArray(new GroupElement[0]);
	}

	public String toString() {
		return "referencerIndex="
			+ referencerIndex
			+ " ,dataSchedule="
			+ dataSchedule.toString();
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
	
	public boolean isVisible() {
		return true;
	}
}
