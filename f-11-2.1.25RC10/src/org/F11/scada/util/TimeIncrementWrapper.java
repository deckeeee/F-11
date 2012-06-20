/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/util/TimeIncrementWrapper.java,v 1.2 2003/10/31 04:38:51 frdm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/10/31 04:38:51 $
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
 *
 */
package org.F11.scada.util;

import java.util.Map;

/**
 * ������ Map ���ɓ����L�[�l�����݂���ꍇ�ɂ́A�L�[�l���C���N�������g����
 * �}�b�v�Ɋi�[����ׂ̃��b�p�[�N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class TimeIncrementWrapper {
	private TimeIncrementWrapper() {}
	
	/**
	 * ������ Map ���ɓ����L�[�l�����݂���ꍇ�ɂ́A�����L�[�������Ȃ�܂�
	 * �L�[�l���C���N�������g���āA���̃L�[�l�Ń}�b�v�Ɋi�[���܂��B
	 * @param key ���ɂȂ�L�[�l
	 * @param value Map �Ɋi�[����I�u�W�F�N�g
	 * @param map �Ώۂ� Map �I�u�W�F�N�g
	 * @return �i�[�����I�u�W�F�N�g
	 */
	public static Object put(long key, Object value, Map map) {
		Long t = null;
		for (long i = 0; i < Long.MAX_VALUE; i++) {
			t = new Long(key + i);
			if (!map.containsKey(t)) {
				break;
			}
		}
		return map.put(new Long(t.longValue()), value);
	}
}
