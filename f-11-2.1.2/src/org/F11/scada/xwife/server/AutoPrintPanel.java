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
package org.F11.scada.xwife.server;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.autoprint.AutoPrintEditor;
import org.F11.scada.server.autoprint.AutoPrintSchedule;
import org.F11.scada.server.autoprint.AutoPrintTask;
import org.F11.scada.server.autoprint.perser.AutoPrintDefine;
import org.F11.scada.server.io.AutoPrintDataService;
import org.F11.scada.server.io.AutoPrintDataStore;
import org.xml.sax.SAXException;

/**
 * @author hori
 */
public class AutoPrintPanel extends JPanel implements AutoPrintEditor {
	private static final long serialVersionUID = -3623430454977285832L;
	private final AutoPrintDataService stor;
	private List schedulePanels;

	/**
	 * 
	 */
	public AutoPrintPanel() throws IOException, SAXException {
		super();
		stor = new AutoPrintDataStore();
		schedulePanels = new ArrayList();
		init();
	}

	private void init() throws IOException, SAXException {
		setLayout(new GridLayout(0, 1));
		JPanel panel = new JPanel(new GridLayout(0, 3));
		panel.add(new JLabel(""));
		panel.add(new JLabel("自動印字", JLabel.CENTER));
		panel.add(new JLabel("日時", JLabel.CENTER));
		add(panel);

		AutoPrintDefine define = new AutoPrintDefine("/resources/AutoPrintDefine.xml");
		List tasks = define.getAutoPrintTasks();
		Map schedules = stor.getAutoPrintSchedules();
		for (Iterator it = tasks.iterator(); it.hasNext();) {
			AutoPrintTask task = (AutoPrintTask) it.next();
			AutoPrintSchedule schedule = (AutoPrintSchedule) schedules.get(task.getName());
			if (schedule != null) {
				task.setAutoPrintSchedule(schedule);
				task.setAutoPrintDataStore();
				SchedulePanel scp = new SchedulePanel(task, schedule);
				add(scp);
				schedulePanels.add(scp);
			}
		}
	}
	
	public void reloadAutoPrint() {
		Map schedules = stor.getAutoPrintSchedules();
		for (Iterator it = schedulePanels.iterator(); it.hasNext();) {
			SchedulePanel scp = (SchedulePanel)it.next();
			AutoPrintSchedule schedule = (AutoPrintSchedule) schedules.get(scp.getTaskName());
			scp.setSchedule(schedule);
		}
	}

	private class SchedulePanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = -9025035773496988005L;
		private AutoPrintTask task;
		private AutoPrintSchedule schedule;

		private JLabel onoffLabel;
		private JLabel ondate;

		public SchedulePanel(AutoPrintTask task, AutoPrintSchedule schedule) {
			this.task = task;
			this.schedule = schedule;

			setLayout(new GridLayout(0, 3));
			JButton but = new JButton(schedule.getScheduleName());
			but.addActionListener(this);
			add(but);

			onoffLabel = new JLabel();
			onoffLabel.setHorizontalAlignment(JLabel.CENTER);
			add(onoffLabel);
			ondate = new JLabel();
			add(ondate);

			setSchedule(schedule);
		}

		public void actionPerformed(ActionEvent e) {
			Frame parent = WifeUtilities.getParentFrame(this);
			schedule = schedule.showParamDialog(parent);
			setSchedule(schedule);

			stor.setAutoPrintSchedule(task.getName(), schedule);
		}
		
		public String getTaskName() {
			return task.getName();
		}
		
		public void setSchedule(AutoPrintSchedule schedule) {
			task.setAutoPrintSchedule(schedule);
			if (schedule.isAutoOn())
				onoffLabel.setText("する");
			else
				onoffLabel.setText("しない");
			ondate.setText(schedule.getDate());
		}
	}

	/**
	 * 自動印刷のサーバー名称を返します。
	 * @return 自動印刷のサーバー名称
	 */
	public String getServerName() {
		return AutoPrintPanel.class.getName();
	}

	/**
	 * サーバーに表示するコンポーネントを返します
	 * @return サーバーに表示するコンポーネントを返します
	 */
	public Component getComponent() {
		return this;
	}
}
