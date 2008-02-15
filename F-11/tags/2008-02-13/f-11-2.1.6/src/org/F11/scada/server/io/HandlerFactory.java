package org.F11.scada.server.io;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.F11.scada.server.event.LoggingDataListener;

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
 * データハンドラーのファクトリークラスです。
 * getHandlerFactory メソッドで、指定のファクトリーオブジェクトを生成します。
 * @see#getHandlerFactory()
 * <!-- Abstract Factory Pattern を使用しています。 -->
 */
public abstract class HandlerFactory {
	/**
	 * 引数で指定したクラス名の、ファクトリーオブジェクトを生成します。
	 * @param className ファクトリーオブジェクト名
	 * @return 生成されたファクトリーオブジェクトのインスタンス
	 */
	public static HandlerFactory getHandlerFactory(String className) {
		HandlerFactory factory = null;
		try {
			factory = (HandlerFactory) Class.forName(className).newInstance();
		} catch (ClassNotFoundException ex) {
			System.out.println("クラス " + className + " が見つかりません。");
		} catch (Exception ex2) {
			ex2.printStackTrace();
		}
		return factory;
	}

	/**
	 * データ更新用データハンドラを返すファクトリーメソッドです。サブクラスで実装してください。
	 * @param device デバイス名(通常はテーブル名、ファイル名)
	 * @return データ更新用データハンドラ
	 * @exception SQLException DBMSコネクトが失敗したときスローされます。
	 */
	public abstract LoggingDataListener createStoreHandler(String device) throws SQLException;

	/**
	 * クライアントハンドラインターフェイスオブジェクトを返すファクトリーメソッドです。サブクラスで実装してください。
	 * @param device デバイス名(通常はテーブル名、ファイル名)
	 * @param dataHolders データホルダーのリスト
	 * @return クライアントハンドラインターフェイスオブジェクトを返すファクトリー
	 * @exception MalformedURLException 名前が適切な形式の URL でない場合
	 * @exception RemoteException RMIレジストリに接続できない場合
	 */
	public abstract ValueListHandlerElement createValueListHandler(
			String device,
			List dataHolders)
			throws MalformedURLException, RemoteException, SQLException;

	/**
	 * グラフ用のセレクトハンドラを返します。
	 * @param device テーブル名
	 * @return グラフ用のセレクトハンドラを返します。
	 */
	public abstract SelectiveValueListHandlerElement createSelectviveHandler(String device);

	/**
	 * グラフ用の全データハンドラを返します。
	 * @param device テーブル名
	 * @return グラフ用の全データハンドラを返します。
	 */
	public abstract SelectiveAllDataValueListHandlerElement createAllDataSelectviveHandler(String device);
}
