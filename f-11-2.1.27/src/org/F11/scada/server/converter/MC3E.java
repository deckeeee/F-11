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
public class MC3E implements Converter {
    /** 標準のFINSコマンド送受信パケット最大サイズを表す定数です */
    public static final int DEFAULT_MC3E_PACKET_MAX_SIZE = 960 * 2;

    /** MC3Eコマンド ヘッダ長 */
    public static final int MC3E_COMMAND_HEADER_LENGTH = 11;

    /** MC3Eコマンド 要求データ長 */
    public static final int MC3E_COMMAND_REQDATA_LENGTH = 12;

    /** MC3Eコマンド 終了コード長 */
    public static final int MC3E_COMMAND_ENDCODE_LENGTH = 2;

    /** メモリ種別 D データレジスタ(word)を表します。 */
    public static final Integer D_AREA = new Integer(0);

    /** メモリ種別 M 内部リレーエリア(bit)を表します。 */
    public static final Integer M_AREA = new Integer(1);

    /** メモリ種別 X 入力リレーエリア(bit)を表します。 */
    public static final Integer X_AREA = new Integer(2);

    /** メモリ種別 Y 出力リレーエリア(bit)を表します。 */
    public static final Integer Y_AREA = new Integer(3);

    /** メモリ種別 B リンクリレーエリア(bit)を表します。 */
    public static final Integer B_AREA = new Integer(4);

    /** メモリ種別 L ラッチリレーエリア(bit)を表します。 */
    public static final Integer L_AREA = new Integer(5);

    /** メモリ種別 ZR ファイルレジスタ(word)を表します。 */
    public static final Integer R_AREA = new Integer(10);

    /** メモリ種別 W リンクレジスタ(word)を表します。 */
    public static final Integer W_AREA = new Integer(11);

    /** メモリ種別 特殊リレーエリア(bit)を表します。 */
    public static final Integer SM_AREA = new Integer(90);

    /** コマンド種別のマップ */
    private static final Map memoryModeMap;
    static {
        memoryModeMap = new HashMap();
        memoryModeMap.put(D_AREA, new ComndKindWorddev(0xA8));
        memoryModeMap.put(M_AREA, new ComndKindBitdev(0x90));
        memoryModeMap.put(X_AREA, new ComndKindBitdev(0x9C));
        memoryModeMap.put(Y_AREA, new ComndKindBitdev(0x9D));
        memoryModeMap.put(B_AREA, new ComndKindBitdev(0xA0));
        memoryModeMap.put(L_AREA, new ComndKindBitdev(0x92));
        memoryModeMap.put(R_AREA, new ComndKindWorddev(0xB0));
        memoryModeMap.put(W_AREA, new ComndKindWorddev(0xB4));
        memoryModeMap.put(SM_AREA, new ComndKindBitdev(0x91));
    }

