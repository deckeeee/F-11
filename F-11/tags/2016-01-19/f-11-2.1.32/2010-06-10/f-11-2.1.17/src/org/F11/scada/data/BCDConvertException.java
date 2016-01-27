/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/BCDConvertException.java,v 1.2.6.1 2005/08/11 07:46:32 frdm Exp $
 * $Revision: 1.2.6.1 $
 * $Date: 2005/08/11 07:46:32 $
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

package org.F11.scada.data;

/**
 * �s����BCD�f�[�^��ϊ������Ƃ��ɔ��������O�ł��B
 * ������ BCD �f�[�^�Ƃ� 0x0000 ���� 0x9999 �܂ł̐��l�ŁA16�i���\���� 'A' ���� 'F' ����菜�����f�[�^�ł��B
 */
public class BCDConvertException extends RuntimeException {

	private static final long serialVersionUID = -3316674999641927021L;

	/**
	 * �w�肳�ꂽ�ڍ׃��b�Z�[�W���g�p���āA�V�������s����O���\�z���܂��B
	 * �����͏��������ꂸ�A���̌� Throwable.initCause(java.lang.Throwable) ���Ăяo�����Ƃŏ���������܂��B
	 * @param message�@�ڍ׃��b�Z�[�W�B�ڍ׃��b�Z�[�W�� Throwable.getMessage() ���\�b�h�ɂ��擾�p�ɕۑ������
	 */
	public BCDConvertException(String message) {
		super(message);
	}
}

