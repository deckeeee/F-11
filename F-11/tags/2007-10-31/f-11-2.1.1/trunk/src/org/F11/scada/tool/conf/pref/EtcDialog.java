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
package org.F11.scada.tool.conf.pref;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;
import org.apache.log4j.Logger;

public class EtcDialog extends JDialog {
	static final long serialVersionUID = -1285878692195628915L;
	static final Logger log = Logger.getLogger(EtcDialog.class);

	// private final StreamManager manager;

	private final JTextField policymap = new JTextField();
	private final JTextField auth = new JTextField();
	private final JTextField saxdriver = new JTextField();
	private final JTextField frameedit = new JTextField();
	private final JTextField autoprint = new JTextField();

	public EtcDialog(final StreamManager manager, Frame parent) {
		super(parent, "���̑��ݒ�", true);
		// this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		// PolicyMap �����N���X�ݒ�
		JLabel label = new JLabel("PolicyMap �����N���X�ݒ�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		policymap.setText(manager.getPreferences("/server/policy/policyMap",
				"org.F11.scada.security.postgreSQL.PostgreSQLPolicyMap"));
		policymap.setEditable(false);
		panel.add(policymap);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(policymap, c);
		// ���[�U�[�F�؃N���X�ݒ�
		label = new JLabel("���[�U�[�F�؃N���X�ݒ�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		auth.setText(manager.getPreferences("/server/policy/authentication",
				"org.F11.scada.security.postgreSQL.PostgreSQLAuthentication"));
		auth.setEditable(false);
		panel.add(auth);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(auth, c);
		// SAX���[�_�[�����N���X
		label = new JLabel("SAX���[�_�[�����N���X�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		saxdriver.setText(manager.getPreferences("/org.xml.sax.driver", ""));
		saxdriver.setEditable(false);
		panel.add(saxdriver);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(saxdriver, c);
		// �y�[�W��`�ҏW�n���h���N���X
		// �����w�肵�Ȃ��ꍇ�� FrameDefineManager ���w�肳�ꂽ�Ƃ��Ĉ����܂�
		// FrameDefineManager : /resources/XWifeApplet.xml�̂�
		// XmlFrameDefineManager : pagedefine �ȉ��̃y�[�W��`�t�@�C��
		label = new JLabel("�y�[�W��`�ҏW�n���h���F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		frameedit.setText(manager.getPreferences("/server/FrameEditHandler",
				"FrameDefineManager"));
		frameedit.setEditable(false);
		panel.add(frameedit);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(frameedit, c);
		// ��������Ŏg�p����N���X
		// �f�t�H���g����N���X org.F11.scada.xwife.server.AutoPrintPanel
		// ���݂��Ȃ��N���X���w�肵���ꍇ�́A�f�t�H���g�̎�������N���X(Excel)���g�p����܂��B
		label = new JLabel("��������N���X�F");
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		gridbag.setConstraints(label, c);
		autoprint.setText(manager.getPreferences("/server/autoprint",
				"org.F11.scada.xwife.server.AutoPrintPanel"));
		autoprint.setEditable(false);
		panel.add(autoprint);
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		gridbag.setConstraints(autoprint, c);

		mainPanel.add(panel, BorderLayout.CENTER);

		panel = new JPanel(new GridLayout(1, 0));
		JButton but = new JButton("�n�j");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		panel.add(but);
		but = new JButton("�L�����Z��");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_cansel();
			}
		});
		panel.add(but);
		mainPanel.add(panel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel);
		pack();
		setLocationRelativeTo(parent);
	}

	private void push_ok() {
		log.debug("push_ok");

		// manager.setPreferences("/server/policy/policyMap",
		// policymap.getText());
		// manager.setPreferences("/server/policy/authentication",
		// auth.getText());
		// manager.setPreferences("/org.xml.sax.driver", saxdriver.getText());
		// manager.setPreferences("/server/FrameEditHandler",
		// frameedit.getText());
		// manager.setPreferences("/server/autoprint", autoprint.getText());

		dispose();
	}
	private void push_cansel() {
		dispose();
	}
}