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

package org.F11.scada.theme;

import java.awt.Font;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.F11.scada.Service;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 時刻と日付を表示するクラスです。
 */
public class LogoTime extends Box implements Runnable, Service {
	private static final long serialVersionUID = -2850619238706853016L;

	private final JLabel timeLabel = new JLabel();
	private final JLabel dateLabel = new JLabel();

	private Thread thread;
	private boolean isViewWeek;

	public LogoTime(AbstractWifeApplet applet) {
		super(BoxLayout.Y_AXIS);
		setIsViewWeek(applet);
		timeLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		timeLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
		timeLabel.setAlignmentX(Box.CENTER_ALIGNMENT);
		dateLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		dateLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
		dateLabel.setAlignmentX(Box.CENTER_ALIGNMENT);

		updateTime();

		add(timeLabel);
		add(dateLabel);

		start();
	}

	private void setIsViewWeek(AbstractWifeApplet applet) {
		if (null != applet) {
			isViewWeek =
				applet.getConfiguration().getBoolean(
						"org.F11.scada.xwife.applet.isViewWeek", false);
		}
	}

	public void start() {
		if (null == thread) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (null != thread) {
			Thread ct = thread;
			thread = null;
			ct.interrupt();
		}
	}

	private void updateTime() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Date today = new Date();
				timeLabel.setText(DateFormatUtils.format(today, "HH:mm:ss"));
				dateLabel.setText(DateFormatUtils.format(today, getFormat()));
			}

			private String getFormat() {
				return isViewWeek ? "yyyy/MM/dd' ('E')'" : "yyyy/MM/dd";
			};
		});
	}

	public void run() {
		Thread ct = Thread.currentThread();
		while (ct == thread) {
			updateTime();
			ThreadUtil.sleep(250);
		}
	}
}