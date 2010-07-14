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

import java.util.Stack;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.F11.scada.applet.graph.bargraph2.BarGraphModel;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.parser.State;
import org.F11.scada.parser.SymbolContainerState;
import org.F11.scada.parser.SymbolState;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.parser.graph.HandlerState;
import org.F11.scada.parser.graph.HandlerStateable;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

public class BarGraphModelState
		implements
			State,
			HandlerStateable,
			SymbolContainerState {
	private static Logger logger = Logger.getLogger(BarGraphModelState.class);

	private JComboBox selector;
	private BarGraphModel model;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public BarGraphModelState(String tagName, Attributes atts,
			JComboBox selector) {
		this.selector = selector;
		try {
			Class modelClass = Class.forName(atts.getValue("class"));
			model = (BarGraphModel) modelClass.newInstance();
		} catch (ClassNotFoundException e) {
			logger.error("Exception caught: ", e);
			throw new NoClassDefFoundError(e.getMessage());
		} catch (Exception e) {
			logger.error("Exception caught: ", e);
		}
		model.setText(atts.getValue("text"));
		String count = atts.getValue("barcount");
		if (!isNullstring(count))
			model.setBarCount(Integer.parseInt(count));
		String backpng = atts.getValue("value");
		if (!isNullstring(backpng))
			model.setIcon(GraphicManager.get(backpng));
	}

	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("handler")) {
			stack.push(new HandlerState(tagName, atts, this));
		} else if (tagName.equals("referencedate")) {
			stack.push(new ReferenceDateState(tagName, atts, model));
		} else if (tagName.equals("graphview")) {
			stack.push(new BarGraph2ViewState(tagName, atts, model));
		} else if (tagName.equals("units")) {
			stack.push(new TextUnitsState(tagName, atts, model));
		} else if (tagName.equals("verticalscale")) {
			stack.push(new VerticallyScaleModelState(tagName, atts, model));
		} else if (tagName.equals("textsymbol")) {
			stack.push(new SymbolState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}
	public void end(String tagName, Stack stack) {
		if (tagName.equals("bargraphmodel")) {
			model.setBarGraphModelIndex(selector.getItemCount());
			selector.addItem(model);
			stack.pop();
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	private boolean isNullstring(String s) {
		return (s == null || "".equals(s));
	}

	public void setHandlerName(String[] handlerNames) {
		model.setHandlerNames(handlerNames);
	}

	public void addPageSymbol(JComponent symbol) {
		model.getJComponent().add(symbol);
	}

	public Authenticationable getAuthenticationable() {
		return null;
	}

	public PageChanger getPageChanger() {
		return null;
	}

}
