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
package org.F11.scada.tool.conf.client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.F11.scada.tool.conf.StreamManager;

public class ClientConf2Tab extends JScrollPane implements DocumentListener {
	private static final long serialVersionUID = -2517467156903094845L;

	private final Frame frameParent;
	private final StreamManager manager;

	private final JTextField operLimit = new JTextField();
	private final JTextField alarmTableHeight = new JTextField();

	public ClientConf2Tab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));

		// 操作ログ検索1ページの行数
		mainPanel.add(new JLabel("操作ログ検索表示行数："));
		operLimit.setText(manager.getClientConf("operation.limit", "20"));
		operLimit.getDocument().addDocumentListener(this);
		mainPanel.add(operLimit);
		// 操作ログを警報一覧に含めるかどうか
		mainPanel.add(new JLabel("操作ログを警報一覧に含める："));
		JComboBox cb = new JComboBox(new String[]{"する", "しない"});
		if ("true".equals(manager.getClientConf("operationlogging.addalarm",
				"true"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager.setClientConf("operationlogging.addalarm",
								"true");
					} else {
						manager.setClientConf("operationlogging.addalarm",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// タイプCの警報一覧部の高さ
		mainPanel.add(new JLabel("タイプC警報一覧部 高さ："));
		alarmTableHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.alarm.table.height", "180"));
		alarmTableHeight.getDocument().addDocumentListener(this);
		mainPanel.add(alarmTableHeight);
		// タイプCの警報一覧部の履歴表示モード (初期値は0) 0:サマリ・ヒストリ・履歴を表示(タブ有り) 1:履歴のみを表示(タブ無し)
		mainPanel.add(new JLabel("タイプC警報一覧部 履歴表示："));
		cb = new JComboBox(new String[]{"タブ有り", "タブ無し"});
		if ("0".equals(manager.getClientConf(
				"xwife.applet.Applet.alarm.table.type", "0"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("タブ有り".equals(e.getItem())) {
						manager.setClientConf(
								"xwife.applet.Applet.alarm.table.type", "0");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.alarm.table.type", "1");
					}
				}
			}
		});
		mainPanel.add(cb);
		// 警報履歴検索の初期表示範囲(日単位) (初期値は1日前)
		// 初期値で選択する項目と項目名を縦棒(|)区切で記述
		// 初期値で選択するラジオボタンを設定
		// 条件：全て-SELECTALL 発生・運転のみ-SELECTTRUE 復旧・停止のみ-SELECTFALSE
		// 確認(ヒストリのみ)：全て-SELECTALL 確認済み-SELECTTRUE 未確認-SELECTFALSE
		mainPanel.add(new JLabel("警報履歴検索初期表示："));
		JButton but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 警報履歴検索の初期表示設定ダイアログ
				new AlarmSearchDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// 操作ログ検索デジタル値の表示/非表示
		mainPanel.add(new JLabel("操作ログ検索デジタル値表示："));
		cb = new JComboBox(new String[]{"する", "しない"});
		if ("true".equals(manager.getClientConf("operation.isDisplayDigital",
				"true"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager.setClientConf("operation.isDisplayDigital",
								"true");
					} else {
						manager.setClientConf("operation.isDisplayDigital",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// 未復旧一覧の表示/非表示
		mainPanel.add(new JLabel("未復旧一覧表示："));
		cb = new JComboBox(new String[]{"する", "しない"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.occurrence", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.occurrence",
								"true");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.occurrence",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// 未確認一覧の表示/非表示
		mainPanel.add(new JLabel("未確認一覧表示："));
		cb = new JComboBox(new String[]{"する", "しない"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.noncheck", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.noncheck",
								"true");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.noncheck",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		setNewAlarmCondition(mainPanel);
		setUseNewInfoMode(mainPanel);
		
		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private void setNewAlarmCondition(JPanel mainPanel) {
		mainPanel.add(new JLabel("警報一覧検索条件表示："));
		JComboBox cb = new JComboBox(new String[]{"旧", "新"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.newalarm", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("旧".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.newalarm",
								"false");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.newalarm",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void setUseNewInfoMode(JPanel mainPanel) {
		mainPanel.add(new JLabel("最新警報表示モード："));
		JComboBox cb = new JComboBox(new String[]{"常に表示(旧)", "モード有効"});
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.AlarmStats.isUseNewInfoMode", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("常に表示(旧)".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.AlarmStats.isUseNewInfoMode",
								"false");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.AlarmStats.isUseNewInfoMode",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	public void changedUpdate(DocumentEvent e) {
		eventPaformed(e);
	}

	public void insertUpdate(DocumentEvent e) {
		eventPaformed(e);
	}

	public void removeUpdate(DocumentEvent e) {
		eventPaformed(e);
	}

	private void eventPaformed(DocumentEvent e) {
		if (e.getDocument() == operLimit.getDocument()) {
			manager.setClientConf("operation.limit", operLimit.getText());
		} else if (e.getDocument() == alarmTableHeight.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.alarm.table.height",
					alarmTableHeight.getText());
		}
	}

}
