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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * �V���{���̓_�œ��ɕK�v�ȓ����C�x���g�𔭐����܂��B
 * �R���X�g���N�^��private�����Ȃ̂�new �ŃC���X�^���X���쐬���邱�Ƃ͂ł��܂���B
 * �����{@link WifeTimer#getInstance() getInstance}�ŃC���X�^���X���擾���܂��B
 * @see javax.swing.Timer
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeTimer extends Timer implements ActionListener{
	private static final long serialVersionUID = 3393412384209563638L;
	/** �B��̃C���X�^���X�B�V���O���g���p�^�[�� */
	private static WifeTimer singleton = new WifeTimer();
	/** �f�t�H���g�̃C�x���g�Ԋu���� */
	public static final int INITIAL_DELAY = 500;
	/** �u�����N��� */
	private volatile boolean showTime;

	/**
	 * �R���X�g���N�^
	 */
	private WifeTimer() {
		super(INITIAL_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		this.addActionListener(this);
		start();
	}

	/**
	 * �C���X�^���X��Ԃ��܂��B
	 */
	public static WifeTimer getInstance() {
		return singleton;
	}

	/**
	 * �������g�̃C�x���g����
	 */
	public void actionPerformed(ActionEvent e) {
		showTime = (showTime ? false : true);
	}

	/**
	 * �\��/��\����Ԃ�Ԃ��܂��B
	 */
	public boolean isShowTime() {
		return showTime;
	}
}
