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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.F11.scada.cat.dao.PolicyDefineDao;
import org.F11.scada.cat.entity.PolicyDefineTable;
import org.F11.scada.cat.logic.ExecuteTask;
import org.F11.scada.cat.util.ExtFileFilter;
import org.F11.scada.server.deploy.FileLister;
import org.F11.scada.server.deploy.PageDefineUtil;
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
 * 書き込みフォルダがポリシー定義されているかチェックするロジック
 * 
 * @author maekawa
 * 
 */
public class WriteFlagCheck extends AbstractCheckLogic {
	private static final ExtFileFilter FILTER = new ExtFileFilter(".xml");
	private final Log log = LogFactory.getLog(WriteFlagCheck.class);
	private PolicyDefineDao dao;
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

	public WriteFlagCheck() {
		Application.getInstance().getContext().getResourceMap(
			AbstractCheckLogic.class).injectFields(this);
		outFile = getOutFile(checkLog);
		writer = new ErrorHolderWriter(logFormat, logFormatInc);
	}

	public void setDao(PolicyDefineDao dao) {
		this.dao = dao;
	}

	@Override
	public String toString() {
		return text;
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
					PageDefineUtil.parse(in, new WriteHolderRuleSet());
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
		for (Map.Entry<String, PageDefine> entry : map.entrySet()) {
			Set<HolderString> set = entry.getValue().getDataHolders();
			List<ErrorHolder> badHolders = list(set.size());
			for (HolderString hs : set) {
				List<PolicyDefineTable> list = dao.getPolicyDefines(getDto(hs));
				if (isBad(list)) {
					badHolders.add(new ErrorHolder(file, hs));
				}
			}
			writer.writeBadholder(badHolders, out);
		}
	}

	private boolean isBad(List<PolicyDefineTable> list) {
		if (list.isEmpty()) {
			return true;
		}
		for (PolicyDefineTable policyDefineTable : list) {
			if (policyDefineTable.getPermission().indexOf("write") < 0) {
				return true;
			}
		}
		return false;
	}

	private PolicyDefineTable getDto(HolderString hs) {
		PolicyDefineTable policy = new PolicyDefineTable();
		policy.setName(hs.getProvider() + "_" + hs.getHolder());
		return policy;
	}

	/**
	 * 書き込みフォルダを設定するタグを抽出するルールセット
	 * 
	 */
	private static class WriteHolderRuleSet extends RuleSetBase {
		private static final String HOLDER_STRING =
			"org.F11.scada.server.register.HolderString";
		private static final String[] ATTRIBUTE_STRINGS =
			{ "provider", "holder" };

		public void addRuleInstances(Digester digester) {
			digester.addObjectCreate("*/fixeddigital", HOLDER_STRING);
			digester.addSetProperties(
				"*/fixeddigital",
				ATTRIBUTE_STRINGS,
				ATTRIBUTE_STRINGS);
			digester.addSetRoot("*/fixeddigital", "add");

			digester.addObjectCreate("*/fixedanalog", HOLDER_STRING);
			digester.addSetProperties(
				"*/fixedanalog",
				ATTRIBUTE_STRINGS,
				ATTRIBUTE_STRINGS);
			digester.addSetRoot("*/fixedanalog", "add");

			digester.addObjectCreate("*/variableanalog", HOLDER_STRING);
			digester.addSetProperties(
				"*/variableanalog",
				ATTRIBUTE_STRINGS,
				ATTRIBUTE_STRINGS);
			digester.addSetRoot("*/variableanalog", "add");

			digester.addObjectCreate("*/variableanalog4", HOLDER_STRING);
			digester.addSetProperties(
				"*/variableanalog4",
				ATTRIBUTE_STRINGS,
				ATTRIBUTE_STRINGS);
			digester.addSetRoot("*/variableanalog4", "add");

			digester.addObjectCreate("*/destination/schedule", HOLDER_STRING);
			digester.addSetProperties(
				"*/destination/schedule",
				ATTRIBUTE_STRINGS,
				ATTRIBUTE_STRINGS);
			digester.addSetRoot("*/destination/schedule", "add");

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
		}
	}
}
