/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.tool.conf;

import java.awt.Component;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.F11.scada.tool.conf.io.AlarmDefineStream;
import org.F11.scada.tool.conf.io.ClientConfigurationStream;
import org.F11.scada.tool.conf.io.ClientsDefineStream;
import org.F11.scada.tool.conf.io.PreferencesStream;
import org.F11.scada.tool.conf.io.RemoveDefineStream;
import org.F11.scada.tool.conf.io.TimeSetBean;
import org.F11.scada.tool.conf.io.TimeSetStream;
import org.F11.scada.tool.conf.io.TimeSetTaskBean;
import org.apache.log4j.Logger;

public class StreamManager implements TimeSetManager {
	private static final Logger log = Logger.getLogger(StreamManager.class);

	private final PreferencesStream preferences = new PreferencesStream();
	private final AlarmDefineStream alarmdefine = new AlarmDefineStream();
	private final ClientConfigurationStream clientConf =
		new ClientConfigurationStream();
	private final ClientsDefineStream clientsDef = new ClientsDefineStream();
	private final TimeSetStream timeSet = new TimeSetStream();
	private final RemoveDefineStream removeDef = new RemoveDefineStream();
	private final Component compo;

	public StreamManager(Component compo) {
		this.compo = compo;
	}

	// Preferences
	public String getPreferences(String key, String def) {
		return preferences.getEnv(key, def);
	}

	public void setPreferences(String key, String value) {
		preferences.putEnv(key, value);
		compo.setEnabled(true);
	}

	// AlarmDefine
	public String getAlarmDefine(String key, String def) {
		return alarmdefine.getData(key, def);
	}

	public void setAlarmDefine(String key, String value) {
		alarmdefine.putData(key, value);
		compo.setEnabled(true);
	}

	// ClientConfiguration
	public String getClientConf(String key, String def) {
		return clientConf.getString(key, def);
	}

	public void setClientConf(String key, String value) {
		clientConf.putString(key, value);
		compo.setEnabled(true);
	}

	// ClientsDefine
	public List getClientBeansList() {
		return clientsDef.getBeansList();
	}

	public void setClientBeansList(List list) {
		clientsDef.setBeansList(list);
		compo.setEnabled(true);
	}

	// TimeSet
	public String getTimeSet(String name, String key, String def) {
		return timeSet.getValue(name, key, def);
	}

	public List<TimeSetBean> getTimeSetBeansList(String name) {
		return timeSet.getBeansList(name);
	}

	public void setTimeSet(String name, String key, String value) {
		timeSet.setValue(name, key, value);
		compo.setEnabled(true);
	}

	public void setTimeSetBeansList(String name, List list) {
		timeSet.setBeansList(name, list);
		compo.setEnabled(true);
	}

	public void setTimeSetTask(TimeSetTaskBean bean) {
		timeSet.setTimeSetTask(bean);
		compo.setEnabled(true);
	}

	public TimeSetTaskBean removeTimeSetTask(TimeSetTaskBean bean) {
		compo.setEnabled(true);
		return timeSet.removeTimeSetTask(bean);
	}

	public List<TimeSetTaskBean> getTimeSetTask() {
		return timeSet.getTimeSetTask();
	}

	// RemoveDefine
	public List getCountRemoveList() {
		return removeDef.getCountRemove();
	}

	public List getSecondRemoveList() {
		return removeDef.getSecondRemove();
	}

	public void setCountRemoveList(List list) {
		removeDef.setCountRemove(list);
		compo.setEnabled(true);
	}

	public void setSecondRemoveList(List list) {
		removeDef.setSecondRemove(list);
		compo.setEnabled(true);
	}

	public void load() throws Exception {
		URL url = getClass().getResource("/.");
		URI uri = new URI(url.toExternalForm());
		String currpath = uri.getPath();
		preferences.load(currpath + "resources/Preferences.xml");
		alarmdefine.load(currpath + "resources/AlarmDefine.xml");
		clientConf.load(currpath + "resources/ClientConfiguration.xml");
		clientsDef.load(currpath + "resources/ClientsDefine.xml");
		timeSet.load(currpath + "resources/TimeSet.xml");
		File fi = new File(currpath + "resources/RemoveDefine.dicon");
		if (fi.exists()) {
			removeDef.load(fi.getPath());
		}
		log.debug("all file loaded.");
	}

	public void save() throws Exception {
		URL url = getClass().getResource("/.");
		URI uri = new URI(url.toExternalForm());
		String currpath = uri.getPath();
		preferences.save(currpath + "resources/Preferences.xml");
		alarmdefine.save(currpath + "resources/AlarmDefine.xml");
		clientConf.save(currpath + "resources/ClientConfiguration.xml");
		clientsDef.save(currpath + "resources/ClientsDefine.xml");
		timeSet.save(currpath + "resources/TimeSet.xml");
		removeDef.save(currpath + "resources/RemoveDefine.dicon");
		log.debug("all file saved.");
	}
}
