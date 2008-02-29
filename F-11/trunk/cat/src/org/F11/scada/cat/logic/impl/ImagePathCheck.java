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

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

import org.F11.scada.cat.util.ExtFileFilter;
import org.F11.scada.server.deploy.FileLister;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

public class ImagePathCheck extends AbstractCheckLogic {
	private static final int EXTENTION_LENGTH = 4;
	private static final String IMAGES_FOLDER = "/images/";
	private static final String CHECK_LOG = "image_path.log";
	private static final FileFilter FILTER = new ExtFileFilter(".xml", ".inc");
	private final Log log = LogFactory.getLog(ImagePathCheck.class);
	/** このチェックロジックのキャプション。AppFrameworkにて注入される。 */
	@Resource
	private String text;
	@Resource
	private String formatMsg;
	/** イメージファイルの拡張子文字列リスト */
	private List<String> extentions;
	private boolean isComment;

	public ImagePathCheck() {
		outFile = getOutFile(CHECK_LOG);
		ResourceMap resourceMap =
			Application.getInstance().getContext().getResourceMap(
				AbstractCheckLogic.class);
		resourceMap.injectFields(this);
		createExtentions(resourceMap);
	}

	private void createExtentions(ResourceMap resourceMap) {
		extentions = new ArrayList<String>();
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

	public void execute(String path) throws IOException, InterruptedException {
		if (isSelected) {
			Formatter out = null;
			try {
				out = new Formatter(outFile);
				FileLister lister = new FileLister();
				Collection<File> files =
					lister.listFiles(getRoot(path), FILTER);
				for (File file : files) {
					checkFile(file, out, path);
				}
			} finally {
				if (null != out) {
					out.close();
				}
			}
		}
	}

	private void checkFile(File file, Formatter out, String path) throws IOException {
		LineNumberReader in = null;
		try {
			in = new LineNumberReader(new FileReader(file));
			for (String line = in.readLine(); null != line; line =
				in.readLine()) {
				checkLine(line, out, in.getLineNumber(), file, path);
			}
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}

	private void checkLine(String line, Formatter out, int i, File file, String path) {
		checkComment(line);
		if (!isComment) {
			String imagePath = getImagePath(line);
			if (null != imagePath) {
				File image = new File(path, imagePath);
				if (!image.exists()) {
					write(imagePath, out, i, file);
				}
			}
		}
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
