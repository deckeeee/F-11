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

import static org.F11.scada.cat.util.CollectionUtil.list;
import static org.F11.scada.cat.util.CollectionUtil.set;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Set;

import org.F11.scada.cat.logic.ExecuteTask;
import org.F11.scada.cat.util.ExtFileFilter;
import org.F11.scada.server.deploy.FileLister;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

/**
 * ページ定義内のイメージファイルが、実在しているかチェックするロジック
 * 
 * @author maekawa
 * 
 */
public class ImagePathCheck extends AbstractCheckLogic {
	private static final int EXTENTION_LENGTH = 4;
	private static final String IMAGES_FOLDER = "/images/";
	private static final FileFilter FILTER = new ExtFileFilter(".xml", ".inc");
	private final Log log = LogFactory.getLog(ImagePathCheck.class);
	/** このチェックロジックのキャプション。AppFrameworkにて注入される。 */
	@Resource
	private String text;
	@Resource
	private String formatMsg;
	@Resource
	private String checkLog;
	/** イメージファイルの拡張子文字列リスト */
	private List<String> extentions;
	/** コメント処理中の有無 */
	private final XmlCommentChecker checker;

	public ImagePathCheck() {
		ResourceMap resourceMap =
			Application.getInstance().getContext().getResourceMap(
				AbstractCheckLogic.class);
		resourceMap.injectFields(this);
		outFile = getOutFile(checkLog);
		checker = new XmlCommentChecker();
		createExtentions(resourceMap);
	}

	private void createExtentions(ResourceMap resourceMap) {
		extentions = list();
		for (int i = 0;; i++) {
			String extention = resourceMap.getString("extentions[" + i + "]");
			if ("//END".equalsIgnoreCase(extention)) {
				break;
			}
			extentions.add(extention);
		}
	}

	@Override
	public String toString() {
		return text;
	}

	public void execute(String path, ExecuteTask task) throws IOException {
		if (isSelected) {
			Set<String> imageFileNameSet = getImageFileNameSet(path);
			Formatter out = null;
			try {
				out = new Formatter(outFile);
				FileLister lister = new FileLister();
				Collection<File> files =
					lister.listFiles(getRoot(path), FILTER);
				int value = 0;
				for (File file : files) {
					if (task.isCancelled()) {
						break;
					}
					checkFile(file, out, path, imageFileNameSet);
					task.setMsg(toString() + "実行中...");
					task.setProgress(value++, files.size());
				}
			} finally {
				if (null != out) {
					out.close();
				}
			}
		}
	}

	/**
	 * /image/以降のファイルパスのセットを作ります
	 * 
	 * @param path 物件フォルダ
	 * 
	 * @return /image/以降のファイルパスのセットを作ります
	 */
	private Set<String> getImageFileNameSet(String path) {
		FileLister lister = new FileLister();
		Collection<File> files =
			lister.listFiles(new File(path, IMAGES_FOLDER), ExtFileFilter
				.getDummy());
		return getImageFileNameSet(files);
	}

	private Set<String> getImageFileNameSet(Collection<File> files) {
		Set<String> set = set(files.size());
		for (File file : files) {
			String imagePath = file.getAbsolutePath().replace('\\', '/');
			int start = imagePath.indexOf(IMAGES_FOLDER);
			if (0 <= start) {
				set.add(imagePath.substring(start));
			}
		}
		return set;
	}

	private void checkFile(
			File file,
			Formatter out,
			String path,
			Set<String> imageFileNameSet) throws IOException {
		LineNumberReader in = null;
		try {
			in = new LineNumberReader(new FileReader(file));
			for (String line = in.readLine(); null != line; line =
				in.readLine()) {
				checkLine(
					line,
					out,
					in.getLineNumber(),
					file,
					path,
					imageFileNameSet);
			}
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	private void checkLine(
			String line,
			Formatter out,
			int i,
			File file,
			String path,
			Set<String> imageFileNameSet) {
		checker.checkComment(line);
		if (!checker.isComment()) {
			String imagePath = getImagePath(line);
			if (null != imagePath) {
				if (!imageFileNameSet.contains(imagePath)) {
					write(imagePath, out, i, file);
				}
			}
		}
	}

	private String getImagePath(String line) {
		int start = line.indexOf(IMAGES_FOLDER);
		if (start >= 0) {
			int end = getEnd(line, start);
			if (end >= 0) {
				return line.substring(start, end + EXTENTION_LENGTH);
			}
		}
		return null;
	}

	private int getEnd(String line, int fromIndex) {
		int end = -1;
		for (String ext : extentions) {
			end = line.indexOf(ext, fromIndex);
			if (end >= 0) {
				break;
			}
		}
		return end;
	}

	private void write(String imagePath, Formatter out, int i, File file) {
		out.format(formatMsg, file.getAbsoluteFile(), i, imagePath);
	}
}
