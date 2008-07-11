/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/AlarmException.java,v 1.1.6.1 2005/08/11 07:46:34 frdm Exp $
 * $Revision: 1.1.6.1 $
 * $Date: 2005/08/11 07:46:34 $
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
package org.F11.scada.server.alarm;

/**
 * 警報・状態テーブルモデル処理時に発生した例外のラッパークラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmException extends Exception {
	
	private static final long serialVersionUID = 3924601934649416261L;

	/**
	 * 例外をラップしたオブジェクトを生成します。
	 * @param cause 発生した例外
	 * @see java.lang.Throwable#Throwable(Throwable)
	 */
	public AlarmException(Throwable cause) {
		super(cause);
	}
}
