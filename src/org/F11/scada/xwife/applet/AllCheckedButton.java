/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.F11.scada.WifeUtilities;
import org.F11.scada.data.DataAccessable;
import org.apache.log4j.Logger;

/**
 * �q�X�g���[�S�m�F�{�^��
 * 
 * @author maekawa
 *
 */
public class AllCheckedButton extends JButton {
	private static final long serialVersionUID = 3425920589353653426L;

	public AllCheckedButton() {
		super("�S�m�F");
		setMargin(new Insets(3, 11, 3, 11));
		addActionListener(new AllCheckListener(this));
	}

	private static class AllCheckListener implements ActionListener {
		private final Logger logger = Logger.getLogger(AllCheckListener.class);
		private final Component parent;

		public AllCheckListener(Component parent) {
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent e) {
			if (JOptionPane.showConfirmDialog(
				parent,
				"�S�Ă̊m�F�����`�F�b�N�ςɂ��܂��B��낵���ł����H\n" +
				"�`�F�b�N���ꗗ�ɔ��f����ɂ́A�N���C�A���g�̍ċN�����K�v�ł��B",
				"�S�m�F����",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				String collectorServer =
					WifeUtilities.createRmiManagerDelegator();
				try {
					DataAccessable alarmRef =
						(DataAccessable) Naming.lookup(collectorServer);
					alarmRef.invoke("HistoryAllCheck", new Object[0]);
				} catch (Exception ex) {
					logger.error("�S�m�FDB�X�V���ɃG���[���������܂���", ex);
				}
			}
		}
	}
}
