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
package org.F11.scada.applet.dialog;

import java.awt.Component;
import java.util.Calendar;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class DateTimePanel extends JPanel {
	private static final long serialVersionUID = 5660748119480967067L;
	private Calendar cal;
	private JComboBox year;
	private JComboBox month;
	private JComboBox day;
	private JComboBox hour;
	private JComboBox minute;
	private JComboBox second;

	public DateTimePanel(Calendar calendar) {
		super();
		cal = Calendar.getInstance();
		if (calendar != null) {
			cal.setTime(calendar.getTime());
		}
		cal.clear(Calendar.MILLISECOND);

		// year
		int thisyear = Calendar.getInstance().get(Calendar.YEAR);
		year = new JComboBox();
		for (int y = thisyear - 10; y <= thisyear; y++) {
			year.addItem(new Integer(y));
		}
		year.setSelectedItem(new Integer(cal.get(Calendar.YEAR)));
		add(year);
		add(new JLabel("/"));
		// month
		month = new JComboBox();
		for (int m = 1; m <= 12; m++) {
			month.addItem(new Integer(m));
		}
		month.setSelectedItem(new Integer(cal.get(Calendar.MONTH) + 1));
		add(month);
		add(new JLabel("/"));
		// day
		day = new JComboBox();
		for (int d = 1; d <= 31; d++) {
			day.addItem(new Integer(d));
		}
		day.setSelectedItem(new Integer(cal.get(Calendar.DAY_OF_MONTH)));
		add(day);
		add(new JLabel("  "));
		// hour
		hour = new JComboBox();
		for (int h = 0; h <= 23; h++) {
			hour.addItem(new Integer(h));
		}
		hour.setSelectedItem(new Integer(cal.get(Calendar.HOUR_OF_DAY)));
		add(hour);
		add(new JLabel(":"));
		// minute
		minute = new JComboBox();
		for (int n = 0; n <= 59; n++) {
			minute.addItem(new Integer(n));
		}
		minute.setSelectedItem(new Integer(cal.get(Calendar.MINUTE)));
		add(minute);
		add(new JLabel(":"));
		// second
		second = new JComboBox();
		for (int s = 0; s <= 59; s++) {
			second.addItem(new Integer(s));
		}
		second.setSelectedItem(new Integer(cal.get(Calendar.SECOND)));
		add(second);
	}

	public Calendar getCalendar() {
		cal.set(Calendar.YEAR, ((Integer) year.getSelectedItem()).intValue());
		cal.set(Calendar.MONTH, ((Integer) month.getSelectedItem()).intValue() - 1);
		cal.set(Calendar.DAY_OF_MONTH, ((Integer) day.getSelectedItem()).intValue());
		cal.set(Calendar.HOUR_OF_DAY, ((Integer) hour.getSelectedItem()).intValue());
		cal.set(Calendar.MINUTE, ((Integer) minute.getSelectedItem()).intValue());
		cal.set(Calendar.SECOND, ((Integer) second.getSelectedItem()).intValue());
		cal.getTime();
		return cal;
	}

	public void setEnabled(boolean enabled) {
		Component[] comp = getComponents();
		for (int i = 0; i < comp.length; i++) {
			comp[i].setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}
}