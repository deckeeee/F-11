/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/Attic/DefaultSelectiveAllDataGraphModel.java,v 1.1.2.6 2007/10/15 00:22:04 frdm Exp $
 * $Revision: 1.1.2.6 $
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.io.SelectiveAllDataValueListHandler;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * �f�t�H���g�� GraphModel �����N���X�ł��B ValueListHandler ���R���|�W�V�������āA�X�g���[�W�f�o�C�X���f�[�^���Q�Ƃ��܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultSelectiveAllDataGraphModel extends AbstractGraphModel {
	/** �f�[�^�n���h���}�l�[�W���[�ł��B */
	private SelectiveAllDataValueListHandler valueListHandler;
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
	/** �\�[�g�ς݃}�b�v�̍ő匏�� */
	private final int maxMapSize;

	private static Logger logger;
	/** �T�[�o�[�G���[��O�ł� */
	private Exception serverError;

	/** ���o����f�[�^�z���_�[�̃��X�g **/
	private List holderStrings;
	/** �f�[�^�n���h���̃t�@�N�g���[ */
	private final SelectiveAllDataValueListHandlerFactory factory;
	/** ���f���������̍ł��Â����R�[�h�̃^�C���X�^���v */
	private final Timestamp firstTime;
	/** ���f���������̍ł��V�������R�[�h�̃^�C���X�^���v */
	private final Timestamp lastTime;
	/** �O���t�E�v���p�e�B�[���f�� */
	private final GraphPropertyModel model;

	/**
	 * �R���X�g���N�^ �f�t�H���g�̃t�@�N�g���[�ŏ��������܂�
	 * 
	 * @param handlerName �f�[�^�n���h����
	 * @param holderStrings ���o����z���_�[�̃��X�g
	 * @param model �O���t�E�v���p�e�B�[���f��
	 */
	public DefaultSelectiveAllDataGraphModel(
			String handlerName,
			List holderStrings,
			GraphPropertyModel model,
			int maxMapSize) throws RemoteException {
		this(
			handlerName,
			holderStrings,
			new DefaultSelectiveAllDataValueListHandlerFactory(),
			maxMapSize,
			model);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param handlerName �f�[�^�n���h����
	 * @param holderStrings ���o����z���_�[�̃��X�g
	 * @param factory �n���h���t�@�N�g���[�N���X
	 * @param model �O���t�E�v���p�e�B�[���f��
	 */
	public DefaultSelectiveAllDataGraphModel(
			String handlerName,
			List holderStrings,
			SelectiveAllDataValueListHandlerFactory factory,
			GraphPropertyModel model,
			int maxMapSize) throws RemoteException {
		this(handlerName, holderStrings, factory, maxMapSize, model);
	}

	/**
	 * �R���X�g���N�^
	 * 
	 * @param handlerName �f�[�^�n���h����
	 * @param holderStrings ���o����z���_�[�̃��X�g
	 * @param factory �n���h���t�@�N�g���[�N���X
	 * @param maxMapSize �o�b�t�@�pMap�̍ő�T�C�Y
	 * @param model �O���t�E�v���p�e�B�[���f��
	 */
	public DefaultSelectiveAllDataGraphModel(
			String handlerName,
			List holderStrings,
			SelectiveAllDataValueListHandlerFactory factory,
			int maxMapSize,
			GraphPropertyModel model) throws RemoteException {
		super();

		this.masterSortedMaps = new ConcurrentHashMap();
		this.maxMapSize = maxMapSize;
		this.holderStrings = new ArrayList(holderStrings);
		logger = Logger.getLogger(getClass().getName());
		this.factory = factory;
		this.model = model;

		logger.info("ValueListHandler:"
			+ WifeUtilities.createRmiSelectiveAllDataValueListHandlerManager());

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
			throw ServerErrorUtil.createException(serverError);
		}

		firstTime = valueListHandler.firstTime(handlerName, holderStrings);
		lastTime = valueListHandler.lastTime(handlerName, holderStrings);

		createMasterSortedMap(handlerName);

		start();

		logger.debug("DefaultSelectiveAllDataGraphModel start.");
		logger.info("max map size : " + maxMapSize);
	}

	private void lookup() {
		valueListHandler = factory.getSelectiveAllDataValueListHandler();
	}

	/**
	 * �Ώۃn���h���[�̃��R�[�h�I�u�W�F�N�g��Ԃ��܂��B
	 * 
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
	 * ���R�[�h�J�[�\�������̃��R�[�h�Ɉʒu�Â��܂��BJDBC��ResultSet�Ɠ������ł��B ���̃��R�[�h�I�u�W�F�N�g�����݂��鎞�́Atrue
	 * ��Ԃ��܂��B
	 * 
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
	 * 
	 * @param name �Ώۃn���h���[��
	 * @return �ŏ����R�[�h�̃^�C���X�^���v
	 */
	public Object firstKey(String name) {
		return firstTime;
	}

	/**
	 * �ŏI���R�[�h�̃^�C���X�^���v��Ԃ��܂��B
	 * 
	 * @param name �Ώۃn���h���[��
	 * @return �ŏI���R�[�h�̃^�C���X�^���v
	 */
	public Object lastKey(String name) {
		return lastTime;
	}

	/**
	 * �^�C���X�^���v������ key �ȑO�̃��R�[�h���������A�|�C���^���ʒu�Â��܂��B
	 * 
	 * @param name �Ώۃn���h���[��
	 * @param key �������郌�R�[�h�̃^�C���X�^���v
	 */
	public void findRecord(String name, Timestamp key) {
		createMasterSortedMap(name);
		key = new Timestamp(key.getTime() - 1L);
		if (key.before(this.firstTime)) {
			key = this.firstTime;
		}
		changeMasterSortedMap(name, key);
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		if (!masterSortedMap.isEmpty()) {
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
	}

	private void changeMasterSortedMap(String name, Timestamp key) {
		// �}�X�^�[�}�b�v�O���v�����ꂽ�ꍇ�A�}�X�^�[�}�b�v�̐ؑ֏������s���B
		SortedMap masterSortedMap = (SortedMap) masterSortedMaps.get(name);
		if (isNotContentKey(masterSortedMap, key)) {
			logger.debug("key:"
				+ key
				+ " "
				+ masterSortedMap.firstKey()
				+ "�`"
				+ masterSortedMap.lastKey());
			try {
				SortedMap data =
					trimSortedMap(valueListHandler.getLoggingData(
						name,
						holderStrings,
						key,
						maxMapSize));
				masterSortedMaps.put(name, data);
				createValueIterator(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isNotContentKey(SortedMap map, Timestamp key) {
		if (!map.isEmpty()) {
			Timestamp firstTime = (Timestamp) map.firstKey();
			Timestamp lastTime = (Timestamp) map.lastKey();

			long scale =
				(model.getHorizontalScaleCount() + 1)
					* model.getHorizontalScaleWidth();
			long remainder = lastTime.getTime() - key.getTime();
			logger.debug("scale:" + scale + " remainder:" + remainder);

			return key.before(firstTime)
				|| key.after(lastTime)
				|| (remainder < scale);
		} else {
			return false;
		}
	}

	private SortedMap trimSortedMap(SortedMap map) {
		Timestamp mapLastkey = (Timestamp) map.lastKey();
		TreeMap newMaster = new TreeMap(map);
		Timestamp trimKey = new Timestamp(lastTime.getTime() + 1);
		if (mapLastkey.after(trimKey)) {
			SortedMap retMap = map.tailMap(trimKey);
			for (Iterator i = retMap.keySet().iterator(); i.hasNext();) {
				Timestamp key = (Timestamp) i.next();
				newMaster.remove(key);
			}
		}

		return newMaster;
	}

	private synchronized void createMasterSortedMap(String name) {
		// ��{�ɂȂ�\�[�g�ς݃}�b�v�̐���
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

	private SortedMap getInitialData(String currentHandlerName) {
		if (serverError != null) {
			return new TreeMap();
		}

		SortedMap map = null;
		for (int i = 0; i < Globals.RMI_METHOD_RETRY_COUNT; i++) {
			try {
				logger.debug("currentHandlerName : " + currentHandlerName);
				logger.debug("holderStrings : " + holderStrings);
				map =
					valueListHandler.getInitialData(
						currentHandlerName,
						holderStrings);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				continue;
			}
		}

		if (serverError != null) {
			ServerErrorUtil.invokeServerError();
			serverError.printStackTrace();
		}

		return map;
	}

	public void start() {
	}

	public void stop() {
	}
}
