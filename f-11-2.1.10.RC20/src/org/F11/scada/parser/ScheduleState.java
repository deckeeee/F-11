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

import org.F11.scada.applet.schedule.DefaultScheduleModel;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.applet.schedule.WifeSchedule;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/schedule 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ScheduleState implements State {
	private static Logger logger;

	private ScheduleModel scheduleModel;
	private SymbolProperty scheduleProperty;
	private boolean isSort;
	private boolean isNonTandT;
	private boolean isLenient;

	private PageState pageState;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public ScheduleState(String tagName, Attributes atts, PageState pageState) {

		logger = Logger.getLogger(getClass().getName());
		this.pageState = pageState;

		scheduleModel =
			new DefaultScheduleModel(pageState.pagemapState.authenticationable);
		scheduleProperty = new SymbolProperty(atts);
		isSort = Boolean.valueOf(atts.getValue("isSort")).booleanValue();
		isNonTandT =
			Boolean.valueOf(atts.getValue("isNonTandT")).booleanValue();
		isLenient = Boolean.valueOf(atts.getValue("isLenient")).booleanValue();
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("group")) {
			stack.push(new ScheduleGroupState(tagName, atts, scheduleModel));
		} else {
			logger.info("tagName:" + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}

		String viewClass = scheduleProperty.getProperty("viewclass");
		logger.info("page name=" + pageState.basePage.getPageName());
		WifeSchedule schedule =
			new WifeSchedule(
				scheduleModel,
				viewClass,
				isSort,
				isNonTandT,
				pageState.basePage.getPageName(),
				isLenient,
				(AbstractWifeApplet) pageState.pagemapState.authenticationable);
		JComponent schpanel = schedule.getMainPanel();
		Color color =
			ColorFactory.getColor(scheduleProperty.getProperty("foreground"));
		if (color != null) {
			schpanel.setForeground(color);
		}
		color =
			ColorFactory.getColor(scheduleProperty.getProperty("background"));
		if (color != null) {
			schpanel.setBackground(color);
		}

		JPanel panel = new JPanel(new BorderLayout());

		pageState.toolBar = schedule.getToolBar();
		logger.debug("toolBar : " + pageState.toolBar);
		panel.add(schpanel, BorderLayout.CENTER);
		String loc_x = scheduleProperty.getProperty("x");
		String loc_y = scheduleProperty.getProperty("y");
		if (loc_x != null && loc_y != null) {
			panel.setLocation(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		}
		loc_x = scheduleProperty.getProperty("width");
		loc_y = scheduleProperty.getProperty("height");
		if (loc_x != null && loc_y != null) {
			panel.setSize(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		}

		pageState.basePage.addPageSymbol(panel);

		stack.pop();
	}
}
