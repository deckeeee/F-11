/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/TableRowSymbolState.java,v 1.8.6.2 2006/02/16 04:59:00 frdm Exp $
 * $Revision: 1.8.6.2 $
 * $Date: 2006/02/16 04:59:00 $
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
package org.F11.scada.parser;

import java.lang.reflect.Constructor;
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
 * XPath=page/table/data/row/&lt;シンボル&gt; 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableRowSymbolState implements State {
	private static Logger logger;

	private Stack parentStack;
	private DataReferencerOwner referencerOwner;
	private Editable symbolEditable;
	private Symbol symbol;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TableRowSymbolState(
			String tagName,
			Attributes atts,
			TableRowState state) {

		logger = Logger.getLogger(getClass().getName());

		if (SymbolClassName.findSymbolClassName(tagName)) {
			createSymbol(state, createSymbol(tagName, atts, state));
		} else {
			throw new IllegalArgumentException(tagName + " is non support tag.");
		}
	}

	
	private Symbol createSymbol(String tagName, Attributes atts, TableRowState state) {
		try {
			Class className =
				Class.forName(
					SymbolClassName.getSymbolClassName(tagName));
			logger.debug("ClassName:" + className.getName());
			try {
				Constructor constructor =
					className.getConstructor(
						new Class[] {
							SymbolProperty.class,
							Authenticationable.class });
				Object[] para =
					new Object[] {
						new SymbolProperty(atts),
						state.dataState.tableState.pageState.pagemapState.authenticationable};
				logger.debug("constructor:" + constructor);
				return (Symbol) constructor.newInstance(para);
			} catch (Exception e1) {
				try {
					Constructor constructor =
						className.getConstructor(
							new Class[] { SymbolProperty.class });
					Object[] para = new Object[] { new SymbolProperty(atts)};
					logger.debug("constructor:" + constructor);
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
	
	private void createSymbol(TableRowState state, Symbol symbol) {
		this.symbol = symbol;
		state.dataState.rowVector.add(this.symbol);

		parentStack = new Stack();
		parentStack.push(this.symbol);
		referencerOwner = this.symbol;
		if (symbol instanceof Editable) {
			symbolEditable = (Editable) this.symbol;
		} else {
			symbolEditable = null;
		}
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("if")) {
			stack.push(
				new IfState(tagName, atts, parentStack, referencerOwner));

		} else if (tagName.equals("destination")) {
			stack.push(new DestinationState(tagName, atts, symbolEditable));

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (SymbolClassName.findSymbolClassName(tagName)) {
			symbol.updateProperty();
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}
}