    /** ヘッダデータ */
    private byte[] head = { (byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xff, (byte) 0x03, (byte) 0x00 };

    /** CPU監視タイマ */
    private byte[] cpu = { (byte) 0x00, (byte) 0x00 };

    /**
     * デフォルトコンストラクタ
     */
    public MC3E() {
    }

    /** 環境を設定し、レスポンスヘッダを返す。 */
    public byte[] setEnvironment(Environment device) {
        head[2] = (byte) device.getPlcNetNo();
        head[3] = (byte) device.getPlcNodeNo();
        cpu[0] = (byte) (device.getPlcWatchWait() % 0x100);
        cpu[1] = (byte) (device.getPlcWatchWait() / 0x100);

        byte[] resp = new byte[head.length];
        resp[0] = (byte) 0xd0;
        resp[1] = (byte) 0x00;
        resp[2] = head[2];
        resp[3] = head[3];
        resp[4] = head[4];
        resp[5] = head[5];
        resp[6] = head[6];
        return resp;
    }

    /** 読込みコマンドを設定する。 */
    public void setReadCommand(WifeCommand commdef) throws WifeException {
        mc3eCommand = READ_MC3ECOMMAND;
        mc3eCommand.setCommand(commdef, cpu, null);
    }

    /** 書込みコマンドを設定する。 */
    public void setWriteCommand(WifeCommand commdef, byte[] data)
            throws WifeException {
        mc3eCommand = WRITE_MC3ECOMMAND;
        mc3eCommand.setCommand(commdef, cpu, data);
    }

    /** コマンドが取得可能か？ */
    public boolean hasCommand() {
        return mc3eCommand.hasCommand();
    }

    /** コマンドを作成し、次回のコマンドを準備します。 */
    public void nextCommand(ByteBuffer sendBuffer) {
        sendBuffer.put(head);
        mc3eCommand.nextCommand(sendBuffer);
    }

    /** 前回実行コマンドを作成します。 */
    public void retryCommand(ByteBuffer sendBuffer) {
        sendBuffer.put(head);
        mc3eCommand.retryCommand(sendBuffer);
    }

    /** 送信データと受信データの整合性を検査します。 */
    public WifeException checkCommandResponce(ByteBuffer recvBuffer)
            throws WifeException {
        byte[] err = { 0, 0 };
        if (recvBuffer.remaining() < MC3E_COMMAND_HEADER_LENGTH) {
            StringBuffer sb = new StringBuffer();
            sb.append("RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(") is short!");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_ERROR, sb.toString()
                            + mc3eCommand.toString());
        }
        // 終了コード
        if (recvBuffer.get(9) != (byte) 0x00
                || recvBuffer.get(10) != (byte) 0x00) {
            StringBuffer sb = new StringBuffer();
            sb.append("End code error: ");
            sb.append(" RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(")");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_ENDCODE_ERROR, err, sb
                            .toString()
                            + mc3eCommand.toString());
        }
        // 応答データ長
        int actualDataLen = recvBuffer.remaining() - MC3E_COMMAND_HEADER_LENGTH
                + MC3E_COMMAND_ENDCODE_LENGTH;
        int headerDataLen = ((int) recvBuffer.get(7) & 0x00ff)
                + (((int) recvBuffer.get(8) * 0x100) & 0x00ff00);
        if (actualDataLen != headerDataLen) {
            StringBuffer sb = new StringBuffer();
            sb.append("Header data length ").append(headerDataLen);
            sb.append(" != Actual data length ").append(actualDataLen);
            sb.append(" RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(")");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb.toString());
        }
        int reqDataLen = mc3eCommand.getReqDataLength();
        if (actualDataLen - 2 != reqDataLen) {
            StringBuffer sb = new StringBuffer();
            sb.append("Request data length ").append(reqDataLen);
            sb.append(" != Actual data length ").append(actualDataLen);
            sb.append(" RecvData (");
            sb.append(WifeUtilities.toString(recvBuffer));
            sb.append(")");
            return new WifeException(WifeException.WIFE_ERROR,
                    WifeException.WIFE_NET_RESPONCE_CMND_ERROR, sb.toString());
        }
        return null;
    }

    /** 受信データからデータ部を取得します。 */
    public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
        mc3eCommand.getResponceData(recvBuffer, recvData);
    }

    /** 通信文の最大長を返します。 */
    public int getPacketMaxSize(WifeCommand commdef) {
        return DEFAULT_MC3E_PACKET_MAX_SIZE / 2;
    }

    /**
     * 文字列表現を返します。
     */
    public String toString() {
        return mc3eCommand.toString();
    }

    /**
     * コマンド変換ヘルパークラスのインターフェイスです。
     */
    private interface Mc3eCommand {
        /** 生成するコマンドを設定する。 */
        public void setCommand(WifeCommand commdef, byte[] cpu, byte[] data)
                throws WifeException;

        /** コマンドが取得可能か？ */
        public boolean hasCommand();

        /** コマンドを作成し、次回のコマンドを準備します。 */
        public void nextCommand(ByteBuffer sendBuffer);

        /** 前回実行コマンドを作成します。 */
        public void retryCommand(ByteBuffer sendBuffer);

        /** コマンドの今回要求長さを取得します。 */
        public int getReqDataLength();

        /** 受信データからデータ部を取得します。 */
        public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData);
    }

    /**
     * 読込みコマンド変換ヘルパークラスのインスタンスです。
     */
    private final Mc3eCommand READ_MC3ECOMMAND = new Mc3eCommand() {
        /** CPU監視タイマ */
        private byte[] cpu;

        /** PLCメモリ種別 */
        private byte memoryMode;

        /** PLCメモリアドレス */
        private long memoryAddress;

        /** 処理すべきPLCデータの残りバイト数 */
        private int restByteLength = 0;

        /** 今回のPLCデータバイト数 */
        private int thisByteLength = 0;

        /** デバイス種別ヘルパークラスへの参照 */
        private ComndKind kind;

        /** 生成するコマンドのネタを設定する。 */
        public void setCommand(WifeCommand commdef, byte[] cpu, byte[] data)
                throws WifeException {
            this.cpu = cpu;
            // デバイス種別ヘルパークラス
            kind = (ComndKind) memoryModeMap.get(new Integer(commdef
                    .getMemoryMode()));
            if (kind == null) {
                throw new WifeException(WifeException.WIFE_ERROR,
                        WifeException.WIFE_NET_COMMAND_ERROR,
                        "Not supported memory mode " + commdef.getMemoryMode());
            }
            memoryMode = kind.getMemKind();

            memoryAddress = commdef.getMemoryAddress();
            restByteLength = commdef.getWordLength() * 2;
            thisByteLength = 0;
        }

        /** コマンドが取得可能か？ */
        public boolean hasCommand() {
            return 0 < restByteLength;
        }

        /** コマンドを作成し、次回のコマンドを準備します。 */
        public void nextCommand(ByteBuffer sendBuffer) {
            long addr = kind.getAddress(memoryAddress);

            // デバイスに依存する長さを超えていれば
            if (DEFAULT_MC3E_PACKET_MAX_SIZE < restByteLength) {
                thisByteLength = DEFAULT_MC3E_PACKET_MAX_SIZE;
                restByteLength -= DEFAULT_MC3E_PACKET_MAX_SIZE;
            } else {
                thisByteLength = restByteLength;
                restByteLength = 0;
            }

            // MC3Eヘッダの作成
            int datalen = MC3E_COMMAND_REQDATA_LENGTH;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // コマンド、サブコマンド
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x04);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // デバイスアドレス
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // デバイスコード
            sendBuffer.put(memoryMode);
            // 長さ
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));

            // 次回のアドレス
            memoryAddress += (thisByteLength / 2);
        }

        /** 前回実行コマンドを作成します。 */
        public void retryCommand(ByteBuffer sendBuffer) {
            long addr = kind.getAddress(memoryAddress - (thisByteLength / 2));

            // MC3Eヘッダの作成
            int datalen = MC3E_COMMAND_REQDATA_LENGTH;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // コマンド、サブコマンド
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x04);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // デバイスアドレス
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // デバイスコード
            sendBuffer.put(memoryMode);
            // 長さ
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
        }

        /** コマンドの今回要求長さを取得します。 */
        public int getReqDataLength() {
            return thisByteLength;
        }

        /** 受信データからデータ部を取得します。 */
        public void getResponceData(ByteBuffer recvBuffer, ByteBuffer recvData) {
            for (int i = MC3E_COMMAND_HEADER_LENGTH; i < recvBuffer.remaining(); i += 2) {
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
    private final Mc3eCommand WRITE_MC3ECOMMAND = new Mc3eCommand() {
        /** CPU監視タイマ */
        private byte[] cpu;

        /** PLCメモリ種別 */
        private byte memoryMode;

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

        /** デバイス種別ヘルパークラスへの参照 */
        private ComndKind kind;

        /** 生成するコマンドを設定する。 */
        public void setCommand(WifeCommand commdef, byte[] cpu, byte[] data)
                throws WifeException {
            this.cpu = cpu;
            // デバイス種別ヘルパークラス
            kind = (ComndKind) memoryModeMap.get(new Integer(commdef
                    .getMemoryMode()));
            if (kind == null) {
                throw new WifeException(WifeException.WIFE_ERROR,
                        WifeException.WIFE_NET_COMMAND_ERROR,
                        "Not supported memory mode " + commdef.getMemoryMode());
            }
            memoryMode = kind.getMemKind();
            memoryAddress = commdef.getMemoryAddress();
            writeData = data;
            writePos = 0;
            restByteLength = commdef.getWordLength() * 2;
            thisByteLength = 0;
        }

        /** コマンドが取得可能か？ */
        public boolean hasCommand() {
            return 0 < restByteLength;
        }

        /** コマンドを作成し、次回のコマンドを準備します。 */
        public void nextCommand(ByteBuffer sendBuffer) {
            long addr = kind.getAddress(memoryAddress);

            // デバイスに依存する長さを超えていれば
            if (DEFAULT_MC3E_PACKET_MAX_SIZE < restByteLength) {
                thisByteLength = DEFAULT_MC3E_PACKET_MAX_SIZE;
                restByteLength -= DEFAULT_MC3E_PACKET_MAX_SIZE;
            } else {
                thisByteLength = restByteLength;
                restByteLength = 0;
            }

            // MC3Eヘッダの作成
            int datalen = MC3E_COMMAND_REQDATA_LENGTH + thisByteLength;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // コマンド、サブコマンド
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x14);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // デバイスアドレス
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // デバイスコード
            sendBuffer.put(memoryMode);
            // 長さ
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
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
            int datalen = MC3E_COMMAND_REQDATA_LENGTH + thisByteLength;
            sendBuffer.put((byte) (datalen % 0x100));
            sendBuffer.put((byte) (datalen / 0x100));
            sendBuffer.put(cpu);
            // コマンド、サブコマンド
            sendBuffer.put((byte) 0x01);
            sendBuffer.put((byte) 0x14);
            sendBuffer.put((byte) 0x00);
            sendBuffer.put((byte) 0x00);
            // デバイスアドレス
            sendBuffer.put((byte) (addr % 0x100));
            sendBuffer.put((byte) ((addr / 0x100) % 0x100));
            sendBuffer.put((byte) ((addr / 0x10000) % 0x100));
            // デバイスコード
            sendBuffer.put(memoryMode);
            // 長さ
            sendBuffer.put((byte) ((thisByteLength / 2) % 0x100));
            sendBuffer.put((byte) ((thisByteLength / 2) / 0x100));
            // 書込みデータ
            for (int i = pos; i < (pos + thisByteLength); i += 2) {
                sendBuffer.put(writeData[i + 1]);
                sendBuffer.put(writeData[i + 0]);
            }
        }

        /** コマンドの今回要求長さを取得します。 */
        public int getReqDataLength() {
            return 0;
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

    /** コマンド変換クラス */
    private Mc3eCommand mc3eCommand = READ_MC3ECOMMAND;

    /**
     * デバイス種別ヘルパークラス
     */
    private abstract static class ComndKind {
        private byte memKind;

        public ComndKind(int kd) {
            this.memKind = (byte) (kd & 0xff);
        }

        public byte getMemKind() {
            return memKind;
        }

        abstract public long getAddress(long addr);
    }

    /**
     * ビットデバイス種別
     */
    private static class ComndKindBitdev extends ComndKind {
        public ComndKindBitdev(int kd) {
            super(kd);
        }

        public long getAddress(long addr) {
            return addr * 16;
        }
    }

    /**
     * ワードデバイス種別
     */
    private static class ComndKindWorddev extends ComndKind {
        public ComndKindWorddev(int kd) {
            super(kd);
        }

        public long getAddress(long addr) {
            return addr;
        }
    }

}
