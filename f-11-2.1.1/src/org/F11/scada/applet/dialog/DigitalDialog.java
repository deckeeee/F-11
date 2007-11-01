/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/dialog/DigitalDialog.java,v 1.5.2.11 2007/07/31 08:27:48 frdm Exp $
 * $Revision: 1.5.2.11 $
 * $Date: 2007/07/31 08:27:48 $
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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ListIterator;

import javax.swing.JButton;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.DigitalEditable;
import org.F11.scada.util.FontUtil;
import org.apache.log4j.Logger;

/**
 * �f�W�^���f�[�^�𑀍삷��_�C�A���O�N���X�ł��B �w������� DigitalButton �Œ�`���܂��B
 * �_�C�A���O�N���X�ł́A�{�^���Ǝw������̊֘A���`���܂��B �L�����Z���{�^���́A�w����������s���Ȃ��܂܃_�C�A���O����܂��B
 */
public class DigitalDialog extends WifeDialog implements ActionListener {
	private static final long serialVersionUID = 3786955747253504330L;
	private final Logger logger = Logger.getLogger(DigitalDialog.class);
	/** �f�W�^���{�^���̎Q�� */
	private DigitalEditable symbol;

	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param frame �e�̃t���[���B
	 */
	public DigitalDialog(Frame frame) {
		super(frame);
		this.getContentPane().setLayout(null);
	}

	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param dialog �e�_�C�A���O�B
	 */
	public DigitalDialog(Dialog dialog) {
		super(dialog);
		this.getContentPane().setLayout(null);
	}

	/**
	 * �C�e���[�^�[���Z�b�g���܂�
	 * 
	 * @param listIterator �ҏW�\�V���{���̃C�e���[�^�[
	 */
	public void setListIterator(ListIterator listIterator) {
		logger.info("setListIterator�J�n");
		symbol = (DigitalEditable) listIterator.next();
	}

	public void show() {
		logger.info("show�J�n");
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		/* �{�^�����̐ݒ� */
		for (int i = 0, textPos = 0; i < getContentPane().getComponentCount(); i++) {
			Component compo = getContentPane().getComponent(i);
			if (compo instanceof JButton) {
				String bt = this.symbol.getButtonString(textPos);
				if (bt != null)
					((JButton) compo).setText(bt);
				textPos++;
			}
		}
		setDefaultFocus();
		super.show();
	}

	/**
	 * �I�����鎞�Ƀt�H�[�J�X�g���o�[�X���f�t�H���g�ɖ߂��܂��B
	 * 
	 * @see java.awt.Dialog#dispose()
	 */
	public void dispose() {
		logger.info("dispose�J�n");
		setDefaultFocus();
		super.dispose();
	}

	public void selectAll() {
		logger.info("selectAll�J�n");
	}

	/**
	 * �����ꂽ�{�^���̓�������s���܂��B
	 */
	public void actionPerformed(ActionEvent e) {
		logger.info("actionPerformed�J�n");
		DialogButton button = (DialogButton) e.getSource();
		button.pushButton();
	}

	/**
	 * �_�C�A���O�Ƀ{�^����ǉ����܂��B
	 * 
	 * @param text �{�^������
	 * @param �w������ԍ�
	 * @param rec �{�^���̑傫���Ɣz�u�ʒu
	 * @param foreground �����F
	 * @param background �w�i�F
	 * @param font �t�H���g��
	 * @param fontStyle �t�H���g�X�^�C��
	 * @param fontSize �t�H���g�T�C�Y
	 */
	public void add(
			String text,
			int actionNo,
			Rectangle rec,
			String foreground,
			String background,
			String font,
			String fontStyle,
			String fontSize) {
		logger.info("add�J�n");
		invalidate();
		DialogButton button = DialogButton.createDialogButton(this, actionNo);
		button.setText(text);
		button.setBounds(rec);
		if (null != ColorFactory.getColor(foreground)) {
			button.setForeground(ColorFactory.getColor(foreground));
		}
		if (null != ColorFactory.getColor(background)) {
			button.setBackground(ColorFactory.getColor(background));
		}
		FontUtil.setFont(font, fontStyle, fontSize, button);
		getContentPane().add(button);
		validate();
	}

	/**
	 * �_�C�A���O�ɕ\������{�^���̊��N���X�ł��B
	 */
	static abstract class DialogButton extends JButton {
		protected static final Logger logger = Logger
				.getLogger(DialogButton.class);
		/** �e�_�C�A���O�̎Q�Ƃł��B */
		protected DigitalDialog dialog;
		/** �{�^���ɒ�`���ꂽ�w������ԍ��ł� */
		protected int actionNo;

		/**
		 * �A�C�R���{�^�����쐬����R���X�g���N�^�ł��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param actionNo �{�^���Ɋ��蓖�Ă�w������ԍ�
		 */
		protected DialogButton(DigitalDialog dialog, int actionNo) {
			super();
			this.dialog = dialog;
			this.actionNo = actionNo;
			addActionListener(this.dialog);
		}

		/**
		 * ���z�֐��ł��B �T�u�N���X�ŁA�{�^�����������ꂽ�Ƃ��́A�������L�q���܂��B
		 */
		abstract public void pushButton();

		static public DialogButton createDialogButton(
				DigitalDialog dialog,
				int actionNo) {
			logger.info("createDialogButton�J�n");
			if (actionNo == 6) {
				return new CancelButton(dialog, actionNo);
			} else {
				return new SendButton(dialog, actionNo);
			}
		}
	}

	/**
	 * �w�����쑗�M�{�^���N���X�ł��B
	 */
	static final class SendButton extends DialogButton {
		private static final long serialVersionUID = -1307199387393391804L;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param dialog �{�^����ǉ�����_�C�A���O�̎Q��
		 * @param actionNo �{�^���̎w������
		 */
		public SendButton(DigitalDialog dialog, int actionNo) {
			super(dialog, actionNo);
		}

		/**
		 * �f�W�^���{�^���̎w����������s���܂��B
		 */
		public void pushButton() {
			logger.info("pushButton�J�n");
			if (ConfirmUtil.isConfirm(dialog)) {
				dialog.symbol.pushButton(actionNo);
				dialog.dispose();
			}
		}
	}

	/**
	 * �L�����Z���{�^���N���X�ł��B
	 */
	static final class CancelButton extends DialogButton {
		private static final long serialVersionUID = -4881864928924466057L;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param dialog �{�^����ǉ�����_�C�A���O�̎Q��
		 * @param actionNo �{�^���̎w������
		 */
		public CancelButton(DigitalDialog dialog, int actionNo) {
			super(dialog, actionNo);
		}

		/**
		 * �f�W�^���{�^���̎w����������s���܂��B
		 */
		public void pushButton() {
			logger.info("pushButton�J�n");
			dialog.dispose();
		}
	}
}
