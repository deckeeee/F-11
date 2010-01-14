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

package org.F11.scada.xwife.server.impl;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.communicater.EnvironmentMap;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.register.HolderRegisterBuilder;
import org.F11.scada.xwife.explorer.ExplorerElement;
import org.F11.scada.xwife.server.DataProviderFactory;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.F11.scada.xwife.server.communicater.EnvironmentPostgreSQL;
import org.F11.scada.xwife.server.communicater.EnvironmentPropertyFiles;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DataProviderFactoryImpl implements DataProviderFactory, ExplorerElement {
    private static Logger logger = Logger.getLogger(DataProviderFactoryImpl.class);

    private final ItemDao itemDao;
    private final HolderRegisterBuilder builder;
    private final int holderSize;
    private final S2Container container;
    private final CommunicaterFactory communicaterFactory;
    private JTabbedPane tabbedPane;

    public DataProviderFactoryImpl(ItemDao itemDao, HolderRegisterBuilder builder, int holderSize, S2Container container, CommunicaterFactory communicaterFactory) {
        this.itemDao = itemDao;
        this.builder = builder;
        this.holderSize = holderSize;
        this.container = container;
        this.communicaterFactory = communicaterFactory;
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    /* (non-Javadoc)
     * @see org.F11.scada.xwife.server.DataProviderFactory#create()
     */
    public Collection create() throws Exception {
		return createProviderByEnvironment();
    }

    private Collection createProviderByEnvironment() throws Exception {
		ArrayList providers = new ArrayList();
		Environment[] environments = null;
		String device = EnvironmentManager.get("/server/device", "");
		if (device != null && !"".equals(device)) {
			logger.info("Device file : " + device);
			environments = EnvironmentPropertyFiles.getEnvironments(device);
		} else {
			logger.info("Device Data Base");
			environments = EnvironmentPostgreSQL.getEnvironments();
		}

		createProviders(environments, providers);
		EnvironmentMap.putAll(environments);
		return Collections.unmodifiableList(providers);
	}

    private void createProviders(Environment[] environmentList, List providers)
			throws Exception {
		for (int i = 0; i < environmentList.length; i++) {
		    AlarmReferencer alarm =
		        (AlarmReferencer) container.getComponent(WifeDataProvider.PARA_NAME_ALARM);
		    AlarmReferencer demand=
		        (AlarmReferencer) container.getComponent(WifeDataProvider.PARA_NAME_DEMAND);
			WifeDataProviderImpl dp = new WifeDataProviderImpl(holderSize, environmentList[i], itemDao, builder, alarm, demand, communicaterFactory);
			createTable(dp, alarm);
			providers.add(dp);
		}
	}

	private void createTable(final WifeDataProvider dp, TableModel model) {
		JTable table = new JTable(model);
		JScrollPane scpane = new JScrollPane(table);
		tabbedPane.addTab(dp.getDataProviderName(), scpane);
	}

	public Component getComponent() {
		return tabbedPane;
	}
}
