package org.F11.scada;

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

/**
 *  Wife システム共通例外
 */
public class WifeException extends Exception
{
	private static final long serialVersionUID = 482171786856143943L;
	/**
	 * エラーのレベルを表します。
	 */
	private int cCode;
	/**
	 * 詳細コードを表します。
	 */
	private int dCode;
	/**
	 * PLC通信のエラーコードを表します。
	 */
	private byte[] plcCode;

	// エラーコードとエラー名称の対比表を記述
	// コンディションコード
	public static final int WIFE_NOTHING = 0;
	public static final int WIFE_WARNING = 1;
	public static final int WIFE_ERROR = 2;

	// 詳細コード
	public static final int WIFE_NET_OPEN_ERROR             = 1;  // ネットワークオープンエラー
	public static final int WIFE_NET_COMMAND_ERROR          = 2;  // コマンド指定パラメータエラー
	public static final int WIFE_NET_RESPONCE_ERROR         = 3;  // レスポンスが異常
	public static final int WIFE_NET_SOCKET_ERROR           = 4;  // タイムアウト設定時エラー
	public static final int WIFE_NET_IO_ERROR               = 5;  // 送受信入出力エラー
	public static final int WIFE_NET_RETRYOVER_ERROR        = 6;  // リトライオーバー
	public static final int WIFE_NET_RESPONCE_HEAD_ERROR    = 7;  // レスポンスヘッダエラー
	public static final int WIFE_NET_RESPONCE_CMND_ERROR    = 8;  // レスポンスコマンドエラー
	public static final int WIFE_NET_RESPONCE_ENDCODE_ERROR = 9;  // レスポンス終了コードエラー
	public static final int WIFE_IOEXCEPTION_ERROR          = 10; // 内部処理I/Oエラー
	public static final int WIFE_UNKNOWNHOSTEXCEPTION_ERROR = 11; // 内部処理エラー

	public static final int WIFE_NOTHING_PLCID_ERROR        = 12; // PLCIDは未登録
	public static final int WIFE_PLCID_OVERLAPS_ERROR       = 13; // PLCIDは登録済み

	public static final int WIFE_EXPRESSION_WARNING         = 14; // 計算式不正
	public static final int WIFE_BAD_DATA_WARNING           = 15; // データ不正
	public static final int WIFE_INITIALDATA_WARNING        = 16; // データ未更新

//	public static final int WIFE_CODING_ERROR               = 9999; // コーディングエラー

	/**
	 * コンストラクタ
	 * デフォルトのコンストラクタです。
	 */
	 public WifeException()
	{
		super();
	}

	/**
	 * コンストラクタ
	 * 各エラーコードを含めたWifeExceptionを生成します。
	 *@param s エラー文字列表現
	 *@param cc コンディションコード
	 *@param dc 詳細コード
	 */
	public WifeException(int cc, int dc, String s)
	{
		this(cc, dc, null, s);
	}

	/**
	 * コンストラクタ
	 * 各エラーコードを含めたWifeExceptionを生成します。
	 *@param s エラー文字列表現
	 *@param cc コンディションコード
	 *@param dc 詳細コード
	 *@param pc PLC通信エラーコード
	 */
	public WifeException(int cc, int dc, byte[] pc, String s)
	{
		super(s);
		cCode = cc;
		dCode = dc;
		plcCode = pc;
	}

	/**
	 * コンストラクタ
	 * 各エラーコードを含めたWifeExceptionを生成します。
	 *@param s エラー文字列表現
	 *@param cc コンディションコード
	 *@param dc 詳細コード
	 *@param pc PLC通信エラーコード
	 *@param cause ネストした例外オブジェクト
	 */
	public WifeException(int cc, int dc, byte[] pc, String s, Throwable cause)
	{
		super(s, cause);
		cCode = cc;
		dCode = dc;
		plcCode = pc;
	}

	/**
	 * コンストラクタ
	 * 各エラーコードを含めたWifeExceptionを生成します。
	 *@param s エラー文字列表現
	 *@param cc コンディションコード
	 *@param dc 詳細コード
	 *@param cause ネストした例外オブジェクト
	 */
	public WifeException(int cc, int dc, String s, Throwable cause)
	{
		this(cc, dc, null, s, cause);
	}

	/**
	 * コンディションコードを返します。
	 */
	public int getConditionCode()
	{
		return cCode;
	}

	/**
	 * 詳細コードを返します。
	 */
	public int getDetailCode()
	{
		return dCode;
	}

	/**
	 * PLC通信エラーコードを返します。
	 */
	public byte[] getPlcErrCode()
	{
		return plcCode;
	}
}
