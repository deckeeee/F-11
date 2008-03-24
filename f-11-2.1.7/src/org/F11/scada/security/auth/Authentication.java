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

/**
 * ユーザー認証ロジックのインターフェイスです。
 * ユーザー認証をするクラスは、このインターフェイスを実装する必要があります。
 */
public interface Authentication {
	/**
	 * ユーザーの認証を行います。
	 * 認証が成功した場合、認証したユーザーのプリンシパルを関連づけた Subject を返します。
	 * 認証に失敗した場合は null を返します。
	 * @param user 認証するユーザー名
	 * @param password 認証するユーザーの暗号化されたパスワード
	 * @return 認証が成功した場合、認証したユーザーのプリンシパルを関連づけた Subject を返します。
	 * 認証に失敗した場合は null を返します。
	 */
	public Subject checkAuthentication(String user, String password);
}
