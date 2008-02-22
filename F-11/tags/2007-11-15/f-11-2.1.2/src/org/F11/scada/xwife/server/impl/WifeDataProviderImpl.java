/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/server/impl/Attic/WifeDataProviderImpl.java,v 1.1.2.34 2007/10/19 10:07:00 frdm Exp $
 * $Revision: 1.1.2.34 $
 * $Date: 2007/10/19 10:07:00 $
 * 
 * =============================================================================
 * Projrct    F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All
 * Rights Reserved.
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

package org.F11.scada.xwife.server.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JComponent;

import jp.gr.javacons.jim.AbstractDataProvider;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.data.BCDConvertException;
import org.F11.scada.data.HolderData;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.util.SingletonSortedMap;
import org.F11.scada.util.TimeIncrementWrapper;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

/**
 * �f�[�^�v���o�C�_�N���X�ł��B�ʐM��PLC����f�[�^���擾���āA�f�[�^�z���_�[�ɒl��ݒ肵�Ă����܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WifeDataProviderImpl extends AbstractDataProvider implements
		WifeDataProvider {

	private static final long serialVersionUID = -4052507689962832517L;
	private static Logger logger = Logger.getLogger(WifeDataProviderImpl.class);
	private static final Class[][] TYPE_INFO = { { DataHolder.class,
			WifeData.class } };

	/* �X���b�h */
	private Thread thread;

	/** �ʐM���W���[���ł��B */
	private final Communicater communicater;
	/** �ʐM��`����f�[�^�z���_�[�ւ̃}�b�v�ł��B */
	private final Map def2holder = new ConcurrentHashMap();

	private final SortedMap holderJurnal;
	private final ItemDao itemDao;
	private final HolderRegisterBuilder builder;
	private SendRequestSupport sendRequestSupport;
	private final long communicateWaitTime;
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * �R���X�g���N�^
	 */
	public WifeDataProviderImpl(
			Environment plc,
			ItemDao itemDao,
			HolderRegisterBuilder builder,
			AlarmReferencer alarm,
			AlarmReferencer demand,
			CommunicaterFactory communicaterFactory) throws Exception {
		this(32, plc, itemDao, builder, alarm, demand, communicaterFactory);
	}

	public WifeDataProviderImpl(
			int holderSize,
			Environment plc,
			ItemDao itemDao,
			HolderRegisterBuilder builder,
			AlarmReferencer alarm,
			AlarmReferencer demand,
			CommunicaterFactory communicaterFactory) throws Exception {
		super(holderSize);
		setDataProviderName(plc.getDeviceID());
		this.itemDao = itemDao;
		this.builder = builder;
		holderJurnal = Collections
				.synchronizedSortedMap(new SingletonSortedMap());

		Manager.getInstance().addDataProvider(this);
		try {
			communicater = communicaterFactory.createCommunicator(plc);
		} catch (ClassNotFoundException ex) {
			throw new NoClassDefFoundError(ex.getMessage());
		}

		setParameter(PARA_NAME_ALARM, alarm);
		setParameter(PARA_NAME_DEMAND, demand);
		createHolders();
		long wait = Long.parseLong(EnvironmentManager.get(
				"/server/communicateWaitTime",
				"500"));
		communicateWaitTime = Math.max(500, wait);
	}

	/**
	 * �f�[�^�z���_�����̃v���o�C�_�ɒǉ����܂��B�p�t�H�[�}���X���ێ�����ׁA�����Ĕr�����������Ă��܂���B�X���b�h���N��������Ƀz���_�̒ǉ���폜������ꍇ�́A���Ȃ炸{@link #lock()}���\�b�h���Ăяo���āA�X���b�h�̃��b�N���擾���Ă��������B�ǉ���폜������������͂�{@link #unlock()}���\�b�h���Ăяo���ă��b�N���O���Ă��������B
	 * 
	 * @param dh �ǉ�����f�[�^�z���_
	 * 
	 * @return �f�[�^�z���_�̒ǉ����ł����ꍇ true �������łȂ��ꍇ false ��Ԃ��܂��B
	 */
	public boolean addDataHolder(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		if (isCycleRead(dh)) {
			WifeCommand dh_define = (WifeCommand) dh
					.getParameter(PARA_NAME_COMAND);

			if (def2holder.containsKey(dh_define)) {
				Map dhmap = (Map) def2holder.get(dh_define);
				if (!dhmap.containsKey(dh.getDataHolderName())) {
					dhmap.put(dh.getDataHolderName(), dh);
				}
			} else {
				LinkedHashMap dhmap = new LinkedHashMap();
				dhmap.put(dh.getDataHolderName(), dh);
				def2holder.put(dh_define, dhmap);
			}

			List defines = new ArrayList();
			defines.add(dh_define);
			communicater.addReadCommand(defines);
		}
		return super.addDataHolder(dh);
	}

	/**
	 * �f�[�^�z���_�����̃v���o�C�_����폜���܂��B�p�t�H�[�}���X���ێ�����ׁA�����Ĕr�����������Ă��܂���B�X���b�h���N��������Ƀz���_�̒ǉ���폜������ꍇ�́A���Ȃ炸{@link #lock()}���\�b�h���Ăяo���āA�X���b�h�̃��b�N���擾���Ă��������B�ǉ���폜������������͂�{@link #unlock()}���\�b�h���Ăяo���ă��b�N���O���Ă��������B
	 * 
	 * @param dh �폜����f�[�^�z���_
	 * 
	 * @return �f�[�^�z���_�̍폜���ł����ꍇ true �������łȂ��ꍇ false ��Ԃ��܂��B
	 */
	public boolean removeDataHolder(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		if (isCycleRead(dh)) {
			WifeCommand dh_define = (WifeCommand) dh
					.getParameter(PARA_NAME_COMAND);
			if (def2holder.containsKey(dh_define)) {
				Map dhmap = (Map) def2holder.get(dh_define);
				if (dhmap.containsKey(dh.getDataHolderName())) {
					dhmap.remove(dh.getDataHolderName());
				}
				if (dhmap.isEmpty()) {
					def2holder.remove(dh_define);
				}
			}
			List defines = new ArrayList();
			defines.add(dh_define);
			communicater.removeReadCommand(defines);
		}

		return super.removeDataHolder(dh);
	}

	public void syncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		LinkedHashSet list = new LinkedHashSet();
		list.add(dh.getParameter(PARA_NAME_COMAND));
		syncRead(list, false);
	}

	/**
	 * �y�[�W�؂�ւ��ĕ\���̎��ɌĂяo�����B
	 * 
	 * @param dhs �ēǂݍ��݂���f�[�^�z���_
	 */
	public void syncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		LinkedHashSet defines = new LinkedHashSet(dhs.length);
		for (int i = 0; i < dhs.length; i++) {
			DataHolder dh = dhs[i];
			if (isCycleRead(dh)) {
				defines.add(dh.getParameter(PARA_NAME_COMAND));
			}
		}
		syncInitRead(defines, false);
	}

	/**
	 * �y�[�W�؂�ւ��̏����ǂݏo���ł̂݌Ăяo���\�ł��B���̃��\�b�h���Ăяo���O��lock�������Ă��Ȃ���΂Ȃ�܂���B
	 * 
	 * @param defines
	 * @param sameDataBalk
	 */
	private void syncInitRead(Set defines, boolean sameDataBalk) {
		DataHolder errdh = getDataHolder(Globals.ERR_HOLDER);
		long entryDate = System.currentTimeMillis();
		try {
			Map bytedataMap = communicater.syncRead(defines, sameDataBalk);
			for (Iterator it = bytedataMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				WifeCommand wc = (WifeCommand) entry.getKey();
				byte[] data = (byte[]) entry.getValue();
				setByteData(wc, data);
			}
			if (sameDataBalk
					&& isNetError(errdh, WifeDataDigital.valueOfTrue(0))) {
				setErrorHolder(errdh, entryDate, WifeDataDigital
						.valueOfFalse(0));
			}
		} catch (InterruptedException e) {
		} catch (Exception e) {
			if (sameDataBalk
					&& isNetError(errdh, WifeDataDigital.valueOfFalse(0))) {
				setErrorHolder(errdh, entryDate, WifeDataDigital.valueOfTrue(0));
			}
			logger.warn("�ʐM�G���[", e);
		}
	}

	private void syncRead(Set defines, boolean sameDataBalk) {
		DataHolder errdh = getDataHolder(Globals.ERR_HOLDER);
		long entryDate = System.currentTimeMillis();
		// �����ł� interrupt �������Ȃ�����
		lock.lock();
		try {
			Map bytedataMap = communicater.syncRead(defines, sameDataBalk);
			for (Iterator it = bytedataMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				WifeCommand wc = (WifeCommand) entry.getKey();
				byte[] data = (byte[]) entry.getValue();
				setByteData(wc, data);
			}
			if (sameDataBalk
					&& isNetError(errdh, WifeDataDigital.valueOfTrue(0))) {
				setErrorHolder(errdh, entryDate, WifeDataDigital
						.valueOfFalse(0));
			}
		} catch (InterruptedException e) {
			return;
		} catch (Exception e) {
			if (sameDataBalk
					&& isNetError(errdh, WifeDataDigital.valueOfFalse(0))) {
				setErrorHolder(errdh, entryDate, WifeDataDigital.valueOfTrue(0));
			}
			logger.warn("�ʐM�G���[", e);
		} finally {
			lock.unlock();
		}
	}

	public void syncRead(Set defines) {
		syncRead(defines, true);
	}

	private void setErrorHolder(DataHolder errdh, long entryDate, WifeData value) {
		errdh.setValue(value, new Date(entryDate), WifeQualityFlag.GOOD);
		synchronized (holderJurnal) {
			TimeIncrementWrapper.put(entryDate, new HolderData(
					Globals.ERR_HOLDER,
					value.toByteArray(),
					entryDate,
					null), holderJurnal);
		}
	}

	private boolean isNetError(DataHolder errdh, WifeDataDigital digital) {
		return errdh != null && digital.equals(errdh.getValue());
	}

	/**
	 * PLC�ɔ񓯊�����
	 */
	public void syncWrite(DataHolder dh) {
		Map defdata = new HashMap();
		WifeCommand def_h = (WifeCommand) dh.getParameter(PARA_NAME_COMAND);
		WifeData wd = (WifeData) dh.getValue();
		defdata.put(def_h, wd.toByteArray());

		DataHolder errdh = getDataHolder(Globals.ERR_HOLDER);
		long entryDate = System.currentTimeMillis();
		try {
			communicater.syncWrite(defdata);
			if (isNetError(errdh, WifeDataDigital.valueOfTrue(0))) {
				setErrorHolder(errdh, entryDate, WifeDataDigital
						.valueOfFalse(0));
			}
		} catch (Exception e) {
			if (isNetError(errdh, WifeDataDigital.valueOfFalse(0))) {
				setErrorHolder(errdh, entryDate, WifeDataDigital.valueOfTrue(0));
			}
			logger.warn("�����ݒʐM�G���[:", e);
		}

		// ���̃N���C�A���g�֒ʒm����
		long entryTime = dh.getTimeStamp().getTime();
		synchronized (holderJurnal) {
			TimeIncrementWrapper
					.put(
							entryTime,
							new HolderData(
									dh.getDataHolderName(),
									wd.toByteArray(),
									entryTime,
									(Map) dh
											.getParameter(DemandDataReferencer.GRAPH_DATA)),
							holderJurnal);
		}
	}

	/**
	 * �X�^�[�g
	 */
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getName() + "-" + getDataProviderName());
			thread.start();
		}
	}

	/**
	 * �X�g�b�v
	 */
	public void stop() {
		if (thread != null) {
			Thread th = thread;
			thread = null;
			th.interrupt();
		}
	}

	/**
	 * �������ŒʐM�v�����L���[�ɓo�^���܂��B
	 */
	public void run() {
		Thread thisThread = Thread.currentThread();

		while (thread == thisThread) {
			syncRead(getCommandDefines());
			try {
				Thread.sleep(communicateWaitTime);
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	private Set getCommandDefines() {
		LinkedHashSet reqdhs = new LinkedHashSet(dataHolders.values().size());
		for (Iterator i = dataHolders.values().iterator(); i.hasNext();) {
			DataHolder dh = (DataHolder) i.next();
			if (isCycleRead(dh)) {
				reqdhs.add(dh.getParameter(PARA_NAME_COMAND));
			}
		}
		return reqdhs;
	}

	/**
	 * �ʐM���W���[������̒ʐM�����C�x���g���������܂��B
	 */
	private void setByteData(WifeCommand define, byte[] readData) {
		// ��M�f�[�^��DataHolder�ɐݒ�
		if (!def2holder.containsKey(define)) {
			return;
		}
		Map dhs = (Map) def2holder.get(define);
		for (Iterator it = dhs.values().iterator(); it.hasNext();) {
			long entryTime = System.currentTimeMillis();
			DataHolder dh = (DataHolder) it.next();
			WifeData wdata = (WifeData) dh.getValue();
			WifeQualityFlag flag = (WifeQualityFlag) dh.getQualityFlag();
			WifeData cutwdata = null;
			// System.out.println("setValue : " + dh.getDataHolderName());
			try {
				cutwdata = wdata.valueOf(readData);
				if (WifeQualityFlag.INITIAL == flag || !wdata.equals(cutwdata)) {
					if (WifeQualityFlag.INITIAL == flag) {
						dh.setValue(
								cutwdata,
								new Date(entryTime),
								WifeQualityFlag.GOOD,
								true);
					} else {
						dh.setValue(
								cutwdata,
								new Date(entryTime),
								WifeQualityFlag.GOOD);
					}
					synchronized (holderJurnal) {
						setJurnal(readData, entryTime, dh);
					}
					// TODO �x�񔲂�����
					if (logger.isDebugEnabled()) {
						if (cutwdata instanceof WifeDataDigital) {
							WifeDataDigital wdd = (WifeDataDigital) cutwdata;
							FastDateFormat f = FastDateFormat
									.getInstance("yyyy/MM/dd HH:mm:ss");
							logger.debug("Holder=" + dh.getDataHolderName()
									+ " Time=" + f.format(dh.getTimeStamp())
									+ " Data=" + wdd.toString());
						}
					}
				}
			} catch (BCDConvertException e) {
				if (dh.getQualityFlag() != WifeQualityFlag.BAD) {
					dh
							.setValue(
									wdata,
									new Date(entryTime),
									WifeQualityFlag.BAD);
					synchronized (holderJurnal) {
						setJurnal(readData, entryTime, dh);
					}
					logger.error("setValue BAD (BCD Convert error) "
							+ dh.getDataHolderName()
							+ dh.getTimeStamp().toString(), e);
				}
			}
		}
	}

	private void setJurnal(byte[] readData, long entryTime, DataHolder dh) {
		TimeIncrementWrapper.put(entryTime, new HolderData(dh
				.getDataHolderName(), readData, entryTime, (Map) dh
				.getParameter(DemandDataReferencer.GRAPH_DATA)), holderJurnal);
	}

	public Class[][] getProvidableDataHolderTypeInfo() {
		return TYPE_INFO;
	}

	public JComponent getDataParameterEditor(DataHolder dh) {
		throw new java.lang.UnsupportedOperationException();
	}

	private void createHolders() {
		Item[] items = itemDao.getSystemItems(getDataProviderName(), true);
		builder.register(items);
	}

	private boolean isCycleRead(DataHolder dh) {
		Boolean cr = (Boolean) dh.getParameter(PARA_NAME_CYCLEREAD);
		return cr.booleanValue();
	}

	/**
	 * ������long�l(�X�V���t��long�l)�����HolderData��Ԃ��܂��B
	 * 
	 * @param t
	 * @return HolderData[]
	 */
	public List getHoldersData(long t, Session session) {
		if (sendRequestSupport == null) {
			throw new IllegalStateException("sendRequestSupport noting.");
		}
		List list = Collections.EMPTY_LIST;
		synchronized (holderJurnal) {
			SortedMap smap = holderJurnal.tailMap(new Long(t + 1));
			list = new ArrayList(smap.values());
		}
		sendRequestSupport.setSendRequestDateMap(session, System
				.currentTimeMillis());
		return list;
	}

	public String toString() {
		return "dataProvider=" + getDataProviderName();
	}

	public void setSendRequestSupport(SendRequestSupport sendRequestSupport) {
		this.sendRequestSupport = sendRequestSupport;
	}

	// Not used Methods
	public void asyncRead(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncRead(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void asyncWrite(DataHolder dh)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void syncWrite(DataHolder[] dhs)
			throws DataProviderDoesNotSupportException {
		throw new DataProviderDoesNotSupportException();
	}

	public void lock() {
		thread.interrupt();
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}
}