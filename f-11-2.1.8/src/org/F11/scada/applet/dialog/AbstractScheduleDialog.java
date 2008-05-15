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
package org.F11.scada.applet.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.schedule.GraphicScheduleViewCreator;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.applet.symbol.ScheduleEditable;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.applet.symbol.ValueSetter;
import org.apache.log4j.Logger;

/**
 * @author hori
 */
abstract public class AbstractScheduleDialog extends WifeDialog implements
		SymbolCollection, ActionListener {
	/** �V���{���̃C�e���[�^�[ */
	private ListIterator listIterator;
	/** �ҏW�ΏۃV���{�� */
	private ScheduleEditable symbol;
	/** �O���[�v�ԍ��̃p�l���ł� */
	private JPanel groupNoPanel;
	/** �O���[�v�ԍ��{�^���ł� */
	private JButton grupNoButton;
	/** �e���L�[�_�C�A���O�̎Q�� */
	private WifeDialog tenkeyDialog;

	private final boolean isSort;
	private final boolean isLenient;
	/** ���M���O�N���X�ł� */
	private final Logger logger = Logger
			.getLogger(AbstractScheduleDialog.class);

	/**
	 * �R���X�g���N�^
	 * 
	 * @param frame �e�̃t���[���ł�
	 */
	protected AbstractScheduleDialog(
			Frame frame,
			boolean isSort,
			boolean isLenient) {
		super(frame);
		this.isSort = isSort;
		this.isLenient = isLenient;
		init();
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param dialog �e�̃_�C�A���O�ł�
	 */
	protected AbstractScheduleDialog(
			Dialog dialog,
			boolean isSort,
			boolean isLenient) {
		super(dialog);
		this.isSort = isSort;
		this.isLenient = isLenient;
		init();
	}

	/**
	 * ���������ł��B
	 */
	private void init() {
		logger.info("init�J�n");

		JPanel subPanel = new JPanel(new BorderLayout());
		JPanel manipulatePanel = new JPanel(new FlowLayout());
		OkButton okButton = new OkButton(this, "OK");
		CancelButton cancelButton = new CancelButton(this, "Cancel");
		manipulatePanel.add(okButton);
		manipulatePanel.add(cancelButton);
		subPanel.add(manipulatePanel, BorderLayout.EAST);

		groupNoPanel = new JPanel(new FlowLayout());
		subPanel.add(groupNoPanel, BorderLayout.CENTER);

		getContentPane().add(subPanel, BorderLayout.SOUTH);
	}

	/**
	 * ���̃_�C�A���O��\�����܂��B
	 */
	public void show() {
		logger.info("show�J�n");
		Rectangle dialogBounds = getBounds();
		dialogBounds.setLocation(symbol.getPoint());
		setLocation(WifeUtilities.getInScreenPoint(screenSize, dialogBounds));
		setDialogValue();
		setDefaultFocus();
		super.show();
	}

	public void dispose() {
		setDefaultFocus();
		super.dispose();
	}

	public void selectAll() {
		logger.info("selectAll�J�n");
	}

	/**
	 * �ҏW�\�A�i���O�I�u�W�F�N�g��ݒ肵�܂��B
	 */
	private void setDialogValue() {
		logger.info("setDialogValue�J�n");
		GraphicScheduleViewCreator view = createView(
				symbol.getScheduleModel(),
				isSort,
				isLenient);
		JComponent viewPanel = view.createView();
		getContentPane().add(viewPanel, BorderLayout.CENTER);

		grupNoButton = new GroupNoButton(this, symbol.getValue());
		groupNoPanel.add(new JLabel("GroupNo : "));
		groupNoPanel.add(grupNoButton);
	}

	abstract public GraphicScheduleViewCreator createView(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isLenient);

	/**
	 * �C�e���[�^�[���Z�b�g���܂�
	 * 
	 * @param listIterator �ҏW�\�V���{���̃C�e���[�^�[
	 */
	public void setListIterator(ListIterator listIterator) {
		logger.info("setListIterator�J�n");
		this.listIterator = listIterator;
		this.symbol = (ScheduleEditable) this.listIterator.next();
	}

	/**
	 * �e�{�^���̉������̓�����������܂��B
	 */
	public void actionPerformed(ActionEvent e) {
		logger.info("actionPerformed�J�n");
		((DialogButton) e.getSource()).pushButton();
	}

	/**
	 * �R���|�[�l���g��̎����V���{���C�e���[�^�[��Ԃ��܂��B
	 * 
	 * @param para �C�ӂ̃p�����[�^�[
	 */
	public ListIterator listIterator(List para) {
		logger.info("listIterator�J�n");
		List list = new ArrayList();
		list.add(grupNoButton);
		return new ScheduleIterator(para, list);
	}

	/**
	 * �����V���{���C�e���[�^�[�N���X�ł��B
	 */
	private static final class ScheduleIterator implements ListIterator {
		private final Logger logger = Logger.getLogger(ScheduleIterator.class);
		/** �V���{���̃��X�g�̎Q�Ƃł� */
		private List symbols;
		/** ���X�g�C�e���[�^�[�ł� */
		private ListIterator listIterator;
		/** ���E�t�����[�h�̕ێ� */
		private boolean isPreviousMode;
		/** �N���b�N���ꂽ�{�^���̃C���f�b�N�X�ł� */
		private int startIndex;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param para �C�ӂ̃p�����[�^�[
		 */
		ScheduleIterator(List para, List buttonList) {
			symbols = new ArrayList(buttonList);
			startIndex = ((Integer) para.get(0)).intValue();
		}

		public boolean hasNext() {
			logger.info("hasNext�J�n");
			return true;
		}

		public Object next() {
			logger.info("next�J�n");
			if (listIterator == null)
				listIterator = symbols.listIterator(startIndex);

			if (isPreviousMode) {
				isPreviousMode = false;
				try {
					listIterator.next();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.next();
				}
			}

			try {
				return listIterator.next();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator();
				return listIterator.next();
			}
		}

		public boolean hasPrevious() {
			logger.info("hasPrevious�J�n");
			return true;
		}

		public Object previous() {
			logger.info("previous�J�n");
			if (listIterator == null)
				listIterator = symbols.listIterator(symbols.size());
			if (!isPreviousMode) {
				isPreviousMode = true;
				try {
					listIterator.previous();
				} catch (NoSuchElementException ex) {
					listIterator = symbols.listIterator(symbols.size());
					listIterator.previous();
				}
			}

			try {
				return listIterator.previous();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator(symbols.size());
				return listIterator.previous();
			}
		}

		public int nextIndex() {
			logger.info("nextIndex�J�n");
			int index = listIterator.nextIndex();
			if (isPreviousMode && index == symbols.size()) {
				ListIterator lit = symbols.listIterator();
				index = lit.nextIndex();
			}
			return index;
		}

		public int previousIndex() {
			logger.info("previousIndex�J�n");
			int index = listIterator.previousIndex();
			if (!isPreviousMode && index < 0) {
				ListIterator lit = symbols.listIterator(symbols.size());
				index = lit.previousIndex();
			}
			return index;
		}

		public void add(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void remove() {
			// non suport
			throw new UnsupportedOperationException();
		}

		public void set(Object o) {
			// non suport
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * �_�C�A���O�ɕ\������{�^���̊��N���X�ł��B
	 */
	private static abstract class DialogButton extends JButton {
		/** �e�_�C�A���O�̎Q�Ƃł��B */
		protected AbstractScheduleDialog dialog;

		/**
		 * �e�L�X�g�\���{�^�����쐬����R���X�g���N�^�ł��B
		 * 
		 * @param dialog �e�_�C�A���O�̎Q��
		 * @param text �{�^���ɕ\������e�L�X�g
		 */
		protected DialogButton(AbstractScheduleDialog dialog, String text) {
			super(text);
			this.dialog = dialog;
			init();
		}

		/**
		 * �e�평�������ł��B
		 */
		private void init() {
			addActionListener(dialog);
		}

		/**
		 * ���z�֐��ł��B �T�u�N���X�ŁA�{�^�����������ꂽ�Ƃ��́A�������L�q���܂��B
		 */
		abstract public void pushButton();
	}

	/**
	 * OK�{�^���N���X�ł��B
	 */
	private static class OkButton extends DialogButton {
		private static final long serialVersionUID = -3466462930674647017L;
		private final Logger logger = Logger.getLogger(OkButton.class);

		public OkButton(AbstractScheduleDialog dialog, String text) {
			super(dialog, text);
		}

		public void pushButton() {
			logger.info("pushButton�J�n");
			dialog.symbol.getScheduleModel().setValue();
			dialog.symbol.getScheduleModel().writeData();
			dialog.symbol.setValue(dialog.grupNoButton.getText());
			dialog.dispose();
		}
	}

	/**
	 * Cancel�{�^���N���X�ł��B
	 */
	private static class CancelButton extends DialogButton {
		private static final long serialVersionUID = 6331220843398896457L;
		private final Logger logger = Logger.getLogger(CancelButton.class);

		public CancelButton(AbstractScheduleDialog dialog, String text) {
			super(dialog, text);
		}

		public void pushButton() {
			logger.info("pushButton�J�n");
			dialog.symbol.getScheduleModel().undoData();
			dialog.dispose();
		}
	}

	/**
	 * �O���[�v�ԍ��ݒ�p�̃{�^���N���X�ł�
	 */
	private final static class GroupNoButton extends DialogButton implements
			TenkeyEditable {
		private static final long serialVersionUID = -3613346010335544912L;
		private final Logger logger = Logger.getLogger(GroupNoButton.class);
		/** �O���[�v�ԍ� */
		private String groupNo;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param dialog �X�P�W���[�������ݒ�_�C�A���O�̎Q��
		 * @param time ����
		 * @param hour ���ԁE���̎��
		 */
		GroupNoButton(AbstractScheduleDialog scheduleDialog, String groupNo) {
			super(scheduleDialog, groupNo);
			this.groupNo = groupNo;
			init();
		}

		/**
		 * �e����������
		 */
		private void init() {
			logger.info("init�J�n");
			DecimalFormat fmt = new DecimalFormat(getFormatString());
			setText(fmt.format(new Integer(groupNo)));
			Border bb = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
			Border eb = BorderFactory.createEmptyBorder(1, 3, 1, 3);
			setBorder(new CompoundBorder(bb, eb));
			setOpaque(true);
			setBackground(Color.WHITE);
		}

		/**
		 * �ҏW����ׂ̃_�C�A���O��Ԃ��܂��B
		 * 
		 * @param window �e�E�B���h�E
		 * @param collection �x�[�X�N���X�̃C���X�^���X
		 * @param �C�ӂ̃p�����[�^���X�g
		 * @todo �C�ӂ̃p�����[�^�͂��������A�^����������ׂ������B
		 */
		public WifeDialog getDialog(
				Window window,
				SymbolCollection collection,
				List para) {
			logger.info("getDialog�J�n");
			WifeDialog d = DialogFactory.get(window, "1");
			if (d == null)
				logger.warn(this.getClass().getName()
						+ " : scheduleDialog null");
			d.setListIterator(collection.listIterator(para));
			return d;
		}

		/**
		 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��Ԃ��܂��B
		 */
		public Point getPoint() {
			logger.info("getPoint�J�n");
			Point p = this.getLocationOnScreen();
			p.y += getSize().height;
			return p;
		}

		/**
		 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��ݒ肵�܂��B
		 * 
		 * @param point �ݒ�_�C�A���O�̍���� Point
		 */
		public void setPoint(Point point) {
			logger.info("setPoint�J�n");
			// NOP
		}

		/**
		 * �V���{���̒l��Ԃ��܂�
		 */
		public String getValue() {
			logger.info("getValue�J�n");
			return getText();
		}

		/**
		 * �V���{���ɒl��ݒ肵�܂�
		 */
		public void setValue(String value) {
			logger.info("setValue�J�n");
			setText(value);
		}

		/**
		 * �ŏ��l��Ԃ��܂�
		 */
		public double getConvertMin() {
			logger.info("getConvertMin�J�n");
			return dialog.symbol.getConvertValue().getConvertMin();
		}

		/**
		 * �ő�l��Ԃ��܂�
		 */
		public double getConvertMax() {
			logger.info("getConvertMax�J�n");
			return dialog.symbol.getConvertValue().getConvertMax();
		}

		/**
		 * ���l�\���t�H�[�}�b�g�������Ԃ��܂�
		 */
		public String getFormatString() {
			logger.info("getFormatString�J�n");
			return dialog.symbol.getConvertValue().getPattern();
		}

		/**
		 * �����C���{�^�����������ꂽ���̏������L�q���܂��B
		 */
		public void pushButton() {
			logger.info("pushButton�J�n");
			List para = new ArrayList();
			para.add(new Integer(0));
			if (this.dialog.tenkeyDialog != null)
				this.dialog.tenkeyDialog.dispose();
			this.dialog.tenkeyDialog = getDialog(this.dialog, this.dialog, para);
			this.dialog.tenkeyDialog.show();
			// logger.info("" + buttonList.indexOf(evt.getSource()));
		}

		public void setEditable(boolean[] editable) {
			logger.info("setEditable�J�n");
		}

		public boolean isEditable() {
			logger.info("isEditable�J�n");
			return true;
		}

		/*
		 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
		 */
		public String[] getDestinations() {
			logger.info("getDestinations�J�n");
			return new String[0];
		}

		/**
		 * �������ݐ�̒ǉ��͂��Ȃ��B
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
		 */
		public void addDestination(Map atts) {
			logger.info("addDestination�J�n");
		}

		/**
		 * �������ݐ�̒ǉ��͂��Ȃ��B
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
		 */
		public void addValueSetter(ValueSetter setter) {
			logger.info("addValueSetter�J�n");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
		 */
		public boolean isTabkeyMove() {
			logger.info("isTabkeyMove�J�n");
			return isVisible();
		}

		public String getDialogTitle() {
			logger.info("getDialogTitle�J�n");
			return "�O���[�vNo.";
		}
	}

}
