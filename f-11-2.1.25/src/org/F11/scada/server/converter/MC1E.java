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
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.WifeUtilities;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.event.WifeCommand;

/**
 * Wife通信モジュールのMC3Eコマンド生成の実装クラス。
 */
public class MC1E implements Converter {
	/** MC1Eコマンド ヘッダ長 */
	public static final int MC1E_COMMAND_HEADER_LENGTH = 1;
	/** MC1Eコマンド 終了コード長 */
	public static final int MC1E_COMMAND_ENDCODE_LENGTH = 1;

	/** メモリ種別  D データレジスタを表します。 */
	public static final Integer D_AREA = new Integer(0);
	/** メモリ種別  M 内部リレーエリアを表します。 */
	public static final Integer M_AREA = new Integer(1);
	/** メモリ種別  X 入力リレーエリアを表します。 */
	public static final Integer X_AREA = new Integer(2);
	/** メモリ種別  Y 出力リレーエリアを表します。 */
	public static final Integer Y_AREA = new Integer(3);
	/** メモリ種別  B リンクリレーエリアを表します。 */
	public static final Integer B_AREA = new Integer(4);
	/** メモリ種別  ZR ファイルレジスタを表します。 */
	public static final Integer R_AREA = new Integer(10);
	/** メモリ種別  W リンクレジスタを表します。 */
	public static final Integer W_AREA = new Integer(11);

	/** コマンド種別のマップ */
	private static final Map memoryModeMap;
	static {
		memoryModeMap = new HashMap();
		memoryModeMap.put(D_AREA, new ComndKindWorddev(0x20, 0x44));
		memoryModeMap.put(M_AREA, new ComndKindBitdev(0x20, 0x4d));
		memoryModeMap.put(X_AREA, new ComndKindBitdev(0x20, 0x58));
		memoryModeMap.put(Y_AREA, new ComndKindBitdev(0x20, 0x59));
		memoryModeMap.put(B_AREA, new ComndKindBitdev(0x20, 0x42));
		memoryModeMap.put(R_AREA, new ComndKindWorddev(0x20, 0x52));
		memoryModeMap.put(W_AREA, new ComndKindWorddev(0x20, 0x57));
	}

	/** PC番号 */
	private byte pcno = (byte) 0x00;
	/** CPU監視タイマ */
	private byte[] cpu = {(byte) 0x00, (byte) 0x00 };

	/**
	 *  デフォルトコンストラクタ
	 */
	public MC1E() {
	}

	/** 環境を設定し、レスポンスヘッダを返す。*/
	public byte[] setEnvironment(Environment device) {
		pcno = (byte) (device.getPlcNodeNo() & 0xff);
		cpu[0] = (byte) (device.getPlcWatchWait() % 0x100);
		cpu[1] = (byte) (device.getPlcWatchWait() / 0x100);

		return new byte[] {(byte) 0x81, (byte) 0x00 };
	}

	/** 読込みコマンドを設定する。*/
	public void setReadCommand(WifeCommand commdef) throws WifeException {
		mc1eCommand = READ_MC1ECOMMAND;
		mc1eCommand.setCommand(commdef, pcno, cpu, null);
	}
	/** 書込みコマンドを設定する。*/
	public void setWriteCommand(WifeCommand commdef, byte[] data)
		throws WifeException {
		mc1eCommand = WRITE_MC1ECOMMAND;
		mc1eCommand.setCommand(commdef, pcno, cpu, data);
	}

	/** コマンドが取得可能か？ */
	public boolean hasCommand() {
		return mc1eCommand.hasCommand();
	}

	/** コマンドを作成し、次回のコマンドを準備します。 */
	public void nextCommand(ByteBuffer sendBuffer) {
		mc1eCommand.nextCommand(sendBuffer);
	}

	/** 前回実行コマンドを作成します。 */
	public void retryCommand(ByteBuffer sendBuffer) {
		mc1eCommand.retryCommand(sendBuffer);
	}

	/** 送信データと受信データの整合性を検査します。 */
	public WifeException checkCommandResponce(ByteBuffer recvBuffer)
		throws WifeException {
		byte[] err = { 0, 0 };
		if (recvBuffer.remaining()
			< (MC1E_COMMAND_HEADER_LENGTH + MC1E_COMMAND_ENDCODE_LENGTH)) {
			StringBuffer sb = new StringBuffer();
			sb.append("RecvData (");
			sb.append(WifeUtilities.toString(recvBuffer));
			sb.append(") is short!");
			return new WifeException(
				WifeException.WIFE_ERROR,
				WifeException.WIFE_NET_RESPONCE_ERROR,
				sb.toString() + mc1eCommand.toString());
		}
		// 終了コード
		if (recvBuffer.get(1) != (byte) 0x00) {
			StringBuffer sb = new StringBuffer();
			sb.append("End code error: ");
			sb.append(" RecvData (");
			sb.append(WifeUtilities.toString(recvBuffer));
			sb.append(")");
			return new WifeException(
				WifeException.WIFE_ERROR,
				WifeException.WIFE_NET_RESPONCE_ENDCODE_ERROR,
				err,
				sb.toString() + mc1eCommand.toString());
		}
		return null;
	}

