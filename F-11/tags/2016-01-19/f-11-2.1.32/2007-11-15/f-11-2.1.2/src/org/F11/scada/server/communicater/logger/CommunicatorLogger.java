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
 */

package org.F11.scada.server.communicater.logger;

import org.apache.log4j.Logger;

/**
 * 通信処理のロギングを一定回数間隔で出力する。
 * 
 * 通信処理のロギングを全て出力すると、すぐに溢れるのでこのクラス
 * を使用して、一定間隔でログ出力を行う。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CommunicatorLogger {
	/** Logging API */
	private static Logger log = Logger.getLogger(CommunicatorLogger.class);
	/** ログ出力カウンタ */
	private int count;
	/** この件数毎にログ出力を行います。 */
	private final int max;

	/**
	 * デフォルトの件数(1000件)毎にログ出力するように初期化します。
	 */
	public CommunicatorLogger() {
		this(1000);
	}

	/**
	 * 引数の件数毎にログ出力するように初期化します。
	 * @param max ログ出力する件数
	 */	
	public CommunicatorLogger(int max) {
		this.max = max;
		count = max;
	}

	/**
	 * ロギング出力します。
	 * @param o ロギング情報
	 */
	public void log(Object o) {
		if (count >= max) {
			if (log.isDebugEnabled()) {
				log.debug(o);
			}
			count = 0;
		} else {
			count++;
		}
	}
}
