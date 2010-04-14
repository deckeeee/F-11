package org.F11.scada.security.auth;

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

import org.F11.scada.EnvironmentManager;

/**
 * <p>Authentication オブジェクトを生成するファクトリクラスです。
 * <p>デフォルトでは PostgreSQLAuthentication のインスタンスを生成して返します。
 * @todo プロパティファイルかなんかで、生成する Authentication オブジェクトを
 * 変更できるようにする。
 */
public class AuthenticationFactory {
	/** デフォルトのユーザー認証クラス */
	private static final String DEFAULT_AUTHENTICATION = "org.F11.scada.security.postgreSQL.PostgreSQLAuthentication";

	private AuthenticationFactory() {}

	/**
	 * <p>デフォルトでは PostgreSQLAuthentication のインスタンスを生成して返します。
	 * @return Authentication オブジェクト
	 * @throws ClassNotFoundException クラスが見つからない場合
	 * @throws InstantiationException この Class が abstract クラス、インタフェース、配列クラス、プリミティブ型、または void を表す場合、クラスが null コンストラクタを保持しない場合、あるいはインスタンスの生成がほかの理由で失敗した場合
	 * @throws IllegalAccessException クラスまたはその null コンストラクタにアクセスできない場合
	 */
	public static Authentication createAuthentication()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class cl =
				Class.forName(EnvironmentManager.get("/server/policy/authentication", DEFAULT_AUTHENTICATION));
		Authentication at = (Authentication)cl.newInstance();
		return at;
	}
}
