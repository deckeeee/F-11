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

package org.F11.scada.util;

import java.util.LinkedList;
import java.util.ListIterator;

import org.apache.log4j.Logger;

/**
 * �ł̗�����\���N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageHistoryImpl implements PageHistory {
	/** �����̏����T�C�Y */
	private static int DEFAULT_HISTORY_SIZE = 50;
	/** �ŗ��� */
	private LinkedList history;
	/** �����̃T�C�Y */
	private final int capacity;
	/** �O�łւ̃��X�g�C�e���[�^ */
	private ListIterator previousIterator;
	/** �O�̑��삪 previous �Ȃ� true �ȊO�Ȃ� false */
	private boolean isPreviousMode;

	private static Logger log = Logger.getLogger(PageHistoryImpl.class);

	/**
	 * �����l(50)�ŕŗ����𐶐����܂��B
	 * 
	 */
	public PageHistoryImpl() {
		this(DEFAULT_HISTORY_SIZE);
	}

	/**
	 * �����̃L���p�V�e�B�[�ŕŗ����𐶐����܂��B
	 * 
	 * @param capacity �L���p�V�e�B�[
	 */
	public PageHistoryImpl(int capacity) {
		history = new LinkedList();
		this.capacity = capacity;
		log.debug("capacity : " + capacity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.util.PageHistory#set(java.lang.String)
	 */
	public void set(String pageId) {
		synchronized (history) {
			if (previousIterator != null) {
				if (isPreviousMode) {
					previousIterator.next();
				}
				while (previousIterator.hasNext()) {
					previousIterator.next();
					previousIterator.remove();
				}
			}
			add(pageId);
		}
	}

	private void add(String pageId) {
		previousIterator = null;
		isPreviousMode = false;
		if (capacity <= history.size()) {
			history.removeFirst();
		}
		history.add(pageId);
		if (log.isDebugEnabled()) {
			log.debug("history : " + history);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.util.PageHistory#next()
	 */
	public String next() {
		synchronized (history) {
			if (previousIterator == null) {
				return EOP;
			}
			if (isPreviousMode) {
				previousIterator.next();
				isPreviousMode = false;
			}
			if (previousIterator.hasNext()) {
				return (String) previousIterator.next();
			} else {
				return EOP;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.util.PageHistory#previous()
	 */
	public String previous() {
		synchronized (history) {
			if (history.size() <= 0) {
				return EOP;
			}
			if (previousIterator == null) {
				previousIterator = history.listIterator(history.size() - 1);
			} else if (!isPreviousMode) {
				previousIterator.previous();
			}
			isPreviousMode = true;
			if (previousIterator.hasPrevious()) {
				return (String) previousIterator.previous();
			} else {
				return EOP;
			}
		}
	}
}
