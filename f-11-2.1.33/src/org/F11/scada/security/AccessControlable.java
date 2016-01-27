package org.F11.scada.security;

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

import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.F11.scada.security.auth.Subject;

/**
 * WIFE のアクセス制御判定クラスの、リモートメソッド呼び出しインターフェイスです。
 * サブクラスでリソースのアクセス権承認と、ユーザー認証を実装します。
 */
public interface AccessControlable extends Remote {
	/*
	 * リソースのアクセス権承認を行います。
	 * 指定した Subject が permission で指定されたアクセスを、許可されているか判定します。
	 * @param subject Subject
	 * @param permission アクセス権
	 * @return 許可されている場合は true、そうでない場合は false を返します。
	 * @throws RemoteException RMI 呼び出しに失敗した場合
	public boolean checkPermission(Subject subject, WifePermission permission)
			throws RemoteException;
	 */

	/**
	 * 全ての編集可能シンボルリソースのアクセス権承認を行います。
	 * 指定した Subject が destinations で指定されたアクセスを、許可されているか判定します。
	 * @param subject Subject
	 * @param destinations 編集可能シンボルが保持しているデータホルダー名の配列
	 * @return 許可されている場合は true、そうでない場合は false の Boolean配列のリストを返します。
	 * @throws RemoteException RMI 呼び出しに失敗した場合
	 */
	public List checkPermission(Subject subject, String[][] destinations)
			throws RemoteException;

	/**
	 * ユーザーの認証を行います。
	 * 認証が成功した場合、認証したユーザーのプリンシパルを関連づけた Subject を返します。
	 * 認証に失敗した場合は null を返します。
	 * @param user 認証するユーザー名
	 * @param password 認証するユーザーの暗号化されたパスワード
	 * @return 認証が成功した場合、認証したユーザーのプリンシパルを関連づけた Subject を返します。
	 * 認証に失敗した場合は null を返します。
	 * @throws RemoteException RMI 呼び出しに失敗した場合
	 */
	public Subject checkAuthentication(String user, String password)
			throws RemoteException;
	
	/**
	 * クライアントのIPアドレスからログアウト時のユーザ名を取得します。
	 * @param local クライアントのIPアドレス
	 * @return ログアウト時のユーザ名
	 * @throws RemoteException
	 */
	public String getLogoutUser(InetAddress local) throws RemoteException;
}
