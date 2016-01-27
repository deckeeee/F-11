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
import javax.swing.JOptionPane;
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
	private final JTextField serverUser = new JTextField();
	private final JTextField serverPass = new JTextField();
	private final JTextField mailErrorHolder = new JTextField();
	private final JTextField alarmAttributeTitle = new JTextField();

	private final JTextField clientMax = new JTextField();
	private final JTextField clientMaxPage = new JTextField();

	public PreferencesTab(Frame parent, StreamManager manager) {
		super();
		this.frameParent = parent;
		this.manager = manager;
		init();
	}

	private void init() {
		JPanel mainPanel = new JPanel(new GridLayout(0, 2));
		// �f�[�^�v���o�C�_�[�T�[�o�[�ݒ�
		mainPanel.add(new JLabel("�f�[�^�v���o�C�_�[�T�[�o�[��(IP)�F"));
		Box box = new Box(BoxLayout.X_AXIS);
		serverIp.setText(manager.getPreferences(
			"/server/rmi/managerdelegator/name",
			""));
		serverIp.getDocument().addDocumentListener(this);
		box.add(serverIp);
		JButton but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ServerDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// �R���N�V�����T�[�o�[�ݒ�
		JLabel label = new JLabel("�R���N�V�����T�[�o�[��(IP)�F");
		label.setToolTipText("Ver.2.1.1�ȍ~�ł͎g�p���Ă��܂���B");
		mainPanel.add(label);
		box = new Box(BoxLayout.X_AXIS);
		collectorIp.setText(manager.getPreferences(
			"/server/rmi/collectorserver/name",
			""));
		collectorIp.getDocument().addDocumentListener(this);
		box.add(collectorIp);
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new CollectorDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// �f�[�^�x�[�X�T�[�o�[�ݒ�
		mainPanel.add(new JLabel("�f�[�^�x�[�X�T�[�o�[��(IP)�F"));
		box = new Box(BoxLayout.X_AXIS);
		dbmsIp.setText(manager.getPreferences("/server/jdbc/servername", ""));
		dbmsIp.getDocument().addDocumentListener(this);
		box.add(dbmsIp);
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new DbmsDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// E-Mail�ݒ�
		mainPanel.add(new JLabel("���[���T�[�o�[���i�󔒂Ń��[�������j�F"));
		box = new Box(BoxLayout.X_AXIS);
		mailIp.setText(manager.getPreferences(
			"/server/mail/smtp/servername",
			""));
		mailIp.getDocument().addDocumentListener(this);
		box.add(mailIp);
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new MailDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// �x��ꗗ�󎚐ݒ�
		mainPanel.add(new JLabel("�x��ꗗ�v�����^���i�󔒂ň󎚖����j�F"));
		box = new Box(BoxLayout.X_AXIS);
		prnName.setText(manager.getPreferences(
			"/server/alarm/print/printservice",
			""));
		prnName.getDocument().addDocumentListener(this);
		box.add(prnName);
		but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PrintDialog(manager, frameParent).setVisible(true);
			}
		});
		box.add(but);
		mainPanel.add(box);
		// �f�o�C�X��`���[�g�f�B���N�g�� (DB��ǂ܂��鎞�� "")
		mainPanel.add(new JLabel("�f�o�C�X��`�Ǎ��ݐ�F"));
		JComboBox cb = new JComboBox();
		cb.addItem("�f�[�^�x�[�X");
		cb.addItem("�t�H���_�idevice/�j");
		String deviceRef = manager.getPreferences("/server/device", "");
		if (deviceRef.length() == 0) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�f�[�^�x�[�X".equals(e.getItem()))
						manager.setPreferences("/server/device", "");
					else
						manager.setPreferences("/server/device", "device");
				}
			}
		});
		mainPanel.add(cb);
		deviceRedundant(mainPanel);
		// �T�[�o�[�^�C�g��
		mainPanel.add(new JLabel("�T�[�o�[�^�C�g���F"));
		serverTitle.setText(manager.getPreferences(
			"/server/title",
			"F-11 Server"));
		serverTitle.getDocument().addDocumentListener(this);
		mainPanel.add(serverTitle);
		// �T�[�o�[�N���ҋ@����(�b)
		label = new JLabel("�T�[�o�[�N���ҋ@����(�b)�F");
		label.setToolTipText("�f�[�^�v���o�C�_�[�T�[�o�[���N������Ă���f�[�^�x�[�X�ɃA�N�Z�X���n�߂�܂ł̑ҋ@���ԁB");
		mainPanel.add(label);
		startupWait
			.setText(manager.getPreferences("/server/startup/wait", "0"));
		startupWait.getDocument().addDocumentListener(this);
		mainPanel.add(startupWait);
		// �ő僌�R�[�h��
		label = new JLabel("�ő僌�R�[�h��(�g�����h�\��)�F");
		label.setToolTipText("�g�����h�O���t�\���̎����X�V���[�h���ɕ\������f�[�^���B");
		mainPanel.add(label);
		maxrecord.setText(manager.getPreferences(
			"/server/logging/maxrecord",
			"4096"));
		maxrecord.getDocument().addDocumentListener(this);
		mainPanel.add(maxrecord);
		// �x�񗚗��̍ő�ێ�����(�q�X�g���E���� ���p)
		label = new JLabel("�ő�ێ�����(�q�X�g���E����)�F");
		label.setToolTipText("�N���C�A���g�̃q�X�g���E�����ŃX�N���[���\�Ȍ����B");
		mainPanel.add(label);
		maxalarm
			.setText(manager.getPreferences("/server/alarm/maxrow", "5000"));
		maxalarm.getDocument().addDocumentListener(this);
		mainPanel.add(maxalarm);
		// ���샍�O�����ꗗ�Ń|�C���g���̂̃v���t�B�b�N�X
		mainPanel.add(new JLabel("���샍�O �|�C���g�ڍ׋@�\�F"));
		cb = new JComboBox();
		cb.addItem("�g��Ȃ�");
		cb.addItem("�g��");
		String prefix =
			manager.getPreferences("/server/operationlog/prefix", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�g��Ȃ�".equals(e.getItem()))
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

		scheduleOpe(mainPanel);
		graphCache(mainPanel);
		pageDeployPeriod(mainPanel);
		// noRevision(mainPanel);
		operationLoggingUtil(mainPanel);
		scheduleCount(mainPanel);
		outputMode(mainPanel);
		testMode(mainPanel);
		communicateWaitTime(mainPanel);
		useFormula(mainPanel);
		soundAttributeMode(mainPanel);
		serverUser(mainPanel);
		serverPass(mainPanel);
		pageChangeInterrupt(mainPanel);
		attributeNDisplay(mainPanel);
		mailErrorHolder(mainPanel);
		alarmCsvWrite(mainPanel);
		alarmAttributeTitle(mainPanel);
		analogRoundMode(mainPanel);
		// autoFinsNode(mainPanel);
		clientMax(mainPanel);
		clientMaxPage(mainPanel);

		JPanel scPanel = new JPanel(new BorderLayout());
		scPanel.add(mainPanel, BorderLayout.NORTH);
		this.setViewportView(scPanel);
	}

	private void deviceRedundant(JPanel mainPanel) {
		// ��d��PLC�ʐM ��`�ǂݍ���
		JLabel label = new JLabel("��d��PLC�ʐM�F");
		label.setToolTipText("��d��PLC�ƒʐM�����`��ǂݍ��ށB");
		mainPanel.add(label);
		JComboBox cb = new JComboBox();
		cb.addItem("���Ȃ�");
		cb.addItem("����");
		String prefix =
			manager.getPreferences("/server/device/redundant", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("���Ȃ�".equals(e.getItem()))
						manager.setPreferences(
							"/server/device/redundant",
							"false");
					else
						manager.setPreferences(
							"/server/device/redundant",
							"true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void scheduleOpe(JPanel mainPanel) {
		// �X�P�W���[������
		JLabel label = new JLabel("�X�P�W���[������F");
		label.setToolTipText("�X�P�W���[���@��ꗗ�A�}�X�^�[=>�ʓ]���Ȃǂ̋@�\�B");
		mainPanel.add(label);
		JComboBox cb = new JComboBox();
		cb.addItem("�g��Ȃ�");
		cb.addItem("�g��");
		String prefix =
			manager.getPreferences("/server/schedulepoint", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�g��Ȃ�".equals(e.getItem()))
						manager
							.setPreferences("/server/schedulepoint", "false");
					else
						manager.setPreferences("/server/schedulepoint", "true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void graphCache(JPanel mainPanel) {
		// �O���t�y�[�W�L���b�V��
		JLabel label = new JLabel("�O���t�y�[�W�L���b�V���F");
		label.setToolTipText("�g�����h�O���t�E�o�[�O���t�̖������L���b�V���B");
		mainPanel.add(label);
		JComboBox cb = new JComboBox();
		cb.addItem("���Ȃ�");
		cb.addItem("����");
		String prefix = manager.getPreferences("/server/graphcache", "true");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("���Ȃ�".equals(e.getItem()))
						manager.setPreferences("/server/graphcache", "false");
					else
						manager.setPreferences("/server/graphcache", "true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void pageDeployPeriod(JPanel mainPanel) {
		mainPanel.add(new JLabel("�y�[�W��`�t�@�C�� �`�F�b�N�Ԋu(�~���b)�F"));
		deployPeriod.setText(manager.getPreferences(
			"/server/deploy/period",
			"69896"));
		deployPeriod.getDocument().addDocumentListener(this);
		mainPanel.add(deployPeriod);
	}

	@SuppressWarnings("unused")
	private void noRevision(JPanel mainPanel) {
		JLabel label = new JLabel("���M���O���r�W�����F");
		label.setToolTipText("��������̃��M���O�f�[�^�̃��r�W�����Ǘ��@�\");
		mainPanel.add(label);
		JComboBox cb = new JComboBox();
		cb.addItem("�L��");
		cb.addItem("����");
		String prefix =
			manager.getPreferences("/server/logging/noRevision", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�L��".equals(e.getItem()))
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
		mainPanel.add(new JLabel("�X�P�W���[�����샍�O�̃t�b�^�F"));
		operationLoggingUtil.setText(manager.getPreferences(
			"/server/operationlog/impl/OperationLoggingUtilImpl",
			""));
		operationLoggingUtil.getDocument().addDocumentListener(this);
		mainPanel.add(operationLoggingUtil);
	}

	private void scheduleCount(JPanel mainPanel) {
		JLabel label = new JLabel("�X�P�W���[�����샍�O�񐔋L�^���[�h�F");
		label.setToolTipText("���샍�O�ŁA�ҏW����ON/OFF�����̈ʒu��\������`���B[�񐔋L�^]�͏�L�t�b�^��t������B");
		mainPanel.add(label);
		JComboBox cb = new JComboBox();
		cb.addItem("�z��Y��");
		cb.addItem("�񐔋L�^");
		String prefix =
			manager
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
					if ("�z��Y��".equals(e.getItem()))
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
		mainPanel.add(new JLabel("���OCSV�o�̓��[�h�F"));
		JComboBox cb = new JComboBox();
		cb.addItem("���R�[�h�ǉ���");
		cb.addItem("������");
		String prefix =
			manager
				.getPreferences("/server/logging/report/outputMode", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("���R�[�h�ǉ���".equals(e.getItem()))
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
		mainPanel.add(new JLabel("���v�ύX���[�h�F"));
		JComboBox cb = new JComboBox();
		cb.addItem("�{��(����L��)");
		cb.addItem("�e�X�g(�������ɕύX�\)");
		String prefix =
			manager.getPreferences("/server/systemtime/testMode", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�{��(����L��)".equals(e.getItem()))
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
		JLabel label = new JLabel("�T�[�o�[PLC�� �ʐM�Ԋu(�~���b)�F");
		label.setToolTipText("���̒ʐM���𑗐M����܂ł̑ҋ@���ԁB");
		mainPanel.add(label);
		communicateWaitTime.setText(manager.getPreferences(
			"/server/communicateWaitTime",
			"100"));
		communicateWaitTime.getDocument().addDocumentListener(this);
		mainPanel.add(communicateWaitTime);
	}

	private void useFormula(JPanel mainPanel) {
		mainPanel.add(new JLabel("���z�z���_�F"));
		JComboBox cb = new JComboBox();
		cb.addItem("�g�p���Ȃ�");
		cb.addItem("�g�p����");
		String prefix =
			manager.getPreferences("/server/formula/isUseFormula", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�g�p���Ȃ�".equals(e.getItem()))
						manager.setPreferences(
							"/server/formula/isUseFormula",
							"false");
					else
						manager.setPreferences(
							"/server/formula/isUseFormula",
							"true");
				}
			}
		});
		mainPanel.add(cb);
	}

	private void soundAttributeMode(JPanel mainPanel) {
		mainPanel.add(new JLabel("�x�񉹔��M���[�h�F"));
		final JComboBox cb = new JComboBox();
		cb.addItem("�����D��(�����l)");
		cb.addItem("�|�C���g�D��");
		String prefix =
			manager.getPreferences("/server/alarm/sound/attributemode", "true");
		if ("true".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�����D��(�����l)".equals(e.getItem())) {
						manager.setPreferences(
							"/server/alarm/sound/attributemode",
							"true");
					} else {
						manager.setPreferences(
							"/server/alarm/sound/attributemode",
							"false");
					}
					JOptionPane.showMessageDialog(
						cb,
						"���[�h�ύX������͕K��rejar���ăT�[�o�[�̍ċN�������ĉ�����",
						"�x�񉹔��M���[�h�ύX",
						JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		mainPanel.add(cb);
	}

	private void serverUser(JPanel mainPanel) {
		mainPanel.add(new JLabel("�T�[�o�[�I���p���[�U�[�F"));
		serverUser.setText(manager.getPreferences("/server/user", "root"));
		serverUser.getDocument().addDocumentListener(this);
		mainPanel.add(serverUser);
	}

	private void serverPass(JPanel mainPanel) {
		mainPanel.add(new JLabel("�T�[�o�[�I���p�p�X���[�h�F"));
		serverPass.setText(manager
			.getPreferences("/server/password", "okusama"));
		serverPass.getDocument().addDocumentListener(this);
		mainPanel.add(serverPass);
	}

	private void pageChangeInterrupt(JPanel mainPanel) {
		JLabel label = new JLabel("�y�[�W�ؑ֒ʐM���[�h�F");
		label.setToolTipText("�y�[�W�ؑ֎��Ƀy�[�W�ʐM���V�X�e���ʐM�̂ǂ����D�悷��̂���ݒ肵�܂��B");
		mainPanel.add(label);
		final JComboBox cb = new JComboBox();
		cb.addItem("�y�[�W�ʐM�D��");
		cb.addItem("�V�X�e���ʐM�D��");
		String prefix =
			manager.getPreferences("/server/isPageChangeInterrupt", "true");
		if ("true".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�y�[�W�ʐM�D��".equals(e.getItem())) {
						manager.setPreferences(
							"/server/isPageChangeInterrupt",
							"true");
					} else {
						manager.setPreferences(
							"/server/isPageChangeInterrupt",
							"false");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void attributeNDisplay(JPanel mainPanel) {
		JLabel label = new JLabel("����n�\�����[�h�F");
		label.setToolTipText("����1,2,3��\�����邩���Ȃ�����ݒ肵�܂��B");
		mainPanel.add(label);
		final JComboBox cb = new JComboBox();
		cb.addItem("����n��\��");
		cb.addItem("����n�\��");
		String prefix =
			manager.getPreferences("/server/alarm/attributen/enable", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("����n��\��".equals(e.getItem())) {
						manager.setPreferences(
							"/server/alarm/attributen/enable",
							"false");
					} else {
						manager.setPreferences(
							"/server/alarm/attributen/enable",
							"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void mailErrorHolder(JPanel mainPanel) {
		JLabel comp = new JLabel("�x�񃁁[�����M�G���[�z���_�F");
		comp.setToolTipText("�x�񃁁[�����M�G���[�������A�r�b�g�𗧂Ă�f�W�^���z���_���u�v���o�C�__�z���_�v�Őݒ肵�܂��B");
		mainPanel.add(comp);
		mailErrorHolder.setText(manager.getPreferences(
			"/server/mail/errorholder",
			""));
		mailErrorHolder.getDocument().addDocumentListener(this);
		mainPanel.add(mailErrorHolder);
	}

	private void alarmCsvWrite(JPanel mainPanel) {
		// �x��ꗗ�󎚐ݒ�
		mainPanel.add(new JLabel("�x�񗚗�CSV�o�́F"));
		JButton but = new JButton("�ڍ�");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AlarmCsvDialog(manager, frameParent).setVisible(true);
			}
		});
		mainPanel.add(but);
	}

	private void alarmAttributeTitle(JPanel mainPanel) {
		JLabel comp = new JLabel("����1,2,3�u������������F");
		comp.setToolTipText("����1,2,3�u��������������J���}��؂�Őݒ肵�܂��B");
		mainPanel.add(comp);
		alarmAttributeTitle.setText(manager.getPreferences(
			"/server/alarm/attribute/title",
			"����1,����2,����3"));
		alarmAttributeTitle.getDocument().addDocumentListener(this);
		mainPanel.add(alarmAttributeTitle);
	}

	private void analogRoundMode(JPanel mainPanel) {
		JLabel label = new JLabel("�A�i���OHEX�����l�l�̌ܓ��F");
		label.setToolTipText("�A�i���OHEX�����l�̎l�̌ܓ���ݒ肵�܂��B�����l�͎l�̌ܓ����Ȃ�");
		mainPanel.add(label);
		final JComboBox cb = new JComboBox();
		cb.addItem("�l�̌ܓ����Ȃ�");
		cb.addItem("�l�̌ܓ�����");
		String prefix =
			manager.getPreferences("/server/analogRoundMode", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("�l�̌ܓ����Ȃ�".equals(e.getItem())) {
						manager.setPreferences(
							"/server/analogRoundMode",
							"false");
					} else {
						manager.setPreferences(
							"/server/analogRoundMode",
							"true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void autoFinsNode(JPanel mainPanel) {
		JLabel label = new JLabel("�������tFINS�m�[�h�A�h���X(FINS/TCP)�F");
		label
			.setToolTipText("FINS/TCP���g�p����ꍇ�A�������tFINS�m�[�h�A�h���X���g�p���邩�̗L���B�T�[�o�[��2��ȏ�̏ꍇ�A�������tFINS�m�[�h�A�h���X�͗��p�ł��܂���B");
		mainPanel.add(label);
		final JComboBox cb = new JComboBox();
		cb.addItem("���p���Ȃ�");
		cb.addItem("���p����");
		String prefix =
			manager.getPreferences("/server/isAutoFinsNode", "false");
		if ("false".equals(prefix)) {
			cb.setSelectedIndex(0);
		} else {
			cb.setSelectedIndex(1);
		}
		cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if ("���p���Ȃ�".equals(e.getItem())) {
						manager.setPreferences(
							"/server/isAutoFinsNode",
							"false");
					} else {
						manager
							.setPreferences("/server/isAutoFinsNode", "true");
					}
				}
			}
		});
		mainPanel.add(cb);
	}

	private void clientMax(JPanel mainPanel) {
		JLabel comp = new JLabel("�ײ��čő�ڑ����F");
		comp.setToolTipText("�ײ��čő�ڑ�����ݒ肵�܂��B");
		mainPanel.add(comp);
		clientMax.setText(manager.getPreferences("/server/clientMax", "10"));
		clientMax.getDocument().addDocumentListener(this);
		mainPanel.add(clientMax);
	}

	private void clientMaxPage(JPanel mainPanel) {
		JLabel comp = new JLabel("�ײ��čő�ڑ����װ�߰�ށF");
		comp.setToolTipText("�ײ��čő�ڑ����𒴂������ɕ\������A�߰��ID��ݒ肵�܂��B");
		mainPanel.add(comp);
		clientMaxPage.setText(manager.getPreferences("/server/clientMaxPage", "connectmax"));
		clientMaxPage.getDocument().addDocumentListener(this);
		mainPanel.add(clientMaxPage);
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
			manager.setPreferences(
				"/server/mail/smtp/servername",
				mailIp.getText());
		} else if (e.getDocument() == prnName.getDocument()) {
			manager.setPreferences(
				"/server/alarm/print/printservice",
				prnName.getText());
		} else if (e.getDocument() == serverTitle.getDocument()) {
			manager.setPreferences("/server/title", serverTitle.getText());
		} else if (e.getDocument() == startupWait.getDocument()) {
			manager.setPreferences(
				"/server/startup/wait",
				startupWait.getText());
		} else if (e.getDocument() == maxrecord.getDocument()) {
			manager.setPreferences(
				"/server/logging/maxrecord",
				maxrecord.getText());
		} else if (e.getDocument() == maxalarm.getDocument()) {
			manager.setPreferences("/server/alarm/maxrow", maxalarm.getText());
		} else if (e.getDocument() == deployPeriod.getDocument()) {
			manager.setPreferences(
				"/server/deploy/period",
				deployPeriod.getText());
		} else if (e.getDocument() == operationLoggingUtil.getDocument()) {
			manager.setPreferences(
				"/server/operationlog/impl/OperationLoggingUtilImpl",
				operationLoggingUtil.getText());
		} else if (e.getDocument() == communicateWaitTime.getDocument()) {
			manager.setPreferences(
				"/server/communicateWaitTime",
				communicateWaitTime.getText());
		} else if (e.getDocument() == serverUser.getDocument()) {
			manager.setPreferences("/server/user", serverUser.getText());
		} else if (e.getDocument() == serverPass.getDocument()) {
			manager.setPreferences("/server/password", serverPass.getText());
		} else if (e.getDocument() == mailErrorHolder.getDocument()) {
			manager.setPreferences(
				"/server/mail/errorholder",
				mailErrorHolder.getText());
		} else if (e.getDocument() == alarmAttributeTitle.getDocument()) {
			manager.setPreferences(
				"/server/alarm/attribute/title",
				alarmAttributeTitle.getText());
		} else if (e.getDocument() == clientMax.getDocument()) {
			manager.setPreferences("/server/clientMax", clientMax.getText());
		} else if (e.getDocument() == clientMaxPage.getDocument()) {
			manager.setPreferences("/server/clientMaxPage", clientMaxPage.getText());
		}
	}
}
