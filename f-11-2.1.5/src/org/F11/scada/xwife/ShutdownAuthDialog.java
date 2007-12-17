/*
 * =============================================================================
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
 *
 */

package org.F11.scada.xwife;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ShutdownAuthDialog extends JDialog {
	private static final long serialVersionUID = -5610588934824601751L;
	/** ���[�U�[�t�B�[���h */
	private JTextField userField;
	/** �p�X���[�h�t�B�[���h */
	private JPasswordField passField;

	JPasswordField getPassField() {
		return passField;
	}

	JTextField getUserField() {
		return userField;
	}

	/**
	 * �F�؃_�C�A���O�����������ăC���X�^���X�𐶐����܂��B
	 * @param frame �e�t���[���̎Q��
	 */
	public ShutdownAuthDialog(Frame frame) {
		super(frame, "�V�X�e���I���F�؃_�C�A���O", true);
		init();
		pack();
		Dimension dialogDim = getSize();
		dialogDim.width = 230;
		setSize(dialogDim);
		Dimension frameDim = frame.getSize();
		Dimension screenSize = getToolkit().getScreenSize();
		Point location = frame.getLocation();
		location.translate(
			(frameDim.width - dialogDim.width) / 2,
			(frameDim.height - dialogDim.height) / 2);
		location.x = Math.max(0, Math.min(location.x, screenSize.width - getSize().width));
		location.y = Math.max(0, Math.min(location.y, screenSize.height - getSize().height));
		setLocation(location.x, location.y);
		show();
	}

	/**
	 * ����������
	 */
	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Container cont = getContentPane();

		cont.setLayout(new GridLayout(3, 2));
		JLabel nameLabel = new JLabel("UserName : ");
		JLabel passLabel = new JLabel("Password : ");
		userField = new JTextField();
		passField = new JPasswordField();
		passField.setEchoChar('*');
		JButton ok = new JButton("OK");
		addActionListener(ok, getOkActionListener());
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new CancelListener(this));

		cont.add(nameLabel);
		cont.add(userField);
		cont.add(passLabel);
		cont.add(passField);
		cont.add(ok);
		cont.add(cancel);
	}

	void addActionListener(JButton ok, ActionListener listener) {
		ok.addActionListener(listener);
	}
	
	private ActionListener getOkActionListener() {
		return new OkActionListener(this);
	}

	/**
	 * <p>OK �{�^�����������ꂽ���Ɏ��s����郊�X�i�[�N���X�ł��B
	 * <p>�F�؃��W�b�N�� checkAuthentication ���\�b�h�ɁA���[�U�[���ƃp�X���[�h��
	 * �����œn���܂��B
	 */
	private class OkActionListener implements ActionListener {
		ShutdownAuthDialog dlg;

		OkActionListener(ShutdownAuthDialog dlg) {
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent evt) {
			char[] pass = new char[] { 'o', 'k', 'u', 's', 'a', 'm', 'a' };
			if (dlg.getUserField().getText().equals("root")
				&& Arrays.equals(dlg.getPassField().getPassword(), pass)) {
				System.exit(0);
			} else {
				JOptionPane.showMessageDialog(
					dlg,
					"���[�U�[�����p�X���[�h��������܂���B\n�p�X���[�h�͑啶���E�������̋�ʂ����܂��B",
					"�F�؎��s",
					JOptionPane.ERROR_MESSAGE);
			}
			dlg.dispose();
		}
	}

	private class CancelListener implements ActionListener {
		ShutdownAuthDialog dlg;

		CancelListener(ShutdownAuthDialog dlg) {
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent evt) {
			dlg.dispose();
		}
	}
}
