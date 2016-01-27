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
package org.F11.scada.parser;

import java.util.Stack;

import org.xml.sax.Attributes;

/**
 * SAX�p�[�T�[�̏�Ԃ�\���N���X�̃C���^�[�t�F�C�X�ł��B(Use State Pattern.)
 * ���̃C���^�[�t�F�C�X����������N���X�́A�^�O�ɂ��킹�ĉ��ʂ̏�ԃI�u�W�F�N�g��
 * �������A��Ԉڊǂ�\�����܂��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface State {
	/**
	 * �^�O�C�x���g�������������ɌĂяo���܂��B
	 * ��ʓI�ɂ͏�ԃX�^�b�N�Ɏ������g��push���܂��B
	 * @param tagName �^�O����
	 * @param atts �^�O�����I�u�W�F�N�g
	 * @param stack ��ԃX�^�b�N
	 */
	public void add(String tagName, Attributes atts, Stack stack);
	
	/**
	 * �I���^�O�C�x���g�������������ɌĂяo���܂��B
	 * ��ʓI�ɂ͏�ԃX�^�b�N���玩�����g��pop���܂��B
	 * @param tagName �^�O����
	 * @param stack ��ԃX�^�b�N
	 */
	public void end(String tagName, Stack stack);
}
