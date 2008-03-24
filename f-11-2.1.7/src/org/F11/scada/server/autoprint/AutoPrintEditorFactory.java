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

package org.F11.scada.server.autoprint;

import java.io.IOException;
import java.sql.SQLException;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.xwife.server.AutoPrintPanel;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * AutoPrintEditorのファクトリークラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AutoPrintEditorFactory {
	/** Logging API */
	private static Logger log = Logger.getLogger(AutoPrintEditorFactory.class);

	/**
	 * サーバー定義ファイルより、自動印刷のオブジェクトを返します
	 * @return 自動印刷のオブジェクトを返します
	 * @throws IOException
	 * @throws SQLException
	 * @throws SAXException
	 */
	public AutoPrintEditor getAutoPrintEditor()
			throws IOException, SAXException {

		AutoPrintEditor editor = null;
		String clazz =
			EnvironmentManager.get(
				"/server/autoprint",
				"org.F11.scada.xwife.server.AutoPrintPanel");
		try {
			Class cl = Class.forName(clazz);
			editor = (AutoPrintEditor) cl.newInstance();
		} catch (ClassNotFoundException e) {
			editor = new AutoPrintPanel();
		} catch (InstantiationException e) {
			editor = new AutoPrintPanel();
		} catch (IllegalAccessException e) {
			editor = new AutoPrintPanel();
		}
		if (log.isInfoEnabled()) {
			log.info(editor.getServerName());
		}
		return editor;
	}
}
