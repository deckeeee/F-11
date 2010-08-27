/*
 * =============================================================================
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

package org.F11.scada.xwife.applet.alarm;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;

import org.F11.scada.Globals;
import org.F11.scada.xwife.applet.AbstractWifeApplet;

public class AlarmFactory {
	private static final String TYPE = "xwife.applet.Applet.alarm.table.type";

	public JComponent getAlarm(AbstractWifeApplet wifeApplet, boolean changePage) {
		int type = wifeApplet.getConfiguration().getInt(TYPE, 0);
		switch (type) {
		case 0:
			return getAlarmTabPane(wifeApplet, changePage);
		case 1:
			return getCareerPanel(wifeApplet, changePage);
		default:
			throw new IllegalArgumentException("unknow type : "
				+ type
				+ ". see "
				+ TYPE
				+ " property in ClientConfiguration.xml.");
		}
	}

	private JComponent getCareerPanel(
			AbstractWifeApplet wifeApplet,
			boolean changePage) {
		CareerPanel careerPanel = new CareerPanel(wifeApplet);
		AlarmStats newMsg = new AlarmStats(wifeApplet);
		careerPanel.addTableModelListener(newMsg);
		careerPanel.addTableModelListener(new PriorityController(
			wifeApplet,
			changePage));

		return createAlarmPanel(wifeApplet, careerPanel, newMsg);
	}

	private JComponent getAlarmTabPane(
			AbstractWifeApplet wifeApplet,
			boolean changePage) {
		AlarmTabbedPane alarmTabPane =
			new AlarmTabbedPane(wifeApplet, JTabbedPane.TOP);
		AlarmStats newMsg = new AlarmStats(wifeApplet);
		alarmTabPane.addTableModelListener(newMsg);
		PriorityController controller =
			new PriorityController(wifeApplet, changePage);
		alarmTabPane.addTableModelListener(controller);
		alarmTabPane.addCheckTableListener(controller);

		return createAlarmPanel(wifeApplet, alarmTabPane, newMsg);
	}

	private JComponent createAlarmPanel(
			AbstractWifeApplet wifeApplet,
			JComponent alarmTabPane,
			AlarmStats newMsg) {
		JPanel panelNewAlarm = new JPanel(new BorderLayout());
		panelNewAlarm.setMinimumSize(Globals.ZERO_DIMENSION);
		JPanel alarmPanel = new JPanel();
		alarmPanel.setLayout(new BoxLayout(alarmPanel, BoxLayout.Y_AXIS));
		alarmPanel.add(newMsg);
		alarmPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		alarmPanel.setBackground(newMsg.getBackGroundColor());
		alarmPanel.setOpaque(true);
		panelNewAlarm.add(alarmPanel, BorderLayout.NORTH);
		panelNewAlarm.add(alarmTabPane, BorderLayout.CENTER);
		panelNewAlarm.setPreferredSize(new Dimension(wifeApplet
			.getMaximumSize().width, wifeApplet.getConfiguration().getInt(
			"xwife.applet.Applet.alarm.table.height",
			180)));
		return panelNewAlarm;
	}
}
