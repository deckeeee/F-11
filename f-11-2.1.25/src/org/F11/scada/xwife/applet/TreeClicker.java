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
 */

package org.F11.scada.xwife.applet;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;

import org.F11.scada.Globals;
import org.F11.scada.Service;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.server.frame.FrameDefineHandler;
import org.apache.log4j.Logger;

/**
 * 一定期間でサーバーページアクセスを巡回するクラスです。
 *
 * Tree.txtの内容よりページのリストを生成し、1時間毎に順次ページの取得処理を 実行します。
 *
 * ※全くアクセスがないとサーバーが止まる現象の対処の為作成
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TreeClicker extends JFrame implements Service {
	private static final long serialVersionUID = -520891988665653935L;
	/** FrameDefineHandlerProxyの参照です */
	private FrameDefineHandler handler;
	/** スケジュールタイマー */
	private Timer timer;

	private int maxNodeCount;
	/** クライアントのツリー */
	private final JTree tree;

	/** Logging API */
	private static Logger log = Logger.getLogger(TreeClicker.class);

	/**
	 * 巡回クラスを初期化します。
	 *
	 * @param tree
	 */
	public TreeClicker(PageTree tree) {
		this.tree = tree;
		init();
		start();
		createTimer(new KeyClickTask());
	}

	private void init() {
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timer != null) {
					expandAll(tree);
					createTree();
					timer.restart();
				}
			}
		});
		JButton stop = new JButton("Stop");
		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (timer != null) {
					timer.stop();
				}
			}
		});
		Container con = getContentPane();
		con.setLayout(new GridLayout());
		con.add(start);
		con.add(stop);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private void expandAll(JTree tree) {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	public void start() {
	}

	private void createTimer(ActionListener l) {
		ClientConfiguration configuration = new ClientConfiguration();
		int intervalTime =
			configuration.getInt("test.treeclick.interval", 5000);
		log.info("intervalTime : " + intervalTime);
		timer = new Timer(intervalTime, l);
	}

	private void createTree() {
		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e1) {
				}
				continue;
			}
		}
		createSelectTree();
	}

	public void stop() {
		timer.stop();
	}

	private void lookup()
		throws MalformedURLException,
		RemoteException,
		NotBoundException {
		handler =
			(FrameDefineHandler) Naming.lookup(WifeUtilities
					.createRmiFrameDefineManager());
	}

	/**
	 * ツリー設定を読み込んでツリーを作成します。
	 */
	private void createSelectTree() {
		maxNodeCount = 0;
		try {
			DefaultMutableTreeNode root =
				(DefaultMutableTreeNode) handler.getMenuTreeRoot("")
						.getRootNode();
			for (Enumeration e = root.depthFirstEnumeration(); e
					.hasMoreElements();) {
				DefaultMutableTreeNode n =
					(DefaultMutableTreeNode) e.nextElement();
				maxNodeCount++;
			}
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}

	private class KeyClickTask implements ActionListener {
		private boolean isNext = true;
		private int pageCount;

		public void actionPerformed(ActionEvent e) {
			if (isNext) {
				RobotUtil.getInstance().keyClick(KeyEvent.VK_DOWN);
				pageCount++;
				if (pageCount >= maxNodeCount) {
					isNext = false;
				}
			} else {
				RobotUtil.getInstance().keyClick(KeyEvent.VK_UP);
				pageCount--;
				if (pageCount < 0) {
					isNext = true;
				}
			}
		}
	}
}
