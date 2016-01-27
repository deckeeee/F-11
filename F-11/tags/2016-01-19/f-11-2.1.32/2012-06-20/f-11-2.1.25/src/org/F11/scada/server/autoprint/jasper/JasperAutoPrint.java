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

package org.F11.scada.server.autoprint.jasper;

import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;

import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.Scheduler;
import org.F11.scada.server.autoprint.AutoPrintEditor;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * 印刷タスクを実行するクラスです
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class JasperAutoPrint implements AutoPrintEditor {
	/** SchedulerTask を実行するスケジューラー */
	private Scheduler scheduler = new Scheduler();
	/** xml 定義より生成されたスケジュールオブジェクトのリスト */
	private ArrayList schedules = new ArrayList();
	private final String file;

	public JasperAutoPrint() throws IOException, SAXException {
		this("/resources/task/task.xml");
	}

	/**
	 * 
	 * @throws IOException
	 * @throws SAXException
	 */
	public JasperAutoPrint(String file) throws IOException, SAXException {
		this.file = file;
		load();
	}

	private void load() throws IOException, SAXException {
		this.schedules.clear();
		Digester digester = new Digester();
		digester.register(
			"-//Project F-11//DTD F-11 Task Configuration 1.0//JP",
			"resources/task/tasks_1_0.dtd");
		digester.setValidating(true);
		digester.push(this);
		digester.addRuleSet(new JasperRuleSet());
		
		URL url = getClass().getResource(this.file);
		InputStream in = null;
		try {
			in = url.openStream();
			digester.parse(in);
			start();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * 実行するスケジュールを追加します
	 * @param schedule スケジュール
	 */
	public void addSchedule(Schedule schedule) {
		this.schedules.add(schedule);
	}

	/**
	 * 定義されたスケジュールを開始します
	 */
	private void start() {
		for (Iterator it = this.schedules.iterator(); it.hasNext();) {
			Schedule schedule = (Schedule) it.next();
			this.scheduler.schedule(schedule);
		}
	}

	/**
	 * スケジュールをリスタートします。
	 */
	public void reloadAutoPrint() {
		this.scheduler.cancel();
		this.scheduler = new Scheduler();
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自動印刷のサーバー名称を返します。
	 * @return 自動印刷のサーバー名称
	 */
	public String getServerName() {
		return JasperAutoPrint.class.getName();
	}

	/**
	 * サーバーに表示するコンポーネントを返します
	 * @return サーバーに表示するコンポーネントを返します
	 */
	public Component getComponent() {
		return new JLabel("Jasperreports Version.");
	}
/*
	public static void main(String[] args) throws DataProviderDoesNotSupportException, IOException, SAXException {
		Manager.getInstance().addDataProvider(TestUtil.createDataProvider());
//		URL url = JasperAutoPrint.class.getResource("/resources/debug_log4j.xml");
//		DOMConfigurator.configure(url);
		BasicConfigurator.configure();
		new JasperAutoPrint();
	}
*/
}
