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

package org.F11.scada.server.io.nio.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.data.BCDConvertException;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.communicater.EnvironmentMap;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.nio.LogDtoCreator;
import org.F11.scada.server.io.nio.dto.LogDto;
import org.F11.scada.server.io.postgresql.SyncReadWrapper;
import org.F11.scada.server.register.WifeDataUtil;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class LogDtoCreatorImpl implements LogDtoCreator {
	private final Logger logger = Logger.getLogger(LogDtoCreatorImpl.class);
	private final Map itemArrayPool;
	private final ItemUtil itemUtil;
	private final CommunicaterFactory communicaterFactory;

	public LogDtoCreatorImpl(
			ItemUtil itemUtil,
			CommunicaterFactory communicaterFactory) {
		itemArrayPool = new ConcurrentHashMap();
		this.itemUtil = itemUtil;
		this.communicaterFactory = communicaterFactory;
	}

	public List getLogDtoList(LoggingDataEvent event) {
		Item[] items = itemUtil.getItems(event.getHolders(), itemArrayPool);
		Map itemMap = itemUtil.getItemMap(items);
		ArrayList dtos = new ArrayList(items.length);
		Timestamp writedate = event.getTimeStamp();

		try {
			for (Iterator it = itemMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				String provider = (String) entry.getKey();
				List itemList = (List) entry.getValue();

				List commands = getCommandList(items, itemList);
				Map bytedataMap = getByteDataMap(provider, commands);

				for (Iterator itemIt = itemList.iterator(), commandIt = commands
						.iterator(); itemIt.hasNext() && commandIt.hasNext();) {
					Item item = (Item) itemIt.next();
					WifeCommand wc = (WifeCommand) commandIt.next();
					byte[] data = (byte[]) bytedataMap.get(wc);
					dtos.add(createLogDto(item, data, writedate));
				}
			}
		} catch (Exception e) {
			logger.error("ロギング通信エラー", e);
		}
		return dtos;
	}

	private Map getByteDataMap(String provider, List commands) throws Exception {
		Environment environment = EnvironmentMap.get(provider);
		Communicater communicater = communicaterFactory
				.createCommunicator(environment);
		communicater.addReadCommand(commands);
		SyncReadWrapper wrapper = new SyncReadWrapper(provider);
		Map bytedataMap = wrapper.syncRead(communicater, commands);
		return bytedataMap;
	}

	private Object[] createLogDto(Item item, byte[] data, Timestamp writedate) {
		LogDto dto = new LogDto();
		dto.setHolderid(item.getProvider() + "_" + item.getHolder());
		dto.setRevision(getRevison());
		dto.setValue(getValue(item, data));
		dto.setWritedate(writedate);
		return dto.toObjectArray();
	}

	// TODO リビジョンNo取得を実装
	private int getRevison() {
		return 0;
	}

	private double getValue(Item item, byte[] data) {
		WifeData wd = WifeDataUtil.getWifeData(item);
		try {
			wd = wd.valueOf(data);
			if (wd instanceof WifeDataAnalog) {
				WifeDataAnalog wda = (WifeDataAnalog) wd;
				return wda.doubleValue();
			} else if (wd instanceof WifeDataDigital) {
				WifeDataDigital wdd = (WifeDataDigital) wd;
				if (wdd.isOnOff(true)) {
					return 1;
				} else {
					return 0;
				}
			} else {
				throw new IllegalArgumentException(
						"value is not WifeDataDigital and WifeDataAnalog! : "
								+ wd.getClass().getName());
			}
		} catch (BCDConvertException e) {
			logger.error("BCD変換エラー発生、初期値をログに書き込みます", e);
			if (wd instanceof WifeDataAnalog || wd instanceof WifeDataDigital) {
				return 0;
			} else {
				throw new IllegalArgumentException(
						"value is not WifeDataDigital and WifeDataAnalog! : "
								+ wd.getClass().getName());
			}
		}
	}

	private List getCommandList(Item[] items, List itemList) {
		ArrayList commands = new ArrayList(items.length);
		for (Iterator it2 = itemList.iterator(); it2.hasNext();) {
			Item item = (Item) it2.next();
			WifeCommand wc = new WifeCommand(item);
			commands.add(wc);
		}
		return commands;
	}

}
