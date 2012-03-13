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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * Wife通信モジュールのFINSコマンド生成の基底クラス。
 */
public abstract class AbstractFINS implements Converter {
	/** FINSコマンドヘッダ長 */
	private static final int FINS_COMMAND_HEADER_LENGTH = 14;

	/** メモリ種別 DMのデータメモリを表します。 */
	public static final Integer DM_AREA = new Integer(0);
	/** メモリ種別 IOのリレーエリアを表します。 */
	public static final Integer CIO_AREA = new Integer(1);
	/** メモリ種別 HRのリレーエリアを表します。 */
	public static final Integer HR_AREA = new Integer(2);
	/** メモリ種別 WRのリレーエリアを表します。 */
	public static final Integer WR_AREA = new Integer(3);
	/** メモリ種別 の拡張メモリバンク 0 ～ C を表します。 */
	public static final Integer EM0_AREA = new Integer(10);
	public static final Integer EM1_AREA = new Integer(11);
	public static final Integer EM2_AREA = new Integer(12);
	public static final Integer EM3_AREA = new Integer(13);
	public static final Integer EM4_AREA = new Integer(14);
	public static final Integer EM5_AREA = new Integer(15);
	public static final Integer EM6_AREA = new Integer(16);
	public static final Integer EM7_AREA = new Integer(17);
	public static final Integer EM8_AREA = new Integer(18);
	public static final Integer EM9_AREA = new Integer(19);
	public static final Integer EMA_AREA = new Integer(20);
	public static final Integer EMB_AREA = new Integer(21);
	public static final Integer EMC_AREA = new Integer(22);
	/** 仮想メモリ種別 PLCステータスを表します。 */
	public static final Integer PLCST_AREA = new Integer(90);
	/** 仮想メモリ種別 PLC時計を表します。 */
	public static final Integer PLCTM_AREA = new Integer(91);

	/** メモリ種別のマップ */
	private static final Map<Integer, Byte> memoryModeMap;
	static {
		memoryModeMap = new HashMap<Integer, Byte>();
		memoryModeMap.put(DM_AREA, new Byte((byte) 0x82));
		memoryModeMap.put(CIO_AREA, new Byte((byte) 0xB0));
		memoryModeMap.put(HR_AREA, new Byte((byte) 0xB2));
		memoryModeMap.put(WR_AREA, new Byte((byte) 0xB1));
		memoryModeMap.put(EM0_AREA, new Byte((byte) 0xA0));
		memoryModeMap.put(EM1_AREA, new Byte((byte) 0xA1));
		memoryModeMap.put(EM2_AREA, new Byte((byte) 0xA2));
		memoryModeMap.put(EM3_AREA, new Byte((byte) 0xA3));
		memoryModeMap.put(EM4_AREA, new Byte((byte) 0xA4));
		memoryModeMap.put(EM5_AREA, new Byte((byte) 0xA5));
		memoryModeMap.put(EM6_AREA, new Byte((byte) 0xA6));
		memoryModeMap.put(EM7_AREA, new Byte((byte) 0xA7));
		memoryModeMap.put(EM8_AREA, new Byte((byte) 0xA8));
		memoryModeMap.put(EM9_AREA, new Byte((byte) 0xA9));
		memoryModeMap.put(EMA_AREA, new Byte((byte) 0xAA));
		memoryModeMap.put(EMB_AREA, new Byte((byte) 0xAB));
		memoryModeMap.put(EMC_AREA, new Byte((byte) 0xAC));
	}
	/** FINS通信シーケンシャルID */
	protected byte sid;

	/** FINSヘッダデータ */
	private byte[] head =
		{
			(byte) 0x80,
			(byte) 0x00,
			(byte) 0x02,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00,
			(byte) 0x00 };

	/**
	 * 標準のFINSコマンド受信パケット最大サイズを返します。
	 *
	 * @return 標準のFINSコマンド受信パケット最大サイズを返します。
	 */
	protected abstract int getReceiveSize();

	/**
	 * 標準のFINSコマンド送信パケット最大サイズを返します。
	 *
	 * @return 標準のFINSコマンド送信パケット最大サイズを返します。
	 */
	protected abstract int getSendSize();

