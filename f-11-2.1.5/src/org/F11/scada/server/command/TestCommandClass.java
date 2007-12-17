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

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command�����̃e�X�g�N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TestCommandClass implements Command {
	/** �v���p�e�B�[ */
	private String key;
	/** �v���p�e�B�[ */
	private String name;

	/**
	 * �f�t�H���g�R���X�g���N�^
	 */	
	public TestCommandClass() {}

	/**
	 * �f�[�^�ύX�C�x���g�������ɁA���s������e���������܂��B
	 */
	public void execute(DataValueChangeEventKey evt) {
		System.out.println(this);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * �v���p�e�B�[��getter
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * �v���p�e�B�[��getter
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * �v���p�e�B�[��setter
	 * @param string
	 */
	public void setKey(String string) {
		key = string;
	}

	/**
	 * �v���p�e�B�[��setter
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

}
