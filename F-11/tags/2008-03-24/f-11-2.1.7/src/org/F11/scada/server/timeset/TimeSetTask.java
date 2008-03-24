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
package org.F11.scada.server.timeset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.util.SystemTimeUtil;
import org.apache.log4j.Logger;

/**
 * @author hori
 */
public class TimeSetTask extends TimerTask {
	private static Logger logger;
	private final String provider;
	private final String holder;
	private List writeHolders;

	/**
	 * 
	 */
	public TimeSetTask(String providerName, String holderName) {
		super();
		logger = Logger.getLogger(getClass().getName());

		this.provider = providerName;
		this.holder = holderName;
		writeHolders = new ArrayList();
	}

	public void addWriteProvidertHolder(String provaideHolderName) {
		writeHolders.add(provaideHolderName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		logger.debug("time set start! " + provider + "_" + holder);

		DataHolder readDataHolder = Manager.getInstance().findDataHolder(provider, holder);
		if (readDataHolder != null) {
			try {
				readDataHolder.syncRead();
				SystemTimeUtil util = new SystemTimeUtil();
				util.setSystemTime(readDataHolder);
			} catch (DataProviderDoesNotSupportException e) {
				e.printStackTrace();
			}
		}
		writeTime();
	}

	private void writeTime() {
		SystemTimeUtil util = new SystemTimeUtil();
		for (Iterator it = writeHolders.iterator(); it.hasNext();) {
			String dataHolderID = (String) it.next();
			logger.info("time write " + dataHolderID);
			util.setPlcTime(dataHolderID);
		}
	}
}
