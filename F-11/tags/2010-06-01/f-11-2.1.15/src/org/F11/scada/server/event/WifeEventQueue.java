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
 * WifeEvent�̃��b�Z�[�W�L���[�N���X�B
 * �L���[�Ƀ��b�Z�[�W�����݂��Ȃ��ꍇ�͓���܂ő҂��܂��B
 * @todo �L���[�̐����`�F�b�N���Ėc�ꂷ���Ȃ��悤������@�\��ǉ��B
 */
public class WifeEventQueue {
	/** �D�惁�b�Z�[�W�L���[ */
	private LinkedList priorityQueue = new LinkedList();
	/** ���b�Z�[�W�L���[ */
	private LinkedList queue = new LinkedList();

	/** �D�惁�b�Z�[�W�L���[�ɃC�x���g�����܂��B */
	public synchronized void priorityEnqueue(WifeEvent event) {
		priorityQueue.addLast(event);
		notifyAll();
	}

	/** ���b�Z�[�W�L���[�ɃC�x���g�����܂��B */
	public synchronized void enqueue(WifeEvent event) {
		queue.addLast(event);
		notifyAll();
	}

	/**
	 * ���b�Z�[�W�L���[����C�x���g�����o���܂��B
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

	/** ���b�Z�[�W�L���[�̌������擾���܂��B */
	public synchronized int size() {
		return queue.size();
	}

	/**
	 * �I�u�W�F�N�g�i�L���[�̒��g�j�̕�����\��
	 */
	public String toString() {
		return priorityQueue.toString() + queue.toString();
	}
}
