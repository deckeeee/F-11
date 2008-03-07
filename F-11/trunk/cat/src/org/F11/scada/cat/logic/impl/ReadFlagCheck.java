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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.F11.scada.cat.logic.ExecuteTask;
import org.F11.scada.cat.util.ExtFileFilter;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.deploy.FileLister;
import org.F11.scada.server.deploy.PageDefineUtil;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.frame.PageDefine;
import org.F11.scada.server.register.HolderString;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.xml.sax.SAXException;

/**
 * 読み込みフラグチェックロジック
 * 
 * @author maekawa
 *
 */
public class ReadFlagCheck extends AbstractCheckLogic {
	private static final ExtFileFilter FILTER = new ExtFileFilter(".xml");
	private final Log log = LogFactory.getLog(ReadFlagCheck.class);
	private ItemDao itemDao;
	/** エラーホルダを出力するヘルパークラス */
	private final ErrorHolderWriter writer;
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
	@Resource
	private String checkLog;

	public ReadFlagCheck() {
		Application.getInstance().getContext().getResourceMap(
			AbstractCheckLogic.class).injectFields(this);
		outFile = getOutFile(checkLog);
		writer = new ErrorHolderWriter(logFormat, logFormatInc);
	}

	@Override
	public String toString() {
		return text;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void execute(String path, ExecuteTask task) throws IOException {
		if (isSelected) {
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
					checkFile(file, out);
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

	private void checkFile(File file, Formatter out) throws IOException {
		if (file.exists()) {
			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(file));
				Map<String, PageDefine> map =
					PageDefineUtil.parse(in, new ReadHolderRuleSet());
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
			ArrayList<ErrorHolder> badHolders =
				new ArrayList<ErrorHolder>(set.size());
			for (HolderString hs : set) {
				Item item = itemDao.getItem(hs);
				if (isReadMode(item)) {
					badHolders.add(new ErrorHolder(file, hs));
				}
			}
			writer.writeBadholder(badHolders, out);
		}
	}

	private boolean isReadMode(Item item) {
		return null != item && !item.isComCycleMode();
	}

	private static class ReadHolderRuleSet extends RuleSetBase {
		private static final String HOLDER_STRING =
			"org.F11.scada.server.register.HolderString";
		private static final String[] ATTRIBUTE_STRINGS =
			{ "provider", "holder" };
		private static final String[] DRAWLINE_ATTRIBUTE =
			{ "start_x", "start_y" };
		private static final String[] LINETO_ATTRIBUTE = { "x", "y" };

		public void addRuleInstances(Digester digester) {
			digester.addSetProperties("*/if", "value", "value");

			digester.addSetProperties("*/textanalogsymbol", "value", "value");

			digester.addSetProperties(
				"*/textpowerfactorsymboleditable",
				"value",
				"value");

			digester.addSetProperties(
				"*/textschedulesymboleditable",
				"value",
				"value");

			digester.addSetProperties(
				"*/textanalog4symboleditable",
				"value",
				"value");

			digester.addSetProperties(
				"*/textpowerfactor4symboleditable",
				"value",
				"value");

			digester.addSetProperties(
				"*/textanalogsymboleditable",
				"value",
				"value");

			digester.addSetProperties("*/group", "value", "value");

			digester.addObjectCreate("*/group", HOLDER_STRING);
			digester.addSetProperties(
				"*/group",
				ATTRIBUTE_STRINGS,
				ATTRIBUTE_STRINGS);
			digester.addSetRoot("*/group", "add");

			digester.addObjectCreate("*/calendar", HOLDER_STRING);
			digester.addSetProperties(
				"*/calendar",
				ATTRIBUTE_STRINGS,
				ATTRIBUTE_STRINGS);
			digester.addSetRoot("*/calendar", "add");

			digester.addSetProperties(
				"*/drawline",
				DRAWLINE_ATTRIBUTE,
				DRAWLINE_ATTRIBUTE);

			digester.addSetProperties(
				"*/lineto",
				LINETO_ATTRIBUTE,
				LINETO_ATTRIBUTE);

			digester.addSetProperties(
				"*/graphproperty/series/holder/name",
				"value",
				"value");

			digester.addSetProperties("*/stringdatasymbol", "value", "value");
		}
	}
}
