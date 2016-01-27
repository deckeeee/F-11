/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.ngraph.model;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.SeriesGroup;


/**
 * グラフモデルの基底クラスです。
 * 
 * @author maekawa
 * 
 */
public abstract class AbstractGraphModel implements GraphModel {
	protected final PropertyChangeSupport changeSupport;
	protected final GraphProperties graphProperties;
	private String logName = "";
	private int maxRecord;

	public AbstractGraphModel(GraphProperties graphProperties) {
		this.graphProperties = graphProperties;
		changeSupport = new PropertyChangeSupport(this);
		maxRecord = graphProperties.getMaxRecord();
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(INITIALIZE, listener);
		changeSupport.addPropertyChangeListener(VALUE_CHANGE, listener);
		changeSupport.addPropertyChangeListener(GROUP_CHANGE, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(INITIALIZE, listener);
		changeSupport.removePropertyChangeListener(VALUE_CHANGE, listener);
		changeSupport.removePropertyChangeListener(GROUP_CHANGE, listener);
	}

	public void setGroupNo(int group) {
		int old = graphProperties.getGroupNo();
		if (graphProperties.setGroupNo(group)) {
			initialize();
			changeSupport.firePropertyChange(GROUP_CHANGE, old, group);
		}
	}

	public int getGroupNo() {
		return graphProperties.getGroupNo();
	}

	public String getGroupName() {
		return graphProperties.getSeriesGroup().getGroupName();
	}

	public List<SeriesGroup> getSeriesGroups() {
		return graphProperties.getSeriesGroups();
	}
	
	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
		initialize();
	}

	public int getMaxRecord() {
		return maxRecord;
	}

}
