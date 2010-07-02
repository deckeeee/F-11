/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/event/WifeEventQueue.java,v 1.4 2003/03/31 09:29:28 hori Exp $
 * $Revision: 1.4 $
 * $Date: 2003/03/31 09:29:28 $
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
 * WifeEventのメッセージキュークラス。
 * キューにメッセージが存在しない場合は入るまで待ちます。
 * @todo キューの数をチェックして膨れすぎないようすする機能を追加。
 */
public class WifeEventQueue {
	/** 優先メッセージキュー */
	private LinkedList priorityQueue = new LinkedList();
	/** メッセージキュー */
	private LinkedList queue = new LinkedList();

	/** 優先メッセージキューにイベントを入れます。 */
	public synchronized void priorityEnqueue(WifeEvent event) {
		priorityQueue.addLast(event);
		notifyAll();
	}

	/** メッセージキューにイベントを入れます。 */
	public synchronized void enqueue(WifeEvent event) {
		queue.addLast(event);
		notifyAll();
	}

	/**
	 * メッセージキューからイベントを取り出します。
	 * @return WifeEvent
	 */
	public synchronized WifeEvent dequeue() {
		while (priorityQueue.isEmpty() && queue.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (!priorityQueue.isEmpty()) {
			return (WifeEvent) priorityQueue.removeFirst();
		}
		return (WifeEvent) queue.removeFirst();
	}

	/** メッセージキューの件数を取得します。 */
	public synchronized int size() {
		return queue.size();
	}

	/**
	 * オブジェクト（キューの中身）の文字列表現
	 */
	public String toString() {
		return priorityQueue.toString() + queue.toString();
	}
}
