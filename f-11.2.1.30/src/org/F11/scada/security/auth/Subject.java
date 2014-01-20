/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/security/auth/Subject.java,v 1.7 2003/01/30 08:24:30 frdm Exp $
 * $Revision: 1.7 $
 * $Date: 2003/01/30 08:24:30 $
 * 
 * =============================================================================
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

package org.F11.scada.security.auth;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>WIFE における主体を抽象的に表すクラスです。
 * 具体的には Subject 内に、関連したユーザーとグループのプリンシパル Set 保持します。
 * <p>プリンシパルには不変オブジェクトの Set 実装を使用しているので、内部プリンシパルの変更操作はできません。
 * 代わりに新しいプリンシパルを保持したオブジェクトを生成してください。
 * 
 * @author Hideaki Maekawa <frdm@users.sorceforge.jp>
 */
public final class Subject implements Serializable {
	private static final long serialVersionUID = -2449886602280598708L;

	/** プリンシパルを持たない無名のユーザーを表すSubject クラスのインスタンスです。 */
	private static final Subject NULL_SUBJECT = new Subject(new HashSet(), "");
	/** プリンシパルの Set です */
	private final Set principals;
	/** 現在のユーザー名です */
	private final String userName;

	/**
	 * プライベートコンストラクタ
	 * 指定したプリンシパルで初期化したインスタンスを生成します。
	 * @param set プリンシパル
	 * @param userName ユーザー名
	 */
	private Subject(Set set, String userName) {
		if (set == null) {
			throw new IllegalArgumentException("set is null.");
		}
		if (userName == null) {
			throw new IllegalArgumentException("userName is null.");
		}
		this.principals = Collections.unmodifiableSet(set);
		this.userName = userName;
	}

	/**
	 * Subject オブジェクトを生成するファクトリーメソッドです。
	 * 指定された引数で初期化したオブジェクトを返します。
	 * @param set プリンシパル
	 * @param userName ユーザー名
	 * @return Subject オブジェクト
	 */
	public static Subject createSubject(Set set, String userName) {
		return new Subject(set, userName);
	}

	/**
	 * プリンシパルを持たない無名のユーザーを表すSubject クラスのインスタンスを返します。
	 * @return Subject
	 */	
	public static Subject getNullSubject() {
		return NULL_SUBJECT;
	}

	/**
	 * この Subject に関連づけられた、プリンシパルのセットを返します。
	 * @return プリンシパルのセット
	 */
	public Set getPrincipals() {
		return principals;
	}

	/**
	 * 現在のユーザー名を返します。
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * このオブジェクトと指定されたオブジェクトを比較します。
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Subject)) {
			return false;
		}
		Subject sub = (Subject)obj;
		return sub.principals.equals(principals)
				&& sub.userName.equals(userName); 
	}

	/**
	 * このオブジェクトのハッシュコードを返します。
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + principals.hashCode();
		result = 37 * result + userName.hashCode();
		return result;
	}
	
	/**
	 * このオブジェクトの文字列形式を返します。
	 * 返される文字列は、将来的に変更される可能性があります。開発中のデバッグ以外に
	 * 使用する事は推奨されません。
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("principals=" + principals.toString());
		buffer.append(" ,userName=" + userName);
		return buffer.toString();
	}
	
	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new Subject(principals, userName);
	}
}
