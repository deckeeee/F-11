/*
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

package org.F11.scada.server.logging;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractLoggingHandler extends DefaultHandler implements
		LoggingContentHandler {
	private final Logger log = Logger.getLogger(AbstractLoggingHandler.class);
	protected Stack stack;
	protected Map taskMap = new HashMap();

	public Map getTaskMap() {
		return taskMap;
	}

	public void putTaskMap(String name, LoggingTask task) {
		taskMap.put(name, task);
	}

	public List getHolderStrings(String taskName) {
		if (taskMap.containsKey(taskName)) {
			LoggingTask task = (LoggingTask) taskMap.get(taskName);
			return task.getDataHolders();
		} else {
			log.warn(taskName + "Ç™ë∂ç›ÇµÇ‹ÇπÇÒÅB");
		}
		return Collections.EMPTY_LIST;
	}

	public String getFactoryName(String taskName) throws RemoteException {
		if (taskMap.containsKey(taskName)) {
			LoggingTask task = (LoggingTask) taskMap.get(taskName);
			return task.getFactoryName();
		} else {
			log.warn(taskName + "Ç™ë∂ç›ÇµÇ‹ÇπÇÒÅB");
		}
		return null;
	}
}
