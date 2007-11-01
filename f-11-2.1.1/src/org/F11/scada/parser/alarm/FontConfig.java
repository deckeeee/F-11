/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/FontConfig.java,v 1.1 2003/10/23 09:58:34 frdm Exp $
 * $Revision: 1.1 $
 * $Date: 2003/10/23 09:58:34 $
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

import java.awt.Font;
import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * �t�H���g����ێ�����ݒ�JavaBean�N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class FontConfig {
	/** �t�H���g�X�^�C���̕�����ƒ萔�̃}�b�v�ł� */
	private static HashMap styleMap = new HashMap();
	/** �t�H���g��� */
	private String type;
	/** �t�H���g�|�C���g�� */
	private int point;
	/** �t�H���g�X�^�C�� */
	private String style;
	
	static {
		styleMap.put("PLAIN", new Integer(Font.PLAIN));
		styleMap.put("BOLD", new Integer(Font.BOLD));
		styleMap.put("ITALIC", new Integer(Font.ITALIC));
		styleMap.put("BOLD+ITALIC", new Integer(Font.BOLD + Font.ITALIC));
	};

	/**
	 * �t�H���g�|�C���g����Ԃ��܂�
	 * @return �t�H���g�|�C���g��
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * �t�H���g�X�^�C����Ԃ��܂�
	 * @return �t�H���g�X�^�C��
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * �t�H���g�^�C�v��Ԃ��܂�
	 * @return �t�H���g�^�C�v
	 */
	public String getType() {
		return type;
	}

	/**
	 * �t�H���g�|�C���g����ݒ肵�܂�
	 * @param i �t�H���g�|�C���g��
	 */
	public void setPoint(int i) {
		point = i;
	}

	/**
	 * �t�H���g�X�^�C����ݒ肵�܂�
	 * @param i �t�H���g�X�^�C��
	 */
	public void setStyle(String string) {
		style = string;
	}

	/**
	 * �t�H���g�^�C�v��ݒ肵�܂�
	 * @param string �t�H���g�^�C�v
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * ���̃I�u�W�F�N�g�̕��������Ԃ��܂��B
	 * jakarta commons Lang, ToStringBuilder�̎����Ɉˑ����Ă��܂��B
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * �t�H���g�X�^�C����Font�Œ�`����Ă���萔�ŕԂ��܂��B
	 * ��`����Ă��Ȃ������񂪎w�肳�ꂽ�ꍇ�́AFont.PLAIN��Ԃ��܂��B
	 * @return Font�Œ�`����Ă���萔
	 */
	private int getFontStyle() {
		String key = style.toUpperCase();
		return styleMap.containsKey(key)
			? ((Integer) styleMap.get(key)).intValue()
			: Font.PLAIN;
	}
	
	/**
	 * �v���p�e�B��萶�������t�H���g��Ԃ��܂��B
	 * @return �v���p�e�B��萶�������t�H���g��Ԃ��܂��B
	 */
	public Font getFont() {
		return new Font(type, getFontStyle(), point);
	}
}
