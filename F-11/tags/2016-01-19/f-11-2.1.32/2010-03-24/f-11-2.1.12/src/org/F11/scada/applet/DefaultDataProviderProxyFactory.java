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
package org.F11.scada.applet;

import java.rmi.RemoteException;

import jp.gr.javacons.jim.DataProvider;

import org.F11.scada.exception.RemoteRuntimeException;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.xwife.applet.Session;
import org.F11.scada.xwife.applet.WifeDataProviderProxy;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class DefaultDataProviderProxyFactory implements DataProviderProxyFactory {
    private final Session session;
    private final Authenticationable authenticationable;
    
    private static Logger logger = Logger.getLogger(DefaultDataProviderProxyFactory.class);

    public DefaultDataProviderProxyFactory(Session session, Authenticationable authenticationable) {
        this.session = session;
        this.authenticationable = authenticationable;
    }

	public DataProvider createDataProvider(DataProviderDesc desc) {
        if (WifeDataProvider.class.isAssignableFrom(desc.getDataProviderClass())) {
            try {
                WifeDataProviderProxy dpProxy = new WifeDataProviderProxy(session, authenticationable);
                dpProxy.setDataProviderName(desc.getProvider());
                return dpProxy;
            } catch (RemoteException e) {
                throw new RemoteRuntimeException(e);
            }
        } else {
            logger.debug("Not create " + desc.getProvider() + " class is " + desc.getDataProviderClass());
            return null;
        }
	}
}
