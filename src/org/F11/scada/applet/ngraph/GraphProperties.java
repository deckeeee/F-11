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

package org.F11.scada.applet.ngraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.F11.scada.applet.ngraph.model.GraphModel;

/**
 * �g�����h�O���t�̃v���p�e�B�B�g�����h�v���p�e�B��ǉ������ꍇ�́A�G�f�B�^�p�̃N���XTrend3Data��TrendRuleSet���ύX���ĉ������B
 *
 * @author maekawa
 *
 */
public class GraphProperties {
	/** ���̃v���p�e�B�[�̃��X�i�[ */
	private final PropertyChangeSupport changeSupport;
	/** ���ّS�\���̉����s�N�Z���� */
	private int horizontalForAllSpanMode;
	/** ���ٗ��\���̉����s�N�Z���� */
	private int horizontalForSelectSpanMode;
	/** ���ڐ��̐� */
	private int horizontalCount;
	/** ���ڐ��̎��ԃX�P�[���� */
	private long horizontalLineSpan;
	/** ���t�\���t�H�[�}�b�g */
	private String dateFormat;
	/** ���ԕ\���t�H�[�}�b�g */
	private String timeFormat;
	/** �c�ڐ�1���̃s�N�Z���� */
	private int verticalScale;
	/** �c�ڐ��̐� */
	private int verticalCount;
	/** �c�ڐ��̃s�N�Z���� */
	private int verticalLine;
	/** �ڐ����̃s�N�Z���� */
	private int scalePixcelSize;
	/** �������[�h�c�ڐ��̐� */
	private int compositionVerticalCount;
	/** �O���t�G���A�̗]�� */
	private Insets insets;
	/** �g�p�t�H���g */
	private Font font;
	/** ���̐F */
	private Color lineColor;
	/** �w�i�F */
	private Color backGround;
	/** �O���t�G���A�̃O���b�h�F */
	private Color verticalScaleColor;
	/** �V���[�Y�O���[�v�̃��X�g */
	private List<SeriesGroup> seriesGroups;
	/** �J�����g�O���[�vNo. */
	private int groupNo;
	/** �y�[�W��`�t�@�C���� */
	private String pagefile;
	/** ���X�P�[���ύX�{�^���̃v���p�e�B�[ */
	private List<HorizontalScaleButtonProperty> horizontalScaleButtonProperty;
	/** �g�����h�O���t�ő�\�����R�[�h */
	private int maxRecord;
	/** �c�[���o�[�\���̗L�� */
	private boolean isVisibleToolbar;
	/** �V���[�Y�\���̗L�� */
	private boolean isVisibleSeries;
	/** �X�e�[�^�X�\���̗L�� */
	private boolean isVisibleStatus;
	/** �X�N���[���o�[�\���̗L�� */
	private boolean isVisibleScroolbar;
	/** �Q�ƈʒu���\���̗L�� */
	private boolean isVisibleReferenceLine;
	/** �c�X�P�[�������\���̗L�� */
	private boolean isVisibleVerticalString;
	/** ���݂̍����E�����\�����[�h */
	private boolean isCompositionMode = true;
	/** ���݂̃X�p���\�����[�h */
	private boolean isAllSpanDisplayMode;
	/** �g�����h����{�^���̕\��/��\�� */
	private boolean isShowTrendOpButton;
	/** �O�X�P�[���\�����A�c�X�P�[���̊Ԋu(�����l56) */
	private int verticalLineInterval;

