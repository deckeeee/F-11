/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.cat.logic;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 * チェックロジックを実行するタスクです。
 * 
 * @author maekawa
 * 
 */
public class ExecuteTask extends Task<Void, Void> {
	private final Log log = LogFactory.getLog(ExecuteTask.class);
	/** チェックロジックのリスト */
	private final List<CheckLogic> checkLogics;
	/** 物件フォルダのパス */
	private final String path;

	public ExecuteTask(
			Application application,
			List<CheckLogic> checkLogics,
			String path) {
		super(application);
		this.checkLogics = checkLogics;
		this.path = path;
		setUserCanCancel(true);
	}

	@Override
	protected Void doInBackground() throws InterruptedException {
		try {
			for (CheckLogic logic : checkLogics) {
				if (!isCancelled()) {
					logic.execute(path, this);
				}
			}
		} catch (IOException e) {
			log.error("チェック処理中にIOエラーが発生", e);
			return null;
		}
		return null;
	}

	@Override
	protected void succeeded(Void ignored) {
		setMessage("Done");
	}

	@Override
	protected void cancelled() {
		setMessage("Canceled");
	}

	public void setProgress(float value, float max) {
		super.setProgress(value, 0, max);
	}

	public void setMsg(String msg) {
		super.setMessage(msg);
	}
}
