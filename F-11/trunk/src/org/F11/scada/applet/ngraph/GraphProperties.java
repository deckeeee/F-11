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
import org.F11.scada.server.register.HolderString;

/**
 * �g�����h�O���t�̃v���p�e�B�B
 * 
 * @author maekawa
 * 
 */
public class GraphProperties {
	private final PropertyChangeSupport changeSupport;
	private int horizontalForAllSpanMode = 112;
	private int horizontalForSelectSpanMode = 168;
	private int horizontalCount = 5;
	private long horizontalLineSpan = 18000000L;
	private String dateFormat = "%1$tm/%1$td";
	private String timeFormat = "%1$tH:%1$tM";
	private int verticalScale = 48;
	private int verticalCount = 10;
	private int verticalLine;
	private int scalePixcelSize = 5;
	private Insets insets = new Insets(50, 80, 60, 50);
	private Font font = new Font("Monospaced", Font.PLAIN, 18);
	private Color lineColor = Color.WHITE;
	private Color backGround = new Color(0, 0, 135);
	private Color verticalScaleColor = new Color(64, 95, 237);
	/** �V���[�Y�O���[�v�̃��X�g */
	private List<SeriesGroup> seriesGroups;
	private int groupNo;

	public GraphProperties() {
		changeSupport = new PropertyChangeSupport(this);
		seriesGroups = getTestGroup();
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
	public void addSeriesGroup(SeriesGroup s) {
		if (null == seriesGroups) {
			seriesGroups = new ArrayList<SeriesGroup>();
		}
		seriesGroups.add(s);
	}

	/**
	 * �V���[�Y���O���[�v����폜���܂��B
	 * 
	 * @param groupNo �O���[�vNo.
	 */
	public void removeSeriesGroup(int groupNo) {
		if (null != seriesGroups) {
			seriesGroups.remove(groupNo);
		}
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

	public SeriesGroup getTestData() {
		List<SeriesProperties> serieses = new ArrayList<SeriesProperties>();
		serieses.add(getProperty(
			true,
			Color.yellow,
			"",
			"�{�ݓ��@��d�@�d��",
			null,
			null,
			"A",
			"%3.0f",
			0,
			100,
			0,
			"P1_D_500_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.magenta,
			"",
			"�{�ݓ��@��d�@�d��",
			null,
			null,
			"V",
			"%04.0f",
			0,
			90,
			1,
			"P1_D_501_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.cyan,
			"",
			"�{�ݓ��@��d�@�d��",
			null,
			null,
			"kW",
			"%3.1f",
			0,
			90,
			2,
			"P1_D_502_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.red,
			"",
			"�{�ݓ��@��d�@�����d��",
			null,
			null,
			"kVar",
			"%3.2f",
			0,
			100,
			3,
			"P1_D_503_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.green,
			"",
			"�{�ݓ��@��d�@�͗�",
			null,
			null,
			null,
			"%03.1f",
			0,
			100,
			4,
			"P1_D_504_BcdSingle"));
		serieses.add(getProperty(
			false,
			Color.white,
			"UNIT-06",
			"���O06",
			null,
			null,
			null,
			"%04.1f",
			0,
			100,
			5,
			"P1_D_506_BcdSingle"));
		return new SeriesGroup("�{�ݓ��@�d��", serieses);
	}

	private SeriesProperties getProperty(
			boolean visible,
			Color color,
			String unit,
			String name,
			Float refValue,
			Float nowValue,
			String unitMark,
			String verticalFormat,
			float min,
			float max,
			int index,
			String holderString) {
		SeriesProperties p = new SeriesProperties();
		p.setVisible(visible);
		p.setColor(color);
		p.setUnit(unit);
		p.setName(name);
		p.setReferenceValue(refValue);
		p.setNowValue(nowValue);
		p.setUnitMark(unitMark);
		p.setVerticalFormat(verticalFormat);
		p.setMin(min);
		p.setMax(max);
		p.setIndex(index);
		p.setHolderString(getHolderString(holderString));
		return p;
	}

	private HolderString getHolderString(String holderString) {
		return null == holderString ? null : new HolderString(holderString);
	}

	private SeriesGroup getTestData2() {
		List<SeriesProperties> serieses = new ArrayList<SeriesProperties>();
		serieses.add(getProperty(
			true,
			Color.yellow,
			"",
			"�����@��d�@�d��",
			null,
			null,
			"kA",
			"%3.0f",
			0,
			2000,
			0,
			"P1_D_3300_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.magenta,
			"",
			"�����@��d�@�d��",
			null,
			null,
			"V",
			"%04.0f",
			0,
			2000,
			1,
			"P1_D_3301_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.cyan,
			"",
			"�����@��d�@�d��",
			null,
			null,
			"kW",
			"%3.1f",
			0,
			5000,
			2,
			"P1_D_3307_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.red,
			"",
			"�����@��d�@�����d��",
			null,
			null,
			"kVar",
			"%3.2f",
			0,
			5000,
			3,
			"P1_D_3310_BcdSingle"));
		serieses.add(getProperty(
			true,
			Color.green,
			"",
			"�����@��d�@�͗�",
			null,
			null,
			null,
			"%03.1f",
			0,
			60,
			4,
			"P1_D_3316_BcdSingle"));
		serieses.add(getProperty(
			false,
			Color.white,
			"UNIT-06",
			"���O06",
			null,
			null,
			null,
			"%04.1f",
			0,
			100,
			5,
			"P1_D_3316_BcdSingle"));
		return new SeriesGroup("�����@�d��", serieses);
	}

	private List<SeriesGroup> getTestGroup() {
		ArrayList<SeriesGroup> l = new ArrayList<SeriesGroup>();
		l.add(getTestData());
		l.add(getTestData2());
		return l;
	}
}
