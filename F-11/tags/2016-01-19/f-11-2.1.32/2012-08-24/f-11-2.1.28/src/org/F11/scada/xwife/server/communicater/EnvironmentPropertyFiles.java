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

package org.F11.scada.xwife.server.communicater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.deploy.FileLister;

/**
 * �v���p�e�B�t�@�C����� Environment �I�u�W�F�N�g�𐶐�����N���X�ł�
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class EnvironmentPropertyFiles implements Environment {
	private String deviceID;
	private String deviceKind;
	private String plcIpAddress;
	private int plcPortNo;
	private String plcCommKind;
	private int plcNetNo;
	private int plcNodeNo;
	private int plcUnitNo;
	private int plcWatchWait;
	private int plcTimeout;
	private int plcRetryCount;
	private int plcRecoveryWait;
	private int hostNetNo;
	private int hostPortNo;
	private String hostIpAddress;

	private String plcIpAddress2;

	/**
	 * �f�B���N�g�����g���q��java�̃t�@�C���𒊏o����t�B���^�[�ł��B
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private static FileFilter filter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory()
					|| pathname.getName().endsWith(".properties")
					? true
					: false;
		}
	};

	/**
	 * ���[�g����v���p�e�B�t�@�C����Ǎ��� Environment �I�u�W�F�N�g�𐶐����܂�
	 * @param file �v���p�e�B�t�@�C��
	 */
	private EnvironmentPropertyFiles(File file) throws IOException {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			Properties p = new Properties();
			p.load(in);

			deviceID = p.getProperty("deviceID");
			deviceKind = p.getProperty("deviceKind");
			plcIpAddress = p.getProperty("plcIpAddress");
			plcPortNo = s2i(p.getProperty("plcPortNo"));
			plcCommKind = p.getProperty("plcCommKind");
			plcNetNo = s2i(p.getProperty("plcNetNo"));
			plcNodeNo = s2i(p.getProperty("plcNodeNo"));
			plcUnitNo = s2i(p.getProperty("plcUnitNo"));
			plcWatchWait = s2i(p.getProperty("plcWatchWait"));
			plcTimeout = s2i(p.getProperty("plcTimeout"));
			plcRetryCount = s2i(p.getProperty("plcRetryCount"));
			plcRecoveryWait = s2i(p.getProperty("plcRecoveryWait"));
			hostNetNo = s2i(p.getProperty("hostNetNo"));
			hostPortNo = s2i(p.getProperty("hostPortNo"));
			hostIpAddress = p.getProperty("hostIpAddress");

			plcIpAddress2 = p.getProperty("plcIpAddress2");
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private int s2i(String s) {
		return Integer.parseInt(s);
	}

	/**
	 * �f�o�C�XID��Ԃ��܂��B
	 * @return �f�o�C�XID
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * �f�o�C�X�̎�ނ�Ԃ��܂��B
	 * @return �f�o�C�X�̎��
	 */
	public String getDeviceKind() {
		return deviceKind;
	}

	/**
	 * �f�o�C�X��IP�A�h���X��Ԃ��܂��B
	 * @return �f�o�C�X��IP�A�h���X
	 */
	public String getPlcIpAddress() {
		return plcIpAddress;
	}

	/**
	 * �f�o�C�X�̒ʐM�|�[�g��Ԃ��܂��B
	 * @return �f�o�C�X�̒ʐM�|�[�g
	 */
	public int getPlcPortNo() {
		return plcPortNo;
	}

	/**
	 * �f�o�C�X�̃R�}���h�`�Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̃R�}���h�`��
	 */
	public String getPlcCommKind() {
		return plcCommKind;
	}

	/**
	 * �f�o�C�X�̃l�b�g�ԍ���Ԃ��܂��B
	 * @return �f�o�C�X�̃l�b�g�ԍ�
	 */
	public int getPlcNetNo() {
		return plcNetNo;
	}

	/**
	 * �f�o�C�X�̃m�[�h�ԍ���Ԃ��܂��B
	 * @return �f�o�C�X�̃m�[�h�ԍ�
	 */
	public int getPlcNodeNo() {
		return plcNodeNo;
	}

	/**
	 * �f�o�C�X�̃��j�b�g�ԍ���Ԃ��܂��B
	 * @return �f�o�C�X�̃��j�b�g�ԍ�
	 */
	public int getPlcUnitNo() {
		return plcUnitNo;
	}

	/**
	 * �f�o�C�X�̒ʐM�҂����Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̒ʐM�҂�����
	 */
	public int getPlcWatchWait() {
		return plcWatchWait;
	}

	/**
	 * �f�o�C�X�̃^�C���A�E�g���Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̃^�C���A�E�g����
	 */
	public int getPlcTimeout() {
		return plcTimeout;
	}

	/**
	 * �f�o�C�X�̃G���[���g���C�񐔂�Ԃ��܂��B
	 * @return �f�o�C�X�̃G���[���g���C��
	 */
	public int getPlcRetryCount() {
		return plcRetryCount;
	}

	/**
	 * �f�o�C�X�̒ʐM�����҂����Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̒ʐM�����҂�����
	 */
	public int getPlcRecoveryWait() {
		return plcRecoveryWait;
	}

	/**
	 * �z�X�g�̃l�b�g�A�h���X��Ԃ��܂��B
	 * @return �z�X�g�̃l�b�g�A�h���X
	 */
	public int getHostNetNo() {
		return hostNetNo;
	}

	/**
	 * �z�X�g�̒ʐM�|�[�g��Ԃ��܂��B
	 * @return �z�X�g�̒ʐM�|�[�g
	 */
	public int getHostPortNo() {
		return hostPortNo;
	}

	/**
	 * �z�X�g��IP�A�h���X��Ԃ��܂��B
	 * @return �z�X�g��IP�A�h���X
	 */
	public String getHostIpAddress() {
		return hostIpAddress;
	}

	/**
	 * �z�X�g�̃z�X�g�A�h���X��Ԃ��܂��B
	 * @return �z�X�g�̃z�X�g�A�h���X
	 */
	public int getHostAddress() {
		String s = getHostIpAddress();
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s.substring(s.lastIndexOf('.') + 1));
	}

	/**
	 * �f�o�C�X��IP�A�h���X(��d���p)��Ԃ��܂��B
	 * @return �f�o�C�X��IP�A�h���X(��d���p)
	 */
	public String getPlcIpAddress2() {
		return plcIpAddress2;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(deviceID + ", ");
		buffer.append(deviceKind + ", ");
		buffer.append(plcIpAddress + ", ");
		buffer.append(plcPortNo + ", ");
		buffer.append(plcCommKind + ", ");
		buffer.append(plcNetNo + ", ");
		buffer.append(plcNodeNo + ", ");
		buffer.append(plcUnitNo + ", ");
		buffer.append(plcWatchWait + ", ");
		buffer.append(plcTimeout + ", ");
		buffer.append(plcRetryCount + ", ");
		buffer.append(plcRecoveryWait + ", ");
		buffer.append(hostNetNo + ", ");
		buffer.append(hostPortNo + ", ");
		buffer.append(hostIpAddress + ", ");

		buffer.append(plcIpAddress2);

		return buffer.toString();
	}

	public static Environment[] getEnvironments(File root) throws IOException {
		FileLister lister = new FileLister();
		Collection<File> col = lister.listFiles(root, filter);

		Environment[] env = new EnvironmentPropertyFiles[col.size()];
		int i = 0;
		for (Iterator<File> it = col.iterator(); it.hasNext(); i++) {
			File file = it.next();
			env[i] = new EnvironmentPropertyFiles(file);
		}

		return env;
	}

	public static Environment[] getEnvironments(String root) throws IOException {
		return getEnvironments(new File(root));
	}
}
