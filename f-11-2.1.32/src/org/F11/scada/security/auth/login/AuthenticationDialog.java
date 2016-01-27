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
 * 利用ユーザーを変更するときに使用する、認証ダイアログクラスです。
 * <p>
 * postgreSQL テーブルに定義された、ユーザーと関連づけられた グループで Subject (と関連づけられたプリンシパル)を生成します。
 * <p>
 * パスワード入力を5回失敗すると認証処理を中断します。
 * 
 * @todo ユーザーのロック、失敗可能回数の変更
 */
public class AuthenticationDialog extends JDialog {
	private static final long serialVersionUID = 3082150358612121359L;
	/** 変更対象の Subject */
	private Subject subject;
	/** ユーザーフィールド */
	private JTextField userField;
	/** パスワードフィールド */
	private JPasswordField passField;
	/** アクセス制御コントロールへのリモート参照 */
	private AccessControlable accessControl;
	/** 最大認証失敗回数 */
	private static final int MAX_AUTHENTICATION_FAIL = 5;
	/** 認証失敗回数 */
	private int authenticationFail;

	/**
	 * 認証ダイアログを初期化してインスタンスを生成します。
	 * 
	 * @param frame 親フレームの参照
	 */
	public AuthenticationDialog(Frame frame) throws NotBoundException,
			RemoteException, MalformedURLException {
		super(frame, "ユーザー認証ダイアログ", true);
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
	 * 初期化処理
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
	 * 認証結果の Subject インスタンスを返します。認証結果に失敗した場合は、 null を返します。
	 * 
	 * @return 認証結果の Subject インスタンスを返します。認証結果に失敗した場合は、null を返します。
	 */
	public Subject getSubject() {
		return this.subject;
	}

	/**
	 * <p>
	 * OK ボタンが押下された時に実行されるリスナークラスです。
	 * <p>
	 * 認証ロジックの checkAuthentication メソッドに、ユーザー名とパスワードを 引数で渡します。
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
						"認証時にネットワークエラーが発生しました。",
						"認証時にネットワークエラー",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
				return;
			} catch (NoSuchAlgorithmException ex2) {
				JOptionPane.showMessageDialog(
						dlg,
						"認証時に暗号化モジュール未存在エラーが発生しました。",
						"認証時に暗号化モジュール未存在エラー",
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
									"ユーザー名かパスワードが見つかりません。もう一度入力して下さい。\nパスワードは大文字・小文字の区別をします。",
									"認証失敗",
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
		JButton change = new JButton("切換");
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
