/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/test/util/Attic/TestUtil.java,v 1.1.2.11 2007/10/25 07:34:07 frdm Exp $
 * $Revision: 1.1.2.11 $
 * $Date: 2007/10/25 07:34:07 $
 * 
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

package org.F11.scada.test.util;


import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.applet.DataProviderProxy;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeDataCalendar;
import org.F11.scada.data.WifeDataDaySchedule;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeDataTimestamp;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * テスト用のユーティリティークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class TestUtil {

	private TestUtil() {}

	/**
	 * テスト用のデータプロバイダを生成します。
	 * @return テスト用のデータプロバイダ
	 */
	public static DataProvider createDataProvider() throws DataProviderDoesNotSupportException {
		// 仮のデータプロバイダと…
		DataProvider dp = createDP();

		// 仮のデータホルダーを生成。
		for (int i = 500; i <= 620; i++) {
			DataHolder dh = new DataHolder();
			dh.setValueClass(WifeData.class);
			dh.setValue(WifeDataAnalog.valueOfBcdSingle(0), new java.util.Date(), WifeQualityFlag.INITIAL);
			dh.setDataHolderName("D_" + i + "_BcdSingle");
			ConvertValue conv = ConvertValue.valueOfANALOG(0, 100, 0, 4000, "000");
			dh.setParameter("convert", conv);
			dp.addDataHolder(dh);
		}
		return dp;
	}


	/**
	 * テスト用のデータプロバイダを生成します。
	 * @return テスト用のデータプロバイダ
	 */
	public static DataProvider createDataProvider2() throws DataProviderDoesNotSupportException {
		// 仮のデータプロバイダと…
		DataProvider dp = createDP();

		// 仮のデータホルダーを生成。
		for (int i = 500; i <= 505; i++) {
			DataHolder dh = new DataHolder();
			dh.setValueClass(WifeData.class);
			dh.setValue(WifeDataAnalog.valueOfBcdSingle(0), new java.util.Date(), WifeQualityFlag.INITIAL);
			dh.setDataHolderName("D_" + i + "_BcdSingle");
			ConvertValue conv = ConvertValue.valueOfANALOG(0, 100, 0, 4000, "000");
			dh.setParameter("convert", conv);
			dp.addDataHolder(dh);
		}

		long addr = 19000;
		int bitNo = 0;
		DecimalFormat format = new DecimalFormat("00");
		for (int i = 0; i < 16; i++) {
			if (0 < (bitNo / 16)) {
				addr += (bitNo / 16);
			}
			bitNo = bitNo % 16;

			WifeDataDigital dataDigital = WifeDataDigital.valueOfFalse(bitNo);
			WifeCommand define = new WifeCommand(dp.getDataProviderName(),
												 0,
												 0,
												 0,
												 addr, 1);

			DataHolder dh = new DataHolder();
			dh.setValueClass(WifeData.class);
			dh.setValue(dataDigital, new Date(), WifeQualityFlag.INITIAL);
			String name = "D" + "_" + String.valueOf(addr) + format.format(bitNo) + "_Digital";
			dh.setDataHolderName(name);
			dh.setParameter("command", define);
			bitNo++;
			dp.addDataHolder(dh);
		}
		return dp;
	}
	
	public static DataProvider createDigitalDataProvider() throws DataProviderDoesNotSupportException {
		DataProvider dp = createDP();
		
		// 仮のデータホルダーを生成。
		for (int i = 1900000, j = 0; i < 1900016; i++, j++) {
			DataHolder dh = new DataHolder();
			dh.setValueClass(WifeData.class);
			dh.setValue(
				WifeDataDigital.valueOfFalse(j),
				new java.util.Date(),
				WifeQualityFlag.INITIAL);
			dh.setDataHolderName("D_" + i + "_Digital");
			dp.addDataHolder(dh);
		}
		return dp;
	}
	
	public static DataProvider createDP() {
		// 仮のデータプロバイダと…
		DataProvider dp = new TestDataProvider();
		dp.setDataProviderName("P1");
		
		return dp;
	}

	private static class TestDataProvider extends AbstractDataProvider implements WifeDataProvider, DataProviderProxy {
		private static final long serialVersionUID = 3797591758942543877L;
		public Class[][] getProvidableDataHolderTypeInfo() {
			Class[][] c = {
				{DataHolder.class,WifeData.class}
			};
			return c;
		}
		public void syncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		    System.out.println("write data : " + dh);
		}
		public void asyncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
			System.out.println("write data : " + dh);
		}
		public void asyncRead(DataHolder dh) throws DataProviderDoesNotSupportException {
		}
		public void asyncRead(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		public void asyncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		public void syncRead(DataHolder dh) throws DataProviderDoesNotSupportException {
		}
		public void syncRead(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		public void syncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
		}
		public List getHoldersData(long t, Session session) {
			return Collections.EMPTY_LIST;
		}
		public void lock() {
		}
		public void setSendRequestSupport(SendRequestSupport sendRequestSupport) {
		}
		public void unlock() {
		}
		public void start() {
		}
		public void stop() {
		}
		public void run() {
		}
		public void setValueChangeNewestTime(long l) {
		}
		public void syncRead() {
		}
	}

	public static void crearJIM() {
	    Manager manager = Manager.getInstance();
        DataProvider[] dps = manager.getDataProviders();
        for (int i = 0; i < dps.length; i++) {
            DataHolder[] dhs = dps[i].getDataHolders();
            for (int j = 0; j < dhs.length; j++) {
                try {
                    dps[i].removeDataHolder(dhs[j]);
                } catch (DataProviderDoesNotSupportException e) {
                    e.printStackTrace();
                }
            }
            manager.removeDataProvider(dps[i]);
        }
	}
	
    /**
     * @return
     * @throws DataProviderDoesNotSupportException
     */
    public static DataHolder createDigitalHolder(String name) {
        return createDigitalHolder(name, true);
    }

    public static DataHolder createDigitalHolder(String name, boolean b) {
        DataHolder dh = new DataHolder();
        dh.setValueClass(WifeData.class);
        dh.setDataHolderName(name);
        dh.setValue(createDigitalData(b), new Date(), WifeQualityFlag.INITIAL);
        return dh;
    }

	private static WifeDataDigital createDigitalData(boolean b) {
		return b ? WifeDataDigital.valueOfTrue(0) : WifeDataDigital.valueOfFalse(0);
	}

    public static DataHolder createAnalogHolder(String name) {
        DataHolder dh = new DataHolder();
        dh.setValueClass(WifeData.class);
        dh.setDataHolderName(name);
        dh.setValue(WifeDataAnalog.valueOfBcdSingle(0), new Date(), WifeQualityFlag.INITIAL);
        ConvertValue v = ConvertValue.valueOfANALOG(0, 100, 0, 100, "");
        dh.setParameter(WifeDataProvider.PARA_NAME_CONVERT, v);
        return dh;
    }
    
    public static DataHolder createAnalog4Holder(String name) {
        DataHolder dh = new DataHolder();
        dh.setValueClass(WifeData.class);
        dh.setDataHolderName(name);
        dh.setValue(WifeDataAnalog4.valueOfBcdSingle(new double[]{0, 0, 100, 100}), new Date(), WifeQualityFlag.INITIAL);
        ConvertValue v = ConvertValue.valueOfANALOG(0, 4000, 0, 4000, "");
        dh.setParameter(WifeDataProvider.PARA_NAME_CONVERT, v);
        return dh;
    }

    public static DataHolder createScheduleHolder(String name) {
    	return createScheduleHolder(name, "Group1");
    }

    public static DataHolder createScheduleHolder(String name, String group) {
        DataHolder dh = new DataHolder();
        dh.setValueClass(WifeData.class);
        dh.setDataHolderName(name);
        dh.setValue(WifeDataSchedule.valueOf(1, 4, group), new Date(), WifeQualityFlag.INITIAL);
        return dh;
    }
    
    public static DataHolder createDayScheduleHolder(String name) {
        DataHolder dh = new DataHolder();
        dh.setValueClass(WifeData.class);
        dh.setDataHolderName(name);
        dh.setValue(WifeDataDaySchedule.valueOf(4), new Date(), WifeQualityFlag.INITIAL);
        return dh;
    }
    
    public static DataHolder createCalendarHolder(String name) {
        DataHolder dh = new DataHolder();
        dh.setValueClass(WifeData.class);
        dh.setDataHolderName(name);
        dh.setValue(WifeDataCalendar.valueOf(6), new Date(), WifeQualityFlag.INITIAL);
        return dh;
    }
    
    public static DataHolder createTimestampHolder(String name) {
        DataHolder dh = new DataHolder();
        dh.setValueClass(WifeData.class);
        dh.setDataHolderName(name);
        Timestamp t = TimestampUtil.parse("2005/07/01 13:00:00");
        dh.setValue(WifeDataTimestamp.valueOfType1(t.getTime()), new Date(), WifeQualityFlag.INITIAL);
        return dh;
    }
    
    public static void sleep(long millis) {
        System.out.println("Wait by " + millis + " milliseconds.");
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
	        public void run() {
	            System.out.print(".");
	        } 
        };
        timer.scheduleAtFixedRate(task, new Date(), 1000);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {}
        timer.cancel();
        System.out.println("");
    }
}
