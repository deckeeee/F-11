/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.xwife.applet;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.tree.TreePath;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.security.AccessControlable;
import org.F11.scada.theme.LogoFactory;
import org.F11.scada.xwife.applet.alarm.AlarmStats;
import org.F11.scada.xwife.applet.alarm.AlarmTabbedPane;
import org.F11.scada.xwife.applet.alarm.PriorityController;
import org.F11.scada.xwife.applet.comp.SystemToolBar;
import org.xml.sax.SAXException;

/**
 * WIFE システムのメインコンソール画面です。 ツリー形式の選択画面、平面図、一覧表、警報一覧表等を表示します。
 */
public class WifeApplet extends AbstractWifeApplet {
	private static final long serialVersionUID = 9159543828998179769L;

	/** Log4j Logging オブジェクトのインスタンスです */

	/**
	 * アプレットを初期化します。ユーザー主体情報もここで初期化されます。
	 */
	public WifeApplet() throws RemoteException {
		this(false, false);
	}

	public WifeApplet(boolean isStandalone, boolean soundoffAtStarted)
			throws RemoteException {
		super(isStandalone, soundoffAtStarted);
	}

	protected void lookup()
		throws MalformedURLException,
		RemoteException,
		NotBoundException {
		accessControl =
			(AccessControlable) Naming.lookup(WifeUtilities
					.createRmiActionControl());
	}

	protected void layoutContainer() throws IOException, SAXException {
		// 画面左のツリー
		JPanel treePanel = new JPanel(new BorderLayout());
		treePanel.setMinimumSize(Globals.ZERO_DIMENSION);
		TreeDefine treeDefine = frameDef.getMenuTreeRoot(subject.getUserName());
		tree = new PageTree(treeDefine.getRootNode(), history, configuration);
		JScrollPane treePane = new JScrollPane(tree);
		treePanel.add(treePane, BorderLayout.CENTER);
		Box treeBox = createBandFButton();

		treePanel.add(treeBox, BorderLayout.NORTH);

		mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		spane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		spane.setDividerLocation(configuration.getInt(
				"xwife.applet.Applet.treeWidth", 150));
		spane.setDividerSize(10);
		spane.setOneTouchExpandable(true);
		spane.add(treePanel);
		mainSplit.add(spane);

		AlarmTabbedPane alarmTabPane =
			new AlarmTabbedPane(this, JTabbedPane.RIGHT);

		// 最新情報
		AlarmStats newMsg = new AlarmStats();
		alarmTabPane.addTableModelListener(newMsg);
		alarmTabPane.addTableModelListener(new PriorityController(this));
		JPanel alarmPanel = new JPanel();
		alarmPanel.setLayout(new BoxLayout(alarmPanel, BoxLayout.Y_AXIS));
		alarmPanel.add(newMsg);
		alarmPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		alarmPanel.setBackground(newMsg.getBackGroundColor());
		alarmPanel.setOpaque(true);

		JPanel p2 = new JPanel(new BorderLayout());
		p2.setMinimumSize(Globals.ZERO_DIMENSION);
		p2.add(alarmPanel, BorderLayout.NORTH);
		p2.add(alarmTabPane, BorderLayout.CENTER);

		mainSplit.add(p2);

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
		mainSplit.setDividerLocation(configuration.getInt(
				"xwife.applet.Applet.treeHeight", 775));
		mainSplit.setDividerSize(10);
		getContentPane().add(mainSplit);

		// 初期画面を開きます
		final TreePath path = searchTreePath(treeDefine.getInitPage());
		System.out.println("path : " + path);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tree.setSelectionPath(path);
				tree.expandPath(path);
			}
		});
	}

	// Main メソッド
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		boolean sound = false;
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if ("-nosound".equalsIgnoreCase(args[i])) {
					sound = true;
				}
			}
		}
		WifeApplet applet = null;
		try {
			applet = new WifeApplet(true, sound);
		} catch (RemoteException e) {
			JOptionPane.showInternalMessageDialog(frame,
					ServerErrorUtil.ERROR_MESSAGE,
					ServerErrorUtil.ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
		}
		setCloseAction(frame, applet);
		frame.setTitle(getTitle());
		frame.getContentPane().add(applet, BorderLayout.CENTER);
		try {
			applet.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		applet.start();
		applet.setFrameBounds(frame);
		if (applet.configuration.getBoolean("xwife.applet.Applet.maximized",
				false)) {
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
		frame.setVisible(true);
	}

}
