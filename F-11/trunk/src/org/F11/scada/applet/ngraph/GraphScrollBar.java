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

package org.F11.scada.applet.ngraph;

import javax.swing.JScrollBar;

/**
 * グラフを操作するスクロールバー
 * 
 * @author maekawa
 * 
 */
public class GraphScrollBar extends JScrollBar {
	private static final long serialVersionUID = -1581244115481109858L;

	public GraphScrollBar(GraphView view) {
		super(HORIZONTAL);
		addAdjustmentListener(view);
		init(view);
	}

	private void init(GraphView view) {
		int maxRecord = view.getModel().getMaxRecord();
		// min は表示可能なレコード数
		GraphProperties p = view.getGraphProperties();
		setValues(
			maxRecord,
			0,
			(int) (p.getHorizontalLineSpan() / 60000),
			maxRecord);
	}
}
