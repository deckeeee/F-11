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

package org.F11.scada.server.io.postgresql;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.event.WifeCommand;
import org.apache.log4j.Logger;

public class SyncReadWrapper {
	private static Logger log = Logger.getLogger(SyncReadWrapper.class);

	/**
	 * このメソッドは通信エラーを考慮しません。通信エラーを考慮する
	 * {@link SyncReadWrapper#syncRead(Communicater, List, boolean)}を使用して下さい。
	 *
	 * @param communicater 通信オブジェクト
	 * @param commands 通信対象オブジェクト
	 * @return コマンドとバイト列のマップ
	 */
	@Deprecated
	public Map<WifeCommand, byte[]> syncRead(Communicater communicater,
			List<WifeCommand> commands) {
		try {
			return communicater.syncRead(commands, false);
		} catch (InterruptedException e) {
			errorLogging(e);
			return getZeroByteArray(commands);
		} catch (IOException e) {
			errorLogging(e);
			return getZeroByteArray(commands);
		} catch (WifeException e) {
			errorLogging(e);
			return getZeroByteArray(commands);
		}
	}

	/**
	 * 通信コマンドのリストを対象に、通信オブジェクトから値を読み込み、通信コマンドと対応するバイト配列のMapオブジェクトを返します。
	 * 通信エラーが発生している場合、ゼロのバイト配列を返します。
	 *
	 * @param communicater 通信オブジェクト
	 * @param commands 通信コマンドのリスト
	 * @param err 通信エラー状態
	 * @return コマンドとバイト列のマップ
	 */
	public Map<WifeCommand, byte[]> syncRead(Communicater communicater,
			List<WifeCommand> commands,
			boolean err) {
		if (err) {
			return getZeroByteArray(commands);
		}
		try {
			return communicater.syncRead(commands, false);
		} catch (InterruptedException e) {
			errorLogging(e);
			return getZeroByteArray(commands);
		} catch (IOException e) {
			errorLogging(e);
			return getZeroByteArray(commands);
		} catch (WifeException e) {
			errorLogging(e);
			return getZeroByteArray(commands);
		}
	}

	private void errorLogging(Throwable t) {
		// ThreadUtil.printSS(); // for JDK 1.4.x
		log.error("通信エラー", t);
	}

	private Map<WifeCommand, byte[]> getZeroByteArray(Collection<WifeCommand> commands) {
		HashMap<WifeCommand, byte[]> map =
			new HashMap<WifeCommand, byte[]>(commands.size());
		for (Iterator<WifeCommand> i = commands.iterator(); i.hasNext();) {
			WifeCommand command = i.next();
			byte[] data = new byte[command.getWordLength() * 2];
			Arrays.fill(data, (byte) 0x00);
			map.put(command, data);
		}
		return map;
	}
}
