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

package org.F11.scada.cat.logic.impl;

/**
 * XML�̃R�����g��Ԃ�ێ�����w���p�[�N���X
 * 
 * @author maekawa
 *
 */
class XmlCommentChecker {
	/** �R�����g�������̗L�� */
	private boolean isComment;

	/**
	 * �s��ǂݍ���ŃR�����g�����ǂ����𔻒肵�܂��B
	 * 
	 * @param line ��������s
	 */
	void checkComment(String line) {
		String startStr = "<!--";
		int start = line.lastIndexOf(startStr);
		if (start >= 0) {
			isComment = true;
		}
		String endStr = "-->";
		int end = line.lastIndexOf(endStr, start + startStr.length());
		if (end >= 0) {
			isComment = false;
		}
	}
	
	/**
	 * �R�����g���ł���� true �� ������� false ��Ԃ��܂��B
	 * 
	 * @return �R�����g���ł���� true �� ������� false ��Ԃ��܂��B
	 */
	boolean isComment() {
		return isComment;
	}
}