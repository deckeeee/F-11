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

package org.F11.scada.xwife.applet;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.server.alarm.table.FindAlarmCondition;
import org.F11.scada.xwife.applet.alarm.AlarmFactory;

/**
 * WIFE システムのメインコンソール画面です。 ツリー形式の選択画面、平面図、一覧表、警報一覧表等を表示します。
 */
public class WifeAppletC extends AbstractNewApplet {
	private static final long serialVersionUID = 9029283868398055560L;

	public WifeAppletC() throws RemoteException {
		this(false, false);
	}

	public WifeAppletC(boolean isStandalone, boolean soundoffAtStarted)
			throws RemoteException {
		super(isStandalone, soundoffAtStarted);
		setAppletTypeC(true);
	}

	protected JComponent createAlarmComponent(
		AbstractNewApplet applet,
		String alarmDefPath) {
		return new AlarmPanel(applet, alarmDefPath);
	}

	private static class AlarmPanel extends AbstractAlarmPanel {
		private static final long serialVersionUID = 5469799141692344961L;

		protected AlarmPanel(
				final AbstractNewApplet wifeApplet,
				String configFile) {
			super(wifeApplet, configFile);
		}

		protected void changeAlarmPanel(ActionEvent e) {
			logger.debug("changeAlarmPanel");
			final JButton but = (JButton) e.getSource();
			if (panelAlarmList.isVisible()) {
				but.setText(VIEWMODE_UP);
				panelAlarmList.setVisible(false);

				wifeApplet.mainSplit
						.setDividerLocation(wifeApplet.configuration.getInt(
								"xwife.applet.Applet.treeHeight", 775));
			} else {
				but.setText(VIEWMODE_DW);
				panelAlarmList.setVisible(true);
				condition = new FindAlarmCondition();
				s_order.setSelectedIndex(0);
				h_order.setSelectedIndex(0);
				c_order.setSelectedIndex(0);
				updateAlarmConditionLabels();

				wifeApplet.mainSplit.setDividerLocation(0);
			}
		}

		protected JComponent getAlarmNewLine(AbstractWifeApplet wifeApplet) {
			AlarmFactory factory = new AlarmFactory();
			return factory.getAlarm(wifeApplet, true);
		}
	}

	// Main メソッド
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		boolean sound = false;
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if ("-nosound".equalsIgnoreCase(args[i])) {
					sound = true;
				}
			}
		}
		WifeAppletC applet = null;
		try {
			applet = new WifeAppletC(true, sound);
		} catch (RemoteException e) {
			JOptionPane.showInternalMessageDialog(frame,
					ServerErrorUtil.ERROR_MESSAGE,
					ServerErrorUtil.ERROR_MESSAGE, JOptionPane.ERROR_MESSAGE);
		}
		setOptions(args, applet);
		setCloseAction(frame, applet);
		frame.setTitle(getTitle());
		frame.getContentPane().add(applet, BorderLayout.CENTER);
		try {
			applet.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		applet.start();
		applet.setFrameBounds(frame);
		if (applet.configuration.getBoolean("xwife.applet.Applet.maximized",
				false)) {
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

	private static void setOptions(String[] args, WifeAppletC applet) {
		if (null != args) {
			for (int i = 0; i < args.length; i++) {
				if ("-treeclick".equals(args[i])) {
					applet.setTreeClick(true);
				}
			}
		}
	}
}
