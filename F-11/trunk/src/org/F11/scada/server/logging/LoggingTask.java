/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/logging/LoggingTask.java,v 1.13.4.4 2006/05/26 05:51:07 frdm Exp $
 * $Revision: 1.13.4.4 $
 * $Date: 2006/05/26 05:51:07 $
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

package org.F11.scada.server.logging;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.HandlerFactory;
import org.F11.scada.server.io.ValueListHandlerElement;
import org.F11.scada.server.io.ValueListHandlerManager;
import org.apache.log4j.Logger;

/**
 * ���M���O�p�̃^�X�N�N���X�ł��B
 */
public class LoggingTask extends TimerTask {
	/** �f�[�^�z���_�̃��X�g�ł� */
	protected List dataHolders;
	/** ���M���O���X�i�[�̃��X�g */
	private List loggingListeners;
	/** ���M���O�f�[�^�n���h�� */
	private ValueListHandlerElement handler;
	/** �n���h���[�t�@�g���[�� */
	private final String factoryName;

	/** ���M���OAPI */
	private static Logger logger;
	
	/** ���Q�Ƃ���e�[�u���� */
	private final List<String> tables;

	/**
	 * �R���X�g���N�^
	 * @param name ���M���O��
	 * @param dataHolders �f�[�^�z���_�[�̃��X�g
	 * @param factoryName �f�[�^�i���N���X��
	 * @exception SQLException DBMS�ɐڑ��ł��Ȃ������Ƃ�
	 */
	public LoggingTask(
			String name,
			List dataHolders,
			String factoryName,
			ValueListHandlerManager handlerManager,
			String schedule,
			List<String> tables)
			throws SQLException, MalformedURLException, RemoteException {
		super();
		this.dataHolders = dataHolders;
		logger = Logger.getLogger(getClass().getName());

		this.factoryName = factoryName;
		HandlerFactory factory = HandlerFactory.getHandlerFactory(factoryName);
		addLoggingListener(factory.createStoreHandler(name));

		// PostgreSQL Value List Handler
		handler = factory.createValueListHandler(name, dataHolders);
		addLoggingListener(handler);
		handlerManager.addValueListHandlerElement(name, handler);
		this.tables = tables;
	}

	/**
	 * �X�P�W���[���J�n�̏����B
	 */
	public void run() {
		logger.debug("data store start!!");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp today = new Timestamp(cal.getTime().getTime());
		//dataHolders��蕶���񁨃f�[�^�z���_�[���C�x���g���΂���B
		LoggingDataEvent dataEvent = new LoggingDataEvent(this, today, dataHolders);
		fireChangeLoggingData(dataEvent);
	}
	
	/**
	 * ���M���O�f�[�^�ύX�C�x���g�̃��X�i�[�o�^���܂��B
	 * @param l ���M���O�f�[�^�ύX�C�x���g�̃��X�i�[�I�u�W�F�N�g
	 */
	public synchronized void addLoggingListener(LoggingDataListener l) {
		if (loggingListeners == null) {
			loggingListeners = new LinkedList();
		}
		loggingListeners.add(l);
	}
	
	/**
	 * ���M���O�f�[�^�ύX�C�x���g�̃��X�i�[���폜���܂��B
	 * @param l ���M���O�f�[�^�ύX�C�x���g�̃��X�i�[�I�u�W�F�N�g
	 */
	public synchronized void removeLoggingListener(LoggingDataListener l) {
		if (loggingListeners == null) {
			return;
		}
		loggingListeners.remove(l);
	}
	
	/**
	 * ���M���O�f�[�^�ύX�C�x���g�����X�i�[�ɔ��΂��܂��B
	 */
	protected void fireChangeLoggingData(LoggingDataEvent event) {
		if (loggingListeners == null || loggingListeners.size() <= 0) {
			return;
		}

		synchronized(this) {
			for (Iterator it = loggingListeners.iterator(); it.hasNext();) {
				LoggingDataListener element = (LoggingDataListener) it.next();
				element.changeLoggingData(event);
			}
		}
	}

	public List getDataHolders() {
		return dataHolders; 
	}
	
	/**
	 * ���M���O�f�[�^�n���h���ɁA�X�V�ナ�X�i�[��ǉ����܂��B
	 * @param l
	 */
	public void addElementLoggingListener(LoggingDataListener l) {
		handler.addLoggingDataListener(l);
	}

	/**
	 * �n���h���[�t�@�N�g���[����Ԃ��܂��B
	 * @return �n���h���[�t�@�N�g���[����Ԃ��܂��B
	 */
	public String getFactoryName() {
		return factoryName;
	}
	
	/**
	 * �A�����鑼�e�[�u�����̃��X�g��Ԃ��܂��B
	 * 
	 * @return �A�����鑼�e�[�u�����̃��X�g��Ԃ��܂��B
	 */
	public List<String> getTables() {
		return Collections.unmodifiableList(tables);
	}
}
