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

package org.F11.scada.applet.ngraph.event;


import java.util.EventObject;

import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.GraphScrollBar;
import org.F11.scada.applet.ngraph.GraphStatusBar;
import org.F11.scada.applet.ngraph.GraphView;
import org.F11.scada.applet.ngraph.SeriesTable;
import org.F11.scada.applet.ngraph.SeriesTableModel;
import org.F11.scada.applet.ngraph.model.GraphModel;


/**
 * グラフプロパティーの変化時に使用するイベントオブジェクト。サブコンポーネントの参照が保持されている。
 * 
 * @author maekawa
 *
 */
public class GraphChangeEvent extends EventObject {
	private static final long serialVersionUID = -4884523860440132889L;
	private final GraphView view;
	private final GraphModel model;
	private final GraphProperties properties;
	private final GraphScrollBar scrollBar;
	private final GraphStatusBar statusBar;
	private final SeriesTable seriesTable;
	private final SeriesTableModel seriesTableModel;

	public GraphChangeEvent(
			Object source,
			GraphView view,
			GraphModel model,
			GraphProperties properties,
			GraphScrollBar scrollBar,
			GraphStatusBar statusBar,
			SeriesTable seriesTable,
			SeriesTableModel seriesTableModel) {
		super(source);
		this.view = view;
		this.model = model;
		this.properties = properties;
		this.scrollBar = scrollBar;
		this.statusBar = statusBar;
		this.seriesTable = seriesTable;
		this.seriesTableModel = seriesTableModel;
	}

	public GraphView getView() {
		return view;
	}

	public GraphModel getModel() {
		return model;
	}

	public GraphProperties getProperties() {
		return properties;
	}

	public GraphScrollBar getScrollBar() {
		return scrollBar;
	}

	public GraphStatusBar getStatusBar() {
		return statusBar;
	}

	public SeriesTable getSeriesTable() {
		return seriesTable;
	}

	public SeriesTableModel getSeriesTableModel() {
		return seriesTableModel;
	}
}
