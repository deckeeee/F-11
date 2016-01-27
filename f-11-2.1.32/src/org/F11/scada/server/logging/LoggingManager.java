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

package org.F11.scada.server.logging;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Map;

import org.F11.scada.EnvironmentManager;
import org.apache.log4j.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class LoggingManager {
    private static final String DEFAULT_DEFINE_FILE = "/resources/Logging.xml";
	private final ContentHandler handlder;
	private final String defineFile;
	
	private final Logger logger = Logger.getLogger(LoggingManager.class);

	public LoggingManager(ContentHandler handlder) throws SAXException, IOException {
	    this(handlder, DEFAULT_DEFINE_FILE);
	}

    public LoggingManager(ContentHandler handlder, String defineFile) throws SAXException, IOException {
	    this.handlder = handlder;
	    this.defineFile = defineFile;
	    init();
	}

	/**
	 * 初期処理。xml をパーサーにかけて、タスクのリストを作成します。作成したタスクをタイマーに登録します。
	 * @throws SAXException 
	 * @throws IOException 
	 */
	private void init() throws SAXException, IOException {
		InputStream stream = null;
		try {
			XMLReader parser =
				XMLReaderFactory.createXMLReader(
					EnvironmentManager.get("/org.xml.sax.driver", ""));
			parser.setContentHandler(handlder);
			stream =
				getClass().getResource(defineFile).openStream();
			InputSource is =
				new InputSource(stream);
			parser.parse(is);
			stream.close();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public Map getTaskMap() {
		try {
			return ((LoggingContentHandler) handlder).getTaskMap();
		} catch (RemoteException e) {
			logger.error("", e);
		}
		return Collections.EMPTY_MAP;
	}
}
