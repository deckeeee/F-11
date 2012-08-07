package org.F11.scada.security.auth;

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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JPasswordField;

/**
 * JCE �Í����A���S���Y�����g�p���āA�p�X���[�h�� char �z����Í�������������ɕϊ����܂��B
 * ���݂̎����ł� MessageDigest �N���X�́AMD5 �Í����A���S���Y�����g�p���܂��B
 */
public class Crypt {

	private Crypt() {}

	/**
	 * JCE �Í����A���S���Y�����g�p���āA�p�X���[�h�� char �z����Í�������������ɕϊ����܂��B
	 * ���݂̎����ł� MessageDigest �N���X�́AMD5 �Í����A���S���Y�����g�p���܂��B
	 * @param password �ϊ��Ώۂ̃p�X���[�h
	 * @return �Í������ꂽ�p�X���[�h
	 * @throws NoSuchAlgorithmException �A���S���Y�����Ăяo�����̊��Ŏg�p�\�łȂ��ꍇ
	 */
	public static String crypt(char[] password) throws NoSuchAlgorithmException {
		byte[] b = new byte[password.length];
		for (int i = 0; i < password.length; i++) {
			b[i] = (byte)password[i];
		}
		MessageDigest md = MessageDigest.getInstance("MD5");
		BigInteger bi = new BigInteger(1, md.digest(b));

		for (int i = 0; i < password.length; i++) {
			b[i] = ' ';
		}

		return bi.toString(16);
	}

	public static void main(String[] argv) {
		if (argv != null) {
			if (argv.length == 0) {
				System.out.println("usage : Crypt <password>");
				return;
			}
			for (int i = 0; i < argv.length; i++) {
				try {
					JPasswordField pf = new JPasswordField(argv[i]);
					System.out.println(argv[i] + ":" + Crypt.crypt(pf.getPassword()));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
