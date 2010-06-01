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
package org.F11.scada;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class ServiceExcecutor implements Service {
    private List services;
    private static Logger log = Logger.getLogger(ServiceExcecutor.class);
    
    public ServiceExcecutor() {
        log.info("start ServiceExcecutor");
    }
    
    public void addService(Service service) {
        if (services == null) {
            services = new CopyOnWriteArrayList();
        }
        log.info("service added : " + service);
        services.add(service);
    }
    
    public void clearService() {
        stop();
        if (services != null) {
            services.clear();
        }
    }

    public void start() {
        log.info("start services");
        if (services != null) {
            for (Iterator i = services.iterator(); i.hasNext();) {
                Service service = (Service) i.next();
                log.info("start service : " + service);
                service.start();
            }
        }
    }

    public void stop() {
        if (services != null) {
            for (Iterator i = services.iterator(); i.hasNext();) {
                Service service = (Service) i.next();
                service.stop();
            }
        }
    }
}
