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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.F11.scada.cat.util.ExtFileFilter;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.deploy.FileLister;
import org.F11.scada.server.deploy.PageDefineUtil;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.frame.PageDefine;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.xml.sax.SAXException;

/**
 * ページに使われているホルダが、item_tableに定義されているかチェックするロジック。
 * もれているホルダが存在した場合に、check/holder_define.logに書き出す。
 * 
 * @author maekawa
 * 
 */
public class HolderDefineCheck extends AbstractCheckLogic {
	private static final int INCLUDE_DEFINE_LINE = 2;
	private static final String CHECK_LOG = "holder_define.log";
	private static final ExtFileFilter FILTER = new ExtFileFilter(".xml");
	private final Log log = LogFactory.getLog(HolderDefineCheck.class);
	private ItemDao itemDao;
	/** このチェックロジックのキャプション。AppFrameworkにて注入される。 */
	@Resource
	private String text;
	@Resource
	private String logFormat;
	@Resource
	private String logFormatInc;
	@Resource
	private String fileNotFoundMsg;
	@Resource
	private String saxErrorMsg;

	public HolderDefineCheck() {
		outFile = getOutFile(CHECK_LOG);
		Application.getInstance().getContext().getResourceMap(
			AbstractCheckLogic.class).injectFields(this);
	}

	@Override
	public String toString() {
		return text;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void execute(String path) throws IOException, InterruptedException {
		if (isSelected) {
			Formatter out = null;
			try {
				out = new Formatter(outFile);
				FileLister lister = new FileLister();
				Collection<File> files = lister.listFiles(getRoot(path), FILTER);
				for (File file : files) {
					checkFile(file, out);
				}
			} finally {
				if (null != out) {
					out.close();
				}
			}
		}
	}

	private void checkFile(File file, Formatter out) throws IOException {
		if (file.exists()) {
			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(file));
				Map<String, PageDefine> map = PageDefineUtil.parse(in);
				checkMap(map, out, file);
			} catch (SAXException e) {
				log.error(saxErrorMsg, e);
			} catch (FileNotFoundException e) {
				log.error(fileNotFoundMsg + file.getAbsolutePath(), e);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

	private void checkMap(Map<String, PageDefine> map, Formatter out, File file)
			throws IOException {
		for (Iterator<Map.Entry<String, PageDefine>> it =
			map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, PageDefine> entry = it.next();
			Set<HolderString> set = entry.getValue().getDataHolders();
			ArrayList<BadHolder> badHolders =
				new ArrayList<BadHolder>(set.size());
			for (HolderString hs : set) {
				Item item = itemDao.getItem(hs);
				if (null == item) {
					badHolders.add(new BadHolder(file, hs));
				}
			}
			writeBadholder(badHolders, out);
		}
	}

	private void writeBadholder(ArrayList<BadHolder> badHolders, Formatter out)
			throws IOException {
		for (BadHolder badHolder : badHolders) {
			LineNumberReader in = null;
			try {
				// ホルダがページ定義XMLに存在するか
				boolean isFind = false;
				in = new LineNumberReader(new FileReader(badHolder.file));
				for (String line = in.readLine(); null != line; line =
					in.readLine()) {
					if (isFindHolder(badHolder, line)) {
						write(out, badHolder, in.getLineNumber(), false);
						isFind = true;
					}
				}
				// ページ定義XMLに無かったので、実体参照によるincファイル内にあるはず。
				if (!isFind) {
					write(out, badHolder, INCLUDE_DEFINE_LINE, true);
				}
			} finally {
				if (null != in) {
					in.close();
				}
			}
		}
	}

	private boolean isFindHolder(BadHolder badHolder, String line) {
		return line.indexOf(badHolder.holderString.getHolder()) > 0;
	}

	private void write(
			Formatter out,
			BadHolder badHolder,
			int lineno,
			boolean notInc) {
		out.format(
			getFormat(notInc),
			badHolder.file.getAbsolutePath(),
			lineno,
			badHolder.holderString.getProvider(),
			badHolder.holderString.getHolder());
	}

	private String getFormat(boolean notInc) {
		return notInc ? logFormatInc : logFormat;
	}

	private static class BadHolder {
		private final File file;
		private final HolderString holderString;

		public BadHolder(File file, HolderString holderString) {
			this.file = file;
			this.holderString = holderString;
		}
	}
}
