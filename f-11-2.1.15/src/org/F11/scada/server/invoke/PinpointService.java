/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.invoke;

import java.util.Iterator;
import java.util.List;

import org.F11.scada.server.dao.CareerDao;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

public class PinpointService implements InvokeHandler {
	private Logger logger = Logger.getLogger(PinpointService.class);
	private CareerDao dao;

	public PinpointService() {
		S2Container container = (S2Container) S2ContainerUtil.getS2Container();
		dao = (CareerDao) container.getComponent(CareerDao.class);
	}

	public Object invoke(Object[] args) {
		List<HolderString> holders = (List<HolderString>) args[0];
		String limit = (String) args[1];
		return dao.getPinpointCareer(getHolders(holders), limit);
	}

	private String getHolders(List<HolderString> holders) {
		StringBuilder b = new StringBuilder(holders.size() * 2);
		for (Iterator<HolderString> i = holders.iterator(); i.hasNext();) {
			HolderString hs = i.next();
			String provider = "'" + hs.getProvider() + "'";
			String holder = "'" + hs.getHolder() + "'";
			if (i.hasNext()) {
				b.append("(i.provider="
					+ provider
					+ " AND i.holder="
					+ holder
					+ ") OR ");
			} else {
				b.append("(i.provider="
					+ provider
					+ " AND i.holder="
					+ holder
					+ ")");
			}
		}
		return b.toString();
	}
}
