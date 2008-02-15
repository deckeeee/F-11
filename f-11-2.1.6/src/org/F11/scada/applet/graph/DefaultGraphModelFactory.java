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
package org.F11.scada.applet.graph;

import java.lang.reflect.Constructor;
import java.util.List;

import org.F11.scada.EnvironmentManager;
import org.apache.log4j.Logger;

/**
 * GraphModelFactoryの実装
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class DefaultGraphModelFactory implements GraphModelFactory {
    /** GraphModel実装のクラス名 */
    private final String className;
    private final GraphPropertyModel model;
    private final String modeName;
    private final int maxMapSize;

    private static Logger log = Logger.getLogger(DefaultGraphModelFactory.class);

    public DefaultGraphModelFactory(String className, GraphPropertyModel model, String modeName) {
    	this(className, model, modeName, Integer.parseInt(EnvironmentManager
				.get("/server/logging/maxrecord", "4096")));
    }

    /**
     * 引数のクラス名のGraphModelを返すファクトリーを生成します。
     * @param className GraphModel実装のクラス名
     */
    public DefaultGraphModelFactory(String className, GraphPropertyModel model, String modeName, int maxMapSize) {
        this.className = className;
        this.model = model;
        this.modeName = modeName;
        this.maxMapSize = maxMapSize;
    }

    public GraphModel getGraphModel(String handlerName, List holderStrings) {
        GraphModel model = null;
        try {
            Class modelClass = Class.forName(className);
            Constructor constructor = modelClass.getConstructor(new Class[]{String.class, List.class, GraphPropertyModel.class, Integer.TYPE});
            model = (GraphModel) constructor.newInstance(new Object[]{handlerName, holderStrings, this.model, new Integer(maxMapSize)});
        } catch (ClassNotFoundException e) {
            log.error("Exception caught: ", e);
            throw new NoClassDefFoundError(e.getMessage());
        } catch (Exception e) {
            log.error("Exception caught: ", e);
        }
        return model;
    }

    public String getModeName() {
        return modeName;
    }
}
