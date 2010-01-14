/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/LoggingDataEvent.java,v 1.3.6.1 2005/08/11 07:46:35 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2005/08/11 07:46:35 $
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

import java.sql.Timestamp;
import java.util.EventObject;
import java.util.List;

/**
 * ロギングデータ変更イベントを表すクラスです。
 * ロギングデータ変更イベントは、ロギングタスクが設定された周期で、
 * 発生させるイベントです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class LoggingDataEvent extends EventObject {
	private static final long serialVersionUID = -5045105167396448059L;
	private Timestamp timestamp;
	private List list;

	/**
	 * コンストラクタ。ロギングデータ変更イベントオブジェクトを生成します。
	 * @param source ソースオブジェクト
	 * @param timestamp タイムスタンプ
	 * @param list データホルダーのリスト
	 */
	public LoggingDataEvent(Object source, Timestamp timestamp, List list) {
		super(source);
		if (timestamp == null) {
			throw new IllegalArgumentException("timestamp is null");
		}
		if (list == null) {
			throw new IllegalArgumentException("list is null");
		}
		this.timestamp = timestamp;
		this.list = list;
	}

	/**
	 * イベントのタイムスタンプを返します。
	 * @return イベントのタイムスタンプ
	 */
	public Timestamp getTimeStamp() {
		return timestamp;
	}

	/**
	 * データホルダーのリストオブジェクトを返します。
	 * @return データホルダーのリストオブジェクト
	 */	
	public List getHolders() {
		return list;
	}
	
	/**
	 * このオブジェクトの文字列表現を返します。
	 * @return このオブジェクトの文字列表現
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append("source=").append(source.toString()).append(", ");
		buffer.append("timestamp=").append(timestamp.toString()).append(", ");
		buffer.append("list=").append(list.toString());
		buffer.append("]");
		return buffer.toString();
	}
}
