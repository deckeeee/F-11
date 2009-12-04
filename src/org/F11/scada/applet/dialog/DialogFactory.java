/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/dialog/DialogFactory.java,v 1.6.2.2 2007/07/12 09:41:33 frdm Exp $
 * $Revision: 1.6.2.2 $
 * $Date: 2007/07/12 09:41:33 $
 * 
 * =============================================================================
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

package org.F11.scada.applet.dialog;

import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.applet.parser.dialog.F11DialogHandler;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 定義ファイルよりダイアログを作成し管理します。
 */
public class DialogFactory {
	private static final Logger logger = Logger.getLogger(DialogFactory.class);
	private Map dialogs;

	private DialogFactory(Window window, PageChanger changer) {
		F11DialogHandler dd = new F11DialogHandler(window, changer);
		InputStream stream = null;
		try {
			XMLReader parser =
				XMLReaderFactory.createXMLReader(EnvironmentManager.get(
					"/org.xml.sax.driver",
					""));
			parser.setContentHandler(dd);
			stream =
				getClass().getResource("/resources/Dialog.xml").openStream();
			InputSource is = new InputSource(stream);
			parser.parse(is);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		dialogs = dd.getDialogs();
	}

	public Map getDialogs() {
		return dialogs;
	}

	public static WifeDialog get(Window window, String name, PageChanger changer) {
		DialogFactory df = new DialogFactory(window, changer);
		WifeDialog wifeDialog = (WifeDialog) df.getDialogs().get(name);
		return wifeDialog;
	}
}
