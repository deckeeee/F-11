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

import java.io.IOException;

import org.F11.scada.cat.logic.ExecuteTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;

public class WriteFlagCheck extends AbstractCheckLogic {
	private final Log log = LogFactory.getLog(WriteFlagCheck.class);

	@Resource
	private String text;

	public WriteFlagCheck() {
		super();
		Application.getInstance().getContext().getResourceMap(
				AbstractCheckLogic.class).injectFields(this);
	}

	@Override
	public String toString() {
		return text;
	}
	
	public void execute(String path, ExecuteTask task) throws IOException {
		if (isSelected) {
			log.info("path = " + path + " execute : " + isSelected);
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
