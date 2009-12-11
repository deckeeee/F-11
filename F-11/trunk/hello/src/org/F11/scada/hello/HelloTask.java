/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.hello;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JTextField;

import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.deploy.PageDefineUtil;
import org.F11.scada.server.frame.PageDefine;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.seasar.framework.container.S2Container;
import org.xml.sax.SAXException;

/**
 * ジャンプページ更新を実行するタスクです。
 *
 * @author maekawa
 *
 */
public class HelloTask extends Task<Void, Void> {
	private final Log log = LogFactory.getLog(HelloTask.class);
	/** ファイルとページ構成オブジェクトのマップ */
	private Map<File, PageFileStruct> pageMap;

	public HelloTask(Application application, JTextField fileField)
			throws IOException,
			SAXException {
		super(application);
		setUserCanCancel(true);
		pageMap = getPageMap(getFilelistMap(fileField));
	}

	private Map<File, Integer> getFilelistMap(JTextField fileField)
		throws IOException {
		Map<File, Integer> filelistMap = new LinkedHashMap<File, Integer>();
		BufferedReader in = null;
		try {
			in =
				new BufferedReader(new InputStreamReader(new FileInputStream(
						fileField.getText())));
			String s = in.readLine();
			while (s != null) {
				if (!s.startsWith("!") && s.length() != 0) {
					String[] st = s.split("\\s+|0:");
					filelistMap.put(new File(st[0]), Integer.parseInt(st[2]));
				}
				s = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return filelistMap;
	}

	private Map<File, PageFileStruct> getPageMap(Map<File, Integer> filelistMap)
		throws IOException,
		SAXException {
		Map<File, PageFileStruct> pageMap =
			new LinkedHashMap<File, PageFileStruct>();
		for (Map.Entry<File, Integer> entry : filelistMap.entrySet()) {
			BufferedInputStream in = null;
			try {
				in =
					new BufferedInputStream(new FileInputStream(entry.getKey()));
				Map<String, PageDefine> map = PageDefineUtil.parse(in);
				pageMap.put(entry.getKey(), new PageFileStruct(
						entry.getValue(), map));
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
		return pageMap;
	}

	@Override
	protected Void doInBackground() throws InterruptedException {
		S2Container container = S2ContainerUtil.getS2Container();
		ItemDao dao = (ItemDao) container.getComponent(ItemDao.class);
		Map<HolderString, Integer> holderMap =
			new HashMap<HolderString, Integer>();
		int i = 0;
		for (Map.Entry<File, PageFileStruct> entry : pageMap.entrySet()) {
			setMessage("Page : "
				+ entry.getKey().getAbsolutePath()
				+ " を処理中です。");
			setProgress(i, 0, pageMap.size());
			for (HolderString hs : entry.getValue().getHolders()) {
				Integer priority = entry.getValue().getPriority();
				if (priority != 0) {
					String page = entry.getValue().getPageName();
					if (holderMap.containsKey(hs)) {
						if (holderMap.get(hs) > priority) {
							holderMap.put(hs, priority);
							dao.updateJumpPage(page, hs.getProvider(), hs
									.getHolder());
						}
					} else {
						holderMap.put(hs, priority);
						dao.updateJumpPage(page, hs.getProvider(), hs
								.getHolder());
					}
				}
			}
			i++;
		}
		return null;
	}

	@Override
	protected void succeeded(Void ignored) {
		setMessage("終了");
	}

	@Override
	protected void cancelled() {
		setMessage("キャンセル");
	}

	private static class PageFileStruct {
		private final Integer priority;
		private final Map<String, PageDefine> pageMap;
		private final String pageName;
		private final PageDefine pageDefine;

		public PageFileStruct(Integer priority, Map<String, PageDefine> pageMap) {
			this.priority = priority;
			this.pageMap = pageMap;
			Set<String> keyset = pageMap.keySet();
			PageDefine pd = null;
			String pn = null;
			if (keyset.isEmpty()) {
				throw new IllegalArgumentException();
			} else {
				for (String string : keyset) {
					pd = pageMap.get(string);
					pn = string;
					break;
				}
			}
			pageDefine = pd;
			pageName = pn;
		}

		public Integer getPriority() {
			return priority;
		}

		public Set<HolderString> getHolders() {
			return pageDefine.getDataHolders();
		}

		public String getPageName() {
			return pageName;
		}

	}
}
