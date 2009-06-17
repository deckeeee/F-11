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

package org.F11.scada.server.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.register.HolderString;

/**
 * 指定されたパスにビット情報を出力するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ScheduledWriteTermCommand implements Command {
	/** スレッドプール実行クラス */
	private static ScheduledExecutorService executor =
		Executors.newScheduledThreadPool(1);
	private static Map<HolderString, WriteTermCommandTask> map =
		new ConcurrentHashMap<HolderString, WriteTermCommandTask>();

	/** プロバイダ名 */
	private String provider;
	/** ホルダ名 */
	private String holder;
	/** 書き込む値 */
	private String value;
	/** 遅延する時間 */
	private Long delay;

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	/**
	 * コマンドを実行します
	 * 
	 * @param evt データ変更イベント
	 */
	public void execute(DataValueChangeEventKey evt) {
		if (null == provider) {
			throw new IllegalStateException("providerが設定されていません");
		}
		if (null == holder) {
			throw new IllegalStateException("holderが設定されていません");
		}
		if (null == value) {
			throw new IllegalStateException("valueが設定されていません");
		}
		if (null == delay) {
			throw new IllegalStateException("delayが設定されていません");
		}

		try {
			HolderString key = new HolderString(provider, holder);
			if (!map.containsKey(key)) {
				WriteTermCommandTask command =
					new WriteTermCommandTask(evt, provider, holder, value, map);
				map.put(key, command);
				executor.schedule(command, delay, TimeUnit.SECONDS);
			}
		} catch (RejectedExecutionException e) {
		}
	}
}
