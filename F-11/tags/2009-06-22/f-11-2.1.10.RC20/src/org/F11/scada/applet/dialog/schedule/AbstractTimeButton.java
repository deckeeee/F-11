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

import java.awt.Color;
import java.awt.Point;
import java.awt.Window;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.F11.scada.applet.dialog.DialogFactory;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.symbol.HandCursorListener;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.TenkeyEditable;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * �����ݒ�p�̃{�^���N���X�ł�
 */
abstract class AbstractTimeButton extends AbstractScheduleButton implements
		TenkeyEditable {
	private static final long serialVersionUID = -7092769000804776115L;
	protected static Logger logger = Logger.getLogger(AbstractTimeButton.class);
	/** ���� */
	protected int time;
	/** ���ԁE���̎�� */
	protected boolean hour;

	private final PageChanger changer;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param dialog �X�P�W���[�������ݒ�_�C�A���O�̎Q��
	 * @param time ����
	 * @param hour ���ԁE���̎��
	 */
	AbstractTimeButton(
			AbstractScheduleDialog scheduleDialog,
			int time,
			boolean hour,
			PageChanger changer) {
		super(scheduleDialog);
		this.time = time;
		this.hour = hour;
		this.changer = changer;
		init();
		addMouseListener(new HandCursorListener());
	}

	/**
	 * �e����������
	 */
	private void init() {
		DecimalFormat fmt = new DecimalFormat(getFormatString());
		if (hour) {
			setText(fmt.format((time / 100)));
		} else {
			setText(fmt.format((time % 100)));
		}
		Border bb = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border eb = BorderFactory.createEmptyBorder(1, 3, 1, 3);
		setBorder(new CompoundBorder(bb, eb));
		setOpaque(true);
		setBackground(Color.white);
		addActionListener(this.scheduleDialog);
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
		WifeDialog d = DialogFactory.get(window, "1", changer);
		if (d == null)
			logger.warn(this.getClass().getName() + " : scheduleDialog null");
		d.setListIterator(collection.listIterator(para));
		return d;
	}

	/**
	 * �ݒ�_�C�A���O�̍���� Point �I�u�W�F�N�g��Ԃ��܂��B
	 */
	public Point getPoint() {
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
		// NOP
	}

	/**
	 * �V���{���̒l��Ԃ��܂�
	 */
	public String getValue() {
		return getText();
	}

	/**
	 * �V���{���ɒl��ݒ肵�܂�
	 */
	public void setValue(String value) {
		setText(value);
	}

	/**
	 * �ŏ��l��Ԃ��܂�
	 */
	public double getConvertMin() {
		return 0;
	}

	/**
	 * ���l�\���t�H�[�}�b�g�������Ԃ��܂�
	 */
	public String getFormatString() {
		return "00";
	}

	/**
	 * �����C���{�^�����������ꂽ���̏������L�q���܂��B
	 */
	public void pushButton() {
		List para = new ArrayList();
		para.add(new Integer(this.scheduleDialog.buttonList.indexOf(this)));
		if (this.scheduleDialog.tenkeyDialog != null)
			this.scheduleDialog.tenkeyDialog.dispose();
		this.scheduleDialog.tenkeyDialog =
			getDialog(this.scheduleDialog, this.scheduleDialog, para);
		// this.scheduleDialog.tenkeyDialog.selectAll();
		this.scheduleDialog.tenkeyDialog.show();
	}

	public void setEditable(boolean[] editable) {
	}

	public boolean isEditable() {
		return true;
	}

	/*
	 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
	 */
	public String[] getDestinations() {
		return new String[0];
	}

	/**
	 * �������ݐ�̒ǉ��͂��Ȃ��B
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
	 */
	public void addDestination(Map atts) {
	}

	/**
	 * �������ݐ�̒ǉ��͂��Ȃ��B
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
	 */
	public void addValueSetter(ValueSetter setter) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return isVisible();
	}

	public String getDialogTitle() {
		return hour ? "��" : "��";
	}
}
