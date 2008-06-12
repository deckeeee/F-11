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
package org.F11.scada.server.frame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.tree.TreeDefine;
import org.F11.scada.xwife.applet.PageTreeNode;
import org.F11.scada.xwife.applet.WifeApplet;

/**
 * メニューツリー定義を管理するクラスです。
 */
public class TreeDefineManager {
	// private static final Logger logger =
	// Logger.getLogger(TreeDefineManager.class);
	/** ユーザー名とツリー定義のマップです。 */
	private final Map treeMap = new ConcurrentHashMap();
	/** デフォルトのツリー定義です。 */
	private TreeDefine defaultTreeDefine;

	/**
	 * 
	 */
	public TreeDefineManager() {
		super();
		defaultTreeDefine = new TreeDefine();
		defaultTreeDefine.setRootNode((TreeNode) createNodeStack()
			.firstElement());
		String initPage =
			new AlarmDefine().getAlarmConfig().getInitConfig().getInitPage();
		defaultTreeDefine.setInitPage(initPage);
	}

	/**
	 * 引数のマップオブジェクトを全てputします
	 * 
	 * @param map ページ名称とページ定義オブジェクトのマップ
	 * @since 1.0.3
	 */
	public void put(String userName, TreeDefine define) {
		treeMap.put(userName, define);
	}

	/**
	 * 指定したユーザー名称のメニューツリー定義を削除します
	 * 
	 * @param userName 削除するユーザー名称
	 * @return 削除するメニューツリーオブジェクト
	 * @since 1.0.3
	 */
	public Object remove(String userName) {
		return treeMap.remove(userName);
	}

	/**
	 * 引数のユーザー名称が既に登録されていれば true をそうでなければ false を返します
	 * 
	 * @param userName 判定するユーザー名称
	 * @return 引数のユーザー名称が既に登録されていれば true をそうでなければ false を返します
	 * @since 1.0.3
	 */
	public boolean containsKey(String userName) {
		return treeMap.containsKey(userName);
	}

	/**
	 * ユーザー毎のメニューツリーを返します。 指定ユーザーにメニュー定義が無ければ、デフォルトのメニューツリーを返します。
	 * 
	 * @param userName ユーザー名
	 * @return メニューツリーの定義
	 * @throws RemoteException
	 */
	public TreeDefine getMenuTreeRoot(String userName) throws RemoteException {
		if (treeMap.containsKey(userName)) {
			return (TreeDefine) treeMap.get(userName);
		} else if (treeMap.containsKey("")) {
			return (TreeDefine) treeMap.get("");
		} else {
			return defaultTreeDefine;
		}
	}

	/**
	 * 旧形式のメニューツリー定義を読み込みます。
	 * 
	 * @return
	 */
	private static Stack createNodeStack() {
		Stack nodeStack = new Stack();
		// open tree data
		URL url = WifeApplet.class.getResource("/resources/tree.txt");
		if (url == null) {
			nodeStack.add(new DefaultMutableTreeNode());
			return nodeStack;
		}
		InputStream is = null;
		BufferedReader reader = null;
		try {
			// convert url to buffered string
			is = url.openStream();
			reader = new BufferedReader(new InputStreamReader(is, "SJIS"));

			// read one line at a time, put into tree
			String line = reader.readLine();
			while (line != null) {
				StringTokenizer st = new StringTokenizer(line, "\t");
				// System.out.println("reading in: ->" + line + "<-");
				int no = Integer.parseInt(st.nextToken());
				if (0 < no && no <= nodeStack.size() + 1) {
					String nodeName = st.nextToken();
					String key = "";
					if (st.hasMoreTokens())
						key = st.nextToken();
					DefaultMutableTreeNode n =
						new DefaultMutableTreeNode(new PageTreeNode(
							nodeName,
							key));

					while (no <= nodeStack.size())
						nodeStack.pop();

					if (!nodeStack.isEmpty()) {
						DefaultMutableTreeNode parentNode =
							(DefaultMutableTreeNode) nodeStack.peek();
						parentNode.add(n);
					}
					nodeStack.push(n);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return nodeStack;
	}

}
