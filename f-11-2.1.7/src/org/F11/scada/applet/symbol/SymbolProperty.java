package org.F11.scada.applet.symbol;

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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

/**
 * �V���{���̃v���p�e�B�[��\���N���X�ł��B
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class SymbolProperty implements CompositeProperty {
	private Map propertys = new HashMap();

	/**
	 * ��̃v���p�e�B�𐶐����܂�
	 */
	public SymbolProperty() {
	}

	/**
	 * ������ Attributes �I�u�W�F�N�g�Ńv���p�e�B�𐶐����܂�
	 * 
	 * @param atts Attributes �I�u�W�F�N�g
	 */
	public SymbolProperty(Attributes atts) {
		for (int i = 0; i < atts.getLength(); i++) {
			propertys.put(atts.getQName(i), atts.getValue(i));
		}
	}

	/**
	 * �v���p�e�B��ݒ肵�܂��B
	 */
	public void setProperty(String key, String param) {
		propertys.put(key, param);
	}

	/**
	 * �v���p�e�B��ݒ肵�܂��B �ŉ��ʂ̃v���p�e�B�ׁ̈A�������Ȃ��B
	 * 
	 * @param property �R���|�W�b�g�p�^�[��
	 */
	public void addCompositeProperty(CompositeProperty property) {
		throw new UnsupportedOperationException();
	}

	/**
	 * �v���p�e�B���擾���܂��B
	 * 
	 * @param key �v���p�e�B�̖���
	 */
	public String getProperty(String key) {
		return (String) propertys.get(key);
	}

	/**
	 * �v���p�e�B���擾���܂��B
	 * 
	 * @param key �v���p�e�B�̖���
	 * @param init �v���p�e�B�����݂��Ȃ��������Ɏg�p����l
	 */
	public String getProperty(String key, String init) {
		if (propertys.containsKey(key)) {
			return (String) propertys.get(key);
		} else {
			return init;
		}
	}
}
