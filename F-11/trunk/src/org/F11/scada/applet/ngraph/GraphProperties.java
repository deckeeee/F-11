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
import org.F11.scada.applet.symbol.ColorFactory;

/**
 * �g�����h�O���t�̃v���p�e�B�B
 * 
 * @author maekawa
 * 
 */
public class GraphProperties {
	/** ���̃v���p�e�B�[�̃��X�i�[ */
	private final PropertyChangeSupport changeSupport;
	/** ���ݑS�\���̉����s�N�Z���� */
	private int horizontalForAllSpanMode = 112;
	/** ���ݗ��\���̉����s�N�Z���� */
	private int horizontalForSelectSpanMode = 168;
	/** ���ڐ��̐� */
	private int horizontalCount = 5;
	/** ���ڐ��̎��ԃX�P�[���� */
	private long horizontalLineSpan = 18000000L;
	/** ���t�\���t�H�[�}�b�g */
	private String dateFormat = "%1$tm/%1$td";
	/** ���ԕ\���t�H�[�}�b�g */
	private String timeFormat = "%1$tH:%1$tM";
	/** �c�ڐ�1���̃s�N�Z���� */
	private int verticalScale = 48;
	/** �c�ڐ��̐� */
	private int verticalCount = 10;
	/** �c�ڐ��̃s�N�Z���� */
	private int verticalLine;
	/** �ڐ����̃s�N�Z���� */
	private int scalePixcelSize = 5;
	/** �O���t�G���A�̗]�� */
	private Insets insets = new Insets(50, 80, 60, 50);
	/** �g�p�t�H���g */
	private Font font = new Font("Monospaced", Font.PLAIN, 18);
	/** ���̐F */
	private Color lineColor = Color.WHITE;
	/** �w�i�F */
	private Color backGround = ColorFactory.getColor("navy");
	/** �O���t�G���A�̃O���b�h�F */
	private Color verticalScaleColor = ColorFactory.getColor("cornflowerblue");
	/** �V���[�Y�O���[�v�̃��X�g */
	private List<SeriesGroup> seriesGroups;
	/** �J�����g�O���[�vNo. */
	private int groupNo;
	/** �y�[�W��`�t�@�C���� */
	private String pagefile;


