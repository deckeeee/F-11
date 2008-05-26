/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmNewsConfig.java,v 1.2.4.2 2005/07/05 10:26:39 hori Exp $
 * $Revision: 1.2.4.2 $
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
 * �ŐV���̐ݒ�N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmNewsConfig {
	/** �f�t�H���g�̔w�i�F */
	private static final Color DEFAULT_BACKGROUND = ColorFactory.getColor("lightgrey");
	/** �t�H���g�ݒ� */
	private FontConfig fontConfig;
	/** �w�i�F */
	private String backGround;
	/** �s����` */
	private LineCountConfig lineCountConfig = new LineCountConfig(5, 1);

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
	 * �w�i�F��Ԃ��܂��B
	 * 
	 * @return �w�i�F��Ԃ��܂�
	 */
	public String getBackGround() {
		return backGround;
	}

	/**
	 * �w�i�F��ݒ肵�܂��B
	 * 
	 * @param color
	 *            �w�i�F��ݒ肵�܂�
	 */
	public void setBackGround(String color) {
		this.backGround = color;
	}

	/**
	 * �w�i�F�� Color �I�u�W�F�N�g��Ԃ��܂��B
	 * 
	 * @return �w�i�F�� Color �I�u�W�F�N�g��Ԃ��܂�
	 */
	public Color getBackGroundColor() {
		return ColorFactory.getColor(backGround) == null ? DEFAULT_BACKGROUND : ColorFactory
				.getColor(backGround);
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

}
