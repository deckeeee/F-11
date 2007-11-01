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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.F11.scada.Globals;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.applet.operationlog.DefaultOperationLoggingTableModel;
import org.F11.scada.applet.operationlog.OperationLoggingFinder;
import org.F11.scada.applet.operationlog.OperationLoggingTable;
import org.F11.scada.server.alarm.table.FindAlarmCondition;
import org.F11.scada.xwife.applet.alarm.AlarmFactory;

/**
 * WIFE システムのメインコンソール画面です。 ツリー形式の選択画面、平面図、一覧表、警報一覧表等を表示します。
 */
public class WifeAppletD extends AbstractNewApplet {
	private static final long serialVersionUID = -5167263979547337541L;

	public WifeAppletD() throws RemoteException {
		this(false);
	}

	public WifeAppletD(boolean isStandalone) throws RemoteException {
		super(isStandalone);
		setAppletTypeC(true);
	}

	protected JComponent createAlarmComponent(
			AbstractNewApplet applet,
			String alarmDefPath) {
		return new AlarmPanel(applet, alarmDefPath);
	}

	private static class AlarmPanel extends AbstractAlarmPanel {
		private static final long serialVersionUID = 5469799141692344961L;
		private Dimension dimension;
		private JPanel mainPanel;
		private JTabbedPane tab;

		protected AlarmPanel(
				final AbstractNewApplet wifeApplet,
				String configFile) {
			super(wifeApplet, configFile);
		}

		protected void setPanelNewAlarm() {
			panelNewAlarm = getAlarmNewLine(wifeApplet);
			// 表示切替ボタン
			Box panelBut = getButton();
			panelNewAlarm.add(panelBut, BorderLayout.EAST);
		}

		protected void setPanelAlarmList() {
			tab = new JTabbedPane();
			AlarmFactory factory = new AlarmFactory();
			tab.addTab("警報一覧", factory.getAlarm(wifeApplet));

			// 検索条件
			panelAlarmList = new JPanel(new BorderLayout());
			panelAlarmList.add(getAlarmButton(), BorderLayout.NORTH);
			mainPanel = new JPanel(new BorderLayout());
			if (isNewLayout()) {
				alarmConditions = createNewAlarmConditions();
			} else {
				alarmConditions = createAlarmConditions();
			}
			mainPanel.add(alarmConditions, BorderLayout.NORTH);
			// 警報一覧
			mainPanel.add(getAlarmTable(), BorderLayout.CENTER);
			tab.addTab("検索", mainPanel);
			panelAlarmList.add(tab, BorderLayout.CENTER);
			panelAlarmList.setVisible(false);
			Box box = Box.createVerticalBox();
			box.add(panelNewAlarm);
			box.add(panelAlarmList);
			add(box, BorderLayout.CENTER);
		}

		protected void createOperationFinder(JTabbedPane tabbedPane) {
			ClientConfiguration configuration = new ClientConfiguration();
			if (configuration.getBoolean("operationlogging.addalarm", true)) {
				DefaultOperationLoggingTableModel loggingTableModel = new DefaultOperationLoggingTableModel();
				final Component loggingFinder = new OperationLoggingFinder(
						loggingTableModel);
				tabbedPane.addTab("操作ログ", null, new OperationLoggingTable(
						loggingTableModel), "操作ログ");

				tabbedPane.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JTabbedPane tab = (JTabbedPane) e.getSource();
						if (tab.getSelectedIndex() == 3) {
							mainPanel.remove(alarmConditions);
							mainPanel.add(loggingFinder, BorderLayout.NORTH);
						} else {
							mainPanel.remove(loggingFinder);
							mainPanel.add(alarmConditions, BorderLayout.NORTH);
						}
						validate();
						repaint();
					}
				});
			}
		}

		private Component getAlarmButton() {
			Box box = Box.createHorizontalBox();
			JButton button = new JButton(VIEWMODE_DW);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					changeAlarmPanel(e);
				}
			});
			box.add(Box.createHorizontalGlue());
			box.add(button);
			return box;
		}

		protected void changeAlarmPanel(ActionEvent e) {
			logger.debug("changeAlarmPanel");
			if (panelAlarmList.isVisible()) {
				panelAlarmList.setVisible(false);
				logger.info(dimension);
				panelNewAlarm.setMinimumSize(dimension);
				panelNewAlarm.setVisible(true);
				wifeApplet.mainSplit.setDividerLocation(wifeApplet.mainSplit
						.getMaximumDividerLocation());
				panelNewAlarm.setMinimumSize(Globals.ZERO_DIMENSION);
			} else {
				if (null == dimension) {
					dimension = panelNewAlarm.getPreferredSize();
				}
				panelAlarmList.setVisible(true);
				panelNewAlarm.setVisible(false);
				if (wifeApplet.configuration.getBoolean(
						"org.F11.scada.xwife.applet.typeDmode",
						false)) {
					tab.setSelectedIndex(0);
				}

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
			return factory.getAlarm(wifeApplet);
		}
	}

	// Main メソッド
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		WifeAppletD applet = null;
		try {
			applet = new WifeAppletD(true);
		} catch (RemoteException e) {
			JOptionPane.showInternalMessageDialog(
					frame,
					ServerErrorUtil.ERROR_MESSAGE,
					ServerErrorUtil.ERROR_MESSAGE,
					JOptionPane.ERROR_MESSAGE);
		}
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
		if (applet.configuration.getBoolean(
				"xwife.applet.Applet.maximized",
				false)) {
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
		frame.setVisible(true);
	}
}
