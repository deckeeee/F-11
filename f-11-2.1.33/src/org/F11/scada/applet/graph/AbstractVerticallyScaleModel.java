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
import java.awt.FontMetrics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.log4j.Logger;

/**
 * �X�P�[���f�[�^���f���̊��N���X�ł��B
 * SwingPropertyChangeSupport �ɂ��C�x���g�f���Q�[�V�������������܂��B
 */
public abstract class AbstractVerticallyScaleModel implements VerticallyScaleModel, PropertyChangeListener {
	/** ���M���OAPI */
	protected static Logger logger = Logger.getLogger(AbstractVerticallyScaleModel.class);
	/** �v���p�e�B�C�x���g�� */
	private static final String PROPERTY_NAME = "SCALE_CHANGEED";
	/** �v���p�e�B�`�F���W�T�|�[�g */
	private SwingPropertyChangeSupport property;
	/** ��ʃR���|�[�l���g */
	protected JComponent comp;
	/** �O���t�v���p�e�B���f�� */
	protected GraphPropertyModel graphPropertyModel;
	/** �V���[�Y */
	protected int series;

	/** �X�P�[���������̉��� */
	protected static final int scaleOneWidth = 10;
	/** �X�P�[���ڐ���̕����� */
	protected String[] scaleStrings;
	/** �X�P�[���ڐ���̕�����̍ő啝 */
	protected int maxStringWidth;
	/** �X�P�[���ڐ���̕�����̍ő卂 */
	protected int maxStringHeight;
	/** �X�P�[���ő�X�p���ύX�̗L�� */
	protected boolean isMaxEnable = true;
	/** �X�P�[���ŏ��X�p���ύX�̗L�� */
	protected boolean isMinEnable = true;

	/**
	 * �R���X�g���N�^
	 * @param comp ��ʃR���|�[�l���g
	 * @param graphPropertyModel �O���t�v���p�e�B���f��
	 * @param series �V���[�Y
	 */
	public AbstractVerticallyScaleModel(JComponent comp,
									   GraphPropertyModel graphPropertyModel,
									   int series) {
		this.comp = comp;
		this.graphPropertyModel = graphPropertyModel;
		this.graphPropertyModel.addPropertyChangeListener(this);
		this.graphPropertyModel.addPropertyChangeListener(GraphPropertyModel.GROUP_CHANGE_EVENT, this);
		this.series = series;
		logger.debug("initialize.");
	}

	/**
	 * �R���|�[�l���g�̏������������������܂��B
	 */
	abstract protected void calcSize();

	/**
	 * PropertyChangeListener �����X�i�[���X�g�ɒǉ����܂��B
	 * @param listener �ǉ����� PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (property == null) {
			property = new SwingPropertyChangeSupport(this);
		}
		property.addPropertyChangeListener(PROPERTY_NAME, listener);
	}

	/**
	 * PropertyChangeListener �����X�i�[���X�g����폜���܂��B
	 * @param listener �폜���� PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (property == null) {
			return;
		}
		property.removePropertyChangeListener(PROPERTY_NAME, listener);
	}

	/**
	 * �o�^����Ă��郊�X�i�[�ɁA�o�E���h�v���p�e�B�̍X�V��ʒm���܂��B
	 * �ȑO�̒l�ƐV�����l���������� null �łȂ��ꍇ�A�C�x���g�̓g���K����܂���B
	 * @param oldValue �v���p�e�B�̌Â��l
	 * @param newValue �v���p�e�B�̐V�����l
	 */
	public void firePropertyChange(Object oldValue, Object newValue) {
		if (property == null) {
			return;
		}
		property.firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}

	/**
	 * �ڐ���̕���Ԃ��܂��B
	 * @return �ڐ���̕�
	 */
	public int getScaleOneWidth() {
		return scaleOneWidth;
	}

	/**
	 * �ڐ���̍�����Ԃ��܂��B
	 * @return �ڐ���̍���
	 */
	public int getScaleOneHeight() {
		return graphPropertyModel.getVerticalScaleHeight();
	}

	/**
	 * �ڐ���̑�����Ԃ��܂��B
	 * @return �ڐ���̑���
	 */
	public int getScaleCount() {
		return graphPropertyModel.getVerticalScaleCount();
	}

	/**
	 * �X�P�[���̍ŏ��l��Ԃ��܂��B
	 * @return �X�P�[���̍ŏ��l
	 */
	public double getScaleMin() {
		return graphPropertyModel.getVerticalMinimum(series);
	}

	/**
	 * �X�P�[���̍ő�l��Ԃ��܂��B
	 * @return �X�P�[���̍ő�l
	 */
	public double getScaleMax() {
		return graphPropertyModel.getVerticalMaximum(series);
	}

	/**
	 * �X�P�[���̍ŏ��l��ݒ肵�܂��B
	 * @param min �X�P�[���̍ŏ��l
	 */
	public void setScaleMin(double min) {
		graphPropertyModel.setVerticalMinimum(series, min);
		calcSize();
	}

