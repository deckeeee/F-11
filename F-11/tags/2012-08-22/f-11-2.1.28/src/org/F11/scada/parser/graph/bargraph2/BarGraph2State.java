/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.parser.graph.bargraph2;

import java.awt.Font;
import java.util.Stack;

import javax.swing.JComponent;

import org.F11.scada.applet.graph.bargraph2.BarGraph2;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.util.FontUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/bargraph2 状態を表すクラスです。
 */
public class BarGraph2State implements State {
	private static Logger logger = Logger.getLogger(BarGraph2State.class);

	PageState pageState;
	BarGraph2 bargraph2;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public BarGraph2State(String tagName, Attributes atts, PageState pageState) {
		this.pageState = pageState;

		Font font = FontUtil.getFont(atts.getValue("font"));
		bargraph2 = new BarGraph2(font);

		JComponent graphPanel = bargraph2.getMainPanel();
		String x = atts.getValue("x");
		String y = atts.getValue("y");
		String width = atts.getValue("width");
		String height = atts.getValue("height");
		if (!isNullstring(x) && !isNullstring(y)) {
			graphPanel.setLocation(Integer.parseInt(x), Integer.parseInt(y));
		}
		if (!isNullstring(width) && !isNullstring(height)) {
			graphPanel.setSize(Integer.parseInt(width),
					Integer.parseInt(height));
		}
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("modelselector")) {
			stack.push(new ModelSelecterState(tagName, atts, bargraph2));
		} else if (tagName.equals("barseries")) {
			stack.push(new BarSeriesState(tagName, atts, bargraph2));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("bargraph2")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}

			try {
				pageState.setToolBar(bargraph2.getToolBar());
				pageState.addPageSymbol(bargraph2.getMainPanel());
				bargraph2.setGroup(0);
			} catch (Exception ex) {
				logger.error("pase error!", ex);
			}
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	private boolean isNullstring(String s) {
		return (s == null || "".equals(s));
	}
}
