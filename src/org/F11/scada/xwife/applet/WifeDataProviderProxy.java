/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/applet/WifeDataProviderProxy.java,v 1.15.2.20 2007/10/18 09:48:43 frdm Exp $
 * $Revision: 1.15.2.20 $
 * $Date: 2007/10/18 09:48:43 $
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

package org.F11.scada.xwife.applet;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.DataAccessableFactory;
import org.F11.scada.applet.DataProviderProxy;
import org.F11.scada.applet.DefaultDataAccessableFactory;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.data.BCDConvertException;
import org.F11.scada.data.DataAccessable;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.security.auth.Subject;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

/**
 * クライアントで動作する、代理データプロバイダです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataProviderProxy extends AbstractDataProvider implements
		DataProviderProxy {
	private static final long serialVersionUID = -3128157966069899808L;
	private static final Class[][] TYPE_INFO = { { DataHolder.class,
			WifeData.class } };
	/* スレッド */
	private Thread thread;
	private DataAccessable alarmRef;
	private final Logger logger = Logger.getLogger(WifeDataProviderProxy.class);
	private final long cycleTime;
	private AtomicLong valueChangeNewestTime = new AtomicLong(0);
	/** 前回エラー値 */
	private boolean oldError;
	/** サーバーエラー例外 */
	private Exception serverError;
	/** セッション */
	private final Session session;
	private final DataAccessableFactory factory;
	private final InetAddress ipAddress;
	private final Authenticationable authenticationable;
	private final ReentrantLock lock = new ReentrantLock();

	public WifeDataProviderProxy(
			Session session,
			Authenticationable authenticationable) throws RemoteException {
		this(session, new DefaultDataAccessableFactory(), authenticationable);
	}

	public WifeDataProviderProxy(
			Session session,
			DataAccessableFactory factory,
			Authenticationable authenticationable) throws RemoteException {
		super();
		this.session = session;
		this.factory = factory;
		this.authenticationable = authenticationable;
		try {
			this.ipAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e.getMessage());
		}
		ClientConfiguration config = new ClientConfiguration();
		cycleTime = config.getLong("xwife.applet.Applet.proxy.cycleTime", 1000);

		String collectorServer = WifeUtilities.createRmiManagerDelegator();
		for (int i = 1; i <= Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e1) {
				logger.info(collectorServer + " retry rmi lookup. (" + i + "/"
						+ Globals.RMI_CONNECTION_RETRY_COUNT + ")");
				serverError = e1;
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				continue;
			}
		}

		if (serverError != null) {
			logger.error("通信エラー:", serverError);
			throw ServerErrorUtil.createException(serverError);
		}
	}

	private void lookup()
			throws MalformedURLException,
			RemoteException,
			NotBoundException {
		alarmRef = factory.getDataAccessable();
	}

	public Class[][] getProvidableDataHolderTypeInfo() {
		return TYPE_INFO;
	}

	public void run() {
		Thread thisThread = Thread.currentThread();
		while (thread == thisThread) {
			syncRead();
			setError();
			ThreadUtil.sleep(cycleTime);
		}
	}

	private void setError() {
		// 通信エラー時に、全てのホルダをBADにする。
		DataHolder errdh = getDataHolder(Globals.ERR_HOLDER);
		if (isCommunicationError(errdh)) {
			if (!oldError) {
				setQualityFlag(WifeQualityFlag.BAD, errdh);
			}
			oldError = true;
		} else {
			if (oldError) {
				setQualityFlag(WifeQualityFlag.GOOD, errdh);
			}
			oldError = false;
		}
	}

	private boolean isCommunicationError(DataHolder errdh) {
		return errdh != null
				&& WifeDataDigital.valueOfTrue(0).equals(errdh.getValue());
	}

	private void setQualityFlag(WifeQualityFlag flag, DataHolder errdh) {
		for (Iterator i = dataHolders.values().iterator(); i.hasNext();) {
			DataHolder dh = (DataHolder) i.next();
			if (errdh != dh) {
				dh.setValue(dh.getValue(), dh.getTimeStamp(), flag);
			}
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName() + "-" + getDataProviderName());
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

	public void setValueChangeNewestTime(long l) {
		long old = valueChangeNewestTime.get();
		do {
			if (old == l) {
				break;
			}
		} while (!valueChangeNewestTime.compareAndSet(old, l));
	}

	public void syncRead() {
		lock.lock();
		try {
			if (getDataHolderCount() > 0) {
				for (int i = 1;; i++) {
					if (i == Globals.RMI_METHOD_RETRY_COUNT) {
						if (serverError != null) {
							ServerErrorUtil.invokeServerError();
							logger.fatal("Exception caught: ", serverError);
						}
					}

					try {
						List holderDatas = null;
						if (valueChangeNewestTime.get() == 0) {
							holderDatas = alarmRef
									.getHoldersData(getDataProviderName());
						} else {
							holderDatas = alarmRef.getHoldersData(
									getDataProviderName(),
									valueChangeNewestTime.get(),
									session);
						}
						setHolderData(holderDatas);
						if (serverError != null
								&& i >= Globals.RMI_METHOD_RETRY_COUNT) {
							try {
								ServerErrorUtil.invokeServerRepair();
								logger.error("Exception caught: ", serverError);
							} catch (Exception e) {
								logger.fatal("Exception caught: ", e);
							}
						}
						serverError = null;
						break;
					} catch (RemoteException e) {
						logger.info("Get HoldersDatas retry rmi lookup. (" + i
								+ ")");
						serverError = e;
						try {
							lookup();
						} catch (Exception e1) {
							serverError = e1;
						}
						ThreadUtil
								.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
						continue;
					} catch (Exception e) {
						logger.info("HoldersDatas retry rmi lookup. (" + i
								+ ")");
						serverError = e;
						try {
							lookup();
						} catch (Exception e1) {
							serverError = e1;
						}
						ThreadUtil
								.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
						continue;
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	private void setHolderData(List holderDatas) {
		DataHolder errdh = getDataHolder(Globals.ERR_HOLDER);
		for (int i = 0; i < holderDatas.size(); i++) {
			HolderData hd = (HolderData) holderDatas.get(i);
			DataHolder dh = getDataHolder(hd.getHolder());
			if (dh != null) {
				WifeData srcWd = (WifeData) dh.getValue();
				Date entryDate = new Date(hd.getTime());
				Map demandData = (Map) hd.getDemandData();
				if (demandData != null) {
					dh
							.setParameter(
									DemandDataReferencer.GRAPH_DATA,
									demandData);
				}
				try {
					WifeData wd = srcWd.valueOf(hd.getValue());

					if (errdh != null
							&& dh != errdh
							&& WifeDataDigital.valueOfTrue(0).equals(
									errdh.getValue())) {
						dh.setValue(wd, entryDate, WifeQualityFlag.BAD);
					} else {
						if (!wd.equals(srcWd)
								|| dh.getQualityFlag() != WifeQualityFlag.GOOD) {
							dh.setValue(wd, entryDate, WifeQualityFlag.GOOD);

							// TODO 警報抜け調査
							if (logger.isDebugEnabled()) {
								if (wd instanceof WifeDataDigital) {
									WifeDataDigital wdd = (WifeDataDigital) wd;
									FastDateFormat f = FastDateFormat
											.getInstance("yyyy/MM/dd HH:mm:ss");
									logger.debug("Holder="
											+ dh.getDataHolderName() + " Time="
											+ f.format(dh.getTimeStamp())
											+ " Data=" + wdd.toString());
								}
							}

							if (valueChangeNewestTime.get() < entryDate
									.getTime()) {
								setValueChangeNewestTime(entryDate.getTime());
							}
						}
					}
				} catch (BCDConvertException e) {
					// if (logger.isDebugEnabled()) {
					// logger.debug("BCDConvertException :" + dh);
					// }
					dh.setValue(dh.getValue(), entryDate, WifeQualityFlag.BAD);
				}
			}
			// else {
			// if (logger.isDebugEnabled()) {
			// logger.debug("DataHolder is null :" + hd.getHolder());
			// }
			// }
		}
	}

	public void syncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		String provider = dh.getDataProvider().getDataProviderName();
		String holder = dh.getDataHolderName();
		WifeData srcWd = (WifeData) dh.getValue();
		try {
			alarmRef.setValue(provider, holder, srcWd, authenticationable
					.getSubject().getUserName(), ipAddress.getHostAddress());
		} catch (RemoteException e) {
			logger.error("データ書込みエラー:", e);
		}
	}

	/**
	 * サブジェクトを返します。
	 * 
	 * @return サブジェクトを返します。
	 */
	public Subject getSubject() {
		return authenticationable.getSubject();
	}

	// Non used methods
	public void asyncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void lock() {
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}
}
