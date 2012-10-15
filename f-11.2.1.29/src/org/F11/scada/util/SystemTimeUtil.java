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

package org.F11.scada.util;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Globals;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataTimestamp;
import org.F11.scada.data.WifeQualityFlag;
import org.apache.log4j.Logger;

public class SystemTimeUtil {
	private final Logger logger = Logger.getLogger(SystemTimeUtil.class);

	public void setSystemTime(String dataHolderID) {
		DataHolder dataHolder =
			Manager.getInstance().findDataHolder(dataHolderID);
		setSystemTime(dataHolder);
	}

	public void setSystemTime(DataHolder dataHolder) {
		writePcTime(dataHolder);
	}

	private void writePcTime(DataHolder readDataHolder) {
		DataHolder errdh =
			readDataHolder.getDataProvider().getDataHolder(Globals.ERR_HOLDER);
		if (errdh == null
			|| WifeDataDigital.valueOfTrue(0).equals(errdh.getValue())) {
			logger.error("通信エラー発生中：時計書込を中断します。");
			return;
		}
		logger.info("write pc time " + readDataHolder);
		Object obj = readDataHolder.getValue();
		if (obj instanceof WifeDataTimestamp) {
			if (isNotError(readDataHolder)) {
				WifeDataTimestamp ts =
					(WifeDataTimestamp) readDataHolder.getValue();
				Date date = ts.calendarValue().getTime();
				setSystemTime(date);
			}
		} else {
			logger.error("指定されたデータホルダ("
				+ readDataHolder.getDataHolderName()
				+ ")はWifeDataTimestampではありません : "
				+ obj.getClass().getName());
		}
	}

	private boolean isNotError(DataHolder readDataHolder) {
		DataHolder errdh =
			readDataHolder.getDataProvider().getDataHolder(Globals.ERR_HOLDER);
		return readDataHolder.getQualityFlag() == WifeQualityFlag.GOOD
			&& errdh != null
			&& WifeDataDigital.valueOfFalse(0).equals(errdh.getValue());
	}

	public void setSystemTime(Date date) {
		String os = System.getProperty("os.name");
		if (os.startsWith("Windows")) {
			executeWindows(date);
		} else {
			executeOtherOs(date);
		}
	}

	private void executeOtherOs(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmyyyy.ss");
		String[] param = new String[2];
		param[0] = "date";
		param[1] = format.format(date);
		try {
			Process pro = Runtime.getRuntime().exec(param);
			pro.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void executeWindows(Date date) {
		try {
			setWindowsDate(date);
			setWindowsTime(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setWindowsDate(Date date) throws IOException,
			InterruptedException {
		String[] param =
			getParam(date, true, new SimpleDateFormat("yyyy/MM/dd"));
		Process pro = Runtime.getRuntime().exec(param);
		pro.waitFor();
	}

	private void setWindowsTime(Date date) throws IOException,
			InterruptedException {
		String[] param =
			getParam(date, false, new SimpleDateFormat("HH:mm:ss"));
		Process pro = Runtime.getRuntime().exec(param);
		pro.waitFor();
	}

	private String[] getParam(Date date, boolean isDate, Format format) {
		String[] param = new String[4];
		param[0] = "CMD.exe";
		param[1] = "/C";
		if (isDate) {
			param[2] = "DATE";
			logger.debug("set date : " + format.format(date));
		} else {
			param[2] = "TIME";
			logger.debug("set time : " + format.format(date));
		}
		param[3] = format.format(date);
		return param;
	}

	public void setPlcTime(String dataHolderID) {
		DataHolder dh = Manager.getInstance().findDataHolder(dataHolderID);
		if (null == dh) {
			logger.error("指定されたデータホルダは登録されていません : " + dataHolderID);
		} else {
			setPlcTime(dh, new Date());
		}
	}

	public void setPlcTime(DataHolder dh, Date date) {
		setPlcTime(dh, date.getTime());
	}

	private void setPlcTime(DataHolder dh, long time) {
		if (null == dh) {
			logger.error("指定されたデータホルダは登録されていません");
		} else {
			dh.setValue(
				WifeDataTimestamp.valueOfType1(time),
				new Date(),
				WifeQualityFlag.GOOD);
			try {
				dh.syncWrite();
			} catch (DataProviderDoesNotSupportException e) {
				e.printStackTrace();
			}
		}
	}
}
