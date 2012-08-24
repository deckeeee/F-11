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

package org.F11.scada.applet.dialog.schedule;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * �X�P�W���[���_�C�A���O�̃{�^���̊��N���X�ł�
 */
abstract class AbstractScheduleButton extends JButton {
	/** �X�P�W���[�������ݒ�_�C�A���O�̎Q�� */
	protected AbstractScheduleDialog scheduleDialog;

	/**
	 * �R���X�g���N�^
	 * @param dialog �X�P�W���[���_�C�A���O�̎Q��
	 */
	protected AbstractScheduleButton(AbstractScheduleDialog scheduleDialog) {
		this.scheduleDialog = scheduleDialog;
	}


	/**
	 * ���̃{�^���ɑΉ��Â���L�[�}�b�v���`���܂��B
	 * @param textValue �Ή��Â���L�[(VK_�����̕���)
	 */
	protected void setInoutKeyMap(String textValue) {
		// define action
		Action key = new AbstractAction(textValue) {
			private static final long serialVersionUID = 7068324749390647020L;

			public void actionPerformed(ActionEvent e) {
				pushButton();
			}
		};

		// associate action with key
		InputMap imap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		if (getKeyStroke(textValue) == null) {
			System.out.println("Not KeyStroke : " + textValue);
		}
		imap.put(
			getKeyStroke(textValue),
			key.getValue(Action.NAME)
		);
		ActionMap amap = getActionMap();
		amap.put(key.getValue(Action.NAME), key);
	}

	/**
	 * �e���L�[�Ƃ���ȊO�Ŕ����ɈقȂ�̂ŁA�����L�[�̓T�u�N���X�ŃI�[�o�[���C�h���܂��B
	 * @return KeyStroke �̏����Ɉˑ����Ă��܂��B
	 */
	protected KeyStroke getKeyStroke(String textValue) {
		return KeyStroke.getKeyStroke(textValue);
	}

	/**
	 * ���z���\�b�h�ł��B
	 * �{�^�����������ꂽ���̏������L�q���܂��B
	 */
	abstract public void pushButton();
}
