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

package org.F11.scada.applet.symbol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * �A�j���[�V�����p�^�[���̃^�C�}�[�I�u�W�F�N�g�ł��B
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class AnimeTimer extends Timer implements ActionListener {
    private static final long serialVersionUID = 8904981111536645188L;
	/** �B��̃C���X�^���X�B�V���O���g���p�^�[�� */
	private static final AnimeTimer singleton = new AnimeTimer();
	/** �f�t�H���g�̃C�x���g�Ԋu���� */
	private static final int INITIAL_DELAY = 200;
	/** �ő�A�j���[�V�����J�E���^ */
	private static final int MAX_ANIME_COUNT = 362880;
	/** �A�j���[�V�����J�E���^ */
	private int animeCount;

	/**
	 * �R���X�g���N�^
	 */
	private AnimeTimer() {
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
	public static AnimeTimer getInstance() {
		return singleton;
	}

	/**
	 * �A�j���[�V�����J�E���^�擾
	 * @para maxIconCount �A�j���[�V��������Icon�̍ő吔
	 */
	public int getAnimeCount(int maxIconCount) {
		if (maxIconCount <= 0) {
			return 0;
		} else {
		    return animeCount % maxIconCount;
		}
	}

	/**
	 * �^�C�}�[�C�x���g�����B
	 */
	public void actionPerformed(ActionEvent e) {
		if (animeCount < MAX_ANIME_COUNT) {
			animeCount++;
		} else {
			animeCount = 0;
		}
	}
}
