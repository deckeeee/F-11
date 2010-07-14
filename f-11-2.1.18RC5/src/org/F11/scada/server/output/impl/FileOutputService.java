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
package org.F11.scada.server.output.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.postgresql.SyncReadWrapper;
import org.F11.scada.server.output.OutputService;
import org.F11.scada.server.output.dto.FileOutputDesc;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.WifeDataUtil;
import org.F11.scada.server.register.impl.RegisterUtil;
import org.F11.scada.util.ThreadUtil;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class FileOutputService implements OutputService {
	private int errorRetryCount = 10;
	private long errorRetryTime = 1000;
	private List services;
	private final Map itemPool = new ConcurrentHashMap();
	private ItemUtil itemUtil;

	private static Logger log = Logger.getLogger(FileOutputService.class);

	public FileOutputService() {
		log.info("construct FileOutputService");
	}

	public void setErrorRetryCount(int errorRetryCount) {
		this.errorRetryCount = errorRetryCount;
	}

	public void setErrorRetryTime(long errorRetryTime) {
		this.errorRetryTime = errorRetryTime;
	}

	public void setItemUtil(ItemUtil itemUtil) {
		this.itemUtil = itemUtil;
	}

	public void addFileOutputDesc(FileOutputDesc desc) {
		if (services == null) {
			services = new ArrayList();
		}
		if (desc == null) {
			throw new IllegalArgumentException();
		}
		services.add(desc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.server.output.OutputService#write()
	 */
	public void write() {
		for (Iterator i = services.iterator(); i.hasNext();) {
			FileOutputDesc desc = (FileOutputDesc) i.next();
			write(desc);
		}
	}

	private void write(FileOutputDesc desc) {
		if (log.isDebugEnabled())
			log.debug("start write data. " + desc);

		try {
			String provider = desc.getProvider();
			List holders = desc.getHolders();
			Communicater communicater = desc.getCommunicater();
			List commands = createCommand(provider, holders);
			communicater.addReadCommand(commands);
			SyncReadWrapper wrapper = new SyncReadWrapper();
			Map bytedataMap = wrapper.syncRead(communicater, commands);
			List writeDatas = createWriteData(provider, holders, bytedataMap);

			write(writeDatas, desc.getFilePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List createWriteData(String provider, List holders, Map bytedataMap) {
		ArrayList writeDatas = new ArrayList(holders.size());
		for (Iterator i = holders.iterator(); i.hasNext();) {
			String holder = (String) i.next();
			HolderString hs = new HolderString();
			hs.setProvider(provider);
			hs.setHolder(holder);
			Item item = itemUtil.getItem(hs, itemPool);
			WifeCommand wc = new WifeCommand(item);
			byte[] data = (byte[]) bytedataMap.get(wc);
			WifeData wd = WifeDataUtil.getWifeData(item);
			wd = wd.valueOf(data);
			if (wd instanceof WifeDataAnalog) {
				WifeDataAnalog wda = (WifeDataAnalog) wd;
				ConvertValue convertValue = RegisterUtil.getConvertValue(item);
				String value = convertValue.convertStringValue(wda
						.doubleValue());
				writeDatas.add(value);
			} else {
				log.warn("not WifeDataAnalog. value class = "
						+ wd.getClass().getName());
			}
		}
		return writeDatas;
	}

	private List createCommand(String provider, List holders) {
		ArrayList commands = new ArrayList();
		for (Iterator i = holders.iterator(); i.hasNext();) {
			String holder = (String) i.next();
			HolderString hs = new HolderString();
			hs.setProvider(provider);
			hs.setHolder(holder);
			Item item = itemUtil.getItem(hs, itemPool);
			WifeCommand wc = new WifeCommand(item);
			commands.add(wc);
		}
		return commands;
	}

	private void write(List writeDatas, String filePath) {
		PrintWriter out = null;
		for (int i = 1; i <= errorRetryCount; i++) {
			try {
				out = new PrintWriter(new BufferedWriter(new FileWriter(
						filePath)));
				for (Iterator it = writeDatas.iterator(); it.hasNext();) {
					String value = (String) it.next();
					out.print(value);
					if (it.hasNext()) {
						out.print(",");
					}
				}
				break;
			} catch (IOException e) {
				log.error("書き込みエラーが発生しました。" + errorRetryTime
						+ "ミリ秒後にリトライします。 (" + i + "/" + errorRetryCount + ")");
				ThreadUtil.sleep(errorRetryTime);
				continue;
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}
}
