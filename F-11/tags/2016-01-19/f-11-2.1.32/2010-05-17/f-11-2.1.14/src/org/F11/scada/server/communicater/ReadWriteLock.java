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

package org.F11.scada.server.communicater;

import java.util.LinkedList;

/**
 * @author hori
 */
/**
 * リードライトロックパターンでロックを行うクラスです。
 * 読込み、書込みはロックされ、読込み同士、書込み同士もロックされます。
 * 解除待ちのスレッドは、書込みが優先し、先にロック待ちに入ったものが優先されます。
 * 
 * obj.lock();
 * try {
 * } finally {
 *   obj.unlock();
 * }
 */
public final class ReadWriteLock {
	/** リードロック */
	private boolean reading = false;
	/** ライトロック */
	private boolean writing = false;
	/** 解除待ちスレッドのリスト */
	private LinkedList waitingList = new LinkedList();

	/**
	 * 読込みロック取得
	 */
	public synchronized void readLock() throws InterruptedException {
		// 解除待ち。書込み優先
		while (reading || writing || 0 < waitingList.size()) {
			wait();
		}
		reading = true;
	}

	/**
	 * 読込みロック解除
	 */
	public synchronized void readUnlock() {
		reading = false;
		notifyAll();
	}

	/**
	 * 書込みロック取得
	 */
	public synchronized void writeLock() throws InterruptedException {
		Thread th = Thread.currentThread();
		waitingList.addLast(th);
		try {
			// 解除待ち。先頭が自分自身ならロック取得。
			while (reading || writing || waitingList.getFirst() != th) {
				wait();
			}
		} finally {
			// 割り込みで抜ける場合でも自分の登録を削除する。
			waitingList.remove(th);
		}
		writing = true;
	}

	/**
	 * 書込みロック解除
	 */
	public synchronized void writeUnlock() {
		writing = false;
		notifyAll();
	}
}
