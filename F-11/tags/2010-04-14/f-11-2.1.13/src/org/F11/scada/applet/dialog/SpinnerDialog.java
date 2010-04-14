/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.dialog;

import java.awt.event.ActionListener;
import java.util.ListIterator;

import javax.swing.JSpinner;

import org.F11.scada.applet.symbol.TenkeyEditable;

/**
 * JSpinner�����_�C�A���O�̃C���^�[�t�F�C�X�ł��B
 * @author maekawa
 */
public interface SpinnerDialog extends ActionListener {
	/**
	 * �X�s�i�[���̃G�f�B�^�[��Ԃ��܂��B
	 * @return �X�s�i�[���̃G�f�B�^�[��Ԃ��܂��B
	 */
	JSpinner.NumberEditor getEditor();
	/**
	 * �_�C�A���O����܂��B
	 */
	void dispose();
	/**
	 * �_�C�A���O���J���܂��B
	 */
	void show();
	/**
	 * ���X�g�C�e���[�^�[��Ԃ��܂��B
	 * @return ���X�g�C�e���[�^�[��Ԃ��܂��B
	 */
	ListIterator listIterator();
	/**
	 * �V���{����ݒ肵�܂��B
	 * @param symbol �V���{��
	 */
	void setSymbol(TenkeyEditable symbol);

	/**
	 * �X�s�i�[�̓��e��Ԃ��܂��B
	 * @return �X�s�i�[�̓��e��Ԃ��܂��B
	 */
	Object getValue();

	/**
	 * �X�s�i�[�ɒl��ݒ肵�܂��B
	 * @param value �X�s�i�[�ɒl��ݒ肵�܂��B
	 */
	void setValue(String value);
	/**
	 * �V���{���̃C���X�^���X��ێ����Ă��邩�B
	 * @return �ێ����Ă���ꍇ��true �����łȂ��ꍇ�� false ��Ԃ��܂��B
	 */
	boolean hasSymbol();
}
