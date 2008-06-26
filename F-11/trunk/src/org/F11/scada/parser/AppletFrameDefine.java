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

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.applet.DataProviderProxy;
import org.F11.scada.applet.DataProviderProxyDefineable;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.applet.symbol.BasePane;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.server.frame.FrameDefineHandler;
import org.F11.scada.server.frame.FrameDefineHandlerFactory;
import org.F11.scada.server.frame.PageDefine;
import org.F11.scada.server.frame.impl.FrameDefineHandlerFactoryImpl;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ConcurrentHashSet;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.applet.PageChanger;
import org.F11.scada.xwife.applet.Session;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * ページオブジェクトを管理するクラスです。 読み込まれていないページを RMI経由で取得します。
 * 
 * @author hori
 */
public class AppletFrameDefine {
	/** FrameDefineHandlerProxyの参照です */
	private FrameDefineHandler handler;
	/** 認証オブジェクトの参照 */
	private Authenticationable authenticationable;
	/** ページ変更オブジェクトの参照 */
	private PageChanger changer;
	/** ステータスバーオブジェクトを保持します。 */
	private JComponent statusBar;
	/** ステータスバーページオブジェクトを保持します。 */
	private PageDefine statusBarPage;
	/** ステータスバーオブジェクトの更新日を保持します。 */
	private long statusBarTime;
	/** サーバーエラー例外 */
	private Exception serverError;
	/** Logging API */
	private static Logger logger = Logger.getLogger(AppletFrameDefine.class);

	/** BasePaneオブジェクトのキー値 */
	public static final String ITEM_KEY_PANE = "PANE";
	/** ToolBarオブジェクトのキー値 */
	public static final String ITEM_KEY_TOOLBAR = "TOOLBAR";
	/** ステータスバーオブジェクトのキー値 */
	public static final String ITEM_KEY_STATUSBAR = "STATUSBAR";

	/** セッション情報とページ定義のマップ */
	private final Map sessionMap = new HashMap();

	private final DataProviderProxyDefineable proxyDefine;

	private final Map cachePageMap = new ConcurrentHashMap();
	private final Set cacheHolderSet = new ConcurrentHashSet();

	private final FrameDefineHandlerFactory frameDefineHandlerFactory;

	/**
	 * コンストラクタ
	 */
	public AppletFrameDefine(
			Authenticationable authenticationable,
			PageChanger changer,
			DataProviderProxyDefineable proxyDefine) throws IOException,
			SAXException {

		this(
				authenticationable,
				changer,
				proxyDefine,
				new FrameDefineHandlerFactoryImpl());
	}

