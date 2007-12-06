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
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.DataProviderProxyDefineable;
import org.F11.scada.applet.symbol.BasePane;
import org.F11.scada.applet.symbol.Editable;
import org.F11.scada.applet.symbol.StatusBar;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.server.deploy.PageDefineUtil;
import org.F11.scada.server.frame.FrameDefineHandler;
import org.F11.scada.server.frame.FrameDefineHandlerFactory;
import org.F11.scada.server.frame.PageDefine;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.test.util.TestUtil;
import org.F11.scada.xwife.applet.PageChangeEvent;
import org.F11.scada.xwife.applet.PageChanger;
import org.F11.scada.xwife.applet.Session;
import org.apache.commons.configuration.Configuration;
import org.xml.sax.SAXException;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AppletFrameDefineTest extends TestCase {
	private AppletFrameDefine appletFrameDefine;
	private Session session;
	private DataProvider dp;
	private TestFrameDefineHandlerFactory frameDefineHandlerFactory;

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(AppletFrameDefineTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		dp = TestUtil.createDP();
		Manager.getInstance().addDataProvider(dp);
		frameDefineHandlerFactory = new TestFrameDefineHandlerFactory();
		appletFrameDefine = new AppletFrameDefine(
				new TestAuthenticationable(),
				new TestPageChanger(),
				new TestDataProviderProxyDefineable(dp),
				frameDefineHandlerFactory);
		session = new Session();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		TestUtil.crearJIM();
		super.tearDown();
	}

	/**
	 * Constructor for AppletFrameDefineTest.
	 * 
	 * @param name
	 */
	public AppletFrameDefineTest(String name) {
		super(name);
	}

	public void testGetPage() {
		Map pageMap = appletFrameDefine.getPage("name1", session);
		BasePane bp = (BasePane) pageMap.get(AppletFrameDefine.ITEM_KEY_PANE);
		assertNotNull(bp);
		assertFalse(bp.isCache());

		Map pageMap2 = appletFrameDefine.getPage("name1", session);
		BasePane bp2 = (BasePane) pageMap2.get(AppletFrameDefine.ITEM_KEY_PANE);
		assertNotNull(bp2);
		assertFalse(bp2.isCache());

		assertFalse(pageMap == pageMap2);

		assertNotNull(dp.getDataHolder("D_1900000_Digital"));
		assertNotNull(dp.getDataHolder("D_1900001_Digital"));
		assertNotNull(dp.getDataHolder("D_1900002_Digital"));
	}

	public void testGetPageCache() {
		Map pageMap = appletFrameDefine.getPage("name2", session);
		BasePane bp = (BasePane) pageMap.get(AppletFrameDefine.ITEM_KEY_PANE);
		assertNotNull(bp);
		assertTrue(bp.isCache());

		Map pageMap2 = appletFrameDefine.getPage("name2", session);
		BasePane bp2 = (BasePane) pageMap2.get(AppletFrameDefine.ITEM_KEY_PANE);
		assertNotNull(bp2);
		assertTrue(bp2.isCache());

		assertSame(pageMap, pageMap2);

		assertNotNull(dp.getDataHolder("D_1910000_Digital"));
		assertNotNull(dp.getDataHolder("D_1910001_Digital"));
		assertNotNull(dp.getDataHolder("D_1910002_Digital"));

		pageMap = appletFrameDefine.getPage("name1", session);
		bp = (BasePane) pageMap.get(AppletFrameDefine.ITEM_KEY_PANE);
		assertNotNull(bp);
		assertFalse(bp.isCache());

		assertNotNull(dp.getDataHolder("D_1900000_Digital"));
		assertNotNull(dp.getDataHolder("D_1900001_Digital"));
		assertNotNull(dp.getDataHolder("D_1900002_Digital"));
		assertNotNull(dp.getDataHolder("D_1910000_Digital"));
		assertNotNull(dp.getDataHolder("D_1910001_Digital"));
		assertNotNull(dp.getDataHolder("D_1910002_Digital"));
	}

	public void testGetPageTimeModify() {
		Map pageMap = appletFrameDefine.getPage("name2", session);
		BasePane bp = (BasePane) pageMap.get(AppletFrameDefine.ITEM_KEY_PANE);
		assertNotNull(bp);
		assertTrue(bp.isCache());

		frameDefineHandlerFactory.getTestFrameDefineHandler().setTime(
				"name2",
				System.currentTimeMillis() + 100000L);

		Map pageMap2 = appletFrameDefine.getPage("name2", session);
		BasePane bp2 = (BasePane) pageMap2.get(AppletFrameDefine.ITEM_KEY_PANE);
		assertNotNull(bp2);
		assertTrue(bp2.isCache());

		assertFalse(pageMap == pageMap2);
	}

	public void testGetStatusBar() throws Exception {
		JComponent comp = appletFrameDefine.getStatusBar();
		assertNotNull(comp);
		assertTrue(comp instanceof StatusBar);

		comp = appletFrameDefine.getStatusBar();
		assertNotNull(comp);
		assertTrue(comp instanceof JPanel);

		frameDefineHandlerFactory.getTestFrameDefineHandler().setTime(
				AppletFrameDefine.ITEM_KEY_STATUSBAR,
				System.currentTimeMillis() + 100000L);
		comp = appletFrameDefine.getStatusBar();
		assertNotNull(comp);
		assertTrue(comp instanceof StatusBar);
	}

	static class TestAuthenticationable implements Authenticationable {

		public void addEditable(Editable symbol) {
		}

		public void logout() {
		}

		public void removeEditable(Editable symbol) {
		}

		public void showAuthenticationDialog() {
		}

		public Subject getSubject() {
			return null;
		}

		public Configuration getConfiguration() {
			return new ClientConfiguration();
		}
	}

	static class TestPageChanger implements PageChanger {

		public void changePage(PageChangeEvent pageChange) {
		}

		public boolean isDisplayLock() {
			return false;
		}

		public void setDisplayLock(boolean isDisplayLock) {
		}

		public void playAlarm(String soundPath) {
		}

		public void pressShiftKey() {
		}
	}

	static class TestDataProviderProxyDefineable implements
			DataProviderProxyDefineable {
		private final DataProvider dp;

		public TestDataProviderProxyDefineable(DataProvider dp) {
			this.dp = dp;
		}

		public void addDataHolder(Set dataHolders) {
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				if (dp.getDataHolder(hs.getHolder()) == null) {
					DataHolder dh = new DataHolder();
					dh.setDataHolderName(hs.getHolder());
					dh.setValueClass(WifeData.class);
					dh.setValue(
							WifeDataDigital.valueOfTrue(0),
							new Date(),
							WifeQualityFlag.GOOD);
					try {
						dp.addDataHolder(dh);
					} catch (DataProviderDoesNotSupportException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	static class TestFrameDefineHandlerFactory implements
			FrameDefineHandlerFactory {
		private final TestFrameDefineHandler frameDefineHandler;

		public TestFrameDefineHandlerFactory() {
			frameDefineHandler = new TestFrameDefineHandler();
		}

		public FrameDefineHandler getFrameDefineHandler() {
			return frameDefineHandler;
		}

		public TestFrameDefineHandler getTestFrameDefineHandler() {
			return frameDefineHandler;
		}

		static class TestFrameDefineHandler implements FrameDefineHandler {
			private Map pageMap;

			public TestFrameDefineHandler() {
				InputStream inputStream = TestFrameDefineHandler.class
						.getResourceAsStream("/org/F11/scada/parser/AppletFrameDefineTest.xml");
				try {
					pageMap = PageDefineUtil.parse(inputStream);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
				System.out.println(pageMap);
			}

			public PageDefine getPage(String name, long key, Session session)
					throws RemoteException {
				return getPage(name, key);
			}

			public PageDefine getPage(String name, long key)
					throws RemoteException {
				PageDefine page = (PageDefine) pageMap.get(name);
				if (isCreateNewPage(key, page)) {
					return page;
				}
				return null;
			}

			public PageDefine getStatusbar(long key) throws RemoteException {
				PageDefine page = (PageDefine) pageMap
						.get(AppletFrameDefine.ITEM_KEY_STATUSBAR);
				if (isCreateNewPage(key, page)) {
					return page;
				}
				return null;
			}

			/**
			 * @param key
			 * @param page
			 * @return
			 */
			private boolean isCreateNewPage(long key, PageDefine page) {
				// System.out.println("key : " + key + " page key : " +
				// page.getEditTime());
				return page != null && key < page.getEditTime();
			}

			public void setTime(String name, long time) {
				PageDefine page = (PageDefine) pageMap.get(name);
				PageDefine newPage = new PageDefine(time, page.getSrcXml());
				pageMap.put(name, newPage);
			}

			public TreeDefine getMenuTreeRoot(String user)
					throws RemoteException {
				return null;
			}

			public List getCachePages() {
				return null;
			}
		}
	}
}
