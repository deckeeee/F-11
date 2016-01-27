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
 * JasperReports コンポーネントを使用した、自動印刷の実行タスククラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class JasperAutoPrintTask extends SchedulerTask {
	/** スレッドプール実行クラス */
	private static Executor executor = Executors.newCachedThreadPool();
	/** 実行する処理内容 */
	private PrintDataSource printDataSource;
	/** Logging API */
	private static Logger log = Logger.getLogger(JasperAutoPrintTask.class);

	/**
	 * 実行する処理を設定します
	 * @param runnable 実行する処理
	 */
	public void setPrintDataSource(PrintDataSource printDataSource) {
		if (log.isDebugEnabled()) {
			log.debug("Set printDataSource : " + printDataSource);
		}
		this.printDataSource = printDataSource;
	}

	/**
	 * タスクを実行します
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