	/**
	 * �X�P�[���̍ő�l��ݒ肵�܂��B
	 * @param max �X�P�[���̍ő�l
	 */
	public void setScaleMax(double max) {
		graphPropertyModel.setVerticalMaximum(series, max);
		calcSize();
	}

	/**
	 * �X�P�[���ڐ��蕶����̔z���Ԃ��܂��B
	 * @return �X�P�[���ڐ��蕶����̔z��
	 */
	public String[] getScaleStrings() {
		if (scaleStrings == null) {
			calcSize();
		}
		return scaleStrings;
	}

	/**
	 * �X�P�[���ڐ��蕶����̍ő啝��Ԃ��܂��B
	 * @param isLeft �����X�P�[�����ǂ���
	 * @return �X�P�[���ڐ��蕶����̍ő啝
	 */
	public int getScaleStringMaxWidth(boolean isLeft) {
		if (maxStringWidth == 0) {
			calcSize();
		}
		FontMetrics metrics = comp.getFontMetrics(comp.getFont());
		return isLeft ? maxStringWidth + metrics.stringWidth("  ") : maxStringWidth;
	}

	/**
	 * �X�P�[���ڐ��蕶����̍ő卂��Ԃ��܂��B
	 * @return �X�P�[���ڐ��蕶����̍ő卂
	 */
	public int getScaleStringMaxHeight() {
		if (maxStringHeight == 0) {
			calcSize();
		}
		return maxStringHeight;
	}

	/**
	 * �X�P�[���ڐ��蕶����̐F��Ԃ��܂��B
	 * @return �X�P�[���ڐ��蕶����̐F
	 */
	public Color getScaleStringColor() {
		return graphPropertyModel.getColors()[series];
	}

	/**
	 * �`��G���A�̃C���Z�b�c��Ԃ��܂�
	 * @return �`��G���A�̃C���Z�b�c
	 */
	public Insets getScaleInsets() {
		return graphPropertyModel.getInsets();
	}

	/**
	 * �X�P�[���ő�X�p���ύX�̗L��
	 * @return �X�P�[���ő�X�p���ύX���ł���ꍇ�� true�A�����łȂ��ꍇ�� false
	 */
	public boolean isMaxEnable() {
		return isMaxEnable;
	}

	/**
	 * �X�P�[���ŏ��X�p���ύX�̗L��
	 * @return �X�P�[���ŏ��X�p���ύX���ł���ꍇ�� true�A�����łȂ��ꍇ�� false
	 */
	public boolean isMinEnable() {
		return isMinEnable;
	}

	/**
	 * �X�P�[���ő�X�p���ύX�̗L����ݒ肵�܂��B
	 * @param isMaxEnable �ύX�\�ɂ���ꍇ�� true�A�ύX�s�ɂ���ꍇ�� false
	 */
	public void setMaxEnable(boolean isMaxEnable) {
		this.isMaxEnable = isMaxEnable;
	}

	/**
	 * �X�P�[���ŏ��X�p���ύX�̗L����ݒ肵�܂��B
	 * @param isMinEnable �ύX�\�ɂ���ꍇ�� true�A�ύX�s�ɂ���ꍇ�� false
	 */
	public void setMinEnable(boolean isMinEnable) {
		this.isMinEnable = isMinEnable;
	}

	/**
	 * �o�E���h�v���p�e�B�̕ύX���ɌĂяo����܂��B
	 * @param evt �C�x���g�\�[�X����ѕύX�����v���p�e�B��
	 * �L�q���� PropertyChangeEvent �I�u�W�F�N�g
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		calcSize();
		firePropertyChange(evt.getOldValue(), evt.getNewValue());
	}

	/**
	 * �X�P�[���ɑΉ�����V���[�Y��Ԃ��܂��B
	 * �Ή�����V���[�Y�����݂��Ȃ��ꍇ��-1��Ԃ��܂��B
	 * 
	 * @return �X�P�[���ɑΉ�����V���[�Y��Ԃ��܂��B�Ή�����V���[�Y�����݂��Ȃ��ꍇ��-1��Ԃ��܂��B
	 */
	public int getSeries() {
		int max = graphPropertyModel.getSeriesSize();
		return (max <= series || 0 > series) ? -1 : series;
	}
	
	/**
	 * �f�[�^�v���o�C�_����Ԃ��܂�
	 * @return �f�[�^�v���o�C�_����Ԃ��܂�
	 */
	public String getDataProviderName() {
		return graphPropertyModel.getDataProviderName(series);
	}
	
	/**
	 * �f�[�^�z���_����Ԃ��܂�
	 * @return �f�[�^�z���_����Ԃ��܂�
	 */
	public String getDataHolderName() {
		return graphPropertyModel.getDataHolderName(series);
	}
}
