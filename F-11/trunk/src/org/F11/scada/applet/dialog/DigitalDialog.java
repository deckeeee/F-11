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
import org.F11.scada.applet.symbol.ScheduleEditable;
import org.F11.scada.util.FontUtil;
import org.F11.scada.util.MemoryLogUtil;
import org.F11.scada.xwife.applet.PageChanger;
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
	/** �y�[�W�ύX�I�u�W�F�N�g */
	private final PageChanger changer;

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param frame �e�̃t���[���B
	 */
	public DigitalDialog(Frame frame, PageChanger changer) {
		super(frame);
		this.changer = changer;
		getContentPane().setLayout(null);
	}

	/**
	 * �R���X�g���N�^�B
	 *
	 * @param dialog �e�_�C�A���O�B
	 */
	public DigitalDialog(Dialog dialog, PageChanger changer) {
		super(dialog);
		this.changer = changer;
		getContentPane().setLayout(null);
	}

	/**
	 * �C�e���[�^�[���Z�b�g���܂�
	 *
	 * @param listIterator �ҏW�\�V���{���̃C�e���[�^�[
	 */
	public void setListIterator(ListIterator listIterator) {
		symbol = (DigitalEditable) listIterator.next();
	}

	public void show() {
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
		setDefaultFocus();
		super.dispose();
	}

	public void selectAll() {
	}

	/**
	 * �����ꂽ�{�^���̓�������s���܂��B
	 */
	public void actionPerformed(ActionEvent e) {
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
			String fontSize,
			String schedule) {
		DialogButton button =
			DialogButton.createDialogButton(this, actionNo, schedule, changer);
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
	}

	/**
	 * �_�C�A���O�ɕ\������{�^���̊��N���X�ł��B
	 */
	static abstract class DialogButton extends JButton {
		protected static final Logger logger =
			Logger.getLogger(DialogButton.class);
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
				int actionNo,
				String schedule,
				PageChanger changer) {
			switch (actionNo) {
			case 5:
				return new ScheduleButton(dialog, actionNo, schedule, changer);
			case 6:
				return new CancelButton(dialog, actionNo, changer);
			default:
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
		public CancelButton(
				DigitalDialog dialog,
				int actionNo,
				PageChanger changer) {
			super(dialog, actionNo);
			ActionMapUtil.setActionMap(this, changer);
		}

		/**
		 * �f�W�^���{�^���̎w����������s���܂��B
		 */
		public void pushButton() {
			dialog.dispose();
		}
	}

	private static class ScheduleButton extends DialogButton {
		private final String scheduleDialogNo;
		private final PageChanger changer;

		public ScheduleButton(
				DigitalDialog dialog,
				int actionNo,
				String schedule,
				PageChanger changer) {
			super(dialog, actionNo);
			this.scheduleDialogNo = schedule;
			this.changer = changer;
			if (null == schedule || "".equals(schedule)) {
				throw new IllegalArgumentException(
					"�X�P�W���[���_�C�A���ONo(schedule����)���ݒ肳��Ă��܂���B");
			}
		}

		@Override
		public void pushButton() {
			WifeDialog wd = DialogFactory.get(dialog, scheduleDialogNo, changer);
			wd.setListIterator(getIterator());
			wd.setTitle(dialog.getTitle());
			logger.info(MemoryLogUtil.getMemory(dialog.getTitle()));
			wd.show();
			dialog.dispose();
		}

		private ListIterator getIterator() {
			return new ScheduleIterator((ScheduleEditable) dialog.symbol);
		}

		private static class ScheduleIterator implements ListIterator {
			private final ScheduleEditable editable;

			public ScheduleIterator(ScheduleEditable editable) {
				this.editable = editable;
			}

			public void add(Object o) {
			}

			public boolean hasNext() {
				return false;
			}

			public boolean hasPrevious() {
				return false;
			}

			public Object next() {
				return editable;
			}

			public int nextIndex() {
				return 0;
			}

			public Object previous() {
				return null;
			}

			public int previousIndex() {
				return 0;
			}

			public void remove() {
			}

			public void set(Object o) {
			}
		}
	}
}
