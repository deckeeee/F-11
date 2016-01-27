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

package org.F11.scada.server.autoprint.jasper;

import org.F11.scada.scheduling.SchedulerTask;
import org.F11.scada.server.autoprint.jasper.data.PrintDataSource;
import org.apache.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * JasperReports �R���|�[�l���g���g�p�����A��������̎��s�^�X�N�N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class JasperAutoPrintTask extends SchedulerTask {
	/** �X���b�h�v�[�����s�N���X */
	private static Executor executor = Executors.newCachedThreadPool();
	/** ���s���鏈�����e */
	private PrintDataSource printDataSource;
	/** Logging API */
	private static Logger log = Logger.getLogger(JasperAutoPrintTask.class);

	/**
	 * ���s���鏈����ݒ肵�܂�
	 * @param runnable ���s���鏈��
	 */
	public void setPrintDataSource(PrintDataSource printDataSource) {
		if (log.isDebugEnabled()) {
			log.debug("Set printDataSource : " + printDataSource);
		}
		this.printDataSource = printDataSource;
	}

	/**
	 * �^�X�N�����s���܂�
	 */
	public void run() {
		if (this.printDataSource == null) {
			throw new IllegalStateException("printDataSources is null.");
		}

		try {
			if (log.isDebugEnabled()) {
				log.debug("Execute printDataSource." + printDataSource);
			}
			executor.execute(printDataSource);
		} catch (RejectedExecutionException e) {}
	}
}
