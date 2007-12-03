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

	public ClientConfTab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));
		// �V���{���̏����l�\��
		mainPanel.add(new JLabel("�V���{�������l�\���F"));
		initialText.setText(manager
				.getClientConf("initialtext", "initial data"));
		initialText.getDocument().addDocumentListener(this);
		mainPanel.add(initialText);
		// �G���[���\��
		mainPanel.add(new JLabel("�V���{���G���[�l�\���F"));
		errorText.setText(manager.getClientConf("errortext", "err.."));
		errorText.getDocument().addDocumentListener(this);
		mainPanel.add(errorText);
		// �߂�E�i�ރ{�^��
		mainPanel.add(new JLabel("�߂�E�i�ރ{�^���F"));
		JButton but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �A�C�R���E�T�C�Y�ݒ�_�C�A���O
				new NextPrevDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// �X�P�W���[���o�[�̐F�ݒ�
		mainPanel.add(new JLabel("�X�P�W���[���o�[�̐F�ݒ�F"));
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �X�P�W���[���F�ݒ�_�C�A���O
				new SchColorsDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// �L���b�V���y�[�W���N�����ɃN���C�A���g�ɓǂݍ���
		mainPanel.add(new JLabel("�N�����ɃL���b�V���y�[�W��ǂݍ��ށF"));
		JComboBox cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("false".equals(manager.getClientConf(
				"parser.AppletFrameDefine.receiveCache",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
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
		 * ����{�^��(X�{�^��)�ŁA�m�F�_�C�A���O���o�����ɕ��邩�ǂ����B true:�m�F�_�C�A���O���o�����ɕ���B
		 * false:�m�F�_�C�A���O���o���B Web�u���E�U�ŃN���C�A���g�𗧂��グ�����ɂ́A���̋@�\�͓��삹���K���I�����܂��B
		 */
		mainPanel.add(new JLabel("����{�^��(X�{�^��)�ŁA�m�F�_�C�A���O�F"));
		cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("true".equals(manager.getClientConf(
				"xwife.applet.Applet.isClose",
				"true"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
								"xwife.applet.Applet.isClose",
								"false");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.isClose",
								"true");
					}
				}
			}
		});
		mainPanel.add(cb);
		// �����グ���ɃN���C�A���g���ő剻����E���Ȃ�
		mainPanel.add(new JLabel("�����グ���ɃN���C�A���g���ő剻�F"));
		cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.maximized",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
								"xwife.applet.Applet.maximized",
								"true");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.maximized",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		/*
		 * �c���[���̏c�� {treeWidth ���� : treeHeight ����} (Pixcel) �c�F�ő剻���^�X�N�o�[�\������ 805
		 * �c�F�ő剻���^�X�N�o�[�\���L�� 775 ���K���ȃT�C�Y�B �����͓K���Ɏw�肵�Ă��������B
		 */
		mainPanel.add(new JLabel("�c���[���T�C�Y�F"));
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JLabel("�����F", JLabel.RIGHT));
		treeWidth.setText(manager.getClientConf(
				"xwife.applet.Applet.treeWidth",
				"150"));
		treeWidth.getDocument().addDocumentListener(this);
		panel.add(treeWidth);
		panel.add(new JLabel("�����F", JLabel.RIGHT));
		treeHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.treeHeight",
				"775"));
		treeHeight.getDocument().addDocumentListener(this);
		panel.add(treeHeight);
		mainPanel.add(panel);
		// �x��E��Ԕ������ɃX�N���[���Z�[�o�[����������E���Ȃ�
		mainPanel.add(new JLabel("�x��E��Ԕ������X�N���[���Z�[�o�[�����F"));
		cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.screenSaver",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
								"xwife.applet.Applet.screenSaver",
								"true");
					} else {
						manager.setClientConf(
								"xwife.applet.Applet.screenSaver",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
		// �c�[���`�b�v�̍ő�\������(�~���b)�B
		mainPanel.add(new JLabel("�c�[���`�b�v�̍ő�\������(�~���b)�F"));
		panel = new JPanel(new GridLayout(1, 0));
		dismissDelay.setText(manager.getClientConf(
				"xwife.applet.Applet.dismissDelay",
				"10000"));
		dismissDelay.getDocument().addDocumentListener(this);
		panel.add(dismissDelay);
		// �c�[���`�b�v�̃J�X�^���\�����@
		panel.add(new JLabel("�\�����@�F", JLabel.RIGHT));
		cb = new JComboBox(new String[] { "�J�X�^��", "�W��" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.customTipLocation",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�J�X�^��".equals(e.getItem())) {
						manager.setClientConf(
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
		// �x�񉹒�~�L�[�ݒ�B�����l�� F12�L�[
		// �x�񉹒�~�f�W�^���C�x���g�ݒ�B
		// �x�񉹒�~�f�W�^���C�x���g�������ݐݒ�B
		mainPanel.add(new JLabel("�x�񉹒�~�F"));
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �x�񉹒�~�ݒ�_�C�A���O
				new AlarmStopDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// �X�P�W���[���̃O���[�v�ꗗ�_�C�A���O�̃T�C�Y
		mainPanel.add(new JLabel("�X�P�W���[���O���[�v�ꗗ�_�C�A���O�F"));
		panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JLabel("�����F", JLabel.RIGHT));
		schDlgWidth.setText(manager.getClientConf(
				"xwife.applet.Applet.schedule.dialog.width",
				"157"));
		schDlgWidth.getDocument().addDocumentListener(this);
		panel.add(schDlgWidth);
		panel.add(new JLabel("�����F", JLabel.RIGHT));
		schDlgHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.schedule.dialog.height",
				"217"));
		schDlgHeight.getDocument().addDocumentListener(this);
		panel.add(schDlgHeight);
		mainPanel.add(panel);
		// �g�����h�O���t�̃O���[�v�ꗗ�_�C�A���O�̃T�C�Y
		mainPanel.add(new JLabel("�g�����h�O���t�O���[�v�ꗗ�_�C�A���O�F"));
		panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JLabel("�����F", JLabel.RIGHT));
		trdDlgWidth.setText(manager.getClientConf(
				"xwife.applet.Applet.trend.dialog.width",
				"157"));
		trdDlgWidth.getDocument().addDocumentListener(this);
		panel.add(trdDlgWidth);
		panel.add(new JLabel("�����F", JLabel.RIGHT));
		trdDlgHeight.setText(manager.getClientConf(
				"xwife.applet.Applet.trend.dialog.height",
				"217"));
		trdDlgHeight.getDocument().addDocumentListener(this);
		panel.add(trdDlgHeight);
		mainPanel.add(panel);
		// �N���C�A���g�̃T�C�Y�ƈʒu
		mainPanel.add(new JLabel("�N���C�A���g�̃T�C�Y�ƈʒu�F"));
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �N���C�A���g�z�u�ݒ�_�C�A���O
				new ClientLocDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// �ҏW�\�ȃV���{���Ń}�E�X�J�[�\������̌`
		mainPanel.add(new JLabel("�ҏW�\�V���{���Ń}�E�X�J�[�\����`�F"));
		cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("false".equals(manager.getClientConf(
				"xwife.applet.Applet.symbol.handcursor",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
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
		// �v���C�I���e�B�ɂ�鎩���W�����v�̐���
		mainPanel.add(new JLabel("�v���C�I���e�B�ɂ�鎩���W�����v�F"));
		cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.PriorityController",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
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
		// �N�����̃X�v���b�V���ݒ�
		mainPanel.add(new JLabel("�N�����̃X�v���b�V���ݒ�F"));
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// �X�v���b�V���̗L��
				// �X�v���b�V���̃^�C�g��
				// �X�v���b�V���̃C���[�W
				new SplashDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
		// �m�F�_�C�A���O�̗L��
		mainPanel.add(new JLabel("�m�F�_�C�A���O�̗L���F"));
		cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.applet.dialog.isConfirm",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
						manager.setClientConf(
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
		imageLoader(mainPanel);

		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private void separateSchedule(JPanel mainPanel) {
		// �X�P�W���[������Ōʕ\���̗L��
		mainPanel.add(new JLabel("�X�P�W���[������Ōʕ\���̗L���F"));
		JComboBox cb = new JComboBox(new String[] { "����", "���Ȃ�" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.applet.schedule.point.SeparateSchedule",
				"false"))) {
			cb.setSelectedIndex(1);
		} else {
			cb.setSelectedIndex(0);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
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
		// �X�P�W���[������Ōʕ\���̍s��
		mainPanel.add(new JLabel("�X�P�W���[���@��ꗗ�\���̍s��"));
		JPanel panel = new JPanel(new GridLayout(1, 0));
		separateScheduleLimit.setText(manager.getClientConf(
				"org.F11.scada.applet.schedule.point.limit",
				"25"));
		separateScheduleLimit.getDocument().addDocumentListener(this);
		panel.add(separateScheduleLimit);
		mainPanel.add(panel);
	}

	private void showSortColumn(JPanel mainPanel) {
		mainPanel.add(new JLabel("�x��ꗗ�Ɏ�ʂ�\���F"));
		JComboBox cb = new JComboBox(new String[] { "���Ȃ�", "����" });
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.alarm.showSortColumn",
				"false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
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
		mainPanel.add(new JLabel("TypeD���[�h�F"));
		JComboBox cb = new JComboBox(new String[] { "�O�̏�Ԃ�ێ�", "�x��ꗗ��\��" });
		cb.setToolTipText("TypeD�Ł��{�^�������������Ƃ��̕\�����@��I���B");
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.typeDmode",
				"false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�x��ꗗ��\��".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.typeDmode",
								"true");
					} else {
						manager.setClientConf(
								"org.F11.scada.xwife.applet.typeDmode",
								"false");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void typeDTabSync(JPanel mainPanel) {
		mainPanel.add(new JLabel("TypeD�^�u�����F"));
		JComboBox cb = new JComboBox(new String[] { "����", "�L��" });
		cb.setToolTipText("TypeD�x��ꗗ�̕\�����@��I���B");
		if ("false".equals(manager.getClientConf(
				"org.F11.scada.xwife.applet.AppletD.tabsync",
				"false"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����".equals(e.getItem())) {
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

	private void imageLoader(JPanel mainPanel) {
		mainPanel.add(new JLabel("�摜�Ǎ��F"));
		JComboBox cb = new JComboBox(new String[] { "�V�`��(BMP��)", "���`��" });
		cb.setToolTipText("�摜�t�@�C���Ǎ����@��I���B");
		if ("true".equals(manager.getClientConf(
				"org.F11.scada.applet.symbol.GraphicManager",
				"true"))) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("���`��".equals(e.getItem())) {
						manager.setClientConf(
								"org.F11.scada.applet.symbol.GraphicManager",
								"false");
					} else {
						manager.setClientConf(
								"org.F11.scada.applet.symbol.GraphicManager",
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
			manager.setClientConf(
					"xwife.applet.Applet.dismissDelay",
					dismissDelay.getText());
		} else if (e.getDocument() == schDlgWidth.getDocument()) {
			manager.setClientConf(
					"xwife.applet.Applet.schedule.dialog.width",
					schDlgWidth.getText());
		} else if (e.getDocument() == schDlgHeight.getDocument()) {
			manager.setClientConf(
					"xwife.applet.Applet.schedule.dialog.height",
					schDlgHeight.getText());
		} else if (e.getDocument() == trdDlgWidth.getDocument()) {
			manager.setClientConf(
					"xwife.applet.Applet.trend.dialog.width",
					trdDlgWidth.getText());
		} else if (e.getDocument() == trdDlgHeight.getDocument()) {
			manager.setClientConf(
					"xwife.applet.Applet.trend.dialog.height",
					trdDlgHeight.getText());
		} else if (e.getDocument() == separateScheduleLimit.getDocument()) {
			manager.setClientConf(
					"org.F11.scada.applet.schedule.point.limit",
					separateScheduleLimit.getText());
		}
	}

}
