/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/WifeCommand.java,v 1.12.4.4 2006/03/20 07:48:37 frdm Exp $
 * $Revision: 1.12.4.4 $
 * $Date: 2006/03/20 07:48:37 $
 * 
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

package org.F11.scada.server.event;

import java.util.Comparator;

import org.F11.scada.data.WifeData;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.WifeDataUtil;

/**
 * 通信コマンド定義データを表すクラスです。
 * @todo 定数のところを enum クラスパターンにリファクタリング
 */
public final class WifeCommand {
	/**
	 * ソート用コンパレータ
	 */
	public static final Comparator comp = new Comparator() {
		public int compare(Object o1, Object o2) {
			WifeCommand wc1 = (WifeCommand) o1;
			WifeCommand wc2 = (WifeCommand) o2;
			if (wc1.deviceID.compareTo(wc2.deviceID) < 0)
				return -1;
			else if (wc1.deviceID.compareTo(wc2.deviceID) > 0)
				return 1;

			if (wc1.cycleTime < wc2.cycleTime)
				return -1;
			else if (wc1.cycleTime > wc2.cycleTime)
				return 1;

			if (wc1.cycleMode < wc2.cycleMode)
				return -1;
			else if (wc1.cycleMode > wc2.cycleMode)
				return 1;

			if (wc1.memoryMode < wc2.memoryMode)
				return -1;
			else if (wc1.memoryMode > wc2.memoryMode)
				return 1;

			if (wc1.memoryAddress < wc2.memoryAddress)
				return -1;
			else if (wc1.memoryAddress > wc2.memoryAddress)
				return 1;

			if (wc1.wordLength < wc2.wordLength)
				return -1;
			else if (wc1.wordLength > wc2.wordLength)
				return 1;

			return 0;
		}
	};

	/** 動作しないコマンドを表します。読込みも書込もしないコマンドとして利用します。 */
	private static final WifeCommand NULL_COMMAND =
		new WifeCommand("NullWifeCommand", 0, 0, 0, 0, 0);

	/** 通信対象のPLCIDです。 */
	private final String deviceID;
	/** 通信周期です。 */
	private final int cycleTime;
	/** 常時通信種別です。 */
	private final int cycleMode;
	/** メモリ種別です。 */
	private final int memoryMode;
	/** 操作対象のメモリアドレスです。 */
	private final long memoryAddress;
	/** 通信対象データのワード長です。 */
	private final int wordLength;

	/**
	 * 通信コマンド定義を表すオブジェクトを作成します。
	 * @param deviceID デバイスID
	 * @param cycleTime 通信周期
	 * @param cycleMode 常時通信種別
	 * @param memoryMode メモリ種別
	 * @param memoryAddress 操作対象のメモリアドレス
	 * @param wordLength 通信対象データのワード長
	 * @param writeData 書込データ
	 * @param accessTime アクセスタイム(最後に通信した時刻)
	 */
	public WifeCommand(
		String deviceID,
		int cycleTime,
		int cycleMode,
		int memoryMode,
		long memoryAddress,
		int wordLength) {
		if (deviceID == null) {
			throw new IllegalArgumentException("deviceID is null.");
		}

		this.deviceID = deviceID;
		this.cycleTime = cycleTime;
		this.cycleMode = cycleMode;
		this.memoryMode = memoryMode;
		this.memoryAddress = memoryAddress;
		this.wordLength = wordLength;
	}

	public WifeCommand(Item item) {
	    if (item == null) {
	        throw new IllegalArgumentException("item is null.");
	    }
	    this.deviceID = item.getProvider();
	    this.cycleTime = item.getComCycle();
	    if (item.isComCycleMode()) {
	        this.cycleMode = 1;
	    } else {
	        this.cycleMode = 0;
	    }
	    this.memoryMode = item.getComMemoryKinds();
	    this.memoryAddress = item.getComMemoryAddress();
	    WifeData wd = WifeDataUtil.getWifeData(item);
	    this.wordLength = wd.getWordSize();
	}
	/**
	 * デバイスIDを返します。
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * 常時通信種別を返します。
	 */
	public int getCycleMode() {
		return cycleMode;
	}

	/**
	 * 常時通信周期を返します。
	 */
	public int getCycleTime() {
		return cycleTime;
	}

	/**
	 * メモリ種別を返します。
	 */
	public int getMemoryMode() {
		return memoryMode;
	}

	/**
	 * 操作対象のメモリアドレスを返します。
	 */
	public long getMemoryAddress() {
		return memoryAddress;
	}

	/**
	 * 通信対象データのワード数を返します。
	 */
	public int getWordLength() {
		return wordLength;
	}

	/**
	 * 常時通信文ならtrueを返します。
	 */
	public boolean isCycleRead() {
		return cycleMode == 0;
	}

	/**
	 * このオブジェクトと他のオブジェクトが等しいかどうかを示します。
	 * 比較対象のオブジェクトが WifeCommand で、値が同じ場合は true を返します。
	 * 
	 * @param obj 比較対象オブジェクト
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeCommand)) {
			return false;
		}
		WifeCommand wc = (WifeCommand) obj;

		return deviceID.equals(wc.deviceID)
			&& cycleTime == wc.cycleTime
			&& cycleMode == wc.cycleMode
			&& memoryMode == wc.memoryMode
			&& memoryAddress == wc.memoryAddress
			&& wordLength == wc.wordLength;

	}

	/**
	 * この WifeCommand のハッシュコードを返します。
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + deviceID.hashCode();
		result = 37 * result + cycleTime;
		result = 37 * result + cycleMode;
		result = 37 * result + memoryMode;
		result = 37 * result + (int) (memoryAddress ^ (memoryAddress >>> 32));
		result = 37 * result + wordLength;

		return result;
	}

	/**
	 * オブジェクトの文字列表現を返します。
	 */
	public String toString() {
		StringBuffer s = new StringBuffer();

		s.append("{DeviceID=").append(deviceID).append(", ");
		s.append("cycleMode=").append(cycleMode).append(", ");
		s.append("memoryMode=").append(memoryMode).append(", ");
		s.append("memoryAddress=").append(memoryAddress).append(", ");
		s.append("wordLength=").append(wordLength).append("}");
		return s.toString();
	}

	/**
	 * 動作しないコマンドを返します。読込みも書込もしないコマンドとして利用します。
	 * @return 動作しないコマンド(NULL_COMMAND)
	 */
	public static WifeCommand getNullCommand() {
		return NULL_COMMAND;
	}

	/**
	 * メモリアドレスとワード長を指定して、新しいWifeCommandオブジェクトを作成します。
	 * @param memoryAddress メモリアドレス
	 * @param wordLength ワード長
	 * @return 新しいオブジェクト
	 */
	public WifeCommand createCommand(long memoryAddress, int wordLength) {
		return new WifeCommand(
			this.deviceID,
			this.cycleTime,
			this.cycleMode,
			this.memoryMode,
			memoryAddress,
			wordLength);
	}
}
