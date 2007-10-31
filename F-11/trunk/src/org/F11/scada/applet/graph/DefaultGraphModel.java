/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/DefaultGraphModel.java,v 1.20.2.12 2007/10/15 00:22:04 frdm Exp $
 * $Revision: 1.20.2.12 $
 * $Date: 2007/10/15 00:22:04 $
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

package org.F11.scada.applet.graph;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.io.ValueListHandler;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;


/**
 * �f�t�H���g�� GraphModel �����N���X�ł��B
 * ValueListHandler ���R���|�W�V�������āA�X�g���[�W�f�o�C�X���f�[�^���Q�Ƃ��܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultGraphModel extends AbstractGraphModel implements Runnable {
	/** �f�[�^�n���h���}�l�[�W���[�ł��B */
	private ValueListHandler valueListHandler;
	/** ���݂̃n���h�����ł��B */
	private String currentHandlerName;

	/** ��{�ɂȂ�\�[�g�ς݃}�b�v */
	private Map masterSortedMaps;
	/** �l�i�V���[�Y�f�[�^���X�g�j�̔����q */
	private Iterator valueIterator;
	/** �L�[�i�^�C���X�^���v�j�̔����q */
	private Iterator keyIterator;
	/** ���݂̒l */
	private Object currentObject;
	
	/** �X���b�h�I�u�W�F�N�g */
	private Thread thread;
	/** �����f�[�^�X�V���� */
	private static final long SLEEP_TIME = 30000L;
	private final int maxMapSize;

	private static Logger logger;
	/** �T�[�o�[�G���[��O�ł� */
	private Exception serverError;
	
	private final ValueListHandlerFactory factory;

	/**
	 * �R���X�g���N�^
	 * @param handlerName �f�[�^�n���h����
	 */
	public DefaultGraphModel(String[] handlerName)
			throws RemoteException {
	    this(handlerName, new DefaultValueListHandlerFactory());
	}

	/**
	 * �R���X�g���N�^
	 * @param handlerName �f�[�^�n���h����
	 * @param factory ValueListHandlerFactory�̎����N���X
	 */
	public DefaultGraphModel(String[] handlerName, ValueListHandlerFactory factory)
			throws RemoteException {
		super();
		this.masterSortedMaps = new ConcurrentHashMap();
		String maxRecord = EnvironmentManager.get("/server/logging/maxrecord", "4096");
		maxMapSize = Integer.parseInt(maxRecord);
		logger = Logger.getLogger(getClass().getName());
		this.factory = factory;

		logger.info("ValueListHandler:" + WifeUtilities.createRmiValueListHandler());

		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				try {
					Thread.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				} catch (InterruptedException e1) {}
				continue;
			}
		}
		
		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}

		createMasterSortedMap(handlerName[0]);

		start();
		
		logger.debug("DefaultGraphModel start.");
		logger.info("max map size : " + maxMapSize);
	}

	private void lookup() {
		valueListHandler = factory.getValueListHandler();
	}

	/**
	 * �Ώۃn���h���[�̃��R�[�h�I�u�W�F�N�g��Ԃ��܂��B
	 * @param name �Ώۃn���h���[�� 
	 * @return ���R�[�h�I�u�W�F�N�g
	 */
	public Object get(String name) {
		if (currentObject == null) {
			throw new NoSuchElementException();
		}
		return currentObject;
	}

	/**
	 * ���R�[�h�J�[�\�������̃��R�[�h�Ɉʒu�Â��܂��BJDBC��ResultSet�Ɠ������ł��B
	 * ���̃��R�[�h�I�u�W�F�N�g�����݂��鎞�́Atrue ��Ԃ��܂��B
	 * @param name �Ώۃn���h���[�� 
	 * @return ���̃��R�[�h�I�u�W�F�N�g�����݂��鎞�́Atrue �������łȂ��ꍇ�� false ��Ԃ��܂��B
	 */
	public boolean next(String name) {
		createMasterSortedMap(name);
		if (valueIterator.hasNext()) {
			currentObject =
				new LoggingData(
					(Timestamp) keyIterator.next(),
					(DoubleList) valueIterator.next());
			return true;
		} else {
			currentObject = null;
			return false;
		}
	}

	/**
	 * �ŏ����R�[�h�̃^�C���X�^���v��Ԃ��܂��B
	 * @param name �Ώۃn���h���[�� 
	 * @return �ŏ����R�[�h�̃^�C���X�^���v
	 */
	public Object firstKey(String name) {
		createMasterSortedMap(name);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		try {
			return masterSortedMap.firstKey();
		} catch (NoSuchElementException e) {
			return new Timestamp(System.currentTimeMillis());
		}
	}

	/**
	 * �ŏI���R�[�h�̃^�C���X�^���v��Ԃ��܂��B
	 * @param name �Ώۃn���h���[�� 
	 * @return �ŏI���R�[�h�̃^�C���X�^���v
	 */
	public Object lastKey(String name) {
		createMasterSortedMap(name);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		try {
			return masterSortedMap.lastKey();
		} catch (NoSuchElementException e) {
			return new Timestamp(System.currentTimeMillis());
		}
	}

	/**
	 * �^�C���X�^���v������ key �ȑO�̃��R�[�h���������A�|�C���^���ʒu�Â��܂��B
	 * @param name �Ώۃn���h���[�� 
	 * @param key �������郌�R�[�h�̃^�C���X�^���v
	 */
	public void findRecord(String name, Timestamp key) {
		createMasterSortedMap(name);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		SortedMap tailMap = masterSortedMap.tailMap(key);
		SortedMap headMap = masterSortedMap.headMap(key);
		SortedMap sortedMap = null;
		if (tailMap.containsKey(key) || headMap.isEmpty()) {
			sortedMap = new TreeMap(tailMap);
		} else {
			Object lastKey = headMap.lastKey();
			Object lastValue = headMap.get(lastKey);
			sortedMap = new TreeMap(tailMap);
			sortedMap.put(lastKey, lastValue);
		}
		createValueIterator(sortedMap);
	}


	private synchronized void createMasterSortedMap(String name) {
		/** ��{�ɂȂ�\�[�g�ς݃}�b�v�̐��� */
		if (!masterSortedMaps.containsKey(name)) {
			currentHandlerName = name;
			masterSortedMaps.put(name, getInitialData(currentHandlerName));
		}

		if (valueIterator == null) {
			createValueIterator((SortedMap) masterSortedMaps.get(name));
		}
	}

	private void createValueIterator(SortedMap map) {
		SortedMap valueSortedMap = new TreeMap(map);
		Collection collection = valueSortedMap.values();
		valueIterator = collection.iterator();
		Set valueSet = valueSortedMap.keySet();
		keyIterator = valueSet.iterator();
	}
	
	/**
	 * ValueListHandler ����ŐV�f�[�^���Ƃ肾���A�ێ����Ă��郍�M���O�f�[�^��
	 * �X�V���܂��B
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Thread ct = Thread.currentThread();

		try {
			while (ct == thread) {
				for (Iterator it = masterSortedMaps.keySet().iterator(); it.hasNext();) {
					String name = (String) it.next();
					SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
					Map updateData =
						getUpdateLoggingData(name, masterSortedMap);
					updateMasterSortedMap(updateData);
				}
				run_sleep();
			}
		} catch (NoSuchElementException e) {
			run_sleep();
		}
	}

	private void run_sleep() {
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
		}
	}
	
	private void updateMasterSortedMap(Map updateData) {
		for (Iterator it = masterSortedMaps.keySet().iterator(); it.hasNext();) {
			String name = (String) it.next();
			SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
			if ((updateData != null) && (updateData.size() > 0)) {
				if (logger.isDebugEnabled()) {
					logger.debug("updateData:" + updateData);
					logger.debug("master size:" + masterSortedMap.size());
				}

				masterSortedMap.putAll(updateData);
				if (masterSortedMap.size() > maxMapSize) {
					for (int i = 0, n = masterSortedMap.size() - maxMapSize
							; i < n
							; i++) {
						logger.debug("delete key:" + masterSortedMap.firstKey());
						masterSortedMap.remove(masterSortedMap.firstKey());
					}
				}
				firePropertyChange(null, masterSortedMap);
			}
		}
	}

	private SortedMap getInitialData(String currentHandlerName) {
		if (serverError != null) {
			return new TreeMap();
		}

		SortedMap map = null;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				map = valueListHandler.getInitialData(currentHandlerName);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				serverError.printStackTrace();
				continue;
			}
		}
		
		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return map;
	}
	
	private Map getUpdateLoggingData(String name, SortedMap masterSortedMap) {
		if (serverError != null) {
			return new HashMap();
		}

		Map map = null;

		for (int i = 1; ; i++) {
			if (i == Globals.RMI_METHOD_RETRY_COUNT) {		
				if (serverError != null) {
					ServerErrorUtil.invokeServerError();
					serverError.printStackTrace();
				}
			}
			try {
				Timestamp time = null;
				try {
					time = (Timestamp) masterSortedMap.lastKey();
				} catch (NoSuchElementException ex) {
					time = new Timestamp(System.currentTimeMillis());
				}
				map =
					valueListHandler.getUpdateLoggingData(
						name, time);
				if (serverError != null && i >= Globals.RMI_METHOD_RETRY_COUNT) {
					ServerErrorUtil.invokeServerRepair();
		            logger.error("Exception caught: ", serverError);
				}
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				continue;
			}
		}
		
		return map;
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
}
