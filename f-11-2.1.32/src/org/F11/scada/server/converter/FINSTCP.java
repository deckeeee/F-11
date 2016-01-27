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
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;
import org.apache.log4j.Logger;

/**
 * Wife通信モジュールのFINS/TCPコマンド生成の実装クラス。
 */
public class FINSTCP extends AbstractFINS {
	private final Logger log = Logger.getLogger(FINSTCP.class);

	private byte[] finsTcpHeader = new byte[] {
		(byte) 0x46, // FINS/TCPヘッダ
		(byte) 0x49,
		(byte) 0x4E,
		(byte) 0x53,
		(byte) 0x00, // データ長(コマンド以降のデータ長)
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x1A,
		(byte) 0x00, // コマンド(2固定)
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x02,
		(byte) 0x00, // エラーコード(送信時は未使用)
		(byte) 0x00,
		(byte) 0x00,
		(byte) 0x00 };

	private byte[] getFinsTcpHeader() {
		return finsTcpHeader;
	}

	protected int getReceiveSize() {
		return 998 * 2;
	}

	protected int getSendSize() {
		return 993 * 2;
	}

	/** 環境を設定し、レスポンスヘッダを返す。 */
	public byte[] setEnvironment(Environment device) {
		byte[] finsHeader = getFinsTcpHeader();
		byte[] tmp = super.setEnvironment(device);

		byte[] resp = new byte[finsHeader.length + tmp.length];
		System.arraycopy(finsHeader, 0, resp, 0, finsHeader.length);
		System.arraycopy(tmp, 0, resp, finsHeader.length, tmp.length);
		resp[7] = (byte) 0x18;
		return resp;
	}

	@Override
	public void setReadCommand(WifeCommand commdef) throws WifeException {
		// データ形式によってFINS/TCPヘッダのデータ長を変更
		if (PLCST_AREA.intValue() == commdef.getMemoryMode()) {
			// PLCステータス読込みコマンド
			finsTcpHeader[6] = (byte) 0x00;
			finsTcpHeader[7] = (byte) 0x14;
		} else if (PLCTM_AREA.intValue() == commdef.getMemoryMode()) {
			// 時計読込みコマンド
			finsTcpHeader[6] = (byte) 0x00;
			finsTcpHeader[7] = (byte) 0x14;
		} else {
			// 読込みコマンド
			finsTcpHeader[6] = (byte) 0x00;
			finsTcpHeader[7] = (byte) 0x1A;
		}
		super.setReadCommand(commdef);
	}

	@Override
	public void setWriteCommand(WifeCommand commdef, byte[] data)
		throws WifeException {
		// データ形式によってFINS/TCPヘッダのデータ長を変更
		if (PLCTM_AREA.intValue() == commdef.getMemoryMode()) {
			// 時計書込みコマンド
			finsTcpHeader[6] = (byte) 0x00;
			finsTcpHeader[7] = (byte) 0x1B;
		} else {
			// 書込みコマンド
			finsTcpHeader[6] = (byte) ((26  + data.length) / 0x100);
			finsTcpHeader[7] = (byte) ((26  + data.length) % 0x100);
		}
		super.setWriteCommand(commdef, data);
	}

	@Override
	public void nextCommand(ByteBuffer sendBuffer) {
		// FINS/TCPヘッダを追加
		sendBuffer.put(getFinsTcpHeader());
		super.nextCommand(sendBuffer);
	}

	@Override
	public void retryCommand(ByteBuffer sendBuffer) {
		// FINS/TCPヘッダを追加
		sendBuffer.put(getFinsTcpHeader());
		super.retryCommand(sendBuffer);
	}

	@Override
	public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
		// FINS/TCPヘッダを削除してFINSコンバーターに渡す
		ByteBuffer b = ByteBuffer.allocate(recvBuffer.limit());
		log.debug(recvBuffer);
		log.debug(WifeUtilities.toString(recvBuffer));
		recvBuffer.position(getFinsTcpHeader().length);
		log.debug(recvBuffer);
		b.put(recvBuffer).flip();
		if (log.isDebugEnabled()) {
			log.debug(WifeUtilities.toString(b));
		}
		return super.checkCommandResponce(b);
	}

	@Override
	public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		// FINS/TCPヘッダを削除してFINSコンバーターに渡す
		ByteBuffer b = ByteBuffer.allocate(recvBuffer.limit());
		recvBuffer.position(getFinsTcpHeader().length);
		b.put(recvBuffer).flip();
		super.getResponceData(b, recvData);
		if (log.isDebugEnabled()) {
			log.debug(WifeUtilities.toString(recvData));
		}
	}

}
