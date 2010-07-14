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
 * 頁の履歴を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageHistoryImpl implements PageHistory {
	/** 履歴の初期サイズ */
	private static int DEFAULT_HISTORY_SIZE = 50;
	/** 頁履歴 */
	private LinkedList history;
	/** 履歴のサイズ */
	private final int capacity;
	/** 前頁へのリストイテレータ */
	private ListIterator previousIterator;
	/** 前の操作が previous なら true 以外なら false */
	private boolean isPreviousMode;

	private static Logger log = Logger.getLogger(PageHistoryImpl.class);

	/**
	 * 初期値(50)で頁履歴を生成します。
	 * 
	 */
	public PageHistoryImpl() {
		this(DEFAULT_HISTORY_SIZE);
	}

	/**
	 * 引数のキャパシティーで頁履歴を生成します。
	 * 
	 * @param capacity キャパシティー
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
