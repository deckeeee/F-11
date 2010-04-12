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
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.F11.scada.tool.conf.StreamManager;

public class ClientConfTab extends JScrollPane implements DocumentListener {
	private static final long serialVersionUID = -5360161794370392714L;

	private final Frame frameParent;
	private final StreamManager manager;

	private final JTextField initialText = new JTextField();
	private final JTextField errorText = new JTextField();
	private final JTextField treeWidth = new JTextField();
	private final JTextField treeHeight = new JTextField();
	private final JTextField dismissDelay = new JTextField();
	private final JTextField schDlgWidth = new JTextField();
	private final JTextField schDlgHeight = new JTextField();
	private final JTextField trdDlgWidth = new JTextField();
	private final JTextField trdDlgHeight = new JTextField();
	private final JTextField separateScheduleLimit = new JTextField();
	private final JTextField soundOnHolder = new JTextField();
	private final JFormattedTextField treeFontSize =
		new JFormattedTextField(new DecimalFormat("###"));
	private final JFormattedTextField soundTimerTime =
		new JFormattedTextField(new DecimalFormat("#####"));

	public ClientConfTab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));
		// シンボルの初期値表示
		mainPanel.add(new JLabel("シンボル初期値表示："));
		initialText.setText(manager
				.getClientConf("initialtext", "initial data"));
		initialText.getDocument().addDocumentListener(this);
		mainPanel.add(initialText);
		// エラー時表示
		mainPanel.add(new JLabel("シンボルエラー値表示："));
		errorText.setText(manager.getClientConf("errortext", "err.."));
		errorText.getDocument().addDocumentListener(this);
		mainPanel.add(errorText);
		// 戻る・進むボタン
		mainPanel.add(new JLabel("戻る・進むボタン："));
		JButton but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// アイコン・サイズ設定ダイアログ
				new NextPrevDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// スケジュールバーの色設定
		mainPanel.add(new JLabel("スケジュールバーの色設定："));
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// スケジュール色設定ダイアログ
				new SchColorsDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// キャッシュページを起動時にクライアントに読み込む
		mainPanel.add(new JLabel("起動時にキャッシュページを読み込む："));
		JComboBox cb = new JComboBox(new String[] { "する", "しない" });
		if ("false".equals(manager.getClientConf(
				"parser.AppletFrameDefine.receiveCache", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager
								.setClientConf(
										"parser.AppletFrameDefine.receiveCache",
										"true");
					} else {
						manager.setClientConf(
								"parser.AppletFrameDefine.receiveCache",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		/*
		 * 閉じるボタン(Xボタン)で、確認ダイアログを出さずに閉じるかどうか。 true:確認ダイアログを出さずに閉じる。
		 * false:確認ダイアログを出す。 Webブラウザでクライアントを立ち上げた時には、この機能は動作せず必ず終了します。
		 */
		mainPanel.add(new JLabel("閉じるボタン(Xボタン)で、確認ダイアログ："));
		cb = new JComboBox(new String[] { "する", "しない" });
		if ("true".equals(manager.getClientConf("xwife.applet.Applet.isClose",
				"true"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager.setClientConf("xwife.applet.Applet.isClose",
								"false");
					} else {
						manager.setClientConf("xwife.applet.Applet.isClose",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
		// 立ち上げ時にクライアントを最大化する・しない
		mainPanel.add(new JLabel("立ち上げ時にクライアントを最大化："));
		cb = new JComboBox(new String[] { "する", "しない" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.maximized", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager.setClientConf("xwife.applet.Applet.maximized",
								"true");
					} else {
						manager.setClientConf("xwife.applet.Applet.maximized",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		/*
		 * ツリー部の縦横 {treeWidth 横幅 : treeHeight 高さ} (Pixcel) 縦：最大化＆タスクバー表示無し 805
		 * 縦：最大化＆タスクバー表示有り 775 が適当なサイズ。 横幅は適当に指定してください。
		 */
		mainPanel.add(new JLabel("ツリー部サイズ："));
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JLabel("横幅：", JLabel.RIGHT));
		treeWidth.setText(manager.getClientConf(
				"xwife.applet.Applet.treeWidth", "150"));
		treeWidth.getDocument().addDocumentListener(this);
		panel.add(treeWidth);
		panel.add(new JLabel("高さ：", JLabel.RIGHT));
		treeHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.treeHeight", "775"));
		treeHeight.getDocument().addDocumentListener(this);
		panel.add(treeHeight);
		mainPanel.add(panel);
		// 警報・状態発生時にスクリーンセーバーを解除する・しない
		mainPanel.add(new JLabel("警報・状態発生時スクリーンセーバー解除："));
		cb = new JComboBox(new String[] { "する", "しない" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.screenSaver", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager.setClientConf(
								"xwife.applet.Applet.screenSaver", "true");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.screenSaver", "false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// ツールチップの最大表示時間(ミリ秒)。
		mainPanel.add(new JLabel("ツールチップの最大表示時間(ミリ秒)："));
		panel = new JPanel(new GridLayout(1, 0));
		dismissDelay.setText(manager.getClientConf(
				"xwife.applet.Applet.dismissDelay", "10000"));
		dismissDelay.getDocument().addDocumentListener(this);
		panel.add(dismissDelay);
		// ツールチップのカスタム表示方法
		JLabel label = new JLabel("表示方法：", JLabel.RIGHT);
		label.setToolTipText("[標準]ではツールチップが消えない事がある。");
		panel.add(label);
		cb = new JComboBox(new String[] { "カスタム", "標準" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.customTipLocation", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("カスタム".equals(e.getItem())) {
						manager
								.setClientConf(
										"xwife.applet.Applet.customTipLocation",
										"true");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.customTipLocation",
								"false");
					}
				}
			}
		});
		panel.add(cb);
		mainPanel.add(panel);
		// 警報音停止キー設定。初期値は F12キー
		// 警報音停止デジタルイベント設定。
		// 警報音停止デジタルイベント書き込み設定。
		mainPanel.add(new JLabel("警報音停止："));
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 警報音停止設定ダイアログ
				new AlarmStopDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// スケジュールのグループ一覧ダイアログのサイズ
		mainPanel.add(new JLabel("スケジュールグループ一覧ダイアログ："));
		panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JLabel("横幅：", JLabel.RIGHT));
		schDlgWidth.setText(manager.getClientConf(
				"xwife.applet.Applet.schedule.dialog.width", "157"));
		schDlgWidth.getDocument().addDocumentListener(this);
		panel.add(schDlgWidth);
		panel.add(new JLabel("高さ：", JLabel.RIGHT));
		schDlgHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.schedule.dialog.height", "217"));
		schDlgHeight.getDocument().addDocumentListener(this);
		panel.add(schDlgHeight);
		mainPanel.add(panel);
		// トレンドグラフのグループ一覧ダイアログのサイズ
		mainPanel.add(new JLabel("トレンドグラフグループ一覧ダイアログ："));
		panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JLabel("横幅：", JLabel.RIGHT));
		trdDlgWidth.setText(manager.getClientConf(
				"xwife.applet.Applet.trend.dialog.width", "157"));
		trdDlgWidth.getDocument().addDocumentListener(this);
		panel.add(trdDlgWidth);
		panel.add(new JLabel("高さ：", JLabel.RIGHT));
		trdDlgHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.trend.dialog.height", "217"));
		trdDlgHeight.getDocument().addDocumentListener(this);
		panel.add(trdDlgHeight);
		mainPanel.add(panel);
		// クライアントのサイズと位置
		mainPanel.add(new JLabel("クライアントのサイズと位置："));
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// クライアント配置設定ダイアログ
				new ClientLocDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// 編集可能なシンボルでマウスカーソルを手の形
		mainPanel.add(new JLabel("編集可能シンボルでマウスカーソル手形："));
		cb = new JComboBox(new String[] { "する", "しない" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.symbol.handcursor", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager
								.setClientConf(
										"xwife.applet.Applet.symbol.handcursor",
										"true");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.symbol.handcursor",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// プライオリティによる自動ジャンプの制御
		mainPanel.add(new JLabel("プライオリティによる自動ジャンプ："));
		cb = new JComboBox(new String[] { "する", "しない" });
		if ("false"
				.equals(manager.getClientConf(
						"org.F11.scada.xwife.applet.alarm.PriorityController",
						"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager
								.setClientConf(
										"org.F11.scada.xwife.applet.alarm.PriorityController",
										"true");
					} else {
						manager
								.setClientConf(
										"org.F11.scada.xwife.applet.alarm.PriorityController",
										"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// 起動時のスプラッシュ設定
		mainPanel.add(new JLabel("起動時のスプラッシュ設定："));
		but = new JButton("詳細");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// スプラッシュの有無
				// スプラッシュのタイトル
				// スプラッシュのイメージ
				new SplashDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// 確認ダイアログの有無
		label = new JLabel("確認ダイアログの有無：");
		label.setToolTipText("PLC書込みに関する全操作について。");
		mainPanel.add(label);
		cb = new JComboBox(new String[] { "する", "しない" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.applet.dialog.isConfirm", "false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager
								.setClientConf(
										"org.F11.scada.applet.dialog.isConfirm",
										"true");
					} else {
						manager.setClientConf(
								"org.F11.scada.applet.dialog.isConfirm",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		separateSchedule(mainPanel);
		separateScheduleLimit(mainPanel);
		showSortColumn(mainPanel);
		typeDmode(mainPanel);
		typeDTabSync(mainPanel);
		// imageLoader(mainPanel);
		treeFontSize(mainPanel);
		alarmTableColumn(mainPanel);
		screenShot(mainPanel);
		soundTimer(mainPanel);
		soundTimerTime(mainPanel);
		isViewWeek(mainPanel);
		soundOnHolder(mainPanel);

		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private void separateSchedule(JPanel mainPanel) {
		// スケジュール操作で個別表示の有無
		mainPanel.add(new JLabel("スケジュール操作で個別表示の有無："));
		JComboBox cb = new JComboBox(new String[] { "する", "しない" });
		if ("false".equals(manager
				.getClientConf(
						"org.F11.scada.applet.schedule.point.SeparateSchedule",
						"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager
								.setClientConf(
										"org.F11.scada.applet.schedule.point.SeparateSchedule",
										"true");
					} else {
						manager
								.setClientConf(
										"org.F11.scada.applet.schedule.point.SeparateSchedule",
										"false");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void separateScheduleLimit(JPanel mainPanel) {
		// スケジュール操作で個別表示の行数
		mainPanel.add(new JLabel("スケジュール機器一覧表示の行数"));
		JPanel panel = new JPanel(new GridLayout(1, 0));
		separateScheduleLimit.setText(manager.getClientConf(
				"org.F11.scada.applet.schedule.point.limit", "25"));
		separateScheduleLimit.getDocument().addDocumentListener(this);
		panel.add(separateScheduleLimit);
		mainPanel.add(panel);
	}

	private void showSortColumn(JPanel mainPanel) {
		mainPanel.add(new JLabel("警報一覧に種別を表示："));
		JComboBox cb = new JComboBox(new String[] { "しない", "する" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.showSortColumn", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("する".equals(e.getItem())) {
						manager
								.setClientConf(
										"org.F11.scada.xwife.applet.alarm.showSortColumn",
										"true");
					} else {
						manager
								.setClientConf(
										"org.F11.scada.xwife.applet.alarm.showSortColumn",
										"false");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void typeDmode(JPanel mainPanel) {
		JLabel label = new JLabel("TypeDモード：");
		label.setToolTipText("TypeDで▲ボタンを押下したときの表示方法。");
		mainPanel.add(label);
		JComboBox cb = new JComboBox(new String[] { "前の状態を保持", "警報一覧を表示" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.typeDmode", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("警報一覧を表示".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.typeDmode", "true");
					} else {
						manager
								.setClientConf(
										"org.F11.scada.xwife.applet.typeDmode",
										"false");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void typeDTabSync(JPanel mainPanel) {
		JLabel label = new JLabel("TypeDタブ同期：");
		label.setToolTipText("TypeD警報一覧の選択タブを同期させるか。");
		mainPanel.add(label);
		JComboBox cb = new JComboBox(new String[] { "しない", "する" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.AppletD.tabsync", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("しない".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.AppletD.tabsync",
								"false");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.AppletD.tabsync",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void treeFontSize(JPanel mainPanel) {
		// ツリーのフォントサイズ
		mainPanel.add(new JLabel("ツリーのフォントサイズ(初期値12)"));
		JPanel panel = new JPanel(new GridLayout(1, 0));
		treeFontSize.setText(manager.getClientConf(
				"org.F11.scada.xwife.applet.pagetree.font", "12"));
		treeFontSize.setInputVerifier(new NumberVerifier("数値"));
		treeFontSize.setFocusLostBehavior(JFormattedTextField.COMMIT);
		treeFontSize.getDocument().addDocumentListener(this);
		panel.add(treeFontSize);
		mainPanel.add(panel);
	}

	private void alarmTableColumn(JPanel mainPanel) {
		mainPanel.add(new JLabel("警報一覧の列幅設定"));
		JButton button = new JButton("詳細");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AlarmTableColumn(frameParent, manager).setVisible(true);
			}
		});
		mainPanel.add(button);
	}

	private void screenShot(JPanel mainPanel) {
		mainPanel.add(new JLabel("スクリーンショット設定"));
		JButton button = new JButton("詳細");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ScreenShot(frameParent, manager).setVisible(true);
			}
		});
		mainPanel.add(button);
	}

	private void soundTimer(JPanel mainPanel) {
		JLabel label = new JLabel("警報音タイマー：");
		label.setToolTipText("警報音タイマーを使用して自動停止するかの設定");
		mainPanel.add(label);
		JComboBox cb = new JComboBox(new String[] { "しない", "する" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.soundTimer", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("しない".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.soundTimer",
								"false");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.alarm.soundTimer",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void soundTimerTime(JPanel mainPanel) {
		// 警報音タイマーで停止するまでの時間
		mainPanel.add(new JLabel("警報音タイマー："));
		JPanel panel = new JPanel(new GridLayout(1, 0));
		soundTimerTime.setText(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.soundTimerTime", "5000"));
		soundTimerTime.setInputVerifier(new NumberVerifier("数値"));
		soundTimerTime.setFocusLostBehavior(JFormattedTextField.COMMIT);
		soundTimerTime.getDocument().addDocumentListener(this);
		panel.add(soundTimerTime);
		mainPanel.add(panel);
	}

	private void isViewWeek(JPanel mainPanel) {
		JLabel label = new JLabel("時計に曜日を表示：");
		label.setToolTipText("右上の時計に曜日を表示するか設定");
		mainPanel.add(label);
		JComboBox cb = new JComboBox(new String[] { "しない", "する" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.isViewWeek", "false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("しない".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.isViewWeek",
								"false");
					} else {
						manager
								.setClientConf(
										"org.F11.scada.xwife.applet.isViewWeek",
										"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void soundOnHolder(JPanel mainPanel) {
		// 警報音禁止をクライアントで共有
		mainPanel.add(new JLabel("警報音禁止をクライアントで共有："));
		JPanel panel = new JPanel(new GridLayout(1, 0));
		soundOnHolder.setText(manager.getClientConf(
				"xwife.applet.Applet.soundOnHolder", ""));
		soundOnHolder.getDocument().addDocumentListener(this);
		soundOnHolder
				.setToolTipText("共有する場合はホルダIDを入力。何も入力しない場合は従来通り、各クライアント毎で警報音禁止状態を保持します。");
		panel.add(soundOnHolder);
		mainPanel.add(panel);
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
		if (e.getDocument() == initialText.getDocument()) {
			manager.setClientConf("initialtext", initialText.getText());
		} else if (e.getDocument() == errorText.getDocument()) {
			manager.setClientConf("errortext", errorText.getText());
		} else if (e.getDocument() == treeWidth.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.treeWidth", treeWidth
					.getText());
		} else if (e.getDocument() == treeHeight.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.treeHeight", treeHeight
					.getText());
		} else if (e.getDocument() == dismissDelay.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.dismissDelay",
					dismissDelay.getText());
		} else if (e.getDocument() == schDlgWidth.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.schedule.dialog.width",
					schDlgWidth.getText());
		} else if (e.getDocument() == schDlgHeight.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.schedule.dialog.height",
					schDlgHeight.getText());
		} else if (e.getDocument() == trdDlgWidth.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.trend.dialog.width",
					trdDlgWidth.getText());
		} else if (e.getDocument() == trdDlgHeight.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.trend.dialog.height",
					trdDlgHeight.getText());
		} else if (e.getDocument() == separateScheduleLimit.getDocument()) {
			manager.setClientConf("org.F11.scada.applet.schedule.point.limit",
					separateScheduleLimit.getText());
		} else if (e.getDocument() == treeFontSize.getDocument()) {
			manager.setClientConf("org.F11.scada.xwife.applet.pagetree.font",
					treeFontSize.getText());
		} else if (e.getDocument() == soundTimerTime.getDocument()) {
			manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.soundTimerTime",
					soundTimerTime.getText());
		} else if (e.getDocument() == soundOnHolder.getDocument()) {
			manager.setClientConf("xwife.applet.Applet.soundOnHolder",
					soundOnHolder.getText());
		}
	}

	private static class NumberVerifier extends InputVerifier {
		private final String format;

		public NumberVerifier(String format) {
			this.format = format;
		}

		@Override
		public boolean verify(JComponent input) {
			if (input instanceof JFormattedTextField) {
				JFormattedTextField ftf = (JFormattedTextField) input;
				AbstractFormatter formatter = ftf.getFormatter();
				if (formatter != null) {
					String text = ftf.getText();
					try {
						formatter.stringToValue(text);
						return true;
					} catch (ParseException pe) {
						JOptionPane.showMessageDialog(ftf, format
							+ "形式で入力してください。");
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			return verify(input);
		}
	}
}
