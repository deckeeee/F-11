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

package org.F11.scada.server.frame;

import java.util.Timer;
import java.util.TimerTask;

/**
 * �^�C���A�E�gPage���폜����N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageTimeout {
	/** �f�t�H���g�̃^�C���A�E�g����(10��) */
	private static final long DEFAULT_TIMEOUT = 1000L * 60L * 10L;

	/** �^�C���A�E�g���� */
	private long timeout;

	/** �^�C�}�[ */
	private final Timer timer;

	/**
	 * �f�t�H���g�̃^�C���A�E�g����(10��)�Ń^�C�}�[�����������܂��B
	 */
	public PageTimeout() {
		this(DEFAULT_TIMEOUT);
	}

	/**
	 * �������Ń^�C���A�E�g���Ԏw�肵�āA�^�C�}�[�����������܂��B
	 * 
	 * @param timeout �^�C���A�E�g����
	 */
	public PageTimeout(long timeout) {
		timer = new Timer();
		this.timeout = timeout;
	}

	/**
	 * �����̃\�[�g�}�b�v��ΏۂɃ^�C���A�E�g�̃��R�[�h���폜���܂��B
	 */
	public void schedule(JimRegister register) {
		timer.schedule(new PageRemoveTask(register, timeout), 0, (timeout / 2));
	}

	static class PageRemoveTask extends TimerTask {
		private JimRegister register;

		private long timeout;

		PageRemoveTask(JimRegister register, long timeout) {
			this.register = register;
			this.timeout = timeout;
		}

		public void run() {
			removePage();
		}

		private void removePage() {
			long now = System.currentTimeMillis();
			long current = now - timeout;
			register.removePages(current);
		}
	}
}
