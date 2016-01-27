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

/**
 * 制御機器を表すインターフェイスです。
 */
public interface Environment {
	/**
	 * デバイスIDを返します。
	 * @return デバイスID
	 */
	public String getDeviceID();

	/**
	 * デバイスの種類を返します。
	 * @return デバイスの種類
	 */
	public String getDeviceKind();

	/**
	 * デバイスのIPアドレスを返します。
	 * @return デバイスのIPアドレス
	 */
	public String getPlcIpAddress();

	/**
	 * デバイスの通信ポートを返します。
	 * @return デバイスの通信ポート
	 */
	public int getPlcPortNo();

	/**
	 * デバイスのコマンド形態を返します。
	 * @return デバイスのコマンド形態
	 */
	public String getPlcCommKind();

	/**
	 * デバイスのネット番号を返します。
	 * @return デバイスのネット番号
	 */
	public int getPlcNetNo();

	/**
	 * デバイスのノード番号を返します。
	 * @return デバイスのノード番号
	 */
	public int getPlcNodeNo();

	/**
	 * デバイスのユニット番号を返します。
	 * @return デバイスのユニット番号
	 */
	public int getPlcUnitNo();

	/**
	 * デバイスの通信待ち時間を返します。
	 * @return デバイスの通信待ち時間
	 */
	public int getPlcWatchWait();

	/**
	 * デバイスのタイムアウト時間を返します。
	 * @return デバイスのタイムアウト時間
	 */
	public int getPlcTimeout();

	/**
	 * デバイスのエラーリトライ回数を返します。
	 * @return デバイスのエラーリトライ回数
	 */
	public int getPlcRetryCount();

	/**
	 * デバイスの通信復旧待ち時間を返します。
	 * @return デバイスの通信復旧待ち時間
	 */
	public int getPlcRecoveryWait();

	/**
	 * ホストのネットアドレスを返します。
	 * @return ホストのネットアドレス
	 */
	public int getHostNetNo();

	/**
	 * ホストの通信ポートを返します。
	 * <p>ホストの通信ポートが空白の場合は、PLCと同じ通信ポートを返します。
	 * @return ホストの通信ポート
	 */
	public int getHostPortNo();

	/**
	 * <p>ホストのIPアドレスを返します。
	 * <p>ホストのネットアドレスが空白の場合は、PLCと同じアドレスを返します。
	 * @return ホストのIPアドレス
	 */
	public String getHostIpAddress();

	/**
	 * ホストのホストアドレスを返します。
	 * @return ホストのホストアドレス
	 */
	public int getHostAddress();

	/**
	 * デバイスのIPアドレス(二重化用)を返します。
	 * @return デバイスのIPアドレス
	 */
	public String getPlcIpAddress2();
}
