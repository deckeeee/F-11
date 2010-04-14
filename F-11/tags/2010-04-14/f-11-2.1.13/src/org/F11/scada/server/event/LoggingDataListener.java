/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/LoggingDataListener.java,v 1.3 2003/01/20 06:52:01 frdm Exp $
 * $Revision: 1.3 $
 * $Date: 2003/01/20 06:52:01 $
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

package org.F11.scada.server.event;

import java.util.EventListener;

/**
 * ロギングデータ変更イベントを受け取る為のリスナーインターフェイスです。
 * ロギングデータ変更イベントに関するクラスは、このインターフェイスを
 * 実装します。更に、そうしたクラスによって作成されたオブジェクトは、
 * LoggingTask オブジェクトの addLoggingListener メソッドを使用することによって
 * LoggingTask オブジェクトに登録されます。
 * ロギングデータ変更イベントが発生すると、オブジェクトの changeLoggingData メソッドが呼び出されます。 
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface LoggingDataListener extends EventListener {
	/**
	 * ロギングデータ変更イベントが発生すると呼び出されます。
	 * @param event ロギングデータ変更イベント
	 */
	public void changeLoggingData(LoggingDataEvent event);
}
