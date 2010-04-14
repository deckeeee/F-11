package org.F11.scada.security.auth.login;

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
 *
 */

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

import org.F11.scada.WifeUtilities;
import org.F11.scada.security.AccessControlable;
import org.F11.scada.security.WifePrincipal;
import org.F11.scada.security.auth.Crypt;
import org.F11.scada.security.auth.Subject;

/**
 * <p>
 * ���p���[�U�[��ύX����Ƃ��Ɏg�p����A�F�؃_�C�A���O�N���X�ł��B
 * <p>
 * postgreSQL �e�[�u���ɒ�`���ꂽ�A���[�U�[�Ɗ֘A�Â���ꂽ �O���[�v�� Subject (�Ɗ֘A�Â���ꂽ�v�����V�p��)�𐶐����܂��B
 * <p>
 * �p�X���[�h���͂�5�񎸔s����ƔF�؏����𒆒f���܂��B
 * 
 * @todo ���[�U�[�̃��b�N�A���s�\�񐔂̕ύX
 */
public class AuthenticationDialog extends JDialog {
	private static final long serialVersionUID = 3082150358612121359L;
	/** �ύX�Ώۂ� Subject */
	private Subject subject;
	/** ���[�U�[�t�B�[���h */
	private JTextField userField;
	/** �p�X���[�h�t�B�[���h */
	private JPasswordField passField;
	/** �A�N�Z�X����R���g���[���ւ̃����[�g�Q�� */
	private AccessControlable accessControl;
	/** �ő�F�؎��s�� */
	private static final int MAX_AUTHENTICATION_FAIL = 5;
	/** �F�؎��s�� */
	private int authenticationFail;

	/**
	 * �F�؃_�C�A���O�����������ăC���X�^���X�𐶐����܂��B
	 * 
	 * @param frame �e�t���[���̎Q��
	 */
	public AuthenticationDialog(Frame frame) throws NotBoundException,
			RemoteException, MalformedURLException {
		super(frame, "���[�U�[�F�؃_�C�A���O", true);
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
		location.x = Math.max(0, Math.min(location.x, screenSize.width
				- getSize().width));
		location.y = Math.max(0, Math.min(location.y, screenSize.height
				- getSize().height));
		setLocation(location.x, location.y);
		show();
	}

	/**
	 * ����������
	 */
	private void init()
			throws NotBoundException,
			RemoteException,
			MalformedURLException {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		accessControl = (AccessControlable) Naming.lookup(WifeUtilities
				.createRmiActionControl());
		Container cont = getContentPane();

		cont.setLayout(new GridLayout(3, 2));
		JLabel nameLabel = new JLabel("UserName : ");
		JLabel passLabel = new JLabel("Password : ");
		userField = new JTextField();
		passField = new JPasswordField();
		passField.setEchoChar('*');
		JButton ok = new JButton("OK");
		ok.addActionListener(new OkActionListener(this));
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new CancelListener(this));

		cont.add(nameLabel);
		cont.add(userField);
		cont.add(passLabel);
		cont.add(passField);
		cont.add(ok);
		cont.add(cancel);
	}

	/**
	 * �F�،��ʂ� Subject �C���X�^���X��Ԃ��܂��B�F�،��ʂɎ��s�����ꍇ�́A null ��Ԃ��܂��B
	 * 
	 * @return �F�،��ʂ� Subject �C���X�^���X��Ԃ��܂��B�F�،��ʂɎ��s�����ꍇ�́Anull ��Ԃ��܂��B
	 */
	public Subject getSubject() {
		return this.subject;
	}

	/**
	 * <p>
	 * OK �{�^�����������ꂽ���Ɏ��s����郊�X�i�[�N���X�ł��B
	 * <p>
	 * �F�؃��W�b�N�� checkAuthentication ���\�b�h�ɁA���[�U�[���ƃp�X���[�h�� �����œn���܂��B
	 */
	static class OkActionListener implements ActionListener {
		AuthenticationDialog dlg;

		OkActionListener(AuthenticationDialog dlg) {
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent evt) {
			try {
				dlg.subject = dlg.accessControl.checkAuthentication(
						dlg.userField.getText(),
						Crypt.crypt(dlg.passField.getPassword()));
			} catch (RemoteException ex) {
				JOptionPane.showMessageDialog(
						dlg,
						"�F�؎��Ƀl�b�g���[�N�G���[���������܂����B",
						"�F�؎��Ƀl�b�g���[�N�G���[",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return;
			} catch (NoSuchAlgorithmException ex2) {
				JOptionPane.showMessageDialog(
						dlg,
						"�F�؎��ɈÍ������W���[�������݃G���[���������܂����B",
						"�F�؎��ɈÍ������W���[�������݃G���[",
						JOptionPane.ERROR_MESSAGE);
				ex2.printStackTrace();
				return;
			}

			if (dlg.subject != null) {
				dlg.dispose();
			} else {
				dlg.authenticationFail++;
				if (dlg.authenticationFail < AuthenticationDialog.MAX_AUTHENTICATION_FAIL) {
					JOptionPane
							.showMessageDialog(
									dlg,
									"���[�U�[�����p�X���[�h��������܂���B������x���͂��ĉ������B\n�p�X���[�h�͑啶���E�������̋�ʂ����܂��B",
									"�F�؎��s",
									JOptionPane.ERROR_MESSAGE);
					try {
						dlg.passField.getDocument().remove(
								0,
								dlg.passField.getPassword().length);
					} catch (BadLocationException ex) {
						ex.printStackTrace();
					}
					dlg.passField.requestFocusInWindow();
				} else {
					dlg.dispose();
				}
			}

			// for (Iterator it = dlg.subject.getPrincipals().iterator();
			// it.hasNext();) {
			// System.out.println(it.next());
			// }
		}
	}

	static class CancelListener implements ActionListener {
		AuthenticationDialog dlg;

		CancelListener(AuthenticationDialog dlg) {
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent evt) {
			dlg.dispose();
		}
	}

	public static void main(String[] argv) {
		final JFrame f = new JFrame();
		JButton change = new JButton("�؊�");
		change.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Set pri = new HashSet();
					pri.add(new WifePrincipal("hoge"));
					Subject.createSubject(null, "user1");

					new AuthenticationDialog(f);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		f.getContentPane().add(change);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
