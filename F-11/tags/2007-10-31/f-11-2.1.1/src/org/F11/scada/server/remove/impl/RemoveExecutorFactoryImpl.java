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

package org.F11.scada.server.remove.impl;

import org.F11.scada.server.remove.RemoveExecutor;
import org.F11.scada.server.remove.RemoveExecutorFactory;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

public class RemoveExecutorFactoryImpl implements RemoveExecutorFactory {
	private static Logger logger = Logger.getLogger(RemoveExecutorFactoryImpl.class);
	private S2Container container;

	public void setContainer(S2Container container) {
		this.container = container;
	}

	public RemoveExecutor getRemoveExecutor(String table) {
		RemoveExecutor executor = null;
		logger.info(table);
		if ("alarm_email_sent_table".equalsIgnoreCase(table)) {
			if ("MySQL".equalsIgnoreCase(SQLUtil.getDatabaseProductName(container))) {
				executor = (RemoveExecutor) container.getComponent("alarmMysql");
			} else {
				executor = (RemoveExecutor) container.getComponent("alarmOther");
			}
		} else {
			executor = (RemoveExecutor) container.getComponent("standard");
		}
		return executor;
	}
}
