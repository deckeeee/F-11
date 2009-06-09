/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/dialog/TenkeyDialog.java,v 1.8.2.12 2007/07/26 01:11:36 frdm Exp $
 * $Revision: 1.8.2.12 $
 * $Date: 2007/07/26 01:11:36 $
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

package org.F11.scada.applet.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.JPanel;

import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * ���l�ݒ�p�e���L�[�^�_�C�A���O�N���X�ł��B
 */
public class TenkeyDialog extends AbstractTenkeyDialog {
	private static final long serialVersionUID = -5590430193792923661L;
	private static final Logger log = Logger.getLogger(TenkeyDialog.class);
	/** �e�R���|�[�l���g�C�e���[�^�[ */
	private ListIterator listIterator;
	/** �f�t�H���g�t�H�[�J�X�g���o�[�X�̎Q�� */
	private static final Set forward;
	private static final Set backward;
	static {
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		forward = kfm
				.getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		backward = kfm
				.getDefaultFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
	};

	/**
	 * �R���X�g���N�^
	 * 
	 * @param frame �e�̃t���[���ł�
	 */
	public TenkeyDialog(Frame frame, PageChanger changer) {
		super(frame, changer);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param dialog �e�̃_�C�A���O�ł�
	 */
	public TenkeyDialog(Dialog dialog, PageChanger changer) {
		super(dialog, changer);
	}

	/**
	 * �I�����鎞�Ƀt�H�[�J�X�g���o�[�X���f�t�H���g�ɖ߂��܂��B
	 * 
	 * @see java.awt.Dialog#dispose()
	 */
	public void dispose() {
		log.info("dispose�J�n");
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				forward);
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				backward);

		super.dispose();
	}

	/**
	 * �C�e���[�^�[���Z�b�g���܂�
	 * 
	 * @param listIterator �ҏW�\�V���{���̃C�e���[�^�[
	 */
	public void setListIterator(ListIterator listIterator) {
		log.info("setListIterator�J�n");
		this.listIterator = listIterator;
		// ��ڂ̃V���{����ݒ肵�܂��B
		symbol = (TenkeyEditable) listIterator.next();
	}

	public ListIterator listIterator() {
		log.info("listIterator�J�n");
		return listIterator;
	}

	/**
	 * ���������ł��B
	 */
	protected void setManipulatePanel(JPanel keyPanel) {
		log.info("setManipulatePanel�J�n");
		JPanel manipulatePanel = new JPanel(new GridLayout(6, 1));
		final OkButton okButton = new OkButton(this, "OK");
		PreviousButton previousButton = new PreviousButton(this, "�O����");
		NextButton nextButton = new NextButton(this, "������");
		CancelButton cancelButton = new CancelButton(this, "Cancel", changer);
		manipulatePanel.add(okButton);
		manipulatePanel.add(previousButton);
		manipulatePanel.add(nextButton);
		manipulatePanel.add(cancelButton);
		keyPanel.add(manipulatePanel, BorderLayout.EAST);
		// �^�u�L�[�̓��̓C�x���g���L�[�{�[�h�t�H�[�J�X�}�l�[�W���[�ɉ���肳���ׁA
		// �L�[�{�[�h�t�H�[�J�X�}�l�[�W���[���A�^�u�L�[�̊��蓖�Ă��폜���܂��B
		KeyboardFocusManager kfm = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
		kfm.setDefaultFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				Collections.EMPTY_SET);
	}
}
