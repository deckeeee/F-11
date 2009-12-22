package org.F11.scada.security.auth.login;

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

import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.security.auth.Subject;
import org.apache.commons.configuration.Configuration;

/**
 * <p>
 * セキュリティ承認のインターフェイスです。
 * <p>
 * このインターフェイスは、WIFE におけるセキュリティ管理を実装します。
 * <p>
 * デフォルトの実装では、postgreSQL のテーブルに、セキュリティ 定義されたデータを使用し、AccessControl クラスを RMI
 * 呼び出しして アクセス権限を判定します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Authenticationable {
	/*
	 * 指定された名前のアクセス権を判定します。 @param name 名前（データホルダー名） @return アクセス権がある場合
	 * true、無い場合 false を返します。 public boolean checkPermission(String name);
	 */

	/**
	 * ユーザー切替ダイアログを表示します。
	 */
	public void showAuthenticationDialog();

	/**
	 * ログアウト処理をします。
	 */
	public void logout();

	/**
	 * 編集可能シンボルをリスナー登録します。リスナー登録されたシンボルは、ユーザー認証
	 * が行われてユーザーが変更された場合に、自分自身の編集可能フラグを更新します。
	 * 
	 * @param symbol 編集可能シンボルオブジェクト
	 */
	public void addEditable(Editable symbol);

	/**
	 * 編集可能シンボルをリスナーから削除します。
	 * 
	 * @param symbol 編集可能シンボルオブジェクト
	 */
	public void removeEditable(Editable symbol);

	/**
	 * ユーザー名を返します。
	 * 
	 * @return ユーザー名を返します。
	 */
	Subject getSubject();

	/**
	 * クライアント設定を返します
	 * 
	 * @return クライアント設定を返します
	 */
	Configuration getConfiguration();
}
