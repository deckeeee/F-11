package org.F11.scada.applet.graph;

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

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.log4j.Logger;


/**
 * GraphPropertyModel �̊��N���X�ł��B���X�i�[�ɂ��f���Q�[�V�������������܂��B
 */
public abstract class AbstractGraphPropertyModel implements GraphPropertyModel {
	/** ���M���OAPI */
	protected static Logger logger;
	private static String PROPERTY_NAME = "GRAPH_PROPERTY_MODEL_CHANGE";
	private SwingPropertyChangeSupport property;

	public AbstractGraphPropertyModel() {
		logger = Logger.getLogger(getClass().getName());
	}

	/**
	 * ���X�i�[��ǉ����܂��B
	 * @param l PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (property == null) {
			property = new SwingPropertyChangeSupport(this);
		}
		property.addPropertyChangeListener(PROPERTY_NAME, l);
	}

	/**
	 * ���X�i�[���폜���܂��B
	 * @param l PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		if (property == null) {
			return;
		}
		property.removePropertyChangeListener(PROPERTY_NAME, l);
	}

	/**
	 * �o�^���X�i�[�ɃC�x���g�ʒm���܂��B
	 * @param oldValue �ω��O�̒l
	 * @param newValue �ω���̒l
	 */
	public void firePropertyChange(Object oldValue, Object newValue) {
		if (property == null) {
			return;
		}
		property.firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}
	
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
		if (property == null) {
			property = new SwingPropertyChangeSupport(this);
		}
        property.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        if (property != null) {
            property.removePropertyChangeListener(propertyName, listener);
        }
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (property != null) {
			property.firePropertyChange(propertyName, oldValue, newValue);
		}
	}
}
