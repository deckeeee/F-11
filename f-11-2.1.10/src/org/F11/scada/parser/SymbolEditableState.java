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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import jp.gr.javacons.jim.DataReferencerOwner;

import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.applet.symbol.Symbol;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.security.auth.login.Authenticationable;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class SymbolEditableState implements State {
	private static Logger logger;

	private static final Map editableClassName;
	static {
		editableClassName = new HashMap();
		editableClassName.put(
			"imagesymboleditable",
			"org.F11.scada.applet.symbol.ImageSymbolEditable");
		editableClassName.put(
			"textanalogsymboleditable",
			"org.F11.scada.applet.symbol.TextAnalogSymbolEditable");
		editableClassName.put(
			"textpowerfactorsymboleditable",
			"org.F11.scada.applet.symbol.TextPowerfactorSymbolEditable");
		editableClassName.put(
				"pointcommentsymbol",
				"org.F11.scada.applet.symbol.PointCommentSymbol");
		editableClassName.put(
				"imagesymbolfixededitable",
				"org.F11.scada.applet.symbol.ImageSymbolFixedEditable");
	}
	private Stack parentStack;
	private DataReferencerOwner referencerOwner;
	private Editable symbolEditable;
	private Symbol symbol;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public SymbolEditableState(String tagName, Attributes atts, SymbolContainerState state) {

		logger = Logger.getLogger(getClass());

		if (isSupport(tagName)) {
			createSymbol(state, createSymbol(tagName, atts, state));
		} else {
			throw new IllegalArgumentException(tagName + " is non support tag.");
		}
	}

	private Symbol createSymbol(String tagName, Attributes atts, SymbolContainerState state) {
		try {
			Class className = Class.forName((String) editableClassName.get(tagName));
			logger.debug("ClassName:" + className.getName());
			try {
				Constructor constructor =
					className.getConstructor(
						new Class[] { SymbolProperty.class, Authenticationable.class });
				Object[] para =
					new Object[] { new SymbolProperty(atts), state.getAuthenticationable()};
				logger.debug("constructor:" + constructor);
				return (Symbol) constructor.newInstance(para);
			} catch (Exception e1) {
				//				e1.printStackTrace();
				try {
					Constructor constructor =
						className.getConstructor(new Class[] { SymbolProperty.class });
					Object[] para = new Object[] { new SymbolProperty(atts)};
					logger.debug("constructor:" + constructor);
					if (className.getName().equals("TextPowerfactorSymbolEditable"))
						System.out.println("TextPowerfactorSymbolEditable");
					return (Symbol) constructor.newInstance(para);
				} catch (Exception e2) {
					e1.printStackTrace();
					e2.printStackTrace();
					throw new NoClassDefFoundError(e1.getMessage() + "\n" + e2.getMessage());
				}
			}
		} catch (ClassNotFoundException e) {
			throw new NoClassDefFoundError(e.getMessage());
		}
	}

	private void createSymbol(SymbolContainerState state, Symbol symbol) {
		this.symbol = symbol;
		state.addPageSymbol(this.symbol);

		parentStack = new Stack();
		parentStack.push(this.symbol);
		referencerOwner = this.symbol;
		symbolEditable = (Editable) this.symbol;
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
			stack.push(new DestinationState(tagName, atts, symbolEditable));
		} else if (tagName.equals("schedule")) {
			stack.push(new PointScheduleState(tagName, atts, symbolEditable));
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
		return editableClassName.containsKey(tagName);
	}
}
