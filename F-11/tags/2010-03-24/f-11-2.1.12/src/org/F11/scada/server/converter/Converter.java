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

package org.F11.scada.server.converter;

import java.nio.ByteBuffer;

import org.F11.scada.WifeException;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * Wife通信モジュールのコマンド生成インターフェイス。
 */
public interface Converter {
	/** 環境を設定し、レスポンスヘッダを返す。*/
	public byte[] setEnvironment(Environment device);

	/** 読込みコマンドを設定する。*/
	public void setReadCommand(WifeCommand commdef) throws WifeException;
	/** 書込みコマンドを設定する。*/
	public void setWriteCommand(WifeCommand commdef, byte[] data)
		throws WifeException;
	/** コマンドが取得可能か？ */
	public boolean hasCommand();
	/** コマンドを作成し、次回のコマンドを準備します。 */
	public void nextCommand(ByteBuffer sendBuffer);
	/** 前回実行コマンドを作成します。 */
	public void retryCommand(ByteBuffer sendBuffer);

	/** 送信データと受信データの整合性を検査します。 */
	public WifeException checkCommandResponce(ByteBuffer recvBuffer)
		throws WifeException;
	/** 受信データからデータ部を取得します。 */
	public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
	/** 通信文の最大長を返します。 */
	public int getPacketMaxSize(WifeCommand commdef);
}
