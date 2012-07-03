/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet.alarm.event;

import java.io.ObjectStreamException;
import java.io.Serializable;

import javax.swing.event.EventListenerList;

/**
 * 確認イベントをサポートするクラスです。
 * このクラスは委譲モデルを利用してください。尚、継承を不可能にするため、final クラスと定義しています。
 * @author maekawa
 *
 */
public final class CheckTableSupport implements CheckTable, Serializable {
	private static final long serialVersionUID = -4766002695111821199L;
	private final EventListenerList listenerList;

	public CheckTableSupport() {
		this(new EventListenerList());
	}

	private CheckTableSupport(EventListenerList listenerList) {
		this.listenerList = listenerList;
	}

	public void addCheckTableListener(CheckTableListener listener) {
		listenerList.add(CheckTableListener.class, listener);
	}

	public void fireCheckEvent(CheckEvent evt) {
	     Object[] listeners = listenerList.getListenerList();
	     for (int i = listeners.length - 2; i >= 0; i -= 2) {
	         if (listeners[i] == CheckTableListener.class) {
	             ((CheckTableListener) listeners[i + 1]).checkedEvent(evt);
	         }
	     }
	}

	public void removeCheckTableListener(CheckTableListener listener) {
		listenerList.remove(CheckTableListener.class, listener);
	}

	private Object readResolve() throws ObjectStreamException {
		return new CheckTableSupport(listenerList);
	}
}
