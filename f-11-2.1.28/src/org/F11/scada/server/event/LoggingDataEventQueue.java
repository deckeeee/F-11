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
 * LoggingDataEvent�̃��b�Z�[�W�L���[�N���X�B
 * �L���[�Ƀ��b�Z�[�W�����݂��Ȃ��ꍇ�͓���܂ő҂��܂��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class LoggingDataEventQueue {
	/** ���b�Z�[�W�L���[ */
	private final LinkedList queue = new LinkedList();

	/**
	 * ���b�Z�[�W�L���[�ɃC�x���g�����܂��B
	 * �҂��Ă���X���b�h�ɑ΂��āA�L���[�ɃC�x���g�����������Ƃ�ʒm���܂��B
	 * @param event �C�x���g�I�u�W�F�N�g
	 */
	public synchronized void enqueue(Object event) {
		queue.addLast(event);
		notifyAll();
	}

	/**
	 * ���b�Z�[�W�L���[����C�x���g�����o���܂��B
	 * �L���[�ɃC�x���g�����݂��Ă��Ȃ��ꍇ�́A�C�x���g���L���[�ɂ͂���܂�
	 * �ҋ@���܂��B
	 * @return �C�x���g�I�u�W�F�N�g 
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
	 * ���b�Z�[�W�L���[�̌������擾���܂��B
	 * @return ���b�Z�[�W�L���[�̌���
	 */
	public synchronized int size() {
		return queue.size();
	}

	/**
	 * �I�u�W�F�N�g�i�L���[�̒��g�j�̕�����\��
	 * @return �I�u�W�F�N�g�i�L���[�̒��g�j�̕�����\��
	 */
	public String toString() {
		return queue.toString();
	}
}
