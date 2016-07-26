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

package org.F11.scada.server.remove;

import org.F11.scada.Service;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;
import org.seasar.framework.util.ResourceNotFoundRuntimeException;

public class Remove implements Service {
	private static Logger logger = Logger.getLogger(Remove.class);
	private RemoveService[] service;

	public Remove() {
		this("resources/RemoveDefine.dicon");
	}
	
	public Remove(String path) {
		try {
			S2Container container = S2ContainerFactory.create(path);
			service = getRemoveServices(container);
		} catch (ResourceNotFoundRuntimeException e) {
			logger.warn(path + " が見つからないので削除機能は起動しません。");
		}
	}

	private RemoveService[] getRemoveServices(S2Container container) {
		Object[] services = container.findComponents(RemoveService.class);
		RemoveService[] removeServices = new RemoveService[services.length];
		for (int i = 0; i < services.length; i++) {
			RemoveService removeService = (RemoveService) services[i];
			removeServices[i] = removeService;
		}
		return removeServices;
	}

	public void start() {
		if (null != service) {
			for (int i = 0; i < service.length; i++) {
				service[i].start();
			}
			logger.info("削除機能　スケジューリング開始。");
		}
	}
	
	public void stop() {
		if (null != service) {
			for (int i = 0; i < service.length; i++) {
				service[i].stop();
			}
			logger.info("削除機能　スケジューリング停止。");
		}
	}
	
	public String toString() {
		return "レコード削除サービス " + getClass().getName();
	}
}
