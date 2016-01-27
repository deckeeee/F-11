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

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * ポートへデータ送信し、結果を受信するまで待つクラスのインターフェースです。
 */
public interface ReplyWaiter {
	/**
	 * ポートをクローズします。
	 */
	public void close() throws InterruptedException;

	/**
	 * データを送受信します。
	 * 受信又はタイムアウトするまで返りません。タイムアウト時の受信バッファは空です。
	 * @param sendBuffer 送信バッファへの参照
	 * @param recvBuffer 受信バッファへの参照
	 */
	public void syncSendRecv(ByteBuffer sendBuffer, ByteBuffer recvBuffer)
		throws IOException, InterruptedException;

}