	/** 環境を設定し、レスポンスヘッダを返す。 */
	public byte[] setEnvironment(Environment device) {
		head[3] = (byte) device.getPlcNetNo();
		head[4] = (byte) device.getPlcNodeNo();
		head[5] = (byte) device.getPlcUnitNo();
		head[6] = (byte) device.getHostNetNo();
		head[7] = (byte) device.getHostAddress();

		byte[] resp = new byte[head.length];
		resp[0] = (byte) 0xc0;
		resp[1] = (byte) 0x00;
		resp[2] = (byte) 0x02;
		resp[3] = head[6];
		resp[4] = head[7];
		resp[5] = head[8];
		resp[6] = head[3];
		resp[7] = head[4];
		resp[8] = head[5];
		return resp;
	}

	/** 読込みコマンドを設定する。 */
	public void setReadCommand(WifeCommand commdef) throws WifeException {
		if (PLCST_AREA.intValue() == commdef.getMemoryMode()) {
			// PLCステータス読込みコマンド
			finsCommand = STATREAD_FINSCOMMAND;
		} else if (PLCTM_AREA.intValue() == commdef.getMemoryMode()) {
			// 時計読込みコマンド
			finsCommand = TIMEREAD_FINSCOMMAND;
		} else {
			// 読込みコマンド
			finsCommand = READ_FINSCOMMAND;
		}
		finsCommand.setCommand(commdef, null);
	}

	/** 書込みコマンドを設定する。 */
	public void setWriteCommand(WifeCommand commdef, byte[] data)
		throws WifeException {
		if (PLCTM_AREA.intValue() == commdef.getMemoryMode()) {
			// 時計書込みコマンド
			finsCommand = TIMEWRITE_FINSCOMMAND;
		} else {
			// 書込みコマンド
			finsCommand = WRITE_FINSCOMMAND;
		}
		finsCommand.setCommand(commdef, data);
	}

	/** コマンドが取得可能か？ */
	public boolean hasCommand() {
		return finsCommand.hasCommand();
	}

	/** コマンドを作成し、次回のコマンドを準備します。 */
	public void nextCommand(ByteBuffer sendBuffer) {
		sendBuffer.put(head);
		incrementSid();
		sendBuffer.put(sid);
		finsCommand.nextCommand(sendBuffer);
	}

	/** 前回実行コマンドを作成します。 */
	public void retryCommand(ByteBuffer sendBuffer) {
		sendBuffer.put(head);
		sendBuffer.put(sid);
		finsCommand.retryCommand(sendBuffer);
	}

