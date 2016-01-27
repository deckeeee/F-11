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

package org.F11.scada.server.autoprint.jasper.exportor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import dori.jasper.engine.JRException;
import dori.jasper.engine.JasperExportManager;
import dori.jasper.engine.JasperPrint;

/**
 * PDF�t�@�C�����o�͂���N���X�ł�
 * 
 * �o�̓N���X�v���p�e�B�[ out �ɂ́A�t�@�C�������w�肵�܂��B
 * �t�@�C�����ɂ�'%'�ň͂ނ��Ƃ� {@link java.text.SimpleDateFormat}�N���X�̓��t/�����p�^�[�����g�p���邱�Ƃ��ł��܂��B
 * 
 * ��.
 * daily%yyyyMMdd%.pdf�́A2004�N4��1���Ɏ��s���邱�Ƃ�daily20040101.pdf�ƓW�J����܂��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PdfExportor implements Exportor {
	/** �o�͐�p�X�̃v���p�e�B�� */
	public static final String PROPERTY_OUT = "out";
	/** ���̃N���X�̃v���p�e�B�[ */
	private final Properties properties;

	/**
	 * �f�t�H���g�R���X�g���N�^
	 */
	public PdfExportor() {
		this.properties = new Properties();
	}

	/**
	 * �v���p�e�B�[��ݒ肵�܂��B
	 * @param key �v���p�e�B�[��
	 * @param value �v���p�e�B�[�l
	 */
	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

	// Only in a test, it is used.
	String getOut() {
		String outPath = this.properties.getProperty(PROPERTY_OUT, "");
		return canonicalFile(outPath);
	}

	/**
	 * ����I�u�W�F�N�g�� PDF �t�@�C���ɏo�͂��܂�
	 * @param print ����I�u�W�F�N�g
	 */
	public void export(JasperPrint print) throws JRException {
		JasperExportManager.exportReportToPdfFile(print, getOut());
	}
	
	private String canonicalFile(String out) {
		if (out.indexOf("%") < 0) {
			return out;
		}

		String[] s = out.split("\\%");
		String dateStr = new SimpleDateFormat(s[1]).format(new Date());
		return s[0] + dateStr + s[2];
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂�
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "properties=[" + properties + "]";
	}

}
