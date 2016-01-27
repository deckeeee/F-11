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

import javax.swing.JComponent;

import org.F11.scada.applet.symbol.TableSymbol;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=page/table/data/row 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableRowState implements State, SymbolContainerState {
	private static Logger logger;

	TableDataState dataState;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public TableRowState(
			String tagName,
			Attributes atts,
			TableDataState dataState) {

		this.dataState = dataState;
		logger = Logger.getLogger(getClass().getName());
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (SymbolState.isSupport(tagName)) {
			stack.push(new SymbolState(tagName, atts, this));
		} else if (SymbolEditableState.isSupport(tagName)) {
			stack.push(new SymbolEditableState(tagName, atts, this));
		} else if (SymbolScheduleEditableState.isSupport(tagName)) {
			stack.push(new SymbolScheduleEditableState(tagName, atts, this));
		} else if (SymbolAnalog4EditableState.isSupport(tagName)) {
			stack.push(new SymbolAnalog4EditableState(tagName, atts, this));
		} else if ("trendjumpbutton".equals(tagName)) {
			stack.push(new TrendJumpButtonState(tagName, atts, this));
		} else {
			logger.info("tagName:" + tagName);
		}
		/*
		 * if (SymbolClassName.findSymbolClassName(tagName)) { stack.push(new
		 * TableRowSymbolState(tagName, atts, this)); } else {
		 * logger.debug("tagName:" + tagName); }
		 */
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		TableSymbol tableSymbol = (TableSymbol) dataState.tableState.listTable
				.getModel();
		tableSymbol.addRow(dataState.rowVector);
		stack.pop();
	}

	/**
	 * シンボルを追加します。
	 * 
	 * @param comp コンポーネントオブジェクト
	 */
	public void addPageSymbol(JComponent symbol) {
		dataState.rowVector.add(symbol);
	}

	/**
	 * 認証オブジェクトを返します。
	 * 
	 * @return 認証オブジェクト
	 */
	public Authenticationable getAuthenticationable() {
		return dataState.tableState.pageState.pagemapState.authenticationable;
	}

	/**
	 * ページ切替オブジェクトを返します。
	 * 
	 * @return ページ切替オブジェクト
	 */
	public PageChanger getPageChanger() {
		return dataState.tableState.pageState.pagemapState.changer;
	}

}
