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
 * タイムアウトPageを削除するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageTimeout {
	/** デフォルトのタイムアウト時間(10分) */
	private static final long DEFAULT_TIMEOUT = 1000L * 60L * 10L;

	/** タイムアウト時間 */
	private long timeout;

	/** タイマー */
	private final Timer timer;

	/**
	 * デフォルトのタイムアウト時間(10分)でタイマーを初期化します。
	 */
	public PageTimeout() {
		this(DEFAULT_TIMEOUT);
	}

	/**
	 * を引数でタイムアウト時間指定して、タイマーを初期化します。
	 * 
	 * @param timeout タイムアウト時間
	 */
	public PageTimeout(long timeout) {
		timer = new Timer();
		this.timeout = timeout;
	}

	/**
	 * 引数のソートマップを対象にタイムアウトのレコードを削除します。
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
