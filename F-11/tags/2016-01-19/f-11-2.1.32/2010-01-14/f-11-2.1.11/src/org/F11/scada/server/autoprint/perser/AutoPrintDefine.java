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
package org.F11.scada.server.autoprint.perser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.F11.scada.server.autoprint.AutoPrintTask;
import org.F11.scada.server.autoprint.CsvExecAutoPrintTask;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * �����y�[�W�����`���p�[�X����N���X�ł��B
 * ��������^�X�N��ێ����܂��B
 * 
 * @author hori
 */
public class AutoPrintDefine {
	/** �����y�[�W�̈���̃^�X�N���X�g */
	private List printTasks;

	/**
	 * �����y�[�W�����`XML���p�[�X���ă^�X�N�̃��X�g�𐶐����܂��B
	 * @param file �����y�[�W�����`XML�t�@�C����
	 */
	public AutoPrintDefine(String file) throws IOException, SAXException {
		printTasks = new ArrayList();

		Digester digester = new Digester();
		digester.push(this);
		digester.setNamespaceAware(true);

		addCsvExecRule(digester);

		URL xml = getClass().getResource(file);
		if (xml == null) {
			throw new IllegalStateException(file + " not found.");
		}

		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
			is.close();
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * �������XML�̃p�[�W���O���[�����`���܂�
	 * @param digester Digester�I�u�W�F�N�g
	 */
	private void addCsvExecRule(Digester digester) {
		String pattern = "autoprint/csv_exec";
		digester.addObjectCreate(pattern, CsvExecAutoPrintTask.class);
		digester.addSetProperties(pattern);
		digester.addSetNext(pattern, "addAutoPrintTask");

		pattern = "autoprint/csv_exec/head/line";
		digester.addSetProperties(pattern, "text", "head");

		pattern = "autoprint/csv_exec/column";
		digester.addObjectCreate(pattern, ColumnBeans.class);
		digester.addSetProperties(pattern);
		digester.addSetNext(pattern, "addColumnBeans");

		pattern = "autoprint/csv_exec/execute";
		digester.addSetProperties(pattern, "command", "execute");

		pattern = "autoprint/csv_exec/execute/param";
		digester.addSetProperties(pattern, "value", "execute");
	}

	/**
	 * �ŐV���ƌx��E��Ԉꗗ�̐ݒ��ݒ肵�܂�
	 * @param config �ŐV���ƌx��E��Ԉꗗ�̐ݒ�
	 */
	public void addAutoPrintTask(AutoPrintTask task) {
		printTasks.add(task);
	}

	/**
	 * ������`����̃^�X�N���X�g��Ԃ��܂��B
	 * ���̃��X�g�͕ύX�ł��Ȃ��R�s�[��Ԃ��܂��B
	 * @return ������`����̃^�X�N���X�g��Ԃ��܂��B
	 */
	public List getAutoPrintTasks() {
		return Collections.unmodifiableList(printTasks);
	}

}
