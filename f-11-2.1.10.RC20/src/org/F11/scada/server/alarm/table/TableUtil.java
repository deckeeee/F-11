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

package org.F11.scada.server.alarm.table;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

/**
 * �e�[�u�����f���𑀍삷�郁�\�b�h���������郆�[�e�B���e�B�[�N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TableUtil {
	/** logging API */
	private static Logger log = Logger.getLogger(TableUtil.class);
	/** �v���C�x�[�g�R���X�g���N�^ */
	private TableUtil() {}

	/**
	 * �����̃e�[�u�����f�����A�upoint�v�������������΂��̗�̃f�[�^��
	 * ���� point �Œ��o�� �L���E���́E�P�ʂ�ύX���܂��B
	 * @param table �ύX����e�[�u���I�u�W�F�N�g
	 * @param nb �V�|�C���g���
	 * @param ob ���|�C���g���
	 */	
	public static void setPoint(TableModel model, PointTableBean nb, PointTableBean ob) {
		int point = -1;
		int unit = -1;
		int name = -1;
		
		for (int i = 0, column = model.getColumnCount();
				i < column;
				i++) {

			String title = model.getColumnName(i);
			if ("point".equals(title)) {
				point = i;
			} else if ("�L��".equals(title)) {
				unit = i;
			} else if ("����".equals(title)) {
				name = i;
			}
		}

		if (point < 0) {
			log.debug("Not found point column.");
			return;
		}
		if (unit < 0) {
		   log.debug("Not found unit column.");
		}
		if (name < 0) {
		   log.debug("Not found name column.");
		}

		for (int i = 0, row = model.getRowCount(), target = ob.getPoint();
				i < row;
				i++) {

			Integer p = (Integer) model.getValueAt(i, point);
			if (target == p.intValue()) {
				if (unit >= 0) {
					String str = (String) model.getValueAt(i, unit);
					model.setValueAt(
						str.replaceAll(escapeRegexMark(ob.getUnit()), escapeRegexMark(nb.getUnit())),
						i,
						unit);
				}
				if (name >= 0) {
					String str = (String) model.getValueAt(i, name);
					model.setValueAt(
						str.replaceAll(escapeRegexMark(ob.getName()), escapeRegexMark(nb.getName())),
						i,
						name);
				}
			}
		}
	}
	
	private static String escapeRegexMark(String s) {
		StringBuffer b = new StringBuffer();
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\\' :
				case '+' :
				case '*' :
				case '[' :
				case ']' :
				case '{' :
				case '}' :
				case '?' :
				case '(' :
				case ')' :
				case '^' :
				case '$' :
				case '|' :
					b.append('\\');
					break;

				default :
					break;
			}
			b.append(c);
		}
		
		return b.toString();
	}
}
