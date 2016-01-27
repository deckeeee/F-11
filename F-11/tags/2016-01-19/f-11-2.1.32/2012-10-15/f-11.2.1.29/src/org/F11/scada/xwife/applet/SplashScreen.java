/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.util.JavaVersion;
import org.apache.commons.configuration.Configuration;

public class SplashScreen extends JFrame {
	private static final long serialVersionUID = 8478667022461158198L;
	private JProgressBar bar;
	private final Configuration configuration;
	private final AbstractWifeApplet applet;

	public SplashScreen(AbstractWifeApplet applet, Configuration configuration) {
		this.applet = applet;
		this.configuration = configuration;
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		init();
	}

	private void init() {
		String title = configuration.getString("org.F11.scada.xwife.applet.splash.title", "システム起動中");
		setTitle(title);
		createProgressBar();
		JLabel logo = createLabel();

		Container container = getContentPane();
		container.add(logo, BorderLayout.CENTER);
		container.add(bar, BorderLayout.SOUTH);
		setPosison();
	}

	private void setPosison() {
		pack();
		WifeUtilities.setScreenCenter(this);
		if (isSetAlwaysOnTop()) {
			setAlwaysOnTop(true);
		}
	}

	private boolean isSetAlwaysOnTop() {
		JavaVersion javaVersion = new JavaVersion();
		return javaVersion.compareTo(new JavaVersion(1, 5, 0, 0)) >= 0
				&& applet.isStandAlone();
	}

	private JLabel createLabel() {
		String image = configuration.getString("org.F11.scada.xwife.applet.splash.image", "/images/splash.png");
		JLabel logo = new JLabel(GraphicManager.get(image));
		logo.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getClickCount() > 4) {
					dispose();
				}
			}
		});
		return logo;
	}

	private void createProgressBar() {
		bar = new JProgressBar(0, 10);
		bar.setOpaque(true);
		bar.setBackground(SystemColor.window);
		bar.setForeground(Color.BLUE);
	}

	public void incrementValue() {
		if (SwingUtilities.isEventDispatchThread()) {
			bar.setValue(bar.getValue() + 1);
		} else {
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					bar.setValue(bar.getValue() + 1);
				}
			});
		}
	}

	public void dispose() {
		super.dispose();
	}
}
