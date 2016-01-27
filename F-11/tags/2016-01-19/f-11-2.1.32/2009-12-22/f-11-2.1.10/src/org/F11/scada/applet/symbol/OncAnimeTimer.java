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
 * �A�j���[�V�����\���̃^�C�}�[�I�u�W�F�N�g�ł��B
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class OncAnimeTimer extends Timer implements ActionListener {
	private static final long serialVersionUID = -4749840783967552311L;
	/** �B��̃C���X�^���X�B�V���O���g���p�^�[�� */
	private static OncAnimeTimer singleton = new OncAnimeTimer();
	/** �f�t�H���g�̃C�x���g�Ԋu���� */
	public static final int INITIAL_DELAY = 200;
	/** �A�j���[�V�����J�E���^ */
	private int animeCount;

	/**
	 * �R���X�g���N�^
	 */
	private OncAnimeTimer() {
		super(INITIAL_DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		/** �������g��o�^���܂��B */
		addActionListener(this);
		start();
	}

	/**
	 * �C���X�^���X��Ԃ��܂��B
	 */
	public static OncAnimeTimer getInstance() {
		return singleton;
	}

	/**
	 * �A�j���[�V�����J�E���^�擾
	 * @para maxIconCount �A�j���[�V��������Icon�̍ő吔
	 */
	public int getAnimeCount(int maxIconCount) {
		if (maxIconCount <= 0)
			return 0;
		return animeCount % maxIconCount;
	}

	/**
	 * �^�C�}�[�C�x���g�����B
	 */
	public void actionPerformed(ActionEvent e) {
		if (animeCount < 362880)
			animeCount++;
		else
			animeCount = 0;
	}
}
