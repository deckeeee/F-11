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
 */

package org.F11.scada.xwife.applet;

import java.io.Serializable;

/**
 * �c���[�̃m�[�h��\���N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageTreeNode implements Serializable {
	private static final long serialVersionUID = -5343871777897236533L;
	/** �c���[�̃m�[�h���̂ł� */
	private String nodeName = "";
	/** �m�[�h���̂ɑ΂���y�[�W�L�[�ł� */
	private String key = "";

	/**
	 * �f�t�H���g�R���X�g���N�^
	 */
	public PageTreeNode() {
		this("", "");
	}

	/**
	 * �m�[�h�I�u�W�F�N�g�����������܂�
	 * @param nodeName �m�[�h����
	 * @param key �y�[�W�L�[
	 */
	public PageTreeNode(String nodeName, String key) {
		this.nodeName = nodeName;
		this.key = key;
	}

	/**
	 * ���̃m�[�h�I�u�W�F�N�g�̃m�[�h���̂�Ԃ��܂�
	 * @return �m�[�h����
	 */
	public String toString() {
		return nodeName;
	}

	/**
	 * ���̃m�[�h�I�u�W�F�N�g�̃y�[�W�L�[��Ԃ��܂�
	 * @return �y�[�W�L�[
	 */
	public String getKey() {
		return key;
	}
}
