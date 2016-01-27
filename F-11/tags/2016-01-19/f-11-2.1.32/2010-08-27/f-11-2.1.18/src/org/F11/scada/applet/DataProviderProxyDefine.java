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

package org.F11.scada.applet;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Globals;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.CreateHolderData;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * 代理データプロバイダ起動クラスです。
 * リモート側のデータプロバイダを管理するクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DataProviderProxyDefine implements DataProviderProxyDefineable {
	/** RMIの再コネクト間隔(㍉秒) */
	private static final long SLEEP_TIME = 500L;
	/** Logging API */
	private static Logger logger;
	/** コネクションサーバーのリモート参照 */
	private DataAccessable dataAccessable;
	/** サーバーエラー */
	private Exception serverError;
	
	private final DataProviderProxyFactory factory;
	
	private final DataAccessableFactory accessableFactory;

	/**
	 * リモート側のデータプロバイダを管理するクラスです。
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws DataProviderDoesNotSupportException
	 */
	public DataProviderProxyDefine(Session session, Authenticationable authenticationable)
			throws RemoteException, DataProviderDoesNotSupportException {
	    this(session, new DefaultDataAccessableFactory(), new DefaultDataProviderProxyFactory(session, authenticationable));
	}

	/**
	 * リモート側のデータプロバイダを管理するクラスです。
	 * @throws MalformedURLException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws DataProviderDoesNotSupportException
	 */
	public DataProviderProxyDefine(Session session, DataAccessableFactory accessableFactory, DataProviderProxyFactory factory)
			throws RemoteException, DataProviderDoesNotSupportException {
		logger = Logger.getLogger(getClass().getName());
		this.factory = factory;
		this.accessableFactory = accessableFactory;

		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				continue;
			}
		}

		if (serverError != null) {
			logger.error("通信エラー:", serverError);
			throw ServerErrorUtil.createException(serverError);
		}
		createDataProviderProxy();
	}
	
	private void lookup() {
		dataAccessable = accessableFactory.getDataAccessable();
	}
	
	private void createDataProviderProxy()
			throws RemoteException, DataProviderDoesNotSupportException {
		List dpns = getDataProviders();
		logger.info("Provider Proxy : " + dpns);
		for (int i = 0; i < dpns.size(); i++) {
		    DataProviderDesc dpn = (DataProviderDesc) dpns.get(i);
		    createDataProvider(dpn);
		}
	}
	
	private void createDataProvider(DataProviderDesc desc) throws RemoteException, DataProviderDoesNotSupportException {
		Manager manager = Manager.getInstance();
        DataProvider dp = manager.getDataProvider(desc.getProvider());
        if (dp == null) {
		    DataProvider dpProxy = factory.createDataProvider(desc);
		    if (dpProxy != null) {
	            manager.addDataProvider(dpProxy);
	            createDataProviderHolders(dpProxy);
		    }
        }
	}
	
	private void createDataProviderHolders(DataProvider dp) throws DataProviderDoesNotSupportException {
	    registerHolders(getCreateHolderDatas(dp.getDataProviderName()));
	}
	
	private void registerHolders(List createHolderDatas) throws DataProviderDoesNotSupportException {
	    Manager manager = Manager.getInstance();
	    Set providerNames = new HashSet();
		for (Iterator i = createHolderDatas.iterator(); i.hasNext();) {
			CreateHolderData createHolderData = (CreateHolderData) i.next();
		    if (!hasDataHolder(manager, createHolderData)) {
				DataHolder dh = new DataHolder();
				dh.setValueClass(WifeData.class);
				dh.setDataHolderName(createHolderData.getHolder());
				WifeData wd = createHolderData.getWifeData();
				if (wd instanceof WifeDataAnalog || wd instanceof WifeDataAnalog4) {
					ConvertValue cv = createHolderData.getConvertValue();
					if (cv != null){ 
						dh.setParameter(WifeDataProvider.PARA_NAME_CONVERT, cv);
					} else {
						throw new IllegalArgumentException("ConvertValue not found.");
					}
					Map demand = createHolderData.getDemandData();
					if (demand != null && demand.size() != 0) {
						dh.setParameter(DemandDataReferencer.GRAPH_DATA, demand);
					}
				}
				dh.setValue(wd, createHolderData.getDate(), WifeQualityFlag.INITIAL);
				DataProvider dp = manager.getDataProvider(createHolderData.getProvider());
				dp.addDataHolder(dh);
				providerNames.add(dp.getDataProviderName());
		    }
		}
		restartProviders(providerNames);
	}
	
	private void restartProviders(Set providerNames) {
		for (Iterator i = providerNames.iterator(); i.hasNext();) {
			String provider = (String) i.next();
		    Manager manager = Manager.getInstance();
			DataProvider dp = manager.getDataProvider(provider);
			if (dp instanceof DataProviderProxy) {
				DataProviderProxy proxy = (DataProviderProxy) dp;
				proxy.setValueChangeNewestTime(0);
				proxy.syncRead();
			}
		}
	}

	private boolean hasDataHolder(Manager manager, CreateHolderData createHolderData) {
		DataProvider dp = manager.getDataProvider(createHolderData.getProvider());
		DataHolder dh = dp.getDataHolder(createHolderData.getHolder());
		return dh == null ? false : true;
	}
	
	private List getDataProviders() {
		if (serverError != null) {
			return Collections.EMPTY_LIST;
		}

		List dpn = null;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				dpn = dataAccessable.getDataProviders();
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				logger.error("サーバーコネクションエラー", serverError);
				ThreadUtil.sleep(SLEEP_TIME);
				continue;
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			logger.error("サーバーコネクションエラー", serverError);
		}

		return dpn;
	}
	
	private List getCreateHolderDatas(String provider) {
		if (serverError != null) {
			return Collections.EMPTY_LIST;
		}

		List createHolderDatas = Collections.EMPTY_LIST;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				createHolderDatas = dataAccessable.getCreateHolderDatas(provider);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				ThreadUtil.sleep(SLEEP_TIME);
				continue;
			}
		}

		if (serverError != null) {
			logger.error("サーバーコネクションエラー", serverError);
			ServerErrorUtil.invokeServerError();
		}
		
		return createHolderDatas;
	}
	
	public void addDataHolder(Set dataHolders) {
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
		    try {
	            List createHolderDatas =
	                dataAccessable.getCreateHolderDatas(dataHolders);
	            registerHolders(createHolderDatas);
				serverError = null;
				break;
	        } catch (DataProviderDoesNotSupportException e) {
	            logger.error("プログラムエラー" , e);
	        } catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				ThreadUtil.sleep(SLEEP_TIME);
				continue;
	        }
		}

		if (serverError != null) {
			logger.error("サーバーコネクションエラー", serverError);
			ServerErrorUtil.invokeServerError();
		}
	}
}
