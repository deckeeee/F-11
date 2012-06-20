package org.F11.scada.server.event;

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

import org.F11.scada.WifeException;

/**
 * Wifeシステムイベントオブジェクト
 */
public class WifeEvent extends java.util.EventObject
{
	private static final long serialVersionUID = -6323589548514689083L;

	/** 送信コマンドのネタです。 */
	private WifeCommand define;

	/** 読込みコマンドの結果が入ります。 */
	private byte[] readData;
	/** 通信エラー状態です。 */
	private boolean errorFlg;
	/** エラー発生時の例外です。 */
	private WifeException errorException;

	/**
	 * コンストラクタ WifeEvent
	 * @param source イベントの発生元
	 * @param oldValue 発生元のイベント内容
	 * @param newValue 通信先のイベント内容
	 */
	public WifeEvent(Object source, WifeCommand define)
	{
		super(source);
		this.define = define;
		this.readData = null;
		errorFlg = false;
		errorException = null;
	}

	/**
	 * 発生元のDeviceIDを返す。
	 * @return 発生元DeviceID
	 */
	public String getDeviceID()
	{
		return define.getDeviceID();
	}

	/**
	 * 発生元のイベント内容を返す。
	 * @return 発生元イベント内容
	 */
	public WifeCommand getDefine()
	{
		return define;
	}

	/**
	 * 読み込んだのデータを返します。
	 * リードライト種別がライトの時はnullを返します。
	 */
	 public byte[] getReadData() {
		return readData;
	 }

	/**
	 * 読み込んだデータを設定します。
	 * このメソッドは通信モジュールで使用します。
	 */
	 public void setReadData(byte [] readdata) {
		this.readData = readdata;
	 }

	 /**
	  * 通信エラーのフラグ状態を返します。
	  */
	 public boolean isError() {
		return errorFlg;
	 }

	 /**
	  * エラー発生時の例外を返します。
	  */
	 public WifeException getErrorException() {
		return errorException;
	 }

	 /**
	  * エラー発生時の例外を設定します。
	  */
	 public void setErrorException(WifeException errorException) {
		errorFlg = true;
		this.errorException = errorException;
	 }

	/**
	 * オブジェクトの文字列表現を返します。
	 */
	 public String toString() {
		return super.toString() + " " + define.toString();
	 }
}
