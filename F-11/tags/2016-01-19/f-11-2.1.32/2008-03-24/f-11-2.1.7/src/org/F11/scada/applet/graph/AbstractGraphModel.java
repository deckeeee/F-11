/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/AbstractGraphModel.java,v 1.4 2002/12/24 07:27:04 frdm Exp $
 * $Revision: 1.4 $
 * $Date: 2002/12/24 07:27:04 $
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

package org.F11.scada.applet.graph;

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.log4j.Logger;


/**
 * GraphModel の基底クラスです。リスナーによるデリゲーションを実装します。
 */
public abstract class AbstractGraphModel implements GraphModel {
	/** ロギングAPI */
	protected static Logger logger;
	private static String PROPERTY_NAME = "GRAPH_MODEL_CHANGE";
	private SwingPropertyChangeSupport property;

	public AbstractGraphModel() {
		logger = Logger.getLogger(getClass().getName());
	}

	/**
	 * リスナーを追加します。
	 * @param l PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (property == null) {
			property = new SwingPropertyChangeSupport(this);
		}
		property.addPropertyChangeListener(PROPERTY_NAME, l);
	}

	/**
	 * リスナーを削除します。
	 * @param l PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		if (property == null) {
			return;
		}
		property.removePropertyChangeListener(PROPERTY_NAME, l);
	}

	/**
	 * 登録リスナーにイベント通知します。
	 * @param oldValue 変化前の値
	 * @param newValue 変化後の値
	 */
	public void firePropertyChange(Object oldValue, Object newValue) {
		if (property == null) {
			return;
		}
		property.firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}
}
