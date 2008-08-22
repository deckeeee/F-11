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
import javax.swing.JToolBar;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.applet.symbol.BasePane;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.parser.Util.DisplayState;
import org.F11.scada.parser.graph.GraphViewState;
import org.F11.scada.parser.graph.TrendGraph2State;
import org.F11.scada.parser.graph.TrendGraphState;
import org.F11.scada.parser.graph.bargraph.BarGraphState;
import org.F11.scada.parser.graph.bargraph2.BarGraph2State;
import org.F11.scada.parser.graph.demand.DemandGraphState;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageState implements State, SymbolContainerState {
	private static Logger logger;

	PagemapState pagemapState;

	BasePane basePage;
	JComponent toolBar;

	private static final String ITEM_KEY_PANE = "PANE";
	private static final String ITEM_KEY_TOOLBAR = "TOOLBAR";
	private static final boolean isGraphCache;
	static {
		String graphCache = EnvironmentManager
				.get("/server/graphcache", "true");
		isGraphCache = Boolean.valueOf(graphCache).booleanValue();
	}

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public PageState(String tagName, Attributes atts, PagemapState pagemapState) {

		logger = Logger.getLogger(getClass());

		this.pagemapState = pagemapState;

		basePage = new BasePane();
		basePage.setPageName(atts.getValue("name"));
		if (atts.getValue("value") != null)
			basePage.setPageIcon(GraphicManager.get(atts.getValue("value")));
		String loc_x = atts.getValue("width");
		String loc_y = atts.getValue("height");
		if (loc_x != null && loc_y != null)
			basePage.setPageSize(Integer.parseInt(loc_x), Integer
					.parseInt(loc_y));
		toolBar = null;

		String pageCacheStr = atts.getValue("cache");
		if (pageCacheStr != null && "true".equalsIgnoreCase(pageCacheStr)) {
			basePage.setCache(true);
		}
	}

	/*
	 * @see orgF11.scada.parser.State#add(String, Attributes)
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
		} else if (tagName.equals("table")) {
			stack.push(new TableState(tagName, atts, this));
		} else if (tagName.equals("schedule")) {
			stack.push(new ScheduleState(tagName, atts, this));
		} else if (tagName.equals("calendar")) {
			stack.push(new CalendarState(tagName, atts, this));
		} else if (tagName.equals("trendgraph")) {
			// 自動キャッシュを有効にした場合、PageXmlCacheRuleSetにルールを追加すること
			stack.push(new TrendGraphState(tagName, atts, this, pagemapState.argv));
			setPageCache();
		} else if (tagName.equals("trendgraph2")) {
			stack.push(new TrendGraph2State(tagName, atts, this, pagemapState.argv));
			setPageCache();
		} else if (tagName.equals("bargraph")) {
			stack.push(new BarGraphState(tagName, atts, this));
			setPageCache();
		} else if (tagName.equals("demandgraph")) {
			stack.push(new DemandGraphState(tagName, atts, this));
		} else if (tagName.equals("drawsymbol")) {
			stack.push(new DrawSymbolState(tagName, atts, this));
		} else if (tagName.equals("pagechangebutton")) {
			stack.push(new PageChangeButtonState(tagName, atts, this));
		} else if (tagName.equals("linegraphsymbol")) {
			stack.push(new LineGraphSymbolState(tagName, atts, this));
		} else if (tagName.equals("programexecutebutton")) {
			stack.push(new ProgramExecuteButtonState(tagName, atts, this));
		} else if (tagName.equals("openurlbutton")) {
			stack.push(new OpenURLButtonState(tagName, atts, this));
		} else if (tagName.equals("operationlogging")) {
			stack.push(new OperationLoggingState(tagName, atts, this));
		} else if (tagName.equals("graphview")) {
			stack.push(new GraphViewState(tagName, atts, this));
		} else if ("trendjumpbutton".equals(tagName)) {
			stack.push(new TrendJumpButtonState(tagName, atts, this));
		} else if (tagName.equals("bargraph2")) {
			stack.push(new BarGraph2State(tagName, atts, this));
			setPageCache();
		} else {
			logger.info("tagName:" + tagName);
		}
	}

	private void setPageCache() {
		if (isGraphCache) {
			basePage.setCache(true);
		}
	}

	/*
	 * 終了タグイベントの処理
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("page")) {
			stack.pop();

			pagemapState.itemMap.put(ITEM_KEY_PANE, basePage);
			if (toolBar == null) {
				toolBar = createToolBar();
			}
			pagemapState.itemMap.put(ITEM_KEY_TOOLBAR, toolBar);
			basePage = null;
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	private JComponent createToolBar() {
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		return tb;
	}

	/**
	 * ベースにシンボルを追加します。
	 * 
	 * @param comp コンポーネントオブジェクト
	 */
	public void addPageSymbol(JComponent comp) {
		basePage.addPageSymbol(comp);
	}

	/**
	 * 認証オブジェクトを返します。
	 * 
	 * @return 認証オブジェクト
	 */
	public Authenticationable getAuthenticationable() {
		return pagemapState.authenticationable;
	}

	/**
	 * ツールバーオブジェクトを設定します。
	 * 
	 * @param comp ツールバーオブジェクト
	 */
	public void setToolBar(JComponent comp) {
		toolBar = comp;
	}

	/**
	 * ページ切替オブジェクトを返します。
	 * 
	 * @return ページ切替オブジェクト
	 */
	public PageChanger getPageChanger() {
		return pagemapState.changer;
	}
}
