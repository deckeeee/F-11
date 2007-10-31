/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.dialog;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.F11.scada.applet.ClientConfiguration;
import org.apache.log4j.Logger;

public abstract class ConfirmUtil {
	private static final Logger logger = Logger.getLogger(ConfirmUtil.class);

	public static boolean isConfirm(Component component) {
		logger.info("isConfirm開始");
		if (isConfirm()) {
			String[] option = { "OK", "CANCEL" };
			return JOptionPane.OK_OPTION == JOptionPane.showOptionDialog(
					component,
					"更新してよろしいですか。",
					"確認ダイアログ",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					option,
					option[1]);
		} else {
			return true;
		}
	}

	private static boolean isConfirm() {
		logger.info("isConfirm開始");
		ClientConfiguration configuration = new ClientConfiguration();
		return configuration.getBoolean(
				"org.F11.scada.applet.dialog.isConfirm",
				false);
	}
}
