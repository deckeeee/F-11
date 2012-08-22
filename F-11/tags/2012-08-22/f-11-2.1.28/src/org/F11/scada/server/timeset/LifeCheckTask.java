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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.apache.log4j.Logger;

/**
 * @author maekawa
 */
public class LifeCheckTask extends TimerTask {
	private static Logger logger;
	private List writeHolders;

	/**
	 * 
	 */
	public LifeCheckTask() {
		super();
		logger = Logger.getLogger(getClass().getName());
		writeHolders = new ArrayList();
	}

	public void addWriteProvidertHolder(String provaideHolderName) {
		writeHolders.add(provaideHolderName);
	}

	public void run() {
		writeTime();
	}

	private void writeTime() {
		for (Iterator it = writeHolders.iterator(); it.hasNext();) {
			String holder = (String) it.next();
			int p = holder.indexOf('_');
			DataHolder dh = Manager.getInstance().findDataHolder(
					holder.substring(0, p), holder.substring(p + 1));
			if (dh != null) {
				try {
					WifeDataDigital digital = (WifeDataDigital) dh.getValue();
					dh.setValue(digital.valueOf(true), new Date(), WifeQualityFlag.GOOD);
					dh.syncWrite();
				} catch (DataProviderDoesNotSupportException e) {
					e.printStackTrace();
				} catch (ClassCastException e) {
					logger.error("ライフチェックにはデジタルタイプを指定してください ("
							+ dh.getDataHolderName() + ")", e);
				}
			}
		}
	}
}
