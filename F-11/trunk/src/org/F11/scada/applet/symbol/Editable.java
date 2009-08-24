package org.F11.scada.applet.symbol;

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

import java.awt.Point;
import java.awt.Window;
import java.util.Map;

import org.F11.scada.applet.dialog.WifeDialog;

/**
 * �ҏW�\�ȃV���{���͂��̃C���^�[�t�F�C�X���������A�V���{���l��ҏW����_�C�A���O��Ԃ��K�v������܂��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Editable {
	/**
	 * �ҏW����ׂ̃_�C�A���O��Ԃ��܂��B
	 * @param window �e�E�B���h�E
	 * @param collection �x�[�X�N���X�̃C���X�^���X
	 * @param �C�ӂ̃p�����[�^���X�g
	 * @todo �C�ӂ̃p�����[�^�͂��������A�^����������ׂ������B
	 */
	public WifeDialog getDialog(Window window, SymbolCollection collection, java.util.List para);
	/**
	 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��Ԃ��܂��B
	 */
	public Point getPoint();
	/**
	 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��ݒ肵�܂��B
	 */
	public void setPoint(Point point);
	/**
	 * ���̃V���{���̕ҏW�\�t���O��ݒ肵�܂��B
	 * @param editable �ҏW�\�ɂ���Ȃ� true �������łȂ���� false ��ݒ肵�܂��B
	 */
	public void setEditable(boolean[] editable);
	/**
	 * ���̃V���{�����ҏW�\���ǂ�����Ԃ��܂��B
	 * @return �ҏW�\�ȏꍇ�� true �������łȂ���� false ��Ԃ��܂��B
	 */
	public boolean isEditable();

	/**
	 * �x�[�X�V�X�e���̃��[�U�[�F�؂ɂ�� Subject ���ύX���ꂽ�Ƃ��Ƀf�B�X�p�b�`����܂��B
	 * �ҏW�\�ȃV���{�����ێ����Ă���A�f�[�^�v���o�C�_���{�f�[�^�z���_�[�����A���_�[�o�[
	 * �Ō�������������z���Ԃ��܂��B
	 * @return �f�[�^�v���o�C�_���{�f�[�^�z���_�[�����A���_�[�o�[�Ō�������������z��
	 */
	public String[] getDestinations();
	
	/**
	 * �������ݐ��ǉ����܂�
	 * @param params 
	 */
	public void addDestination(Map params);
	
	/**
	 * �������ݐ��ݒ肵�܂��B
	 * @param setter
	 */
	public void addValueSetter(ValueSetter setter);
	
	/**
	 * �^�u�L�[�_�C�A���O�ړ��g�p�̗L��
	 * @return �^�u�L�[�ړ�������Ȃ� true �� �����łȂ��Ȃ� false ��Ԃ��܂�
	 */
	public boolean isTabkeyMove();
	
	/**
	 * �\�����Ȃ� true �� �����łȂ��Ȃ� false ��Ԃ��܂��B
	 * 
	 * @return �\�����Ȃ� true �� �����łȂ��Ȃ� false ��Ԃ��܂��B
	 */
	boolean isVisible();
}
