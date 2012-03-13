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
package org.F11.scada.server.output.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.Service;
import org.F11.scada.server.output.OutputService;
import org.F11.scada.util.ThreadUtil;
import org.apache.log4j.Logger;


/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class Output implements Runnable, Service {
    private Thread thread;
    private List services;
    private long poolingTime;
    private static Logger log = Logger.getLogger(Output.class);
    
    public Output() {
        log.info("constrauct Output Service");
    }
    
    public void setPoolingTime(long poolingTime) {
        this.poolingTime = poolingTime;
    }

    public void addOutputService(OutputService service) {
        if (services == null) {
            services = new ArrayList();
        }
        log.info("added output service : " + service);
        services.add(service);
    }
    
    public void run() {
        Thread ct = Thread.currentThread();
        
        while (ct == thread) {
            for (Iterator i = services.iterator(); i.hasNext();) {
                OutputService desc = (OutputService) i.next();
                desc.write();
                ThreadUtil.sleep(poolingTime);
            }
        }
    }

    public void start() {
        log.info("start output service.");
        if (thread == null) {
            thread = new Thread(this);
            thread.setName(getClass().getName());
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            Thread th = thread;
            thread = null;
            th.interrupt();
        }
    }
}
