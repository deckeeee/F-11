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
package org.F11.scada.tool.conf.pref;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.F11.scada.tool.conf.StreamManager;

public class PreferencesTab extends JScrollPane implements DocumentListener {
	private static final long serialVersionUID = 2345373469214171559L;

	private final Frame frameParent;
	private final StreamManager manager;

	private final JTextField serverIp = new JTextField();
	private final JTextField collectorIp = new JTextField();
	private final JTextField dbmsIp = new JTextField();
	private final JTextField mailIp = new JTextField();
	private final JTextField prnName = new JTextField();
	private final JTextField serverTitle = new JTextField();
	private final JTextField startupWait = new JTextField();
	private final JTextField maxrecord = new JTextField();
	private final JTextField maxalarm = new JTextField();
	private final JTextField deployPeriod = new JTextField();
	private final JTextField operationLoggingUtil = new JTextField();
	private final JTextField communicateWaitTime = new JTextField();

	public PreferencesTab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));
		// データプロバイダーサーバー設定
		mainPanel.add(new JLabel("データプロバイダーサーバー名(IP)："));
		Box box = new Box(BoxLayout.X_AXIS);
		serverIp.setText(manager.getPreferences(
				"/server/rmi/managerdelegator/name",
				""));
		serverIp.getDocument().addDocumentListener(this);
		box.add(serverIp);
		JButton but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ServerDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// コレクションサーバー設定
		mainPanel.add(new JLabel("コレクションサーバー名(IP)："));
		box = new Box(BoxLayout.X_AXIS);
		collectorIp.setText(manager.getPreferences(
				"/server/rmi/collectorserver/name",
				""));
		collectorIp.getDocument().addDocumentListener(this);
		box.add(collectorIp);
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CollectorDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// データベースサーバー設定
		mainPanel.add(new JLabel("データベースサーバー名(IP)："));
		box = new Box(BoxLayout.X_AXIS);
		dbmsIp.setText(manager.getPreferences("/server/jdbc/servername", ""));
		dbmsIp.getDocument().addDocumentListener(this);
		box.add(dbmsIp);
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DbmsDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// E-Mail設定
		mainPanel.add(new JLabel("メールサーバー名（空白でメール無し）："));
		box = new Box(BoxLayout.X_AXIS);
		mailIp.setText(manager.getPreferences(
				"/server/mail/smtp/servername",
				""));
		mailIp.getDocument().addDocumentListener(this);
		box.add(mailIp);
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MailDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// 警報一覧印字設定
		mainPanel.add(new JLabel("警報一覧プリンタ名（空白で印字無し）："));
		box = new Box(BoxLayout.X_AXIS);
		prnName.setText(manager.getPreferences(
				"/server/alarm/print/printservice",
				""));
		prnName.getDocument().addDocumentListener(this);
		box.add(prnName);
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PrintDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// デバイス定義ルートディレクトリ (DBを読ませる時は "")
		mainPanel.add(new JLabel("デバイス定義読込み先："));
		JComboBox cb = new JComboBox();
		cb.addItem("データベース");
		cb.addItem("フォルダ（device/）");
		String deviceRef = manager.getPreferences("/server/device", "");
		if (deviceRef.length() == 0) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("データベース".equals(e.getItem()))
						manager.setPreferences("/server/device", "");
					else
						manager.setPreferences("/server/device", "device");
				}
			}
		});
		mainPanel.add(cb);
		// サーバータイトル
		mainPanel.add(new JLabel("サーバータイトル："));
		serverTitle.setText(manager.getPreferences(
				"/server/title",
				"F-11 Server"));
		serverTitle.getDocument().addDocumentListener(this);
		mainPanel.add(serverTitle);
		// サーバー起動待機時間(秒)
		mainPanel.add(new JLabel("サーバー起動待機時間(秒)："));
		startupWait
				.setText(manager.getPreferences("/server/startup/wait", "0"));
		startupWait.getDocument().addDocumentListener(this);
		mainPanel.add(startupWait);
		// 最大レコード数
		mainPanel.add(new JLabel("最大レコード数(トレンド表示)："));
		maxrecord.setText(manager.getPreferences(
				"/server/logging/maxrecord",
				"4096"));
		maxrecord.getDocument().addDocumentListener(this);
		mainPanel.add(maxrecord);
		// 警報履歴の最大保持件数(ヒストリ・履歴 共用)
		mainPanel.add(new JLabel("最大保持件数(ヒストリ・履歴)："));
		maxalarm
				.setText(manager.getPreferences("/server/alarm/maxrow", "5000"));
		maxalarm.getDocument().addDocumentListener(this);
		mainPanel.add(maxalarm);
		// 操作ログ検索一覧でポイント名称のプレフィックス
		mainPanel.add(new JLabel("操作ログポイント名称プレフィックス："));
		cb = new JComboBox();
		cb.addItem("false");
		cb.addItem("true");
		String prefix = manager.getPreferences(
				"/server/operationlog/prefix",
				"false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("false".equals(e.getItem()))
						manager.setPreferences(
								"/server/operationlog/prefix",
								"false");
					else
						manager.setPreferences(
								"/server/operationlog/prefix",
								"true");
				}
			}
		});
		mainPanel.add(cb);

		mainPanel.add(new JLabel("その他："));
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EtcDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);

		scheduleOpe(mainPanel);
		graphCache(mainPanel);
		pageDeployPeriod(mainPanel);
		noRevision(mainPanel);
		operationLoggingUtil(mainPanel);
		scheduleCount(mainPanel);
		outputMode(mainPanel);
		testMode(mainPanel);
		communicateWaitTime(mainPanel);
		useFormula(mainPanel);

		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private void scheduleOpe(JPanel mainPanel) {
		// スケジュール操作
		mainPanel.add(new JLabel("スケジュール操作："));
		JComboBox cb = new JComboBox();
		cb.addItem("false");
		cb.addItem("true");
		String prefix = manager
				.getPreferences("/server/schedulepoint", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("false".equals(e.getItem()))
						manager
								.setPreferences(
										"/server/schedulepoint",
										"false");
					else
						manager.setPreferences("/server/schedulepoint", "true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void graphCache(JPanel mainPanel) {
		// グラフページキャッシュ
		mainPanel.add(new JLabel("グラフページキャッシュ："));
		JComboBox cb = new JComboBox();
		cb.addItem("false");
		cb.addItem("true");
		String prefix = manager.getPreferences("/server/graphcache", "true");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("false".equals(e.getItem()))
						manager.setPreferences("/server/graphcache", "false");
					else
						manager.setPreferences("/server/graphcache", "true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void pageDeployPeriod(JPanel mainPanel) {
		mainPanel.add(new JLabel("ページ定義ファイル チェック間隔(ミリ秒)："));
		deployPeriod.setText(manager.getPreferences(
				"/server/deploy/period",
				"69896"));
		deployPeriod.getDocument().addDocumentListener(this);
		mainPanel.add(deployPeriod);
	}

	private void noRevision(JPanel mainPanel) {
		mainPanel.add(new JLabel("ロギングリビジョン："));
		JComboBox cb = new JComboBox();
		cb.addItem("有り");
		cb.addItem("無し");
		String prefix = manager.getPreferences(
				"/server/logging/noRevision",
				"false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("有り".equals(e.getItem()))
						manager.setPreferences(
								"/server/logging/noRevision",
								"false");
					else
						manager.setPreferences(
								"/server/logging/noRevision",
								"true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void operationLoggingUtil(JPanel mainPanel) {
		mainPanel.add(new JLabel("スケジュール操作ログのフッタ："));
		operationLoggingUtil.setText(manager.getPreferences(
				"/server/operationlog/impl/OperationLoggingUtilImpl",
				""));
		operationLoggingUtil.getDocument().addDocumentListener(this);
		mainPanel.add(operationLoggingUtil);
	}

	private void scheduleCount(JPanel mainPanel) {
		mainPanel.add(new JLabel("スケジュール操作ログ回数記録モード："));
		JComboBox cb = new JComboBox();
		cb.addItem("配列添字");
		cb.addItem("回数記録");
		String prefix = manager
				.getPreferences(
						"/server/operationlog/impl/OperationLoggingUtilImpl/scheduleCount",
						"false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("配列添字".equals(e.getItem()))
						manager
								.setPreferences(
										"/server/operationlog/impl/OperationLoggingUtilImpl/scheduleCount",
										"false");
					else
						manager
								.setPreferences(
										"/server/operationlog/impl/OperationLoggingUtilImpl/scheduleCount",
										"true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void outputMode(JPanel mainPanel) {
		mainPanel.add(new JLabel("ログCSV出力モード："));
		JComboBox cb = new JComboBox();
		cb.addItem("レコード追加時");
		cb.addItem("毎正時");
		String prefix = manager.getPreferences(
				"/server/logging/report/outputMode",
				"false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("レコード追加時".equals(e.getItem()))
						manager.setPreferences(
								"/server/logging/report/outputMode",
								"false");
					else
						manager.setPreferences(
								"/server/logging/report/outputMode",
								"true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void testMode(JPanel mainPanel) {
		mainPanel.add(new JLabel("時計変更モード："));
		JComboBox cb = new JComboBox();
		cb.addItem("本番(制約有り)");
		cb.addItem("テスト(無制限に変更可能)");
		String prefix = manager.getPreferences(
				"/server/systemtime/testMode",
				"false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("本番".equals(e.getItem()))
						manager.setPreferences(
								"/server/systemtime/testMode",
								"false");
					else
						manager.setPreferences(
								"/server/systemtime/testMode",
								"true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void communicateWaitTime(JPanel mainPanel) {
		mainPanel.add(new JLabel("サーバーPLC間 通信間隔(ミリ秒)："));
		communicateWaitTime.setText(manager.getPreferences(
				"/server/communicateWaitTime",
				"100"));
		communicateWaitTime.getDocument().addDocumentListener(this);
		mainPanel.add(communicateWaitTime);
	}

	private void useFormula(JPanel mainPanel) {
		mainPanel.add(new JLabel("仮想ホルダ："));
		JComboBox cb = new JComboBox();
		cb.addItem("使用しない");
		cb.addItem("使用する");
		String prefix = manager.getPreferences(
				"/server/formula/isUseFormula",
				"false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("使用しない".equals(e.getItem()))
						manager.setPreferences(
								"/server/systemtime/testMode",
								"false");
					else
						manager.setPreferences(
								"/server/systemtime/testMode",
								"true");
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
		if (e.getDocument() == serverIp.getDocument()) {
			manager.setPreferences(
					"/server/rmi/managerdelegator/name",
					serverIp.getText());
		} else if (e.getDocument() == collectorIp.getDocument()) {
			manager.setPreferences(
					"/server/rmi/collectorserver/name",
					collectorIp.getText());
		} else if (e.getDocument() == dbmsIp.getDocument()) {
			manager.setPreferences("/server/jdbc/servername", dbmsIp.getText());
		} else if (e.getDocument() == mailIp.getDocument()) {
			manager.setPreferences("/server/mail/smtp/servername", mailIp
					.getText());
		} else if (e.getDocument() == prnName.getDocument()) {
			manager.setPreferences("/server/alarm/print/printservice", prnName
					.getText());
		} else if (e.getDocument() == serverTitle.getDocument()) {
			manager.setPreferences("/server/title", serverTitle.getText());
		} else if (e.getDocument() == startupWait.getDocument()) {
			manager.setPreferences("/server/startup/wait", startupWait
					.getText());
		} else if (e.getDocument() == maxrecord.getDocument()) {
			manager.setPreferences("/server/logging/maxrecord", maxrecord
					.getText());
		} else if (e.getDocument() == maxalarm.getDocument()) {
			manager.setPreferences("/server/alarm/maxrow", maxalarm.getText());
		} else if (e.getDocument() == deployPeriod.getDocument()) {
			manager.setPreferences("/server/deploy/period", deployPeriod
					.getText());
		} else if (e.getDocument() == operationLoggingUtil.getDocument()) {
			manager.setPreferences(
					"/server/operationlog/impl/OperationLoggingUtilImpl",
					operationLoggingUtil.getText());
		} else if (e.getDocument() == communicateWaitTime.getDocument()) {
			manager.setPreferences(
					"/server/communicateWaitTime",
					communicateWaitTime.getText());
		}
	}
}