	/** 送信データと受信データの整合性を検査します。 */
	public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
		byte[] err = { 0, 0 };
		if (recvBuffer.remaining() < FINS_COMMAND_HEADER_LENGTH) {
			StringBuffer sb = new StringBuffer();
			sb.append("RecvData (");
			sb.append(WifeUtilities.toString(recvBuffer));
			sb.append(") is short!");
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_ERROR, sb.toString());
		}
		// SID整合
		if (recvBuffer.get(9) != sid) {
			StringBuffer sb = new StringBuffer();
			sb.append("Head error: send ");
			sb.append(WifeUtilities.toString(head));
			byte[] s = new byte[1];
			s[0] = sid;
			sb.append(WifeUtilities.toString(s));
			sb.append(" recv ").append(
					WifeUtilities.toString(recvBuffer, 0, 10));
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_HEAD_ERROR, sb.toString());
		}
		// コマンドコード
		WifeException ec = finsCommand.checkCommandResponce(recvBuffer);
		if (ec != null) {
			return ec;
		}
		// 終了コード
		if (recvBuffer.get(12) != (byte) 0x00
			|| (recvBuffer.get(13) & 0x3f) != 0x00) {
			err[0] = recvBuffer.get(12);
			err[1] = recvBuffer.get(13);
			StringBuffer sb = new StringBuffer();
			sb.append("End code error: ");
			sb.append(WifeUtilities.toString(err));
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_ENDCODE_ERROR, err, sb
							.toString());
		}
		// レスポンスデータ長
		if (finsCommand.getResponceLength() != recvBuffer.remaining()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Expected length ").append(
					finsCommand.getResponceLength());
			sb.append(" != RecvData length ").append(recvBuffer.remaining());
			return new WifeException(WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_RESPONCE_ERROR, sb.toString());
		}
		return null;
	}

	/** 受信データからデータ部を取得します。 */
	public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		finsCommand.getResponceData(recvBuffer, recvData);
	}

	/** 通信文の最大長を返します。 */
	public int getPacketMaxSize(WifeCommand commdef) {
		return getReceiveSize() / 2;
	}

	/**
	 * FINS通信シーケンシャルID更新
	 */
	private void incrementSid() {
		sid++;
		if (127 < sid)
			sid = 0;
	}

	/**
	 * 文字列表現を返します。
	 */
	public String toString() {
		return finsCommand.toString();
	}

	/**
	 * コマンド変換ヘルパークラスのインターフェイスです。
	 */
	private interface FinsCommand {
		/** 生成するコマンドを設定する。 */
		public void setCommand(WifeCommand commdef, byte[] data)
			throws WifeException;

		/** コマンドが取得可能か？ */
		public boolean hasCommand();

		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer);

		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer);

		/** コマンドからレスポンスデータのバイト長を取得します。 */
		public int getResponceLength();

		/** 送信データと受信データの整合性を検査します。 */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer);

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
	}

	/**
	 * 読込みコマンド変換ヘルパークラスのインスタンスです。
	 */
	private final FinsCommand READ_FINSCOMMAND = new FinsCommand() {
		/** PLCメモリ種別 */
		private byte memoryMode;
		/** PLCメモリアドレス */
		private long memoryAddress;
		/** 処理すべきPLCデータの残りバイト数 */
		private int restByteLength;
		/** 今回のPLCデータバイト数 */
		private int thisByteLength;

		/** 生成するコマンドを設定する。 */
		public void setCommand(WifeCommand commdef, byte[] data)
			throws WifeException {
			Byte mm =
				(Byte) memoryModeMap.get(new Integer(commdef.getMemoryMode()));
			if (mm == null) {
				throw new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_COMMAND_ERROR,
						"Not supported memory mode " + commdef.getMemoryMode());
			}
			this.memoryMode = mm.byteValue();

			this.memoryAddress = commdef.getMemoryAddress();
			this.restByteLength = commdef.getWordLength() * 2;
			this.thisByteLength = 0;
		}

		/** コマンドが取得可能か？ */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer) {
			// デバイスに依存する長さを超えていれば
			if (getReceiveSize() < restByteLength) {
				thisByteLength = getReceiveSize();
				restByteLength -= getReceiveSize();
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// PLCコマンド種別
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x01);
			// 通常コマンド
			// PLCメモリ種別
			sendBuffer.put(memoryMode);
			// メモリアドレス、長さ
			sendBuffer.put((byte) (memoryAddress / 0x100));
			sendBuffer.put((byte) (memoryAddress % 0x100));
			sendBuffer.put((byte) 0x00); // ビット指定、ワード読込の場合は常に0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			// 次回のアドレス
			memoryAddress += (thisByteLength / 2);
		}

		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = memoryAddress - (thisByteLength / 2);

			// PLCコマンド種別
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x01);
			// 通常コマンド
			// PLCメモリ種別
			sendBuffer.put(memoryMode);
			// メモリアドレス、長さ
			sendBuffer.put((byte) (addr / 0x100));
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) 0x00); // ビット指定、ワード読込の場合は常に0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
		}

		/** コマンドからレスポンスデータのバイト長を取得します。 */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH + thisByteLength;
		}

		/** 送信データと受信データの整合性を検査します。 */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// コマンドコード
			if ((byte) 0x01 != recvBuffer.get(10)
				|| (byte) 0x01 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0101 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
			recvBuffer.position(FINS_COMMAND_HEADER_LENGTH);
			recvData.put(recvBuffer);
		}

		/**
		 * このインスタンスの文字列表現を返します。
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("ReadMode:").append("\n");
			s.append("memoryMode:").append(memoryMode).append("\n");
			s.append("memoryAddress:").append(memoryAddress).append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/**
	 * PLCステータス読込みコマンド変換ヘルパークラスのインスタンスです。
	 */
	private final FinsCommand STATREAD_FINSCOMMAND = new FinsCommand() {
		/** PLCメモリアドレス */
		private long memoryAddress;
		/** 処理すべきPLCデータの残りバイト数 */
		private int restByteLength;
		/** 今回のPLCデータバイト数 */
		private int thisByteLength;

		/** 要求されたデータバイト数 */
		// private int rreqByteLength;

		/** 生成するコマンドのネタを設定する。 */
		public void setCommand(WifeCommand commdef, byte[] data) {
			this.memoryAddress = commdef.getMemoryAddress();
			this.restByteLength = 13 * 2;
			// this.rreqByteLength = commdef.getWordLength() * 2;
			this.thisByteLength = 0;
		}

		/** コマンドが取得可能か？ */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer) {
			// デバイスに依存する長さを超えていれば
			if (getReceiveSize() < restByteLength) {
				thisByteLength = getReceiveSize();
				restByteLength -= getReceiveSize();
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// PLCコマンド種別
			sendBuffer.put((byte) 0x06);
			sendBuffer.put((byte) 0x01);
		}

		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer) {
			// PLCコマンド種別
			sendBuffer.put((byte) 0x06);
			sendBuffer.put((byte) 0x01);
		}

		/** コマンドからレスポンスデータのバイト長を取得します。 */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH + thisByteLength;
		}

		/** 送信データと受信データの整合性を検査します。 */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// コマンドコード
			if ((byte) 0x06 != recvBuffer.get(10)
				|| (byte) 0x01 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0601 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
			recvBuffer.position(FINS_COMMAND_HEADER_LENGTH
				+ ((int) memoryAddress * 2));
			recvData.put(recvBuffer);
		}

		/**
		 * このインスタンスの文字列表現を返します。
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("StatusReadMode:").append("\n");
			s.append("memoryAddress:").append(memoryAddress).append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/**
	 * 書込みコマンド変換ヘルパークラスのインスタンスです。
	 */
	private final FinsCommand WRITE_FINSCOMMAND = new FinsCommand() {
		/** PLCメモリ種別 */
		private byte memoryMode;
		/** PLCメモリアドレス */
		private long memoryAddress;
		/** 書込みデータ */
		private byte[] writeData;
		/** 書込みデータ位置 */
		private int writePos;
		/** 処理すべきPLCデータの残りバイト数 */
		private int restByteLength;
		/** 今回のPLCデータバイト数 */
		private int thisByteLength;

		/** 生成するコマンドのネタを設定する。 */
		public void setCommand(WifeCommand commdef, byte[] data)
			throws WifeException {
			Byte mm =
				(Byte) memoryModeMap.get(new Integer(commdef.getMemoryMode()));
			if (mm == null) {
				throw new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_COMMAND_ERROR,
						"Not supported memory mode " + commdef.getMemoryMode());
			}
			this.memoryMode = mm.byteValue();

			this.memoryAddress = commdef.getMemoryAddress();
			this.writeData = data;
			this.writePos = 0;
			this.restByteLength = commdef.getWordLength() * 2;
			this.thisByteLength = 0;
		}

		/** コマンドが取得可能か？ */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer) {
			// デバイスに依存する長さを超えていれば
			if (getSendSize() < restByteLength) {
				thisByteLength = getSendSize();
				restByteLength -= getSendSize();
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// PLCコマンド種別
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x02);
			// PLCメモリ種別
			sendBuffer.put(memoryMode);
			// メモリアドレス、長さ
			sendBuffer.put((byte) (memoryAddress / 0x100));
			sendBuffer.put((byte) (memoryAddress % 0x100));
			sendBuffer.put((byte) 0x00); // ビット指定、ワード書込の場合は常に0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			// 書込みデータ
			sendBuffer.put(writeData, writePos, thisByteLength);

			// 次回のアドレス
			memoryAddress += (thisByteLength / 2);
			writePos += thisByteLength;
		}

		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = memoryAddress - (thisByteLength / 2);
			int pos = writePos - thisByteLength;

			// PLCコマンド種別
			sendBuffer.put((byte) 0x01);
			sendBuffer.put((byte) 0x02);
			// PLCメモリ種別
			sendBuffer.put(memoryMode);
			// メモリアドレス、長さ
			sendBuffer.put((byte) (addr / 0x100));
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) 0x00); // ビット指定、ワード書込の場合は常に0
			sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			// 書込みデータ
			sendBuffer.put(writeData, pos, thisByteLength);
		}

		/** コマンドからレスポンスデータのバイト長を取得します。 */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH;
		}

		/** 送信データと受信データの整合性を検査します。 */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// コマンドコード
			if ((byte) 0x01 != recvBuffer.get(10)
				|| (byte) 0x02 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0102 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		}

		/**
		 * このインスタンスの文字列表現を返します。
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("WriteMode:").append("\n");
			s.append("memoryMode:").append(memoryMode).append("\n");
			s.append("memoryAddress:").append(memoryAddress).append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/**
	 * 時計読込みコマンド変換ヘルパークラスのインスタンスです。
	 */
	private final FinsCommand TIMEREAD_FINSCOMMAND = new FinsCommand() {
		/** 処理すべきPLCデータの残りバイト数 */
		private int restByteLength;
		/** 今回のPLCデータバイト数 */
		private int thisByteLength;

		/** 生成するコマンドのネタを設定する。 */
		public void setCommand(WifeCommand commdef, byte[] data) {
			this.restByteLength = 7;
			this.thisByteLength = 0;
		}

		/** コマンドが取得可能か？ */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer) {
			thisByteLength = restByteLength;
			restByteLength = 0;

			// PLCコマンド種別
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x01);
		}

		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer) {
			// PLCコマンド種別
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x01);
		}

		/** コマンドからレスポンスデータのバイト長を取得します。 */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH + thisByteLength;
		}

		/** 送信データと受信データの整合性を検査します。 */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// コマンドコード
			if ((byte) 0x07 != recvBuffer.get(10)
				|| (byte) 0x01 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0701 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
			byte[] data = new byte[8 * 2];
			Arrays.fill(data, (byte) 0);
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(System.currentTimeMillis());
			int yh = cal.get(Calendar.YEAR) / 100;
			recvData.put((byte) ((yh / 10) * 16 + (yh % 10)));
			recvData.put(recvBuffer.get(14)); // 年
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(15)); // 月
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(16)); // 日
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(20)); // 曜日
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(17)); // 時
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(18)); // 分
			recvData.put((byte) 0x00);
			recvData.put(recvBuffer.get(19)); // 秒
			recvData.put((byte) 0x00);
			recvData.put((byte) 0x01); // フラグ
		}

		/**
		 * このインスタンスの文字列表現を返します。
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("TimeReadMode:").append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/**
	 * 時計書込みコマンド変換ヘルパークラスのインスタンスです。
	 */
	private final FinsCommand TIMEWRITE_FINSCOMMAND = new FinsCommand() {
		/** 書込みデータ */
		private byte[] writeData;
		/** 処理すべきPLCデータの残りバイト数 */
		private int restByteLength;
		/** 今回のPLCデータバイト数 */
		private int thisByteLength;

		/** 生成するコマンドのネタを設定する。 */
		public void setCommand(WifeCommand commdef, byte[] data) {
			this.writeData = data;
			this.restByteLength = 7;
			this.thisByteLength = 0;
		}

		/** コマンドが取得可能か？ */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer) {
			thisByteLength = restByteLength;
			restByteLength = 0;

			// PLCコマンド種別
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x02);
			// 書込みデータ
			sendBuffer.put(writeData[1]); // 年
			sendBuffer.put(writeData[3]); // 月
			sendBuffer.put(writeData[5]); // 日
			sendBuffer.put(writeData[9]); // 時
			sendBuffer.put(writeData[11]); // 分
			sendBuffer.put(writeData[13]); // 秒
			sendBuffer.put(writeData[7]); // 曜日
		}

		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer) {

			// PLCコマンド種別
			sendBuffer.put((byte) 0x07);
			sendBuffer.put((byte) 0x02);
			// 書込みデータ
			sendBuffer.put(writeData[1]); // 年
			sendBuffer.put(writeData[3]); // 月
			sendBuffer.put(writeData[5]); // 日
			sendBuffer.put(writeData[9]); // 時
			sendBuffer.put(writeData[11]); // 分
			sendBuffer.put(writeData[13]); // 秒
			sendBuffer.put(writeData[7]); // 曜日
		}

		/** コマンドからレスポンスデータのバイト長を取得します。 */
		public int getResponceLength() {
			return FINS_COMMAND_HEADER_LENGTH;
		}

		/** 送信データと受信データの整合性を検査します。 */
		public WifeException checkCommandResponce(ByteBuffer recvBuffer) {
			// コマンドコード
			if ((byte) 0x07 != recvBuffer.get(10)
				|| (byte) 0x02 != recvBuffer.get(11)) {
				StringBuffer sb = new StringBuffer();
				sb.append("Command error: send 0702 recv ");
				sb.append(WifeUtilities.toString(recvBuffer, 10, 2));
				return new WifeException(WifeException.WIFE_ERROR,
						WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb
								.toString());
			}
			return null;
		}

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		}

		/**
		 * このインスタンスの文字列表現を返します。
		 */
		public String toString() {
			StringBuffer s = new StringBuffer();
			s.append("TimeWriteMode:").append("\n");
			s.append("restByteLength:").append(restByteLength).append("\n");
			s.append("thisByteLength:").append(thisByteLength).append("\n");
			return s.toString();
		}
	};

	/** コマンド変換ヘルパークラス */
	private FinsCommand finsCommand = READ_FINSCOMMAND;
}
