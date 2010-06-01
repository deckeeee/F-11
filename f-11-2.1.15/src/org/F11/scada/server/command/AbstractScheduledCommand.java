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

import org.F11.scada.server.alarm.DataValueChangeEventKey;

/**
 * 指定されたパスにビット情報を出力するクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public abstract class AbstractScheduledCommand implements Command {
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

	public String getProvider() {
		return provider;
	}

	public String getHolder() {
		return holder;
	}

	public String getValue() {
		return value;
	}

	public Long getDelay() {
		return delay;
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

		schedule(evt);
	}

	abstract protected void schedule(DataValueChangeEventKey evt);
}
