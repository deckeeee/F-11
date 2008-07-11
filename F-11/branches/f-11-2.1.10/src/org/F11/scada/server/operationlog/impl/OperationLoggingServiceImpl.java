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
package org.F11.scada.server.operationlog.impl;

import java.sql.Timestamp;

import jp.gr.javacons.jim.DataHolder;

import org.F11.scada.server.operationlog.OperationLoggingService;
import org.F11.scada.server.operationlog.OperationLoggingUtil;
import org.F11.scada.server.operationlog.dao.OperationLoggingDao;
import org.F11.scada.server.operationlog.dto.OperationLogging;
import org.F11.scada.util.RmiUtil;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingServiceImpl implements OperationLoggingService {
    private OperationLoggingDao dao;
	/** データ更新ログユーティリティー */
	private OperationLoggingUtil util;
	
	private static final String LOGIN_MESSAGE = "ログイン";
	private static final String LOGOUT_MESSAGE = "ログアウト";

    public OperationLoggingServiceImpl() {
    	RmiUtil.registryServer(this, OperationLoggingService.class);
    }
    
    public void setDao(OperationLoggingDao dao) {
        this.dao = dao;
    }

    public void setUtil(OperationLoggingUtil util) {
        this.util = util;
    }

    public void logging(DataHolder dh,
            Object dataValue, String user, String ip, Timestamp timestamp) {
        OperationLogging logging = util.getOperationLogging(dh, dataValue, user, ip, timestamp);
        dao.insert(logging);
    }
    
    public void login(String user, String ip, Timestamp timestamp) {
    	OperationLogging logging = createOperationLogging(user, ip, timestamp, LOGIN_MESSAGE);
    	dao.insert(logging);
    }
    
    public void logout(String user, String ip, Timestamp timestamp) {
    	OperationLogging logging = createOperationLogging(user, ip, timestamp, LOGOUT_MESSAGE);
    	dao.insert(logging);
    }

	private OperationLogging createOperationLogging(String user, String ip, Timestamp timestamp, String message) {
		OperationLogging logging = new OperationLogging();
		logging.setOpeDate(timestamp);
		logging.setOpeIp(ip);
		logging.setOpeUser(user);
		logging.setOpeBeforeValue(message);
		logging.setOpeAfterValue("");
		logging.setOpeProvider("");
		logging.setOpeHolder("");
		return logging;
	}
}
