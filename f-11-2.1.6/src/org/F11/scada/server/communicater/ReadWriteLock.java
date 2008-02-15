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
 * ���[�h���C�g���b�N�p�^�[���Ń��b�N���s���N���X�ł��B
 * �Ǎ��݁A�����݂̓��b�N����A�Ǎ��ݓ��m�A�����ݓ��m�����b�N����܂��B
 * �����҂��̃X���b�h�́A�����݂��D�悵�A��Ƀ��b�N�҂��ɓ��������̂��D�悳��܂��B
 * 
 * obj.lock();
 * try {
 * } finally {
 *   obj.unlock();
 * }
 */
public final class ReadWriteLock {
	/** ���[�h���b�N */
	private boolean reading = false;
	/** ���C�g���b�N */
	private boolean writing = false;
	/** �����҂��X���b�h�̃��X�g */
	private LinkedList waitingList = new LinkedList();

	/**
	 * �Ǎ��݃��b�N�擾
	 */
	public synchronized void readLock() throws InterruptedException {
		// �����҂��B�����ݗD��
		while (reading || writing || 0 < waitingList.size()) {
			wait();
		}
		reading = true;
	}

	/**
	 * �Ǎ��݃��b�N����
	 */
	public synchronized void readUnlock() {
		reading = false;
		notifyAll();
	}

	/**
	 * �����݃��b�N�擾
	 */
	public synchronized void writeLock() throws InterruptedException {
		Thread th = Thread.currentThread();
		waitingList.addLast(th);
		try {
			// �����҂��B�擪���������g�Ȃ烍�b�N�擾�B
			while (reading || writing || waitingList.getFirst() != th) {
				wait();
			}
		} finally {
			// ���荞�݂Ŕ�����ꍇ�ł������̓o�^���폜����B
			waitingList.remove(th);
		}
		writing = true;
	}

	/**
	 * �����݃��b�N����
	 */
	public synchronized void writeUnlock() {
		writing = false;
		notifyAll();
	}
}
