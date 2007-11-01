/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeData.java,v 1.2 2003/02/05 06:52:06 frdm Exp $
 * $Revision: 1.2 $
 * $Date: 2003/02/05 06:52:06 $
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
 * Wifeシステムで使用するデータのインターフェイスです。
 * 必ずこのインターフェイスを実装しなければなりません。
 */
public interface WifeData {
	/**
	 * バイト配列をデータに変換します。
	 * @param b バイト配列
	 */
	public WifeData valueOf(byte[] b);

	/**
	 * このデータの値をバイト配列変換して返します。
	 * @return バイト配列
	 */
	public byte[] toByteArray();

	/**
	 * このデータのワード長を返します。
	 * @return このデータのワード長
	 */
	public int getWordSize();
}
