/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/LoggingDataEventQueue.java,v 1.3.6.1 2006/05/09 06:06:24 frdm Exp $
 * $Revision: 1.3.6.1 $
 * $Date: 2006/05/09 06:06:24 $
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

import java.util.LinkedList;

/**
 * LoggingDataEventのメッセージキュークラス。
 * キューにメッセージが存在しない場合は入るまで待ちます。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class LoggingDataEventQueue {
	/** メッセージキュー */
	private final LinkedList queue = new LinkedList();

	/**
	 * メッセージキューにイベントを入れます。
	 * 待っているスレッドに対して、キューにイベントが入ったことを通知します。
	 * @param event イベントオブジェクト
	 */
	public synchronized void enqueue(Object event) {
		queue.addLast(event);
		notifyAll();
	}

	/**
	 * メッセージキューからイベントを取り出します。
	 * キューにイベントが存在していない場合は、イベントがキューにはいるまで
	 * 待機します。
	 * @return イベントオブジェクト 
	 */
	public synchronized Object dequeue() {
		while (queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}

		return queue.removeFirst();
	}

	/**
	 * メッセージキューの件数を取得します。
	 * @return メッセージキューの件数
	 */
	public synchronized int size() {
		return queue.size();
	}

	/**
	 * オブジェクト（キューの中身）の文字列表現
	 * @return オブジェクト（キューの中身）の文字列表現
	 */
	public String toString() {
		return queue.toString();
	}
}
