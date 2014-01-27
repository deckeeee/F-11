/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.event.WifeEventListener;

/**
 * 外部機器との通信インターフェースです。
 */
public interface Communicater {
	/**
	 * このCommunicaterを閉じます。
	 */
	public void close() throws InterruptedException;

	/**
	 * 読込み通信コマンドの追加。（任意実装） 次回同期読込みを最適化する為、予め登録しておきます。
	 * @param commands
	 */
	public void addReadCommand(Collection<WifeCommand> commands);

	/**
	 * 読込み通信コマンドの削除。（任意実装）
	 * @param commands
	 */
	public void removeReadCommand(Collection<WifeCommand> commands);

	/**
	 * 通信イベントを受け取るリスナーを追加します。（任意実装）
	 * @param l リスナーオブジェクト
	 */
	public void addWifeEventListener(WifeEventListener l);

	/**
	 * 通信イベントを受け取るリスナーを削除します。（任意実装）
	 * @param l リスナーオブジェクト
	 */
	public void removeWifeEventListener(WifeEventListener l);

	/**
	 * 同期読込み。読込み通信が完了するまでブロックします。sameDataBalk を true にした場合と同様です。
	 * @param commands WifeCommandのコレクション
	 * @return コマンドオブジェクト(WifeCommand)とバイト配列のマップを返します。
	 */
	public Map<WifeCommand, byte[]> syncRead(Collection<WifeCommand> commands)
			throws InterruptedException, IOException, WifeException;

	/**
	 * 同期読込み。読込み通信が完了するまでブロックします。
	 * @param commands WifeCommandのコレクション
	 * @param sameDataBalk true の場合、前回通信結果が同じ場合空のマップを返します。false の場合は常に通信結果を返します。
	 * @return コマンドオブジェクト(WifeCommand)とバイト配列のマップを返します。
	 */
	public Map<WifeCommand, byte[]> syncRead(Collection<WifeCommand> commands, boolean sameDataBalk)
			throws InterruptedException, IOException, WifeException;

	/**
	 * 同期書込み。書込み通信が完了するまでブロックします。
	 * @param commands WifeCommandをキーにしたbyte[]のMap
	 */
	public void syncWrite(Map<WifeCommand, byte[]> commands)
			throws InterruptedException, IOException, WifeException;
}
