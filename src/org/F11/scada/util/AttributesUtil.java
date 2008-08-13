/*
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

import static org.F11.scada.cat.util.CollectionUtil.list;

import java.util.Collections;
import java.util.List;

import org.xml.sax.Attributes;

/**
 * �����I�u�W�F�N�g���������[�e�B���e�B�[�N���X�ł��B
 * 
 * @author maekawa
 */
public abstract class AttributesUtil {
	/**
	 * �������̒l��Ԃ��܂��B�A���A�l���w�肳��Ă��Ȃ��ꍇ�E�k�������̏ꍇ�́Anull��Ԃ��܂��B
	 * 
	 * @param attname ������
	 * @param atts �����I�u�W�F�N�g
	 * @return �������̒l��Ԃ��܂��B
	 */
	public static String getValue(String attname, Attributes atts) {
		return getNonNullString(atts.getValue(attname));
	}

	/**
	 * ������ null ��󔒂̏ꍇ null ���ȊO�̏ꍇ�͈����̕������Ԃ��܂��B
	 * 
	 * @param str ���肷�镶����
	 * @return ������ null ��󔒂̏ꍇ null ���ȊO�̏ꍇ�͈����̕������Ԃ��܂��B
	 */
	public static String getNonNullString(String str) {
		return (str == null || "".equals(str)) ? null : str;
	}

	/**
	 * �������̒l��boolean�ŕԂ��܂��B�A���A�l���w�肳��Ă��Ȃ��ꍇ�E�k�������̏ꍇ�́Afalse��Ԃ��܂��B
	 * �l������̔���ɂ�Boolean#valueOf���\�b�h���g�p���Ă��܂��B
	 * 
	 * @param attname ������
	 * @param atts �����I�u�W�F�N�g
	 * @return �������̒l��Ԃ��܂��B
	 * @see Boolean#valueOf(java.lang.String)
	 */
	public static boolean getBooleanValue(String attname, Attributes atts) {
		return Boolean.valueOf(getValue(attname, atts)).booleanValue();
	}

	/**
	 * �����̕����� null �� �󔒂̏ꍇ true ��Ԃ��܂��B
	 * 
	 * @param str ���肷�镶����
	 * @return �����̕����� null �� �󔒂̏ꍇ true ��Ԃ��܂��B
	 */
	public static boolean isSpaceOrNull(String str) {
		return str == null || "".equals(str);
	}

	public static long getLongValue(String s) {
		if (isSpaceOrNull(s)) {
			return 0L;
		} else {
			try {
				return Long.parseLong(s);
			} catch (Exception e) {
				return 0L;
			}
		}
	}

	/**
	 * ������ null ���󔒂Ȃ� null ���ȊO�̏ꍇ��"%"�ň͂񂾕������Ԃ��܂��B
	 * 
	 * @param s ������
	 * @return ������ null ���󔒂Ȃ� null ���ȊO�̏ꍇ��"%"�ň͂񂾕������Ԃ��܂��B
	 */
	public static String getLikeString(String s) {
		return isSpaceOrNull(s) ? null : "%" + s + "%";
	}

	/**
	 * �����̕�������u,�v�ŕ������܂�
	 * 
	 * @param tables �e�[�u�������u,�v�ŋ�؂���������
	 * @return �e�[�u����������΃e�[�u�����̃��X�g�𖳂���΋�̃��X�g��Ԃ��܂��B
	 */
	public static List<String> getTables(String tables) {
		if (null != tables) {
			List<String> l = list();
			String[] s = tables.split("\\,");
			for (String string : s) {
				l.add(string.trim());
			}
			return l;
		} else {
			return Collections.emptyList();
		}
	}
}
