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

import org.F11.scada.server.register.HolderString;

public class SeriesProperties {
	/** �V���[�Y�̃C���f�b�N�X */
	private int index;
	/** �\���E��\�� */
	private Boolean visible;
	/** �F */
	private Color color;
	/** �@��ԍ� */
	private String unit;
	/** �@�햼�� */
	private String name;
	/** �Q�ƒl */
	private Float referenceValue;
	/** ���ݒl */
	private Float nowValue;
	/** �P�ʋL�� */
	private String unitMark;
	/** �ڐ����l�̃t�H�[�}�b�g */
	private String verticalFormat;
	/** �\���ő�l */
	private float max;
	/** �\���ŏ��l */
	private float min;
	/** ���ݒl�̃z���_ID */
	private HolderString holderString;

	/**
	 * �V���[�Y�̃C���f�b�N�X
	 * 
	 * @return �V���[�Y�̃C���f�b�N�X
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * �V���[�Y�̃C���f�b�N�X��ݒ肵�܂�
	 * 
	 * @param index �V���[�Y�̃C���f�b�N�X
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * �V���[�Y�̐F
	 * 
	 * @return �V���[�Y�̐F
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * �V���[�Y�̐F��ݒ肵�܂��B
	 * 
	 * @param color �V���[�Y�̐F
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * �c�X�P�[���̖ڐ����l�\���t�H�[�}�b�g
	 * 
	 * @return �c�X�P�[���̖ڐ����l�\���t�H�[�}�b�g
	 */
	public String getVerticalFormat() {
		return verticalFormat;
	}

	/**
	 * �c�X�P�[���̖ڐ����l�\���t�H�[�}�b�g��ݒ�
	 * 
	 * @param verticalFormat �c�X�P�[���̖ڐ����l�\���t�H�[�}�b�g
	 */
	public void setVerticalFormat(String verticalFormat) {
		this.verticalFormat = verticalFormat;
	}

	/**
	 * �V���[�Y�̕\���E��\��
	 * 
	 * @return �V���[�Y�̕\���E��\��
	 */
	public Boolean isVisible() {
		return visible;
	}

	/**
	 * �V���[�Y�̕\���E��\����ݒ�
	 * 
	 * @param visible �V���[�Y�̕\���E��\��
	 */
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	/**
	 * �@��ԍ�
	 * 
	 * @return �@��ԍ�
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * �@��ԍ���ݒ�
	 * 
	 * @param unit �@��ԍ�
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * �@�햼��
	 * 
	 * @return �@�햼��
	 */
	public String getName() {
		return name;
	}

	/**
	 * �@�햼�̂�ݒ�
	 * 
	 * @param name �@�햼��
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * �Q�ƒl
	 * 
	 * @return �Q�ƒl
	 */
	public Float getReferenceValue() {
		return referenceValue;
	}

	/**
	 * �Q�ƒl��ݒ�
	 * 
	 * @param referenceValue �Q�ƒl
	 */
	public void setReferenceValue(Float referenceValue) {
		this.referenceValue = referenceValue;
	}

	/**
	 * ���ݒl
	 * 
	 * @return ���ݒl
	 */
	public Float getNowValue() {
		return nowValue;
	}

	/**
	 * ���ݒl��ݒ�
	 * 
	 * @param nowValue ���ݒl
	 */
	public void setNowValue(Float nowValue) {
		this.nowValue = nowValue;
	}

	/**
	 * �P�ʖ���
	 * 
	 * @return �P�ʖ���
	 */
	public String getUnitMark() {
		return unitMark;
	}

	/**
	 * �P�ʖ��̂�ݒ�
	 * 
	 * @param unitMark �P�ʖ���
	 */
	public void setUnitMark(String unitMark) {
		this.unitMark = unitMark;
	}

	/**
	 * �V���[�Y�̕\���ő�l
	 * 
	 * @return �V���[�Y�̕\���ő�l
	 */
	public float getMax() {
		return max;
	}

	/**
	 * �V���[�Y�̕\���ő�l��ݒ�
	 * 
	 * @param max �V���[�Y�̕\���ő�l
	 */
	public void setMax(float max) {
		this.max = max;
	}

	/**
	 * �V���[�Y�̕\���ŏ��l
	 * 
	 * @return �V���[�Y�̕\���ŏ��l
	 */
	public float getMin() {
		return min;
	}

	/**
	 * �V���[�Y�̕\���ŏ��l��ݒ�
	 * 
	 * @param min �V���[�Y�̕\���ŏ��l
	 */
	public void setMin(float min) {
		this.min = min;
	}

	/**
	 * ���̃V���[�Y���Q�Ƃ��Ă���z���_��Ԃ��܂�
	 * 
	 * @return ���̃V���[�Y���Q�Ƃ��Ă���z���_��Ԃ��܂�
	 */
	public HolderString getHolderString() {
		return holderString;
	}

	/**
	 * ���̃V���[�Y���Q�Ƃ��Ă���z���_��ݒ肵�܂�
	 * 
	 * @param holderString ���̃V���[�Y���Q�Ƃ��Ă���z���_��ݒ肵�܂�
	 */
	public void setHolderString(HolderString holderString) {
		this.holderString = holderString;
	}
}
