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
package org.F11.scada.server.autoprint.perser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.F11.scada.server.autoprint.AutoPrintTask;
import org.F11.scada.server.autoprint.CsvExecAutoPrintTask;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * 自動ページ印刷定義をパースするクラスです。
 * 自動印刷タスクを保持します。
 * 
 * @author hori
 */
public class AutoPrintDefine {
	/** 自動ページの印刷のタスクリスト */
	private List printTasks;

	/**
	 * 自動ページ印刷定義XMLをパースしてタスクのリストを生成します。
	 * @param file 自動ページ印刷定義XMLファイル名
	 */
	public AutoPrintDefine(String file) throws IOException, SAXException {
		printTasks = new ArrayList();

		Digester digester = new Digester();
		digester.push(this);
		digester.setNamespaceAware(true);

		addCsvExecRule(digester);

		URL xml = getClass().getResource(file);
		if (xml == null) {
			throw new IllegalStateException(file + " not found.");
		}

		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
			is.close();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 自動印刷XMLのパージングルールを定義します
	 * @param digester Digesterオブジェクト
	 */
	private void addCsvExecRule(Digester digester) {
		String pattern = "autoprint/csv_exec";
		digester.addObjectCreate(pattern, CsvExecAutoPrintTask.class);
		digester.addSetProperties(pattern);
		digester.addSetNext(pattern, "addAutoPrintTask");

		pattern = "autoprint/csv_exec/head/line";
		digester.addSetProperties(pattern, "text", "head");

		pattern = "autoprint/csv_exec/column";
		digester.addObjectCreate(pattern, ColumnBeans.class);
		digester.addSetProperties(pattern);
		digester.addSetNext(pattern, "addColumnBeans");

		pattern = "autoprint/csv_exec/execute";
		digester.addSetProperties(pattern, "command", "execute");

		pattern = "autoprint/csv_exec/execute/param";
		digester.addSetProperties(pattern, "value", "execute");
	}

	/**
	 * 最新情報と警報・状態一覧の設定を設定します
	 * @param config 最新情報と警報・状態一覧の設定
	 */
	public void addAutoPrintTask(AutoPrintTask task) {
		printTasks.add(task);
	}

	/**
	 * 自動定義印刷のタスクリストを返します。
	 * このリストは変更できないコピーを返します。
	 * @return 自動定義印刷のタスクリストを返します。
	 */
	public List getAutoPrintTasks() {
		return Collections.unmodifiableList(printTasks);
	}

}
