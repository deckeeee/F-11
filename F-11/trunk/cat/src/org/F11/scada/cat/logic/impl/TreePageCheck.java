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

package org.F11.scada.cat.logic.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.cat.logic.ExecuteTask;
import org.F11.scada.cat.util.ExtFileFilter;
import org.F11.scada.server.deploy.FileLister;
import org.F11.scada.server.deploy.PageDefineUtil;
import org.F11.scada.server.frame.PageDefine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.xml.sax.SAXException;

/**
 * ツリーに定義されているページ定義が存在しているかをチェックするロジック。
 * 
 * @author maekawa
 *
 */
public class TreePageCheck extends AbstractCheckLogic {
	private static final String TREE_FOLDER = "treedefine";
	private static final ExtFileFilter FILTER = new ExtFileFilter(".xml");
	private static final String CHECK_LOG = "tree_page.log";
	private final Log log = LogFactory.getLog(TreePageCheck.class);
	/** コメント処理中の有無 */
	private boolean isComment;
	@Resource
	private String text;
	@Resource
	private String fileNotFoundMsg;
	@Resource
	private String saxErrorMsg;
	@Resource
	private String formatMsg;

	public TreePageCheck() {
		outFile = getOutFile(CHECK_LOG);
		Application.getInstance().getContext().getResourceMap(
			AbstractCheckLogic.class).injectFields(this);
	}

	@Override
	public String toString() {
		return text;
	}

	public void execute(String path, ExecuteTask task)
			throws IOException {
		if (isSelected) {
			Formatter out = null;
			try {
				out = new Formatter(outFile);
				FileLister lister = new FileLister();
				Collection<File> files =
					lister.listFiles(getRoot(path), FILTER);
				Map<String, PageDefine> pageMap = createPageMap(files, task);
				checkTree(out, path, pageMap, task);
			} finally {
				if (null != out) {
					out.close();
				}
			}
		}
	}

	private Map<String, PageDefine> createPageMap(
			Collection<File> files,
			ExecuteTask task) throws IOException {
		HashMap<String, PageDefine> map = new HashMap<String, PageDefine>();
		int value = 0;
		for (File file : files) {
			if (task.isCancelled()) {
				break;
			}
			map.putAll(getPageMap(file));
			task.setMsg(toString() + "実行中...");
			task.setProgress(value++, files.size());
		}
		return map;
	}

	private Map<String, PageDefine> getPageMap(File file)
			throws IOException {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			return PageDefineUtil.parse(in);
		} catch (SAXException e) {
			log.error(saxErrorMsg, e);
		} catch (FileNotFoundException e) {
			log.error(fileNotFoundMsg + file.getAbsolutePath(), e);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return Collections.emptyMap();
	}

	private void checkTree(
			Formatter out,
			String path,
			Map<String, PageDefine> pageMap,
			ExecuteTask task) throws IOException {
		FileLister lister = new FileLister();
		Collection<File> files =
			lister.listFiles(new File(path, TREE_FOLDER), FILTER);
		int value = 0;
		for (File file : files) {
			if (task.isCancelled()) {
				break;
			}
			checkFile(out, path, file, pageMap);
			task.setMsg(toString() + "実行中...");
			task.setProgress(value++, files.size());
		}
	}

	private void checkFile(
			Formatter out,
			String path,
			File file,
			Map<String, PageDefine> pageMap)
			throws IOException {
		LineNumberReader in = null;
		try {
			in = new LineNumberReader(new FileReader(file));
			for (String line = in.readLine(); null != line; line =
				in.readLine()) {
				checkLine(out, path, file, line, pageMap, in.getLineNumber());
			}
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	private void checkLine(
			Formatter out,
			String path,
			File file,
			String line,
			Map<String, PageDefine> pageMap,
			int i) {
		checkComment(line);
		if (!isComment) {
			String pageName = getPageName(line);
			if (isContainPage(pageMap, pageName)) {
				write(out, file, pageName, i);
			}
		}
	}

	private boolean isContainPage(
			Map<String, PageDefine> pageMap,
			String pageName) {
		return null != pageName
			&& !"".equals(pageName)
			&& !pageMap.containsKey(pageName);
	}

	private void checkComment(String line) {
		String startStr = "<!--";
		int start = line.lastIndexOf(startStr);
		if (start >= 0) {
			isComment = true;
		}
		String endStr = "-->";
		int end = line.lastIndexOf(endStr, start + startStr.length());
		if (end >= 0) {
			isComment = false;
		}
	}

	private String getPageName(String line) {
		String startStr = "page=\"";
		String endStr = "\"";
		int start = line.indexOf(startStr);
		if (start >= 0) {
			int end = line.indexOf(endStr, start + startStr.length());
			if (end >= 0) {
				return line.substring(start + startStr.length(), end);
			}
		}
		return null;
	}

	private void write(Formatter out, File file, String pageName, int i) {
		out.format(formatMsg, file.getAbsolutePath(), i, pageName);
	}
}
