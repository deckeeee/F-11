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

package org.F11.scada;


/**
 * ���ݒ�}�l�[�W���[�ł��BRMI, JDBC ���̃T�[�o�[�����Ǘ����܂��B
 */
public class EnvironmentManager {
	/** �V���O���g���p�^�[���ł��B */
    private static final EnvironmentManager instance = new EnvironmentManager();
	private final EnvironmentManagerStrategy strategy;

	/**
	 * �v���C�x�[�g�R���X�g���N�^�B
	 */
	private EnvironmentManager() {
	    EnvironmentManagerStrategyFactory factory =
	        new EnvironmentManagerStrategyFactory();
	    strategy = factory.getEnvironmentManagerStrategy();
	}

	/**
	 * �L�[���w�肵�đΉ�����l��Ԃ��܂��B�w�肵���L�[�����݂��Ȃ��ꍇ�́A�f�t�H���g�l��Ԃ��܂��B
	 * @param key �L�[
	 * @param def �f�t�H���g�l
	 * @return �L�[�ɑΉ�����l
	 */
	public static String get(String key, String def) {
	    return instance.strategy.get(key, def);
	}
}