	public GraphProperties() {
		changeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListeners() {
		PropertyChangeListener[] l = changeSupport.getPropertyChangeListeners();
		for (PropertyChangeListener listener : l) {
			changeSupport.removePropertyChangeListener(listener);
		}
	}

	/**
	 * �c�ڐ��̃s�N�Z�������擾���܂��B
	 *
	 * @return �c�ڐ��̃s�N�Z����
	 */
	public int getVerticalLine() {
		if (verticalLine == 0) {
			verticalLine = verticalScale * compositionVerticalCount;
		}
		return verticalLine;
	}

	/**
	 * �c�ڐ�1���̃s�N�Z�������擾���܂��B
	 *
	 * @return �c�ڐ�1���̃s�N�Z����
	 */
	public int getVerticalScale() {
		return verticalScale;
	}

	/**
	 * �c�ڐ�1���̃s�N�Z������ݒ肵�܂��B
	 *
	 * @param verticalScale �c�ڐ�1���̃s�N�Z����
	 */
	public void setVerticalScale(int verticalScale) {
		this.verticalScale = verticalScale;
	}

	/**
	 * �c�ڐ��̐����擾���܂��B
	 *
	 * @return �c�ڐ��̐�
	 */
	public int getVerticalCount() {
		return verticalCount;
	}

	/**
	 * �c�ڐ��̐���ݒ肵�܂��B
	 *
	 * @param verticalCount �c�ڐ��̐�
	 */
	public void setVerticalCount(int verticalCount) {
		this.verticalCount = verticalCount;
	}

	/**
	 * �������[�h�c�ڐ��̐����擾���܂��B
	 *
	 * @return �������[�h�c�ڐ��̐�
	 */
	public int getCompositionVerticalCount() {
		return compositionVerticalCount;
	}

	/**
	 * �������[�h�c�ڐ��̐���ݒ肵�܂��B
	 *
	 * @param compositionVerticalCount �������[�h�c�ڐ��̐�
	 */
	public void setCompositionVerticalCount(int compositionVerticalCount) {
		this.compositionVerticalCount = compositionVerticalCount;
	}

	/**
	 * �����̑��s�N�Z��
	 *
	 * @param isAllSpanDisplayMode �X�p���S�\�����[�h�Ȃ� true �𗪕\�����[�h�Ȃ� false ���w��
	 *
	 * @return �����̑��s�N�Z��
	 */
	public int getHorizontalLine(boolean isAllSpanDisplayMode) {
		return getHorizontalScale(isAllSpanDisplayMode) * horizontalCount;
	}

	/**
	 * �ڐ����̃s�N�Z�������擾���܂��B
	 *
	 * @return �ڐ����̃s�N�Z����
	 */
	public int getScalePixcelSize() {
		return scalePixcelSize;
	}

	/**
	 * �ڐ����̃s�N�Z������ݒ肵�܂��B
	 *
	 * @param scalePixcelSize �ڐ����̃s�N�Z����
	 */
	public void setScalePixcelSize(int scalePixcelSize) {
		this.scalePixcelSize = scalePixcelSize;
	}

	/**
	 * ���ڐ��̎��ԃX�P�[�������擾���܂��B
	 *
	 * @return ���ڐ��̎��ԃX�P�[����
	 */
	public long getHorizontalLineSpan() {
		return horizontalLineSpan;
	}

	/**
	 * ���ڐ��̎��ԃX�P�[������ݒ肵�܂��B
	 *
	 * @param horizontalLineSpan ���ڐ��̎��ԃX�P�[����
	 */
	public void setHorizontalLineSpan(long horizontalLineSpan) {
		this.horizontalLineSpan = horizontalLineSpan;
	}

	/**
	 * ����1�ڐ��̒���(�s�N�Z��)
	 *
	 * @param isAllSpanDisplayMode
	 *
	 * @return ����1�ڐ��̒���(�s�N�Z��)
	 */
	public int getHorizontalScale(boolean isAllSpanDisplayMode) {
		return isAllSpanDisplayMode
			? horizontalForAllSpanMode
			: horizontalForSelectSpanMode;
	}

	/**
	 * ���ّS�\���̉����s�N�Z������ݒ肵�܂��B
	 *
	 * @param horizontalForAllSpanMode ���ّS�\���̉����s�N�Z����
	 */
	public void setHorizontalForAllSpanMode(int horizontalForAllSpanMode) {
		this.horizontalForAllSpanMode = horizontalForAllSpanMode;
	}

	/**
	 * ���ٗ��\���̉����s�N�Z������ݒ肵�܂��B
	 *
	 * @param horizontalForSelectSpanMode ���ٗ��\���̉����s�N�Z����
	 */
	public void setHorizontalForSelectSpanMode(int horizontalForSelectSpanMode) {
		this.horizontalForSelectSpanMode = horizontalForSelectSpanMode;
	}

	/**
	 * ���ڐ��̐����擾���܂��B
	 *
	 * @return ���ڐ��̐�
	 */
	public int getHorizontalCount() {
		return horizontalCount;
	}

	/**
	 * ���ڐ��̐���ݒ肵�܂��B
	 *
	 * @param horizontalCount ���ڐ��̐�
	 */
	public void setHorizontalCount(int horizontalCount) {
		this.horizontalCount = horizontalCount;
	}

	/**
	 * ���t�\���t�H�[�}�b�g���擾���܂��B
	 *
	 * @return ���t�\���t�H�[�}�b�g
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * ���t�\���t�H�[�}�b�g��ݒ肵�܂��B
	 *
	 * @param dateFormat ���t�\���t�H�[�}�b�g
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * ���ԕ\���t�H�[�}�b�g���擾���܂��B
	 *
	 * @return ���ԕ\���t�H�[�}�b�g
	 */
	public String getTimeFormat() {
		return timeFormat;
	}

	/**
	 * ���ԕ\���t�H�[�}�b�g��ݒ肵�܂��B
	 *
	 * @param timeFormat ���ԕ\���t�H�[�}�b�g
	 */
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * �O���t�G���A�̗]�����擾���܂��B
	 *
	 * @return �O���t�G���A�̗]��
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * �O���t�G���A�̗]����ݒ肵�܂��B
	 *
	 * @param insets �O���t�G���A�̗]��
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	/**
	 * �g�p�t�H���g���擾���܂��B
	 *
	 * @return �g�p�t�H���g
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * �g�p�t�H���g��ݒ肵�܂��B
	 *
	 * @param font �g�p�t�H���g
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * ���̐F���擾���܂��B
	 *
	 * @return ���̐F
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * ���̐F��ݒ肵�܂��B
	 *
	 * @param lineColor ���̐F
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * �w�i�F���擾���܂��B
	 *
	 * @return �w�i�F
	 */
	public Color getBackGround() {
		return backGround;
	}

	/**
	 * �w�i�F��ݒ肵�܂��B
	 *
	 * @param backGround �w�i�F
	 */
	public void setBackGround(Color backGround) {
		this.backGround = backGround;
	}

	/**
	 * �O���t�G���A�̃O���b�h�F���擾���܂��B
	 *
	 * @return �O���t�G���A�̃O���b�h�F
	 */
	public Color getVerticalScaleColor() {
		return verticalScaleColor;
	}

	/**
	 * �O���t�G���A�̃O���b�h�F��ݒ肵�܂��B
	 *
	 * @param verticalScaleColor �O���t�G���A�̃O���b�h�F
	 */
	public void setVerticalScaleColor(Color verticalScaleColor) {
		this.verticalScaleColor = verticalScaleColor;
	}

	/**
	 * �V���[�Y���O���[�v�ɒǉ����܂��B
	 *
	 * @param s �V���[�Y���O���[�v�ɒǉ����܂��B
	 */
	public void setSeriesGroup(List<SeriesGroup> s) {
		seriesGroups = new ArrayList<SeriesGroup>(s);
	}

	/**
	 * �J�����g�O���[�vNo.�̃V���[�Y�O���[�v��Ԃ��܂��B
	 *
	 * @return �J�����g�O���[�vNo.�̃V���[�Y�O���[�v��Ԃ��܂��B
	 */
	public SeriesGroup getSeriesGroup() {
		return seriesGroups.get(groupNo);
	}

	/**
	 * �V���[�Y�O���[�v�̃��X�g���擾���܂��B
	 *
	 * @return �V���[�Y�O���[�v�̃��X�g
	 */
	public List<SeriesGroup> getSeriesGroups() {
		return Collections.unmodifiableList(seriesGroups);
	}

	/**
	 * �J�����g�O���[�vNo.���擾���܂��B
	 *
	 * @return �J�����g�O���[�vNo.
	 */
	public int getGroupNo() {
		return groupNo;
	}

	/**
	 * �J�����g�O���[�vNo.��ݒ肵�܂��B
	 *
	 * @param groupNo �J�����g�O���[�vNo.
	 */
	public boolean setGroupNo(int groupNo) {
		boolean validGroupNo = isValidGroupNo(groupNo);
		if (validGroupNo) {
			int old = this.groupNo;
			this.groupNo = groupNo;
			changeSupport.firePropertyChange(
				GraphModel.GROUP_CHANGE,
				old,
				this.groupNo);
		}
		return validGroupNo;
	}

	private boolean isValidGroupNo(int groupNo) {
		return 0 <= groupNo && seriesGroups.size() - 1 >= groupNo;
	}

	/**
	 * �y�[�W��`�t�@�C�������擾���܂��B
	 *
	 * @return �y�[�W��`�t�@�C����
	 */
	public String getPagefile() {
		return pagefile;
	}

	/**
	 * �y�[�W��`�t�@�C������ݒ肵�܂��B
	 *
	 * @param pagefile �y�[�W��`�t�@�C����
	 */
	public void setPagefile(String pagefile) {
		this.pagefile = pagefile;
	}

	/**
	 * ���ّS�\���̉����s�N�Z�������擾���܂��B
	 *
	 * @return ���ّS�\���̉����s�N�Z����
	 */
	public int getHorizontalForAllSpanMode() {
		return horizontalForAllSpanMode;
	}

	/**
	 * ���ٗ��\���̉����s�N�Z�������擾���܂��B
	 *
	 * @return ���ٗ��\���̉����s�N�Z����
	 */
	public int getHorizontalForSelectSpanMode() {
		return horizontalForSelectSpanMode;
	}

	/**
	 * ���X�P�[���ύX�{�^���̃v���p�e�B�[���擾���܂��B
	 *
	 * @return ���X�P�[���ύX�{�^���̃v���p�e�B�[
	 */
	public List<HorizontalScaleButtonProperty> getHorizontalScaleButtonProperty() {
		return horizontalScaleButtonProperty;
	}

	/**
	 * ���X�P�[���ύX�{�^���̃v���p�e�B�[��ݒ肵�܂��B
	 *
	 * @param horizontalScaleButtonProperty ���X�P�[���ύX�{�^���̃v���p�e�B�[
	 */
	public void setHorizontalScaleButtonProperty(List<HorizontalScaleButtonProperty> horizontalScaleButtonProperty) {
		this.horizontalScaleButtonProperty = horizontalScaleButtonProperty;
	}

	/**
	 * �g�����h�O���t�ő�\�����R�[�h���擾���܂��B
	 *
	 * @return �g�����h�O���t�ő�\�����R�[�h
	 */
	public int getMaxRecord() {
		return maxRecord;
	}

	/**
	 * �g�����h�O���t�ő�\�����R�[�h��ݒ肵�܂��B
	 *
	 * @param maxRecord �g�����h�O���t�ő�\�����R�[�h
	 */
	public void setMaxRecord(int maxRecord) {
		this.maxRecord = maxRecord;
	}

	/**
	 * �c�[���o�[�\���̗L�����擾���܂��B
	 *
	 * @return �c�[���o�[�\���̗L��
	 */
	public boolean isVisibleToolbar() {
		return isVisibleToolbar;
	}

	/**
	 * �c�[���o�[�\���̗L����ݒ肵�܂��B
	 *
	 * @param isVisibleToolbar �c�[���o�[�\���̗L��
	 */
	public void setVisibleToolbar(boolean isVisibleToolbar) {
		this.isVisibleToolbar = isVisibleToolbar;
	}

	/**
	 * �V���[�Y�\���̗L�����擾���܂��B
	 *
	 * @return �V���[�Y�\���̗L��
	 */
	public boolean isVisibleSeries() {
		return isVisibleSeries;
	}

	/**
	 * �V���[�Y�\���̗L����ݒ肵�܂��B
	 *
	 * @param isVisibleSeries �V���[�Y�\���̗L��
	 */
	public void setVisibleSeries(boolean isVisibleSeries) {
		this.isVisibleSeries = isVisibleSeries;
	}

	/**
	 * �X�e�[�^�X�\���̗L�����擾���܂��B
	 *
	 * @return �X�e�[�^�X�\���̗L��
	 */
	public boolean isVisibleStatus() {
		return isVisibleStatus;
	}

	/**
	 * �X�e�[�^�X�\���̗L����ݒ肵�܂��B
	 *
	 * @param isVisibleStatus �X�e�[�^�X�\���̗L��
	 */
	public void setVisibleStatus(boolean isVisibleStatus) {
		this.isVisibleStatus = isVisibleStatus;
	}

	/**
	 * �X�N���[���o�[�\���̗L�����擾���܂��B
	 *
	 * @return �X�N���[���o�[�\���̗L��
	 */
	public boolean isVisibleScroolbar() {
		return isVisibleScroolbar;
	}

	/**
	 * �X�N���[���o�[�\���̗L����ݒ肵�܂��B
	 *
	 * @param isVisibleScroolbar �X�N���[���o�[�\���̗L��
	 */
	public void setVisibleScroolbar(boolean isVisibleScroolbar) {
		this.isVisibleScroolbar = isVisibleScroolbar;
	}

	/**
	 * �Q�ƈʒu���\���̗L�����擾���܂��B
	 *
	 * @return �Q�ƈʒu���\���̗L��
	 */
	public boolean isVisibleReferenceLine() {
		return isVisibleReferenceLine;
	}

	/**
	 * �Q�ƈʒu���\���̗L����ݒ肵�܂��B
	 *
	 * @param isVisibleReferenceLine �Q�ƈʒu���\���̗L��
	 */
	public void setVisibleReferenceLine(boolean isVisibleReferenceLine) {
		this.isVisibleReferenceLine = isVisibleReferenceLine;
	}

	/**
	 * �c�X�P�[�������\���̗L�����擾���܂��B
	 *
	 * @return �c�X�P�[�������\���̗L��
	 */
	public boolean isVisibleVerticalString() {
		return isVisibleVerticalString;
	}

	/**
	 * �c�X�P�[�������\���̗L����ݒ肵�܂��B
	 *
	 * @param isVisibleVerticalString �c�X�P�[�������\���̗L��
	 */
	public void setVisibleVerticalString(boolean isVisibleVerticalString) {
		this.isVisibleVerticalString = isVisibleVerticalString;
	}

	/**
	 * ���݂̍����E�����\�����[�h���擾���܂��B
	 *
	 * @return ���݂̍����E�����\�����[�h
	 */
	public boolean isCompositionMode() {
		return isCompositionMode;
	}

	/**
	 * ���݂̍����E�����\�����[�h��ݒ肵�܂��B
	 *
	 * @param isCompositionMode ���݂̍����E�����\�����[�h
	 */
	public void setCompositionMode(boolean isCompositionMode) {
		this.isCompositionMode = isCompositionMode;
	}

	/**
	 * ���݂̃X�p���\�����[�h���擾���܂��B
	 *
	 * @return ���݂̃X�p���\�����[�h
	 */
	public boolean isAllSpanDisplayMode() {
		return isAllSpanDisplayMode;
	}

	/**
	 * ���݂̃X�p���\�����[�h��ݒ肵�܂��B
	 *
	 * @param isAllSpanDisplayMode ���݂̃X�p���\�����[�h
	 */
	public void setAllSpanDisplayMode(boolean isAllSpanDisplayMode) {
		this.isAllSpanDisplayMode = isAllSpanDisplayMode;
	}

	/**
	 * �g�����h����{�^���̕\��/��\�����擾���܂��B
	 *
	 * @return �g�����h����{�^���̕\��/��\��
	 */
	public boolean isShowTrendOpButton() {
		return isShowTrendOpButton;
	}

	/**
	 * �g�����h����{�^���̕\��/��\����ݒ肵�܂��B
	 *
	 * @param isShowTrendOpButton �g�����h����{�^���̕\��/��\��
	 */
	public void setShowTrendOpButton(boolean isShowTrendOpButton) {
		this.isShowTrendOpButton = isShowTrendOpButton;
	}

	/**
	 * �c�X�P�[���̕\���Ԋu��Ԃ��܂�
	 *
	 * @return �c�X�P�[���̕\���Ԋu��Ԃ��܂�
	 */
	public int getVerticalLineInterval() {
		return verticalLineInterval;
	}

	/**
	 * �c�X�P�[���̕\���Ԋu��ݒ肵�܂�
	 *
	 * @param verticalLineInterval �c�X�P�[���̕\���Ԋu��ݒ肵�܂�
	 */
	public void setVerticalLineInterval(int verticalLineInterval) {
		this.verticalLineInterval = verticalLineInterval;
	}

}
