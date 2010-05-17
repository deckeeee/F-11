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

import java.rmi.RemoteException;
import java.util.List;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.operationlog.OperationLoggingFinderService;
import org.F11.scada.server.operationlog.dao.OperationLoggingFinderDao;
import org.F11.scada.server.operationlog.dto.FinderConditionDto;
import org.F11.scada.util.RmiUtil;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class OperationLoggingFinderServiceImpl implements OperationLoggingFinderService {
    private OperationLoggingFinderDao dao;

    public OperationLoggingFinderServiceImpl() {
    	RmiUtil.registryServer(this, OperationLoggingFinderService.class);
    }

    public void setDao(OperationLoggingFinderDao dao) {
        this.dao = dao;
    }

    public List getOperationLogging(FinderConditionDto finder) {
        return dao.select(finder);
    }
    
    public int getCount(FinderConditionDto finder) throws RemoteException {
        return dao.getCount(finder);
    }

    public boolean isPrefix() {
    	return Boolean.valueOf(
				EnvironmentManager.get("/server/operationlog/prefix", "false"))
				.booleanValue();
    }
}
