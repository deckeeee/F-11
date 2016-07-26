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
package org.F11.scada.server.deploy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.parser.tree.MenuTreeDefineHandler;
import org.F11.scada.server.frame.TreeDefineManager;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;

/**
 * メニューツリー定義ファイルを配備処理をするクラスです。
 */
public class TreeFileDeployer implements Deployer {
	/** ロギングAPI */
	private static Logger logger = Logger.getLogger(PageFileDeployer.class);
	/** メニューツリー定義マネージャー */
	private final TreeDefineManager manager;
	/** メニューツリー定義ファイル名とユーザー名のマップ */
	private Map fileToDefine;

	/**
	 * 
	 */
	public TreeFileDeployer(TreeDefineManager manager) {
		super();
		this.manager = manager;
		fileToDefine = new HashMap();
	}

	/**
	 * 配備処理を行います
	 * 
	 * @param file 配備するメニューツリー定義XMLファイル
	 */
	public void deploy(File file) throws DeploymentException {
		if (file.getName().indexOf("tree") != 0)
			return;

		logger.info("deploy : " + file);

		MenuTreeDefineHandler handler = new MenuTreeDefineHandler();
		Digester digester = new Digester();
		URL url = getClass().getResource("/resources/tree10.dtd");
		if (null == url) {
			throw new IllegalStateException(
					"/resources/tree10.dtd がクラスパス上に存在しません");
		}
		digester.register("-//F-11 2.0//DTD F11 Tree Configuration//EN", url
				.toString());
		digester.setValidating(true);
		digester.push(handler);
		handler.addPageRule(digester);

		BufferedInputStream stream = null;
		try {
			// パース
			stream = new BufferedInputStream(new FileInputStream(file));

			digester.parse(stream);

			if (logger.isDebugEnabled()) {
				logger.debug(handler.getTreeDefine());
			}
			String userName = file.getName().substring("tree".length());
			int p = userName.indexOf('.');
			if (0 <= p) {
				userName = userName.substring(0, p);
			}
			if (0 < userName.length()) {
				userName = userName.substring(1);
			}

			logger.info("deploy succsess. " + userName + " userName at " + file
					+ ".");

			fileToDefine.put(file, userName);
			manager.put(userName, handler.getTreeDefine());
			stream.close();
		} catch (Exception e) {
			throw new DeploymentException("Error file = " + file, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					throw new DeploymentException(e1);
				}
			}
		}
	}

	/**
	 * 非配備処理を行います
	 * 
	 * @param file 配備するメニューツリー定義XMLファイル
	 */
	public void undeploy(File file) throws DeploymentException {
		if (file.getName().indexOf("tree") != 0)
			return;

		logger.info("undeploy : " + file);
		String userName = (String) fileToDefine.remove(file);
		if (logger.isDebugEnabled()) {
			logger.debug(userName);
		}
		if (userName == null) {
			logger.error("undeploy faild. Not deployed " + file + ".");
			return;
		}
		manager.remove(userName);
	}

	/**
	 * 引数のメニューツリー定義XMLが配備されているかを判定します
	 * 
	 * @param userName ユーザー名称
	 * @return 既に配備済みであれば true をそうでなければ false を返します
	 */
	public boolean isDeployed(String userName) {
		return manager.containsKey(userName);
	}

}