	/**
	 * コンストラクタ
	 */
	public AppletFrameDefine(
			Authenticationable authenticationable,
			PageChanger changer,
			DataProviderProxyDefineable proxyDefine,
			FrameDefineHandlerFactory frameDefineHandlerFactory)
			throws IOException, SAXException {

		this.authenticationable = authenticationable;
		this.changer = changer;
		this.proxyDefine = proxyDefine;
		this.frameDefineHandlerFactory = frameDefineHandlerFactory;

		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("AppletFrameDefine()");
				}
				serverError = e;
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e1) {
				}
				continue;
			}
		}

		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
	}

	private void lookup()
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		handler = frameDefineHandlerFactory.getFrameDefineHandler();
	}

	public void receiveCache(Session session) throws RemoteException {
		// キャッシュページをサーバーから受信しcachePageMapに追加する。
		logger.info("キャッシュページを読み込みます");
		List cachePages = handler.getCachePages();
		for (Iterator i = cachePages.iterator(); i.hasNext();) {
			String pageName = (String) i.next();
			getPage(pageName, session);
			logger.info("cached Page : " + pageName);
		}
	}

	/**
	 * RMI経由で更新されたページ定義を取得し、パースします。
	 * 
	 * @param name ページ名称
	 * @throws SAXException
	 * @throws IOException
	 */
	public Map getPage(String name, Session session) {
		if (serverError != null) {
			logger.debug(serverError);
			return null;
		}

		if (cachePageMap.containsKey(name)) {
			PageMapWrapper key = (PageMapWrapper) cachePageMap.get(name);
			return getPage(name, key, session);
		} else {
			return getPage(name, 0, session, null);
		}
	}

	/**
	 * RMI経由で更新されたページ定義を取得し、パースします。
	 * 
	 * @param name ページ名称
	 * @throws SAXException
	 * @throws IOException
	 */
	public Map getPage(String name, Session session, Object argv) {
		if (serverError != null) {
			logger.debug(serverError);
			return null;
		}
		return getPage(name, 0, session, argv);
	}

	private Map getPage(String name, PageMapWrapper key, Session session) {
		Map pageMap = key.getPageMap();
		PageDefine page = getPageDefine(name, key.getUsedTime(), session);

		if (page != null) {
			List wdps = getWifeDataProviders(page, session);
			providerLock(wdps);
			try {
				unregisterJim(session);
				registerJim(session, page);
			} finally {
				providerUnlock(wdps, page);
			}

			// logger.debug(page.getDataHolders());

			// パース
			StringReader sr = null;
			try {
				F11Handler frameDef = new F11Handler(
						authenticationable,
						changer,
						null);
				XMLReader parser = XMLReaderFactory
						.createXMLReader(EnvironmentManager.get(
								"/org.xml.sax.driver",
								""));
				parser.setContentHandler(frameDef);
				sr = new StringReader(page.getSrcXml());
				InputSource is = new InputSource(sr);
				parser.parse(is);
				pageMap = frameDef.getItemMap();

				PageMapWrapper pageMapWrapper = (PageMapWrapper) cachePageMap
						.remove(name);
				cacheHolderSet.removeAll(pageMapWrapper.pageDefine
						.getDataHolders());
				BasePane bp = (BasePane) pageMapWrapper.getPageMap().get(
						ITEM_KEY_PANE);
				bp.destroyPage();

				cachePageMap.put(name, new PageMapWrapper(System
						.currentTimeMillis(), pageMap, page));
				cacheHolderSet.addAll(page.getDataHolders());
			} catch (Exception e) {
				cachePageMap.remove(name);
				cacheHolderSet.removeAll(page.getDataHolders());
				throw new BasePaneNotFoundException(e);
			} finally {
				if (sr != null) {
					sr.close();
				}
			}
		}

		// logger.debug("pagemap : " + pageMap);

		return pageMap;
	}

	private List getWifeDataProviders(PageDefine pd, Session session) {
		HashSet set = new HashSet();
		getRemoveProvider(session, set);
		getNextProvider(pd, set);
		ArrayList wdps = new ArrayList();
		Manager manager = Manager.getInstance();
		for (Iterator i = set.iterator(); i.hasNext();) {
			String dpname = (String) i.next();
			wdps.add(manager.getDataProvider(dpname));
		}
		return wdps;
	}

	private void getRemoveProvider(Session session, HashSet set) {
		if (sessionMap.containsKey(session)) {
			PageDefine define = (PageDefine) sessionMap.get(session);
			if (!define.isCache()) {
				getNextProvider(define, set);
			}
		}
	}

	private void getNextProvider(PageDefine pd, HashSet set) {
		if (null != pd) {
			Set nextPageHolders = pd.getDataHolders();
			for (Iterator i = nextPageHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				if (!set.contains(hs.getProvider())) {
					set.add(hs.getProvider());
				}
			}
		}
	}

	private void providerLock(List wdps) {
		for (Iterator i = wdps.iterator(); i.hasNext();) {
			DataProviderProxy wdp = (DataProviderProxy) i.next();
			wdp.lock();
		}
	}

	private void providerUnlock(List wdps, PageDefine define) {
		for (Iterator i = wdps.iterator(); i.hasNext();) {
			DataProviderProxy wdp = (DataProviderProxy) i.next();
			wdp.unlock();
		}
	}

	/**
	 * @param name
	 * @param key
	 * @param session
	 * @return
	 */
	private PageDefine getPageDefine(String name, long editTime, Session session) {
		PageDefine page = null;
		for (int i = 1; i <= Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				page = handler.getPage(name, editTime, session);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e2) {
					if (logger.isDebugEnabled()) {
						logger.debug("getPageDefine()");
					}
					serverError = e2;
					e2.printStackTrace();
				}

				if (logger.isDebugEnabled()) {
					logger.debug("getPageDefine()");
				}
				e.printStackTrace();
				serverError = e;
				e.printStackTrace();
				continue;
			}
		}
		return page;
	}

	private Map getPage(String name, long editTime, Session session, Object argv) {
		logger.info("getPage 1");
		PageDefine page = getPageDefine(name, editTime, session);
		logger.info("getPage 2");

		if (page == null) {
			if (serverError != null) {
				ServerErrorUtil.invokeServerError();
				serverError.printStackTrace();
			}
			throw new BasePaneNotFoundException(name + " not found at server.");
		}

		List wdps = getWifeDataProviders(page, session);
		providerLock(wdps);
		logger.info("getPage 3");
		try {
			unregisterJim(session);
			logger.info("getPage 4");
			registerJim(session, page);
		} finally {
			logger.info("getPage 5");
			providerUnlock(wdps, page);
			logger.info("getPage 6");
		}

		// logger.debug(page.getDataHolders());

		// パース
		StringReader sr = null;
		try {
			F11Handler frameDef = new F11Handler(
					authenticationable,
					changer,
					argv);
			XMLReader parser = XMLReaderFactory
					.createXMLReader(EnvironmentManager.get(
							"/org.xml.sax.driver",
							""));
			parser.setContentHandler(frameDef);
			sr = new StringReader(page.getSrcXml());
			InputSource is = new InputSource(sr);
			parser.parse(is);
			Map pageMap = frameDef.getItemMap();

			BasePane bp = (BasePane) pageMap.get(ITEM_KEY_PANE);
			if (bp.isCache()) {
				cachePageMap.put(name, new PageMapWrapper(System
						.currentTimeMillis(), pageMap, page));
				cacheHolderSet.addAll(page.getDataHolders());
			}
			return pageMap;
		} catch (Exception e) {
			cachePageMap.remove(name);
			cacheHolderSet.removeAll(page.getDataHolders());
			throw new BasePaneNotFoundException(e);
		} finally {
			if (sr != null) {
				sr.close();
			}
		}
	}

	private synchronized void unregisterJim(Session session) {
		if (sessionMap.containsKey(session)) {
			PageDefine page = (PageDefine) sessionMap.get(session);
			Set deleteHolders = removeCacheHolders(page.getDataHolders());
			// logger.info(deleteHolders);
			Manager manager = Manager.getInstance();
			for (Iterator it = deleteHolders.iterator(); it.hasNext();) {
				HolderString hs = (HolderString) it.next();
				DataProvider dp = manager.getDataProvider(hs.getProvider());
				DataHolder dh = dp.getDataHolder(hs.getHolder());
				if (dh == null
						|| Globals.ERR_HOLDER.equals(dh.getDataHolderName())) {
					continue;
				}
				try {
					// logger.debug("before : " +
					// manager.findDataHolder(hs.getProvider(),
					// hs.getHolder()));
					dp.removeDataHolder(dh);
					// logger.debug("after : " +
					// manager.findDataHolder(hs.getProvider(),
					// hs.getHolder()));
				} catch (DataProviderDoesNotSupportException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Set removeCacheHolders(Set holders) {
		holders.removeAll(cacheHolderSet);
		if (statusBarPage != null) {
			holders.removeAll(statusBarPage.getDataHolders());
		}
		addAlarmWriteHolder(holders);
		addAlarmEventHolder(holders);

		return holders;
	}

	private void addAlarmWriteHolder(Set holders) {
		if (null != authenticationable) {
			String value = authenticationable.getConfiguration().getString(
					"xwife.applet.Applet.alarmStopKey.write",
					"");
			if (!"".equals(value)) {
				int p = value.indexOf('_');
				if (0 < p) {
					HolderString hs = new HolderString(
							value.substring(0, p),
							value.substring(p + 1));
					holders.remove(hs);
				}
			}
		}
	}

	private void addAlarmEventHolder(Set holders) {
		if (null != authenticationable) {
			String value = authenticationable.getConfiguration().getString(
					"xwife.applet.Applet.alarmStopKey.event",
					"");
			if (!"".equals(value)) {
				int p = value.indexOf('_');
				if (0 < p) {
					HolderString hs = new HolderString(
							value.substring(0, p),
							value.substring(p + 1));
					holders.remove(hs);
				}
			}
		}
	}

	private synchronized void registerJim(Session session, PageDefine page) {
		sessionMap.put(session, page);

		Set addHolders = page.getDataHolders();
		proxyDefine.addDataHolder(addHolders);
	}

	/**
	 * ステータスバーオブジェクトを返します。
	 * 
	 * @return ステータスバーオブジェクト
	 */
	public JComponent getStatusBar() {
		statusBar = new JPanel();
		if (serverError != null) {
			return statusBar;
		}

		statusBarPage = null;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				statusBarPage = handler.getStatusbar(statusBarTime);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e2) {
					if (logger.isDebugEnabled()) {
						logger.debug("getStatusBar()");
					}
					serverError = e2;
				}

				if (logger.isDebugEnabled()) {
					logger.debug("getStatusBar()");
				}
				serverError = e;
				continue;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		if (statusBarPage == null)
			return statusBar;

		StringReader sr = null;
		try {
			XMLReader parser = XMLReaderFactory
					.createXMLReader(EnvironmentManager.get(
							"/org.xml.sax.driver",
							""));
			F11Handler frameDef = new F11Handler(
					authenticationable,
					changer,
					null);
			parser.setContentHandler(frameDef);
			sr = new StringReader(statusBarPage.getSrcXml());
			InputSource is = new InputSource(sr);
			parser.parse(is);
			Map itemMap = frameDef.getItemMap();
			statusBar = (JComponent) itemMap.get(ITEM_KEY_STATUSBAR);
			statusBarTime = statusBarPage.getEditTime();
			return getStatusBarComponent();
		} catch (SAXException e) {
			logger.fatal("xmlファイルの文法に間違いがあります", e);
		} catch (IOException e) {
			logger.fatal("xmlファイルが存在しません", e);
		} finally {
			if (sr != null) {
				sr.close();
			}
		}
		return statusBar;
	}

	private JComponent getStatusBarComponent() {
		return null == statusBar ? new JPanel() : statusBar;
	}

	/**
	 * ユーザー毎のメニューツリーを返します。 指定ユーザーにメニュー定義が無ければ、デフォルトのメニューツリーを返します。
	 * 
	 * @param user ユーザー名
	 * @return メニューツリーの定義
	 * @throws RemoteException
	 */
	public TreeDefine getMenuTreeRoot(String user) {
		TreeDefine rootNode = null;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				rootNode = handler.getMenuTreeRoot(user);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e2) {
					if (logger.isDebugEnabled()) {
						logger.debug("getMenuTreeRoot()");
					}
					serverError = e2;
				}

				if (logger.isDebugEnabled()) {
					logger.debug("getMenuTreeRoot()");
				}
				serverError = e;
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				continue;
			}
		}
		return rootNode;
	}

	static class PageMapWrapper {
		/**
		 * Logger for this class
		 */

		private final long usedTime;
		private final Map pageMap;
		private final PageDefine pageDefine;

		PageMapWrapper(long usedTime, Map pageMap, PageDefine pageDefine) {
			this.usedTime = usedTime;
			this.pageMap = pageMap;
			this.pageDefine = pageDefine;
		}

		Map getPageMap() {
			return pageMap;
		}

		long getUsedTime() {
			return usedTime;
		}

		PageDefine getPageDefine() {
			return pageDefine;
		}
	}
}
