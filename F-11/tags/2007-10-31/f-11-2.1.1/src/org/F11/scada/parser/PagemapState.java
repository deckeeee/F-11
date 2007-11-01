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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PagemapState implements State {
	private static Logger logger;

	Authenticationable authenticationable;
	PageChanger changer;
	Map itemMap;
	Object argv;

	/**
	 * ページマップ状態のオブジェクトを生成します。
	 * 
	 * @param authenticationable 認証コントロールオブジェクト
	 */
	public PagemapState(
			Authenticationable authenticationable,
			PageChanger changer,
			Object argv) {
		logger = Logger.getLogger(getClass().getName());

		this.authenticationable = authenticationable;
		this.changer = changer;
		this.argv = argv;

		// logger.debug("auth:" + authenticationable);

		itemMap = new HashMap();
	}

	/*
	 * @see orgF11.scada.parser.State#add(String, Attributes)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("page")) {
			stack.push(new PageState(tagName, atts, this));
		} else if (tagName.equals("statusbar")) {
			stack.push(new StatusbarState(tagName, atts, this));
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
		if (tagName.equals("page_map")) {
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * ベースページオブジェクトを返します。
	 * 
	 * @param key ページID
	 * @return ベースページオブジェクト
	 */
	public Map getItemMap() {
		return itemMap;
	}
}
