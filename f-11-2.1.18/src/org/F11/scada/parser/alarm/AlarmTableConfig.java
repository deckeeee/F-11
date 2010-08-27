/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmTableConfig.java,v 1.3.2.2 2005/07/05 10:26:39 hori Exp $
 * $Revision: 1.3.2.2 $
 * $Date: 2005/07/05 10:26:39 $
 * 
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
 */

package org.F11.scada.parser.alarm;

import java.awt.Color;

import org.F11.scada.applet.symbol.ColorFactory;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * �x��E��Ԉꗗ�̐ݒ�N���X�ł�
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmTableConfig {
	/** �f�t�H���g�̃e�[�u���w�i�F */
	private static final Color DEFAULT_BACKGROUND = ColorFactory.getColor("white");
	/** �f�t�H���g�̃w�b�_�[�w�i�F */
	private static final Color DEFAULT_HEADERBACKGROUND = ColorFactory.getColor("lightgrey");
	/** �f�t�H���g�̃w�b�_�[�����F */
	private static final Color DEFAULT_HEADERFOREGROUND = ColorFactory.getColor("black");
	/** �t�H���g�ݒ� */
	private FontConfig fontConfig;
	/** �e�[�u���w�i�F */
	private String backGround;
	/** �w�b�_�[�w�i�F */
	private String headerBackGround;
	/** �w�b�_�[�����F */
	private String headerForeGround;
	/** �f�t�H���g�\���̃^�u�ԍ� (0�`2) */
	private int defaultTabNo;
	/** �s����` */
	private LineCountConfig lineCountConfig = new LineCountConfig(20, 0);

	/**
	 * �t�H���g�ݒ��Ԃ��܂�
	 * 
	 * @return �t�H���g�ݒ�
	 */
	public FontConfig getFontConfig() {
		return fontConfig;
	}

	/**
	 * �t�H���g�ݒ��ݒ肵�܂�
	 * 
	 * @param config
	 *            �t�H���g�ݒ�
	 */
	public void setFontConfig(FontConfig config) {
		fontConfig = config;
	}

	/**
	 * �e�[�u���w�i�F��Ԃ��܂��B
	 * 
	 * @return �e�[�u���w�i�F��Ԃ��܂�
	 */
	public String getBackGround() {
		return backGround;
	}

	/**
	 * �e�[�u���w�i�F��ݒ肵�܂��B
	 * 
	 * @param color
	 *            �e�[�u���w�i�F
	 */
	public void setBackGround(String color) {
		this.backGround = color;
	}

	/**
	 * �w�b�_�[�w�i�F��Ԃ��܂��B
	 * 
	 * @return �w�b�_�[�w�i�F��Ԃ��܂�
	 */
	public String getHeaderBackGround() {
		return headerBackGround;
	}

	/**
	 * �w�b�_�[�w�i�F��ݒ肵�܂��B
	 * 
	 * @param color
	 *            �w�b�_�[�w�i�F
	 */
	public void setHeaderBackGround(String color) {
		this.headerBackGround = color;
	}

	/**
	 * �w�b�_�[�����F��Ԃ��܂��B
	 * 
	 * @return �w�b�_�[�����F��Ԃ��܂�
	 */
	public String getHeaderForeGround() {
		return headerForeGround;
	}

	/**
	 * �w�b�_�[�����F��ݒ肵�܂��B
	 * 
	 * @param color
	 *            �w�b�_�[�����F
	 */
	public void setHeaderForeGround(String color) {
		this.headerForeGround = color;
	}

	/**
	 * �e�[�u���w�i�F�� Color �I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @return �e�[�u���w�i�F�� Color �I�u�W�F�N�g��Ԃ��܂��B
	 */
	public Color getBackGroundColor() {
		return ColorFactory.getColor(backGround) == null ? DEFAULT_BACKGROUND : ColorFactory
				.getColor(backGround);
	}

	/**
	 * �w�b�_�[�w�i�F�� Color �I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @return �w�b�_�[�w�i�F�� Color �I�u�W�F�N�g��Ԃ��܂��B
	 */
	public Color getHeaderBackGroundColor() {
		return ColorFactory.getColor(headerBackGround) == null
				? DEFAULT_HEADERBACKGROUND
				: ColorFactory.getColor(headerBackGround);
	}

	/**
	 * �w�b�_�[�����F�� Color �I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @return �w�b�_�[�����F�� Color �I�u�W�F�N�g��Ԃ��܂��B
	 */
	public Color getHeaderForeGroundColor() {
		return ColorFactory.getColor(headerForeGround) == null
				? DEFAULT_HEADERFOREGROUND
				: ColorFactory.getColor(headerForeGround);
	}

	/**
	 * �s����`��Ԃ��܂��B
	 * 
	 * @return
	 */
	public LineCountConfig getLineCountConfig() {
		return lineCountConfig;
	}

	/**
	 * �s����`��ݒ肵�܂��B
	 * 
	 * @param lineCountConfig
	 */
	public void setLineCountConfig(LineCountConfig lineCountConfig) {
		this.lineCountConfig = lineCountConfig;
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂�
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * �f�t�H���g�\���̃^�u�ԍ���Ԃ��܂�
	 * 
	 * @return �f�t�H���g�\���̃^�u�ԍ�
	 */
	public int getDefaultTabNo() {
		return defaultTabNo;
	}

	/**
	 * �f�t�H���g�\���̃^�u�ԍ���ݒ肵�܂��B 0 <= i <= 2 �Őݒ肷��K�v������܂��B���͈̔͊O�̐��l���n���ꂽ�ꍇ�́A0 ��ݒ肵�܂��B
	 * 
	 * @param i
	 *            �\���^�u�ԍ� (0�`2)
	 */
	public void setDefaultTabNo(int i) {
		defaultTabNo = i;
	}

}
