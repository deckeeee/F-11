/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/BCDConvertException.java,v 1.2.6.1 2005/08/11 07:46:32 frdm Exp $
 * $Revision: 1.2.6.1 $
 * $Date: 2005/08/11 07:46:32 $
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

package org.F11.scada.data;

/**
 * 不正なBCDデータを変換したときに発生する例外です。
 * 正当な BCD データとは 0x0000 から 0x9999 までの数値で、16進数表現の 'A' から 'F' を取り除いたデータです。
 */
public class BCDConvertException extends RuntimeException {

	private static final long serialVersionUID = -3316674999641927021L;

	/**
	 * 指定された詳細メッセージを使用して、新しい実行時例外を構築します。
	 * 原因は初期化されず、その後 Throwable.initCause(java.lang.Throwable) を呼び出すことで初期化されます。
	 * @param message　詳細メッセージ。詳細メッセージは Throwable.getMessage() メソッドによる取得用に保存される
	 */
	public BCDConvertException(String message) {
		super(message);
	}
}

