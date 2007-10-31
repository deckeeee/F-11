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

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.security.auth.Subject;

/**
 * WIFE のセキュリティポリシーを表すクラスです。
 * ユーザーとグループに許可されたパーミッションを保持します。
 * 指定した Subject が、データホルダーの許可を持っているのか判定します。
 * このクラスは唯一のインスタンスを保持します(Singleton パターン)
 */
public class WifePolicy extends Policy {
	/** WifePolicy のインスタンス */
	private static Policy _policy = new WifePolicy();
	/** WifePrincipal と PermissionCollection のハッシュマップです */
	private Map policyMap;
	/** デフォルトの policyMap ファクトリクラスです */
	private static final String DEFAULT_POLICYMAP =
			"org.F11.scada.security.postgreSQL.PostgreSQLPolicyMap";

	/**
	 * 指定された Map 生成クラスで、セキュリティポリシーを初期化します。
	 * デフォルトでは、PostgreSQLPolicyMap クラスを使用します。
	 */
	private WifePolicy() {
		try {
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 指定された Map 生成クラスで、セキュリティポリシーを初期化します。
	 * @throws ClassNotFoundException Map 生成クラスが見つからない場合
	 * @throws InstantiationException この Class が abstract クラス、インタフェース、
	 * 配列クラス、プリミティブ型、または void を表す場合、
	 * クラスが null コンストラクタを保持しない場合、
	 * あるいはインスタンスの生成がほかの理由で失敗した場合
	 * @throws IllegalAccessException クラスまたはその null コンストラクタにアクセスできない場合
	 */
	private void init()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		policyMap =
				PolicyMapFactory.createPolicyMap(EnvironmentManager.get("/server/policy/policyMap", DEFAULT_POLICYMAP));
	}

	/**
	 * このポリシーのインスタンスを返します。
	 * @return このポリシーのインスタンス
	 */
	public static Policy getPolicy() {
		return _policy;
	}

	/**
	 * このメソッドはサポートされていません。
	 * @param policy セットするポリシー
	 * @throws UnsupportedOperationException 常に UnsupportedOperationException をスローします
	 */
	public static void setPolicy(Policy policy) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドはサポートされていません。
	 * @param codesource コードソース
	 * @return PermissionCollection
	 * @throws UnsupportedOperationException 常に UnsupportedOperationException をスローします
	 */
	public PermissionCollection getPermissions(CodeSource codesource) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドはサポートされていません。
	 * @param domain ProtectionDomain
	 * @return PermissionCollection
	 * @throws UnsupportedOperationException 常に UnsupportedOperationException をスローします
	 */
	public PermissionCollection getPermissions(ProtectionDomain domain) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このメソッドはサポートされていません。
	 * @param domain ProtectionDomain
	 * @param permission Permission
	 * @return true
	 * @throws UnsupportedOperationException 常に UnsupportedOperationException をスローします
	 */
	public boolean implies(ProtectionDomain domain, Permission permission) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Subject に許可されたアクセス権についてグローバルポリシーを評価し、
	 * そのアクセス権が許可されているかどうかを判定します。
	 * @param subject 判定対象の Subject
	 * @param permission 含まれているかどうかを判定する Permission オブジェクト
	 * @return permission がこの subject に許可されたアクセス権の適切なサブセットの場合は true。
	 */
	public boolean implies(Subject subject, Permission permission) {
		Set principals = subject.getPrincipals();
		for (Iterator it = principals.iterator(); it.hasNext();) {
			Principal principal = (Principal) it.next();
			Object o = policyMap.get(principal);
			if (o != null) {
				PermissionCollection pc = (PermissionCollection) o;
//				System.out.println("principal:" + principal + " pc:" + pc);
				if (pc.implies(permission)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * ポリシー設定をリフレッシュまたは再読み込みします。
	 * デフォルトの PostgreSQLPolicyMap クラスでは、データベースを再読込してポリシーを再構築します。
	 */
	public void refresh() {
		try {
			init();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * このポリシーの文字列表現を返します。
	 * 文字列の表示方法は,使用されている Map 実装に依存します。デフォルトの PostgreSQLPolicyMap
	 * では、HashMap の toString が使用されます。
	 * @return ポリシーの文字列表現
	 */
	public String toString() {
		return policyMap.toString();
	}
}
