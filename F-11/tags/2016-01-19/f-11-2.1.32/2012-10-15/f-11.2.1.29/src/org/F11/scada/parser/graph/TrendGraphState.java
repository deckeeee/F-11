/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/graph/TrendGraphState.java,v 1.3.6.5 2007/07/19 09:27:04 frdm Exp $
 * $Revision: 1.3.6.5 $
 * $Date: 2007/07/19 09:27:04 $
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
package org.F11.scada.parser.graph;

import java.util.Stack;

import javax.swing.JComponent;

import org.F11.scada.applet.graph.TrendGraph;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.Util.DisplayState;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TrendGraphState extends AbstractTrendGraphState {

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TrendGraphState(
			String tagName,
			Attributes atts,
			PageState pageState,
			Object argv) {
		super(tagName, atts, pageState, argv);
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("trendgraph")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}

			try {
				if (null != argv) {
					model.setGroup(getGroupNo(argv));
				}
				TrendGraph graph = new TrendGraph(model, horizontalScaleFile);
				graph.setStrokeWidth(strokeWidth);
				JComponent graphPanel = graph.getMainPanel();
				if (foreground != null) {
					graphPanel.setForeground(foreground);
				}
				if (background != null) {
					graphPanel.setBackground(background);
				}
				pageState.setToolBar(graph.getToolBar());

				if (x != null && y != null) {
					graphPanel.setLocation(Integer.parseInt(x), Integer
							.parseInt(y));
				}
				if (width != null && height != null) {
					graphPanel.setSize(Integer.parseInt(width), Integer
							.parseInt(height));
				}

				pageState.addPageSymbol(graphPanel);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
}
