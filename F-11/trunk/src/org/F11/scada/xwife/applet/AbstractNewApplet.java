/*
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

package org.F11.scada.xwife.applet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.F11.scada.WifeUtilities;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.security.AccessControlable;
import org.F11.scada.server.alarm.table.AlarmListFinder;
import org.F11.scada.theme.LogoFactory;
import org.F11.scada.xwife.applet.comp.SystemToolBar;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public abstract class AbstractNewApplet extends AbstractWifeApplet {
	private static final long serialVersionUID = -2238806542284807713L;
	/** Log4j Logging オブジェクトのインスタンスです */
	protected transient static Logger logger;

	/** 履歴検索オブジェクトのリモート参照 */
	protected transient AlarmListFinder alarmListFinder;

	public AbstractNewApplet(boolean isStandalone, boolean soundoffAtStarted) throws RemoteException {
		super(isStandalone, soundoffAtStarted);
		logger = Logger.getLogger(getClass().getName());
	}

	protected void lookup() throws MalformedURLException, RemoteException,
			NotBoundException {
		accessControl =
			(AccessControlable) Naming.lookup(WifeUtilities
				.createRmiActionControl());
		alarmListFinder =
			(AlarmListFinder) Naming.lookup(WifeUtilities
				.createRmiAlarmListFinderManager());
	}

	protected void layoutContainer() throws IOException, SAXException {
		// 画面左のツリー
		JPanel treePanel = new JPanel(new BorderLayout());
		TreeDefine treeDefine = frameDef.getMenuTreeRoot(subject.getUserName());
		tree = new PageTree(treeDefine.getRootNode(), history, configuration);
		JScrollPane treePane = new JScrollPane(tree);
		treePanel.add(treePane, BorderLayout.CENTER);
		Box treeBox = createBandFButton();

		treePanel.add(treeBox, BorderLayout.NORTH);
		treePanel.setMinimumSize(new Dimension(0, 0));

		mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		spane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		// ツリーの横幅
		spane.setDividerLocation(configuration.getInt(
			"xwife.applet.Applet.treeWidth",
			150));
		spane.setDividerSize(10);
		spane.setOneTouchExpandable(true);
		spane.add(treePanel);
		mainSplit.add(spane);

		// 最新情報及び警報検索
		JComponent alarmComp =
			createAlarmComponent(this, "/resources/AlarmDefine.xml");
		mainSplit.add(alarmComp);

		// 画面用ツールバー
		WifeToolBar pageToolBarPanel = new WifeToolBar(this);
		tree.addTreeSelectionListener(pageToolBarPanel);

		JPanel toolBarsPanel = new JPanel(new BorderLayout());
		toolBarsPanel.add(new SystemToolBar(this), BorderLayout.WEST);
		toolBarsPanel.add(frameDef.getStatusBar(), BorderLayout.EAST);
		toolBarsPanel.add(pageToolBarPanel, BorderLayout.CENTER);

		JPanel LogoAndToolBarsPanel = new JPanel(new BorderLayout());
		LogoFactory logoFactory = new LogoFactory();
		LogoAndToolBarsPanel.add(logoFactory.getLogo(this), BorderLayout.EAST);
		LogoAndToolBarsPanel.add(toolBarsPanel, BorderLayout.CENTER);
		getContentPane().add(LogoAndToolBarsPanel, BorderLayout.NORTH);

		mainSplit.setOneTouchExpandable(true);
		// 警報以外の縦幅
		mainSplit.setDividerLocation(configuration.getInt(
			"xwife.applet.Applet.treeHeight",
			775));
		mainSplit.setDividerSize(10);
		getContentPane().add(mainSplit);

		// 初期画面を開きます
		/*
		 * final TreePath path = searchTreePath(treeDefine.getInitPage());
		 * logger.info("init page = " + treeDefine.getInitPage() + " ,path = " +
		 * path); SwingUtilities.invokeLater(new Runnable() { public void run()
		 * { tree.setSelectionPath(path); tree.expandPath(path);
		 * tree.requestFocusInWindow(); } });
		 */
		splashScreen.incrementValue();
	}

	protected abstract JComponent createAlarmComponent(AbstractNewApplet applet,
			String alarmDefPath);
}