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

package org.F11.scada.xwife.applet;

import org.F11.scada.EnvironmentManager;

/**
 * ����n��\�����郂�[�h�Ɋւ��郆�[�e�B���e�B�N���X
 * 
 * @author maekawa
 *
 */
public abstract class AttributeNColumnUtil {
	/**
	 * ����n��\�����邩�ǂ�����Ԃ��B
	 * 
	 * @return ����n��\�����邩�ǂ�����Ԃ��B�\������ꍇ�� true �������Ŗ����ꍇ�� false ��Ԃ��B
	 */
	public static boolean isAttributeDisplay() {
		return Boolean.parseBoolean(EnvironmentManager.get(
			"/server/alarm/attributen/enable",
			"false"));
	}

	/**
	 * �ŐV�x���g�ݗ��Ă��𒊏o���܂��B
	 * 
	 * @param i ��ԍ�
	 * @param value ��̓��e
	 * @return �\�������̏ꍇ true �������Ŗ����ꍇ�� false ��Ԃ��B
	 */
	public static boolean isDisplayColumn(int i, Object value) {
		if (!AttributeNColumnUtil.isAttributeDisplay()) {
			return null != value && 17 != i && 18 != i && 19 != i && 20 != i;
		}
		return null != value && 17 != i;
	}

}
