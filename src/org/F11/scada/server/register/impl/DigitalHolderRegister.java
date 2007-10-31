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

package org.F11.scada.server.register.impl;

import java.util.Map;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.WifeData;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.HolderRegister;
import org.F11.scada.server.register.WifeDataUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * デジタルデータのHolderRegisterクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DigitalHolderRegister implements HolderRegister {
	private final Logger logger = Logger.getLogger(DigitalHolderRegister.class);
	private final Map referencerMap = new ConcurrentHashMap();

	public void register(Item item) {
		DataReferencer rf = new DataReferencer(item.getProvider(), item
				.getHolder());
		referencerMap.put(getKey(item), rf);
		WifeData wd = WifeDataUtil.getWifeData(item);
		DataHolder dh = RegisterUtil.getDataHolder(item, wd);
		RegisterUtil.addDataHolder(dh, item);
		DataProvider dp = dh.getDataProvider();
		AlarmReferencer ar = (AlarmReferencer) dp
				.getParameter(WifeDataProvider.PARA_NAME_ALARM);
		ar.addReferencer(rf);
	}

	private String getKey(Item item) {
		return item.getProvider() + "_" + item.getHolder();
	}

	public void unregister(Item item) {
		DataReferencer rf = (DataReferencer) referencerMap.remove(getKey(item));
		DataProvider dp = Manager.getInstance().getDataProvider(
				item.getProvider());
		AlarmReferencer ar = (AlarmReferencer) dp
				.getParameter(WifeDataProvider.PARA_NAME_ALARM);
		if (null != rf) {
			ar.removeReferencer(rf);
		} else {
			logger.error("DataReferencer is null = " + getKey(item));
		}
		RegisterUtil.removeDataHolder(item);
	}
}
