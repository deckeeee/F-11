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
package org.F11.scada.parser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.F11.scada.applet.schedule.WifeCalendar;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/calendar 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CalendarState implements State {
	private static Logger logger;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public CalendarState(
			String tagName,
			Attributes atts,
			PageState pageState) {
				
		logger = Logger.getLogger(getClass().getName());
		
		String provider = atts.getValue("provider");
		if (provider == null) {
			throw new IllegalArgumentException("provider name is null");
		}
		String holder = atts.getValue("holder");
		if (holder == null) {
			throw new IllegalArgumentException("holder name is null");
		}

		WifeCalendar calendar = new WifeCalendar(provider, holder, pageState.pagemapState.authenticationable);

		JComponent calpanel = calendar.getMainPanel();
		Color color = ColorFactory.getColor(atts.getValue("foreground"));
		if (color != null) {
			calpanel.setForeground(color);
		}
		color = ColorFactory.getColor(atts.getValue("background"));
		if (color != null) {
			calpanel.setBackground(color);
		}

		JPanel panel = new JPanel(new BorderLayout());
		pageState.toolBar = calendar.getToolBar();
		panel.add(calpanel, BorderLayout.CENTER);
		String loc_x = atts.getValue("x");
		String loc_y = atts.getValue("y");
		if (loc_x != null && loc_y != null)
			panel.setLocation(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		loc_x = atts.getValue("width");
		loc_y = atts.getValue("height");
		if (loc_x != null && loc_y != null)
			panel.setSize(Integer.parseInt(loc_x), Integer.parseInt(loc_y));

		pageState.basePage.addPageSymbol(panel);
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		stack.pop();
	}
}