	/** 受信データからデータ部を取得します。 */
	public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
		mc1eCommand.getResponceData(recvBuffer, recvData);
	}

	/** 通信文の最大長を返します。 */
	public int getPacketMaxSize(WifeCommand commdef) {
		ComndKind kind =
			(ComndKind) memoryModeMap.get(new Integer(commdef.getMemoryMode()));
		return kind.getReadMaxLen() / 2;
	}

	/** 
	 * 文字列表現を返します。 
	 */
	public String toString() {
		return mc1eCommand.toString();
	}

	/**
	 * コマンド変換ヘルパークラスのインターフェイスです。 
	 */
	private interface Mc1eCommand {
		/** 生成するコマンドを設定する。*/
		public void setCommand(
			WifeCommand commdef,
			byte pcno,
			byte[] cpu,
			byte[] data)
			throws WifeException;
		/** コマンドが取得可能か？ */
		public boolean hasCommand();
		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer);
		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer);

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
	}

	/** 
	 * 読込みコマンド変換ヘルパークラスのインスタンスです。
	 */
	private final Mc1eCommand READ_MC1ECOMMAND = new Mc1eCommand() {
		/** PC番号 */
		private byte pcno;
		/** CPU監視タイマ */
		private byte[] cpu;
		/** PLCメモリ種別 */
		private byte[] memoryMode;
		/** PLCメモリアドレス */
		private long memoryAddress;
		/** 処理すべきPLCデータの残りバイト数 */
		private int restByteLength = 0;
		/** 今回のPLCデータバイト数 */
		private int thisByteLength = 0;
		/** 最大PLCデータバイト数 */
		private int maxByteLen = 0;
		/** デバイス種別ヘルパークラスへの参照 */
		private ComndKind kind;

		/** 生成するコマンドのネタを設定する。*/
		public void setCommand(
			WifeCommand commdef,
			byte pcno,
			byte[] cpu,
			byte[] data)
			throws WifeException {
			this.pcno = pcno;
			this.cpu = cpu;
			// デバイス種別ヘルパークラス
			kind =
				(ComndKind) memoryModeMap.get(
					new Integer(commdef.getMemoryMode()));
			if (kind == null) {
				throw new WifeException(
					WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_COMMAND_ERROR,
					"Not supported memory mode " + commdef.getMemoryMode());
			}
			memoryMode = kind.getMemKind();
			memoryAddress = commdef.getMemoryAddress();
			restByteLength = commdef.getWordLength() * 2;
			thisByteLength = 0;
			maxByteLen = kind.getReadMaxLen();
		}

		/** コマンドが取得可能か？ */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}
		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress);

			// デバイスに依存する長さを超えていれば
			if (maxByteLen < restByteLength) {
				thisByteLength = maxByteLen;
				restByteLength -= maxByteLen;
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// MC1Eヘッダの作成
			sendBuffer.put((byte) 0x01); // ワード読込み
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// デバイスアドレス
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// デバイスコード
			sendBuffer.put(memoryMode);
			// 長さ
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);

			// 次回のアドレス
			memoryAddress += (thisByteLength / 2);
		}
		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress - (thisByteLength / 2));

			// MC1Eヘッダの作成
			sendBuffer.put((byte) 0x01); // ワード読込み
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// デバイスアドレス
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// デバイスコード
			sendBuffer.put(memoryMode);
			// 長さ
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);
		}

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(
			ByteBuffer recvBuffer,
			ByteBuffer recvData) {
			for (int i =
				MC1E_COMMAND_HEADER_LENGTH + MC1E_COMMAND_ENDCODE_LENGTH;
				i < recvBuffer.remaining();
				i += 2) {
				recvData.put(recvBuffer.get(i + 1));
				recvData.put(recvBuffer.get(i + 0));
			}
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
	 * 書込みコマンド変換ヘルパークラスのインスタンスです。
	 */
	private final Mc1eCommand WRITE_MC1ECOMMAND = new Mc1eCommand() {
		/** PC番号 */
		private byte pcno;
		/** CPU監視タイマ */
		private byte[] cpu;
		/** PLCメモリ種別 */
		private byte[] memoryMode;
		/** PLCメモリアドレス */
		private long memoryAddress;
		/** 書込みデータ */
		private byte[] writeData;
		/** 書込みデータ位置 */
		private int writePos;
		/** 処理すべきPLCデータの残りバイト数 */
		private int restByteLength = 0;
		/** 今回のPLCデータバイト数 */
		private int thisByteLength = 0;
		/** 最大PLCデータバイト数 */
		private int maxByteLen = 0;
		/** デバイス種別ヘルパークラスへの参照 */
		private ComndKind kind;

		/** 生成するコマンドを設定する。*/
		public void setCommand(
			WifeCommand commdef,
			byte pcno,
			byte[] cpu,
			byte[] data)
			throws WifeException {
			this.pcno = pcno;
			this.cpu = cpu;
			// デバイス種別ヘルパークラス
			kind =
				(ComndKind) memoryModeMap.get(
					new Integer(commdef.getMemoryMode()));
			if (kind == null) {
				throw new WifeException(
					WifeException.WIFE_ERROR,
					WifeException.WIFE_NET_COMMAND_ERROR,
					"Not supported memory mode " + commdef.getMemoryMode());
			}
			memoryMode = kind.getMemKind();
			memoryAddress = commdef.getMemoryAddress();
			writeData = data;
			writePos = 0;
			restByteLength = commdef.getWordLength() * 2;
			thisByteLength = 0;
			maxByteLen = kind.getWriteMaxLen();
		}

		/** コマンドが取得可能か？ */
		public boolean hasCommand() {
			return 0 < restByteLength;
		}

		/** コマンドを作成し、次回のコマンドを準備します。 */
		public void nextCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress);

			// デバイスに依存する長さを超えていれば
			if (maxByteLen < restByteLength) {
				thisByteLength = maxByteLen;
				restByteLength -= maxByteLen;
			} else {
				thisByteLength = restByteLength;
				restByteLength = 0;
			}

			// MC3Eヘッダの作成
			sendBuffer.put((byte) 0x03); // ワード書込み
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// デバイスアドレス
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// デバイスコード
			sendBuffer.put(memoryMode);
			// 長さ
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);
			// 書込みデータ
			for (int i = writePos; i < (writePos + thisByteLength); i += 2) {
				sendBuffer.put(writeData[i + 1]);
				sendBuffer.put(writeData[i + 0]);
			}

			// 次回のアドレス
			memoryAddress += (thisByteLength / 2);
			writePos += thisByteLength;
		}

		/** 前回実行コマンドを作成します。 */
		public void retryCommand(ByteBuffer sendBuffer) {
			long addr = kind.getAddress(memoryAddress - (thisByteLength / 2));
			int pos = writePos - thisByteLength;

			// MC3Eヘッダの作成
			sendBuffer.put((byte) 0x03); // ワード書込み
			sendBuffer.put(pcno);
			sendBuffer.put(cpu);
			// デバイスアドレス
			sendBuffer.put((byte) (addr % 0x100));
			sendBuffer.put((byte) ((addr / 0x100) % 0x100));
			sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
			sendBuffer.put((byte) 0x00);
			// デバイスコード
			sendBuffer.put(memoryMode);
			// 長さ
			sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
			sendBuffer.put((byte) 0x00);
			// 書込みデータ
			for (int i = pos; i < (pos + thisByteLength); i += 2) {
				sendBuffer.put(writeData[i + 1]);
				sendBuffer.put(writeData[i + 0]);
			}
		}

		/** 受信データからデータ部を取得します。 */
		public void getResponceData(
			ByteBuffer recvBuffer,
			ByteBuffer recvData) {
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

	/** コマンド変換クラス */
	private Mc1eCommand mc1eCommand = READ_MC1ECOMMAND;

	/**
	 * デバイス種別ヘルパークラス
	 */
	private abstract static class ComndKind {
		private final byte[] memKind;

		public ComndKind(int kd1, int kd2) {
			this.memKind = new byte[2];
			this.memKind[0] = (byte) (kd1 & 0xff);
			this.memKind[1] = (byte) (kd2 & 0xff);
		}

		public byte[] getMemKind() {
			return memKind;
		}

		abstract public int getReadMaxLen();
		abstract public int getWriteMaxLen();
		abstract public long getAddress(long addr);
	}

	/**
	 * ビットデバイス種別
	 */
	private static class ComndKindBitdev extends ComndKind {
		/** ビットデバイス種別の送受信パケット最大サイズを表す定数です */
		public static final int BITREAD_MC1E_PACKET_MAX_SIZE = 128 * 2;
		public static final int BITWRITE_MC1E_PACKET_MAX_SIZE = 40 * 2;
		public ComndKindBitdev(int kd1, int kd2) {
			super(kd1, kd2);
		}
		public int getReadMaxLen() {
			return BITREAD_MC1E_PACKET_MAX_SIZE;
		}
		public int getWriteMaxLen() {
			return BITWRITE_MC1E_PACKET_MAX_SIZE;
		}
		public long getAddress(long addr) {
			return addr * 16;
		}
	}

	/**
	 * ワードデバイス種別
	 */
	private static class ComndKindWorddev extends ComndKind {
		/** ワードデバイス種別の送受信パケット最大サイズを表す定数です */
		public static final int WORD_MC1E_PACKET_MAX_SIZE = 256 * 2;
		public ComndKindWorddev(int kd1, int kd2) {
			super(kd1, kd2);
		}
		public int getReadMaxLen() {
			return WORD_MC1E_PACKET_MAX_SIZE;
		}
		public int getWriteMaxLen() {
			return WORD_MC1E_PACKET_MAX_SIZE;
		}
		public long getAddress(long addr) {
			return addr;
		}
	}

}
