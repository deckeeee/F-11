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
 *
 */

package org.F11.scada.server.frame.impl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.frame.FrameDefineHandler;
import org.F11.scada.server.frame.FrameDefineHandlerFactory;

/**
 * 初期値のFrameDefineHandlerFactoryの実装クラスです。
 * ネームサーバーから指定のオブジェクトを返します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FrameDefineHandlerFactoryImpl implements FrameDefineHandlerFactory {

    public FrameDefineHandlerFactoryImpl() {
        super();
    }

    /* (non-Javadoc)
     * @see org.F11.scada.server.frame.FrameDefineHandlerFactory#getFrameDefineHandler()
     */
    public FrameDefineHandler getFrameDefineHandler() {
        FrameDefineHandler handler = null;
        try {
            handler = (FrameDefineHandler) Naming.lookup(WifeUtilities.createRmiFrameDefineManager());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return handler;
    }
}
