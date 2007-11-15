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
		super(parent, "その他設定", true);
		// this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel(new BorderLayout());
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		JPanel panel = new JPanel(gridbag);
		// PolicyMap 生成クラス設定
		JLabel label = new JLabel("PolicyMap 生成クラス設定：");
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
		// ユーザー認証クラス設定
		label = new JLabel("ユーザー認証クラス設定：");
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
		// SAXリーダー実装クラス
		label = new JLabel("SAXリーダー実装クラス：");
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
		// ページ定義編集ハンドラクラス
		// 何も指定しない場合は FrameDefineManager が指定されたとして扱います
		// FrameDefineManager : /resources/XWifeApplet.xmlのみ
		// XmlFrameDefineManager : pagedefine 以下のページ定義ファイル
		label = new JLabel("ページ定義編集ハンドラ：");
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
		// 自動印刷で使用するクラス
		// デフォルト印刷クラス org.F11.scada.xwife.server.AutoPrintPanel
		// 存在しないクラスを指定した場合は、デフォルトの自動印刷クラス(Excel)が使用されます。
		label = new JLabel("自動印刷クラス：");
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
		JButton but = new JButton("ＯＫ");
		but.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				push_ok();
			}
		});
		panel.add(but);
		but = new JButton("キャンセル");
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