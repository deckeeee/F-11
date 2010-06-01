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

package org.F11.scada.server.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Command��`�N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CommandConfig implements ClassConfigContainer {
	/** �v���o�C�_���ł� */
	private String provider;
	/** �z���_���ł� */
	private String holder;
	/** �R�}���h�N�� Class ��`�̃��X�g */
	private ArrayList classConfigs = new ArrayList();
	
	/**
	 * �z���_����Ԃ��܂�
	 * @return �z���_��
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * �v���o�C�_����Ԃ��܂�
	 * @return �v���o�C�_��
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * �z���_����ݒ肵�܂�
	 * @param string �z���_��
	 */
	public void setHolder(String string) {
		holder = string;
	}

	/**
	 * �v���o�C�_����ݒ肵�܂�
	 * @param string �v���o�C�_��
	 */
	public void setProvider(String string) {
		provider = string;
	}
	
	/**
	 * �R�}���h�N�� Class ��`��ǉ����܂�
	 * @param config �R�}���h�N�� Class ��`
	 */
	public void addClassConfig(ClassConfig config) {
		classConfigs.add(config);
	}
	
	/**
	 * �R�}���h�N�� Class ��`�̃��X�g��Ԃ��܂�
	 * @return �R�}���h�N�� Class ��`�̃��X�g
	 */
	public List getdClassConfigs() {
		return Collections.unmodifiableList(classConfigs);
	}
}
