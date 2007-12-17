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
package org.F11.scada.server.wifi.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.server.wifi.LocationHistorysDao;
import org.F11.scada.server.wifi.WiFiTerminalMap;
import org.F11.scada.server.wifi.dto.LocationHistorys;
import org.F11.scada.server.wifi.dto.WiFiTerminal;
import org.F11.scada.util.SingletonSortedMap;
import org.F11.scada.util.TimeIncrementWrapper;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class LocationHistorysDataProvider extends AbstractDataProvider
        implements WifeDataProvider {

    private static final long serialVersionUID = 840154041944019851L;
	private static final Class[][] TYPE_INFO =
		{ { DataHolder.class, WifeData.class }
	};
    private static Logger logger = Logger.getLogger(LocationHistorysDataProvider.class);
    private static final String TIMESTAMP = "Timestamp";

	private Thread thread;
	private final SortedMap holderJurnal;
    private final long pollingTime;
    private final WiFiTerminalMap terminalMap;
    private final LocationHistorysDao dao;
    private final ItemDao itemDao;
	private final HolderRegisterBuilder builder;
	private SendRequestSupport sendRequestSupport;
    
    public LocationHistorysDataProvider(
            String providerName,
            long pollingTime,
            WiFiTerminalMap terminalMap,
            LocationHistorysDao dao,
            AlarmReferencer alarm,
            ItemDao itemDao,
            HolderRegisterBuilder builder) {

        setDataProviderName(providerName);
        this.pollingTime = pollingTime;
        this.terminalMap = terminalMap;
        this.dao = dao;
        this.itemDao = itemDao;
        this.builder = builder;
		holderJurnal =
			Collections.synchronizedSortedMap(new SingletonSortedMap());
        
        Manager.getInstance().addDataProvider(this);

        setParameter(PARA_NAME_ALARM, alarm);
        
        createHolders();
        
        logger.info(getClass().getName() + " start.");
    }
    

	private void createHolders() {
	    Item[] items = itemDao.getSystemItems(getDataProviderName(), true);
	    builder.register(items);
	}
    
    public List getHoldersData(long t, Session session) {
	    if (sendRequestSupport == null) {
	        throw new IllegalStateException("sendRequestSupport noting.");
	    }
		SortedMap smap = holderJurnal.tailMap(new Long(t + 1));
		List list = new ArrayList(smap.values());
		sendRequestSupport.setSendRequestDateMap(session, System.currentTimeMillis());
		return list;
    }

    public void setSendRequestSupport(SendRequestSupport sendRequestSupport) {
        this.sendRequestSupport = sendRequestSupport;
    }

    public void run() {
		Thread ct = Thread.currentThread();

		while (thread == ct) {
		    DataHolder[] holders = getDataHolders();
		    asyncRead(holders);
		    sleep(pollingTime);
		}
    }

    private void sleep(long pollingTime) {
	    try {
            Thread.sleep(pollingTime);
        } catch (InterruptedException e) {}
    }

    public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
    }

    public void stop() {
		if (thread != null) {
			Thread th = thread;
			thread = null;
			th.interrupt();
		}
    }
    
    public void asyncRead(DataHolder[] holders) {
        for (int i = 0; i < holders.length; i++) {
            asyncRead(holders[i]);
        }
    }
    
    public void asyncRead(DataHolder holder) {
        String providerName = holder.getDataProvider().getDataProviderName();
        String holderName = holder.getDataHolderName();

        logger.debug(providerName + "_" + holderName + " read.");
        
        if (terminalMap.containsKey(providerName, holderName)) {
            WiFiTerminal term = terminalMap.get(providerName, holderName);
            Timestamp time = getTimestamp(holder);
            List list = dao.findLocationHistorys(term.getId(), term.getFloorId(), time);

            if (logger.isDebugEnabled()) {
            	logger.debug(list);
            }

            if (!list.isEmpty()) {
                LocationHistorys historys = (LocationHistorys) list.get(0);
                holder.setParameter(TIMESTAMP, historys.getTimeStamp());
                setHolderData(holder, historys, term);
            }
        } else {
            logger.warn("Not found terminal define. : " + providerName + " " + holderName);
        }
    }

    private Timestamp getTimestamp(DataHolder holder) {
        Timestamp time = (Timestamp) holder.getParameter(TIMESTAMP);
        if (time == null) {
            time = new Timestamp(0);
        }
        return time;
    }
    
    private void setHolderData(DataHolder holder, LocationHistorys historys, WiFiTerminal term) {
        Rectangle rec = new Rectangle(term.getX(), term.getY(),
                term.getWidth(), term.getHeight());
        Point point = new Point(historys.getXPosition(), Math.abs(historys.getYPosition()));
        boolean isTrue = rec.contains(point);
        
        WifeData data = (WifeData) holder.getValue();
        if (data instanceof WifeDataDigital) {
            WifeDataDigital dd = (WifeDataDigital) data;
            if (!dd.isOnOff(isTrue)) {
    			setWifeData(holder, isTrue, dd);
            }
        }
    }

    /**
     * @param holder
     * @param isTrue
     * @param dd
     */
    private void setWifeData(DataHolder holder, boolean isTrue, WifeDataDigital dd) {
        long entryTime = System.currentTimeMillis();
        holder.setValue(dd.valueOf(isTrue), new Date(entryTime),
                WifeQualityFlag.GOOD);
		synchronized(holderJurnal) {
	        TimeIncrementWrapper.put(entryTime, new HolderData(holder
	                .getDataHolderName(), dd.valueOf(isTrue).toByteArray(),
	                entryTime, null), holderJurnal);
		}
        if (logger.isDebugEnabled()) {
	        logger.debug("setValue GOOD " + holder.getDataHolderName()
	                + holder.getTimeStamp().toString() + isTrue);
        }
    }


    public Class[][] getProvidableDataHolderTypeInfo() {
        return TYPE_INFO;
    }

    // Not use these methods.
	public void asyncWrite(DataHolder dh) throws DataProviderDoesNotSupportException {
	}


	public void asyncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
	}


	public void syncRead(DataHolder dh) throws DataProviderDoesNotSupportException {
	}


	public void syncRead(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
	}


	public void syncWrite(DataHolder dh) throws DataProviderDoesNotSupportException {
	}


	public void syncWrite(DataHolder[] dhs) throws DataProviderDoesNotSupportException {
	}


	public void lock() {
	}


	public void unlock() {
	}
}
