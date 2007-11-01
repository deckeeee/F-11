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

import java.util.Stack;

import jp.gr.javacons.jim.DataReferencerOwner;

import org.F11.scada.applet.symbol.ScheduleEditable;
import org.F11.scada.applet.symbol.Symbol;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TextScheduleSymbolEditable;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class SymbolScheduleEditableState implements State {
	private static Logger logger;

	private Stack parentStack;
	private DataReferencerOwner referencerOwner;
	private Symbol symbol;
	private ScheduleEditable symbolScheduleEditable;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public SymbolScheduleEditableState(
		String tagName,
		Attributes atts,
		SymbolContainerState state) {

		logger = Logger.getLogger(getClass());

		if (isSupport(tagName)) {
			this.symbol =
				new TextScheduleSymbolEditable(
					new SymbolProperty(atts),
					state.getAuthenticationable());
			state.addPageSymbol(this.symbol);

			parentStack = new Stack();
			parentStack.push(this.symbol);
			referencerOwner = this.symbol;
			symbolScheduleEditable = (ScheduleEditable) this.symbol;
		} else {
			throw new IllegalArgumentException(tagName + " is non support tag.");
		}
	}

	/*
	 * @see orgF11.scada.parser.State#add(String, Attributes)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("if")) {
			stack.push(new IfState(tagName, atts, parentStack, referencerOwner));

		} else if (tagName.equals("destination")) {
			stack.push(new DestinationScheduleState(tagName, atts, symbolScheduleEditable));

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/*
	 * 終了タグイベントの処理
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		symbol.updateProperty();
		stack.pop();
	}

	public static boolean isSupport(String tagName) {
		return tagName.equals("textschedulesymboleditable");
	}
}
