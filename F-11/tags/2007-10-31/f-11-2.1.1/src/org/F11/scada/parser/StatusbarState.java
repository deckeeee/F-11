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

import org.F11.scada.applet.symbol.StatusBar;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /page_map/statusbar 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class StatusbarState implements State, SymbolContainerState {
	private static final String ITEM_KEY_STATUSBAR = "STATUSBAR";
	private static Logger logger;

	private PagemapState pagemapState;
	private StatusBar statusBar;

	/**
	 * 状態オブジェクトを生成します。
	 */
	public StatusbarState(
			String tagName,
			Attributes atts,
			PagemapState pagemapState) {

		logger = Logger.getLogger(getClass());

		this.pagemapState = pagemapState;
		statusBar = new StatusBar();
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
		} else if ("programexecutebutton".equalsIgnoreCase(tagName)) {
			stack.push(new ProgramExecuteButtonState(tagName, atts, this));
		} else {
			logger.info("tagName:" + tagName);
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("statusbar")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Pop : " + DisplayState.toString(tagName, stack));
			}
			pagemapState.itemMap.put(ITEM_KEY_STATUSBAR, statusBar);
			statusBar = null;
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * ベースにシンボルを追加します。
	 * @param comp コンポーネントオブジェクト
	 */	
	public void addPageSymbol(JComponent comp) {
		statusBar.addPageSymbol(comp);
	}
	
	/**
	 * 認証オブジェクトを返します。
	 * @return 認証オブジェクト
	 */
	public Authenticationable getAuthenticationable() {
		return pagemapState.authenticationable;
	}

	/**
	 * ページ切替オブジェクトを返します。
	 * @return ページ切替オブジェクト
	 */
	public PageChanger getPageChanger() {
		return pagemapState.changer;
	}

}