	public GraphProperties() {
		changeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * �c���̑��s�N�Z�� verticalScale �~ verticalCount
	 * 
	 * @return �c���̑��s�N�Z�� verticalScale �~ verticalCount
	 */
	public int getVerticalLine() {
		if (verticalLine == 0) {
			verticalLine = verticalScale * verticalCount;
		}
		return verticalLine;
	}

	/**
	 * �c1�ڐ��̃s�N�Z����
	 * 
	 * @return �c1�ڐ��̃s�N�Z����
	 */
	public int getVerticalScale() {
		return verticalScale;
	}

	/**
	 * �c1�ڐ��̃s�N�Z����
	 * 
	 * @param verticalScale �c1�ڐ��̃s�N�Z����
	 */
	public void setVerticalScale(int verticalScale) {
		this.verticalScale = verticalScale;
	}

	/**
	 * �c�ڐ��̐�(�ʏ��10)
	 * 
	 * @return�@�c�ڐ��̐�(�ʏ��10)
	 */
	public int getVerticalCount() {
		return verticalCount;
	}

	/**
	 * �c�ڐ��̐�(�ʏ��10)
	 * 
	 * @param verticalCount �c�ڐ��̐�(�ʏ��10)
	 */
	public void setVerticalCount(int verticalCount) {
		this.verticalCount = verticalCount;
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
	 * �c���X�P�[���ڐ����g�̃s�N�Z����
	 * 
	 * @return �c���X�P�[���ڐ����g�̃s�N�Z����
	 */
	public int getScalePixcelSize() {
		return scalePixcelSize;
	}

	/**
	 * �c���X�P�[���ڐ����g�̃s�N�Z������ݒ�
	 * 
	 * @param scalePixcelSize �c���X�P�[���ڐ����g�̃s�N�Z����
	 */
	public void setScalePixcelSize(int scalePixcelSize) {
		this.scalePixcelSize = scalePixcelSize;
	}

	/**
	 * ���X�P�[���̕\�����Ԃ��~���b�ŕԂ��܂��B
	 * 
	 * @return ���X�P�[���̕\�����Ԃ��~���b�ŕԂ��܂��B
	 */
	public long getHorizontalLineSpan() {
		return horizontalLineSpan;
	}

	/**
	 * ���X�P�[���̕\�����Ԃ��~���b�Őݒ肵�܂��B
	 * 
	 * @param horizontalLineSpan ���X�P�[���̕\�����Ԃ��~���b�Őݒ肵�܂��B
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
	 * �X�p���S�\���̉����̖ڐ��ЂƂ��s�N�Z����
	 * 
	 * @param horizontalForAllSpanMode �X�p���S�\���̉����̖ڐ��ЂƂ��s�N�Z����
	 */
	public void setHorizontalForAllSpanMode(int horizontalForAllSpanMode) {
		this.horizontalForAllSpanMode = horizontalForAllSpanMode;
	}

	/**
	 * �X�p�����\���̉����̖ڐ��ЂƂ��s�N�Z����
	 * 
	 * @param horizontalForSelectSpanMode �X�p�����\���̉����̖ڐ��ЂƂ��s�N�Z����
	 */
	public void setHorizontalForSelectSpanMode(int horizontalForSelectSpanMode) {
		this.horizontalForSelectSpanMode = horizontalForSelectSpanMode;
	}

	/**
	 * �����̖ڐ���
	 * 
	 * @return �����̖ڐ���
	 */
	public int getHorizontalCount() {
		return horizontalCount;
	}

	/**
	 * �����̖ڐ���
	 * 
	 * @param horizontalCount �����̖ڐ���
	 */
	public void setHorizontalCount(int horizontalCount) {
		this.horizontalCount = horizontalCount;
	}

	/**
	 * ��i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������ �t�H�[�}�b�g��{@link java.util.Formatter}�𗘗p���Ă��܂��B
	 * 
	 * @return ��i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * ��i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������
	 * 
	 * @param dateFormat ��i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * ���i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������ �t�H�[�}�b�g��{@link java.util.Formatter}�𗘗p���Ă��܂��B
	 * 
	 * @return ���i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������
	 */
	public String getTimeFormat() {
		return timeFormat;
	}

	/**
	 * ���i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������
	 * 
	 * @param timeFormat ���i�̉��X�P�[���P�ʂ̃t�H�[�}�b�g������
	 */
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}

	/**
	 * �O���t����̗]��
	 * 
	 * @return �O���t����̗]��
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * �O���t����̗]��
	 * 
	 * @param insets �O���t����̗]��
	 */
	public void setInsets(Insets insets) {
		this.insets = insets;
	}

	/**
	 * �O���t�Ɏg�p����t�H���g
	 * 
	 * @return �O���t�Ɏg�p����t�H���g
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * �O���t�Ɏg�p����t�H���g
	 * 
	 * @param font �O���t�Ɏg�p����t�H���g
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * ���ɂȂ�c���̐��̐F�B�����̕�����̐F�ɂ��g�p����B
	 * 
	 * @return ���ɂȂ�c���̐��̐F�B�����̕�����̐F�ɂ��g�p����B
	 */
	public Color getLineColor() {
		return lineColor;
	}

	/**
	 * ���ɂȂ�c���̐��̐F�B�����̕�����̐F�ɂ��g�p����B
	 * 
	 * @param lineColor ���ɂȂ�c���̐��̐F�B�����̕�����̐F�ɂ��g�p����B
	 */
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	/**
	 * �O���t�̔w�i�F
	 * 
	 * @return �O���t�̔w�i�F
	 */
	public Color getBackGround() {
		return backGround;
	}

	/**
	 * �O���t�̔w�i�F
	 * 
	 * @param backGround �O���t�̔w�i�F
	 */
	public void setBackGround(Color backGround) {
		this.backGround = backGround;
	}

	/**
	 * �ڐ����(�_��)�̐F
	 * 
	 * @return �ڐ����(�_��)�̐F
	 */
	public Color getVerticalScaleColor() {
		return verticalScaleColor;
	}

	/**
	 * �ڐ����(�_��)�̐F
	 * 
	 * @param verticalScaleColor �ڐ����(�_��)�̐F
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
	 * �V���[�Y�O���[�v�̃��X�g��Ԃ��܂��B
	 * 
	 * @return �V���[�Y�O���[�v���X�g��Ԃ��܂��B
	 */
	public List<SeriesGroup> getSeriesGroups() {
		return Collections.unmodifiableList(seriesGroups);
	}

	/**
	 * �J�����g�O���[�vNo.��Ԃ��܂��B
	 * 
	 * @return �J�����g�O���[�vNo.��Ԃ��܂��B
	 */
	public int getGroupNo() {
		return groupNo;
	}

	/**
	 * �J�����g�O���[�vNo.��ݒ肵�܂��B
	 * 
	 * @param groupNo �O���[�vNo.
	 * @return �O���[�vNo.��ύX�ł����ꍇ�� true �������łȂ��ꍇ�� false ��Ԃ��܂��B
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

	public String getPagefile() {
		return pagefile;
	}

	public void setPagefile(String pagefile) {
		this.pagefile = pagefile;
	}

	public int getHorizontalForAllSpanMode() {
		return horizontalForAllSpanMode;
	}

	public int getHorizontalForSelectSpanMode() {
		return horizontalForSelectSpanMode;
	}
}
